package com.interpreter.relational.util;

public interface GenericConverter<A, B> {
    B convert(A convertible);
}
