package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.configuration.AppConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Collections;

import static no.fintlabs.operator.repository.ClusterRepositoryFactory.getLabels;

@Slf4j
@Repository
public class ClusterRepository {

    private final KubernetesClient client;

    public ClusterRepository(KubernetesClient client, ApplicationContext context, AppConfiguration configuration) {
        this.client = client;
    }

    public Namespace applyNamespace(String namespaceName) {
        Namespace namespace = new NamespaceBuilder()
                .withNewMetadata()
                .withName(namespaceName)
                .endMetadata()
                .build();
        return client.namespaces().withName(namespaceName).createOrReplace(namespace);
    }

    public Service applyFintCoreConsumerService(String namespace, String stack) {
        Service service = new ServiceBuilder()
                .withNewMetadata()
                .withAnnotations(Collections.singletonMap("service.beta.kubernetes.io/azure-load-balancer-internal", "true"))
                .withLabels(getLabels(stack))
                .withName("consumer-" + stack)
                .endMetadata()
                .withNewSpec()
                .withType("LoadBalancer")
                .withPorts(new ServicePortBuilder().withName("8080").withPort(8080).build())
                .withSelector(getLabels(stack))
                .endSpec()
                .build();

        return client.services().inNamespace(namespace).createOrReplace(service);
    }


}