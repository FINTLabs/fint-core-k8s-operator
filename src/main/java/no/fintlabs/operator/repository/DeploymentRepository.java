package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentStrategy;
import io.fabric8.kubernetes.api.model.apps.DeploymentStrategyBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.configuration.AppConfiguration;
import org.springframework.stereotype.Repository;

import java.util.*;

import static no.fintlabs.operator.repository.ClusterRepositoryFactory.getLabels;


@Slf4j
@Repository
public class DeploymentRepository {

    private final KubernetesClient client;
    private final AppConfiguration configuration;

    public DeploymentRepository(KubernetesClient client, AppConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    public Deployment applyFintCoreConsumerDeployment(String namespace, String stack, String resourceSize, String path, String image) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(configuration.getConsumerName(stack))
                .withLabels(getLabels(stack))
                .withAnnotations(onePasswordAnnotations())
                .endMetadata()
                .withNewSpec()
                .withReplicas(configuration.getReplicas())
                .withSelector(new LabelSelectorBuilder().withMatchLabels(getLabels(stack)).build())
                .withStrategy(getRollingUpdate())
                .withNewTemplate()
                .withNewMetadata()
                .withLabels(getLabels(stack))
                .withAnnotations(prometheusAnnotations(path + "/prometheus"))
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withEnv(getConsumerEnvironmentVaribels(namespace, stack, path))
                .withImage(image)
                .withName(configuration.getConsumerName(stack))
                .withPorts(getContainerPorts())
                .withNewReadinessProbe()
                .withHttpGet(getHttpGetAction(path))
                .withInitialDelaySeconds(60)
                .withTimeoutSeconds(30)
                .endReadinessProbe()
                .withNewResources()
                .withRequests(configuration.getResourceRequest().get(resourceSize))
                .withLimits(configuration.getResourceLimit().get(resourceSize))
                .endResources()
                .withEnvFrom(
                        new EnvFromSourceBuilder()
                                .withNewConfigMapRef("fint-environment", false)
                                .build(),
                        new EnvFromSourceBuilder()
                                .withNewSecretRef(configuration.getEventhubSecretName(), false)
                                .build())
                .endContainer()
                .withRestartPolicy("Always")
                .endSpec()
                .endTemplate()
                .and()
                .build();

        return client.apps().deployments().inNamespace(namespace).createOrReplace(deployment);
    }

    private HTTPGetAction getHttpGetAction(String path) {
        return new HTTPGetActionBuilder()
                .withPath(path + "/health")
                .withPort(new IntOrString(configuration.getPort()))
                .build();
    }

    private List<ContainerPort> getContainerPorts() {
        return Collections.singletonList(new ContainerPortBuilder().withContainerPort(configuration.getPort()).build());
    }

    private DeploymentStrategy getRollingUpdate() {
        return new DeploymentStrategyBuilder()
                .withType("RollingUpdate")
                .withNewRollingUpdate()
                .withMaxSurge(new IntOrString(configuration.getDeploymentStrategy().getMaxSurge()))
                .withMaxUnavailable(new IntOrString(configuration.getDeploymentStrategy().getMaxUnavailable()))
                .and()
                .build();
    }

    private List<EnvVar> getConsumerEnvironmentVaribels(String namespace, String stack, String path) {
        List<EnvVar> envVars = new ArrayList<>() {{
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.enabled").withValue("true").build());
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.namespace").withValue(namespace).build());
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.labelName").withValue("fint.stack").build());
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.labelValue").withValue(stack).build());
            add(new EnvVarBuilder().withName("server.context-path").withValue(path).build());
        }};

        configuration.getCacheDisabledFor()
                .stream()
                .map(entity -> new EnvVarBuilder()
                        .withName("fint.consumer.cache.disabled." + entity)
                        .withValue("true")
                        .build()
                )
                .forEach(envVars::add);

        return envVars;
    }

    private Map<String, String> prometheusAnnotations(String path) {
        return new HashMap<>() {{
            put("prometheus.io/scrape", "true");
            put("prometheus.io/port", configuration.getPort().toString());
            put("prometheus.io/path", path);
        }};
    }

    private Map<String, String> onePasswordAnnotations() {
        return new HashMap<>() {{
            put("operator.1password.io/item-path",
                    String.format(
                            "vaults/%s/items/%s",
                            configuration.getOnePasswordVault(),
                            configuration.getEventhubSecretName())
            );
            put("operator.1password.io/item-name", configuration.getEventhubSecretName());
        }};
    }
}
