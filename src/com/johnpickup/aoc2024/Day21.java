package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.CharGrid;
import com.johnpickup.aoc2024.util.Coord;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day21 {
    static boolean isTest;

    public static void main(String[] args) {
        String day = new Object() {
        }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                //prefix + "-test2.txt"
                prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Robot> robots = stream
                        .filter(s -> !s.isEmpty())
                        .map(Robot::new)
                        .collect(Collectors.toList());

                long part1 = robots.stream().map(Robot::part1).reduce(0L, Long::sum);
                System.out.println("Part 1: " + part1);

                long part2 = robots.stream().map(Robot::part2).reduce(0L, Long::sum);
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Robot {
        final String code;

        long part1() {
            long shortest = calcShortest(3);
            long codeValue = Long.parseLong(code.substring(0, code.length() - 1));
            System.out.printf("PART 1 : %d * %d = %d %n", shortest, codeValue, shortest * codeValue);
            return shortest * codeValue;
        }

        long part2() {
            long shortest = calcShortest(26);
            long codeValue = Long.parseLong(code.substring(0, code.length() - 1));
            System.out.printf("PART 2 : %d * %d = %d %n", shortest, codeValue, shortest * codeValue);
            return shortest * codeValue;
        }

        long calcShortest(int levels) {
            List<Character> keyList = new ArrayList<>();
            for (char c : code.toCharArray()) {
                keyList.add(c);
            }
            NumericKeypad numericKeypad = new NumericKeypad();
            DirectionalKeypad directionalKeypad = new DirectionalKeypad();
            Map<KeyPair, Long> pairLengths = directionalKeypad.initialPairLengths;
            for (int i = 0; i < levels; i++) {
                Keypad keypad = i == levels - 1 ? numericKeypad : directionalKeypad;
                pairLengths = keypad.calcPairLengths(pairLengths);
            }
            return numericKeypad.fewestPresses(keyList, pairLengths);
        }
    }

    static class Keypad {
        final CharGrid grid;
        final Coord spaceLocation;
        final Map<Character, Coord> keyCoords = new HashMap<>();
        final Map<KeyPair, Long> initialPairLengths = new HashMap<>();

        Keypad(List<String> lines) {
            grid = new CharGrid(lines);
            for (int x = 0; x < grid.getWidth(); x++) {
                for (int y = 0; y < grid.getHeight(); y++) {
                    Coord c = new Coord(x, y);
                    keyCoords.put(grid.getCell(c), c);
                }
            }
            for (Character key1 : keyCoords.keySet()) {
                for (Character key2 : keyCoords.keySet()) {
                    initialPairLengths.put(new KeyPair(key1, key2), 1L);
                }
            }
            spaceLocation = grid.findAll(' ').stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Space key not found"));
        }

        static int iter = 1;

        Map<KeyPair, Long> calcPairLengths(Map<KeyPair, Long> pairLengths) {
            Map<KeyPair, Long> newPairLengths = new HashMap<>();
            for (Map.Entry<Character, Coord> keyCoordEntry1 : keyCoords.entrySet()) {
                for (Map.Entry<Character, Coord> keyCoordEntry2 : keyCoords.entrySet()) {
                    int dx = keyCoordEntry2.getValue().getX() - keyCoordEntry1.getValue().getX();
                    int dy = keyCoordEntry2.getValue().getY() - keyCoordEntry1.getValue().getY();
                    List<Character> horizontalKeys = generateKeyCount(Math.abs(dx), dx > 0 ? '>' : '<');
                    List<Character> verticalKeys = generateKeyCount(Math.abs(dy), dy > 0 ? 'v' : '^');
                    List<Character> aKey = Collections.singletonList('A');
                    long fewestHorizontalFirst = fewestPresses(joinLists(Arrays.asList(horizontalKeys, verticalKeys, aKey)), pairLengths);
                    long fewestVerticalFirst = fewestPresses(joinLists(Arrays.asList(verticalKeys, horizontalKeys, aKey)), pairLengths);
                    if (spaceLocation.equals(new Coord(keyCoordEntry2.getValue().getX(), keyCoordEntry1.getValue().getY()))) {
                        fewestHorizontalFirst = Long.MAX_VALUE;
                    }
                    if (spaceLocation.equals(new Coord(keyCoordEntry1.getValue().getX(), keyCoordEntry2.getValue().getY()))) {
                        fewestVerticalFirst = Long.MAX_VALUE;
                    }
                    long fewest = Math.min(fewestHorizontalFirst, fewestVerticalFirst);
                    newPairLengths.put(new KeyPair(keyCoordEntry1.getKey(), keyCoordEntry2.getKey()), fewest);
                }
            }
            return newPairLengths;
        }

        private void dumpPairLengths(Map<KeyPair, Long> pairLengths) {
            for (Map.Entry<KeyPair, Long> entry : pairLengths.entrySet()) {
                System.out.printf("%s -> %s%n", entry.getKey(), entry.getValue());
            }
        }

        long fewestPresses(List<Character> keys, Map<KeyPair, Long> pairLengths) {
            long result = 0L;
            Character prev = 'A';
            for (Character key : keys) {
                KeyPair keyPair = new KeyPair(prev, key);
                result += pairLengths.get(keyPair);
                prev = key;
            }
            return result;
        }

        List<Character> joinLists(List<List<Character>> lists) {
            List<Character> result = new ArrayList<>();
            for (List<Character> list : lists) {
                result.addAll(list);
            }
            return result;
        }

        private List<Character> generateKeyCount(int count, char c) {
            List<Character> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                result.add(c);
            }
            return result;
        }
    }

    static class NumericKeypad extends Keypad {
        NumericKeypad() {
            super(Arrays.asList("789", "456", "123", " 0A"));
        }
    }

    static class DirectionalKeypad extends Keypad {
        DirectionalKeypad() {
            super(Arrays.asList(" ^A", "<v>"));
        }
    }

    @Data
    static class KeyPair {
        final char key1;
        final char key2;

        @Override
        public String toString() {
            return "(" + key1 + "," + key2 + ")";
        }
    }
}
