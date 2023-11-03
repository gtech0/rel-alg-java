package com.interpreter.relational.operation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Projection {
    public static Set<Multimap<String, String>> projection(Set<Multimap<String, String>> relation,
                                                           List<String> attributes) {
        return relation
                .stream()
                .map(map -> {
                    Multimap<String, String> newMap = ArrayListMultimap.create();
                    attributes.forEach(
                            attribute -> map.get(attribute).forEach(
                                    value -> newMap.put(attribute, value)
                            )
                    );
                    return newMap;
                })
                .collect(Collectors.toSet());
    }
}
