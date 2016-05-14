package Alg;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Read from command line
        // Scanner scanner = new Scanner(System.in);

        // Reaf from file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("instances/001.graph"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Multigraph<String, DefaultEdge> graph = InputReader.readGraph(scanner);
        System.out.println(graph);
    }
}
