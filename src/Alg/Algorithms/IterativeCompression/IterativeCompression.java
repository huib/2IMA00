/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import Alg.FVSAlgorithmInterface;
import java.util.HashSet;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

/**
 *
 * @author huib
 */
public class IterativeCompression implements FVSAlgorithmInterface
{

    @Override
    public Integer[] findFeedbackVertexSet(Multigraph<? extends Integer, ? extends DefaultEdge> graph)
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
        
        return (Integer[]) solution.toArray();
    }
    
    public class FVS<V> extends HashSet<V>
    {
        public void compress(Multigraph<? extends Integer, ? extends DefaultEdge> graph)
        {
            //try every strick subset of the current solution
        }
    }
}