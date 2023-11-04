package com.interpreter.relational.service;

import com.google.common.collect.ImmutableList;
import com.interpreter.relational.repository.RelationRepository;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.operation.CartesianProduct;
import com.interpreter.relational.operation.Join;
import com.interpreter.relational.operation.Select;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.interpreter.relational.operation.Division.division;
import static com.interpreter.relational.operation.Projection.projection;

@Service
@RequiredArgsConstructor
public class InterpreterService {
    private final RelationRepository relationRepository;

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

    public Set<Multimap<String, String>> inputProcessing(List<String> query) throws IllegalAccessException {
        Map<String, Set<Multimap<String, String>>> relationMap = relationRepository.newMap();

        if (query.isEmpty()) {
            return new HashSet<>();
        }

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

            Set<Multimap<String, String>> result;
            //try {
            switch (tokenizedOperation[0]) {
                case "UNION":
                    result = Sets.union(
                            relationMap.get(tokenizedOperation[1]),
                            relationMap.get(tokenizedOperation[3])
                    );

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return result;
                    break;
                case "DIFFERENCE":
                    result = Sets.difference(
                            relationMap.get(tokenizedOperation[1]),
                            relationMap.get(tokenizedOperation[3])
                    );

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return result;
                    break;
                case "TIMES":
                    result = CartesianProduct.product(
                            relationMap.get(tokenizedOperation[1]),
                            relationMap.get(tokenizedOperation[3])
                    );

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return result;
                    break;
                case "INTERSECT":
                    result = Sets.intersection(
                            relationMap.get(tokenizedOperation[1]),
                            relationMap.get(tokenizedOperation[3])
                    );

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return result;
                    break;
                case "PROJECT":
                    result = projection(
                            relationMap.get(tokenizedOperation[1]),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                    );

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return result;
                    break;
                case "SELECT":
                    result = Select.selection(
                            relationMap.get(tokenizedOperation[1]),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 3, lastAttribute)).toList()
                    );

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return result;
                    break;
                case "DIVIDE":
                    result = division(
                            relationMap.get(tokenizedOperation[1]),
                            relationMap.get(tokenizedOperation[3]),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                    );

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return result;
                    break;
                case "JOIN":
                    result = Join.join(
                            relationMap.get(tokenizedOperation[1]),
                            relationMap.get(tokenizedOperation[3]),
                            Arrays.stream(Arrays.copyOfRange(tokenizedOperation, 5, lastAttribute)).toList()
                    );

                    if (queryIterator.hasNext())
                        relationMap.put(tokenizedOperation[lastIndex], result);
                    else
                        return result;
                    break;
                default:
                    break;
            }
//            } catch (Exception e) {
//                throw new BaseException("Exception occurred while executing " + tokenizedOperation[0] + " operation");
//            }
        }
        return new HashSet<>();
    }
}
