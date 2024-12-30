package com.johnpickup.aoc2020;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.InputUtils;
import com.johnpickup.util.Sets;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day20 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
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
                        .collect(Collectors.toList());

                List<List<String>> groups = InputUtils.splitIntoGroups(lines);
                List<Tile> tiles = groups.stream().map(Tile::new).collect(Collectors.toList());
                Image image = new Image(tiles);

                long part1 = image.part1();
                System.out.println("Part 1: " + part1);

                CharGrid seaMonster = new CharGrid(Files.lines(
                        Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day20/SeaMonster.txt"))
                        .collect(Collectors.toList()));

                long part2 = image.part2(seaMonster);
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Image {
        final List<Tile> tiles;
        final int imageSize;

        Image(List<Tile> tiles) {
            this.tiles = tiles;
            imageSize = (int)Math.sqrt(tiles.size());
        }

        Map<Tile, Set<Tile>> tileVariants;
        Set<TilePair> pairs;
        Set<TilePair> alignedPairs;
        Map<Tile, Set<Tile>> pairsByTile;
        Map<Tile, Set<Tile>> alignedWEPairsByTile;
        Map<Tile, Set<Tile>> alignedNSPairsByTile;
        Set<Tile> corners;
        Set<Tile> edges;
        Set<Tile> interior;


        public long part1() {
//            Map<Coord, Tile> arrangement = arrangeTiles();
//            if (arrangement == null) throw new RuntimeException("No arrangement found");
//            return arrangement.get(new Coord(0,0)).number
//                    * arrangement.get(new Coord(imageSize-1,0)).number
//                    * arrangement.get(new Coord(0,imageSize-1)).number
//                    * arrangement.get(new Coord(imageSize-1,imageSize-1)).number;

            getTileCategories();
            System.out.printf("%d corners, %d edges, %d interior%n", corners.size(), edges.size(), interior.size());
            return corners.stream().map(t -> t.number).reduce(1L, (a, b) -> a * b);
        }

        private void getTileCategories() {
            pairs = findPairs();
            pairsByTile = new HashMap<>();
            for (TilePair pair : pairs) {
                pairsByTile.putIfAbsent(pair.tile1, new HashSet<>());
                pairsByTile.putIfAbsent(pair.tile2, new HashSet<>());
                pairsByTile.get(pair.tile1).add(pair.tile2);
                pairsByTile.get(pair.tile2).add(pair.tile1);
            }
            //pairsByTile.entrySet().forEach(pbt -> System.out.println(pbt.getKey().number + " -> " + pbt.getValue().size()));
            corners = pairsByTile.entrySet().stream().filter(e -> e.getValue().size() == 2).map(Map.Entry::getKey).collect(Collectors.toSet());
            edges = pairsByTile.entrySet().stream().filter(e -> e.getValue().size() == 3).map(Map.Entry::getKey).collect(Collectors.toSet());
            interior = pairsByTile.entrySet().stream().filter(e -> e.getValue().size() == 4).map(Map.Entry::getKey).collect(Collectors.toSet());
        }

        public long part2(CharGrid seaMonster) {
            Map<Coord, Tile> arrangement = arrangeTiles2();
            if (arrangement == null) throw new RuntimeException("No arrangement found");
            int tileWidth = tiles.get(0).grid.getWidth();
            int tileHeight = tiles.get(0).grid.getHeight();
            int fullWidth = imageSize * (tileWidth - 2);
            int fullHeight= imageSize * (tileHeight - 2);
            CharGrid fullGrid = new CharGrid(fullWidth, fullHeight, new char[fullWidth][fullHeight]);
            for (int tileY=0; tileY < imageSize; tileY++) {
                for (int tileX=0; tileX < imageSize; tileX++) {
                    Tile tile = arrangement.get(new Coord(tileX, tileY));
                    for (int offX=1; offX < tileWidth-1; offX++) {
                        for (int offY=1; offY < tileHeight-1; offY++) {
                            fullGrid.setCell(
                                    new Coord(tileX * (tileWidth-2) + (offX-1), tileY * (tileHeight-2) + (offY-1)),
                                    tile.grid.getCell(new Coord(offX, offY)));
                        }
                    }
                }
            }
            long best = Long.MAX_VALUE;
            Set<CharGrid> fullGridVariants = fullGrid.generateVariants();
            for (CharGrid fullGridVariant : fullGridVariants) {
                Set<Coord> seaMonsterCoords = findSeaMonsters(fullGridVariant, seaMonster);
                Set<Coord> allChoppy = fullGridVariant.findAll('#');
                allChoppy.removeAll(seaMonsterCoords);
                if (allChoppy.size() < best) best = allChoppy.size();
            }
            return best;
        }

        private Set<Coord> findSeaMonsters(CharGrid sea, CharGrid seaMonster) {
            Set<Coord> result = new HashSet<>();
            for (int x = 0; x < sea.getWidth()-seaMonster.getWidth(); x++) {
                for (int y = 0; y < sea.getHeight()-seaMonster.getHeight(); y++) {
                    result.addAll(monsterMatches(sea, x, y, seaMonster));
                }
            }
            return result;
        }

        private Set<Coord> monsterMatches(CharGrid sea, int xOffset, int yOffset, CharGrid seaMonster) {
            boolean allMatch = true;
            Set<Coord> result = new HashSet<>();
            for (int x = 0 ; x < seaMonster.getWidth(); x++) {
                for (int y = 0 ; y < seaMonster.getHeight(); y++) {
                    if (seaMonster.getCell(new Coord(x,y)) == '#') {
                        Coord seaCoord = new Coord(x + xOffset, y + yOffset);
                        allMatch &= sea.getCell(seaCoord)=='#';
                        result.add(seaCoord);
                    }
                }
            }
            return allMatch ? result : Collections.emptySet();
        }


        private Map<Coord, Tile> arrangeTiles2() {
            Set<Tile> unplaced = new HashSet<>(tiles);
            // pick any corner
            Tile firstCorner = corners.stream().findFirst().orElseThrow(() -> new RuntimeException("No corner to choose"));

            Map<Coord, Tile> result = setupInitialCorner(firstCorner, unplaced);

            while (unplaced.size() > 0) {
                Coord firstUnfilled = findFirstUnfilled(result.keySet());
                Tile westTile = result.get(firstUnfilled.west());
                Tile northTile = result.get(firstUnfilled.north());
                Set<Tile> eastCandidates = findEastCandidates(westTile, unplaced);
                Set<Tile> southCandidates = findSouthCandidates(northTile, unplaced);
                Set<Tile> possible = Sets.intersection(eastCandidates, southCandidates);
                if (possible.size()>1) throw new RuntimeException("Expected a simple input!");
                if (possible.size()==0) throw new RuntimeException("Couldn't find any solution");
                Tile foundTile = possible.stream().findFirst().get();
                result.put(firstUnfilled, foundTile);
                unplaced = unplaced.stream().filter(u -> u.number != foundTile.number).collect(Collectors.toSet());
            }

            return result;
        }

        private Set<Tile> findEastCandidates(Tile westTile, Set<Tile> unplaced) {
            return findCandidates(westTile, unplaced, this::tilesAlignWE);
        }

        private Set<Tile> findSouthCandidates(Tile northTile, Set<Tile> unplaced) {
            return findCandidates(northTile, unplaced, this::tilesAlignNS);
        }

        private Set<Tile> findCandidates(Tile adjacentTile, Set<Tile> unplaced, BiFunction<Tile, Tile, Boolean> checker) {
            Set<Tile> result = new HashSet<>();
            for (Tile tile : unplaced) {
                for (Tile potentialTile : tileVariants.get(tile)) {
                    if (adjacentTile == null || checker.apply(adjacentTile, potentialTile)) {
                        result.add(potentialTile);
                    }
                }
            }
            return result;
        }

        private Map<Coord, Tile> setupInitialCorner(Tile firstCorner, Set<Tile> unplaced) {
            Set<Tile> originalUnplaced = unplaced;
            Map<Coord, Tile> result = new HashMap<>();

            List<Tile> adjacentToFirstCorner = new ArrayList<>(pairsByTile.get(firstCorner));
            if (adjacentToFirstCorner.size() != 2) throw new RuntimeException("Corner doesn't have two neighbours");
            Tile firstAdjacent = adjacentToFirstCorner.get(0);
            Tile secondAdjacent = adjacentToFirstCorner.get(1);

            // find the first correct orientation
            for (Tile transformedFirstCorner : tileVariants.get(firstCorner)) {
                Set<Tile> possibleEast = Optional.ofNullable(alignedWEPairsByTile.get(transformedFirstCorner)).orElse(Collections.emptySet());
                Set<Tile> possibleSouth = Optional.ofNullable(alignedNSPairsByTile.get(transformedFirstCorner)).orElse(Collections.emptySet());

                if (possibleEast.size() == 1 && possibleSouth.size() == 1) {
                    result.put(new Coord(0, 0), transformedFirstCorner);
                    unplaced = removeFromUnplaced(unplaced, transformedFirstCorner);
                    result.put(new Coord(1, 0), possibleEast.stream().findFirst().get());
                    unplaced = removeFromUnplaced(unplaced, possibleEast.stream().findFirst().get());
                    result.put(new Coord(0, 1), possibleSouth.stream().findFirst().get());
                    unplaced = removeFromUnplaced(unplaced, possibleSouth.stream().findFirst().get());
                    // mutate the unplaced as passed in
                    originalUnplaced.clear();
                    originalUnplaced.addAll(unplaced);
                    return result;
                }
            }
            throw new RuntimeException("Failed to orient first corner");
        }

        private Set<Tile> removeFromUnplaced(Set<Tile> unplaced, Tile tile) {
            return unplaced.stream().filter(u -> u.number != tile.number).collect(Collectors.toSet());
        }

        private Set<TilePair> findPairs() {
            alignedPairs = new HashSet<>();
            alignedWEPairsByTile = new HashMap<>();
            alignedNSPairsByTile = new HashMap<>();
            Set<TilePair> result = new HashSet<>();
            tileVariants = generateVariants();
            for (Map.Entry<Tile, Set<Tile>> entry1 : tileVariants.entrySet()) {
                for (Map.Entry<Tile, Set<Tile>> entry2 : tileVariants.entrySet()) {
                    if (entry1.getKey().number != entry2.getKey().number) {
                        for (Tile tile1 : entry1.getValue()) {
                            for (Tile tile2 : entry2.getValue()) {
                                if (tilesAlignNS(tile1, tile2)) {
                                    result.add(new TilePair(entry1.getKey(), entry2.getKey()));
                                    alignedPairs.add(new TilePair(tile1, tile2));
                                    alignedNSPairsByTile.putIfAbsent(tile1, new HashSet<>());
                                    alignedNSPairsByTile.get(tile1).add(tile2);
                                }
                                if (tilesAlignNS(tile2, tile1)) {
                                    result.add(new TilePair(entry1.getKey(), entry2.getKey()));
                                    alignedPairs.add(new TilePair(tile2, tile1));
                                    alignedNSPairsByTile.putIfAbsent(tile2, new HashSet<>());
                                    alignedNSPairsByTile.get(tile2).add(tile1);
                                }
                                if (tilesAlignWE(tile1, tile2)) {
                                    result.add(new TilePair(entry1.getKey(), entry2.getKey()));
                                    alignedPairs.add(new TilePair(tile1, tile2));
                                    alignedWEPairsByTile.putIfAbsent(tile1, new HashSet<>());
                                    alignedWEPairsByTile.get(tile1).add(tile2);
                                }
                                if (tilesAlignWE(tile2, tile1)) {
                                    result.add(new TilePair(entry1.getKey(), entry2.getKey()));
                                    alignedPairs.add(new TilePair(tile2, tile1));
                                    alignedWEPairsByTile.putIfAbsent(tile2, new HashSet<>());
                                    alignedWEPairsByTile.get(tile2).add(tile1);
                                }
                            }
                        }
                    }
                }
            }
            return result;
        }

        private Coord findFirstUnfilled(Set<Coord> coords) {
            for (int y = 0; y < imageSize; y++) {
                for (int x = 0; x < imageSize; x++) {
                    Coord coord = new Coord(x, y);
                    if (!coords.contains(coord)) return coord;
                }
            }
            throw new RuntimeException("No unused coords");
        }

        private boolean tilesAlignNS(Tile northTile, Tile southTile) {
            return Arrays.equals(northTile.getBottomRow(),southTile.getTopRow());
        }

        private boolean tilesAlignWE(Tile westTile, Tile eastTile) {
            return Arrays.equals(westTile.getRightCol(), eastTile.getLeftCol());
        }

        private Map<Tile, Set<Tile>> generateVariants() {
            Map<Tile, Set<Tile>> result = new HashMap<>();
            for (Tile tile : tiles) {
                result.put(tile, tile.generateVariants());
            }
            return result;
        }
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class Tile {
        final long number;
        final CharGrid grid;
        Tile(List<String> lines) {
            number = Long.parseLong(lines.get(0).replace("Tile ","").replace(":", ""));
            grid = new CharGrid(lines.subList(1, lines.size()));
        }

        Tile(Tile other) {
            number = other.number;
            grid = new CharGrid(other.grid);
        }

        public Set<Tile> generateVariants() {
            return grid.generateVariants().stream().map(g -> new Tile(this.number, g)).collect(Collectors.toSet());
        }

        public char[] getTopRow() {
            return grid.getRow(0);
        }
        public char[] getBottomRow() {
            return grid.getRow(grid.getHeight()-1);
        }

        public char[] getLeftCol() {
            return grid.getCol(0);
        }
        public char[] getRightCol() {
            return grid.getCol(grid.getWidth()-1);
        }

        @Override
        public String toString() {
            return "" + number;
        }
    }

    @RequiredArgsConstructor
    static class TilePair {
        final Tile tile1;
        final Tile tile2;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TilePair tilePair = (TilePair) o;
            return (tile1.number == tilePair.tile1.number && tile2.number == tilePair.tile2.number)
                    || (tile1.number == tilePair.tile2.number && tile2.number == tilePair.tile1.number);
        }

        @Override
        public int hashCode() {
            return (int)(tile1.number + tile2.number);
        }
    }
}
