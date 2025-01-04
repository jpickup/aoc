package com.johnpickup.aoc2020;

import com.johnpickup.util.Coord;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2020.Day24.TileState.BLACK;
import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day24 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<DirectionList> directionLists = stream
                        .filter(s -> !s.isEmpty())
                        .map(DirectionList::new)
                        .collect(Collectors.toList());

                Floor floor = new Floor(directionLists);

                floor.applyDirectionLists();

                long part1 = floor.part1();
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

    @ToString
    @RequiredArgsConstructor
    static class Floor {
        final List<DirectionList> directionLists;
        final Map<Coord, TileState> tiles = new HashMap<>();

        long part1() {
            return tiles.values().stream().filter(t -> t.equals(BLACK)).count();
        }

        public void applyDirectionLists() {
            directionLists.forEach(dl -> dl.apply(this));

        }
    }

    enum TileState {
        WHITE,
        BLACK;
        TileState flip() {
            switch (this) {
                case BLACK: return WHITE;
                case WHITE: return BLACK;
                default: throw new RuntimeException("Unknown tile state " + this);
            }
        }
    }

    @ToString
    static class DirectionList {
        final List<Direction> directions;
        DirectionList(String line) {
            directions = new ArrayList<>();
            while (!line.isEmpty()) {
                for (Direction d : Direction.allDirections) {
                    if (line.startsWith(d.label)) {
                        directions.add(d);
                        line = line.substring(d.label.length());
                    }
                }
            }
        }

        public void apply(Floor floor) {
            Coord tile = Coord.ORIGIN;
            for (Direction direction : directions) {
                tile  = direction.applyTo(tile);
            }
            TileState priorState = floor.tiles.getOrDefault(tile, TileState.WHITE);
            floor.tiles.put(tile, priorState.flip());
        }
    }


    @RequiredArgsConstructor
    enum Direction {
        EAST("e"),
        SOUTHEAST("se"),
        SOUTHWEST("sw"),
        WEST("w"),
        NORTHWEST("nw"),
        NORTHEAST("ne");
        @Getter
        final String label;
        static List<Direction> allDirections = Arrays.asList(EAST, WEST, SOUTHEAST, SOUTHWEST, NORTHEAST, NORTHWEST);

        public Coord applyTo(Coord coord) {
            switch (this) {
                case EAST: return coord.east();
                case WEST: return coord.west();
                case SOUTHEAST: return coord.southEast();
                case SOUTHWEST: return coord.south();
                case NORTHEAST: return coord.north();
                case NORTHWEST: return coord.northWest();
                default: throw new RuntimeException("Unknown direction " + this);
            }
        }
    }
}
