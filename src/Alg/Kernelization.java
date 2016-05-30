package Alg;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 5/24/2016.
 *
 * Instead of using brute force to go through an enormous search space,
 * we used simple observations to reduce the search space to a manageable size.
 * This algorithmic technique, using reduction rules to decrease the size
 * of the instance, is called kernelization,
 */

public class Kernelization {

    // save vertices v that decrease k in an ArrayList. Length of ArrayList = k
    public static Multigraph<Integer, DefaultEdge> kernelize( Multigraph<Integer, DefaultEdge> graph, ArrayList<Integer> vertexSet ) {

        for ( int v : vertexSet ) {
            //System.out.println(graph.get(i));
            //int v = vertexSet.get(i);

            //Returns the degree of the specified vertex.
            int DegreeOfVertex = graph.degreeOf(v); // swap vertex "v" with actual vertex identifier

            //==================//
            // Reduction Rules: //
            //==================//

            // Rule 0: The Degree-Zero Rule. (unconnected vertices)
            // If v is a vertex of degree 0 in G, then delete v.
            // The parameter k is unchanged.
            if (DegreeOfVertex == 0) {
                //Removes the specified vertex from this graph including all its touching edges if present.
                graph.removeVertex(v);
            }

            // Rule 1: The Degree-One Rule. (leafs)
            // If v is a vertex of degree 1 in G, then delete v.
            // The parameter k is unchanged.
            if (DegreeOfVertex == 1) {
                graph.removeVertex(v);
            }

            // Rule 2: The Degree-Two Rule.
            // If v is a vertex of degree 2 in G, with neighbors a and b
            // (allowing possibly a = b), then modify G by replacing v and its
            // two incident edges with a single edge between a and b
            // (or a loop on a = b, in which case Rule 3 will remove it).
            // The parameter k is unchanged.
            /* if (DegreeOfVertex == 2) {
                //Returns a list of vertices which are adjacent to a specified vertex.
                List<Integer> neighbors = neighborListOf(graph, v); // get neighbors a and b of vertex v (allowing possibly a = b)
                int a = neighbors.get(0);
                int b = neighbors.get(1);
                //Creates a new edge in this graph, going from the source vertex to the target vertex, and returns the created edge.
                graph.addEdge(a, b); // a= sourceVertex, b = targetVertex
                graph.removeVertex(v);
            } */

            // Rule 3: The Loop Rule. (self-loops)
            // If there is a loop on a vertex v then take v into the solution set,
            // and reduce to the instance (G − v, k − 1).
            if ( graph.containsEdge(v, v) ) // Returns true if this graph contains an between the specified source vertex and target vertex
            {
                // add v to arraylist (solution)
                graph.removeVertex(v);
            }

            // Rule 4: Multiedge Reduction.
            // If there are more than two edges between u and v then delete all
            // but two of these.
            // The parameter k is unchanged.

            // from paper: A 4k^2 kernel for feedback vertex set
            // Rule 5 Center of flower Paths
            // If there exists an x-flower F of order p and a set of
            // q pairwise disjoint cycles which are moreover disjoint
            // from F such that p + q ≥ k + 1, we reduce to G^n := G \ x
            // and k' := k − 1.

            // Rule 6 ??
            // If there is a set of vertices X, a vertex x ∈ V \ X and a set
            // of connected components C of G \ (X ∪ x) [not necessarily all
            // the connected components] such that:
            // • There is exactly one edge between x and every C ∈ C.
            // • Every C ∈ C induces a tree.
            // • For every subset Z ⊆ X, the number of components of C having
            //   some neighbor in Z is at least 2|Z|.
            // Then one can form a graph G0 by joining x to every vertex of X
            // by double edges, and removing the edges between x and the
            // components of C. We then reduce to G' and k' := k
        }

        return graph;
    }
}
