package com.johnpickup.aoc2019;

import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ToString
class Program {
    final List<Long> initialMemory;
    Memory memory;
    long instructionPointer;
    long relativeBase;
    List<Long> inputs = new ArrayList<>();
    List<Long> outputs = new ArrayList<>();
    boolean terminated;

    Program(String line) {
        initialMemory = Arrays.stream(line.split(",")).map(Long::parseLong).collect(Collectors.toList());
        reset();
    }


    public void reset() {
        memory = new Memory(initialMemory);
        instructionPointer = 0;
        relativeBase = 0;
        terminated = false;
        inputs.clear();
        outputs.clear();
    }

    public List<Long> processInputs(List<Long> inputs) {
        this.inputs.addAll(inputs);
        try {
            execute();
        } catch (MissingInputException e) {
            // NOOP
        }
        List<Long> result = new ArrayList<>(outputs);
        outputs.clear();
        return result;
    }

    public void execute() {
        while (true) {
            Instruction instruction = new Instruction(memory, instructionPointer);
            if (instruction.isTerminate()) {
                terminated = true;
                break;
            }
            instruction.execute(this);
        }
    }

    public long getMemory(long location) {
        return memory.get(location);
    }

    public void setMemory(long location, long value) {
        memory.set(location, value);
    }

    public long read() {
        if (inputs.isEmpty()) throw new MissingInputException();
        long result = inputs.get(0);
        inputs.remove(0);
        return result;
    }

    public void write(long value) {
        outputs.add(value);
    }

    public String showOutput() {
        return outputs.stream().map(i -> Long.toString(i)).collect(Collectors.joining(","));
    }

    public void addInput(long value) {
        inputs.add(value);
    }

    public long getOutput(int index) {
        if (index < 0 || index >= outputs.size()) throw new RuntimeException("No output with index " + index);
        return outputs.get(index);
    }

    public void moveInstructionPointer(int moveBy) {
        instructionPointer += moveBy;
    }

    public void setInstructionPointer(long value) {
        instructionPointer = value;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public List<Long> consumeOutputs() {
        ArrayList<Long> currentOutputs = new ArrayList<>(outputs);
        outputs.clear();
        return currentOutputs;
    }

    public void adjustRelativeBase(int value) {
        relativeBase += value;
    }

    static class MissingInputException extends RuntimeException {
        public MissingInputException() {
            super("No inputs available");
        }
    }

}

