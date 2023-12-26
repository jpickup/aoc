package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day21 {
    static Map<String, Monkey> monkeyMap;

    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day21-test.txt"))) {
            long start = System.currentTimeMillis();
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Day21 day21 = new Day21(lines);

            //System.out.println(day21.solvePart1());
            System.out.println(day21.solvePart2());
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Day21(List<String> input) {
        monkeyMap = input.stream().map(Monkey::parse).collect(Collectors.toMap(m -> m.name, m -> m));
    }

    private BigDecimal solvePart1() {
        Monkey root = monkeyMap.get("root");
        return root.getValue();
    }

    private String solvePart2() {
        Monkey root = monkeyMap.get("root");
        return root.getLeft().getExpression() + " = " + root.getRight().getExpression();
    }

    @Builder
    static class Monkey {
        String name;
        BigDecimal value;
        String leftName;
        String rightName;
        Monkey lhs;
        Monkey rhs;
        Operator operator;

        static Monkey parse(String s) {
            String[] parts = s.split(":");
            String n = parts[0];
            try {
                int literal = Integer.parseInt(parts[1].trim());
                return Monkey.builder()
                        .name(n).value(BigDecimal.valueOf(literal)).build();
            } catch (NumberFormatException ignore) {
            }
            String[] expressionParts = parts[1].trim().split(" ");
            return Monkey.builder()
                    .name(n)
                    .leftName(expressionParts[0].trim()).operator(Operator.parse(expressionParts[1])).rightName(expressionParts[2].trim()).build();
        }

        String getExpression() {
            if (name.equals("humn")) return "X";
            if (value != null) {
                return value.toString();
            }
            return "(" + getLeft().getExpression() + operator.ch + getRight().getExpression() + ")";
        }

        BigDecimal getValue() {
            if (value == null) {
                value = evaluate();
            }
            return value;
        }

        private BigDecimal evaluate() {
            BigDecimal left = getLeft().getValue();
            BigDecimal right = getRight().getValue();
            switch (operator) {
                case ADD:
                    return left.add(right);
                case SUBTRACT:
                    return left.subtract(right);
                case MULTIPLY:
                    return left.multiply(right);
                case DIVIDE:
                    return left.divide(right, 10, RoundingMode.HALF_UP);
                default:
                    throw new RuntimeException("Unknown operation " + operator);
            }
        }

        private Monkey getLeft() {
            if (lhs == null)
                lhs = monkeyMap.get(leftName);
            return lhs;
        }

        private Monkey getRight() {
            if (rhs == null)
                rhs = monkeyMap.get(rightName);
            return rhs;
        }

        @Override
        public String toString() {
            return "Monkey{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    ", leftName='" + leftName + '\'' + ", rightName='" + rightName + '\'' +
                    ", operator=" + operator +
                    '}';
        }
    }

    @RequiredArgsConstructor
    enum Operator {
        ADD('+'),
        SUBTRACT('-'),
        MULTIPLY('*'),
        DIVIDE('/');
        final char ch;

        static Operator parse(String s) {
            switch (s) {
                case "+":
                    return ADD;
                case "-":
                    return SUBTRACT;
                case "*":
                    return MULTIPLY;
                case "/":
                    return DIVIDE;
                default:
                    throw new RuntimeException("Unknown operator " + s);
            }
        }
    }
}
