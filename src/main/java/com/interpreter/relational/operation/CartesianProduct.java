package com.interpreter.relational.operation;

import com.google.common.collect.Sets;
import com.interpreter.relational.service.RowMap;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CartesianProduct {
    public static Set<RowMap> product(Set<RowMap> relation1,
                                      Set<RowMap> relation2) {
        Set<List<RowMap>> cartMap = Sets.cartesianProduct(relation1, relation2);
        return cartMap
                .stream()
                .map(maps -> {
                    RowMap newMap = new RowMap();
                    maps.forEach(newMap::putAll);
                    return newMap;
                })
                .collect(Collectors.toSet());
    }
}
