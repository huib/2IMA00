package Alg;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.Stack;
import java.util.Arrays;
import java.util.Scanner;

import Alg.Kernelization.ReductionSolution;
import Alg.Kernelization.Kernelization;

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
    public static Multigraph<Integer, DefaultEdge> readGraph(Scanner scanner){
        ReductionSolution solution = loopSafeReadGraph(scanner);
        return solution.reducedGraph;
    }

    /**
     * Read in the file, and return the reduction solution,
     * including graph with self looped vertices moved to removed vertices
     *
     * @param scanner
     * @return
     */
    public static ReductionSolution loopSafeReadGraph(Scanner scanner)
    {
        Multigraph<Integer, DefaultEdge> graph = new Multigraph<>(DefaultEdge.class);
        Stack<Integer> selfLoops = new Stack<>();

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

            int source = Integer.parseInt(vertices[0]);
            int target = Integer.parseInt(vertices[1]);

            // Document self-loops
            if(source == target) selfLoops.push(source);
            else graph.addEdge(source, target);
        }

        ReductionSolution solution = new ReductionSolution();
        solution.reducedGraph = graph;

        // Eliminate self-loops
        while (!selfLoops.isEmpty()) Kernelization.removeVertex(solution, selfLoops.pop(), true);

        return solution;
    }
}
