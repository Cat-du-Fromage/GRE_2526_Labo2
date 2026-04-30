package ch.heig.gre.labo2;

import ch.heig.gre.labo2.graph.SSSPResult;
import ch.heig.gre.labo2.graph.SSSPResult.NegativeCycle;
import ch.heig.gre.labo2.graph.SSSPResult.ShortestPathTree;
import ch.heig.gre.labo2.graph.WeightedDigraph;
import ch.heig.gre.labo2.graph.WeightedDigraphReader;
import ch.heig.gre.labo2.groupQ.SPFA;
//import ch.heig.gre.labo2.groupQ.SPFASLF;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        String dataDir = "data";

        File dir = new File(dataDir);
        if(!dir.exists() || !dir.isDirectory()) {
            System.err.println("Le répertoire " + dataDir + " n'existe pas.");
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if(files == null || files.length == 0) {
            System.err.println("Aucun fichier .txt trouvé dans le répertoire " + dataDir);
            return;
        }

        Arrays.sort(files, Comparator.comparing(File::getName));

        for (File file : files) {
            System.out.println("\nRéseau : " + file.getName());
            System.out.println("-----------------------------");

            WeightedDigraph graph;
            try {
                graph = WeightedDigraphReader.fromFile(file.getPath());
            } catch (IOException e) {
                System.err.println("Erreur de lecture de " + file.getName() + ": " + e.getMessage());
                continue;
            }

            int source = 0;

            System.out.println("SPFA FIFO");
            runAlgo(graph, source, new SPFA());

            /*System.out.println("SPFA SLF");
            runAlgo(graph, source, new SPFASLF());*/
        }
    }

    private static void runAlgo(WeightedDigraph graph, int source, SPFA algo) {
        SSSPResult result = algo.compute(graph, source);
        SPFA.Stats stats = algo.getStats();

        System.out.println("Stats :");
        System.out.println("- Sommets retirés de la file : " + stats.nbrRemovedFromQueue());
        System.out.println("- Arcs examinés              : " + stats.nbrExaminedArcs());
        System.out.println("- Relaxations réussies       : " + stats.nbrRelaxations());
        System.out.println("- Mise en file               : " + stats.nbrEnqueues());
        System.out.printf("Temps d'exéc : %.2f ms%n", stats.execTimeInMs() / 1_000_000.0);

        int n = graph.getNVertices();
        if (result.isNegativeCycle()) {
            NegativeCycle negativeCycle = result.getNegativeCycle();
            System.out.println("Cycle de poids négatif détecté : ");
            System.out.println("- Poids total       : " + negativeCycle.length());
            System.out.println("- Cycle des sommets : " + negativeCycle.values());
        } else {
            ShortestPathTree shortestPathTree = result.getShortestPathTree();
            if (n < 25) {
                System.out.println("Arbre des plus courts chemins de source -> " + shortestPathTree.source() + " : ");
                System.out.print("- Distances:      [");
                for (int i = 0; i < shortestPathTree.distances().length; i++) {
                    if (i > 0) System.out.print(", ");
                    int d = shortestPathTree.distances()[i];
                    System.out.print(d == Integer.MAX_VALUE ? "infini" : d);
                }
                System.out.println("]");
                System.out.print("- Predecesseurs:  [");
                for (int i = 0; i < shortestPathTree.predecessors().length; i++) {
                    if (i > 0) System.out.print(", ");
                    int p = shortestPathTree.predecessors()[i];
                    System.out.print(p == -1 ? "null" : p);
                }
                System.out.println("]");
            } else {
                System.out.println("Arbre de plus court chemin trouvé (graphe >= 25 sommets)");
            }
        }
    }
}