/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import java.util.Collection;
import java.util.HashSet;

import Alg.Kernelization.Kernelization;
import Alg.Kernelization.ReductionSolution;
import Alg.Kernelization.SimpleDisjointKernelization;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

/**
 *
 * @author huib
 */
class SimpleDisjointAlg<V> implements DisjointFVSAlgorithm<V>
{
    @Override
    public Collection<V> solve(Multigraph<V, DefaultEdge> g, HashSet<V> prohibited)
    {
        return this.solve((Multigraph<V, DefaultEdge>) g.clone(), prohibited, prohibited.size()-1);
    }


    // ALGORITHM
    // 1. Check for a cycle in the graph consisting of only vertices in prohibited
    //    If there exists such a cycle, return null (no FVS disjoint of prohibited is possible)
    // 2. Exhaustively apply reduction rules (you can stop before when the solution grows too
    //    big, see step 3)
    // 3. When after applying the reduction rules, the intermediate solution (created by rule 2)
    //    is larger of equal to prohibited.size(), return null. There is no solution small enough
    // There is a vertex v not in prohibited with degree 1 (not counting edges to vertices in
    // prohibited)
    // 4. Find a vertex v not in prohibited with exectly one neightbour that is not in
    //    prohibited. Try this.solve(g, prohibited+v, k), otherwise return
    //    this.solve(g-v, prohibited, k-1)

    /**
     * Solve the Simple Disjoint algorithm problem
     *
     * @param graph
     * @param prohibited
     * @param k
     * @return
     */
    private Collection<V> solve(Multigraph<V, DefaultEdge> graph, HashSet<V> prohibited, int k)
    {
        // 1. Check for a cycle in the graph consisting of only vertices in prohibited
        //    If there exists such a cycle, return null (no FVS disjoint of prohibited is possible)
        if (this.containsCycleWithOnlyProhibited(graph, prohibited)) {
            return null;
        }

        // 2. Exhaustively apply reduction rules (you can stop before when the solution grows too
        //    big, see step 3)
        ReductionSolution red = this.applyReductionRules(graph, prohibited, k);

        // 3. When after applying the reduction rules, the intermediate solution (created by rule 2)
        //    is larger of equal to prohibited.size(), return null. There is no solution small enough
        if (red == null || red.verticesToRemoved.size() > k) {
            return null;
        }

        // 3' When the reduction rules left no vertices to be included in the solution
        //    prohibited imposes a tree on graph
        //    when the graph contains no other vertices than the ones in prohibited, the graph is
        //    a tree, i.e. solution is the empty set.
        
        // There is a vertex v not in prohibited with degree 1 (not counting edges to vertices in
        // prohibited)
        // 4. Find a vertex v not in prohibited with exectly one neightbour that is not in
        //    prohibited. Try this.solve(g, prohibited+v, k), otherwise return
        //    this.solve(g-v, prohibited, k-1)
        V vertex = this.findVertexWithOneNonProhibitedNeighbour(graph, prohibited);
        if(vertex == null) // no nonprohibited vertices in the graph
            return new HashSet<>();
        
        prohibited.add(vertex);

        Collection<V> solution = this.solve(graph, prohibited, k);
        if (solution != null) {
            return solution;
        }
        prohibited.remove(vertex);

        graph.removeVertex(vertex);
        solution = this.solve(graph, prohibited, k-1);
        solution.add(vertex);
        return solution;
    }

    /**
     * Find a vertex in the graph that is not in prohibited, and has exactly one neighbour that is not in prohibited
     *
     * @param graph
     * @param prohibited
     * @return
     */
    protected V findVertexWithOneNonProhibitedNeighbour(Multigraph<V, DefaultEdge> graph, HashSet<V> prohibited)
    {
        // other code didn't make vertex type generic.. so we'll have to do it non generic too now..
        Multigraph<Integer, DefaultEdge> integerGraph = (Multigraph<Integer, DefaultEdge>) graph;
        
        boolean atLeastOneNonProhibited = false;
        for (V v:graph.vertexSet()) {
            if (prohibited.contains(v)) {
                continue;
            }
            atLeastOneNonProhibited = true;
            int nonProhibitedNeighbours = SimpleDisjointKernelization.getNeighbours(integerGraph, (Integer)v)
                   .mapToInt(neighbour -> prohibited.contains(neighbour) ? 0 : 1)
                   .sum();
            if (nonProhibitedNeighbours <= 1) {
                return v;
            }
        }

        // + Stefan If I understand correctly this is what we want to find? Uncommented for now
        // + Huib, we want to find at least one non prohibited, but one of them should have at most
        // one non prohibited neighbour, meaning it passes the second 'if' in the loop, returning.
        // if we reach the statements below it means we haven't found a non prohibited vertex with
        // at most one non prohibited neighbour. Since the vertices not in prohibited form a forrest
        // the only way for there not to be a vertex without degree 1 (after removing prohibited), is
        // when removing prohibited results in an EMPTY graph. So, if we have encountered
        // atLeastOneNonProhibited, but it did not have degree at most 1, something is wrong.
        if(atLeastOneNonProhibited)
            throw new IllegalStateException("There should be a vertex satisfying these properties, but there is not, so there must be something wrong..");

        return null;
    }

    // REDUCTION RULES
    // 1. remove vertices with degree at most 1.
    // 2. remove any vertex v not in prohibited that is part of a cycle where all other vertices
    //    are in prohibited. Add v to the solution.
    // 3. remove any vertex v not in prohibited with degree 2 and at least one of its
    //    neightbours also not in prohibited. Connect the neightbours of v.

    /**
     * Applies the reduction rules to the graph
     *
     * @param graph
     * @param prohibited
     */
    protected ReductionSolution applyReductionRules(Multigraph<V, DefaultEdge> graph, HashSet<V> prohibited, int k)
    {
        // other code didn't make vertex type generic.. so we'll have to do it non generic too now..
        Multigraph<Integer, DefaultEdge> integerGraph = (Multigraph<Integer, DefaultEdge>) graph;
        HashSet<Integer> integerProhibited = (HashSet<Integer>) prohibited;
        
        ReductionSolution reductionSolution = new ReductionSolution();
        reductionSolution.reducedGraph = graph;

        // Apply the reduction rules exhaustively
        while (reductionSolution.verticesToRemoved.size() <= k) {
            boolean changed = false;

            // Applies reduction rule 1 to the graph
            changed |= Kernelization.rule0and1(reductionSolution, integerGraph);
            // If one of the prohibited vertices is removed, it must be removed from prohibited as well, otherwise the rest of the
            // algorithm

            // Applies reduction rule 2 on the graph
            changed |= SimpleDisjointKernelization.removeOnlyVertexInProhibitedCycle(reductionSolution, integerGraph, integerProhibited);

            // Applies reduction rule 3 on the graph
            changed |= SimpleDisjointKernelization.removeNonProhibitedVertexWithDegree2(reductionSolution, integerGraph, integerProhibited);

            if (!changed) {
                return reductionSolution;
            }
        }
        
        // if we get here, our reduction rules found that for this problem we need to remove at least
        // k vertices. This is too many.
        return null;
    }

    /**
     * Checks if there is a cycle in the graph consisting only of vertices in the prohibited list
     *
     * @param graph
     * @param prohibited
     * @return
     */
    protected boolean containsCycleWithOnlyProhibited(Multigraph<V, DefaultEdge> graph, HashSet<V> prohibited)
    {
        // other code didn't make vertex type generic.. so we'll have to do it non generic too now..
        Multigraph<Integer, DefaultEdge> integerGraph = (Multigraph<Integer, DefaultEdge>) graph;
        HashSet<Integer> integerProhibited = (HashSet<Integer>) prohibited;
        
        for (V v : prohibited) {
            // The vertex may already be removed, since it may have a degree of 1 at some point
            if (!graph.containsVertex(v)) {
                continue;
            }
            if (SimpleDisjointKernelization.inCycleWith((Integer)v, integerGraph, integerProhibited)) {
                return true;
            }
        }
        return false;
    }


}
