package com.interpreter.relational.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.lang.reflect.Field;

public class ObjectToMultimap {
    public static Multimap<String, String> convertUsingReflection(Object object) throws IllegalAccessException {
        Multimap<String, String> map = ArrayListMultimap.create();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), (String) field.get(object));
        }

        return map;
    }
}
