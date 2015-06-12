/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package linnottograph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import proteingraphs.SSEGraph;
import proteingraphs.SpatRel;
import tools.DP;

/**
 * A utility class to parse RED linear notation strings of FGs. Note that this parses a linnot string,
 * but constructs a PG (not an FG) from it. You can use the PG function to compute FGs in order to get
 * an FG from it (there will only be a single FG obviously).
 * @author ts
 */
public class LinnotParserRED implements ILinnotParser {
    
    protected final String linnot;
    protected final String[] tokens;
    protected final String graphType;
    
    public LinnotParserRED(String linnot, String graphType) {
        this.linnot = linnot;
        this.tokens = LinnotParserTools.getTokensFromLinnot(linnot);
        this.graphType = graphType;
    }
    
    @Override
    public Integer getNumParsedSSEs() {
        
        return tokens.length;
    }
    
    @Override
    public Integer getNumParsedEdges() {
        return tokens.length - 1;
    }
    
    @Override
    public Integer getNumBackEdges() {
        int num = 0;
        for(String t : tokens) {
            if(LinnotParserTools.isBackwardsEdge(t)) {
                num++;
            }
        }
        return num;
    }

    
    @Override
    public List<String> getSSETypesList() {
        List<String> types = new ArrayList<>();
        for (String t : tokens) {
            types.add(LinnotParserTools.getSSETypeFromToken(t, graphType));
        }
        return types;
    }

    
    /**
     * Returns the path of visited vertices. Starts with the vertex visited first, which is given the value 0. All distances are relative to this one.     
     * @return 
     */
    @Override
    public List<Integer> getVisitPath() {
        List<Integer> relDistList = this.getRelDistList();
        List<Integer> p = new ArrayList<>();        
        
        Integer current = 0;
        p.add(current);
        for(Integer rel : relDistList) {
            current += rel;
            p.add(current);
        }
                        
        return p;
    }
    
    /**
     * Returns how much left of the starting vertex the left-most vertex is.
     * @return 
     */
    @Override
    public Integer getMaxShiftLeft() {
        List<Integer> relDistList = this.getRelDistList();
        Integer min = 0;
        Integer current = 0;
        for(Integer rel : relDistList) {
            current += rel;
            if(current < min) {
                min = current;
            }
        }
                        
        return min;
    }
    protected Integer getMaxShiftRight() {
        List<Integer> relDistList = this.getRelDistList();
        Integer max = 0;
        Integer current = 0;
        for(Integer rel : relDistList) {
            current += rel;
            if(current > max) {
                max = current;
            }
        }
                        
        return max;
    }
    
    @Override
    public Boolean distancesMakeSense() {
        Integer min = this.getMaxShiftLeft();
        Integer max = this.getMaxShiftRight();
        Integer numVertsExpected = Math.abs(min) + Math.abs(max) + 1;
        return(numVertsExpected.equals(this.getVertexTypesNtoC().size()));
    }
    
    @Override
    public List<String> getVertexTypesNtoC() {
        List<String> vtypes = new ArrayList<>();
        Integer maxShift = this.getMaxShiftLeft();
        List<Integer> visited = this.getVisitPath();
        int numVerts = this.getAllVisitedVertices().size();
        Map<Integer, String> m = new HashMap<>();
        
        //System.out.println("visited " + visited.size() + ". maxShift = " + maxShift + "." );
        
        Integer indexNtoC;
        Integer reltoStartPos;
        for(int i = 0; i < visited.size(); i++) {
            reltoStartPos = visited.get(i);
            String vtype = LinnotParserTools.getSSETypeFromToken(tokens[i], SSEGraph.GRAPHTYPE_ALBELIG);
            indexNtoC = reltoStartPos - maxShift;
            //System.out.println("indexNtoC=" + indexNtoC);
            m.put(indexNtoC, vtype);
        }
        
        for(int i = 0; i < numVerts; i++) {
            vtypes.add(m.get(i));
        }                
        
        
        return vtypes;
    }
    
    @Override
    public List<Integer> getNtoCPositionsOfVisitPath() {
        Integer maxShift = this.getMaxShiftLeft();
        List<Integer> visitPath = this.getVisitPath();
        List<Integer> nToCPositions = new ArrayList<>();
              
        Integer indexNtoC;
        Integer reltoStartPos;
        for(int i = 0; i < visitPath.size(); i++) {
            reltoStartPos = visitPath.get(i);
            indexNtoC = reltoStartPos - maxShift;
            nToCPositions.add(indexNtoC);
        }
                
        return nToCPositions;
    }
    
    /** Returns a sorted list of all visited vertices. This is NOT the visiting order (and vertices visited several times only appear once in this list).  */
    @Override
    public List<Integer> getAllVisitedVertices() {      
        List<Integer> visitPath = this.getVisitPath();
        Set<Integer> s = new HashSet<>(visitPath);
        List<Integer> l = new ArrayList<>(s);
        Collections.sort(l);
        return l;
    }

    
    /**
     * Returns the list of relative distances, parsed from the token list.
     * @return 
     */
    @Override
    public List<Integer> getRelDistList() {
        return LinnotParserTools.getRelDistsFromTokenList(this.tokens, this.graphType);
    }
    

    public static List<String> getContactTypesFromTokenList(String[] tokens) {
        List<String> types = new ArrayList<>();
        for (String t : tokens) {
            types.add(LinnotParserTools.getContactTypeFromToken(t));
        }
        return types;
    }
    
    @Override
    public List<Integer[]> getNonZEdges() {
        List<Integer[]> edges = new ArrayList<>();
        List<Integer> visitPath = this.getVisitPath();
        
        if(visitPath.size() < 2) {
            return edges;   // empty
        }
        
        Integer last = 0;
        Integer current = null;
        Integer edgeType = SpatRel.stringToInt(SpatRel.STRING_MIXED);
        for (int i = 1; i < visitPath.size(); i++) {
            current = visitPath.get(i);
            if( ! LinnotParserTools.isBackwardsEdge(tokens[i])) {
                edgeType = SpatRel.stringToInt(LinnotParserTools.getContactTypeFromToken(tokens[i]));
                edges.add(new Integer[] { last, current, edgeType });
            }
            last = current;
        }
        
        return edges;
    }
    
    
    @Override
    public List<Integer[]> getOutGraphEdges() {
        List<Integer[]> shiftedEdges = this.getNonZEdges();
        List<Integer[]> finalEdges = new ArrayList<>();
        Integer maxShift = this.getMaxShiftLeft();
                       
        Integer [] oe;
        for(Integer[] e : shiftedEdges) {
            oe = new Integer[]{ e[0] - maxShift, e[1] - maxShift, e[2] };
            finalEdges.add(oe);
        }
        
        if(! this.distancesMakeSense()) {
            DP.getInstance().w("LinnotParserRED", "getOutGraphEdges: Distances make no sense, linnot may not be a valid RED linnot string.");
        }
        return finalEdges;
    }
    
    
    @Override
    public List<String> getContactTypesList() {
        List<String> types = new ArrayList<>();
        if(tokens.length < 2) {
            return types;
        }
        for (int i = 1; i < tokens.length; i++) {
            String t = tokens[i];
            types.add(LinnotParserTools.getContactTypeFromToken(t));
        }
        return types;
    }

    
}
