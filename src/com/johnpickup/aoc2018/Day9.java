package com.johnpickup.aoc2018;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;

public class Day9 {
    static boolean isTest;

    public static void main(String[] args) {
        List<MarbleGame> games = Arrays.asList(
                new MarbleGame(9, 25, 32L)
                , new MarbleGame(10, 1618, 8317L)
                , new MarbleGame(13, 7999, 146373L)
                , new MarbleGame(21, 6111, 2764L)
                , new MarbleGame(30, 5807, 54718L)
                , new MarbleGame(418, 71339, 412127L)
                , new MarbleGame(418, 7133900, 0L)
        );

        for (MarbleGame game : games) {
            System.out.println(game);
            long start = System.currentTimeMillis();

            game.playGame();
            long winningScore = game.winningScore();
            if (winningScore != game.expected) System.out.printf("Test failed, expected %d but got %d %n",
                    game.expected, winningScore);

            System.out.println("Part 1: " + winningScore);
            long part2 = 0L;
            System.out.println("Part 2: " + part2);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");

        }
    }

    @RequiredArgsConstructor
    @ToString
    static class MarbleGame {
        final int numberOfPlayers;
        final int lastMarble;
        final long expected;

        int currentMarble;
        int nextMarble;
        int currentPlayer;
        //List<Integer> marbles;
        Map<Integer, Integer> marblesByLocation;
        Map<Integer, Integer> locationByMarble;
        Map<Integer, Set<Integer>> playerMarbles;

        private void init() {
            currentMarble = 0;
            currentPlayer = 0;
            nextMarble = 0;
            //marbles = new ArrayList<>();
            //marbles.add(currentMarble);
            marblesByLocation = new TreeMap<>();
            locationByMarble = new TreeMap<>();
            marblesByLocation.put(0,0);
            locationByMarble.put(0,0);
            playerMarbles = new HashMap<>();
        }

        public void playGame() {
            init();
            while (currentMarble <= lastMarble) {
                playTurn();
            }
        }

        private void playTurn() {
            nextMarble = nextMarble + 1;
            int cw1Idx = (locationByMarble.get(currentMarble)+1) % locationByMarble.size();
            //System.out.println(marbles);

            if (nextMarble % 23 == 0) {
                playerMarbles.putIfAbsent(currentPlayer, new HashSet<>());
                playerMarbles.get(currentPlayer).add(nextMarble);
                int ccw7Idx = (locationByMarble.get(currentMarble) - 7 + locationByMarble.size()) % locationByMarble.size();
                currentMarble = marblesByLocation.get((ccw7Idx + 1) % marblesByLocation.size());
                int removed = marblesByLocation.remove(ccw7Idx);
                locationByMarble.remove(removed);
                playerMarbles.get(currentPlayer).add(removed);
            } else {
                // insert marble between cw1 & cw2
                if (cw1Idx < locationByMarble.size()) {
                    marblesByLocation.put(cw1Idx + 1, nextMarble);
                    locationByMarble.put(nextMarble, cw1Idx + 1);
                } else {
                    int nextLoc = marblesByLocation.size();
                    marblesByLocation.put(locationByMarble.size(), nextMarble);
                    locationByMarble.put(nextMarble, locationByMarble.size());
                }
                currentMarble = nextMarble;
            }
            currentPlayer = (currentPlayer+1) % numberOfPlayers;
        }

        public long winningScore() {
            return playerMarbles.values().stream()
                    .map(v -> v.stream()
                            .map(a -> (long)a)
                            .reduce(0L, (Long::sum)))
                    .max(Long::compareTo)
                    .orElse(0L);
        }
    }
}
