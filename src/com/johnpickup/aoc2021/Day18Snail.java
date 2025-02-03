package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18Snail {
    List<String> lines;

    boolean hasExploded = false;
    static boolean hasSplit = false;
    boolean hasReduced = false;
    static boolean debug = false;

    public static void main(String[] args) {
        Day18Snail day18 = new Day18Snail("/Volumes/Users/john/Development/AdventOfCode/resources/Day18Input.txt");

        //day18.test();

        //day18.solvePart1();
        day18.solvePart2();
    }

    private void test() {
        SnailNumber s = parseString("[[[[4,0],[5,0]],[[[4,5],[2,6]],[9,5]]],[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]]");
        System.out.println(s);
        System.out.println(explode(s));
    }

    private void solvePart1() {
        List<SnailNumber> snailNumbers =  parse(lines);
        //snailNumbers.stream().forEach(s -> System.out.println(s.toString() + " = Mag " +s.magnitude()));

        SnailNumber result = snailNumbers.get(0);

        for (int i = 1; i < snailNumbers.size(); i++) {
            result = new SnailPair(result, snailNumbers.get(i));
            System.out.println("Before Reduce" + result);
            result = reduce(result);
            System.out.println("After Reduce" + result);
        }

        System.out.println("Magnitude: " + result.magnitude());

    }

    private void solvePart2() {
        int max = 0;
        SnailNumber bestLeft = null;
        SnailNumber bestRight = null;
        SnailNumber bestSum = null;
        List<SnailNumber> snailNumbers =  parse(lines);

        System.out.println("Size: " + (snailNumbers.size()));
        System.out.println("Iterations: " + (snailNumbers.size() * snailNumbers.size()));

        for (int i = 0; i < snailNumbers.size() - 1; i++) {
            System.out.print("*");
            for (int j = 0; j < snailNumbers.size() - 1; j++) {
                System.out.print(".");
                SnailNumber sn1 = snailNumbers.get(i);
                SnailNumber sn2 = snailNumbers.get(j);

                SnailNumber sum1 = reduce(new SnailPair(sn1.clone(), sn2.clone()));

                if (sum1.magnitude() > max) {
                    max = sum1.magnitude();
                    bestLeft = sn1;
                    bestRight = sn2;
                    bestSum = sum1;
                }

                SnailNumber sum2 = reduce(new SnailPair(sn2.clone(), sn1.clone()));
                if (sum2.magnitude() > max) {
                    max = sum2.magnitude();
                    bestLeft = sn2;
                    bestRight = sn1;
                    bestSum = sum2;
                }
            }
        }
        System.out.println();
        System.out.println("Max magnitude: " + max);
        System.out.println("From : " + bestSum);
        System.out.println("Left: " + bestLeft);
        System.out.println("Right: " + bestRight);



    }


    private SnailNumber reduce(SnailNumber snailNumber) {
        hasReduced = true;
        while (hasReduced) {
            hasReduced = false;
            snailNumber = repeatExplode(snailNumber);
            snailNumber = split(snailNumber);
            hasReduced = hasExploded | hasSplit;
        }
        return snailNumber;
    }

    private SnailNumber repeatExplode(SnailNumber snailNumber) {
        hasExploded = true;
        while (hasExploded) {
            hasExploded = false;
            snailNumber = explode(snailNumber);
        }
        return snailNumber;
    }

    private SnailNumber explode(SnailNumber snailNumber) {
        if (snailNumber instanceof SnailValue) return snailNumber;
        // walk the number looking for the first pair at a depth of 4
        SnailPair snailPair = findAtDepth((SnailPair)snailNumber, 4);

        if (snailPair != null) {
            hasExploded = true;
            SnailPair parentWhereRight = goUpUntilImTheRight(snailPair);
            if (parentWhereRight != null) {
                SnailValue left = (SnailValue) findRight(parentWhereRight.left);
                if (left != null) left.value += ((SnailValue) snailPair.left).value;

            }

            SnailPair parentWhereLeft = goUpUntilImTheLeft(snailPair);
            if (parentWhereLeft != null) {
                SnailValue right = (SnailValue) findLeft(parentWhereLeft.right);
                if (right != null) right.value += ((SnailValue) snailPair.right).value;
            }
            snailPair.parent.replace(snailPair, new SnailValue(0));
        }
        if (debug) System.out.println("EXPLO: "+ snailNumber);
        return snailNumber;
    }

    private SnailNumber split(SnailNumber snailNumber) {
        hasSplit = false;
        SnailNumber split = snailNumber.split();
        if (debug) System.out.println("SPLIT: "+ split);
        return split;
    }

    private SnailPair goUpUntilImTheLeft(SnailPair snailPair) {
        if (snailPair.parent==null) return null;
        if (snailPair.parent.left == snailPair) return snailPair.parent;
        return goUpUntilImTheLeft(snailPair.parent);
    }

    private SnailPair goUpUntilImTheRight(SnailPair snailPair) {
        if (snailPair.parent==null) return null;
        if (snailPair.parent.right == snailPair) return snailPair.parent;
        return goUpUntilImTheRight(snailPair.parent);
    }

    private SnailNumber findLeft(SnailNumber snailNumber) {
        if (snailNumber == null) return null;

        if (snailNumber instanceof SnailValue) return snailNumber;

        return findLeft(((SnailPair)snailNumber).left);
    }

    private SnailNumber findRight(SnailNumber snailNumber) {
        if (snailNumber == null) return null;

        if (snailNumber instanceof SnailValue) return snailNumber;

        return findRight(((SnailPair)snailNumber).right);
    }


    private SnailPair findAtDepth(SnailPair snailPair, int i) {
        if (i==0) return snailPair;

        if (snailPair.left instanceof SnailPair) {
            SnailPair found = findAtDepth((SnailPair)snailPair.left, i-1);
            if (found != null) return found;
        }

        if (snailPair.right instanceof SnailPair) {
            SnailPair found = findAtDepth((SnailPair)snailPair.right, i-1);
            if (found != null) return found;
        }

        return null;
    }

    private List<SnailNumber> parse(List<String> lines) {
        return lines.stream().map(this::parseString).collect(Collectors.toList());
    }

    private SnailNumber parseString(String s) {
        char ch = s.charAt(0);
        if (ch >='0' && ch <= '9')
            return new SnailValue(ch - '0');
        else {
            if (ch == '[') {
                // find the comma at this depth and split on that
                String left = null;
                String right = null;
                int depth = 0;
                for (int i = 0; i < s.length(); i++) {
                    switch (s.charAt(i)) {
                        case '[' :
                            depth++; break;
                        case ']' :
                            depth--; break;
                        case ',' :
                            if (depth == 1) {
                                left = s.substring(1, i);
                                right = s.substring(i+1);
                                right = right.substring(0, right.length()-1);
                            }
                            break;
                        }
                    }
                return new SnailPair(parseString(left), parseString(right));
            }
        }
        throw new RuntimeException("Parse error" + s);
    }

    Day18Snail(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    interface SnailNumber {
        int magnitude();
        SnailNumber split();
        SnailNumber explode();
        SnailNumber clone();
    }

    static class SnailPair implements SnailNumber {
        SnailPair parent;
        SnailNumber left;
        SnailNumber right;

        public SnailPair(SnailNumber left, SnailNumber right) {
            this.left = left;
            if (left instanceof SnailPair) ((SnailPair) left).parent = this;
            this.right = right;
            if (right instanceof SnailPair) ((SnailPair) right).parent = this;
        }

        public int depth() {
            if (parent==null)
                return 0;
            else
                return parent.depth()+1;
        }

        @Override
        public int magnitude() {
            return left.magnitude()*3 + right.magnitude()*2;
        }

        @Override
        public SnailNumber split() {
            SnailNumber leftSplit = left.split();

            SnailNumber rightSplit = leftSplit.equals(left)?right.split():right;

            SnailPair result = new SnailPair(leftSplit, rightSplit);
            result.parent = this.parent;
            return result;
        }

        @Override
        public SnailNumber explode() {
            return null;
        }

        @Override
        public SnailNumber clone() {
            return new SnailPair(left.clone(), right.clone());
        }

        @Override
        public String toString() {
            return "[" + left +
                    "," + right +
                    ']';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SnailPair snailPair = (SnailPair) o;
            return left.equals(snailPair.left) &&
                    right.equals(snailPair.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }

        public void replace(SnailPair snailPair, SnailValue newValue) {
            if (left == snailPair) left = newValue;
            if (right == snailPair) right = newValue;
        }
    }

    static class SnailValue implements SnailNumber {
        int value;

        public SnailValue(int value) {
            this.value = value;
        }

        @Override
        public int magnitude() {
            return value;
        }

        @Override
        public SnailNumber split() {
            if (value<=9) return this;
            hasSplit = true;
            return new SnailPair(new SnailValue(value / 2), new SnailValue(value / 2 + value % 2));
        }

        @Override
        public SnailNumber explode() {
            return this;
        }

        @Override
        public SnailNumber clone() {
            return new SnailValue(value);
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SnailValue that = (SnailValue) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
