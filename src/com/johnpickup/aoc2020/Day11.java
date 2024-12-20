package com.johnpickup.aoc2020;

import com.johnpickup.aoc2020.util.CharGrid;
import com.johnpickup.aoc2020.util.Coord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {
    public static void main(String[] args) {
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day11";
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Seats seats = new Seats(lines);
                System.out.println(seats);

                long part1 = Seats.part1(seats);
                System.out.println("Part 1: " + part1);
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Seats extends CharGrid {

        public Seats(int width, int height, char[][] cells) {
            super(width, height, cells);
        }

        public Seats(List<String> lines) {
            super(lines);
        }

        public Seats(Seats source) {
            super(source);
        }

        public static long part1(Seats seats) {
            Seats prevSeats = seats;
            Seats newSeats = seats;
            do {
                prevSeats = newSeats;
                newSeats = newSeats.iterate();
            } while (!newSeats.equals(prevSeats));
            return newSeats.occupiedCount();
        }

        private Seats iterate() {
            Seats result = new Seats(this);
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    Coord coord = new Coord(x, y);
                    if (this.getCell(coord) != '.') {
                        int occupied = this.getOccupied(coord);
                        if (occupied == 0) result.getCells()[x][y] = '#';
                        else if (occupied >= 4) result.getCells()[x][y] = 'L';
                    }
                }
            }
//            System.out.println("--------------------------");
//            System.out.println(result);

            return result;
        }

        private long occupiedCount() {
            long result = 0L;
            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    if (getCell(new Coord(x, y)) == '#') {
                        result += 1;
                    }
                }
            }
            return result;
        }

        private int getOccupied(Coord coord) {
            int result = 0;
            if (getCell(coord.north()) == '#') result++;
            if (getCell(coord.south()) == '#') result++;
            if (getCell(coord.east()) == '#') result++;
            if (getCell(coord.west()) == '#') result++;
            if (getCell(coord.northEast()) == '#') result++;
            if (getCell(coord.northWest()) == '#') result++;
            if (getCell(coord.southEast()) == '#') result++;
            if (getCell(coord.southWest()) == '#') result++;
            return result;
        }
    }
}
