package Alg.TreeDecomposition;

import java.util.ArrayList;
import java.util.Set;
import org.jgrapht.graph.Multigraph;

public class TreeDecomposition {
    
    // Returns the root of a tree decomposition of g
    // Might completely mangle g
    // If g is not connected, it will simply add edges to make it so
    public static Bag makeTreeDecomposition(Multigraph g){
        // stack overflow check
        try{
            Choice c;
            c = new DeGreedy();
            Bag toRet = permutationToTD(g, c);
            return toRet;
        } catch(StackOverflowError e) {
            return simpleTD(g);
        }
    }
    
    // Turns the given tree decomposition into a nice tree decomposition, i.e. each bag is either:
    // - The root (no parent, 1 child, empty)
    // - Leaf bag (1 parent, no children, empty)
    // - Introduce bag (1 parent, 1 child, contains 1 more vertex than its child)
    // - Forget (1 parent, 1 child, contains 1 less vertex than its child)
    // - Join (1 parent, 2 children, contains the same vertices as its children)
    public static Bag makeNice(Bag b){
        // Make the root nice
        while(!b.isEmpty()){
            Bag b2 = new Bag(b);
            b2.removeLastVert();
            b2.children.add(b);
            b.parent = b2;
            b = b2;
        }
        // Recursively make the rest of the tree nice
        return makeSubtreeNice(b);
    }
    
    // Adds `add edge' bags to the tree
    public static Bag addEdges(Bag b, Multigraph g){
        if(b.isAdd()){
            ArrayList<Integer> ve = (ArrayList<Integer>) b.vert.clone();
            ve.removeAll(b.children.get(0).vert);
            Integer v = ve.get(0);
            for(Integer w: b.vert){
                if(w != v){
                    if(g.containsEdge(v, w) || g.containsEdge(w, v)){
                        Bag c = new Bag(b);
                        c.setEdge(v,w);
                        b.insertAbove(c);
                    }
                }
            }
        }
        for(Bag c: b.children){
            c = addEdges(c, g);
        }
        return b;
    }
    
    // Returns the root of a path decomposition
    public static Bag makePathDecomposition(Multigraph g){
        return simpleTD(g);
    }
    
    // Recursively make a subtree nice
    private static Bag makeSubtreeNice(Bag b){
        if(b.isLeaf()){
            if(!b.isEmpty()){
                Bag b2 = new Bag(b);
                b2.removeLastVert();
                b2.parent = b;
                b2 = makeSubtreeNice(b2);
                b.children.add(b2);
            }
        } else if(b.isAddOrForget()){
            Bag child = b.children.get(0);
            
            ArrayList<Integer> toRem = (ArrayList<Integer>) b.vert.clone();
            toRem.removeAll(child.vert);
            
            ArrayList<Integer> toAdd = (ArrayList<Integer>) child.vert.clone();
            toAdd.removeAll(b.vert);
            
            if(toRem.isEmpty() && toAdd.size() > 1){
                // add
                Bag b2 = new Bag(b);
                b2.vert.add(toAdd.get(0));
                b.insertBelow(b2);
                b2 = makeSubtreeNice(b2);
            } else if( (toRem.isEmpty() && toAdd.size() <= 1) || (toRem.size() == 1 && toAdd.isEmpty()) ){
                // done
                child = makeSubtreeNice(child);
            } else {
                // rem
                Bag b2 = new Bag(b);
                b2.vert.remove(toRem.get(0));
                b.insertBelow(b2);
                b2 = makeSubtreeNice(b2);
            }
        } else {
            // b has at least 2 children
            if(b.children.size() == 2){
                // make sure they are equal
                for(int i = 0; i < 2; i++){
                    Bag child = b.children.get(i);
                    ArrayList<String> vert = (ArrayList<String>) child.vert.clone();
                    vert.removeAll(b.vert);
                    if(child.vert.size() != b.vert.size() || vert.size() > 0){
                        // child not equal to parent. Fix that
                        Bag b2 = new Bag(b);
                        b2.children.add(child);
                        b.children.set(i, b2);
                    }
                    b.children.set(i, makeSubtreeNice(b.children.get(i)));
                }
            } else {
                // > 2 children
                Bag b2 = new Bag(b);
                b2.children.add(b.children.get(0));
                b2.children.add(b.children.get(1));
                b.children.remove(0);
                b.children.remove(0);
                b.children.add(b2);
                b = makeSubtreeNice(b);
            }
        }
        return b;
    }
    
    // Prints the Tree Decomposition (sub)tree starting at this bag
    public static void print(Bag b){
        print(b, 0);
    }
    
    // Prints the Tree Decomposition (sub)tree starting at this bag with an extra indent of size d
    private static void print(Bag b, int d){
        for(int i = 0; i < d; i++){
            System.out.print(" ");
        }
        if(b.vert.isEmpty()){
            System.out.print("∅");
        } else {
            for(Integer v: b.vert){
                if(v < 10){
                    System.out.print(".");
                }
                System.out.print(v + " ");
            }
        }
        System.out.println("");
        for(Bag b2: b.children){
            print(b2, d + 1);
        }
    }
    
    // Returns the tree decomposition gotten by putting all vertices in one bag
    private static Bag simpleTD(Multigraph g){
        Bag root = new Bag();
        
        Set<Integer> vertices = g.vertexSet();
        for(Integer vertex: vertices){
            root.add(vertex);
        }
        return root;
    }
    
    // Converts the given graph g and the list of vertices to a TD.
    // Might completely mangle g and the list of vertices.
    private static Bag permutationToTD(Multigraph g, Choice c){
        // find v0 and its neighbours
        Integer v0 = c.nextChoice(g);
        ArrayList<Integer> neighbours = new ArrayList<>();
        for(Object e: g.edgesOf(v0)){
            Integer t = (Integer) g.getEdgeTarget(e);
            if(!neighbours.contains(t)){
                neighbours.add(t);
            }
            Integer s = (Integer) g.getEdgeSource(e);
            if(!neighbours.contains(s)){
                neighbours.add(s);
            }
        }
        neighbours.remove(v0);
        
        // if n=1 then trivial
        if(g.vertexSet().size() == 1){
            Bag root = new Bag();
            root.add(v0);
            root.num = v0;
            return root;
        }
        
        // If it has no neighbours, the instance was not connected.
        if(neighbours.isEmpty()){
            //System.out.println("The given instance was not connected, adding a random edge...");
            Set<Integer> vertices = g.vertexSet();
            boolean foundOne = false;
            for(Integer v: vertices){
                if(!foundOne){
                    if(v != v0){
                        //System.out.println("Added (" + v0 + ", " + v + ")");
                        g.addEdge(v0, v);
                        foundOne = true;
                        neighbours.add(v);
                    }
                }
            }
        }
        
        // Construct bag with neighbours of v0 in G
        Bag toAdd = new Bag();
        toAdd.add(v0);
        toAdd.num = v0;
        for(Integer i: neighbours){
//            print("Add " + i, d);
            toAdd.add(i);
        }
        
        // v_j = lowest neighbour of v_0
        Integer vj = Integer.MAX_VALUE;
        for(Integer i: neighbours){
            if(i < vj){
                vj = i;
            }
        }
        
        // compute G' obtained by elim. v0: add an edge between all non-adjacent neighbours of v0, then remove v0
        for(Integer s: neighbours){
            for(Integer t: neighbours){
                if(s != t){
                    if(!g.containsEdge(s, t)){
                        g.addEdge(s, t);
                    }
                }
            }
        }
        g.removeVertex(v0);
        
        // call PermToTD(G',(v1,...v_{n-1}))
        if(!g.vertexSet().contains(vj)){
            System.out.println("Paniek!");
        }
        Bag subTD = permutationToTD(g, c);
        
        // connect X_v_0 to X_v_j
        Bag toConnect = subTD.findNum(vj);
        toConnect.children.add(toAdd);
        toAdd.parent = toConnect;
        
        return subTD;
    }
    
}
