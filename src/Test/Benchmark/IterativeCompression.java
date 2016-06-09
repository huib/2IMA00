package Test.Benchmark;

/**
 * Run a benchmark for a algorithm. For all the example graph, reports the k. And calculates the time it takes to do so
 */
public class IterativeCompression extends Benchmark{

    public IterativeCompression() {
        super(new Alg.Algorithms.IterativeCompression.IterativeCompression());
    }
}
