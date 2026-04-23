package ch.heig.gre.labo2.groupQ;

import ch.heig.gre.labo2.graph.*;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        WeightedDigraph graph = WeightedDigraphReader.fromFile("data/reseau4.txt");
        SPFA algo = new SPFA();
        SSSPResult result = algo.compute(graph, 0);

        System.out.println(result);
    }
}