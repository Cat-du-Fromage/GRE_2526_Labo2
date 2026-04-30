package ch.heig.gre.labo2.groupQ;

import ch.heig.gre.labo2.graph.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        /*
        WeightedDigraph graph = WeightedDigraphReader.fromFile("data/geneve_big_neg.txt");
        SPFA algo = new SPFA();
        SSSPResult result = algo.compute(graph, 0);
        System.out.println(result);
        */
        Test();
    }

    private static void Test() throws IOException
    {
        File dataDir = new File("data");
        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("Aucun fichier .txt trouvé dans le dossier data/");
            return;
        }

        Arrays.sort(files); // ordre alphabétique

        SSSPAlgorithm[] algos     = {new SPFA()/*, new SPFAwithSLF()*/};
        String[]        algoNames = {"SPFA-FIFO"/*, "SPFA-SLF"*/};

        for (File file : files) {
            System.out.println("=".repeat(60));
            System.out.println("Réseau : " + file.getName());
            System.out.println("=".repeat(60));

            WeightedDigraph graph = WeightedDigraphReader.fromFile(file.getPath());
            int n = graph.getNVertices();
            System.out.println("Sommets : " + n);

            for (int a = 0; a < algos.length; a++) {
                System.out.println("\n--- " + algoNames[a] + " ---");

                long startTime = System.nanoTime();
                SSSPResult result = algos[a].compute(graph, 0);
                long elapsed = System.nanoTime() - startTime;

                System.out.printf("Temps d'exécution : %.3f ms%n", elapsed / 1_000_000.0);

                if (result.isNegativeCycle()) {
                    System.out.println(result);
                } else {
                    if (n < 25) {
                        System.out.println(result);
                    } else {
                        System.out.println("Arborescence de plus courts chemins trouvée.");
                    }
                }
            }
            System.out.println();
        }
    }
}