package com.interpreter.relational.operation;

import com.interpreter.relational.service.RowMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.interpreter.relational.util.BasicUtilityMethods.*;

public class Join {
    public static Set<RowMap> join(Pair<String, Set<RowMap>> relation1,
                                   Pair<String, Set<RowMap>> relation2,
                                   List<String> attributes
    ) {
        Set<RowMap> joinedRelation = new HashSet<>();
        Set<RowMap> product = CartesianProduct.product(relation1.getRight(), relation2.getRight());
        if (attributes.isEmpty()) {
            return product;
        } else {
            Map<String, Set<RowMap>> relations = Map.ofEntries(relation1, relation2);
            for (RowMap map : product) {
                int joinedAttributes = 0;
                for (String attribute : attributes) {
                    String finalAttribute = returnAttributeIfExist(relations, attribute);

                    List<String> content = map.get(finalAttribute);
                    if (content.size() % 2 == 0) {
                        List<String> part1 = content.subList(0, content.size() / 2);
                        List<String> part2 = content.subList(content.size() / 2, content.size());

                        if (new HashSet<>(part1).containsAll(part2)) {
                            joinedAttributes++;
                            map.put(finalAttribute, part1);
                        }
                    }
                }

                if (joinedAttributes == attributes.size()) {
                    joinedRelation.add(map);
                }
            }
        }
        return joinedRelation;
    }
}
