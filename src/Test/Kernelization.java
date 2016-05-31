package Test;

import Alg.InputReader;
import Alg.ReductionSolution;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Some unittests to test kernelization
 */
public class Kernelization {

    /**
     * Load in a graph from a file
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    protected Multigraph<Integer, DefaultEdge> loadGraph(String filename) throws FileNotFoundException {
        // Read from command line
        // Scanner scanner = new Scanner(System.in);

        // Read from file
        Scanner scanner = null;
        scanner = new Scanner(new File(filename));
        return InputReader.readGraph(scanner);
    }

    /**
     * The following graph should be reduced completely if edge two is deleted
     # A graph in the form of a wheel, with one missing piece K=1, and the solution must be 2
     #
     #   0
     #  /|\
     # 1-2-3
     #  \|
     #   4
     */
    @Test
    public void testReduceCompletely() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/001.graph");
        graph.removeVertex(2);

        ReductionSolution solution = Alg.Kernelization.kernelize(graph, 0);

        assertSame(0, solution.verticesToRemoved.size());
        assertSame(0, solution.reducedGraph.vertexSet().size());
        assertSame(0, solution.reducedK);
        assertTrue(solution.stillPossible);
    }

    /**
     * The following graph should return no solution possible for k=1 and 0 removed
     #
     #   0
     #  /|\
     # 1-2-3
     #  \|
     #   4
     */
    @Test
    public void testDoNotReduceCompletely() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/001.graph");
        graph.removeVertex(1);

        ReductionSolution solution = Alg.Kernelization.kernelize(graph, 0);

        assertTrue(!solution.stillPossible);
    }
}
