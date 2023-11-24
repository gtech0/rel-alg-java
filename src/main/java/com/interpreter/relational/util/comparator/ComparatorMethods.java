package com.interpreter.relational.util.comparator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class ComparatorMethods {

    static private final Map<String, String> operations = Map.ofEntries(
            entry("=", "isEqual"),
            entry("!=", "isNotEqual"),
            entry("<=", "lessThanOrEqual"),
            entry(">=", "greaterThanOrEqual"),
            entry("<", "lessThan"),
            entry(">", "greaterThan")
    );

    public static boolean isQuoted(String value) {
        return value.startsWith("\"")
                && value.endsWith("\"")
                && value.chars().filter(c -> c == '\"').count() == 2;
    }

    public static boolean numericComparator(String token, boolean notCheck, String value1, String value2) {
        if (!(isANumber(value1) && isANumber(value2))) {
            return false;
        }

        double currentVal = Double.parseDouble(value1);
        double newVal = Double.parseDouble(value2);

        return NumericComparatorStrategy
                .valueOf(operations.get(token))
                .execute(notCheck, currentVal, newVal);
    }

    public static boolean dateComparator(String token, boolean notCheck, String value1, String value2) {
        String currentStrVal = isQuoted(value1) ? removeQuotes(value1) : value1;
        String newStrVal = isQuoted(value2) ? removeQuotes(value2) : value2;

        if (!(isADate(currentStrVal) && isADate(newStrVal))) {
            return false;
        }

        LocalDate currentVal = LocalDate.parse(currentStrVal);
        LocalDate newVal = LocalDate.parse(newStrVal);

        return DateComparatorStrategy
                .valueOf(operations.get(token))
                .execute(notCheck, currentVal, newVal);
    }

    public static boolean isEqualOrNotEqualString(String token, boolean notCheck, String value1, String value2) {
        String currentStrVal = isQuoted(value1) ? removeQuotes(value1) : value1;
        String newStrVal = isQuoted(value2) ? removeQuotes(value2) : value2;
        return (Objects.equals(token, "=")
                && (Objects.equals(currentStrVal, newStrVal) && !notCheck
                || !Objects.equals(currentStrVal, newStrVal) && notCheck))

                || (Objects.equals(token, "!=")
                && (!Objects.equals(currentStrVal, newStrVal) && !notCheck
                || Objects.equals(currentStrVal, newStrVal) && notCheck));
    }

    public static String removeQuotes(String value) {
        return value.replaceAll("\"", "");
    }

    public static boolean isANumber(String value) {
        if (value == null) {
            return false;
        }
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isADate(String value) {
        if (value == null) {
            return false;
        }
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
