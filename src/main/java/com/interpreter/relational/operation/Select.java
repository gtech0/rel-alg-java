package com.interpreter.relational.operation;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.interpreter.relational.exception.BaseException;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class Select {
    public static Set<Multimap<String, String>> selection(Set<Multimap<String, String>> relation,
                                                          List<String> tokens) {
        Queue<String> RPN = shuntingYard(tokens);

        Stack<Object> results = new Stack<>();
        for (String token : RPN) {
            boolean notCheck = false;
            Set<Multimap<String, String>> result;
            //try {
            switch (token) {
                case "=", "!=", ">", "<", ">=", "<=" -> {
                    String op1 = (String) results.pop();
                    if (Objects.equals(results.peek(), "NOT")) {
                        notCheck = true;
                        results.pop();
                    }
                    boolean finalNotCheck = notCheck;
                    String op2 = (String) results.pop();

                    result = new HashSet<>();
                    relation.forEach(map -> {
                                Collection<String> mapValues2 = map.get(op1);
                                Collection<String> mapValues1 = map.get(op2);
                                if (!mapValues2.isEmpty() && !mapValues1.isEmpty()) {
                                    mapValues2.forEach(
                                            value2 -> mapValues1.forEach(
                                                    value1 -> valueComparator(
                                                            token,
                                                            map,
                                                            value2,
                                                            value1,
                                                            finalNotCheck,
                                                            result
                                                    )
                                            )
                                    );
                                } else if (!mapValues2.isEmpty()) {
                                    mapValues2.forEach(
                                            value2 -> valueComparator(
                                                    token,
                                                    map,
                                                    value2,
                                                    op1,
                                                    finalNotCheck,
                                                    result
                                            )
                                    );
                                } else if (!mapValues1.isEmpty()) {
                                    mapValues1.forEach(
                                            value1 -> valueComparator(
                                                    token,
                                                    map,
                                                    op2,
                                                    value1,
                                                    finalNotCheck,
                                                    result
                                            )
                                    );
                                } else {
                                    valueComparator(
                                            token,
                                            map,
                                            op2,
                                            op1,
                                            finalNotCheck,
                                            result
                                    );
                                }
                            }
                    );
                    results.push(result);
                }
                case "+", "-", "*", "/" -> {
                    String op1 = (String) results.pop();
                    String op2 = (String) results.pop();
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
                case "OR" -> {
                    Set<Multimap<String, String>> op1 = (Set<Multimap<String, String>>) results.pop();
                    Set<Multimap<String, String>> op2 = (Set<Multimap<String, String>>) results.pop();
                    result = Sets.union(op2, op1);
                    results.push(result);
                }
                case "AND" -> {
                    Set<Multimap<String, String>> op1 = (Set<Multimap<String, String>>) results.pop();
                    Set<Multimap<String, String>> op2 = (Set<Multimap<String, String>>) results.pop();
                    result = Sets.intersection(op2, op1);
                    results.push(result);
                }
                default -> results.push(token);
            }
//            } catch (Exception e) {
//                throw new BaseException("Incorrect SELECT syntax");
//            }
        }

        if (results.empty()) {
            return new HashSet<>();
        }
        return (Set<Multimap<String, String>>) results.firstElement();
    }

    private static void valueComparator(
            String token,
            Multimap<String, String> map,
            String value,
            String op1,
            boolean finalNotCheck1,
            Set<Multimap<String, String>> result
    ) {
        if (Objects.equals(token, ">")) {
            double currentVal = Double.parseDouble(value);
            double newVal = Double.parseDouble(op1);
            if (currentVal > newVal && !finalNotCheck1 || currentVal <= newVal && finalNotCheck1) {
                result.add(map);
            }
        }

        if (Objects.equals(token, "<")) {
            double currentVal = Double.parseDouble(value);
            double newVal = Double.parseDouble(op1);
            if (currentVal < newVal && !finalNotCheck1 || currentVal >= newVal && finalNotCheck1) {
                result.add(map);
            }
        }

        if (Objects.equals(token, ">=")) {
            double currentVal = Double.parseDouble(value);
            double newVal = Double.parseDouble(op1);
            if (currentVal >= newVal && !finalNotCheck1 || currentVal < newVal && finalNotCheck1) {
                result.add(map);
            }
        }

        if (Objects.equals(token, "<=")) {
            double currentVal = Double.parseDouble(value);
            double newVal = Double.parseDouble(op1);
            if (currentVal <= newVal && !finalNotCheck1 || currentVal > newVal && finalNotCheck1) {
                result.add(map);
            }
        }

        if (Objects.equals(token, "=") || Objects.equals(token, "!=")) {
            if (isNumeric(value) && isNumeric(op1)) {
                double currentVal = Double.parseDouble(value);
                double newVal = Double.parseDouble(op1);
                if ((Objects.equals(token, "=") &&
                        (currentVal == newVal && !finalNotCheck1 || currentVal != newVal && finalNotCheck1))

                        || (Objects.equals(token, "!=")
                        && (currentVal != newVal && !finalNotCheck1 || currentVal == newVal && finalNotCheck1))
                ) {
                    result.add(map);
                }
            } else if (op1.startsWith("\"")
                    && op1.endsWith("\"")
                    && op1.chars().filter(c -> c == '\"').count() == 2
            ) {
                String currentVal = value.replaceAll("\"", "");
                String newVal = op1.replaceAll("\"", "");
                if ((Objects.equals(token, "=")
                        && (Objects.equals(currentVal, newVal) && !finalNotCheck1 || !Objects.equals(currentVal, newVal) && finalNotCheck1))

                        || (Objects.equals(token, "!=")
                        && (!Objects.equals(currentVal, newVal) && !finalNotCheck1 || Objects.equals(currentVal, newVal) && finalNotCheck1))
                ) {
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
