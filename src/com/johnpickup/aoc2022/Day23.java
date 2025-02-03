package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day23 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day23-test.txt"))) {
            long start = System.currentTimeMillis();
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Grove grove = new Grove();

            for (int y = 0; y < lines.size(); y++) {
                for (int x = 0; x < lines.get(y).length(); x++) {
                    if (lines.get(y).charAt(x) == '#') {
                        grove.add(Elf.builder().x(x).y(y).build());
                    }
                }
            }
            System.out.println("== Initial State ==");
            grove.display();
            System.out.println();

            Direction[] directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

            int round = 0;
            boolean somethingMoved = true;
            while (somethingMoved) {
                round ++;
                //System.out.println("== End of Round " + round + " ==");
                //System.out.println(Arrays.stream(directions).map(Enum::toString).collect(Collectors.joining(",")));

                List<Elf> proposals = grove.proposals(directions);
                List<Elf> uniqueProposals = removeDuplicates(proposals);

                somethingMoved = uniqueProposals.stream().anyMatch(p -> !p.equals(p.priorState));

                for (Elf uniqueProposal : uniqueProposals) {
                    grove.remove(uniqueProposal.priorState);
                    grove.add(uniqueProposal);
                }
                //grove.display();
                //System.out.println();

                // rotate directions
                Direction temp = directions[0];
                directions[0] = directions[1];
                directions[1] = directions[2];
                directions[2] = directions[3];
                directions[3] = temp;
            }

            System.out.println("Result: " + grove.score());
            System.out.println("# rounds: " + round);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Elf> removeDuplicates(List<Elf> proposals) {
        List<Elf> result = new ArrayList<>();
        for (Elf proposal : proposals) {
            boolean unique = proposals.stream().filter(p -> p.equals(proposal)).count() == 1;
            if (unique) result.add(proposal);
        }
        return result;
    }

    @Builder
    @ToString(exclude = {"priorState"})
    @EqualsAndHashCode(exclude = {"priorState"})
    static class Elf {
        final int x;
        final int y;
        final Elf priorState;

        public Elf propose(Direction[] directions, Grove grove) {
            if (!allEmpty(grove)) {
                for (Direction direction : directions) {
                    if (isEmpty(direction, grove)) {
                        return Elf.builder().x(direction.applyX(x)).y(direction.applyY(y)).priorState(this).build();
                    }
                }
            }
            return Elf.builder().x(this.x).y(this.y).priorState(this).build();      // no change
        }

        private boolean isEmpty(Direction direction, Grove grove) {
            switch (direction) {
                case NORTH: return northEmpty(grove);
                case SOUTH: return southEmpty(grove);
                case EAST: return eastEmpty(grove);
                case WEST: return westEmpty(grove);
                default: throw new RuntimeException("Unknown direction "+ direction);
            }
        }

        private boolean northEmpty(Grove grove) {
            return !grove.hasElf(x - 1, y - 1)
                    && !grove.hasElf(x, y - 1)
                    && !grove.hasElf(x + 1, y - 1);
        }

        private boolean southEmpty(Grove grove) {
            return !grove.hasElf(x - 1, y + 1)
                    && !grove.hasElf(x, y + 1)
                    && !grove.hasElf(x + 1, y + 1);
        }

        private boolean eastEmpty(Grove grove) {
            return !grove.hasElf(x + 1, y - 1)
                    && !grove.hasElf(x + 1, y)
                    && !grove.hasElf(x + 1, y + 1);
        }

        private boolean westEmpty(Grove grove) {
            return !grove.hasElf(x - 1, y - 1)
                    && !grove.hasElf(x - 1, y)
                    && !grove.hasElf(x - 1, y + 1);
        }

        private boolean allEmpty(Grove grove) {
            return northEmpty(grove) && southEmpty(grove) && eastEmpty(grove) && westEmpty(grove);
        }
    }

    static class Grove {
        final List<Elf> elves = new ArrayList<>();

        void add(Elf elf) {
            elves.add(elf);
        }

        public void remove(Elf priorState) {
            elves.remove(priorState);
        }

        List<Elf> proposals(Direction[] directions) {
            return elves.stream().map(e -> e.propose(directions, this)).collect(Collectors.toList());
        }

        int minX() {
            return elves.stream().map(e -> e.x).min(Integer::compare).get();
        }

        int maxX() {
            return elves.stream().map(e -> e.x).max(Integer::compare).get();
        }

        int minY() {
            return elves.stream().map(e -> e.y).min(Integer::compare).get();
        }

        int maxY() {
            return elves.stream().map(e -> e.y).max(Integer::compare).get();
        }


        void display() {
            for (int y = minY() - 1; y <= maxY() + 1; y++) {
                for (int x = minX() - 1; x <= maxX() + 1; x++) {
                    System.out.print(hasElf(x,y) ? "#" : ".");
                }
                System.out.println();
            }

        }

        public boolean hasElf(int x, int y) {
            return elves.contains(Elf.builder().x(x).y(y).build());
        }

        public int score() {
            int result = 0;
            for (int y = minY(); y <= maxY(); y++) {
                for (int x = minX(); x <= maxX(); x++) {
                    if (!hasElf(x,y)) result++;
                }
            }
            return result;
        }
    }

    enum Direction {
        NORTH,
        SOUTH,
        WEST,
        EAST;

        public int applyX(int x) {
            switch (this) {
                case NORTH: return x;
                case SOUTH: return x;
                case EAST: return x+1;
                case WEST: return x-1;
                default: throw new RuntimeException("Unknown direction "+ this);
            }
        }

        public int applyY(int y) {
            switch (this) {
                case NORTH: return y-1;
                case SOUTH: return y+1;
                case EAST: return y;
                case WEST: return y;
                default: throw new RuntimeException("Unknown direction "+ this);
            }
        }

    }

}
