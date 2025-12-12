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
        List<String> inputFilenames = getInputFilenames(new Object() {
        });
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
                long part2 = part2(machines);
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static long part1(List<Machine> machines) {
        return machines.stream().map(Machine::minimumStatePresses).reduce(0L, Long::sum);
    }

    static long part2(List<Machine> machines) {
        return machines.stream().map(Machine::minimumJoltagePresses).reduce(0L, Long::sum);
    }

    @Data
    @RequiredArgsConstructor
    static class Machine {
        private final MachineState targetState;
        private final List<ButtonAction> buttonActions;
        private final List<Integer> joltages;
        private final MachineJoltageState targetJoltageState;

        public static Machine parse(String line) {
            String[] parts = line.split(" ");
            String targetPart = parts[0].substring(1, parts[0].length() - 1);
            String joltagePart = parts[parts.length - 1].substring(1, parts[parts.length - 1].length() - 1);
            List<ButtonAction> buttons = new ArrayList<>();
            for (int i = 1; i < parts.length - 1; i++) {
                buttons.add(new ButtonAction(parts[i].substring(1, parts[i].length() - 1)));
            }

            List<Integer> joltages = Arrays.stream(joltagePart.split(",")).map(Integer::parseInt).toList();

            return new Machine(new MachineState(targetPart), buttons, joltages, MachineJoltageState.of(joltages));
        }

        public long minimumStatePresses() {
            // bfs
            int depth = 0;
            Set<MachineState> levelStates = Collections.singleton(MachineState.initialState(targetState.size()));
            Set<MachineState> knownStates = new HashSet<>(levelStates); // ignore these if seen before
            while (!levelStates.contains(targetState)) {
                levelStates = nextStates(levelStates, knownStates);
                depth++;
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
            if (currentStates.equals(result)) throw new RuntimeException("No progress made");
            return result;
        }

        public long minimumJoltagePresses() {
            int depth = 0;
            Set<MachineJoltageState> levelStates = Collections.singleton(MachineJoltageState.initialState(targetState.size()));
            Map<ButtonAction, Integer> maxPushesByButton = new HashMap<>();

            for (ButtonAction buttonAction : buttonActions) {
                maxPushesByButton.put(buttonAction, calcMaxPushesForJoltage(buttonAction));
            }

            System.out.println(maxPushesByButton);

            Set<ButtonSequence> knownButtonSequences = new HashSet<>(); // ignore these if seen before
            Set<MachineJoltageState> knownStates = new HashSet<>(levelStates);
            knownButtonSequences.add(ButtonSequence.initial(buttonActions));
            while (!levelStates.contains(targetJoltageState)) {
                levelStates = nextJoltageStates(levelStates, ButtonSequence.initial(buttonActions), knownButtonSequences, knownStates, maxPushesByButton);
                depth++;
            }
            System.out.println("Solved " + this);
            return depth;
        }

        // from the target joltages, which action in this button can be performed the least times,
        // that's the most times we can push this button
        private int calcMaxPushesForJoltage(ButtonAction buttonAction) {
            int result = Integer.MAX_VALUE;

            for (int buttonNumber : buttonAction.buttonNumbers) {
                int targetForButton = targetJoltageState.getJoltages()[buttonNumber];
                if (targetForButton < result) result = targetForButton;
            }
            return result;
        }

        Set<MachineJoltageState> nextJoltageStates(Set<MachineJoltageState> currentStates, ButtonSequence sequenceToGetHere, Set<ButtonSequence> knownButtonSequences, Set<MachineJoltageState> knownStates, Map<ButtonAction, Integer> maxPushesByButton) {
            Set<MachineJoltageState> result = new HashSet<>();
            for (MachineJoltageState currentState : currentStates) {
                for (ButtonAction buttonAction : buttonActions) {
                    if (sequenceToGetHere.getButtonPushes().get(buttonAction) < maxPushesByButton.get(buttonAction)) {
                        ButtonSequence nextButtonSequence = sequenceToGetHere.pushButton(buttonAction);
                        if (!knownButtonSequences.contains(nextButtonSequence)) {
                            MachineJoltageState nextState = currentState.apply(buttonAction);
                            if (nextState.isValidForTarget(targetJoltageState)) {
                                knownButtonSequences.add(nextButtonSequence);
                                result.add(nextState);
                            }
                        }
                    }
                }
            }
            knownStates.addAll(result);
            if (currentStates.equals(result)) throw new RuntimeException("No progress made");
            return result;
        }
    }

    @Data
    static class ButtonSequence {
        private final Map<ButtonAction, Integer> buttonPushes = new HashMap<>();

        public static ButtonSequence initial(Collection<ButtonAction> possibleActions) {
            ButtonSequence result = new ButtonSequence();
            for (ButtonAction possibleAction : possibleActions) {
                result.buttonPushes.put(possibleAction, 0);
            }
            return result;
        }

        public ButtonSequence pushButton(ButtonAction action) {
            ButtonSequence result = new ButtonSequence();
            result.buttonPushes.putAll(this.buttonPushes);
            result.buttonPushes.put(action, result.buttonPushes.getOrDefault(action, 0) + 1);
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
            boolean[] result = new boolean[size()];
            if (size() >= 0) System.arraycopy(this.buttons, 0, result, 0, size());
            buttonAction.buttonNumbers.forEach(bn -> result[bn] = !result[bn]);
            return new MachineState(result);
        }

        public int size() {
            return buttons.length;
        }
    }

    @RequiredArgsConstructor
    @Data
    static class MachineJoltageState {
        private final int[] joltages;

        public static MachineJoltageState initialState(int size) {
            return new MachineJoltageState(new int[size]);
        }

        public static MachineJoltageState of(List<Integer> joltages) {
            int[] result = new int[joltages.size()];
            for (int i = 0; i < joltages.size(); i++) {
                result[i] = joltages.get(i);
            }
            return new MachineJoltageState(result);
        }

        @Override
        public String toString() {
            return Arrays.toString(joltages);
        }

        public MachineJoltageState apply(ButtonAction buttonAction) {
            int[] result = new int[size()];
            if (size() >= 0) System.arraycopy(this.joltages, 0, result, 0, size());
            buttonAction.buttonNumbers.forEach(bn -> result[bn]++);
            return new MachineJoltageState(result);
        }

        public int size() {
            return joltages.length;
        }

        public boolean isValidForTarget(MachineJoltageState targetState) {
            boolean result = true;
            for (int i = 0; i < size(); i++) {
                result &= joltages[i] <= targetState.joltages[i];
            }
            return result;
        }
    }

    @Data
    static class ButtonAction {
        private final Set<Integer> buttonNumbers;

        public ButtonAction(String text) {
            buttonNumbers = Arrays.stream(text.split(",")).map(Integer::parseInt).collect(Collectors.toSet());
        }

        @Override
        public String toString() {
            return buttonNumbers.stream().map(bn -> Integer.toString(bn)).reduce("", String::concat);
        }
    }

}
