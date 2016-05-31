package Test.Benchmark;

/**
 * Run a benchmark for a algorithm. For all the example graph, reports the k. And calculates the time it takes to do so
 */
public class Randomized extends Benchmark{

    public Randomized() {
        super(new Alg.Algorithms.Randomized.Randomized());
    }
}
