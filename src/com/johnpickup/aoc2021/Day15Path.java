package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15Path {
    int[][] cells;
    int size;

    public static void main(String[] args) {
        Day15Path day15 = new Day15Path("/Users/john/Development/AdventOfCode/resources/Day15Test.txt");
        day15.solve();
    }

    public Day15Path(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {

            List<String> lines = stream.collect(Collectors.toList());
            size = lines.size();
            cells = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    cells[row][col] = lines.get(row).charAt(col) - '0';
                }
            }
            printCells(cells);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void solve() {
//        Set<Coord> q = new HashSet<>();
//    }

    public void solve() {
        Coord start = new Coord(0, 0);
        Coord target = new Coord(size - 1, size - 1);
        List<Coord> visited = Collections.emptyList();

        List<Coord> solution = solve(start, target, visited);

        System.out.println("Solution: " + calcTotal(solution));
    }

    private List<Coord> solve(Coord location, Coord target, List<Coord> visited) {
        if (location.equals(target)) return Collections.singletonList(location);

        Coord north = new Coord(location.x, location.y-1);
        Coord south = new Coord(location.x, location.y+1);
        Coord east = new Coord(location.x+1, location.y);
        Coord west = new Coord(location.x-1, location.y);

        List<Coord> newVisited = new ArrayList<>(visited);
        newVisited.add(location);

        List<List<Coord>> paths = new ArrayList<>();
        if (isValid(north) && !visited.contains(north)) paths.add(solve(north, target, newVisited));
        if (isValid(south) && !visited.contains(south)) paths.add(solve(south, target, newVisited));
        if (isValid(east) && !visited.contains(east)) paths.add(solve(east, target, newVisited));
        if (isValid(west) && !visited.contains(west)) paths.add(solve(west, target, newVisited));

        List<Coord> leastWeightPath = null;
        int leastWeight = Integer.MAX_VALUE;
        for (List<Coord> path : paths) {
            if (path != null) {
                int weight = calcTotal(path);
                if (weight < leastWeight) {
                    leastWeight = weight;
                    leastWeightPath = path;
                }
            }
        }
        return leastWeightPath;
    }

    private int calcTotal(List<Coord> path) {
        int total = 0;
        for (Coord coord : path) {
            total += cells[coord.y][coord.x];
        }
        return total;
    }

    private boolean isValid(Coord coord) {
        return coord.x>=0 && coord.y>=0 && coord.x<size && coord.y<size;
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
