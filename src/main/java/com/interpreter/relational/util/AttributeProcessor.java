package com.interpreter.relational.util;

import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;

import java.util.List;

import static com.interpreter.relational.util.UtilityMethods.*;
import static com.interpreter.relational.util.comparator.ComparatorMethods.isANumber;

public class AttributeProcessor {
    public static String extractAttribute(List<String> relationNames, String attribute) {
        String finalAttribute = attribute;
        if (attribute.contains(".") && !isANumber(attribute) && !isQuoted(attribute)) {
            String[] relationAttribute = attribute.split("\\.");
            if (!attributeOfRelationExists(relationNames, relationAttribute)) {
                throw new BaseException("Relation " + relationAttribute[0] + " doesn't exist in this context",
                        StatusType.CE.toString());
            }
            finalAttribute = relationAttribute[1];
        }
        return finalAttribute;
    }

    private static boolean attributeOfRelationExists(List<String> relationNames, String[] relationAttribute) {
        return (relationNames.contains(relationAttribute[0])
                || (relationNames.contains("") && relationNames.size() == 1))
                && relationAttribute.length == 2;
    }
}
