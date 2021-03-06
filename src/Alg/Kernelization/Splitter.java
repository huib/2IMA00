package Alg.Kernelization;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that removes vertices not part of a cycle, and splits the remaining graph into subgraphs that can be solved independently
 */
public class Splitter {

    /**
     * Splits the graph into multiple graphs by deleting all edges that are not part of a cycle
     * @param graph
     * @return
     */
    public static List<Multigraph<Integer, DefaultEdge>> split(Multigraph<Integer, DefaultEdge> graph)
    {
        removeEdgesNotInCylce(graph);


        return splitGraph(graph);
    }

    /**
     * Remove all the edges from the graph that are not part of a cycle
     *
     * @param graph
     */
    public static void removeEdgesNotInCylce(Multigraph<Integer, DefaultEdge> graph)
    {
        Set<DefaultEdge> edgeSet = graph.edgeSet();
        DefaultEdge[] edgeArray = edgeSet.toArray(new DefaultEdge[edgeSet.size()]);
        for (DefaultEdge edge: edgeArray) {
            HashSet<Integer> done = new HashSet<>();
            done.add(graph.getEdgeSource(edge)); // Makes sure that we do not visit the source againrce(edge) + " -> " + graph.getEdgeTarget(edge));
            // Only works because our implementation does not visit nodes twice. So it won't visit the start node again
            // Furthermore, it does not go to the target node directly
            // So it only finds the target node if
            if (! Splitter.DFSRecursive(
                    graph.getEdgeTarget(edge),  // Find the target of the edge
                    graph,
                    graph.getEdgeSource(edge), // From the source of the edge
                    graph.getEdgeTarget(edge), // And we are not allowed to go back directly
                    done
            )) {
                graph.removeEdge(edge);
            }
        }

    }

    /**
     * Creates a new
     * @param graph
     */
    public static List<Multigraph<Integer, DefaultEdge>> splitGraph(Multigraph<Integer, DefaultEdge> graph)
    {
        List<Multigraph<Integer, DefaultEdge>> graphs = new ArrayList<>();
        // While the original graph still has vertices, we must split further
        while (graph.vertexSet().size() > 0) {
            // take one vertex at random, and create a new graph from this vertex;
            Multigraph<Integer, DefaultEdge> newGraph = new Multigraph<>(DefaultEdge.class);
            copySubGraph(graph, newGraph, graph.vertexSet().iterator().next());
            graphs.add(newGraph);
        }

        return graphs;
    }

    /**
     * Copies all the components of the original graph connected to vertex into the subgraph and removes them from the original graph
     *
     * @param originalGraph
     * @param newgraph New graph to create
     * @param vertex
     * @return
     */
    protected static void copySubGraph(
            Multigraph<Integer, DefaultEdge> originalGraph,
            Multigraph<Integer, DefaultEdge> newgraph,
            int vertex
    )  {
        // add vertex to new graph
        newgraph.addVertex(vertex);

        // Copy all the neighbours, and edges
        SimpleDisjointKernelization.getNeighbours(originalGraph, vertex)
                .collect(Collectors.toCollection(ArrayList::new))  // Collect to prevent concurrentmodificationexceptions
                .forEach(v -> {
                    // Only continue with our recursive copy if the vertex is not copied yet
                    if (!newgraph.containsVertex(v)) {
                        copySubGraph(originalGraph, newgraph, v);
                    }

                    // Make sure that edges are only copied once
                    if (!newgraph.containsEdge(vertex, v)) {
                        newgraph.addEdge(vertex, v);
                    }
                });

        // Remove the vertex from the original graph, and return new graph
        originalGraph.removeVertex(vertex);
    }


    /**
     * Recursive function to do a dfs for a node v from node currentVertex
     *
     * @param v target vertex we have to search
     * @param graph base graph
     * @param currentVertex current vertex we are searching at
     * @param lastVertex Last vertex, needed to ensure we do not go back the same way
     * @return
     */
    protected static boolean DFSRecursive(
            Integer v,
            Multigraph<Integer, DefaultEdge> graph,
            Integer currentVertex,
            Integer lastVertex,
            HashSet<Integer> done
    ) {
        Collection<Integer> neighbours = SimpleDisjointKernelization.getNeighbours(graph, currentVertex)
                .collect(Collectors.toCollection(ArrayList<Integer>::new));

        for (int vertex : neighbours) {
            // Do not go back the same way
            if (vertex == lastVertex) {
                continue;
            }

            // We found a cycle
            if (vertex == v) {
                return true;
            }

            // We have already checked this vertex.
            if (done.contains(vertex)) {
                continue;
            }

            done.add(vertex);
            if (Splitter.DFSRecursive(v, graph, vertex, currentVertex, done)) {
                return true;
            }
        }

        // We could not find
        return false;
    }
}
