package com.johnpickup.aoc2020;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {
    public static void main(String[] args) {
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day11/Day11";
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

                seats = new Seats(lines);       // reset
                long part2 = Seats.part2(seats);
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
            Seats prevSeats;
            Seats newSeats = seats;
            do {
                prevSeats = newSeats;
                newSeats = newSeats.iteratePart1();
            } while (!newSeats.equals(prevSeats));
            return newSeats.occupiedCount();
        }

        public static long part2(Seats seats) {
            Set<Coord> seatCoords = seats.findCells('L');
            Seats prevSeats;
            Seats newSeats = seats;
            do {
                prevSeats = newSeats;
                newSeats = newSeats.iteratePart2(seatCoords);
            } while (!newSeats.equals(prevSeats));
            return newSeats.occupiedCount();
        }

        private Seats iteratePart1() {
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

        private Seats iteratePart2(Set<Coord> seatCoords) {
            Seats result = new Seats(this);

            for (Coord seatCoord : seatCoords) {
                int occupied = 0;
                occupied += countOccupiedInDirection(seatCoord, 1, 0);
                occupied += countOccupiedInDirection(seatCoord, -1, 0);
                occupied += countOccupiedInDirection(seatCoord, 0, 1);
                occupied += countOccupiedInDirection(seatCoord, 0, -1);
                occupied += countOccupiedInDirection(seatCoord, 1, 1);
                occupied += countOccupiedInDirection(seatCoord, 1, -1);
                occupied += countOccupiedInDirection(seatCoord, -1, 1);
                occupied += countOccupiedInDirection(seatCoord, -1, -1);
                if (occupied == 0) result.setCell(seatCoord, '#');
                else if (occupied >= 5) result.setCell(seatCoord, 'L');
            }
//            System.out.println("--------------------------");
//            System.out.println(result);

            return result;
        }

        private int countOccupiedInDirection(Coord seatCoord, int dx, int dy) {
            Integer result = null;
            Coord coord  = new Coord(seatCoord).moveBy(dx, dy);

            while (result == null && inBounds(coord)) {
                if (getCell(coord) == '#') result = 1;
                if (getCell(coord) == 'L') result = 0;
                coord = coord.moveBy(dx, dy);
            }
            return Optional.ofNullable(result).orElse(0);
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
