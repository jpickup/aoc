package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class Day12 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day12.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());
            int[][] heights = new int[lines.size()][lines.get(0).length()];
            Coord start = null;
            Coord end = null;
            int row=0;
            for (String line : lines) {
                for (int col=0; col<line.length(); col++) {
                    heights[row][col] = line.charAt(col)-'a';
                    if (line.charAt(col) == 'S') {
                        start = Coord.builder().x(col).y(row).build();
                        heights[row][col] = 0;
                    }
                    if (line.charAt(col) == 'E') {
                        end = Coord.builder().x(col).y(row).build();
                        heights[row][col] = 25;
                    }
                }
                row++;
            }

            Day12 solver = new Day12();

            solver.solve(start, end, heights);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void solve(Coord start, Coord end, int[][] cells) {
        Map<Coord, Long> dist = new HashMap<>();
        dist.put(start, 0L);

        int rowCount = cells.length;
        int colCount = cells[0].length;

        Map<Coord, Coord> prev = new HashMap<>();
        PriorityQueue<Coord> q = new PriorityQueue<>(Comparator.comparing(dist::get));
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                Coord v = new Coord(col, row);
                if (!v.equals(start)) {
                    dist.put(v, Long.MAX_VALUE);
                    prev.put(v, null);
                }
                q.add(v);
            }
        }

        while (!q.isEmpty()) {
            Coord u = q.remove();

            Coord north = new Coord(u.x, u.y - 1);
            if (isValid(north, rowCount, colCount) && q.contains(north) && canMove(u, north, cells)) {
                long alt = dist.get(u) + 1;
                if (alt < dist.get(north)) {
                    dist.put(north, alt);
                    prev.put(north, u);
                    q.remove(north);
                    q.add(north);
                }
            }
            Coord south = new Coord(u.x, u.y + 1);
            if (isValid(south, rowCount, colCount) && q.contains(south) && canMove(u, south, cells)) {
                long alt = dist.get(u) + 1;
                if (alt < dist.get(south)) {
                    dist.put(south, alt);
                    prev.put(south, u);
                    q.remove(south);
                    q.add(south);
                }
            }
            Coord east = new Coord(u.x + 1, u.y);
            if (isValid(east, rowCount, colCount) && q.contains(east) && canMove(u, east, cells)) {
                long alt = dist.get(u) + 1;
                if (alt < dist.get(east)) {
                    dist.put(east, alt);
                    prev.put(east, u);
                    q.remove(east);
                    q.add(east);
                }
            }
            Coord west = new Coord(u.x - 1, u.y);
            if (isValid(west, rowCount, colCount) && q.contains(west) && canMove(u, west, cells)) {
                long alt = dist.get(u) + 1;
                if (alt < dist.get(west)) {
                    dist.put(west, alt);
                    prev.put(west, u);
                    q.remove(west);
                    q.add(west);
                }
            }
        }
        List<Coord> s = new ArrayList<>();
        Coord u = end;
        if (prev.get(u) != null || u.equals(start)) {
            while (u != null) {
                s.add(0, u);
                u = prev.get(u);
            }
        }

        System.out.println("Number of steps: " + (s.size()-1));
    }

    private boolean canMove(Coord from, Coord to, int[][] cells) {
        int fromHeight = cells[from.y][from.x];
        int toHeight = cells[to.y][to.x];
        return fromHeight >= toHeight-1;
    }

    private boolean isValid(Coord coord, int rowCount, int colCount) {
        return coord.x >= 0 && coord.y >= 0 && coord.x < colCount && coord.y < rowCount;
    }


    @ToString
    @Builder
    @EqualsAndHashCode
    static class Coord {
        int x;
        int y;
    }
}
