package Test.Benchmark;

import java.io.FileNotFoundException;

/**
 * Run a benchmark for a algorithm. For all the example graph, reports the k. And calculates the time it takes to do so
 */
public class IterativeCompression extends Benchmark{

    public IterativeCompression() {
        super(new Alg.Algorithms.IterativeCompression.IterativeCompression());
        //super(new Alg.Algorithms.Randomized.RandomizedDensity());
    }
    
    
    public static void main(String[] args) throws FileNotFoundException
    {
        IterativeCompression test = new IterativeCompression();
        
        //test.benchMark();
        test.largerBenchMark();
        //test.sub20SecondProblems();
        //test.promisingProblems();
        //test.orderBenchMark();
    }
}
