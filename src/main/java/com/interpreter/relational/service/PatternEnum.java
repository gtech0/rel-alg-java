package com.interpreter.relational.service;

import java.util.regex.Pattern;

public enum PatternEnum {
    UNION("UNION\\s+.*AND\\s+.*"),
    DIFFERENCE("DIFFERENCE\\s+.*AND\\s+.*"),
    TIMES("TIMES\\s+.*AND\\s+.*"),
    PROJECT("PROJECT\\s+.*OVER\\s+.*"),
    SELECT("SELECT\\s+.*WHERE\\s+.*"),
    INTERSECT("INTERSECT\\s+.*AND\\s+.*"),
    DIVIDE("DIVIDE\\s+.*BY\\s+.*OVER\\s+.*"),
    JOIN("JOIN\\s+.*AND\\s+.*OVER\\s+.*"),
    GET("GET\\s+.*"),
    ANSWER("ANSWER\\s+.*");

    private final String pattern;

    PatternEnum(String pattern) {
        this.pattern = pattern;
    }

    public boolean patternMatches(String s) {
        return Pattern.compile(pattern).matcher(s).matches();
    }
}
