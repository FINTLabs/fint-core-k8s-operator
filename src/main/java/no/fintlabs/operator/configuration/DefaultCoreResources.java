package no.fintlabs.operator.configuration;

import io.fabric8.kubernetes.api.model.Quantity;

import java.util.HashMap;
import java.util.Map;

public class DefaultCoreResources {
    public static Map<String, Map<String, Quantity>> getRequest() {
        return new HashMap<>() {{
            put("small", new HashMap<>() {{
                put("memory", new Quantity("2Gi"));
                put("cpu", new Quantity("250m"));
            }});
            put("medium", new HashMap<>() {{
                put("memory", new Quantity("5Gi"));
                put("cpu", new Quantity("500m"));
            }});
            put("large", new HashMap<>() {{
                put("memory", new Quantity("10Gi"));
                put("cpu", new Quantity("1"));
            }});
        }};
    }

    public static Map<String, Map<String, Quantity>> getLimit() {
        return new HashMap<>() {{
            put("small", new HashMap<>() {{
                put("memory", new Quantity("4Gi"));
                put("cpu", new Quantity("1"));
            }});
            put("medium", new HashMap<>() {{
                put("memory", new Quantity("10Gi"));
                put("cpu", new Quantity("2"));
            }});
            put("large", new HashMap<>() {{
                put("memory", new Quantity("15Gi"));
                put("cpu", new Quantity("2"));
            }});
        }};
    }
}
