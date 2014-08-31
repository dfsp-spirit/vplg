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
    public static final String EDGE_ATTRIBUTE_STATUS = "status";
    public static final String STATUS_NOT_VISITED = "0";
    public static final String STATUS_VISITED = "1";
    
    
    
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
                Integer next, left, right, adjv;
                String edgeType = "";   // what should we init this to? It seems undef in the Perl script.
                Boolean found = false;
                order.put(adjcur, "+");                
                Boolean hc = hasCycle;
                int adjct = 0;
                tvertex.put(adjcur, adjct + 1);
                
                
                // line 260 of Perl script
                List<Integer> adjNeighbors;
                g.resetVertexStates();
                while( ! isFinished(adjdegrees, ccVerts) || (hc && adjvisited.size() <= ccVerts.size())) {
                    adjNeighbors = g.neighborsOf(adjcur);
                    Collections.sort(adjNeighbors);
                    
                    found = false;
                    next = -1;
                    
                    for(int k = 0; k < adjNeighbors.size(); k++) {
                        
                        adjv = adjNeighbors.get(k);
                        
                        if(hc && adjvisited.size() == ccVerts.size()) {
                            found = true;
                            next = adjv;
                            hc = false;                            
                        }
                        
                        left = adjcur;
                        right = adjv;
                        
                        if(left > right) {
                            left = adjv;
                            right = adjcur;
                        }
                        
                        String edgeStatus = g.getEdgeAttribute(left, right, PTGLNotations.EDGE_ATTRIBUTE_STATUS);
                        if(edgeStatus != null) {
                            if(edgeStatus.equals(PTGLNotations.STATUS_VISITED)) {
                                continue;
                            }
                        }
                        
                        if(adjdegrees.get(adjv) == 0) {
                            continue;
                        }
                        
                        if(! found) {
                            next = adjv;
                            found = true;
                            break;
                        }
                    }
                    
                    // line 304 in Perl script
                    if(found) {
                        left = adjcur;
                        right = next;
                        
                        if(left > right) {
                            left = next;
                            right = adjcur;
                        }
                        
                        edgeType = g.getEdgeLabel(left, right);
                        
                        if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && adjvisited.size() > 1)) {
                            ADJ.append(",");
                        }
                        
                        ADJ.append(next - adjcur).append(edgeType.toLowerCase());
                        
                        g.setEdgeAttribute(left, right, PTGLNotations.EDGE_ATTRIBUTE_STATUS, PTGLNotations.STATUS_VISITED);
                        
                        adjdegrees.put(adjcur, adjdegrees.get(adjcur) - 1);
                        adjdegrees.put(next, adjdegrees.get(next) - 1);
                    }
                    else {  // not found
                        // end of the path
                        next = getVertexDegree1(adjdegrees, ccVerts);
                        if(next != null) {
                            if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && adjvisited.size() > 1)) {
                                ADJ.append(",");
                            }
                            
                            ADJ.append((next - adjcur) + "z");
                            
                        } else {
                            next = getVertexDegreeGreater1(adjdegrees, ccVerts);
                            if(next != null) {
                                if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && adjvisited.size() > 1)) {
                                    ADJ.append(",");
                                }
                            }
                            else {
                                System.err.println("ADJ notation error: could not find next vertex in circle.");
                            }
                        }
                    }
                    
                    if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                        ADJ.append(g.getVertex(next).getLinearNotationLabel());
                    }
                    
                    // ordering, line 352
                    if(edgeType.equals("p") || edgeType.equals("m")) {
                        order.put(next, order.get(adjcur));
                    }else if(order.get(adjcur).equals("-")) {
                        order.put(next, "+");
                    }else{
                        order.put(next, "-");
                    }
                    
                    adjcur = next;
                    
                    if(tvertexList.size() < ccVerts.size()) {
                        adjct++;
                        tvertexList.add(adjcur);
                        tvertex.put(adjcur, adjct + 1);
                    }
                    
                    adjvisited.add(next);
                    
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
     * Returns the first vertex with degree 1 from the list, or null if the list contains no such vertex.
     * @param degrees the degrees Map: key is a vertex index, value is its degree
     * @param vertices the list of vertices
     * @return the first vertex with degree 1 from the list, or null if the list contains no such vertex
     */
    private Integer getVertexDegree1(HashMap<Integer, Integer> degrees, List<Integer> vertices) {            
        Collections.sort(vertices);
        for(int i = 0; i < vertices.size(); i++) {
            if(degrees.get(vertices.get(i)) == 1) {
                return vertices.get(i);
            }
        }
        return null;
    }

    
    /**
     * Returns the first vertex with degree greater 1 from the list, or null if the list contains no such vertex.
     * @param degrees the degrees Map: key is a vertex index, value is its degree
     * @param vertices the list of vertices
     * @return the first vertex with degree greater 1 from the list, or null if the list contains no such vertex
     */
    private Integer getVertexDegreeGreater1(HashMap<Integer, Integer> degrees, List<Integer> vertices) {            
        Collections.sort(vertices);
        for(int i = 0; i < vertices.size(); i++) {
            if(degrees.get(vertices.get(i)) > 1) {
                return vertices.get(i);
            }
        }
        return null;
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
