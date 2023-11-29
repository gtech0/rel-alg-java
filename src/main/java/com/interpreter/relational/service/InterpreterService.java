package com.interpreter.relational.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.dto.ResponseDataDto;
import com.interpreter.relational.dto.ResultDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.operation.CartesianProduct;
import com.interpreter.relational.operation.Join;
import com.interpreter.relational.operation.Select;
import com.interpreter.relational.operation.Union;
import com.interpreter.relational.repository.SolutionRepository;
import com.interpreter.relational.repository.TestRepository;
import com.interpreter.relational.util.converter.GenericConverter;
import com.interpreter.relational.util.converter.MapToMultimapRelation;
import com.interpreter.relational.util.converter.MultimapToMapRelation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.interpreter.relational.operation.Division.division;
import static com.interpreter.relational.operation.Projection.projection;

@Service
@RequiredArgsConstructor
public class InterpreterService {
    private final TestRepository testRepository;
    private final SolutionRepository solutionRepository;
    GenericConverter<Set<Map<String, Collection<String>>>, Set<Multimap<String, String>>> mapToMultimap
            = new MapToMultimapRelation();
    GenericConverter<Set<Multimap<String, String>>, Set<Map<String, Collection<String>>>> multimapToMap
            = new MultimapToMapRelation();

    public void checkQuery(List<String> query) {
        for (int i = 0; i < query.size(); i++) {
            Integer index = null;
            for (PatternEnum currentPattern : PatternEnum.values()) {
                if (currentPattern.patternMatches(query.get(i))) {
                    index = i;
                }
            }

            if (index == null) {
                throw new BaseException("No matching in line " + (i + 1), StatusType.CE.toString());
            }
        }
    }

    public Map<String, Set<Multimap<String, String>>> inputConversion(Map<String, Set<Map<String, String>>> input) {
        Map<String, Set<Multimap<String, String>>> data = new HashMap<>();
        input.forEach(
                (k, v) -> {
                    Set<Multimap<String, String>> newSet = new HashSet<>();
                    v.forEach(
                            map -> {
                                Multimap<String, String> newMultimap = ArrayListMultimap.create();
                                map.forEach(newMultimap::put);
                                newSet.add(newMultimap);
                            }
                    );
                    data.put(k, newSet);
                }
        );
        return data;
    }

    public ResponseDataDto buildResponse(Set<Map<String, Collection<String>>> result,
                                         Map<String, Set<Map<String, Collection<String>>>> getRelationMap) {
        return ResponseDataDto.builder()
                .result(result)
                .getResults(getRelationMap)
                .build();
    }

    public ResultDto validation(List<String> query, String problemName) throws IOException {
        solutionRepository.initialize();
        Collection<String> problemCollection = solutionRepository.getProblemCollection(problemName);
        int problemNum = 0;

        for (String problem : problemCollection) {
            Map<String, Set<Multimap<String, String>>> solutionRelations = solutionRepository
                    .getSolutionRelations(problem);

            Set<Map<String, Collection<String>>> executionResult = inputProcessing(query, solutionRelations).getResult();
            Set<Multimap<String, String>> result = mapToMultimap.convert(executionResult);

            Set<Multimap<String, String>> solutionResult = solutionRepository.getSolutionResult(problem);

            ++problemNum;
            if (!Objects.equals(solutionResult, result)) {
                throw new BaseException("Test " + problemNum + " has failed", StatusType.WA.toString());
            }
        }

        return new ResultDto("OK");
    }

    public ResponseDataDto inputProcessing(List<String> query,
                                           Map<String, Set<Multimap<String, String>>> data
    ) {
        testRepository.clear();
        testRepository.storeInMap(data);
        Map<String, Set<Multimap<String, String>>> relationMap = testRepository.findAll();
        Map<String, Set<Map<String, Collection<String>>>> relationGetMap = new HashMap<>();

        if (query.isEmpty()) {
            return new ResponseDataDto(new HashSet<>(), new HashMap<>());
        }

        query.removeIf(String::isBlank);
        checkQuery(query);

        for (String queryOperation : query) {
            String[] tokenizedOperation = queryOperation.split("\\s+");

            String operationName = tokenizedOperation[0];
            int lastIndex = tokenizedOperation.length - 1;
            int lastAttribute = lastIndex - 1;
            boolean hasArrow = Objects.equals(tokenizedOperation[lastIndex - 1], "->");
            if (!hasArrow && !Objects.equals(operationName, "GET") && !Objects.equals(operationName, "ANSWER"))
                continue;

            Set<Multimap<String, String>> result = new HashSet<>();

            String firstRelationName = tokenizedOperation[1];
            Set<Multimap<String, String>> firstRelation = testRepository.getRelation(firstRelationName);

            String secondRelationName = null;
            if (tokenizedOperation.length > 3) {
                secondRelationName = tokenizedOperation[3];
            }

            switch (operationName) {
                case "UNION" -> result = Union.union(firstRelation, testRepository.getRelation(secondRelationName));
                case "DIFFERENCE" ->
                        result = Sets.difference(firstRelation, testRepository.getRelation(secondRelationName));
                case "TIMES" ->
                        result = CartesianProduct.product(firstRelation, testRepository.getRelation(secondRelationName));
                case "INTERSECT" ->
                        result = Sets.intersection(firstRelation, testRepository.getRelation(secondRelationName));
                case "PROJECT" -> result = projection(
                        new ImmutablePair<>(firstRelationName, firstRelation),
                        Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                );
                case "SELECT" -> result = Select.selection(
                        new ImmutablePair<>(firstRelationName, firstRelation),
                        Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                );
                case "DIVIDE" -> result = division(
                        new ImmutablePair<>(firstRelationName, firstRelation),
                        new ImmutablePair<>(secondRelationName, testRepository.getRelation(secondRelationName)),
                        Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                );
                case "JOIN" -> result = Join.join(
                        new ImmutablePair<>(firstRelationName, firstRelation),
                        new ImmutablePair<>(secondRelationName, testRepository.getRelation(secondRelationName)),
                        Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                );
                case "GET" -> relationGetMap.put(firstRelationName, multimapToMap.convert(firstRelation));
                case "ANSWER" -> relationMap.put("", firstRelation);
                default -> throw new BaseException("Unexpected error", StatusType.RT.toString());
            }

            if (!Objects.equals(operationName, "GET") && !Objects.equals(operationName, "ANSWER")) {
                relationMap.put(tokenizedOperation[lastIndex], result);
            }
        }

        Set<Multimap<String, String>> output = testRepository.getResult();
        return buildResponse(multimapToMap.convert(output), relationGetMap);
    }
}
