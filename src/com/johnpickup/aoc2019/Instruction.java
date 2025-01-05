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

    Instruction(List<Integer> inputs) {
        opCode = OpCode.parse(inputs.get(0) % 100);
        int modes = inputs.get(0) / 100;
        parameters = generateParameters(new ArrayList<>(inputs.subList(1, opCode.paramCount + 1)), modes);
    }

    private List<Parameter> generateParameters(List<Integer> params, int modes) {
        List<Parameter> result = new ArrayList<>();
        while (!params.isEmpty()) {
            result.add(new Parameter(params.get(0), modes%10==0?Mode.POSITION:Mode.IMMEDIATE));
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
                program.setMemory(parameters.get(2).value, parameters.get(0).evaluate(program) + parameters.get(1).evaluate(program));
                break;
            case MULTIPLY:
                program.setMemory(parameters.get(2).value, parameters.get(0).evaluate(program) * parameters.get(1).evaluate(program));
                break;
            case READ:
                program.setMemory(parameters.get(0).value, program.read());
                break;
            case WRITE:
                program.write(parameters.get(0).evaluate(program));
                break;
            default:
                throw new RuntimeException("Unknown instruction " + opCode);
        }
    }


    @RequiredArgsConstructor
    @Data
    static class Parameter {
        final int value;
        final Mode mode;

        public int evaluate(Program program) {
            switch(mode) {
                case IMMEDIATE: return value;
                case POSITION: return program.getMemory(value);
                default: throw new RuntimeException("Unknown mode " + mode);
            }
        }
    }

    enum Mode {
        POSITION,
        IMMEDIATE
    }
}
