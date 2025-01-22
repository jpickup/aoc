package com.johnpickup.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Compare the output of two programmes that have dumped a map and compare the content
 */
public class MapComparer {
    public static void main(String[] args) {
        String filename1="/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day21/p1.txt";
        String filename2="/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day21/p2.txt";
        try {
            Stream<String> stream1 = Files.lines(Paths.get(filename1));
            MapFromFile map1 = new MapFromFile((stream1.collect(Collectors.toList())));
            Stream<String> stream2 = Files.lines(Paths.get(filename2));
            MapFromFile map2 = new MapFromFile((stream2.collect(Collectors.toList())));

            System.out.println(map1.showDiffs(map2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class MapFromFile {
        final Map<Integer, Map<String, String>> map = new HashMap<>();
        MapFromFile(List<String> lines) {
            int iteration = 0;
            for (String line : lines) {
                try {
                    if (line.startsWith("---")) {
                        String[] iterParts = line.split(" ");
                        iteration = Integer.parseInt(iterParts[3].trim());
                        map.put(iteration, new HashMap<>());
                    } else if (line.startsWith("(")) {
                        String[] parts = line.split("->");
                        if (parts.length > 1) {
                            map.get(iteration).put(parts[0].replaceAll(" ", ""), parts[1].replaceAll(" ", ""));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing " + line + " - " + e.getMessage());
                }
            }
        }

        public String showDiffs(MapFromFile other) {
            StringBuilder sb = new StringBuilder();
            Set<Integer> allIterations = new TreeSet<>();
            allIterations.addAll(map.keySet());
            allIterations.addAll(other.map.keySet());
            for (Integer iteration : allIterations) {
                sb.append("--------- Iteration " + iteration + " --------------------\n");
                Map<String, String> map1 = Optional.ofNullable(map.get(iteration)).orElse(Collections.emptyMap());
                Map<String, String> map2 = Optional.ofNullable(other.map.get(iteration)).orElse(Collections.emptyMap());
                Set<String> allKeys = new TreeSet<>();
                allKeys.addAll(map1.keySet());
                allKeys.addAll(map2.keySet());
                for (String key : allKeys) {
                    String vThis = map1.getOrDefault(key, "");
                    String vOther = map2.getOrDefault(key, "");
                    if (!vThis.equals(vOther)) {
                        sb.append(String.format("%s -> %s != %s%n", key, vThis, vOther));
                    }
                }
            }
            return sb.toString();
        }
    }
}
