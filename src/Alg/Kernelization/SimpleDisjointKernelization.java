package Alg.Kernelization;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Kernelization rules mainly focessed on the Simple Disjoint Kernelization Problem.
 *
 * See Section 4.3.1 from the book
 */
public class SimpleDisjointKernelization extends Kernelization {
    /**
     * remove any vertex v not in prohibited that is part of a cycle where all other vertices
     *    are in prohibited. Add v to the solution.s
     *
     * @param reductionSolution
     * @param graph
     * @param prohibited
     */
    public static void removeOnlyVertexInProhibitedCycle(
            ReductionSolution reductionSolution,
            Multigraph<Integer, DefaultEdge> graph,
            HashSet<Integer> prohibited
    ) {
        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);

        for (int v: vertices) {
            // We do not have to check prohibited vertices
            if (prohibited.contains(v)) {
                return;
            }

            if (SimpleDisjointKernelization.inCycleWith(v, graph, prohibited)) {
                reductionSolution.verticesToRemoved.add(v);
                graph.removeVertex(v);
            }
        }

    }

    /**
     * Checks if vertex v is in a cycle with only edges from set withSet
     *
     * @param v
     * @param graph
     * @param withSet
     * @return
     */
    public static boolean inCycleWith(Integer v, Multigraph<Integer, DefaultEdge> graph, HashSet<Integer> withSet)
    {
        return SimpleDisjointKernelization.inCycleWithRecusive(v, graph, withSet, v, v);
    }

    /**
     * Recursive function to check if vertex v is in a cycle with only edges from set withSet
     *
     * @param v target vertex we have to search
     * @param graph base graph
     * @param withSet set of other vertices we can use
     * @param currentVertex current vertex we are searching at
     * @param lastVertex Last vertex, needed to ensure we do not go back the same way
     * @return
     */
    protected static boolean inCycleWithRecusive(
            Integer v,
            Multigraph<Integer, DefaultEdge> graph,
            HashSet<Integer> withSet,
            Integer currentVertex,
            Integer lastVertex
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

            if (withSet.contains(vertex)) {
                return SimpleDisjointKernelization.inCycleWithRecusive(v, graph, withSet, vertex, v);
            }
        }

        // We could not find
        return false;
    }

    /**
     * Get the neighbours of vertex v in graph graph
     *
     * @param graph
     * @param v
     * @return
     */
    public static Stream<Integer> getNeighbours(Multigraph<Integer, DefaultEdge> graph, Integer v)
    {
        return SimpleDisjointKernelization.getEdgesOf(graph, v)
                .stream()
                .map((DefaultEdge e)-> new Integer(graph.getEdgeSource(e) == v ? graph.getEdgeTarget(e) : graph.getEdgeSource(e)));
    }

    /**
     * Returns all the edges that have either the source or target in v
     *
     * @param graph
     * @param v
     * @return
     */
    public static Set<DefaultEdge> getEdgesOf(Multigraph<Integer, DefaultEdge> graph, Integer v)
    {
        Set<DefaultEdge> edges = graph.incomingEdgesOf(v);
        edges.addAll(graph.outgoingEdgesOf(v));
        return edges;
    }
}
