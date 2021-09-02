package no.fintlabs.operator.configuration.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeploymentStrategyConfiguration {
    private int maxSurge;
    private int maxUnavailable;
}