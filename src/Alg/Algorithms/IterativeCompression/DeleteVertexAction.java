/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import org.jgrapht.graph.AbstractBaseGraph;

/**
 *
 * @author huib
 */
public class DeleteVertexAction<V> implements GraphAction
{
    private final AbstractBaseGraph<V,Object> graph;
    private final V v;
    private final EdgeWrapper<V,Object>[] edges;
    
    static int order=0;
    private int place;
    
    public DeleteVertexAction(AbstractBaseGraph<V,Object> g, V v)
    {
        this.v     = v;
        this.graph = g;
        
        this.edges = new EdgeWrapper[this.graph.degreeOf(v)];
        int i = 0;
        for(Object edge : this.graph.edgesOf(v))
            this.edges[i++] = new EdgeWrapper<>(
                    this.graph.getEdgeSource(edge),
                    this.graph.getEdgeTarget(edge),
                    this.graph.getEdgeWeight(edge),
                    edge
            );
    }
    
    public V getVertex()
    {
        return this.v;
    }
    
    @Override
    public void perform()
    {
        this.graph.removeVertex(this.v);
        this.place = order++;
        
        System.out.println(new String(new char[this.place]).replace('\0', ' ') + "removed "+this.v);
    }

    @Override
    public void revert()
    {
        System.out.println(new String(new char[this.place]).replace('\0', ' ') + "adding "+this.v);
        
        if(this.place != order-1)
            throw new RuntimeException("GraphActions out of order...");
        order--;
        
        this.graph.addVertex(v);
        
        for(EdgeWrapper<V,Object> edge : this.edges)
        {
            this.graph.addEdge(edge.source, edge.target, edge.edge);
            this.graph.setEdgeWeight(edge.edge, edge.weight);
        }
    }
}