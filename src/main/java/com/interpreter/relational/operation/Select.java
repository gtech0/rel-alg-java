package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.exception.BaseException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.interpreter.relational.util.AttributeClass.extractAttribute;
import static com.interpreter.relational.util.ComparatorMethods.*;
import static org.apache.commons.lang3.StringUtils.isNumeric;

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
                    String op1 = (String) results.pop();
                    if (Objects.equals(results.peek(), "NOT")) {
                        notCheck = true;
                        results.pop();
                    }
                    String op2 = (String) results.pop();

                    for (Multimap<String, String> map : relation.getRight()) {
                        String attribute1 = extractAttribute(List.of(relationName), op1);
                        String attribute2 = extractAttribute(List.of(relationName), op2);

                        Collection<String> mapValues1 = map.get(attribute1);
                        Collection<String> mapValues2 = map.get(attribute2);
                        attributeExist(token, map, mapValues1, mapValues2, notCheck, result, op1, op2);
                    }
                    results.push(result);
                }
                case "+", "-", "*", "/" -> {
                    String op1 = (String) results.pop();
                    String op2 = (String) results.pop();
                    simpleMathParser(token, op1, op2, results);
                }
                case "OR" -> {
                    var op1 = (Set<Multimap<String, String>>) results.pop();
                    var op2 = (Set<Multimap<String, String>>) results.pop();
                    result = Sets.union(op2, op1);
                    results.push(result);
                }
                case "AND" -> {
                    var op1 = (Set<Multimap<String, String>>) results.pop();
                    var op2 = (Set<Multimap<String, String>>) results.pop();
                    result = Sets.intersection(op2, op1);
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

    private static void simpleMathParser(String token, String op1, String op2, Stack<Object> results) {
        if (isNumeric(op1) && isNumeric(op2)) {
            double val1 = Double.parseDouble(op1);
            double val2 = Double.parseDouble(op2);
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

    private static void attributeExist(String token,
                                       Multimap<String, String> map,
                                       Collection<String> mapValues2,
                                       Collection<String> mapValues1,
                                       boolean notCheck,
                                       Set<Multimap<String, String>> result,
                                       String op2, String op1
    ) {
        if (!mapValues2.isEmpty() && !mapValues1.isEmpty()) {
            mapValues2.forEach(
                    value2 -> mapValues1.forEach(
                            value1 -> valueComparator(token, map, value2, value1, notCheck, result)
                    )
            );
        } else if (!mapValues2.isEmpty()) {
            mapValues2.forEach(value2 -> valueComparator(token, map, value2, op1, notCheck, result));
        } else if (!mapValues1.isEmpty()) {
            mapValues1.forEach(value1 -> valueComparator(token, map, op2, value1, notCheck, result));
        } else {
            valueComparator(token, map, op2, op1, notCheck, result);
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
        List<String> comparatorTokens = List.of(">", "<", ">=", "<=");

        if (comparatorTokens.contains(token)) {
            double currentVal = Double.parseDouble(value1);
            double newVal = Double.parseDouble(value2);

            if (greaterThan(token, notCheck, currentVal, newVal)
                    || lessThan(token, notCheck, currentVal, newVal)
                    || greaterThanOrEqual(token, notCheck, currentVal, newVal)
                    || lessThanOrEqual(token, notCheck, currentVal, newVal)
            ) {
                result.add(map);
            }
        }

        if (Objects.equals(token, "=") || Objects.equals(token, "!=")) {
            if (isNumeric(value1) && isNumeric(value2)) {
                double currentVal = Double.parseDouble(value1);
                double newVal = Double.parseDouble(value2);
                if (numericValuesEqual(token, notCheck, currentVal, newVal)) {
                    result.add(map);
                }
            } else if (value2.startsWith("\"")
                    && value2.endsWith("\"")
                    && value2.chars().filter(c -> c == '\"').count() == 2
            ) {
                String currentStrVal = value1.replaceAll("\"", "");
                String newStrVal = value2.replaceAll("\"", "");
                if (stringValuesEqual(token, notCheck, currentStrVal, newStrVal)) {
                    result.add(map);
                }
            }
        }
    }

    public static Queue<String> shuntingYard(List<String> tokens) {
        Queue<String> outputQueue = new LinkedList<>();
        Stack<String> operatorStack = new Stack<>();
        Map<String, Integer> operators = Map.ofEntries(
                Map.entry("AND", 1),
                Map.entry("OR", 1),
                Map.entry("=", 2),
                Map.entry("!=", 2),
                Map.entry(">", 2),
                Map.entry("<", 2),
                Map.entry(">=", 2),
                Map.entry("<=", 2),
                Map.entry("NOT", 3),
                Map.entry("+", 4),
                Map.entry("-", 4),
                Map.entry("*", 5),
                Map.entry("/", 5),
                Map.entry("(", 6),
                Map.entry(")", 6)
        );

        for (String token : tokens) {
            if (!(operators.containsKey(token)
                    || Objects.equals(token, "(")
                    || Objects.equals(token, ")"))
            ) {
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
            } else if (operators.containsKey(token)) {
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
