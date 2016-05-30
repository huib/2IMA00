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
    private AbstractBaseGraph graph;
    private V v;
    private EdgeWrapper[] edges;
    
    public DeleteVertexAction(AbstractBaseGraph g, V v)
    {
        this.v     = v;
        this.graph = g;
        
        this.edges = new EdgeWrapper[this.graph.degreeOf(v)];
        int i = 0;
        for(Object edge : this.graph.edgesOf(v))
            this.edges[i++] = new EdgeWrapper(
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
        if(!this.graph.containsVertex(v) || !this.graph.edgesOf(v).equals(this.edges))
        {
            throw new RuntimeException("Cannot perform deletion of vertex, graph has changed with"
                    + " regard to the to-be-deleted vertex.");
        }
        
        this.graph.removeVertex(this.v);
    }

    @Override
    public void revert()
    {
        this.graph.addVertex(v);
        
        for(EdgeWrapper edge : this.edges)
        {
            this.graph.addEdge(edge.source, edge.target, edge.edge);
            this.graph.setEdgeWeight(edge.edge, edge.weight);
        }
    }
    
    class EdgeWrapper<E extends Object>
    {
        public final V source, target;
        public final E edge;
        public final double weight;
        
        public EdgeWrapper(V source, V target, double weight, E edge)
        {
            this.source = source;
            this.target = target;
            this.edge   = edge;
            this.weight = weight;
        }
    }
}