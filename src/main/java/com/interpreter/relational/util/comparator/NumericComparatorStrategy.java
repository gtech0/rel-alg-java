package com.interpreter.relational.util.comparator;

public enum NumericComparatorStrategy {
    isEqual("=") {
        @Override
        boolean execute(double currentVal, double newVal) {
            return currentVal == newVal;
        }
    },
    isNotEqual("!=") {
        @Override
        boolean execute(double currentVal, double newVal) {
            return currentVal != newVal;
        }
    },
    lessThanOrEqual("<=") {
        @Override
        boolean execute(double currentVal, double newVal) {
            return currentVal <= newVal;
        }
    },
    greaterThanOrEqual(">=") {
        @Override
        boolean execute(double currentVal, double newVal) {
            return currentVal >= newVal;
        }
    },
    lessThan("<") {
        @Override
        boolean execute(double currentVal, double newVal) {
            return currentVal < newVal;
        }
    },
    greaterThan(">") {
        @Override
        boolean execute(double currentVal, double newVal) {
            return currentVal > newVal;
        }
    };

    NumericComparatorStrategy(String operator) {}

    abstract boolean execute(double currentVal, double newVal);
}
