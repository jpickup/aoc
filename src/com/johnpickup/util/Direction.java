package com.johnpickup.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Direction {
    NORTH('^'),
    SOUTH('v'),
    EAST('>'),
    WEST('<');
    final char ch;

    public Direction left() {
        switch (this) {
            case NORTH: return WEST;
            case WEST: return SOUTH;
            case SOUTH: return EAST;
            case EAST: return NORTH;
            default: throw new RuntimeException("Unknown dir " +  this);
        }
    }
    public Direction right() {
        switch (this) {
            case NORTH: return EAST;
            case EAST: return SOUTH;
            case SOUTH: return WEST;
            case WEST: return NORTH;
            default: throw new RuntimeException("Unknown dir " +  this);
        }
    }

    public Direction opposite() {
        switch (this) {
            case NORTH: return SOUTH;
            case EAST: return WEST;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            default: throw new RuntimeException("Unknown dir " +  this);
        }
    }
    public Coord apply(Coord c) {
        switch (this) {
            case NORTH: return c.north();
            case EAST: return c.east();
            case SOUTH: return c.south();
            case WEST: return c.west();
            default:
                throw new RuntimeException("Unknown dir " + this);
        }
    }

    @Override
    public String toString() {
        return "" + ch;
    }
}