package com.johnpickup.aoc2024.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Dijkstra<State> {

  protected abstract Set<State> allStates();
  protected abstract State initialState();
  protected abstract State targetState();
  protected abstract long calculateCost(State fromState, State toState);
  protected abstract boolean statesAreConnected(State state1, State state2);
  protected abstract boolean findAllRoutes();

  public Set<List<State>> findRoutes() {
    Map<State, Long> unvisited = new HashMap<>();
    Map<State, Long> visited = new HashMap<>();
    Map<State, Set<List<State>>> paths = new HashMap<>();
    Set<State> spaces = allStates();
    for (State space : spaces) {
      unvisited.put(space, Long.MAX_VALUE);
    }
    unvisited.put(initialState(), 0L);
    paths.put(initialState(), Collections.singleton(Collections.emptyList()));

    Map.Entry<State, Long> lowestCostEntry = findSmallest(unvisited);

    while (lowestCostEntry != null && lowestCostEntry.getValue() < Long.MAX_VALUE) {
      Map<State, Long> neighbours = findNeighbours(unvisited, lowestCostEntry.getKey());
      for (Map.Entry<State, Long> entry : neighbours.entrySet()) {
        long cost = lowestCostEntry.getValue() + calculateCost(lowestCostEntry.getKey(), entry.getKey());
        if (cost < entry.getValue() || (findAllRoutes() && cost == entry.getValue()) ) {
          Set<List<State>> possiblePathsToState = paths.get(lowestCostEntry.getKey());
          Set<List<State>> existingPathsToState = Optional.ofNullable(paths.get(entry.getKey())).orElse(new HashSet<>());
          for (List<State> possiblePathToState : possiblePathsToState) {
            List<State> newPathToState = appendToPath(possiblePathToState, entry.getKey());
            existingPathsToState.add(newPathToState);
          }
          unvisited.put(entry.getKey(), cost);
          paths.put(entry.getKey(), existingPathsToState);
        }
        visited.put(lowestCostEntry.getKey(), lowestCostEntry.getValue());
      }
      unvisited.remove(lowestCostEntry.getKey());
      lowestCostEntry = findSmallest(unvisited);
    }

    return Optional.ofNullable(paths.get(targetState())).orElse(Collections.emptySet());
  }

  private Map<State, Long> findNeighbours(Map<State, Long> unvisited, State key) {
    return unvisited.entrySet().stream().filter(e ->  statesAreConnected(e.getKey(), key))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map.Entry<State, Long> findSmallest(Map<State, Long> nodes) {
    Map.Entry<State, Long> smallest = null;
    for (Map.Entry<State, Long> entry : nodes.entrySet()) {
      if (entry.getValue() < Optional.ofNullable(smallest).map(Map.Entry::getValue).orElse(Long.MAX_VALUE)) {
        smallest = entry;
      }
    }
    return smallest;
  }

  private List<State> appendToPath(List<State> path, State newState) {
    List<State> result = new ArrayList<>(path);
    result.add(newState);
    return result;
  }
}
