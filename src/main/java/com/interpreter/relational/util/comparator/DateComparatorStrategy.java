package com.interpreter.relational.util.comparator;

import java.time.LocalDate;

public enum DateComparatorStrategy {
    isEqual("=") {
        @Override
        boolean execute(boolean notCheck, LocalDate currentVal, LocalDate newVal) {
            return currentVal.isEqual(newVal) && !notCheck || !currentVal.isEqual(newVal) && notCheck;
        }
    },
    isNotEqual("!=") {
        @Override
        boolean execute(boolean notCheck, LocalDate currentVal, LocalDate newVal) {
            return !currentVal.isEqual(newVal) && !notCheck || currentVal.isEqual(newVal) && notCheck;
        }
    },
    lessThanOrEqual("<=") {
        @Override
        boolean execute(boolean notCheck, LocalDate currentVal, LocalDate newVal) {
            return (currentVal.isBefore(newVal) || currentVal.isEqual(newVal)) && !notCheck
                    || currentVal.isAfter(newVal)  && notCheck;
        }
    },
    greaterThanOrEqual(">=") {
        @Override
        boolean execute(boolean notCheck, LocalDate currentVal, LocalDate newVal) {
            return (currentVal.isAfter(newVal) || currentVal.isEqual(newVal)) && !notCheck
                    || currentVal.isBefore(newVal) && notCheck;
        }
    },
    lessThan("<") {
        @Override
        boolean execute(boolean notCheck, LocalDate currentVal, LocalDate newVal) {
            return currentVal.isBefore(newVal) && !notCheck
                    || (currentVal.isAfter(newVal) || currentVal.isEqual(newVal)) && notCheck;
        }
    },
    greaterThan(">") {
        @Override
        boolean execute(boolean notCheck, LocalDate currentVal, LocalDate newVal) {
            return currentVal.isAfter(newVal) && !notCheck
                    || (currentVal.isBefore(newVal) || currentVal.isEqual(newVal)) && notCheck;
        }
    };

    DateComparatorStrategy(String operation) {}

    abstract boolean execute(boolean notCheck, LocalDate currentVal, LocalDate newVal);
}
