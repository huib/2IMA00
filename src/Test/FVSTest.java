package Test;

import Alg.Algorithms.Randomized;
import Alg.FVSAlgorithmInterface;
import Alg.InputReader;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Test for randomized algorithms
 */
public abstract class FVSTest {

    protected FVSAlgorithmInterface alg;

    /**
     * Create the randomized tests for the algorithm
     * @param alg
     */
    public FVSTest(FVSAlgorithmInterface alg) {
        this.alg = alg;
    }

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
     * Test that if we have a simple cycle in our graph, that it can be found
     *
     * @throws FileNotFoundException
     */
    @Test
    public void testSimpleCycle() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/000.graph");
        FVSAlgorithmInterface randomized = new Randomized();
        List<Integer> solution = randomized.findFeedbackVertexSet(graph);
        assertSame(1, solution.size());
    }

    /**
     * Test that we have a wheel with 3 vertices in the outside, and 1 in the inside. The 1 in the inside should be deleted
     *
     * @throws FileNotFoundException
    # A graph in the form of a wheel, with one missing piece K=1, and the solution must be 2
    #
    #   0
    #  /|\
    # 1-2-3
    #  \|
    #   4
     */
    @Test
    public void testWheel() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/001.graph");
        FVSAlgorithmInterface randomized = new Randomized();
        List<Integer> solution = randomized.findFeedbackVertexSet(graph);

        for (int i:solution) {
            System.out.println(i);
        }

        assertSame(1, solution.size());
        assertTrue(solution.contains(2));
    }

}