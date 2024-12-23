package com.johnpickup.aoc2024;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day23 {
    static boolean isTest;

    public static void main(String[] args) {
        String day = new Object() {
        }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
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

                Connections connections = new Connections(lines);

                long part1 = connections.part1();
                System.out.println("Part 1: " + part1);
                String part2 = connections.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Connections {
        final Set<Connection> connections;
        final Set<String> computers;

        Connections(List<String> lines) {
            connections = lines.stream().map(Connection::new).collect(Collectors.toSet());
            computers = new HashSet<>();
            computers.addAll(connections.stream().map(c -> c.computer1).collect(Collectors.toSet()));
            computers.addAll(connections.stream().map(c -> c.computer2).collect(Collectors.toSet()));
        }


        public long part1() {
            Set<Group> groups = new HashSet<>();
            for (String computer : computers.stream().filter(c -> c.startsWith("t")).collect(Collectors.toList())) {
                Set<Connection> connectedToThis = connections.stream().filter(con -> con.hasComputer(computer)).collect(Collectors.toSet());
                for (Connection conn : connectedToThis) {
                    groups.addAll(findTriples(conn));
                }

            }
            for (Group group : groups) {
                System.out.println(group);
            }

            return groups.size();
        }

        private Set<String> directlyConnectedTo(String computer) {
            Set<String> result = new HashSet<>();
            result.addAll(connections.stream().filter(conn -> conn.hasComputer(computer)).map(Connection::getComputer1).collect(Collectors.toSet()));
            result.addAll(connections.stream().filter(conn -> conn.hasComputer(computer)).map(Connection::getComputer2).collect(Collectors.toSet()));
            result.remove(computer);
            return result;
        }

        private Collection<Group> findTriples(Connection connection) {
            Collection<Group> result = new HashSet<>();
            String c1 = connection.computer1;
            String c2 = connection.computer2;
            Set<String> c3s = new HashSet<>();
            c3s.addAll(directlyConnectedTo(c1));
            c3s.addAll(directlyConnectedTo(c2));
            c3s.remove(c1);
            c3s.remove(c2);
            for (String c3 : c3s) {
                Connection con1 = new Connection(c1, c2);
                Connection con2 = new Connection(c2, c3);
                Connection con3 = new Connection(c1, c3);
                if (connections.contains(con1) && connections.contains(con2) && connections.contains(con3))
                    result.add(new Group(c1, c2, c3));
            }
            return result;
        }

        public String part2() {
            Set<Group> groups = findGroups();
            List<Group> sorted = groups.stream().sorted(Comparator.comparingLong(Group::size)).collect(Collectors.toList());
            Group largestGroup = sorted.get(sorted.size() - 1);

            return largestGroup.password();
        }

        private Set<Group> findGroups() {
            Set<Group> result = new HashSet<>();
            result.addAll(computers.stream().map(Group::new).collect(Collectors.toSet()));

            long prevSize;
            do {
                prevSize = result.size();
                Set<Group> newGroups = new HashSet<>(result);
                for (Group group : result) {
                    newGroups.addAll(expandGroup(group));
                }
                result = newGroups;
            } while (result.size() > prevSize);
            return result;
        }

        private Set<Group> expandGroup(Group group) {
            Set<Group> result = new HashSet<>();
            for (String computer : computers) {
                boolean connectedToAll = true;
                if (!group.contains(computer)) {
                    connectedToAll &= isConnectedToAll(computer, group);
                }
                if (connectedToAll) {
                    Group newGroup = new Group(group);
                    newGroup.add(computer);
                    result.add(newGroup);
                }
            }
            return result;
        }

        private boolean isConnectedToAll(String computer, Group group) {
            return group.computers.stream().allMatch(c2 -> connections.contains(new Connection(computer, c2)));
        }


        @Data
        @RequiredArgsConstructor
        static class Connection {
            final String computer1;
            final String computer2;

            Connection(String line) {
                String[] parts = line.split("-");
                computer1 = parts[0];
                computer2 = parts[1];
            }

            public boolean hasComputer(String computer) {
                return computer1.equals(computer) || computer2.equals(computer);
            }

            @Override
            public boolean equals(Object o) {
                Connection other = (Connection) o;
                return (this.computer1.equals(other.computer1) && this.computer2.equals(other.computer2))
                        || (this.computer2.equals(other.computer1) && this.computer1.equals(other.computer2));
            }

            @Override
            public int hashCode() {
                return computer1.hashCode() + computer2.hashCode();
            }
        }

        @EqualsAndHashCode
        @ToString
        static class Group {
            final Set<String> computers = new HashSet<>();

            Group(String c1, String c2, String c3) {
                computers.add(c1);
                computers.add(c2);
                computers.add(c3);
            }

            public Group(Group group) {
                computers.addAll(group.computers);
            }

            public Group(String computer) {
                computers.add(computer);
            }

            void add(String computer) {
                computers.add(computer);
            }

            public long size() {
                return computers.size();
            }

            public boolean contains(String computer) {
                return computers.contains(computer);
            }

            public String password() {
                return computers.stream().sorted(String::compareTo).collect(Collectors.joining(","));
            }
        }
    }
}
