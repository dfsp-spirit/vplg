/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import io.IO;

/**
 * An undirected graph. This can be used as is or extended. It is based on the abstract Graph class.
 * @author ts
 */
public class UndirectedGraph extends Graph<Integer> {
    
    /**
     * Creates a new undirected graph from a list of vertices. For a list of length n, an edge matrix
     * of size n*n will be created and initialized with empty edges.
     * @param vertList a list of vertices.
     */
    public UndirectedGraph(ArrayList<Integer> vertList) {
        super(vertList);
    }
    
    public UndirectedGraph() {
        super();
    }
    
    
    /**
     * Testing main
     * @param args 
     */
    public static void main(String[] args) {
        System.out.println("Creating graph...");
        UndirectedGraph g = new UndirectedGraph(new ArrayList<Integer>(Arrays.asList(0, 1, 2)));
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        
        String doc = null;
        String fileName = "tmp_graph.xml";
        
        try {
            doc = g.toGraphMLFormat();
        } catch(Exception e) {
            System.err.println("ERROR: Exception message was '" + e.getMessage() + "'.");
            System.exit(1);
        }
        
        
        IO.stringToTextFile(fileName, doc);
        System.out.println("GraphML representation of graph written to file '" + fileName + "'.");
        System.exit(0);
    }
        
}
