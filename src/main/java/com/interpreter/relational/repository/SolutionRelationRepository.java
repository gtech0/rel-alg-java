package com.interpreter.relational.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.interpreter.relational.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SolutionRelationRepository {
    private final ObjectMapper mapper;
    Map<String, Set<Multimap<String, String>>> solutionResult = new HashMap<>();
    Map<String, Map<String, Set<Multimap<String, String>>>> solutionRelations = new HashMap<>();

    public void storeInSolutionResult(Map<String, Set<Multimap<String, String>>> newMap) {
        solutionResult.putAll(newMap);
    }

    public void storeInSolutionRelations(Map<String, Map<String, Set<Multimap<String, String>>>> newMap) {
        solutionRelations.putAll(newMap);
    }

    public Set<Multimap<String, String>> getSolutionResult(String key) {
        Set<Multimap<String, String>> relation = solutionResult.get(key);
        if (relation == null)
            throw new BaseException("Relation is null");
        return relation;
    }

    public Map<String, Set<Multimap<String, String>>> getSolutionRelations(String key) {
        Map<String, Set<Multimap<String, String>>> relation = solutionRelations.get(key);
        if (relation == null)
            throw new BaseException("Relation is null");
        return relation;
    }

    public void initialize() throws IOException {
        Map<String, Set<Multimap<String, String>>> solutionResult =
                mapper.readValue(ResourceUtils.getFile("classpath:solutionResult.json"),
                        new TypeReference<>() {});

        Map<String, Map<String, Set<Multimap<String, String>>>> solutionRelations =
                mapper.readValue(ResourceUtils.getFile("classpath:solutionRelations.json"),
                        new TypeReference<>() {});

        storeInSolutionResult(solutionResult);
        storeInSolutionRelations(solutionRelations);
    }
}
