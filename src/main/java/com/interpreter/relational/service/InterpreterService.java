package com.interpreter.relational.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.dto.ResponseDataDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.operation.CartesianProduct;
import com.interpreter.relational.operation.Join;
import com.interpreter.relational.operation.Select;
import com.interpreter.relational.repository.SolutionRelationRepository;
import com.interpreter.relational.repository.TestRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.interpreter.relational.operation.Division.division;
import static com.interpreter.relational.operation.Projection.projection;

@Service
@RequiredArgsConstructor
public class InterpreterService {
    private final TestRelationRepository testRelationRepository;
    private final SolutionRelationRepository solutionRelationRepository;

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

    public Set<Map<String, Collection<String>>> resultConversion(Set<Multimap<String, String>> resultSet) {
        Set<Map<String, Collection<String>>> mappedSet = new HashSet<>();
        for (Multimap<String, String> resultMap : resultSet) {
            Map<String, Collection<String>> map = resultMap.asMap().entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            (entry) -> ImmutableList.copyOf(entry.getValue())
                    ));
            mappedSet.add(map);
        }
        return mappedSet;
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

    public ResponseDataDto inputProcessing(List<String> query,
                                           Map<String, Set<Map<String, String>>> data
    ) throws IOException {
        solutionRelationRepository.fillSolution();
        testRelationRepository.storeInMap(inputConversion(data));
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

            ResponseDataDto response;
            Set<Multimap<String, String>> result;
            //try {
            switch (tokenizedOperation[0]) {
                case "UNION" -> {
                    result = Sets.union(
                            testRelationRepository.getRelation(tokenizedOperation[1]),
                            testRelationRepository.getRelation(tokenizedOperation[3])
                    );

                    response = buildResponse(resultConversion(result), getRelationMap);

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return response;
                }
                case "DIFFERENCE" -> {
                    result = Sets.difference(
                            testRelationRepository.getRelation(tokenizedOperation[1]),
                            testRelationRepository.getRelation(tokenizedOperation[3])
                    );

                    response = buildResponse(resultConversion(result), getRelationMap);

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return response;
                }
                case "TIMES" -> {
                    result = CartesianProduct.product(
                            testRelationRepository.getRelation(tokenizedOperation[1]),
                            testRelationRepository.getRelation(tokenizedOperation[3])
                    );

                    response = buildResponse(resultConversion(result), getRelationMap);

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return response;
                }
                case "INTERSECT" -> {
                    result = Sets.intersection(
                            testRelationRepository.getRelation(tokenizedOperation[1]),
                            testRelationRepository.getRelation(tokenizedOperation[3])
                    );

                    response = buildResponse(resultConversion(result), getRelationMap);

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return response;
                }
                case "PROJECT" -> {
                    result = projection(
                            testRelationRepository.getRelation(tokenizedOperation[1]),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                    );

                    response = buildResponse(resultConversion(result), getRelationMap);

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return response;
                }
                case "SELECT" -> {
                    result = Select.selection(
                            testRelationRepository.getRelation(tokenizedOperation[1]),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                    );

                    response = buildResponse(resultConversion(result), getRelationMap);

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return response;
                }
                case "DIVIDE" -> {
                    result = division(
                            testRelationRepository.getRelation(tokenizedOperation[1]),
                            testRelationRepository.getRelation(tokenizedOperation[3]),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                    );

                    response = buildResponse(resultConversion(result), getRelationMap);

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return response;
                }
                case "JOIN" -> {
                    result = Join.join(
                            testRelationRepository.getRelation(tokenizedOperation[1]),
                            testRelationRepository.getRelation(tokenizedOperation[3]),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                    );

                    response = buildResponse(resultConversion(result), getRelationMap);

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return response;
                }
                case "GET" -> getRelationMap.put(
                        tokenizedOperation[1],
                        resultConversion(testRelationRepository.getRelation(tokenizedOperation[1]))
                );
                default -> throw new BaseException("Unexpected error");
            }
//            } catch (Exception e) {
//                throw new BaseException("Exception occurred while executing " + tokenizedOperation[0] + " operation");
//            }
        }
        return new ResponseDataDto(new HashSet<>(), new HashMap<>());
    }
}
