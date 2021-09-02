package no.fintlabs.operator.status;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatusController {
    private final KubernetesClient kubernetesClient;

    public StatusController(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @GetMapping("k8s/{name}")
    public ResponseEntity<StatusModel> getDeploymentStatus(@PathVariable final String name) {
        return ResponseEntity.ok(StatusModel.builder().deploymentStatus(kubernetesClient
                                .apps()
                                .deployments()
                                .inNamespace("default")
                                .withName(name)
                                .get()
                                .getStatus()
                        )
                        .serviceStatus(kubernetesClient
                                .services()
                                .inNamespace("default")
                                .withName(name)
                                .get()
                                .getStatus()
                        ).build()
        );
    }
}
