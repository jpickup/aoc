package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {
    static int part = 2;
    static int divisorProduct;
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day11.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());
            Map<Integer, Monkey> monkeys = new TreeMap<>();
            for (int m=0; m <= lines.size()/7; m++) {
                Monkey monkey = Monkey.parse(lines.subList(m * 7, m * 7 + 6));
                monkeys.put(monkey.id, monkey);
            }
            divisorProduct = monkeys.values().stream().map(m -> m.test.divisor).collect(Collectors.toSet()).stream().reduce((a, b) -> a * b).get();

            for (int round=1; round<=(part==1?20:10000); round++) {
                for (Monkey monkey : monkeys.values()) {
                    monkey.execute(monkeys);
                }
                System.out.println(" --- ROUND " + round + " ---");
                for (Monkey monkey : monkeys.values()) {
                    System.out.println("Monkey " +  monkey.id + ": " + monkey.items);
                }
            }

            List<Monkey> sortedMonkeys = monkeys.values().stream().sorted(Comparator.comparing(a -> a.inspections)).collect(Collectors.toList());
            System.out.println(sortedMonkeys);

            BigInteger result = sortedMonkeys.get(sortedMonkeys.size()-1).inspections.multiply(sortedMonkeys.get(sortedMonkeys.size()-2).inspections);
            System.out.println("RESULT = " + result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Getter
    @ToString
    @Builder
    static class Monkey {
        private static final BigInteger THREE = new BigInteger("3");
        final int id;
        final List<BigInteger> items;
        final MonkeyOperation operation;
        final MonkeyTest test;
        final int trueTarget;
        final int falseTarget;
        BigInteger inspections;

        static Monkey parse(List<String> defn) {
            return Monkey.builder()
                    .id(defn.get(0).charAt(7)-'0')
                    .items(Arrays.stream(defn.get(1).substring(18).split(", ")).map(BigInteger::new).collect(Collectors.toList()))
                    .operation(MonkeyOperation.parse(defn.get(2).substring(13)))
                    .test(MonkeyTest.parse(defn.get(3).substring(7)))
                    .trueTarget(defn.get(4).charAt(29)-'0')
                    .falseTarget(defn.get(5).charAt(30)-'0')
                    .inspections(BigInteger.ZERO)
                    .build();
        }

        public void execute(Map<Integer, Monkey> monkeys) {
            for (BigInteger item : items) {
                inspections = inspections.add(BigInteger.ONE);
                BigInteger newValue = operation.evaluate(item);
                if (part == 1) {
                    newValue = newValue.divide(THREE);
                } else {
                    newValue = newValue.mod(BigInteger.valueOf(divisorProduct));
                }
                boolean testResult = test.evaluate(newValue);
                if (testResult) {
                    monkeys.get(trueTarget).items.add(newValue);
                } else {
                    monkeys.get(falseTarget).items.add(newValue);
                }
            }
            // all thrown
            items.clear();
        }
    }

    @Builder
    @ToString
    static class MonkeyOperation {
        final char operation;
        final String left;
        final String right;

        public static MonkeyOperation parse(String s) {
            String[] parts = s.split(" ");
            return MonkeyOperation.builder()
                    .operation(parts[3].charAt(0))
                    .left(parts[2])
                    .right(parts[4])
                    .build();
        }

        public BigInteger evaluate(BigInteger item) {
            BigInteger leftValue = left.equals("old")?item:new BigInteger(left);
            BigInteger rightValue = right.equals("old")?item:new BigInteger(right);
            switch (operation) {
                case '+': return leftValue.add(rightValue);
                case '*': return leftValue.multiply(rightValue);
                default:
                    throw new RuntimeException("Unknown operation " + operation);
            }
        }
    }

    @Builder
    @ToString
    static class MonkeyTest {
        final int divisor;

        public static MonkeyTest parse(String s) {
            String[] parts = s.split(" ");
            return MonkeyTest.builder()
                    .divisor(Integer.parseInt(parts[3]))
                    .build();
        }

        public boolean evaluate(BigInteger item) {
            return item.mod(BigInteger.valueOf(divisor)).equals(BigInteger.ZERO);
        }
    }
}
