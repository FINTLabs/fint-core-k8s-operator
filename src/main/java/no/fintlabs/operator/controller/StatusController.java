package no.fintlabs.operator.controller;

import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatusController {
    private final KubernetesClient kubernetesClient;

    public StatusController(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @GetMapping("deployment")
    public DeploymentStatus getDeploymentStatus() {
        return kubernetesClient
                .apps()
                .deployments()
                .inNamespace("rogfk-no")
                .withName("consumer-administrasjon-personal")
                .get()
                .getStatus();
    }
}
