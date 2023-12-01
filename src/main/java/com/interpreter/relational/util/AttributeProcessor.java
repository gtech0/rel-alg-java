package com.interpreter.relational.util;

import com.interpreter.relational.dto.AttributeDto;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;

import java.util.List;

import static com.interpreter.relational.util.UtilityMethods.*;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

public class AttributeProcessor {
    public static AttributeDto extractAttribute(List<String> relationNames, String attribute) {
        String finalAttribute = attribute;
        String relation = null;
        if (attribute.contains(".") && !isCreatable(attribute) && !isQuoted(attribute)) {
            String[] relationAttribute = attribute.split("\\.");
            relation = relationAttribute[0];
            if (!attributeOfRelationExists(relationNames, relationAttribute)) {
                throw new BaseException("Relation " + relationAttribute[0] + " doesn't exist in this context",
                        StatusType.CE.toString());
            }
            finalAttribute = relationAttribute[1];
        }
        return new AttributeDto(relation, finalAttribute);
    }

    private static boolean attributeOfRelationExists(List<String> relationNames, String[] relationAttribute) {
        return (relationNames.contains(relationAttribute[0])
                || (relationNames.contains("") && relationNames.size() == 1))
                && relationAttribute.length == 2;
    }
}
