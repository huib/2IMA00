package Alg;

import Alg.Kernelization.ReductionSolution;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;
import java.util.List;

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
    List<Integer> findFeedbackVertexSet(ReductionSolution partialSolution);

    /**
     * Return the names of all the edges that are in the feedback vertex set
     *
     * @return
     */
    List<Integer> findFeedbackVertexSet(Multigraph<Integer, DefaultEdge> graph);
}
