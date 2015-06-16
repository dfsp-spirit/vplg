/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

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
    
}
