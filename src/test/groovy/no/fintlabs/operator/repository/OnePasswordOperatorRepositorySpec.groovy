package no.fintlabs.operator.repository

import io.fabric8.kubernetes.api.model.EnvVar
import io.fabric8.kubernetes.api.model.EnvVarBuilder
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import spock.lang.Specification

class OnePasswordOperatorRepositorySpec extends Specification {

    def "When adding a new organisation a new namespace should be added to OP"() {
        given:
        def repository = new OnePasswordOperatorRepository(Mock(KubernetesClient))

        def deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName("test")
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewTemplate()
                .withNewMetadata()
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withEnv(new ArrayList<EnvVar>() {
                    {
                        add(new EnvVarBuilder().withName("WATCH_NAMESPACE").withValue("default").build());
                        add(new EnvVarBuilder().withName("WATCH_NAMESPACE").withValue("test1_no").build());
                    }
                })
                .endContainer()
                .endSpec()
                .endTemplate()
                .and()
                .build()

        when:
        def namespaces = repository.getUpdatedOnePasswordWatchNamespaces(deployment, "test2_no")

        then:
        namespaces == "default,test1_no,test2_no"
    }
}
