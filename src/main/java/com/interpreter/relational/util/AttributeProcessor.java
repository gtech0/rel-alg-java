package com.interpreter.relational.util;

import com.interpreter.relational.dto.AttributeDto;

import java.util.Objects;

import static com.interpreter.relational.util.BasicUtilityMethods.*;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

public class AttributeProcessor {
    public static AttributeDto extractAttribute(String relationName, String attribute) {
        String finalAttribute = attribute;
        String relation = null;
        if (attribute.contains(".") && !isCreatable(attribute) && !isQuoted(attribute)) {
            String[] relationAttribute = attribute.split("\\.");
            relation = relationAttribute[0];
            if (!attributeOfRelationExists(relationName, relationAttribute)) {
                return null;
            }
            finalAttribute = relationAttribute[1];
        }
        return new AttributeDto(relation, finalAttribute);
    }

    private static boolean attributeOfRelationExists(String relationName, String[] relationAttribute) {
        return Objects.equals(relationName, relationAttribute[0])
                || Objects.equals(relationName, "")
                && relationAttribute.length == 2;
    }
}
