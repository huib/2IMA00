/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import Alg.FVSAlgorithmInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

/**
 *
 * @author huib
 */
public class IterativeCompression implements FVSAlgorithmInterface
{

    @Override
    public List<Integer> findFeedbackVertexSet(Multigraph<Integer, DefaultEdge> graph)
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
    
    public class FVS<V> extends ArrayList<V>
    {
        public void compress(Multigraph<V, DefaultEdge> graph)
        {
            // try every strict subset Z of the current solution C
            // remove this subset from the graph, G-Z
            // solve disjoint problem: find FVS in graph G-Z using only vertices in V(G)\C
            
            DisjointFVSAlgorithm disjointSolver;
            disjointSolver = new SimpleDisjointAlg();
            
            for(Collection<V> subset : this.subsets())
            {
                if(subset.size() == this.size())
                    continue; // not a strict subset
                
                HashSet<V> complement = this.complementOf(subset);
                
                DeleteVerticesAction<V> removeVertices = new DeleteVerticesAction<>(graph, subset);
                removeVertices.perform();
                Collection<V> solution = disjointSolver.solve(graph, complement);
                removeVertices.revert();
                
                if(solution != null) // we found one!
                {
                    this.update(complement, solution);
                    return;
                }
            }
        }
        
        private void update(HashSet<V> remove, Collection<V> add)
        {
            assert remove.size() == add.size()+1;
            
            Iterator<V> it = add.iterator();
            
            for(int i=0; i<this.size(); i++)
            {
                V v = this.get(i);
                if(remove.contains(v))
                {
                    if(it.hasNext())
                    {
                        this.set(i, it.next());
                    }
                    else
                    {
                        this.remove(v);
                        return;
                    }
                }
            }
        }
        
        private Iterable<Collection<V>> subsets()
        {
            return () -> new SubsetIterator(FVS.this);
        }
        
        private HashSet<V> complementOf(Collection<V> set)
        {
            HashSet<V> complement = new HashSet<>();
            this.stream().filter((v) -> (!set.contains(v))).forEach((v) ->
            {
                complement.add(v);
            });
            
            return complement;
        }
    }
    
    public class SubsetIterator<V> extends HashSet<V> implements Iterator<Collection<V>>
    {
        private ArrayList<V> set;
        private long iteration = 0;
        
        public SubsetIterator(ArrayList<V> set)
        {
            this.set = set;
            
            if(this.set.size() > 64)
                throw new RuntimeException("Sorry, not going to do this sh.t, this would take ages");
            if(this.set.size() > 42)
                System.err.println("Iterating over all strict subsets of a large set, going to take a looong time, but your choice!");
            if(this.set.size() > 32)
                System.err.println("Iterating over all strict subsets of a set of size "+this.set.size()+" may take a long time");
        }
        
        @Override
        public boolean hasNext()
        {
            return this.getFlipIndex() < this.set.size();
        }

        @Override
        public Collection<V> next()
        {
            V flip = this.set.get(this.getFlipIndex());
            
            if(this.contains(flip))
                this.remove(flip);
            else
                this.add(flip);
            
            this.iteration++;
            
            return this;
        }
        
        private int getFlipIndex()
        {
            return Long.numberOfTrailingZeros(~this.iteration);
        }
    }
}
