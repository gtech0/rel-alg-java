package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Join {
    public static Set<Multimap<String, String>> join(Set<Multimap<String, String>> relation1,
                                                     Set<Multimap<String, String>> relation2,
                                                     List<String> uniqueAttributes) {
        Set<Multimap<String, String>> joinedRelation = new HashSet<>();
        Set<Multimap<String, String>> product = CartesianProduct.product(relation1, relation2);
        if (uniqueAttributes.isEmpty()) {
            return product;
        } else {
            for (Multimap<String, String> multimap : product) {
                int joinedAttributes = 0;
                for (String attribute : uniqueAttributes) {
                    Collection<String> content = multimap.get(attribute);
                    if (content.stream().distinct().count() == 1 && content.size() > 1) {
                        joinedAttributes++;
                        for (String first : new HashSet<>(content)) {
                            multimap.remove(attribute, first);
                        }
                    }
                }

                if (joinedAttributes == uniqueAttributes.size()) {
                    joinedRelation.add(multimap);
                }
            }
        }
        return joinedRelation;
    }
}
