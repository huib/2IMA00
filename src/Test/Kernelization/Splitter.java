package Test.Kernelization;

import Alg.InputReader;
import Alg.Kernelization.ReductionSolution;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
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

    /**
     * The following graph needs to be split into two graphs after edge 2-3 is removed: One graph containing 0,1,2
     * and one graph containing 3,4,5. The original graph must be empty
     #   0       4
     #  / \     / \
     # 1 - 2 - 3 - 5
     */
    @Test
    public void testSplitGraph() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/002.graph");
        graph.removeEdge(2, 3);
        List<Multigraph<Integer, DefaultEdge>> newGraphs = Alg.Kernelization.Splitter.splitGraph(graph);

        assertSame(2, newGraphs.size());

        int graphWithEdge0 = newGraphs.get(0).containsVertex(0) ? 0 : 1;

        // Graph with edges 0, 1 and 2
        Multigraph<Integer, DefaultEdge> graph1 = newGraphs.get(graphWithEdge0);

        // Graph with edges 3, 4 and 5
        Multigraph<Integer, DefaultEdge> graph2 = newGraphs.get(Math.abs(graphWithEdge0 - 1));

        assertTrue(graph1.containsVertex(0));
        assertTrue(graph1.containsVertex(1));
        assertTrue(graph1.containsVertex(2));
        assertSame(3, graph1.edgeSet().size());

        assertTrue(graph2.containsVertex(3));
        assertTrue(graph2.containsVertex(4));
        assertTrue(graph2.containsVertex(5));
        assertSame(3, graph2.edgeSet().size());
    }

    /**
     * The following graph must not be split, since it is connected
     #   0       4
     #  / \     / \
     # 1 - 2 - 3 - 5
     */
    @Test
    public void testSplitGraphNonSplit() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/002.graph");
        List<Multigraph<Integer, DefaultEdge>> newGraphs = Alg.Kernelization.Splitter.splitGraph(graph);

        assertSame(1, newGraphs.size());

        Multigraph<Integer, DefaultEdge> graph1 = newGraphs.get(0);

        assertTrue(graph1.containsVertex(0));
        assertTrue(graph1.containsVertex(1));
        assertTrue(graph1.containsVertex(2));
        assertTrue(graph1.containsVertex(3));
        assertTrue(graph1.containsVertex(4));
        assertTrue(graph1.containsVertex(5));
        assertSame(7, graph1.edgeSet().size());
    }

    /**
     * The following graph needs to be split into two graphs: One graph containing 0,1,2
     * and one graph containing 3,4,5. The original graph must be empty
     #   0       4
     #  / \     / \
     # 1 - 2 - 3 - 5
     */
    @Test
    public void testSplit() throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph("instances/simple/002.graph");
        List<Multigraph<Integer, DefaultEdge>> newGraphs = Alg.Kernelization.Splitter.split(graph);

        assertSame(2, newGraphs.size());

        int graphWithEdge0 = newGraphs.get(0).containsVertex(0) ? 0 : 1;

        // Graph with edges 0, 1 and 2
        Multigraph<Integer, DefaultEdge> graph1 = newGraphs.get(graphWithEdge0);

        // Graph with edges 3, 4 and 5
        Multigraph<Integer, DefaultEdge> graph2 = newGraphs.get(Math.abs(graphWithEdge0 - 1));

        assertTrue(graph1.containsVertex(0));
        assertTrue(graph1.containsVertex(1));
        assertTrue(graph1.containsVertex(2));
        assertSame(3, graph1.edgeSet().size());

        assertTrue(graph2.containsVertex(3));
        assertTrue(graph2.containsVertex(4));
        assertTrue(graph2.containsVertex(5));
        assertSame(3, graph2.edgeSet().size());
    }

}
