package com.interpreter.relational.operation;

import com.google.common.collect.Sets;
import com.interpreter.relational.service.RowMap;
import com.interpreter.relational.util.BasicUtilityMethods;

import java.util.Set;

public class Union {
    public static Set<RowMap> union(Set<RowMap> relation1, Set<RowMap> relation2, int operationNumber) {
        BasicUtilityMethods.checkArity(relation1, relation2, operationNumber);
        return Sets.union(relation1, relation2);
    }
}
