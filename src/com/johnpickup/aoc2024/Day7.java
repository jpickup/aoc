package com.johnpickup.aoc2024;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {
    static int part = 1;
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day7/Day7.txt"))) {
            List<Equation> equations = stream.filter(s -> !s.isEmpty()).map(Equation::new).collect(Collectors.toList());

            System.out.println("Part 1 --------");
            System.out.println("Method 1: " + equations.stream().filter(Equation::canBeTrue).map(e -> e.total).reduce(0L, Long::sum));
            System.out.println("Method 2: " + equations.stream().filter(Equation::canBeTrueMethod2).map(e -> e.total).reduce(0L, Long::sum));

            part = 2;
            System.out.println("Part 2 --------");
            System.out.println("Method 1: " + equations.stream().filter(Equation::canBeTrue).map(e -> e.total).reduce(0L, Long::sum));
            System.out.println("Method 2: " + equations.stream().filter(Equation::canBeTrueMethod2).map(e -> e.total).reduce(0L, Long::sum));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start) + "ms");
    }


    @ToString
    static class Equation {
        final long total;
        final List<Long> numbers;

        Equation(String line) {
            String[] leftRight = line.split(": ");
            total = Long.parseLong(leftRight[0]);
            numbers = Arrays.stream(leftRight[1].split(" ")).map(String::trim).map(Long::parseLong).collect(Collectors.toList());
        }

        public boolean canBeTrue() {
            List<Long> possibleValues = possibleValues(numbers);
            return possibleValues.stream().anyMatch(l -> l.equals(total));
        }

        static List<Long> possibleValues(List<Long> inputs) {
            long last = inputs.get(inputs.size() - 1);
            if (inputs.size() == 1) return Collections.singletonList(last);

            List<Long> remaining = new ArrayList<>(inputs);
            remaining.remove(inputs.size() - 1);
            List<Long> possibleFromRemaining = possibleValues(remaining);
            List<Long> addValues = possibleFromRemaining.stream().map(l -> l + last).collect(Collectors.toList());
            List<Long> mulValues = possibleFromRemaining.stream().map(l -> l * last).collect(Collectors.toList());
            List<Long> result = new ArrayList<>(addValues);
            result.addAll(mulValues);
            if (part == 2) {
                List<Long> concValues = possibleFromRemaining.stream().map(l -> concat(l, last)).collect(Collectors.toList());
                result.addAll(concValues);
            }
            return result;
        }

        public boolean canBeTrueMethod2() {
            List<Long> possibleValues = possibleValuesMethod2(numbers);
            return possibleValues.stream().anyMatch(l -> l.equals(total));
        }


        static long concat(long l1, long l2) {
            return Long.parseLong(String.format("%d%d", l1, l2));
        }

        List<Long> possibleValuesMethod2(List<Long> inputs) {
            List<Long> result = new ArrayList<>();
            List<Operator> availableOperators = part == 1 ? Arrays.asList(Operator.ADD, Operator.MUL) : Arrays.asList(Operator.ADD, Operator.MUL, Operator.CONC);
            List<List<Operator>> operatorOptions = allPossible(inputs.size() - 1, availableOperators);

            for (List<Operator> operators : operatorOptions) {
                List<Operator> operatorsCopy = new ArrayList<>(operators);
                long possible = applyOperators(operatorsCopy, numbers);
                result.add(possible);
            }
            return result;
        }

        private long applyOperators(List<Operator> operators, List<Long> numbers) {
            List<Long> numbersCopy = new ArrayList<>(numbers);
            if (operators.size() != numbers.size() - 1) throw new RuntimeException("Incorrect number of operators");

            long result = numbersCopy.get(0);
            for (int i = 0; i < operators.size(); i++) {
                long num = numbersCopy.get(i+1);
                Operator operator = operators.get(i);
                switch (operator) {
                    case ADD: result = result + num; break;
                    case MUL: result = result * num; break;
                    case CONC: result = concat(result, num); break;
                }
            }
            return result;
        }

        // Mistakenly thought that concatenation had to have priority - which was the whole reason for this second approach - doh!
        private void applyConcatenation(List<Operator> operators, List<Long> numbers) {
            while (operators.stream().anyMatch(op -> op.equals(Operator.CONC))) {
                for (int i = 0; i < operators.size(); i++) {
                    if (operators.get(i) == Operator.CONC) {
                        long left = numbers.get(i);
                        long right = numbers.get(i + 1);
                        long conc = concat(left, right);
                        numbers.remove(i + 1);
                        numbers.remove(i);
                        numbers.add(i, conc);
                        operators.remove(i);
                        break;
                    }
                }
            }
        }

        private List<List<Operator>> allPossible(int size, List<Operator> operators) {
            if (size == 1) return operators.stream().map(Collections::singletonList).collect(Collectors.toList());

            List<List<Operator>> oneLess = allPossible(size - 1, operators);

            List<List<Operator>> result = new ArrayList<>();
            for (List<Operator> list : oneLess) {
                result.addAll(appendAll(list, operators));
            }
            return result;
        }

        static List<List<Operator>> appendAll(List<Operator> list, List<Operator> ops) {
            return ops.stream().map(op -> {
                ArrayList<Operator> longer = new ArrayList<>(list);
                longer.add(op);
                return longer;
            }).collect(Collectors.toList());
        }
    }

    enum Operator {
        ADD,
        MUL,
        CONC
    }
}
