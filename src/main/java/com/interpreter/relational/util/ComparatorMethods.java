package com.interpreter.relational.util;

import java.util.Objects;

public class ComparatorMethods {
    public static boolean lessThanOrEqual(String token, boolean notCheck, double currentVal, double newVal) {
        return Objects.equals(token, "<=")
                && (currentVal <= newVal && !notCheck || currentVal > newVal && notCheck);
    }

    public static boolean greaterThanOrEqual(String token, boolean notCheck, double currentVal, double newVal) {
        return Objects.equals(token, ">=")
                && (currentVal >= newVal && !notCheck || currentVal < newVal && notCheck);
    }

    public static boolean lessThan(String token, boolean notCheck, double currentVal, double newVal) {
        return Objects.equals(token, "<")
                && (currentVal < newVal && !notCheck || currentVal >= newVal && notCheck);
    }

    public static boolean greaterThan(String token, boolean notCheck, double currentVal, double newVal) {
        return Objects.equals(token, ">")
                && (currentVal > newVal && !notCheck || currentVal <= newVal && notCheck);
    }

    public static boolean isEqualOrNotEqualNumeric(String token, boolean notCheck, double currentVal, double newVal) {
        return (Objects.equals(token, "=")
                && (currentVal == newVal && !notCheck || currentVal != newVal && notCheck))

                || (Objects.equals(token, "!=")
                && (currentVal != newVal && !notCheck || currentVal == newVal && notCheck));
    }

    public static boolean isNumericComparator(String token, boolean notCheck, double currentVal, double newVal) {
        return isEqualOrNotEqualNumeric(token, notCheck, currentVal, newVal)
                || greaterThan(token, notCheck, currentVal, newVal)
                || lessThan(token, notCheck, currentVal, newVal)
                || greaterThanOrEqual(token, notCheck, currentVal, newVal)
                || lessThanOrEqual(token, notCheck, currentVal, newVal);
    }

    public static boolean isEqualOrNotEqualString(String token, boolean notCheck, String currentVal, String newVal) {
        return (Objects.equals(token, "=") || Objects.equals(token, "!="))
                && isQuoted(newVal)
                && stringValuesEqual(token, notCheck, currentVal, newVal);
    }

    public static boolean isQuoted(String value2) {
        return value2.startsWith("\"")
                && value2.endsWith("\"")
                && value2.chars().filter(c -> c == '\"').count() == 2;
    }

    public static boolean stringValuesEqual(String token, boolean notCheck, String value1, String value2) {
        String currentStrVal = value1.replaceAll("\"", "");
        String newStrVal = value2.replaceAll("\"", "");
        return (Objects.equals(token, "=")
                && (Objects.equals(currentStrVal, newStrVal) && !notCheck
                || !Objects.equals(currentStrVal, newStrVal) && notCheck))

                ||

                (Objects.equals(token, "!=")
                && (!Objects.equals(currentStrVal, newStrVal) && !notCheck
                || Objects.equals(currentStrVal, newStrVal) && notCheck));
    }

    public static boolean isANumber(String value) {
        if (value == null) {
            return false;
        }
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
