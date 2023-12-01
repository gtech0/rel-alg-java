package com.interpreter.relational.util.comparator;

import com.interpreter.relational.dto.ComparatorParams;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

import static com.interpreter.relational.util.UtilityMethods.*;
import static java.util.Map.entry;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

public class ComparatorMethods {

    static private final Map<String, String> operations = Map.ofEntries(
            entry("=", "isEqual"),
            entry("!=", "isNotEqual"),
            entry("<=", "lessThanOrEqual"),
            entry(">=", "greaterThanOrEqual"),
            entry("<", "lessThan"),
            entry(">", "greaterThan")
    );

    private static String removeQuotes(String value) {
        return value.replaceAll("\"", "");
    }

    public static boolean numericComparator(ComparatorParams params) {
        if (!(isCreatable(params.getOperandLeft()) && isCreatable(params.getOperandRight()))) {
            return false;
        }

        double currentVal = Double.parseDouble(params.getOperandLeft());
        double newVal = Double.parseDouble(params.getOperandRight());

        return NumericComparatorStrategy
                .valueOf(operations.get(params.getToken()))
                .execute(currentVal, newVal);
    }

    public static boolean dateComparator(ComparatorParams params) {
        String currentStrVal = isQuoted(params.getOperandLeft()) ? removeQuotes(params.getOperandLeft()) : params.getOperandLeft();
        String newStrVal = isQuoted(params.getOperandRight()) ? removeQuotes(params.getOperandRight()) : params.getOperandRight();

        if (!(isADate(currentStrVal) && isADate(newStrVal))) {
            return false;
        }

        LocalDate currentVal = LocalDate.parse(currentStrVal);
        LocalDate newVal = LocalDate.parse(newStrVal);

        return DateComparatorStrategy
                .valueOf(operations.get(params.getToken()))
                .execute(currentVal, newVal);
    }

    public static boolean isEqualOrNotEqualString(ComparatorParams params) {
        String currentStrVal = isQuoted(params.getOperandLeft()) ? removeQuotes(params.getOperandLeft()) : params.getOperandLeft();
        String newStrVal = isQuoted(params.getOperandRight()) ? removeQuotes(params.getOperandRight()) : params.getOperandRight();
        return ((Objects.equals(params.getToken(), "=") && Objects.equals(currentStrVal, newStrVal))
                || (Objects.equals(params.getToken(), "!=") && !Objects.equals(currentStrVal, newStrVal)));
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
