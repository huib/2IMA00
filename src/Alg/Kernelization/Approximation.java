package Alg.Kernelization;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.alg.util.UnionFind;

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

    public static int determineFVS(Multigraph<Integer, DefaultEdge> ingraph, boolean cloneGraph, Integer[] weightedVertices, int weight) // changed from boolean to int
    {
        Multigraph<Integer, DefaultEdge> graph = cloneGraph ? (Multigraph<Integer, DefaultEdge>) ingraph.clone(): ingraph;
        ArrayList<Integer> approxVerticesToRemoved = new ArrayList();

        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);
        return Approximation.determineFVS(graph, vertices, approxVerticesToRemoved, weightedVertices, weight);

    }

    /**
     * Determine the FVS (superset) of G (see FEEDBACK pseudo-code from paper)
     *
     * @param graph
     * @param vertices
     * @param weightedVertices
     * @param weight
     * @return
     */
    public static int determineFVS(Multigraph<Integer, DefaultEdge> graph, Integer[] vertices, ArrayList<Integer> approxVerticesToRemoved, Integer[] weightedVertices, int weight){
        boolean changed = false;
        boolean semidisjoint = true;
        boolean semidisjointexception = false;
        int gamma = 1; // default value = min{  weight(u) : u ∈ V of (semidisjoint) wgraph  }
        int addedWeight = 0;
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
                addedWeight += weight;
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
                if (!approxVerticesToRemoved.contains(u.id)) {
                    approxVerticesToRemoved.add(u.id); // add to solution F (superset) [but don't remove from subgraph nor update k yet]}
                }

                // In FEEDBACK, graph G is deleted at this point and it uses subgraph Gi for the next iteration.
                // However, we don't need to do this here because our weighted-vertex graph makes it so trivial.
            }
        }
        ReductionSolution solution = new ReductionSolution();
        solution.reducedGraph = graph;
        Kernelization.simpleVertexRules(solution);  // CleanUp(G) again, because we deleted all w(v) = 0 vertices it

        UnionFind<Integer> union = new UnionFind(graph.vertexSet());
        for (int v : approxVerticesToRemoved ) {
            List<Integer> neighbors = Graphs.neighborListOf(graph, v);
            TreeSet<Integer> neighborComponents = new TreeSet();
            boolean hasDuplicates = false;

            for ( Integer n:neighbors ) {
                neighborComponents.add( union.find(n) );
                hasDuplicates |= !neighborComponents.add( union.find(n) );
            }

            if(!hasDuplicates){//(v is redundant)
                union.addElement(v);
                for ( Integer n:neighbors ) {
                    union.union(v, n);
                    approxVerticesToRemoved.remove(v);
                }
            }
        }

        int c = 0;
        for (int v : approxVerticesToRemoved ) {
            for (int w : weightedVertices){
                if(v == w) c++;
            }
        }

        int total_FVS_weight = approxVerticesToRemoved.size() + c*(weight-1);
        return total_FVS_weight;
    }
}
