package com.johnpickup.aoc2019;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day16 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
                , prefix + "-test3.txt"
                , prefix + "-test4.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Fft fft = new Fft(lines.get(0));
                System.out.println(fft);

                String part1 = fft.part1(100);
                System.out.println("Part 1: " + part1);
                long part2 = fft.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @Data
    static class Fft {
        final List<Integer> initialDigits = new ArrayList<>();
        Fft(String line) {
            for (int i = 0 ; i < line.length(); i++) {
                initialDigits.add((int)line.charAt(i) - '0');
            }
        }

        static final List<Integer> basePattern = Arrays.asList(0, 1, 0, -1);
        public String part1(int phases) {
            List<Integer> digits = new ArrayList<>(initialDigits);
            for (int phase = 0; phase < phases; phase ++) {
                digits = applyPhase(digits);
                //System.out.println(phase + " : " + digits);
            }
            return initialEightDigits(digits);
        }

        public long part2() {
            return 0;
        }

        String initialEightDigits(List<Integer> digits) {
            return digits.stream().limit(8).map(d -> "" + (char)('0' + d)).collect(Collectors.joining(""));
        }

        List<Integer> applyPhase(List<Integer> digits) {
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < digits.size(); i++) {
                result.add(computeDigit(digits, basePattern, i));
            }
            return result;
        }

        int computeDigit(List<Integer> digits, List<Integer> pattern, int idx) {
            int result = 0;
            for (int i = 0; i < digits.size(); i++) {
                int patternDigit = getPatternDigit(idx, i, pattern);
                //System.out.printf("%d * %d  + ", digits.get(i), patternDigit);
                result += digits.get(i) * patternDigit;
            }
            result = Math.abs(result) % 10;
            //System.out.printf("= %d%n", result);
            return result;
        }

        int getPatternDigit(int outputIdx, int inputIdx, List<Integer> pattern) {
            int patternIdx = (inputIdx+1) / (outputIdx+1) ;
            return pattern.get(patternIdx % pattern.size());
        }
    }
}
