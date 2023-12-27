package com.interpreter.relational.operation;

import com.google.common.collect.Sets;
import com.interpreter.relational.dto.AttributeDto;
import com.interpreter.relational.dto.ComparatorParams;
import com.interpreter.relational.exception.BaseException;
import com.interpreter.relational.exception.StatusType;
import com.interpreter.relational.service.RowMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.interpreter.relational.util.AttributeProcessor.extractAttribute;
import static com.interpreter.relational.util.BasicUtilityMethods.*;
import static com.interpreter.relational.util.comparator.ComparatorMethods.*;
import static java.util.Map.entry;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

public class Select {
    public static Set<RowMap> selection(Pair<String, Set<RowMap>> relation, List<String> tokens) {
        Queue<String> RPN = shuntingYard(tokens);
        String relationName = relation.getLeft();
        Stack<Object> results = new Stack<>();

        for (String token : RPN) {
            Set<RowMap> result = new HashSet<>();
            switch (token) {
                case "=", "!=", ">", "<", ">=", "<=" -> {
                    String operandRight = (String) results.pop();
                    if (Objects.equals(results.peek(), "NOT")) {
                        token = revertToken(token);
                        results.pop();
                    }
                    String operandLeft = (String) results.pop();

                    ComparatorParams params = ComparatorParams.builder()
                            .token(token)
                            .operandLeft(operandLeft)
                            .operandRight(operandRight)
                            .build();
                    compareOperands(relation, relationName, params, result, results);
                }
                case "+", "-", "*", "/" -> {
                    String operandRight = (String) results.pop();
                    String operandLeft = (String) results.pop();
                    simpleMathParser(token, operandLeft, operandRight, results);
                }
                case "OR" -> {
                    var operandRight = (Set<RowMap>) results.pop();
                    var operandLeft = (Set<RowMap>) results.pop();
                    result = Sets.union(operandLeft, operandRight);
                    results.push(result);
                }
                case "AND" -> {
                    var operandRight = (Set<RowMap>) results.pop();
                    var operandLeft = (Set<RowMap>) results.pop();
                    result = Sets.intersection(operandLeft, operandRight);
                    results.push(result);
                }
                default -> results.push(token);
            }
        }

        if (results.empty()) {
            return new HashSet<>();
        }

        return (Set<RowMap>) results.firstElement();
    }

    private static void compareOperands(Pair<String, Set<RowMap>> relation,
                                        String relationName,
                                        ComparatorParams params,
                                        Set<RowMap> result,
                                        Stack<Object> results
    ) {
        AttributeDto attributeLeft = extractAttribute(relationName, params.getOperandLeft());
        AttributeDto attributeRight = extractAttribute(relationName, params.getOperandRight());
        if (attributeLeft != null && attributeRight != null) {
            List<String> attributes = List.of(attributeLeft.getAttribute(), attributeRight.getAttribute());
            for (RowMap map : relation.getRight()) {
                for (String attribute : attributes) {
                    if (incorrectAttribute(map, attribute)) {
                        throw new BaseException("Attribute " + attribute + " of relation "
                                + relationName + " doesn't exist", StatusType.CE.toString());
                    }
                }

                List<String> valuesOfLeft = map.get(attributeLeft.getAttribute());
                List<String> valuesOfRight = map.get(attributeRight.getAttribute());
                checkIfAttributeAndCompare(map, valuesOfLeft, valuesOfRight, result, params);
            }
            results.push(result);
        }
    }

    private static boolean incorrectAttribute(RowMap map, String value) {
        return !isQuoted(value) && !isCreatable(value) && !map.containsKey(value);
    }

    private static void checkIfAttributeAndCompare(RowMap map,
                                                   List<String> valuesOfLeft,
                                                   List<String> valuesOfRight,
                                                   Set<RowMap> result,
                                                   ComparatorParams params
    ) {
        if (!valuesOfLeft.isEmpty() && !valuesOfRight.isEmpty()) {
            valuesOfLeft.forEach(
                    valueLeft -> valuesOfRight.forEach(
                            valueRight -> {
                                params.setOperandLeft(valueLeft);
                                params.setOperandRight(valueRight);
                                valueComparator(map, params, result);
                            }
                    )
            );
        } else if (!valuesOfLeft.isEmpty()) {
            valuesOfLeft.forEach(valueLeft -> {
                params.setOperandLeft(valueLeft);
                valueComparator(map, params, result);
            });
        } else if (!valuesOfRight.isEmpty()) {
            valuesOfRight.forEach(valueRight -> {
                params.setOperandRight(valueRight);
                valueComparator(map, params, result);
            });
        } else {
            valueComparator(map, params, result);
        }
    }

    private static void valueComparator(RowMap map,
                                        ComparatorParams params,
                                        Set<RowMap> result
    ) {
        List<String> comparatorTokens = List.of(">", "<", ">=", "<=", "=", "!=");

        if (comparatorTokens.contains(params.getToken())
                && (numericComparator(params) || dateComparator(params) || isEqualOrNotEqualString(params))
        ) {
            result.add(map);
        }
    }

    private static String revertToken(String token) {
        Map<String, String> reversedTokens = Map.ofEntries(
                entry("=", "!="),
                entry("!=", "="),
                entry(">", "<="),
                entry("<", ">="),
                entry(">=", "<"),
                entry("<=", ">")
        );

        return reversedTokens.get(token);
    }

    private static void simpleMathParser(String token, String operandLeft, String operandRight, Stack<Object> results) {
        if (isCreatable(operandLeft) && isCreatable(operandRight)) {
            double valueLeft = Double.parseDouble(operandLeft);
            double valueRight = Double.parseDouble(operandRight);
            switch (token) {
                case "+" -> results.push(Double.toString(valueLeft + valueRight));
                case "-" -> results.push(Double.toString(valueLeft - valueRight));
                case "*" -> results.push(Double.toString(valueLeft * valueRight));
                case "/" -> results.push(Double.toString(valueLeft / valueRight));
            }
        } else {
            throw new BaseException("Incorrect mathematical expression", StatusType.CE.toString());
        }
    }

    private static Queue<String> shuntingYard(List<String> tokens) {
        Queue<String> outputQueue = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();
        Map<String, Integer> operators = Map.ofEntries(
                entry("OR", 1),
                entry("AND", 2),
                entry("=", 3),
                entry("!=", 3),
                entry(">", 3),
                entry("<", 3),
                entry(">=", 3),
                entry("<=", 3),
                entry("NOT", 4),
                entry("+", 5),
                entry("-", 5),
                entry("*", 6),
                entry("/", 6),
                entry("(", 7),
                entry(")", 7)
        );

        for (String token : tokens) {
            if (!operators.containsKey(token)) {
                outputQueue.offer(token);
            } else if (Objects.equals(token, "(")) {
                operatorStack.push(token);
            } else if (Objects.equals(token, ")")) {
                while (!operatorStack.isEmpty()
                        && !Objects.equals(operatorStack.peek(), "(")) {
                    outputQueue.offer(operatorStack.pop());
                }

                if (!operatorStack.empty() && Objects.equals(operatorStack.peek(), "(")) {
                    operatorStack.pop();
                }
            } else {
                while (!operatorStack.empty()
                        && operators.get(token) <= operators.get(operatorStack.peek())
                        && !Objects.equals(operatorStack.peek(), "(")
                ) {
                    outputQueue.offer(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }

        while (!operatorStack.empty()) {
            if (Objects.equals(operatorStack.peek(), "("))
                throw new BaseException("Invalid expression", StatusType.CE.toString());
            outputQueue.offer(operatorStack.pop());
        }

        return outputQueue;
    }
}
