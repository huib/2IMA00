package Test.Benchmark;

import Alg.FVSAlgorithmInterface;

import Alg.InputReader;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.junit.Test;
import sun.java2d.pipe.SolidTextRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Run a benchmark for a algorithm. For all the example graph, reports the k. And calculates the time it takes to do so
 */
public abstract class Benchmark {

    private FVSAlgorithmInterface alg;

    public Benchmark(FVSAlgorithmInterface alg) {
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
        scanner = new Scanner(new File("instances/" + filename));
        return InputReader.readGraph(scanner);
    }

    /**
     * Run the benchmark
     */
    @Test
    public void benchMark() throws FileNotFoundException {
        Instance[] instances = new Instance[]{
                new Instance("099.graph", 8)
        };

        long totalTime = 0;
        int mistakes = 0;

        for (Instance i: instances) {
            Multigraph<Integer, DefaultEdge> graph = this.loadGraph(i.filename);

            long startTime = System.nanoTime();
            List<Integer> solution = alg.findFeedbackVertexSet(graph);
            long endTime = System.nanoTime();

            totalTime += (endTime - startTime) / 1_000_000;
            System.out.println("Graph " + i.filename + " Time:" + (endTime - startTime) / 1_000_000 + "ms");


            if (solution.size() != i.k){
                System.out.println("MISTAKE! Required k:" + i.k + " Found k:" + solution.size());
                mistakes++;
            }
        }

        System.out.println("TEST RESULTS:");
        System.out.println("Total time "
                + (totalTime / 60_000) + " m "
                + ((totalTime / 1000) % 60) + " s "
                + (totalTime % 1000) + " ms "
        );
        System.out.println("Total mistakes: " + mistakes);

    }

    class Instance
    {
        public Instance(String filename, int k) {
            this.filename = filename;
            this.k = k;
        }

        /**
         * The file name to open
         */
        String filename;

        /**
         * The k that should be the solution
         */
        int k;
    }
}
