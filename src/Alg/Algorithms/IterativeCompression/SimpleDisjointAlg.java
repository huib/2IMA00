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
class SimpleDisjointAlg implements DisjointFVSAlgorithm
{
    @Override
    public Collection<Integer> solve(Multigraph<Integer, DefaultEdge> g, HashSet<Integer> prohibited)
    {
        return this.solve(g, prohibited, prohibited.size()-1);
    }
    
    private Collection<Integer> solve(Multigraph<Integer, DefaultEdge> g, HashSet<Integer> prohibited, int k)
    {
        ReductionSolution red = this.applyReductionRules(g, prohibited);


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
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
     * @param g
     * @param prohibited
     */
    protected ReductionSolution applyReductionRules(Multigraph<Integer, DefaultEdge> g, HashSet<Integer> prohibited)
    {
        ReductionSolution reductionSolution = new ReductionSolution();
        reductionSolution.reducedGraph = g;

        // Applies reduction rule 1 to the graph
        Kernelization.rule0and1(reductionSolution, g);

        // Applies reduction rule 2 on the graph
        SimpleDisjointKernelization.removeOnlyVertexInProhibitedCycle(reductionSolution, g, prohibited);

        // Applies reduction rule 3 on the graph
        SimpleDisjointKernelization.removeNonProhibitedVertexWithDegree2(reductionSolution, g, prohibited);

        return reductionSolution;
    }


}
