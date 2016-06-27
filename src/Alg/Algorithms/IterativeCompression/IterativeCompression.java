/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import Alg.FVSAlgorithmInterface;
import Alg.GraphDisplayer;
import Alg.Kernelization.Approximation;
import Alg.Kernelization.Kernelization;
import Alg.Kernelization.ReductionSolution;
import Alg.Kernelization.SimpleDisjointKernelization;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

/**
 *
 * @author huib
 */
public class IterativeCompression implements FVSAlgorithmInterface
{
    public static int orderID = 4;
    
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
        
        //*
        List<Integer> vertices = new ArrayList(graph.vertexSet());
        Collections.shuffle(vertices);
        Map<Integer, Integer> weights = Kernelization.getImportanceApprox(graph);
        List<Integer> approxSolution = Approximation.determineFVS2(graph, new Integer[]{}, 1).verticesToRemoved;
        
        // <editor-fold desc="Order definitions" defaultstate="collapsed">
        // The comparator is used to define the order in which the vertices are removed, the order
        // in which the vertices are added is reversed. The naming of the comparators refers to the
        // order in which the vertices are added.
        Comparator<Integer> degreeInc = (Integer o1, Integer o2) ->
        {
            int d1 = graph.degreeOf((int)o1);
            int d2 = graph.degreeOf((int)o2);
            
            if(d1 < d2)
                return 1;
            else if(d1 == d2)
                return 0;
            else
                return -1;
        };
        Comparator<Integer> degreeDec = (Integer o1, Integer o2) ->
        {
            return degreeInc.compare(o2, o1);
        };
        Comparator<Integer> weightInc = (Integer o1, Integer o2) ->
        {
            int w1 = weights.get((int)o1);
            int w2 = weights.get((int)o2);
            
            if(w1 < w2)
                return 1;
            else if(w1 == w2)
                return 0;
            else
                return -1;
        };
        Comparator<Integer> weightDec = (Integer o1, Integer o2) ->
        {
            return weightInc.compare(o2, o1);
        };
        Comparator<Integer> weightIncDegreeInc = (Integer o1, Integer o2) ->
        {
            int r = weightInc.compare(o1, o2);
            if(r == 0)
                r = degreeInc.compare(o1, o2);
            
            return r;
        };
        Comparator<Integer> weightDecDegreeInc = (Integer o1, Integer o2) ->
        {
            int r = weightDec.compare(o1, o2);
            if(r == 0)
                r = degreeInc.compare(o1, o2);
            
            return r;
        };
        Comparator<Integer> weightIncDegreeDec = (Integer o1, Integer o2) ->
        {
            int r = weightInc.compare(o1, o2);
            if(r == 0)
                r = degreeDec.compare(o1, o2);
            
            return r;
        };
        Comparator<Integer> weightDecDegreeDec = (Integer o1, Integer o2) ->
        {
            int r = weightDec.compare(o1, o2);
            if(r == 0)
                r = degreeDec.compare(o1, o2);
            
            return r;
        };
        Comparator<Integer> degreeIncWeightInc = (Integer o1, Integer o2) ->
        {
            int r = degreeInc.compare(o1, o2);
            if(r == 0)
                r = weightInc.compare(o1, o2);
            
            return r;
        };
        Comparator<Integer> degreeIncWeightDec = (Integer o1, Integer o2) ->
        {
            int r = degreeInc.compare(o1, o2);
            if(r == 0)
                r = weightDec.compare(o1, o2);
            
            return r;
        };
        Comparator<Integer> degreeDecWeightInc = (Integer o1, Integer o2) ->
        {
            int r = degreeDec.compare(o1, o2);
            if(r == 0)
                r = weightInc.compare(o1, o2);
            
            return r;
        };
        Comparator<Integer> degreeDecWeightDec = (Integer o1, Integer o2) ->
        {
            int r = degreeDec.compare(o1, o2);
            if(r == 0)
                r = weightDec.compare(o1, o2);
            
            return r;
        };
        //</editor-fold>
        Comparator<Integer>[] orders = new Comparator[] {
            degreeInc, degreeDec, weightInc, weightDec,
            degreeIncWeightInc, degreeDecWeightInc,
            degreeIncWeightDec, degreeDecWeightDec,
            weightIncDegreeInc, weightDecDegreeInc,
            weightIncDegreeDec, weightDecDegreeDec,
        };
        Comparator<Integer> treeFirst = (Integer o1, Integer o2) ->
        {
            boolean c1 = approxSolution.contains(o1);
            boolean c2 = approxSolution.contains(o2);
            Comparator<Integer> comp = orders[orderID];
            
            if(c1 == c2)
                return comp.compare(o1, o2);
            else if(c1)
                return -1;
            else if(c2)
                return 1;
            else
                return 0; // this can never happen...
        };
        
        Collections.sort(vertices, treeFirst);
        
//        vertices.stream().forEach((v) ->
//        {
//            System.out.print(InputReader.map(v)+" ");
//        });
//        System.out.println();
//        
//        vertices.stream().forEach((v) ->
//        {
//            System.out.print(String.format("%02d", weights.get(v))+" ");
//        });
//        System.out.println();
//        
//        vertices.stream().forEach((v) ->
//        {
//            System.out.print(String.format("%02d", graph.degreeOf(v))+" ");
//        });
//        System.out.println();
        
        vertices
                .stream()
                //.filter((v) -> approxSolution.contains(v))
                .forEach((v) ->
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
//                    if(k>8)
//                        System.out.println("Compressing, k="+k+" -- "+nVertices+" vertices to go");
                    solution.compress(graph);
                }
                catch (InterruptedException ex)
                {
                    System.out.println("Interupted with k="+k+", and "+nVertices+" vertices to go");
                    throw new RuntimeException(ex);
                }
                k = Math.max(k, solution.size());
                //System.out.println("new solution size= "+solution.size()+", k= "+k);
            }
        }
        
//        Map<Integer, Integer> map = Kernelization.getImportanceApprox(g);
//        g.vertexSet().stream().forEach((v) ->
//        {
//            String name = InputReader.map(v);
//            int weight = map.get(v);
//            int degree = g.degreeOf(v);
//            int minND = Integer.MAX_VALUE;
//            int maxND = 0;
//            float avgND = 0;
//            for(DefaultEdge e : g.edgesOf(v))
//            {
//                int nb = g.getEdgeSource(e);
//                if(nb == v)
//                    nb = g.getEdgeTarget(e);
//                
//                nb = g.degreeOf(nb);
//                avgND += nb;
//                minND = Math.min(minND, nb);
//                maxND = Math.max(maxND, nb);
//            }
//            avgND/=degree+0.0f;
//                
//            System.out.println(name+", "+weight+", "+degree+", "+minND+", "+maxND+", "+avgND+", "+solution.contains(v));
//        });
        
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
        // the last vertex added since the last compression
        // equals null if no vertex was added since the last compression, or if we never compressed
        private V lastVertexAdded;
        // true iff since the last compression more than one vertex was added (should never be true)
        private boolean moreThanOneVertexAdded = false;
        
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
                
                if(     // not a strict subset
                        subset.size() == this.size()
                        // or the last (single!) vertex added is in the subset (meaning we can't compress this way)
                        || !this.moreThanOneVertexAdded && subset.contains(this.lastVertexAdded)
                        )
                {
                    continue;
                }
                
                
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
                    
                    this.lastVertexAdded = null;
                    this.moreThanOneVertexAdded = false;
                    return;
                }
            }
            
            this.lastVertexAdded = null;
            this.moreThanOneVertexAdded = false;
        }
        
        @Override
        public boolean add(V vertex)
        {
            if(this.lastVertexAdded != null)
                this.moreThanOneVertexAdded = true;
            this.lastVertexAdded = vertex;
            
            return super.add(vertex);
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
