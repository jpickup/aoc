package com.johnpickup.aoc2019;

import com.johnpickup.util.Coord3D;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day12 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
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
                List<Moon> moons = stream
                        .filter(s -> !s.isEmpty())
                        .map(Moon::new)
                        .collect(Collectors.toList());

                int stepCount = isTest ? 100 : 1000;

                for (int step = 1; step <= stepCount; step++) {
                    Map<Moon, Coord3D> deltaVelocities = calculateDeltas(moons);
                    applyVelocities(moons, deltaVelocities);
                }

                long part1 = moons.stream().map(Moon::calcEnergy).reduce(0L, Long::sum);
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

    private static void applyVelocities(List<Moon> moons, Map<Moon, Coord3D> deltaVelocities) {
        for (Moon moon : moons) {
            Coord3D delta = deltaVelocities.get(moon);
            moon.applyDelta(delta);
        }
    }

    private static Map<Moon, Coord3D> calculateDeltas(List<Moon> moons) {
        Map<Moon, Coord3D> result = new HashMap<>();
        for (Moon moon1 : moons) {
            Coord3D delta = Coord3D.ORIGIN;
            for (Moon moon2 : moons) {
                if (!moon1.equals(moon2)) {
                    delta = calcDelta(moon1, moon2, delta);
                }
            }
            result.put(moon1, delta);
        }
        return result;
    }

    private static Coord3D calcDelta(Moon moon, Moon other, Coord3D delta) {
        int dx = (int)Math.signum(other.getPosition().getX() - moon.getPosition().getX());
        int dy = (int)Math.signum(other.getPosition().getY() - moon.getPosition().getY());
        int dz = (int)Math.signum(other.getPosition().getZ() - moon.getPosition().getZ());
        return new Coord3D(delta.getX() + dx, delta.getY() + dy, delta.getZ() + dz);
    }

    @Data
    static class Moon {
        final Coord3D initialPosition;
        Coord3D position;
        Coord3D velocity;

        Moon(String line) {
            line = line.replace(" ","")
                    .replace("<","").replace(">","")
                    .replace("=","")
                    .replace("x","")
                    .replace("y","")
                    .replace("z","");

            initialPosition = new Coord3D(line);
            position = new Coord3D(initialPosition);
            velocity = Coord3D.ORIGIN;
        }

        public void applyDelta(Coord3D deltaVelocity) {
            velocity = new Coord3D(velocity.getX() + deltaVelocity.getX(), velocity.getY() + deltaVelocity.getY(), velocity.getZ() + deltaVelocity.getZ());
            position = new Coord3D(position.getX() + velocity.getX(), position.getY() + velocity.getY(), position.getZ() + velocity.getZ());
        }

        public long calcEnergy() {
            long potential = position.distanceFrom(Coord3D.ORIGIN);
            long kinetic = velocity.distanceFrom(Coord3D.ORIGIN);
            return potential * kinetic;
        }
    }
}
