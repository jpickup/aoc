package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11Octopus {
    public static void main(String[] args) throws Exception {

        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day11Input.txt"))) {
            List<String> lines = stream.filter(Objects::nonNull).collect(Collectors.toList());
            int rows = lines.size();
            int cols = lines.get(0).length();

            int[][] energy = new int[rows][cols];
            int rowLoad=0;
            for (String line : lines) {
                for (int col=0; col < line.length(); col++) {
                    energy[rowLoad][col] = line.toCharArray()[col] - '0';
                }
                rowLoad++;
            }

            printEnergy(energy);

            int totalFlashCount = 0;

            for (int step = 0; step < 10000; step ++) {
                int stepFlashCount = 0;
                boolean[][] flashed = new boolean[rows][cols];
                // increase energy
                for (int row=0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        energy[row][col]++;
                    }
                }

                int flashCount;
                do {
                    flashCount = 0;
                    // check for flashes
                    for (int row=0; row < rows; row++) {
                        for (int col = 0; col < cols; col++) {
                            if (energy[row][col] > 9) {
                                flashCount++;
                                totalFlashCount++;
                                stepFlashCount++;
                                energy[row][col] = 0;
                                flashed[row][col]=true;

                                // increase adjacent
                                // LEFT
                                if ((row > 0) && (!flashed[row-1][col])) {
                                    energy[row-1][col]++;
                                }
                                // RIGHT
                                if ((row < rows-1) && (!flashed[row+1][col])) {
                                    energy[row+1][col]++;
                                }
                                // ABOVE
                                if ((col > 0) && (!flashed[row][col-1])) {
                                    energy[row][col-1]++;
                                }
                                // RIGHT
                                if ((col < cols-1) && (!flashed[row][col+1])) {
                                    energy[row][col+1]++;
                                }
                                // ABOVE-LEFT
                                if ((row > 0) && (col>0) && (!flashed[row-1][col-1])) {
                                    energy[row-1][col-1]++;
                                }
                                // ABOVE-RIGHT
                                if ((row < rows-1) && (col>0) && (!flashed[row+1][col-1])) {
                                    energy[row+1][col-1]++;
                                }
                                // BELOW-LEFT
                                if ((row > 0) && (col<cols-1) && (!flashed[row-1][col+1])) {
                                    energy[row-1][col+1]++;
                                }
                                // BELOW-RIGHT
                                if ((row < rows-1) && (col<cols-1) && (!flashed[row+1][col+1])) {
                                    energy[row+1][col+1]++;
                                }
                            }
                        }
                    }
                } while (flashCount != 0);
                System.out.println("STEP: " + (step+1));
                printEnergy(energy);
                System.out.println("Count: " + stepFlashCount);
                if (stepFlashCount == 100) System.exit(0);

            }

            System.out.println("Result " + totalFlashCount);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printEnergy(int[][] energies) {
        for (int row = 0; row < energies.length; row++) {
            for (int col=0; col < energies[row].length; col++) {
                System.out.print(energies[row][col]);
            }
            System.out.println();
        }

    }

}
