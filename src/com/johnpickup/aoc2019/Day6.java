package com.johnpickup.aoc2019;

import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day6 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test2.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Orbit> orbits = stream
                        .filter(s -> !s.isEmpty())
                        .map(Orbit::new)
                        .collect(Collectors.toList());

                for (Orbit orbit : orbits) {
                    new Planet(orbit.inner);
                    new Planet(orbit.outer);
                }

                for (Orbit orbit : orbits) {
                    Planet inner = Planet.byName(orbit.inner);
                    Planet outer = Planet.byName(orbit.outer);
                    inner.addOrbiter(outer);
                }

                List<Planet> centres = Planet.allPlanets.values().stream().filter(p -> p.orbiting == null).collect(Collectors.toList());
                System.out.println("Centre " + centres);

                Planet centre = centres.get(0);

                long part1 = centre.totalOrbits();
                System.out.println("Part 1: " + part1);

                long part2 = 0L;
                Planet p = Planet.byName("YOU").orbiting;
                Planet target = Planet.byName("SAN").orbiting;
                while (!target.isInOrbitAround(p)) {
                    p = p.orbiting;
                    part2++;
                }
                while (!target.equals(p)) {
                    target = target.orbiting;
                    part2++;
                }
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Orbit {
        final String inner;
        final String outer;

        Orbit(String line) {
            String[] parts = line.split("\\)");
            inner = parts[0];
            outer = parts[1];
        }
    }

    @EqualsAndHashCode(exclude = {"orbiting", "orbiters"})
    static class Planet {
        static final Map<String, Planet> allPlanets = new HashMap<>();
        final String name;
        List<Planet> orbiters = new ArrayList<>();
        Planet orbiting;

        Planet(String name) {
            this.name = name;
            allPlanets.put(name, this);
        }

        static Planet byName(String name) {
            return allPlanets.get(name);
        }

        @Override
        public String toString() {
            return name;
        }

        public long totalOrbits() {
            return totalOrbits(0);
        }
        private long totalOrbits(int level) {
            long result = level;
            for (Planet orbiter : orbiters) {
                result += orbiter.totalOrbits(level + 1);
            }
            return result;
        }

        public void addOrbiter(Planet planet) {
            this.orbiters.add(planet);
            planet.orbiting = this;
        }

        public boolean isInOrbitAround(Planet p) {
            if (name.equals(p.name)) return true;
            if (this.orbiting == null) return false;
            return this.orbiting.isInOrbitAround(p);
        }
    }
}
