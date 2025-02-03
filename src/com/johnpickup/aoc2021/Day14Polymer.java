package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14Polymer {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day14Input.txt"))) {

            List<String> inputs = stream.collect(Collectors.toList());

            String polymer = inputs.get(0);

            Map<String, String> transitions = inputs.stream()
                    .filter(s -> s.contains("->"))
                    .collect(Collectors.toMap(s -> s.split("->")[0].trim(), s -> s.split("->")[1].trim()));

            Map<String, Long> pairCounts = new HashMap<>();
            for (int i = 0; i < polymer.length()-1; i++) {
                String pair = polymer.substring(i, i+2);
                pairCounts.put(pair, Optional.ofNullable(pairCounts.get(pair)).map(n -> n+1).orElse(1L));
            }
            pairCounts.put(" "+polymer.charAt(0), 1L);
            pairCounts.put(polymer.charAt(polymer.length()-1)+" ", 1L);

            for (int gen = 0; gen < 40; gen++) {
                Map<String, Long> newPairCounts = new HashMap<>();
                for (Map.Entry<String, Long> pair : pairCounts.entrySet()) {
                    if (transitions.containsKey(pair.getKey())) {
                        String newChar = transitions.get(pair.getKey());
                        String lhs = pair.getKey().substring(0,1) + newChar;
                        String rhs = newChar + pair.getKey().substring(1);
                        newPairCounts.put(lhs, Optional.ofNullable(newPairCounts.get(lhs)).orElse(0L) + pair.getValue());
                        newPairCounts.put(rhs, Optional.ofNullable(newPairCounts.get(rhs)).orElse(0L) + pair.getValue());
                    }
                    else {
                        newPairCounts.put(pair.getKey(), pair.getValue());
                    }
                }
                pairCounts = newPairCounts;
            }

            Map<Character, Long> freq = new HashMap<>();
            for (Map.Entry<String, Long> pair : pairCounts.entrySet()) {
                char left = pair.getKey().charAt(0);
                char right = pair.getKey().charAt(1);

                freq.put(left, Optional.ofNullable(freq.get(left)).orElse(0L) + pair.getValue());
                freq.put(right, Optional.ofNullable(freq.get(right)).orElse(0L) + pair.getValue());
            }

//            for (int gen = 0; gen < 40; gen++) {
//                polymer = applyTransitions(polymer, transitions);
//                System.out.println("Gen: " + (gen+1));
//                //System.out.println(polymer);
//                System.out.println(polymer.length());
//            }
//
//            Map<Character, Long> freq = new HashMap<>();
//            char[] chars = polymer.toCharArray();
//            for (char aChar : chars) {
//                freq.put(aChar, Optional.ofNullable(freq.get(aChar)).map(i -> i+1). orElse(1L));
//            }
//
            Map<Long, Character> revFreq = freq.entrySet().stream()
                    .filter(e -> e.getKey() != ' ')
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

            List<Long> sizes = new ArrayList<>(revFreq.keySet());
            sizes.sort(Long::compareTo);
            Long least = sizes.get(0) / 2;
            Long most = sizes.get(sizes.size()-1) / 2;
            System.out.printf("Least %d\n", least);
            System.out.printf("Most %d\n", most);
            System.out.printf("Diff %d\n", most - least);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String applyTransitions(String polymer, Map<String, String> transitions) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < polymer.length()-1; i++) {
            if (transitions.containsKey(polymer.substring(i, i+2))) {
                result.append(polymer, i, i + 1).append(transitions.get(polymer.substring(i, i + 2)));
            }
            else {
                result.append(polymer, i, i + 1);
            }
        }
        result.append(polymer.substring(polymer.length()-1));
        return result.toString();
    }

}
