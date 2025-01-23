package com.johnpickup.aoc2018;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.Direction;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day13 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2018/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                //prefix + "-test.txt"
                 prefix + "-test2.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                TrackMaze trackMaze = new TrackMaze(lines);
                System.out.println("Part 1: " + trackMaze.locationOfFirstCrash());

                trackMaze = new TrackMaze(lines);
                System.out.println("Part 2: " + trackMaze.locationOfLastCart());
                // not 49,100

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }


    static class TrackMaze {
        final CharGrid grid;
        final Set<Coord> junctions;
        final List<Cart> carts = new ArrayList<>();
        TrackMaze(List<String> lines) {
            grid = new CharGrid(lines);
            junctions = grid.findCells('+');
            grid.findCells('^').forEach(cell -> carts.add(new Cart(cell, Direction.NORTH)));
            grid.findCells('v').forEach(cell -> carts.add(new Cart(cell, Direction.SOUTH)));
            grid.findCells('<').forEach(cell -> carts.add(new Cart(cell, Direction.WEST)));
            grid.findCells('>').forEach(cell -> carts.add(new Cart(cell, Direction.EAST)));
            carts.sort(Comparator.comparing(c -> c.location));

            // fix-up the grid by removing the carts
            carts.forEach(c -> grid.setCell(c.location,
                    c.direction == Direction.NORTH || c.direction == Direction.SOUTH ? '|' : '-'));
        }

        public Coord locationOfFirstCrash() {
            boolean crashed = false;
            int cartIdx = 0;
            while (!crashed) {
                Cart cart = carts.get(cartIdx);
                cart.moveAlongMaze(this);
                crashed = hasCrashed();
                cartIdx = (cartIdx + 1) % carts.size();
            }
            return crashLocation();
        }

        public Coord locationOfLastCart() {
            int tick = 0;
            int finalTick = Integer.MAX_VALUE;
            List<Cart> cartQueue = new ArrayList<>();
            while (carts.size() > 1 || tick <= finalTick) {
                if (cartQueue.isEmpty()) {
                    cartQueue = new ArrayList<>(carts);
                    cartQueue.sort(Comparator.comparing(c -> c.location));
                }
                Cart cart = cartQueue.remove(0);
                cart.moveAlongMaze(this);
                Coord crashLocation = crashLocation();
                if (crashLocation != null) {
                    List<Cart> crashedCarts = carts.stream().filter(c -> c.location.equals(crashLocation)).collect(Collectors.toList());
                    carts.removeAll(crashedCarts);
                    cartQueue.removeAll(crashedCarts);
                    if (carts.size() <= 1) finalTick = tick;
                }
                if (cartQueue.isEmpty()) {
                    tick++;
                }
            }
            return carts.isEmpty() ? null : carts.get(0).location;
        }

        @Override
        public String toString() {
            CharGrid printGrid = new CharGrid(grid);
            carts.forEach(c -> printGrid.setCell(c.location, c.direction.toString().charAt(0)));
            return printGrid.toString();
        }

        private Coord crashLocation() {
            return carts.stream()
                    .collect(Collectors.groupingBy(c -> c.location))
                    .entrySet().stream()
                    .filter(e -> e.getValue().size() > 1)
                    .map(e -> e.getKey())
                    .findFirst()
                    .orElse(null);
        }

        private boolean hasCrashed() {
            Map<Coord, List<Cart>> cartsByLocation = carts.stream().collect(Collectors.groupingBy(c -> c.location));
            return cartsByLocation.values().stream().anyMatch(v -> v.size() > 1);
        }

        public char getTrackChar(Coord location) {
            return grid.getCell(location);
        }
    }

    @Data
    static class Cart {
        Coord location;
        Direction direction;
        Turn nextTurn;
        Cart(Coord location, Direction direction) {
            this.location = location;
            this.direction = direction;
            this.nextTurn = Turn.LEFT;
        }


        public void moveAlongMaze(TrackMaze trackMaze) {
            Coord nextLocation = location.move(direction, 1);
            Direction nextDirection;
            char trackChar = trackMaze.getTrackChar(nextLocation);
            switch (trackChar) {
                case '/': {
                    switch (direction) {
                        case NORTH:
                            nextDirection = Direction.EAST;
                            break;
                        case WEST:
                            nextDirection = Direction.SOUTH;
                            break;
                        case SOUTH:
                            nextDirection = Direction.WEST;
                            break;
                        case EAST:
                            nextDirection = Direction.NORTH;
                            break;
                        default:
                            throw new RuntimeException("Unexpected direction at turn " + direction);
                    }
                    break;
                }
                case '\\': {
                    switch (direction) {
                        case NORTH:
                            nextDirection = Direction.WEST;
                            break;
                        case EAST:
                            nextDirection = Direction.SOUTH;
                            break;
                        case WEST:
                            nextDirection = Direction.NORTH;
                            break;
                        case SOUTH:
                            nextDirection = Direction.EAST;
                            break;
                        default:
                            throw new RuntimeException("Unexpected direction at turn " + direction);
                    }
                    break;
                }
                case '+': {
                    nextDirection = nextTurn.apply(direction);
                    nextTurn = nextTurn.next();
                    break;
                }
                default:
                    nextDirection = direction;
            }
            location = nextLocation;
            direction = nextDirection;
        }
    }

    enum Turn {
        LEFT,
        STRAIGHT,
        RIGHT;
        Turn next() {
            switch (this) {
                case LEFT: return STRAIGHT;
                case STRAIGHT: return RIGHT;
                case RIGHT: return LEFT;
                default: throw new RuntimeException("Unknown turn direction");
            }
        }
        Direction apply(Direction d) {
            switch (this) {
                case LEFT: return d.left();
                case STRAIGHT: return d;
                case RIGHT: return d.right();
                default: throw new RuntimeException("Unknown turn direction");
            }
        }
    }
}
