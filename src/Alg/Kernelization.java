package Alg;

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


(From the paper: A 4k^2 kernel for feedback vertex set]

// Rule 5 Center of flower Paths
If there exists an x-flower F of order p and a set of
q pairwise disjoint cycles which are moreover disjoint
from F such that p + q ≥ k + 1, we reduce to G^n := G \ x
and k' := k − 1.

// Rule 6 ??
If there is a set of vertices X, a vertex x ∈ V \ X and a set
of connected components C of G \ (X ∪ x) [not necessarily all
the connected components] such that:
• There is exactly one edge between x and every C ∈ C.
• Every C ∈ C induces a tree.
• For every subset Z ⊆ X, the number of components of C having
some neighbor in Z is at least 2|Z|.
Then one can form a graph G0 by joining x to every vertex of X
by double edges, and removing the edges between x and the
components of C. We then reduce to G' and k' := k
*/

    /* return ReductionSolution:
    boolean stillPossible;
    ArrayList<Integer> verticesToRemoved;
    int reducedK;
    Multigraph reducedGraph;
    */

public class Kernelization {

    public static ReductionSolution kernelize( Multigraph<Integer, DefaultEdge> graph, int k) {

        ReductionSolution solution = new ReductionSolution();
        solution.reducedGraph = (Multigraph<Integer, DefaultEdge>)graph.clone();
        solution.reducedK = k;

        final Multigraph<Integer, DefaultEdge> reducedGraph = solution.reducedGraph;
        boolean changed;

        do {
            changed = false;

            for ( int v : graph.vertexSet()) {
                //Returns the degree of the specified vertex.
                int degree = reducedGraph.degreeOf(v); // swap vertex "v" with actual vertex identifier

                // Rule 0 & Rule 1
                if (degree <= 1 ) {
                    Kernelization.removeVertex(solution, v, false);
                    changed = true;
                }

                // Rule 2
                if (degree == 2) {
                    //Returns a list of vertices which are adjacent to a specified vertex.
                    List<Integer> neighbors = Graphs.neighborListOf(reducedGraph, v); // get neighbors a and b of vertex v (allowing possibly a = b)
                    int a = neighbors.get(0);
                    int b = neighbors.get(1);

                    // If the new edge that needs to be introduced is a self loop, then it can be removed,
                    if (a == b) {
                        Kernelization.removeVertex(solution, v, true);
                    } else {
                        //Creates a new edge in this graph, going from the source vertex to the target vertex, and returns the created edge.
                        reducedGraph.addEdge(a, b); // a= sourceVertex, b = targetVertex
                        Kernelization.removeVertex(solution, v, false);
                    }

                    changed = true;
                }

                // Rule 3
                if ( graph.containsEdge(v, v) ) // Returns true if this graph contains an edge between the specified source vertex and target vertex
                {
                    Kernelization.removeVertex(solution, v, true);
                    changed = true;
                }

                // Rule 4
                LinkedList<DefaultEdge> edges = new LinkedList(graph.edgeSet());
                Collections.sort(edges, (o1, o2) -> {
                    int a = graph.getEdgeSource(o1) - graph.getEdgeSource(o2);
                    if ( a > 0 )
                    {
                        return 1;
                    }
                    if ( a < 0 )
                    {
                        return -1;
                    }

                    return graph.getEdgeTarget(o1) - graph.getEdgeTarget(o2);
                });
                Iterator<DefaultEdge> it = edges.iterator();
                DefaultEdge current = it.next();

                while (it.hasNext())
                {
                    DefaultEdge next = it.next();

                    if ( graph.getEdgeSource(current) == graph.getEdgeSource(next) )
                    {
                        if ( graph.getEdgeTarget(current) == graph.getEdgeTarget(next) )
                        {
                            it.remove();
                            continue;
                        }
                    }
                    current = next;
                }

                // Rule 5

                // Rule 6
            }
        }
        while(changed);
        solution.stillPossible = (solution.reducedK > 0 || reducedGraph.edgeSet().size() == 0);
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
