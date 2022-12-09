package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day9 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day9.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());

            List<Instruction> instructions = lines.stream().map(Instruction::parse).collect(Collectors.toList());
            int totalSteps = instructions.stream().map(c -> c.distance).reduce(Integer::sum).get();
            Set<Coord> visited = new HashSet<>();

            Coord head = Coord.builder().x(0).y(0).build();
            Coord[] knots = new Coord[9];
            for (int i=0; i<9; i++) knots[i]=Coord.builder().x(0).y(0).build();
            visited.add(knots[8]);

            int clock = 0;
            printState(clock, head, knots);

            for (Instruction instruction : instructions) {
                System.out.println(instruction);
                for (int t=0; t<instruction.distance; t++) {
                    head = head.move(instruction.command);
                    knots[0] = knots[0].follow(head);
                    for (int i=1; i<9; i++)
                        knots[i] = knots[i].follow(knots[i-1]);
                    visited.add(knots[8]);
                    clock++;
                }
                printState(clock, head, knots);
            }
            System.out.println("Tail visited coords: " + visited.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printState(int clock, Coord head, Coord[] knots) {
        System.out.println("CLOCK: " + clock + " ----------");
        List<Coord> allCoord = new ArrayList<>();
        allCoord.add(head);
        allCoord.addAll(Arrays.asList(knots));
        int maxX= allCoord.stream().map(k -> k.x).reduce((a, b) -> a > b ? a : b).get() + 2;
        int minX= allCoord.stream().map(k -> k.x).reduce((a, b) -> a < b ? a : b).get() - 2;
        int maxY= allCoord.stream().map(k -> k.y).reduce((a, b) -> a > b ? a : b).get() + 2;
        int minY= allCoord.stream().map(k -> k.y).reduce((a, b) -> a < b ? a : b).get() - 2;

        for (int y=maxY; y>=minY; y--) {
          for (int x=minX; x<=maxX; x++) {
              boolean found = false;
              for (int k=0; k < allCoord.size(); k++) {
                  if (allCoord.get(k).equals(Coord.builder().x(x).y(y).build())) {
                      System.out.print(k);
                      found = true;
                      break;
                  }
              }
              if (!found) System.out.print(".");
          }
          System.out.println();
        }
        System.out.println();
    }

    @Builder
    @ToString
    static class Instruction {
        String command;
        int distance;

        public static Instruction parse(String s) {
            String[] parts= s.split(" ");
            return Instruction.builder()
                    .command(parts[0])
                    .distance(Integer.parseInt(parts[1]))
                    .build();
        }
    }

    @Builder
    @Data
    static class Coord {
        final int x;
        final int y;

        public Coord move(String command) {
            switch (command) {
                case "R": return Coord.builder().x(x+1).y(y).build();
                case "L": return Coord.builder().x(x-1).y(y).build();
                case "U": return Coord.builder().x(x).y(y+1).build();
                case "D": return Coord.builder().x(x).y(y-1).build();
            }
            throw new RuntimeException("Unknown command " + command);
        }

        public Coord follow(Coord head) {
            int dx = head.x-x;
            int dy = head.y-y;

            if (Math.abs(dx)<=1 && Math.abs(dy)<=1) return this;

            return Coord.builder().x(x+(int)(Math.signum(dx))).y(y+(int)(Math.signum(dy))).build();
        }
    }
}
