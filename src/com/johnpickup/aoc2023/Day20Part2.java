package com.johnpickup.aoc2023;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20Part2 {
  static Map<String, Module> moduleMap;
  static Map<String, Map<Long, Long>> moduleFreq = new HashMap<>();
  static boolean debug = false;
  static long tick = 0;

  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    try (Stream<String> stream = Files.lines(Paths.get("c:/dev/aoc/resources/2023/Day20.txt"))) {
      List<Module> modules = stream.filter(s -> !s.isEmpty()).map(Module::parse).collect(Collectors.toList());

      System.out.println(modules);
      moduleMap = modules.stream().collect(Collectors.toMap(m -> m.id, m -> m));
      modules.forEach(m -> moduleFreq.put(m.id, null));

      long lowCount = 0L;
      long highCount = 0L;

      modules.forEach(Module::reset);
      long part2 = 0;

      List<Pulse> pulses = new ArrayList<>();

      while (part2==0) {
        tick++;
        if (++tick%1000000 == 0) System.out.println(tick + " in " + (System.currentTimeMillis() - start)/1000L + "s");

        if (debug) System.out.println("---------- Tick "+ tick + " ----------");
        pulses.add(new Pulse("button", "broadcaster", false));

        while (!pulses.isEmpty()) {
          Pulse pulse = pulses.remove(0);
          if (debug) System.out.println(pulse);

          if (pulse.value) {
            highCount++;
          } else {
            lowCount++;
          }
          Module target = moduleMap.get(pulse.destination);
          if (target!=null) {
            pulses.addAll(target.process(pulse));
          } else {
            if (pulse.destination.equals("rx") && !pulse.value && part2==0) {
              part2 = tick;
            }
          }
        }
        if (debug) System.out.printf("Low: %d, High: %d%n", lowCount, highCount);
      }
      System.out.printf("Part 1: %d%n", highCount*lowCount);
      System.out.printf("Part 2: %d%n", part2);
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

    long lastLow = 0;
    long lastHigh = 0;
    boolean lastValue = false;

    protected List<Pulse> pulseForTargets(boolean value) {
      if (value != lastValue) {
        lastValue = value;
        // transition, record the tick
        if (value)
          lastHigh = tick;
        else
          lastLow = tick;

        if (lastHigh > 0 && lastLow > 0) {
          int knownBefore = moduleFreq.values().stream().filter(Objects::nonNull).map(Map::size).reduce(0, Integer::sum);

          long period = Math.abs(lastHigh - lastLow);

          moduleFreq.computeIfAbsent(id, k -> new HashMap<>());
          moduleFreq.get(id).putIfAbsent(period, 0L);
          moduleFreq.get(id).put(period, moduleFreq.get(id).get(period)+1L);

          int knownAfter = moduleFreq.values().stream().filter(Objects::nonNull).map(Map::size).reduce(0, Integer::sum);

          if (knownAfter > knownBefore) {
            System.out.printf("%d known frequencies:%n", knownAfter);
            moduleFreq.forEach((id, map) -> System.out.println(id + " => " + map));
          }
        }
      }
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
