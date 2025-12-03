package com.johnpickup.aoc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day3 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();
                long part1 = 0L;
                long part2 = 0L;
                for (String line : lines) {
                    part1 += maxJoltage(line, 2);
                    part2 += maxJoltage(line, 12);
                }
                System.out.println("Part 1: " + part1);
                System.out.println("Part 2: " + part2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static long maxJoltage(String line, int bankSize) {
        String result = "";
        int index = 0;
        for (int i = 0; i < bankSize; i++) {
            int bestIndex = -1;
            for (char ch = '9'; ch >= '0'; ch--) {
                int firstSubIndex = line.substring(index).indexOf(ch);
                if (firstSubIndex == -1) continue;
                int firstIndex = firstSubIndex + index;
                if (firstIndex >= 0 && firstIndex <= line.length() - (bankSize - i)) {
                    bestIndex = firstIndex;
                    break;
                }
            }
            result += "" + line.charAt(bestIndex);
            index = bestIndex+1;
        }
        return Long.parseLong(result);
    }
}