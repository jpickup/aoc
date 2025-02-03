package com.johnpickup.aoc2018;

import com.johnpickup.util.Coord;
import com.johnpickup.util.Coord3D;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day11 {
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

                PowerGrid powerGrid = new PowerGrid(lines.get(0));

                System.out.println("Part 1: " + powerGrid.maxPowerCoord(3));
                System.out.println("Part 2: " + powerGrid.maxPowerGrid());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class PowerGrid {
        final int serialNumber;
        final Map<Coord, Integer> cellCache = new HashMap<>();
        final Map<Coord3D, Integer> gridCache = new HashMap<>();

        PowerGrid(String line) {
            serialNumber = Integer.parseInt(line);
            cacheCells();
        }

        private void cacheCells() {
            for (int x = 1; x <= 300-1; x++) {
                for (int y = 1; y <= 300-1; y++) {
                    Coord c = new Coord(x, y);
                    calcPowerLevel(c);
                }
            }
            // need size 1 & 2 for part 1 (size 3)
            maxPowerCoord(2);
        }

        Coord maxPowerCoord(int gridSize) {
            int maxPower = Integer.MIN_VALUE;
            Coord result = null;
            for (int x = 1; x <= 300-gridSize; x++) {
                for (int y = 1; y <= 300-gridSize; y++) {
                    Coord c = new Coord(x, y);
                    int powerLevel = calcGridPowerLevel(c, gridSize);
                    if (powerLevel > maxPower) {
                        maxPower = powerLevel;
                        result = c;
                    }
                }
            }
            return result;
        }

        public Coord3D maxPowerGrid() {
            int maxPower = Integer.MIN_VALUE;
            Coord3D result = null;
            for (int gridSize = 1; gridSize <= 300; gridSize++) {
                for (int x = 1; x <= 300-gridSize; x++) {
                    for (int y = 1; y <= 300-gridSize; y++) {
                        Coord c = new Coord(x, y);
                        int powerLevel = calcGridPowerLevel(c, gridSize);
                        if (powerLevel > maxPower) {
                            maxPower = powerLevel;
                            result = new Coord3D(c.getX(), c.getY(), gridSize);
                        }
                    }
                }
            }
            return result;
        }

        private int calcGridPowerLevel(Coord c, int gridSize) {
            Coord3D gridCoord = new Coord3D(c.getX(), c.getY(), gridSize);
            Coord3D smallerGridCoord = new Coord3D(c.getX(), c.getY(), gridSize - 1);
            if (gridCache.containsKey(gridCoord))  return gridCache.get(gridCoord);
            int result = 0;
            if (gridCache.containsKey(smallerGridCoord)) {
                // just add the extra xs and ys
                result = gridCache.get(smallerGridCoord);
                for (int dx = 0; dx < gridSize-1; dx++) {
                    result += calcPowerLevel(new Coord(c.getX() + dx, c.getY() + gridSize-1));
                }
                for (int dy = 0; dy < gridSize-1; dy++) {
                    result += calcPowerLevel(new Coord(c.getX() + gridSize-1, c.getY()+dy));
                }
                result += calcPowerLevel(new Coord(c.getX() + gridSize-1, c.getY() + gridSize-1));
            }
            gridCache.put(gridCoord, result);

            return result;
        }

        private int calcPowerLevel(Coord c) {
            if (cellCache.containsKey(c)) return cellCache.get(c);
            int rackId = c.getX() + 10;
            long powerLevel = rackId * c.getY();
            powerLevel += serialNumber;
            powerLevel *= rackId;
            powerLevel = (powerLevel / 100L) % 10L;
            powerLevel -= 5;
            int result = (int)powerLevel;
            cellCache.put(c, result);
            gridCache.put(new Coord3D(c.getX(), c.getY(), 1), result);
            return result;
        }

    }

}
