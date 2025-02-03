package com.johnpickup.aoc2020;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day14 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test2.txt"
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

                Memory memory = new Memory(lines);
                System.out.println(memory);

                long part1 = memory.part1();
                System.out.println("Part 1: " + part1);
                memory.reset();
                long part2 = memory.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @ToString
    static class Memory {
        final List<Instruction> instructions;
        final Map<Long, Long> values = new HashMap<>();
        Mask mask;

        Memory(List<String> lines) {
            instructions = lines.stream().map(Instruction::parse).collect(Collectors.toList());
        }

        public long part1() {
            instructions.forEach(i -> i.apply(this));
            return values.values().stream().reduce(0L, Long::sum);
        }

        public long part2() {
            instructions.forEach(i -> i.apply2(this));
            return values.values().stream().reduce(0L, Long::sum);
        }


        public void writeValue(long address, long value) {
            long maskedValue = mask.applyMask(value);
            values.put(address, maskedValue);
        }

        public void writeDirect(long address, long value) {
            values.put(address, value);

        }

        public void reset() {
            values.clear();
        }
    }

    static abstract class Instruction {
        abstract void apply(Memory memory);
        abstract void apply2(Memory memory);
        public static Instruction parse(String line) {
            if (line.startsWith("mask"))
                return new Mask(line);
            else
                return new Write(line);
        }
    }

    @ToString
    static class Mask extends Instruction {
        final String mask;

        Mask(String line) {
            mask = line.split(" = ")[1];
        }

        @Override
        void apply(Memory memory) {
            memory.mask = this;
        }

        @Override
        void apply2(Memory memory) {
            memory.mask = this;
        }

        public long applyMask(long value) {
            long result = 0;
            long bit = 1;
            for (int i = 0; i < 36; i++) {
                char bitMask = mask.charAt(mask.length() - (i + 1));
                long valueBit = value & bit;
                long resultBit = 0;
                switch (bitMask) {
                    case '0': resultBit = 0; break;
                    case '1': resultBit = bit; break;
                    case 'X': resultBit = valueBit; break;
                    default: throw new RuntimeException("Unknown bit mask " + bitMask);
                }
                result += resultBit;
                bit *= 2;
            }
            return result;
        }

        public Set<Long> getAddresses(long address) {
            long basicResult = 0;
            Set<Long> result = new HashSet<>();
            List<Long> floatingBits = new ArrayList<>();
            long bit = 1;
            for (int i = 0; i < 36; i++) {
                char bitMask = mask.charAt(mask.length() - (i + 1));
                long addressBit = address & bit;

                switch (bitMask) {
                    case '0': basicResult += addressBit; break;
                    case '1': basicResult += bit; break;
                    case 'X': floatingBits.add(bit); break;
                    default: throw new RuntimeException("Unknown bit mask " + bitMask);
                }
                bit *= 2;
            }
            result.add(basicResult);

            int prevSize = 0;
            while (result.size() > prevSize) {
                prevSize = result.size();
                for (Long floatingBit : floatingBits) {
                    Set<Long> newAddresses = new HashSet<>();
                    for (Long resultItem : result) {
                        newAddresses.add(resultItem | floatingBit);
                    }
                    result.addAll(newAddresses);
                }
            }

            return result;
        }
    }

    @ToString
    static class Write extends Instruction {
        final long address;
        final long value;

        static final Pattern pattern = Pattern.compile("mem\\[([0-9]+)\\] = ([0-9]+)");
        Write(String line) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) throw new RuntimeException("Unrecognised line " + line);
            address = Long.parseLong(matcher.group(1));
            value = Long.parseLong(matcher.group(2));
        }

        @Override
        void apply(Memory memory) {
            memory.writeValue(address, value);
        }

        void apply2(Memory memory) {
            Set<Long> addresses = memory.mask.getAddresses(address);
            addresses.forEach(a -> memory.writeDirect(a, value));
        }

    }

}
