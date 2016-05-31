package Alg.Algorithms.Randomized;

import Alg.FVSAlgorithmInterface;
import Alg.Kernelization;
import Alg.ReductionSolution;
import com.sun.org.apache.xpath.internal.operations.Mult;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import sun.java2d.pipe.SolidTextRenderer;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/**
 * Same as the randomized algorithm, but distributes density rather then trying each solution
 */
public class RandomizedDensity implements FVSAlgorithmInterface
{
    /**
     * How many times we have to repeat the 4^k tries.
     *
     * The probability of an error must be at most 10^-12. Since one run gets an error of at most 1/e, we can calculate
     * this value:
     *
     * https://www.wolframalpha.com/input/?i=(1%2Fe)%5Ex+%3D+10%5E-12
     */
    final int REPEATS = 28;

    /**
     * Random number generator
     */
    protected Random random;

    @Override
    public ArrayList<Integer> findFeedbackVertexSet(Multigraph graph) {


        // Reduce the graph already for our kernelization
        // This may reduce the k upto which we have to search by a lot
        ReductionSolution reduced = Kernelization.kernelize(graph, 1000);
        this.random = new Random();

        for (int k = 1; ;k++) {
            Solution solution = this.findSolutionRecursive((Multigraph) reduced.reducedGraph.clone(), k, (long)(REPEATS * Math.pow(4, k)));

            if (solution.hasSolution) {
                reduced.verticesToRemoved.addAll(solution.solution);
                return reduced.verticesToRemoved;
            }
        }
    }

    /**
     * Recursively distribute the probability density * amount of runs over all possibilities
     *
     * @param graph Graph we need to find a fvs for
     * @param k total depth we need to reach
     * @param runs Total amount of runs that need to be distributed
     * @return
     */
    public Solution findSolutionRecursive(Multigraph graph, int k, long runs)
    {
        // No runs. we do not check this. So no solution
        if (runs == 0) {
            return new Solution(false);
        }

        // Run the kernelization over the graph
        ReductionSolution reductionSolution = Kernelization.kernelize(graph, k);


        Multigraph reducedGraph = reductionSolution.reducedGraph;
        int reducedK = reductionSolution.reducedK;


        if (reductionSolution.stillPossible == false) {
            return new Solution(false);
        }

        // If the graph is empty, but a solution is still possible, then we have found the solution
        if (reducedGraph.edgeSet().size() == 0) {
            return new Solution(true, reductionSolution.verticesToRemoved);
        }

        // Divide the runs over all the other graphs
        Set<DefaultEdge> edges = reducedGraph.edgeSet();

        // Randomly partion runs in amountEdges. This is done using a Geometric Distribution.
        int amountEdgesLeft = edges.size(); // The amount of edges which still need to have runs distributed
        long runsLeft = runs; // The total amount of runs that still need to be distributed

        for (DefaultEdge e: edges) {
            // Randomly decide how many runs are dedicated to this edge
            long runsForEdge = this.randomBinomial(runsLeft, 1.0/amountEdgesLeft);

            // Randomly distribute the runs over the two vertices in the edge
            long runsForSourceVertex = this.randomBinomial(runsForEdge, 0.5);
            long runsForTargetVertex = runsForEdge - runsForSourceVertex;

            Multigraph<Integer, DefaultEdge> sourceVertexRemovalGraph = (Multigraph<Integer, DefaultEdge>) reducedGraph.clone();
            Multigraph<Integer, DefaultEdge> targetVertexRemovalGraph = (Multigraph<Integer, DefaultEdge>) reducedGraph.clone();

            int sourceVertex = (int) reducedGraph.getEdgeSource(e);
            int targetVertex = (int) reducedGraph.getEdgeTarget(e);


            // Run the algorithm recursively for the source vertex
            sourceVertexRemovalGraph.removeVertex(sourceVertex);
            Solution recursiveSolutionSource = this.findSolutionRecursive(sourceVertexRemovalGraph, reducedK - 1, runsForTargetVertex);
            if (recursiveSolutionSource.hasSolution) {
                System.out.println("s" + sourceVertex);
                recursiveSolutionSource.solution.add(sourceVertex);
                recursiveSolutionSource.solution.addAll(reductionSolution.verticesToRemoved);
                return recursiveSolutionSource;
            }


            // Run the algorithm recursively for the target vertex
            targetVertexRemovalGraph.removeVertex(targetVertex);
            Solution recursiveSolutionTarget = this.findSolutionRecursive(targetVertexRemovalGraph, reducedK-1, runsForTargetVertex);

            if (recursiveSolutionTarget.hasSolution) {
                recursiveSolutionTarget.solution.add(targetVertex);
                recursiveSolutionSource.solution.addAll(reductionSolution.verticesToRemoved);
                return recursiveSolutionTarget;
            }

            // Set variables for other edges
            amountEdgesLeft -= 1;
            runsLeft -= runsForEdge;
        }

        // No solution was found
        return new Solution(false);
    }

    /**
     * Generate a random number between 0 and 1 from the binomial distribution
     *
     * The result of this function should be equal to the answer of a normally distributed value
     * with mean = np and variance = np (1-p)
     *
     * @param p
     */
    protected long randomBinomial(long n, double p)
    {
        // Find a normally distributed number between 0 and 1
        double guassian = this.random.nextGaussian();

        // Now do an inverted Z-transformation with variance = np(1-p) and mean = np;
        double variance = n*p*(1-p);
        double mean = n*p;
        double zTransformed = guassian * variance + mean;

        return Math.round(zTransformed);
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