package com.johnpickup.aoc2019;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
class Instruction {
    final OpCode opCode;
    final List<Parameter> parameters;

    Instruction(Memory memory, long offset) {
        int op = (int)(memory.get(offset));
        opCode = OpCode.parse(op % 100);
        int modes = (op / 100);

        List<Long> paramList = new ArrayList<>(opCode.paramCount);
        for (int i = 0; i < opCode.paramCount; i++) {
            paramList.add(memory.get(offset + i + 1));
        }
        parameters = generateParameters(paramList, modes);
    }

    private List<Parameter> generateParameters(List<Long> params, int modes) {
        List<Parameter> result = new ArrayList<>();
        while (!params.isEmpty()) {
            result.add(new Parameter(params.get(0), Mode.of(modes%10)));
            params.remove(0);
            modes = modes / 10;
        }
        return result;
    }

    int inputSize() {
        return parameters.size() + 1;
    }

    public boolean isTerminate() {
        return opCode.equals(OpCode.END);
    }

    public void execute(Program program) {
        switch (opCode) {
            case ADD:
                program.setMemory(parameters.get(2).literal(program), parameters.get(0).evaluate(program) + parameters.get(1).evaluate(program));
                program.moveInstructionPointer(inputSize());
                break;
            case MULTIPLY:
                program.setMemory(parameters.get(2).literal(program), parameters.get(0).evaluate(program) * parameters.get(1).evaluate(program));
                program.moveInstructionPointer(inputSize());
                break;
            case READ:
                program.setMemory(parameters.get(0).literal(program), program.read());
                program.moveInstructionPointer(inputSize());
                break;
            case WRITE:
                program.write(parameters.get(0).evaluate(program));
                program.moveInstructionPointer(inputSize());
                break;
            case JIT:
                if (parameters.get(0).evaluate(program) != 0) {
                    program.setInstructionPointer(parameters.get(1).evaluate(program));
                } else {
                    program.moveInstructionPointer(inputSize());
                }
                break;
            case JIF:
                if (parameters.get(0).evaluate(program) == 0) {
                    program.setInstructionPointer((int)parameters.get(1).evaluate(program));
                } else {
                    program.moveInstructionPointer(inputSize());
                }
                break;
            case LT:
                boolean isLessThan = parameters.get(0).evaluate(program) < parameters.get(1).evaluate(program);
                program.setMemory(parameters.get(2).literal(program), isLessThan ? 1 : 0);
                program.moveInstructionPointer(inputSize());
                break;
            case EQ:
                boolean isEqual = parameters.get(0).evaluate(program) == parameters.get(1).evaluate(program);
                program.setMemory(parameters.get(2).literal(program), isEqual ? 1 : 0);
                program.moveInstructionPointer(inputSize());
                break;
            case RBO:
                program.adjustRelativeBase((int)parameters.get(0).evaluate(program));
                program.moveInstructionPointer(inputSize());
                break;
            default:
                throw new RuntimeException("Unknown instruction " + opCode);
        }
    }


    @RequiredArgsConstructor
    @Data
    static class Parameter {
        final long value;
        final Mode mode;

        public long evaluate(Program program) {
            switch(mode) {
                case IMMEDIATE: return value;
                case POSITION: return program.getMemory((int)value);
                case RELATIVE: return program.getMemory((int)value + program.relativeBase);
                default: throw new RuntimeException("Unknown mode " + mode);
            }
        }

        public long literal(Program program) {
            switch(mode) {
                case IMMEDIATE:
                case POSITION:
                    return value;
                case RELATIVE:
                    return value + program.relativeBase;
                default:
                    throw new RuntimeException("Unknown mode " + mode);
            }
        }
    }

    enum Mode {
        POSITION,
        IMMEDIATE,
        RELATIVE;

        public static Mode of(int value) {
            switch (value) {
                case 0: return POSITION;
                case 1: return IMMEDIATE;
                case 2: return RELATIVE;
                default: throw new RuntimeException("Unknown mode " + value);
            }
        }
    }
}
