package ch.heig.gre.labo2.graph;

/**
 * Useless interface to force compliance with the method signature.
 */
public interface SSSPAlgorithm {
  SSSPResult compute(WeightedDigraph graph, int from);
}
