package Test.Kernelization;

import Alg.InputReader;
import Alg.Kernelization.ReductionSolution;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by Stefan on 6/9/2016.
 */
public class Splitter {
    /**
     * Load in a graph from a file
     *
     * @param filename
     * @return
     * @throws FileNotFoundException
     */
    protected Multigraph<Integer, DefaultEdge> loadGraph(String filename) throws FileNotFoundException {
        // Read from file
        Scanner scanner = null;
        scanner = new Scanner(new File(filename));
        return InputReader.readGraph(scanner);
    }

    /**
     * Has the following graph, and should  remove the edge between 2 and 3
     #   0       4
     #  / \     / \
     # 1 - 2 - 3 - 5
     */
    @Test
    public void testRemoveEdge() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/002.graph");
        Alg.Kernelization.Splitter.removeEdgesNotInCylce(graph);

        assertSame(6, graph.edgeSet().size());
        assertTrue(! graph.containsEdge(2, 3));
    }


    /**
     * In the following graph none of the edges must be removed, since they are all part of a cycle
     #
     #   0
     #  /|\
     # 1-2-3
     #  \|
     #   4
     */
    @Test
    public void testRemoveNoneAllCycle() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/001.graph");
        Alg.Kernelization.Splitter.removeEdgesNotInCylce(graph);

        assertSame(7, graph.edgeSet().size());
    }

    /**
     * In the following graph if edge 2 is removed, all edges should be removed since they are not part of a cycle
     #
     #   0
     #  /|\
     # 1-2-3
     #  \|
     #   4
     */
    @Test
    public void testRemoveAllNoneCycle() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/001.graph");
        graph.removeVertex(2);
        Alg.Kernelization.Splitter.removeEdgesNotInCylce(graph);
        assertSame(0, graph.edgeSet().size());
    }

}
