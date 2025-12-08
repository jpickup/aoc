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

public class Day8Wrong {
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
        Map<Coord3D, Integer> coordIndexes = new TreeMap<>();

        public long part1(int limit) {
            int i = 0;
            for (Coord3D coord : coords) {
                coordIndexes.put(coord, i++);
            }

            Map<Pair<Coord3D, Coord3D>, Long> distances = calcDistances(coords);
            List<Pair<Coord3D, Coord3D>> orderedByDistance = orderedDistances(distances);
            System.out.println("CONNECTIONS\n" + dumpConnections(orderedByDistance));

            Set<Set<Pair<Coord3D, Coord3D>>> connectionGroups = getConnectionGroups(orderedByDistance, limit);
            System.out.println("CONNECTION GROUPS\n" + dumpConnectionGroups(connectionGroups));

            Map<Set<Pair<Coord3D, Coord3D>>, Integer> groupSizes = new HashMap<>();

            for (Set<Pair<Coord3D, Coord3D>> connectionGroup : connectionGroups) {
                groupSizes.put(connectionGroup, countBoxes(connectionGroup));
            }

            TreeMap<Integer, Set<Set<Pair<Coord3D, Coord3D>>>> groupsBySize = new TreeMap<>(Maps.reverseMap(groupSizes));

            for (Map.Entry<Integer, Set<Set<Pair<Coord3D, Coord3D>>>> groupsEntry : groupsBySize.entrySet()) {
                System.out.printf("--- %d Groups of size %d %n", groupsEntry.getValue().size(), groupsEntry.getKey());
                groupsEntry.getValue().forEach(g -> g.forEach(c ->
                        System.out.printf("%s-%s, ", coordIndexes.get(c.getValue1()), coordIndexes.get(c.getValue2()))));
                System.out.println();
            }


            long part1 = 1;
            int topN=0;
            while (topN < 3) {
                Set<Set<Pair<Coord3D, Coord3D>>> largestGroups = groupsBySize.lastEntry().getValue();
                while (!largestGroups.isEmpty() && topN < 3) {
                    Set<Pair<Coord3D, Coord3D>> largest = largestGroups.stream().findFirst().orElseThrow();
                    part1 *= countBoxes(largest);
                    largestGroups.remove(largest);
                    topN++;
                }
                if (groupsBySize.lastEntry().getValue().isEmpty()) {
                    groupsBySize.remove(groupsBySize.lastEntry().getKey());
                }
            }

            return part1;
        }

        private int countBoxes(Set<Pair<Coord3D, Coord3D>> connectionGroup) {
            Set<Coord3D> allCoords = distinctCoords(connectionGroup);
            return allCoords.size();
        }

        private List<Pair<Coord3D, Coord3D>> orderedDistances(Map<Pair<Coord3D, Coord3D>, Long> distances) {
            Map<Long, Set<Pair<Coord3D, Coord3D>>> byDistance = new TreeMap<>(Maps.reverseMap(distances));
            List<Pair<Coord3D, Coord3D>> result = new ArrayList<>();
            while (!byDistance.isEmpty()) {
                    long shortest = byDistance.keySet().stream().findFirst().orElseThrow();
                    Pair<Coord3D, Coord3D> first = byDistance.get(shortest).stream().findFirst().orElseThrow();
                    result.add(first);
                    byDistance.get(shortest).remove(first);
                    if (byDistance.get(shortest).isEmpty()) {
                        byDistance.remove(shortest);
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

        private Set<Set<Pair<Coord3D, Coord3D>>> getConnectionGroups(List<Pair<Coord3D, Coord3D>> connections, int limit) {
            int numConnectionsUsed = 0;
            List<Pair<Coord3D, Coord3D>> unusedConnections = new ArrayList<>(connections);
            Set<Set<Pair<Coord3D, Coord3D>>> result = new HashSet<>();
            while (!unusedConnections.isEmpty() && numConnectionsUsed < limit) {
                Pair<Coord3D, Coord3D> connection = unusedConnections.stream().findFirst().orElseThrow();
                if (!oneGroupAlreadyContainsConnection(connection, result)) {
                    Set<Pair<Coord3D, Coord3D>> newGroup = new HashSet<>();
                    newGroup.add(connection);
                    result.add(newGroup);
                    numConnectionsUsed++;
                }
                unusedConnections.remove(connection);
                result = joinAnyConnected(result);
            }
            return result;
        }

        private boolean oneGroupAlreadyContainsConnection(Pair<Coord3D, Coord3D> connection, Set<Set<Pair<Coord3D, Coord3D>>> groups) {
            return groups.stream().anyMatch(g -> groupAlreadyContains(g, connection));
        }

        private boolean groupAlreadyContains(Set<Pair<Coord3D, Coord3D>> group, Pair<Coord3D, Coord3D> connection) {
            return distinctCoords(group).contains(connection.getValue1())
                    && distinctCoords(group).contains(connection.getValue2());
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
                            || (existingConnection.getValue1().equals(connection.getValue2())) || (existingConnection.getValue2().equals(connection.getValue1())) || (existingConnection.getValue2().equals(connection.getValue2()))) {
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



        private String dumpConnections(List<Pair<Coord3D, Coord3D>> connections) {
            StringBuilder sb = new StringBuilder();
            for (Pair<Coord3D, Coord3D> connection : connections) {
                sb.append(connection).append("\t = ").append(Coord3D.straightLineDistanceSqr(connection.getValue1(), connection.getValue2()));
                sb.append('\n');
            }
            return sb.toString();
        }

        private String dumpConnectionGroups(Set<Set<Pair<Coord3D, Coord3D>>> connectionGroups) {
            StringBuilder sb = new StringBuilder();
            for (Set<Pair<Coord3D, Coord3D>> connectionGroup : connectionGroups) {
                sb.append(connectionGroup);
                sb.append(" (").append(countBoxes(connectionGroup)).append(")");
                sb.append('\n');
            }
            return sb.toString();
        }
    }


}
