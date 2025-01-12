package com.johnpickup.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class SparseGrid<T> implements Grid<T>{
    private static final char ORIGIN_CHAR = '@';
    @Setter
    boolean showOrigin = false;
    @Getter
    final Map<Coord, T> cells = new TreeMap<>();

    public T getCell(Coord c) {
        return cells.get(c);
    }

    public void setCell(Coord c, T value) {
        cells.put(c, value);
    }

    public Range<Coord> bounds() {
        int minX = cells.keySet().stream().map(Coord::getX).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No elements in grid"));
        int minY = cells.keySet().stream().map(Coord::getY).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No elements in grid"));
        int maxX = cells.keySet().stream().map(Coord::getX).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No elements in grid"));
        int maxY = cells.keySet().stream().map(Coord::getY).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No elements in grid"));

        return new Range<>(new Coord(minX, minY), new Coord(maxX, maxY));
    }

    @Override
    public int size() {
        return cells.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Range<Coord> bounds = bounds();
        for (int y = bounds.getLower().getY(); y <= bounds.getUpper().getY(); y++) {
            for (int x = bounds.getLower().getX(); x <= bounds.getUpper().getX(); x++) {
                Coord coord = new Coord(x, y);
                if (showOrigin && coord.equals(Coord.ORIGIN)) sb.append(ORIGIN_CHAR);
                else sb.append(Optional.ofNullable(getCell(coord)).map(Object::toString).orElse(" "));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public boolean hasCell(Coord coord) {
        return cells.containsKey(coord);
    }

    public Set<Coord> findCells(T target) {
        return cells.entrySet().stream().filter(e -> e.getValue().equals(target)).map(Map.Entry::getKey).collect(Collectors.toSet());
    }
}
