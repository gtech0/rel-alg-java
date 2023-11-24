package com.interpreter.relational.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.dto.ResponseDataDto;
import com.interpreter.relational.dto.ResultDto;
import com.interpreter.relational.exception.BaseException;
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
                throw new BaseException("No matching in line " + (i + 1));
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
                return new ResultDto("Test " + problemNum + " has failed");
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
        Map<String, Set<Map<String, Collection<String>>>> getRelationMap = new HashMap<>();

        if (query.isEmpty()) {
            return new ResponseDataDto(new HashSet<>(), new HashMap<>());
        }

        query.removeIf(String::isBlank);
        checkQuery(query);

        String lastCommand = null;
        for (int strIndex = 0; strIndex < query.size(); strIndex++) {
            String queryIterator = query.get(strIndex);
            String[] tokenizedOperation = queryIterator.split("\\s+");

            int lastIndex = tokenizedOperation.length - 1;

            int lastAttribute;
            if (strIndex != query.size() - 1)
                lastAttribute = lastIndex - 1;
            else
                lastAttribute = lastIndex + 1;

            Set<Multimap<String, String>> result = new HashSet<>();
            switch (tokenizedOperation[0]) {
                case "UNION" -> result = Union.union(
                        testRepository.getRelation(tokenizedOperation[1]),
                        testRepository.getRelation(tokenizedOperation[3])
                );
                case "DIFFERENCE" -> result = Sets.difference(
                        testRepository.getRelation(tokenizedOperation[1]),
                        testRepository.getRelation(tokenizedOperation[3])
                );
                case "TIMES" -> result = CartesianProduct.product(
                        testRepository.getRelation(tokenizedOperation[1]),
                        testRepository.getRelation(tokenizedOperation[3])
                );
                case "INTERSECT" -> result = Sets.intersection(
                        testRepository.getRelation(tokenizedOperation[1]),
                        testRepository.getRelation(tokenizedOperation[3])
                );
                case "PROJECT" -> {
                    String relationName = tokenizedOperation[1];

                    result = projection(
                            new ImmutablePair<>(relationName, testRepository.getRelation(relationName)),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                    );
                }
                case "SELECT" -> {
                    String relationName = tokenizedOperation[1];

                    result = Select.selection(
                            new ImmutablePair<>(relationName, testRepository.getRelation(relationName)),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                    );
                }
                case "DIVIDE" -> {
                    String relName1 = tokenizedOperation[1];
                    String relName2 = tokenizedOperation[3];

                    result = division(
                            new ImmutablePair<>(relName1, testRepository.getRelation(relName1)),
                            new ImmutablePair<>(relName2, testRepository.getRelation(relName2)),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                    );
                }
                case "JOIN" -> {
                    String relName1 = tokenizedOperation[1];
                    String relName2 = tokenizedOperation[3];

                    result = Join.join(
                            new ImmutablePair<>(relName1, testRepository.getRelation(relName1)),
                            new ImmutablePair<>(relName2, testRepository.getRelation(relName2)),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                    );
                }
                case "GET" -> getRelationMap.put(
                        tokenizedOperation[1],
                        multimapToMap.convert(testRepository.getRelation(tokenizedOperation[1]))
                );
                default -> throw new BaseException("Unexpected error");
            }

            lastCommand = tokenizedOperation[0];
            if (!Objects.equals(tokenizedOperation[0], "GET")) {
                if (strIndex == query.size() - 1) {
                    return buildResponse(multimapToMap.convert(result), getRelationMap);
                }

                if (Objects.equals(tokenizedOperation[lastIndex - 1], "->")) {
                    relationMap.put(tokenizedOperation[lastIndex], result);
                } else {
                    relationMap.put("", result);
                }
            }
        }

        Set<Multimap<String, String>> output = testRepository.getResult();
        if (Objects.equals(lastCommand, "GET")) {
            return buildResponse(multimapToMap.convert(output), getRelationMap);
        }

        return new ResponseDataDto(new HashSet<>(), new HashMap<>());
    }
}
