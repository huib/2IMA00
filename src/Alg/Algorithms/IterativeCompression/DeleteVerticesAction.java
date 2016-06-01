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
    private final EdgeWrapper<V,Object>[] edges;
    
    public DeleteVerticesAction(AbstractBaseGraph g, Collection<V> vertices)
    {
        this.vertices = vertices;
        this.graph    = g;
        
        int size = 0;
        size = this.vertices.stream().map((v) -> this.graph.degreeOf(v)).reduce(size, Integer::sum);
        
        this.edges = new EdgeWrapper[size];
        
        this.vertices.stream().forEach((v) ->
        {
            int i = 0;
            for(Object edge : this.graph.edgesOf(v))
                this.edges[i++] = new EdgeWrapper<>(
                        this.graph.getEdgeSource(edge),
                        this.graph.getEdgeTarget(edge),
                        this.graph.getEdgeWeight(edge),
                        edge
                );
        });
    }
    
    @Override
    public void perform()
    {
        this.graph.removeAllVertices(this.vertices);
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
            this.graph.setEdgeWeight(edge.edge, edge.weight);
        }
    }
    
}
