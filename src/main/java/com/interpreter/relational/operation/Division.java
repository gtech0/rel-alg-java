package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.exception.BaseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.interpreter.relational.util.AttributeProcessor.extractAttribute;

public class Division {
    public static Set<Multimap<String, String>> division(Pair<String, Set<Multimap<String, String>>> relation1,
                                                         Pair<String, Set<Multimap<String, String>>> relation2,
                                                         List<String> commonAttributes) {
        String relName1 = relation1.getLeft();
        String relName2 = relation2.getLeft();
        List<String> relationNames = List.of(relName1, relName2);
        Set<Multimap<String, String>> commonRelation = Stream
                .concat(relation1.getRight().stream(), relation2.getRight().stream())
                .collect(Collectors.toSet());
        for (Multimap<String, String> multimap : commonRelation)
        {
            for (String attribute : commonAttributes) {
                String finalAttribute = extractAttribute(relationNames, attribute);
                if (!multimap.containsKey(finalAttribute)) {
                    throw new BaseException("Attribute " + finalAttribute +
                            " of relations " + relationNames + " doesn't exist");
                }
            }
        }

        Pair<String, Set<Multimap<String, String>>> finalRelation1 = new ImmutablePair<>("", relation1.getRight());
        Set<Multimap<String, String>> temp1 = CartesianProduct
                .product(Projection.projection(finalRelation1, commonAttributes), relation2.getRight());
        Pair<String, Set<Multimap<String, String>>> temp2 =
                new ImmutablePair<>("", Sets.difference(temp1, finalRelation1.getRight()));
        Set<Multimap<String, String>> temp3 = Projection.projection(temp2, commonAttributes);
        return Sets.difference(Projection.projection(finalRelation1, commonAttributes), temp3);
    }
}
