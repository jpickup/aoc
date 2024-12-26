package com.johnpickup.aoc2020;

import com.johnpickup.util.Coord;
import com.johnpickup.util.Direction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day12 {
    static boolean isTest;

    public static void main(String[] args) {
        String day = new Object() {
        }.getClass().getEnclosingClass().getSimpleName();
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
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Ship ship = new Ship(lines);
                ship.executeShipInstructions();
                long part1 = ship.manhattanDistance();
                System.out.println("Part 1: " + part1);

                ship.reset();
                ship.executeWaypointInstructions();
                long part2 = ship.manhattanDistance();
                System.out.println("Part 2: " + part2);
                // 85857 too high

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Ship {
        final List<Instruction> instructions;
        Coord location;
        Direction heading;

        Coord waypoint;

        Ship(List<String> lines) {
            instructions = lines.stream().map(Instruction::new).collect(Collectors.toList());
            reset();
        }

        public void executeShipInstructions() {
            for (Instruction instruction : instructions) {
                instruction.executeForShip(this);
            }
        }

        public long manhattanDistance() {
            return Math.abs(location.getX()) + Math.abs(location.getY());
        }

        public void executeWaypointInstructions() {
            for (Instruction instruction : instructions) {
                instruction.executeForWaypoint(this);
                System.out.println(instruction + " -> " + location + " / " + waypoint);
            }
        }

        public void reset() {
            location = new Coord(0, 0);
            heading = Direction.EAST;
            waypoint = new Coord(10, 1);
        }
    }

    static class Instruction {
        final Action action;
        final int amount;

        Instruction(String line) {
            action = Action.parse(line.charAt(0));
            amount = Integer.parseInt(line.substring(1));
        }

        @Override
        public String toString() {
            return action + " " + amount;
        }

        public void executeForShip(Ship ship) {
            switch (action) {
                case NORTH:
                    ship.location = ship.location.moveBy(0, amount);
                    break;
                case SOUTH:
                    ship.location = ship.location.moveBy(0, -amount);
                    break;
                case EAST:
                    ship.location = ship.location.moveBy(amount, 0);
                    break;
                case WEST:
                    ship.location = ship.location.moveBy(-amount, 0);
                    break;
                case LEFT:
                    ship.heading = rotateBy(ship.heading, amount);
                    break;
                case RIGHT:
                    ship.heading = rotateBy(ship.heading, -amount);
                    break;
                case FORWARD:
                    ship.location = moveBy(ship.location, ship.heading, amount);
                    break;
                default:
                    throw new RuntimeException("Unsupported action " + action);
            }
        }

        public void executeForWaypoint(Ship ship) {
            switch (action) {
                case NORTH:
                    ship.waypoint = ship.waypoint.moveBy(0, amount);
                    break;
                case SOUTH:
                    ship.waypoint = ship.waypoint.moveBy(0, -amount);
                    break;
                case EAST:
                    ship.waypoint = ship.waypoint.moveBy(amount, 0);
                    break;
                case WEST:
                    ship.waypoint = ship.waypoint.moveBy(-amount, 0);
                    break;
                case LEFT:
                    ship.waypoint = rotateWaypoint(ship.waypoint, -amount);
                    break;
                case RIGHT:
                    ship.waypoint = rotateWaypoint(ship.waypoint, +amount);
                    break;
                case FORWARD:
                    ship.location = moveToWaypoint(ship.location, ship.waypoint, amount);
                    break;
                default:
                    throw new RuntimeException("Unsupported action " + action);
            }
        }

        private Coord rotateWaypoint(Coord waypoint, int degrees) {
            return waypoint.rotateAround(Coord.ORIGIN, degrees/90);
        }

        private Coord moveToWaypoint(Coord location, Coord waypoint, int amount) {
            int dx = waypoint.getX();
            int dy = waypoint.getY();
            return new Coord(location.getX() + dx * amount, location.getY() + dy * amount);
        }

        Direction rotateBy(Direction d, int degrees) {
            Direction result = d;
            while (degrees > 0) {
                result = result.right();
                degrees -= 90;
            }
            while (degrees < 0) {
                result = result.left();
                degrees += 90;
            }
            return result;
        }

        Coord moveBy(Coord c, Direction d, int amount) {
            switch (d) {
                case NORTH:
                    return c.moveBy(0, -amount);
                case SOUTH:
                    return c.moveBy(0, amount);
                case EAST:
                    return c.moveBy(amount, 0);
                case WEST:
                    return c.moveBy(-amount, 0);
                default:
                    throw new RuntimeException("Unknown direction " + d);
            }
        }
    }

    enum Action {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        LEFT,
        RIGHT,
        FORWARD;

        public static Action parse(char c) {
            switch (c) {
                case 'N':
                    return NORTH;
                case 'S':
                    return SOUTH;
                case 'E':
                    return EAST;
                case 'W':
                    return WEST;
                case 'L':
                    return LEFT;
                case 'R':
                    return RIGHT;
                case 'F':
                    return FORWARD;
                default:
                    throw new RuntimeException("Unknown action " + c);
            }
        }
    }
}
