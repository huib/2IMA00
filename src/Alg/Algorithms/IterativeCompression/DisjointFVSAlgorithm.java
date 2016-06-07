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
 * Made this an interface, because I can imagine multiple implementations for the disjoint problem,
 * for example one where we take into account the previous runs where the graph and prohibited set
 * were only slightly different from the current run.
 * 
 * @author huib
 */
public interface DisjointFVSAlgorithm
{
    /**
     * Returns a feedback vertex set x for graph g that satisfies the following:
     * - x does not contains vertices from the set prohibited
     * - x has size prohibited.size()-1
     * , returns null of no such feedback vertex set exists.
     * @param g - graph for which to find the FVS
     * @param prohibited - A feedback vertex set for g, for which we have prohibited to use its
     * vertices for our own FVS.
     * @return a FVS of size prohibited.size()-1 of g, not containing any vertex in prohibited.
     */
    public Collection<Integer> solve(Multigraph<Integer, DefaultEdge> g, HashSet<Integer> prohibited);
}
