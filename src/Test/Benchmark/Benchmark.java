package Test.Benchmark;

import Alg.FVSAlgorithmInterface;
import Alg.InputReader;
import Alg.InputWrapper;
import Alg.Lib.CycleDetector;
import Alg.SplitSolve;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.junit.Test;


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
    protected static Multigraph<Integer, DefaultEdge> loadGraph(String filename) throws FileNotFoundException {
        // Read from command line
        // Scanner scanner = new Scanner(System.in);

        // Read from file
        Scanner scanner = null;
        scanner = new Scanner(new File("instances/" + filename));
        return InputReader.readGraph(scanner);
    }
    
    protected static InputWrapper loadGraph2(String filename) throws FileNotFoundException {
        // Read from command line
        // Scanner scanner = new Scanner(System.in);

        // Read from file
        Scanner scanner = null;
        scanner = new Scanner(new File("instances/" + filename));
        return InputReader.loopSafeReadGraph(scanner);
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
            Multigraph<Integer, DefaultEdge> graph = loadGraph(i.filename);

            long totalTimeGraph = 0;
            int k = 10;
            while(k --> 0) {
                Multigraph<Integer, DefaultEdge> clonedgraph = (Multigraph<Integer, DefaultEdge>)graph.clone();
                long startTime = System.nanoTime();
                List<Integer> solution = alg.findFeedbackVertexSet(clonedgraph);
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime) / 1_000_000;
                totalTimeGraph +=(endTime - startTime) / 1_000_000;

                if (solution.size() != i.k){
                    System.out.println("MISTAKE! Required k:" + i.k + " Found k:" + solution.size());
                    mistakes++;
                }

                if (!verifySolution(i.filename, solution)) {
                    System.out.println("ERROR, THIS IS NOT A FEEDBACK VERTEX SET!");
                    mistakes += 10;
                }

                System.out.println("Graph " + i.filename + " Time:" + (endTime - startTime) / 1_000_000 + "ms");
            }

            System.out.println("Graph " + i.filename + " Average time:" + (totalTimeGraph / 10) + "ms");


        }

        System.out.println("TEST RESULTS:");
        System.out.println("Total time "
                + (totalTime / 60_000) + " m "
                + ((totalTime / 1000) % 60) + " s "
                + (totalTime % 1000) + " ms "
        );
        System.out.println("Total mistakes: " + mistakes);
    }
    
    @Test
    public void sub20SecondProblems() throws FileNotFoundException {
        Instance[] instances = new Instance[]{
                new Instance("003.graph", 10),
                new Instance("006.graph", 11),
                new Instance("020.graph", 8),
                new Instance("028.graph", 8),
                new Instance("031.graph", 33),
                new Instance("042.graph", 11),
                new Instance("050.graph", 7),
                new Instance("072.graph", 9),
                new Instance("083.graph", 7),
                new Instance("085.graph", 51),
                new Instance("091.graph", 21),
                new Instance("095.graph", 8),
                new Instance("096.graph", 6),
                new Instance("099.graph", 8),
        };

        long totalTime = 0;
        int mistakes = 0;

        for (Instance i: instances) {
            Multigraph<Integer, DefaultEdge> graph = loadGraph(i.filename);

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

    
    @Test
    public void promisingProblems() throws FileNotFoundException {
        Instance[] instances = new Instance[]{
                //new Instance("007.graph", 17), //solved in 20 seconds
                //new Instance("046.graph", 18), //solved in 400 seconds
                new Instance("059.graph", 18),
                // Graph 059.graph Time:6804546ms
                // NEW SOLUTION! found a solution with k=18
                new Instance("070.graph", 19),
                // Graph 070.graph Time:434357ms
                // NEW SOLUTION! found a solution with k=19
                new Instance("029.graph", -1),
                //new Instance("024.graph", -1),
                //new Instance("027.graph", -1),
        };

        long totalTime = 0;
        int mistakes = 0;

        for (Instance i: instances) {
            Multigraph<Integer, DefaultEdge> graph = loadGraph(i.filename);

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
     * A larger benchmark, with more files, for those algorithms that are even faster
     */
    @Test
    public void largerBenchMark() throws FileNotFoundException {
        // Instances are added by sorting by filesize ascending
        // Last instance added 047.graph
        Instance[] instances = new Instance[]{
//                new Instance("096.graph", 6),   // Record 765 ms       (RandomizedDensity)
//                new Instance("099.graph", 8),   // Record 618 ms       (RandomizedDensity)
//                new Instance("050.graph", 7),   // Record 3443 ms      (Randomized)
//                new Instance("062.graph", 7),   // Record 3287 ms      (Randomized)
//                new Instance("083.graph", 7),   // Record 3898 ms      (Randomized)
//                new Instance("095.graph", 8),   // Record 9153 ms      (RandomizedDensity)
//                new Instance("028.graph", 8),   // Record 8827 ms      (RandomizedDensity)
//                new Instance("003.graph", 10),
//                new Instance("020.graph", 8),   // Record 4569 ms      (RandomizedDEnsity
//                new Instance("042.graph", 11),  // Record 454646 ms    (RandomizedDensity)
            
                // All of the above: less than 2 seconds in total with iterative compression              
                // All of the below: times using iterative compression (other algorithms didn't terminate)

                //new Instance("065.graph", -1),
                
                //new Instance("046.graph", 18),
                // Graph 046.graph Time:241681ms
                // NEW SOLUTION! found a solution with k=18
                
                //new Instance("005.graph", 19),
                // Graph 005.graph Time:74265ms
                // NEW SOLUTION! found a solution with k=19
                
                //new Instance("029.graph", -1),
                //new Instance("047.graph", -1),
                
                //new Instance("077.graph", 16),
                // Graph 077.graph Time:32123ms
                // NEW SOLUTION! found a solution with k=16
            
                // ------------------------
                // fast instances (<5 seconds each)
                new Instance("002.graph", 47),
                new Instance("003.graph", 10),
                new Instance("006.graph", 11),
                new Instance("020.graph", 8),
                new Instance("028.graph", 8),
                new Instance("031.graph", 33),
                new Instance("042.graph", 11),
                new Instance("050.graph", 7),
                new Instance("062.graph", 7),
                new Instance("072.graph", 9),
                new Instance("083.graph", 7),
                new Instance("085.graph", 51),
                new Instance("091.graph", 21),
                new Instance("095.graph", 8),
                new Instance("096.graph", 6),
                new Instance("099.graph", 8),
                
                // slower instances (15s - 5m)
                new Instance("007.graph", 17), //~20000ms
                new Instance("015.graph", 18), // 26821ms
                new Instance("070.graph", 19), // 36962ms
                new Instance("077.graph", 16), // 32123ms
                
                // 5-10 minutes
                new Instance("005.graph", 19), // 74265ms
                new Instance("046.graph", 18), // 241681ms
                new Instance("070.graph", 19), // 434357ms
                new Instance("098.graph", 18), // 20128ms
                
                // takes forever
                new Instance("059.graph", 18), // 2411895ms
                //new Instance("092.graph", 16), // 3920522ms             (IterativeCompression)
                new Instance("065.graph", 21), // 34 mins

        };

        long totalTime = 0;
        int mistakes = 0;

        for (Instance i: instances) {
            InputWrapper input = loadGraph2(i.filename);
            Multigraph<Integer, DefaultEdge> graph = input.reductionSolution.reducedGraph;

            long startTime = System.nanoTime();
            List<Integer> solution = alg.findFeedbackVertexSet(graph);
            long endTime = System.nanoTime();

            totalTime += (endTime - startTime) / 1_000_000;
            System.out.println("Graph " + i.filename + " Time:" + (endTime - startTime) / 1_000_000 + "ms");
//            for (Integer s: solution) {
//                System.out.print(input.nameMapping.get(s) + ", ");
//            }
//            System.out.println();


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
     * A larger benchmark, with more files, for those algorithms that are even faster
     */
    @Test
    public void orderBenchMark() throws FileNotFoundException {
        Instance[] instances = new Instance[]{
                // fast instances (<2 seconds each)
                new Instance("003.graph", 10),
                new Instance("006.graph", 11),
                new Instance("020.graph", 8),
                new Instance("028.graph", 8),
                new Instance("031.graph", 33),
                new Instance("042.graph", 11),
                new Instance("050.graph", 7),
                new Instance("062.graph", 7),
                new Instance("072.graph", 9),
                new Instance("083.graph", 7),
                new Instance("085.graph", 51),
                new Instance("091.graph", 21),
                new Instance("095.graph", 8),
                new Instance("096.graph", 6),
                new Instance("099.graph", 8),
                
                // slower instances (2s - 2m)
                new Instance("007.graph", 17),
                new Instance("077.graph", 16),
                new Instance("005.graph", 19),
                new Instance("046.graph", 18),
                new Instance("059.graph", 18),
                new Instance("070.graph", 19),
                new Instance("098.graph", 18),
                
                // takes forever (~30m)
                //new Instance("092.graph", 16),
        };
        
        List orderIDs = new ArrayList();
        for(int i=0; i<12; i++)
            orderIDs.add(i);
        List[] instanceOrderIDs = new List[instances.length];
        for(int i=0; i<instances.length; i++)
        {
            instanceOrderIDs[i] = new ArrayList(orderIDs);
        }
        List<Instance> instanceList = Arrays.asList(instances);
        List<Instance> shuffledInstances = new ArrayList();
        shuffledInstances.addAll(instanceList);
        
        int count = 0;
        while(true)
        {
            for(List l : instanceOrderIDs)
                Collections.shuffle(l);
            for(int o=0; o<orderIDs.size(); o++)
            {
                Collections.shuffle(shuffledInstances);

                for (Instance i: shuffledInstances) {
                    Alg.Algorithms.IterativeCompression.IterativeCompression.orderID = (int)instanceOrderIDs[instanceList.indexOf(i)].get(o);
                    InputWrapper input = loadGraph2(i.filename);
                    Multigraph<Integer, DefaultEdge> graph = input.reductionSolution.reducedGraph;

                    long startTime = System.nanoTime();
                    List<Integer> solution = alg.findFeedbackVertexSet(graph);
                    long endTime = System.nanoTime();

                    System.out.println(
                            count+", "+
                            o+", "+
                            i.filename+", "+
                            Alg.Algorithms.IterativeCompression.IterativeCompression.orderID+", "+
                            (endTime - startTime) / 1_000_000
                    );
                    
                    if (solution.size() != i.k){
                        System.err.print(", MISTAKE! Required k:" + i.k + " Found k:" + solution.size());
                    }

                    if (!verifySolution(i.filename, solution)) {
                        System.err.print(", ERROR, THIS IS NOT A FEEDBACK VERTEX SET!");
                    }
                }
            }
            count++;
        }
    }


    /**
     * Verify that the given answer is a correct answer
     *
     * @param filename
     * @param solution
     * @return
     */
    static boolean verifySolution(String filename, List<Integer> solution) throws FileNotFoundException {
        Multigraph<Integer, DefaultEdge> graph = loadGraph(filename);
        for  (int i: solution) {
            graph.removeVertex(i);
        }

        return !CycleDetector.hasCycle(graph);
    }

    static class Instance
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
