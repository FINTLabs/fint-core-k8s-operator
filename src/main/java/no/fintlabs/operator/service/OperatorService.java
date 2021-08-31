package no.fintlabs.operator.service;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.configuration.AppConfiguration;
import no.fintlabs.operator.model.FintConsumerDefinition;
import no.fintlabs.operator.model.K8sDeploymentModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
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

        final long services = fintConsumers
                .stream()
                .map(FintConsumerDefinition::getService)
                .peek(client.services().inNamespace(configuration.getNamespace())::createOrReplace)
                .count();
        log.info("Updated {} services.", services);

        final long deployments = fintConsumers
                .stream()
                .map(FintConsumerDefinition::getDeployment)
                .peek(client.apps().deployments().inNamespace(configuration.getNamespace())::createOrReplace)
                .count();

        log.info("Updated {} deployments.", deployments);
    }


}
