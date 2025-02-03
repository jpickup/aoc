package com.johnpickup.aoc2020;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day25 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Key> keys = stream
                        .filter(s -> !s.isEmpty())
                        .map(Key::new)
                        .collect(Collectors.toList());

                System.out.println(keys);

                Key key1 = keys.get(0);
                Key key2 = keys.get(1);
                long encryptionKey1 = key1.encrypt(key2.publicKey);
                long encryptionKey2 = key2.encrypt(key1.publicKey);
                System.out.println("Key1: " + encryptionKey1);
                System.out.println("Key2: " + encryptionKey2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @ToString
    static class Key {
        final long publicKey;
        final static long divisor = 20201227L;
        int loopSize;
        Key(String line) {
            publicKey = Integer.parseInt(line);
            calcLoopSize();
        }

        long encrypt(long subject) {
            long result = 1L;
            for (int i = 0; i < loopSize; i++) {
                result = transform(result, subject);
            }
            return result;
        }

        void calcLoopSize() {
            boolean found = false;
            long value = 1L;

            int loop = 0;
            while (!found) {
                loop++;
                value = transform(value, 7L);
                found = value == publicKey;
            }
            loopSize = loop;
        }

        long transform(long value, long subject) {
            return (value * subject) % divisor;
        }
    }
}
