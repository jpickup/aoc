package com.johnpickup.aoc2020;

import com.johnpickup.util.InputUtils;
import lombok.Data;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day22 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .collect(Collectors.toList());

                List<Player> players = InputUtils.splitIntoGroups(lines).stream().map(Player::new).collect(Collectors.toList());
                System.out.println(players);

                Game game = new Game(players);
                long part1 = game.part1();
                System.out.println(players);
                System.out.println("Part 1: " + part1);
                Game game2 = new Game(players);
                long part2 = game2.part2();
                System.out.println("Part 2: " + part2);
                // 34007 too high
                // 7282 too low
                // 32731 correct

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Game {
        final Player player1;
        final Player player2;
        static final List<GameState> previousStates = new ArrayList<>();
        Game(List<Player> players) {
            if (players.size() != 2) throw new RuntimeException("Only two-player games supported");
            player1 = new Player(players.get(0));
            player2 = new Player(players.get(1));
        }

        Game subGame(){
            return new Game(Arrays.asList(player1, player2));
        }

        long part1() {
            Player winner = playGame();
            return winner.calcScore();
        }

        long part2() {
            Player winner = playRecursiveGame();
            return winner.calcScore();
        }

        Player playRecursiveGame() {
            Player winner = null;
            while (!gameOver()) {
                if (previousStates.contains(new GameState(this))) {
                    return player1;
                    //break;
                }

                int player1Card = player1.takeTopCard();
                int player2Card = player2.takeTopCard();
                int deckSize = player1.deckSize() + player2.deckSize();

                if (player1Card <= player1.deckSize() && player2Card <= player2.deckSize()) {
                    // recursive combat
                    winner = subGame().playRecursiveGame();
//                    if (player1.maxCard() > player2.maxCard() && player1.maxCard() > deckSize) {
//                        winner = player1;
//                    } else {
//                        winner = subGame().playRecursiveGame();
//                    }
                } else {
                    winner = player1Card > player2Card ? player1 : player2;
                }
                if (winner == player1) {
                    player1.addCards(Arrays.asList(player1Card, player2Card));
                } else {
                    player2.addCards(Arrays.asList(player2Card, player1Card));
                }
            }
            previousStates.add(new GameState(this));
            return winner;
        }

        Player playGame() {
            while (!gameOver()) {
                playTurn();
            }
            return player1.gameOver() ? player2 : player1;
        }

        boolean gameOver() {
            return player1.gameOver() || player2.gameOver();
        }

        void playTurn() {
            if (gameOver()) throw new RuntimeException("Game Over!");
            int player1Card = player1.takeTopCard();
            int player2Card = player2.takeTopCard();
            if (player1Card == player2Card) throw new RuntimeException("Draw - not expected!");
            if (player1Card > player2Card) {
                player1.addCards(Arrays.asList(player1Card, player2Card));
            } else {
                player2.addCards(Arrays.asList(player2Card, player1Card));
            }
        }
    }


    @ToString
    static class Player {
        final int id;
        final List<Integer> cards;
        Player(List<String> lines) {
            id = Integer.parseInt(lines.get(0).replace("Player ","").replace(":",""));
            cards = new ArrayList<>(lines.stream().skip(1).map(Integer::parseInt).collect(Collectors.toList()));
        }

        Player(Player source) {
            id = source.id;
            cards = new ArrayList<>(source.cards);
        }

        int takeTopCard() {
            return cards.remove(0);
        }

        void addCards(List<Integer> newCards) {
            cards.addAll(newCards);
        }

        boolean gameOver() {
            return cards.isEmpty();
        }

        public long calcScore() {
            long result = 0;
            for (int i = 1; i <= cards.size(); i++) {
                result += (long)(i * cards.get(cards.size() - i));
            }
            return result;
        }

        public int deckSize() {
            return cards.size();
        }

        public int maxCard() {
            return cards.stream().max(Integer::compareTo).orElse(0);
        }
    }

    @Data
    static class GameState {
        final Player player1;
        final Player player2;

        GameState(Game game) {
            this.player1 = new Player(game.player1);
            this.player2 = new Player(game.player2);
        }
    }
}
