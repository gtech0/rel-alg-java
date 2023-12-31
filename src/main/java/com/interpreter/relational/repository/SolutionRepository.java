package com.interpreter.relational.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.service.RowMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class SolutionRepository {

    private final ObjectMapper mapper;
    RowMap problem = new RowMap();
    Map<String, Map<String, Set<RowMap>>> solutionRelations = new HashMap<>();
    Map<String, Set<RowMap>> solutionResult = new HashMap<>();

    public Set<RowMap> getSolutionResult(String key) {
        Set<RowMap> relation = solutionResult.get(key);
        if (relation == null)
            throw new BaseException("Relation " + key + " is null", StatusType.CE.toString());
        return relation;
    }

    public Map<String, Set<RowMap>> getSolutionRelations(String key) {
        Map<String, Set<RowMap>> relation = solutionRelations.get(key);
        if (relation == null)
            throw new BaseException("Relation " + key + " is null", StatusType.CE.toString());
        return relation;
    }

    public List<String> getProblemCollection(String key) {
        List<String> problemCollection = problem.get(key);
        if (problemCollection.isEmpty())
            throw new BaseException("No " + key + " problem exist", StatusType.CE.toString());
        return problem.get(key);
    }

    public void initialize() throws IOException {
        this.solutionResult.clear();
        this.solutionRelations.clear();
        this.problem.clear();

        File result = ResourceUtils.getFile("classpath:solutionResult.json");
        File relation = ResourceUtils.getFile("classpath:solutionRelations.json");
        File problem = ResourceUtils.getFile("classpath:problem.json");

        Map<String, Set<RowMap>> solutionResult = mapper.readValue(result, new TypeReference<>() {});
        Map<String, Map<String, Set<RowMap>>> solutionRelations = mapper.readValue(relation, new TypeReference<>() {});
        RowMap problems = mapper.readValue(problem, new TypeReference<>() {});

        this.solutionResult.putAll(solutionResult);
        this.solutionRelations.putAll(solutionRelations);
        this.problem.putAll(problems);
    }
}
