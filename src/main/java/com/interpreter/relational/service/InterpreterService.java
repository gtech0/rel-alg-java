package com.interpreter.relational.service;

import com.interpreter.relational.dto.ResponseDataDto;
import com.interpreter.relational.dto.ResultDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.repository.SolutionRepository;
import com.interpreter.relational.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.interpreter.relational.operation.CartesianProduct.product;
import static com.interpreter.relational.operation.Difference.difference;
import static com.interpreter.relational.operation.Division.division;
import static com.interpreter.relational.operation.Intersection.intersection;
import static com.interpreter.relational.operation.Join.join;
import static com.interpreter.relational.operation.Projection.projection;
import static com.interpreter.relational.operation.Select.selection;
import static com.interpreter.relational.operation.Union.union;

@Service
@RequiredArgsConstructor
public class InterpreterService {
    private final TestRepository testRepository;
    private final SolutionRepository solutionRepository;

    private void checkQuery(List<String> query) {
        for (int queryIndex = 0; queryIndex < query.size(); queryIndex++) {
            Integer index = null;
            for (PatternEnum currentPattern : PatternEnum.values()) {
                if (currentPattern.patternMatches(query.get(queryIndex))) {
                    index = queryIndex;
                }
            }

            if (index == null) {
                throw new BaseException("No matching in line " + (queryIndex + 1), StatusType.CE.toString());
            }
        }
    }

    private ResponseDataDto buildResponse(Set<RowMap> result,
                                          Map<String, Set<RowMap>> getRelationMap) {
        return ResponseDataDto.builder()
                .result(result)
                .getResults(getRelationMap)
                .build();
    }

    public ResultDto validation(List<String> query, String problemName) throws IOException {
        solutionRepository.initialize();
        List<String> problemCollection = solutionRepository.getProblemCollection(problemName);

        int problemNum = 0;
        for (String problem : problemCollection) {
            var solutionRelations = solutionRepository.getSolutionRelations(problem);
            Set<RowMap> solutionResult = solutionRepository.getSolutionResult(problem);

            Set<RowMap> result = inputProcessing(query, solutionRelations).getResult();

            ++problemNum;
            if (!Objects.equals(solutionResult, result)) {
                throw new BaseException("Test " + problemNum + " has failed", StatusType.WA.toString());
            }
        }

        return new ResultDto("OK");
    }

    public ResponseDataDto inputProcessing(List<String> query,
                                           Map<String, Set<RowMap>> data
    ) {
        testRepository.clear();
        testRepository.storeInMap(data);
        Map<String, Set<RowMap>> relationMap = testRepository.findAll();
        Map<String, Set<RowMap>> relationGetMap = new HashMap<>();

        if (query.isEmpty()) {
            return buildResponse(new HashSet<>(), new HashMap<>());
        }

        query.removeIf(String::isBlank);
        checkQuery(query);

        for (int index = 0; index < query.size(); index++) {
            String[] tokenizedOperation = query.get(index).split("\\s+");

            String operationName = tokenizedOperation[0];
            int operationNumber = index + 1;
            int lastIndex = tokenizedOperation.length - 1;
            int lastAttribute = lastIndex - 1;
            boolean hasArrow = Objects.equals(tokenizedOperation[lastIndex - 1], "->");
            Set<RowMap> result = new HashSet<>();

            Set<String> tokensWithOneRelation = Set.of("PROJECT", "SELECT", "GET", "ANSWER");

            String firstRelationName = tokenizedOperation[1];
            Set<RowMap> firstRelation = testRepository.getRelation(firstRelationName);

            String secondRelationName = null;
            Set<RowMap> secondRelation = null;
            if (tokenizedOperation.length >= 4) {
                secondRelationName = tokenizedOperation[3];
                if (!tokensWithOneRelation.contains(operationName)) {
                    secondRelation = testRepository.getRelation(secondRelationName);
                }
            }

            switch (operationName) {
                case "UNION" -> result = union(firstRelation, secondRelation, operationNumber);
                case "DIFFERENCE" -> result = difference(firstRelation, secondRelation, operationNumber);
                case "TIMES" -> result = product(firstRelation, secondRelation);
                case "INTERSECT" -> result = intersection(firstRelation, secondRelation, operationNumber);
                case "PROJECT" -> result = projection(
                        new ImmutablePair<>(firstRelationName, firstRelation),
                        Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                );
                case "SELECT" -> result = selection(
                        new ImmutablePair<>(firstRelationName, firstRelation),
                        Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                );
                case "DIVIDE" -> result = division(
                        new ImmutablePair<>(firstRelationName, firstRelation),
                        new ImmutablePair<>(secondRelationName, secondRelation),
                        Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList(),
                        operationNumber
                );
                case "JOIN" -> result = join(
                        new ImmutablePair<>(firstRelationName, firstRelation),
                        new ImmutablePair<>(secondRelationName, secondRelation),
                        Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                );
                case "GET" -> relationGetMap.put(firstRelationName, firstRelation);
                case "ANSWER" -> relationMap.put("", firstRelation);
                default -> throw new BaseException("Unexpected error", StatusType.RT.toString());
            }

            if (!Objects.equals(operationName, "GET") && !Objects.equals(operationName, "ANSWER") || hasArrow) {
                relationMap.put(tokenizedOperation[lastIndex], result);
            }
        }

        Set<RowMap> output = testRepository.getResult();
        return buildResponse(output, relationGetMap);
    }
}
