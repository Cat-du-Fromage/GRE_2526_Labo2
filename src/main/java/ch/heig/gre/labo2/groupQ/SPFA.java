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
    Arrays.fill(distances, Integer.MAX_VALUE);
    Arrays.fill(predecessors, -1);
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
            addedCount[dest] += 1;
            //Circuit absorbant
            if(addedCount[dest] >= verticesCount){
              return negativeCircuit(predecessors, dest);
            }
          }
        }
      }
    }
    return new SSSPResult.ShortestPathTree(from, distances, predecessors);
  }

  SSSPResult negativeCircuit(int[] predecessors, int source) {
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
}