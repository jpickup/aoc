package com.johnpickup.aoc2019;

import com.johnpickup.util.DijkstraWithoutFullState;
import com.johnpickup.util.Direction;
import com.johnpickup.util.Sets;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day25 {
    static boolean isTest;

    public static void main(String[] args) {
        String day = new Object() {
        }.getClass().getEnclosingClass().getSimpleName();
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

                boolean interactive = query("Interactive? ").equalsIgnoreCase("y");
                Droid droid = new Droid(lines.get(0), interactive);

                droid.solveAdventure();

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static String query(String prompt) {
        System.out.print(prompt);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class Droid {
        private static final String COMMAND = "Command?";
        final Program program;
        final GameSolver gameSolver;
        final GameData gameData;
        GameState currentState;

        List<Long> input = new ArrayList<>();

        Droid(String line, boolean interactive) {
            program = new Program(line);
            program.setOutputConsumer(interactive ? this::interactiveOutputConsumer : this::automatedOutputConsumer);
            program.setInputSupplier(interactive ? this::interactiveInputSupplier : this::automatedInputSupplier);
            gameData = new GameData();
            currentState = gameData.initialState();
            gameSolver = interactive ? null : new GameSolver(this, gameData);
        }

        private Long automatedInputSupplier() {
            if (input.isEmpty()) {
                GameAction nextInputCommand = provideInput();
                if (nextInputCommand == null) throw new RuntimeException("No input ready");
                input.addAll(stringToAscii(nextInputCommand.command() + "\n"));
                System.out.println("Command: " + nextInputCommand);
            }
            return input.remove(0);
        }

        List<Long> output = new ArrayList<>();
        List<String> outputLines = new ArrayList<>();

        private void automatedOutputConsumer(Long value) {
            if (value == 10L) {
                String line = asciiToString(output);
                outputLines.add(line);
                output.clear();
                if (line.equals(COMMAND)) {
                    parseOutput(outputLines);
                    outputLines.clear();
                }
            } else {
                output.add(value);
            }
        }

        GameAction lastAction = null;
        Set<String> inventory = Collections.emptySet();

        private void parseOutput(List<String> outputLines) {
            GameState previousState = currentState;
            RoomState roomState = RoomState.parse(outputLines);
            System.out.println(roomState);
            currentState = new GameState(roomState.name, generateInventory(inventory, lastAction));
            gameData.addConnection(previousState.getLocation(), lastAction, currentState.getLocation());
            gameData.addGameState(currentState, roomState);
        }

        private Set<String> generateInventory(Set<String> inventory, GameAction lastAction) {
            return lastAction.generateInventory(inventory);
        }

        private GameAction provideInput() {
            lastAction = gameSolver.nextAction();
            return lastAction;
        }

        private Long interactiveInputSupplier() {
            if (input.isEmpty()) {
                input.addAll(stringToAscii(query("> ")));
                input.add(10L);
            }
            return input.remove(0);
        }

        private void interactiveOutputConsumer(Long value) {
            char ch = (char) ((long) value);
            System.out.print(ch);
        }

        public void solveAdventure() {
            program.reset();
            program.execute();
        }


        private String asciiToString(List<Long> values) {
            StringBuilder sb = new StringBuilder();
            values.stream().filter(l -> l < 256).map(l -> (char) (l % 256)).forEach(sb::append);
            return sb.toString();
        }

        private List<Long> stringToAscii(String s) {
            List<Long> result = new ArrayList<>();
            for (int i = 0; i < s.length(); i++) {
                int ch = s.charAt(i);
                result.add((long) ch);
            }
            return result;
        }
    }

    static class GameData {
        private static final String INITIAL_LOCATION = "Hull Breach";
        private static final String FINAL_LOCATION = "Pressure-Sensitive Floor";
        private static final String SECURITY_CHECKPOINT = "Security Checkpoint";
        final Map<StateExit, String> connections = new HashMap<>();
        final Map<GameState, RoomState> gameStates = new HashMap<>();

        public GameState initialState() {
            return new GameState(INITIAL_LOCATION, Collections.emptySet());
        }

        public void addConnection(String fromLocation, GameAction action, String toLocation) {
            connections.put(new StateExit(fromLocation, action), toLocation);
        }

        public void addGameState(GameState gameState, RoomState roomState) {
            gameStates.put(gameState, roomState);
        }

        public boolean statesAreConnected(GameState fromState, GameState toState) {
            if (fromState.location.equals(toState.location)) {
                return canCollectItem(fromState, toState)
                        || canDropItem(fromState, toState);
            }  else {
                return roomsAreConnected(fromState.location, toState.location);
            }
        }

        private boolean canDropItem(GameState fromState, GameState toState) {
            // can only drop an item at the security checkpoint
            return fromState.location.equals(SECURITY_CHECKPOINT)
                    && (fromState.inventory.size() - 1 == toState.inventory.size())
                        && fromState.inventory.containsAll(toState.inventory);
        }

        private boolean canCollectItem(GameState fromState, GameState toState) {
            // same location = collects an item
            boolean collectsItem = (fromState.inventory.size() + 1 == toState.inventory.size())
                    && toState.inventory.containsAll(fromState.inventory);
            return collectsItem
                    && roomContainsItem(fromState,
                    Sets.disjoint(fromState.inventory, toState.inventory).stream().findFirst().orElse(null));
        }

        private boolean roomsAreConnected(String fromLocation, String toLocation) {
            return getAdjacentLocations(fromLocation).contains(toLocation);
        }

        private boolean roomContainsItem(GameState state, String item) {
            return gameStates.get(state).items.contains(item);
        }

        public Set<String> getAdjacentLocations(String location) {
            return connections.entrySet().stream()
                    .filter(e -> e.getKey().roomName.equals(location))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toSet());
        }

        public Set<String> getLocationItems(GameState gameState) {
            return Optional.ofNullable(gameStates.get(gameState)).map(rs -> rs.items).orElse(Collections.emptySet());
        }

        public boolean isFinalLocation(GameState state) {
            return state.location.equals(FINAL_LOCATION);
        }
    }

    @RequiredArgsConstructor
    static class GameSolver extends DijkstraWithoutFullState<GameState> {
        final Droid droid;
        final GameData gameData;

        public GameAction nextAction() {
            // TODO: how to implement?
            return null;
        }

        @Override
        protected Set<GameState> adjacentStates(GameState gameState) {
            Set<GameState> result = new HashSet<>();
            // add all adjacent rooms
            Set<String> adjacentLocations = gameData.getAdjacentLocations(gameState.location);
            result.addAll(adjacentLocations.stream()
                    .map(loc -> new GameState(loc, gameState.inventory))
                    .collect(Collectors.toSet()));
            // add all states where we collect an item
            Set<String> availableItems = gameData.getLocationItems(gameState);
            result.addAll(availableItems.stream()
                    .map(item -> new GameState(gameState.location, Sets.addElement(gameState.inventory, item)))
                    .collect(Collectors.toSet()));

            // remove any known bad states that end the game - we can just not go there
            return result.stream().filter(this::isTerminalStateToAvoid).collect(Collectors.toSet());
        }

        private static final List<String> itemsToAvoid = Arrays.asList(
                "photons",
                "infinite loop",
                "escape pod",
                "molten lava",
                "giant electromagnet",
                "boulder"           // we know it's too heavy
        );

        private static final List<String> locationsToAvoid = Collections.emptyList();

        private boolean isTerminalStateToAvoid(GameState gs) {
            boolean result = false;
            // add in checks for states that cause failure
            result |= containsItemToAvoid(gs.inventory);
            result |= locationsToAvoid.contains(gs.location);
            return result;
        }

        private boolean containsItemToAvoid(Set<String> inventory) {
            return inventory.stream().anyMatch(itemsToAvoid::contains);
        }

        @Override
        protected GameState initialState() {
            return gameData.initialState();
        }

        @Override
        protected boolean isTargetState(GameState state) {
            return gameData.isFinalLocation(state);
        }

        @Override
        protected GameState targetState() {
            // use isTargetState instead - should never be called
            throw new RuntimeException("Not implemented");
        }

        @Override
        protected long calculateCost(GameState fromState, GameState toState) {
            return 1;
        }

        @Override
        protected boolean statesAreConnected(GameState toState, GameState fromState) {
            return gameData.statesAreConnected(fromState, toState);
        }

        @Override
        protected boolean findAllRoutes() {
            return false;
        }
    }

    @Data
    static class StateExit {
        final String roomName;
        final GameAction action;
    }

    @Data
    static class GameState {
        final String location;
        final Set<String> inventory;
    }

    @Data
    static class RoomState {
        final String name;
        final String description;
        final Set<Direction> directions;
        final Set<String> items;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(" -- ").append(name).append("\n");
            sb.append(description).append("\n");
            sb.append(directions).append("\n");
            sb.append(items);
            return sb.toString();
        }

        static RoomState parse(List<String> lines) {
            String foundName = null;
            String foundDescription = "";
            Set<Direction> foundDirections = new HashSet<>();
            Set<String> foundItems = new HashSet<>();
            int section = 0;
            for (String line : lines) {
                if (section == 0 && line.startsWith("==")) {
                    foundName = line.replaceAll("==", "").trim();
                    section = 1;
                } else if (section == 1) {
                    if (line.trim().equals("Doors here lead:")) {
                        section = 2;
                    } else {
                        foundDescription += line.trim();
                    }
                } else if (section == 2) {
                    if (line.trim().equals("Items here:")) {
                        section = 3;
                    }
                    if (line.startsWith("-"))
                        foundDirections.add(Direction.valueOf(line.replaceAll("-", "").trim().toUpperCase()));
                } else if (section == 3) {
                    if (line.trim().equals("Items here:")) {
                        section = 4;
                    }
                    if (line.startsWith("-")) foundItems.add(line.replaceAll("-", "").trim());
                }
            }
            if (foundName == null) throw new RuntimeException("Location with no name! " + lines);
            if (foundDescription.isEmpty()) throw new RuntimeException("Location with no description! " + lines);
            if (foundDirections.isEmpty()) throw new RuntimeException("Location with no doors! " + lines);

            return new RoomState(foundName, foundDescription, foundDirections, foundItems);
        }
    }

    interface GameAction {
        String command();

        Set<String> generateInventory(Set<String> inventory);
    }

    @Data
    static class Movement implements GameAction {
        final Direction direction;

        @Override
        public String command() {
            switch (direction) {
                case NORTH:
                    return "north";
                case SOUTH:
                    return "south";
                case EAST:
                    return "east";
                case WEST:
                    return "west";
                default:
                    throw new RuntimeException("Unknown direction " + direction);
            }
        }

        @Override
        public Set<String> generateInventory(Set<String> inventory) {
            return inventory;
        }
    }

    @Data
    static class TakeItem implements GameAction {
        final String item;

        @Override
        public String command() {
            return "take " + item;
        }

        @Override
        public Set<String> generateInventory(Set<String> inventory) {
            return Sets.addElement(inventory, item);
        }
    }

    @Data
    static class DropItem implements GameAction {
        final String item;

        @Override
        public String command() {
            return "drop " + item;
        }

        @Override
        public Set<String> generateInventory(Set<String> inventory) {
            return Sets.removeElement(inventory, item);
        }
    }
}
