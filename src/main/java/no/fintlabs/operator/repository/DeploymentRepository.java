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

import static no.fintlabs.operator.repository.LabelHelper.getLabels;


@Slf4j
@Repository
public class DeploymentRepository {

    private final KubernetesClient client;
    private final AppConfiguration configuration;

    public DeploymentRepository(KubernetesClient client, AppConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    public Deployment applyFintCoreConsumerDeployment(String namespace, String stack, String resourceSize, String path, String image, String orgId) {
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(configuration.getDeployment().getName(stack, orgId))
                .withLabels(getLabels(stack, orgId))
                .withAnnotations(onePasswordAnnotations())
                .endMetadata()
                .withNewSpec()
                .withReplicas(configuration.getDeployment().getReplicas())
                .withSelector(new LabelSelectorBuilder().withMatchLabels(getLabels(stack, orgId)).build())
                .withStrategy(getRollingUpdate())
                .withNewTemplate()
                .withNewMetadata()
                .withLabels(getLabels(stack, orgId))
                .withAnnotations(prometheusAnnotations(path + "/prometheus"))
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withEnv(getConsumerEnvironmentVaribels(namespace, stack, path, orgId))
                .withImage(image)
                .withName(configuration.getDeployment().getName(stack, orgId))
                .withPorts(getContainerPorts())
                .withNewReadinessProbe()
                .withHttpGet(getHttpGetAction(path))
                .withInitialDelaySeconds(60)
                .withTimeoutSeconds(30)
                .endReadinessProbe()
                .withNewResources()
                .withRequests(configuration.getDeployment().getResources().getRequest().get(resourceSize))
                .withLimits(configuration.getDeployment().getResources().getLimit().get(resourceSize))
                .endResources()
                .withEnvFrom(
                        new EnvFromSourceBuilder()
                                .withNewConfigMapRef("fint-environment", false)
                                .build(),
                        new EnvFromSourceBuilder()
                                .withNewSecretRef(configuration.getDeployment().getSecret(), false)
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
                .withPort(new IntOrString(configuration.getDeployment().getPort()))
                .build();
    }

    private List<ContainerPort> getContainerPorts() {
        return Collections.singletonList(new ContainerPortBuilder().withContainerPort(configuration.getDeployment().getPort()).build());
    }

    private DeploymentStrategy getRollingUpdate() {
        return new DeploymentStrategyBuilder()
                .withType("RollingUpdate")
                .withNewRollingUpdate()
                .withMaxSurge(new IntOrString(configuration.getDeployment().getDeploymentStrategy().getMaxSurge()))
                .withMaxUnavailable(new IntOrString(configuration.getDeployment().getDeploymentStrategy().getMaxUnavailable()))
                .and()
                .build();
    }

    private List<EnvVar> getConsumerEnvironmentVaribels(String namespace, String stack, String path, String orgId) {
        List<EnvVar> envVars = new ArrayList<>() {{
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.enabled").withValue("true").build());
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.namespace").withValue(namespace).build());
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.labelName").withValue("fint.stack").build());
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.labelValue").withValue(stack).build());
            add(new EnvVarBuilder().withName("fint.events.orgIds").withValue(orgId).build());
            add(new EnvVarBuilder().withName("fint.consumer.dynamic-registration").withValue("false").build());
            add(new EnvVarBuilder().withName("server.context-path").withValue(path).build());
        }};

        configuration.getDeployment().getCacheDisabledFor()
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
            put("prometheus.io/port", configuration.getDeployment().getPort().toString());
            put("prometheus.io/path", path);
        }};
    }

    private Map<String, String> onePasswordAnnotations() {
        return new HashMap<>() {{
            put("operator.1password.io/item-path",
                    String.format(
                            "vaults/%s/items/%s",
                            configuration.getDeployment().getOnepasswordVault(),
                            configuration.getDeployment().getSecret())
            );
            put("operator.1password.io/item-name", configuration.getDeployment().getSecret());
        }};
    }
}
