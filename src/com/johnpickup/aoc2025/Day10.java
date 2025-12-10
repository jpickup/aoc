package com.johnpickup.aoc2025;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day10 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Machine> machines = stream
                        .filter(s -> !s.isEmpty())
                        .map(Machine::parse)
                        .toList();
                System.out.println(machines);

                long part1 = part1(machines);
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

    static long part1(List<Machine> machines) {
        return machines.stream().map(Machine::minimumPresses).reduce(0L, Long::sum);
    }

    @Data
    @RequiredArgsConstructor
    static class Machine {
        private final MachineState targetState;
        private final List<ButtonAction> buttonActions;
        private final List<Integer> joltages;

        public static Machine parse(String line) {
            String[] parts = line.split(" ");
            String targetPart = parts[0].substring(1, parts[0].length()-1);
            String joltagePart = parts[parts.length-1].substring(1, parts[parts.length-1].length()-1);
            List<ButtonAction> buttons = new ArrayList<>();
            for (int i = 1; i < parts.length-1; i++) {
                buttons.add(new ButtonAction(parts[i].substring(1, parts[i].length()-1)));
            }

            List<Integer> joltages = Arrays.stream(joltagePart.split(",")).map(Integer::parseInt).toList();;
            return new Machine(new MachineState(targetPart), buttons, joltages);
        }

        public long minimumPresses() {
            // bfs
            int depth = 0;
            Set<MachineState> levelStates = Collections.singleton(MachineState.initialState(targetState.size()));
            Set<MachineState> knownStates = new HashSet<>(levelStates); // ignore these if seen before
            while (!levelStates.contains(targetState)) {
                levelStates = nextStates(levelStates, knownStates);
            }
            return depth;
        }

        Set<MachineState> nextStates(Set<MachineState> currentStates, Set<MachineState> knownStates) {
            Set<MachineState> result = new HashSet<>();
            for (MachineState currentState : currentStates) {
                for (ButtonAction buttonAction : buttonActions) {
                    MachineState nextState = currentState.apply(buttonAction);
                    if (!knownStates.contains(nextState)) {
                        result.add(nextState);
                    }
                }
            }
            knownStates.addAll(result);
            return result;
        }
    }

    @RequiredArgsConstructor
    @Data
    static class MachineState {
        private final boolean[] buttons;
        public MachineState(String text) {
            buttons = new boolean[text.length()];
            for (int i = 0; i < text.length(); i++) buttons[i] = text.charAt(i) == '#';
        }

        @Override
        public String toString() {
            //return Arrays.stream(this.buttons).map(b -> b?"#":".").reduce("", String::concat);
            String result = "";
            for (boolean button : buttons) result += button ? "#" : ".";
            return result;
        }

        public static MachineState initialState(int size) {
            return new MachineState(new boolean[size]);
        }

        public MachineState apply(ButtonAction buttonAction) {
            MachineState result = new MachineState(this.buttons);
            buttonAction.buttonNumbers.forEach(bn -> result.buttons[bn] = !result.buttons[bn]);
            return result;
        }

        public int size() {
            return buttons.length;
        }
    }

    @Data
    static class ButtonAction {
        private final Set<Integer> buttonNumbers;
        public ButtonAction(String text) {
            buttonNumbers = Arrays.stream(text.split(",")).map(Integer::parseInt).collect(Collectors.toSet());
        }
    }

}
