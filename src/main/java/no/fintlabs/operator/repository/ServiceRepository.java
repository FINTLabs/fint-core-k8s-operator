package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.configuration.AppConfiguration;
import org.springframework.stereotype.Repository;

import static no.fintlabs.operator.repository.RepositoryHelper.getLabels;
import static no.fintlabs.operator.repository.RepositoryHelper.getSelectors;

@Slf4j
@Repository
public class ServiceRepository {

    private final AppConfiguration configuration;

    public ServiceRepository(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    public Service createFintCoreConsumerService(String orgId, String component) {
        return new ServiceBuilder()
                .withNewMetadata()
                .withLabels(getLabels(orgId, component))
                .withName(configuration.getDeployment().getName(orgId, component))
                .endMetadata()
                .withNewSpec()
                .withType("ClusterIP")
                .withPorts(servicePort())
                .withSelector(getSelectors(orgId, component))
                .endSpec()
                .build();
    }

    private ServicePort servicePort() {
        return new ServicePortBuilder()
                .withName(configuration.getService().getPort().toString())
                .withPort(configuration.getService().getPort())
                .build();
    }
}
