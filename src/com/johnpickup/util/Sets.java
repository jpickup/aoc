package com.johnpickup.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sets {

    /**
     * Find the union of the two sets - elements that exist in either set (or both)
     */
    public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.addAll(set2);
        return result;
    }

    /**
     * Find the intersection of the two sets - elements that exist in both
     */
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>();
        for (T t : set1) {
            if (set2.contains(t)) result.add(t);
        }
        return result;
    }

    /**
     * Find the elements that are only in one set and not in both sets - the inverse of the intersection
     */
    public static <T> Set<T> disjoint(Set<T> set1, Set<T> set2) {
        Set<T> onlyIn1 = new HashSet<>(set1);
        Set<T> onlyIn2 = new HashSet<>(set2);
        onlyIn1.removeAll(set2);
        onlyIn2.removeAll(set1);
        Set<T> result = new HashSet<>(onlyIn1.size() + onlyIn2.size());
        result.addAll(onlyIn1);
        result.addAll(onlyIn2);
        return result;
    }

    public static<T>  Set<Set<T>> subsets(Set<T> set) {
        if (set.isEmpty()) return Collections.singleton(Collections.emptySet());
        Set<Set<T>> result = new HashSet<>();
        result.add(set);
        for (T element : set) {
            Set<T> subset = new HashSet<>(set);
            subset.remove(element);
            result.addAll(subsets(subset));
        }
        return result;
    }
}
