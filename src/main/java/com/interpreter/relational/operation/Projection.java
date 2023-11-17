package com.interpreter.relational.operation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.interpreter.relational.exception.BaseException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.interpreter.relational.util.AttributeClass.extractAttribute;

public class Projection {
    public static Set<Multimap<String, String>> projection(Pair<String, Set<Multimap<String, String>>> relation,
                                                           List<String> attributes) {
        return relation.getRight()
                .stream()
                .map(map -> {
                    Multimap<String, String> newMap = ArrayListMultimap.create();
                    attributes.forEach(
                            attribute -> {
                                String finalAttribute = extractAttribute(List.of(relation.getLeft()), attribute);

                                if (map.containsKey(finalAttribute)) {
                                    map.get(finalAttribute).forEach(
                                            value -> newMap.put(finalAttribute, value)
                                    );
                                } else {
                                    throw new BaseException("Attribute " + finalAttribute +
                                            " of relation " + relation.getLeft() + " doesn't exist");
                                }
                            }
                    );
                    return newMap;
                })
                .collect(Collectors.toSet());
    }
}
