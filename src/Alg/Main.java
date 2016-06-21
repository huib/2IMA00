package Alg;

import Alg.Algorithms.IterativeCompression.IterativeCompression;
import Alg.Algorithms.Randomized.Randomized;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import Alg.Algorithms.Randomized.RandomizedDensity;
import Alg.Kernelization.ReductionSolution;
import jdk.internal.util.xml.impl.Input;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

public class Main {

    public static void main(String[] args) {

        // Read from command line
        // Scanner scanner = new Scanner(System.in);

        // Read from file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("instances/095.graph"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        InputWrapper input = InputReader.loopSafeReadGraph(scanner);


        //FVSAlgorithmInterface randomized = new Randomized();
        FVSAlgorithmInterface randomized = new SplitSolve(new Randomized());

        List<Integer> solution = randomized.findFeedbackVertexSet(input.reductionSolution);

        for (Integer s: solution) {
            System.out.print(input.nameMapping.get(s) + ", ");
        }


    }
}
