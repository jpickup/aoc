package com.johnpickup.aoc2021;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10Syntax {
    static Map<Character, Character> pairs = new HashMap<>();
    static {
        pairs.put('(',')');
        pairs.put('[',']');
        pairs.put('{','}');
        pairs.put('<','>');
    }
    static Map<Character, Integer> scores = new HashMap<>();
    static {
        scores.put(')', 3);
        scores.put(']', 57);
        scores.put('}', 1197);
        scores.put('>', 25137);
    }

    public static void main(String[] args) throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day10Input.txt"))) {
            List<String> lines = stream.filter(Objects::nonNull).collect(Collectors.toList());
            System.out.println("Input size " + lines.size());

            BigInteger totalScore = BigInteger.ZERO;
            List<BigInteger> scores = new ArrayList<>();
            for (String line : lines) {
                BigInteger score = parseLine2(line);
                System.out.println("Score " + score);
                //totalScore += score;
                if (score != BigInteger.ZERO) {
                    scores.add(score);
                }
            }
            System.out.println("Total Score " + totalScore);
            scores.sort(BigInteger::compareTo);
            BigInteger middleScore = scores.get(scores.size()/2);
            System.out.println("Middle Score " + middleScore);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int parseLine(String line) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (pairs.keySet().contains(ch)) {
                stack.push(pairs.get(ch));
            }
            else {
                char expected = stack.pop();
                if (expected != ch) {
                    System.out.printf("Expected %c but got %c\n", expected, ch);
                    return scores.get(ch);
                }
            }
        }
        return 0;
    }

    private static BigInteger parseLine2(String line) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (pairs.keySet().contains(ch)) {
                stack.push(pairs.get(ch));
            }
            else {
                char expected = stack.pop();
                if (expected != ch) {
                    System.out.printf("Expected %c but got %c - ignoring line\n", expected, ch);
                    return BigInteger.ZERO;
                }
            }
        }
        BigInteger lineScore = BigInteger.ZERO;
        while (!stack.empty()) {
            lineScore = lineScore.multiply(new BigInteger("5"));
            switch (stack.pop()) {
                case ')':
                    lineScore = lineScore.add(BigInteger.ONE);
                    break;
                case ']':
                    lineScore = lineScore.add(new BigInteger("2"));
                    break;
                case '}':
                    lineScore = lineScore.add(new BigInteger("3"));
                    break;
                case '>':
                    lineScore = lineScore.add(new BigInteger("4"));
                    break;
            }
        }
        return lineScore;
    }
}
