/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Alg.Algorithms.IterativeCompression;

import java.util.LinkedList;

/**
 *
 * @author huib
 */
public class ActionStack
{
    private final LinkedList<GraphAction> actions = new LinkedList<>();
    private final LinkedList<Integer> subroutines = new LinkedList<>();
    
    public void push(GraphAction a)
    {
        a.perform();
        actions.push(a);
    }
    
    public GraphAction pop()
    {
        if(actions.size() > this.getCurrentSubroutine())
            return actions.pop();
        else
            throw new RuntimeException("Cannot pop this action, it was pushed in another subroutine");
    }
    
    public boolean isEmpty()
    {
        return this.actions.isEmpty();
    }
    
    public void startSubroutine()
    {
        subroutines.push(actions.size());
    }
    
    public void stopSubroutine()
    {
        if(actions.size() == this.getCurrentSubroutine())
            subroutines.pop();
        else
            throw new RuntimeException("Cannot end subroutine here, not all actions are reverted");
    }
    
    private int getCurrentSubroutine()
    {
        return subroutines.peek();
    }
}
