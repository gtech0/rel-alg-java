package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.dto.ComparatorParams;
import com.interpreter.relational.exception.BaseException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.interpreter.relational.util.AttributeProcessor.extractAttribute;
import static com.interpreter.relational.util.comparator.ComparatorMethods.*;
import static com.interpreter.relational.util.comparator.ComparatorMethods.isANumber;
import static java.util.Map.entry;

public class Select {
    public static Set<Multimap<String, String>> selection(Pair<String, Set<Multimap<String, String>>> relation,
                                                          List<String> tokens) {
        Queue<String> RPN = shuntingYard(tokens);
        String relationName = relation.getLeft();
        Stack<Object> results = new Stack<>();

        for (String token : RPN) {
            Set<Multimap<String, String>> result = new HashSet<>();
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
                    var operandRight = (Set<Multimap<String, String>>) results.pop();
                    var operandLeft = (Set<Multimap<String, String>>) results.pop();
                    result = Sets.union(operandLeft, operandRight);
                    results.push(result);
                }
                case "AND" -> {
                    var operandRight = (Set<Multimap<String, String>>) results.pop();
                    var operandLeft = (Set<Multimap<String, String>>) results.pop();
                    result = Sets.intersection(operandLeft, operandRight);
                    results.push(result);
                }
                default -> results.push(token);
            }
        }

        if (results.empty()) {
            return new HashSet<>();
        }

        return (Set<Multimap<String, String>>) results.firstElement();
    }

    private static void compareOperands(Pair<String, Set<Multimap<String, String>>> relation,
                                        String relationName,
                                        ComparatorParams params,
                                        Set<Multimap<String, String>> result,
                                        Stack<Object> results
    ) {
        String attributeLeft = extractAttribute(List.of(relationName), params.getOperandLeft());
        String attributeRight = extractAttribute(List.of(relationName), params.getOperandRight());

        for (Multimap<String, String> map : relation.getRight()) {
            Collection<String> valuesOfLeft = map.get(attributeLeft);
            Collection<String> valuesOfRight = map.get(attributeRight);

            checkIfAttributeAndCompare(map, valuesOfLeft, valuesOfRight, result, params);
        }
        results.push(result);
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

    private static void simpleMathParser(String token, String operand1, String operand2, Stack<Object> results) {
        if (isANumber(operand1) && isANumber(operand2)) {
            double val1 = Double.parseDouble(operand1);
            double val2 = Double.parseDouble(operand2);
            switch (token) {
                case "+":
                    results.push(Double.toString(val2 + val1));
                    break;
                case "-":
                    results.push(Double.toString(val2 - val1));
                    break;
                case "*":
                    results.push(Double.toString(val2 * val1));
                    break;
                case "/":
                    results.push(Double.toString(val2 / val1));
                    break;
            }
        } else {
            throw new BaseException("Incorrect mathematical expression");
        }
    }

    private static void checkIfAttributeAndCompare(Multimap<String, String> map,
                                                   Collection<String> valuesOfLeft,
                                                   Collection<String> valuesOfRight,
                                                   Set<Multimap<String, String>> result,
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

    private static void valueComparator(Multimap<String, String> map,
                                        ComparatorParams params,
                                        Set<Multimap<String, String>> result
    ) {
        List<String> comparatorTokens = List.of(">", "<", ">=", "<=", "=", "!=");

        if (comparatorTokens.contains(params.getToken())
                && (numericComparator(params) || dateComparator(params) || isEqualOrNotEqualString(params))
        ) {
                result.add(map);
        }
    }

    public static Queue<String> shuntingYard(List<String> tokens) {
        Queue<String> outputQueue = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();
        Map<String, Integer> operators = Map.ofEntries(
                entry("AND", 1),
                entry("OR", 1),
                entry("=", 2),
                entry("!=", 2),
                entry(">", 2),
                entry("<", 2),
                entry(">=", 2),
                entry("<=", 2),
                entry("NOT", 3),
                entry("+", 4),
                entry("-", 4),
                entry("*", 5),
                entry("/", 5),
                entry("(", 6),
                entry(")", 6)
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
                throw new BaseException("Invalid expression");
            outputQueue.offer(operatorStack.pop());
        }

        return outputQueue;
    }
}
