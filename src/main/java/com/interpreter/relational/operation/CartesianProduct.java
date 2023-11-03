package com.interpreter.relational.operation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CartesianProduct {
    public static Set<Multimap<String, String>> product(Set<Multimap<String, String>> relation1,
                                                        Set<Multimap<String, String>> relation2) {
        Set<List<Multimap<String, String>>> cartMap = Sets.cartesianProduct(relation1, relation2);
        return cartMap
                .stream()
                .map(maps -> {
                    Multimap<String, String> newMap = ArrayListMultimap.create();
                    maps.forEach(newMap::putAll);
                    return newMap;
                })
                .collect(Collectors.toSet());
    }
}
