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
  
    
    public static final String foldNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    
    
    
    public PTGLNotations(ProtGraph g) {
        this.g = g;
    }
    
    
    public void computeLinearNotations() {                        
        
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
        int adjstart, redstart, keystart, seqstart;
        int foldNum = 0;
        for(Integer i : startVertices) {
            List<Integer> ccVerts = sortedConnectedComponents.get(i);
            System.out.println("At fold # " + foldNum + ", CC with start vertex " + i + ": " + IO.intListToString(ccVerts));
            
            StringBuilder RED = new StringBuilder();
            StringBuilder ADJ = new StringBuilder();
            StringBuilder KEY = new StringBuilder();
            StringBuilder SEQ = new StringBuilder();
        
            
            String bracketStart = "[";
            String bracketEnd = "]";
            Boolean hasCycle = false;
            
            Boolean isBifurcated = g.vertexSetIsBifurcated(ccVerts);
            
            
            if (ccVerts.size() == 1){
                ADJ.append(bracketStart);
                RED.append(bracketStart);
                SEQ.append(bracketStart);
                KEY.append(bracketStart);
                
		if(g.getGraphType().equals("albe")) {		     
                     ADJ.append(g.getVertex(ccVerts.get(0)).getLinearNotationLabel());
                     RED.append(g.getVertex(ccVerts.get(0)).getLinearNotationLabel());
                     SEQ.append(g.getVertex(ccVerts.get(0)).getLinearNotationLabel());
                     KEY.append(g.getVertex(ccVerts.get(0)).getLinearNotationLabel());			
		} 
                
                ADJ.append(bracketEnd);
                RED.append(bracketEnd);
                SEQ.append(bracketEnd);
                KEY.append(bracketEnd);
                
                
		adjstart = ccVerts.get(0);
		redstart = ccVerts.get(0);
		keystart = ccVerts.get(0);
		seqstart = ccVerts.get(0);
            } else {
                
                if(! isBifurcated) {
                    bracketStart = "{";
                    bracketEnd = "}";
                    System.out.println("Fold # " + foldNum + " is not bifurcated.");
		} else if(g.hasCycleInVertexSet((ArrayList<Integer>) ccVerts)) {                    
                    bracketStart = "(";
                    bracketEnd = ")";
                    System.out.println("Fold # " + foldNum + " has a cycle.");
                    hasCycle = true;
		} else {
                    System.out.println("Fold # " + foldNum + " is bifurcated.");
		}
		
		// write the opening bracket and for albe the type of the starting SSE
		ADJ.append(bracketStart);
                RED.append(bracketStart);
                KEY.append(bracketStart);
                SEQ.append(bracketStart);
            }
            
            System.out.println("# " + foldNum + " RED: " + RED.toString());
            System.out.println("# " + foldNum + " ADJ: " + ADJ.toString());
            System.out.println("# " + foldNum + " KEY: " + KEY.toString());
            System.out.println("# " + foldNum + " SEQ: " + SEQ.toString());
            
            foldNum++;
        }
                
            
       
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
        
        
    }
    
    
    
}
