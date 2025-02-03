package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day25 {
    List<String> lines;

    public static void main(String[] args) {
        Day25 day26 = new Day25("/Volumes/Users/john/Development/AdventOfCode/resources/2021/Day25Input.txt");
        day26.solve();
    }

    private void solve() {
        Board board = Board.parse(lines);
        Board prevBoard = null;
        int i = 0;

        System.out.println(board);

        while (prevBoard==null || !board.toString().equals(prevBoard.toString())) {
            prevBoard = board.copy();
            board = board.play();

            System.out.printf(" -- %d --------------\n", ++i);
            //System.out.println(board);
        }

        System.out.printf(" -- FINAL %d --------------\n", i);
        System.out.println(board);

    }

    public Day25(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Board {
        Cucumber[][] cucumbers;

        public Board(Cucumber[][] cucumbers) {
            this.cucumbers = cucumbers;
        }

        public static Board parse(List<String> lines) {
            Cucumber[][] cucumbers = new Cucumber[lines.size()][lines.get(0).length()];
            for (int row = 0; row < lines.size(); row++) {
                for (int col = 0; col < lines.get(0).length(); col++) {
                    cucumbers[row][col] = Cucumber.of(lines.get(row).charAt(col));
                }
            }
            return new Board(cucumbers);
        }

        public Board play() {
            Cucumber[][] copy = new Cucumber[this.cucumbers.length][this.cucumbers[0].length];
            for (int row = 0; row < cucumbers.length; row++) {
                for (int col = 0; col < this.cucumbers[0].length; col++) {
                    int nextCol = nextHorz(col);
                    if (cucumbers[row][col] == Cucumber.HORZ &&
                            (cucumbers[row][nextCol] == Cucumber.NONE)) {
                        copy[row][nextCol] = cucumbers[row][col];
                        copy[row][col] = Cucumber.NONE;
                    }
                    else{
                        if (copy[row][col]==null) copy[row][col] = cucumbers[row][col];
                    }
                }

            }

            for (int row = 0; row < cucumbers.length; row++) {
                for (int col = 0; col < this.cucumbers[0].length; col++) {
                    int nextRow = nextVert(row);
                    if (cucumbers[row][col] == Cucumber.VERT) {
                        if (((copy[nextRow][col] == Cucumber.NONE) || (copy[nextRow][col] == null))
                                && cucumbers[nextRow][col] != Cucumber.VERT) {
                            copy[nextRow][col] = cucumbers[row][col];
                            copy[row][col] = Cucumber.NONE;
                        } else {
                            if (copy[row][col]==null) copy[row][col] = cucumbers[row][col];
                        }
                    }
                }
            }

            for (int row = 0; row < cucumbers.length; row++) {
                for (int col = 0; col < this.cucumbers[0].length; col++) {
                    if (copy[row][col] == null) {
                        copy[row][col] = cucumbers[row][col];
                    }
                }
            }

            return new Board(copy);
        }

        private int nextHorz(int from) {
            return (from + 1) < cucumbers[0].length ? from + 1 : 0;
        }

        private int nextVert(int from) {
            return (from + 1) < cucumbers.length ? from + 1 : 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Board board = (Board) o;
            return Arrays.equals(cucumbers, board.cucumbers);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(cucumbers);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int row = 0; row < cucumbers.length; row++) {
                for (int col = 0; col < this.cucumbers[0].length; col++) {
                    builder.append(cucumbers[row][col]);
                }
                builder.append('\n');
            }

            return builder.toString();

        }

        public Board copy() {
            Cucumber[][] copy = new Cucumber[this.cucumbers.length][this.cucumbers[0].length];
            for (int row = 0; row < cucumbers.length; row++) {
                for (int col = 0; col < this.cucumbers[0].length; col++) {
                    copy[row][col] = cucumbers[row][col];
                }
            }

            return new Board(copy);
        }
    }

    enum Cucumber {
        UNASSIGNED(' '),
        NONE('.'),
        HORZ('>'),
        VERT('v');

        char ch;

        Cucumber(char ch) {
            this.ch = ch;
        }

        static Cucumber of(char ch) {
            switch (ch) {
                case ' ':
                    return UNASSIGNED;
                case '.':
                    return NONE;
                case '>':
                    return HORZ;
                case 'v':
                    return VERT;
                default:
                    throw new RuntimeException("Unrecognised char " + ch);
            }
        }

        public String toString() {
            return "" + ch;

        }

    }
}
