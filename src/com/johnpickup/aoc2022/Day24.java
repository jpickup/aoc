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


public class Day24 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day24.txt"))) {
            long start = System.currentTimeMillis();
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Valley valley = Valley.parse(lines);

            List<Move> moves = findMinimumMoves(valley, valley.entry, Collections.emptyList(), 0);
            System.out.println("Minimum moves: " + moves.size());

//            for (int i = 0; i < 10; i++) {
//                System.out.println(i);
//                valley.display();
//                valley = valley.processBlizzards();
//            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Move> findMinimumMoves(Valley valley, Coord position, List<Move> moves, int time) {
        if (position.equals(valley.exit)) return moves;   // found it

        if (moves.size() > (valley.width + valley.height) * 2) return null;       // too long - give up

        Valley newValley = valley.processBlizzards(time);

        Map<Move, Coord> allMoves = new TreeMap<>();
        allMoves.put(Move.STAY, position); // do nothing
        allMoves.put(Move.LEFT, Coord.builder().x(position.x-1).y(position.y).build());
        allMoves.put(Move.RIGHT, Coord.builder().x(position.x+1).y(position.y).build());
        allMoves.put(Move.UP, Coord.builder().x(position.x).y(position.y-1).build());
        allMoves.put(Move.DOWN, Coord.builder().x(position.x).y(position.y+1).build());

        Map<Move, Coord> possibleMoves = allMoves.entrySet().stream()
                .filter(e -> newValley.isValid(e.getValue()))
                .filter(e -> newValley.findBlizzardsAt(e.getValue()).isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (possibleMoves.isEmpty()) return null;

        // do we have a solution below?
        Optional<Map.Entry<Move, Coord>> solution = possibleMoves.entrySet().stream().filter(e -> e.getValue().equals(newValley.exit)).findFirst();
        if (solution.isPresent()) {
            List<Move> newMoves = new ArrayList<>(moves);
            newMoves.add(solution.get().getKey());
            return newMoves;
        }

        List<Move> bestMoves = null;
        for (Map.Entry<Move, Coord> possibleMove : possibleMoves.entrySet()) {
            List<Move> newMoves = new ArrayList<>(moves);
            newMoves.add(possibleMove.getKey());
            List<Move> potentialMoves = findMinimumMoves(newValley, possibleMove.getValue(), newMoves, time+1);
            if (potentialMoves != null && (bestMoves == null || potentialMoves.size() < bestMoves.size())) {
                bestMoves = potentialMoves;
            }
        }

        return bestMoves;
    }

    @Builder
    static class Valley {
        final int width;
        final int height;
        final Coord entry;
        final Coord exit;
        final List<Blizzard> blizzards;
        static final Map<Integer, List<Blizzard>> blizzardsByTime = new TreeMap<>();

        public static Valley parse(List<String> lines) {
            return Valley.builder()
                    .width(lines.get(0).length() - 2)
                    .height(lines.size() - 2)
                    .entry(Coord.builder()
                            .y(-1)
                            .x(lines.get(0).indexOf('.') - 1)
                            .build())
                    .exit(Coord.builder()
                            .y(lines.size() - 2)
                            .x(lines.get(lines.size() - 1).indexOf('.') - 1)
                            .build())
                    .blizzards(parseBlizzards(lines))
                    .build();
        }

        boolean isValid(Coord c) {
            return c.equals(exit) || ((c.x >= 0) && (c.x < width) && (c.y >= 0) && (c.y < height));
        }

        void display() {
            for (int x = 0; x < width + 2; x++) {
                System.out.print((entry.x == x - 1) ? '.' : '#');
            }
            System.out.println();
            for (int y = 0; y < height; y++) {
                System.out.print('#');
                for (int x = 0; x < width; x++) {
                    List<Blizzard> blizzardsAt = findBlizzardsAt(Coord.builder().x(x).y(y).build());
                    if (blizzardsAt.isEmpty()) System.out.print('.');
                    else if (blizzardsAt.size() == 1) System.out.print(blizzardsAt.get(0).direction.symbol);
                    else System.out.print(blizzardsAt.size());
                }
                System.out.println('#');
            }
            for (int x = 0; x < width + 2; x++) {
                System.out.print((exit.x == x - 1) ? '.' : '#');
            }
            System.out.println();
        }

        List<Blizzard> findBlizzardsAt(Coord coord) {
            return blizzards.stream().filter(b -> b.coord.equals(coord)).collect(Collectors.toList());
        }

        private static List<Blizzard> parseBlizzards(List<String> lines) {
            List<Blizzard> blizzards = new ArrayList<>();
            for (int y = 1; y < lines.size() - 1; y++) {
                String line = lines.get(y);
                for (int x = 1; x < line.length() - 1; x++) {
                    if (line.charAt(x) != '.') {
                        blizzards.add(Blizzard.builder()
                                .coord(Coord.builder().x(x-1).y(y-1).build())
                                .direction(Direction.parse(line.charAt(x)))
                                .build());
                    }
                }
            }
            return blizzards;
        }

        public Valley processBlizzards(int time) {
            if (!blizzardsByTime.containsKey(time)) {
                List<Blizzard> updatedBlizzards = blizzards.stream().map(b -> b.move(this)).collect(Collectors.toList());
                blizzardsByTime.put(time, updatedBlizzards);
            }

            return Valley.builder()
                    .width(this.width)
                    .height(this.height)
                    .entry(this.entry)
                    .exit(this.exit)
                    .blizzards(blizzardsByTime.get(time))
                    .build();
        }
    }

    @Builder
    @EqualsAndHashCode
    @ToString
    static class Blizzard {
        final Coord coord;
        final Direction direction;

        public Blizzard move(Valley valley) {
            return Blizzard.builder()
                    .coord(coord.move(direction, valley.width, valley.height))
                    .direction(this.direction)
                    .build();
        }
    }

    @ToString
    @Builder
    @EqualsAndHashCode
    static class Coord {
        final int x;
        final int y;

        public Coord move(Direction direction, int width, int height) {
            int newX = x, newY = y;
            switch (direction) {
                case LEFT:
                    newX = newX - 1;
                    if (newX < 0) newX = width - 1;
                    break;
                case RIGHT:
                    newX = x + 1;
                    if (newX >= width) newX = 0;
                    break;
                case UP:
                    newY = newY - 1;
                    if (newY < 0) newY = height - 1;
                    break;
                case DOWN:
                    newY = newY + 1;
                    if (newY >= height) newY = 0;
                    break;
                default:
                    throw new RuntimeException("Unknown direction " + direction);
            }
            return Coord.builder()
                    .x(newX)
                    .y(newY)
                    .build();
        }
    }

    @RequiredArgsConstructor
    enum Direction {
        UP('^'),
        DOWN('v'),
        LEFT('<'),
        RIGHT('>');
        final char symbol;

        public static Direction parse(char c) {
            switch (c) {
                case '^':
                    return UP;
                case 'v':
                    return DOWN;
                case '<':
                    return LEFT;
                case '>':
                    return RIGHT;
                default:
                    throw new RuntimeException("Unknown direction " + c);
            }
        }
    }

    enum Move {
        UP, DOWN, LEFT, RIGHT, STAY
    }


}
