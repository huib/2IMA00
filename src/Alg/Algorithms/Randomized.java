package Alg.Algorithms;

import Alg.FVSAlgorithmInterface;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import Alg.Lib.CycleDetector;

/**
 * Created by Stefan on 5/22/2016.
 */
public class Randomized implements FVSAlgorithmInterface
{
    /**
     * Random number generator
     */
    protected Random random;

    @Override
    public String[] findFeedbackVertexSet(Multigraph graph) {
        this.random = new Random();


        for (int k =1; ;k++) {
            Solution s = oneSidedMonteCarloFVS(graph, k);

            if (s.hasSolution) {
                return s.solution;
            }
        }
    }


    /**
     * Do a one sided Monte Carlo version of the Randomized Algorithm
     *
     * If the (G, k) is a "Yes" instance, returns a solution w.p. > 4^-k
     * Otherwise always returns "No"
     *
     * @param graph
     * @param k
     * @return
     */
    public Solution oneSidedMonteCarloFVS(Multigraph graph, int k)
    {
        ReductionSolution reductionSolution = this.runReductionRules(graph, k);
        if (reductionSolution.stillPossible == false) {
            return new Solution(false);
        }

        if (reductionSolution.reducedK == 0) {
            return new Solution(true, reductionSolution.verticesToRemoved);
        }

        // Select one edge at random
        Set edgeset = graph.edgeSet();
        int edgeIndexToRemove = this.random.nextInt(edgeset.size());
        DefaultEdge edgeToRemove = (DefaultEdge) edgeset.toArray()[edgeIndexToRemove];

        // Select one random vertex from the edge, and remove it from the graph
        String vertexToRemove = (String) (this.random.nextBoolean() ? graph.getEdgeSource(edgeToRemove) : graph.getEdgeTarget(edgeToRemove));

        // Call the method recursively
        reductionSolution.reducedGraph.removeVertex(vertexToRemove);
        Solution recursiveSolution = this.oneSidedMonteCarloFVS(reductionSolution.reducedGraph, reductionSolution.reducedK - 1);

        // If the solution is no, nothing needs to be done
        if (!recursiveSolution.hasSolution) {
            return recursiveSolution;
        }

        // Else, we need to set the solution set
        ArrayList<String> solutionEdges = new ArrayList<>();
        solutionEdges.add(vertexToRemove);
        solutionEdges.addAll(Arrays.asList(recursiveSolution.solution));
        solutionEdges.addAll(Arrays.asList(reductionSolution.verticesToRemoved));
        recursiveSolution.solution = solutionEdges.toArray(new String[solutionEdges.size()]);
        return recursiveSolution;
    }

    /**
     * Run the reduction rules on the graph
     *
     * @param graph
     * @param k
     * @return what is found in the reduction rules
     */
    public ReductionSolution runReductionRules(Multigraph graph, int k)
    {

        ReductionSolution solution =  new ReductionSolution();
        solution.stillPossible = (k != 0) || (CycleDetector.hasCycle(graph));
        solution.verticesToRemoved = new String[]{};
        solution.reducedK = k;
        solution.reducedGraph = (Multigraph) graph.clone();
        return solution;
    }

    /**
     * Solution for one run of the Randomized Algorithm
     */
    class Solution
    {
        /**
         * Create an instance with an empty instance set
         *
         * @param hasSolution
         */
        public Solution(boolean hasSolution)
        {
            this(hasSolution, new String[0]);
        }

        /**
         * Create an instance of the solution
         *
         * @param hasSolution
         * @param solution
         */
        public Solution(boolean hasSolution, String[] solution)
        {
            this.hasSolution = hasSolution;
            this.solution = solution;
        }

        /**
         * Check if there is a solution found
         */
        public boolean hasSolution;

        /**
         * If a  solution is found -> contains the names of the edges that is the solution
         */
        public String[] solution;
    }


    class ReductionSolution
    {
        /**
         * Boolean indicating whether after reduction there is still a possibility
         */
        public boolean stillPossible;

        /**
         * Set of all the vertices that have to be removed by this solution
         */
        public String[] verticesToRemoved;

        /**
         * The k value after the reduction algorithm
         */
        public int reducedK;

        /**
         * Graph after reduction Solution
         */
        public Multigraph reducedGraph;
    }
}
