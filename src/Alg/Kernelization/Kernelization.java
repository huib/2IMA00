package Alg.Kernelization;

import org.jgrapht.Graphs;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import sun.reflect.generics.tree.Tree;

import java.util.*;
import java.util.regex.Matcher;

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
            if(!graph.containsVertex(v)) continue;

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
     *
     * @param graph
     * @param k The parameter for maximum FVS size, if k<0, approximation will be used where required
     * @return
     */
    public static ReductionSolution kernelittle( Multigraph<Integer, DefaultEdge> graph, boolean cloneGraph, int k) {
        return kernelize( graph, k, cloneGraph, true, true);
    }


    /**
     *
     * @param graph
     * @return
     */
    public static ReductionSolution kernelittle( Multigraph<Integer, DefaultEdge> graph, boolean cloneGraph) {
        return kernelize( graph, 0, cloneGraph, true, false);
    }

    /**
     *
     * @param graph
     * @param k The parameter for maximum FVS size, if k<0, approximation will be used where required
     * @return
     */
    public static ReductionSolution kernelot( Multigraph<Integer, DefaultEdge> graph, boolean cloneGraph, int k) {
        return kernelize( graph, k, cloneGraph, false, true);
    }

    /**
     *
     * @param graph
     * @return
     */
    public static ReductionSolution kernelot( Multigraph<Integer, DefaultEdge> graph, boolean cloneGraph) {
        return kernelize( graph, 0, cloneGraph, false, false);
    }


    /**
     *
     * @param graph
     * @param k The parameter for maximum FVS size, if k<0, approximation will be used where required
     * @param cloneGraph Do we clone the graph, or work on the original graph directly
     * @param simpleOnly Do we perform only quick, simple kernelization, or include more costly reductions
     * @param useK Do we use the value for K from the input, or make our own where needed
     * @return
     */
    public static ReductionSolution kernelize( Multigraph<Integer, DefaultEdge> graph, int k, boolean cloneGraph, boolean simpleOnly, boolean useK) {
        ReductionSolution solution = new ReductionSolution();
        solution.reducedGraph = cloneGraph ? (Multigraph<Integer, DefaultEdge>) graph.clone(): graph;
        if(useK) solution.reducedK = k;
        else solution.reducedK = 0;
        solution.stillPossible = true;
        return kernelize(solution, solution.reducedGraph, simpleOnly, useK, false); //Don't use queue, overhead makes slower
    }


        /**
         *
         * @param solution
         * @param simpleOnly Do we perform only quick, simple kernelization, or include more costly reductions
         * @param useK Do we use the value for K from the input, or make our own where needed
         * @return
         */
    public static ReductionSolution kernelize( ReductionSolution solution, Multigraph<Integer, DefaultEdge> graph, boolean simpleOnly, boolean useK, boolean useQueue) {

        TreeSet<Integer> relevantVertices = new TreeSet<>();
        if(useQueue){
            // Set up set of relevant vertices
            for (Integer v: graph.vertexSet()) {
                relevantVertices.add(v);
            }
        }

        LinkedList<Integer> flowerCoreVertices = new LinkedList<>();
        if (!simpleOnly){
            flowerCoreVertices = new LinkedList(solution.reducedGraph.vertexSet());
            Collections.sort(flowerCoreVertices, (o1, o2) ->
                    solution.reducedGraph.degreeOf(o1)-solution.reducedGraph.degreeOf(o2));
        }

        boolean changed;
        do {
            changed = false;

            // Call Rule 4, reducing any multi edge of multiplicity >2 to multiplicity 2
            //rule4Q(solution, solution.reducedGraph.edgeSet());

            //Perform all reduction rules on vertex degree <3, eliminating self-loops and >2 multi edges along the way
            if(useQueue) simpleVertexRulesQ(solution, relevantVertices);
            else simpleVertexRules(solution);

            // Call Rule 5, if possible
            if (useK) {
                rule5(solution);
                if(!solution.stillPossible) return solution;

                // Return if finished, graph is empty
                if(solution.reducedGraph.vertexSet().isEmpty()) return solution;

                rule6(solution);
                if(!solution.stillPossible) return solution;
            }

            // Do advanced rules, if desired
            if (!simpleOnly) {

                int getApprox = solution.reducedGraph.vertexSet().size();
                int usedK = useK ? solution.reducedK : getApprox;

                relevantVertices.addAll(ruleSFV(solution, flowerCoreVertices, usedK));
                changed |= !relevantVertices.isEmpty();
            }
        } while (changed);

        return solution;
    }

    /**
     * Applies Rule 0, 1, 2 and 3 to the graph.
     *
     * @param solution
     * @param relevantVertices set of vertices to consider
     * @return set of changed vertices required to re-inspect
     */
    public static Set<Integer> simpleVertexRulesQ(ReductionSolution solution, TreeSet<Integer> relevantVertices)
    {
        while (!relevantVertices.isEmpty()) {
            Integer current = relevantVertices.last();
            relevantVertices.addAll(simpleVertexRulesQ(solution, current, relevantVertices));
            relevantVertices.remove(current);
        }
        return new TreeSet<>();
    }

    /**
     * Applies Rule 0, 1, 2 and 3 to the graph.
     *
     * @param solution
     * @return set of changed vertices required to re-inspect
     */
    public static void simpleVertexRules(ReductionSolution solution)
    {
        boolean changed;
        do {
            changed = false;
            Integer[] vertices = ((Set<Integer>) solution.reducedGraph.vertexSet()).toArray(new Integer[solution.reducedGraph.vertexSet().size()]);
            for(Integer v: vertices){
                if(solution.reducedGraph.containsVertex(v)) changed |= simpleVertexRules(solution, v);
            }
        } while (changed);
    }

    /**
     * Applies Rule 0, 1, 2 and 3 to the graph.
     *
     * @param solution
     * @return set of changed vertices required to re-inspect
     */
    public static Set<Integer> simpleVertexRulesQ(ReductionSolution solution, Integer v, TreeSet<Integer> relevantVertices)
    {
        Set<Integer> changedVertices;
        int d = solution.reducedGraph.degreeOf(v);
        changedVertices = rule0and1Q(solution, v, d);
        if(changedVertices.isEmpty()){
            changedVertices = rule2Q(solution, v, d, relevantVertices);
        }
        return changedVertices;
    }

    /**
     * Applies Rule 0, 1, 2 and 3 to the graph.
     *
     * @param solution
     * @return set of changed vertices required to re-inspect
     */
    public static boolean simpleVertexRules(ReductionSolution solution, Integer v)
    {
        boolean changed = false;
        int d = solution.reducedGraph.degreeOf(v);
        changed |= rule0and1(solution, v, d);
        if(!changed){
            changed |= rule2(solution, v, d);
        }
        return changed;
    }


    /**
     * Applies Rule 0 and 1 to the graph.
     *
     * @param solution
     * @param v
     * @return
     */
    public static Set<Integer> rule0and1Q(ReductionSolution solution, Integer v, int d)
    {
        TreeSet<Integer> changedVertices = new TreeSet<>();
        if(d<=1){
            changedVertices.addAll(Graphs.neighborListOf(solution.reducedGraph, v));
            removeVertex(solution, v, false);
        }
        return changedVertices;
    }


    /**
     * Applies Rule 0 and 1 to the graph.
     *
     * @param solution
     * @param v
     * @return
     */
    public static boolean rule0and1(ReductionSolution solution, Integer v, int d)
    {
        if(d<=1){
            removeVertex(solution, v, false);
            return true;
        }
        return false;
    }


    /**
     * Applies Rule 2 to the graph. Takes care to apply Rule 3 and 4 when needed.
     *
     * @param solution
     * @param v
     * @return
     */
    public static Set<Integer> rule2Q(ReductionSolution solution, Integer v, int d, TreeSet<Integer> relevantVertices)
    {
        TreeSet<Integer> changedVertices = new TreeSet<>();
        if(d==2){
            changedVertices.addAll(Graphs.neighborListOf(solution.reducedGraph, v));
            removeVertex(solution, v, false);
            //Check for rule 3
            if(changedVertices.first().equals(changedVertices.last())){
                removeVertex(solution, changedVertices.first(), true);
                relevantVertices.remove(changedVertices.first());
                return new TreeSet<>();
            } else{
                solution.reducedGraph.addEdge(changedVertices.first(), changedVertices.last());
                rule4Q(solution, solution.reducedGraph.edgesOf(changedVertices.first()));
            }
        }
        return changedVertices;
    }


    /**
     * Applies Rule 2 to the graph. Takes care to apply Rule 3 and 4 when needed.
     *
     * @param solution
     * @param v
     * @return
     */
    public static boolean rule2(ReductionSolution solution, Integer v, int d)
    {
        return !rule2Q(solution, v, d, new TreeSet<>()).isEmpty();
    }


    /**
     * Applies Rule 4 to the graph, with respect to a set of edges.
     *
     * @param solution
     * @return
     */
    public static Set<Integer> rule4Q(ReductionSolution solution, Set<DefaultEdge> relevantEdges)
    {
        Multigraph<Integer, DefaultEdge> graph = solution.reducedGraph;
        LinkedList<DefaultEdge> edges = new LinkedList(graph.edgeSet());
        Collections.sort(edges, (o1, o2) -> {
            int a = Math.max(graph.getEdgeSource(o1), graph.getEdgeTarget(o1))
                    - Math.max(graph.getEdgeSource(o2), graph.getEdgeTarget(o2));
            if ( a > 0 ) {
                return 1;
            }
            if ( a < 0 ) {
                return -1;
            }
            return Math.min(graph.getEdgeSource(o1), graph.getEdgeTarget(o1))
                    - Math.min(graph.getEdgeSource(o2), graph.getEdgeTarget(o2));
        });
        Iterator<DefaultEdge> it = edges.iterator();

        // If the iterator does not have a next item, we do not have edges. Thus rule 1 will take care of this
        // in the next run
        TreeSet<Integer> changedVertices = new TreeSet<>();

        if (!it.hasNext()) return changedVertices;
        DefaultEdge twoBack = it.next();

        if (!it.hasNext()) return changedVertices;
        DefaultEdge oneBack = it.next();

        while (it.hasNext()) {
            DefaultEdge current = it.next();
            if ( Math.max(graph.getEdgeSource(twoBack), graph.getEdgeTarget(twoBack)) ==
                    Math.max(graph.getEdgeSource(current), graph.getEdgeTarget(current)) &&
                    Math.min(graph.getEdgeSource(twoBack), graph.getEdgeTarget(twoBack)) ==
                    Math.min(graph.getEdgeSource(current), graph.getEdgeTarget(current))
                    ) {
                changedVertices.add(graph.getEdgeSource(current));
                changedVertices.add(graph.getEdgeTarget(current));
                graph.removeEdge(current);
                continue;
            }
            twoBack = oneBack;
            oneBack = current;
        }
        return changedVertices;
    }


    /**
     * Applies Rule 5 to the graph.
     *
     * @param solution
     * @return
     */
    public static boolean rule5(ReductionSolution solution)
    {
        if(solution.reducedK < 0){
            solution.stillPossible = false;
        }
        return solution.stillPossible;
    }


    /**
     * Applies Rule 6 to the graph.
     *
     * @param solution
     * @return
     */
    public static boolean rule6(ReductionSolution solution)
    {
        int maxDegree = 0;
        Multigraph<Integer, DefaultEdge> graph = solution.reducedGraph;
        for (Integer v: graph.vertexSet()){
            maxDegree = Math.max(maxDegree, graph.degreeOf(v));
        }
        if(graph.vertexSet().size() >= solution.reducedK*(1+maxDegree)){
            solution.stillPossible = false;
        }
        if(graph.edgeSet().size() >= solution.reducedK*(2*maxDegree)){
            solution.stillPossible = false;
        }
        return solution.stillPossible;
    }


    /**
     * Applies the Strongly Forced Vertex Rule to the graph.
     *
     * @param solution
     * @return
     */
    public static Set<Integer> ruleSFV(ReductionSolution solution, LinkedList<Integer> flowerCoreVertices, int k)
    {
        Set<Integer> changedVertices;
        while(!flowerCoreVertices.isEmpty()){
            changedVertices = ruleSFV(solution, flowerCoreVertices.pollFirst(), k);
            if(!changedVertices.isEmpty()){
                return changedVertices;
            }
        }
        return new TreeSet<>();
    }


    /**
     * Applies the Strongly Forced Vertex Rule to the graph.
     *
     * @param solution
     * @return
     */
    public static Set<Integer> ruleSFV(ReductionSolution solution, Integer v, int k)
    {
        TreeSet<Integer> changedVertices = new TreeSet<>();
        //Get approximation with uniform weights, except v with weight 2k+1
        int getApprox = solution.reducedGraph.vertexSet().size();
        if (getApprox >= (2*k+1)) {
            // v should be removed, all neighbours flagged for change
            // (should remove neighbours as well and flagg their neighbours??)
            changedVertices.addAll(Graphs.neighborListOf(solution.reducedGraph, v));
            removeVertex(solution, v, true);
        }
        return changedVertices;
    }

    /**
     * Helper function to remove a vertex from the graph
     *
     * @param solution The solution found thus far
     * @param vertex Vertex that needs to be removed
     * @param inSolution IF the vertex needs to be in the solution, or if it can be removed, but is not in the solution
     */
    public static void removeVertex(ReductionSolution solution, int vertex, boolean inSolution) {
        solution.reducedGraph.removeVertex(vertex);

        if (inSolution) {
            solution.verticesToRemoved.add(vertex);
            solution.reducedK -= 1;
        }
    }
}
