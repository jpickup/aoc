package com.johnpickup.aoc2024;

import lombok.Data;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day24 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
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

                States states = new States();
                Gates gates = new Gates();

                for (String line : lines) {
                    if (line.contains(":")) {
                        states.parse(line);
                    } else {
                        gates.parse(line);
                    }
                }

                System.out.println(states);
                System.out.println(gates);

                boolean doneWork;
                do {
                    doneWork = false;
                    List<Gate> allIncomplete = gates.getAllIncomplete();
                    for (Gate gate : allIncomplete) {
                        if (gate.actOn(states)) {
                            doneWork = true;
                        }
                    }
                } while (doneWork);

                long part1 = states.part1();
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

    @ToString
    static class Gates {
        final List<Gate> gates = new ArrayList<>();
        void parse(String line) {
            gates.add(Gate.parse(line));
        }

        public List<Gate> getAllIncomplete() {
            return gates.stream().filter(g -> !g.complete).collect(Collectors.toList());
        }
    }

    @Data
    static class Gate {
        final String input1;
        final String input2;
        final String output;
        final Operation operation;
        boolean complete = false;

        public static Gate parse(String line) {
            String[] parts = line.split(" ");
            return new Gate(parts[0], parts[2], parts[4], Operation.of(parts[1]));
        }

        public boolean actOn(States states) {
            boolean canActOn = states.contains(input1) && states.contains(input2);
            if (canActOn) {
                boolean outputValue = operation.apply(states.get(input1), states.get(input2));
                states.setState(output, outputValue);
                complete = true;
                //System.out.printf("%s = %s %s %s = %s %n", output, input1, operation, input2, outputValue);
            }
            return canActOn;
        }
    }

    enum Operation {
        AND,
        OR,
        XOR;

        public static Operation of(String symbol) {
            switch (symbol) {
                case "AND": return AND;
                case "OR": return OR;
                case "XOR": return XOR;
                default: throw new RuntimeException("Unknown operation " + symbol);
            }
        }

        public boolean apply(boolean in1, boolean in2) {
            switch (this) {
                case AND: return in1 & in2;
                case OR: return in1 | in2;
                case XOR: return in1 ^ in2;
                default: throw new RuntimeException("Unknown operation " + this);
            }
        }
    }

    @ToString
    static class States {
        final Map<String, Boolean> states = new HashMap<>();

        public long part1() {
            List<Map.Entry<String, Boolean>> outputs = states.entrySet().stream()
                    .filter(e -> e.getKey().startsWith("z"))
                    .collect(Collectors.toList());

            long result = 0;
            for (Map.Entry<String, Boolean> output : outputs) {
                result += decimalValueOf(output);
            }
            return result;
        }

        private long decimalValueOf(Map.Entry<String, Boolean> output) {
            if (!output.getValue()) return 0;
            int significance = Integer.parseInt(output.getKey().substring(1));
            return (long)Math.pow(2, significance);
        }

        void setState(String name, boolean value) {
            states.put(name, value);
        }

        void parse(String line) {
            String[] parts = line.split(":");
            setState(parts[0], parts[1].contains("1"));
        }

        public boolean contains(String name) {
            return states.containsKey(name);
        }

        public boolean get(String name) {
            return states.get(name);
        }
    }

}
