package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4Bingo {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day4Input.txt"))) {


            List<String> lines = stream.collect(Collectors.toList());

            String[] numbers = lines.get(0).split(",");
            lines.remove(0);

            List<Board> boards = new ArrayList<>();

            Board board = null;
            int rowIdx = 0;

            for (String line : lines) {
                if (line.length() == 0) {
                    if (board != null) {
                        boards.add(board);
                        board.print();
                        System.out.println();
                    }
                    board = new Board();
                    rowIdx = 0;
                }
                else {
                    board.setRow(rowIdx, line.split(" "));
                    rowIdx++;
                }
            }

            Map<Board, Integer> winners = new HashMap<>();
            Board lastBoard = null;
            for (int i = 0; i < numbers.length; i++) {
                for (Board board1 : boards) {

                    if (winners.containsKey(board1)) continue;

                    List<Integer> subNumbers = Arrays.stream(numbers).limit(i + 1).map(Integer::parseInt).collect(Collectors.toList());
                    if (board1.isWin(subNumbers)) {
                        System.out.println("WINNER");
                        lastBoard = board1;

                        winners.put(board1, board1.score(subNumbers));
                    }
                }
            }

            System.out.printf("Score: %d\n", winners.get(lastBoard));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Board {
        int[][] numbers = new int[5][5];
        boolean isWin(List<Integer> values) {
            boolean result = false;
            for (int i = 0; i < 5; i++) {
                boolean rowMatch = true;
                boolean colMatch = true;
                for (int j = 0; j < 5; j++) {
                    rowMatch &= values.contains(numbers[i][j]);
                    colMatch &= values.contains(numbers[j][i]);
                }
                result |= rowMatch || colMatch;
            }
            return result;
        }

        public void setRow(int row, String[] rowNumbers) {
            List<Integer> input = Arrays.stream(rowNumbers)
                    .filter(s -> !s.isEmpty())
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            for (int col = 0; col < 5; col++) {
                numbers[row][col] = input.get(col);
            }
        }

        public void print() {
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    System.out.printf("%02d ", numbers[row][col]);
                }
                System.out.println();
            }
        }

        public int score(List<Integer> values) {
            int unMarked = 0;
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    if (!values.contains(numbers[row][col])) {
                        unMarked += numbers[row][col];
                    }
                }
            }
            return unMarked * values.get(values.size()-1);
        }
    }
}
