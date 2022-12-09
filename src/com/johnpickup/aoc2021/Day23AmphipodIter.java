package com.johnpickup.aoc2021;

import java.util.*;
import java.util.stream.Collectors;

public class Day23AmphipodIter {
    private static final int MAX_ENERGY = 12700;

    public static void main(String[] args) {
        Day23AmphipodIter day23 = new Day23AmphipodIter();
        //Board initial = testBoard();
        Board initial = realBoard();
        Set<Board> solutions = day23.solve(initial);
        System.out.println(solutions);
    }

    private Set<Board> solve(Board board) {
        if (board.solved()) return Collections.singleton(board);

        Set<Board> knownBoards = new HashSet<>();
        Set<Board> solvedBoards = new HashSet<>();
        Set<Board> unknownBoards = new HashSet<>();
        unknownBoards.add(board);

        while (!unknownBoards.isEmpty()) {
            System.out.println("Unknown:" + unknownBoards.size() + "  Solved:" + solvedBoards.size());
            solvedBoards.stream().forEach(b -> System.out.println(b.totalEnergy));
            List<Board> newBoards = new ArrayList<>();
            for (Board unknownBoard : unknownBoards) {
                Set<Move> possibleMoves = unknownBoard.getPossibleMoves();

                newBoards.addAll(possibleMoves.stream()
                        .filter(move -> !isRetrograde(unknownBoard, move))
                        .map(move -> unknownBoard.applyMove(move))
                        .filter(b -> knownBoards.stream().noneMatch(kb -> kb.sameAmphipods(b)))
                        .collect(Collectors.toSet()));
            }
            knownBoards.addAll(newBoards);
            solvedBoards.addAll(newBoards.stream().filter(Board::solved).collect(Collectors.toSet()));
            unknownBoards = newBoards.stream()
                    .filter(b -> !b.solved())
                    .filter(b -> b.totalEnergy < MAX_ENERGY)
                    .collect(Collectors.toSet());
        }
        return solvedBoards;
    }

    private boolean isRetrograde(Board board, Move move) {
        Character amphi = board.amphipods.get(move.from);
        return amphi != null &&
                (
                        (amphi == 'A' && move.from == 11 && board.amphipods.get(15) != null && 'A'==board.amphipods.get(15))
                                || (amphi == 'B' && move.from == 12 && board.amphipods.get(16)!=null && 'B'==board.amphipods.get(16))
                                || (amphi == 'C' && move.from == 13 && board.amphipods.get(17)!=null && 'C'==board.amphipods.get(17))
                                || (amphi == 'D' && move.from == 14 && board.amphipods.get(18)!=null && 'D'==board.amphipods.get(18))
                );
    }

    private static Board testBoard() {
        final Map<Integer, Character> amphipods = new HashMap<>();
        // test input
        /*
 01234567890
#############
#...........#
###B#C#B#D###
  #A#D#C#A#
  #########
  11 12 13 14
  15 16 17 18
         */
        amphipods.put(11, 'B');
        amphipods.put(15, 'A');
        amphipods.put(12, 'C');
        amphipods.put(16, 'D');
        amphipods.put(13, 'B');
        amphipods.put(17, 'C');
        amphipods.put(14, 'D');
        amphipods.put(18, 'A');
        return new Board(amphipods, 0);
    }

    private static Board realBoard() {
        final Map<Integer, Character> amphipods = new HashMap<>();
        // test input
        /*
 01234567890
#############
#...........#
###B#B#D#A###
  #C#A#D#C#
  #########
  11 12 13 14
  15 16 17 18
         */
        amphipods.put(11, 'B');
        amphipods.put(15, 'C');
        amphipods.put(12, 'B');
        amphipods.put(16, 'A');
        amphipods.put(13, 'D');
        amphipods.put(17, 'D');
        amphipods.put(14, 'A');
        amphipods.put(18, 'C');
        return new Board(amphipods, 0);
    }

    static class Board {
        final int totalEnergy;
        static final Map<Character, Integer> stepEnergy = new HashMap<>();

        static {
            stepEnergy.put('A', 1);
            stepEnergy.put('B', 10);
            stepEnergy.put('C', 100);
            stepEnergy.put('D', 1000);
        }

        static final Set<Move> validMoves = new HashSet<>();

        static {
            validMoves.add(new Move(11, 0, 3, "ABCD", Arrays.asList(1)));
            validMoves.add(new Move(11, 1, 2, "ABCD", Collections.emptyList()));
            validMoves.add(new Move(11, 3, 2, "ABCD", Collections.emptyList()));
            validMoves.add(new Move(11, 5, 4, "ABCD", Arrays.asList(3)));
            validMoves.add(new Move(11, 7, 6, "ABCD", Arrays.asList(3, 5)));
            validMoves.add(new Move(11, 9, 8, "ABCD", Arrays.asList(3, 5, 7)));
            validMoves.add(new Move(11, 10, 9, "ABCD", Arrays.asList(3, 5, 7, 8)));

            validMoves.add(new Move(15, 0, 4, "BCD", Arrays.asList(1, 11)));
            validMoves.add(new Move(15, 1, 3, "BCD", Arrays.asList(11)));
            validMoves.add(new Move(15, 3, 3, "BCD", Arrays.asList(11)));
            validMoves.add(new Move(15, 5, 5, "BCD", Arrays.asList(3, 11)));
            validMoves.add(new Move(15, 7, 7, "BCD", Arrays.asList(3, 5, 11)));
            validMoves.add(new Move(15, 9, 9, "BCD", Arrays.asList(3, 5, 7, 11)));
            validMoves.add(new Move(15, 10, 10, "BCD", Arrays.asList(3, 5, 7, 8, 11)));

            validMoves.add(new Move(0, 11, 3, "A", Arrays.asList(1)));
            validMoves.add(new Move(1, 11, 2, "A", Collections.emptyList()));
            validMoves.add(new Move(3, 11, 2, "A", Collections.emptyList()));
            validMoves.add(new Move(5, 11, 4, "A", Arrays.asList(3)));
            validMoves.add(new Move(7, 11, 6, "A", Arrays.asList(3, 5)));
            validMoves.add(new Move(9, 11, 8, "A", Arrays.asList(3, 5, 7)));
            validMoves.add(new Move(10, 11, 9, "A", Arrays.asList(3, 5, 7, 8)));

            validMoves.add(new Move(0, 15, 4, "A", Arrays.asList(1, 11)));
            validMoves.add(new Move(1, 15, 3, "A", Arrays.asList(11)));
            validMoves.add(new Move(3, 15, 3, "A", Arrays.asList(11)));
            validMoves.add(new Move(5, 15, 5, "A", Arrays.asList(3, 11)));
            validMoves.add(new Move(7, 15, 7, "A", Arrays.asList(3, 5, 11)));
            validMoves.add(new Move(9, 15, 9, "A", Arrays.asList(3, 5, 7, 11)));
            validMoves.add(new Move(10, 15, 10, "A", Arrays.asList(3, 5, 7, 8, 11)));


            validMoves.add(new Move(12, 0, 5, "ABCD", Arrays.asList(1, 3)));
            validMoves.add(new Move(12, 1, 4, "ABCD", Arrays.asList(3)));
            validMoves.add(new Move(12, 3, 2, "ABCD", Collections.emptyList()));
            validMoves.add(new Move(12, 5, 2, "ABCD", Collections.emptyList()));
            validMoves.add(new Move(12, 7, 4, "ABCD", Arrays.asList(5)));
            validMoves.add(new Move(12, 9, 6, "ABCD", Arrays.asList(5, 7)));
            validMoves.add(new Move(12, 10, 7, "ABCD", Arrays.asList(5, 7, 8)));

            validMoves.add(new Move(16, 0, 6, "ACD", Arrays.asList(12, 1, 3)));
            validMoves.add(new Move(16, 1, 5, "ACD", Arrays.asList(12, 3)));
            validMoves.add(new Move(16, 3, 3, "ACD", Arrays.asList(12)));
            validMoves.add(new Move(16, 5, 3, "ACD", Arrays.asList(12)));
            validMoves.add(new Move(16, 7, 5, "ACD", Arrays.asList(12, 5)));
            validMoves.add(new Move(16, 9, 7, "ACD", Arrays.asList(12, 5, 7)));
            validMoves.add(new Move(16, 10, 8, "ACD", Arrays.asList(12, 5, 7, 8)));

            validMoves.add(new Move(0, 12, 5, "B", Arrays.asList(1, 3)));
            validMoves.add(new Move(1, 12, 4, "B", Arrays.asList(3)));
            validMoves.add(new Move(3, 12, 2, "B", Collections.emptyList()));
            validMoves.add(new Move(5, 12, 2, "B", Collections.emptyList()));
            validMoves.add(new Move(7, 12, 4, "B", Arrays.asList(5)));
            validMoves.add(new Move(9, 12, 6, "B", Arrays.asList(5, 7)));
            validMoves.add(new Move(10, 12, 7, "B", Arrays.asList(5, 7, 8)));

            validMoves.add(new Move(0, 16, 6, "B", Arrays.asList(12, 1, 3)));
            validMoves.add(new Move(1, 16, 5, "B", Arrays.asList(12, 3)));
            validMoves.add(new Move(3, 16, 3, "B", Arrays.asList(12)));
            validMoves.add(new Move(5, 16, 3, "B", Arrays.asList(12)));
            validMoves.add(new Move(7, 16, 5, "B", Arrays.asList(12, 5)));
            validMoves.add(new Move(9, 16, 7, "B", Arrays.asList(12, 5, 7)));
            validMoves.add(new Move(10, 16, 8, "B", Arrays.asList(12, 5, 7, 8)));


            validMoves.add(new Move(13, 0, 7, "ABCD", Arrays.asList(1, 3, 5)));
            validMoves.add(new Move(13, 1, 6, "ABCD", Arrays.asList(3, 5)));
            validMoves.add(new Move(13, 3, 4, "ABCD", Arrays.asList(5)));
            validMoves.add(new Move(13, 5, 2, "ABCD", Collections.emptyList()));
            validMoves.add(new Move(13, 7, 2, "ABCD", Collections.emptyList()));
            validMoves.add(new Move(13, 9, 4, "ABCD", Arrays.asList(7)));
            validMoves.add(new Move(13, 10, 5, "ABCD", Arrays.asList(7, 8)));

            validMoves.add(new Move(17, 0, 8, "ABD", Arrays.asList(13, 1, 3, 5)));
            validMoves.add(new Move(17, 1, 7, "ABD", Arrays.asList(13, 3, 5)));
            validMoves.add(new Move(17, 3, 5, "ABD", Arrays.asList(13, 5)));
            validMoves.add(new Move(17, 5, 4, "ABD", Arrays.asList(13)));
            validMoves.add(new Move(17, 7, 4, "ABD", Arrays.asList(13)));
            validMoves.add(new Move(17, 9, 5, "ABD", Arrays.asList(13, 7)));
            validMoves.add(new Move(17, 10, 6, "ABD", Arrays.asList(13, 7, 8)));

            validMoves.add(new Move(0, 13, 7, "C", Arrays.asList(1, 3, 5)));
            validMoves.add(new Move(1, 13, 6, "C", Arrays.asList(3, 5)));
            validMoves.add(new Move(3, 13, 4, "C", Arrays.asList(5)));
            validMoves.add(new Move(5, 13, 2, "C", Collections.emptyList()));
            validMoves.add(new Move(7, 13, 2, "C", Collections.emptyList()));
            validMoves.add(new Move(9, 13, 4, "C", Arrays.asList(7)));
            validMoves.add(new Move(10, 13, 5, "C", Arrays.asList(7, 8)));

            validMoves.add(new Move(0, 17, 8, "C", Arrays.asList(13, 1, 3, 5)));
            validMoves.add(new Move(1, 17, 7, "C", Arrays.asList(13, 3, 5)));
            validMoves.add(new Move(3, 17, 5, "C", Arrays.asList(13, 5)));
            validMoves.add(new Move(5, 17, 4, "C", Arrays.asList(13)));
            validMoves.add(new Move(7, 17, 4, "C", Arrays.asList(13)));
            validMoves.add(new Move(9, 17, 5, "C", Arrays.asList(13, 7)));
            validMoves.add(new Move(10, 17, 6, "C", Arrays.asList(13, 7, 8)));


            validMoves.add(new Move(14, 0, 9, "ABCD", Arrays.asList(1, 3, 5, 7)));
            validMoves.add(new Move(14, 1, 8, "ABCD", Arrays.asList(3, 5, 7)));
            validMoves.add(new Move(14, 3, 6, "ABCD", Arrays.asList(5, 7)));
            validMoves.add(new Move(14, 5, 4, "ABCD", Arrays.asList(7)));
            validMoves.add(new Move(14, 7, 2, "ABCD", Collections.emptyList()));
            validMoves.add(new Move(14, 9, 2, "ABCD", Collections.emptyList()));
            validMoves.add(new Move(14, 10, 3, "ABCD", Arrays.asList(8)));

            validMoves.add(new Move(18, 0, 10, "ABC", Arrays.asList(14, 1, 3, 5, 7)));
            validMoves.add(new Move(18, 1, 9, "ABC", Arrays.asList(14, 3, 5, 7)));
            validMoves.add(new Move(18, 3, 7, "ABC", Arrays.asList(14, 5, 7)));
            validMoves.add(new Move(18, 5, 5, "ABC", Arrays.asList(14, 7)));
            validMoves.add(new Move(18, 7, 3, "ABC", Arrays.asList(14)));
            validMoves.add(new Move(18, 9, 3, "ABC", Arrays.asList(14)));
            validMoves.add(new Move(18, 10, 4, "ABC", Arrays.asList(14, 8)));

            validMoves.add(new Move(0, 14, 9, "D", Arrays.asList(1, 3, 5, 7)));
            validMoves.add(new Move(1, 14, 8, "D", Arrays.asList(3, 5, 7)));
            validMoves.add(new Move(3, 14, 6, "D", Arrays.asList(5, 7)));
            validMoves.add(new Move(5, 14, 4, "D", Arrays.asList(7)));
            validMoves.add(new Move(7, 14, 2, "D", Collections.emptyList()));
            validMoves.add(new Move(9, 14, 2, "D", Collections.emptyList()));
            validMoves.add(new Move(10, 14, 3, "D", Arrays.asList(8)));

            validMoves.add(new Move(0, 18, 10, "D", Arrays.asList(14, 1, 3, 5, 7)));
            validMoves.add(new Move(1, 18, 9, "D", Arrays.asList(14, 3, 5, 7)));
            validMoves.add(new Move(3, 18, 7, "D", Arrays.asList(14, 5, 7)));
            validMoves.add(new Move(5, 18, 5, "D", Arrays.asList(14, 7)));
            validMoves.add(new Move(7, 18, 3, "D", Arrays.asList(14)));
            validMoves.add(new Move(9, 18, 3, "D", Arrays.asList(14)));
            validMoves.add(new Move(10, 18, 4, "D", Arrays.asList(14, 8)));
        }

        static final Map<Integer, Character> targetState = new HashMap<>();

        static {
            targetState.put(11, 'A');
            targetState.put(15, 'A');
            targetState.put(12, 'B');
            targetState.put(16, 'B');
            targetState.put(13, 'C');
            targetState.put(17, 'C');
            targetState.put(14, 'D');
            targetState.put(18, 'D');
        }


        final Map<Integer, Character> amphipods;

        public Board(Map<Integer, Character> amphipods, int totalEnergy) {
            this.amphipods = amphipods;
            this.totalEnergy = totalEnergy;
        }

        public Board applyMove(Move move) {
            Map<Integer, Character> newPositions = new HashMap<>(this.amphipods);
            Character moved = newPositions.remove(move.from);
            newPositions.put(move.to, moved);
            return new Board(newPositions, totalEnergy + stepEnergy.get(moved) * move.cost);
        }

        public boolean solved() {
            return amphipods.equals(targetState);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Board board = (Board) o;
            return Objects.equals(amphipods, board.amphipods);
        }

        public boolean sameAmphipods(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Board board = (Board) o;
            return Objects.equals(amphipods, board.amphipods);
        }

        @Override
        public int hashCode() {
            return Objects.hash(totalEnergy, amphipods);
        }

        public Set<Move> getPossibleMoves() {
            Set<Move> result = new HashSet<>();

            for (Map.Entry<Integer, Character> amphipod : amphipods.entrySet()) {
                result.addAll(validMoves.stream()
                        .filter(m -> m.from == amphipod.getKey())
                        .filter(m -> allUnoccupied(m.viaPoints))
                        .filter(m -> m.amphipods.indexOf(amphipod.getValue()) >= 0)
                        .filter(m -> !amphipods.containsKey(m.to))
                        .collect(Collectors.toSet()));
            }
            return result;
        }

        private boolean allUnoccupied(List<Integer> viaPoints) {
            return viaPoints.stream().noneMatch(amphipods::containsKey);
        }

        @Override
        public String toString() {
            return "Board{" +
                    "totalEnergy=" + totalEnergy +
                    ", amphipods=" + amphipods +
                    '}';
        }
    }

    static class Move {
        final int from, to, cost;
        final String amphipods;
        final List<Integer> viaPoints;

        public Move(int from, int to, int cost, String amphipods, List<Integer> viaPoints) {
            this.from = from;
            this.to = to;
            this.cost = cost;
            this.amphipods = amphipods;
            this.viaPoints = viaPoints;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Move move = (Move) o;
            return from == move.from &&
                    to == move.to &&
                    cost == move.cost &&
                    Objects.equals(amphipods, move.amphipods) &&
                    Objects.equals(viaPoints, move.viaPoints);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to, cost, amphipods, viaPoints);
        }

        @Override
        public String toString() {
            return "Move{" +
                    "from=" + from +
                    ", to=" + to +
                    ", cost=" + cost +
                    ", amphipods='" + amphipods + '\'' +
                    ", viaPoints=" + viaPoints +
                    '}';
        }


    }
}
