/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plcc;

import datastructures.SimpleGraphDrawer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author spirit
 */
public class PTGLNotations {
    
    
    ProtGraph g;
    String redNotation;
    String adjNotation;

    public String getRedNotation() {
        return redNotation;
    }

    public String getAdjNotation() {
        return adjNotation;
    }

    public String getKeyNotation() {
        return keyNotation;
    }

    public String getSeqNotation() {
        return seqNotation;
    }
    String keyNotation;
    String seqNotation;
    
    public static final String foldNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    
    
    
    public PTGLNotations(ProtGraph g) {
        this.g = g;
        redNotation = adjNotation = keyNotation = seqNotation = "";
    }
    
    
    public void computeLinearNotations() {
        
        redNotation = adjNotation = keyNotation = seqNotation = "";
        
        StringBuilder RED = new StringBuilder();
        StringBuilder ADJ = new StringBuilder();
        StringBuilder KEY = new StringBuilder();
        StringBuilder SEQ = new StringBuilder();
        
        
        // prepare the data we need
        List<FoldingGraph> foldingGraphs = g.getConnectedComponents();
        System.out.println("Detected " + foldingGraphs.size() + " connected components.");
        
        List<List<Integer>> connectedComponents = new ArrayList<List<Integer>>();
        for(int i = 0; i < foldingGraphs.size(); i++) {
            List<Integer> vertexOrdering = foldingGraphs.get(i).getVertexIndexListInParentGraph();
            connectedComponents.add(vertexOrdering);
            //System.out.println(" Rec: " + IO.intListToString(vertexOrdering));
        }
        
        //System.out.println("Received " + connectedComponents.size() + " vertex sets.");
        //for (int i = 0; i < connectedComponents.size(); i++) {
        //    System.out.println("  " + IO.intListToString(connectedComponents.get(i)));
        //}
        
        List<Integer> degrees = g.getAllVertexDegrees();
        
        /** Contains a list of the sorted connected components of g. The keys are the indices of the left-most vertex in each set (and the values are the set). */
        HashMap<Integer, List<Integer>> sortedConnectedComponents = new HashMap<Integer, List<Integer>>();
        for(List<Integer> connComp : connectedComponents) {
            Collections.sort(connComp);
            //System.out.println("Sorted CC: " + IO.intListToString(connComp));
            sortedConnectedComponents.put(connComp.get(0), connComp);
        }
        
        System.out.println("Received " + sortedConnectedComponents.size() + " sorted vertex sets.");
        
        // need to sort entries 
        List<Integer> startVertices = new ArrayList<Integer>();
        startVertices.addAll(sortedConnectedComponents.keySet());
        Collections.sort(startVertices);
        
        // OK, start the linear notation computation
        for(Integer i : startVertices) {
            List<Integer> verts = sortedConnectedComponents.get(i);
            //System.out.println("CC with start vertex " + i + ": " + IO.intListToString(verts) + "\n");
            
        }
        
        String bracketStart = "[";
        String bracketEnd = "]";
        
        
        
        
        // all done, set results
        this.redNotation = RED.toString();
        this.adjNotation = ADJ.toString();
        this.keyNotation = KEY.toString();
        this.seqNotation = SEQ.toString();
    }
    
    
    
    /**
     * Testing main only
     * @param args ignored
     */
    public static void main(String[] args) {
        
        //ProtGraph g = ProtGraphs.generateRandomPG(10, "albe", "A", "f4k3");
        ProtGraph g = ProtGraphs.generate_7tim_A_albe();
        
        SimpleGraphDrawer sgd1 = new SimpleGraphDrawer(g);
        System.out.println("Graph :\n" + sgd1.getGraphConsoleDrawing());
        
        PTGLNotations p = new PTGLNotations(g);
        p.computeLinearNotations();
        
        System.out.println("RED: " + p.getRedNotation());
        System.out.println("ADJ: " + p.getAdjNotation());
        System.out.println("KEY: " + p.getKeyNotation());
        System.out.println("SEQ: " + p.getSeqNotation());
    }
    
    
    
}
