package com.johnpickup.aoc2022;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Day25 {
    private static final BigInteger THREE = new BigInteger("3");
    private static final BigInteger FIVE = new BigInteger("5");
    private static final BigInteger MINUS_ONE = new BigInteger("-1");
    private static final BigInteger MINUS_TWO = new BigInteger("-2");

    public static void main(String[] args) {
        runTests();

        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day25.txt"))) {
            long start = System.currentTimeMillis();

            BigInteger total = stream.filter(s -> !s.isEmpty())
                    //.map(Day25::test)
                    .map(Day25::parseSnafu)
                    .reduce(BigInteger.ZERO, BigInteger::add);
            System.out.println("Total: " + total);

            String result = generateSnafu(total);
            System.out.println("Result: " + result);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String test(String s) {
        BigInteger parsed = parseSnafu(s);
        String generated = generateSnafu(parsed);
        if (!generated.equals(s)) throw new RuntimeException("Expected " + s + " but got " + generated + " when generating " + parsed);
        return s;
    }

    private static void runTests() {
        runTest(new BigInteger("1"), "1");
        runTest(new BigInteger("2"), "2");
        runTest(new BigInteger("3"), "1=");
        runTest(new BigInteger("4"), "1-");
        runTest(new BigInteger("5"), "10");
        runTest(new BigInteger("6"), "11");
        runTest(new BigInteger("7"), "12");
        runTest(new BigInteger("8"), "2=");
        runTest(new BigInteger("9"), "2-");
        runTest(new BigInteger("10"), "20");
        runTest(new BigInteger("15"), "1=0");
        runTest(new BigInteger("20"), "1-0");
        runTest(new BigInteger("2022"), "1=11-2");
        runTest(new BigInteger("12345"), "1-0---0");
        runTest(new BigInteger("314159265"), "1121-1110-1=0");
    }

    private static void runTest(BigInteger i, String s) {
        BigInteger parsed = parseSnafu(s);
        String generated = generateSnafu(i);
        if (!parsed.equals(i)) throw new RuntimeException("Expected " + i + " but got " + parsed + " when parsing " + s);
        if (!generated.equals(s)) throw new RuntimeException("Expected " + s + " but got " + generated + " when generating " + i);
    }

    static Map<Integer, Character> digits = new HashMap<>();
    static {
        digits.put(0, '0');
        digits.put(1, '1');
        digits.put(2, '2');
        digits.put(-1, '-');
        digits.put(-2, '=');
    }

    private static String generateSnafu(BigInteger n) {
        String result = "";
        BigInteger borrow = BigInteger.ZERO;
        while (n.compareTo(BigInteger.ZERO) > 0) {
            BigInteger m = (n.mod(FIVE).add(borrow));
            if (m.compareTo(THREE) >= 0) {
                borrow = BigInteger.ONE;
                m = m.subtract(FIVE);
            }
            else if (m.compareTo(MINUS_TWO) < 0)
                borrow = MINUS_ONE;
            else {
                borrow = BigInteger.ZERO;
            }
            result = digits.get(m.intValue()) + result;
            n = n.divide(FIVE);
        }
        if (!borrow.equals(BigInteger.ZERO)) {
            result = digits.get(borrow.intValue()) + result;
        }
        return result;
    }

    static Map<Character, Integer> digitValues = new HashMap<>();
    static {
        digitValues.put('0', 0);
        digitValues.put('1', 1);
        digitValues.put('2', 2);
        digitValues.put('-', -1);
        digitValues.put('=', -2);
    }

    private static BigInteger parseSnafu(String s) {
        BigInteger result = BigInteger.ZERO;
        for (int place = 0; place < s.length(); place++) {
            char digit = s.charAt(s.length() - 1 - place);
            int digitValue = digitValues.get(digit);
            result = result.add(BigInteger.valueOf(digitValue).multiply(BigDecimal.valueOf(Math.pow(5, place)).toBigInteger()));
        }
        return result;
    }

}
