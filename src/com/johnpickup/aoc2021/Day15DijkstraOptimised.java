package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15DijkstraOptimised {
    int[][] cells;
    int size;

    public static void main(String[] args) {
        Day15DijkstraOptimised day15 = new Day15DijkstraOptimised("/Volumes/Users/john/Development/AdventOfCode/resources/Day15Input.txt");
        long start = System.currentTimeMillis();
        day15.solve();
        long end = System.currentTimeMillis();
        System.out.printf("Completed in %d ms\n", end - start);
    }

    public Day15DijkstraOptimised(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {

            List<String> lines = stream.collect(Collectors.toList());
            int tileSize = lines.size();
            size = lines.size() * 5;
            cells = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    cells[row][col] = (lines.get(row % tileSize).charAt(col % tileSize) - '0' + (row/tileSize) + (col/tileSize) -1) % 9 + 1;
                }
            }
            printCells(cells);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void solve() {
        Coord source = new Coord(0, 0);
        Coord target = new Coord(size - 1, size - 1);
        Map<Coord, Long> dist = new HashMap<>();
        dist.put(source, 0L);
        Map<Coord, Coord> prev = new HashMap<>();
        PriorityQueue<Coord> q = new PriorityQueue<>(Comparator.comparing(dist::get));
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Coord v = new Coord(col, row);
                if (!v.equals(source)) {
                    dist.put(v, Long.MAX_VALUE);
                    prev.put(v, null);
                }
                q.add(v);
            }
        }
        while (!q.isEmpty()) {
            Coord u = q.remove();

            Coord north = new Coord(u.x, u.y - 1);
            if (isValid(north) && q.contains(north)) {
                long alt = dist.get(u) + cells[north.y][north.x];
                if (alt < dist.get(north)) {
                    dist.put(north, alt);
                    prev.put(north, u);
                    q.remove(north);
                    q.add(north);
                }
            }
            Coord south = new Coord(u.x, u.y + 1);
            if (isValid(south) && q.contains(south)) {
                long alt = dist.get(u) + cells[south.y][south.x];
                if (alt < dist.get(south)) {
                    dist.put(south, alt);
                    prev.put(south, u);
                    q.remove(south);
                    q.add(south);
                }
            }
            Coord east = new Coord(u.x + 1, u.y);
            if (isValid(east) && q.contains(east)) {
                long alt = dist.get(u) + cells[east.y][east.x];
                if (alt < dist.get(east)) {
                    dist.put(east, alt);
                    prev.put(east, u);
                    q.remove(east);
                    q.add(east);
                }
            }
            Coord west = new Coord(u.x - 1, u.y);
            if (isValid(west) && q.contains(west)) {
                long alt = dist.get(u) + cells[west.y][west.x];
                if (alt < dist.get(west)) {
                    dist.put(west, alt);
                    prev.put(west, u);
                    q.remove(west);
                    q.add(west);
                }
            }
        }
        List<Coord> s = new ArrayList<>();
        Coord u = target;
        if (prev.get(u) != null || u.equals(source)) {
            while (u != null) {
                s.add(0, u);
                u = prev.get(u);
            }
        }

        int total = calcTotal(s) - cells[0][0];
        System.out.println(total);
    }


    private int calcTotal(List<Coord> path) {
        int total = 0;
        for (Coord coord : path) {
            total += cells[coord.y][coord.x];
        }
        return total;
    }

    private boolean isValid(Coord coord) {
        return coord.x >= 0 && coord.y >= 0 && coord.x < size && coord.y < size;
    }

    private void printCells(int[][] cells) {
        for (int[] cell : cells) {
            for (int i : cell) {
                System.out.print(i);
            }
            System.out.println();
        }
    }

    static class Coord {
        int x, y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return x == coord.x &&
                    y == coord.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }


}
