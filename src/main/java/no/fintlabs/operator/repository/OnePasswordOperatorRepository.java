package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class OnePasswordOperatorRepository {
    private final KubernetesClient client;

    public OnePasswordOperatorRepository(KubernetesClient client) {
        this.client = client;
    }

    public void updateOnePasswordOperator(String namespaceToAdd) {
        RollableScalableResource<Deployment> onePasswordConnectOperator = client
                .apps()
                .deployments()
                .inNamespace("default")
                .withName("onepassword-connect-operator");

        onePasswordConnectOperator
                .edit(d -> new DeploymentBuilder(d)
                        .editSpec()
                        .editTemplate()
                        .editSpec()
                        .editFirstContainer()
                        .editMatchingEnv(e -> e.getName().equals("WATCH_NAMESPACE"))
                        .withValue(getUpdatedOnePasswordWatchNamespaces(onePasswordConnectOperator.get(), namespaceToAdd))
                        .endEnv()
                        .endContainer()
                        .endSpec()
                        .endTemplate()
                        .endSpec()
                        .build()
                );

    }

    public String getUpdatedOnePasswordWatchNamespaces(Deployment deployment, String namespaceToAdd) {
        List<String> watchNamespaces = deployment
                .getSpec()
                .getTemplate()
                .getSpec()
                .getContainers()
                .get(0)
                .getEnv()
                .stream()
                .filter(e -> e.getName()
                        .equals("WATCH_NAMESPACE"))
                .map(EnvVar::getValue)
                .map(e -> e.split(","))
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());

        watchNamespaces.add(namespaceToAdd);

        return watchNamespaces.stream().distinct().collect(Collectors.joining(","));
    }
}
