package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day13.txt"))) {
            List<PacketPair> packetPairs = new ArrayList<>();
            List<String> lines = stream.collect(Collectors.toList());
            for (int i = 0; i < lines.size()/3; i++) {
                String s1 = lines.get(i*3);
                String s2 = lines.get(i*3+1);
                packetPairs.add(PacketPair.builder()
                        .left(parsePacket(s1))
                        .right(parsePacket(s2))
                        .build());
            }
            System.out.println(" --- PART 1 ---");
            int result = 0;
            int packetPairIdx = 1;
            List<Packet> packets = new ArrayList<>();
            for (PacketPair packetPair : packetPairs) {
                packets.add(packetPair.left);
                packets.add(packetPair.right);
                if (isOrdered(packetPair)) {
                    result += packetPairIdx;
                }
                packetPairIdx++;
            }
            System.out.println("Part 1 result: " + result);

            System.out.println(" --- PART 2 ---");
            Packet divider1 = parsePacket("[[2]]");
            Packet divider2 = parsePacket("[[6]]");
            packets.add(divider1);
            packets.add(divider2);

            List<Packet> sortedPackets = packets.stream().sorted(Day13::compare).collect(Collectors.toList());
            int divider1Idx = sortedPackets.indexOf(divider1)+1;
            int divider2Idx = sortedPackets.indexOf(divider2)+1;
            int result2 = divider1Idx * divider2Idx;
            System.out.println("Part 2 result: " + result2 + " (= " + divider1Idx + " * " + divider2Idx + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isOrdered(PacketPair packetPair) {
        return compare(packetPair.left, packetPair.right) <= 0;
    }

    private static int compare(Packet left, Packet right) {
        if (left == null) return -1;
        if (right == null) return 1;

        if (left instanceof PacketValue && right instanceof PacketValue) {
            int leftValue = ((PacketValue)left).value;
            int rightValue = ((PacketValue)right).value;
            return leftValue - rightValue;
        }

        PacketList leftList;
        if (left instanceof PacketList) {
            leftList = (PacketList)left;
        } else {
            leftList  = PacketList.builder().subPackets(Collections.singletonList(left)).build();
        }
        PacketList rightList;
        if (right instanceof PacketList) {
            rightList = (PacketList)right;
        } else {
            rightList  = PacketList.builder().subPackets(Collections.singletonList(right)).build();
        }

        int itemCompare = 0;
        for (int i = 0; i < Math.max(leftList.subPackets.size(), rightList.subPackets.size()); i++) {
            itemCompare = compare(leftList.subPackets.size() > i ? leftList.subPackets.get(i) : null,
                    rightList.subPackets.size() > i ? rightList.subPackets.get(i) : null);
            if (itemCompare < 0) return itemCompare;
            if (itemCompare > 0) return itemCompare;
        }
        return itemCompare;
    }

    static private Packet parsePacket(String s) {
        char ch = s.charAt(0);
        if (ch != '[') {
            throw new RuntimeException("Parse error: " + s);
        }

        // find the commas at this depth and split on that
        List<String> parts = new ArrayList<>();
        int lastPartIdx = 1;
        int depth = 0;
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '[':
                    depth++;
                    break;
                case ']':
                    depth--;
                    if (depth == 0) {     // final bracket
                        parts.add(s.substring(lastPartIdx, i));
                        lastPartIdx = i + 2;
                    }
                    break;
                case ',':
                    if (depth == 1) {
                        parts.add(s.substring(lastPartIdx, i));
                        lastPartIdx = i + 1;
                    }
                    break;
            }
        }
        return PacketList.builder()
                .subPackets(parts.stream()
                        .filter(part -> !part.isEmpty())
                        .map(part -> part.charAt(0) == '[' ? parsePacket(part) : PacketValue.parse(part))
                        .collect(Collectors.toList()))
                .build();
    }

    @ToString
    @Builder
    static class PacketPair {
        Packet left;
        Packet right;
    }

    static abstract class Packet {
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    static class PacketList extends Packet {
        List<Packet> subPackets;
        @Override
        public String toString() {
            return "[" + subPackets.stream().map(Object::toString).collect(Collectors.joining(",")) + "]";
        }
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    static class PacketValue extends Packet {
        final int value;
        @Override
        public String toString() {
            return Integer.toString(value);
        }
        static PacketValue parse(String s) {
            return PacketValue.builder().value(Integer.parseInt(s)).build();
        }
    }
}
