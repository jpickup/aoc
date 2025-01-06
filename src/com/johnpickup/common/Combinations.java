package com.johnpickup.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Combinations {
    public static <T> List<List<T>> allPossibleCombinations(int size, List<T> values) {
        if (size == 1) return values.stream().map(Collections::singletonList).collect(Collectors.toList());

        List<List<T>> oneLess = allPossibleCombinations(size - 1, values);

        List<List<T>> result = new ArrayList<>();
        for (List<T> list : oneLess) {
            result.addAll(appendAll(list, values));
        }
        return result;
    }

    public static <T> List<List<T>> allPossiblePermutations(int size, List<T> values) {
        List<List<T>> result = new ArrayList<>();
        for (List<T> combination : allPossibleCombinations(size, values)) {
            if (!hasDuplicateElements(combination)) {
                result.add(combination);
            }
        }
        return result;
    }

    private static <T> boolean hasDuplicateElements(List<T> items) {
        boolean result = false;
        for (T item : items) {
            result = result || items.stream().filter(i -> i.equals(item)).count() != 1;
        }
        return result;
    }


    static <T> List<List<T>> appendAll(List<T> list, List<T> ops) {
        return ops.stream().map(op -> {
            ArrayList<T> longer = new ArrayList<>(list);
            longer.add(op);
            return longer;
        }).collect(Collectors.toList());
    }
}
