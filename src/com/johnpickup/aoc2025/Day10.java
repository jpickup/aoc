package com.johnpickup.aoc2025;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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
                //System.out.println(machines);

                long part1 = part1(machines);
                System.out.println("Part 1: " + part1);
                long part2 = part2(machines);
                System.out.println("Part 2: " + part2); // 10583 too low // 2147499647 too high
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
    @EqualsAndHashCode(exclude = {"targetState", "buttonActions", "joltages", "targetJoltageState", "allPossibleActions"})
    @ToString(exclude = {"targetState", "buttonActions", "joltages", "targetJoltageState", "allPossibleActions"})
    static class Machine {
        private final String description;
        private final MachineState targetState;
        private final List<ButtonAction> buttonActions;
        private final List<Integer> joltages;
        private final MachineJoltageState targetJoltageState;
        private final Set<Set<ButtonAction>> allPossibleActions;

        public static Machine parse(String line) {
            String[] parts = line.split(" ");
            String targetPart = parts[0].substring(1, parts[0].length() - 1);
            String joltagePart = parts[parts.length - 1].substring(1, parts[parts.length - 1].length() - 1);
            List<ButtonAction> buttons = new ArrayList<>();
            for (int i = 1; i < parts.length - 1; i++) {
                buttons.add(new ButtonAction(parts[i].substring(1, parts[i].length() - 1)));
            }
            List<Integer> joltages = Arrays.stream(joltagePart.split(",")).map(Integer::parseInt).toList();
            return new Machine(line, new MachineState(targetPart), buttons, joltages, MachineJoltageState.of(joltages), generatePossibleActions(buttons));
        }

        public long minimumStatePresses() {
            MachineState initialState = MachineState.initialState(targetState.size());
            Map<Set<ButtonAction>, MachineState> possibleStates = allPossibleSubsequentStates(initialState);
            return possibleStates.entrySet().stream()
                    .filter(e -> e.getValue().equals(targetState))   // the possible states that match the target
                    .map(e -> e.getKey().size())                     // number of pressed
                    .min(Integer::compareTo).orElseThrow();                                    // take the minimum
        }

        public long minimumJoltagePresses() {
            System.out.printf("***** SOLVING %s%n", this.description);
            long solution = minimumJoltagePressesToState(targetJoltageState);
            System.out.printf("***** %s = %d%n", this.description, solution);
            return solution;
        }

        private Map<Set<ButtonAction>, MachineState> allPossibleSubsequentStates(MachineState machineState) {
            Map<Set<ButtonAction>, MachineState> result = new HashMap<>();
            for (Set<ButtonAction> possibleAction : allPossibleActions) {
                result.put(possibleAction, machineState.applyActions(possibleAction));
            }
            return result;
        }

        private static final int FAILED_TO_SOLVE = 1000000;

        public long minimumJoltagePressesToState(MachineJoltageState to) {
            int multiplier = 1;
            while (to.allEven() && to.isValid() && !to.solved()) {
                to = to.divideByTwo();
                multiplier *= 2;
            }

            System.out.printf("Looking for %s%n", to);
            // get the parity of the target state joltage
            boolean[] targetParity = to.parity();
            if (allFalse(targetParity)) return 0;     // all done

            MachineState nextState = new MachineState(targetParity);
            MachineState fromState = MachineState.initialState(targetState.size());
            Map<Set<ButtonAction>, MachineState> possibleStates = allPossibleSubsequentStates(fromState).entrySet().stream()
                    .filter(e -> e.getValue().equals(nextState))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            System.out.printf("Possible states %d%n", possibleStates.size());

            if (possibleStates.isEmpty()) return FAILED_TO_SOLVE;

            MachineJoltageState from = MachineJoltageState.initialState(targetState.size());
            // recurse each and take the least
            long minimum = FAILED_TO_SOLVE;
            for (Map.Entry<Set<ButtonAction>, MachineState> possibleState : possibleStates.entrySet()) {
                int stepsForThis = possibleState.getKey().size();
                MachineJoltageState afterActions = from.applyActions(possibleState.getKey());
                MachineJoltageState newTarget = calculateNewTarget(to, afterActions);

                if (!newTarget.isValid()) continue;
                long stepsNext = minimumJoltagePressesToState(newTarget);
                long totalSteps = multiplier * (stepsForThis + stepsNext);
                if (totalSteps < FAILED_TO_SOLVE) minimum = totalSteps;
            }
            //System.out.printf("Found %d for for %s%n", minimum, to);
            if (minimum == FAILED_TO_SOLVE) throw new RuntimeException("Can't solve");
            return minimum;
        }

        private MachineJoltageState calculateNewTarget(MachineJoltageState before, MachineJoltageState actions) {
            MachineJoltageState result = new MachineJoltageState(new int[before.size()]);
            for (int i = 0; i < before.size(); i++) {
                result.joltages[i] = (before.joltages[i] - actions.joltages[i]);
            }
            return result;
        }

        private boolean allFalse(boolean[] parity) {
            boolean result = true;
            for (boolean b : parity) {
                result &= (!b);
            }
            return result;
        }

        private static Set<Set<ButtonAction>> generatePossibleActions(List<ButtonAction> buttonActions) {
            Set<Set<ButtonAction>> result = new HashSet<>();
            int maxVal = 0x1 << buttonActions.size();
            for (int i = 1; i < maxVal; i++) {
                Set<ButtonAction> oneSet = new HashSet<>();
                int bitMask = i;
                for (int buttonIdx = 0; buttonIdx < buttonActions.size(); buttonIdx++) {
                    if (bitMask % 2 == 1) oneSet.add(buttonActions.get(buttonIdx));
                    bitMask = bitMask / 2;
                    if (bitMask == 0) break;
                }
                result.add(oneSet);
            }
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

        public MachineState applyAction(ButtonAction buttonAction) {
            boolean[] result = new boolean[size()];
            System.arraycopy(this.buttons, 0, result, 0, size());
            buttonAction.buttonNumbers.forEach(bn -> result[bn] = !result[bn]);
            return new MachineState(result);
        }

        public MachineState applyActions(Set<ButtonAction> possibleAction) {
            MachineState result = new MachineState(buttons);
            for (ButtonAction action : possibleAction) {
                result = result.applyAction(action);
            }
            return result;
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
            buttonAction.buttonNumbers.forEach(bn -> result[bn] ++);
            return new MachineJoltageState(result);
        }

        public int size() {
            return joltages.length;
        }

        public boolean[] parity() {
            boolean[] result = new boolean[joltages.length];
            for (int i = 0; i < joltages.length; i++) {
                result[i] = joltages[i] % 2 == 1;
            }
            return result;
        }

        public MachineJoltageState applyActions(Set<ButtonAction> actions) {
            MachineJoltageState result = this;
            for (ButtonAction action : actions) {
                result = result.apply(action);
            }
            return result;
        }

        public boolean isValid() {
            boolean result = true;
            for (int i = 0; i < size(); i++) {
                result &= joltages[i] >= 0;
            }
            return result;
        }

        public boolean allEven() {
            boolean result = true;
            for (int i = 0; i < size(); i++) {
                result &= joltages[i] % 2 == 0;
            }
            return result;
        }

        public MachineJoltageState divideByTwo() {
            int[] result = new int[size()];
            for (int i = 0; i < size(); i++) {
                result[i] = joltages[i] / 2;
            }
            return new MachineJoltageState(result);
        }

        public boolean solved() {
            boolean result = true;
            for (int i = 0; i < size(); i++) {
                result &= joltages[i] == 0;
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
    }

}
