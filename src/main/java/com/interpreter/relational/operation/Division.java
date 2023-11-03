package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class Division {
    public static Set<Multimap<String, String>> division(Set<Multimap<String, String>> relation1,
                                                         Set<Multimap<String, String>> relation2,
                                                         List<String> uniqueAttributes) {
        Set<Multimap<String, String>> temp1 = CartesianProduct.product(Projection.projection(relation1, uniqueAttributes), relation2);
        Set<Multimap<String, String>> temp2 = Sets.difference(temp1, relation1);
        Set<Multimap<String, String>> temp3 = Projection.projection(temp2, uniqueAttributes);
        Set<Multimap<String, String>> temp4 = Sets.difference(Projection.projection(relation1, uniqueAttributes), temp3);
        return temp4;
    }
}
