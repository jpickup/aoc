package com.johnpickup.aoc2020;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day4.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());

            List<String> linesBlock = new ArrayList<>();
            List<Passport> passports = new ArrayList<>();
            for (String line : lines) {
                if (line.isEmpty()) {
                    passports.add(new Passport(linesBlock));
                    linesBlock.clear();
                } else {
                    linesBlock.add(line);
                }
            }
            if (!linesBlock.isEmpty()) passports.add(new Passport(linesBlock));

            List<String> requiredKeys = Arrays.asList("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid");
            System.out.println("Part 1: " + Passport.part1(passports, requiredKeys));
            System.out.println("Part 2: " + Passport.part2(passports, requiredKeys));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @ToString
    static class Passport {
        final Map<String, String> fields;

        Passport(List<String> lines) {
            fields = new HashMap<>();
            for (String line : lines) {
                fields.putAll(parseFields(line));
            }
        }

        static long part1(List<Passport> passports, List<String> requiredKeys) {
            return passports.stream().filter(p -> p.isValid(requiredKeys)).count();
        }

        boolean isValid(List<String> requiredKeys) {
            return requiredKeys.stream().allMatch(fields::containsKey);
        }

        /* rules for part 2
    byr (Birth Year) - four digits; at least 1920 and at most 2002.
    iyr (Issue Year) - four digits; at least 2010 and at most 2020.
    eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
    hgt (Height) - a number followed by either cm or in:
        If cm, the number must be at least 150 and at most 193.
        If in, the number must be at least 59 and at most 76.
    hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
    ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
    pid (Passport ID) - a nine-digit number, including leading zeroes.
    cid (Country ID) - ignored, missing or not.
         */
        static long part2(List<Passport> passports, List<String> requiredKeys) {
            return passports.stream().filter(p -> p.isValidPart2(requiredKeys)).count();
        }

        Pattern hclPattern = Pattern.compile("#[0-9,a-f]{6}");
        Pattern eclPattern = Pattern.compile("amb|blu|brn|gry|grn|hzl|oth");
        Pattern pidPattern = Pattern.compile("[0-9]{9}");
        boolean isValidPart2(List<String> requiredKeys) {
            boolean result = requiredKeys.stream().allMatch(fields::containsKey);
            if (result) {
                for (Map.Entry<String, String> fieldEntry : fields.entrySet()) {
                    String v = fieldEntry.getValue();
                    switch (fieldEntry.getKey()) {
                        case "byr" : result = result && Integer.parseInt(v) >= 1920 &&  Integer.parseInt(v) <= 2002; break;
                        case "iyr" : result = result && Integer.parseInt(v) >= 2010 &&  Integer.parseInt(v) <= 2020; break;
                        case "eyr" : result = result && Integer.parseInt(v) >= 2020 &&  Integer.parseInt(v) <= 2030; break;
                        case "hgt" :
                            if (v.endsWith("cm")) {
                                v = v.substring(0, v.length() - 2);
                                result = result && Integer.parseInt(v) >= 150 &&  Integer.parseInt(v) <= 193;
                            } else if (v.endsWith("in")) {
                                v = v.substring(0, v.length() - 2);
                                result = result && Integer.parseInt(v) >= 59 &&  Integer.parseInt(v) <= 76;
                            } else {
                                result = false;
                            }
                            break;
                        case "hcl": result = result && hclPattern.matcher(v).matches(); break;
                        case "ecl": result = result && eclPattern.matcher(v).matches(); break;
                        case "pid": result = result && pidPattern.matcher(v).matches(); break;
                    }
                }
            }
            return result;
        }

        private Map<String, String> parseFields(String line) {
            Map<String, String> result = new HashMap<>();
            String[] parts = line.split(" ");
            for (String part : parts) {
                String[] fieldParts = part.split(":");
                result.put(fieldParts[0], fieldParts[1]);
            }
            return result;
        }
    }
}
