package no.fintlabs.operator.repository;

import java.util.HashMap;
import java.util.Map;

public class RepositoryHelper {

    public static Map<String, String> getLabels(String orgId, String component) {
        return new HashMap<>() {{
            put("fint.stack", component);
            put("fint.role", "consumer");
            put("fint.org", orgId);
        }};
    }

    public static String getXmx(String limit) {
        String unit = limit.replaceAll("\\d+", "").replace("i", "");
        double memoryLimit = Double.parseDouble(limit.replaceAll("[a-zA-Z]+", ""));

        return String.format("%d%s", Math.round((memoryLimit * 0.5) - 0.5), unit);
    }
}
