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
    private EdgeWrapper<V,Object>[] edges;
    
    static int order=0;
    private int place;
    
    public DeleteVertexAction(AbstractBaseGraph<V,Object> g, V v)
    {
        this.v     = v;
        this.graph = g;
    }
    
    public V getVertex()
    {
        return this.v;
    }
    
    @Override
    public void perform()
    {
        this.edges = new EdgeWrapper[this.graph.degreeOf(v)];
        int i = 0;
        for(Object edge : this.graph.edgesOf(v))
            this.edges[i++] = new EdgeWrapper<>(
                    this.graph.getEdgeSource(edge),
                    this.graph.getEdgeTarget(edge),
                    this.graph.getEdgeWeight(edge),
                    edge
            );

        //removeVertex doesn't seem to remove edges touching the vertex, even though the
        // documentation says it does... So let's remove the edge manually
        for(EdgeWrapper<V,Object> e : this.edges)
        {
            this.graph.removeEdge(e.source, e.target);
            this.graph.removeEdge(e.target, e.source);
            this.graph.removeEdge(e.edge);
        }
        
        this.graph.removeVertex(this.v);
        this.place = order++;
        
        System.out.println(new String(new char[this.place*2]).replace('\0', ' ') + "removed "+this.v);
        System.out.print(new String(new char[this.place*2]).replace('\0', ' ') + " including edges to ");
        for(EdgeWrapper<V, Object> e : this.edges)
            if(e.target == this.v)
                System.out.print(e.source + " ");
            else
                System.out.print(e.target + " ");
        System.out.println();
    }

    @Override
    public void revert()
    {
        System.out.println(new String(new char[this.place*2]).replace('\0', ' ') + "adding "+this.v);
        
        if(this.place != order-1)
            throw new RuntimeException("GraphActions out of order...");
        order--;
        
        this.graph.addVertex(v);
        
        for(EdgeWrapper<V,Object> edge : this.edges)
        {
            this.graph.addEdge(edge.source, edge.target, edge.edge);
            //this.graph.setEdgeWeight(edge.edge, edge.weight);
        }
    }
}