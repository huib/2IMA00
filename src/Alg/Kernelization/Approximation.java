package Alg.Kernelization;

import org.jgrapht.Graphs;
import org.jgrapht.alg.util.UnionFind;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.*;

/**
 * Created by Christopher on 6/15/2016.
 *
 * 2-Approximation Algorithm (FEEDBACK) from: http://epubs.siam.org/doi/abs/10.1137/S0895480196305124
 *
 * FEEDBACK outputs a feedback vertex superset, if a FVS is present in graph G
 * Definitions:
 * • A graph is called clean if it contains no vertex of degree less than 2 (This is done using reduction rules 0 and 1)
 * • A cycle C is semidisjoint if, for every vertex u of C, d(u) = 2 with at most one exception.
 *
 * Summary of FEEDBACK:
 * First, given a graph (G, w) with G = (V,E), any vertex of weight zero is removed from G and placed into the solution
 * F at the outset. [However, we can skip this step in our code because we add the weights to the vertices ourselves
 * with a default value of 1]
 * After that, FEEDBACK initiates a While-loop until G becomes empty. The while-loop decomposes graph (G, w) into
 * subgraphs (Gi, wi)’s by iteratively:
 * • subtracting wi from w
 * • removing vertices of weight reduced to zero
 * • adding them into solution F
 * • and cleaning up G (reduction rule 0 and 1)
 *
 * The subgraph Gi that is derived in the ith iteration is either a semidisjoint cycle C contained in G or,
 * otherwise, G itself. Note that the first case has precedence over the second; that is, Gi is a semidisjoint cycle
 * whenever G contains one.
 *
 * After creating F, the algorithm checks for redundant vertices in F and removes them, before returning F.
 */
public class Approximation {

    /**
     * Applies Rule 0 and 1 to the graph. Mainly meant for outside usage.
     *
     * @param solution
     * @param graph
     * @return
     */
//    public static boolean cleanUp(ReductionSolution solution, Multigraph<Integer, DefaultEdge> graph)
//    {
//        // kernelization reduction rules 0 and 1
//        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);
//        return Approximation.cleanUp(solution, graph, vertices);
//
//    }

    /**
     * Fast application of rule 0 and 1 to the graph, where the set of verties is already extracted. Is faster than
     * the other rule0and1 method therefore
     */
//    public static boolean cleanUp(ReductionSolution solution, Multigraph<Integer, DefaultEdge> graph, Integer[] vertices)
//    {
//        boolean changed = false;
//        for (int v:vertices) {        //Returns the degree of the specified vertex.
//            int degree = graph.degreeOf(v); // swap vertex "v" with actual vertex identifier
//
//            // Rule 0 & Rule 1
//            if (degree <= 1) {
//                if(solution.reducedGraph.containsVertex(v)) {
//                    Approximation.removeVertex(solution, v, false);
//                    changed = true;
//                }
//            }
//        }
//        return changed;
//    }

    public static int determineFVS(ReductionSolution solution, Multigraph<Integer, DefaultEdge> graph, Integer[] weightedVertices, int weight) // changed from boolean to int
    {
        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);
        return Approximation.determineFVS(solution, graph, vertices, weightedVertices, weight);

    }

    /**
     * Determine the FVS (superset) of G (see FEEDBACK pseudo-code from paper)
     *
     * @param solution
     * @param graph
     * @param vertices
     * @return
     */
    public static int determineFVS(ReductionSolution solution, Multigraph<Integer, DefaultEdge> graph, Integer[] vertices, Integer[] weightedVertices, int weight){
        boolean changed = false;
        boolean semidisjoint = true;
        boolean semidisjointexception = false;
        int gamma = 1; // default value = min{  weight(u) : u ∈ V of (semidisjoint) wgraph  }

//        for (int wv:weightedVertices){
//            WeightedVertex u = new WeightedVertex(wv);
//            u.weight = weight; //set weight to input
//        }
        /*
         * Check if semi-disjoint cycle in G and find min gamma value that is needed in case there isn't
         *
         * Note: Since we only have weights 1 gamma doesn't do much for us, but it's here to show that we follow FEEBACK
         * step by step in case this script is at some point expanded for use on actual vertex-weighted graphs.
         */
        for (int v : vertices) {
            if (!graph.containsVertex(v)) {
                continue;
            }

            WeightedVertex u = new WeightedVertex(v); // give default weight=1 to all v
            if (Arrays.asList(weightedVertices).contains(u.id)){
                u.weight = weight; //set weight to input
            }
            int degree = graph.degreeOf(u.id); // v == u.id
            if (degree <= 1) { // these vertices aren't actually removed from reducedGraph, so we simply skip them
                continue;
            }
            //System.out.print(v + ", ");
            if ( gamma > u.weight/(degree-1) ) {
                gamma = (u.weight / (degree - 1));
            }

            // check if G contains semidisjoint cycle C
            if (degree != 2 && !semidisjointexception) { // may contain at most one exception
                semidisjointexception = true;
            }
            else if (degree != 2 && semidisjointexception) {// G does not contain semidisjoint cycle C
                semidisjoint = false;
                //break; // we don't break here, because we need to find min(gamma) first!
            }
        }

        /*
         * Fill the solution set F with the semidisjoint cycle or, otherwise, all vertices with weight reduced to 0
         *
         * Note 1: we cannot merge both for-loops on vertices v because we can only know for sure it is semidisjoint
         * after checking ALL vertices once first.
         * Note 2: We skip over vertices with degree <= 1 because these are technically still contained within our graph,
         * even though we already performed a cleanUp on it.
         */
        for (int v:vertices) {
            if (!graph.containsVertex(v)) {
                continue;
            }

            WeightedVertex u = new WeightedVertex(v);
            int degree = graph.degreeOf(u.id);
            if (degree <= 1) {
                continue;
            }

            if (semidisjoint) { // in this case, G definitely contains an FVS
                u.weight = 0; //min{w(u) : u ∈ V (C)}=1, since that is the default w(u)
                /* Output in FEEDBACK: subgraph with u.weight = 1 (minimum weight) for all u in subgraph
                * Gi = solution.reducedGraph
                * wi(u) = 1, for all u in Gi
                */
            } else { // in this case, G is a clean but not semidisjoint. However, G might still contain an FVS
                u.weight = u.weight - gamma * (degree - 1); // thus, w(u) = 0
                /* Output in FEEDBACK: subgraph with u.weight = gamma*(degree-1) for all u in subgraph
                * Gi = solution.reducedGraph
                * wi(u) = w(u)-gamma*(d(u)-1), for all u in Gi
                */
            }
            if (u.weight == 0) {
                if (!solution.approxVerticesToRemoved.contains(u.id)) {
                    solution.approxVerticesToRemoved.add(u.id); // add to solution F (superset) [but don't remove from subgraph nor update k yet]}
                }
                if (!solution.verticesToRemoved.contains(u.id)) {
                    solution.verticesToRemoved.add(u.id); // add to final solution FVS [but don't remove from subgraph nor update k yet]
                }

                // In FEEDBACK, graph G is deleted at this point and it uses subgraph Gi for the next iteration.
                // However, we don't need to do this here because our weighted-vertex graph makes it so trivial.
            }
        }
        Kernelization.simpleVertexRules(solution);  // CleanUp(G) again, because we deleted all w(v) = 0 vertices it

        //UnionFind(graph.vertexSet());

        /*
        * At this point, FEEDBACK uses a STACK to check for redundant vertices in F
        * The following approach is very similar to that by checking for every vertex v in F (FVS superset):
        * • What are its neighbors n in graph G-F.
        * • Knowing what its neighbors are, check for all possible pairs whether there is an edge between them such that
        *   a connection between v and its two neighbors n_i and n_j (where i =/= j) would create a loop.
        *       - If this is the case, then v is essential. => Keep v in F.
        *       - If not, then v is redundant. => remove v from F and put v back in G-F
        */
        for (int v : solution.approxVerticesToRemoved ) {
            if (!solution.reducedGraph.containsVertex(v)) { // this should never occur...
                continue;
            }
            List<Integer> neighbors = Graphs.neighborListOf(solution.reducedGraph, v);

            for (int i = 0; i < neighbors.size() - 1; i++) {
                for (int j = 1; j < neighbors.size(); j++) {
                    if(!graph.containsEdge(i,j)) {
                        // no loop created by adding v to G-F, so v is redundant
                        solution.verticesToRemoved.removeAll(Collections.singleton((Integer) v));
                        //solution.approxVerticesToRemoved.removeAll(Collections.singleton("v"));
                    } else {
                        // else v is essential, because it creates a loop in G-F
                        solution.reducedGraph.removeVertex(v); // finally, remove v from subgraph
                        solution.reducedK -= 1; // finally, update k
                    }
                }
            }
        }
        int total_FVS_weight = solution.verticesToRemoved.size();
        solution.FVSweight = total_FVS_weight;
        return total_FVS_weight;
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
            solution.approxVerticesToRemoved.add(vertex);
            solution.verticesToRemoved.add(vertex);
            solution.reducedK -= 1;
        }
    }
}
