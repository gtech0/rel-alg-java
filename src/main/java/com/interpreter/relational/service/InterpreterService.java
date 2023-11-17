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
import com.interpreter.relational.repository.SolutionRelationRepository;
import com.interpreter.relational.repository.TestRelationRepository;
import com.interpreter.relational.util.GenericConverter;
import com.interpreter.relational.util.MapToMultimapRelation;
import com.interpreter.relational.util.MultimapToMapRelation;
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
    private final TestRelationRepository testRelationRepository;
    private final SolutionRelationRepository solutionRelationRepository;
    GenericConverter<Set<Map<String, Collection<String>>>, Set<Multimap<String, String>>> mapToMultimapConverter
            = new MapToMultimapRelation();
    GenericConverter<Set<Multimap<String, String>>, Set<Map<String, Collection<String>>>> multimapToMapConverter
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
        solutionRelationRepository.initialize();
        Collection<String> problemCollection = solutionRelationRepository.getProblemCollection(problemName);
        int problemNum = 0;

        for (String problem : problemCollection) {
            ++problemNum;
            Map<String, Set<Multimap<String, String>>> solutionRelations = solutionRelationRepository
                    .getSolutionRelations(problem);

            Set<Map<String, Collection<String>>> executionResult = inputProcessing(query, solutionRelations).getResult();
            Set<Multimap<String, String>> result = mapToMultimapConverter
                    .convert(executionResult);

            Set<Multimap<String, String>> solutionResult = solutionRelationRepository
                    .getSolutionResult(problem);

            if (!Objects.equals(solutionResult, result)) {
                return new ResultDto("Test " + problemNum + " has failed");
            }
        }

        return new ResultDto("OK");
    }

    public ResponseDataDto inputProcessing(List<String> query,
                                           Map<String, Set<Multimap<String, String>>> data
    ) {
        testRelationRepository.storeInMap(data);
        Map<String, Set<Multimap<String, String>>> relationMap = testRelationRepository.findAll();
        Map<String, Set<Map<String, Collection<String>>>> getRelationMap = new HashMap<>();

        if (query.isEmpty()) {
            return new ResponseDataDto(new HashSet<>(), new HashMap<>());
        }

        checkQuery(query);

        Iterator<String> queryIterator = query.iterator();
        while (queryIterator.hasNext()) {
            String operation = queryIterator.next();
            String[] tokenizedOperation = operation.split("\\s+");

            int lastIndex = tokenizedOperation.length - 1;

            int lastAttribute;
            if (queryIterator.hasNext())
                lastAttribute = lastIndex - 1;
            else
                lastAttribute = lastIndex + 1;

            Set<Multimap<String, String>> result = new HashSet<>();
            switch (tokenizedOperation[0]) {
                case "UNION" -> result = Sets.union(
                        testRelationRepository.getRelation(tokenizedOperation[1]),
                        testRelationRepository.getRelation(tokenizedOperation[3])
                );
                case "DIFFERENCE" -> result = Sets.difference(
                        testRelationRepository.getRelation(tokenizedOperation[1]),
                        testRelationRepository.getRelation(tokenizedOperation[3])
                );
                case "TIMES" -> result = CartesianProduct.product(
                        testRelationRepository.getRelation(tokenizedOperation[1]),
                        testRelationRepository.getRelation(tokenizedOperation[3])
                );
                case "INTERSECT" -> result = Sets.intersection(
                        testRelationRepository.getRelation(tokenizedOperation[1]),
                        testRelationRepository.getRelation(tokenizedOperation[3])
                );
                case "PROJECT" -> {
                    String relationName = tokenizedOperation[1];

                    result = projection(
                            new ImmutablePair<>(relationName, testRelationRepository.getRelation(relationName)),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                    );
                }
                case "SELECT" -> {
                    String relationName = tokenizedOperation[1];

                    result = Select.selection(
                            new ImmutablePair<>(relationName, testRelationRepository.getRelation(relationName)),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                    );
                }
                case "DIVIDE" -> {
                    String relName1 = tokenizedOperation[1];
                    String relName2 = tokenizedOperation[3];

                    result = division(
                            new ImmutablePair<>(relName1, testRelationRepository.getRelation(relName1)),
                            new ImmutablePair<>(relName2, testRelationRepository.getRelation(relName2)),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                    );
                }
                case "JOIN" -> {
                    String relName1 = tokenizedOperation[1];
                    String relName2 = tokenizedOperation[3];

                    result = Join.join(
                            new ImmutablePair<>(relName1, testRelationRepository.getRelation(relName1)),
                            new ImmutablePair<>(relName2, testRelationRepository.getRelation(relName2)),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                    );
                }
                case "GET" -> getRelationMap.put(
                        tokenizedOperation[1],
                        multimapToMapConverter.convert(testRelationRepository.getRelation(tokenizedOperation[1]))
                );
                default -> throw new BaseException("Unexpected error");
            }
            if (queryIterator.hasNext()) {
                if (!Objects.equals(tokenizedOperation[0], "GET"))
                    relationMap.put(tokenizedOperation[lastIndex], result);
            } else {
                testRelationRepository.clear();
                return buildResponse(multimapToMapConverter.convert(result), getRelationMap);
            }
        }
        return new ResponseDataDto(new HashSet<>(), new HashMap<>());
    }
}
