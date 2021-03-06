package Alg.Lib;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import javax.print.DocFlavor;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Stefan on 5/22/2016.
 */
public class CycleDetector {

    /**
     * Detect if a graph has a cycle or not
     *
     * @param graph
     * @return
     */
    public static boolean hasCycle(Multigraph<Integer, DefaultEdge> graph)
    {
        HashSet<Integer> foundVertices = new HashSet<>();
        for (Integer vertex: graph.vertexSet()) {
            if (!foundVertices.contains(vertex)) {
                boolean isCyclicDisjoint = CycleDetector.hasCycleDisjoint(graph, vertex, vertex, foundVertices);
                if (isCyclicDisjoint) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Try to find a cycle in one disjoint part of the graph
     *
     * @param graph
     * @param vertex
     * @param lastVertex
     * @param foundVertices
     * @return
     */
    public static boolean hasCycleDisjoint(Multigraph<Integer, DefaultEdge> graph, Integer vertex, Integer lastVertex, HashSet<Integer> foundVertices)
    {
        Set<DefaultEdge> edges = graph.edgesOf(vertex);

        return CycleDetector.getNeighbours(graph, vertex).anyMatch(neighbour -> {

            // Do not go back the path we came from, this will result a false negative
            if (neighbour.equals(lastVertex)) {
                return false;
            }

            // The neighbour has already been found, and thus we have a cycle
            if (foundVertices.contains(neighbour)) {
                return true;
            }

            // Mark as found, and check recursively in the tree
            foundVertices.add(neighbour);
            return CycleDetector.hasCycleDisjoint(graph, neighbour, vertex, foundVertices);
        });
    }

    /**
     * Return all the neighbours of a vertex in a graph
     *
     * @param graph
     * @param vertex
     * @return
     */
    public static Stream<Integer> getNeighbours(Multigraph<Integer, DefaultEdge> graph, Integer vertex)
    {
        Set<DefaultEdge> edges = graph.edgesOf(vertex);
        return edges.stream().map(edge -> {
            // Edges have a source and target node. We must select the vertex that is not
            // the current vertex.
            Integer sourceVertex = graph.getEdgeSource(edge);
            Integer endVertex = graph.getEdgeTarget(edge);

            return sourceVertex.equals(vertex) ? endVertex :  sourceVertex;
        });
    }
}
