package com.johnpickup.aoc2024;

import lombok.Data;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day24 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
//                prefix + "-part2-test.txt"
//                , prefix + "-test2.txt"
                 prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            BiFunction<Long, Long, Long> correctOperation =
                    isTest ? (a,b) -> a & b
                    : Long::sum;

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

//                System.out.println(" ----- Graphviz -----");
//                System.out.println(gates.graphviz(states));
//                System.out.println(" --------------------");


                for (int i = 0; i < 44; i++) {
                    gates.extractAdder(i);
                }
                // manual inspection of graphviz output
                // Failed to find pps XOR ? -> z12
                // z12 & vdc
                //
                // Failed to find bbn XOR ? -> z21
                // rsc XOR bbn should go to z21 not nhn
                // z21 & nhn
                //
                // Failed to find khg XOR ? -> z25
                // tvb & khg
                //
                // Failed to find jbr XOR ? -> z33
                // gst & z33
                //
                //gst,khg,nhn,tvb,vdc,z12,z21,z33

                String part2 = "";
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

        public String graphviz(States states) {
            StringBuilder sb = new StringBuilder();
            sb.append("digraph day24 { \n");
            sb.append("  rankdir=LR;\n");
            states.states.keySet().forEach(s -> sb.append(String.format("  node [shape=circle] %s; %n", s)));
            gates.forEach(g -> sb.append(String.format("  node [shape=%s] \"%s\"; %n", g.shape(), g.name())));
            gates.forEach(g -> sb.append(String.format("  \"%s\" -> \"%s\"; %n", g.input1, g.name())));
            gates.forEach(g -> sb.append(String.format("  \"%s\" -> \"%s\"; %n", g.input2, g.name())));
            gates.forEach(g -> sb.append(String.format("  \"%s\" -> \"%s\"; %n", g.name(), g.output)));
            sb.append("}");
            return sb.toString();
        }

        public void extractAdder(int bitNo) {
            String x = String.format("x%02d",bitNo);
            String y = String.format("y%02d",bitNo);
            String z = String.format("z%02d",bitNo);
            Gate xor1 = findGateXY(Operation.XOR, x, y);
            if (xor1 == null) {
                System.out.println("Failed to find " + x + " XOR " + y);
            }
            Gate and1 = findGateXY(Operation.AND, x, y);
            if (and1 == null) {
                System.out.println("Failed to find " + x + " AND " + y);
            }
            String xor1out = xor1.output;
            String and1out = and1.output;
            Gate xor2 = findGateXZ(Operation.XOR, xor1out, z);
            if (xor2 == null) {
                System.out.println("Failed to find " + xor1out + " XOR ? -> " + z);
            }
        }

        private Gate findGateXY(Operation operation, String x, String y) {
            return gates.stream()
                    .filter(g -> g.operation == operation)
                    .filter(g -> (g.input1.equals(x) && g.input2.equals(y)) || (g.input1.equals(y) && g.input2.equals(x)))
                    .findFirst()
                    .orElse(null);
        }
        private Gate findGateXZ(Operation operation, String x, String z) {
            return gates.stream()
                    .filter(g -> g.operation == operation)
                    .filter(g -> (g.input1.equals(x) && g.output.equals(z)) || (g.input2.equals(x) && g.output.equals(z)))
                    .findFirst()
                    .orElse(null);
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

        public String name() {
            return String.format("%s %s %s", input1, operation, input2);
        }

        public String shape() {
            return operation.shape();
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

        public String shape() {
            switch (this) {
                case AND: return "square";
                case OR: return "diamond";
                case XOR: return "octagon";
                default: throw new RuntimeException("Unknown operation " + this);
            }
        }
    }

    @ToString
    static class States {
        final Map<String, Boolean> states = new HashMap<>();

        public long part1() {
            return getValue("z");
        }

        public long getValue(String prefix) {
            List<Map.Entry<String, Boolean>> nodes = states.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(prefix))
                    .collect(Collectors.toList());

            long result = 0;
            for (Map.Entry<String, Boolean> node : nodes) {
                result += decimalValueOf(node);
            }
            return result;
        }

        public void setValue(String prefix, long value) {
            List<Map.Entry<String, Boolean>> nodes = states.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(prefix))
                    .collect(Collectors.toList());

            for (Map.Entry<String, Boolean> node : nodes) {
                int significance = Integer.parseInt(node.getKey().substring(1));
                long bitValue = (long)Math.pow(2, significance);
                states.put(node.getKey(), (value & bitValue) != 0);
            }
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
