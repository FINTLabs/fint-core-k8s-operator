package no.fintlabs.operator.configuration;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClusterConfiguration {

    @Value("${fint.kubernetes.api-server}")
    private String kubernetesApiServer;

    @Bean
    public DefaultKubernetesClient kubernetesClient() {
        Config config = new ConfigBuilder()
                .withMasterUrl(kubernetesApiServer)
                .build();

        return new DefaultKubernetesClient(config);
    }
}
