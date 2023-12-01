package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.ToString;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day20 {

    static final BigInteger KEY = new BigInteger("811589153");

    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day20.txt"))) {
            long start = System.currentTimeMillis();
            List<Integer> inputs = stream.filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toList());

            //firstAttempt(inputs);

            BigInteger result = solve(inputs);

            System.out.println("Result: " + result);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BigInteger solve(List<Integer> inputs) {
        DoublyLinkedList list = new DoublyLinkedList(inputs);

        list.print();

        // fails if there are duplicate elements (works for test input)
//        for (Integer input : inputs) {
//            list.moveValue(input);
//            //list.print();
//        }

        // 14459 is wrong; works for test input though
        // 3660 is too low
        // correct: 7278

        for (int iter = 0; iter < 10; iter ++) {
            for (int i = 0; i < inputs.size(); i++) {
                DoublyLinkedElement entry = list.entryByOriginalIndex(i);
                list.moveElement(entry);
                //list.print();
            }
        }

        DoublyLinkedElement zero = list.find(BigInteger.ZERO);

        DoublyLinkedElement thousand = zero;
        for (int i = 0; i < 1000; i++) {
            thousand = thousand.next;
        }

        DoublyLinkedElement twoThousand = thousand;
        for (int i = 0; i < 1000; i++) {
            twoThousand = twoThousand.next;
        }

        DoublyLinkedElement threeThousand = twoThousand;
        for (int i = 0; i < 1000; i++) {
            threeThousand = threeThousand.next;
        }

        System.out.println(thousand);
        System.out.println(twoThousand);
        System.out.println(threeThousand);

        return thousand.value.add(twoThousand.value).add(threeThousand.value);
        //return 0;
    }

    private static void firstAttempt(List<Integer> inputs) {
        List<Integer> working = new ArrayList<>(inputs);
        System.out.println("INITIAL: " + working);

        for (Integer input : inputs) {
            int currentPosition = working.indexOf(input);
            int newPosition = (currentPosition + input) % inputs.size();
            if (newPosition<=0) newPosition += inputs.size()-1;
            working.remove(input);
            //if (newPosition > currentPosition) newPosition--; // correct for removal
            working.add(newPosition, input);
            System.out.printf("%7d: %s\n", input, working);
        }
    }

    static class DoublyLinkedList {
        DoublyLinkedElement first;
        final Map<Integer, DoublyLinkedElement> originalEntriesByIndex = new HashMap<>();

        int size() {
            return originalEntriesByIndex.size();
        }

        DoublyLinkedList(List<Integer> items) {
            DoublyLinkedElement prev = null;
            DoublyLinkedElement curr = null;
            int idx = 0;
            for (Integer item : items) {
                curr = DoublyLinkedElement.builder().value(BigInteger.valueOf(item).multiply(KEY)).build();
                originalEntriesByIndex.put(idx++, curr);
                if (first == null) first = curr;
                curr.prev = prev;
                if (prev != null) {
                    prev.next = curr;
                }
                prev = curr;
            }
            first.prev = curr;
            curr.next = first;
        }

        DoublyLinkedElement entryByOriginalIndex(int index) {
            return originalEntriesByIndex.get(index);
        }



        void print() {
            DoublyLinkedElement curr = first;
            do {
                System.out.print(curr.value + ", ");
                curr = curr.next;
            } while (curr != first);
            System.out.println();
        }

        void moveValue(BigInteger input) {
            DoublyLinkedElement element = find(input);
            moveElement(element);
        }

        void moveElement(DoublyLinkedElement element) {
            BigInteger input = element.value;
            BigInteger size = BigInteger.valueOf(size() - 1);

            int moveBy = 0;

            if (input.compareTo(BigInteger.ZERO) < 0) moveBy = -(input.abs().mod(size).intValue());
            if (input.compareTo(BigInteger.ZERO) == 0) moveBy = 0;
            if (input.compareTo(BigInteger.ZERO) > 0) moveBy = input.mod(size).intValue();

            if (moveBy > 0) {
                DoublyLinkedElement target  = element.next;
                element.remove();
                for (int i = 1; i < moveBy; i++) {
                    target = target.next;
                }
                element.insertAfter(target);
            }
            if (moveBy < 0) {
                DoublyLinkedElement target  = element.prev;
                element.remove();
                for (int i = -1; i > moveBy; i--) {
                    target = target.prev;
                }
                element.insertBefore(target);
            }
        }

        DoublyLinkedElement find(BigInteger value) {
            DoublyLinkedElement curr = first;
            do {
                if (curr.value.equals(value)) return curr;
                curr = curr.next;
            } while (curr != first);
            throw new RuntimeException("Value not found " + value);
        }

    }

    @ToString(exclude = {"prev","next"})
    @Builder
    static class DoublyLinkedElement {
        BigInteger value;
        DoublyLinkedElement prev;
        DoublyLinkedElement next;

        void remove() {
            prev.next = next;
            next.prev = prev;
            prev = null;
            next = null;
        }

        void insertAfter(DoublyLinkedElement target) {
            this.next = target.next;
            this.prev = target;
            target.next = this;
            this.next.prev = this;
        }

        void insertBefore(DoublyLinkedElement target) {
            insertAfter(target.prev);
        }

    }

}
