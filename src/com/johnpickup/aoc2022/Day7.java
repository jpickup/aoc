package com.johnpickup.aoc2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day7-test.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());

            Directory rootNode = new Directory();
            rootNode.name = "/";
            rootNode.parent = null;
            Directory currentNode = rootNode;

            Stack<String> currentDir = new Stack<>();

            for (String line : lines) {
                if (line.startsWith("$")) {
                    //command
                    String command = line.substring(2,4);
                    String argument = line.substring(4).trim();
                    switch (command) {
                        case "cd":
                            System.out.println("cd " + argument);
                            switch (argument) {
                                case "/":
                                    currentDir.clear();
                                    currentNode = rootNode;
                                    break;
                                case "..":
                                    currentDir.pop();
                                    currentNode = currentNode.parent;
                                    break;
                                default:
                                    currentDir.push(argument);
                                    currentNode = (Directory) currentNode.entries.get(argument);
                            }
                            break;
                        case "ls":
                            System.out.println("ls " + argument);
                            break;
                        default:
                            System.out.println("*** UNKNNOWN " + command);
                    }
                }
                else {
                    // entry
                    String[] parts = line.split(" ");

                    if (parts[0].equals("dir")) {
                        String dir = parts[1];
                        System.out.println("Dir: " + dir);
                        Directory directory = new Directory();
                        directory.name = dir;
                        directory.parent = currentNode;
                        currentNode.entries.put(dir, directory);
                    }
                    else {
                        long size = Long.parseLong(parts[0]);
                        String fileName = parts[1];
                        System.out.println("File: " + fileName + " " + size);
                        File file = new File();
                        file.name = fileName;
                        file.size = size;
                        file.parent = currentNode;
                        currentNode.entries.put(fileName, file);
                    }
                }

            }

            display(rootNode, 0);
            System.out.println(ans);
            long totalSize = rootNode.size();
            System.out.println("Total size: "+totalSize);

            long free = 70000000L - totalSize;
            long target = 30000000L - free;
            System.out.println("Target: "+target);

            List<Long> candidates = sizes.values().stream().filter(s -> s >= target).sorted().collect(Collectors.toList());
            System.out.println(candidates);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static long ans = 0;
    static Map<Directory, Long> sizes = new HashMap<>();
    private static void display(Node node, int depth) {
        for (int i = 0; i < depth; i++) System.out.print("  ");
        System.out.print("- ");
        if (node instanceof File) {
            File file = (File) node;
            System.out.println(file.name + " (file, size="+file.size+")");
        }
        if (node instanceof Directory) {
            Directory dir = (Directory) node;
            long dirSize = dir.size();
            sizes.put(dir, dirSize);
            System.out.println(dir.name + " (dir, size="+ dirSize +")");
            if (dirSize < 100000)
                ans += dirSize;
            for (Node value : dir.entries.values()) {
                display(value, depth+1);
            }
        }
    }

    static abstract class Node {
        String name;
        Directory parent = null;
        abstract long size();
    }

    static class Directory extends Node {
        @Override
        public String toString() {
            return parent == null ? "" : parent + "/" + name;
        }

        Map<String, Node> entries = new TreeMap<>();

        @Override
        long size() {
            long result = 0;
            for (Node value : entries.values()) {
                result = result + value.size();
            }
            return result;
        }
    }

    static class File extends Node {
        long size;

        @Override
        long size() {
            return size;
        }

    }
}