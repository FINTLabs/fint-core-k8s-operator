package no.fintlabs.operator.repository.model;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.Data;

@Data
public class FintConsumerDefinition {
    private final String name;
    private final String orgId;
    private final Service service;
    private final Deployment deployment;
}
