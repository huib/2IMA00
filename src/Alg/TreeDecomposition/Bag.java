package Alg.TreeDecomposition;

import java.util.ArrayList;
import java.util.Objects;

public class Bag {
    
    Integer num;
    Bag parent;
    ArrayList<Bag> children;
    ArrayList<Integer> vert;
    Edge edge;
    
    class Edge {
        Integer a;
        Integer b;
        Edge(Integer a, Integer b){
            this.a = a;
            this.b = b;
        }
        boolean equals(Integer c, Integer d){
            return (a == c && b == d) || (a == d && b == c);
        }
    }
    
    Bag(){
        children = new ArrayList<>();
        vert = new ArrayList<>();
    }
    
    Bag(Bag b){
        children = new ArrayList<>();
        vert = (ArrayList<Integer>) b.vert.clone();
    }
    
    void setEdge(Integer a, Integer b){
        edge = new Edge(a,b);
    }
    
    void add(Integer s){
        if(!vert.contains(s)){
            vert.add(s);
        }
    }
    
    boolean isEmpty(){
        return vert.isEmpty();
    }
    
    boolean isLeaf(){
        return children.isEmpty();
    }
    
    boolean isAddOrForget(){
        return children.size() == 1 && edge == null;
    }
    
    boolean isAdd(){
        if(parent == null){
            return false;
        }
        return parent.vert.size() < vert.size();
    }
    
    boolean isForget(){
        return isAddOrForget() && !isAdd();
    }
    
    void removeLastVert(){
        vert.remove(vert.size()-1);
    }
    
    void insertBelow(Bag b){
        if(!isAddOrForget()){
            System.out.println("~ERROR~!");
        } else {
            b.children.add(children.get(0));
            b.parent = this;
            this.children.set(0, b);
        }
    }
    
    void insertAbove(Bag b){
        parent.insertBelow(b);
    }
    
    Bag findNum(Integer n){
//        System.out.print("Looking for " + n);
        if(n.equals(num)){
//            System.out.println(", is this one");
            return this;
        } else {
//            System.out.println(", I am " + num + ", so maybe in child?");
            for(Bag b: children){
                Bag c = b.findNum(n);
                if(c != null){
                    return c;
                }
            }
        }
        //System.out.println(n + " not found");
        return null;
    }
    
    public int treeWidth(){
        int max = vert.size()-1;
        for(Bag c: children){
            max = Math.max(max, c.treeWidth());
        }
        return max;
    }
}
