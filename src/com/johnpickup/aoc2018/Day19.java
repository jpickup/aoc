package com.johnpickup.aoc2018;

import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day19 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                List<Instruction>  instructions = lines.stream()
                        .filter(s -> !s.startsWith("#"))
                        .map(Instruction::new)
                        .collect(Collectors.toList());

                String firstLine = lines.get(0);

                int ipRegister = Integer.parseInt(firstLine.replace("#ip ", ""));
                Program program = new Program(instructions, ipRegister);
                Registers registers = program.execute();
                System.out.println("Part 1: " + registers.getRegister(0));

                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static final boolean debug = true;
    static class Program {
        final List<Instruction> instructions;
        int instructionPointerRegister;
        Registers registers;

        Program(List<Instruction> instructions, int ipRegister) {
            this.instructions = instructions;
            this.instructionPointerRegister = ipRegister;
            this.registers = new Registers();
        }

        Registers execute() {
            boolean running = true;
            while (running) {
                int instructionPointer = registers.getInstructionPointer();
                Registers before = Registers.copy(registers);
                Instruction instruction = instructions.get(instructionPointer);
                registers = instruction.opCode.apply(registers, instruction);
                if (debug) System.out.printf("ip=%d %s %s %s%n", instructionPointer, before, instruction, registers);
                int incrementedInstructionPointer = registers.getInstructionPointer() + 1;
                running = incrementedInstructionPointer >= 0 && incrementedInstructionPointer < instructions.size();
                if (running) registers.setInstructionPointer(incrementedInstructionPointer);
            }
            return registers;
        }
    }

    interface OpCode {
        static OpCode parse(String name) {
            try {
                Class<?> opCodeClass = opCodeClasses.get(name);
                return (OpCode)opCodeClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String name();
        Registers apply(Registers input, Instruction instruction);
    }

    static class Instruction {
        final OpCode opCode;
        long[] values = new long[4];

        public Instruction(String s) {
            String[] parts = s.split(" ");
            if (parts.length != 4) throw new RuntimeException("Invalid instruction input " + s);
            opCode = OpCode.parse(parts[0]);
            for (int i = 1; i < 4; i++) {
                values[i] = Long.parseLong(parts[i]);
            }
        }

        public OpCode opcode() {return opCode;}
        public long A() {return values[1];}
        public long B() {return values[2];}
        public long C() {return values[3];}

        @Override
        public String toString() {
            return String.format("%s %d %d %d", opCode.name(), A(), B(), C());
        }
    }

    @EqualsAndHashCode
    static class Registers {
        long[] values = new long[6];
        @Setter
        int ipRegister;

        public long getRegister(long index) {
            if (index < 0 || index > 5) throw new RuntimeException("Invalid register index");
            return values[effectiveIndex(index)];
        }

        public Registers setRegister(long index, long value) {
            if (index < 0 || index > 5) throw new RuntimeException("Invalid register index");
            Registers result = Registers.copy(this);
            result.values[effectiveIndex(index)] = value;
            result.ipRegister = ipRegister;
            return result;
        }

        public int getInstructionPointer() {
            return (int)values[ipRegister];
        }

        public void setInstructionPointer(int ip) {
            values[ipRegister] = ip;
        }

        public void incrementInstructionPointer() {
            values[ipRegister] += 1;
        }

        private int effectiveIndex(long index) {
            return (int)index;
//            if (index < ipRegister)
//                return (int)index;
//            else
//                return (int)index + 1;
        }

        public Registers(String s) {
            String withoutCommas = s.replaceAll(",", "");
            String[] parts = withoutCommas.split(" ");
            if (parts.length != 6) throw new RuntimeException("Invalid register input " + s);
            for (int i = 0; i < 6; i++) {
                values[i] = Long.parseLong(parts[i]);
            }
        }

        public Registers() {
            for (int i = 0; i < 6; i++) {
                values[i] = 0;
            }
        }

        @Override
        public String toString() {
            return String.format("[%d, %d, %d, %d, %d, %d]",
                    values[0], values[1], values[2], values[3], values[4], values[5]);
        }

        static Registers copy(Registers source) {
            Registers result = new Registers();
            for (int i = 0; i < 4; i++) {
                result.values[i] = source.getRegister(i);
            }
            return result;
        }
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
}
