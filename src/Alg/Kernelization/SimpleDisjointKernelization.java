package Alg.Kernelization;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Kernelization rules mainly focused on the Simple Disjoint Kernelization Problem.
 *
 * See Section 4.3.1 from the book
 */
public class SimpleDisjointKernelization extends Kernelization {
    /**
     * remove any vertex v not in prohibited that is part of a cycle where all other vertices
     *    are in prohibited. Add v to the solution.s
     *
     * @param solution
     * @param graph
     * @param prohibited
     * @return Was a change done on the graph?
     */
    public static boolean removeOnlyVertexInProhibitedCycle(
            ReductionSolution solution,
            Multigraph<Integer, DefaultEdge> graph,
            HashSet<Integer> prohibited
    ) {
        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);

        boolean changed = false;
        for (int v: vertices) {
            // We do not have to check prohibited vertices
            if (prohibited.contains(v)) {
                continue;
            }

            if (SimpleDisjointKernelization.inCycleWith(v, graph, prohibited)) {
                Kernelization.removeVertex(solution, v, true);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Checks if vertex v is in a cycle with only edges from set withSet
     *
     * @param v
     * @param graph
     * @param withSet
     * @return
     */
    public static boolean inCycleWith(Integer v, Multigraph<Integer, DefaultEdge> graph, Set<Integer> withSet)
    {
        return SimpleDisjointKernelization.inCycleWithRecursive(v, graph, withSet, v, v, new HashSet<>(v));
    }

    public static class Switch
    {
        protected boolean value = false;
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
    protected static boolean inCycleWithRecursive(
            Integer v,
            Multigraph<Integer, DefaultEdge> graph,
            Set<Integer> withSet,
            Integer currentVertex,
            Integer lastVertex,
            Set<Integer> done
    ) {
        final Switch secondBack = new Switch();

        return SimpleDisjointKernelization.getNeighbours(graph, currentVertex).anyMatch(vertex -> {
            // Do not go back the same way
            // unless the lastVertex is the vertex we are looking for (v) and this is the second time
            // we can go back (meaning two edges going back, one already visited to get here, the
            // other one completing the cycle)
            if (vertex.equals(lastVertex) && !(secondBack.value && lastVertex.equals(v))) {
                secondBack.value = true;
                return false;
            }

            // We found a cycle
            if (vertex.equals(v)) {
                return true;
            }

            // We have already checked this vertex.
            if (done.contains(vertex)) {
                return false;
            }

            if (withSet.contains(vertex)) {
                done.add(vertex);
                if (SimpleDisjointKernelization.inCycleWithRecursive(v, graph, withSet, vertex, currentVertex, done)) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * remove any vertex v not in prohibited with degree 2 and at least one of its
     * neighbours also not in prohibited. Connect the neighbours of v.
     *
     * @param solution
     * @param graph
     * @return Was a change done on the graph?
     */
    public static boolean removeNonProhibitedVertexWithDegree2(
            ReductionSolution solution,
            Multigraph<Integer, DefaultEdge> graph,
            HashSet<Integer> prohibited
    ) {
        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);

        boolean changed = false;
        for (int v:vertices) {
            // Skip prohibited graphs
            if (prohibited.contains(v) || !graph.containsVertex(v)) {
                continue;
            }

            // Make sure that the vertex is of degree 2
            if (graph.degreeOf(v) == 2) {
                ArrayList<Integer> neighbours = SimpleDisjointKernelization.getNeighbours(graph, v)
                        .collect(Collectors.toCollection(ArrayList<Integer>::new));

                // Both neighbours are prohibited, so we can not do anything about it
                if (prohibited.containsAll(neighbours)) {
                    continue;
                }

                changed = true;
                // Now we can remove the vertex, and join the neighbours
                try {
                    graph.addEdge(neighbours.get(0), neighbours.get(1));
                } catch(IllegalArgumentException e) {
                    Kernelization.removeVertex(solution, neighbours.get(0), true);
                }
                Kernelization.removeVertex(solution, v, false);
            }
        }
        return changed;
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
        return graph.edgesOf(v)
                .stream()
                .map((DefaultEdge e)-> new Integer(graph.getEdgeSource(e).equals(v) ? graph.getEdgeTarget(e) : graph.getEdgeSource(e)));
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
        return graph.edgesOf(v);
        /*
        Set<DefaultEdge> edges = graph.incomingEdgesOf(v);
        edges.addAll(graph.outgoingEdgesOf(v));
        return edges;
        */
    }
}
