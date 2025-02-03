package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3Power {
    public static void main(String[] args) throws Exception {

        final int wordSize = 12;
        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day3Input.txt"))) {

            List<Integer> readings = stream.map(Day3Power::parseBinary)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            int[] bitCounts = new int[wordSize];
            for (int i = 0; i < wordSize ; i++) bitCounts[i]=0;

            for (Integer reading : readings) {
                for (int i = 0; i < wordSize; i++) {
                    if ((reading & (1<<i)) == (1<<i)) bitCounts[i]++;
                }
            }

            int gamma = 0;
            int epsilon = 0;

            for (int i = 0; i < wordSize; i++) {
                if (bitCounts[i] > (readings.size() / 2)) gamma += 1 << i; else epsilon += 1 << i;
            }

            System.out.println("Gamma " + gamma);
            System.out.println("Epsilon " + epsilon);
            System.out.println("Gamma*Epsilon " + gamma * epsilon);

            // part 2

            int oxygen = 0;
            int co2 = 0;

            List<Integer> oxyReadings = new ArrayList<>(readings);
            List<Integer> co2Readings = new ArrayList<>(readings);

            for (int i = wordSize-1; i >=0; i--) {
                for (int j = 0; j < wordSize ; j++) bitCounts[j]=0;
                for (Integer reading : oxyReadings) {
                    boolean bit = (reading & (1 << i)) == (1 << i);
                    if (bit) bitCounts[i]++;
                }

                int ones = bitCounts[i];
                int zeroes = oxyReadings.size() - bitCounts[i];
                boolean mostCommon = ones >= zeroes;

                // remove entries that aren't most common

                if (oxyReadings.size() > 1) {
                    Iterator<Integer> oxyIterator = oxyReadings.iterator();
                    while (oxyIterator.hasNext()) {
                        Integer reading = oxyIterator.next();
                        boolean bit = (reading & (1 << i)) == (1 << i);
                        if (bit != mostCommon) {
                            oxyIterator.remove();
                        }
                    }
                }
            }

            for (int i = wordSize-1; i >=0; i--) {
                for (int j = 0; j < wordSize ; j++) bitCounts[j]=0;
                for (Integer reading : co2Readings) {
                    boolean bit = (reading & (1 << i)) == (1 << i);
                    if (bit) bitCounts[i]++;
                }

                int ones = bitCounts[i];
                int zeroes = co2Readings.size() - bitCounts[i];
                boolean leastCommon = zeroes > ones;

                if (co2Readings.size() > 1) {
                    Iterator<Integer> co2Iterator = co2Readings.iterator();
                    while (co2Iterator.hasNext()) {
                        Integer reading = co2Iterator.next();
                        boolean bit = (reading & (1 << i)) == (1 << i);
                        if (bit != leastCommon) {
                            co2Iterator.remove();
                        }
                    }
                }
            }
            System.out.println("O2 " + oxyReadings.get(0));
            System.out.println("CO2 " + co2Readings.get(0));
            System.out.println("LifeSupport " + oxyReadings.get(0) * co2Readings.get(0));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static int parseBinary(String s){
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            result *= 2;
            result += s.charAt(i)=='1'?1:0;
        }
        System.out.println(s + " = " + result);
        return result;
    }
}
