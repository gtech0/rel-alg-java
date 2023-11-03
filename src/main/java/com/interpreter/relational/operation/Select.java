package com.interpreter.relational.operation;

import com.interpreter.relational.exception.BaseException;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class Select {
    public static Set<Multimap<String, String>> selection(Set<Multimap<String, String>> relation,
                                                          List<String> tokens) {
        Queue<String> RPN = shuntingYard(tokens);

        Stack<Object> results = new Stack<>();
        for (String token : RPN) {
            boolean notCheck = false;
            Object op1;
            Object op2;
            String finalOp1;
            String finalOp2;
            Set<Multimap<String, String>> result;
            //try {
            switch (token) {
                case "=":
                case "!=":
                case ">":
                case "<":
                case ">=":
                case "<=":
                    op1 = results.pop();
                    if (Objects.equals(results.peek(), "NOT")) {
                        notCheck = true;
                        results.pop();
                    }
                    boolean finalNotCheck1 = notCheck;

                    op2 = results.pop();

                    result = new HashSet<>();
                    finalOp2 = (String) op2;
                    finalOp1 = (String) op1;
                    relation.forEach(map -> {
                                Collection<String> mapValues2 = map.get(finalOp2);
                                Collection<String> mapValues1 = map.get(finalOp1);
                                if (!mapValues2.isEmpty() && !mapValues1.isEmpty()) {
                                    mapValues2.forEach(
                                            value2 -> mapValues1.forEach(
                                                    value1 -> valueComparator(
                                                            token,
                                                            map,
                                                            value2,
                                                            value1,
                                                            finalNotCheck1,
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
                                                    finalOp1,
                                                    finalNotCheck1,
                                                    result
                                            )
                                    );
                                } else if (!mapValues1.isEmpty()) {
                                    mapValues1.forEach(
                                            value1 -> valueComparator(
                                                    token,
                                                    map,
                                                    finalOp2,
                                                    value1,
                                                    finalNotCheck1,
                                                    result
                                            )
                                    );
                                } else  {
                                    valueComparator(
                                            token,
                                            map,
                                            finalOp2,
                                            finalOp1,
                                            finalNotCheck1,
                                            result
                                    );
                                }
                            }
                    );
                    results.push(result);
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    op1 = results.pop();
                    op2 = results.pop();
                    finalOp1 = (String) op1;
                    finalOp2 = (String) op2;
                    if (isNumeric(finalOp1) && isNumeric(finalOp2)) {
                        double val1 = Double.parseDouble(finalOp1);
                        double val2 = Double.parseDouble(finalOp2);
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
                    break;
                case "OR":
                    op1 = results.pop();
                    op2 = results.pop();
                    result = Sets.union((Set<Multimap<String, String>>) op2, (Set<Multimap<String, String>>) op1);
                    results.push(result);
                    break;
                case "AND":
                    op1 = results.pop();
                    op2 = results.pop();
                    result = Sets.intersection((Set<Multimap<String, String>>) op2, (Set<Multimap<String, String>>) op1);
                    results.push(result);
                    break;
                default:
                    results.push(token);
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
