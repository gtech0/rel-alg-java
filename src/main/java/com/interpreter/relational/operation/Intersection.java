package com.interpreter.relational.operation;

import com.google.common.collect.Sets;
import com.interpreter.relational.service.RowMap;
import com.interpreter.relational.util.UtilityMethods;

import java.util.Set;

public class Intersection {
    public static Set<RowMap> intersection(Set<RowMap> relation1, Set<RowMap> relation2, String operationName) {
        UtilityMethods.checkArity(relation1, relation2, operationName);
        return Sets.intersection(relation1, relation2);
    }
}
