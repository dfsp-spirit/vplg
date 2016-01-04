/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package algorithms;

import datastructures.IMutableGraph;
import datastructures.SimpleGraphInterface;
import datastructures.SparseGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Randomizes (rewires) the edges of the given graph with the given probability.
 * @author spirit
 */
public class GraphRandomizer {
    
    private static Random rnd = new Random();
    
    
    /**
     * Constructor, instantly randomizes the edges of the given graph with the given probability, CHANGING the graph.
     * @param g the input graph, will be changed
     * @param edgeRewireProb the edge rewiring probability
     */
    public GraphRandomizer(SparseGraph g, Double edgeRewireProb) {
        Double draw;

        List<Integer[]> allEdges = g.getEdgeListIndex();
        List<Integer[]> edgesToChange = new ArrayList<>();
        
        // determine which edges to change
        for(int i = 0; i < allEdges.size(); i++) {
            draw = Math.random();
            if(draw <= edgeRewireProb) {
                edgesToChange.add(allEdges.get(i));
            }
            
        }
        
        // change edges
        Integer[] e;
        for(int i = 0; i < edgesToChange.size(); i++) {
            e = edgesToChange.get(i);
            
            // delete old edge
            g.deleteEdge(e[0], e[1]);
            
            // now add a new edge for it
            int edgeTo = GraphRandomizer.getRandomVertex(g);
            if(rnd.nextBoolean()) {
                // keep start vertex identical
                while(g.containsEdge(e[0], edgeTo)) {   // ensure we really need a new edge, the one we intend to create may already exist
                    edgeTo = GraphRandomizer.getRandomVertex(g);
                }
                g.addEdge(e[0], edgeTo, null);
            }
            else {
                // keep target vertex identical
                while(g.containsEdge(edgeTo, e[0])) {   // ensure we really need a new edge, the one we intend to create may already exist
                    edgeTo = GraphRandomizer.getRandomVertex(g);
                }
                g.addEdge(edgeTo, e[0], null);
            }
        }
        
        
        
        
    }
    
    /**
     * Returns a random vertex from g, by index.
     * @param g a graph, must not be empty (no verts)
     * @return a vertex, by index
     */
    private static int getRandomVertex(SparseGraph g) {
        return GraphRandomizer.getRandomIntBtween(0, g.getNumVertices());
    }
  
    
  /**
    * Returns a pseudo-random number between min and max, inclusive.
    * The difference between min and max can be at most
    * <code>Integer.MAX_VALUE - 1</code>.
    *
    * @param min Minimum value
    * @param max Maximum value.  Must be greater than min.
    * @return Integer between min and max, inclusive.
    */
    public static int getRandomIntBtween(int min, int max) {
        int randomNum = rnd.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
}
