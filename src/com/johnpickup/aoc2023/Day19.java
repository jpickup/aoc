package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day19.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Map<String, Workflow> workflows = lines.stream().filter(s -> !s.startsWith("{"))
                    .map(Workflow::parse)
                    .collect(Collectors.toMap(w -> w.name, w -> w));
            List<Part> parts = lines.stream().filter(s -> s.startsWith("{")).map(Part::parse).collect(Collectors.toList());

            System.out.println(parts);
            System.out.println(workflows);

            List<Part> accepted = new ArrayList<>();
            List<Part> rejected = new ArrayList<>();

            process(parts, workflows, accepted, rejected);

            Long part1 = accepted.stream().map(p -> (long) p.a + p.m + p.x + p.s).reduce(0L, Long::sum);
            System.out.println("Accepted: " + accepted);
            System.out.println("Rejected: " + rejected);

            System.out.println("Part 1 : " + part1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static void process(List<Part> parts, Map<String, Workflow> workflows, List<Part> accepted, List<Part> rejected) {
        for (Part part : parts) {
            process(part, workflows, accepted, rejected);
        }
    }

    private static void process(Part part, Map<String, Workflow> workflows, List<Part> accepted, List<Part> rejected) {
        Workflow firstWorkflow = workflows.get("in");
        String result = firstWorkflow.execute(part, workflows);
        if (result.equals("A")) accepted.add(part);
        else if (result.equals("R")) rejected.add(part);
        else throw new RuntimeException("Processing failed for part " + part);
    }

    @RequiredArgsConstructor
    @Data
    static class Part {
        public static Part parse(String input) {
            if (input.charAt(0) == '{') {
                int x=0;
                int m=0;
                int a=0;
                int s=0;
                String[] parts = input.substring(1, input.length() - 1).split(",");
                for (String part : parts) {
                    String[] parts2 = part.split("=");
                    switch (parts2[0]) {
                        case "x": x = Integer.parseInt(parts2[1]); break;
                        case "m": m = Integer.parseInt(parts2[1]); break;
                        case "a": a = Integer.parseInt(parts2[1]); break;
                        case "s": s = Integer.parseInt(parts2[1]); break;
                        default: throw new RuntimeException("Unknown variable in " + input);
                    }
                }
                return new Part(x,m,a,s);
            }
            else {
                return null;
            }
        }
        final int x;
        final int m;
        final int a;
        final int s;
    }

    @RequiredArgsConstructor
    @Data
    static class Workflow {
        public static Workflow parse(String input) {
            String[] parts = input.split("\\{");
            List<Expression> expressions = Arrays.stream(parts[1].substring(0, parts[1].length() - 1).split(","))
                    .map(Expression::parse)
                    .collect(Collectors.toList());
            return new Workflow(parts[0], expressions);
        }
        final String name;
        final List<Expression> expressions;

        public boolean matches(Part part) {
            return expressions.get(0).matches(part);
        }

        public String execute(Part part, Map<String, Workflow> workflows) {
            int expressionIdx = 0;

            while (!expressions.get(expressionIdx).matches(part)) expressionIdx++;

            String result = expressions.get(expressionIdx).getResult();

            if (workflows.containsKey(result)) {
                return workflows.get(result).execute(part, workflows);
            } else {
                return result;
            }
        }
    }

    @RequiredArgsConstructor
    @Data
    static abstract class Expression {
        public static Expression parse(String input) {
            String[] parts = input.split(":");
            if (parts.length==1) return new FixedExpression(input);

            if (parts.length != 2) throw new RuntimeException("Invalid expression: " + input);
            char variable = parts[0].charAt(0);
            Operator op = (parts[0].contains("<"))?Operator.LESS_THAN : Operator.GREATER_THAN;
            int value = Integer.parseInt(parts[0].substring(2));
            return new RelopExpression(variable, op, value, parts[1]);
        }

        public abstract boolean matches(Part part);
        public abstract String getResult();
    }

    @RequiredArgsConstructor
    @Data
    static class FixedExpression extends Expression {
        final String result;

        @Override
        public boolean matches(Part part) {
            return true;
        }
    }

    @RequiredArgsConstructor
    @Data
    static class RelopExpression extends Expression {
        final char variable;
        final Operator op;
        final int value;
        final String result;

        @Override
        public boolean matches(Part part) {
            switch (variable) {
                case 'x': return op.evaluate(part.x, value);
                case 'm': return op.evaluate(part.m, value);
                case 'a': return op.evaluate(part.a, value);
                case 's': return op.evaluate(part.s, value);
                default: throw new RuntimeException("Unknown variable " + variable);
            }
        }
    }

    @RequiredArgsConstructor
    enum Operator {
        LESS_THAN('<'),
        GREATER_THAN('>');
        final char ch;
        public static Operator parse(String input) {
            switch (input) {
                case "<": return LESS_THAN;
                case ">": return GREATER_THAN;
                default: throw new RuntimeException("Unknown input " + input);
            }
        }

        public boolean evaluate(int value, int constant) {
            switch (this) {
                case LESS_THAN: return value < constant;
                case GREATER_THAN: return value > constant;
                default: throw new RuntimeException("Unsupported operation");
            }
        }
    }
}
