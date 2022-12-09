package com.johnpickup.aoc2021;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day17Trajectory {
    public static void main(String[] args) {
        // test 1
        //Bounds bounds = new Bounds(20,30,-10,-5);

        // real
        Bounds bounds = new Bounds(211,232,-124,-69);

        System.out.println(testGuess(6,9, bounds));

        List<Integer> possibleXs = findXs(bounds);


        System.out.println("Possible Xs: " + possibleXs);

        List<Solution> solutions = new ArrayList<>();
        for (Integer x : possibleXs) {
            solutions.addAll(solveY(x, bounds));
        }

        solutions.sort(Comparator.comparingInt(a -> a.highestY));

        System.out.println("Possible Solutions: " + solutions);
        System.out.println("Possible Solutions Size: " + solutions.size());
        // 1971 is too low

        System.out.println("Best Solution: " + solutions.get(solutions.size()-1));
        // 7626 is correct
    }

    private static List<Solution> solveY(Integer x, Bounds bounds) {
        boolean found = false;
        List<Solution> result = new ArrayList<>();
        int guess = 1000;

        while (guess >= bounds.minY){

            Solution solution = testGuess(x, guess, bounds);
            if (solution != null) {
                result.add(solution);
                found = true;
            }
            guess--;
        }
        return result;
    }

    private static Solution testGuess(Integer x, int y, Bounds bounds) {
        int currX = 0;
        int currY = 0;
        int velX = x;
        int velY = y;
        int maxY = 0;
        while (currX < bounds.maxX && currY > bounds.minY) {
            currX += velX;
            velX--;
            if (velX <0) velX = 0;
            currY += velY;
            velY--;

            if (currY > maxY) maxY = currY;

            //System.out.printf("(%d, %d)\n", currX, currY);

            if (bounds.contains(currX, currY)) {
                return new Solution(x, y, maxY);
            }
        }
        return null;
    }

    private static List<Integer> findXs(Bounds bounds) {
        List<Integer> xs = new ArrayList<>();

        for (int x = bounds.maxX; x > 0; x--) {
            if (canSum(x, bounds.minX, bounds.maxX)) {
                xs.add(x);
            }
        }
        return xs;
    }

    private static boolean canSum(int x, int targetXmin, int targetXmax) {
        int sum = 0;
        for (int i = x; i > 0; i--) {
            sum += i;
            if (sum >= targetXmin && sum <= targetXmax) return true;
        }
        return false;
    }

    static class Bounds {
        int minX, maxX, minY, maxY;

        public Bounds(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        public boolean contains(int x, int y) {
            return (x>=minX) && (x<=maxX) && (y>=minY) && (y<=maxY);
        }
    }

    static class Solution {
        int x, y;
        int highestY;


        @Override
        public String toString() {
            return "Solution{" +
                    "x=" + x +
                    ", y=" + y +
                    ", highestY=" + highestY +
                    "}\n";
        }

        public Solution(int x, int y, int highestY) {
            this.x = x;
            this.y = y;
            this.highestY = highestY;
        }
    }
}
