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
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day12.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());
            int[][] heights = new int[lines.size()][lines.get(0).length()];
            Coord start = null;
            Coord end = null;
            int row=0;

            List<Coord> starts = new ArrayList<>();

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
                    if (heights[row][col] == 0) {
                        starts.add(Coord.builder().x(col).y(row).build());
                    }
                }
                row++;
            }

            System.out.println("No of potential starts " + starts.size());

            Day12 solver = new Day12();
            System.out.println("Part1");
            int steps = solver.solve(Collections.singletonList(start), end, heights);
            System.out.println("Length = " + steps);

            System.out.println("Part2");
            steps = solver.solve(starts, end, heights);
            System.out.println("Shortest = " + steps);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int solve(List<Coord> starts, Coord end, int[][] cells) {
        Map<Coord, Long> dist = new HashMap<>();
        dist.put(end, 0L);

        int rowCount = cells.length;
        int colCount = cells[0].length;

        Map<Coord, Coord> prev = new HashMap<>();
        PriorityQueue<Coord> q = new PriorityQueue<>(Comparator.comparing(dist::get));
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                Coord v = new Coord(col, row);
                if (!v.equals(end)) {
                    dist.put(v, Long.MAX_VALUE);
                    prev.put(v, null);
                }
                q.add(v);
            }
        }

        while (!q.isEmpty()) {
            Coord u = q.remove();

            Coord north = new Coord(u.x, u.y - 1);
            if (isValid(north, rowCount, colCount) && q.contains(north) && canMoveDown(u, north, cells)) {
                long alt = dist.get(u) + 1;
                if (alt < dist.get(north)) {
                    dist.put(north, alt);
                    prev.put(north, u);
                    q.remove(north);
                    q.add(north);
                }
            }
            Coord south = new Coord(u.x, u.y + 1);
            if (isValid(south, rowCount, colCount) && q.contains(south) && canMoveDown(u, south, cells)) {
                long alt = dist.get(u) + 1;
                if (alt < dist.get(south)) {
                    dist.put(south, alt);
                    prev.put(south, u);
                    q.remove(south);
                    q.add(south);
                }
            }
            Coord east = new Coord(u.x + 1, u.y);
            if (isValid(east, rowCount, colCount) && q.contains(east) && canMoveDown(u, east, cells)) {
                long alt = dist.get(u) + 1;
                if (alt < dist.get(east)) {
                    dist.put(east, alt);
                    prev.put(east, u);
                    q.remove(east);
                    q.add(east);
                }
            }
            Coord west = new Coord(u.x - 1, u.y);
            if (isValid(west, rowCount, colCount) && q.contains(west) && canMoveDown(u, west, cells)) {
                long alt = dist.get(u) + 1;
                if (alt < dist.get(west)) {
                    dist.put(west, alt);
                    prev.put(west, u);
                    q.remove(west);
                    q.add(west);
                }
            }
        }

        Map<Coord, List<Coord>> startPaths = new HashMap<>();

        for (Coord start : starts) {
            List<Coord> path = new ArrayList<>();
            Coord u = start;
            if (prev.get(u) != null || u.equals(end)) {
                while (u != null) {
                    path.add(0, u);
                    u = prev.get(u);
                }
            }
            if (path.size() > 0 && path.get(0).equals(end)) {
                startPaths.put(start, path);
            }
        }

        List<List<Coord>> sortedPaths = startPaths.values().stream().sorted(Comparator.comparingInt(List::size)).collect(Collectors.toList());

        return sortedPaths.get(0).size()-1;
    }

    private boolean canMoveUp(Coord from, Coord to, int[][] cells) {
        int fromHeight = cells[from.y][from.x];
        int toHeight = cells[to.y][to.x];
        // we can go to a height either one higher, or equal or any lower
        return fromHeight >= toHeight-1;
    }

    private boolean canMoveDown(Coord from, Coord to, int[][] cells) {
        int fromHeight = cells[from.y][from.x];
        int toHeight = cells[to.y][to.x];
        // we can go to a height either one lower, or equal or any higher
        return fromHeight <= toHeight+1;
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
