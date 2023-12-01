package com.johnpickup.aoc2022;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

// Part 1
// Fail:
// Total: 996631958
// Result: -02012-211=2=

public class Day25v2 {
    private static final BigInteger THREE = new BigInteger("3");
    private static final BigInteger FIVE = new BigInteger("5");

    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day25-test.txt"))) {
            long start = System.currentTimeMillis();

            String result = stream.filter(s -> !s.isEmpty()).reduce("0", Snafu::add);
            System.out.println("Result: " + result);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static Map<Character, Integer> digitValues = new HashMap<>();
    static {
        digitValues.put('0', 0);
        digitValues.put('1', 1);
        digitValues.put('2', 2);
        digitValues.put('-', -1);
        digitValues.put('=', -2);
    }

    static Map<Integer, Character> digits = new HashMap<>();
    static {
        digits.put(0, '0');
        digits.put(1, '1');
        digits.put(2, '2');
        digits.put(-1, '-');
        digits.put(-2, '=');
    }

    private static class Snafu {
        public static String add(String s1, String s2) {
            String result = "";
            int carry = 0;
            while (s1.length() > 0 || s2.length() > 0) {
                char c1 = s1.isEmpty()?'0':s1.charAt(s1.length()-1);
                char c2 = s2.isEmpty()?'0':s2.charAt(s2.length()-1);
                int d = digitValues.get(c1) + digitValues.get(c2) + carry;
                carry = 0;
                if (d < -2) {
                    carry = -1;
                    d += 5;
                }
                else if (d < 0) {
                    carry = -1;
                }
                else if (d > 2) {
                    carry = 1;
                    d -= 5;
                }
                result = digits.get(d) + result;

                s1 = s1.isEmpty()?"":s1.substring(0, s1.length()-1);
                s2 = s2.isEmpty()?"":s2.substring(0, s2.length()-1);
            }

            return result;
        }
    }
}
