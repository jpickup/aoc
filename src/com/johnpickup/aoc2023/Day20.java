package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 {

  static Map<String, Module> moduleMap;

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day20.txt"))) {
      List<Module> modules = stream.filter(s -> !s.isEmpty()).map(Module::parse).collect(Collectors.toList());

      System.out.println(modules);
      moduleMap = modules.stream().collect(Collectors.toMap(m -> m.id, m -> m));

      long lowCount = 0L;
      long highCount = 0L;

      modules.forEach(Module::reset);

      List<Pulse> pulses = new ArrayList<>();
      for (int tick = 1; tick <= 1000; tick++) {
        System.out.println("---------- Tick "+ tick + " ----------");
        pulses.add(new Pulse("button", "broadcaster", false));

        while (!pulses.isEmpty()) {
          Pulse pulse = pulses.remove(0);
          System.out.println(pulse);

          if (pulse.value) {
            highCount++;
          } else {
            lowCount++;
          }
          Module target = moduleMap.get(pulse.destination);
          if (target!=null) {
            pulses.addAll(target.process(pulse));
          }
        }
        System.out.printf("Low: %d, High: %d%n", lowCount, highCount);
      }
      System.out.printf("Part 1: %d%n", highCount*lowCount);

      // 899579128 = too high

    } catch (IOException e) {
      e.printStackTrace();
    }
    long end = System.currentTimeMillis();
    System.out.println("Time: " + (end - start) + "ms");
  }

  @RequiredArgsConstructor
  @ToString
  static class Pulse {
    private final String source;
    private final String destination;
    private final boolean value;

    @Override
    public String toString() {
      return source
          + " -" + (value?"high":"low")
          + " -> " +
          destination;
    }
  }

  @RequiredArgsConstructor
  @ToString
  static abstract class Module {
    protected final String id;
    protected final List<String> targets;

    protected List<Pulse> pulseForTargets(boolean value) {
      return targets.stream().map(t -> new Pulse(this.id, t, value)).collect(Collectors.toList());
    }

    public abstract List<Pulse> process(Pulse input);

    public abstract void reset();

    public static Module parse(String s) {
      List<String> targets = Arrays.stream(s.split("->")[1].trim().split(",")).map(String::trim).collect(Collectors.toList());
      String id = s.split("->")[0].trim();
      switch(s.charAt(0)) {
      case '%': return new FlipFlop(id.substring(1), targets);
      case '&': return new Conjunction(id.substring(1), targets);
      default: return new Broadcaster(id, targets);
      }
    }
  }

  @ToString(callSuper = true)
  static class Broadcaster extends Module {
    public Broadcaster(String id, List<String> targets) {
      super(id, targets);
    }

    @Override
    public List<Pulse> process(Pulse input) {
      // send a low pulse to every target
      return pulseForTargets(false);
    }

    @Override
    public void reset() {
      // noop
    }

  }

  @ToString(callSuper = true)
  static class FlipFlop extends Module {
    boolean state = false;
    public FlipFlop(String id, List<String> targets) {
      super(id, targets);
    }

    @Override
    public void reset() {
      state = false;
    }

    @Override
    public List<Pulse> process(Pulse input) {
      if (!input.value) {
        state = !state;
        return pulseForTargets(state);
      }
      return Collections.emptyList();
    }
  }

  /**
   * Conjunction modules (prefix &) remember the type of the most recent pulse received from each of their connected input modules;
   * they initially default to remembering a low pulse for each input. When a pulse is received, the conjunction module first
   * updates its memory for that input. Then, if it remembers high pulses for all inputs, it sends a low pulse; otherwise,
   * it sends a high pulse.
   */
  @ToString(callSuper = true)
  static class Conjunction extends Module {
    private Map<String, Boolean> state = new HashMap<>();
    public Conjunction(String id, List<String> targets) {
      super(id, targets);
    }

    @Override
    public void reset() {
      state.clear();
      moduleMap.values().stream().filter(m -> m.targets.contains(this.id)).map(m -> m.id).forEach(source -> state.put(source, false));
    }
    @Override
    public List<Pulse> process(Pulse input) {
      state.put(input.source, input.value);
      boolean allTrue = state.values().stream().allMatch(v -> v);
      return pulseForTargets(!allTrue);
    }
  }
}
