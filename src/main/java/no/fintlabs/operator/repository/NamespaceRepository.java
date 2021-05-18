package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class NamespaceRepository {

    private final KubernetesClient client;

    public NamespaceRepository(KubernetesClient client) {
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
}