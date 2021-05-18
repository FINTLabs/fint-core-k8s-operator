package no.fintlabs.operator.configuration.model;

import io.fabric8.kubernetes.api.model.Quantity;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
//@Component
//@ConfigurationProperties(prefix = "fint.kubernetes.deployment.resources")
public class ResourcesConfiguration {
    private Map<String, Map<String, Quantity>> request;
    private Map<String, Map<String, Quantity>> limit;
}
