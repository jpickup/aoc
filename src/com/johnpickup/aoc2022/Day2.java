package com.johnpickup.aoc2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2 {
    public static void main(String[] args) throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day2.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            long totalScore=0;

            for (String line : lines) {
                RPS op = decode(line.charAt(0));
                // part1 : RPS me = decode(line.charAt(2));
                RPS me = chose(line.charAt(2), op);
                System.out.println(op.toString()+me.toString());
                long score = compare(op,  me);
                totalScore += score;
            }
            System.out.println(totalScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static RPS chose(char input, RPS rps) {
        switch (input) {
            case 'X': return loser(rps);
            case 'Y': return rps;
            case 'Z': return winner(rps);
            default: throw new RuntimeException("unknown "+input);
        }
    }

    private static long compare(RPS op, RPS me) {
        if (op == me) {
            return 3 + me.score;
        }
        if (beats(op,me)) {
            return 6 + me.score;
        }
        return me.score;
    }

    private static boolean beats(RPS op, RPS me) {
        switch (me) {
            case ROCK: return op==RPS.SCISSORS;
            case PAPER: return op==RPS.ROCK;
            case SCISSORS: return op==RPS.PAPER;
            default: throw new RuntimeException("unknown "+me);
        }
    }

    private static RPS loser(RPS rps) {
        switch (rps) {
            case ROCK: return RPS.SCISSORS;
            case PAPER: return RPS.ROCK;
            case SCISSORS: return RPS.PAPER;
            default: throw new RuntimeException("unknown "+rps);
        }
    }

    private static RPS winner(RPS rps) {
        switch (rps) {
            case ROCK: return RPS.PAPER;
            case PAPER: return RPS.SCISSORS;
            case SCISSORS: return RPS.ROCK;
            default: throw new RuntimeException("unknown "+rps);
        }
    }

    static RPS decode(char c) {
        switch (c) {
            case 'A':
            case 'X': return RPS.ROCK;
            case 'B':
            case 'Y': return RPS.PAPER;
            case 'C':
            case 'Z': return RPS.SCISSORS;
            default:
                throw new RuntimeException("unknown "+ c);
        }
    }

    enum RPS {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);
        final int score;
        RPS(int score) {this.score = score;}
    }
}
