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
    static final boolean test = false;
    static final List<State> states = new ArrayList<>();
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day22.txt"))) {
            long start = System.currentTimeMillis();
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            List<String> boardLines = lines.subList(0, lines.size() - 1);
            Board board = Board.parse(boardLines);
            board.display();

            //testBoard(board);

            Instructions instructions = Instructions.parse(lines.get(lines.size() - 1));
            System.out.println(instructions);

            State initial = State.builder().position(board.startPosition()).direction(Direction.RIGHT).build();
            System.out.println("Initial: " + initial);
            states.add(initial);
            //board.display();

            int step = 1;
            State current = initial;
            for (Instruction instruction : instructions.instructions) {
                System.out.println("Step: " + step++ + " - " + instruction);
                current = current.execute(instruction, board);
                System.out.println(current);
            }
            board.display();

            // Part 2
            // 175006 too high
            // 25365 too low
            // 146011

            int result = current.position.row * 1000 + current.position.col * 4 + current.direction.facing;
            System.out.println("Result: " + result);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tests for a blank cube only
    private static void testBoard(Board board) {
        // simple
        testMove(board,
                State.builder().position(Coord.builder().col(75).row(75).build()).direction(Direction.UP).build(),
                State.builder().position(Coord.builder().col(75).row(74).build()).direction(Direction.UP).build());
        testMove(board,
                State.builder().position(Coord.builder().col(75).row(75).build()).direction(Direction.DOWN).build(),
                State.builder().position(Coord.builder().col(75).row(76).build()).direction(Direction.DOWN).build());
        testMove(board,
                State.builder().position(Coord.builder().col(75).row(75).build()).direction(Direction.LEFT).build(),
                State.builder().position(Coord.builder().col(74).row(75).build()).direction(Direction.LEFT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(75).row(75).build()).direction(Direction.RIGHT).build(),
                State.builder().position(Coord.builder().col(76).row(75).build()).direction(Direction.RIGHT).build());
        // edges
        //face 1
        testMove(board,
                State.builder().position(Coord.builder().col(51).row(1).build()).direction(Direction.UP).build(),
                State.builder().position(Coord.builder().col(1).row(151).build()).direction(Direction.RIGHT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(100).row(1).build()).direction(Direction.UP).build(),
                State.builder().position(Coord.builder().col(1).row(200).build()).direction(Direction.RIGHT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(51).row(1).build()).direction(Direction.LEFT).build(),
                State.builder().position(Coord.builder().col(1).row(150).build()).direction(Direction.RIGHT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(51).row(50).build()).direction(Direction.LEFT).build(),
                State.builder().position(Coord.builder().col(1).row(101).build()).direction(Direction.RIGHT).build());
        // face 2
        testMove(board,
                State.builder().position(Coord.builder().col(101).row(1).build()).direction(Direction.UP).build(),
                State.builder().position(Coord.builder().col(1).row(200).build()).direction(Direction.UP).build());
        testMove(board,
                State.builder().position(Coord.builder().col(150).row(1).build()).direction(Direction.UP).build(),
                State.builder().position(Coord.builder().col(50).row(200).build()).direction(Direction.UP).build());
        testMove(board,
                State.builder().position(Coord.builder().col(150).row(1).build()).direction(Direction.RIGHT).build(),
                State.builder().position(Coord.builder().col(100).row(150).build()).direction(Direction.LEFT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(150).row(50).build()).direction(Direction.RIGHT).build(),
                State.builder().position(Coord.builder().col(100).row(101).build()).direction(Direction.LEFT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(101).row(50).build()).direction(Direction.DOWN).build(),
                State.builder().position(Coord.builder().col(100).row(51).build()).direction(Direction.LEFT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(150).row(50).build()).direction(Direction.DOWN).build(),
                State.builder().position(Coord.builder().col(100).row(100).build()).direction(Direction.LEFT).build());
        //face 3
        testMove(board,
                State.builder().position(Coord.builder().col(51).row(51).build()).direction(Direction.LEFT).build(),
                State.builder().position(Coord.builder().col(1).row(101).build()).direction(Direction.DOWN).build());
        testMove(board,
                State.builder().position(Coord.builder().col(51).row(100).build()).direction(Direction.LEFT).build(),
                State.builder().position(Coord.builder().col(50).row(101).build()).direction(Direction.DOWN).build());
        testMove(board,
                State.builder().position(Coord.builder().col(100).row(51).build()).direction(Direction.RIGHT).build(),
                State.builder().position(Coord.builder().col(101).row(50).build()).direction(Direction.UP).build());
        testMove(board,
                State.builder().position(Coord.builder().col(100).row(100).build()).direction(Direction.RIGHT).build(),
                State.builder().position(Coord.builder().col(150).row(50).build()).direction(Direction.UP).build());
        //face 4
        testMove(board,
                State.builder().position(Coord.builder().col(1).row(101).build()).direction(Direction.UP).build(),
                State.builder().position(Coord.builder().col(51).row(51).build()).direction(Direction.RIGHT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(50).row(101).build()).direction(Direction.UP).build(),
                State.builder().position(Coord.builder().col(51).row(100).build()).direction(Direction.RIGHT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(1).row(101).build()).direction(Direction.LEFT).build(),
                State.builder().position(Coord.builder().col(51).row(50).build()).direction(Direction.RIGHT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(1).row(150).build()).direction(Direction.LEFT).build(),
                State.builder().position(Coord.builder().col(51).row(1).build()).direction(Direction.RIGHT).build());
        //face 5
        testMove(board,
                State.builder().position(Coord.builder().col(100).row(101).build()).direction(Direction.RIGHT).build(),
                State.builder().position(Coord.builder().col(150).row(50).build()).direction(Direction.LEFT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(100).row(150).build()).direction(Direction.RIGHT).build(),
                State.builder().position(Coord.builder().col(150).row(1).build()).direction(Direction.LEFT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(51).row(150).build()).direction(Direction.DOWN).build(),
                State.builder().position(Coord.builder().col(50).row(151).build()).direction(Direction.LEFT).build());
        testMove(board,
                State.builder().position(Coord.builder().col(100).row(150).build()).direction(Direction.DOWN).build(),
                State.builder().position(Coord.builder().col(50).row(200).build()).direction(Direction.LEFT).build());
        // face 6
        testMove(board,
                State.builder().position(Coord.builder().col(1).row(151).build()).direction(Direction.LEFT).build(),
                State.builder().position(Coord.builder().col(51).row(1).build()).direction(Direction.DOWN).build());
        testMove(board,
                State.builder().position(Coord.builder().col(1).row(200).build()).direction(Direction.LEFT).build(),
                State.builder().position(Coord.builder().col(100).row(1).build()).direction(Direction.DOWN).build());
        testMove(board,
                State.builder().position(Coord.builder().col(1).row(200).build()).direction(Direction.DOWN).build(),
                State.builder().position(Coord.builder().col(101).row(1).build()).direction(Direction.DOWN).build());
        testMove(board,
                State.builder().position(Coord.builder().col(50).row(200).build()).direction(Direction.DOWN).build(),
                State.builder().position(Coord.builder().col(150).row(1).build()).direction(Direction.DOWN).build());
        testMove(board,
                State.builder().position(Coord.builder().col(50).row(151).build()).direction(Direction.RIGHT).build(),
                State.builder().position(Coord.builder().col(51).row(150).build()).direction(Direction.UP).build());
        testMove(board,
                State.builder().position(Coord.builder().col(50).row(200).build()).direction(Direction.RIGHT).build(),
                State.builder().position(Coord.builder().col(100).row(150).build()).direction(Direction.UP).build());
    }

    private static void testMove(Board board, State from, State expected) {
        State actual = board.move(from);
        if (!expected.equals(actual)) {
            throw new RuntimeException("Test move failed " + actual + " is not " + expected);
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
                    State foundState = null;
                    for (State state : states) {
                        if ((state.position.col == col) && (state.position.row == row)) {
                            foundState = state;
                        }
                    }

                    if (foundState != null) {
                        System.out.print(foundState.direction.symbol);
                    }
                    else {
                        System.out.print(tiles[col][row].symbol);
                    }
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

        public State move(State state) {
            switch (state.direction) {
                case UP:
                    return moveUp(state);
                case DOWN:
                    return moveDown(state);
                case LEFT:
                    return moveLeft(state);
                case RIGHT:
                    return moveRight(state);
                default:
                    throw new RuntimeException("Unknown direction " + state.direction);
            }
        }

        private State moveUp(State state) {
            Direction direction = state.direction;
            int row = (state.position.row - 1);
            int col = state.position.col;
            if (test) {
                if (row == 0) {
                    // 1 to 2
                    direction = Direction.DOWN;
                    col = 1 + (12 - col);
                    row = 5;
                }
                else if ((row == 4) && (col >= 1) && (col <= 4)) {
                    // 2 to 1
                    direction = Direction.DOWN;
                    col = 9 + (4 - col);
                    row = 1;
                }
                else if ((row == 4) && (col >= 5) && (col <= 8)) {
                    // 3 to 1
                    direction = Direction.RIGHT;
                    row = 1 + (col - 5);
                    col = 9;
                }
                else if ((row == 8) && (col >= 13) && (col <= 16)) {
                    // 6 to 4
                    direction = Direction.LEFT;
                    row = 5 + (16 - col);
                    col = 12;
                }
            } else {
                if ((row == 0) && (col >= 51) && (col <= 100)) {
                    // 1 to 6
                    direction = Direction.RIGHT;
                    row = 151 + (col - 51);
                    col = 1;
                }
                else if ((row == 0) && (col >= 101) && (col <= 150)) {
                    // 2 to 6
                    direction = Direction.UP;
                    col = 1 + (col - 101);
                    row = 200;
                }
                else if ((row == 100) && (col >= 1) && (col <= 50)) {
                    // 4 to 3
                    direction = Direction.RIGHT;
                    row = 51 + (col - 1);
                    col = 51;
                }
            }

            if (tiles[col][row] == Tile.OPEN) {
                return State.builder()
                        .position(
                                Coord.builder()
                                        .col(col)
                                        .row(row)
                                        .build())
                        .direction(direction)
                        .build();
            } else {
                // can't move
                return state;
            }
        }

        private State moveDown(State state) {
            Direction direction = state.direction;
            int row = (state.position.row + 1);
            int col = state.position.col;

            if (test) {
                if ((row == 9) && (col >= 1) && (col <= 4)) {
                    // 2 to 5
                    direction = Direction.UP;
                    col = 9 + (4 - col);
                    row = 12;
                }
                else if ((row == 9) && (col >= 5) && (col <= 8)) {
                    // 3 to 5
                    direction = Direction.RIGHT;
                    row = 9 + (8 - col);
                    col = 9;
                }
                else if ((row == 13) && (col >= 9) && (col <= 12)) {
                    // 5 to 2
                    direction = Direction.UP;
                    col = 1 + (12 - col);
                    row = 8;
                }
                else if ((row == 13) && (col >= 13) && (col <= 16)) {
                    // 6 to 2
                    direction = Direction.RIGHT;
                    row = 5 + (16 - col);
                    col = 1;
                }
            }
            else {
                if ((row == 51) && (col >= 101) && (col <= 150)) {
                    // 2 to 3
                    direction = Direction.LEFT;
                    row = 51 + (col - 101);
                    col = 100;
                }
                else if ((row == 151) && (col >= 51) && (col <= 100)) {
                    // 5 to 6
                    direction = Direction.LEFT;
                    row = 151 + (col - 51);
                    col = 50;
                }
                else if ((row == 201) && (col >= 1) && (col <= 50)) {
                    // 6 to 2
                    direction = Direction.DOWN;
                    col = 101 + (col - 1);
                    row = 1;
                }
            }

            if (tiles[col][row] == Tile.OPEN) {
                return State.builder()
                        .position(
                                Coord.builder()
                                        .col(col)
                                        .row(row)
                                        .build())
                        .direction(direction)
                        .build();
            } else {
                // can't move
                return state;
            }
        }

        private State moveLeft(State state) {
            Direction direction = state.direction;
            int row = state.position.row;
            int col = state.position.col - 1;

            if (test) {
                if ((col == 8) && (row >= 1) && (row <= 4)) {
                    // 1 to 3
                    direction = Direction.DOWN;
                    col = 5 + (4 - row);
                    row = 5;
                }
                else if ((col == 0) && (row >= 5) && (row <= 8)) {
                    // 2 to 6
                    direction = Direction.UP;
                    col = 13 + (8 - row);
                    row = 12;
                }
                else if ((col == 8) && (row >= 9) && (row <= 12)) {
                    // 5 to 3
                    direction = Direction.UP;
                    col = 5 + (12 - row);
                    row = 8;
                }
            }
            else {
                if ((col == 50) && (row >= 1) && (row <= 50)) {
                    // 1 to 4
                    direction = Direction.RIGHT;
                    row = 101 + (50 - row);
                    col = 1;
                }
                else if ((col == 50) && (row >= 51) && (row <= 100)) {
                    // 3 to 4
                    direction = Direction.DOWN;
                    col = 1 + (row - 51);
                    row = 101;
                }
                else if ((col == 0) && (row >= 101) && (row <= 150)) {
                    // 4 to 1
                    direction = Direction.RIGHT;
                    row = 1 + (150 - row);
                    col = 51;
                }
                else if ((col == 0) && (row >= 151) && (row <= 200)) {
                    // 6 to 1
                    direction = Direction.DOWN;
                    col = 51 + (row - 151);
                    row = 1;
                }
            }

            if (tiles[col][row] == Tile.OPEN) {
                return State.builder()
                        .position(
                                Coord.builder()
                                        .col(col)
                                        .row(row)
                                        .build())
                        .direction(direction)
                        .build();
            } else {
                // can't move
                return state;
            }
        }

        private State moveRight(State state) {
            Direction direction = state.direction;
            int row = state.position.row;
            int col = state.position.col + 1;

            if (test) {
                if ((col == 13) && (row >= 1) && (row <= 4)) {
                    // 1 to 6
                    direction = Direction.LEFT;
                    row = 9 + (4 - row);
                    col = 16;
                }
                else if ((col == 13) && (row >= 5) && (row <= 8)) {
                    // 4 to 6
                    direction = Direction.DOWN;
                    col = 13 + (8 - row);
                    row = 9;
                }
                else if ((col == 17) && (row >= 9) && (row <= 12)) {
                    // 6 to 1
                    direction = Direction.LEFT;
                    row = 1 + (12 - row);
                    col = 12;
                }
            }
            else {
                if ((col == 151) && (row >= 1) && (row <= 50)) {
                    // 2 to 5
                    direction = Direction.LEFT;
                    row = 101 + (50 - row);
                    col = 100;
                }
                else if ((col == 101) && (row >= 51) && (row <= 100)) {
                    // 3 to 2
                    direction = Direction.UP;
                    col = 101 + (row - 51);
                    row = 50;
                }
                else if ((col == 101) && (row >= 101) && (row <= 150)) {
                    // 5 to 2
                    direction = Direction.LEFT;
                    row = 1 + (150 - row);
                    col = 150;
                }
                else if ((col == 51) && (row >= 151) && (row <= 200)) {
                    // 6 to 5
                    direction = Direction.UP;
                    col = 51 + (row - 151);
                    row = 150;
                }
            }

            if (tiles[col][row] == Tile.OPEN) {
                return State.builder()
                        .position(
                                Coord.builder()
                                        .col(col)
                                        .row(row)
                                        .build())
                        .direction(direction)
                        .build();
            } else {
                // can't move
                return state;
            }
        }

        public Tile getTile(Coord position) {
            return tiles[position.col][position.row];
        }
    }

    @ToString
    @Builder
    @EqualsAndHashCode
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
                result = board.move(result);
                if (board.getTile(state.position) == Tile.NULL) throw new RuntimeException("Invalid location " + state);
                states.add(result);
            }
            return result;
        }
    }

}
