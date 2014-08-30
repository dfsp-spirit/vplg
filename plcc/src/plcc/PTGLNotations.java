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
import java.util.Map;

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
        
        //List<Integer> degrees = g.getAllVertexDegrees();
        HashMap<Integer, Integer> degrees = g.getAllVertexDegreesMap();
        
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
                
		if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {		     
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
                
                if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                    SEQ.append(g.getVertex(ccVerts.get(0)).getLinearNotationLabel());
                }
                
                Integer cur = ccVerts.get(0);
                
                adjstart = cur;
		redstart = cur;
		keystart = cur;
		seqstart = cur;
                
                // TODO: continue here, use the Perl script under plcc/tool/graphs/.
                //       This is in line 207.
                HashMap<Integer, String> order = new HashMap<>();
                HashMap<Integer, Integer> pos = new HashMap<>();
                
                pos.put(cur, 1);
                
                // check where to begin: the first vertex with degree 1 in the CC
                Boolean deg1found = false;
                int degree;
                for(int j = 0; j < ccVerts.size(); j++) {
                    degree = g.degreeOfVertex(ccVerts.get(j));
                    if( ! deg1found && degree == 1) {
                        cur = ccVerts.get(j);
                        deg1found = true;
                    }
                    pos.put(ccVerts.get(j), j+1);
                }
                
                // ----------------------------- ADJ notation ----------------------
                System.out.println("-----ADJ Notation-----");
                Integer adjcur = cur;
                HashSet<Integer> adjvisited = new HashSet<>();
                
                if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                    ADJ.append(g.getVertex(ccVerts.get(0)).getLinearNotationLabel());
                }
                adjstart = cur;
                adjvisited.add(cur);
                //List<Integer> adjdegrees = new ArrayList<>();
                HashMap<Integer, Integer> adjdegrees = new HashMap<>();
                for(Integer key : degrees.keySet()) {
                    adjdegrees.put(key, degrees.get(key));
                }
                
                
                //System.out.println("Degrees: " + IO.intListToString(degrees));
                //System.out.println("ADJ Degrees: " + IO.intListToString(adjdegrees));
                List<Integer> tvertexList = new ArrayList<>();
                tvertexList.add(adjcur);
                
                HashMap<Integer, Integer> tvertex = new HashMap<>();
                int found = 0;
                order.put(adjcur, "+");                
                Boolean hc = hasCycle;
                int adjct = 0;
                tvertex.put(adjcur, adjct + 1);
                
                while( ! isFinished(adjdegrees, ccVerts) || (hc && adjvisited.size() <= ccVerts.size())) {
                    
                }
                
                //throw new java.lang.UnsupportedOperationException("computeLinearNotations(): Not implemented yet");
            }
            
            System.out.println("# " + foldNum + " RED: " + RED.toString());
            System.out.println("# " + foldNum + " ADJ: " + ADJ.toString());
            System.out.println("# " + foldNum + " KEY: " + KEY.toString());
            System.out.println("# " + foldNum + " SEQ: " + SEQ.toString());
            
            foldNum++;
        }
                
            
        
       
    }
    
    /**
     * Checks whether none of the vertices in the list has a degree > 0.
     * @param degrees the degrees Map: key is a vertex index, value is its degree
     * @param vertices the list of vertices
     * @return true if all vertices have a degree of 0 according to the Map, false otherwise
     */
    private Boolean isFinished(HashMap<Integer, Integer> degrees, List<Integer> vertices) {            
        for(int i = 0; i < vertices.size(); i++) {
            if(degrees.get(vertices.get(i)) > 0) {
                return false;
            }
        }
        return true;
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
