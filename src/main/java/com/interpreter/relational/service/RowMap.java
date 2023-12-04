package com.interpreter.relational.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RowMap extends HashMap<String, Collection<String>> {

    public Collection<String> get(String key) {
        Collection<String> values = super.get(key);
        if (values == null) {
            return List.of();
        }
        return values;
    }

    public void remove(String key, String value) {
        get(key).remove(value);
    }
}
