package com.johnpickup.aoc2019;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day23 {
    static boolean isTest;

    public static void main(String[] args) {
        String day = new Object() {
        }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Network network = new Network(lines.get(0), 50);
                network.execute(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Network {
        static final int NAT = 255;
        final int size;
        final Map<Integer, NIC> nics = new TreeMap<>();
        final Map<Integer, List<Packet>> packetQueues = new TreeMap<>();

        final NAT nat;

        int part;
        long part1 = 0L;
        long part2 = 0L;

        Network(String line, int size) {
            this.size = size;
            nat = new NAT(this, size);
            for (int i = 0; i < size; i++) {
                nics.put(i, new NIC(this, i, line));
            }
        }

        void execute(int part) {
            System.out.println("Executing part " + part);
            this.part = part;
            for (int i = 0; i < size; i++) {
                NIC nic = nics.get(i);
                Thread nicThread = new Thread(nic, "NIC" + nic.id);
                nicThread.start();
            }
        }

        private final Object mutex = new Object();

        void processPacket(int source, PacketWithDestination packetWithDestination) {
            synchronized (mutex) {
                if (source == NAT && packetWithDestination.equals(previousNatPacket)) {
                    // part 2 done
                    part2 = packetWithDestination.packet.y;
                    nics.values().forEach(NIC::stop);
                    System.out.println("Part 2: " + part2);
                } else {
                    previousNatPacket = packetWithDestination;
                }

                if (packetWithDestination.destination == NAT) {
                    if (part == 1) {
                        System.out.println(packetWithDestination);
                        part1 = packetWithDestination.getPacket().y;
                        nics.values().forEach(NIC::stop);
                        System.out.println("Part 1: " + part1);
                    } else {
                        nat.processPacket(packetWithDestination.packet);
                    }
                } else {
                    packetQueues.putIfAbsent(packetWithDestination.destination, new ArrayList<>());
                    packetQueues.get(packetWithDestination.destination).add(packetWithDestination.packet);
                }
            }
        }

        PacketWithDestination previousNatPacket = null;

        Packet getPacket(int destination) {
            Packet result;
            synchronized (mutex) {
                packetQueues.putIfAbsent(destination, new ArrayList<>());
                List<Packet> queue = packetQueues.get(destination);
                if (queue.isEmpty()) {
                    result = null;
                } else {
                    nat.setIdleStatus(destination, false);
                    result = queue.remove(0);
                }
                if (result == null && nat.networkIsIdle()) {
                    PacketWithDestination natPacket = nat.generatePacket();
                    if (natPacket != null) {
                        System.out.println("NAT: " + natPacket);
                        processPacket(NAT, natPacket);
                    }
                }
                if (result == null) {
                    nat.setIdleStatus(destination, true);
                }
            }
            return result;
        }

        public boolean queuesAllEmpty() {
            synchronized (mutex) {
                return packetQueues.values().stream().allMatch(List::isEmpty);
            }
        }

        public boolean nicsIdle() {
            return nics.values().stream().allMatch(NIC::isIdle);
        }
    }

    static class NAT {
        private static final Integer IDLE_THRESHOLD = 2;
        private final Network network;
        final int size;
        Packet lastPacket;

        Map<Integer, Integer> idleStatus = new TreeMap<>();

        NAT(Network network, int size) {
            this.network = network;
            this.size = size;
            for (int i = 0; i < size; i++) {
                idleStatus.put(i, 0);
            }
        }

        private final Object mutex = new Object();

        public void setIdleStatus(int destination, boolean isIdle) {
            synchronized (mutex) {
                if (destination != Network.NAT)
                    // increment the idle count if we are idle, else reset to zero
                    idleStatus.put(destination, isIdle ? idleStatus.get(destination)+1 : 0);
            }
        }

        public void processPacket(Packet packet) {
            synchronized (mutex) {
                lastPacket = packet;
            }
        }

        public PacketWithDestination generatePacket() {
            synchronized (mutex) {
                if (lastPacket == null) return null;
                return new PacketWithDestination(0, lastPacket);
            }
        }

        public boolean networkIsIdle() {
            synchronized (mutex) {
                return idleStatus.values().stream().allMatch(v -> v >= IDLE_THRESHOLD)
                        && network.queuesAllEmpty()
                        && network.nicsIdle();
            }
        }
    }

    static class NIC implements Runnable {
        final int id;
        final Program program;
        final Network network;
        boolean idle = false;

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
                network.processPacket(id, new PacketWithDestination(outputs));
                outputs.clear();
            }
        }

        boolean firstInput = true;

        List<Long> inputs = new ArrayList<>();

        private final Object mutex = new Object();

        public boolean isIdle() {
            synchronized (mutex) {
                return idle;
            }
        }

        private Long inputSupplier() {
            if (firstInput) {
                firstInput = false;
                return (long) id;
            }

            if (inputs.isEmpty()) {
                Packet packet = network.getPacket(id);
                if (packet != null) {
                    inputs.add(packet.x);
                    inputs.add(packet.y);
                }
            }

            synchronized (mutex) {
                if (inputs.isEmpty()) {
                    idle = true;
                    return -1L;
                } else {
                    idle = false;
                    return inputs.remove(0);
                }
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
            destination = (int) ((long) (outputs.get(0)));
            packet = new Packet(outputs.get(1), outputs.get(2));
        }

        public PacketWithDestination(int destination, Packet packet) {
            this.destination = destination;
            this.packet = packet;
        }
    }
}
