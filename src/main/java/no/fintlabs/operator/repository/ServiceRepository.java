package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.configuration.AppConfiguration;
import org.springframework.stereotype.Repository;

import static no.fintlabs.operator.repository.LabelHelper.getLabels;

@Slf4j
@Repository
public class ServiceRepository {

    private final KubernetesClient client;
    private final AppConfiguration configuration;

    public ServiceRepository(KubernetesClient client, AppConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    public Service applyFintCoreConsumerService(String namespace, String stack) {
        Service service = new ServiceBuilder()
                .withNewMetadata()
                //.withAnnotations(Collections.singletonMap("service.beta.kubernetes.io/azure-load-balancer-internal", "true"))
                .withLabels(getLabels(stack))
                .withName(configuration.getService().getName(stack))
                .endMetadata()
                .withNewSpec()
                .withType("ClusterIP")
                .withPorts(servicePort())
                .withSelector(getLabels(stack))
                .endSpec()
                .build();

        return client.services().inNamespace(namespace).createOrReplace(service);
    }

    private ServicePort servicePort() {
        return new ServicePortBuilder()
                .withName(configuration.getService().getPort().toString())
                .withPort(configuration.getService().getPort())
                .build();
    }
}
