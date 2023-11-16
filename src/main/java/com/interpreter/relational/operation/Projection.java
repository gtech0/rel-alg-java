package com.interpreter.relational.operation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.interpreter.relational.exception.BaseException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Projection {
    public static Set<Multimap<String, String>> projection(Pair<String, Set<Multimap<String, String>>> relation,
                                                           List<String> attributes) {
        return relation.getRight()
                .stream()
                .map(map -> {
                    Multimap<String, String> newMap = ArrayListMultimap.create();
                    attributes.forEach(
                            attribute -> {
                                String finalAttribute = attribute;
                                if (attribute.contains(".")) {
                                    String[] relationAttribute = attribute.split("\\.");
                                    if (relationAttribute.length == 2 &&
                                            Objects.equals(relationAttribute[0], relation.getLeft())) {
                                        finalAttribute = relationAttribute[1];
                                    } else {
                                        throw new BaseException("Relation " + relation.getLeft() + " doesn't exist");
                                    }
                                }

                                if (map.containsKey(finalAttribute)) {
                                    map.get(finalAttribute).forEach(
                                            value -> newMap.put(attribute, value)
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
