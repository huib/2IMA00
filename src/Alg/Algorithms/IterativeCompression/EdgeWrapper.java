/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

/**
 *
 * @author huib
 */
public class EdgeWrapper<V, E>
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
