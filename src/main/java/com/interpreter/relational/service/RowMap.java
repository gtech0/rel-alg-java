package com.interpreter.relational.service;

import java.util.HashMap;
import java.util.List;

public class RowMap extends HashMap<String, List<String>> {

    public List<String> get(String key) {
        List<String> values = super.get(key);
        if (values == null) {
            return List.of();
        }
        return values;
    }

    public void remove(String key, String value) {
        get(key).remove(value);
    }
}
