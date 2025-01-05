package com.johnpickup.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Maps {
    public static <K,V> Map<V, Set<K>> reverseMap(Map<K, V> map) {
        Map<V, Set<K>> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.putIfAbsent(entry.getValue(), new HashSet<>());
            result.get(entry.getValue()).add(entry.getKey());
        }
        return result;
    }
}
