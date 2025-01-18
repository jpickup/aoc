package com.johnpickup.aoc2019;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day23 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Network network = new Network(lines.get(0), 50);
                network.execute();
                long part1 = network.part1;
                System.out.println("Part 1: " + part1);
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Network {
        final int size;
        final Map<Integer, NIC> nics = new TreeMap<>();
        final Map<Integer, List<Packet>> packetQueues = new TreeMap<>();
        long part1 = 0L;
        Network(String line, int size) {
            this.size = size;
            for (int i = 0; i < size; i++) {
                nics.put(i, new NIC(this, i, line));
            }
        }

        void execute() {
            for (int i = 0 ; i < size; i++) {
                NIC nic = nics.get(i);
                Thread nicThread = new Thread(nic,"NIC"+nic.id);
                nicThread.start();
            }
        }

        Object mutex = new Object();

        void processPacket(PacketWithDestination packetWithDestination) {
            synchronized (mutex) {
                if (packetWithDestination.destination == 255) {
                    System.out.println(packetWithDestination);
                    part1 = packetWithDestination.getPacket().y;
                    nics.values().forEach(NIC::stop);
                }
                packetQueues.putIfAbsent(packetWithDestination.destination, new ArrayList<>());
                packetQueues.get(packetWithDestination.destination).add(packetWithDestination.packet);
            }
        }

        Packet getPacket(int destination) {
            synchronized (mutex) {
                packetQueues.putIfAbsent(destination, new ArrayList<>());
                List<Packet> queue = packetQueues.get(destination);
                if (queue.isEmpty()) {
                    return null;
                } else {
                    return queue.remove(0);
                }
            }
        }
    }

    static class NIC implements Runnable {
        final int id;
        final Program program;
        final Network network;
        NIC(Network network, int id, String line) {
            this.network = network;
            this.id = id;
            program = new Program(line);
            program.setInputSupplier(this::inputSupplier);
            program.setOutputConsumer(this::outputConsumer);
        }

        List<Long> outputs = new ArrayList<>();
        private void outputConsumer(Long value) {
            outputs.add(value);
            if (outputs.size() == 3) {
                network.processPacket(new PacketWithDestination(outputs));
                outputs.clear();
            }
        }

        boolean firstInput = true;

        List<Long> inputs = new ArrayList<>();
        private Long inputSupplier() {
            if (firstInput) {
                firstInput = false;
                return (long)id;
            }

            if (inputs.isEmpty()) {
                Packet packet = network.getPacket(id);
                if (packet != null) {
                    inputs.add(packet.x);
                    inputs.add(packet.y);
                }
            }

            if (inputs.isEmpty()) {
                return -1L;
            } else {
                return inputs.remove(0);
            }
        }

        @Override
        public void run() {
            program.execute();
        }

        public void stop() {
            program.stop();
        }
    }

    @Data
    static class Packet {
        final long x;
        final long y;
    }

    @Data
    static class PacketWithDestination {
        final int destination;
        final Packet packet;

        public PacketWithDestination(List<Long> outputs) {
            if (outputs.size() != 3) throw new RuntimeException("Invalid packet data size " + outputs.size());
            destination = (int)((long)(outputs.get(0)));
            packet = new Packet(outputs.get(1), outputs.get(2));
        }
    }
}
