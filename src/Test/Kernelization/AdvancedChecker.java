package Test.Kernelization;

import Alg.InputReader;
import Alg.InputWrapper;
import Alg.Kernelization.Kernelization;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import Alg.Kernelization.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;
import org.junit.Test;

/**
 * Created by leo on 29-6-16.
 */
public class AdvancedChecker {

    public AdvancedChecker() {
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
    public void checkKernelot() throws FileNotFoundException {
        Instance[] instances = new Instance[]{
                new Instance("001.graph"),
                new Instance("002.graph"),
                new Instance("003.graph"),
                new Instance("004.graph"),
                new Instance("005.graph"),
                new Instance("006.graph"),
                new Instance("007.graph"),
                new Instance("008.graph"),
                new Instance("009.graph"),
                new Instance("010.graph"),
                new Instance("011.graph"),
                new Instance("012.graph"),
                new Instance("013.graph"),
                new Instance("014.graph"),
                new Instance("015.graph"),
                new Instance("016.graph"),
                new Instance("017.graph"),
                new Instance("018.graph"),
                new Instance("019.graph"),
                new Instance("020.graph"),
                new Instance("021.graph"),
                new Instance("022.graph"),
                new Instance("023.graph"),
                new Instance("024.graph"),
                new Instance("025.graph"),
                new Instance("026.graph"),
                new Instance("027.graph"),
                new Instance("028.graph"),
                new Instance("029.graph"),
                new Instance("030.graph"),
                new Instance("031.graph"),
                new Instance("032.graph"),
                new Instance("033.graph"),
                new Instance("034.graph"),
                new Instance("035.graph"),
                new Instance("036.graph"),
                //new Instance("037.graph"),
                new Instance("038.graph"),
                new Instance("039.graph"),
                new Instance("040.graph"),
                //new Instance("041.graph"),
                new Instance("042.graph"),
                new Instance("043.graph"),
                new Instance("044.graph"),
                new Instance("045.graph"),
                new Instance("046.graph"),
                new Instance("047.graph"),
                new Instance("048.graph"),
                new Instance("049.graph"),
                new Instance("050.graph"),
                new Instance("051.graph"),
                new Instance("052.graph"),
                new Instance("053.graph"),
                new Instance("054.graph"),
                new Instance("055.graph"),
                new Instance("056.graph"),
                new Instance("057.graph"),
                //new Instance("058.graph"),
                new Instance("059.graph"),
                new Instance("060.graph"),
                new Instance("061.graph"),
                new Instance("062.graph"),
                new Instance("063.graph"),
                new Instance("064.graph"),
                new Instance("065.graph"),
                new Instance("066.graph"),
                new Instance("067.graph"),
                new Instance("068.graph"),
                new Instance("069.graph"),
                new Instance("070.graph"),
                new Instance("071.graph"),
                new Instance("072.graph"),
                new Instance("073.graph"),
                new Instance("074.graph"),
                new Instance("075.graph"),
                new Instance("076.graph"),
                new Instance("077.graph"),
                new Instance("078.graph"),
                new Instance("079.graph"),
                new Instance("080.graph"),
                new Instance("081.graph"),
                new Instance("082.graph"),
                new Instance("083.graph"),
                new Instance("084.graph"),
                new Instance("085.graph"),
                new Instance("086.graph"),
                new Instance("087.graph"),
                new Instance("088.graph"),
                new Instance("089.graph"),
                new Instance("090.graph"),
                new Instance("091.graph"),
                new Instance("092.graph"),
                new Instance("093.graph"),
                new Instance("094.graph"),
                new Instance("095.graph"),
                new Instance("096.graph"),
                new Instance("097.graph"),
                new Instance("098.graph"),
                new Instance("099.graph"),
                new Instance("100.graph")
        };
        instances = new Instance[]{
                new Instance("037.graph"),
                new Instance("041.graph"),
                new Instance("058.graph")
        };
        long totalTime = 0;

        for (Instance i: instances) {
            Multigraph<Integer, DefaultEdge> graph = loadGraph(i.filename);

            long totalTimeGraph = 0;
            int k = 1;

            Multigraph<Integer, DefaultEdge> clonedgraph = (Multigraph<Integer, DefaultEdge>)graph.clone();
            long startTime = System.nanoTime();
            ReductionSolution solution = Alg.Kernelization.Kernelization.kernelot(clonedgraph, true);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime) / 1_000_000;
            totalTimeGraph +=(endTime - startTime) / 1_000_000;


            System.out.println("#Vertices removed: " + solution.verticesToRemoved.size());
            System.out.println("Graph " + i.filename + " Time:" + (totalTimeGraph / 10) + "ms");


        }

        System.out.println("TEST RESULTS:");
        System.out.println("Total time "
                + (totalTime / 60_000) + " m "
                + ((totalTime / 1000) % 60) + " s "
                + (totalTime % 1000) + " ms "
        );
    }

    static class Instance
    {
        public Instance(String filename) {
            this.filename = filename;
        }

        /**
         * The file name to open
         */
        String filename;
    }
}
