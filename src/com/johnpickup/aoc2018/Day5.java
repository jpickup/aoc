package com.johnpickup.aoc2018;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day5 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Polymer polymer = new Polymer(lines.get(0));
                System.out.println("Part 1: " + polymer.reduce().length());
                System.out.println("Part 2: " + polymer.shortest().length());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class Polymer {
        final List<Character> elements;

        public Polymer(String line) {
            elements = new ArrayList<>();
            for (int i = 0 ; i < line.length(); i++) elements.add(line.charAt(i));
        }

        public Polymer reduce() {
            int i = 0;
            List<Character> newElements = new ArrayList<>(elements);
            while (i < newElements.size()-1) {
                char e1 = newElements.get(i);
                char e2 = newElements.get(i+1);
                boolean delete = e1 != e2 && Character.toUpperCase(e1)== Character.toUpperCase(e2);
                if (delete) {
                    newElements.remove(i+1);
                    newElements.remove(i);
                    i = i > 1 ? i-1 : 0;
                } else {
                    i++;
                }
            }
            return new Polymer(newElements);
        }

        Polymer withoutElement(char element) {
            final char remove = Character.toUpperCase(element);
            return new Polymer(elements.stream().filter(e -> Character.toUpperCase(e) != remove).collect(Collectors.toList()));
        }

        public Polymer shortest() {
            Polymer result = this;
            for (char c = 'a'; c <= 'z'; c++) {
                Polymer shorter = this.withoutElement(c).reduce();
                if (shorter.length() < result.length()) result = shorter;
            }
            return result;
        }

        @Override
        public String toString() {
            return elements.stream().map(c -> c + "").reduce((a,b) -> "" + a + b).orElse("");
        }

        public long length() {
            return elements.size();
        }
    }
}
