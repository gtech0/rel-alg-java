package com.interpreter.relational.operation;

import com.interpreter.relational.dto.AttributeDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.service.RowMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.interpreter.relational.util.AttributeProcessor.extractAttribute;

public class Projection {
    public static Set<RowMap> projection(Pair<String, Set<RowMap>> relation,
                                         List<String> attributes) {
        return relation.getRight()
                .stream()
                .map(map -> {
                    RowMap newMap = new RowMap();
                    attributes.forEach(
                            attribute -> {
                                AttributeDto attributeDto = extractAttribute(relation.getLeft(), attribute);

                                if (attributeDto != null) {
                                    String finalAttribute = attributeDto.getAttribute();

                                    if (map.containsKey(finalAttribute)) {
                                        map.get(finalAttribute).forEach(
                                                value -> newMap.put(finalAttribute, List.of(value))
                                        );
                                    } else {
                                        throw new BaseException("Attribute "
                                                + finalAttribute + " of relation "
                                                + relation.getLeft() + " doesn't exist",
                                                StatusType.CE.toString());
                                    }
                                }
                            }
                    );
                    return newMap;
                })
                .collect(Collectors.toSet());
    }
}
