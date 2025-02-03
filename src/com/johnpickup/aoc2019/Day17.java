package com.johnpickup.aoc2019;

import com.johnpickup.util.Coord;
import com.johnpickup.util.Direction;
import com.johnpickup.util.SparseGrid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day17 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                VacuumRobot robot = new VacuumRobot(lines.get(0));

                long part1 = robot.part1();
                System.out.println("Part 1: " + part1);
                long part2 = robot.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class VacuumRobot {
        final Program program;
        final SparseGrid<Character> grid = new SparseGrid<>();
        VacuumRobot(String line) {
            program = new Program(line);
            program.setOutputConsumer(this::part1OutputConsumer);
        }

        Coord position = Coord.ORIGIN;

        private void part1OutputConsumer(long value) {
            if (value == 10L) {
                position = new Coord(0, position.getY()+1);
            } else {
                grid.setCell(position, (char)value);
                position = new Coord(position.getX()+1, position.getY());
            }
        }

        long part1() {
            program.execute();
            System.out.println(grid);
            Set<Coord> intersections = findIntersections(grid);
            System.out.println(intersections);
            return intersections.stream().map(this::alignmentParameter).reduce(0L, Long::sum);
        }

        public long part2() {
            List<String> fullPath = calcPath();
            System.out.println("Full path " + fullPath);
            // 82 elements, 169 charts with commas
            // Max of 20 chars in a sub-sequence
            Map<List<String>, Integer> subLists = calcValidSubLists(fullPath);

            List<List<String>> subListsOrderedByScore = subLists.entrySet().stream()
                    .filter(e -> e.getKey().size() >= 6)
                    .sorted((a, b) -> (b.getValue() * b.getKey().size() - a.getValue() * a.getKey().size())) // weight
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // find three valid sub-lists that can build the whole path list.
            List<List<String>> matchingSubList = findSubList(fullPath, subListsOrderedByScore);
            System.out.println(matchingSubList);

            part2RobotInput = buildRobotInput(matchingSubList) + "\nn\n";
            System.out.println(part2RobotInput);

            program.reset();
            program.setMemory(0L, 2L);
            program.setInputSupplier(this::part2InputSupplier);
            program.setOutputConsumer(this::part2OutputConsumer);
            program.execute();
            return part2Result;
        }

        private String buildRobotInput(List<List<String>> matchingSubList) {
            List<String> result = new ArrayList<>();
            Map<List<String>, String> commandsToFunctions = mapToFunctions(matchingSubList);
            System.out.println(commandsToFunctions);
            List<String> functionCalls = convertToFunctionCalls(matchingSubList, commandsToFunctions);

            result.add(String.join(",", functionCalls));
            List<List<String>> orderedFunctionCommands = commandsToFunctions.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            orderedFunctionCommands.forEach(fc -> result.add(String.join(",", fc)));
            return String.join("\n", result);
        }

        private List<String> convertToFunctionCalls(List<List<String>> list, Map<List<String>, String> commandsToFunctions) {
            return list.stream().map(commandsToFunctions::get).collect(Collectors.toList());
        }

        private Map<List<String>, String> mapToFunctions(List<List<String>> matchingSubList) {
            Map<List<String>, String> result = new HashMap<>();
            Set<List<String>> uniqueLists = new HashSet<>(matchingSubList);
            char command = 'A';
            for (List<String> uniqueList : uniqueLists) {
                result.put(uniqueList, command++ + "");
            }
            return result;
        }

        private List<List<String>> findSubList(List<String> list, List<List<String>> subListsOrderedByScore) {
            return findSubLists(list, subListsOrderedByScore).get(0);
        }
        private List<List<List<String>>> findSubLists(List<String> list, List<List<String>> subListsOrderedByScore) {
            if (subListsOrderedByScore.contains(list)) {
                return Collections.singletonList(Collections.singletonList(list));
            }

            List<List<String>> validPrefixes = subListsOrderedByScore.stream().filter(sl -> isPrefix(sl, list)).collect(Collectors.toList());
            if (validPrefixes.isEmpty()) return null;

            List<List<List<String>>> result = new ArrayList<>();
            for (List<String> subList : validPrefixes) {
                if (isPrefix(subList, list)) {
                    List<List<List<String>>> possibleSubListsForRemainder = findSubLists(withoutPrefix(list, subList), subListsOrderedByScore);
                    if (possibleSubListsForRemainder != null) {
                        for (List<List<String>> subListsForRemainder : possibleSubListsForRemainder) {
                            Set<List<String>> uniqueSubLists = new HashSet<>(subListsForRemainder);
                            uniqueSubLists.add(subList);
                            if (uniqueSubLists.size() <= 3) {
                                List<List<String>> withThisPrefix = new ArrayList<>(subListsForRemainder);
                                withThisPrefix.add(0, subList);
                                result.add(withThisPrefix);
                            }
                        }
                    }
                }
            }
            return result.isEmpty()?null:result;
        }

        private List<String> withoutPrefix(List<String> list, List<String> subList) {
            return list.subList(subList.size(), list.size());
        }

        private boolean isPrefix(List<String> sublist, List<String> list) {
            return list.subList(0, Math.min(sublist.size(), list.size())).equals(sublist);
        }

        private Map<List<String>, Integer> calcValidSubLists(List<String> list) {
            Map<List<String>, Integer> subLists = calcSubLists(list);
            return subLists.entrySet().stream().filter(e -> isValidList(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        private boolean isValidList(List<String> list) {
            String firstItem = list.get(0);
            String lastItem = list.get(list.size()-1);
            boolean startsWithDirection = firstItem.equals("L") || firstItem.equals("R");
            boolean endsWithNumber = !lastItem.equals("L") && !lastItem.equals("R");
            boolean shortEnough = String.join(",", list).length() <= 20;
            return startsWithDirection && endsWithNumber && shortEnough;
        }

        private Map<List<String>, Integer> calcSubLists(List<String> list) {
            Map<List<String>, Integer> result = new HashMap<>();

            for (int i = 0; i < list.size()-1; i++) {
                for (int j = i; j < list.size(); j++) {
                    List<String> subList = list.subList(i, j+1);
                    if (result.containsKey(subList)) continue;
                    result.put(subList, frequencyOf(subList, list));
                }
            }
            return result;
        }

        private int frequencyOf(List<String> subList, List<String> list) {
            int result = 0;
            for (int i = 0; i < list.size() - subList.size(); i++) {
                if (list.subList(i, i+subList.size()).equals(subList)) result++;
            }
            return result;
        }

        private List<String> calcPath() {
            List<String> result = new ArrayList<>();
            Coord position = grid.findCells('^').stream().findFirst().orElseThrow(() -> new RuntimeException("Can't find initial position"));
            System.out.println(position);
            Direction direction = Direction.NORTH;
            int forwardCount = 0;
            while (direction != null) {
                Direction prevDirection = direction;
                direction = findNextDirection(direction, position);
                if (prevDirection != direction) {
                    if (forwardCount > 0) {
                        result.add(""+forwardCount);
                    }
                    if (direction == prevDirection.left()) result.add("L");
                    if (direction == prevDirection.right()) result.add("R");
                    forwardCount = 0;
                } else {
                    forwardCount++;
                    position = position.move(direction, 1);
                }
            }

            return result;
        }

        private Direction findNextDirection(Direction direction, Coord position) {
            if (Optional.ofNullable(grid.getCell(position.move(direction, 1))).orElse(' ') == '#') return direction;
            if (Optional.ofNullable(grid.getCell(position.move(direction.left(), 1))).orElse(' ') == '#') return direction.left();
            if (Optional.ofNullable(grid.getCell(position.move(direction.right(), 1))).orElse(' ') == '#') return direction.right();
            return null;
        }

        String part2RobotInput;
        int part2InputIdx = 0;
        long part2Result = 0L;
        private long part2InputSupplier() {
            return part2RobotInput.charAt(part2InputIdx++);
        }

        private void part2OutputConsumer(long value) {
            part2Result = value;
        }

        private long alignmentParameter(Coord coord) {
            return (long) coord.getX() * coord.getY();
        }

        private Set<Coord> findIntersections(SparseGrid<Character> grid) {
            Set<Coord> allScaffold = grid.findCells('#');
            return allScaffold.stream()
                    .filter(c -> isIntersection(c, allScaffold))
                    .collect(Collectors.toSet());
        }

        private boolean isIntersection(Coord coord, Set<Coord> points) {
            return points.contains(coord.north())
                    && points.contains(coord.south())
                    && points.contains(coord.east())
                    && points.contains(coord.west())
                    ;
        }
    }
}
