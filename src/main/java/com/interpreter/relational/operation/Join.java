package com.interpreter.relational.operation;

import com.interpreter.relational.service.RowMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.interpreter.relational.util.BasicUtilityMethods.returnAttributeIfExist;

public class Join {
    public static Set<RowMap> join(Pair<String, Set<RowMap>> relation1,
                                   Pair<String, Set<RowMap>> relation2,
                                   List<String> uniqueAttributes
    ) {
        Set<RowMap> joinedRelation = new HashSet<>();
        Set<RowMap> product = CartesianProduct.product(relation1.getRight(), relation2.getRight());
        if (uniqueAttributes.isEmpty()) {
            return product;
        } else {
            Map<String, Set<RowMap>> relations = Map.ofEntries(relation1, relation2);
            for (RowMap map : product) {
                int joinedAttributes = 0;
                for (String attribute : uniqueAttributes) {
                    String finalAttribute = returnAttributeIfExist(relations, attribute);

                    List<String> content = map.get(finalAttribute);
                    if (content.stream().distinct().count() == 1 && content.size() > 1) {
                        joinedAttributes++;
                        for (String first : new HashSet<>(content)) {
                            map.remove(finalAttribute, first);
                        }
                    }
                }

                if (joinedAttributes == uniqueAttributes.size()) {
                    joinedRelation.add(map);
                }
            }
        }
        return joinedRelation;
    }
}
