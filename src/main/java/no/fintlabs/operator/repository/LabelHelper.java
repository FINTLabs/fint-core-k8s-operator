package no.fintlabs.operator.repository;

import java.util.HashMap;
import java.util.Map;

public class LabelHelper {

    public static Map<String, String> getLabels(String stack) {
        return new HashMap<>() {{
            put("fint.stack", stack);
            put("fint.role", "consumer");
        }};
    }
}
