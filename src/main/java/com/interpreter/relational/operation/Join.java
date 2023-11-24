package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;
import com.interpreter.relational.exception.BaseException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.interpreter.relational.util.AttributeProcessor.extractAttribute;

public class Join {
    public static Set<Multimap<String, String>> join(Pair<String, Set<Multimap<String, String>>> relation1,
                                                     Pair<String, Set<Multimap<String, String>>> relation2,
                                                     List<String> uniqueAttributes) {
        String relName1 = relation1.getLeft();
        String relName2 = relation2.getLeft();
        List<String> relationNames = List.of(relName1, relName2);

        Set<Multimap<String, String>> joinedRelation = new HashSet<>();
        Set<Multimap<String, String>> product = CartesianProduct.product(relation1.getRight(), relation2.getRight());
        if (uniqueAttributes.isEmpty()) {
            return product;
        } else {
            for (Multimap<String, String> multimap : product) {
                int joinedAttributes = 0;
                for (String attribute : uniqueAttributes) {
                    String finalAttribute = extractAttribute(relationNames, attribute);
                    if (!multimap.containsKey(finalAttribute)) {
                        throw new BaseException("Attribute " + finalAttribute +
                                " of relation " + relationNames + " doesn't exist");
                    }

                    Collection<String> content = multimap.get(finalAttribute);
                    if (content.stream().distinct().count() == 1 && content.size() > 1) {
                        joinedAttributes++;
                        for (String first : new HashSet<>(content)) {
                            multimap.remove(finalAttribute, first);
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
