package com.interpreter.relational.operation;

import com.google.common.collect.Sets;
import com.interpreter.relational.service.RowMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.interpreter.relational.util.UtilityMethods.*;

public class Division {
    public static Set<RowMap> division(Pair<String, Set<RowMap>> relation1,
                                       Pair<String, Set<RowMap>> relation2,
                                       List<String> commonAttributes
    ) {
        String relName1 = relation1.getLeft();
        String relName2 = relation2.getLeft();

        List<String> relationNames = List.of(relName1, relName2);
        Set<RowMap> commonRelation = Stream
                .concat(relation1.getRight().stream(), relation2.getRight().stream())
                .collect(Collectors.toSet());

        for (RowMap multimap : commonRelation) {
            for (String attribute : commonAttributes) {
                returnAttributeIfExist(multimap, attribute, relationNames);
            }
        }

        Pair<String, Set<RowMap>> finalRelation = new ImmutablePair<>("", relation1.getRight());
        Set<RowMap> temp1 = CartesianProduct
                .product(Projection
                        .projection(finalRelation, commonAttributes), relation2.getRight());

        Pair<String, Set<RowMap>> temp2 =
                new ImmutablePair<>("", Sets.difference(temp1, finalRelation.getRight()));

        Set<RowMap> temp3 = Projection.projection(temp2, commonAttributes);
        return Sets.difference(Projection.projection(finalRelation, commonAttributes), temp3);
    }
}
