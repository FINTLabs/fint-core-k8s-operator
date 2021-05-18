package no.fintlabs.operator.configuration;

import io.fabric8.kubernetes.api.model.Quantity;
import lombok.Data;
import no.fintlabs.operator.configuration.model.ConfigMapConfiguration;
import no.fintlabs.operator.configuration.model.DeploymentConfiguration;
import no.fintlabs.operator.configuration.model.DeploymentStrategyConfiguration;
import no.fintlabs.operator.configuration.model.ServiceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "fint.kubernetes")
public class AppConfiguration {
    private String apiServer;
    private DeploymentConfiguration deployment;
    private ServiceConfiguration service;
    private ConfigMapConfiguration configMap;

//    private String environmentUrl;
//    private DeploymentStrategyConfiguration deploymentStrategy = DeploymentStrategyConfiguration.getDefaultDeploymentStrategy();
//    private Integer port = 8080;
//    private Integer replicas = 1;
//    private final Map<String, Map<String, Quantity>> resourceRequest = DefaultCoreResources.getRequest();
//    private final Map<String, Map<String, Quantity>> resourceLimit = DefaultCoreResources.getLimit();
//    private String consumerNameTemplate = "consumer-%s";
//    private String onepasswordVault;
//    private String eventhubSecretName = "fint-events-azure-eventhub";
//    private List<String> cacheDisabledFor = new ArrayList<>();
//
//    public String getConsumerName(String stack) {
//        return String.format(consumerNameTemplate, stack);
//    }


}
