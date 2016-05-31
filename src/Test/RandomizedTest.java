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
public class RandomizedTest {

    @Test
    public void testSimpleCycle() {
        // Read from command line
        // Scanner scanner = new Scanner(System.in);

        // Read from file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("instances/simple/000.graph"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Multigraph<Integer, DefaultEdge> graph = InputReader.readGraph(scanner);

        FVSAlgorithmInterface randomized = new Randomized();

        List<Integer> solution = randomized.findFeedbackVertexSet(graph);


        for (Integer s: solution) {
            System.out.print(s + ", ");
        }

    }

}