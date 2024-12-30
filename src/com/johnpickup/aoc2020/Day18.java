package com.johnpickup.aoc2020;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day18 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Expression> expressions = stream
                        .filter(s -> !s.isEmpty())
                        .map(Expression::new)
                        .collect(Collectors.toList());

                System.out.println(expressions);

                long part1 = expressions.stream().map(Expression::evaluate).reduce(0L, Long::sum);
                System.out.println("Part 1: " + part1);
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }


    @ToString
    static class Expression {
        final String exp;

        final List<Expression> subExpressions = new ArrayList<>();
        final List<Operator> operators = new ArrayList<>();

        Expression(String s) {
            exp = s;
            parse(s);
        }

        private void parse(String s) {
            while (!s.isEmpty()) {
                char ch = s.charAt(0);
                if (ch >= '0' && ch <= '9') {
                    subExpressions.add(new LiteralExpression(ch - '0'));
                }

                if (ch == '+') {
                    operators.add(Operator.PLUS);
                }
                if (ch == '*') {
                    operators.add(Operator.TIMES);
                }
                if (ch == '(') {
                    String nestedExpression = findSubExpressionByMatchingBracket(s, 0);
                    subExpressions.add(new Expression(nestedExpression));
                    s = s.substring(nestedExpression.length());
                }
                s = s.substring(1);
            }
        }

        private String findSubExpressionByMatchingBracket(String s, int i) {
            int level = 0;
            String result = "";
            do {
                char c = s.charAt(i);
                if (c == ')') level--;
                if (level != 0) result += c;
                if (c == '(') level++;
                i++;
            } while (level > 0);
            return result;
        }

        long evaluate() {
//            System.out.println("evaluating " + this.exp);
            long result = subExpressions.get(0).evaluate();
            if (subExpressions.size() != operators.size() + 1)
                throw new RuntimeException("Mismatched sub-expressions and operators: " + exp + " : " + subExpressions.size() + "-1 != " + operators.size());
            for (int i = 0; i < operators.size(); i++) {
                result = operators.get(i).apply(result, subExpressions.get(i+1).evaluate());
            }
//            System.out.println(this.exp + " = " + result);
            return result;
        }
    }

    static class LiteralExpression extends Expression {
        final int value;
        LiteralExpression(int value) {
            super("");
            this.value = value;
        }
        @Override
        long evaluate() {
            return value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    enum Operator {
        PLUS,
        TIMES;
        public long apply(long left, long right) {
            switch (this) {
                case PLUS: return left + right;
                case TIMES : return left * right;
                default: throw new RuntimeException("Unknown operator " + this);
            }
        }
    }
}
