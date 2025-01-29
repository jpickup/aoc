package com.johnpickup.aoc2018;

import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;
import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day12 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                PlantProcess plantProcess = new PlantProcess(lines);

                System.out.println("Part 1: " + plantProcess.livingCount(20));
                System.out.println("Part 2: " + plantProcess.part2());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class PlantProcess {
        final Plants initialPlants;
        final List<Rule> rules;


        PlantProcess(List<String> lines) {
            initialPlants = Plants.parse(lines.get(0));
            rules = lines.subList(1, lines.size()).stream().map(Rule::new).collect(Collectors.toList());
        }

        public int livingCount(int generations) {
            Plants plants = new Plants(initialPlants);

            for (int i = 0; i < generations; i++) {
                System.out.println(plants);
                plants = plants.applyRules(rules);
            }
            System.out.println(plants);

            return plants.livingPotSum();
        }

        public long part2() {
            Map<Integer, Integer> generationsValue = new HashMap<>();

            int generation = 0;
            Plants current = initialPlants;

            while (generation <= 110) {
                generationsValue.put(generation, current.livingPotSum());
                generation++;
                current = current.applyRules(rules);
            }
            //System.out.println(generationsValue);
            // appears to reach a steady state at around generation 100

            long valueAt100 = generationsValue.get(100);
            long delta = generationsValue.get(100) - generationsValue.get(99);

            return valueAt100 + (delta * (50000000000L - 100L));
        }
    }
    @EqualsAndHashCode
    static class Plants {
        final Map<Integer, Boolean> states;
        static Plants parse(String line) {
            if (!line.startsWith("initial state: ")) throw new RuntimeException("Invalid input " + line);
            String plantString = line.substring("initial state: ".length());
            return new Plants(stringToMap(plantString));
        }

        public Plants(Plants source) {
            this(new TreeMap<>(source.states));
        }

        public Plants(Map<Integer, Boolean> states) {
            this.states = states;
        }

        Plants applyRules(List<Rule> rules) {
            Map<Integer, Boolean> newStates = new TreeMap<>();
            int minIdx = states.keySet().stream().min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No states"));
            int maxIdx = states.keySet().stream().max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No states"));

            for (int i = minIdx-2; i <= maxIdx+2; i++) {
                final int idx = i;
                boolean newState = rules.stream().anyMatch(r -> r.apply(states, idx));
                if (newState) newStates.put(idx, true);
            }
            return new Plants(newStates);
        }

        @Override
        public String toString() {
            int minIdx = states.keySet().stream().min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No states"));
            int maxIdx = states.keySet().stream().max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No states"));
            StringBuilder sb = new StringBuilder();
            for (int i = minIdx; i <= maxIdx; i++) {
                sb.append(states.getOrDefault(i, false) ? "#" : '.');
            }
            return sb.toString();
        }

        public int livingPotSum() {
            return states.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).reduce(0, Integer::sum);
            //return (int)states.values().stream().filter(v -> v).count();
        }
    }

    static class Rule {
        final Map<Integer, Boolean> input;
        final boolean output;
        Rule(String line) {
            String[] parts = line.split(" => ");
            input = stringToMap(parts[0]);
            output = parts[1].equals("#");
        }

        public boolean apply(Map<Integer, Boolean> states, int idx) {
            boolean matches = true;
            for (int i = -2; i <= 2; i++) {
                boolean ruleValue = input.getOrDefault(i + 2, false);
                boolean stateValue = states.getOrDefault(idx + i, false);
                matches &= (ruleValue == stateValue);
            }
            return matches && output;
        }
    }

    static Map<Integer, Boolean> stringToMap(String s) {
        Map<Integer, Boolean> result = new TreeMap();
        for (int i = 0 ; i < s.length(); i++) if (s.charAt(i)=='#') result.put(i, true);
        return result;
    }
}
