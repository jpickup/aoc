package com.johnpickup.aoc2021;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Day21DicePart2 {
    static byte boardSize = 10;
    static byte winningScore = 21;  // part2

    // Target for 4 / 8:  444356092776315 universes, while player 2 merely wins in 341960390180808
    //    Player 1 wins:   43645560504
    //    Player 2 wins: 11375660404922


    public static void main(String[] args) {
        State initialState = new State((byte) 5, (byte) 9);

        Map<State, BigInteger> states = new HashMap<>();
        states.put(initialState, BigInteger.ONE);


        Map<Byte, Byte> possibleRollDistribution = new TreeMap<>();
        for (int i = 1; i <= 3; i++)
            for (int j = 1; j <= 3; j++)
                for (int k = 1; k <= 3; k++)
                    possibleRollDistribution.put((byte) (i + j + k),
                            (byte) (Optional.ofNullable(possibleRollDistribution.get((byte) (i + j + k))).orElse((byte) 0) + 1));

        System.out.printf("%s possible rolls\n", possibleRollDistribution);

        Map<State, BigInteger> finalStates = new HashMap<>();


        while (!states.isEmpty()) {
            System.out.println("States " + states.size() + "; Final states so far: " + finalStates.size());
            showCounts(finalStates);
            Map<State, BigInteger> newStates = new HashMap<>();
            // consider each non-final state and apply all the possible dice rolls
            for (Map.Entry<State, BigInteger> stateEntry : states.entrySet()) {
                State state = stateEntry.getKey();
                BigInteger stateCount = stateEntry.getValue();

                if (state.isFinal()) {
                    finalStates.put(state, Optional.ofNullable(newStates.get(state)).orElse(BigInteger.ZERO).add(stateCount));
                } else {
                    //for (Map.Entry<Byte, Byte> diceRoll1Entry : possibleRollDistribution.entrySet()) {
                    for (int i = 1; i <= 3; i++) {
                        for (int j = 1; j <= 3; j++) {
                            for (int k = 1; k <= 3; k++) {
                                byte roll1 = (byte) (i + j + k);

//                                Byte player1Roll = diceRoll1Entry.getKey();
//                        Byte player1Freq = diceRoll1Entry.getValue();

                                State firstRollState = stateEntry.getKey().applyPlayer1(roll1);

                                if (firstRollState.isFinal()) {
//                            BigInteger combinedRollFreq = BigInteger.valueOf((long) (player1Freq));
                                    finalStates.put(firstRollState,
                                            (Optional.ofNullable(finalStates.get(firstRollState)).orElse(BigInteger.ZERO)).add(stateCount));
                                } else {

                                    //for (Map.Entry<Byte, Byte> diceRoll2Entry : possibleRollDistribution.entrySet()) {
//                                Byte player2Roll = diceRoll2Entry.getKey();
//                                Byte player2Freq = diceRoll2Entry.getValue();

                                    for (int l = 1; l <= 3; l++) {
                                        for (int m = 1; m <= 3; m++) {
                                            for (int n = 1; n <= 3; n++) {
                                                byte roll2 = (byte) (l + m + n);

                                                State secondRollState = firstRollState.applyPlayer2(roll2);
                                                //combinedRollFreq = combinedRollFreq.multiply(BigInteger.valueOf((long) (player2Freq)));

                                                if (secondRollState.isFinal()) {
                                                    finalStates.put(secondRollState,
                                                            (Optional.ofNullable(finalStates.get(secondRollState)).orElse(BigInteger.ZERO)).add(stateCount));
                                                }
                                                else {
                                                    newStates.put(secondRollState,
                                                            (Optional.ofNullable(newStates.get(secondRollState)).orElse(BigInteger.ZERO)).add(stateCount));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            states = newStates;
        }

        System.out.println();
        System.out.println("Final states: " + finalStates.size());


        BigInteger player1Wins = BigInteger.ZERO;
        BigInteger player2Wins = BigInteger.ZERO;
        for (Map.Entry<State, BigInteger> stateEntry : finalStates.entrySet()) {
            if (stateEntry.getKey().isFinal()) {
                //System.out.println(stateEntry);
            } else {
                throw new RuntimeException("Shouldn't be here - non final state");
            }

            if (stateEntry.getKey().player1Won()) {
                player1Wins = player1Wins.add(stateEntry.getValue());
            }
            if (stateEntry.getKey().player2Won()) {
                player2Wins = player2Wins.add(stateEntry.getValue());
            }
        }
        System.out.println("Player 1 wins: " + player1Wins.toString());
        System.out.println("Player 2 wins: " + player2Wins.toString());

        if (player1Wins.compareTo(player2Wins) > 0) {
            System.out.println("Result: " + player1Wins.toString());
        } else {
            System.out.println("Result: " + player2Wins.toString());
        }

    }

    static void showCounts(Map<State, BigInteger> finalStates) {
        BigInteger player1Wins = BigInteger.ZERO;
        BigInteger player2Wins = BigInteger.ZERO;
        for (Map.Entry<State, BigInteger> stateEntry : finalStates.entrySet()) {
            if (stateEntry.getKey().isFinal()) {
                //System.out.println(stateEntry);
            } else {
                throw new RuntimeException("Shouldn't be here - non final state");
            }

            if (stateEntry.getKey().player1Won()) {
                player1Wins = player1Wins.add(stateEntry.getValue());
            }
            if (stateEntry.getKey().player2Won()) {
                player2Wins = player2Wins.add(stateEntry.getValue());
            }
        }
        System.out.print("Player 1 wins: " + player1Wins.toString());
        System.out.println("  Player 2 wins: " + player2Wins.toString());
    }

    static class State {
        final byte position1;
        final byte position2;
        final byte score1;
        final byte score2;

        public State apply(byte roll1, Byte roll2) {
            byte newPosition1 = position1;
            byte newPosition2 = position2;
            byte newScore1 = score1;
            byte newScore2 = score2;
            if (!isFinal()) {
                newPosition1 = (byte) (((position1 + roll1 - 1) % boardSize) + 1);
                newScore1 += newPosition1;
            }
            if (!isFinal()) {
                newPosition2 = (byte) (((position2 + roll2 - 1) % boardSize) + 1);
                newScore2 += newPosition2;
            }
            return new State(newPosition1, newPosition2, newScore1, newScore2);
        }

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


}
