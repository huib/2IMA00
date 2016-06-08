package Alg.TreeDecomposition;

import org.jgrapht.graph.Multigraph;

public class FixedOrderChoice extends Choice {

    @Override
    Integer nextChoice(Multigraph g) {
        return (Integer) g.vertexSet().iterator().next();
    }

}
