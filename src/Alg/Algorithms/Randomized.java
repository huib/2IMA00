package Alg.Algorithms;

import Alg.FVSAlgorithmInterface;
import Alg.Kernelization;
import Alg.ReductionSolution;
import com.sun.org.apache.xpath.internal.operations.Mult;
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
    public ArrayList<Integer> findFeedbackVertexSet(Multigraph graph) {
        this.random = new Random();


        for (int k =1; ;k++) {
            System.out.println("k: " + k);
            for (int j = 0; j < Math.pow(4, k); j++) {
                Solution s = oneSidedMonteCarloFVS(graph, k);

                if (s.hasSolution) {
                    return s.solution;
                }
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
    public Solution oneSidedMonteCarloFVS(Multigraph<Integer, DefaultEdge> graph, int k)
    {
        ReductionSolution reductionSolution = this.runReductionRules(graph, k);
        graph = reductionSolution.reducedGraph;
        k = reductionSolution.reducedK;


        if (reductionSolution.stillPossible == false) {
            return new Solution(false);
        }

        // If the graph is empty, but a solution is still possible, then we have found the solution
        if (graph.edgeSet().size() == 0) {
            return new Solution(true, reductionSolution.verticesToRemoved);
        }

        // Select one edge at random
        Set<DefaultEdge> edgeset = graph.edgeSet();
        int edgeIndexToRemove = this.random.nextInt(edgeset.size());
        DefaultEdge edgeToRemove = (DefaultEdge) edgeset.toArray()[edgeIndexToRemove];

        // Select one random vertex from the edge, and remove it from the graph
        Integer vertexToRemove = (this.random.nextBoolean() ? graph.getEdgeSource(edgeToRemove) : graph.getEdgeTarget(edgeToRemove));

        // Call the method recursively
        reductionSolution.reducedGraph.removeVertex(vertexToRemove);
        Solution recursiveSolution = this.oneSidedMonteCarloFVS(reductionSolution.reducedGraph, reductionSolution.reducedK - 1);

        // If the solution is no, nothing needs to be done
        if (!recursiveSolution.hasSolution) {
            return recursiveSolution;
        }

        // Else, we need to set the solution set
        ArrayList<Integer> solutionEdges = new ArrayList<>();
        solutionEdges.add(vertexToRemove);
        solutionEdges.addAll(recursiveSolution.solution);
        solutionEdges.addAll(reductionSolution.verticesToRemoved);
        recursiveSolution.solution = solutionEdges;
        return recursiveSolution;
    }

    /**
     * Run the reduction rules on the graph
     *
     * @param graph
     * @param k
     * @return what is found in the reduction rules
     */
    public ReductionSolution runReductionRules(Multigraph<Integer, DefaultEdge> graph, int k)
    {
        return Kernelization.kernelize(graph, k);
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
            this(hasSolution, new ArrayList<Integer>());
        }

        /**
         * Create an instance of the solution
         *
         * @param hasSolution
         * @param solution
         */
        public Solution(boolean hasSolution, ArrayList<Integer> solution)
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
        public ArrayList<Integer> solution;
    }
}
