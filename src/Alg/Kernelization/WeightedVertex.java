package Alg.Kernelization;

import org.jgrapht.Graphs;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;

/**
 * Created by Christopher on 6/14/2016.
 */
public class WeightedVertex {
    /**
     * Adds a (uniform) weight to vertices to simulate having a vertex-weighted graph
     * @param id
     * @param weight
     * @param component
     */
    public int id;
    public float weight;
    public WeightedVertex(int id) {
        this.id = id;
        this.weight = 1; // default value
    }
}
