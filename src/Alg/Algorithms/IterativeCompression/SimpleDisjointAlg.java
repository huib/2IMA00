/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import java.util.Collection;
import java.util.HashSet;
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
        return this.solve(g, prohibited, prohibited.size()-1);
    }
    
    private Collection<V> solve(Multigraph<V, DefaultEdge> g, HashSet<V> prohibited, int k)
    {
        // REDUCTION RULES
        // 1. remove vertices with degree at most 1.
        // 2. remove any vertex v not in prohibited that is part of a cycle where all other vertices
        //    are in prohibited. Add v to the solution.
        // 3. remove any vertex v not in prohibited with degree 2 and at least one of its
        //    neightbours also not in prohibited. Connect the neightbours of v.
        
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
        
        return null; // act as if there is no solution that is small enough.
    }
}
