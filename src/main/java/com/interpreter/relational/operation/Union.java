package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;

import java.util.Set;

public class Union {
    public static Set<Multimap<String, String>> union(Set<Multimap<String, String>> relation1,
                                                      Set<Multimap<String, String>> relation2) {
        for (Multimap<String, String> map1 : relation1) {
            for (Multimap<String, String> map2 : relation2) {
                boolean setsEqual = Sets.symmetricDifference(map1.keySet(), map2.keySet()).isEmpty();
                if (!setsEqual) {
                    throw new BaseException("Incorrect arity", StatusType.CE.toString());
                }
            }
        }
        return Sets.union(relation1, relation2);
    }
}
