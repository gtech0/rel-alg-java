package com.interpreter.relational.util;

import com.google.common.collect.Sets;
import com.interpreter.relational.dto.AttributeDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.service.RowMap;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;

import static com.interpreter.relational.util.AttributeProcessor.extractAttribute;

@UtilityClass
public class UtilityMethods {
    public boolean isQuoted(String value) {
        return value.startsWith("\"")
                && value.endsWith("\"")
                && value.chars().filter(c -> c == '\"').count() == 2;
    }

    public String returnAttributeIfExist(RowMap multimap,
                                         String attribute,
                                         List<String> relationNames
    ) {
        AttributeDto attributeDto = extractAttribute(relationNames, attribute);
        String finalAttribute = attributeDto.getAttribute();
        String relation = attributeDto.getRelation() != null ? attributeDto.getRelation() : String.valueOf(relationNames);

        if (!multimap.containsKey(finalAttribute)) {
            throw new BaseException("Attribute " + finalAttribute + " of relation "
                    + relation + " doesn't exist", StatusType.CE.toString());
        }
        return finalAttribute;
    }

    public void checkArity(Set<RowMap> relation1, Set<RowMap> relation2, String operationName) {
        for (RowMap map1 : relation1) {
            for (RowMap map2 : relation2) {
                boolean setsEqual = Sets.symmetricDifference(map1.keySet(), map2.keySet()).isEmpty();
                if (!setsEqual) {
                    throw new BaseException("Incorrect arity in " + operationName + " operation",
                            StatusType.CE.toString());
                }
            }
        }
    }
}
