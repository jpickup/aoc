package com.johnpickup.aoc2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day9 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day9/Day9.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Disk disk1 = new Disk(lines.get(0));
            long part1 = disk1.part1();
            System.out.println("Part 1: " + part1);

            Disk disk2 = new Disk(lines.get(0));
            long part2 = disk2.part2();
            System.out.println("Part 2: " + part2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    static class Disk {
        final int[] blocks;
        final Map<Integer, Integer> fileSizes = new HashMap<>();
        final Map<Integer, Integer> initialFileLocations = new HashMap<>();
        final int size;
        Disk(String input) {
            size = calcSize(input);
            blocks = new int[size];
            int location = 0;
            for (int i = 0; i < input.length(); i++) {
                boolean isFreeSpace = i % 2 == 1;
                int fileNo = i / 2;
                int fileSize = input.charAt(i) - '0';
                if (!isFreeSpace) {
                    fileSizes.put(fileNo, fileSize);
                    initialFileLocations.put(fileNo, location);
                }
                for (int j=0; j < fileSize; j++) {
                    blocks[location] = isFreeSpace ? -1 : fileNo;
                    location++;
                }
            }
        }

        long part1() {
            compact();
            return calcChecksum();
        }

        long part2() {
            compactWholeFiles();
            return calcChecksum();
        }

        void compact() {
            for (int i = 0; i < size; i++) {
                if (blocks[i] == -1) {
                    for (int j = size-1; j>i; j--) {
                        if (blocks[j] != -1) {
                            // move block j to block i
                            blocks[i] = blocks[j];
                            blocks[j] = -1;
                            break;
                        }
                    }
                }
            }
        }

        int findFile(int fileNum) {
            return initialFileLocations.get(fileNum);
        }

        int findSpace(int spaceRequired) {
            for (int i = 0; i < size; i++) {
                if (blocks[i] == -1) {
                    int found = 0;
                    for (int j = i; j < size; j++) {
                        boolean isSpace = blocks[j] == -1;
                        if (isSpace) {
                            found++;
                        } else {
                            break;
                        }
                    }
                    if (found >= spaceRequired) {
                        return i;
                    }
                }
            }
            return -1;
        }

        void compactWholeFiles() {
            List<Integer> fileNumbers = fileSizes.keySet().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
            for (Integer fileNumber : fileNumbers) {
                int requiredSize = fileSizes.get(fileNumber);
                int fileLocation = findFile(fileNumber);
                int spaceLocation = findSpace(requiredSize);

                if (spaceLocation >= 0 && spaceLocation < fileLocation) {
                    for (int j = 0; j < requiredSize; j++) {
                        blocks[spaceLocation + j] = blocks[fileLocation + j];
                        blocks[fileLocation + j] = -1;
                    }
                }
            }
        }

        @Override
        public String toString() {
            // only really useful for the test data as > 10 files isn't all that helpful
            StringBuilder sb = new StringBuilder();
            for (int i=0; i < size; i++) {
                sb.appendCodePoint(blocks[i]==-1?'.':'0'+(blocks[i]%10));
            }
            return sb.toString();
        }

        int calcSize(String input) {
            int result = 0;
            for (int i = 0; i < input.length(); i++) {
                int blocks = input.charAt(i) - '0';
                result += blocks;
            }
            return result;
        }

        long calcChecksum() {
            long result = 0;
            for (int i = 0; i < size; i++) {
                if (blocks[i] != -1) {
                    result += i * blocks[i];
                }
            }
            return result;
        }
    }
}
