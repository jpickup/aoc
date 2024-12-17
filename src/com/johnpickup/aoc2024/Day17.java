package com.johnpickup.aoc2024;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day17 {

    static BigInteger TWO = BigInteger.valueOf(2L);
    static BigInteger EIGHT = BigInteger.valueOf(8L);
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                //prefix + "-test2.txt"
                prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Computer computer = new Computer(lines);
                System.out.println(computer);
                List<Integer> output = computer.runProgram();
                System.out.println(computer);
                System.out.println("Part 1: " + output.stream().map(i -> "" + i).collect(Collectors.joining(",")));

                // part 2
                System.out.println(computer.listing());

                List<Integer> target = Arrays.asList(2,4,1,5,7,5,1,6,0,3,4,6,5,5,3,0);
                List<Integer> reversedTarget = new ArrayList<>();
                for (int i = target.size()-1; i>=0; i--) reversedTarget.add(target.get(i));

                System.out.println("Target : " + target);
                int digitsToMatch = 0;

                // for each output string portion, the possible inputs that can produce it
                Map<List<Integer>, List<BigInteger>> possibleValues = new HashMap<>();
                possibleValues.put(Collections.emptyList(), Collections.singletonList(BigInteger.ZERO));

                for (Integer targetOutput : reversedTarget) {
                    digitsToMatch++;
                    System.out.println("Looking for digit " + targetOutput);

                    List<Integer> desiredPortion = target.subList(target.size() - digitsToMatch, target.size());
                    List<Integer> prevDesiredPortion = target.subList(target.size() - digitsToMatch + 1, target.size());
                    for (int aInc = 0; aInc < 8; aInc++) {
                        List<BigInteger> prevAs = possibleValues.get(prevDesiredPortion);
                        for (BigInteger prevA : prevAs) {
                            BigInteger testA = prevA.multiply(EIGHT).add(BigInteger.valueOf(aInc));
                            computer.reset(testA, BigInteger.ZERO, BigInteger.ZERO);
                            List<Integer> outputPortion = computer.runProgram();
                            if (outputPortion.equals(desiredPortion)) {
                                possibleValues.putIfAbsent(desiredPortion, new ArrayList<>());
                                possibleValues.get(desiredPortion).add(testA);
                            }
                        }
                    }
                }
                System.out.println("Part 2 : " + possibleValues.get(target).stream().sorted().findFirst().orElse(null));
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }


    @ToString
    static class Computer {
        List<Integer> output = new ArrayList<>();
        List<Instruction> instructions = new ArrayList<>();

        BigInteger A;
        BigInteger B;
        BigInteger C;
        int IP = 0;

        int maxOutputSize = 1000;

        static Pattern regPattern = Pattern.compile("Register ([A-C]): ([0-9]+)");
        Computer(List<String> lines) {
            Matcher matcherA = regPattern.matcher(lines.get(0));
            Matcher matcherB = regPattern.matcher(lines.get(1));
            Matcher matcherC = regPattern.matcher(lines.get(2));
            if (matcherA.matches() && matcherB.matches() && matcherC.matches()) {
                A = new BigInteger(matcherA.group(2));
                B = new BigInteger(matcherB.group(2));
                C = new BigInteger(matcherC.group(2));
                String[] is = lines.get(3).substring(lines.get(3).indexOf(' ')+1).split(",");
                List<Integer> progInts = Arrays.stream(is).map(Integer::parseInt).collect(Collectors.toList());

                for (int i = 0; i < progInts.size() / 2; i++) {
                    instructions.add(new Instruction(progInts.get(i*2), progInts.get(i*2+1)));
                }
            }
            else {
                throw new RuntimeException("Invalid program");
            }
        }

        public List<Integer> runProgram() {
            while (IP < instructions.size() && output.size() < maxOutputSize) {
                Instruction instruction = instructions.get(IP);
                instruction.execute(this);
            }
            return output;
        }

        public String listing() {
            StringBuilder sb = new StringBuilder();
            sb.append("IP | Ins  Arg").append("\n");
            for (int i = 0; i < instructions.size(); i++) {
                Instruction instruction = instructions.get(i);
                sb.append(String.format("%02d | ", i))
                  .append(instruction.listingLine())
                  .append("\n");
            }
            return sb.toString();
        }

        public void reset(BigInteger a, BigInteger b, BigInteger c) {
            A = a;
            B = b;
            C = c;
            IP = 0;
            output.clear();
        }
    }

    @RequiredArgsConstructor
    @ToString
    static class Instruction {
        final OpCode opCode;
        final int operand;
        Instruction(int opCode, int operand) {
            this.opCode = OpCode.parse(opCode);
            this.operand = operand;
        }

        public void execute(Computer computer) {
            switch (opCode) {
                case ADV:
                    computer.A = computer.A.divide(TWO.pow(comboOperand(operand, computer).intValue()));
                    computer.IP++;
                    break;
                case BXL:
                    computer.B = computer.B.xor(BigInteger.valueOf(operand));
                    computer.IP++;
                    break;
                case BST:
                    computer.B = comboOperand(operand, computer).mod(EIGHT);
                    computer.IP++;
                    break;
                case JNZ:
                    if (!computer.A.equals(BigInteger.ZERO))
                        computer.IP = operand;
                    else
                        computer.IP++;
                    break;
                case BXC:
                    computer.B = computer.B.xor(computer.C);
                    computer.IP++;
                    break;
                case OUT:
                    computer.output.add(comboOperand(operand, computer).mod(EIGHT).intValue());
                    computer.IP++;
                    break;
                case BDV:
                    computer.B = computer.A.divide(TWO.pow(comboOperand(operand, computer).intValue()));
                    computer.IP++;
                    break;
                case CDV:
                    computer.C = computer.A.divide(TWO.pow(comboOperand(operand, computer).intValue()));
                    computer.IP++;
                    break;
                default:
                    throw new RuntimeException("Invalid instruction " + opCode);
            }
        }

        private BigInteger comboOperand(int operand, Computer computer) {
            switch (operand) {
                case 0:
                case 1:
                case 2:
                case 3:
                        return BigInteger.valueOf(operand);
                case 4:
                    return computer.A;
                case 5:
                    return computer.B;
                case 6:
                    return computer.C;
                default:
                    throw new RuntimeException("Invalid combo operand " + operand);
            }
        }

        public String listingLine() {
            return String.format("%s  %s", opCode, operandString(operand));
        }

        private String operandString(int operand) {
            switch (operand) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return "" + operand;
                case 4:
                    return "A";
                case 5:
                    return "B";
                case 6:
                    return "C";
                default:
                    throw new RuntimeException("Invalid combo operand " + operand);
            }
        }
    }

    @RequiredArgsConstructor
    enum OpCode {
        ADV(0),
        BXL(1),
        BST(2),
        JNZ(3),
        BXC(4),
        OUT(5),
        BDV(6),
        CDV(7);
        final int value;

        public static OpCode parse(int o) {
            switch(o) {
                case 0: return ADV;
                case 1: return BXL;
                case 2: return BST;
                case 3: return JNZ;
                case 4: return BXC;
                case 5: return OUT;
                case 6: return BDV;
                case 7: return CDV;
                default: throw new RuntimeException("Unrecognised opcode " + o);
            }
        }
    }
}
