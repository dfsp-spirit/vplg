/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2015. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

import java.util.HashMap;
import java.util.Map;
import proteingraphs.FoldingGraph;
import proteingraphs.SSEGraph;
import proteingraphs.SpatRel;
import tools.DP;

/**
 * Colors used in drawing protein graphs. We could consider moving these to the settings.
 * These are hex color codes (HTML-style). You should use something like Color aColor = Color.decode("#FFFFFF") to use these.
 * @author ts
 */
public class ProteinGraphColors {
    
    
    
    public static final String VERTEXCOLOR_HELIX = "#FF0000";    // red
    public static final String VERTEXCOLOR_STRAND = "#000000";   // black
    public static final String VERTEXCOLOR_LIGAND = "#FF00FF";   // magenta
    public static final String VERTEXCOLOR_OTHER = "#808080";    // gray
    
    public static final String EDGECOLOR_PARALLEL = "#FF0000";   // red
    public static final String EDGECOLOR_ANTIPARALLEL = "#0000FF";   // blue
    public static final String EDGECOLOR_MIXED = "#20FF20";  // green
    public static final String EDGECOLOR_LIGAND = "#FF00FF";   // magenta
    public static final String EDGECOLOR_OTHER = "#808080";  // gray
    
    private static final Map<String, String> vertexTypeColorMap;
    static {
        vertexTypeColorMap = new HashMap<>();
        vertexTypeColorMap.put(FoldingGraph.notationLabelHelix, ProteinGraphColors.VERTEXCOLOR_HELIX);
        vertexTypeColorMap.put(FoldingGraph.notationLabelStrand, ProteinGraphColors.VERTEXCOLOR_STRAND);
        vertexTypeColorMap.put(FoldingGraph.notationLabelLigand, ProteinGraphColors.VERTEXCOLOR_LIGAND);
        vertexTypeColorMap.put(FoldingGraph.notationLabelOther, ProteinGraphColors.VERTEXCOLOR_OTHER);        
    }
    
    private static final Map<String, String> edgeTypeColorMap;
    static {
        edgeTypeColorMap = new HashMap<>();
        edgeTypeColorMap.put(SpatRel.STRING_ANTIPARALLEL, ProteinGraphColors.EDGECOLOR_ANTIPARALLEL);
        edgeTypeColorMap.put(SpatRel.STRING_PARALLEL, ProteinGraphColors.EDGECOLOR_PARALLEL);
        edgeTypeColorMap.put(SpatRel.STRING_MIXED, ProteinGraphColors.EDGECOLOR_MIXED);
        edgeTypeColorMap.put(SpatRel.STRING_LIGAND, ProteinGraphColors.EDGECOLOR_LIGAND);
        edgeTypeColorMap.put(SpatRel.STRING_OTHER, ProteinGraphColors.EDGECOLOR_OTHER);
    }
    
    /**
     * Returns a map which maps vertex types (linnot labels, e.g., "e" for beta strand) to colors for drawing the graph.
     * This is the default color map used by the PTGL to color protein ligand graphs.
     * @return the vertex type to color-map
     */
    public static Map<String, String> getVertexTypeColorMap() {
        return vertexTypeColorMap;
    }
    
    /**
     * Returns a map which maps edge types (spatial relation labels, e.g., "p" for parallel) to colors for drawing the graph.
     * This is the default color map used by the PTGL to color protein ligand graphs.
     * @return the edge type to color-map
     */
    public static Map<String, String> getEdgeTypeColorMap() {
        return edgeTypeColorMap;
    }
    
    
    public static Integer[] getRGBColorArrayForVertexFGLinnot(String vfgl) {
        if(vfgl.equals(FoldingGraph.notationLabelHelix)) {
            return new Integer[] { 255, 0, 0 };
        }
        else if(vfgl.equals(FoldingGraph.notationLabelStrand)) {
            return new Integer[] { 0, 0, 0 };
        }
        else if(vfgl.equals(FoldingGraph.notationLabelLigand)) {
            return new Integer[] { 255, 0, 255 };
        }
        else if(vfgl.equals(FoldingGraph.notationLabelOther)) {
            return new Integer[] { 127, 127, 127 };
        }
        else {
            DP.getInstance().w("ProteinGraphColors", "getRGBColorArrayForVertexFGLinnot: unknown vertex linear notation label.");
            return new Integer[] { 127, 127, 127 };
        }
    }
    
    public static String getShapeStringForVertexFGLinnot(String vfgl) {
        if(vfgl.equals(FoldingGraph.notationLabelHelix)) {
            return "disc";
        }
        else if(vfgl.equals(FoldingGraph.notationLabelStrand)) {
            return "square";
        }
        else if(vfgl.equals(FoldingGraph.notationLabelLigand)) {
            return "triangle";
        }
        else if(vfgl.equals(FoldingGraph.notationLabelOther)) {
            return "diamond";
        }
        else {
            DP.getInstance().w("ProteinGraphColors", "getHexColorStringForVertexFGLinnot: unknown vertex linear notation label, returning VERTEXCOLOR_OTHER.");
            return "diamond";
        }
    }
    
    public static String getHexColorStringForVertexFGLinnot(String vfgl) {
        if(vfgl.equals(FoldingGraph.notationLabelHelix)) {
            return ProteinGraphColors.VERTEXCOLOR_HELIX;
        }
        else if(vfgl.equals(FoldingGraph.notationLabelStrand)) {
            return ProteinGraphColors.VERTEXCOLOR_STRAND;
        }
        else if(vfgl.equals(FoldingGraph.notationLabelLigand)) {
            return ProteinGraphColors.VERTEXCOLOR_LIGAND;
        }
        else if(vfgl.equals(FoldingGraph.notationLabelOther)) {
            return ProteinGraphColors.VERTEXCOLOR_OTHER;
        }
        else {
            DP.getInstance().w("ProteinGraphColors", "getHexColorStringForVertexFGLinnot: unknown vertex linear notation label, returning VERTEXCOLOR_OTHER.");
            return "#808080";
        }
    }
    
    public static String getHexColorStringForEdgeFGLinnot(String efgl) {
        if(efgl.equals(SpatRel.STRING_ANTIPARALLEL)) {
            return ProteinGraphColors.EDGECOLOR_ANTIPARALLEL;
        }
        else if(efgl.equals(SpatRel.STRING_PARALLEL)) {
            return ProteinGraphColors.EDGECOLOR_PARALLEL;
        }
        else if(efgl.equals(SpatRel.STRING_MIXED)) {
            return ProteinGraphColors.EDGECOLOR_MIXED;
        }
        else if(efgl.equals(SpatRel.STRING_LIGAND)) {
            return ProteinGraphColors.EDGECOLOR_LIGAND;
        }
        else if(efgl.equals(SpatRel.STRING_OTHER)) {
            return ProteinGraphColors.EDGECOLOR_OTHER;
        }
        else {
            DP.getInstance().w("ProteinGraphColors", "getHexColorStringForEdgeFGLinnot: unknown edge linear notation label, returning EDGECOLOR_OTHER.");
            return "#808080";
        }
    }
    
    public static Integer[] getRGBColorArrayForEdgeFGLinnot(String efgl) {
        if(efgl.equals(SpatRel.STRING_ANTIPARALLEL)) {
            return new Integer[] { 0, 0, 255 };
        }
        else if(efgl.equals(SpatRel.STRING_PARALLEL)) {
            return new Integer[] { 255, 0, 0 };
        }
        else if(efgl.equals(SpatRel.STRING_MIXED)) {
            return new Integer[] { 0, 255, 0 };
        }
        else if(efgl.equals(SpatRel.STRING_LIGAND)) {
            return new Integer[] { 255, 0, 255 };
        }
        else if(efgl.equals(SpatRel.STRING_OTHER)) {
            return new Integer[] { 127, 127, 127 };
        }
        else {
            DP.getInstance().w("ProteinGraphColors", "getgetRGBColorArrayForEdgeFGLinnot: unknown edge linear notation label.");
            return new Integer[] { 127, 127, 127 };
        }
    }
    
}
