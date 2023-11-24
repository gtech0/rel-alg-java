package com.interpreter.relational.util.comparator;

public enum NumericComparatorStrategy {
    isEqual("=") {
        @Override
        boolean execute(boolean notCheck, double currentVal, double newVal) {
            return currentVal == newVal && !notCheck || currentVal != newVal && notCheck;
        }
    },
    isNotEqual("!=") {
        @Override
        boolean execute(boolean notCheck, double currentVal, double newVal) {
            return currentVal != newVal && !notCheck || currentVal == newVal && notCheck;
        }
    },
    lessThanOrEqual("<=") {
        @Override
        boolean execute(boolean notCheck, double currentVal, double newVal) {
            return currentVal <= newVal && !notCheck || currentVal > newVal && notCheck;
        }
    },
    greaterThanOrEqual(">=") {
        @Override
        boolean execute(boolean notCheck, double currentVal, double newVal) {
            return currentVal >= newVal && !notCheck || currentVal < newVal && notCheck;
        }
    },
    lessThan("<") {
        @Override
        boolean execute(boolean notCheck, double currentVal, double newVal) {
            return currentVal < newVal && !notCheck || currentVal >= newVal && notCheck;
        }
    },
    greaterThan(">") {
        @Override
        boolean execute(boolean notCheck, double currentVal, double newVal) {
            return currentVal > newVal && !notCheck || currentVal <= newVal && notCheck;
        }
    };

    NumericComparatorStrategy(String operator) {}

    abstract boolean execute(boolean notCheck, double currentVal, double newVal);
}
