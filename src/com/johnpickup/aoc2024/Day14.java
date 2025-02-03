package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.Coord;
import lombok.Data;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 {
    static int WIDTH;
    static int HEIGHT;
    static int MID_X;
    static int MID_Y;

    public static void main(String[] args) {
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2024/day14/Day14";
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                ,prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Robot> robots = stream
                        .filter(s -> !s.isEmpty())
                        .map(Robot::new)
                        .collect(Collectors.toList());

                if (inputFilename.contains("test")) {
                    WIDTH = 11;
                    HEIGHT = 7;
                } else {
                    WIDTH = 101;
                    HEIGHT = 103;
                }
                MID_X = WIDTH / 2;
                MID_Y = HEIGHT / 2;

                for (int i = 0; i < 100; i++) {
                    robots.forEach(Robot::move);
                }
                Map<Integer, Long> quadrantCounts = robots.stream().map(Robot::quadrant)
                        .filter(q -> !q.equals(0))
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                long part1 = quadrantCounts.values().stream()
                        .reduce(1L, (a, b) -> a * b);
                long total = quadrantCounts.values().stream().reduce(0L, Long::sum);
                System.out.println("Part 1: " + part1);

                // reload to initial state
                robots = Files.lines(Paths.get(inputFilename))
                        .filter(s -> !s.isEmpty())
                        .map(Robot::new)
                        .collect(Collectors.toList());
                long part2 = 0L;

                while (!isTree(robots)) {
                    part2++;
                    robots.forEach(Robot::move);
//                    if (potentialTree(robots)) {
//                        System.out.println(part2 + " -------------------------------------------------------------------------");
//                        displayRobots(robots);
//                        System.out.println();
//                    }
                }
                displayRobots(robots, false);
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    // are at least half the robots horizontally adjacent?
    private static boolean isTree(List<Robot> robots) {
        int horizontalAdjacent = 0;
        Set<Coord> locations = robots.stream().map(Robot::getPosition).collect(Collectors.toSet());
        for (Coord location1 : locations) {
            for (Coord location2 : locations) {
                if ((location1.getY() == location2.getY())
                    && (Math.abs(location1.getX() - location2.getX()) == 1))
                    horizontalAdjacent ++;
            }
        }

        return horizontalAdjacent > robots.size() / 2;
    }

    // Doesn't work - it's not symmetrical
    private static boolean isXmasTree(List<Robot> robots) {
        Map<Coord, Long> locations = robots.stream().map(Robot::getPosition).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        boolean result = true;
        for (Map.Entry<Coord, Long> entry : locations.entrySet()) {
            Coord reflect = reflect(entry.getKey());
            result &= locations.containsKey(reflect) && locations.get(reflect).equals(entry.getValue());
        }
        // has symmetry
        return result;
    }

    private static Coord reflect(Coord c) {
        return new Coord(MID_X * 2 - c.getX(), c.getY());
    }

    private static void displayRobots(List<Robot> robots, boolean middleGap) {
        Map<Coord, Long> locations = robots.stream().map(Robot::getPosition).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Coord coord = new Coord(x, y);
                if (middleGap && ((coord.getX() == MID_X || coord.getY() == MID_Y))) System.out.print(' ');
                else if (locations.containsKey(coord)) System.out.print(locations.get(coord));
                else System.out.print(".");
            }
            System.out.println();
        }
    }

    @ToString
    @Data
    static class Robot {
        Coord position;
        Coord velocity;

        static final Pattern pattern = Pattern.compile("p=(\\-?[0-9]+),(\\-?[0-9]+) v=(\\-?[0-9]+),(\\-?[0-9]+)");

        Robot(String line) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) throw new RuntimeException("Invalid robot input: " + line);
            int px = Integer.parseInt(matcher.group(1));
            int py = Integer.parseInt(matcher.group(2));
            int vx = Integer.parseInt(matcher.group(3));
            int vy = Integer.parseInt(matcher.group(4));
            position = new Coord(px, py);
            velocity = new Coord(vx, vy);
        }

        void move() {
            int newX = (position.getX() + velocity.getX() + WIDTH) % WIDTH;
            int newY = (position.getY() + velocity.getY() + HEIGHT) % HEIGHT;
            position = new Coord(newX, newY);
        }

        int quadrant() {
            if (position.getX() == MID_X || position.getY() == MID_Y) return 0;
            if (position.getX() < MID_X && position.getY() < MID_Y) return 1;
            if (position.getX() > MID_X && position.getY() < MID_Y) return 2;
            if (position.getX() < MID_X && position.getY() > MID_Y) return 3;
            if (position.getX() > MID_X && position.getY() > MID_Y) return 4;
            System.out.printf("%d %d %n", MID_X, MID_Y);
            throw new RuntimeException("Impossible " + position);
        }
    }

}
