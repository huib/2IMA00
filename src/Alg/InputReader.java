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
        Multigraph<Integer, DefaultEdge> graph = new Multigraph<>(DefaultEdge.class);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();

            // Comments should not be read, Empty lines must be ignored
            boolean isComment = line.startsWith("#");
            boolean isEmpty = line.length() == 0;
            if (isComment || isEmpty) {
                continue;
            }

            String[] vertices = line.split(" ");
            Arrays.stream(vertices).forEach(s -> {
                int v = Integer.parseInt(s);
                if (!graph.containsVertex(v)) {
                    graph.addVertex(v);
                }
            });
            graph.addEdge(Integer.parseInt(vertices[0]), Integer.parseInt(vertices[1]));
        }

        return graph;
    }
}
