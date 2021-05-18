package no.fintlabs.operator.configuration.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
//@Component
//@ConfigurationProperties(prefix = "fint.kubernetes.deployment.deployment-strategy")
public class DeploymentStrategyConfiguration {
    private int maxSurge;
    private int maxUnavailable;
}