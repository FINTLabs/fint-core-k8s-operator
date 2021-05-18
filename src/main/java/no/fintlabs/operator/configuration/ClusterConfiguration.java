package no.fintlabs.operator.configuration;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClusterConfiguration {

    private final AppConfiguration configuration;

    public ClusterConfiguration(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public DefaultKubernetesClient kubernetesClient() {
        Config config = new ConfigBuilder()
                .withMasterUrl(configuration.getApiServer())
                .build();

        return new DefaultKubernetesClient(config);
    }
}
