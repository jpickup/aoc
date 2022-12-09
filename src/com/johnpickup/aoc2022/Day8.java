package com.johnpickup.aoc2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day8 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day8.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());

            int rowCount = lines.size();
            int colCount = lines.get(0).length();
            int[][] trees = new int[rowCount][colCount];

            int row=0;
            for (String line : lines) {
                for (int col=0; col < line.length(); col++) {
                    trees[row][col] = line.charAt(col) - '0';
                }
                row++;
            }

            long visibleCount = 0;
            long bestScore = 0;
            for (row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    if (isVisible(trees, row, col)) {
                        visibleCount++;
                    }
                    long treeScore = score(trees, row, col);
                    if (treeScore > bestScore) bestScore = treeScore;
                }
            }

            System.out.println("Count: " + visibleCount);
            System.out.println("Best: " + bestScore);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isVisible(int[][] trees, int row, int col) {
        int rowCount = trees.length;
        int colCount = trees[0].length;

        int height = trees[row][col];

        boolean leftVisible = true;
        for (int i =0; i < col; i++) {
            leftVisible &= (trees[row][i]<height);
        }

        boolean rightVisible = true;
        for (int i = col + 1; i < colCount; i++) {
            rightVisible &= (trees[row][i]<height);
        }

        boolean topVisible = true;
        for (int i =0; i < row; i++) {
            topVisible &= (trees[i][col]<height);
        }

        boolean bottomVisible = true;
        for (int i = row + 1; i < rowCount; i++) {
            bottomVisible &= (trees[i][col]<height);
        }

        return leftVisible || rightVisible || topVisible || bottomVisible;
    }

    private static long score(int[][] trees, int row, int col) {
        int rowCount = trees.length;
        int colCount = trees[0].length;

        int height = trees[row][col];

        long leftScore = 0;
        for (int i = col-1; i >= 0; i--) {
            if (trees[row][i]<height) leftScore++;
            if (trees[row][i]>=height) {
                leftScore++;
                break;
            }
        }

        long rightScore = 0;
        for (int i = col+1; i < colCount; i++) {
            if (trees[row][i]<height) rightScore++;
            if (trees[row][i]>=height) {
                rightScore++;
                break;
            }
        }

        long topScore = 0;
        for (int i = row-1; i >= 0; i--) {
            if (trees[i][col]<height) topScore++;
            if (trees[i][col]>=height) {
                topScore++;
                break;
            }
        }

        long bottomScore = 0;
        for (int i = row + 1; i < rowCount; i++) {
            if (trees[i][col]<height) bottomScore++;
            if (trees[i][col]>=height) {
                bottomScore++;
                break;
            }
        }

        return leftScore * rightScore * topScore * bottomScore;
    }
}
