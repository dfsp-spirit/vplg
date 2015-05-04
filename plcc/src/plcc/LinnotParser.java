/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to parse linear notation strings.
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
    public Integer getNumSSEs() {
        return tokens.length;
    }

    private static String stripSSETypes(String token) {
        String[] knownTypes = new String[]{SSEGraph.notationLabelHelix, SSEGraph.notationLabelStrand, SSEGraph.notationLabelLigand};
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

    protected static String stripAllBrackets(String linnot) {
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
        if (token.isEmpty()) {
            return 1;
        } else {
            Integer i = Integer.parseInt(token);
            return i;
        }
    }

    
    @Override
    public List<Integer> getRelDistList() {
        return LinnotParser.getRelDistsFromTokenList(this.tokens, this.graphType);
    }
    
    public static List<Integer> getRelDistsFromTokenList(String[] tokens, String graphType) {
        List<Integer> dists = new ArrayList<>();
        for (String t : tokens) {
            dists.add(LinnotParser.getRelDistFromToken(t));
        }
        return dists;
    }

    public static String[] getTokensFromLinnot(String linnot) {
        linnot = LinnotParser.stripAllBrackets(linnot);
        String[] tokens = linnot.split(",");
        return tokens;
    }

    protected static String getContactTypeFromToken(String token) {
        String[] knownTypes = new String[]{SpatRel.STRING_PARALLEL, SpatRel.STRING_MIXED, SpatRel.STRING_ANTIPARALLEL, SpatRel.STRING_LIGAND};
        for (String s : knownTypes) {
            if (token.contains(s)) {
                return s;
            }
        }
        return "?";
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
    public List<String> getContactTypesList() {
        List<String> types = new ArrayList<>();
        for (String t : tokens) {
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
