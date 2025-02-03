package com.johnpickup.aoc2022;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day17 {
    private static final int BOARD_WIDTH = 7;

    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day17.txt"))) {
            long start = System.currentTimeMillis();
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            String jets = lines.get(0);
            System.out.println(jets.length());

            Board board = new Board(BOARD_WIDTH);

            Piece piece1 = new Piece(new boolean[][]{{true}, {true}, {true}, {true}});     // ----
            Piece piece2 = new Piece(new boolean[][]{{false, true, false}, {true, true, true}, {false, true, false}}); // +
            Piece piece3 = new Piece(new boolean[][]{{true, false, false}, {true, false, false}, {true, true, true}}); // _|
            Piece piece4 = new Piece(new boolean[][]{{true, true, true, true}});           // |
            Piece piece5 = new Piece(new boolean[][]{{true, true}, {true, true}});        // []

            Piece[] pieces = new Piece[]{piece1, piece2, piece3, piece4, piece5};

            int jetIdx = 0;
            for (int pieceIdx = 0; pieceIdx < 2022; pieceIdx++) {
                Piece piece = pieces[pieceIdx % pieces.length];
                // Each rock appears so that its left edge is two units away from the left wall
                int pieceX = 2;
                // and its bottom edge is three units above the highest rock in the room (or the floor, if there isn't one).
                long pieceY = 4 + board.getMaxRow();

                while (true) {
                    char jet = jets.charAt(jetIdx++ % jets.length());
                    switch (jet) {
                        case '>':
                            pieceX = piece.moveRight(board, pieceX, pieceY);
                            break;
                        case '<':
                            pieceX = piece.moveLeft(board, pieceX, pieceY);
                            break;
                        default:
                            throw new RuntimeException("Unknown jet " + jet);
                    }
                    long newPieceY = piece.drop(board, pieceX, pieceY);
                    if (newPieceY == pieceY) break;
                    pieceY = newPieceY;
                }
                piece.updateBoard(board, pieceX, pieceY);
//                System.out.println("PIECE IDX: " + pieceIdx);
//                board.display();
//                System.out.println("------------------");

                System.out.println("PIECE IDX: " + pieceIdx + " - " + board.getMaxRow());
            }

            System.out.println("Max row: " + board.getMaxRow());


            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Board {
        @Getter
        int width;
        final Column[] columns;

        Board(int width) {
            this.width = width;
            columns = new Column[width];
            for (int i = 0; i < width; i++) {
                columns[i] = new Column();
            }
        }

        boolean hasEntry(int column, long row) {
            return columns[column].entries.contains(row);
        }

        public long getMaxRow() {
            return Arrays.stream(columns).map(Column::getMax).reduce(0L, (max, elem) -> (elem > max ? elem : max));
        }

        public void addEntry(int column, long row) {
            columns[column].entries.add(row);
        }

        public void display() {
            for (long row = getMaxRow(); row >= 0; row--) {
                System.out.printf("%05d : ", row);
                for (int col = 0; col < width; col++) {
                    if (hasEntry(col, row)) System.out.print("#"); else System.out.print(".");
                }
                System.out.println();
            }
        }
    }

    static class Column {
        final List<Long> entries = new ArrayList<>();

        Column() {
            entries.add(0L);
        }

        public long getMax() {
            return entries.isEmpty()?0:entries.get(entries.size() - 1);
        }
    }

    @RequiredArgsConstructor
    static class Piece {
        final boolean[][] cols;

        public int moveRight(Board board, int pieceX, long pieceY) {
            if (pieceX + cols.length >= board.getWidth()) return pieceX;
            for (int cellX = 0; cellX < cols.length; cellX++) {
                for (int cellY = 0; cellY < cols[cellX].length; cellY++) {
                    if (cols[cellX][cellY]) {
                        int boardX = pieceX + cellX;
                        long boardY = pieceY + cellY;
                        if (board.hasEntry(boardX + 1, boardY)) {
                            return pieceX;
                        }
                    }
                }
            }
            return pieceX + 1;
        }

        public int moveLeft(Board board, int pieceX, long pieceY) {
            if (pieceX <= 0) return pieceX;
            for (int cellX = 0; cellX < cols.length; cellX++) {
                for (int cellY = 0; cellY < cols[cellX].length; cellY++) {
                    if (cols[cellX][cellY]) {
                        int boardX = pieceX + cellX;
                        long boardY = pieceY + cellY;
                        if (board.hasEntry(boardX - 1, boardY)) {
                            return pieceX;
                        }
                    }
                }
            }
            return pieceX - 1;
        }

        public boolean touchingBelow(Board board, int pieceX, long pieceY) {
            for (int cellX = 0; cellX < cols.length; cellX++) {
                for (int cellY = 0; cellY < cols[cellX].length; cellY++) {
                    if (cols[cellX][cellY]) {
                        int boardX = pieceX + cellX;
                        long boardY = pieceY + cellY;
                        if (board.hasEntry(boardX, boardY - 1)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public long drop(Board board, int pieceX, long pieceY) {
            for (int cellX = 0; cellX < cols.length; cellX++) {
                for (int cellY = 0; cellY < cols[cellX].length; cellY++) {
                    if (cols[cellX][cellY]) {
                        int boardX = pieceX + cellX;
                        long boardY = pieceY + cellY;
                        if (board.hasEntry(boardX, boardY - 1)) {
                            return pieceY;
                        }
                    }
                }
            }
            return pieceY - 1;
        }

        public void updateBoard(Board board, int pieceX, long pieceY) {
            for (int cellX = 0; cellX < cols.length; cellX++) {
                for (int cellY = 0; cellY < cols[cellX].length; cellY++) {
                    if (cols[cellX][cellY]) {
                        int boardX = pieceX + cellX;
                        long boardY = pieceY + cellY;
                        board.addEntry(boardX, boardY);
                    }
                }
            }
        }
    }
}

