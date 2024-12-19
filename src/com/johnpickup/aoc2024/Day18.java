package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.CharGrid;
import com.johnpickup.aoc2024.util.Coord;
import com.johnpickup.aoc2024.util.Dijkstra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
    String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
    //String prefix = "c:/dev/aoc/resources/2024/" + day + "/" + day;
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

  static class Memory extends Dijkstra<Coord> {
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

    @Override
    protected Set<Coord> allStates() {
      return grid.findAll(SPACE);
    }

    @Override
    protected Coord initialState() {
      return entrance;
    }

    @Override
    protected Coord targetState() {
      return exit;
    }

    @Override
    protected long calculateCost(Coord fromState, Coord toState) {
      return 1L;
    }

    @Override
    protected boolean statesAreConnected(Coord state1, Coord state2) {
      return state1.isAdjacentTo4(state2);
    }

    @Override
    protected boolean findAllRoutes() {
      return false;
    }
  }
}
