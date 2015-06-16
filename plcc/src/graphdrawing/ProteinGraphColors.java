/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphdrawing;

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
    
}
