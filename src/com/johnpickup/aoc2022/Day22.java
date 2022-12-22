package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day22 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day22.txt"))) {
            long start = System.currentTimeMillis();
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            List<String> boardLines = lines.subList(0, lines.size() - 1);
            Board board = Board.parse(boardLines);
            board.display();

            Instructions instructions = Instructions.parse(lines.get(lines.size() - 1));
            System.out.println(instructions);

            // Part 1
            System.out.println("PART 1 --------");
            State initial = State.builder().position(board.startPosition()).direction(Direction.RIGHT).build();
            System.out.println("Initial: " + initial);

            State current = initial;
            for (Instruction instruction : instructions.instructions) {
                System.out.println(instruction);
                current = current.execute(instruction, board);
                System.out.println(current);
            }

            int result = current.position.row * 1000 + current.position.col * 4 + current.direction.facing;
            System.out.println("Result: " + result);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Builder
    static class Board {
        final int width;
        final int height;
        final Tile[][] tiles;

        static Board parse(List<String> lines) {
            // pad the board with null tiles to keep the coords one-based.
            int width = lines.stream().map(String::length).max(Integer::compare).get() + 1;
            int height = lines.size() + 1;
            Tile[][] tiles = new Tile[width][height];
            for (int col = 0; col < width; col++) {
                for (int row = 0; row < height; row++) {
                    tiles[col][row] = Tile.NULL;
                }
            }

            for (int row = 1; row < height; row++) {
                for (int col = 1; col < lines.get(row - 1).length() + 1; col++) {
                    tiles[col][row] = Tile.parse(lines.get(row - 1).charAt(col - 1));
                }
            }

            Board board = Board.builder()
                    .width(width)
                    .height(height)
                    .tiles(tiles)
                    .build();

            return board;
        }

        void display() {
            for (int row = 1; row < height; row++) {
                for (int col = 1; col < width; col++) {
                    System.out.print(tiles[col][row].symbol);
                }
                System.out.println();
            }
        }

        Coord startPosition() {
            for (int col = 0; col < width; col++) {
                if (tiles[col][1] == Tile.OPEN) return Coord.builder().col(col).row(1).build();
            }
            throw new RuntimeException("Can't find a start location");
        }

        public Coord move(Coord position, Direction direction) {
            switch (direction) {
                case UP:
                    return moveUp(position);
                case DOWN:
                    return moveDown(position);
                case LEFT:
                    return moveLeft(position);
                case RIGHT:
                    return moveRight(position);
                default:
                    throw new RuntimeException("Unknown direction " + direction);
            }
        }

        private Coord moveUp(Coord position) {
            int row = (position.row - 1) % height;
            if (row < 0) row = height - 1;
            int col = position.col;

            while (tiles[col][row] == Tile.NULL) {
                row = (row - 1) % height;
                if (row < 0) row = height - 1;
            }

            if (tiles[col][row] == Tile.OPEN) {
                return Coord.builder()
                        .col(col)
                        .row(row)
                        .build();
            } else {
                // can't move
                return position;
            }
        }

        private Coord moveDown(Coord position) {
            int row = (position.row + 1) % height;
            int col = position.col;

            while (tiles[col][row] == Tile.NULL) {
                row = (row + 1) % height;
            }

            if (tiles[col][row] == Tile.OPEN) {
                return Coord.builder()
                        .col(col)
                        .row(row)
                        .build();
            } else {
                // can't move
                return position;
            }
        }

        private Coord moveLeft(Coord position) {
            int row = position.row;
            int col = position.col - 1;
            if (col < 0) col = width - 1;

            while (tiles[col][row] == Tile.NULL) {
                col = col - 1;
                if (col < 0) col = width - 1;
            }

            if (tiles[col][row] == Tile.OPEN) {
                return Coord.builder()
                        .col(col)
                        .row(row)
                        .build();
            } else {
                // can't move
                return position;
            }
        }

        private Coord moveRight(Coord position) {
            int row = position.row;
            int col = (position.col + 1) % width;

            while (tiles[col][row] == Tile.NULL) {
                col = (col + 1) % width;
            }

            if (tiles[col][row] == Tile.OPEN) {
                return Coord.builder()
                        .col(col)
                        .row(row)
                        .build();
            } else {
                // can't move
                return position;
            }
        }
    }

    @Builder
    static class State {
        final Coord position;
        final Direction direction;

        public State execute(Instruction instruction, Board board) {
            return instruction.execute(this, board);
        }
    }

    @RequiredArgsConstructor
    enum Direction {
        UP('^', 3),
        DOWN('v', 1),
        LEFT('<', 2),
        RIGHT('>', 0);
        final char symbol;
        final int facing;

        Direction rotate(Turn t) {
            switch (this) {
                case UP:
                    return t == Turn.LEFT ? LEFT : RIGHT;
                case DOWN:
                    return t == Turn.LEFT ? RIGHT : LEFT;
                case RIGHT:
                    return t == Turn.LEFT ? UP : DOWN;
                case LEFT:
                    return t == Turn.LEFT ? DOWN : UP;
                default:
                    throw new RuntimeException("Unknown direction " + this);
            }
        }
    }

    @RequiredArgsConstructor
    enum Turn {
        LEFT('L'),
        RIGHT('R');
        final char symbol;

        public static Turn parse(char c) {
            switch (c) {
                case 'L':
                    return LEFT;
                case 'R':
                    return RIGHT;
                default:
                    throw new RuntimeException("Unknown turn char " + c);
            }
        }
    }

    @RequiredArgsConstructor
    enum Tile {
        NULL(' '),
        OPEN('.'),
        WALL('#');
        final char symbol;

        static Tile parse(char c) {
            switch (c) {
                case ' ':
                    return NULL;
                case '.':
                    return OPEN;
                case '#':
                    return WALL;
                default:
                    throw new RuntimeException("Unknown tile char " + c);
            }
        }
    }

    @ToString
    @Builder
    @EqualsAndHashCode
    static class Coord {
        int col;
        int row;
    }

    @ToString
    static class Instructions {
        final List<Instruction> instructions = new ArrayList<>();

        static Instructions parse(String s) {
            Instructions result = new Instructions();
            String buff = "";
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == 'L' || s.charAt(i) == 'R') {
                    if (!buff.isEmpty()) {
                        result.instructions.add(Instruction.parse(buff));
                        buff = "";
                    }
                    result.instructions.add(Instruction.parse(s.substring(i, i + 1)));
                } else {
                    buff = buff + s.charAt(i);
                }
            }
            if (!buff.isEmpty()) {
                result.instructions.add(Instruction.parse(buff));
                buff = "";
            }

            return result;
        }
    }

    abstract static class Instruction {
        static Instruction parse(String s) {
            switch (s) {
                case "R":
                case "L":
                    return Rotate.parse(s);
                default:
                    return Move.parse(s);
            }
        }

        public abstract State execute(State state, Board board);
    }

    @Builder
    @ToString
    @EqualsAndHashCode(callSuper = false)
    static class Rotate extends Instruction {
        final Turn direction;

        static Instruction parse(String s) {
            return Rotate.builder().direction(Turn.parse(s.charAt(0))).build();
        }

        @Override
        public State execute(State state, Board board) {
            return State.builder()
                    .position(state.position)
                    .direction(state.direction.rotate(this.direction))
                    .build();
        }
    }


    @Builder
    @ToString
    @EqualsAndHashCode(callSuper = false)
    static class Move extends Instruction {
        final int distance;

        static Instruction parse(String s) {
            return Move.builder().distance(Integer.parseInt(s)).build();
        }

        @Override
        public State execute(State state, Board board) {
            State result = state;
            for (int i = 0; i < this.distance; i++) {
                result = State.builder()
                        .direction(result.direction)
                        .position(board.move(result.position, result.direction))
                        .build();
            }
            return result;
        }
    }

}
