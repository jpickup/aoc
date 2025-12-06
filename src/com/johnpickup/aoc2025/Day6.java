package com.johnpickup.aoc2025;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day6 {
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
                        .toList();

                System.out.println("Part1: " + part1(lines));
                System.out.println("Part 2: " + part2(lines));
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static long part1(List<String> lines) {
        List<DataRow> dataRows = lines.stream()
                .map(Day6::compactSpaces)
                .filter(l -> !l.isEmpty())
                .filter(l -> !l.contains("+")).map(DataRow::new)
                .toList();
        OperatorRow operatorRow  = lines.stream()
                .map(Day6::compactSpaces)
                .filter(l -> !l.isEmpty())
                .filter(l -> l.contains("+")).map(OperatorRow::new)
                .findFirst().orElseThrow();

        long part1 = 0L;
        for (int i = 0 ; i < operatorRow.operators.size(); i++) {
            final int index = i;
            part1 += operatorRow.applyPart1(dataRows.stream().map(dr -> dr.getValue(index)).toList(), index);
        }
        return part1;
    }

    private static long part2(List<String> inputLines) {
        List<String> lines = padToLongest(inputLines);
        String operatorLine = lines.removeLast();
        int colIndex = 0;
        long part2 = 0;

        char operator = operatorLine.charAt(0);
        List<String> values = new ArrayList<>();

        while (colIndex < operatorLine.length()) {
            if (!values.isEmpty() && operatorLine.charAt(colIndex) != ' ') {
                part2 += applyPart2Operator(operator, values);
                operator = operatorLine.charAt(colIndex);
                values = new ArrayList<>();
            }
            // accumulate chars into values
            List<String> newValues = new ArrayList<>();
            for (int lineIdx = 0; lineIdx < lines.size();  lineIdx++) {
                newValues.add(
                        (values.size() <= lineIdx ? "" : values.get(lineIdx)) + lines.get(lineIdx).charAt(colIndex));
            }
            values = newValues;
            colIndex++;
        }
        part2 += applyPart2Operator(operator, values);

        return part2;
    }

    private static List<String> padToLongest(List<String> lines) {
        int longest = lines.stream().map(String::length).max(Integer::compareTo).orElseThrow();
        return new ArrayList<>(lines.stream().map(l -> padTo(l, longest)).toList());
    }

    private static String padTo(String s, int length) {
        while (s.length() < length) s += ' ';
        return s;
    }

    private static long applyPart2Operator(char operator, List<String> values) {
        long identity = operator == '+' ? 0L : 1L;
        long result = identity;

        List<String> columnValues = new ArrayList<>();
        int colWidth = values.getFirst().length();
        for (int colIdx = colWidth-1; colIdx>=0; colIdx--) {
            String value = "";
            for (int valIdx = 0 ; valIdx < values.size(); valIdx++) {
                value += values.get(valIdx).charAt(colIdx);
            }
            columnValues.add(value);
        }

        for (String colValue : columnValues) {
            long v = colValue.trim().isEmpty() ? identity : Long.parseLong(colValue.trim());
            if (operator == '+') result += v;
            if (operator == '*') result *= v;
        }
        System.out.println(operator + " : " + columnValues + " = " + result);
        return result;
    }

    private static String compactSpaces(String line) {
        return line.replaceAll(" +", " ");
    }

    @RequiredArgsConstructor
    @Data
    static class DataRow {
        final List<Long> values;
        DataRow(String line) {
            values = Arrays.stream(line.split(" "))
                    .filter(f -> !f.isEmpty())
                    .map(Long::parseLong).toList();
        }

        public long getValue(int i) {
            return values.get(i);
        }
    }

    @Data
    @RequiredArgsConstructor
    static class OperatorRow {
        List<Character> operators;

        OperatorRow(String line) {
            operators = Arrays.stream(line.split(" "))
                    .filter(f -> !f.isEmpty())
                    .map(s -> s.charAt(0)).toList();
        }

        public long applyPart1(List<Long> values, int operatorIndex) {
            Character operator = operators.get(operatorIndex);
            long identity = operator=='+'?0L:1L;
            return values.stream().reduce(identity, operator=='+'?Long::sum:(a,b)->a*b);
        }
    }


}
