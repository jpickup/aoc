package com.johnpickup.aoc2018;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.Dijkstra;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day15 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Users/john/Development/AdventOfCode/resources/2018/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
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

                Battle battle = new Battle(lines);
                System.out.println("Part 1: " + battle.outcome());
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static final char SPACE = '.';
    static final char WALL = '#';
    static final char ELF = 'E';
    static final char GOBLIN = 'G';

    static class Battle {
        final int width;
        final int height;
        List<Unit> units;
        Set<Coord> spaces;
        Set<Coord> walls;

        Battle(List<String> lines) {
            CharGrid grid = new CharGrid(lines);
            width = grid.getWidth();
            height = grid.getHeight();
            units = new ArrayList<>();
            units.addAll(findUnits(grid, UnitType.GOBLIN));
            units.addAll(findUnits(grid, UnitType.ELF));
            walls = new TreeSet<>();
            walls.addAll(grid.findCells('#'));
            spaces = new TreeSet<>();
            spaces.addAll(grid.findCells('.'));
            spaces.addAll(units.stream().map(Unit::getPosition).collect(Collectors.toSet()));
        }

        @Override
        public String toString() {
            CharGrid grid = new CharGrid(width, height, new char[width][height]);
            walls.forEach(w -> grid.setCell(w, WALL));
            spaces.forEach(w -> grid.setCell(w, SPACE));
            units.stream().filter(Unit::isAlive).forEach(u -> grid.setCell(u.position, u.type.ch));
            return grid.toString();
        }

        public long outcome() {
            System.out.println(this);
            long round = 0;
            boolean madeProgress = true;
            System.out.println("----- Initial -------");
            System.out.println(this);
            while (madeProgress) {
                madeProgress = takeTurn();
                if (madeProgress) round++;
                System.out.println("----- " + round + " ------");
                System.out.println(this);
            }
            long hitPoints = units.stream().filter(Unit::isAlive).map(Unit::getHitPoints).reduce(0, Integer::sum);
            return round * hitPoints;
        }

        private boolean takeTurn() {
            TreeMap<Coord, Unit> startingPositions = new TreeMap<>();
            units.stream().filter(Unit::isAlive).forEach(u -> startingPositions.put(u.position, u));
            boolean couldMove = takeMoveTurns(startingPositions.values());
            boolean couldAttack = takeAttackTurns(startingPositions.values());
            return couldMove || couldAttack;
        }

        private boolean takeMoveTurns(Collection<Unit> units) {
            boolean madeMove = false;
            for (Unit unit : units) {
                Action action = unit.takeMoveTurn();
                if (action != null) {
                    madeMove = true;
                    action.perform(this);
                }
            }
            return madeMove;
        }

        private boolean takeAttackTurns(Collection<Unit> units) {
            boolean tookAction = false;
            for (Unit unit : units) {
                Action action = unit.takeActionTurn();
                if (action != null) {
                    tookAction = true;
                    action.perform(this);
                }
            }
            return tookAction;
        }

        Set<Unit> findUnits(CharGrid grid, UnitType type) {
            return grid.findCells(type.ch).stream().map(c -> new Unit(this, type, c)).collect(Collectors.toSet());
        }

        public boolean isEmpty(Coord coord) {
            return spaces.contains(coord) &&  units.stream().noneMatch(u -> u.position.equals(coord));
        }

        public Set<Coord> emptySpaces() {
            Set<Coord> result = new TreeSet<>(spaces);
            result.removeAll(units.stream().filter(Unit::isAlive).map(Unit::getPosition).collect(Collectors.toSet()));
            return result;
        }

        public List<Coord> findPath(Coord from, Coord to) {
            ShortestPathSolver pathSolver = new ShortestPathSolver(this, from, to);
            Set<List<Coord>> routes = pathSolver.findRoutes();
            if (routes.isEmpty()) return null;
            List<List<Coord>> routesByLength = routes.stream().sorted(Comparator.comparingInt(List::size)).collect(Collectors.toList());
            int shortestLength = routesByLength.get(0).size();
            return routesByLength.stream()
                    .filter(r -> r.size() == shortestLength)
                    .min(Comparator.comparing(r -> r.get(0)))
                    .orElse(null);
        }

        public List<Unit> liveUnitsOfType(UnitType type) {
            return units.stream().filter(Unit::isAlive).filter(u -> u.type == type).collect(Collectors.toList());
        }
    }

    @RequiredArgsConstructor
    static class ShortestPathSolver extends Dijkstra<Coord> {
        final Battle battle;
        final Coord from;
        final Coord to;

        @Override
        protected Set<Coord> allStates() {
            Set<Coord> result = new TreeSet<>(battle.emptySpaces());
            result.add(from);
            result.add(to);
            return result;
        }

        @Override
        protected Coord initialState() {
            return from;
        }

        @Override
        protected Coord targetState() {
            return to;
        }

        @Override
        protected long calculateCost(Coord fromState, Coord toState) {
            return 1;
        }

        @Override
        protected boolean statesAreConnected(Coord toState, Coord fromState) {
            return fromState.isAdjacentTo4(toState);
        }

        @Override
        protected boolean findAllRoutes() {
            return true;
        }
    }

    @Getter
    static class Unit implements Comparable<Unit> {
        private static final int INITIAL_ATTACK_POWER = 3;
        private static final int INITIAL_HIT_POINTS = 200;
        final Battle battle;
        final UnitType type;
        final Coord initialPosition;
        Coord position;
        int attackPower;
        int hitPoints;

        public Unit(Battle battle, UnitType type, Coord initialPosition) {
            this.battle = battle;
            this.type = type;
            this.initialPosition = initialPosition;
            position = initialPosition;
            attackPower = INITIAL_ATTACK_POWER;
            hitPoints = INITIAL_HIT_POINTS;
        }

        @Override
        public String toString() {
            return type.ch + "(" + hitPoints + ")@" + position;
        }

        public Action takeMoveTurn() {
            List<Unit> enemyUnits = battle.liveUnitsOfType(type.enemy()).stream()
                    .sorted(Comparator.comparing(Unit::getPosition)).collect(Collectors.toList());
            if (enemyUnits.isEmpty()) return null;

            List<Unit> canAttack = enemyUnits.stream().filter(u -> u.position.isAdjacentTo4(position)).collect(Collectors.toList());
            List<Coord> spacesAdjacentToEnemies = enemyUnits.stream().flatMap(u -> u.adjacentSpaces().stream()).collect(Collectors.toList());
            if (canAttack.isEmpty()) {
                // MOVE
                List<Coord> shortestPath = null;
                for (Coord spaceAdjacentToEnemy : spacesAdjacentToEnemies) {
                    List<Coord> path = battle.findPath(position, spaceAdjacentToEnemy);
                    if (path == null) continue; // no move possible to that unit
                    if (shortestPath == null || path.size() < shortestPath.size()) shortestPath = path;
                }
                if (shortestPath == null) return null;
                return new Move(this, shortestPath.get(0));
            }
            return null;
        }

        private Set<Coord> adjacentSpaces() {
            Set<Coord> result = new HashSet<>();
            if (battle.isEmpty(position.north())) result.add(position.north());
            if (battle.isEmpty(position.south())) result.add(position.south());
            if (battle.isEmpty(position.east())) result.add(position.east());
            if (battle.isEmpty(position.west())) result.add(position.west());
            return result;
        }

        public Action takeActionTurn() {
            return battle.liveUnitsOfType(type.enemy()).stream()
                    .filter(u -> u.getPosition().isAdjacentTo4(position))
                    .min(Unit::compareTo)
                    .map(u -> new Attack(this, u))
                    .orElse(null);
        }

        @Override
        public int compareTo(Unit other) {
            if (other.hitPoints == this.hitPoints) return this.position.compareTo(other.position);
            return Integer.compare(this.hitPoints, other.hitPoints);
        }

        public void takeHit(int damage) {
            hitPoints -= damage;
        }

        public boolean isAlive() {
            return hitPoints > 0;
        }

        public void moveTo(Coord newPosition) {
            position = newPosition;
        }
    }

    interface Action {
        void perform(Battle battle);
    }

    @RequiredArgsConstructor
    static class Move implements Action {
        final Unit unit;
        final Coord newPosition;

        @Override
        public void perform(Battle battle) {
            System.out.println("Moving " + unit + " to " + newPosition);
            unit.moveTo(newPosition);
        }
    }

    @RequiredArgsConstructor
    static class Attack implements Action {
        final Unit attacker;
        final Unit target;

        @Override
        public void perform(Battle battle) {
            System.out.println("Attack of " + target + " by " + attacker);
            target.takeHit(attacker.attackPower);
        }
    }

    @RequiredArgsConstructor
    enum UnitType {
        GOBLIN(Day15.GOBLIN),
        ELF(Day15.ELF);
        final char ch;
        UnitType enemy() {
            switch (this) {
                case GOBLIN: return ELF;
                case ELF: return GOBLIN;
                default: throw new RuntimeException("Unknown unit type " + this);
            }
        }
    }
}
