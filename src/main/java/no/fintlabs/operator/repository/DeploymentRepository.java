package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentStrategy;
import io.fabric8.kubernetes.api.model.apps.DeploymentStrategyBuilder;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.operator.configuration.AppConfiguration;
import no.fintlabs.operator.repository.model.ComponentModel;
import no.fintlabs.operator.repository.model.ComponentSizes;
import org.springframework.stereotype.Repository;

import java.util.*;

import static no.fintlabs.operator.repository.RepositoryHelper.getLabels;
import static no.fintlabs.operator.repository.RepositoryHelper.getSelectors;


@Slf4j
@Repository
public class DeploymentRepository {

    private final AppConfiguration configuration;

    public DeploymentRepository(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    public Deployment createFintCoreConsumerDeployment(String orgId, ComponentModel component /*String stack, ComponentSizes.Size resourceSize, String path, String image*/) {
        return new DeploymentBuilder()
                .withNewMetadata()
                .withName(configuration.getDeployment().getName(orgId, component.getComponentName()))
                .withLabels(getLabels(orgId, component.getComponentName()))
                .withAnnotations(onePasswordAnnotations())
                .endMetadata()
                .withNewSpec()
                .withReplicas(configuration.getDeployment().getReplicas())
                .withSelector(new LabelSelectorBuilder().withMatchLabels(getSelectors(orgId, component.getComponentName())).build())
                .withStrategy(getRollingUpdate())
                .withNewTemplate()
                .withNewMetadata()
                .withLabels(getLabels(orgId, component.getComponentName()))
                .withAnnotations(prometheusAnnotations(component.getComponentPath() + "/prometheus"))
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withEnv(getConsumerEnvironmentVaribels(orgId, component/*component.getComponentName(), component.getComponentPath(), component.getSize()*/))
                .withImage(component.getComponentImage())
                .withName(configuration.getDeployment().getName(orgId, component.getComponentName()))
                .withPorts(getContainerPorts())
                .withNewReadinessProbe()
                .withHttpGet(getHttpGetAction(component.getComponentPath()))
                .withInitialDelaySeconds(60)
                .withTimeoutSeconds(30)
                .endReadinessProbe()
                .withNewResources()
                .withRequests(component.getSize().getRequest().toMap())
                .withLimits(component.getSize().getLimit().toMap())
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

    private List<EnvVar> getConsumerEnvironmentVaribels(String orgId, ComponentModel component/*String stack, String path, ComponentSizes.Size resourceSize*/) {
        List<EnvVar> envVars = new ArrayList<>() {{
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.enabled").withValue("true").build());
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.namespace").withValue(orgId).build());
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.labelName").withValue("fint.stack").build());
            add(new EnvVarBuilder().withName("fint.hazelcast.kubernetes.labelValue").withValue(component.getComponentName()).build());
            add(new EnvVarBuilder().withName("server.context-path").withValue(component.getComponentPath()).build());
            add(new EnvVarBuilder().withName("fint.consumer.dynamic-registration").withValue("false").build());
            add(new EnvVarBuilder().withName("fint.events.orgIds").withValue(orgId.replace("_", ".")).build());
            add(new EnvVarBuilder().withName("fint.security.role").withValue(String.format("FINT_Client_%s", component.getComponentName())).build());
            add(new EnvVarBuilder().withName("fint.security.scope").withValue("fint-client").build());

            add(new EnvVarBuilder().withName("JAVA_TOOL_OPTIONS").withValue(
                    String.format("-XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -Xmx%s -verbose:gc",
                            RepositoryHelper.getXmx(component.getSize().getLimit().getMemory())
                    )
            ).build());
        }};


        component.getCacheDisabledFor()
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
