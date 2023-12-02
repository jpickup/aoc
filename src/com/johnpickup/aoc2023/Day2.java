package com.johnpickup.aoc2023;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day2.txt"))) {
            long start = System.currentTimeMillis();
            long maxRed = 12, maxGreen = 13, maxBlue = 14;
            long part1 = 0, part2 = 0;
            List<Game> games = stream.filter(s -> !s.isEmpty()).map(Game::parse).collect(Collectors.toList());

            for (Game game : games) {
                System.out.println(game.toString());
                if (game.isPossible(maxRed, maxGreen, maxBlue)) {
                    part1 += game.number;
                }
                part2 += game.fewest().power();
            }
            System.out.println("Part 1 = " + part1);
            System.out.println("Part 2 = " + part2);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiredArgsConstructor
    static class Game {
        static Game parse(String input) {
            String[] parts = input.split(":");
            int num = Integer.parseInt(parts[0].replace("Game ", ""));
            String[] turns = parts[1].split(";");
            return new Game(num, Arrays.stream(turns).map(RGB::parse).collect(Collectors.toList()));
        }
        private final int number;
        private final List<RGB> turns;

        public boolean isPossible(long red, long green, long blue) {
            return turns.stream().allMatch(turn -> turn.isPossible(red, green, blue));
        }

        public RGB fewest() {
            return new RGB(
                    turns.stream().map(t -> t.red).max(Long::compareTo).orElse(0L),
                    turns.stream().map(t -> t.green).max(Long::compareTo).orElse(0L),
                    turns.stream().map(t -> t.blue).max(Long::compareTo).orElse(0L)
            );
        }
    }

    @RequiredArgsConstructor
    static class RGB {
        static RGB parse(String input) {
            String[] colours = input.split(",");
            long red = 0;
            long green = 0;
            long blue = 0;
            for (String colour : colours) {
                String[] parts = colour.trim().split(" ");
                switch (parts[1]) {
                    case "red" : red = Long.parseLong(parts[0]); break;
                    case "green" : green = Long.parseLong(parts[0]); break;
                    case "blue" : blue = Long.parseLong(parts[0]); break;
                }
            }
            return new RGB(red, green, blue);
        }
        final long red;
        final long green;
        final long blue;

        public boolean isPossible(long red, long green, long blue) {
            return this.red <= red && this.green <= green && this.blue <= blue ;
        }

        public long power() {
            return red * green * blue;
        }
    }
}
