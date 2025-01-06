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

    Program(String line) {
        initialMemory = Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        reset();
    }


    public void reset() {
        memory = new ArrayList<>(initialMemory);
        instructionPointer = 0;
        inputs.clear();
        outputs.clear();
    }

    public void execute() {
        while (true) {
            Instruction instruction = new Instruction(memory.subList(instructionPointer, memory.size()));
            if (instruction.isTerminate()) break;
            instruction.execute(this);
        }
    }

    public int getMemory(int location) {
        if (location < 0 || location >= memory.size()) throw new RuntimeException("Memory address out of bounds : " + location);
        return memory.get(location);
    }

    public void setMemory(int location, int value) {
        if (location < 0 || location >= memory.size()) throw new RuntimeException("Memory address out of bounds : " + location);
        memory.remove(location);
        memory.add(location, value);
    }

    public int read() {
        if (inputs.isEmpty()) throw new RuntimeException("No inputs available");
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
}
