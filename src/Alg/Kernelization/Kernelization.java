package Alg.Kernelization;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.*;

/**
 * Created on 5/24/2016.
 *
 * Instead of using brute force to go through an enormous search space,
 * we used simple observations to reduce the search space to a manageable size.
 * This algorithmic technique, using reduction rules to decrease the size
 * of the instance, is called kernelization,
 */

/*
//==================//
// Reduction Rules: //
//==================//

// Rule 0: The Degree-Zero Rule. (unconnected vertices)
If v is a vertex of degree 0 in G, then delete v.
The parameter k is unchanged.

// Rule 1: The Degree-One Rule. (leafs)
If v is a vertex of degree 1 in G, then delete v.
The parameter k is unchanged.

// Rule 2: The Degree-Two Rule.
If v is a vertex of degree 2 in G, with neighbors a and b
(allowing possibly a = b), then modify G by replacing v and its
two incident edges with a single edge between a and b
(or a loop on a = b, in which case Rule 3 will remove it).
The parameter k is unchanged.

// Rule 3: The Loop Rule. (self-loops)
If there is a loop on a vertex v then take v into the solution set,
and reduce to the instance (G − v, k − 1).

// Rule 4: Multiedge Reduction.
If there are more than two edges between u and v then delete all
but two of these.
The parameter k is unchanged.

Additional:
// 2-Approximation Alg:
Outputs feedback vertex superset, if FVS is present
• A graph is called clean if it contains no vertex of degree less than 2 (Rule0and1)
• A cycle C is semidisjoint if, for every vertex u of C, d(u) = 2 with at most one exception.
1: Given a graph (G, w) with G = (V,E), any vertex of weight zero is removed from G and placed in the solution set
F at the outset. [We can skip this step, because we add the weights to the vertices ourselves with a default value of 1]
2: <While loop until G becomes empty:>
    Decompose graph (G, w) into subgraphs (Gi, wi)’s by iteratively
    - subtracting wi from w
    - removing vertices of weight reduced to zero
    - adding them into F
    - and cleaning up G (rule 0 and 1)
    *The subgraph Gi derived in the ith iteration is either a semidisjoint cycle C contained in G or, otherwise, G itself.
    **Note that the first case has precedence over the second; that is, Gi is a semidisjoint cycle whenever G contains one.

// Rule 11: Strongly Forced Vertex Rule

// Rule 12: Strongly Forced Pair Rule

// Reduction FVS 6 (book):
If |V (G)| ≥ (d + 1)k or |E(G)| ≥ 2dk, where d is the maximum degree of G,
then terminate the algorithm and return that (G, k) is a no-instance.

// Reduction FVS 7 (book): [flower rule]
If there exists a vertex v ∈ V (G) and a flower with core v and more than k petals,
then delete v and decrease k by 1.
*/

    /* return ReductionSolution:
    boolean stillPossible;
    ArrayList<Integer> verticesToRemoved;
    int reducedK;
    Multigraph reducedGraph;
    */

public class Kernelization {

    public static ReductionSolution kernelize(Multigraph<Integer, DefaultEdge> graph, int k) {
        return kernelize(graph, k, true);
    }

    /**
     * Applies Rule 0 and 1 to the graph. Mainly meant for outside usage.
     *
     * @param solution
     * @param graph
     * @return
     */
    public static boolean rule0and1(ReductionSolution solution, Multigraph<Integer, DefaultEdge> graph)
    {
        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);
        return Kernelization.rule0and1(solution, graph, vertices);

    }

    /**
     * Fast application of rule 0 and 1 to the graph, where the set of verties is already extracted. Is faster than
     * the other rule0and1 method therefore
     */
    public static boolean rule0and1(ReductionSolution solution, Multigraph<Integer, DefaultEdge> graph, Integer[] vertices)
    {
        boolean changed = false;
        for (int v:vertices) {        //Returns the degree of the specified vertex.
            int degree = graph.degreeOf(v); // swap vertex "v" with actual vertex identifier

            // Rule 0 & Rule 1
            if (degree <= 1) {
                Kernelization.removeVertex(solution, v, false);
                changed = true;
            }
        }
        return changed;
    }

    public static boolean rule2(ReductionSolution solution, Multigraph<Integer, DefaultEdge> graph)
    {
        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);
        return Kernelization.rule2(solution, graph, vertices);
    }

    /**
     * Fast application of rule 2
     *
     * @param solution
     * @param graph
     * @param vertices
     * @return
     */
    public static boolean rule2(ReductionSolution solution, Multigraph<Integer, DefaultEdge> graph, Integer[] vertices){
        boolean changed = false;
        for (int v:vertices) {

            // Vertex might be removed already
            if (!graph.containsVertex(v)) {
                continue;
            }
            int degree = graph.degreeOf(v);
            // Rule 2
            if (degree == 2) {
                //Returns a list of vertices which are adjacent to a specified vertex.
                List<Integer> neighbors = Graphs.neighborListOf(graph, v); // get neighbors a and b of vertex v (allowing possibly a = b)
                int a = neighbors.get(0);
                int b = neighbors.get(1);

                // If the new edge that is places introduces a self loop, then it can be removed,
                // also remove the "self-loop vertex" and add it to the solution
                if (a == b) {
                    Kernelization.removeVertex(solution, v, false);
                    Kernelization.removeVertex(solution, a, true);
                } else {
                    //Creates a new edge in this graph, going from the source vertex to the target vertex, and returns the created edge.
                    graph.addEdge(a, b); // a = sourceVertex, b = targetVertex
                    Kernelization.removeVertex(solution, v, false);
                }

                changed = true;
            }
        }
        return changed;
    }

    /**
     *
     * @param graph
     * @param k
     * @param cloneGraph Do we clone the graph, or work on the original graph directly
     * @return
     */
    public static ReductionSolution kernelize( Multigraph<Integer, DefaultEdge> graph, int k, boolean cloneGraph) {

        ReductionSolution solution = new ReductionSolution();
        solution.reducedGraph = cloneGraph ? (Multigraph<Integer, DefaultEdge>) graph.clone(): graph;
        solution.reducedK = k;

        final Multigraph<Integer, DefaultEdge> reducedGraph = solution.reducedGraph;
        boolean changed;

        do {
            changed = false;
            changed |= Kernelization.rule0and1(solution, reducedGraph);
            changed |= Kernelization.rule2(solution, reducedGraph);



//            // Rule 4
//            LinkedList<DefaultEdge> edges = new LinkedList(reducedGraph.edgeSet());
//            Collections.sort(edges, (o1, o2) -> {
//                int a = reducedGraph.getEdgeSource(o1) - reducedGraph.getEdgeSource(o2);
//                if ( a > 0 ) {
//                    return 1;
//                }
//                if ( a < 0 ) {
//                    return -1;
//                }
//                return reducedGraph.getEdgeTarget(o1) - reducedGraph.getEdgeTarget(o2);
//            });
//            Iterator<DefaultEdge> it = edges.iterator();
//
//            // If the iterator does not have a next item, we do not have edges. Thus rule 1 will take care of this
//            // in the next run
//            if (!it.hasNext()) {
//                continue;
//            }
//            DefaultEdge current = it.next();
//
//            while (it.hasNext()) {
//                DefaultEdge next = it.next();
//                if ( reducedGraph.getEdgeSource(current) == reducedGraph.getEdgeSource(next) ) {
//                    if ( reducedGraph.getEdgeTarget(current) == reducedGraph.getEdgeTarget(next) ) {
//                        reducedGraph.removeEdge(current);
//                        changed = true;
//                        continue;
//                    }
//                }
//                current = next;
//            }

            // Rule 5

            // Rule 6
        } while(changed);

        solution.stillPossible = solution.reducedK > 0 || (solution.reducedK == 0 && reducedGraph.edgeSet().size() == 0);
        return solution;
    }

    /**
     * Helper function to remove a vertex from the graph
     *
     * @param solution The solution found thus far
     * @param vertex Vertex that needs to be removed
     * @param inSolution IF the vertex needs to be in the solution, or if it can be removed, but is not in the solution
     */
    protected static void removeVertex(ReductionSolution solution, int vertex, boolean inSolution) {
        solution.reducedGraph.removeVertex(vertex);

        if (inSolution) {
            solution.verticesToRemoved.add(vertex);
            solution.reducedK -= 1;
        }
    }
}