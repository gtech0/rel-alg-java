package com.interpreter.relational.util;

import com.interpreter.relational.exception.BaseException;

import java.util.List;

public class AttributeClass {
    public static String extractAttribute(List<String> relationNames, String attribute) {
        String finalAttribute = attribute;
        if (attribute.contains(".")) {
            String[] relationAttribute = attribute.split("\\.");
            if ((relationNames.contains(relationAttribute[0])
                    || (relationNames.contains("") && relationNames.size() == 1))
                    && relationAttribute.length == 2
            ) {
                finalAttribute = relationAttribute[1];
            } else {
                throw new BaseException("Relation " + relationAttribute[0] + " doesn't exist");
            }
        }
        return finalAttribute;
    }
}