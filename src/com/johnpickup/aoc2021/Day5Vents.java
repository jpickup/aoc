package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5Vents {
    public static void main(String[] args) throws Exception {

        final int gridSize = 1000;
        try (Stream<String> stream = Files.lines(Paths.get(
                "/Users/john/Development/AdventOfCode/resources/Day5Input.txt"))) {

            List<Vent> vents = stream.map(Day5Vents::parseVent)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            int[][] grid = new int[gridSize][gridSize];

            for (Vent vent : vents) {
                if (vent.isHorizontal()) {
                    for (int x = Math.min(vent.x1, vent.x2); x <= Math.max(vent.x1, vent.x2); x++) {
                        grid[x][vent.y1]++;
                    }
                }
                else if (vent.isVertical()) {
                    for (int y = Math.min(vent.y1, vent.y2); y <= Math.max(vent.y1, vent.y2); y++) {
                        grid[vent.x1][y]++;
                    }
                }
                else {
                    if (((vent.x1 < vent.x2) && (vent.y1 < vent.y2)) || ((vent.x1 > vent.x2) && (vent.y1 > vent.y2))) {
                        for (int x = Math.min(vent.x1, vent.x2); x <= Math.max(vent.x1, vent.x2); x++) {
                            if (vent.y1 < vent.y2)
                                grid[x][vent.y1 + x - Math.min(vent.x1, vent.x2)]++;
                            else
                                grid[x][vent.y2 + x - Math.min(vent.x1, vent.x2)]++;
                        }
                    }
                    else {
                        for (int x = Math.min(vent.x1, vent.x2); x <= Math.max(vent.x1, vent.x2); x++) {
                            if (vent.y1 < vent.y2)
                                grid[x][vent.y1 - x + Math.max(vent.x1, vent.x2)]++;
                            else
                                grid[x][vent.y2 - x + Math.max(vent.x1, vent.x2)]++;
                        }
                    }
                }
            }

            for (int y = 0; y < gridSize; y++) {
                for (int x= 0; x < gridSize; x++) {
                    System.out.print(grid[x][y]);
                }
                System.out.println();
            }

            int multi=0;
            for (int y = 0; y < gridSize; y++) {
                for (int x= 0; x < gridSize; x++) {
                    if (grid[x][y] > 1) multi++;
                }
            }
            System.out.println("Multi: " +  multi);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Vent parseVent(String s){
        if (s==null || s.isEmpty()) return null;

        Vent result = new Vent();

        String[] coords = s.split("->");

        String[] xy1 = coords[0].trim().split(",");
        String[] xy2 = coords[1].trim().split(",");

        result.x1 = Integer.parseInt(xy1[0]);
        result.y1 = Integer.parseInt(xy1[1]);
        result.x2 = Integer.parseInt(xy2[0]);
        result.y2 = Integer.parseInt(xy2[1]);

        return result;
    }

    static class Vent {
        int x1, y1, x2, y2;

        boolean isHorizontal() {
            return y1 == y2;
        }
        boolean isVertical() {
            return x1 == x2;
        }
    }
}
