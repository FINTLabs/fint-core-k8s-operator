package no.fintlabs.operator.status;

import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusModel {
    private DeploymentStatus deploymentStatus;
    private ServiceStatus serviceStatus;
}
