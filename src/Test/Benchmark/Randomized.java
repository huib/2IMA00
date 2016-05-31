package Test.Benchmark;

import Alg.FVSAlgorithmInterface;
import Alg.InputReader;
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
public class Randomized extends Benchmark{

    public Randomized() {
        super(new Alg.Algorithms.Randomized());
    }
}
