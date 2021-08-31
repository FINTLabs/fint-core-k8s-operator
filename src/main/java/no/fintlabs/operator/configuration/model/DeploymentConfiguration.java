package no.fintlabs.operator.configuration.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeploymentConfiguration extends AbstractNameTemplateConfiguration {
    private Integer port;
    private String onepasswordVault;
    private String secret;
    private List<String> cacheDisabledFor;
    private Integer replicas;
    private DeploymentStrategyConfiguration deploymentStrategy;
}
