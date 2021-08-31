package no.fintlabs.operator.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepositoryHelper {

    public static Map<String, String> getLabels(String orgId, String component) {
        return new HashMap<>() {{
            put("fint.stack", component);
            put("fint.role", "consumer");
            put("fint.org", orgId);
        }};
    }

    public static String getXmx(String limit) {
        return parseSize(limit)
                .multiply(new BigDecimal("0.9"))
                .subtract(new BigDecimal("268435456"))
                .divide(new BigDecimal("1048576"), RoundingMode.HALF_UP)
                .toBigInteger() + "M";
    }

    public static BigDecimal parseSize(String size) {
        final Matcher matcher = Pattern.compile("([0-9.e]+)([EPTGMkK]i?)?").matcher(size);
        if (matcher.matches()) {
            return new BigDecimal(matcher.group(1)).multiply(new BigDecimal(parseUnit(matcher.group(2))));
        }
        throw new IllegalArgumentException(size);
    }

    public static BigInteger parseUnit(String unit) {
        if (unit == null) {
            return BigInteger.ONE;
        }
        switch (unit) {
            default:
                return BigInteger.ONE;
            case "Ki":
                return BigInteger.TWO.pow(10);
            case "Mi":
                return BigInteger.TWO.pow(20);
            case "Gi":
                return BigInteger.TWO.pow(30);
            case "Ti":
                return BigInteger.TWO.pow(40);
            case "Pi":
                return BigInteger.TWO.pow(50);
            case "Ei":
                return BigInteger.TWO.pow(60);
            case "k":
                return BigInteger.TEN.pow(3);
            case "M":
                return BigInteger.TEN.pow(6);
            case "G":
                return BigInteger.TEN.pow(9);
            case "T":
                return BigInteger.TEN.pow(12);
            case "P":
                return BigInteger.TEN.pow(15);
            case "E":
                return BigInteger.TEN.pow(18);
        }
    }
}
