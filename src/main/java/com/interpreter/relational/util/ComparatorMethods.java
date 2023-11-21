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

    public static boolean numericValuesEqual(String token, boolean notCheck, double currentVal, double newVal) {
        return (Objects.equals(token, "=")
                && (currentVal == newVal && !notCheck || currentVal != newVal && notCheck))

                || (Objects.equals(token, "!=")
                && (currentVal != newVal && !notCheck || currentVal == newVal && notCheck));
    }

    public static boolean stringValuesEqual(String token, boolean notCheck, String currentStrVal, String newStrVal) {
        return (Objects.equals(token, "=")
                && (Objects.equals(currentStrVal, newStrVal) && !notCheck
                || !Objects.equals(currentStrVal, newStrVal) && notCheck))

                || (Objects.equals(token, "!=")
                && (!Objects.equals(currentStrVal, newStrVal) && !notCheck
                || Objects.equals(currentStrVal, newStrVal) && notCheck));
    }
}
