package com.interpreter.relational.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UtilityMethods {
    public boolean isQuoted(String value) {
        return value.startsWith("\"")
                && value.endsWith("\"")
                && value.chars().filter(c -> c == '\"').count() == 2;
    }
}
