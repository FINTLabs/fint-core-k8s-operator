package no.fintlabs.operator.service;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.repository.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class OperatorService {

    private final KubernetesClient kubernetesClient;
    private final NamespaceRepository namespaceRepository;
    private final OnePasswordOperatorRepository onePasswordOperatorRepository;
    private final ConfigMapRepository configMapRepository;
    private final DeploymentRepository deploymentRepository;
    private final ServiceRepository serviceRepository;

    public OperatorService(KubernetesClient kubernetesClient, NamespaceRepository namespaceRepository, OnePasswordOperatorRepository onePasswordOperatorRepository, ConfigMapRepository configMapRepository, DeploymentRepository deploymentRepository, ServiceRepository serviceRepository) {
        this.kubernetesClient = kubernetesClient;
        this.namespaceRepository = namespaceRepository;
        this.onePasswordOperatorRepository = onePasswordOperatorRepository;
        this.configMapRepository = configMapRepository;
        this.deploymentRepository = deploymentRepository;
        this.serviceRepository = serviceRepository;
    }

    @PostConstruct
    public void init() {
        configMapRepository.applyCoreEnvironmentConfig("rogfk-no");
        onePasswordOperatorRepository.updateOnePasswordOperator("rogfk-no");
        namespaceRepository.applyNamespace("rogfk-no");
        serviceRepository.applyFintCoreConsumerService("rogfk-no", "administrasjon-personal", "rogfk.no");
        deploymentRepository.applyFintCoreConsumerDeployment(
                "rogfk-no",
                "administrasjon-personal",
                "small",
                "/administrasjon/personal",
                "fintlabsacr.azurecr.io/consumer-administrasjon-personal:3.8.0",
                "rogfk.no");
    }


}
