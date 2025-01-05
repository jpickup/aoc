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
                long part1 = floor.blackTileCount();
                System.out.println("Part 1: " + part1);

                Floor part2Floor = new Floor(floor);
                for (int d=1; d <= 100; d++) {
                    part2Floor = part2Floor.flipTiles();
                    //System.out.printf("Day %d: %d%n", d, part2Floor.blackTileCount());
                }
                long part2 = part2Floor.blackTileCount();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Floor {
        final List<DirectionList> directionLists;
        final Map<Coord, TileState> tiles;

        Floor(List<DirectionList> directionLists) {
            this.directionLists = directionLists;
            tiles = new HashMap<>();
        }
        Floor(Floor source) {
            directionLists = Collections.emptyList();
            tiles = new HashMap<>(source.tiles);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int minX = tiles.keySet().stream().map(Coord::getX).min(Integer::compareTo).orElse(0);
            int minY = tiles.keySet().stream().map(Coord::getY).min(Integer::compareTo).orElse(0);
            int maxX = tiles.keySet().stream().map(Coord::getX).max(Integer::compareTo).orElse(0);
            int maxY = tiles.keySet().stream().map(Coord::getY).max(Integer::compareTo).orElse(0);
            sb.append("Top left: ").append(new Coord(minX, minY)).append('\n');
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    sb.append(get(new Coord(x,y)));
                }
                sb.append('\n');
            }
            sb.append("Bottom Right: ").append(new Coord(maxX, maxY)).append('\n');
            return sb.toString();
        }

        long blackTileCount() {
            return tiles.values().stream().filter(t -> t.equals(TileState.BLACK)).count();
        }

        public void applyDirectionLists() {
            directionLists.forEach(dl -> dl.apply(this));
        }

        public Floor flipTiles() {
            Floor result = new Floor(this);
            Set<Coord> coordsToConsider = new HashSet<>();
            for (Coord c : tiles.keySet()) {
                coordsToConsider.add(c);
                coordsToConsider.add(c.east());
                coordsToConsider.add(c.west());
                coordsToConsider.add(c.southEast());
                coordsToConsider.add(c.south());
                coordsToConsider.add(c.north());
                coordsToConsider.add(c.northWest());
            }
            for (Coord coord : coordsToConsider) {
                int adjacentBlackCount = countAdjacentBlack(coord);
                TileState tileState = get(coord);
                switch (tileState) {
                    case BLACK:
                        if (adjacentBlackCount == 0 || adjacentBlackCount>2) {
                            result.flipTile(coord);
                        }
                        break;
                    case WHITE:
                        if (adjacentBlackCount == 2) {
                            result.flipTile(coord);
                        }
                        break;
                }
            }
            return result;
        }

        private int countAdjacentBlack(Coord coord) {
            int result = 0;
            if (isBlack(coord.east())) result++;
            if (isBlack(coord.west())) result++;
            if (isBlack(coord.southEast())) result++;
            if (isBlack(coord.south())) result++;
            if (isBlack(coord.north())) result++;
            if (isBlack(coord.northWest())) result++;
            return result;
        }

        private boolean isBlack(Coord coord) {
            return get(coord).equals(TileState.BLACK);
        }

        TileState get(Coord coord) {
            return tiles.getOrDefault(coord, TileState.WHITE);
        }

        private void flipTile(Coord coord) {
            tiles.put(coord, get(coord).flip());
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

        @Override
        public String toString() {
            return this==BLACK ? "#":".";
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
            Coord coord = Coord.ORIGIN;
            for (Direction direction : directions) {
                coord  = direction.applyTo(coord);
            }
            floor.tiles.put(coord, floor.get(coord).flip());
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
        static final List<Direction> allDirections = Arrays.asList(EAST, WEST, SOUTHEAST, SOUTHWEST, NORTHEAST, NORTHWEST);

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
