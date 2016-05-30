package Alg.TreeDecomposition;

import java.util.Set;
import org.jgrapht.graph.Multigraph;

public class TreeDecomposition {
    
    public static Bag makeTreeDecomposition(Multigraph g){
        Bag root = new Bag();
        
        Set<String> vertices = g.vertexSet();
        for(String vertex: vertices){
            root.add(vertex);
        }
        return root;
    }
    
}
