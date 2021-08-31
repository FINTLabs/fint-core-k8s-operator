package no.fintlabs.operator.service;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.model.K8sDeploymentModel;
import no.fintlabs.operator.repository.DeploymentRepository;
import no.fintlabs.operator.repository.ServiceRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class OperatorService {

    private final DeploymentRepository deploymentRepository;
    private final ServiceRepository serviceRepository;
    private final WebClient webClient;

    public OperatorService(DeploymentRepository deploymentRepository, ServiceRepository serviceRepository, WebClient webClient) {
        this.deploymentRepository = deploymentRepository;
        this.serviceRepository = serviceRepository;
        this.webClient = webClient;
    }

    @PostConstruct
    public void init() {
        updateDeployments();
    }

    public void updateDeployments() {
        Objects.requireNonNull(webClient.get()
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<K8sDeploymentModel>>() {
                        })
                        .block())
                .forEach(organisation -> organisation.getComponents()
                        .forEach(deployment -> {
                            serviceRepository.applyFintCoreConsumerService(organisation.getOrgId(), deployment.getComponentName());
                            if (deployment.getComponentImage() != null) {
                                deploymentRepository.applyFintCoreConsumerDeployment(
                                        organisation.getOrgId(),
                                        deployment
                                );
                            }
                        }));
    }


}
