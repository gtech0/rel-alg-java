package com.interpreter.relational.util.comparator;

import java.time.LocalDate;

public enum DateComparatorStrategy {
    isEqual("=") {
        @Override
        boolean execute(LocalDate currentVal, LocalDate newVal) {
            return currentVal.isEqual(newVal);
        }
    },
    isNotEqual("!=") {
        @Override
        boolean execute(LocalDate currentVal, LocalDate newVal) {
            return !currentVal.isEqual(newVal);
        }
    },
    lessThanOrEqual("<=") {
        @Override
        boolean execute(LocalDate currentVal, LocalDate newVal) {
            return currentVal.isBefore(newVal) || currentVal.isEqual(newVal);
        }
    },
    greaterThanOrEqual(">=") {
        @Override
        boolean execute(LocalDate currentVal, LocalDate newVal) {
            return currentVal.isAfter(newVal) || currentVal.isEqual(newVal);
        }
    },
    lessThan("<") {
        @Override
        boolean execute(LocalDate currentVal, LocalDate newVal) {
            return currentVal.isBefore(newVal);
        }
    },
    greaterThan(">") {
        @Override
        boolean execute(LocalDate currentVal, LocalDate newVal) {
            return currentVal.isAfter(newVal);
        }
    };

    DateComparatorStrategy(String operation) {}

    abstract boolean execute(LocalDate currentVal, LocalDate newVal);
}
