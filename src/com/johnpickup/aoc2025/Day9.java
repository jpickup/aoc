package com.johnpickup.aoc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.Rect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day9 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Coord> coords = stream
                        .filter(s -> !s.isEmpty())
                        .map(Coord::new)
                        .toList();

                Theatre theatre = new Theatre(coords);

                System.out.println("Part 1: " + theatre.part1());
                System.out.println("Part 2: " + theatre.part2());
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Theatre {
        private final List<Coord> tiles;
        public Theatre(List<Coord> coords) {
            this.tiles = coords;
        }

        public long part1() {
            long maxArea = 0L;
            for (Coord tile1 : tiles) {
                for (Coord tile2 : tiles) {
                    if (tile1.compareTo(tile2) < 0) {
                        Rect<Integer> rect = createRect(tile1, tile2);
                        long area = calcArea(rect);
                        if (area > maxArea) maxArea = area;
                    }
                }
            }
            return maxArea;
        }

        public long part2() {
            Map<Integer, Integer> xValueByIndex = new TreeMap<>();
            Map<Integer, Integer> yValueByIndex = new TreeMap<>();
            Map<Integer, Integer> xIndexByValue = new TreeMap<>();
            Map<Integer, Integer> yIndexByValue = new TreeMap<>();
            List<Integer> distinctX = tiles.stream().map(Coord::getX).distinct().sorted(Integer::compareTo).toList();
            for (int i = 0; i < distinctX.size(); i++) {
                xValueByIndex.put(i, distinctX.get(i));
                xIndexByValue.put(distinctX.get(i), i);
            }
            List<Integer> distinctY = tiles.stream().map(Coord::getY).distinct().sorted(Integer::compareTo).toList();
            for (int i = 0; i < distinctY.size(); i++) {
                yValueByIndex.put(i, distinctY.get(i));
                yIndexByValue.put(distinctY.get(i), i);
            }

            List<Coord> tilesCompressed = tiles.stream().map(t -> new Coord(xIndexByValue.get(t.getX()), yIndexByValue.get(t.getY()))).toList();

            Set<Coord> boundaryCoords = extractBoundaryCoords(tilesCompressed);
            System.out.println("Boundary ---");
            visualiseCoords(boundaryCoords);
            System.out.println("Valid area ---");
            Set<Coord> validCoords = fillBoundary(boundaryCoords);
            visualiseCoords(validCoords);

            long maxArea = 0L;
            for (Coord tile1 : tilesCompressed) {
                for (Coord tile2 : tilesCompressed) {
                    if (tile1.compareTo(tile2) < 0 && tile1.getX() != tile2.getX() && tile1.getY() != tile2.getY()) {
                        Rect<Integer> rect = createRect(tile1, tile2);
                        if (isValidRect(rect, validCoords)) {
                            Rect<Integer> realRect = new Rect<>(xValueByIndex.get(rect.left()), yValueByIndex.get(rect.top()),
                                    xValueByIndex.get(rect.right()), yValueByIndex.get(rect.bottom()));
                            long area = calcArea(realRect);
                            if (area > maxArea) maxArea = area;
                        }
                    }
                }
            }
            return maxArea;
        }

        private void visualiseCoords(Set<Coord> coords) {
            int maxX = coords.stream().map(Coord::getX).max(Integer::compareTo).orElseThrow() + 1;
            int maxY = coords.stream().map(Coord::getY).max(Integer::compareTo).orElseThrow() + 1;
            CharGrid grid = new CharGrid(maxX, maxY, '.');
            coords.forEach(c -> grid.setCell(c, '#'));
            System.out.println(grid);
        }

        private Set<Coord> fillBoundary(Set<Coord> boundaryCoords) {
            int maxX = boundaryCoords.stream().map(Coord::getX).max(Integer::compareTo).orElseThrow() + 1;
            Set<Coord> result = new HashSet<>(boundaryCoords);

            Coord startFill;
            if (maxX < 10)
                startFill = new Coord(2,1);
            else
                startFill = new Coord(maxX / 2, 50);
            fillAround(startFill, result);
            return result;
        }

        private void fillAround(Coord c, Set<Coord> result) {
            Set<Coord> tryFill = new HashSet<>();
            tryFill.add(c);

            while (!tryFill.isEmpty()) {
                Coord prev = c;
                Set<Coord> next = new HashSet<>();
                for (Coord coord : tryFill) {
                    result.add(coord);
                    prev = coord;
                    next.addAll(coord.adjacent8().stream().filter(f -> !result.contains(f)).collect(Collectors.toSet()));
                }
                result.addAll(next);
                tryFill.remove(prev);
                tryFill.addAll(next);
            }
        }

        private Set<Coord> extractBoundaryCoords(List<Coord> tilesCompressed) {
            Set<Coord> result = new HashSet<>();
            Coord prev = tilesCompressed.getLast();
            for (Coord coord : tilesCompressed) {
                result.addAll(pointsBetween(prev, coord));
                prev = coord;
            }
            return result;
        }

        private Set<Coord> pointsBetween(Coord c1, Coord c2) {
            Set<Coord> result = new HashSet<>();
            if (c1.getX() == c2.getX()) {
                for (int y = Math.min(c1.getY(), c2.getY()); y <= Math.max(c1.getY(), c2.getY()); y++) {
                    result.add(new Coord(c1.getX(), y));
                }
            }
            if (c1.getY() == c2.getY()) {
                for (int x = Math.min(c1.getX(), c2.getX()); x <= Math.max(c1.getX(), c2.getX()); x++) {
                    result.add(new Coord(x, c1.getY()));
                }
            }
            return result;
        }

        private boolean isValidRect(Rect<Integer> rect, Set<Coord> validCoords) {
            for (int x = rect.left(); x <= rect.right(); x++) {
                for (int y = rect.top(); y <= rect.bottom(); y++) {
                    if (!validCoords.contains(new Coord(x, y))) return false;
                }
            }
            return true;
        }

        private Rect<Integer> createRect(Coord tile1, Coord tile2) {
            return new Rect<>(tile1.getX(), tile1.getY(), tile2.getX(), tile2.getY());
        }
    }

    private static long calcArea(Rect<Integer> rect) {
        long dx = Math.abs((long)rect.right() - rect.left()) + 1L;
        long dy = Math.abs((long)rect.top() - rect.bottom()) + 1L;
        return dx * dy;
    }
}
