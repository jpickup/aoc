package com.johnpickup.aoc2025;

import com.johnpickup.util.Coord3D;
import com.johnpickup.util.Maps;
import com.johnpickup.util.Pair;
import com.johnpickup.util.Sets;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day8 {
    static boolean isTest;

    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object() {
        });
        for (String inputFilename : inputFilenames) {

            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Coord3D> coords = stream
                        .filter(s -> !s.isEmpty())
                        .map(Coord3D::new)
                        .toList();

                Playground playground = new Playground(coords);

                System.out.println("Part 1: " + playground.part1(isTest ? 10 : 1000));
                System.out.println("Part 2: " + playground.part2());
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Playground {
        final List<Coord3D> coords;

        public long part1(int limit) {
            List<Connection> connections = generateConnections(coords);
            connections.sort(Comparator.comparing(a -> a.distanceSqr));
            List<Circuit> circuits = new ArrayList<>();

            int connectionsUsed = 0;
            while (connectionsUsed < limit) {
                Connection first = connections.getFirst();
                if (circuits.stream().noneMatch(c -> c.canAdd(first))) {
                    Circuit circuit = new Circuit();
                    circuit.add(first);
                    circuits.add(circuit);
                } else {
                    circuits.stream().filter(c -> c.canAdd(first)).findFirst().orElseThrow().add(first);
                }
                circuits = joinCircuits(circuits);
                connections.remove(first);
                connectionsUsed++;
            }

            long part1 = 1;
            Map<Circuit, Integer> circuitLengths = circuits.stream().collect(Collectors.toMap(c -> c, Circuit::length));
            TreeMap<Integer, Set<Circuit>> circuitsByLength = new TreeMap<>(Maps.reverseMap(circuitLengths));
            int topN=0;
            while (topN < 3) {
                Set<Circuit> longestCircuits = circuitsByLength.lastEntry().getValue();
                while (!longestCircuits.isEmpty() && topN < 3) {
                    Circuit longestCircuit = longestCircuits.stream().findFirst().orElseThrow();
                    part1 *= longestCircuit.length();
                    longestCircuits.remove(longestCircuit);
                    topN++;
                }
                if (longestCircuits.isEmpty()) {
                    circuitsByLength.remove(circuitsByLength.lastEntry().getKey());
                }
            }
            return part1;
        }

        public long part2() {
            List<Connection> connections = generateConnections(coords);
            connections.sort(Comparator.comparing(a -> a.distanceSqr));
            List<Circuit> circuits = new ArrayList<>();
            Set<Coord3D> connected = new HashSet<>();

            Connection last = null;
            while (!isComplete(connected, circuits) && !connections.isEmpty()) {
                Connection first = connections.getFirst();
                connected.addAll(first.getEnds());
                last = first;
                if (circuits.stream().noneMatch(c -> c.canAdd(first))) {
                    Circuit circuit = new Circuit();
                    circuit.add(first);
                    circuits.add(circuit);
                } else {
                    circuits.stream().filter(c -> c.canAdd(first)).findFirst().orElseThrow().add(first);
                }
                circuits = joinCircuits(circuits);
                connections.remove(first);
            }
            return Optional.ofNullable(last).orElseThrow()
                    .getEnds().stream().map(Coord3D::getX)
                    .map(x -> (long)x)
                    .reduce(1L, (a, b) -> a*b);
        }

        private boolean isComplete(Set<Coord3D> boxes, List<Circuit> circuits) {
            return circuits.size() == 1 && boxes.size() == coords.size();
        }

        private List<Circuit> joinCircuits(List<Circuit> circuits) {
            List<Circuit> result = new ArrayList<>();
            for (Circuit circuit : circuits) {
                if (result.stream().noneMatch(c -> c.canJoin(circuit))) {
                    result.add(circuit);
                } else {
                    result.stream().filter(c -> c.canJoin(circuit)).findFirst().orElseThrow().join(circuit);
                }
            }
            return result;
        }

        private List<Connection> generateConnections(List<Coord3D> coords) {
            List<Connection> result = new ArrayList<>();
            for (Coord3D c1 : coords) {
                for (Coord3D c2 : coords) {
                    if (c1.compareTo(c2) < 0) {
                        result.add(new Connection(c1, c2));
                    }
                }
            }
            return result;
        }
    }

    @Data
    static class Connection {
        private final Pair<Coord3D, Coord3D> ends;
        private final long distanceSqr;

        Connection(Coord3D start, Coord3D end) {
            ends = new Pair<>(start, end);
            distanceSqr = Coord3D.straightLineDistanceSqr(start, end);
        }

        Set<Coord3D> getEnds() {
            return new HashSet<>(Arrays.asList(ends.getValue1(), ends.getValue2()));
        }
    }

    @Data
    static class Circuit {
        private final Set<Coord3D> boxes = new HashSet<>();

        public boolean contains(Coord3D box) {
            return boxes.contains(box);
        }

        public boolean canAdd(Connection connection) {
            return boxes.isEmpty() || Sets.intersection(boxes, connection.getEnds()).size() == 1;
        }

        public void add(Connection connection) {
            if (!canAdd(connection)) throw new RuntimeException("Can't add " + connection);
            boxes.addAll(connection.getEnds());
        }

        public boolean canJoin(Circuit circuit) {
            return boxes.stream().anyMatch(circuit::contains);
        }

        public void join(Circuit circuit) {
            boxes.addAll(circuit.boxes);
        }

        public int length() {
            return boxes.size();
        }
    }
}
