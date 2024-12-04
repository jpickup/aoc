package com.johnpickup.aoc2024;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day4.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            Grid grid = new Grid(lines);
            int part1 = grid.findXmasCount();
            System.out.println("Part 1: " + part1);
            int part2 = grid.findMasCount();
            System.out.println("Part 2: " + part2);

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @Getter
    static class Grid {
        final int width;
        final int height;
        final char[][] cell;

        Grid(List<String> lines) {
            width = lines.get(0).length();
            height = lines.size();
            cell = new char[width][height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    cell[x][y] = lines.get(y).charAt(x);
                }
            }
        }

        char getCell(int x, int y) {
            if (x >= 0 && x < width && y >= 0 && y < height) {
                return cell[x][y];
            } else {
                return ' ';
            }

        }

        public int findXmasCount() {
            int result = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (findXmasDir(x,y,1,0)) result += 1;
                    if (findXmasDir(x,y,-1,0)) result += 1;
                    if (findXmasDir(x,y,0,1)) result += 1;
                    if (findXmasDir(x,y,0,-1)) result += 1;
                    if (findXmasDir(x,y,1,1)) result += 1;
                    if (findXmasDir(x,y,1,-1)) result += 1;
                    if (findXmasDir(x,y,-1,1)) result += 1;
                    if (findXmasDir(x,y,-1,-1)) result += 1;
                }
            }
            return result;
        }

        static final String XMAS = "XMAS";
        private boolean findXmasDir(int startX, int startY, int incX, int incY) {
            boolean result = true;
            for (int i = 0; i < XMAS.length(); i++) {
                int x = startX + i * incX;
                int y = startY + i * incY;
                result = result && XMAS.charAt(i) == getCell(x, y);
            }
            return result;
        }

        public int findMasCount() {
            int result = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (findMas1(x,y)) result +=1;
                    if (findMas2(x,y)) result +=1;
                    if (findMas3(x,y)) result +=1;
                    if (findMas4(x,y)) result +=1;
                }
            }
            return result;
        }

        // M left top & bot
        private boolean findMas1(int x, int y) {
            return getCell(x,y) == 'M'
                    && getCell(x+2,y) == 'S'
                    && getCell(x+1,y+1) == 'A'
                    && getCell(x,y+2) == 'M'
                    && getCell(x+2,y+2) == 'S'
                    ;
        }
        private boolean findMas2(int x, int y) {
            return getCell(x,y) == 'S'
                    && getCell(x+2,y) == 'S'
                    && getCell(x+1,y+1) == 'A'
                    && getCell(x,y+2) == 'M'
                    && getCell(x+2,y+2) == 'M'
                    ;
        }

        private boolean findMas3(int x, int y) {
            return getCell(x,y) == 'M'
                    && getCell(x+2,y) == 'M'
                    && getCell(x+1,y+1) == 'A'
                    && getCell(x,y+2) == 'S'
                    && getCell(x+2,y+2) == 'S'
                    ;
        }
        private boolean findMas4(int x, int y) {
            return getCell(x,y) == 'S'
                    && getCell(x+2,y) == 'M'
                    && getCell(x+1,y+1) == 'A'
                    && getCell(x,y+2) == 'S'
                    && getCell(x+2,y+2) == 'M'
                    ;
        }

    }
}
