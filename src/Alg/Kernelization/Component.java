package Alg.Kernelization;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;

/**
 * Created by Christopher on 6/14/2016.
 */
public class Component {

    /**
     * Graph component should be a forest
     */
    public Multigraph<WeightedVertex,DefaultEdge> forest;

    /**
     * Component id
     */
    public int id;

    public Component(int c) {
        this.id = c; // set component id
    }
}
