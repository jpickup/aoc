package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.CharGrid;
import com.johnpickup.aoc2024.util.Coord;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 {
    final static char SPACE = '.';
    final static char WALL = '#';
    final static char GOODS = 'O';
    final static char GOODS_LEFT = '[';
    final static char GOODS_RIGHT = ']';
    final static char ROBOT = '@';

    public static void main(String[] args) {
        String day = new Object() {
        }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                //prefix + "-tiny.txt"
                //prefix + "-tiny2.txt"
                prefix + "-test.txt"
                ,prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .collect(Collectors.toList());

                Grid grid = null;
                List<String> block = new ArrayList<>();
                for (String line : lines) {
                    if (line.isEmpty() && grid == null) {
                        grid = new Grid(block);
                        block.clear();
                    } else {
                        block.add(line);
                    }
                }
                Instructions instructions = new Instructions(block);

                // copy before it gets mutated
                Grid grid2 = grid.expand();

                instructions.apply(grid);
                long part1 = grid.calcSum();
                System.out.println("Part 1: " + part1);

                instructions.apply(grid2);
                long part2 = grid2.calcSum();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Grid extends CharGrid {
        Coord robot;

        public Grid(List<String> lines) {
            super(lines);
            robot = findRobot();
        }

        public Grid(int width, int height, char[][] cells) {
            super(width, height, cells);
        }


        Coord findRobot() {
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    Coord c = new Coord(x, y);
                    if (getCell(c) == ROBOT) {
                        setCell(c, SPACE);
                        return c;
                    }
                }
            }
            throw new RuntimeException("Robot not found");
        }

        List<Coord> findGoods() {
            List<Coord> result = new ArrayList<>();
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    Coord c = new Coord(x, y);
                    if (getCell(c) == GOODS || getCell(c) == GOODS_LEFT) {
                        result.add(c);
                    }
                }
            }
            return result;
        }


        public void applyInstruction(Direction d) {
            rearrangeGrid(robot, d);
        }

        private Coord applyDirection(Coord coord, Direction d) {
            switch (d) {
                case NORTH:
                    return coord.north();
                case SOUTH:
                    return coord.south();
                case EAST:
                    return coord.east();
                case WEST:
                    return coord.west();
                default:
                    throw new RuntimeException("Unknown direction " + d);
            }
        }

        private void rearrangeGrid(Coord coord, Direction direction) {
            Optional<Set<Coord>> toMove = goodsToMove(coord, direction);
            if (toMove.isPresent()) {
                moveGoods(toMove.get(), direction);
                robot = applyDirection(coord, direction);
            }
        }

        private void moveGoods(Set<Coord> coords, Direction direction) {
            char[][] oldCells = new char[getWidth()][getHeight()];
            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    oldCells[x][y] = getCell(new Coord(x, y));
                }
            }
            Set<Coord> newCoords = coords.stream().map(c -> applyDirection(c, direction)).collect(Collectors.toSet());
            // copy contents of all moving locations to their new location
            for (Coord coord : coords) {
                Coord newCoord = applyDirection(coord, direction);
                setCell(newCoord, oldCells[coord.getX()][coord.getY()]);
            }
            coords.removeAll(newCoords);    // these will be empty afterwards
            for (Coord coord : coords) {
                setCell(coord, SPACE);
            }
        }

        private Optional<Set<Coord>> goodsToMove(Coord location, Direction direction) {
            Coord target = applyDirection(location, direction);
            boolean northOrSouth = direction == Direction.NORTH || direction == Direction.SOUTH;

            switch (getCell(target)) {
                case SPACE:
                    return Optional.of(Collections.emptySet());
                case WALL:
                    return Optional.empty();
                case GOODS:
                    return goodsToMove(target, direction).map(g -> setUnion(g, Collections.singleton(target)));
                case GOODS_LEFT: {
                    Optional<Set<Coord>> leftGoods = goodsToMove(target, direction);
                    Optional<Set<Coord>> rightGoods = northOrSouth ? goodsToMove(target.east(), direction) : Optional.of(Collections.emptySet());
                    if (leftGoods.isPresent() && rightGoods.isPresent()) {
                        Set<Coord> result = new HashSet<>();
                        result.add(target);
                        result.add(target.east());
                        result.addAll(leftGoods.get());
                        result.addAll(rightGoods.get());
                        return Optional.of(result);
                    } else {
                        return Optional.empty();
                    }
                }
                case GOODS_RIGHT: {
                    Optional<Set<Coord>> leftGoods = northOrSouth ? goodsToMove(target.west(), direction) : Optional.of(Collections.emptySet());
                    Optional<Set<Coord>> rightGoods = goodsToMove(target, direction);
                    if (leftGoods.isPresent() && rightGoods.isPresent()) {
                        Set<Coord> result = new HashSet<>();
                        result.add(target);
                        result.add(target.west());
                        result.addAll(leftGoods.get());
                        result.addAll(rightGoods.get());
                        return Optional.of(result);
                    } else {
                        return Optional.empty();
                    }
                }
                default: throw new RuntimeException("Unknown cell" + getCell(location) + " at " + location);
            }
        }

        private static Set<Coord> setUnion(Set<Coord> s1, Set<Coord> s2) {
            Set<Coord> result = new HashSet<>();
            result.addAll(s1);
            result.addAll(s2);
            return result;
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    Coord coord = new Coord(x, y);
                    if (coord.equals(robot)) {
                        sb.appendCodePoint(ROBOT);
                    } else {
                        sb.appendCodePoint(getCell(coord));
                    }
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        public long calcSum() {
            List<Coord> goods = findGoods();
            return goods.stream().map(this::gpsCoord).reduce(0L, Long::sum);
        }

        private long gpsCoord(Coord c) {
            return c.getY() * 100L + c.getX();
        }

        public Grid expand() {
            int newWidth = getWidth() * 2;
            Grid result = new Grid(newWidth, getHeight(), new char[newWidth][getHeight()]);

            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    Coord coord = new Coord(x, y);
                    Coord extra = new Coord(x * 2, y);
                    Coord extra2 = new Coord(x * 2 + 1, y);
                    switch (getCell(coord)) {
                        case SPACE:
                            result.setCell(extra, SPACE);
                            result.setCell(extra2, SPACE);
                            break;
                        case WALL:
                            result.setCell(extra, WALL);
                            result.setCell(extra2, WALL);
                            break;
                        case GOODS:
                            result.setCell(extra, GOODS_LEFT);
                            result.setCell(extra2, GOODS_RIGHT);
                            break;
                        default:
                            throw new RuntimeException("Unknown cell " + getCell(coord));
                    }
                }
            }
            result.robot = new Coord(robot.getX() * 2, robot.getY());
            return result;
        }
    }

    static class Instructions {
        List<Direction> instructions = new ArrayList<>();

        public Instructions(List<String> lines) {
            for (String line : lines) {
                for (int i = 0; i < line.length(); i++) {
                    instructions.add(Direction.parse(line.charAt(i)));
                }
            }
        }

        public void apply(Grid grid) {
            for (Direction instruction : instructions) {
//                System.out.println(instruction);
                grid.applyInstruction(instruction);
//                System.out.println(grid);
            }

        }
    }

    @RequiredArgsConstructor
    enum Direction {
        NORTH('^'),
        SOUTH('v'),
        EAST('>'),
        WEST('<');
        final char ch;

        static Direction parse(char c) {
            switch (c) {
                case '^':
                    return NORTH;
                case 'v':
                    return SOUTH;
                case '>':
                    return EAST;
                case '<':
                    return WEST;
                default:
                    throw new RuntimeException("Unknown direction " + c);
            }
        }

        @Override
        public String toString() {
            return "" + ch;
        }

    }
}
