package com.johnpickup.aoc2019;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Memory {
    final Map<Long, Long> values = new TreeMap<>();

    public Memory(List<Long> initialMemory) {
        initialise(initialMemory);
    }

    public void initialise(List<Long> initialMemory) {
        for (int i = 0; i < initialMemory.size(); i++) values.put((long)i, initialMemory.get(i));
    }

    public long get(long address) {
        if (address < 0) throw new MemoryException("Address out of bounds " + address);
        values.putIfAbsent(address, 0L);
        return values.get(address);
    }

    public void set(long address, long value) {
        values.put(address, value);
    }

    static class MemoryException extends RuntimeException {
        public MemoryException(String error) {
            super(error);
        }
    }
}
