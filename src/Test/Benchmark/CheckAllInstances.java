/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test.Benchmark;

import Alg.FVSAlgorithmInterface;
import Alg.SplitSolve;
import Test.Benchmark.Benchmark.Instance;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

/**
 *
 * @author huib
 */
public class CheckAllInstances
{
    public static void main(String[] args) throws FileNotFoundException
    {
        int n = 100;
        Instance[] instances = new Instance[n];
        for(int i=1; i<=n; i++)
            instances[i-1] = new Instance(String.format("%03d", i)+".graph",-1);
        
        benchmark(instances);
    }
    
    public static void benchmark(Instance[] instances) throws FileNotFoundException
    {
        FVSAlgorithmInterface alg = new SplitSolve(new Alg.Algorithms.IterativeCompression.IterativeCompression());
        
        for (Benchmark.Instance i: instances) {
            Multigraph<Integer, DefaultEdge> graph = Benchmark.loadGraph(i.filename);

            SwingWorker worker = new SwingWorker(){
                @Override
                protected Object doInBackground() throws Exception
                {
                    long startTime = System.nanoTime();
                    List<Integer> solution = alg.findFeedbackVertexSet(graph);
                    long endTime = System.nanoTime();
                    
                    System.out.println("Graph " + i.filename + " Time:" + (endTime - startTime) / 1_000_000 + "ms");
                    return solution;
                }
            };
            
            System.out.println("--- "+i.filename+" ---");
            worker.execute();
            
            try
            {
                List<Integer> solution = (List<Integer>) worker.get(20, TimeUnit.SECONDS);
                if(i.k <0){
                    System.out.println("NEW SOLUTION! found a solution with k="+solution.size());
                }
                else if (solution.size() != i.k){
                    System.out.println("MISTAKE! Required k:" + i.k + " Found k:" + solution.size());
                }

                if (!Benchmark.verifySolution(i.filename, solution)) {
                    System.out.println("ERROR, THIS IS NOT A FEEDBACK VERTEX SET!");
                }
            }
            catch(InterruptedException | ExecutionException | TimeoutException ex)
            {
                if(worker.cancel(true))
                    System.out.println(i.filename+ " timed out");
                else
                    System.out.println(i.filename+ " worker not canceling...");
            }
        }
    }
}
