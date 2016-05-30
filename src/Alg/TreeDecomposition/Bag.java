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
    
    void add(String s){
        if(!vert.contains(s)){
            vert.add(s);
        }
    }
}
