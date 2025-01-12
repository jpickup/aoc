package com.johnpickup.util;

import java.util.Set;

public interface Grid<T> {
    T getCell(Coord c);
    void setCell(Coord c, T value);
    Range<Coord> bounds();
    int size();
    boolean hasCell(Coord coord);
    Set<Coord> findCells(T target);

}
