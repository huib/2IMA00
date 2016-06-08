/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author huib
 */
public class ActionStack
{
    private final LinkedList<GraphAction> actions = new LinkedList<>();
    
    public void push(GraphAction a)
    {
        a.perform();
        actions.addLast(a);
    }
    
    public GraphAction pop()
    {
        return actions.pollLast();
    }
    
    public boolean isEmpty()
    {
        return this.actions.isEmpty();
    }
}
