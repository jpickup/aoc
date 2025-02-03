package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
---- 1 multiplier ----
-1,-1=985      0,-1=5749      1,-1=961
-1,0=5766      0,0=7632       1,0=5779
-1,1=980       0,1=5796       1,1=969
---- 3 multiplier ----
-3,-3=0        -2,-3=0        -1,-3=985      0,-3=5749      1,-3=961       2,-3=0         3,-3=0
-3,-2=0        -2,-2=985      -1,-2=6694     0,-2=7632      1,-2=6704      2,-2=961       3,-2=0
-3,-1=985      -2,-1=6694     -1,-1=7632     0,-1=7649      1,-1=7632      2,-1=6704      3,-1=961
-3,0=5766      -2,0=7632      -1,0=7649      0,0=7632       1,0=7649       2,0=7632       3,0=5779
-3,1=980       -2,1=6721      -1,1=7632      0,1=7649       1,1=7632       2,1=6724       3,1=969
-3,2=0         -2,2=980       -1,2=6721      0,2=7632       1,2=6724       2,2=969        3,2=0
-3,3=0         -2,3=0         -1,3=980       0,3=5796       1,3=969        2,3=0          3,3=0
---- 5 multiplier ----
-5,-5=0      -4,-5=0      -3,-5=0      -2,-5=0      -1,-5=985    0,-5=5749    1,-5=961     2,-5=0       3,-5=0       4,-5=0       5,-5=0
-5,-4=0      -4,-4=0      -3,-4=0      -2,-4=985    -1,-4=6694   0,-4=7632    1,-4=6704    2,-4=961     3,-4=0       4,-4=0       5,-4=0
-5,-3=0      -4,-3=0      -3,-3=985    -2,-3=6694   -1,-3=7632   0,-3=7649    1,-3=7632    2,-3=6704    3,-3=961     4,-3=0       5,-3=0
-5,-2=0      -4,-2=985    -3,-2=6694   -2,-2=7632   -1,-2=7649   0,-2=7632    1,-2=7649    2,-2=7632    3,-2=6704    4,-2=961     5,-2=0
-5,-1=985    -4,-1=6694   -3,-1=7632   -2,-1=7649   -1,-1=7632   0,-1=7649    1,-1=7632    2,-1=7649    3,-1=7632    4,-1=6704    5,-1=961
-5,0=5766    -4,0=7632    -3,0=7649    -2,0=7632    -1,0=7649    0,0=7632     1,0=7649     2,0=7632     3,0=7649     4,0=7632     5,0=5779
-5,1=980     -4,1=6721    -3,1=7632    -2,1=7649    -1,1=7632    0,1=7649     1,1=7632     2,1=7649     3,1=7632     4,1=6724     5,1=969
-5,2=0       -4,2=980     -3,2=6721    -2,2=7632    -1,2=7649    0,2=7632     1,2=7649     2,2=7632     3,2=6724     4,2=969      5,2=0
-5,3=0       -4,3=0       -3,3=980     -2,3=6721    -1,3=7632    0,3=7649     1,3=7632     2,3=6724     3,3=969      4,3=0        5,3=0
-5,4=0       -4,4=0       -3,4=0       -2,4=980     -1,4=6721    0,4=7632     1,4=6724     2,4=969      3,4=0        4,4=0        5,4=0
-5,5=0       -4,5=0       -3,5=0       -2,5=0       -1,5=980     0,5=5796     1,5=969      2,5=0        3,5=0        4,5=0        5,5=0
---- 7 multiplier ----
-7,-7=0        -6,-7=0        -5,-7=0        -4,-7=0        -3,-7=0        -2,-7=0        -1,-7=985      0,-7=5749      1,-7=961       2,-7=0         3,-7=0         4,-7=0         5,-7=0         6,-7=0         7,-7=0
-7,-6=0        -6,-6=0        -5,-6=0        -4,-6=0        -3,-6=0        -2,-6=985      -1,-6=6694     0,-6=7632      1,-6=6704      2,-6=961       3,-6=0         4,-6=0         5,-6=0         6,-6=0         7,-6=0
-7,-5=0        -6,-5=0        -5,-5=0        -4,-5=0        -3,-5=985      -2,-5=6694     -1,-5=7632     0,-5=7649      1,-5=7632      2,-5=6704      3,-5=961       4,-5=0         5,-5=0         6,-5=0         7,-5=0
-7,-4=0        -6,-4=0        -5,-4=0        -4,-4=985      -3,-4=6694     -2,-4=7632     -1,-4=7649     0,-4=7632      1,-4=7649      2,-4=7632      3,-4=6704      4,-4=961       5,-4=0         6,-4=0         7,-4=0
-7,-3=0        -6,-3=0        -5,-3=985      -4,-3=6694     -3,-3=7632     -2,-3=7649     -1,-3=7632     0,-3=7649      1,-3=7632      2,-3=7649      3,-3=7632      4,-3=6704      5,-3=961       6,-3=0         7,-3=0
-7,-2=0        -6,-2=985      -5,-2=6694     -4,-2=7632     -3,-2=7649     -2,-2=7632     -1,-2=7649     0,-2=7632      1,-2=7649      2,-2=7632      3,-2=7649      4,-2=7632      5,-2=6704      6,-2=961       7,-2=0
-7,-1=985      -6,-1=6694     -5,-1=7632     -4,-1=7649     -3,-1=7632     -2,-1=7649     -1,-1=7632     0,-1=7649      1,-1=7632      2,-1=7649      3,-1=7632      4,-1=7649      5,-1=7632      6,-1=6704      7,-1=961
-7,0=5766      -6,0=7632      -5,0=7649      -4,0=7632      -3,0=7649      -2,0=7632      -1,0=7649      0,0=7632       1,0=7649       2,0=7632       3,0=7649       4,0=7632       5,0=7649       6,0=7632       7,0=5779
-7,1=980       -6,1=6721      -5,1=7632      -4,1=7649      -3,1=7632      -2,1=7649      -1,1=7632      0,1=7649       1,1=7632       2,1=7649       3,1=7632       4,1=7649       5,1=7632       6,1=6724       7,1=969
-7,2=0         -6,2=980       -5,2=6721      -4,2=7632      -3,2=7649      -2,2=7632      -1,2=7649      0,2=7632       1,2=7649       2,2=7632       3,2=7649       4,2=7632       5,2=6724       6,2=969        7,2=0
-7,3=0         -6,3=0         -5,3=980       -4,3=6721      -3,3=7632      -2,3=7649      -1,3=7632      0,3=7649       1,3=7632       2,3=7649       3,3=7632       4,3=6724       5,3=969        6,3=0          7,3=0
-7,4=0         -6,4=0         -5,4=0         -4,4=980       -3,4=6721      -2,4=7632      -1,4=7649      0,4=7632       1,4=7649       2,4=7632       3,4=6724       4,4=969        5,4=0          6,4=0          7,4=0
-7,5=0         -6,5=0         -5,5=0         -4,5=0         -3,5=980       -2,5=6721      -1,5=7632      0,5=7649       1,5=7632       2,5=6724       3,5=969        4,5=0          5,5=0          6,5=0          7,5=0
-7,6=0         -6,6=0         -5,6=0         -4,6=0         -3,6=0         -2,6=980       -1,6=6721      0,6=7632       1,6=6724       2,6=969        3,6=0          4,6=0          5,6=0          6,6=0          7,6=0
-7,7=0         -6,7=0         -5,7=0         -4,7=0         -3,7=0         -2,7=0         -1,7=980       0,7=5796       1,7=969        2,7=0          3,7=0          4,7=0          5,7=0          6,7=0          7,7=0
 */

public class Day21Part2 {
    static final int MULTIPLIER = 7;
    static final int MAX_STEPS = 65+(131*MULTIPLIER)+1;

    static Garden garden;
    static Map<Integer, List<SearchState>> stateByCost = new HashMap<>();
    static Map<SearchState, Integer> visited = new HashMap<>();
    static int boardHalfWidth;
    static int boardHalfHeight;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day21.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            garden = Garden.parse(lines);
            System.out.println(garden);
            Coord start = garden.findStart();
            System.out.println("Start: " + start);

            System.out.println("Height: " + garden.height);
            System.out.println("Width: " + garden.width);
            int multiples = 26501365 / garden.width;
            System.out.println("Width multiples: " + multiples);
            int remainder = 26501365 % garden.width;
            System.out.println("Width mod: " + remainder);

            boardHalfWidth = garden.width / 2;
            boardHalfHeight = garden.height / 2;

            List<Coord> enclosed = garden.getEnclosed();
            System.out.println("Enclosed: " + enclosed);
            List<Coord> evenEnclosed = enclosed.stream().filter(Coord::isEven).collect(Collectors.toList());
            System.out.println("Even enclosed: " + evenEnclosed);
            List<Coord> oddEnclosed = enclosed.stream().filter(c -> !c.isEven()).collect(Collectors.toList());
            System.out.println("Odd enclosed: " + oddEnclosed);

            List<Coord> enclosedTopLeft = garden.getEnclosedWithinDistanceOf(new Coord(0, 0), remainder);
            System.out.println("Enclosed-TopLeft: " + enclosedTopLeft);
            List<Coord> enclosedTopRight = garden.getEnclosedWithinDistanceOf(new Coord(garden.width, 0), remainder);
            System.out.println("Enclosed-TopRight: " + enclosedTopRight);
            List<Coord> enclosedBottomLeft = garden.getEnclosedWithinDistanceOf(new Coord(0, garden.height), remainder);
            System.out.println("Enclosed-BottomLeft: " + enclosedBottomLeft);
            List<Coord> enclosedBottomRight = garden.getEnclosedWithinDistanceOf(new Coord(garden.width, garden.height), remainder);
            System.out.println("Enclosed-BottomRight: " + enclosedBottomRight);
            List<Coord> enclosedCentre = garden.getEnclosedWithinDistanceOf(start, remainder);
            System.out.println("Enclosed-Centre: " + enclosedCentre);

            List<Coord> everyCord = garden.getAll();

            long oddFullSize = everyCord.stream().filter(c -> !c.isEven()).filter(c -> !garden.isRock(c)).filter(c -> !garden.isSurrounded(c)).count();
            long evenFullSize = everyCord.stream().filter(Coord::isEven).filter(c -> !garden.isRock(c)).filter(c -> !garden.isSurrounded(c)).count();
            System.out.println("oddFullSize: " + oddFullSize);
            System.out.println("evenFullSize: " + evenFullSize);

            // 702028954414377 is too high
            // 351014477207188 is too low

            // should be a bit bigger than this number of boards:
            // (multiples * multiples) + multiples * 2


            // 65 is both the middle and the remainder. we therefore extend to the edge of the repeated board.
            // start coordinate is (odd,odd)
            // on odd steps, e.g. step 1, we discover coordinates that are (odd, even) and (even, odd)
            // odd steps fullBoardCells in coordinates that have odd x+y
            //
            // all internal boards will be (width*height)-enclosed with the odd/even removed
            //
            // edge boards need to count unenclosed for the triangle that's left, 3 of the 4 are empty. just the bottom left one has one
            // the internal boards are checkerboarded into odd & even boards!
            //
            // how many boards do we have?
            // full boards = 1 [centre odd]
            //               + (multiples - 1) * 2                  [middle row]
            //               + (multiples - 1) * 2                  [middle col]
            //               + (triangle(multiples - 2)) * 4        [4 quadrants]
            // --- these might not be 50/50 odd/even boards!
            long fullBoards = 1 + (multiples-1)*4 + triangle(multiples - 2) * 4;
            System.out.println("Full boards: " + fullBoards);

            long oddFullBoards = Math.round(fullBoards/2.0d);
            long evenFullBoards = fullBoards/2;
            System.out.println("oddFullBoards: " + oddFullBoards);
            System.out.println("evenFullBoards: " + evenFullBoards);

            BigInteger fullBoardCells = BigInteger.valueOf(oddFullBoards).multiply(BigInteger.valueOf(oddFullSize))
                    .add(BigInteger.valueOf(evenFullBoards).multiply(BigInteger.valueOf(evenFullSize)));
            System.out.println("Result from full boards: " + fullBoardCells);

            // partial boards
            long partialBoards = (multiples * 4);
            long oddPartialBoards = Math.round(partialBoards/2.0d) + 1;
            long evenPartialBoards = partialBoards/2 - 1;
            System.out.println("oddFullBoards: " + oddPartialBoards);
            System.out.println("evenFullBoards: " + evenPartialBoards);

            BigInteger partialBoardsCells = BigInteger.valueOf(oddPartialBoards).multiply(BigInteger.valueOf(oddFullSize))
                    .add(BigInteger.valueOf(evenPartialBoards).multiply(BigInteger.valueOf(evenFullSize)));
            System.out.println("Result from partial boards: " + partialBoardsCells);

            System.out.println("Result: " + fullBoardCells.add(partialBoardsCells));


            // partial corner boards =
            //                 top middle
            //               + left middle
            //               + right middle
            //               + bottom middle
            // -- these 4 each exclude 4 corner triangles, twice. so total excluded for these 4 is just 8
            // -- these are all odd boards
            // partial edge boards =
            //


            //               + (multiples - 2) * 4  [adjacent to middle row]
            //               ...
            //               + 1 *


            //System.exit(0);

            System.out.println("--------------------");

            long part2 = countDiscoverableCells(start);
            System.out.println(garden);

            System.out.println("Part 2: " + part2);
            // break down parts of board:

            System.out.println("In centre board: " + countInBoard(new Coord(65,65)));

            System.out.println("In left corner board: " + countInBoard(new Coord(65-131,65)));
            System.out.println("In right corner board: " + countInBoard(new Coord(65+131,65)));
            System.out.println("In top corner board: " + countInBoard(new Coord(65,65-131)));
            System.out.println("In bottom corner board: " + countInBoard(new Coord(65,65+131)));

            for (int by = -MULTIPLIER; by <= MULTIPLIER; by++) {
                for (int bx = -MULTIPLIER; bx <= MULTIPLIER; bx++) {
                    long bc = countInBoard(new Coord(65 + bx * 131, 65 + by * 131));
                    System.out.print(pad(String.format("%d,%d=%d", bx, by, bc), 15));
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }

    static String pad(String s, int len) {
        while (s.length()<len) s += " ";
        return s;
    }

    static long countInBoard(Coord centre) {
        return visited.entrySet().stream()
                .filter(e -> e.getValue() == MAX_STEPS)
                .filter(e -> inSameBoardAs(e.getKey().coord, centre))
                .count();
    }

    private static boolean inSameBoardAs(Coord coord, Coord centre) {
        return (Math.abs(coord.x - centre.x) <= boardHalfWidth)
                && (Math.abs(coord.y - centre.y) <= boardHalfHeight);
    }


    private static long triangle(long n) {
        return (n*n + n)/2;
    }

    private static long countDiscoverableCells(Coord start) {
        addState(new SearchState(start, 0), 0);
        while (!stateByCost.isEmpty()) {
            int currentCost = stateByCost.keySet().stream().min(Integer::compareTo).orElseThrow(() -> new RuntimeException("Failed to find current cost"));

            // prune older visited - not interesting
            visited = visited.entrySet().stream().filter(e -> e.getValue() == currentCost).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            List<SearchState> nextStates = stateByCost.remove(currentCost);
            System.out.println(currentCost + " has " + nextStates.size());
            for (SearchState nextState : nextStates) {
                addState(new SearchState(nextState.coord.north(), nextState.steps + 1), currentCost);
                addState(new SearchState(nextState.coord.south(), nextState.steps + 1), currentCost);
                addState(new SearchState(nextState.coord.east(), nextState.steps + 1), currentCost);
                addState(new SearchState(nextState.coord.west(), nextState.steps + 1), currentCost);
            }
        }
        System.out.println(visited.size());

        return visited.entrySet().stream().filter(e -> e.getValue() == MAX_STEPS).count();
    }

    private static boolean addState(SearchState state, int cost) {
        int newCost = cost + 1;
        if (garden.inBounds(state.coord) && garden.getCell(state.coord) != Cell.ROCK && newCost <= MAX_STEPS) {
            if (!visited.containsKey(state)) {
                visited.put(state, newCost);
                if (!stateByCost.containsKey(newCost)) stateByCost.put(newCost, new ArrayList<>());
                stateByCost.get(newCost).add(state);
                return true;
            }
        }
        return false;
    }

    @RequiredArgsConstructor
    @Data
    static class SearchState {
        final Coord coord;
        final int steps;
    }

    @RequiredArgsConstructor
    @Data
    static class Garden {
        private final Cell[][] cells;
        private final int width;
        private final int height;

        public static Garden parse(List<String> lines) {
            int height = lines.size();
            int width = lines.get(0).length();
            Cell[][] cells = new Cell[height][width];
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    cells[row][col] = Cell.parse(lines.get(row).charAt(col));
                }
            }
            return new Garden(cells, width, height);
        }

        public Coord findStart() {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    if (getCell(coord).equals(Cell.START)) return coord;
                }
            }
            throw new RuntimeException("Can't find start");
        }

        @Override
        public String toString() {
            Set<Coord> v = visited.entrySet().stream().filter(e -> e.getValue() == MAX_STEPS).map(e -> e.getKey().coord).collect(Collectors.toSet());

            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    if (v.contains(coord)) {
                        sb.append("O");
                    } else {
                        sb.append(getCell(coord).ch);
                    }
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        public Cell getCell(int col, int row) {
            return cells[(int) modInclNeg(row, height)][(int) modInclNeg(col, width)];
        }

        public Cell getCell(Coord coord) {
            return getCell(coord.x, coord.y);
        }

        boolean inBounds(Coord coord) {
            return true;
        }

        public List<Coord> getEnclosed() {
            return getAll().stream()
                    .filter(this::isSurrounded)
                    .collect(Collectors.toList());
        }

        public List<Coord> getEnclosedWithinDistanceOf(Coord origin, int distance) {
            return getAll().stream()
                    .filter(this::isSurrounded)
                    .filter(coord -> origin.distanceTo(coord) <= distance)
                    .collect(Collectors.toList());
        }

        public boolean isSurrounded(Coord coord) {
            return isRock(coord.north())
                    && isRock(coord.south())
                    && isRock(coord.east())
                    && isRock(coord.west());
        }

        public boolean isRock(Coord coord) {
            return getCell(coord) == Cell.ROCK;
        }

        public List<Coord> getAll() {
            List<Coord> result = new ArrayList<>();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    result.add(new Coord(col, row));
                }
            }
            return result;
        }
    }

    static long modInclNeg(long a, long b) {
        if (a >= 0) return a % b;
        else return (a + ((-a / b) + 1) * b) % b;
    }

    @RequiredArgsConstructor
    enum Cell {
        START('S'),
        PLOT('.'),
        ROCK('#');

        final char ch;

        public static Cell parse(char c) {
            switch (c) {
                case 'S':
                    return START;
                case '.':
                    return PLOT;
                case '#':
                    return ROCK;
                default:
                    throw new RuntimeException("Unknown cell " + c);
            }
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Coord {
        final int x;
        final int y;

        public Coord move(Direction direction) {
            switch (direction) {
                case NORTH:
                    return this.north();
                case EAST:
                    return this.east();
                case WEST:
                    return this.west();
                case SOUTH:
                    return this.south();
                default:
                    throw new RuntimeException("Unknown direction");
            }
        }

        public Coord east() {
            return new Coord(x + 1, y);
        }

        public Coord north() {
            return new Coord(x, y - 1);
        }

        public Coord south() {
            return new Coord(x, y + 1);
        }

        public Coord west() {
            return new Coord(x - 1, y);
        }

        public int distanceTo(Coord other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }

        public boolean isEven() {
            return (x + y) % 2 == 0;
        }
    }

    @RequiredArgsConstructor
    enum Direction {
        NORTH('^'),
        SOUTH('v'),
        EAST('>'),
        WEST('<');

        final char ch;
    }
}
