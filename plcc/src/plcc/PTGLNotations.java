/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
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
    public Boolean verbose;
    public Boolean adjverbose;
    public Boolean redverbose;
    public Boolean keyverbose;
    public Boolean seqverbose;
    
    public static final String foldNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    public static final String EDGE_ATTRIBUTE_STATUS = "status";
    public static final String STATUS_NOT_VISITED = "0";
    public static final String STATUS_VISITED = "1";
    
    
    List<PTGLNotationFoldResult> results;
    
    /**
     * Disables all debug output.
     */
    public void stfu() {
        this.verbose = false;
        this.adjverbose = false;
        this.redverbose = false;
        this.keyverbose = false;
        this.seqverbose = false;
    }
    
    /**
     * Enables all debug output.
     */
    public void blare() {
        this.verbose = true;
        this.adjverbose = true;
        this.redverbose = true;
        this.keyverbose = true;
        this.seqverbose = true;
    }
    
    
    /**
     * Inits the class with the protein graph it should work on. Atm, the only supported graph types are albe, alpha and beta.
     * @param g the protein graph. Supported graph types are albe, alpha and beta.
     */
    public PTGLNotations(ProtGraph g) {
        
        String gt = g.getGraphType();
        if( ! gt.equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || gt.equals(ProtGraphs.GRAPHTYPE_STRING_ALPHA) || gt.equals(ProtGraphs.GRAPHTYPE_STRING_BETA)) {
            System.err.println("ERROR: PTGLNotations: Graph type not supported. Please provide an alpha, beta or albe graph.");
            System.exit(1);
        }
        
        this.g = g;
        this.verbose = false;
        this.adjverbose = false;
        this.redverbose = false;
        this.keyverbose = false;
        this.seqverbose = false;
        
        this.results = new ArrayList<>();
    }
    
    
    /**
     * Computes all PTGL linear notations for all folds.
     */
    public void computeLinearNotations() {                        
        
        this.results = new ArrayList<>();
        
        // prepare the data we need
        List<FoldingGraph> foldingGraphs = g.getConnectedComponents();
        
        if(verbose) {
            System.out.println("Detected " + foldingGraphs.size() + " connected components.");
            System.out.println("|V|=" + g.getSize() + ", |E|=" + g.getEdgeList().size() + ".");
        }
        
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
            sortedConnectedComponents.put(connComp.get(0), connComp);   // use first vertex of the CC as key
        }
        
        System.out.println("Received " + sortedConnectedComponents.size() + " sorted vertex sets.");
        
        // need to sort entries 
        List<Integer> startVertices = new ArrayList<Integer>();
        startVertices.addAll(sortedConnectedComponents.keySet());
        Collections.sort(startVertices);
        
        // OK, start the linear notation computation
        int adjstart, redstart, keystart, seqstart;
        int foldNum = 0;
        String foldName;
        for(Integer i : startVertices) {
            List<Integer> ccVerts = sortedConnectedComponents.get(i);
            
            if(foldNum <= foldNames.length() - 1) {
                foldName = "" + foldNames.charAt(foldNum);
            } else {
                foldName = "0";     // the last name in the foldNames list
                System.err.println("WARNING: Fold number " + foldNum + " of connected component exceeds available fold name characters. Setting name to non-unique string '" + foldName + "'.");
            }
            
            
            resetEdgeStatus(ccVerts);
            if(verbose) {
                System.out.println("At fold # " + foldNum + ", CC with start vertex " + i + ": " + IO.intListToString(ccVerts));
            }
            
            StringBuilder RED = new StringBuilder();
            StringBuilder ADJ = new StringBuilder();
            StringBuilder KEY = new StringBuilder();
            StringBuilder SEQ = new StringBuilder();
        
            
            String bracketStart = "[";
            String bracketEnd = "]";
            Boolean hasCycle = false;
            
            Boolean isNotBifurcated = g.vertexSetIsNotBifurcated(ccVerts);
            if(verbose) {
                if(isNotBifurcated) {
                    System.out.println("  Fold is not bifurcated -- but may also have a cycle, will check this later.");
                }
            }
            
            
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
                
                if(! isNotBifurcated) {
                    bracketStart = "{";
                    bracketEnd = "}";
                    if(verbose) {
                        System.out.println("  Fold # " + foldNum + " is bifurcated.");
                    }
		} else if(g.hasCycleInVertexSet((ArrayList<Integer>) ccVerts)) {                    
                    bracketStart = "(";
                    bracketEnd = ")";
                    if(verbose) {
                        System.out.println("  Fold # " + foldNum + " has a cycle.");
                    }
                    hasCycle = true;
		} else {
                    if(verbose) {
                        System.out.println("  Fold # " + foldNum + " is not bifurcated.");
                    }
		}
		
		// write the opening bracket and for albe the type of the starting SSE
		ADJ.append(bracketStart);
                RED.append(bracketStart);
                KEY.append(bracketStart);
                SEQ.append(bracketStart);
                
                if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                    if(verbose) {
                        System.out.println("  This is an ALBE graph.");
                    }
                    SEQ.append(g.getVertex(ccVerts.get(0)).getLinearNotationLabel());
                }
                else {
                    if(verbose) {
                        System.out.println("  This is an NOT an ALBE graph, graph type is '" + g.getGraphType() + ".");
                    }
                }
                
                Integer cur = ccVerts.get(0);
                
                adjstart = cur;
		redstart = cur;
		keystart = cur;
		seqstart = cur;
                
                //       This is in line 207 of the Perl script.
                HashMap<Integer, String> order = new HashMap<>();
                
                /** This stores the position of vertex in the list for the RED notation. */
                HashMap<Integer, Integer> pos = new HashMap<>();
                
                pos.put(cur, 1);    // this is 'pos.put(cur, 1)' in the orginal Perl script
                
                // check where to begin: the first vertex with degree 1 in the CC
                Boolean deg1found = false;
                int degree;
                for(int j = 0; j < ccVerts.size(); j++) {
                    degree = g.degreeOfVertex(ccVerts.get(j));
                    if( ! deg1found && degree == 1) {
                        cur = ccVerts.get(j);
                        deg1found = true;
                    }
                    pos.put(ccVerts.get(j), j+1); // this is 'pos.put(ccVerts.get(j), j+1)' in the orginal Perl script
                }
                
                if(verbose) {
                    System.out.println("  pos: " + IO.hashMapToString(pos));
                }
                
                //#########################################################################
                //############################### ADJ #####################################
                //#########################################################################
                
                // line 230 in the Perl script
                // ----------------------------- ADJ notation ----------------------
                if(adjverbose) {
                    System.out.println("  #" + foldNum + " -----ADJ Notation-----");
                }
                Integer adjcur = cur;
                HashSet<Integer> adjvisited = new HashSet<>();
                
                if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                    String startLabel = g.getVertex(cur).getLinearNotationLabel();
                    ADJ.append(startLabel);
                    if(adjverbose) {
                        System.out.println("    Start vertex is " + cur + ", type:" + g.getVertex(cur).getPLCCSSELabel() + ", label=" + startLabel + ".");
                    }
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
                Boolean foundNextVertex = false;
                order.put(adjcur, "+");                
                Boolean hc = hasCycle;  // we need a copy because hc gets reset later
                Integer adjct = 0;
                tvertex.put(adjcur, adjct); // tvertex.put(adjcur, adjct + 1);
                
                
                // line 260 of Perl script
                List<Integer> adjNeighbors;
                //g.resetVertexStates();
                resetEdgeStatus(ccVerts);
                int numIterations = 0;
                while( ! isFinished(adjdegrees, ccVerts) || (hc && (adjvisited.size() <= ccVerts.size()) )) {
                    
                    Boolean fin = isFinished(adjdegrees, ccVerts);
                    
                    if(adjverbose) {
                        System.out.println("    Fold#" + foldNum + "  ===== At iteration #" + numIterations + ", adjcur=" + adjcur + ". adjvisited.size()=" + adjvisited.size() + ", ccVerts.size()=" + ccVerts.size() + ", finished=" + fin + ". =====");
                        System.out.println("    adjvisited (" + adjvisited.size() + " of " + ccVerts.size() + "): " + IO.hashSetToString(adjvisited));
                    }                    
                    numIterations++;
                    
                    adjNeighbors = g.neighborsOf(adjcur);
                    Collections.sort(adjNeighbors);
                    
                    if(adjverbose) {
                        System.out.println("    Fold#" + foldNum + "    Neighbors of " + adjcur + ": " + IO.intListToString(adjNeighbors));
                    } 
                    
                    foundNextVertex = false;
                    next = -1;
                    
                    // line 271 of Perl script   
                    // Determine the next vertex
                    for(int k = 0; k < adjNeighbors.size(); k++) {
                        
                        adjv = adjNeighbors.get(k);
                        
                        if(hc && adjvisited.size() == ccVerts.size()) {
                            foundNextVertex = true;
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
                        
                        if(adjdegrees.get(adjv) <= 0) {
                            continue;
                        }
                        
                        if(! foundNextVertex) {
                            next = adjv;
                            foundNextVertex = true;
                            break;
                        }
                    }
                    
                    // line 304 in Perl script
                    if(foundNextVertex) {
                        left = adjcur;
                        right = next;
                        
                        if(left > right) {
                            left = next;
                            right = adjcur;
                        }
                        
                        edgeType = g.getEdgeLabel(left, right);
                        if(adjverbose) {
                            System.out.println("    Fold#" + foldNum + ": Found next vertex " + next + ". Edge label is '" + edgeType + "'.");
                        }
                        
                        if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && adjvisited.size() > 1)) {
                            ADJ.append(",");
                        }
                        
                        ADJ.append(next - adjcur).append(edgeType.toLowerCase());
                        
                        g.setEdgeAttribute(left, right, PTGLNotations.EDGE_ATTRIBUTE_STATUS, PTGLNotations.STATUS_VISITED);
                        
                        Integer adjcurDegree = adjdegrees.get(adjcur);
                        Integer nextDegree = adjdegrees.get(next);
                        
                        if(adjcurDegree > 0) {
                            adjdegrees.put(adjcur, adjcurDegree - 1);
                        }
                        if(nextDegree > 0) {
                            adjdegrees.put(next, nextDegree - 1);
                        }
                                                
                        if(adjverbose) {
                            System.out.println("    Fold#" + foldNum + ": Found next vertex. Set adjdegree of " + adjcur + " to " + adjdegrees.get(adjcur) + " (was " + adjcurDegree + ")" + ", set adjdegree of " + next + " to " + adjdegrees.get(next) + " (was " + nextDegree + ").");
                        }
                    }
                    else {  // not found
                        
                        if(adjverbose) {
                            System.out.println("    Fold#" + foldNum + ": Did NOT find next vertex, end of path.");
                        }
                        
                        // end of the path
                        next = getVertexDegree1(adjdegrees, ccVerts);
                        if(next != null) {
                            if(adjverbose) {
                                System.out.println("    Fold#" + foldNum + ": Found vertex with degree 1, it is " + next + ".");
                            }
                            if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && adjvisited.size() > 1)) {
                                ADJ.append(",");
                            }
                            
                            ADJ.append((next - adjcur) + "z");
                            
                        } else {
                            next = getVertexDegreeGreater1(adjdegrees, ccVerts);
                            if(next != null) {
                                if(adjverbose) {
                                    System.out.println("    Fold#" + foldNum + ": Found vertex with degree GREATER 1 (z-vertex), it is " + next + " with degree " + adjdegrees.get(next) + ".");
                                }
                                if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && adjvisited.size() > 1)) {
                                    ADJ.append(",");
                                }
                            }
                            else {
                                // next is still null, this makes no sense
                                System.err.println("    ADJ notation error: could not find next vertex in circle.");
                                System.exit(1);
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
                    
                    if(adjverbose) {
                        System.out.println("    Set order of vertex next=" + next + " to '" + order.get(next) + "'. adjcur=" + adjcur + ".");
                    }
                    
                    adjcur = next;
                    
                    if(tvertexList.size() < ccVerts.size()) {
                        adjct++;
                        tvertexList.add(adjcur);
                        //tvertex.put(adjcur, adjct + 1);
                        tvertex.put(adjcur, adjct);
                    }
                    
                    adjvisited.add(next);
                    
                    if(adjverbose) {
                        System.out.println("    ADJ Notation so far: '" + ADJ.toString() + "'.");
                    }
                    
                }
                
                ADJ.append(bracketEnd);
                
                resetEdgeStatus(ccVerts);
                
                if(adjverbose) {
                    System.out.println("    ADJ notation done.");
                    System.out.println("    -----------------------------.");
                }
                
                //#########################################################################
                //############################### RED #####################################
                //#########################################################################
                
                if(redverbose) {
                    System.out.println("    RED notation starts");
                }
                
                Integer redcur = cur;
                HashSet<Integer> redvisited = new HashSet<>();
                
                if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                    RED.append(g.getVertex(redcur).getLinearNotationLabel());
                }
                                                
                redstart = pos.get(redcur);
                redvisited.add(redcur);
                
                if(redverbose) {
                    System.out.println("    Start vertex is " + redcur + ", pos(recur)=" + redstart + ".");
                }
                
                HashMap<Integer, Integer> reddegrees = new HashMap<>();
                for(Integer key : degrees.keySet()) {
                    reddegrees.put(key, degrees.get(key));
                }
                
                hc = hasCycle;
                foundNextVertex = false;
                
                while( ! isFinished(reddegrees, ccVerts) || (hc && redvisited.size() <= ccVerts.size())) {
                    
                    List<Integer> redNeighbors = g.neighborsOf(redcur);
                    Collections.sort(redNeighbors);
                    
                    foundNextVertex = false;
                    next = -1;
                    
                    Integer redv;
                    for(int k = 0; k < redNeighbors.size(); k++) {
                        
                        redv = redNeighbors.get(k);
                        
                        left = redcur;
                        right = redv;
                        
                        if(left > right) {
                            left = redv;
                            right = redcur;
                        }
                        
                        // close cycle
                        if(hc && redvisited.size() == ccVerts.size()) {                            
                            foundNextVertex = true;
                            next = redv;
                            hc = false;                            
                            if(redverbose) {
                                System.out.println("    Cycle closed: Found next vertex " + next + ".");
                            }
                        }
                        
                        
                                                                        
                        String edgeStatus = g.getEdgeAttribute(left, right, PTGLNotations.EDGE_ATTRIBUTE_STATUS);
                        if(edgeStatus != null) {
                            if(edgeStatus.equals(PTGLNotations.STATUS_VISITED)) {
                                continue;
                            }
                        }
                        
                        if(reddegrees.get(redv) <= 0) {
                            continue;
                        }
                        
                        if(! foundNextVertex) {
                            next = redv;
                            foundNextVertex = true;
                            if(redverbose) {
                                System.out.println("    No next set yet, setting next vertex to " + next + ".");
                            }
                            break;
                        }
                    }
                    
                    if(foundNextVertex) {
                        left = redcur;
                        right = next;
                        
                        if(left > right) {
                            left = next;
                            right = redcur;
                        }
                        
                        edgeType = g.getEdgeLabel(left, right);
                        if(redverbose) {
                            System.out.println("    Fold#" + foldNum + ": Found next vertex " + next + ". Edge label is '" + edgeType + "'.");
                        }
                        
                        if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && adjvisited.size() > 1)) {
                            RED.append(",");
                        }
                        
                        RED.append(pos.get(next) - pos.get(redcur)).append(edgeType.toLowerCase());
                        
                        g.setEdgeAttribute(left, right, PTGLNotations.EDGE_ATTRIBUTE_STATUS, PTGLNotations.STATUS_VISITED);
                        
                        Integer redcurDegree = reddegrees.get(redcur);
                        Integer nextDegree = reddegrees.get(next);
                        
                        if(redcurDegree > 0) {
                            reddegrees.put(redcur, redcurDegree - 1);
                        }
                        if(nextDegree > 0) {
                            reddegrees.put(next, nextDegree - 1);
                        }
                                                
                        if(redverbose) {
                            System.out.println("    Fold#" + foldNum + ": Found next vertex. Set reddegree of " + redcur + " to " + reddegrees.get(redcur) + " (was " + redcurDegree + ")" + ", set reddegree of " + next + " to " + reddegrees.get(next) + " (was " + nextDegree + ").");
                        }
                    }
                    else {  // not found
                        
                        if(redverbose) {
                            System.out.println("    Fold#" + foldNum + ": Did NOT find next vertex, end of path.");
                        }
                        
                        // end of the path
                        next = getVertexDegree1(reddegrees, ccVerts);
                        if(next != null) {
                            if(redverbose) {
                                System.out.println("    Fold#" + foldNum + ": Found vertex with degree 1, it is " + next + ".");
                            }
                            if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && redvisited.size() > 1)) {
                                RED.append(",");
                            }
                            
                            RED.append((pos.get(next) - pos.get(redcur)) + "z");
                            
                        } else {
                            next = getVertexDegreeGreater1(reddegrees, ccVerts);
                            if(next != null) {
                                if(redverbose) {
                                    System.out.println("    Fold#" + foldNum + ": Found vertex with degree GREATER 1 (z-vertex), it is " + next + " with degree " + reddegrees.get(next) + ".");
                                }
                                
                                if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && adjvisited.size() > 1)) {
                                    RED.append(",");
                                }
                                
                                RED.append((pos.get(next) - pos.get(redcur)) + "z");
                            }
                            else {
                                // next is still null, this makes no sense
                                System.err.println("    RED notation error: could not find next vertex in circle.");
                                System.exit(1);
                            }
                        }
                    }
                    
                    if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                        RED.append(g.getVertex(next).getLinearNotationLabel());
                    }
                    
                    redcur = next;
                    redvisited.add(next);
                    
                }
                
                RED.append(bracketEnd);                                    

                if(redverbose) {
                    System.out.println("    RED notation done.");
                    System.out.println("    -----------------------------.");
                }
                
                resetEdgeStatus(ccVerts);
                
                //#########################################################################
                //############################### KEY #####################################
                //#########################################################################
                
                if(keyverbose) {
                    System.out.println("    KEY notation starts for fold  #" + foldNum + ".");
                }
                
                if(isNotBifurcated) {
                    Integer keycur = cur;
                    HashSet<Integer> keyvisited = new HashSet<>();
                    
                    List<Integer> keyorder = new ArrayList<>();
                    keyorder.addAll(tvertex.keySet());
                    Collections.sort(keyorder);
                    
                    List<Integer> keypos = new ArrayList<>();
                    keypos.addAll(pos.keySet());
                    Collections.sort(keypos);
                    
                    keystart = tvertex.get(keypos.get(0));
                    
                    if(keyverbose) {
                        System.out.println("    KEY starts. keyorder=" + IO.intListToString(keyorder) + ".");
                        System.out.println("      keypos=" + IO.intListToString(keypos) + ".");
                        System.out.println("      tvertex=" + IO.hashMapToString(tvertex) + ".");
                    }
                    
                    if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                        KEY.append(g.getVertex(keypos.get(0)).getLinearNotationLabel());
                    }
                    
                    int dist;
                    Integer u, v, tu, tv;
                    for(int k = 1; k < keypos.size(); k++) {
                        u = keypos.get(i);
                        v = keypos.get(i-1);
                        tu = tvertex.get(u);
                        tv = tvertex.get(v);
                        dist = tu - tv;
                        
                        if(keyverbose) {
                            System.out.println("      u=" + u + ", v=" + v + ", tu=" + tu + ", tv=" + tv + ", dist(tu,tv)=" + dist + ".");
                        }
                        
                        if((k==1 && g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || (k > 1))) {
                            KEY.append(",");
                        }
                        
                        KEY.append(dist);
                        
                        Integer posi = keypos.get(i);
                        Integer posiminus1 = keypos.get(i-1);
                        String order_posi = order.get(posi);
                        String order_posiminus1 = order.get(posiminus1);
                        
                        if(keyverbose) {
                            System.out.println("posi=" + posi + ", posimines1=" + posiminus1 + ", order_posi=" + order_posi + ", order_posiminus1=" + order_posiminus1 + ".");
                        }
                        
                        if(order_posiminus1.equals(order_posi)) {
                            KEY.append("x");
                        }
                        
                        if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                            KEY.append(g.getVertex(ccVerts.get(k)).getLinearNotationLabel());
                        }
                    }
                    
                    KEY.append(bracketEnd);
                    // KEY done
                }
                else {
                    if(keyverbose) {
                        System.out.println("    No KEY notation available, vertex set bifurcated.");
                        KEY.setLength(0);   // erase all contents in it so far -- this includes the starting bracket which has already been set
                    }
                }
                                
                if(keyverbose) {
                    System.out.println("    KEY notation done.");
                    System.out.println("    -----------------------------.");
                }
                
                //#########################################################################
                //############################### SEQ #####################################
                //#########################################################################
                
                for(int k = 1; k < ccVerts.size(); k++) {
                    if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) || ( ! g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE) && k > 1)) {
                        SEQ.append(",");
                    }
                    
                    int dist = Math.abs(ccVerts.get(k) - ccVerts.get(k-1));
                    SEQ.append(dist);
                    
                    if(g.getGraphType().equals(ProtGraphs.GRAPHTYPE_STRING_ALBE)) {
                        SEQ.append(g.getVertex(ccVerts.get(k)).getLinearNotationLabel());
                    }                    
                }
                
                SEQ.append(bracketEnd);
                
                if(seqverbose) {
                    System.out.println("    SEQ notation done.");
                    System.out.println("    -----------------------------.");
                }                
            }
            
            if(verbose) {
                System.out.println("  #" + foldNum + ": All notations done. Overview:");
            }
            
            if(redverbose) {
                System.out.println("    #" + foldNum + " RED: " + RED.toString());
            }
            
            if(adjverbose) {
                System.out.println("    #" + foldNum + " ADJ: " + ADJ.toString());
            }
            
            if(keyverbose) {
                System.out.println("    #" + foldNum + " KEY: " + KEY.toString());
            }
            
            if(seqverbose) {    
                System.out.println("    #" + foldNum + " SEQ: " + SEQ.toString());
            }
            
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
        Integer v;
        for(int i = 0; i < vertices.size(); i++) {
            v = vertices.get(i);
            if(degrees.get(v) > 0) {
                if(adjverbose) {
                    //System.out.println("  isFinished(): No, degree of vertex #" + i + ", which is " + vertices.get(i) + ", is " + degrees.get(vertices.get(i)) + ". All degrees >0: " + IO.hashMapValuesGreater0ToString(degrees) + ".");
                    System.out.println("  isFinished(): No, degree of vertex #" + i + ", which is " + v + ", is " + degrees.get(vertices.get(i)) + ". All degrees >0: " + IO.hashMapValuesGreater0OfKeysToString(degrees, vertices) + ".");
                }                
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
        //Collections.sort(vertices);
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
        //Collections.sort(vertices);
        for(int i = 0; i < vertices.size(); i++) {
            if(degrees.get(vertices.get(i)) > 1) {
                return vertices.get(i);
            }
        }
        return null;
    }
    
    
    /**
     * Sets the edge status of all the edges of all vertices in the list to STATUS_NOT_VISITED.
     * @param verts a list of vertices. All edges adjacent to these vertices will be affected.
     */
    private void resetEdgeStatus(List<Integer> verts) {
        setEdgeStatusOfVerticesTo(verts, PTGLNotations.STATUS_NOT_VISITED);
    }
    
    
    /**
     * Sets the edge status (visited / not yet visited) of all the edges of all vertices in the list to the given
     * status.
     * @param verts a list of vertices. All edges adjacent to these vertices will be affected.
     * @param status the status to set. Use the constants PTGLNotations.STATUS_*
     */
    private void setEdgeStatusOfVerticesTo(List<Integer> verts, String status) {
        Integer left, right;
        for(Integer v : verts) {
            List<Integer> neighbors = g.neighborsOf(v);
            for(Integer neighbor : neighbors) {
                left = v;
                right = neighbor;
                if(left > right) {
                    left = neighbor; right = v;
                }
                g.setEdgeAttribute(left, right, PTGLNotations.EDGE_ATTRIBUTE_STATUS, status);
            }
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
        p.blare();
        p.computeLinearNotations();
        
        
    }
    
    
    
}
