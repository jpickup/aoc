package com.johnpickup.aoc2023;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Character.isDigit;

public class Day3 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day3.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Board board = Board.parse(lines);
            //System.out.println(board);
            List<Long> partNumbers = board.partNumbers();
            System.out.println(partNumbers);
            long part1 = partNumbers.stream().reduce(0L, Long::sum);
            System.out.println("Part 1 :" + part1);     // 539713 - correct
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "(ms)");
    }

    @RequiredArgsConstructor
    static class Board {
        final int width;
        final int height;
        final char[][] cells;

        static Board parse(List<String> lines) {
            Board board = new Board(lines.get(0).length(), lines.size(), new char[lines.get(0).length()][lines.size()]);
            int row = 0;
            for (String line : lines) {
                for (int col=0; col < line.length(); col++) {
                    board.cells[col][row] = line.charAt(col);
                }
                row++;
            }
            return board;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    sb.append(cells[col][row]);
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        public List<Long> partNumbers() {
            List<Long> result = new ArrayList<>();
            String partNo = "";
            boolean isAdjacent = false;
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    char ch = cells[col][row];
                    if (ch == '.' || isSymbol(col, row)) {
                        if (isAdjacent && !partNo.isEmpty()) {
                            result.add(Long.parseLong(partNo));
                        }
                        partNo = "";
                        isAdjacent = false;
                    }
                    else {
                        if (isDigit(ch)) {
                            partNo = partNo + ch;
                            isAdjacent = isAdjacent
                                    || isSymbol(col-1,row)
                                    || isSymbol(col+1,row)
                                    || isSymbol(col,row-1)
                                    || isSymbol(col,row+1)
                                    || isSymbol(col-1,row-1)
                                    || isSymbol(col-1,row+1)
                                    || isSymbol(col+1,row-1)
                                    || isSymbol(col+1,row+1);
                        }
                    }
                }
            }
            if (isAdjacent && !partNo.isEmpty()) {
                result.add(Long.parseLong(partNo));
            }
            return result;
        }

        private boolean isSymbol(int col, int row) {
            if (col<0 || col>=width || row<0 || row>=height)
                return false;
            char ch = cells[col][row];
            return ch != '.' && !isDigit(ch);
        }

    }

}
