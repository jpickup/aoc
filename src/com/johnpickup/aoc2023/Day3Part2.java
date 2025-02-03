package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Character.isDigit;

public class Day3Part2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day3.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Board board = Board.parse(lines);
            List<PartNumber> partNumbers = board.partNumbers();
            long part1 = partNumbers.stream().map(pn -> pn.number).reduce(0L, Long::sum);
            System.out.println("Part 1 :" + part1);     // 539713 - correct

            // the gears are those symbols where the number of adjacent part numbers is 2
            Map<Symbol, Set<PartNumber>> partsAdjacentToGearSymbols = new HashMap<>();
            for (PartNumber part : partNumbers) {
                List<Symbol> gearsForPart = part.adjacentSymbols.stream().filter(s -> s.symbol == '*').collect(Collectors.toList());
                for (Symbol gear : gearsForPart) {
                    if (!partsAdjacentToGearSymbols.containsKey(gear)) {
                        partsAdjacentToGearSymbols.put(gear, new HashSet<>());
                    }
                    partsAdjacentToGearSymbols.get(gear).add(part);
                }
            }

            Long part2 = partsAdjacentToGearSymbols.values().stream()
                    .filter(numbers -> numbers.size() == 2)
                    .map(Day3Part2::calcRatio)
                    .reduce(0L, Long::sum);
            System.out.println("Part 2 :" + part2);   // 84159075 - correct
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "(ms)");
    }

    private static long calcRatio(Set<PartNumber> parts) {
        return parts.stream().map(p -> p.number).reduce(1L, (a,b) -> a*b);
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

        public char getCell(int col, int row) {
            if (col<0 || col>=width || row<0 || row>=height) return '.';
            return cells[col][row];
        }

        public List<PartNumber> partNumbers() {
            List<PartNumber> result = new ArrayList<>();
            String partNo = "";
            List<Symbol> symbols = new ArrayList<>();
            int col = 0, row;
            for (row = 0; row < height; row++) {
                for (col = 0; col < width; col++) {
                    char ch = getCell(col, row);
                    if (!isDigit(ch)) {
                        if (!symbols.isEmpty() && !partNo.isEmpty()) {
                            result.add(new PartNumber(Long.parseLong(partNo), col, row, symbols));
                        }
                        partNo = "";
                        symbols.clear();
                    }
                    else {
                        if (isDigit(ch)) {
                            partNo = partNo + ch;
                            checkAdjacentSymbol(col-1,row, symbols);
                            checkAdjacentSymbol(col+1,row, symbols);
                            checkAdjacentSymbol(col,row-1, symbols);
                            checkAdjacentSymbol(col,row+1, symbols);
                            checkAdjacentSymbol(col-1,row-1, symbols);
                            checkAdjacentSymbol(col-1,row+1, symbols);
                            checkAdjacentSymbol(col+1,row-1, symbols);
                            checkAdjacentSymbol(col+1,row+1, symbols);
                        }
                    }
                }
            }
            if (!symbols.isEmpty() && !partNo.isEmpty()) {
                result.add(new PartNumber(Long.parseLong(partNo), col, row, symbols));
            }
            return result;
        }

        private void checkAdjacentSymbol(int col, int row, List<Symbol> symbols) {
            if (isSymbol(col, row)) {
                symbols.add(new Symbol(getCell(col, row), col, row));
            }
        }

        private boolean isSymbol(int col, int row) {
            char ch = getCell(col, row);
            return ch != '.' && !isDigit(ch);
        }
    }

    @Data
    static class PartNumber {
        final long number;
        final int row;
        final int col;
        final List<Symbol> adjacentSymbols;

        public PartNumber(long number, int col, int row, List<Symbol> adjacentSymbols) {
            this.number = number;
            this.row = row;
            this.col = col;
            this.adjacentSymbols = new ArrayList<>(adjacentSymbols);
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Symbol {
        final char symbol;
        final int row;
        final int col;
    }
}
