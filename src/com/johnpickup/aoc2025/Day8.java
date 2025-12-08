package com.johnpickup.aoc2025;

import com.johnpickup.util.Coord3D;
import com.johnpickup.util.Maps;
import com.johnpickup.util.Pair;
import com.johnpickup.util.Sets;
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
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Coord3D> coords = stream
                        .filter(s -> !s.isEmpty())
                        .map(Coord3D::new)
                        .toList();

                PlayGround playGround = new PlayGround(coords);

                System.out.println("Part 1: " + playGround.part1(isTest ? 10 : 1000));
                System.out.println("Part 2: " + playGround.part2());
break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class PlayGround {
        final List<Coord3D> coords;

        public long part1(int limit) {
            Map<Pair<Coord3D, Coord3D>, Long> distances = calcDistances(coords);
            List<Pair<Coord3D, Coord3D>> shortDistances = shortestDistances(distances, limit);
            System.out.println("SHORTEST DISTANCES\n");
            for (Pair<Coord3D, Coord3D> s : shortDistances) {
                System.out.printf("%d %s%n", Coord3D.straightLineDistanceSqr(s.getValue1(), s.getValue2()), s);
            }

            Set<Set<Pair<Coord3D, Coord3D>>> connectionGroups = getConnectionGroups(shortDistances);
            System.out.println("CONNECTION GROUPS\n" + connectionGroups);

            Map<Set<Pair<Coord3D, Coord3D>>, Integer> groupSizes = new HashMap<>();

            for (Set<Pair<Coord3D, Coord3D>> connectionGroup : connectionGroups) {
                    groupSizes.put(connectionGroup, countBoxes(connectionGroup));
            }

            Map<Integer, Set<Set<Pair<Coord3D, Coord3D>>>> groupsBySize = Maps.reverseMap(groupSizes);
            groupsBySize.entrySet().stream()
                    .sorted((a, b) -> b.getKey() - a.getKey()).forEach(g ->
            {
                System.out.println(g.getKey());
                System.out.println(g.getValue());
            });

            return 0;
        }

        private int countBoxes(Set<Pair<Coord3D, Coord3D>> connectionGroup) {
            return distinctCoords(connectionGroup).size();
        }

        private List<Pair<Coord3D, Coord3D>> shortestDistances(Map<Pair<Coord3D, Coord3D>, Long> distances, int limit) {
            Map<Long, Set<Pair<Coord3D, Coord3D>>> byDistance = new TreeMap<>(Maps.reverseMap(distances));
            List<Pair<Coord3D, Coord3D>> result = new ArrayList<>();
            while (result.size() < limit && !byDistance.isEmpty()) {
                while (result.size() < limit) {
                    long shortest = byDistance.keySet().stream().findFirst().orElseThrow();
                    Pair<Coord3D, Coord3D> first = byDistance.get(shortest).stream().findFirst().orElseThrow();
                    result.add(first);
                    byDistance.get(shortest).remove(first);
                    if (byDistance.get(shortest).isEmpty()) {
                        byDistance.remove(shortest);
                    }
                }
            }
            return result;
        }

        private Map<Pair<Coord3D, Coord3D>, Long> calcDistances(List<Coord3D> coords) {
            Map<Pair<Coord3D, Coord3D>, Long> result = new HashMap<>();
            for (Coord3D c1 : coords) {
                for (Coord3D c2 : coords) {
                    if (c1.compareTo(c2) < 0) {
                        result.put(new Pair<>(c1, c2), Coord3D.straightLineDistanceSqr(c1, c2));
                    }
                }
            }
            return result;
        }

        private Set<Set<Pair<Coord3D, Coord3D>>> getConnectionGroups(List<Pair<Coord3D, Coord3D>> connections) {
            List<Pair<Coord3D, Coord3D>> unusedConnections = new ArrayList<>(connections);
            Set<Set<Pair<Coord3D, Coord3D>>> result = new HashSet<>();
            while (!unusedConnections.isEmpty()) {
                Pair<Coord3D, Coord3D> connection = unusedConnections.stream().findFirst().orElseThrow();
                System.out.println("Adding connection " + connection);
                Set<Pair<Coord3D, Coord3D>> newGroup = new HashSet<>();
                newGroup.add(connection);
                result.add(newGroup);
//                Set<Pair<Coord3D, Coord3D>> existingGroup = findOneEnd(result, connection);
//                if (existingGroup != null) {
//                    existingGroup.add(connection);
//                } else {
//                    Set<Pair<Coord3D, Coord3D>> newGroup = new HashSet<>();
//                    newGroup.add(connection);
//                    result.add(newGroup);
//                }
                unusedConnections.remove(connection);
                System.out.printf("BEFORE JOINING: %n" + showGroups(result));
                result = joinAnyConnected(result);
                System.out.printf("AFTER JOINING: %n"  + showGroups(result));

            }
            return result;
        }

        private String showGroups(Set<Set<Pair<Coord3D, Coord3D>>> groups) {
            final StringBuilder sb = new StringBuilder();
            groups.forEach(g -> {
                sb.append(String.format("%s%n", g));
            });
            return sb.toString();
        }

        private Set<Set<Pair<Coord3D, Coord3D>>> joinAnyConnected(Set<Set<Pair<Coord3D, Coord3D>>> groups) {
            Set<Set<Pair<Coord3D, Coord3D>>> result = new HashSet<>(groups);
            boolean found;
            do {
                found = false;
                Set<Pair<Coord3D, Coord3D>> found1 = null;
                Set<Pair<Coord3D, Coord3D>> found2 = null;

                for (Set<Pair<Coord3D, Coord3D>> g1 : result) {
                    for (Set<Pair<Coord3D, Coord3D>> g2 : result) {
                        if (!g1.equals(g2) && (!found)) {
                            if (canConnectGroups(g1, g2)) {
                                found1 = g1;
                                found2 = g2;
                                found = true;
                                System.out.printf("Joined %s to %s%n", g1, g2);
                            }
                        }
                    }
                }
                if (found) {
                    result.add(Sets.union(found1, found2));
                    result.remove(found1);
                    result.remove(found2);
                }
                break;
            } while (!found);
            return result;
        }

        private Set<Set<Pair<Coord3D, Coord3D>>> joinAnyConnectedBroken(Set<Set<Pair<Coord3D, Coord3D>>> groups) {
            Set<Set<Pair<Coord3D, Coord3D>>> result = new HashSet<>(groups);
            Set<Pair<Coord3D, Coord3D>> connectableGroup = null;
            Set<Pair<Coord3D, Coord3D>> group = null;
            for (Set<Pair<Coord3D, Coord3D>> group1 : groups) {
                group = group1;
                connectableGroup = canConnect(group, result);
                if (connectableGroup != null) break;
            }
            if (connectableGroup != null) {
                connectableGroup.addAll(group);
                result.remove(group);
            }
            return result;
        }

        private Set<Pair<Coord3D, Coord3D>> canConnect(Set<Pair<Coord3D, Coord3D>> group, Set<Set<Pair<Coord3D, Coord3D>>> groups) {
            for (Set<Pair<Coord3D, Coord3D>> existingGroup : groups) {
                if (canConnectGroups(group, existingGroup)) return existingGroup;
            }
            return null;
        }

        private boolean canConnectGroups(Set<Pair<Coord3D, Coord3D>> g1, Set<Pair<Coord3D, Coord3D>> g2) {
            if (g1.equals(g2)) return false;
            Set<Coord3D> g1Coords = distinctCoords(g1);
            Set<Coord3D> g2Coords = distinctCoords(g2);
            return !Sets.intersection(g1Coords, g2Coords).isEmpty();
        }

        private Set<Coord3D> distinctCoords(Set<Pair<Coord3D, Coord3D>> connections) {
            HashSet<Coord3D> result = connections.stream().map(Pair::getValue1).collect(Collectors.toCollection(HashSet::new));
            result.addAll(connections.stream().map(Pair::getValue2).collect(Collectors.toSet()));
            return result;
        }

        private Set<Pair<Coord3D, Coord3D>> findOneEnd(Set<Set<Pair<Coord3D, Coord3D>>> groups, Pair<Coord3D, Coord3D> connection) {
            for (Set<Pair<Coord3D, Coord3D>> group : groups) {
                for (Pair<Coord3D, Coord3D> existingConnection : group) {
                    if ((existingConnection.getValue1().equals(connection.getValue1()))
                        || (existingConnection.getValue1().equals(connection.getValue2())) ||(existingConnection.getValue2().equals(connection.getValue1())) ||(existingConnection.getValue2().equals(connection.getValue2()))) {
                        return group;
                    }
                }
            }
            return null;
        }

//        private List<Pair<Coord3D, Coord3D>> getConnections(List<Pair<Coord3D, Coord3D>> distances) {
//            List<Pair<Coord3D, Coord3D>> connections = new ArrayList<>();
//            Map<Pair<Coord3D, Coord3D>, Long> unusedDistances = new HashMap<>(distances);
//
//            while (!unusedDistances.isEmpty()) {
//                Map<Long, Set<Pair<Coord3D, Coord3D>>> byDistance = Maps.reverseMap(unusedDistances);
//                Long closest = byDistance.keySet().stream().sorted().findFirst().orElseThrow();
//                Set<Pair<Coord3D, Coord3D>> bestPairs = byDistance.get(closest);
//                Pair<Coord3D, Coord3D> bestPair = bestPairs.stream().findFirst().orElseThrow();
//System.out.printf("Best pair = %s (%d)%n", bestPair, closest);
//                unusedDistances.remove(bestPair);
//                connections.add(bestPair);
//            }
//            return connections;
//        }

        public long part2() {
            return 0;
        }
    }

}
