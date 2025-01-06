package com.johnpickup.aoc2019;

import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ToString
class Program {
    final List<Integer> initialMemory;
    List<Integer> memory;
    int instructionPointer;
    List<Integer> inputs = new ArrayList<>();
    List<Integer> outputs = new ArrayList<>();
    boolean terminated;

    Program(String line) {
        initialMemory = Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        reset();
    }


    public void reset() {
        memory = new ArrayList<>(initialMemory);
        instructionPointer = 0;
        terminated = false;
        inputs.clear();
        outputs.clear();
    }

    public void execute() {
        while (true) {
            Instruction instruction = new Instruction(memory.subList(instructionPointer, memory.size()));
            if (instruction.isTerminate()) {
                terminated = true;
                break;
            }
            instruction.execute(this);
        }
    }

    public int getMemory(int location) {
        if (location < 0 || location >= memory.size()) throw new MemoryException("Memory address out of bounds : " + location);
        return memory.get(location);
    }

    public void setMemory(int location, int value) {
        if (location < 0 || location >= memory.size()) throw new MemoryException("Memory address out of bounds : " + location);
        memory.remove(location);
        memory.add(location, value);
    }

    public int read() {
        if (inputs.isEmpty()) throw new MissingInputException();
        int result = inputs.get(0);
        inputs.remove(0);
        return result;
    }

    public void write(int value) {
        outputs.add(value);
    }

    public String showOutput() {
        return outputs.stream().map(i -> Integer.toString(i)).collect(Collectors.joining(","));
    }

    public void addInput(int value) {
        inputs.add(value);
    }

    public int getOutput(int index) {
        if (index < 0 || index >= outputs.size()) throw new RuntimeException("No output with index " + index);
        return outputs.get(index);
    }

    public void moveInstructionPointer(int moveBy) {
        instructionPointer += moveBy;
    }

    public void setInstructionPointer(int value) {
        instructionPointer = value;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public List<Integer> consumeOutputs() {
        ArrayList<Integer> currentOutputs = new ArrayList<>(outputs);
        outputs.clear();
        return currentOutputs;
    }

    static class MissingInputException extends RuntimeException {
        public MissingInputException() {
            super("No inputs available");
        }
    }

    static class MemoryException extends RuntimeException {
        public MemoryException(String error) {
            super(error);
        }
    }

}

