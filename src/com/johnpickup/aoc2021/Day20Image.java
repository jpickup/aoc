package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20Image {
    List<String> lines;

    public static void main(String[] args) {
        Day20Image day20 = new Day20Image("/Volumes/Users/john/Development/AdventOfCode/resources/Day20Input.txt");

        Day20Data data = day20.parseInput();
        data.printImage();

        day20.solvePart1(data);
        //day20.solvePart2();
    }

    private Day20Data parseInput() {
        return Day20Data.parse(lines);
    }

    private void test(Day20Data data) {
        int value = data.getValue(2, 2, false);
        System.out.println("Value: " + value);
        System.out.println("New bit: " + data.algo[value]);
    }

    private Day20Data solvePart1(Day20Data data) {

        boolean oob = false;
        for (int iteration = 0; iteration < 50; iteration++) {
            System.out.println("------ " + iteration);
            Day20Data newData = Day20Data.cloneData(data);
            for (int row = 0; row < newData.rows(); row++) {
                for (int col = 0; col < newData.cols(); col++) {
                    int value = data.getValue(row-2, col-2, oob);
                    boolean newPixel = data.algo[value];
                    newData.image[row][col] = newPixel;
                }
            }
            data = newData;
            //data.printImage();
            System.out.printf("(%d * %d) = %d\n", data.rows(), data.cols(), data.pixelLitCount());
            oob = data.algo[0] ^ oob;
        }
        return data;
    }


    Day20Image(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Day20Data {
        boolean[] algo;
        boolean[][] image;

        static Day20Data cloneData(Day20Data source) {
            boolean[] algoCopy = Arrays.copyOf(source.algo, source.algo.length);
            boolean[][] imageCopy = new boolean[source.rows()+4][source.cols()+4];
            for (int row=0; row < source.rows(); row++) {
                for (int col=0; col < source.cols(); col++) {
                    imageCopy[row+2][col+2] = source.image[row][col];
                }
            }
            return new Day20Data(algoCopy, imageCopy);
        }

        static Day20Data parse(List<String> input) {
            boolean[] algo = parseAlgo(input.get(0));

            List<String> imageLines = new ArrayList<>(input);
            imageLines.remove(0);
            imageLines.remove(0);

            boolean[][] image = parseImage(imageLines);

            return new Day20Data(algo, image);
        }

        static boolean[] parseAlgo(String data) {
            boolean[] result = new boolean[data.length()];

            for (int i = 0; i < data.length(); i++) {
                result[i] = data.charAt(i)=='#';
            }
            return  result;
        }

        static boolean[][] parseImage(List<String> lines) {
            boolean[][] result = new boolean[lines.size()][lines.get(0).length()];

            for (int row=0; row < lines.size(); row++) {
                for (int col=0; col < lines.get(row).length(); col++) {
                    result[row][col] = lines.get(row).charAt(col)=='#';
                }
            }

            return result;
        }

        public Day20Data(boolean[] algo, boolean[][] image) {
            this.algo = algo;
            this.image = image;
        }

        public void printImage() {

            for (int row=0; row < rows(); row++) {
                for (int col=0; col < cols(); col++) {
                    System.out.print(image[row][col]?'*':' ');
                }
                System.out.println();
            }
            System.out.printf("(%d * %d) = %d\n", rows(), cols(), pixelLitCount());
        }

        public int rows() {
            return image.length;
        }

        public int cols() {
            return image[0].length;
        }

        public int getValue(int row, int col, boolean outOfBounds) {
            return ((getBitValue(row-1, col-1, outOfBounds) << 8) +
                    (getBitValue(row-1, col, outOfBounds) << 7) +
                    (getBitValue(row-1, col+1, outOfBounds) << 6) +
                    (getBitValue(row, col-1, outOfBounds) << 5) +
                    (getBitValue(row, col, outOfBounds) << 4) +
                    (getBitValue(row, col+1, outOfBounds) << 3) +
                    (getBitValue(row+1, col-1, outOfBounds) << 2) +
                    (getBitValue(row+1, col, outOfBounds) << 1) +
                    (getBitValue(row+1, col+1, outOfBounds)));
        }

        public boolean getBit(int row, int col, boolean outOfBounds) {
            if (row < 0 || row >= rows()) return outOfBounds;
            if (col < 0 || col >= cols()) return outOfBounds;
            return image[row][col];
        }

        public int getBitValue(int row, int col, boolean outOfBounds) {
            return getBit(row,col, outOfBounds)?1:0;
        }

        public int pixelLitCount() {
            int result = 0;
            for (int row=0; row < rows(); row++) {
                for (int col=0; col < cols(); col++) {
                    if (image[row][col]) result ++;
                }
            }
            return result;
        }
    }
}

