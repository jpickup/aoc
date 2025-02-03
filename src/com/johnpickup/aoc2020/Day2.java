package com.johnpickup.aoc2020;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2020/Day2/Day2.txt"))) {
            List<Password> passwords = stream.filter(s -> !s.isEmpty()).map(Password::new).collect(Collectors.toList());
            System.out.println("Part 1: " + passwords.stream().filter(Password::isValid).count());
            System.out.println("Part 2: " + passwords.stream().filter(Password::isValidPart2).count());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @ToString
    static class Password {
        final int min;
        final int max;
        final char ch;
        final String password;
        Password(String line) {
            String[] parts = line.split(" ");
            String[] minMax = parts[0].split("-");
            min = Integer.parseInt(minMax[0]);
            max = Integer.parseInt(minMax[1]);
            ch = parts[1].charAt(0);
            password = parts[2].trim();
        }

        boolean isValid() {
            int count = 0;
            for (int i = 0; i < password.length(); i++) {
                if (password.charAt(i) == ch) count++;
            }
            return count >= min && count <= max;
        }

        boolean isValidPart2() {
            return password.length() >= max
                    && (password.charAt(min-1) == ch || password.charAt(max-1) == ch)
                    && (password.charAt(min-1) != password.charAt(max-1));
        }
    }
}
