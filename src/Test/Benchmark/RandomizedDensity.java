package Test.Benchmark;

/**
 * Run a benchmark for a algorithm. For all the example graph, reports the k. And calculates the time it takes to do so
 */
public class RandomizedDensity extends Benchmark{

    public RandomizedDensity() {
        super(new Alg.Algorithms.Randomized.RandomizedDensity());
    }
}
