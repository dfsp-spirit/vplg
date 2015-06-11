/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package linnottograph;

import java.util.ArrayList;
import java.util.List;
import plcc.SSEGraph;
import plcc.SpatRel;

/**
 * Utility class which holds some static methods.
 * @author spirit
 */
public class LinnotParserTools {

    protected static Integer getRelDistFromToken(String token) {
        token = LinnotParserTools.stripContactTypes(token);
        token = LinnotParserTools.stripSSETypes(token);
        token = LinnotParserTools.stripZEdgeLabelFromToken(token);
        if (token.isEmpty()) {
            return 1;
        } else {
            Integer i = Integer.parseInt(token);
            return i;
        }
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

    protected static Boolean isBackwardsEdge(String token) {
        if (token.contains("z")) {
            return true;
        }
        return false;
    }

    protected static String stripSSETypes(String token) {
        String[] knownTypes = new String[]{SSEGraph.notationLabelHelix, SSEGraph.notationLabelStrand, SSEGraph.notationLabelLigand};
        for (String s : knownTypes) {
            token = token.replace(s, "");
        }
        return token;
    }

    protected static List<Integer> getRelDistsFromTokenList(String[] tokens, String graphType) {
        List<Integer> dists = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (i == 0) {
                continue;
            }
            String t = tokens[i];
            dists.add(LinnotParserTools.getRelDistFromToken(t));
        }
        return dists;
    }

    protected static String[] getTokensFromLinnot(String linnot) {
        linnot = LinnotParserTools.stripAllBracketsFromLinnot(linnot);
        String[] tokens = linnot.split(",");
        return tokens;
    }

    protected static List<String> getSSETypesFromTokenList(String[] tokens, String graphType) {
        List<String> types = new ArrayList<>();
        for (String t : tokens) {
            types.add(LinnotParserTools.getSSETypeFromToken(t, graphType));
        }
        return types;
    }

    protected static String stripContactTypes(String token) {
        String[] knownTypes = new String[]{SpatRel.STRING_PARALLEL, SpatRel.STRING_MIXED, SpatRel.STRING_ANTIPARALLEL, SpatRel.STRING_LIGAND};
        for (String s : knownTypes) {
            token = token.replace(s, "");
        }
        return token;
    }

    protected static String getDefaultSSE(String graphType) {
        if (graphType.equals(SSEGraph.GRAPHTYPE_ALPHA)) {
            return SSEGraph.notationLabelHelix;
        }
        if (graphType.equals(SSEGraph.GRAPHTYPE_BETA)) {
            return SSEGraph.notationLabelStrand;
        }
        return "?";
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

    protected static String getSSETypeFromToken(String token, String graphType) {
        String[] knownTypes = new String[]{SSEGraph.notationLabelHelix, SSEGraph.notationLabelStrand, SSEGraph.notationLabelLigand};
        for (String s : knownTypes) {
            if (token.contains(s)) {
                return s;
            }
        }
        return LinnotParserTools.getDefaultSSE(graphType);
    }

    protected static String stripZEdgeLabelFromToken(String token) {
        String[] knownTypes = new String[]{"z"};
        for (String s : knownTypes) {
            token = token.replace(s, "");
        }
        return token;
    }
    
}
