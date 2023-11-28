package com.interpreter.relational.repository;

import com.google.common.collect.Multimap;
import com.interpreter.relational.exception.BaseException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
public class TestRepository {

    Map<String, Set<Multimap<String, String>>> relationMap = new HashMap<>();

    public void storeInMap(Map<String, Set<Multimap<String, String>>> newMap) {
        relationMap.putAll(newMap);
    }

    public Map<String, Set<Multimap<String, String>>> findAll() {
        return relationMap;
    }

    public Set<Multimap<String, String>> getResult() {
        Set<Multimap<String, String>> result = relationMap.get("");

        if (result != null) {
            return result;
        } else {
            throw new BaseException("Result is null");
        }
    }

    public Set<Multimap<String, String>> getRelation(String key) {
        Set<Multimap<String, String>> relation = relationMap.get(key);
        if (relation == null)
            throw new BaseException("Relation " + key + " is null");
        return relation;
    }

    public void clear() {
        relationMap.clear();
    }
}
