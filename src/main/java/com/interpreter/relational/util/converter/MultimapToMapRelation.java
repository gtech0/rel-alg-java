package com.interpreter.relational.util.converter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MultimapToMapRelation implements GenericConverter<Set<Multimap<String, String>>, Set<Map<String, Collection<String>>>> {

    @Override
    public Set<Map<String, Collection<String>>> convert(Set<Multimap<String, String>> convertible) {
        Set<Map<String, Collection<String>>> mappedSet = new HashSet<>();
        for (Multimap<String, String> resultMap : convertible) {
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
}
