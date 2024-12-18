package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.CharGrid;
import com.johnpickup.aoc2024.util.Coord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day18 {
  static int FULL_SIZE = 70 + 1;
  static int TEST_SIZE = 6 + 1;
  static char SPACE = '.';
  static char OCCUPIED = '#';
  public static void main(String[] args) {
    String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
    //String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
    String prefix = "c:/dev/aoc/resources/2024/" + day + "/" + day;
    List<String> inputFilenames = Arrays.asList(
        //prefix + "-test.txt"
        prefix + ".txt"
    );
    for (String inputFilename : inputFilenames) {
      createEmptyTestFileIfMissing(inputFilename);
      long start = System.currentTimeMillis();
      System.out.println(inputFilename);
      try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
        List<String> lines = stream
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

        Memory memory = new Memory(lines, 1024);
        System.out.println(memory);

        long part1 = memory.part1();
        System.out.println("Part 1: " + part1);
        String part2 = Memory.part2(lines);
        System.out.println("Part 2: " + part2);

      } catch (IOException e) {
        e.printStackTrace();
      }
      long end = System.currentTimeMillis();
      System.out.println("Time: " + (end - start) + "ms");
    }
  }

  static class Memory {
    final CharGrid grid;

    final Coord entrance = new Coord(0,0);
    final Coord exit;

    Memory(List<String> lines, int number) {
      Set<Coord> occupied = lines.stream().limit(number).map(Coord::new).collect(Collectors.toSet());
      Integer maxX = occupied.stream().map(Coord::getX).max(Integer::compareTo).get();
      int size = maxX > TEST_SIZE ? FULL_SIZE : TEST_SIZE;
      grid = new CharGrid(size, size, new char[size][size]);
      exit = new Coord(size-1, size-1);
      for (int x = 0; x < size; x++) {
        for (int y = 0; y < size; y++) {
          Coord coord = new Coord(x, y);
          grid.setCell(coord, occupied.contains(coord) ? OCCUPIED : SPACE);
        }
      }
    }

    @Override
    public String toString() {
      return grid.toString();
    }

    long part1() {
      Set<List<Coord>> routes = findRoutes();
      return routes.stream().findFirst().map(List::size).orElse(0);
    }

    public static String part2(List<String> lines) {
      for (int i = lines.size(); i > 0; i--) {
        Memory memory = new Memory(lines,i);
        Set<List<Coord>> routes = memory.findRoutes();
        if (!routes.isEmpty()) {
          return lines.get(i);
        }
      }
      return "";
    }

    Set<List<Coord>> findRoutes() {
      Map<Coord, Long> unvisited = new HashMap<>();
      Map<Coord, Long> visited = new HashMap<>();
      Map<Coord, Set<List<Coord>>> paths = new HashMap<>();
      Set<Coord> spaces = grid.findAll(SPACE);
      for (Coord space : spaces) {
        unvisited.put(new Coord(space), Long.MAX_VALUE);
      }
      unvisited.put(entrance, 0L);
      paths.put(entrance, Collections.singleton(Collections.emptyList()));

      Map.Entry<Coord, Long> lowestCostCoord = findSmallest(unvisited);

      while (lowestCostCoord != null) {
        Map<Coord, Long> neighbours = findNeighbours(unvisited, lowestCostCoord.getKey());
        for (Map.Entry<Coord, Long> entry : neighbours.entrySet()) {
          long cost = lowestCostCoord.getValue() + 1;
          if (cost < entry.getValue()) {
            Set<List<Coord>> possibleCoordsToCoord = paths.get(lowestCostCoord.getKey());

            Set<List<Coord>> possibleCoordsToEntry = Optional.ofNullable(paths.get(entry.getKey())).orElse(new HashSet<>());
            for (List<Coord> CoordsToCoord : possibleCoordsToCoord) {
              List<Coord> CoordsToEntry = calcPath(CoordsToCoord, entry.getKey());
              possibleCoordsToEntry.add(CoordsToEntry);
            }
            unvisited.put(entry.getKey(), cost);
            paths.put(entry.getKey(), possibleCoordsToEntry);
          }
          visited.put(lowestCostCoord.getKey(), lowestCostCoord.getValue());
        }
        unvisited.remove(lowestCostCoord.getKey());
        lowestCostCoord = findSmallest(unvisited);
      }

      return Optional.ofNullable(paths.get(exit)).orElse(Collections.emptySet());
    }

    private Map<Coord, Long> findNeighbours(Map<Coord, Long> unvisited, Coord key) {
      return unvisited.entrySet().stream().filter(e -> e.getKey().isAdjacentTo4(key)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<Coord, Long> findSmallest(Map<Coord, Long> coords) {
      Map.Entry<Coord, Long> smallest = null;
      for (Map.Entry<Coord, Long> entry : coords.entrySet()) {
        if (entry.getValue() < Optional.ofNullable(smallest).map(Map.Entry::getValue).orElse(Long.MAX_VALUE)) {
          smallest = entry;
        }
      }
      return smallest;
    }

    private List<Coord> calcPath(List<Coord> from, Coord coord2) {
      List<Coord> result = new ArrayList<>(from);
      result.add(coord2);
      return result;
    }
  }
}
