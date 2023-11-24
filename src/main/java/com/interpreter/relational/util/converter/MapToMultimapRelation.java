package com.interpreter.relational.util.converter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapToMultimapRelation implements GenericConverter<Set<Map<String, Collection<String>>>, Set<Multimap<String, String>>> {

    @Override
    public Set<Multimap<String, String>> convert(Set<Map<String, Collection<String>>> convertible) {
        Set<Multimap<String, String>> mappedSet = new HashSet<>();
        for (Map<String, Collection<String>> resultMap : convertible) {
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
