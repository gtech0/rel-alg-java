package com.interpreter.relational.util.converter;

public interface GenericConverter<A, B> {
    B convert(A convertible);
}
