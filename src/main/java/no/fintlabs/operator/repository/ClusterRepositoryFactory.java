package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.Quantity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterRepositoryFactory {

    public static Map<String, String> getLabels(String stack) {
        return new HashMap<>() {{
            put("fint.stack", stack);
            put("fint.role", "consumer");
        }};
    }




}
