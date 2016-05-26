package Alg;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

/**
 * Created on 5/24/2016.
 *
 * Instead of using brute force to go through an enormous search space,
 * we used simple observations to reduce the search space to a manageable size.
 * This algorithmic technique, using reduction rules to decrease the size
 * of the instance, is called kernelization,
 */
public class Kernelization {
    //====================//
    // 4 Reduction Rules: //
    //====================//

    // Rule 0: The Degree-Zero Rule. (unconnected vertices)
    // If v is a vertex of degree 0 in G, then delete v.
    // The parameter k is unchanged.

    // Rule 1: The Degree-One Rule. (leafs)
    // If v is a vertex of degree 1 in G, then delete v.
    // The parameter k is unchanged.

    // Rule 2: The Degree-Two Rule.
    // If v is a vertex of degree 2 in G, with neighbors a and b
    // (allowing possibly a = b), then modify G by replacing v and its
    // two incident edges with a single edge between a and b
    // (or a loop on a = b, in which case Rule 3 will remove it).
    // The parameter k is unchanged.

    // Rule 3: The Loop Rule. (self-loops)
    // If there is a loop on a vertex v then take v into the solution set,
    // and reduce to the instance (G − v, k − 1).

    // Rule 4: Multiedge Reduction.
    // If there are more than two edges between u and v then delete all
    // but two of these.
    // The parameter k is unchanged.

    // from paper: A 4k^2 kernel for feedback vertex set
    // Rule 5 ??
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
