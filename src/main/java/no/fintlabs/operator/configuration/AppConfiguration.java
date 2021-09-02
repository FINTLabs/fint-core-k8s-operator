package no.fintlabs.operator.configuration;

import lombok.Data;
import no.fintlabs.operator.configuration.model.DeploymentConfiguration;
import no.fintlabs.operator.configuration.model.ServiceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "fint.kubernetes")
public class AppConfiguration {
    private String apiServer;
    private String defaultNamespace;
    private String deploymentSourceUri;
    private DeploymentConfiguration deployment;
    private ServiceConfiguration service;
}
