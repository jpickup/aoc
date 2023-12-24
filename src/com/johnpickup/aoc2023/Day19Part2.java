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

public class Day19Part2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day19-test2.txt"))) {
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
        final Range4D ranges;

        public static Part full() {
            return new Part(Range4D.full());
        }

        public BigInteger countValues() {
            return ranges.countValues();
        }

        public static Part union(Part part1, Part part2) {
            Part result = new Part(part1.ranges.union(part2.ranges));
            System.out.printf("Union of %s and %s is %s%n", part1, part2, result);
            return result;
        }

        public void assign(Part part) {
            assign(part.ranges.x, part.getRanges().m, part.ranges.a, part.ranges.s);
        }

        public void assign(Range x, Range m, Range a, Range s) {
            this.ranges.x.subRanges.clear();
            this.ranges.x.subRanges.addAll(x.subRanges);
            this.ranges.m.subRanges.clear();
            this.ranges.m.subRanges.addAll(m.subRanges);
            this.ranges.a.subRanges.clear();
            this.ranges.a.subRanges.addAll(a.subRanges);
            this.ranges.s.subRanges.clear();
            this.ranges.s.subRanges.addAll(s.subRanges);
        }

        public void clear() {
            this.ranges.x.subRanges.clear();
            this.ranges.m.subRanges.clear();
            this.ranges.a.subRanges.clear();
            this.ranges.s.subRanges.clear();
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Range4D {
        final Range x;
        final Range m;
        final Range a;
        final Range s;

        public static Range4D full() {
            return new Range4D(Range.full(), Range.full(), Range.full(), Range.full());
        }

        public static Range4D empty() {
            return new Range4D(Range.empty(), Range.empty(), Range.empty(), Range.empty());
        }

        public Range4D union(Range4D other) {
            return new Range4D(
                    x.union(other.x),
                    m.union(other.m),
                    a.union(other.a),
                    s.union(other.s));
        }

        public BigInteger countValues() {
            return x.countValues()
                    .multiply(m.countValues())
                    .multiply(a.countValues())
                    .multiply(s.countValues());
        }
    }


    @Data
    @RequiredArgsConstructor
    static class SubRange implements Comparable<SubRange> {
        final int from;
        final int to;

        public int countValues() {
            // inclusive
            return (to - from) + 1;
        }

        public SubRange matches(Operator op, int value) {
            switch (op) {
                case LESS_THAN:
                    // case 1: the range is entirely above the value - result is nothing
                    if (from >= value) {
                        return null;
                    }
                    // case 2: the range is entirely below the value - result is the complete range
                    if (to < value) {
                        return this;
                    }
                    // case 3: the range is split by the value
                    return new SubRange(from, value - 1);
                case GREATER_THAN:
                    // case 1: the range is entirely below the value - result is nothing
                    if (to <= value) {
                        return null;
                    }
                    // case 2: the range is entirely above the value - result is the complete range
                    if (from > value) {
                        return this;
                    }
                    // case 3: the range is split by the value
                    return new SubRange(value + 1, to);
                default:
                    throw new RuntimeException("Unknown operator");
            }
        }

        public SubRange mismatches(Operator op, int value) {
            switch (op) {
                case LESS_THAN:
                    // case 1: the range is entirely above the value - result is everything
                    if (from >= value) {
                        return this;
                    }
                    // case 2: the range is entirely below the value - result is nothing
                    if (to < value) {
                        return null;
                    }
                    // case 3: the range is split by the value
                    return new SubRange(value, to);
                case GREATER_THAN:
                    // case 1: the range is entirely below the value - result is everything as all mismatched
                    if (to <= value) {
                        return this;
                    }
                    // case 2: the range is entirely above the value - result is nothing (everything matched, nothing doesn't)
                    if (from > value) {
                        return null;
                    }
                    // case 3: the range is split by the value
                    return new SubRange(from, value);
                default:
                    throw new RuntimeException("Unknown operator");
            }
        }

        public List<SubRange> union(SubRange other) {
            // case 1: disjoint - return both
            if (!intersects(other)) {
                return Arrays.asList(this, other);
            }

            // case 2: some kind of overlap - expand to cover both
            return Collections.singletonList(new SubRange(Math.min(this.from, other.from), Math.max(this.to, other.to)));
        }

        @Override
        public int compareTo(SubRange o) {
            int compareFrom = Integer.compare(from, o.from);
            return compareFrom == 0 ? Integer.compare(to, o.to) : compareFrom;
        }

        public boolean intersects(SubRange other) {
            return !((this.from > other.to) || (this.to < other.from));
        }

        @Override
        public String toString() {
            return "(" +
                    from +
                    ".." + to +
                    ')';
        }
    }

    @Data
    @RequiredArgsConstructor
    static class Range {
        static final int MIN_RANGE = 1;
        static final int MAX_RANGE = 4000;
        final Set<SubRange> subRanges;

        public static Range full() {
            return new Range(new TreeSet<>(Collections.singleton(new SubRange(MIN_RANGE, MAX_RANGE))));
        }

        public static Range empty() {
            return new Range(new TreeSet<>());
        }

        public BigInteger countValues() {
            return subRanges.stream().map(SubRange::countValues).map(BigInteger::valueOf).reduce(BigInteger.ZERO, BigInteger::add);
        }

        public Range matches(Operator op, int value) {
            return new Range(subRanges.stream()
                    .map(sr -> sr.matches(op, value))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
        }

        public Range mismatches(Operator op, int value) {
            return new Range(subRanges.stream()
                    .map(sr -> sr.mismatches(op, value))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
        }

        public Range union(Range other) {
            TreeSet<SubRange> allRanges = new TreeSet<>(this.subRanges);
            allRanges.addAll(other.subRanges);
            TreeSet<SubRange> result = new TreeSet<>();
            while (!allRanges.isEmpty()) {
                SubRange first = allRanges.first();
                allRanges.remove(first);
                if (result.isEmpty())
                    result.add(first);
                else {
                    SubRange last = result.last();
                    result.remove(last);
                    result.addAll(last.union(first));
                }
            }
            return new Range(result);
        }

        @Override
        public String toString() {
            return subRanges.toString();
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

            Part matches = new Part(Range4D.empty());
            Part mismatches = new Part(Range4D.empty());

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

            //mismatches.assign(input);
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
            Range xMatches = input.ranges.x;
            Range xMismatches = input.ranges.x;
            Range mMatches = input.ranges.m;
            Range mMismatches = input.ranges.m;
            Range aMatches = input.ranges.a;
            Range aMismatches = input.ranges.a;
            Range sMatches = input.ranges.s;
            Range sMismatches = input.ranges.s;
            switch (variable) {
                case 'x':
                    xMatches = input.ranges.x.matches(op, value);
                    xMismatches = input.ranges.x.mismatches(op, value);
                    break;
                case 'm':
                    mMatches = input.ranges.m.matches(op, value);
                    mMismatches = input.ranges.m.mismatches(op, value);
                    break;
                case 'a':
                    aMatches = input.ranges.a.matches(op, value);
                    aMismatches = input.ranges.a.mismatches(op, value);
                    break;
                case 's':
                    sMatches = input.ranges.s.matches(op, value);
                    sMismatches = input.ranges.s.mismatches(op, value);
                    break;
                default:
                    throw new RuntimeException("Unknown variable " + variable);
            }
            matches.assign(xMatches, mMatches, aMatches, sMatches);
            mismatches.assign(xMismatches, mMismatches, aMismatches, sMismatches);
            //System.out.printf(" - matches = %s%n", matches);
            //System.out.printf(" - mismatches = %s%n", mismatches);
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

        @Override
        public String toString() {
            return "" + ch;
        }
    }


    private void rangeTests() {
        SubRange sr1 = new SubRange(1, 5);
        SubRange sr2 = new SubRange(4, 9);

        System.out.println(sr1.intersects(sr2));
        System.out.println(sr2.intersects(sr1));

        System.out.println(sr1.union(sr2));
        System.out.println(sr2.union(sr1));
        System.out.println("-----------");

        Range r1 = new Range(new HashSet<>(Arrays.asList(new SubRange(1, 3), new SubRange(7, 10))));
        Range r2 = new Range(new HashSet<>(Arrays.asList(new SubRange(5, 7), new SubRange(12, 15))));

        System.out.println(r1.union(r2));
        System.out.println(r2.union(r1));
    }
}
