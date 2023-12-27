package com.interpreter.relational.service;

import com.interpreter.relational.dto.ResponseDataDto;
import com.interpreter.relational.dto.ResultDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.repository.SolutionRepository;
import com.interpreter.relational.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

    private List<String> getAttributesFromQuery(String[] tokenizedOperation, int start, int end) {
        return Arrays
                .stream(Arrays.copyOfRange(tokenizedOperation, start, end))
                .toList();
    }

    public ResultDto validation(List<String> query, String problemName) throws IOException {
        solutionRepository.initialize();
        List<String> problems = solutionRepository.getProblemCollection(problemName);

        for (int problemIndex = 0; problemIndex < problems.size(); problemIndex++) {
            var solutionRelations = solutionRepository.getSolutionRelations(problems.get(problemIndex));
            Set<RowMap> solutionResult = solutionRepository.getSolutionResult(problems.get(problemIndex));

            Set<RowMap> result = inputProcessing(query, solutionRelations).getResult();

            if (!Objects.equals(solutionResult, result)) {
                throw new BaseException("Test " + (problemIndex + 1) + " has failed", StatusType.WA.toString());
            }
        }

        return new ResultDto("OK");
    }

    public ResponseDataDto inputProcessing(List<String> query, Map<String, Set<RowMap>> data) {
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
            if (!hasArrow && !Objects.equals(operationName, "ANSWER") && !Objects.equals(operationName, "GET")) {
                continue;
            }

            Set<RowMap> result = new HashSet<>();

            Set<String> tokensWithOneRelation = Set.of("PROJECT", "SELECT", "GET", "ANSWER");

            String firstRelationName = tokenizedOperation[1];
            Set<RowMap> firstRelation = testRepository.getRelation(firstRelationName);
            Pair<String, Set<RowMap>> firstRelationPair = new ImmutablePair<>(firstRelationName, firstRelation);

            String secondRelationName;
            Set<RowMap> secondRelation = null;
            Pair<String, Set<RowMap>> secondRelationPair = null;
            if (tokenizedOperation.length >= 4) {
                secondRelationName = tokenizedOperation[3];
                if (!tokensWithOneRelation.contains(operationName)) {
                    secondRelation = testRepository.getRelation(secondRelationName);
                    secondRelationPair = new ImmutablePair<>(secondRelationName, secondRelation);
                }
            }

            switch (operationName) {
                case "UNION" -> result = union(firstRelation, secondRelation, operationNumber);
                case "DIFFERENCE" -> result = difference(firstRelation, secondRelation, operationNumber);
                case "TIMES" -> result = product(firstRelation, secondRelation);
                case "INTERSECT" -> result = intersection(firstRelation, secondRelation, operationNumber);
                case "PROJECT" -> result = projection(
                        firstRelationPair,
                        getAttributesFromQuery(tokenizedOperation, 3, lastAttribute)
                );
                case "SELECT" -> result = selection(
                        firstRelationPair,
                        getAttributesFromQuery(tokenizedOperation, 3, lastAttribute)
                );
                case "DIVIDE" -> result = division(
                        firstRelationPair,
                        secondRelationPair,
                        getAttributesFromQuery(tokenizedOperation, 5, lastAttribute),
                        operationNumber
                );
                case "JOIN" -> result = join(
                        firstRelationPair,
                        secondRelationPair,
                        getAttributesFromQuery(tokenizedOperation, 5, lastAttribute)
                );
                case "GET" -> relationGetMap.put(firstRelationName, firstRelation);
                case "ANSWER" -> relationMap.put("", firstRelation);
                default -> throw new BaseException("Unexpected error", StatusType.RT.toString());
            }

            if (!Objects.equals(operationName, "GET") && !Objects.equals(operationName, "ANSWER") && hasArrow) {
                relationMap.put(tokenizedOperation[lastIndex], result);
            }
        }

        Set<RowMap> output = testRepository.getResult();
        return buildResponse(output, relationGetMap);
    }
}
