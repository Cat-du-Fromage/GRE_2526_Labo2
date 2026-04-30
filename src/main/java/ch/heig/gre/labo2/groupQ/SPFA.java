package ch.heig.gre.labo2.groupQ;

import ch.heig.gre.labo2.graph.SSSPAlgorithm;
import ch.heig.gre.labo2.graph.SSSPResult;
import ch.heig.gre.labo2.graph.WeightedDigraph;

import java.util.*;

public class SPFA implements SSSPAlgorithm {

  private Stats stats;

  /**
   * Stats of the SPFA algorithm execution.
   * @param nbrRemovedFromQueue Le nombre total de sommets retirés de la file
   * @param nbrExaminedArcs Le nombre d’arcs examinés
   * @param nbrRelaxations Le nombre de relaxations réussies
   * @param nbrEnqueues  Le nombre de mises en file
   * @param execTimeInMs Temps d’exécution total
   */
  public record Stats(int nbrRemovedFromQueue,
                      int nbrExaminedArcs,
                      int nbrRelaxations,
                      int nbrEnqueues,
                      long execTimeInMs) {}

  public Stats getStats() {return stats;}

  @Override
  public SSSPResult compute(WeightedDigraph graph, int from) {

    final int n = graph.getNVertices();
    // On divise par 2 pour éviter un potentiel overflow
    final int INFINITY = Integer.MAX_VALUE / 2;
    int[] distance = new int[n];
    int[] predecessor = new int[n];
    int[] count = new int[n];
    boolean[] isInQueue = new boolean[n];
    // Poids de l'arc qui a permis d'atteindre le sommet
    int[] arcWeight = new int[n];

    // Toutes les distances à l'infini
    Arrays.fill(distance, INFINITY);
    // Tous les prédécesseurs à -1
    Arrays.fill(predecessor, SSSPResult.UNREACHABLE);
    // Sommet de départ à 0
    distance[from] = 0;
    // File FIFO
    Deque<Integer> queue = new ArrayDeque<>();
    queue.add(from);
    isInQueue[from] = true;
    count[from]++;

    int removed = 0;
    int exminedArcs = 0;
    int relaxations = 0;
    int enqueues = 1;

    long start = System.nanoTime();

    while (!queue.isEmpty()) {
      // Rtirer le premier élément de la queue (FIFO)
      int u = queue.poll();
      isInQueue[u] = false;
      removed++;

      // Pour chaque arc (u, v) avec poids c(u, v)
      for (WeightedDigraph.Edge edge : graph.getOutgoingEdges(u)) {
        exminedArcs++;
        int v = edge.to();
        int weight = edge.weight();

        if (distance[v] > distance[u] + weight /*&& distance[u] != INFINITY*/) {
          distance[v] = distance[u] + weight;
          predecessor[v] = u;
          arcWeight[v] = weight;
          relaxations++;

          if (!isInQueue[v]) {
            // FIFO donc on ajoute à la fin de la queue
            queue.addLast(v);
            isInQueue[v] = true;
            enqueues++;
            count[v]++;

            if (count[v] >= n) {
              long elapsedTime = System.nanoTime() - start;
              stats = new Stats(removed, exminedArcs, relaxations, enqueues, elapsedTime);
              List<Integer> cycle = buildNegativeCycle(predecessor, v, n);
              int totalWeight = getCycleWeigt(cycle, arcWeight);

              // Retourner un circuit absorbant accessible depuis s détecté
              return new SSSPResult.NegativeCycle(cycle, totalWeight);
            }
          }
        }
      }
    }

    long elapsedTime = System.nanoTime() - start;
    stats = new Stats(removed, exminedArcs, relaxations, enqueues, elapsedTime);

    // Retourner l'arbre des plus courts chemins de source -> from
    return new SSSPResult.ShortestPathTree(from, distance, predecessor);
  }

  /**
   * Calcule le poids total d'un cycle à partir de la liste de ses sommets et du tableau des poids des arcs.
   * @param cycle Liste des sommets formant le cycle, dans l'ordre du cycle
   * @param arcWeight Tableau des poids des arcs qui ont permis d'atteindre chaque sommet du cycle
   * @return Le poids total du cycle
   */
  private int getCycleWeigt(List<Integer> cycle, int[] arcWeight) {
    int total = 0;

    // On commence à 1 pour ne pas compter le poids de l'arc qui arrive au premier sommet du cycle
    for (int i = 1; i < cycle.size(); i++)
      // On ajoute le poids de l'arc qui a permis d'atteindre le sommet du cycle
      total += arcWeight[cycle.get(i)];

    return total;
  }

  /**
   * Construit un cycle de poids négatif à partir du tableau des prédécesseurs.
   * @param pred Tableau des prédécesseurs de chaque sommet dans l'arbre des plus courts chemins
   * @param start Sommet à partir duquel on a détecté le cycle de poids négatif
   *              (le sommet qui a été inséré dans la queue n fois)
   * @param n Nombre de sommets dans le graphe
   * @return Une liste de sommets formant un cycle de poids négatif accessible depuis le sommet
   * de départ, dans l'ordre du cycle (le premier sommet est répété à la fin pour fermer le cycle)
   */
  private List<Integer> buildNegativeCycle(int[] pred, int start, int n) {
    // Sommet qui appartient à un cycle en remontant les predécesseurs à partir de start
    // Comme on a inséré un sommet n fois, il y a aun moins un cycle parmis les predécesseurs
    boolean[] visited = new boolean[n];
    int current = start;

    // On remonte les précdecesseurs jusqu'à trouver un sommet déjà visité
    // Signifie qu'on a trouvé un cycle
    while (!visited[current]) {
      visited[current] = true;
      current = pred[current];
    }

    // Premier sommet visité deux fois
    int firstVertice = current;
    List<Integer> cycle = new ArrayList<>();
    cycle.add(firstVertice);
    // On parcourt le circuit une première fois pour collecter ses sommet
    // On acance sur les predécesseurs jusqu'à retrouver le premier sommet du cycle
    for (int v = pred[firstVertice]; v != firstVertice; v = pred[v])
      cycle.add(v);

    // On ajoute le premier cycle à la fin pour terminer le cycle
    cycle.add(firstVertice);

    // On remonte les predécesseurs donc on a la séquence dans l'ordre inverse
    Collections.reverse(cycle);
    return cycle;
  }
}