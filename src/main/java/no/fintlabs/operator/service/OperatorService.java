package no.fintlabs.operator.service;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.configuration.AppConfiguration;
import no.fintlabs.operator.repository.model.FintConsumerDefinition;
import no.fintlabs.operator.repository.model.DeploymentModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OperatorService {

    private final WebClient webClient;
    private final FintConsumerService fintConsumerService;
    private final KubernetesClient client;
    private final AppConfiguration configuration;

    public OperatorService(WebClient webClient, FintConsumerService fintConsumerService, KubernetesClient client, AppConfiguration configuration) {
        this.webClient = webClient;
        this.fintConsumerService = fintConsumerService;
        this.client = client;
        this.configuration = configuration;
    }


    @Scheduled(
            fixedRateString = "${fint.kubernetes.operator.schedule.fixed-rate}",
            initialDelayString = "${fint.kubernetes.operator.schedule.initial-delay}"
    )
    public void refreshConsumers() {
        updateConsumers();
    }

    public void updateConsumers() {
        final List<FintConsumerDefinition> fintConsumers = getFintConsumerDefinitions();
        log.info("There are {} FINT consumers defined.", fintConsumers.size());

        updateServices(fintConsumers);
        updateDeployments(fintConsumers);
        deleteUnusedDeployments(fintConsumers);
        deleteUnusedServices(fintConsumers);

    }

    private List<FintConsumerDefinition> getFintConsumerDefinitions() {
        return Objects.requireNonNull(webClient.get()
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<DeploymentModel>>() {
                        })
                        .block())
                .stream()
                .flatMap(fintConsumerService::createFintConsumers)
                //.peek(it -> log.info("-> {} [{}]", it.getName(), it.getOrgId()))
                .collect(Collectors.toList());
    }

    private void updateDeployments(List<FintConsumerDefinition> fintConsumers) {
        log.info("Updating deployments...");
        fintConsumers
                .stream()
                .map(FintConsumerDefinition::getDeployment)
                .forEach(deployment -> {
                    log.info("-> Updating {}", deployment.getMetadata().getName());
                    client.apps().deployments().inNamespace(configuration.getDefaultNamespace()).createOrReplace(deployment);
                });
    }

    private void updateServices(List<FintConsumerDefinition> fintConsumers) {
        log.info("Updating services...");
        fintConsumers
                .stream()
                .map(FintConsumerDefinition::getService)
                .forEach(service -> {
                    log.info("-> Updating {}", service.getMetadata().getName());
                    client.services().inNamespace(configuration.getDefaultNamespace()).createOrReplace(service);
                });
    }

    private void deleteUnusedDeployments(List<FintConsumerDefinition> fintConsumers) {
        client.apps().deployments().delete(client.apps().deployments()
                .inNamespace(configuration.getDefaultNamespace())
                .withLabels(Collections.singletonMap("fint.created-by", "fint-core-k8s-operator"))
                .list()
                .getItems()
                .stream()
                .filter(deployment -> fintConsumers.stream().noneMatch(fc -> fc.getName().equals(deployment.getMetadata().getName())))
                .peek(deployment -> log.info("-> Deleting deployment {}", deployment.getMetadata().getName()))
                .collect(Collectors.toList()));
    }

    private void deleteUnusedServices(List<FintConsumerDefinition> fintConsumers) {
        client.services().delete(client.services()
                .inNamespace(configuration.getDefaultNamespace())
                .withLabels(Collections.singletonMap("fint.created-by", "fint-core-k8s-operator"))
                .list()
                .getItems()
                .stream()
                .filter(service -> fintConsumers.stream().noneMatch(fc -> fc.getName().equals(service.getMetadata().getName())))
                .peek(service -> log.info("-> Deleting service {}", service.getMetadata().getName()))
                .collect(Collectors.toList()));
    }


}
