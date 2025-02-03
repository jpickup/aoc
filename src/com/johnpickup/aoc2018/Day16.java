package com.johnpickup.aoc2018;

import com.johnpickup.util.InputUtils;
import com.johnpickup.util.Sets;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day16 {
    static boolean isTest;

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .collect(Collectors.toList());

                List<OpCodeTest> tests = new ArrayList<>();

                // chunk into groups separated by new lines, these will be before/after triples plus the input
                List<List<String>> groups = InputUtils.splitIntoGroups(lines);
                for (List<String> group : groups) {
                    if (group.size() == 3) {
                        OpCodeTest opCodeTest = new OpCodeTest(group);
                        tests.add(opCodeTest);
                    }
                }

                Map<String, Integer> possible = new HashMap<>();
                Map<OpCodeTest, Integer> matchCount = new HashMap<>();
                Map<String, Set<Long>> matches = new HashMap<>();

                for (OpCodeTest test : tests) {
                    for (Map.Entry<String, Class> opCodeClass : opCodeClasses.entrySet()) {
                        Class c = opCodeClass.getValue();
                        OpCode oc = (OpCode)(c.newInstance());
                        Registers actual = oc.apply(test.before, test.instruction);
                        if (actual.equals(test.after)) {
                            possible.put(opCodeClass.getKey(), possible.getOrDefault(opCodeClass.getKey(), 0) + 1);
                            matchCount.put(test, matchCount.getOrDefault(test, 0) + 1);
                            matches.put(opCodeClass.getKey(), Sets.addElement(matches.getOrDefault(opCodeClass.getKey(), new HashSet<>()), test.instruction.opcode()));
                        }
                    }
                }
                long part1 = matchCount.entrySet().stream().filter(e -> e.getValue() >= 3).count();
                System.out.println("Part 1: " + part1);

                Map<Long, String> resolved = new HashMap<>();
                while (!matches.isEmpty()) {
                    Map<String, Long> known = matches.entrySet().stream()
                            .filter(m -> m.getValue().size() == 1)
                            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().findFirst().orElseThrow(() -> new RuntimeException("Empty"))));
                    for (Map.Entry<String, Long> knownEntry : known.entrySet()) {
                        matches.remove(knownEntry.getKey());
                        resolved.put(knownEntry.getValue(), knownEntry.getKey());
                        for (Map.Entry<String, Set<Long>> matchEntry : matches.entrySet()) {
                            matchEntry.getValue().remove(knownEntry.getValue());
                        }
                    }
                }
                System.out.println(resolved);

                if (isTest) continue;       // no program to run in the test input

                List<Instruction> instructions = groups.get(groups.size() - 1).stream().map(Instruction::new).collect(Collectors.toList());
                Registers registers = new Registers();

                for (Instruction instruction : instructions) {
                    Class opCodeClass = opCodeClasses.get(resolved.get(instruction.opcode()));
                    OpCode opCode = (OpCode)opCodeClass.newInstance();
                    registers = opCode.apply(registers, instruction);
                }
                System.out.println("Part 2: " + registers.getRegister(0));

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @EqualsAndHashCode
    @ToString
    static class OpCodeTest {
        final Registers before;
        final Instruction instruction;
        final Registers after;

        OpCodeTest(List<String> lines) {
            if (lines.size() != 3) throw new RuntimeException("Invalid op code test input: " + lines);
            before = new Registers(lines.get(0). replace("Before: [","").replace("]", ""));
            instruction = new Instruction(lines.get(1));
            after = new Registers(lines.get(2). replace("After:  [","").replace("]", ""));
        }
    }

    interface OpCode {
        String name();
        Registers apply(Registers input, Instruction instruction);
    }

    @ToString
    static class Instruction {
        long[] values = new long[4];

        public Instruction(String s) {
            String[] parts = s.split(" ");
            if (parts.length != 4) throw new RuntimeException("Invalid instruction input " + s);
            for (int i = 0; i < 4; i++) {
                values[i] = Long.parseLong(parts[i]);
            }
        }

        public long opcode() {return values[0];}
        public long A() {return values[1];}
        public long B() {return values[2];}
        public long C() {return values[3];}
    }

    @EqualsAndHashCode
    @ToString
    static class Registers {
        long[] values = new long[4];

        public long getRegister(long index) {
            if (index < 0 || index >= 4) throw new RuntimeException("Invalid register index");
            return values[(int) index];
        }

        public Registers setRegister(long index, long value) {
            if (index < 0 || index >= 4) throw new RuntimeException("Invalid register index");
            Registers result = Registers.copy(this);
            result.values[(int) index] = value;
            return result;
        }

        public  Registers() {
        }

        public Registers(String s) {
            String withoutCommas = s.replaceAll(",", "");
            String[] parts = withoutCommas.split(" ");
            if (parts.length != 4) throw new RuntimeException("Invalid register input " + s);
            for (int i = 0; i < 4; i++) {
                values[i] = Long.parseLong(parts[i]);
            }
        }

        static Registers copy(Registers source) {
            Registers result = new Registers();
            for (int i = 0; i < 4; i++) {
                result.values[i] = source.getRegister(i);
            }
            return result;
        }
    }

    static Map<String, Class> opCodeClasses = new HashMap<>();
    static Map<Class, String> opCodeClassNames = new HashMap<>();

    static {
        opCodeClasses.put("addr", addr.class);
        opCodeClasses.put("addi", addi.class);
        opCodeClasses.put("mulr", mulr.class);
        opCodeClasses.put("muli", muli.class);
        opCodeClasses.put("banr", banr.class);
        opCodeClasses.put("bani", bani.class);
        opCodeClasses.put("borr", borr.class);
        opCodeClasses.put("bori", bori.class);
        opCodeClasses.put("setr", setr.class);
        opCodeClasses.put("seti", seti.class);
        opCodeClasses.put("gtir", gtir.class);
        opCodeClasses.put("gtri", gtri.class);
        opCodeClasses.put("gtrr", gtrr.class);
        opCodeClasses.put("eqir", eqir.class);
        opCodeClasses.put("eqri", eqri.class);
        opCodeClasses.put("eqrr", eqrr.class);
        opCodeClasses.forEach((key, value) -> opCodeClassNames.put(value, key));
    }

    static class addr implements OpCode {
        @Override
        public String name() {
            return "addr";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) + input.getRegister((instruction.B())));
        }
    }

    static class addi implements OpCode {
        @Override
        public String name() {
            return "addi";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) + instruction.B());
        }
    }

    static class mulr implements OpCode {
        @Override
        public String name() {
            return "mulr";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) * input.getRegister((instruction.B())));
        }
    }

    static class muli implements OpCode {
        @Override
        public String name() {
            return "muli";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) * instruction.B());
        }
    }

    static class banr implements OpCode {
        @Override
        public String name() {
            return "banr";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) & input.getRegister((instruction.B())));
        }
    }

    static class bani implements OpCode {
        @Override
        public String name() {
            return "bani";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) & instruction.B());
        }
    }

    static class borr implements OpCode {
        @Override
        public String name() {
            return "borr";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) | input.getRegister((instruction.B())));
        }
    }

    static class bori implements OpCode {
        @Override
        public String name() {
            return "bori";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) | instruction.B());
        }
    }

    static class setr implements OpCode {
        @Override
        public String name() {
            return "setr";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()));
        }
    }

    static class seti implements OpCode {
        @Override
        public String name() {
            return "seti";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), instruction.A());
        }
    }

    static class gtir implements OpCode {
        @Override
        public String name() {
            return "gtir";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), instruction.A() > input.getRegister((instruction.B())) ? 1L : 0L);
        }
    }

    static class gtri implements OpCode {
        @Override
        public String name() {
            return "gtri";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) > instruction.B() ? 1L : 0L);
        }
    }

    static class gtrr implements OpCode {
        @Override
        public String name() {
            return "gtrr";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) > input.getRegister((instruction.B())) ? 1L : 0L);
        }
    }

    static class eqir implements OpCode {
        @Override
        public String name() {
            return "eqir";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), instruction.A() == input.getRegister((instruction.B())) ? 1L : 0L);
        }
    }

    static class eqri implements OpCode {
        @Override
        public String name() {
            return "eqri";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) == instruction.B() ? 1L : 0L);
        }
    }

    static class eqrr implements OpCode {
        @Override
        public String name() {
            return "eqrr";
        }

        @Override
        public Registers apply(Registers input, Instruction instruction) {
            return input.setRegister(instruction.C(), input.getRegister(instruction.A()) == input.getRegister((instruction.B())) ? 1L : 0L);
        }
    }
}
