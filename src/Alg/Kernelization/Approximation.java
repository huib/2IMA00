package Alg.Kernelization;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.alg.util.UnionFind;

import java.lang.reflect.Array;
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
     * Helper function to produce clean graphs (with degree >= 2)
     *
     * @param solution The solution from the current iteration (G-F)
     * @param vertex Vertex from the current iteration
     * @param degree Degree of the vertex in the current iteration
     */
    public static ReductionSolution cleanUp(ReductionSolution solution, Integer vertex, int degree)
    {
        // Rule 0 & Rule 1
        if (degree <= 1) {
            Kernelization.removeVertex(solution, vertex, false);
        }

        return solution;
    }

    /**
     * Helper function to find gamma value for the semidisjoint cycle case
     *
     * @param graph The graph G-F from the current iteration
     * @param semiDisjointCycle The vertices from the semidisjoint cycle C of the current iteration
     */
    public static float gammaCase1(Multigraph<Integer, DefaultEdge> graph, List<Integer> semiDisjointCycle)
    {
        float gamma = -999; // initialize minimum degree
        for (Integer c : semiDisjointCycle) {
            int degree = graph.degreeOf(c);

            if (degree == -999 || degree < gamma) {
                gamma = degree;
            }
        }

        return gamma;
    }

    /**
     * Helper function to find gamma value for the case that no semidisjoint cycle was found
     *
     * @param graph The graph G-F from the current iteration
     */
    public static float gammaCase2(Multigraph<Integer, DefaultEdge> graph, Integer[] vertices)
    {
        int initializeDegree = graph.degreeOf(vertices[0]);
        WeightedVertex initializeVertex = new WeightedVertex(vertices[0]);

        float gamma = initializeVertex.weight / (initializeDegree - 1); // initialize gamma value to compare with

        for (Integer v : vertices) {
            if (!graph.containsVertex(v)) {
                continue;
            }
            int degree = graph.degreeOf(v);
            WeightedVertex wv = new WeightedVertex(v);
            if (gamma > wv.weight / (degree - 1)) { // set new min gamma value
                gamma = (wv.weight / (degree - 1));
            }
        }

        return gamma;
    }


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
        float gammaCase1, gammaCase2;
        Multigraph<Integer, DefaultEdge> tempgraph = (Multigraph<Integer, DefaultEdge>) ingraph.clone();
        ReductionSolution solution = new ReductionSolution();
        solution.reducedGraph = graph;


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
             if (!solution.reducedGraph.containsVertex(v)) {
                 continue;
             }

             WeightedVertex u = new WeightedVertex(v);
             int degree = solution.reducedGraph.degreeOf(u.id);

             // cleanup G (again) until nog more vertices with degrees <=1 exist
             cleanUp(solution, v, degree);

             if (degree <= 1) { // safety check
                 continue;
             }

             // we now check if G contains semidisjoint cycles [SDC] (plural)
             // • This includes steps that resemble kernelization rule 2, but rule 2 is executed slightly different
             // • If a vertex isn't a member of an SDC, we reduce its weight by gamma := min{w(u)/(d(u) − 1) : u ∈ V }, for vertex u with weight w(u) and degree d(u)
             // • gamma reduction creates and ordering of vertices for STACK, which is used to check for redundancy
             if (degree == 2) {
                 List<Integer> semiDisjointCycle = new ArrayList();
                 List<Integer> leftNeighbors;
                 List<Integer> rightNeighbors;

                 semiDisjointCycle.add(v);

                 List<Integer> neighbors = Graphs.neighborListOf(solution.reducedGraph, v);
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
                     int degreeLeft = solution.reducedGraph.degreeOf(left);
                     Integer l1;
                     Integer l2;
                     Integer leftException;
                     int degreeRight = solution.reducedGraph.degreeOf(right);
                     Integer r1;
                     Integer r2;
                     Integer rightException;
                     while (degreeLeft == 2) { // still potential vertex contained SDC?
                         leftNeighbors = Graphs.neighborListOf(solution.reducedGraph, left);
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
                         rightNeighbors = Graphs.neighborListOf(solution.reducedGraph, right);
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

                     /**
                      * We now set gamma value for current iteration and subtract from the appropriate vertices
                      * • Case 1: SDC was found; gamma = min{w(u) : u ∈ V (C)}, for vertex-weights w(u) and semidisjoint cycle C
                      * • Case 2: no SDC was found; gamma = min{w(u)/(d(u) − 1) : u ∈ V }, for degree d(u)
                      *
                      * Note 1: We skip over vertices with degree <= 1 because these are technically still contained within our graph,
                      * even though we already performed a cleanUp on it.
                      * Note 2: gamma changes for every iteration of the for-loop following this one, depending on the case of either
                      * finding a semidisjoint cycle (SDC) or not.
                      */

                     // An SDC may contain at most 1 exception, so we must have that (leftException == rightException)
                     if (leftException == rightException) { // Case 1
                         gammaCase1 = gammaCase1(solution.reducedGraph, semiDisjointCycle);
                         for (Integer c : semiDisjointCycle) {
                             for (Integer w : vertices) {
                                 if (!solution.reducedGraph.containsVertex(w)) {
                                     continue;
                                 }
                                 if (w == c) {
                                     WeightedVertex wv = new WeightedVertex(w);
                                     wv.weight = wv.weight - gammaCase1;
                                     if (wv.weight <= 0) {
                                         if (!approxVerticesToRemoved.contains(w)) {
                                             approxVerticesToRemoved.add(w); // add to approx solution
                                         }
                                         solution.reducedGraph.removeVertex(w); // update G-F
                                     }
                                 }
                             }
                         }
                     } else { // Case 2
                         gammaCase2 = gammaCase2(solution.reducedGraph, vertices);
                         for (Integer w : vertices) {
                             if (!solution.reducedGraph.containsVertex(w)) {
                                 continue;
                             }
                             WeightedVertex wv = new WeightedVertex(w);
                             wv.weight = wv.weight - gammaCase2 * (degree - 1);
                             if (wv.weight <= 0) {
                                 if (!approxVerticesToRemoved.contains(w)) {
                                     approxVerticesToRemoved.add(w); // add to approx solution
                                 }
                                 solution.reducedGraph.removeVertex(w); // update G-F
                             }
                         }
                     }
                     semiDisjointCycle.clear(); // prep for next iteration
                 } // endif (left != right)
             } else { // endif (degree == 2)
                 // gamma value Case 2 again
                 gammaCase2 = gammaCase2(solution.reducedGraph, vertices);
                 for (Integer w : vertices) {
                     if (!solution.reducedGraph.containsVertex(w)) {
                         continue;
                     }
                     WeightedVertex wv = new WeightedVertex(w);
                     wv.weight = wv.weight - gammaCase2 * (degree - 1);
                     if (wv.weight <= 0) {
                         if (!approxVerticesToRemoved.contains(w)) {
                             approxVerticesToRemoved.add(w); // add to approx solution
                         }
                         solution.reducedGraph.removeVertex(w); // update G-F
                     }
                 }
             }
         }// endfor (v:vertices)

        // The previous steps empties G completely, so now we create forest G-F from scratch
        boolean GminusF = tempgraph.removeAllVertices(approxVerticesToRemoved);
        if(GminusF){
            solution.reducedGraph = tempgraph;
        } else {
            System.out.println("ERROR: Something wen't wrong when creating G - F.");
        }

        // Current status: Any semidisjoint cycle (SDC) contained within G is now in F. G-F contains no (more) SDC
        // We now filter out redundant vertices from F, since we added entire cycles instead of singular vertices
        UnionFind<Integer> union = new UnionFind(solution.reducedGraph.vertexSet());
        ArrayList<Integer> toBeRemoved = new ArrayList();
        for (Integer v : approxVerticesToRemoved ) {
            if (solution.verticesToRemoved.contains(v)) continue; // if already added to definitive solution F

            LinkedList<DefaultEdge> edges = new LinkedList(ingraph.edgesOf(v));// all edges from v in the original graph G
            List<Integer> neighbors = new ArrayList();
            for (DefaultEdge e:edges) {
                Integer neighbor = Graphs.getOppositeVertex(ingraph, e, v);
                if (solution.reducedGraph.containsVertex(neighbor)) { // if neighbor in G-F
                    if (!neighbors.contains(neighbor)) neighbors.add(neighbor);
                }
            }
            TreeSet<Integer> neighborComponents = new TreeSet();
            boolean hasDuplicates = false; // check if v is connected to the same component more than once == duplicate

            for ( Integer n:neighbors ) {
                if (solution.reducedGraph.containsVertex(n)) { // if n in G - F
                    neighborComponents.add(union.find(n));
                    hasDuplicates |= !neighborComponents.add(union.find(n));
                }
                if (hasDuplicates) break; // v is essential
            }

            if(!hasDuplicates){ // v is redundant
                union.addElement(v);
                for ( Integer n:neighbors ) {
                    if (solution.reducedGraph.containsVertex(n)) { // if n in G - F
                        union.union(v, n);
                    }
                }
                solution.reducedGraph.addVertex(v); // add vertex back to G-F
                toBeRemoved.add(v); // equivalent for "STACK" from FEEDBACK (paper ALG)
            } else {
                solution.verticesToRemoved.add(v); // add v to definitive solution F
                solution.reducedK -= 1; // reduce k by one
            }
        }
        approxVerticesToRemoved.removeAll(toBeRemoved); // remove (e.g. "pop STACK") after loop

        // now, to account for any vertex whose weight was artificially increased:
        int c = 0;
        for (int v : solution.verticesToRemoved ) {
            for (int w : weightedVertices){
                if(v == w) c++;
            }
        }
        int total_FVS_weight = solution.verticesToRemoved.size() + c*(weight-1);
        solution.totalFVSweight = total_FVS_weight;

        return solution; // where: solution.verticesToRemoved = F
    }

}
