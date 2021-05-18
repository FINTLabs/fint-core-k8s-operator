package no.fintlabs.operator.service;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.repository.ClusterRepository;
import no.fintlabs.operator.repository.ConfigMapRepository;
import no.fintlabs.operator.repository.DeploymentRepository;
import no.fintlabs.operator.repository.OnePasswordOperatorRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class OperatorService {

    private final KubernetesClient kubernetesClient;
    private final ClusterRepository clusterRepository;
    private final OnePasswordOperatorRepository onePasswordOperatorRepository;
    private final ConfigMapRepository configMapRepository;
    private final DeploymentRepository deploymentRepository;

    public OperatorService(KubernetesClient kubernetesClient, ClusterRepository clusterRepository, OnePasswordOperatorRepository onePasswordOperatorRepository, ConfigMapRepository configMapRepository, DeploymentRepository deploymentRepository) {
        this.kubernetesClient = kubernetesClient;
        this.clusterRepository = clusterRepository;
        this.onePasswordOperatorRepository = onePasswordOperatorRepository;
        this.configMapRepository = configMapRepository;
        this.deploymentRepository = deploymentRepository;
    }

    @PostConstruct
    public void init() {
        configMapRepository.applyCoreEnvironmentConfig("rogfk-no");
        onePasswordOperatorRepository.updateOnePasswordOperator("rogfk-no");
        clusterRepository.applyNamespace("rogfk-no");
        clusterRepository.applyFintCoreConsumerService("rogfk-no", "administrasjon-personal");
        deploymentRepository.applyFintCoreConsumerDeployment(
                "rogfk-no",
                "administrasjon-personal",
                "small",
                "/administrasjon/personal",
                "fintlabsacr.azurecr.io/consumer-administrasjon-personal:3.8.0"
        );
    }


}
