package Alg;

import org.jgrapht.graph.Multigraph;

/**
 * Interface for an algorithm that solves the Algorithm problem
 */
public interface FVSAlgorithmInterface
{
    /**
     * Return the names of all the edges that are in the feedback vertex set
     *
     * @return
     */
    String[] findFeedbackVertexSet(Multigraph graph);
}
