package no.fintlabs.operator.service;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import no.fintlabs.operator.configuration.AppConfiguration;
import no.fintlabs.operator.model.FintConsumerDefinition;
import no.fintlabs.operator.model.K8sDeploymentModel;
import no.fintlabs.operator.repository.DeploymentRepository;
import no.fintlabs.operator.repository.ServiceRepository;
import org.springframework.util.StringUtils;

import java.util.stream.Stream;

@org.springframework.stereotype.Service
public class FintConsumerService {

    private final ServiceRepository serviceRepository;
    private final DeploymentRepository deploymentRepository;
    private final AppConfiguration configuration;


    public FintConsumerService(ServiceRepository serviceRepository, DeploymentRepository deploymentRepository, AppConfiguration configuration) {
        this.serviceRepository = serviceRepository;
        this.deploymentRepository = deploymentRepository;
        this.configuration = configuration;
    }

    public Stream<FintConsumerDefinition> createFintConsumers(K8sDeploymentModel deploymentModel) {
        return deploymentModel
                .getComponents()
                .stream()
                .filter(comp -> StringUtils.hasText(comp.getComponentImage()))
                .map(component -> {
                    final Service service = serviceRepository.createFintCoreConsumerService(deploymentModel.getOrgId(), component.getComponentName());
                    final Deployment deployment = deploymentRepository.createFintCoreConsumerDeployment(deploymentModel.getOrgId(), component);
                    return new FintConsumerDefinition(configuration.getDeployment().getName(deploymentModel.getOrgId(), component.getComponentName()), deploymentModel.getOrgId(), service, deployment);
                });
    }
}
