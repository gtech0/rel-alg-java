package com.interpreter.relational.operation;

import com.interpreter.relational.dto.AttributeDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.service.RowMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
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
                    for (String attribute : attributes) {
                        AttributeDto attributeDto = extractAttribute(relation.getLeft(), attribute);

                        if (attributeDto == null) {
                            continue;
                        }

                        String finalAttribute = attributeDto.getAttribute();
                        if (!map.containsKey(finalAttribute)) {
                            throw new BaseException("Attribute "
                                    + finalAttribute + " of relation "
                                    + relation.getLeft() + " doesn't exist",
                                    StatusType.CE.toString());
                        }

                        List<String> list = new ArrayList<>(newMap.get(finalAttribute));
                        list.addAll(map.get(finalAttribute));
                        newMap.put(finalAttribute, list);
                    }
                    return newMap;
                })
                .collect(Collectors.toSet());
    }
}
