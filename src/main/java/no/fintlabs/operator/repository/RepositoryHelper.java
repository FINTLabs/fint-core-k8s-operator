package no.fintlabs.operator.repository;

import io.fabric8.kubernetes.api.model.Quantity;

import java.util.HashMap;
import java.util.Map;

public class RepositoryHelper {

    public static Map<String, String> getLabels(String stack) {
        return new HashMap<>() {{
            put("fint.stack", stack);
            put("fint.role", "consumer");
        }};
    }

    public static String getXmx(Quantity limit) {
        double memoryLimit = Double.parseDouble(limit.getAmount().replace("Gi", ""));

        return Long.toString(Math.round((memoryLimit * 0.9) - 0.5));
    }
}
