package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public class Division {
    public static Set<Multimap<String, String>> division(Pair<String, Set<Multimap<String, String>>> relation1,
                                                         Pair<String, Set<Multimap<String, String>>> relation2,
                                                         List<String> uniqueAttributes) {
        Set<Multimap<String, String>> temp1 = CartesianProduct
                .product(Projection.projection(relation1, uniqueAttributes), relation2.getRight());
        Pair<String, Set<Multimap<String, String>>> temp2 =
                new ImmutablePair<>("temp2", Sets.difference(temp1, relation1.getRight()));
        Set<Multimap<String, String>> temp3 = Projection.projection(temp2, uniqueAttributes);
        return Sets.difference(Projection.projection(relation1, uniqueAttributes), temp3);
    }
}
