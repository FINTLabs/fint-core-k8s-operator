package no.fintlabs.operator.configuration.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CoreDeploymentStrategy {
    private final int maxSurge;
    private final int maxUnavailable;

    public static CoreDeploymentStrategy getDefaultDeploymentStrategy() {
        return CoreDeploymentStrategy
                .builder()
                .maxSurge(1)
                .maxUnavailable(0)
                .build();
    }
}