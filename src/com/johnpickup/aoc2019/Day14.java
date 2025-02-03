package com.johnpickup.aoc2019;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day14 {
    private static final String ORE = "ORE";
    private static final Ingredient SINGLE_ORE = new Ingredient(1L, ORE);
    private static final Reaction NULL_ORE_REACTION = new Reaction(Collections.singletonList(SINGLE_ORE), SINGLE_ORE);
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
                , prefix + "-test3.txt"
                , prefix + "-test4.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Reaction> reactions = stream
                        .filter(s -> !s.isEmpty())
                        .map(Reaction::new)
                        .collect(Collectors.toList());

                List<Reaction> finalReactions = reactions.stream().filter(r -> r.output.chemical.equals("FUEL")).collect(Collectors.toList());
                // all final reactions produce a single fuel and there is only one per input
                Reaction finalReaction = finalReactions.get(0);
                long part1 = calcMinimumOre(finalReaction, new Ingredient(1L, "FUEL"), reactions, new HashMap<>());
                System.out.println("Part 1: " + part1);
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static long calcMinimumOre(Reaction reaction, Ingredient targetIngredient, List<Reaction> reactions, Map<String, Long> residuals) {
        if (targetIngredient.chemical.equals(ORE)) {
            return targetIngredient.quantity;
        }
        residuals.putIfAbsent(targetIngredient.chemical, 0L);

        long targetQuantity = targetIngredient.getQuantity();
        long residualOfTarget = residuals.get(targetIngredient.chemical);
        long additionalTarget = targetQuantity - residualOfTarget;
        System.out.printf("%s needs %d as we already have %d %n", targetIngredient, additionalTarget, residualOfTarget);
        Ingredient actualTargetIngredient = new Ingredient(additionalTarget, targetIngredient.chemical);
        List<Ingredient> ingredients = reaction.ingredientsForDesiredOutput(actualTargetIngredient);
        long produced = reaction.howManyFor(additionalTarget) * reaction.output.quantity;
        long newResidual = produced - targetQuantity;
        residuals.put(targetIngredient.chemical, newResidual);
        System.out.printf("Produced %d %s which is %d more than we needed %n", produced, targetIngredient.chemical, newResidual);

        long total = 0L;

        Map<Ingredient, Reactions> reactionsPerIngredient = new HashMap<>();
        for (Ingredient requiredIngredient : ingredients) {
            long best = Long.MAX_VALUE;
            Reaction bestReaction = null;
            if (requiredIngredient.is(ORE)) {
                best = requiredIngredient.quantity;
                bestReaction = NULL_ORE_REACTION;
            } else {
                for (Reaction nextReaction : reactions) {
                    if (nextReaction.produces(requiredIngredient)) {
                        long attempt = calcMinimumOre(nextReaction, requiredIngredient, reactions, residuals);
                        if (attempt < best) {
                            best = attempt;
                            bestReaction = nextReaction;
                        }
                    }
                }
            }
            if (best != Long.MAX_VALUE) {
                reactionsPerIngredient.put(requiredIngredient, new Reactions(bestReaction, best));
                total += best;
            } else {
                throw new RuntimeException("No reactions produce " + targetIngredient);
            }
        }
        System.out.printf("To get %s we need: %s, which we get from:%n %s %n", targetIngredient, ingredients, reactionsPerIngredient);

        return total;
    }

    @Data
    static class Reactions {
        final Reaction reaction;
        final long count;
        @Override
        public String toString() {
            return String.format("%d * %s", count, reaction);
        }
    }

    @AllArgsConstructor
    static class Reaction {
        final List<Ingredient> inputs;
        final Ingredient output;

        Reaction(String line) {
            String[] leftRight = line.split("=>");
            String[] inputParts = leftRight[0].split(",");
            inputs = Arrays.stream(inputParts).map(s -> new Ingredient(s.trim())).collect(Collectors.toList());
            output = new Ingredient(leftRight[1].trim());
        }

        @Override
        public String toString() {
            return String.format("%s => %s",
                    inputs.stream().map(Ingredient::toString).collect(Collectors.joining(", ")),
                    output.toString());
        }

        List<Ingredient> ingredientsForDesiredOutput(Ingredient desiredOutput) {
            if (!produces(output)) return Collections.emptyList();
            long count = howManyFor(desiredOutput.quantity);
            return inputs.stream().map(i -> new Ingredient(i.quantity * count, i.chemical)).collect(Collectors.toList());
        }

        boolean produces(Ingredient requiredIngredient) {
            return output.chemical.equals(requiredIngredient.chemical);
        }

        public long howManyFor(long target) {
            double ratio = 1.0d * target / output.quantity;
            return (long)Math.ceil(ratio);
        }
    }

    @AllArgsConstructor
    @Data
    static class Ingredient {
        long quantity;
        String chemical;

        Ingredient(String s) {
            this(Long.parseLong(s.split(" ")[0]), s.split(" ")[1]);
        }

        public boolean is(String chemical) {
            return this.chemical.equals(chemical);
        }

        @Override
        public String toString() {
            return String.format("%d %s", quantity, chemical);
        }
    }
}
