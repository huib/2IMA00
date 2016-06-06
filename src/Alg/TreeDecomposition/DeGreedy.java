package Alg.TreeDecomposition;

import org.jgrapht.graph.Multigraph;

// Greedily chooses the next vertex: the one with the smallest degree. Yes, pun intended.
public class DeGreedy extends Choice {

    @Override
    Integer nextChoice(Multigraph g) {
        int lowestDegreeSoFar = Integer.MAX_VALUE;
        Integer bestChoiceSoFar = -1;
        
        for(Object o : g.vertexSet()){
            Integer i = (Integer)o;
            int deg = g.degreeOf(i);
            if(deg < lowestDegreeSoFar){
                lowestDegreeSoFar = deg;
                bestChoiceSoFar = i;
            }
        }
        return bestChoiceSoFar;
    }

}
