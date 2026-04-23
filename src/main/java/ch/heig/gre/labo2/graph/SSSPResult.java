package ch.heig.gre.labo2.graph;

import java.util.Arrays;
import java.util.List;

/**
 * Result type for the Single Source Shortest Path (SSSP) algorithm.
 * <p>
 * A result is either:
 * <ul>
 *   <li>a {@link ShortestPathTree}, when no negative cycle is reachable from the source vertex;</li>
 *   <li>a {@link NegativeCycle}, when a negative cycle is reachable from the source vertex.</li>
 * </ul>
 */
public sealed interface SSSPResult {
  /**
   * Value used to represent an unreachable vertex in the shortest path tree.
   */
  int UNREACHABLE = -1;

  /**
   * @return {@code true} if the result is a {@link NegativeCycle}, {@code false} otherwise.
   */
  boolean isNegativeCycle();

  /**
   * @return The {@link ShortestPathTree} if the result is a {@link ShortestPathTree}, {@code null} otherwise.
   */
  default ShortestPathTree getShortestPathTree() {
    return null;
  }

  /**
   * @return The {@link NegativeCycle} if the result is a {@link NegativeCycle}, {@code null} otherwise.
   */
  default NegativeCycle getNegativeCycle() {
    return null;
  }

  /**
   * SSSP result as the shortest path tree rooted at the source vertex.
   *
   * @param source       Source vertex of the tree.
   *                     {@link Integer#MAX_VALUE} if the vertex is unreachable.
   * @param distances    Distance from the source vertex to each reachable vertex in the graph.
   *                     {@link Integer#MAX_VALUE} if the vertex is unreachable.
   * @param predecessors Predecessor of each reachable vertex in the shortest path tree.
   *                     {@link SSSPResult#UNREACHABLE} if the vertex is unreachable.
   */
  record ShortestPathTree(int source, int[] distances, int[] predecessors) implements SSSPResult {
    @Override
    public boolean isNegativeCycle() {
      return false;
    }

    @Override
    public ShortestPathTree getShortestPathTree() {
      return this;
    }

    @Override
    public String toString() {
      return "ShortestPathTree{\n" +
          " source=" + source + ",\n distances=   " + Arrays.toString(distances) +
          ",\n predecessors=" + Arrays.toString(predecessors) +
          "\n}";
    }
  }

  /**
   * SSSP result as the first negative cycle reachable from the source vertex.
   * <p>
   * There are no guarantees on the implementation type of the {@code values} List.
   *
   * @param values List of vertices in the first negative cycle reachable from the source vertex.
   * @param length Total length of the negative cycle.
   */
  record NegativeCycle(List<Integer> values, int length) implements SSSPResult {
    @Override
    public boolean isNegativeCycle() {
      return true;
    }

    @Override
    public NegativeCycle getNegativeCycle() {
      return this;
    }

    @Override
    public String toString() {
      return "NegativeCycle of length " + length + " found: " + values;
    }
  }
}
