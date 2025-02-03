package com.johnpickup.aoc2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2020/Day3/Day3.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Grid grid = new Grid(lines);
            System.out.println("Part 1 : " + grid.part1());
            System.out.println("Part 2 : " + grid.part2());

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    static class Grid {
        final int width;
        final int height;
        final char[][] cells;
        Grid(List<String> lines) {
            width = lines.get(0).length();
            height = lines.size();
            cells = new char[lines.get(0).length()][lines.size()];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    cells[x][y] = lines.get(y).charAt(x);
                }
            }
        }

        char getCell(int x, int y) {
            if (y >= height) throw new RuntimeException("Out of bounds");
            return cells[x % width][y];
        }

        long part1() {
            return countTrees(3, 1);
        }

        long part2() {
            return countTrees(1, 1)
                   * countTrees(3, 1)
                    * countTrees(5, 1)
                    * countTrees(7, 1)
                    * countTrees(1, 2);
        }

        long countTrees(int slopeX, int slopeY) {
            long result = 0;
            int x = 0;
            int y = 0;
            while (y < height) {
                if (getCell(x, y) == '#') result ++;
                x += slopeX;
                y += slopeY;
            }
            return result;
        }
    }
}
