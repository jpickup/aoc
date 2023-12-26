package com.johnpickup.aoc2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 {
    public static void main(String[] args) throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day3.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            long totalScore=0;

            String[] group = new String[3];
            int index = 0;

            for (String line : lines) {
// Part2
                group[index%3] = line;
                if (index%3 == 2) {
                    char common = findCommon3(group);
                    long score = calcScore(common);
                    totalScore += score;
                }
// Part1
//                String left = line.substring(0, line.length()/2);
//                String right = line.substring(line.length()/2);
//                System.out.println("left " + left + ", right " + right);
//                char common = findCommon(left,right);
//                long score = calcScore(common);
//                totalScore += score;

                index++;
            }
            System.out.println(totalScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static char findCommon3(String[] group) {
        String common = findCommons(group[0], group[1]);
        return findCommons(common, group[2]).charAt(0);
    }

    private static String findCommons(String left, String right) {
        Set<Character> result = new HashSet<>();
        for (int i=0; i<left.length(); i++) {
            if (right.contains(left.charAt(i)+"")) {
                result.add(left.charAt(i));
            }
        }
        return result.stream().map(c -> ""+c).collect(Collectors.joining());
    }

    private static char findCommon(String left, String right) {
        for (int i=0; i<left.length(); i++) {
            if (right.contains(left.charAt(i)+"")) {
                return left.charAt(i);
            }
        }
        return 0;
    }

    private static long calcScore(char c) {
        if (c>='a' && c<='z') {
            return c-'a'+1;
        }
        if (c>='A' && c<='Z') {
            return c-'A'+27;
        }
        return 0;
    }

}
