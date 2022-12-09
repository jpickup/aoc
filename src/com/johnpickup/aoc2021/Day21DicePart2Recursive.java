package com.johnpickup.aoc2021;

import java.math.BigInteger;
import java.util.*;

public class Day21DicePart2Recursive {
    static byte boardSize = 10;
    static byte winningScore = 21;  // part2
    final Map<Byte, Byte> possibleRollDistribution = new TreeMap<>();

    // Target for 4 / 8:  444356092776315 universes, while player 2 merely wins in 341960390180808
    //    Wins{player1=  3110492649434205, player2=341960390180808}
    //    Player 1 wins:   45427635889013
    //    Player 2 wins: 11375660404922


    public static void main(String[] args) {
        Day21DicePart2Recursive day21 = new Day21DicePart2Recursive();
        State initialState = new State((byte) 4, (byte) 8);
        System.out.println(day21.solve(initialState));
    }


    public Day21DicePart2Recursive() {
        init();
    }

    private void init() {
        for (int i = 1; i <= 3; i++)
            for (int j = 1; j <= 3; j++)
                for (int k = 1; k <= 3; k++) {
                    byte roll = (byte) (i + j + k);
                    possibleRollDistribution.put(roll,
                            (byte) (Optional.ofNullable(possibleRollDistribution.get(roll)).orElse((byte) 0) + 1));
                }

        System.out.printf("%s possible rolls\n", possibleRollDistribution);
    }

    public Wins solve(State state) {
        if (state.player1Won()) return new Wins(BigInteger.ONE, BigInteger.ZERO);
        if (state.player2Won()) return new Wins(BigInteger.ZERO, BigInteger.ONE);

        Wins result = new Wins();
        for (Map.Entry<Byte, Byte> diceRoll1Entry : possibleRollDistribution.entrySet()) {
            Byte player1Roll = diceRoll1Entry.getKey();
            Byte player1Freq = diceRoll1Entry.getValue();

            State newState = state.applyPlayer1(player1Roll);
            short rollCount = player1Freq;

            if (!newState.isFinal()) {
                for (Map.Entry<Byte, Byte> diceRoll2Entry : possibleRollDistribution.entrySet()) {

                    Byte player2Roll = diceRoll2Entry.getKey();
                    Byte player2Freq = diceRoll2Entry.getValue();

                    newState = newState.applyPlayer2(player2Roll);
                    rollCount *= player2Freq;

                    Wins newStateWins = solve(newState);
                    result = result.add(newStateWins.times(rollCount));
                }
            } else {
                Wins newStateWins = solve(newState);
                result = result.add(newStateWins.times(rollCount));
            }
        }
        return result;
    }

    static class State {
        final byte position1;
        final byte position2;
        final byte score1;
        final byte score2;

        public State applyPlayer1(byte roll1) {
            byte newScore1 = score1;
            if (!isFinal()) {
                byte newPosition1 = (byte) (((position1 + roll1 - 1) % boardSize) + 1);
                newScore1 += newPosition1;
                return new State(newPosition1, position2, newScore1, score2);
            }
            return null;
        }

        public State applyPlayer2(byte roll2) {
            byte newScore2 = score2;
            if (!isFinal()) {
                byte newPosition2 = (byte) (((position2 + roll2 - 1) % boardSize) + 1);
                newScore2 += newPosition2;
                return new State(position1, newPosition2, score1, newScore2);
            }
            return null;
        }


        boolean isFinal() {
            return player1Won() || player2Won();
        }

        boolean player1Won() {
            return score1 >= winningScore;
        }

        boolean player2Won() {
            return score2 >= winningScore;
        }

        public State(byte position1, byte position2) {
            this(position1, position2, (byte) 0, (byte) 0);
        }

        private State(byte position1, byte position2, byte score1, byte score2) {
            this.position1 = position1;
            this.position2 = position2;
            this.score1 = score1;
            this.score2 = score2;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return position1 == state.position1 &&
                    position2 == state.position2 &&
                    score1 == state.score1 &&
                    score2 == state.score2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(position1, position2, score1, score2);
        }

        @Override
        public String toString() {
            return "State{" +
                    "position1=" + position1 +
                    ", position2=" + position2 +
                    ", score1=" + score1 +
                    ", score2=" + score2 +
                    '}';
        }
    }


    static class Wins {
        final BigInteger player1;
        final BigInteger player2;

        public Wins add(Wins wins) {
            return new Wins(this.player1.add(wins.player1), this.player2.add(wins.player2));
        }

        public Wins times(short rollCount) {
            BigInteger n = BigInteger.valueOf(rollCount);
            return new Wins(this.player1.multiply(n), this.player2.multiply(n));
        }

        public Wins() {
            this(BigInteger.ZERO, BigInteger.ZERO);
        }

        public Wins(BigInteger player1, BigInteger player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        @Override
        public String toString() {
            return "Wins{" +
                    "player1=" + player1 +
                    ", player2=" + player2 +
                    '}';
        }
    }

}
