/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import Alg.FVSAlgorithmInterface;

import java.util.LinkedList;
import java.util.ListIterator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

/**
 *
 * @author huib
 */
public class IterativeCompression implements FVSAlgorithmInterface
{

    @Override
    public LinkedList<Integer> findFeedbackVertexSet(Multigraph<? extends Integer, ? extends DefaultEdge> graph)
    {
        ActionStack actions = new ActionStack();
        
        // delete all vertices from graph
        graph.vertexSet().stream().forEach((v) ->
        {
            actions.push(new DeleteVertexAction(graph, v));
        });
        
        // put the vertices back, one by one, in reverse order
        // with each vertex we put back, find a minimal FVS
        int k=0;
        FVS<Integer> solution = new FVS<>();
        while(!actions.isEmpty())
        {
            DeleteVertexAction<Integer> action = (DeleteVertexAction<Integer>) actions.pop();
            action.revert();
            solution.add(action.getVertex());
            
            if(solution.size() > k)
            {
                solution.compress(graph);
                k = Math.max(k, solution.size());
            }
        }
        
        return solution;
    }
    
    public class FVS<V> extends LinkedList<V>
    {
        public void compress(Multigraph<? extends Integer, ? extends DefaultEdge> graph)
        {
            // try every strict subset Z of the current solution C
            // remove this subset from the graph, G-Z
            // solve disjoint problem: find FVS in graph G-Z using only vertices in V(G)\C
            
            ActionStack action = new ActionStack();
            
            ListIterator<V> it = this.listIterator();
            
        }
    }
}
