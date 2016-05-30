package Alg;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Class with logic to read in the data and produces a Graph suitable for the Feedback Vertex Set problem
 */
public class InputReader {

    /**
     * Read in the file, and return the graph
     *
     * @param scanner
     * @return
     */
    public static Multigraph<Integer, DefaultEdge> readGraph(Scanner scanner)
    {
        Multigraph<Integer, DefaultEdge> graph = new Multigraph<Integer, DefaultEdge>(DefaultEdge.class);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();

            // Comments should not be read, Empty lines must be ignored
            boolean isComment = line.startsWith("#");
            boolean isEmpty = line.length() == 0;
            if (isComment || isEmpty) {
                continue;
            }

            String[] vertices = line.split(" ");
            int[] intArray = new int[vertices.length];
            for(int i = 0; i < vertices.length; i++) { // str to int
                intArray[i] = Integer.parseInt( vertices[i] );
            }

            Arrays.stream(intArray).forEach(s -> {
                if (!graph.containsVertex(s)) {
                    graph.addVertex(s);
                }
            });
            graph.addEdge(intArray[0], intArray[1]);
        }

        return graph;
    }
}
