package com.johnpickup.aoc2021;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day16Bits {

    private String input;

    public static void main(String[] args) {
        Day16Bits day16 = new Day16Bits("/Users/john/Development/AdventOfCode/resources/Day16Input.txt");
        long start = System.currentTimeMillis();
        day16.solve();
        long end = System.currentTimeMillis();
        System.out.printf("Completed in %d ms\n", end - start);
    }

    public Day16Bits(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {

            List<String> lines = stream.collect(Collectors.toList());
            input = lines.get(0);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void solve() {
        Data data = new Data(input);
        printBits(data.bits);

        List<Packet> allPackets = new ArrayList<>();
        while (data.hasData()) {
            List<Packet> packets = readPackets(data, 1);
            printPackets(packets);
            allPackets.addAll(packets);
        }
        int versionSum = sumVersions(allPackets);
        System.out.println("Version sum: " + versionSum);

        System.out.println("Expression: " + allPackets.get(0).expression());
        BigInteger result = evaluate(allPackets.get(0));
        System.out.println("Result: " + result);
    }

    private BigInteger evaluate(Packet packet) {
        switch (packet.type) {
            case 0: return packet.subPackets.stream().map(this::evaluate).reduce(BigInteger.ZERO, BigInteger::add);
            case 1: return packet.subPackets.stream().map(this::evaluate).reduce(BigInteger.ONE, BigInteger::multiply);
            case 2: return packet.subPackets.stream().map(this::evaluate).min(BigInteger::compareTo).orElse(BigInteger.ZERO);
            case 3: return packet.subPackets.stream().map(this::evaluate).max(BigInteger::compareTo).orElse(BigInteger.ZERO);
            case 4: return BigInteger.valueOf(packet.data);
            case 5: return evaluate(packet.subPackets.get(0)).compareTo(evaluate(packet.subPackets.get(1)))>0?BigInteger.ONE:BigInteger.ZERO;
            case 6: return evaluate(packet.subPackets.get(0)).compareTo(evaluate(packet.subPackets.get(1)))<0?BigInteger.ONE:BigInteger.ZERO;
            case 7: return evaluate(packet.subPackets.get(0)).compareTo(evaluate(packet.subPackets.get(1)))==0?BigInteger.ONE:BigInteger.ZERO;
            default: return BigInteger.ZERO;
        }
    }

    private int sumVersions(List<Packet> packets) {
        int result = 0;
        for (Packet packet : packets) {
            result += packet.version + sumVersions(packet.subPackets);
        }
        return result;
    }

    private List<Packet> readPackets(Data data, int packetsToRead) {
        List<Packet> result = new ArrayList<>();
        try {
            while (packetsRead(result) < packetsToRead && data.hasData()) {
                int version = data.consume(3);
                int type = data.consume(3);
                int read = 6;

                // literal packet
                if (type == 4) {
                    long value = 0;
                    boolean another;
                    do {
                        another = data.consume(1) == 1;
                        value = value << 4;
                        long valuePart = data.consume(4);
                        read += 5;
                        value |= valuePart;
                    } while (another);
                    // skip trailing zeroes
                    //if (read % 4 > 0) data.consume(4 - (read % 4));

                    result.add(new Packet(version, type, value));
                } else {
                    boolean lengthTypeId = data.consume(1) == 1;
                    Packet operatorPacket = new Packet(version, type, 0);
                    if (!lengthTypeId) {
                        // next 15 bits are total length of sub-packets
                        int totalLength = data.consume(15);
                        Data subData = new Data(data, totalLength);
                        operatorPacket.subPackets.addAll(readPackets(subData, Integer.MAX_VALUE));
                    } else {
                        // next 11 bits are sub-packet count
                        int subPacketCount = data.consume(11);
                        operatorPacket.subPackets.addAll(readPackets(data, subPacketCount));
                    }
                    result.add(operatorPacket);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("tried to read past end");
        }

        return result;
    }

    private int packetsRead(List<Packet> result) {
        // maybe needs to be recursive for sub-packets?
        return result.size();
    }

    private void printPackets(List<Packet> packets) {
        for (Packet packet : packets) {
            printPacket(packet);
        }
    }

    private void printPacket(Packet packet) {
        System.out.println(packet.toString());
    }

    private void printBits(boolean[] bits) {
        for (int i = 0; i < bits.length; i++) {
            System.out.print(bits[i]?'1':'0');
        }
        System.out.println();
    }



    static class Data {
        boolean[] bits;
        int pointer = 0;
        public Data(String hexString) {
            bits = hexStringToBits(hexString);
        }

        public Data(Data parent, int length) {
            bits = new boolean[length];
            for (int i = 0; i < length; i++) {
                bits[i] = parent.consume(1)==1;
            }
        }

        private boolean[] hexStringToBits(String hexString) {
            boolean[] result = new boolean[hexString.length()*4];
            for (int i = 0; i < hexString.length(); i++) {
                char ch = hexString.charAt(i);
                int value;
                if (ch >= '0' && ch <= '9') value = ch - '0'; else value = ch - 'A' + 10;
                result[i*4]= (value&8) == 8;
                result[i*4+1]= (value&4) == 4;
                result[i*4+2]= (value&2) == 2;
                result[i*4+3]= (value&1) == 1;
            }
            return result;
        }

        public int consume(int bitCount) {
            int result = 0;
            for (int i = 0; i < bitCount; i++) {
                result = result << 1;
                result |= bits[pointer]?1:0;
                pointer++;
            }
            return result;
        }

        public boolean hasData() {
            return pointer < bits.length-1;
        }
    }

    static class Packet {
        int version;
        int type;
        long data;
        List<Packet> subPackets = new ArrayList<>();

        public Packet(int version, int type, long data) {
            this.version = version;
            this.type = type;
            this.data = data;
        }

        public boolean isOperator() {
            return type != 4;
        }

        public boolean isLiteral() {
            return type == 4;
        }

        public String expression() {
            switch (type) {
                case 0: return "((" + subPackets.stream().map(Packet::expression).collect(Collectors.joining(")+(")) + "))";
                case 1: return "((" + subPackets.stream().map(Packet::expression).collect(Collectors.joining(")*(")) + "))";
                case 2: return "min (" + subPackets.stream().map(Packet::expression).collect(Collectors.joining(",")) + ")";
                case 3: return "max (" + subPackets.stream().map(Packet::expression).collect(Collectors.joining(",")) + ")";
                case 4: return Long.toString(data);
                case 5: return "((" + subPackets.stream().map(Packet::expression).collect(Collectors.joining(")>(")) + "))";
                case 6: return "((" + subPackets.stream().map(Packet::expression).collect(Collectors.joining(")<(")) + "))";
                case 7: return "((" + subPackets.stream().map(Packet::expression).collect(Collectors.joining(")==(")) + "))";
            }
            return "";
        }

        @Override
        public String toString() {
            return "Packet{" +
                    "version=" + version +
                    ", type=" + type +
                    ", data=" + data +
                    ", subPackets=" + subPackets +
                    '}';
        }
    }


}
