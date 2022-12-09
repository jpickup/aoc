package com.johnpickup.aoc2021;

public class Day21DicePart1 {
    static short boardSize = 10;
    static short winningScore = 1000;


    public static void main(String[] args) {

        Player player1 = new Player((short)5);
        Player player2 = new Player((short)9);
        Dice dice = new DeterministicDice();

        while (!player1.won() && !player2.won()) {
            player1.move((short)(dice.roll() + dice.roll() + dice.roll()));
            if (!player1.won()) {
                player2.move((short)(dice.roll() + dice.roll() + dice.roll()));
            }
        }
        System.out.printf("Player 1: %d\n", player1.score);
        System.out.printf("Player 2: %d\n", player2.score);
        System.out.printf("Dice Rolls: %d\n", dice.rollCount());

        long result = (player1.won()?player2.score:player1.score) * dice.rollCount();

        System.out.printf("Result: %d\n", result);

    }



    static class Player {
        short position;
        short score = 0;

        public Player(short position) {
            this.position = position;
        }

        public void move(short distance) {
            position = (short)(((position + distance - 1) % boardSize) + 1);
            score += position;
        }
        public boolean won() {
            return score >= winningScore;
        }
    }

    interface Dice {
        long rollCount();
        short roll();
    }

    static class DeterministicDice implements Dice {
        short lastRoll = 0;
        long count = 0;

        @Override
        public long rollCount() {
            return count;
        }

        @Override
        public short roll() {
            count++;
            lastRoll++;
            if (lastRoll>1000) lastRoll=1;

            return lastRoll;
        }
    }
}
