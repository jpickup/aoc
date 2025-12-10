package com.johnpickup.aoc2025;

import com.johnpickup.util.*;

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
                break;
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
            Set<Rect<Integer>> coveredAreas = buildCoveredAreas();
            System.out.println(visualiseCoveredAreas(coveredAreas));
            long maxArea = 0L;
            for (Coord tile1 : tiles) {
                for (Coord tile2 : tiles) {
                    if (tile1.compareTo(tile2) < 0) {
                        Rect<Integer> rect = createRect(tile1, tile2);
                        if (isValidRect(rect, coveredAreas)) {
                            long area = calcArea(rect);
                            if (area > maxArea) maxArea = area;
                        }
                    }
                }
            }
            return maxArea;
        }

        private String visualiseCoveredAreas(Set<Rect<Integer>> coveredAreas) {
            System.out.println(coveredAreas);
            int maxX = tiles.stream().map(Coord::getX).max(Integer::compareTo).orElseThrow() + 2;
            int maxY = tiles.stream().map(Coord::getY).max(Integer::compareTo).orElseThrow() + 2;
            CharGrid grid = new CharGrid(maxX, maxY);
            for (int x=0; x < maxX; x++) {
                for (int y=0; y < maxY; y++) {
                    grid.setCell(new Coord(x, y), '.');
                }
            }

            for (Rect<Integer> coveredArea : coveredAreas) {
                Range<Integer> rx = coveredArea.getX();
                Range<Integer> ry = coveredArea.getY();
                for (int x = rx.getLower(); x <= rx.getUpper(); x++) {
                    for (int y = ry.getLower(); y <= ry.getUpper(); y++) {
                        grid.setCell(new Coord(x,y), 'X');
                    }
                }
            }
            tiles.forEach(t -> grid.setCell(t, '#'));

            return grid.toString();
        }

        private Set<Rect<Integer>> buildCoveredAreas() {
            // generate all possible squares
            Set<Rect<Integer>> result = new TreeSet<>();
            TreeSet<Integer> distinctXs = new TreeSet<>();
            TreeSet<Integer> distinctYs = new TreeSet<>();
            for (Coord tile : tiles) {
                distinctXs.add(tile.getX());
                distinctYs.add(tile.getY());
            }
            Integer prevX = null;
            for (Integer x : distinctXs) {
                Integer prevY = null;
                for (Integer y : distinctYs) {
                    if (prevX != null && prevY != null
                            && x.compareTo(prevX) != 0 && y.compareTo(prevY) != 0) {
                        result.add(createRect(new Coord(prevX, prevY), new Coord(x, y)));
                    }
                    prevY = y;
                }
                prevX = x;
            }

            System.out.println(visualiseCoveredAreas(result));

            // remove the ones that aren't included when we trace a path of lines segments between the tiles
            List<LineSegment> segments = generateSegments(tiles);
            System.out.println(visualiseSegments(segments));
            return result.stream()
                    .filter(r -> isEnclosedByBoundary(r, segments))
                    .collect(Collectors.toSet());
            // TODO: rationalise the set, e.g. merging adjacent rects that can be joined
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

        /**
         * if we count the number of bounding line segments we cross going up/down and going right/left
         * then we are enclosed if both numbers are odd
         */
        private boolean isEnclosedByBoundary(Rect<Integer> r, List<LineSegment> segments) {
            LineSegment vert = LineSegment.createFrom(new Coord(r.left(), r.top()), new Coord(r.left(), Integer.MAX_VALUE));
            LineSegment horz = LineSegment.createFrom(new Coord(r.left(), r.top()), new Coord(Integer.MAX_VALUE, r.top()));

            long vertCrossCount = segments.stream().filter(s -> s.crosses(vert) && !s.equals(horz)).count();
            long horzCrossCount = segments.stream().filter(s -> s.crosses(horz) && !s.equals(vert)).count();

            boolean result = (vertCrossCount % 2 == 1) && (horzCrossCount % 2 == 1);
            System.out.printf("%s enclosed V=%d H=%d : %s%n", r, vertCrossCount, horzCrossCount, result?"Y":"N");

            return result;
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

        /**
         * Is the rectangle completely covered by all the tiled areas?
         */
        private boolean isValidRect(Rect<Integer> rect, Set<Rect<Integer>> coveredAreas) {
            // remove each of the covered areas from the rect, if, at the end, there is nothing left then it's valid
            Set<Rect<Integer>> uncoveredAreas = Collections.singleton(rect);
            for (Rect<Integer> coveredArea : coveredAreas) {
                uncoveredAreas = removeArea(coveredArea, uncoveredAreas);
            }
            return uncoveredAreas.isEmpty();
        }

        /**
         * Remove the given area from the original areas
         */
        private Set<Rect<Integer>> removeArea(Rect<Integer> areaToRemove, Set<Rect<Integer>> originalAreas) {
            Set<Rect<Integer>> result = new TreeSet<>();
            for (Rect<Integer> uncoveredArea : originalAreas) {
                result.addAll(removeArea(areaToRemove, uncoveredArea));
            }
            return result;
        }

        /**
         * Remove the area from the original
         */
        private Set<Rect<Integer>> removeArea(Rect<Integer> areaToRemove, Rect<Integer> original) {
            // moved code to rect class
            return original.removeArea(areaToRemove);
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
