package com.interpreter.relational.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ConversionMethods {
    public Set<Map<String, Collection<String>>> multimapToMapConversion(Set<Multimap<String, String>> resultSet) {
        Set<Map<String, Collection<String>>> mappedSet = new HashSet<>();
        for (Multimap<String, String> resultMap : resultSet) {
            Map<String, Collection<String>> map = resultMap.asMap().entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            (entry) -> ImmutableList.copyOf(entry.getValue())
                    ));
            mappedSet.add(map);
        }
        return mappedSet;
    }

    public Set<Multimap<String, String>> mapToMultimapConversion(Set<Map<String, Collection<String>>> resultSet) {
        Set<Multimap<String, String>> mappedSet = new HashSet<>();
        for (Map<String, Collection<String>> resultMap : resultSet) {
            Multimap<String, String> map = ArrayListMultimap.create();
            resultMap.forEach(
                    (k, v) -> v.forEach(
                            value -> map.put(k, value)
                    )
            );
            mappedSet.add(map);
        }
        return mappedSet;
    }
}
