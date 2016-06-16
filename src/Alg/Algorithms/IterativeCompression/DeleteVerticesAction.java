/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import java.util.Collection;
import org.jgrapht.graph.AbstractBaseGraph;

/**
 *
 * @author huib
 */
class DeleteVerticesAction<V> implements GraphAction
{
    private final AbstractBaseGraph<V,Object> graph;
    private final Collection<V> vertices;
    private EdgeWrapper<V,Object>[] edges;
    
    public DeleteVerticesAction(AbstractBaseGraph g, Collection<V> vertices)
    {
        if(vertices == null)
            throw new IllegalArgumentException("vertices may not be null");
        if(g == null)
            throw new IllegalArgumentException("graph may not be null");
        
        this.vertices = vertices;
        this.graph    = g;
    }
    
    @Override
    public void perform()
    {
        int size = 0;
        size = this.vertices
                .stream()
                .filter((v) -> (this.graph.containsVertex(v)))
                .map((v) -> this.graph.degreeOf(v))
                .reduce(size, Integer::sum);
        
        this.edges = new EdgeWrapper[size];
        
        int i = 0;
        for(V v : this.vertices)
        {
            if(!this.graph.containsVertex(v))
                continue;
            
            for(Object edge : this.graph.edgesOf(v))
                this.edges[i++] = new EdgeWrapper<>(
                        this.graph.getEdgeSource(edge),
                        this.graph.getEdgeTarget(edge),
                        this.graph.getEdgeWeight(edge),
                        edge
                );
        }
        
        //this.graph.removeAllVertices(this.vertices);
        this.vertices.stream().filter((v) -> (this.graph.containsVertex(v))).forEach((v) ->
        {
            this.graph.removeVertex(v);
        });
    }

    @Override
    public void revert()
    {
        this.vertices.stream().forEach((v) ->
        {
            this.graph.addVertex(v);
        });
        
        for(EdgeWrapper<V,Object> edge : this.edges)
        {
            this.graph.addEdge(edge.source, edge.target, edge.edge);
            //this.graph.setEdgeWeight(edge.edge, edge.weight);
        }
    }
    
}
