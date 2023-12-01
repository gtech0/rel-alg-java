package com.interpreter.relational.util;

import com.google.common.collect.Multimap;
import com.interpreter.relational.dto.AttributeDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import lombok.experimental.UtilityClass;

import java.util.List;

import static com.interpreter.relational.util.AttributeProcessor.extractAttribute;

@UtilityClass
public class UtilityMethods {
    public boolean isQuoted(String value) {
        return value.startsWith("\"")
                && value.endsWith("\"")
                && value.chars().filter(c -> c == '\"').count() == 2;
    }

    public String returnAttributeIfExist(Multimap<String, String> multimap,
                                         String attribute,
                                         List<String> relationNames
    ) {
        AttributeDto attributeDto = extractAttribute(relationNames, attribute);
        String finalAttribute = attributeDto.getAttribute();
        String relation = attributeDto.getRelation() != null
                ? attributeDto.getRelation() : String.valueOf(relationNames);

        if (!multimap.containsKey(finalAttribute)) {
            throw new BaseException("Attribute " + finalAttribute + " of relation "
                    + relation + " doesn't exist", StatusType.CE.toString());
        }
        return finalAttribute;
    }
}
