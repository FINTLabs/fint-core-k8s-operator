package no.fintlabs.operator.service;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.configuration.AppConfiguration;
import no.fintlabs.operator.model.FintConsumerDefinition;
import no.fintlabs.operator.model.K8sDeploymentModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        updateDeployments();
    }

    @Scheduled(fixedRate = 30000L)
    public void refreshConsumers() {
        updateDeployments();
    }

    public void updateDeployments() {
        final List<FintConsumerDefinition> fintConsumers = Objects.requireNonNull(webClient.get()
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<K8sDeploymentModel>>() {
                        })
                        .block())
                .stream()
                .flatMap(fintConsumerService::createFintConsumers)
                .peek(it -> log.info("-> {} [{}]", it.getName(), it.getOrgId()))
                .collect(Collectors.toList());
        log.info("There are {} FINT consumers defined.", fintConsumers.size());

        fintConsumers
                .stream()
                .map(FintConsumerDefinition::getService)
                .forEach(service -> {
                    log.info("-> Updating service {}", service.getMetadata().getName());
                    client.services().inNamespace(configuration.getNamespace()).createOrReplace(service);
                });

        //log.info("Updated {} services.", fintConsumers.size());

        fintConsumers
                .stream()
                .map(FintConsumerDefinition::getDeployment)
                .forEach(deployment -> {
                    log.info("-> Updating deployment {}", deployment.getMetadata().getName());
                    client.apps().deployments().inNamespace(configuration.getNamespace()).createOrReplace(deployment);
                });


        //log.info("Updated {} deployments.", deployments);

        List<Deployment> deploymentsToDelete = client.apps().deployments()
                .inNamespace(configuration.getNamespace())
                .withLabels(Collections.singletonMap("fint.created-by", "fint-core-k8s-operator"))
                .list()
                .getItems()
                .stream()
                .filter(deployment -> fintConsumers.stream().noneMatch(fc -> fc.getName().equals(deployment.getMetadata().getName())))
                .collect(Collectors.toList());

//        List<Deployment> consumerDeployment = deployments.stream()
//                .filter(deployment -> fintConsumers.stream().noneMatch(fc -> fc.getName().equals(deployment.getMetadata().getName())))
//                .collect(Collectors.toList());

        log.info("Deleting {} deployments", deploymentsToDelete.size());
        client.apps().deployments().delete(deploymentsToDelete);


        List<io.fabric8.kubernetes.api.model.Service> servicesToDelete = client.services()
                .inNamespace(configuration.getNamespace())
                .withLabels(Collections.singletonMap("fint.created-by", "fint-core-k8s-operator"))
                .list()
                .getItems()
                .stream()
                .filter(service -> fintConsumers.stream().noneMatch(fc -> fc.getName().equals(service.getMetadata().getName())))
                .collect(Collectors.toList());

//        List<io.fabric8.kubernetes.api.model.Service> consumerServices = items.stream()
//                .filter(service -> fintConsumers.stream().noneMatch(fc -> fc.getName().equals(service.getMetadata().getName())))
//                .collect(Collectors.toList());

        log.info("Deleting {} services", servicesToDelete.size());
        client.services().delete(servicesToDelete);

    }


}
