package Alg;


import jdk.internal.util.xml.impl.Input;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.*;

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
        ReductionSolution solution = loopSafeReadGraph(scanner).reductionSolution;
        return solution.reducedGraph;
    }

    /**
     * Read in the file, and return the reduction solution,
     * including graph with self looped vertices moved to removed vertices
     *
     * @param scanner
     * @return
     */
    public static InputWrapper loopSafeReadGraph(Scanner scanner)
    {
        Multigraph<Integer, DefaultEdge> graph = new Multigraph<>(DefaultEdge.class);
        Stack<Integer> selfLoops = new Stack<>();
        HashMap<String, Integer> reverseMapping = new HashMap<>();

        int counter = 0; // Counter for marking the vertices

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
                //intArray[i] = Integer.parseInt( vertices[i] );
                Integer mapped = reverseMapping.get(vertices[i]);
                if (mapped == null) {
                    reverseMapping.put(vertices[i], counter);
                    intArray[i] = counter;
                    counter++;
                } else {
                    intArray[i] = mapped;
                }
            }

            Arrays.stream(intArray).forEach(s -> {
                if (!graph.containsVertex(s)) {
                    graph.addVertex(s);
                }
            });

            int source = intArray[0];
            int target = intArray[1];

            // Document self-loops
            if(source == target) selfLoops.push(source);
            else graph.addEdge(source, target);
        }

        // Construct actual mapping from reverse mapping
        HashMap<Integer, String> mapping = new HashMap<>();
        Set<String> keys = reverseMapping.keySet();
        for (String s: keys){
            mapping.put(reverseMapping.get(s), s);
        }

        InputWrapper wrapper = new InputWrapper();
        wrapper.reductionSolution = new ReductionSolution();
        wrapper.reductionSolution.reducedGraph = graph;
        wrapper.nameMapping = mapping;

        // Eliminate self-loops
        while (!selfLoops.isEmpty()) Kernelization.removeVertex(wrapper.reductionSolution, selfLoops.pop(), true);

        return wrapper;
    }
}
