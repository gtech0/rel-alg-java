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

import java.io.File;
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
        File result = ResourceUtils.getFile("classpath:solutionResult.json");
        File relation = ResourceUtils.getFile("classpath:solutionRelations.json");
        File problem = ResourceUtils.getFile("classpath:problem.json");

        Map<String, Set<Multimap<String, String>>> solutionResult = mapper.readValue(result, new TypeReference<>() {});

        Map<String, Map<String, Set<Multimap<String, String>>>> solutionRelations = mapper
                .readValue(relation, new TypeReference<>() {});

        Multimap<String, String> problems = mapper.readValue(problem, new TypeReference<>() {});

        storeInSolutionResult(solutionResult);
        storeInSolutionRelations(solutionRelations);
        storeInProblem(problems);
    }
}
