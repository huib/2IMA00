/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import Alg.FVSAlgorithmInterface;
import Alg.GraphDisplayer;
import Alg.Kernelization.SimpleDisjointKernelization;
import Alg.Lib.CycleDetector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import Alg.Kernelization.Kernelization;
import Alg.Kernelization.ReductionSolution;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

/**
 *
 * @author huib
 */
public class IterativeCompression implements FVSAlgorithmInterface
{

    @Override
    public List<Integer> findFeedbackVertexSet(ReductionSolution partialSolution){
        List<Integer> result = findFeedbackVertexSet(partialSolution.reducedGraph);
        result.addAll(partialSolution.verticesToRemoved);
        return result;
    }

    @Override
    public List<Integer> findFeedbackVertexSet(Multigraph<Integer, DefaultEdge> graph)
    {
        ActionStack actions = new ActionStack();
        
        int nVertices = graph.vertexSet().size();
        
        // sort them on degree (not needed, but might speed up the algorithm)
        //*
        List<Integer> vertices = new ArrayList(graph.vertexSet());
        Collections.sort(vertices, (Object o1, Object o2) ->
        {
            int d1 = graph.degreeOf((int)o1);
            int d2 = graph.degreeOf((int)o2);
            
            if(d1 < d2)
                return 1;
            else if(d1 == d2)
                return 0;
            else
                return -1;
        });
        
        vertices.stream().forEach((v) ->
        {
            actions.push(new DeleteVertexAction(graph, v));
        });
        //*/
        /*
        // delete all vertices from graph
        // using a queue and two passes because of deletion during iteration doesn't work..
        Queue<GraphAction> queue = new LinkedList<>();
        
        graph.vertexSet().stream().forEach((v) ->
        {
            queue.add(new DeleteVertexAction(graph, v));
        });
        
        while(!queue.isEmpty())
        {
            actions.push(queue.poll());
        }
        //*/
        
        // put the vertices back, one by one, in reverse order
        // with each vertex we put back, find a minimal FVS
        int k=0;
        FVS<Integer> solution = new FVS<>();
        while(!actions.isEmpty())
        {
            DeleteVertexAction<Integer> action = (DeleteVertexAction<Integer>) actions.pop();
            action.revert();
            nVertices--;
            
            Set remainder = new HashSet();
            graph.vertexSet()
                    .stream()
                    .filter((v) -> (!solution.contains(v)))
                    .forEach((v) -> remainder.add(v));
            
            if(SimpleDisjointKernelization.inCycleWith(action.getVertex(), graph, remainder))
                solution.add(action.getVertex());
            //checkValidSolution(graph, solution);
            
            //System.out.println("solution size= "+solution.size()+", k= "+k);
            if(solution.size() > k)
            {
                try
                {
                    //System.out.println("Compressing, k="+k+" -- "+nVertices+" vertices to go");
                    solution.compress(graph);
                }
                catch (InterruptedException ex)
                {
                    System.out.println("Interupted with k="+k+", and "+nVertices+" vertices to go");
                    throw new RuntimeException(ex);
                }
                k = Math.max(k, solution.size());
                //System.out.println("new solution size= "+solution.size()+", k= "+k);
            }        }
        
        return solution;
    }
    
    public static void checkValidSolution(Multigraph graph, Collection solution)
    {
        DeleteVerticesAction removeFVS = new DeleteVerticesAction(graph, solution);
        removeFVS.perform();
        Multigraph cloned = (Multigraph)graph.clone();
        removeFVS.revert();

        Alg.Kernelization.Splitter.removeEdgesNotInCylce(cloned);
        if(cloned.edgeSet().size() > 0)
        {
            GraphDisplayer.display(graph);
            throw new RuntimeException("stop! "+solution+" is not a FVS for "+graph);
        }
    }
    
    public class FVS<V> extends ArrayList<V>
    {
        public void compress(Multigraph<V, DefaultEdge> graph) throws InterruptedException
        {
            // try every strict subset Z of the current solution C
            // remove this subset from the graph, G-Z
            // solve disjoint problem: find FVS in graph G-Z using only vertices in V(G)\C
            
            DisjointFVSAlgorithm<V> disjointSolver;
            disjointSolver = new SimpleDisjointAlg<>();
            
            for(Collection<V> subset : this.subsets())
            {
                
                if(Thread.currentThread().isInterrupted())
                    throw new InterruptedException();
                
                if(subset.size() == this.size())
                    continue; // not a strict subset
                
                HashSet<V> complement = this.complementOf(subset);
                //if(subset.size() + complement.size() != this.size())
                //    throw new RuntimeException("complement wrong!");
                
                DeleteVerticesAction<V> removeVertices = new DeleteVerticesAction<>(graph, subset);
                removeVertices.perform();
                //checkValidSolution(graph, complement);
                Collection<V> solution = disjointSolver.solve(graph, (HashSet)complement.clone());
                //if(solution != null)
                //    checkValidSolution(graph, solution);
                removeVertices.revert();
                
                if(solution != null) // we found one!
                {
                    //ArrayList tempSolution = new ArrayList(solution);
                    //tempSolution.addAll(subset);
                    //checkValidSolution(graph, tempSolution);
                    //System.out.println("Found a solution of size:"+tempSolution.size()+" ("+solution.size()+"+"+subset.size()+"), previous solution: "+this.size());
                    
                    this.update(complement, solution);
                    //checkValidSolution(graph, this);
                    return;
                }
            }
        }
        
        private void update(HashSet<V> remove, Collection<V> add)
        {
            if(remove.size() != add.size()+1)
                throw new IllegalArgumentException("remove.size() should be one larger than add.size()\nremove: "+remove+"\nadd: "+add);
            
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
        private long iteration = -1;
        
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
            return this.iteration == -1 || this.getFlipIndex() < this.set.size();
        }

        @Override
        public Collection<V> next()
        {
            if(this.iteration >= 0)
            {
                V flip = this.set.get(this.getFlipIndex());
            
                if(this.contains(flip))
                    this.remove(flip);
                else
                    this.add(flip);
            }
            
            this.iteration++;
            
            return this;
        }
        
        private int getFlipIndex()
        {
            return Long.numberOfTrailingZeros(~this.iteration);
        }
    }
}
