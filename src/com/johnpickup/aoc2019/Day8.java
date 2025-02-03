package com.johnpickup.aoc2019;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day8 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Image image = new Image(lines);

                CharGrid layerWithFewestZeros = image.layerWithFewestZeros();

                long part1 = (long)layerWithFewestZeros.findCells('1').size() * (long)layerWithFewestZeros.findCells('2').size();
                System.out.println("Part 1: " + part1);

                String part2 = image.render();
                System.out.println("Part 2: \n" + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }
    @ToString
    static class Image {
        final int width;
        final int height;
        List<CharGrid> layers;
        Image(List<String> lines) {
            width = Integer.parseInt(lines.get(0));
            height = Integer.parseInt(lines.get(1));
            layers = parseLayers(lines.get(2));
        }

        private List<CharGrid> parseLayers(String s) {
            List<CharGrid> result = new ArrayList<>();
            int layerCount = s.length() / (width * height);
            for (int i = 0 ; i < layerCount; i++) {
                char[][] cells = new char[width][height];
                for (int y = 0; y < height; y++) {
                    for (int x = 0 ; x < width; x++) {
                        cells[x][y]=s.charAt(x + y * width + i * width * height);
                    }
                }
                CharGrid layer = new CharGrid(width, height, cells);
                result.add(layer);
            }
            return result;
        }

        public CharGrid layerWithFewestZeros() {
            CharGrid result = null;
            int fewestZeros = Integer.MAX_VALUE;

            for (CharGrid layer : layers) {
                int zeros = layer.findCells('0').size();
                if (zeros < fewestZeros) {
                    fewestZeros = zeros;
                    result = layer;
                }
            }
            return result;
        }

        public String render() {
            CharGrid combined = new CharGrid(width, height, new char[width][height]);
            for (int i = layers.size()-1; i>= 0; i--) {
                CharGrid layer = layers.get(i);
                for (int x = 0; x < width; x++) {
                    for (int y = 0 ; y < height; y++) {
                        Coord coord = new Coord(x, y);
                        char cell = layer.getCell(coord);
                        switch (cell) {
                            case '0': combined.setCell(coord, ' '); break;
                            case '1': combined.setCell(coord, '#'); break;
                        }
                    }
                }
            }
            return combined.toString();
        }
    }
}
