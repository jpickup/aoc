package com.johnpickup.aoc2019;

import com.johnpickup.util.Direction;
import com.johnpickup.util.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    private static final List<String> itemsToAvoid = Arrays.asList(
            "photons",
            "infinite loop",
            "escape pod",
            "molten lava",
            "giant electromagnet",
            "boulder"           // we know it's too heavy
    );
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
                if (nextInputCommand == null) {
                    System.out.println(gameData);
                    System.out.println(currentState);
                    throw new RuntimeException("No input ready");
                }
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
                System.out.println(line);
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
            if (lastAction == null || lastAction instanceof Movement) {
                RoomState roomState = RoomState.parse(outputLines);
                System.out.println(roomState);
                currentState = new GameState(roomState.name, inventory);
                gameData.addGameState(currentState, roomState);
            } else {
                currentState = lastAction.apply(currentState);
                inventory = new HashSet<>(currentState.inventory);
                gameData.addGameState(currentState, gameData.gameStates.get(previousState));
            }
            gameData.addConnection(previousState.getLocation(), lastAction, currentState.getLocation());
        }

        private Set<String> generateInventory(Set<String> inventory, GameAction lastAction) {
            return Optional.ofNullable(lastAction).map(a -> a.generateInventory(inventory)).orElse(inventory);
        }

        private GameAction provideInput() {
            lastAction = gameSolver.nextAction(currentState);
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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Set<String> knownLocations = connections.keySet().stream().map(k -> k.roomName).collect(Collectors.toSet());
            sb.append("Explored locations (").append(knownLocations.size()).append(") : ").append(knownLocations);
            sb.append('\n');
            sb.append("Explored states (").append(gameStates.size()).append(")");
            sb.append('\n');
            return sb.toString();
        }

        public GameState initialState() {
            return new GameState(INITIAL_LOCATION, Collections.emptySet());
        }

        public void addConnection(String fromLocation, GameAction action, String toLocation) {
            if (action != null) {
                connections.put(new StateExit(fromLocation, action), toLocation);
                connections.put(new StateExit(toLocation, action.opposite()), fromLocation);
            }
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

        public Set<GameAction> getUnexploredMovements(GameState fromState) {
            if (fromState.location.equals(SECURITY_CHECKPOINT)) return Collections.emptySet();
            RoomState roomState = gameStates.get(fromState);
            Set<GameAction> possibleMovements = roomState.directions.stream().map(Movement::new).collect(Collectors.toCollection(HashSet::new));
            Set<GameAction> knownActions = connections.keySet().stream().filter(k -> k.roomName.equals(roomState.name)).map(StateExit::getAction).collect(Collectors.toSet());
            possibleMovements.removeAll(knownActions);

            return possibleMovements;
        }

        public Set<GameAction> getUnexploredTakeActions(GameState fromState) {
            RoomState roomState = gameStates.get(fromState);
            return roomState.items.stream()
                    .filter(item -> !itemsToAvoid.contains(item))
                    .filter(item -> !fromState.inventory.contains(item))
                    .map(TakeItem::new).collect(Collectors.toSet());
        }
    }

    @RequiredArgsConstructor
    static class GameSolver {
        final Droid droid;
        final GameData gameData;

        final List<GameAction> actionsTaken = new ArrayList<>();
        List<GameAction> movementsToTarget = null;

        boolean exploredMap = false;

        List<Set<String>> possibleInventories = null;
        Set<String> inventoryToTry = null;

        public GameAction nextAction(GameState currentState) {
            if (!exploredMap) {
                // which states have we not explored
                Set<GameAction> unexploredActions = gameData.getUnexploredTakeActions(currentState);

                if (unexploredActions.isEmpty()) {
                    unexploredActions = gameData.getUnexploredMovements(currentState);
                }

                if (!unexploredActions.isEmpty()) {
                    GameAction gameAction = unexploredActions.stream().findFirst().orElse(null);
                    actionsTaken.add(0, gameAction);
                    System.out.println("Exploring: " + gameAction);
                    return gameAction;
                }

                if (currentState.location.equals(GameData.SECURITY_CHECKPOINT)) {
                    movementsToTarget = actionsTaken.stream().filter(a -> a instanceof Movement).collect(Collectors.toList());
                }

                // back-track
                while (!actionsTaken.isEmpty()) {
                    GameAction reverse = actionsTaken.remove(0).opposite();
                    if (reverse instanceof DropItem) {
                        continue;
                    }
                    System.out.println("Backtracking: " + reverse);
                    return reverse;
                }
                exploredMap = true;
            }

            // navigate back to the security check
            if (movementsToTarget != null && !movementsToTarget.isEmpty()) {
                return movementsToTarget.remove(movementsToTarget.size()-1);
            }

            // try every combination of inventory items
            if (possibleInventories == null) {
                Set<String> fullInventory = new HashSet<>(currentState.inventory);
                possibleInventories = new ArrayList<>(Sets.subsets(fullInventory));
            }

            if (inventoryToTry == null) {
                inventoryToTry = possibleInventories.remove(0);
            }

            if (inventoryToTry.equals(currentState.inventory)) {
                inventoryToTry = null;
                // try this inventory
                return new Movement(Direction.SOUTH);
            }

            if (inventoryToTry == null && !possibleInventories.isEmpty()) {
                inventoryToTry = possibleInventories.remove(0);
            }

            if (inventoryToTry != null) {
                // what's missing?
                Set<String> missing = new HashSet<>(inventoryToTry);
                missing.removeAll(currentState.inventory);
                if (!missing.isEmpty()) {
                    return missing.stream().map(TakeItem::new).findFirst().orElseThrow(() -> new RuntimeException("nothing to take"));
                }

                // what should be dropped?
                Set<String> toDrop = new HashSet<>(currentState.inventory);
                toDrop.removeAll(inventoryToTry);
                if (!toDrop.isEmpty()) {
                    return toDrop.stream().map(DropItem::new).findFirst().orElseThrow(() -> new RuntimeException("nothing to drop"));
                }
            }
            throw new RuntimeException("Failed to find solution");
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
    @EqualsAndHashCode(exclude = "description")
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

        GameAction opposite();

        GameState apply(GameState state);
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

        @Override
        public GameAction opposite() {
            switch (direction) {
                case NORTH:
                    return new Movement(Direction.SOUTH);
                case SOUTH:
                    return new Movement(Direction.NORTH);
                case EAST:
                    return new Movement(Direction.WEST);
                case WEST:
                    return new Movement(Direction.EAST);
                default:
                    throw new RuntimeException("Unknown direction " + direction);
            }
        }

        @Override
        public GameState apply(GameState state) {
            throw new RuntimeException("Not implemented");
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

        @Override
        public GameAction opposite() {
            return new DropItem(item);
        }

        @Override
        public GameState apply(GameState state) {
            return new GameState(state.location, Sets.addElement(state.inventory, item));
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

        @Override
        public GameAction opposite() {
            return new TakeItem(item);
        }

        @Override
        public GameState apply(GameState state) {
            return new GameState(state.location, Sets.removeElement(state.inventory, item));
        }
    }
}
