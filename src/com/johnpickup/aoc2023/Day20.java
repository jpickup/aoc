package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day20-test.txt"))) {
            List<Module> modules = stream.filter(s -> !s.isEmpty()).map(Module::parse).collect(Collectors.toList());

            System.out.println(modules);
            Map<String, Module> moduleMap = modules.stream().collect(Collectors.toMap(m -> m.id, m -> m));


        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }


    @RequiredArgsConstructor
    @ToString
    static abstract class Module {
        private final String id;
        private final List<String> targets;

        public abstract boolean process(boolean input);

        public abstract void reset();

        public abstract void tick();

        public static Module parse(String s) {
            List<String> targets = Arrays.stream(s.split("->")[1].trim().split(",")).collect(Collectors.toList());
            String id = s.split("->")[0].trim();
            switch(s.charAt(0)) {
                case '%': return new FlipFlop(id.substring(1), targets);
                case '&': return new Conjunction(id.substring(1), targets);
                default: return new Broadcaster(id, targets);
            }
        }
    }

    @ToString(callSuper = true)
    static class Broadcaster extends Module {
        public Broadcaster(String id, List<String> targets) {
            super(id, targets);
        }

        @Override
        public boolean process(boolean input) {
            return false;       // always produce a low
        }

        @Override
        public void reset() {
            // noop
        }
    }

    @ToString(callSuper = true)
    static class FlipFlop extends Module {
        boolean state = false;
        public FlipFlop(String id, List<String> targets) {
            super(id, targets);
        }

        @Override
        public void reset() {
            state = false;
        }

        @Override
        public boolean process(boolean input) {
            if (!input) {
                state = !state;
            }
            return state;
        }
    }


    @ToString(callSuper = true)
    static class Conjunction extends Module {
        private boolean state;
        public Conjunction(String id, List<String> targets) {
            super(id, targets);
        }

        @Override
        public void reset() {
            state = false;
        }
        @Override
        public boolean process(boolean input) {
            state = input;
            return false;
        }
    }
}
