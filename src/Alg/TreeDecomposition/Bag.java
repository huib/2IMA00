package Alg.TreeDecomposition;

import java.util.ArrayList;

public class Bag {
    
    Integer num;
    Bag parent;
    ArrayList<Bag> children;
    ArrayList<Integer> vert;
    
    Bag(){
        children = new ArrayList<>();
        vert = new ArrayList<>();
    }
    
    Bag(Bag b){
        children = new ArrayList<>();
        vert = (ArrayList<Integer>) b.vert.clone();
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
    
    Bag findNum(Integer n){
//        System.out.print("Looking for " + n);
        if(n == num){
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
        return null;
    }
}
