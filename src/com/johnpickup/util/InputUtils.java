package com.johnpickup.util;

import java.util.ArrayList;
import java.util.List;

public class InputUtils {
    public static List<List<String>> splitIntoGroups(List<String> lines) {
        List<List<String>> result = new ArrayList<>();
        List<String> group = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) {
                result.add(new ArrayList<>(group));
                group.clear();
            } else {
                group.add(line);
            }
        }
        if (!group.isEmpty()) result.add(group);
        return result;
    }
}
