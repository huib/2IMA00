package Alg;

import Alg.Algorithms.Randomized;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Read from command line
        // Scanner scanner = new Scanner(System.in);

        // Read from file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("instances/001.graph"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Multigraph<Integer, DefaultEdge> graph = InputReader.readGraph(scanner);

       // GraphDisplayer.display(graph);

        FVSAlgorithmInterface randomized = new Randomized();

        Integer[] solution = randomized.findFeedbackVertexSet(graph);


        for (Integer s: solution) {
            System.out.print(s + ", ");
        }


    }
}
