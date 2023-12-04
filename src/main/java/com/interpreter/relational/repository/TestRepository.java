package com.interpreter.relational.repository;

import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.service.RowMap;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
public class TestRepository {

    Map<String, Set<RowMap>> relationMap = new HashMap<>();

    public void storeInMap(Map<String, Set<RowMap>> newMap) {
        relationMap.putAll(newMap);
    }

    public Map<String, Set<RowMap>> findAll() {
        return relationMap;
    }

    public Set<RowMap> getResult() {
        Set<RowMap> result = relationMap.get("");

        if (result != null) {
            return result;
        } else {
            throw new BaseException("Result is null", StatusType.CE.toString());
        }
    }

    public Set<RowMap> getRelation(String key) {
        Set<RowMap> relation = relationMap.get(key);
        if (relation == null)
            throw new BaseException("Relation " + key + " is null", StatusType.CE.toString());
        return relation;
    }

    public void clear() {
        relationMap.clear();
    }
}
