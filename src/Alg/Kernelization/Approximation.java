package Alg.Kernelization;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.alg.util.UnionFind;
import sun.awt.image.ImageWatched;

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
 *
 * The value for gamma depends on the following cases:
 * • Case 1: SDC was found; gamma = min{w(u) : u ∈ V (C)}, for vertex-weights w(u) and semidisjoint cycle C
 * • Case 2: no SDC was found; gamma = min{w(u)/(d(u) − 1) : u ∈ V }, for degree d(u)
 * ~Note that the value for gamma changes for every iteration
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
    public static float gammaCase1(Multigraph<Integer, DefaultEdge> graph, List<WeightedVertex> semiDisjointCycle)
    {
        float gamma = semiDisjointCycle.get(0).weight;
        for (WeightedVertex c : semiDisjointCycle) {
            if (c.weight < gamma) {
                gamma = c.weight;
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
        int initializeDegree = -1;
        WeightedVertex initializeVertex = new WeightedVertex(-1);
        for(int i=0; i<graph.vertexSet().size(); i++) {
            if(graph.containsVertex(vertices[i])) {
                initializeDegree = graph.degreeOf(vertices[i]);
                initializeVertex = new WeightedVertex(vertices[i]);
                break;
            }
        }

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
        Deque<Integer> STACK = new ArrayDeque();


        Integer[] vertices = (graph.vertexSet()).toArray(new Integer[graph.vertexSet().size()]);
        return Approximation.determineFVS(ingraph, graph, vertices, STACK, weightedVertices, weight);

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
    public static ReductionSolution determineFVS(Multigraph<Integer, DefaultEdge> ingraph, Multigraph<Integer, DefaultEdge> graph, Integer[] vertices, Deque<Integer> STACK, Integer[] weightedVertices, int weight){
        float gammaCase1, gammaCase2;

        ReductionSolution solution = new ReductionSolution();
        solution.reducedGraph = graph;

        /**
         * Iterative reduction of G to G-F by checking for semidisjoint cycles.
         *
         * We fill the STACK with all vertices with weight reduced to 0. After that, we remove the vertices from
         * this STACK that turn out to be redundant and add the rest to our solution F.
         */

         for (Integer v : vertices) {
             if (!solution.reducedGraph.containsVertex(v)) {
                 continue;
             }

             WeightedVertex u = new WeightedVertex(v);
             int degree = solution.reducedGraph.degreeOf(u.id);

             if (degree <= 1) { // safety check; however, this should never occur
                 continue;
             }

             // we now check if G contains semidisjoint cycles [SDC] (plural)
             // • This includes steps that resemble kernelization rule 2, but rule 2 is executed slightly different
             // • If a vertex isn't a member of an SDC, we reduce its weight by gamma := min{w(u)/(d(u) − 1) : u ∈ V }, for vertex u with weight w(u) and degree d(u)
             // • gamma reduction creates an ordering of vertices for STACK, which is used to check for redundant vertices later on
             if (degree == 2) {
                 List<WeightedVertex> semiDisjointCycle = new ArrayList();
                 List<Integer> leftNeighbors;
                 List<Integer> rightNeighbors;

                 List<Integer> neighbors = Graphs.neighborListOf(solution.reducedGraph, v);
                 WeightedVertex leftNeighbor = new WeightedVertex(neighbors.get(0));
                 WeightedVertex rightNeighbor = new WeightedVertex(neighbors.get(1));

                 // Create new vertex placeholders that will be overwritten in the loops
                 Integer predecessor = u.id;
                 Integer vertexPlaceholder = -1;

                 // prematurely add vertices to our potential semidisjointCycle container
                 semiDisjointCycle.add(u);
                 semiDisjointCycle.add(leftNeighbor);
                 semiDisjointCycle.add(rightNeighbor);

                 if (leftNeighbor == rightNeighbor) { // we have a self-loop -> remove it
                     Kernelization.removeVertex(solution, u.id, false);
                     Kernelization.removeVertex(solution, leftNeighbor.id, true);
                 } else { // check if degrees of both neighbors uplod the properties of an SDC
                     int degreeLeftNeighbor = solution.reducedGraph.degreeOf(leftNeighbor.id);
                     WeightedVertex l1; // placeholder for one of the neighbors of leftNeighbor
                     WeightedVertex l2; // placeholder for one of the neighbors of leftNeighbor
                     WeightedVertex leftException; // placeholder for leftNeighbor.neighbor that violates SDC rules
                     int degreeRightNeighbor = solution.reducedGraph.degreeOf(rightNeighbor.id);
                     WeightedVertex r1; // placeholder for one of the neighbors of rightNeighbor
                     WeightedVertex r2; // placeholder for one of the neighbors of rightNeighbor
                     WeightedVertex rightException; // placeholder for rightNeighbor.neighbor that violates SDC rules

                     while (degreeLeftNeighbor == 2) { // still potential vertex contained SDC?
                         leftNeighbors = Graphs.neighborListOf(solution.reducedGraph, leftNeighbor.id);
                         vertexPlaceholder = leftNeighbor.id;
                         l1 = new WeightedVertex(leftNeighbors.get(0));
                         l2 = new WeightedVertex(leftNeighbors.get(1));
                         if (l1.id != predecessor) { // make sure the neighbor we process wasn't already looked at before
                             degreeLeftNeighbor = ingraph.degreeOf(l1.id); // get degree of v in original graph G
                             semiDisjointCycle.add(l1);
                             leftNeighbor = l1; // set leftNeighbor of next loop (this is why we needed vertexPlaceholder)
                         } else {
                             degreeLeftNeighbor = ingraph.degreeOf(l2.id);
                             semiDisjointCycle.add(l2);
                             leftNeighbor = l2; // set leftNeighbor of next loop (this is why we needed vertexPlaceholder)
                         }
                         predecessor = vertexPlaceholder; // remember vertex used in previous iteration to avoid reviewing it again
                     }
                     leftException = leftNeighbor; // semidisjoint cycle exception found
                     predecessor = u.id; //reset value for rightNeighbor-loop

                     while (degreeRightNeighbor == 2) { // still potential vertex contained SDC?
                         rightNeighbors = Graphs.neighborListOf(solution.reducedGraph, rightNeighbor.id);
                         vertexPlaceholder = rightNeighbor.id;
                         r1 = new WeightedVertex(rightNeighbors.get(0));
                         r2 = new WeightedVertex(rightNeighbors.get(1));
                         if (r1.id != predecessor) { // make sure the neighbor we process wasn't already looked at before
                             degreeRightNeighbor = ingraph.degreeOf(r1.id); // get degree of v in original graph G
                             semiDisjointCycle.add(r1);
                             rightNeighbor = r1; // set leftNeighbor of next loop (this is why we needed vertexPlaceholder)
                         } else {
                             degreeRightNeighbor = ingraph.degreeOf(r2.id);
                             semiDisjointCycle.add(r2);
                             rightNeighbor = r2; // set leftNeighbor of next loop (this is why we needed vertexPlaceholder)
                         }
                         predecessor = vertexPlaceholder; // remember vertex used in previous iteration to avoid reviewing it again
                     }
                     rightException = rightNeighbor; // semidisjoint cycle exception found

                     // An SDC may contain at most 1 exception, so we must have that (leftException == rightException)
                     if (leftException == rightException) { // Case 1: SDC found in current graph
                         gammaCase1 = gammaCase1(solution.reducedGraph, semiDisjointCycle);
                         for (WeightedVertex c : semiDisjointCycle) { // for all members of the cycle
                             for (Integer w : vertices) {
                                 if (!solution.reducedGraph.containsVertex(w)) {
                                     continue;
                                 }
                                 if (w == c.id) {
                                     c.weight = c.weight - gammaCase1;
                                     if (c.weight <= 0) {
                                         STACK.push(c.id); // add vertex to STACK
                                         solution.reducedGraph.removeVertex(c.id); // update G-F
                                     }
                                 }
                             }
                         }
                     } else { // Case 2: no SDC found in current graph
                         gammaCase2 = gammaCase2(solution.reducedGraph, vertices);
                         u.weight = u.weight - gammaCase2 * (degree - 1); // only for the observed vertex
                         if (u.weight <= 0) {
                             if (solution.reducedGraph.containsVertex(u.id)) {
                                 STACK.push(u.id);
                                 solution.reducedGraph.removeVertex(u.id); // update G-F
                             }
                         }
                     }
                     semiDisjointCycle.clear(); // clear collection for next iteration
                 } // endif (left != right)
             } else { // endif (degree == 2)
                 // in case we know for certain that the vertex does not belong to an SDC, immediately do:
                 gammaCase2 = gammaCase2(solution.reducedGraph, vertices);
                 u.weight = u.weight - gammaCase2 * (degree - 1); // only for the observed vertex
                 if (u.weight <= 0) {
                     if (solution.reducedGraph.containsVertex(u.id)) {
                         STACK.push(u.id);
                         solution.reducedGraph.removeVertex(u.id); // update G-F
                     }
                 }
             }

             // cleanup G (again) until no more vertices with degrees <=1 exist
             cleanUp(solution, v, degree);
         }// endfor (v:vertices)



        // At this point, G-F contains no (more) SDC and STACK contains all potential vertices from the solution.
        // • We now filter out redundant vertices by popping vertices from STACK and checking whether placing them back
        //   into G-F would create a cycle. We do this using UnionFind
        UnionFind<Integer> union = new UnionFind(solution.reducedGraph.vertexSet());
        while (!STACK.isEmpty()){
            Integer currentVertex = STACK.peek(); // view top item from stack

            // get all edges from current vertex in the original graph G
            LinkedList<DefaultEdge> edges = new LinkedList(ingraph.edgesOf(currentVertex));
            // get all corresponding neigbors n and, if n in G-F, store them in collection: neighbors
            List<Integer> neighbors = new ArrayList();
            for (DefaultEdge e:edges) {
                neighbors.add(Graphs.getOppositeVertex(ingraph, e, currentVertex));
            }

            // check if v is connected to the same component more than once using a treeset (duplicates)
            TreeSet<Integer> neighborComponents = new TreeSet();
            boolean hasDuplicates = false;
            // check for multiple neighbors of currentVertex that are members of the same component
            for ( Integer n:neighbors ) {
                if(solution.reducedGraph.containsVertex(n)) hasDuplicates |= !neighborComponents.add(union.find(n));
                if (hasDuplicates) break; // we found a loop
            }

            // in case we didn't find a loop, currentVertex is redundant
            if(!hasDuplicates){
                union.addElement(currentVertex); // add currentVertex back into G-F
                for ( Integer n:neighbors ) {
                    if (solution.reducedGraph.containsVertex(n)) {
                        union.union(currentVertex, n); // connect the vertex to its neighbors in G-F (UnionFind components)
                    }
                }
                solution.reducedGraph.addVertex(currentVertex); // add vertex back to G-F
            } else { //if we found a loop, currentVertex is essential
                solution.verticesToRemoved.add(currentVertex); // add currentVertex to solution F
            }
            STACK.pop();
        }

        // Next we update any vertex whose weight was artificially increased for certain reduction rules
        int c = 0;
        for (int v : solution.verticesToRemoved ) {
            for (int w : weightedVertices){
                if(v == w) c++;
            }
        }
        solution.totalFVSweight = solution.verticesToRemoved.size() + c*(weight-1);

        return solution;
    }

    /**
     * Determine a weighted 2-approximation FVS of a graph G, where vertices have weight 1, or weight w if specified
     *
     * @param ingraph The graph G
     * @param weightedVertices Vertices with weight w
     * @param weight Weight w
     * @return
     */
    public static ReductionSolution determineFVS2(Multigraph<Integer, DefaultEdge> ingraph, Integer[] weightedVertices, int weight){

        ReductionSolution solution = new ReductionSolution();
        // VerticesToRemoved acts as solution set F
        solution.reducedGraph = (Multigraph<Integer, DefaultEdge>) ingraph.clone();
        // Skip initial filling of F, by definition, no weights are 0
        Stack<Integer> stack = new Stack<>(); // The STACK

        cleanUp2(solution.reducedGraph, new LinkedList<>(solution.reducedGraph.vertexSet()));

        for(Integer v: ingraph.vertexSet()){

        }




        //Determine solution weight
        solution.totalFVSweight = 0;
        for (Integer v: solution.verticesToRemoved){
            solution.totalFVSweight++;
            for (Integer u: weightedVertices){
                if (v.equals(u)){
                    solution.totalFVSweight += weight-1;
                }
            }
        }

        return solution;
    }

    public static void cleanUp2(Multigraph<Integer, DefaultEdge> graph, LinkedList<Integer> relevantVertices){
        while (!relevantVertices.isEmpty()) {
            Integer current = relevantVertices.pop();
            if(graph.containsVertex(current)){
                if(graph.degreeOf(current) <= 1){
                    relevantVertices.addAll(Graphs.neighborListOf(graph, current));
                    graph.removeVertex(current);
                }
            }
        }
    }

}
