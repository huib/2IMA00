package Alg;

import Alg.Kernelization.Kernelization;
import Alg.Kernelization.ReductionSolution;
import Alg.Kernelization.Splitter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Multigraph;

/**
 * Wrapper for the splitter kernelization. Uses the splitter to split the problem into smaller parts. Then calls the
 * algorithm for all the diffferent parts, and combines the result
 */
public class SplitSolve implements FVSAlgorithmInterface {

    /**
     * Give the algorithm  to solve the problem
     * @param implementation
     */
    public SplitSolve(FVSAlgorithmInterface implementation) {
        this.implementation = implementation;
    }

    FVSAlgorithmInterface implementation;

    @Override
    public List<Integer> findFeedbackVertexSet(ReductionSolution partialSolution) {
        List<Integer> result = findFeedbackVertexSet(partialSolution.reducedGraph);
        result.addAll(partialSolution.verticesToRemoved);
        return result;
    }

    @Override
    public List<Integer> findFeedbackVertexSet(Multigraph<Integer, DefaultEdge> graph) {
        List<Multigraph<Integer, DefaultEdge>> split = Splitter.split(graph);
        
        int i=0;
        List<Integer> solution = new LinkedList<>();
        for(Multigraph<Integer, DefaultEdge> g : split)
        {
            i++;
            try
            {
                ReductionSolution reduced = Kernelization.kernelot(g, true);
                solution.addAll(reduced.verticesToRemoved);
                if(!reduced.reducedGraph.vertexSet().isEmpty())
                    solution.addAll(this.implementation.findFeedbackVertexSet(reduced.reducedGraph));
            }
            catch(RuntimeException ex)
            {
                System.out.println("Interrupted while solving component "+i+"/"+split.size());
                throw new RuntimeException(ex);
            }
        }
        
        return solution;
        
        /*
        return Splitter.split(graph)    // split the graph
                .stream()
                .map(g -> this.implementation.findFeedbackVertexSet(g)) // solve each subgraphs
                .flatMap(l -> l.stream())   // stream all the results
                .collect(Collectors.toList()); // combine them in one big list
        //*/

    }
}
