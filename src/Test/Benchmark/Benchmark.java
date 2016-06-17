package Test.Benchmark;

import Alg.FVSAlgorithmInterface;

import Alg.InputReader;
import Alg.Lib.CycleDetector;
import Alg.SplitSolve;
import com.sun.org.apache.xpath.internal.operations.Mult;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;


/**
 * Run a benchmark for a algorithm. For all the example graph, reports the k. And calculates the time it takes to do so
 */
public abstract class Benchmark {

    /*
    Log of all the benchmarks that are done

    Moment            Algorithm                          Time
    31/5/16 14:16     Randomized                         5m 35s 447 ms
    31/5/16 14:34     Randomized                         3m 16s 795 ms
    31/5/16 19:45     RandomizedDensity                     46s 933 ms
    31/5/16 20:45     Randomized                         1m 42s 212 ms
    01/6/16 7:02      Randomized                            42s 033 ms
    01/6/16 7:04      RandomizedDensity                     34s 941 ms
    01/6/16 21:11     Randomized + SplitSolve               50s 940 ms
    01/6/16 21:18     RandomizedDensity + SplitSolve        33s 937 ms

     */
    private FVSAlgorithmInterface alg;

    public Benchmark(FVSAlgorithmInterface alg) {
        this.alg = new SplitSolve(alg);
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
        // Instances are added by sorting by filesize ascending
        // Last instance added 028.graph
        Instance[] instances = new Instance[]{
                new Instance("096.graph", 6),   // Record 765 ms       (RandomizedDensity)
                new Instance("099.graph", 8),   // Record 618 ms      (RandomizedDensity)
                new Instance("050.graph", 7),   // Record 3443 ms      (Randomized)
                new Instance("062.graph", 7),   // Record 3287 ms      (Randomized)
                new Instance("083.graph", 7),   // Record 3898 ms      (Randomized)
                new Instance("095.graph", 8),   // Record 9153 ms      (RandomizedDensity)
                new Instance("028.graph", 8)   // Record 8827 ms     (RandomizedDensity)
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

            if (!verifySolution(i.filename, solution)) {
                System.out.println("ERROR, THIS IS NOT A FEEDBACK VERTEX SET!");
                mistakes += 10;
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

    /**
     * A larger benchmark, with more files, for those algorithms that are even faster
     */
    @Test
    public void largerBenchMark() throws FileNotFoundException {
        // Instances are added by sorting by filesize ascending
        // Last instance added 047.graph
        Instance[] instances = new Instance[]{
                new Instance("096.graph", 6),   // Record 765 ms       (RandomizedDensity)
                new Instance("099.graph", 8),   // Record 618 ms       (RandomizedDensity)
                new Instance("050.graph", 7),   // Record 3443 ms      (Randomized)
                new Instance("062.graph", 7),   // Record 3287 ms      (Randomized)
                new Instance("083.graph", 7),   // Record 3898 ms      (Randomized)
                new Instance("095.graph", 8),   // Record 9153 ms      (RandomizedDensity)
                new Instance("028.graph", 8),   // Record 8827 ms      (RandomizedDensity)
                new Instance("003.graph", 10),
                new Instance("020.graph", 8),   // Record 4569 ms      (RandomizedDEnsity
                new Instance("042.graph", 11),  // Record 454646 ms    (RandomizedDensity)
                
                // All of the above: less than 2 seconds in total with iterative compression
                // All of the below: times using iterative compression (other algorithms didn't terminate)
                
                //new Instance("092.graph", -1), // k=15 or 16 (last compression step takes ages)
                //new Instance("065.graph", -1),
                
                //new Instance("046.graph", 18),
                // Graph 046.graph Time:399301ms
                // NEW SOLUTION! found a solution with k=18
                
                //new Instance("005.graph", 19),
                // Graph 005.graph Time:110131ms
                // NEW SOLUTION! found a solution with k=19
                
                //new Instance("029.graph", -1),
                //new Instance("047.graph", -1),
                
                //new Instance("077.graph", 16),
                // Graph 077.graph Time:32123ms
                // NEW SOLUTION! found a solution with k=16
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


            if(i.k <0){
                System.out.println("NEW SOLUTION! found a solution with k="+solution.size());
            }
            else if (solution.size() != i.k){
                System.out.println("MISTAKE! Required k:" + i.k + " Found k:" + solution.size());
                mistakes++;
            }

            if (!verifySolution(i.filename, solution)) {
                System.out.println("ERROR, THIS IS NOT A FEEDBACK VERTEX SET!");
                mistakes += 10;
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


    /**
     * Verify that the given answer is a correct answer
     *
     * @param filename
     * @param solution
     * @return
     */
    boolean verifySolution(String filename, List<Integer> solution) throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = this.loadGraph(filename);
        for  (int i: solution) {
            graph.removeVertex(i);
        }

        return !CycleDetector.hasCycle(graph);
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
