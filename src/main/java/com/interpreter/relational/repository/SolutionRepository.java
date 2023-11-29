package com.interpreter.relational.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SolutionRepository {

    private final ObjectMapper mapper;
    Map<String, Set<Multimap<String, String>>> solutionResult = new HashMap<>();
    Map<String, Map<String, Set<Multimap<String, String>>>> solutionRelations = new HashMap<>();
    Multimap<String, String> problem = ArrayListMultimap.create();

    public void storeInSolutionResult(Map<String, Set<Multimap<String, String>>> newMap) {
        solutionResult.putAll(newMap);
    }

    public void storeInSolutionRelations(Map<String, Map<String, Set<Multimap<String, String>>>> newMap) {
        solutionRelations.putAll(newMap);
    }

    public void storeInProblem(Multimap<String, String> newMap) {
        problem.putAll(newMap);
    }

    public Set<Multimap<String, String>> getSolutionResult(String key) {
        Set<Multimap<String, String>> relation = solutionResult.get(key);
        if (relation == null)
            throw new BaseException("Relation " + key + " is null", StatusType.CE.toString());
        return relation;
    }

    public Map<String, Set<Multimap<String, String>>> getSolutionRelations(String key) {
        Map<String, Set<Multimap<String, String>>> relation = solutionRelations.get(key);
        if (relation == null)
            throw new BaseException("Relation " + key + " is null", StatusType.CE.toString());
        return relation;
    }

    public Collection<String> getProblemCollection(String key) {
        Collection<String> problemCollection = problem.get(key);
        if (problemCollection.isEmpty())
            throw new BaseException("No " + key + " problem exist", StatusType.CE.toString());
        return problem.get(key);
    }

    public void initialize() throws IOException {
        String resultPath = "classpath:solutionResult.json";
        String relationPath = "classpath:solutionRelations.json";
        String problemPath = "classpath:problem.json";

        Map<String, Set<Multimap<String, String>>> solutionResult = mapper
                .readValue(ResourceUtils.getFile(resultPath), new TypeReference<>() {});

        Map<String, Map<String, Set<Multimap<String, String>>>> solutionRelations = mapper
                .readValue(ResourceUtils.getFile(relationPath), new TypeReference<>() {});

        Multimap<String, String> problem = mapper
                .readValue(ResourceUtils.getFile(problemPath), new TypeReference<>() {});

        storeInSolutionResult(solutionResult);
        storeInSolutionRelations(solutionRelations);
        storeInProblem(problem);
    }
}
