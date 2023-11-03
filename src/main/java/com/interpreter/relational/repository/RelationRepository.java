package com.interpreter.relational.repository;

import com.interpreter.relational.entity.Message;
import com.interpreter.relational.entity.User;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.interpreter.relational.util.ObjectToMultimap.convertUsingReflection;

@Repository
public class RelationRepository {

    public Map<String, Set<Multimap<String, String>>> newMap() throws IllegalAccessException {
        Map<String, Set<Multimap<String, String>>> relationMap = new HashMap<>();

        Multimap<String, String> userTuple1 = convertUsingReflection(new User(
                "john",
                "123123",
                "group1"
        ));

        Multimap<String, String> userTuple2 = convertUsingReflection(new User(
                "alex",
                "133123",
                "group2"
        ));

        Multimap<String, String> userTuple3 = convertUsingReflection(new User(
                "andrew",
                "134123",
                "group1"
        ));

        Multimap<String, String> userTuple4 = convertUsingReflection(new User(
                "jim",
                "135121",
                "group1"
        ));

        Multimap<String, String> messageTuple1 = convertUsingReflection(new Message(
                "john",
                "qwerty"
        ));

        Multimap<String, String> messageTuple2 = convertUsingReflection(new Message(
                "alex",
                "uiop"
        ));

        Set<Multimap<String, String>> relation1 = ImmutableSet.of(userTuple1, userTuple2);
        Set<Multimap<String, String>> relation2 = ImmutableSet.of(userTuple1, userTuple3, userTuple4);
        Set<Multimap<String, String>> relation3 = ImmutableSet.of(messageTuple1, messageTuple2);

        relationMap.put("R1", relation1);
        relationMap.put("R2", relation2);
        relationMap.put("R3", relation3);

        return relationMap;
    }
}
