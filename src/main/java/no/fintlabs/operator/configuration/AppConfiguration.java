package no.fintlabs.operator.configuration;

import io.fabric8.kubernetes.api.model.Quantity;
import lombok.Data;
import no.fintlabs.operator.configuration.model.CoreDeploymentStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "fint.garden-gnome", ignoreUnknownFields = false)
public class AppConfiguration {
    private String environmentUrl;
    private CoreDeploymentStrategy deploymentStrategy = CoreDeploymentStrategy.getDefaultDeploymentStrategy();
    private Integer port = 8080;
    private Integer replicas = 1;
    private final Map<String, Map<String, Quantity>> resourceRequest = DefaultCoreResources.getRequest();
    private final Map<String, Map<String, Quantity>> resourceLimit = DefaultCoreResources.getLimit();
    private String consumerNameTemplate = "consumer-%s";
    private String onePasswordVault;
    private String eventhubSecretName = "fint-events-azure-eventhub";
    private List<String> cacheDisabledFor = new ArrayList<>();

    public String getConsumerName(String stack) {
        return String.format(consumerNameTemplate, stack);
    }


}
