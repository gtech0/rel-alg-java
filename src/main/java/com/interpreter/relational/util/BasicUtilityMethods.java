package com.interpreter.relational.util;

import com.google.common.collect.Sets;
import com.interpreter.relational.dto.AttributeDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.service.RowMap;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.interpreter.relational.util.AttributeProcessor.extractAttribute;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

@UtilityClass
public class BasicUtilityMethods {
    public boolean isQuoted(String value) {
        return value.startsWith("\"")
                && value.endsWith("\"")
                && value.chars().filter(c -> c == '\"').count() == 2;
    }

    public boolean isADate(String value) {
        if (value == null) {
            return false;
        }
        try {
            LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public String returnAttributeIfExist(Map<String, Set<RowMap>> relations, String attribute) {
        String finalAttribute = null;
        for (Map.Entry<String, Set<RowMap>> relation : relations.entrySet()) {
            for (RowMap map : relation.getValue()) {
                AttributeDto attributeDto = extractAttribute(relation.getKey(), attribute);
                if (attributeDto != null) {
                    finalAttribute = attributeDto.getAttribute();

                    if (map.containsKey(finalAttribute)) {
                        return finalAttribute;
                    }
                }
            }
        }

        throw new BaseException("Attribute " + finalAttribute
                + " of relations " + relations.keySet()
                + " doesn't exist", StatusType.CE.toString());
    }

    public void checkArity(Set<RowMap> relation1, Set<RowMap> relation2, int operationNumber) {
        for (RowMap map1 : relation1) {
            for (RowMap map2 : relation2) {
                boolean setsEqual = Sets.symmetricDifference(map1.keySet(), map2.keySet()).isEmpty();
                if (!setsEqual) {
                    throw new BaseException("Incorrect arity in the "
                            + operationNumber + " line: names don't match",
                            StatusType.CE.toString());
                }

                checkDomainEquality(operationNumber, map1, map2);
            }
        }
    }

    private void checkDomainEquality(int operationNumber, RowMap map1, RowMap map2) {
        Set<String> keySet = map1.keySet();
        for (String key : keySet) {
            List<String> values1 = map1.get(key);
            List<String> values2 = map2.get(key);

            for (String value1 : values1) {
                for (String value2 : values2) {
                    if (isCreatable(value1) && !isCreatable(value2)
                            || isADate(value1) && !isADate(value2)
                    ) {
                        throw new BaseException("Incorrect arity in the "
                                + operationNumber + " line: types don't match",
                                StatusType.CE.toString());
                    }
                }
            }
        }
    }
}
