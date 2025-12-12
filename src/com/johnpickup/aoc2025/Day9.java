package com.johnpickup.aoc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.LineSegment;
import com.johnpickup.util.Rect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
                //break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Theatre {
        private static final char RED = '#';
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
            List<LineSegment> segments = generateSegments(tiles);
            //System.out.println(visualiseSegments(segments));

            long maxArea = 0L;
            for (Coord tile1 : tiles) {
                for (Coord tile2 : tiles) {
                    if (tile1.compareTo(tile2) < 0 && tile1.getX() != tile2.getX() && tile1.getY() != tile2.getY()) {
                        Rect<Integer> rect = createRect(tile1, tile2);
                        if (isValidRect(rect, segments)) {
                            long area = calcArea(rect);
                            if (area > maxArea) maxArea = area;
                        }
                    }
                }
            }
            return maxArea;
        }

        private String visualiseSegments(List<LineSegment> segments) {
            int maxX = tiles.stream().map(Coord::getX).max(Integer::compareTo).orElseThrow() + 2;
            int maxY = tiles.stream().map(Coord::getY).max(Integer::compareTo).orElseThrow() + 2;
            CharGrid grid = new CharGrid(maxX, maxY);
            for (int x=0; x < maxX; x++) {
                for (int y=0; y < maxY; y++) {
                    Coord coord = new Coord(x, y);
                    grid.setCell(coord, '.');
                    for (LineSegment segment : segments) {
                        if (segment.contains(coord))
                            grid.setCell(coord, '+');
                    }
                }
            }
            return grid.toString();
        }

        private List<LineSegment> generateSegments(List<Coord> tiles) {
            List<LineSegment> result = new ArrayList<>();
            Coord prev = tiles.getLast();
            for (Coord curr : tiles) {
                result.add(LineSegment.createFrom(prev, curr));
                prev = curr;
            }
            return result;
        }

        private boolean isValidRect(Rect<Integer> rect, List<LineSegment> borderSegments) {
            // valid if it crosses no border segments
            return borderSegments.stream().noneMatch(s -> rectIntersectsSegment(rect, s));
        }

        private boolean rectIntersectsSegment(Rect<Integer> rect, LineSegment segment) {
            Coord topLeft = new Coord(rect.left(), rect.top());
            Coord topRight = new Coord(rect.right(), rect.top());
            Coord bottomLeft = new Coord(rect.left(), rect.bottom());
            Coord bottomRight = new Coord(rect.right(), rect.bottom());
            LineSegment top = LineSegment.createFrom(topLeft, topRight);
            LineSegment right = LineSegment.createFrom(bottomRight, topRight);
            LineSegment bottom = LineSegment.createFrom(bottomLeft, bottomRight);
            LineSegment left = LineSegment.createFrom(bottomLeft, topLeft);

            return top.crosses(segment)
                    || right.crosses(segment)
                    || bottom.crosses(segment)
                    || left.crosses(segment);
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
