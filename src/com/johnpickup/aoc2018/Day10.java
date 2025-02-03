package com.johnpickup.aoc2018;

import com.johnpickup.util.Coord;
import com.johnpickup.util.SparseGrid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day10 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Star> stars = stream
                        .filter(s -> !s.isEmpty())
                        .map(Star::new)
                        .collect(Collectors.toList());

                Sky sky = new Sky(stars);

                int time =0;
                Sky prevSky;
                do {
                    prevSky = sky;
                    sky = sky.next();
                    time++;

                }
                while (sky.bounds().distanceFrom(Coord.ORIGIN) < prevSky.bounds().distanceFrom(Coord.ORIGIN));

                System.out.println("Part 1: ");
                System.out.println(prevSky);
                System.out.println("Part 2: " + (time-1));

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Star {
        final Coord position;
        final Coord velocity;

        static final Pattern pattern = Pattern.compile("position=< *(-?[0-9]+), *(-?[0-9]+)> velocity=< *(-?[0-9]+), *(-?[0-9]+)>");

        Star(String line) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) throw new RuntimeException("Unrecognised input " + line);
            position = new Coord(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            velocity = new Coord(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
        }

        Star next() {
            return new Star(position.moveBy(velocity.getX(), velocity.getY()), velocity);
        }
    }

    @RequiredArgsConstructor
    static class Sky {
        final List<Star> stars;

        Sky next() {
            return new Sky(stars.stream().map(Star::next).collect(Collectors.toList()));
        }

        @Override
        public String toString() {
            SparseGrid<Character> grid = new SparseGrid<>();
            stars.forEach(s -> grid.setCell(s.position, '*'));
            return grid.toString();
        }

        public Coord bounds() {
            int minY = stars.stream().map(s -> s.position.getY()).min(Integer::compareTo)
                    .orElseThrow(() -> new RuntimeException("No stars"));
            int minX = stars.stream().map(s -> s.position.getX()).min(Integer::compareTo)
                    .orElseThrow(() -> new RuntimeException("No stars"));
            int maxY = stars.stream().map(s -> s.position.getY()).max(Integer::compareTo)
                    .orElseThrow(() -> new RuntimeException("No stars"));
            int maxX = stars.stream().map(s -> s.position.getX()).max(Integer::compareTo)
                    .orElseThrow(() -> new RuntimeException("No stars"));
            return new Coord(maxX - minX, maxY - minY);
        }

        // Only worked for the test input
        public boolean isMessage(int lineSize) {
            return hasStarsInAVerticalLine(lineSize, stars) || hasStarsInAHorizontalLine(lineSize, stars) ;
        }

        private boolean hasStarsInAVerticalLine(int number, List<Star> stars) {
            List<Integer> xs = stars.stream().map(s -> s.position.getX()).collect(Collectors.toList());
            for (Integer x : xs) {
                List<Integer> inLine = stars.stream().filter(s -> s.position.getX() == x).map(s -> s.position.getY()).collect(Collectors.toList());
                if (inLine.size() >= number && areContinuous(inLine)) return true;
            }
            return false;
        }

        private boolean hasStarsInAHorizontalLine(int number, List<Star> stars) {
            List<Integer> ys = stars.stream().map(s -> s.position.getY()).collect(Collectors.toList());
            for (Integer y : ys) {
                List<Integer> inLine = stars.stream().filter(s -> s.position.getY() == y).map(s -> s.position.getX()).collect(Collectors.toList());
                if (inLine.size() >= number && areContinuous(inLine)) return true;
            }
            return false;
        }

        private boolean areContinuous(List<Integer> line) {
            line.sort(Integer::compareTo);
            boolean result = true;
            for (int i = 1; i < line.size(); i++)
                result &= (line.get(i-1)+1 == line.get(i));
            return result;
        }
    }

}
