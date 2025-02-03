package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day8Digits {
    public static void main(String[] args) throws Exception {

        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day8Input.txt"))) {
            List<Line> lines = stream.map(Day8Digits::parseLine).filter(Objects::nonNull).collect(Collectors.toList());

            int count = 0;
            int total = 0;
            for (Line line : lines) {
                for (String word : line.rhs) {
                    if ((word.length() == 2) || (word.length() == 4) || (word.length() == 7) || (word.length() == 3))
                        count++;
                }

                line.lhs.sort(Comparator.comparingInt(String::length));

                /*
                Segment Count | Possible digits
                            2 | 1
                            3 | 7
                            4 | 4
                            5 | 2, 3, 5
                            6 | 0, 6, 9
                            7 | 8
                */

                Map<String, Integer> knownValues = new HashMap<>();
                Map<String, Set<Integer>> possibleValues = new HashMap<>();
                Map<Integer, String> actualValues = new HashMap<>();

                for (String signal : line.lhs) {
                    switch (signal.length()) {
                        case 2: // digit is 1
                            possibleValues.put(signal, Collections.singleton(1));
                            actualValues.put(1, signal);
                            knownValues.put(signal, 1);
                            break;
                        case 3: // digit is 7
                            possibleValues.put(signal, Collections.singleton(7));
                            actualValues.put(7, signal);
                            knownValues.put(signal, 7);
                            break;
                        case 4: // digit is 4
                            possibleValues.put(signal, Collections.singleton(4));
                            actualValues.put(4, signal);
                            knownValues.put(signal, 4);
                            break;
                        case 5: // digit is 2 3 or 5
                            possibleValues.put(signal, new HashSet<>(Arrays.asList(2, 3, 5)));
                            break;
                        case 6: // digit is 0, 6 or 9
                            possibleValues.put(signal, new HashSet<>(Arrays.asList(6, 9)));
                            break;
                        case 7: // digit is 8
                            possibleValues.put(signal, Collections.singleton(8));
                            actualValues.put(8, signal);
                            knownValues.put(signal, 8);
                            break;
                    }
                }
                // digit 3 can only be the item that contains the items in digit 1
                String one = actualValues.get(1);
                List<String> lengthFives = line.lhs.stream().filter(s -> s.length() == 5).collect(Collectors.toList());
                for (String lengthFive : lengthFives) {
                    if (containsAll(lengthFive, one)) {
                        actualValues.put(3, lengthFive);
                        possibleValues.put(lengthFive, Collections.singleton(3));
                        knownValues.put(lengthFive, 3);
                    }
                }
                lengthFives.remove(actualValues.get(3));

                // 6 vs 9, the 9 matches 1, the 6 doesn't
                String three = actualValues.get(3);
                List<String> lengthSixes = line.lhs.stream().filter(s -> s.length() == 6).collect(Collectors.toList());
                for (String lengthSix : lengthSixes) {
                    if (containsAll(lengthSix, three)) {
                        actualValues.put(9, lengthSix);
                        possibleValues.put(lengthSix, Collections.singleton(9));
                        knownValues.put(lengthSix, 9);
                    }
                    else if (containsAll(lengthSix, one)) {
                        actualValues.put(0, lengthSix);
                        possibleValues.put(lengthSix, Collections.singleton(0));
                        knownValues.put(lengthSix, 0);
                    }
                    else {
                        actualValues.put(6, lengthSix);
                        possibleValues.put(lengthSix, Collections.singleton(6));
                        knownValues.put(lengthSix, 6);
                    }
                }

                // digit 2 vs digit 5 - 9 contains all of 5 but not 2
                String nine = actualValues.get(9);
                for (String s : lengthFives) {
                    if (containsAll(nine, s)) {
                        actualValues.put(5, s);
                        possibleValues.put(s, Collections.singleton(5));
                        knownValues.put(s, 5);
                    }
                    else {
                        actualValues.put(2, s);
                        possibleValues.put(s, Collections.singleton(2));
                        knownValues.put(s, 2);
                    }
                }

                // we are done, emit the values
                int value = 0;
                for (String s : line.rhs) {
                    if (s==null || s.length()==0) continue;
                    Integer integer = knownValues.get(s);
                    value = value * 10 + integer;
                    //System.out.print(integer);
                }
                System.out.println(value);
                total += value;


            }
            System.out.println("TOTAL: " + total);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean containsAll(String container, String contained) {
        for (int i=0; i < contained.length(); i++) {
            if (!container.contains(contained.substring(i, i+1))) {
                return false;
            }
        }
        return true;
    }

    private static Line parseLine(String s) {
        if (s==null || s.isEmpty()) return null;
        Line line = new Line();
        String[] leftRight = s.split("\\|");
        String[] lefts = leftRight[0].split(" ");
        String[] rights = leftRight[1].split(" ");
        line.lhs = Arrays.stream(lefts).map(Day8Digits::charSortedString).collect(Collectors.toList());
        line.rhs = Arrays.stream(rights).map(Day8Digits::charSortedString).collect(Collectors.toList());
        return line;
    }

    private static class Line {
        List<String> lhs;
        List<String> rhs;
    }

    private static String charSortedString(String s) {
        char[] chars = s.toCharArray();
        List<Character> characterList = new ArrayList<>();

        for (char ch : chars) {
            characterList.add(ch);
        }
        characterList.sort(Character::compare);

        String result = "";
        for (Character character : characterList) {
            result += character;
        }
        return result;
    }

}
