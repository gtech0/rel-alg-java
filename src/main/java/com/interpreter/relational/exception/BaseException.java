package com.interpreter.relational.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseException extends RuntimeException {
    private final String type;
    public BaseException(String message, String type) {
        super(message);
        this.type = type;
    }
}
