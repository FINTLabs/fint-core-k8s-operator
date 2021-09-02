package no.fintlabs.operator.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClient {
    private final AppConfiguration configuration;

    public RestClient(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .baseUrl(configuration.getDeploymentSourceUri())
                .build();
    }
}
