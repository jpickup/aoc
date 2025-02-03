package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19Part2v2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day19.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Map<String, Workflow> workflows = lines.stream().filter(s -> !s.startsWith("{"))
                    .map(Workflow::parse)
                    .collect(Collectors.toMap(w -> w.name, w -> w));

            workflows.put("A", new Workflow("A", Collections.singletonList(new AcceptAllExpression())));
            workflows.put("R", new Workflow("A", Collections.singletonList(new RejectAllExpression())));

            System.out.println(workflows);

            Part part = process(Part.full(), workflows);

            System.out.println("Part 2 : " + part);
            BigInteger part2 = part.countValues();
            System.out.println("Part 2 count : " + part2);
            //                                    93081275000000
            System.out.println("Test target is 167409079868000");

            // 67815251120429 = too low (real input)
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static Part process(Part part, Map<String, Workflow> workflows) {
        return workflows.get("in").execute(part, workflows);
    }

    @RequiredArgsConstructor
    @Data
    static class Part {
        final Set<Range4D> ranges = new HashSet<>();

        public static Part empty() {
            return new Part().assignRanges(Collections.singleton(Range4D.empty()));
        }

        public static Part full() {
            return new Part().assignRanges(Collections.singleton(Range4D.full()));
        }

        public BigInteger countValues() {
            return ranges.stream().map(Range4D::countValues).reduce(BigInteger.ZERO, BigInteger::add);
        }

        public static Part union(Part part1, Part part2) {
            Set<Range4D> combined = new HashSet<>(part1.ranges);
            combined.addAll(part2.ranges);
            Part result = new Part().assignRanges(combined);
            System.out.printf("Union of %s and %s is %s%n", part1, part2, result);
            return result;
        }

        public void assign(Part part) {
            assignRanges(part.ranges);
        }

        public void clear() {
            this.ranges.clear();
        }

        @Override
        public String toString() {
            return "Part{" +
                    ranges +
                    '}';
        }

        public Part assignRanges(Set<Range4D> ranges) {
            this.ranges.clear();
            this.ranges.addAll(ranges);
            return this;
        }
    }

    @Data
    static class Range4D {
        final Range x;
        final Range m;
        final Range a;
        final Range s;

        public Range4D(Range x, Range m, Range a, Range s) {
            this.x = new Range(x);
            this.m = new Range(m);
            this.a = new Range(a);
            this.s = new Range(s);
        }

        public static Range4D full() {
            return new Range4D(Range.full(), Range.full(), Range.full(), Range.full());
        }

        public static Range4D empty() {
            return new Range4D(Range.empty(), Range.empty(), Range.empty(), Range.empty());
        }

        public Range getRangeForVariable(char variable) {
            switch (variable) {
                case 'x': return x;
                case 'm': return m;
                case 'a': return a;
                case 's': return s;
                default: throw new RuntimeException("Unknown variable " + variable);
            }
        }

        public void setRangeForVariable(char variable, Range r) {
            switch (variable) {
                case 'x': x.assign(r); break;
                case 'm': m.assign(r); break;
                case 'a': a.assign(r); break;
                case 's': s.assign(r); break;
                default: throw new RuntimeException("Unknown variable " + variable);
            }
        }

        public BigInteger countValues() {
            return x.countValues()
                    .multiply(m.countValues())
                    .multiply(a.countValues())
                    .multiply(s.countValues());
        }

        @Override
        public String toString() {
            return "{x=" + x +
                    ", m=" + m +
                    ", a=" + a +
                    ", s=" + s + "}";
        }
    }

    @Data
    static class Range {
        static final int MIN_RANGE = 1;
        static final int MAX_RANGE = 4000;
        final Set<Integer> values;

        public Range(Set<Integer> values) {
            this.values = new TreeSet<>(values);
        }

        public Range(Range other) {
            this(other.values);
        }

        public static Range full() {
            Set<Integer>values = new TreeSet<>();
            for (int i=MIN_RANGE; i <= MAX_RANGE; i++) {
                values.add(i);
            }
            return new Range(values);
        }

        public static Range empty() {
            return new Range(new TreeSet<>());
        }

        public BigInteger countValues() {
            return BigInteger.valueOf(values.size());
        }

        public Range matches(Operator op, int value) {
            return new Range(values.stream().filter(v -> op.evaluate(v, value)).collect(Collectors.toSet()));
        }

        public Range mismatches(Operator op, int value) {
            return new Range(values.stream().filter(v -> !op.evaluate(v, value)).collect(Collectors.toSet()));
        }

        public Range union(Range other) {
            Set<Integer> result = new TreeSet<>(this.values);
            result.addAll(other.values);
            return new Range(result);
        }

        @Override
        public String toString() {
            return ""+values.size();
        }

    public void clear() {
        values.clear();
    }

    public void assign(Range other) {
        values.clear();
        values.addAll(other.values);
    }
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

        public Part execute(Part part, Map<String, Workflow> workflows) {
            return executeExpressions(part, workflows, this.expressions);
        }

        public static Part executeExpressions(Part part, Map<String, Workflow> workflows, List<Expression> expressions) {
            System.out.println("Executing expressions : " + expressions + " on " + part);
            if (expressions.isEmpty()) return part;

            Part matches = Part.empty();
            Part mismatches = Part.empty();

            Expression expression = expressions.get(0);
            String partitionResult = expression.partition(part, matches, mismatches);
            System.out.printf("Partition of %s on %s gives%n\tmatches %s%n\tmismatches %s%n", expression, part, matches, mismatches);

            Workflow nextWorkflow = workflows.get(partitionResult);

            if (nextWorkflow==null) {
                return matches;
            }

            return Part.union(
                    nextWorkflow.execute(matches, workflows),
                    executeExpressions(mismatches, workflows, expressions.subList(1, expressions.size())));
        }
    }

    @RequiredArgsConstructor
    @Data
    static abstract class Expression {
        public static Expression parse(String input) {
            String[] parts = input.split(":");
            if (parts.length == 1) return new FixedExpression(input);

            if (parts.length != 2) throw new RuntimeException("Invalid expression: " + input);
            char variable = parts[0].charAt(0);
            Operator op = (parts[0].contains("<")) ? Operator.LESS_THAN : Operator.GREATER_THAN;
            int value = Integer.parseInt(parts[0].substring(2));
            return new RelopExpression(variable, op, value, parts[1]);
        }

        public abstract String partition(Part input, Part matches, Part mismatches);

        public abstract String getResult();
    }

    @RequiredArgsConstructor
    @Data
    static class AcceptAllExpression extends Expression {
        @Override
        public String partition(Part input, Part matches, Part mismatches) {
            System.out.printf("Accept All partition - matches are all of input %s%n", input);

            matches.assign(input);
            mismatches.clear();

            return "";
        }

        @Override
        public String getResult() {
            return "";
        }

        @Override
        public String toString() {
            return "AcceptAll";
        }
    }

    @RequiredArgsConstructor
    @Data
    static class RejectAllExpression extends Expression {
        @Override
        public String partition(Part input, Part matches, Part mismatches) {
            System.out.printf("Reject All partition - mismatches are all of input %s%n", input);

            mismatches.clear();
            matches.clear();

            return "";
        }

        @Override
        public String getResult() {
            return "";
        }

        @Override
        public String toString() {
            return "RejectAll";
        }
    }


    @RequiredArgsConstructor
    @Data
    static class FixedExpression extends Expression {
        final String result;

        @Override
        public String partition(Part input, Part matches, Part mismatches) {
            System.out.printf("Fixed partition %s - matches are all of input %s%n", result, input);

            matches.assign(input);
            mismatches.clear();

            return result;
        }

        @Override
        public String toString() {
            return "Fixed='" + result + '\'';
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
        public String partition(Part input, Part matches, Part mismatches) {
            System.out.printf("Relop partition %s%s%s -> %s of %s%n", variable, op, value, result, input);

            // split every range into two
            Set<Range4D> variableAllMatches = new HashSet<>();
            Set<Range4D> variableAllMismatches = new HashSet<>();
            for (Range4D range : input.ranges) {
                Range variableMatchesRange = range.getRangeForVariable(variable).matches(op, value);
                Range4D matchesRange = new Range4D(range.x, range.m, range.a, range.s);
                matchesRange.setRangeForVariable(variable, variableMatchesRange);
                variableAllMatches.add(matchesRange);

                Range variableMismatchesRange = range.getRangeForVariable(variable).mismatches(op, value);
                Range4D mismatchesRange = new Range4D(range.x, range.m, range.a, range.s);
                mismatchesRange.setRangeForVariable(variable, variableMismatchesRange);
                variableAllMismatches.add(mismatchesRange);
            }
            matches.assignRanges(variableAllMatches);
            mismatches.assignRanges(variableAllMismatches);
            return result;
        }

        @Override
        public String toString() {
            return "(" +
                    variable +
                    op +
                    value +
                    ')';
        }
    }

    @RequiredArgsConstructor
    enum Operator {
        LESS_THAN('<'),
        GREATER_THAN('>');
        final char ch;

        public static Operator parse(String input) {
            switch (input) {
                case "<":
                    return LESS_THAN;
                case ">":
                    return GREATER_THAN;
                default:
                    throw new RuntimeException("Unknown input " + input);
            }
        }

        public boolean evaluate(int value, int constant) {
            switch (this) {
                case LESS_THAN:
                    return value < constant;
                case GREATER_THAN:
                    return value > constant;
                default:
                    throw new RuntimeException("Unsupported operation");
            }
        }

        @Override
        public String toString() {
            return "" + ch;
        }
    }

}
