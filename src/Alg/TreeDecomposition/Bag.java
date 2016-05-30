package Alg.TreeDecomposition;

import java.util.ArrayList;

public class Bag {
    
    Bag parent;
    ArrayList<Bag> children;
    ArrayList<String> vert;
    
    Bag(){
        children = new ArrayList<>();
        vert = new ArrayList<>();
    }
    
    Bag(Bag b){
        children = new ArrayList<>();
        vert = (ArrayList<String>) b.vert.clone();
    }
    
    void add(String s){
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
        return children.size() == 1;
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
}
