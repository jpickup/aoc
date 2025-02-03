package com.johnpickup.aoc2020;

import com.johnpickup.util.Sets;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day21 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Food> foods = stream
                        .filter(s -> !s.isEmpty())
                        .map(Food::new)
                        .collect(Collectors.toList());

                Set<String> allIngredients = new HashSet<>();
                Set<String> allAllergens = new HashSet<>();
                foods.forEach(f -> allIngredients.addAll(f.ingredients));
                foods.forEach(f -> allAllergens.addAll(f.allergens));

                // each allergen is found in exactly one ingredient
                Map<String, Set<String>> allergenToIngredients = new HashMap<>();
                // get the foods for each allergen
                Map<String, Set<Food>> allergenToFoods = new HashMap<>();
                for (String allergen : allAllergens) {
                    allergenToFoods.put(allergen, foods.stream().filter(f -> f.allergens.contains(allergen)).collect(Collectors.toSet()));
                }
                // from the set of foods find the common ingredient for each
                for (String allergen : allAllergens) {
                    Set<String> ingredientsWithAllergen = new HashSet<>(allIngredients);
                    for (Food food : allergenToFoods.get(allergen)) {
                        ingredientsWithAllergen = Sets.intersection(ingredientsWithAllergen, food.ingredients);
                    }
                    allergenToIngredients.put(allergen, ingredientsWithAllergen);
                }

                Set<String> ingredientsWithNoAllergens = new HashSet<>(allIngredients);
                allergenToIngredients.values().forEach(ingredientsWithNoAllergens::removeAll);
                System.out.println(allergenToIngredients);
                System.out.println(ingredientsWithNoAllergens);

                long part1 = 0L;
                for (Food food : foods) {
                    for (String ingredientWithNoAllergen : ingredientsWithNoAllergens) {
                        if (food.ingredients.contains(ingredientWithNoAllergen)) part1++;
                    }
                }
                System.out.println("Part 1: " + part1);

                Map<String, String> knownAllergenToIngredient = new HashMap<>();
                boolean madeProgress = true;
                int prevKnown = 0;
                while (madeProgress) {
                    Map<String, String> single = allergenToIngredients.entrySet().stream()
                            .filter(e -> e.getValue().size() == 1)
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    e->e.getValue().stream().findFirst().orElseThrow(() -> new RuntimeException("No single ingredient"))));
                    knownAllergenToIngredient.putAll(single);
                    allergenToIngredients.values().forEach(is -> is.removeAll(knownAllergenToIngredient.values()));
                    madeProgress = knownAllergenToIngredient.size() > prevKnown;
                    prevKnown = knownAllergenToIngredient.size();
                }
                System.out.println(knownAllergenToIngredient);
                String part2 = knownAllergenToIngredient.entrySet().stream().sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue).collect(Collectors.joining(","));
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @ToString
    static class Food {
        final Set<String> ingredients;
        final Set<String> allergens;

        Food(String line) {
            String[] parts = line.split("\\(");
            ingredients = Arrays.stream(parts[0].split(" ")).collect(Collectors.toSet());
            if (parts.length==2) {
                allergens = Arrays.stream(parts[1]
                        .replace("contains ","")
                        .replace(")","").split(", ")).collect(Collectors.toSet());
            } else {
                allergens = Collections.emptySet();
            }
        }
    }
}
