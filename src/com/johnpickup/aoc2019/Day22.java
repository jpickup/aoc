package com.johnpickup.aoc2019;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day22 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
                , prefix + "-test3.txt"
                , prefix + "-test4.txt"
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
//                Deck test = new Deck(10);
//                test.init();
//                DealNew dealNew = new DealNew();
//                System.out.println("New deal: " + dealNew.apply(test));
//
//                test.init();
//                Cut cut = new Cut(3);
//                System.out.println("Cut 3: " + cut.apply(test));
//
//                test.init();
//                Cut cutNeg = new Cut(-4);
//                System.out.println("Cut -4: " + cutNeg.apply(test));
//
//                test.init();
//                DealWithIncrement dealWithIncrement = new DealWithIncrement(3);
//                System.out.println("Deal with inc 3: " + dealWithIncrement.apply(test));

                // the real deal
                Deck deck = new Deck(isTest?10:10007);
                deck.init();
                Shuffle shuffle = new Shuffle(lines);
                Deck shuffled = shuffle.shuffleDeck(deck);

                System.out.println(shuffled);

                long part1 = isTest ? 0L : shuffled.cards.indexOf(2019);
                System.out.println("Part 1: " + part1);
                // 8562 too high
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }
    static class Deck {
        final int size;
        final List<Integer> cards;
        Deck(int size) {
           this.size = size;
           cards = new ArrayList<>();
        }

        void init() {
            cards.clear();
            for (int i = 0; i < size; i++) {
                cards.add(i);
            }
        }

        boolean empty() {
            return cards.isEmpty();
        }

        int takeTop() {
            return cards.remove(0);
        }

        List<Integer> takeTopN(int n) {
            List<Integer> result = new ArrayList<>(cards.subList(0, n));
            for (int i = 0; i < n; i++) cards.remove(0);
            return result;
        }

        void addToTop(int card) {
            cards.add(0, card);
        }

        void addToTop(List<Integer> cards) {
            this.cards.addAll(0, cards);
        }

        @Override
        public String toString() {
            return cards.toString();
        }

        public List<Integer> remaining() {
            return cards;
        }

        public int getCard(int index) {
            return cards.get(index);
        }
    }

    @ToString
    static class Shuffle {
        final List<Deal> deals;
        Shuffle(List<String> lines) {
            deals = lines.stream().map(DealFactory::parse).collect(Collectors.toList());
        }

        Deck shuffleDeck(Deck deck) {
            Deck result = deck;
            for (Deal deal : deals) {
                result = deal.apply(result);
            }
            return result;
        }
    }

    interface Deal {
        Deck apply(Deck deck);
    }

    @ToString
    static class DealNew implements Deal {
        @Override
        public Deck apply(Deck deck) {
            Deck result = new Deck(deck.size);

            while (!deck.empty()) {
                result.addToTop(deck.takeTop());
            }

            return result;
        }
    }

    @RequiredArgsConstructor
    @ToString
    static class DealWithIncrement implements Deal {
        final int increment;

        @Override
        public Deck apply(Deck deck) {
            Map<Integer, Integer> cards = new TreeMap<>();
            for (int i = 0; i < deck.size; i++) {
                int card = deck.getCard(i);
                cards.put((i * increment) % deck.size, card);
            }
            Deck result = new Deck(deck.size);
            result.addToTop(new ArrayList<>(cards.values()));
            return result;
        }
    }


    @RequiredArgsConstructor
    @ToString
    static class Cut implements Deal {
        final int cut;

        @Override
        public Deck apply(Deck deck) {
            Deck result = new Deck(deck.size);
            int positiveCut = cut < 0 ? deck.size + cut : cut;
            result.addToTop(deck.takeTopN(positiveCut));
            result.addToTop(deck.remaining());
            return result;
        }
    }

    static class DealFactory {
        static Deal parse(String line) {
            if (line.equals("deal into new stack")) return new DealNew();
            else if (line.startsWith("deal with increment")) return new DealWithIncrement(Integer.parseInt(line.substring("deal with increment ".length()).trim()));
            else if (line.startsWith("cut")) return new Cut(Integer.parseInt(line.substring("cut ".length()).trim()));
            else throw new RuntimeException("Failed to parse " + line);
        }
    }
}
