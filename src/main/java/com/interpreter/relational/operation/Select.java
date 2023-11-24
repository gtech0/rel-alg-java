package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.exception.BaseException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.interpreter.relational.util.AttributeClass.extractAttribute;
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
            boolean notCheck = false;
            Set<Multimap<String, String>> result = new HashSet<>();
            switch (token) {
                case "=", "!=", ">", "<", ">=", "<=" -> {
                    String operand1 = (String) results.pop();
                    if (Objects.equals(results.peek(), "NOT")) {
                        notCheck = true;
                        results.pop();
                    }
                    String operand2 = (String) results.pop();

                    for (Multimap<String, String> map : relation.getRight()) {
                        String attribute1 = extractAttribute(List.of(relationName), operand1);
                        String attribute2 = extractAttribute(List.of(relationName), operand2);

                        Collection<String> mapValues1 = map.get(attribute1);
                        Collection<String> mapValues2 = map.get(attribute2);
                        checkIfAttrAndCompare(token, map, mapValues1, mapValues2, notCheck, result, operand1, operand2);
                    }
                    results.push(result);
                }
                case "+", "-", "*", "/" -> {
                    String operand1 = (String) results.pop();
                    String operand2 = (String) results.pop();
                    simpleMathParser(token, operand1, operand2, results);
                }
                case "OR" -> {
                    var operand1 = (Set<Multimap<String, String>>) results.pop();
                    var operand2 = (Set<Multimap<String, String>>) results.pop();
                    result = Sets.union(operand2, operand1);
                    results.push(result);
                }
                case "AND" -> {
                    var operand1 = (Set<Multimap<String, String>>) results.pop();
                    var operand2 = (Set<Multimap<String, String>>) results.pop();
                    result = Sets.intersection(operand2, operand1);
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

    private static void checkIfAttrAndCompare(String token,
                                              Multimap<String, String> map,
                                              Collection<String> mapValues2,
                                              Collection<String> mapValues1,
                                              boolean notCheck,
                                              Set<Multimap<String, String>> result,
                                              String operand2, String operand1
    ) {
        if (!mapValues2.isEmpty() && !mapValues1.isEmpty()) {
            mapValues2.forEach(
                    value2 -> mapValues1.forEach(
                            value1 -> valueComparator(token, map, value2, value1, notCheck, result)
                    )
            );
        } else if (!mapValues2.isEmpty()) {
            mapValues2.forEach(value2 -> valueComparator(token, map, value2, operand1, notCheck, result));
        } else if (!mapValues1.isEmpty()) {
            mapValues1.forEach(value1 -> valueComparator(token, map, operand2, value1, notCheck, result));
        } else {
            valueComparator(token, map, operand2, operand1, notCheck, result);
        }
    }

    private static void valueComparator(
            String token,
            Multimap<String, String> map,
            String value2,
            String value1,
            boolean notCheck,
            Set<Multimap<String, String>> result
    ) {
        List<String> comparatorTokens = List.of(">", "<", ">=", "<=", "=", "!=");

        if (comparatorTokens.contains(token)
                && (numericComparator(token, notCheck, value1, value2)
                || dateComparator(token, notCheck, value1, value2)
                || isEqualOrNotEqualString(token, notCheck, value1, value2))
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
