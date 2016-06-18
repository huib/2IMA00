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

    public static ReductionSolution determineFVS(Multigraph<Integer, DefaultEdge> ingraph, boolean cloneGraph, Integer[] weightedVertices, int weight) // changed from boolean to int
    {
        Multigraph<Integer, DefaultEdge> graph = cloneGraph ? (Multigraph<Integer, DefaultEdge>) ingraph.clone(): ingraph;
        ArrayList<Integer> approxVerticesToRemoved = new ArrayList();

        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);
        return Approximation.determineFVS(ingraph, graph, vertices, approxVerticesToRemoved, weightedVertices, weight);

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
    public static ReductionSolution determineFVS(Multigraph<Integer, DefaultEdge> ingraph, Multigraph<Integer, DefaultEdge> graph, Integer[] vertices, ArrayList<Integer> approxVerticesToRemoved, Integer[] weightedVertices, int weight){
        float gamma = 1; // default value = min{  weight(u) : u ∈ V of (semidisjoint) wgraph  }
        boolean couldContainSemiDisjointCycle = false;
        ReductionSolution solution = new ReductionSolution();
        solution.reducedGraph = graph;

        /**
         * Check if semi-disjoint cycle in G and find min gamma value that is needed in case there isn't
         *
         * Note : We skip over vertices with degree <= 1 because these are technically still contained within our graph,
         * even though we already performed a cleanUp on it.
         */
        for (Integer v : vertices) { // find minimum gamma value. Needed for vertices that are not part of semidisjoint cycles
            if (!graph.containsVertex(v)) {
                continue;
            }

            WeightedVertex u = new WeightedVertex(v); // give default weight=1 to all v

            int degree = graph.degreeOf(u.id); // v == u.id
            if (degree <= 1) { // these vertices aren't actually removed from reducedGraph, so we simply skip them
                continue;
            }

            if (gamma > u.weight / (degree - 1)) {
                gamma = (u.weight / (degree - 1));
            }

            if (degree == 2){ // if no vertex with degree 2 is found, we certainly cannot find any SDC
                couldContainSemiDisjointCycle = true;
            }
        }

        /**
         * Iterative reduction of G to G-F.
         *
         * We fill the approximated solution set F with all vertices with weight reduced to 0, being at all vertices from
         * all the semidisjoint cycles (SDC) that are detected. After that, we remove the vertices from this solution that
         * turn out to be redundant.
         *
         * Note 1: we cannot merge both for-loops on vertices v, because we need to process each vertex once in order to
         * find the minimum value for gamma which is used to decrease vertex weights in the following for-loop.
         * Note 2: Since our default weight is 1, gamma equals 1 for vertices from an SDC.
         */

         for (Integer v : vertices) {
             if (!graph.containsVertex(v)) {
                 continue;
             }

             WeightedVertex u = new WeightedVertex(v);
             int degree = graph.degreeOf(u.id);
             if (degree <= 1) {
                 continue;
             }

             if (couldContainSemiDisjointCycle) {
                 // we now check if G contains semidisjoint cycles [SDC] (plural)
                 // • This includes steps that resemble kernelization rule 2, but rule 2 is executed slightly different
                 // • If a vertice isn't a member of an SDC, we reduce its weight by gamma := min{w(u)/(d(u) − 1) : u ∈ V }, for vertex u with weight w(u) and degree d(u)
                 // • gamma reduction creates and ordering of vertices for STACK, which is used to check for redundancy
                 if (degree == 2) {
                     List<Integer> semiDisjointCycle = new ArrayList();
                     List<Integer> leftNeighbors;
                     List<Integer> rightNeighbors;

                     semiDisjointCycle.add(v);

                     List<Integer> neighbors = Graphs.neighborListOf(graph, v);
                     Integer left = neighbors.get(0);
                     Integer right = neighbors.get(1);

                     Integer departureVertex = v;
                     Integer terminal = -1;

                     // clear this at the end if it turns out v is not contained in an SDC
                     // otherwise
                     semiDisjointCycle.add(v);
                     semiDisjointCycle.add(left); // clear this if it turns out v is not contained in an SDC
                     semiDisjointCycle.add(right); // clear this if it turns out v is not contained in an SDC

                     if (left == right) {
                         // would removing v create a self-loop? if yes, put neighbor of v in solution and remove both
                         Kernelization.removeVertex(solution, v, false);
                         Kernelization.removeVertex(solution, left, true);
                     } else { // a != b, check cycle for matching exception vertex
                         int degreeLeft = graph.degreeOf(left);
                         Integer l1;
                         Integer l2;
                         Integer leftException;
                         int degreeRight = graph.degreeOf(right);
                         Integer r1;
                         Integer r2;
                         Integer rightException;
                         while (degreeLeft == 2) { // still potential vertex contained SDC?
                             leftNeighbors = Graphs.neighborListOf(graph, left);
                             terminal = left;
                             l1 = leftNeighbors.get(0);
                             l2 = leftNeighbors.get(1);
                             if (l1 != departureVertex) {
                                 degreeLeft = ingraph.degreeOf(l1); // use degree of original graph! maybe degree got lowered cause of other SDC searches
                                 semiDisjointCycle.add(l1);
                                 left = l1;
                             } else {
                                 degreeLeft = ingraph.degreeOf(l2);
                                 semiDisjointCycle.add(l2);
                                 left = l2;
                             }
                             departureVertex = terminal;
                         }
                         leftException = left; // semidisjoint cycle exception found
                         while (degreeRight == 2) {
                             rightNeighbors = Graphs.neighborListOf(graph, right);
                             terminal = right;
                             r1 = rightNeighbors.get(0);
                             r2 = rightNeighbors.get(1);
                             if (r1 != departureVertex) {
                                 degreeRight = ingraph.degreeOf(r1);
                                 semiDisjointCycle.add(r1);
                                 right = r1;
                             } else {
                                 degreeRight = ingraph.degreeOf(r2);
                                 semiDisjointCycle.add(r2);
                                 right = r2;
                             }
                             departureVertex = terminal;
                         }
                         rightException = right; // another semidisjoint cycle exception found

                         // SDC may contain at most 1 exception, so we must have that (leftException == rightException)
                         if (leftException == rightException) {
                             for (Integer c : semiDisjointCycle) {
                                 for (Integer w : vertices) {
                                     if (w == c) {
                                         WeightedVertex wv = new WeightedVertex(w);
                                         wv.weight = 0; // = d(v)-gamma = 0, because the default weight is 1 => min weight from v in G
                                     }
                                 }
                                 graph.removeVertex(c); // remove cycle from G to create G-F
                             }
                             approxVerticesToRemoved.addAll(semiDisjointCycle); // add entire cycle to approx. solution F.
                             semiDisjointCycle.clear();
                         } else { // more than one exception contained in path -> no semidisjoint cycle!
                             for (Integer c : semiDisjointCycle) {
                                 for (Integer w : vertices) {
                                     if (w == c) {
                                         WeightedVertex wv = new WeightedVertex(w);
                                         wv.weight = wv.weight - gamma * (degree - 1);
                                         if (wv.weight == 0) {
                                             approxVerticesToRemoved.add(c);
                                             graph.removeVertex(c);
                                         }
                                     }
                                 }
                             }
                             // clear cycle for next iteration (find next cycle, if present)
                             semiDisjointCycle.clear();
                         }
                     } // endif (left != right)
                 } // endif (degree == 2)
             } else {// endif (couldContainSemiDisjointCycle)
                 // if this is reached, we only need to check for other weight 0 vertices. This ELSE is done to save running time
                 u.weight = u.weight - gamma * (degree - 1);
                 if (u.weight == 0) {
                     approxVerticesToRemoved.add(u.id);
                     graph.removeVertex(u.id);
                 }
             }
         }// endfor (v:vertices)

        // Current status: Any semidisjoint cycle (SDC) contained within G is now in F. G-F contains no (more) SDC
        // We now filter out redundant vertices from F, since we added entire cycles instead of singular vertices
        UnionFind<Integer> union = new UnionFind(graph.vertexSet());
        for (Integer v : approxVerticesToRemoved ) {
            if (solution.verticesToRemoved.contains(v)) continue; // if already added to definitive solution F

            List<Integer> neighbors = Graphs.neighborListOf(ingraph, v);
            TreeSet<Integer> neighborComponents = new TreeSet();
            boolean hasDuplicates = false; // check if v is connected to the same component more than once == duplicate

            for ( Integer n:neighbors ) {
                if (graph.containsVertex(n)) { // if n in G - F
                    neighborComponents.add(union.find(n));
                    hasDuplicates |= !neighborComponents.add(union.find(n));
                }
                if (hasDuplicates) break; // v is essential
            }

            if(!hasDuplicates){ // v is redundant
                union.addElement(v);
                for ( Integer n:neighbors ) {
                    union.union(v, n);
                }
                approxVerticesToRemoved.remove(v); // equivalent for "pop STACK" from FEEDBACK (paper ALG)
                graph.addVertex(v); // add vertex back to G-F
            } else {
                solution.verticesToRemoved.add(v); // add v to definitive solution F
                solution.reducedK -= 1; // reduce k by one
            }
        }

        // now, to account for any vertex whose weight was artificially increased:
        int c = 0;
        for (int v : approxVerticesToRemoved ) {
            for (int w : weightedVertices){
                if(v == w) c++;
            }
        }
        int total_FVS_weight = approxVerticesToRemoved.size() + c*(weight-1);
        solution.totalFVSweight = total_FVS_weight;

        return solution; // where: solution.verticesToRemoved = F
    }

}
