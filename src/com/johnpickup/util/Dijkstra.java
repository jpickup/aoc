package com.johnpickup.util;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Dijkstra<State> {

  protected abstract Set<State> allStates();
  protected abstract State initialState();
  protected abstract State targetState();
  protected abstract long calculateCost(State fromState, State toState);
  protected abstract boolean statesAreConnected(State toState, State fromState);
  protected abstract boolean findAllRoutes();

  /**
   * To have more than one target state override this method
   */
  protected boolean isTargetState(State state) {
    return state.equals(targetState());
  }

  private Map<State, Set<List<State>>> paths = null;
  @Getter
  private Map<State, Long> costs = null;

  public long lowestCost() {
    if (costs == null) generatePaths();
    return costs.entrySet().stream()
            .filter(e -> isTargetState(e.getKey()))
            .map(Map.Entry::getValue).min(Long::compareTo)
            .orElseThrow(() -> new RuntimeException("No solution"));
  }

  public Set<List<State>> findRoutes() {
    if (paths == null) generatePaths();
    return paths.entrySet().stream()
            .filter(e -> isTargetState(e.getKey()))
            .map(Map.Entry::getValue)
            .reduce(Collections.emptySet(), Sets::union);
  }

  private void generatePaths() {
    Map<State, Long> unvisited = new HashMap<>();
    Map<State, Long> visited = new HashMap<>();
    costs = new HashMap<>();
    paths = new HashMap<>();
    Set<State> spaces = allStates();
    for (State space : spaces) {
      unvisited.put(space, Long.MAX_VALUE);
    }
    unvisited.put(initialState(), 0L);
    paths.put(initialState(), Collections.singleton(Collections.emptyList()));

    Map.Entry<State, Long> lowestCostEntry = findSmallest(unvisited);

    while (lowestCostEntry != null && lowestCostEntry.getValue() < Long.MAX_VALUE) {
      Map<State, Long> neighbours = findNeighbours(unvisited, lowestCostEntry.getKey());
      for (Map.Entry<State, Long> neighbour : neighbours.entrySet()) {
        long cost = lowestCostEntry.getValue() + calculateCost(lowestCostEntry.getKey(), neighbour.getKey());
        if (cost < neighbour.getValue() || (findAllRoutes() && cost == neighbour.getValue()) ) {
          Set<List<State>> possiblePathsToState = paths.get(lowestCostEntry.getKey());
          Set<List<State>> existingPathsToState = Optional.ofNullable(paths.get(neighbour.getKey())).orElse(new HashSet<>());
          for (List<State> possiblePathToState : possiblePathsToState) {
            List<State> newPathToState = appendToPath(possiblePathToState, neighbour.getKey());
            existingPathsToState.add(newPathToState);
          }
          unvisited.put(neighbour.getKey(), cost);
          paths.put(neighbour.getKey(), existingPathsToState);
          costs.put(neighbour.getKey(), cost);
        }
        visited.put(lowestCostEntry.getKey(), lowestCostEntry.getValue());
      }
      unvisited.remove(lowestCostEntry.getKey());
      lowestCostEntry = findSmallest(unvisited);
    }
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
