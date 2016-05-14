package Alg;

import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.AbstractGraph;

import javax.swing.*;
import java.awt.*;
/**
 * Displays the graph
 */
public class GraphDisplayer {

    /**
     * Create a UI and display the graph in this ui such that it can be visually looked at
     * @param displayGraph
     */
    public static void display(AbstractGraph displayGraph)
    {

        JGraphModelAdapter adapter = new JGraphModelAdapter(displayGraph);
        JGraph jgraph = new JGraph(adapter);

        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(jgraph);

        frame.setSize(new Dimension(1000, 1000));
        frame.setVisible(true);
    }
}
