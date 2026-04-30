package ch.heig.gre.labo2.groupQ;

import ch.heig.gre.labo2.graph.SSSPAlgorithm;
import ch.heig.gre.labo2.graph.SSSPResult;
import ch.heig.gre.labo2.graph.WeightedDigraph;

import java.util.*;

/**
 * Shortest Path Faster Algorithm (SPFA).
 */
public class SPFA implements SSSPAlgorithm {


  @Override
  public SSSPResult compute(WeightedDigraph graph, int from) {

    Deque<Integer> queue = new ArrayDeque<>();

    int verticesCount = graph.getNVertices();
    int[] distances = new int[verticesCount];
    int[] predecessors = new int[verticesCount];
    int[] addedCount = new int[verticesCount];
    boolean[] inQueue = new boolean[verticesCount];
    Arrays.fill(distances, Integer.MAX_VALUE);
    Arrays.fill(predecessors, SSSPResult.UNREACHABLE);

    distances[from] = 0;
    queue.add(from);
    addedCount[from] = 1;
    inQueue[from] = true;

    while (!queue.isEmpty()) {
      int current = queue.poll();
      inQueue[current] = false;

      for (WeightedDigraph.Edge edge : graph.getOutgoingEdges(current)) {
        int dest = edge.to();
        int weight = edge.weight();

        if (distances[current] == Integer.MAX_VALUE) continue; // garde overflow

        if (distances[dest] > distances[current] + weight) {
          distances[dest] = distances[current] + weight;
          predecessors[dest] = current;

          if (!inQueue[dest]) {
            queue.add(dest);
            inQueue[dest] = true;
            if (addedCount[dest]++ >= verticesCount) {
              return negativeCircuit(predecessors, dest);
            }
          }
        }
      }
    }
    return new SSSPResult.ShortestPathTree(from, distances, predecessors);

    /*
    Deque<Integer> queue = new ArrayDeque<>();

    int verticesCount = graph.getNVertices();
    int[] distances = new int[verticesCount];
    int[] predecessors = new int[verticesCount];
    int[] addedCount = new int[verticesCount];
    Arrays.fill(distances, Integer.MAX_VALUE);
    Arrays.fill(predecessors, SSSPResult.UNREACHABLE);
    Arrays.fill(addedCount, 0);

    distances[from] = 0;
    queue.add(from);
    addedCount[from] = 1;

    while (!queue.isEmpty()) {
      int current = queue.poll();
      for (WeightedDigraph.Edge edge : graph.getOutgoingEdges(current))
      {
        //int source = edge.from(); // must be equal to "current"
        int dest = edge.to();
        int weight = edge.weight();
        if(distances[dest] == Integer.MAX_VALUE || distances[dest] > distances[current] + weight) {
          distances[dest] = distances[current] + weight;
          predecessors[dest] = current;
          if(!queue.contains(dest)) {
            queue.add(dest);
            //addedCount[dest] += 1;
            //Circuit absorbant
            if(addedCount[dest]++ >= verticesCount){
              return negativeCircuit(predecessors, dest);
            }
          }
        }
      }
    }
    return new SSSPResult.ShortestPathTree(from, distances, predecessors);
    */
  }
  /*
  SSSPResult negativeCircuit(int[] predecessors, int source) {
    System.out.println("negativeCircuit at " + source);
    List<Integer> cycle = new ArrayList<>();
    int current = source;
    do {
      cycle.add(current);
      current = predecessors[current];
    } while (current != source);
    cycle.add(source);
    Collections.reverse(cycle);
    return new SSSPResult.NegativeCycle(cycle, cycle.size());
  }
  */


  SSSPResult negativeCircuit(int[] predecessors, int source) {
    System.out.println("negativeCircuit at " + source);
    int current = source;
    int n = predecessors.length;
    for (int i = 0; i < n; i++) {
      current = predecessors[current];
      if (current == SSSPResult.UNREACHABLE) {
        throw new IllegalStateException("Prédécesseur UNREACHABLE pendant la remontée !");
      }
    }

    // 2. Parcourir le cycle depuis ce nœud garanti dedans
    List<Integer> cycle = new ArrayList<>();
    int cycleStart = current;
    do {
      cycle.add(current);
      current = predecessors[current];
    } while (current != cycleStart);
    cycle.add(cycleStart); // fermer le cycle

    Collections.reverse(cycle);
    return new SSSPResult.NegativeCycle(cycle, cycle.size());
  }

  /*
  SSSPResult negativeCircuit(int[] predecessors, int source) {
    int current = source;
    for (int i = 0; i < predecessors.length; i++) {
      current = predecessors[current];
    }

    System.out.println("cycleStart = " + current);

    // Affiche la chaîne de prédécesseurs depuis cycleStart
    int debug = current;
    System.out.print("Predecessors chain: " + debug);
    for (int i = 0; i < 10; i++) {
      debug = predecessors[debug];
      System.out.print(" -> " + debug);
    }
    System.out.println();

    List<Integer> cycle = new ArrayList<>();
    int cycleStart = current;
    do {
      cycle.add(current);
      current = predecessors[current];
    } while (current != cycleStart);
    cycle.add(cycleStart);

    System.out.println("Before reverse: " + cycle);
    Collections.reverse(cycle);
    System.out.println("After reverse:  " + cycle);

    return new SSSPResult.NegativeCycle(cycle, cycle.size());
  }
  */

}