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
import plcc.SSEGraph;
import plcc.SpatRel;

/**
 * A utility class to parse RED linear notation strings of FGs.
 * @author ts
 */
public class LinnotParser implements ILinnotParser {
    
    private final String linnot;
    private final String[] tokens;
    private final String graphType;
    
    public LinnotParser(String linnot, String graphType) {
        this.linnot = linnot;
        this.tokens = LinnotParser.getTokensFromLinnot(linnot);
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
            if(LinnotParser.isBackwardsEdge(t)) {
                num++;
            }
        }
        return num;
    }

    private static String stripSSETypes(String token) {
        String[] knownTypes = new String[]{SSEGraph.notationLabelHelix, SSEGraph.notationLabelStrand, SSEGraph.notationLabelLigand};
        for (String s : knownTypes) {
            token = token.replace(s, "");
        }
        return token;
    }
    
    private static String stripZEdgeLabelFromToken(String token) {
        String[] knownTypes = new String[]{ "z" };
        for (String s : knownTypes) {
            token = token.replace(s, "");
        }
        return token;
    }

    private static String getDefaultSSE(String graphType) {
        if (graphType.equals(SSEGraph.GRAPHTYPE_ALPHA)) {
            return SSEGraph.notationLabelHelix;
        }
        if (graphType.equals(SSEGraph.GRAPHTYPE_BETA)) {
            return SSEGraph.notationLabelStrand;
        }
        return "?";
    }

    private static List<String> getSSETypesFromTokenList(String[] tokens, String graphType) {
        List<String> types = new ArrayList<>();
        for (String t : tokens) {
            types.add(LinnotParser.getSSETypeFromToken(t, graphType));
        }
        return types;
    }
    
    @Override
    public List<String> getSSETypesList() {
        List<String> types = new ArrayList<>();
        for (String t : tokens) {
            types.add(LinnotParser.getSSETypeFromToken(t, graphType));
        }
        return types;
    }

    protected static String stripAllBracketsFromLinnot(String linnot) {
        linnot = linnot.replace("(", "");
        linnot = linnot.replace(")", "");
        linnot = linnot.replace("[", "");
        linnot = linnot.replace("]", "");
        linnot = linnot.replace("{", "");
        linnot = linnot.replace("}", "");
        return linnot;
    }

    protected static Integer getRelDistFromToken(String token) {
        token = LinnotParser.stripContactTypes(token);
        token = LinnotParser.stripSSETypes(token);
        token = LinnotParser.stripZEdgeLabelFromToken(token);
        if (token.isEmpty()) {
            return 1;
        } else {
            Integer i = Integer.parseInt(token);
            return i;
        }
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
            String vtype = LinnotParser.getSSETypeFromToken(tokens[i], SSEGraph.GRAPHTYPE_ALBELIG);
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
        List<Integer> visited = this.getVisitPath();
        List<Integer> nToCPositions = new ArrayList<>();
              
        Integer indexNtoC;
        Integer reltoStartPos;
        for(int i = 0; i < visited.size(); i++) {
            reltoStartPos = visited.get(i);
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
        return LinnotParser.getRelDistsFromTokenList(this.tokens, this.graphType);
    }
    
    public static List<Integer> getRelDistsFromTokenList(String[] tokens, String graphType) {
        List<Integer> dists = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if(i == 0) { continue; }
            String t = tokens[i];
            dists.add(LinnotParser.getRelDistFromToken(t));
        }
        return dists;
    }

    public static String[] getTokensFromLinnot(String linnot) {
        linnot = LinnotParser.stripAllBracketsFromLinnot(linnot);
        String[] tokens = linnot.split(",");
        return tokens;
    }

    private static String getContactTypeFromToken(String token) {
        String[] knownTypes = new String[]{SpatRel.STRING_PARALLEL, SpatRel.STRING_MIXED, SpatRel.STRING_ANTIPARALLEL, SpatRel.STRING_LIGAND};
        for (String s : knownTypes) {
            if (token.contains(s)) {
                return s;
            }
        }
        return "?";
    }
    
    private static Boolean isBackwardsEdge(String token) {
        if (token.contains("z")) {
            return true;
        }
        return false;   
    }

    protected static String stripContactTypes(String token) {
        String[] knownTypes = new String[]{SpatRel.STRING_PARALLEL, SpatRel.STRING_MIXED, SpatRel.STRING_ANTIPARALLEL, SpatRel.STRING_LIGAND};
        for (String s : knownTypes) {
            token = token.replace(s, "");
        }
        return token;
    }

    public static List<String> getContactTypesFromTokenList(String[] tokens) {
        List<String> types = new ArrayList<>();
        for (String t : tokens) {
            types.add(LinnotParser.getContactTypeFromToken(t));
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
            if( ! LinnotParser.isBackwardsEdge(tokens[i])) {
                edgeType = SpatRel.stringToInt(LinnotParser.getContactTypeFromToken(tokens[i]));
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
            types.add(LinnotParser.getContactTypeFromToken(t));
        }
        return types;
    }

    protected static String getSSETypeFromToken(String token, String graphType) {
        String[] knownTypes = new String[]{SSEGraph.notationLabelHelix, SSEGraph.notationLabelStrand, SSEGraph.notationLabelLigand};
        for (String s : knownTypes) {
            if (token.contains(s)) {
                return s;
            }
        }
        return LinnotParser.getDefaultSSE(graphType);
    }
    
}