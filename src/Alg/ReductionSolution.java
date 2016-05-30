package Alg;

import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;

/**
 * Created by Christopher on 5/30/2016.
 */
public class ReductionSolution
{
    /**
     * Boolean indicating whether after reduction there is still a possibility
     */
    public boolean stillPossible;

    /**
     * Set of all the vertices that have to be removed by this solution
     */
    public ArrayList<Integer> verticesToRemoved = new ArrayList();

    /**
     * The k value after the reduction algorithm
     */
    public int reducedK;

    /**
     * Graph after reduction Solution
     */
    public Multigraph reducedGraph;
}