package Alg.TreeDecomposition;

import java.util.ArrayList;
import java.util.Set;
import org.jgrapht.graph.Multigraph;

public class TreeDecomposition {
    
    // Returns the root of a tree decomposition of g
    public static Bag makeTreeDecomposition(Multigraph g){
        Bag root = new Bag();
        
        Set<String> vertices = g.vertexSet();
        for(String vertex: vertices){
            root.add(vertex);
        }
        return root;
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
            
            ArrayList<String> toRem = (ArrayList<String>) b.vert.clone();
            toRem.removeAll(child.vert);
            
            ArrayList<String> toAdd = (ArrayList<String>) child.vert.clone();
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
    
    public static void print(Bag b){
        print(b, 0);
    }
    
    public static void print(Bag b, int d){
        for(int i = 0; i < d; i++){
            System.out.print(" ");
        }
        for(String v: b.vert){
            System.out.print(v + " ");
        }
        System.out.println("");
        for(Bag b2: b.children){
            print(b2, d + 1);
        }
    }
}
