package Alg;

import Alg.Algorithms.IterativeCompression.IterativeCompression;
import Alg.Algorithms.Randomized.Randomized;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Read from command line
        Scanner scanner = new Scanner(System.in);

        // Read from file
//        Scanner scanner = null;
//        try {
//            scanner = new Scanner(new File("instances/065.graph"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return;
//        }

        InputWrapper input = InputReader.loopSafeReadGraph(scanner);


        //FVSAlgorithmInterface alg = new Randomized();
        //FVSAlgorithmInterface alg = new SplitSolve(new Randomized());
        FVSAlgorithmInterface alg = new SplitSolve(new IterativeCompression());

        List<Integer> solution = alg.findFeedbackVertexSet(input.reductionSolution);

        for (Integer s: solution) {
            System.out.println(input.nameMapping.get(s));
        }


    }
}
