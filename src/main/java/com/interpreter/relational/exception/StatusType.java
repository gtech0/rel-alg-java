package com.interpreter.relational.exception;

public enum StatusType {
    OK("OK"),
    WA("Wrong Answer"),
    CE("Compilation Error"),
    RT("Runtime Error"),
    TL("Time limit exceeded"),
    PE("Presentation Error"),
    ML("Memory limit exceeded"),
    SE("Security exception");

    private final String status;
    StatusType(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
