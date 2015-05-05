/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package htmlgen;

import java.util.ArrayList;
import java.util.List;
import plcc.Position3D;
import plcc.Residue;
import plcc.SSE;
import plcc.SSEGraph;
import plcc.SpatRel;

/**
 * Some helper functions which generate the Jmol commands to visualize stuff.
 * @author ts
 */
public class JmolTools {
    
    public static final Double drawSSEcircleDiameter = 5.0;
    
    /**
     * Produces the Jmol command to draw a line from start to end, with a reference name of 'refName'.
     * Example command: 'draw myline1 color blue { 1 1 1} {5 5 5};'
     * @param refName the name to assign to the drawn object
     * @param start the 3D start position of the line
     * @param end the 3D end position of the line
     * @return the Jmol command to draw the line
     */
    public static String getCommandDrawLine(String refName, Position3D start, Position3D end, String color) {
        StringBuilder sb = new StringBuilder();
        sb.append("draw ").append(refName).append(" color ").append(color).append(" ").append(JmolTools.posString(start)).append(" ").append(JmolTools.posString(end)).append("; ");
        return sb.toString();        
    }
    
    
    /**
     * Generates the command to draw a circle around the given atom.
     * Example command: 'draw mycircle diameter 2.0 circle {atomno=3};'
     * @param refName the name to assign to the drawn object
     * @param atomNum the atom number in the data file
     * @param color the color (try 'red' or 'green')
     * @return the Jmol command to draw the circle
     */
    public static String getCommandDrawSSECircleAroundAtom(String refName, Integer atomNum, Double diameter, String color) {
        StringBuilder sb = new StringBuilder();
        sb.append("draw ").append(refName).append(" diameter ").append(diameter).append(" color ").append(color).append(" circle {atomno=").append(atomNum).append("}; ");
        return sb.toString();        
    }
    
    /**
     * Returns the Jmol position string of this position.
     * @param p the position
     * @return the Jmol position string, e.g., '{1 2 5}'
     */ 
    public static String posString(Position3D p) {
        return "{" + p.x + " " + p.y + " " + p.z + "}";
    }
    
    /**
     * Generates the command to draw a circle around the given atom.
     * Example command: 'draw mycircle3 color green diameter 7.0 circle {4 4 4};'
     * @param refName the name to assign to the drawn object
     * @param atomNum the atom number in the data file
     * @param color the color (try 'red' or 'green')
     * @return the Jmol command to draw the circle
     */
    public static String getCommandDrawSSECircleAtPosition(String refName, Position3D position, Double diameter, String color) {
        StringBuilder sb = new StringBuilder();
        sb.append("draw ").append(refName).append(" color ").append(color).append(" diameter ").append(diameter).append(" circle ").append(JmolTools.posString(position)).append("; ");
        return sb.toString();        
    }
    
    /**
     * Returns the Jmol color string for a certain SSE type.
     * @param sse the SSE
     * @return the Jmol color string for this SSE type, e.g., "red"
     */
    public static String getColorForSSE(SSE sse) {
        if(sse.isHelix()) {
            //return "red";
            return "[255 0 0]";
        }
        else if(sse.isBetaStrand()) {
            return "black";
        }
        else if(sse.isLigandSSE()) {
            //return "purple";
            return "[255 9 255]";
        }
        else {
            return "gray";
        }
    }
    
    
    /**
     * Returns the jmol command to hide water.
     * @return the jmol command to hide water.
     */
    public static String getHideWatersCommand() {
        return "hide waters;";
    }
    
    /**
     * Returns the Jmol color string for a certain spatial orientation type.
     * @param spatRel the spatial orientation (between two SSEs), e.g., SpatRel.PARALLEL
     * @return the Jmol color string, e.g., "red"
     */
    public static String getColorForSpatRel(Integer spatRel) {
        if(spatRel.equals(SpatRel.ANTIPARALLEL)) {
            //return "blue";
            return "[51 51 255]";
        }
        else if(spatRel.equals(SpatRel.PARALLEL)) {            
            //return "red";
            return "[255 0 0]";
        }
        else if(spatRel.equals(SpatRel.LIGAND) ){
            //return "purple";
            return "[255 9 255]";
        }
        else if(spatRel.equals(SpatRel.MIXED) ){
            //return "green";
            return "[51 255 51]";
        }
        else if(spatRel.equals(SpatRel.DISULFIDE) ){
            return "yellow";
        }
        else if(spatRel.equals(SpatRel.COMPLEX) ){
            return "orange";
        }
        else {
            return "gray";
        }
    }
    
    
    /**
     * High-level command to visualize an SSE.
     * @param s the SSE
     * @return the Jmol command
     */
    public static String visualizeSSECommands(SSE s) {
        StringBuilder sb = new StringBuilder();
        
        String label = s.shortLabel();
        Position3D sseCenter = s.getCentralAtomPosition();
        if(sseCenter != null) {
            sb.append(getCommandDrawSSECircleAtPosition(label, sseCenter, JmolTools.drawSSEcircleDiameter, JmolTools.getColorForSSE(s)));
        }
        
        return sb.toString();
    }
    
    /**
     * High-level command to visualize a contact between two SSEs.
     * @param s1 an SSE
     * @param s2 another SSE that has a 3D contact with the first one
     * @param spatRel the relative spatial orientation between s1 and s2
     * @return the Jmol command
     */
    public static String visualizeContactCommands(SSE s1, SSE s2, Integer spatRel) {
        StringBuilder sb = new StringBuilder();
        
        Position3D sseCenter1 = s1.getCentralAtomPosition();
        Position3D sseCenter2 = s2.getCentralAtomPosition();
        if(sseCenter1 != null && sseCenter2 != null) {
            String label = "" + s1.shortLabel() + SpatRel.getString(spatRel).toLowerCase() + s2.shortLabel();
            sb.append(getCommandDrawLine(label, sseCenter1, sseCenter2, JmolTools.getColorForSpatRel(spatRel)));
        }
        
        return sb.toString();
    }
    
    
    /**
     * Highest-level function to visualize a whole SSE graph.
     * @param g the input graph
     * @param hideOtherChains whether to hide the other chains
     * @param forceReload whether to force a reload of the PDB file before starting the coloring commands
     * @return the Jmol command string to visualize the graph.
     */
    public static String visualizeGraphCommands(SSEGraph g, boolean hideOtherChains, boolean forceReload) {
        StringBuilder sb = new StringBuilder();
        
        if(forceReload) {
            sb.append("load =").append(g.getPdbid().toUpperCase()).append("; ");
        }
        
        sb.append(JmolTools.getHideWatersCommand());
        
        // edges first (lines)
        for(Integer[] edge : g.getEdgeList()) {
            sb.append(JmolTools.visualizeContactCommands(g.getVertex(edge[0]), g.getVertex(edge[1]), g.getContactType(edge[0], edge[1])));
        }
        // now the vertices (SSE circles)
        for(int i = 0; i < g.numVertices(); i++) {
            sb.append(JmolTools.visualizeSSECommands(g.getVertex(i)));
        }
        
        if(hideOtherChains) {
            sb.append(limitOneChainTranslucentCommands(g.getChainid()));
        }
        
        return sb.toString();
    }
    
    /**
     * Returns the jmol commands to color a subset of the SSEs in the graph in blue.
     * @param g the graph (used for meta data like PDB ID only)
     * @param ssesToColor the list of SSEs to color in blue. (All their residues will be colored.)
     * @param hideOtherChains whether to hide the other chains
     * @param forceReload whether to force a reload of the PDB file before starting the coloring commands
     * @return the jmol command string
     */
    public static String visualizeGraphSubsetSSEsInBlue(SSEGraph g, List<SSE> ssesToColor, boolean hideOtherChains, boolean forceReload) {
        ArrayList<Residue> allResidues = new ArrayList<Residue>();
        for(SSE sse : ssesToColor) {
            for(Residue r : sse.getResidues()) {
                allResidues.add(r);
            }
        }
        return JmolTools.visualizeGraphSubsetResiduesInBlue(g, allResidues, hideOtherChains, forceReload);
    }
    
    
    /**
     * Returns the jmol commands to color a subset of the residues in the graph in blue.
     * @param g the graph (used for meta data like PDB ID only)
     * @param residuesToColor the list of residues to color in blue
     * @param hideOtherChains whether to hide the other chains
     * @param forceReload whether to force a reload of the PDB file before starting the coloring commands
     * @return the jmol command string
     */
    public static String visualizeGraphSubsetResiduesInBlue(SSEGraph g, ArrayList<Residue> residuesToColor, boolean hideOtherChains, boolean forceReload) {
        StringBuilder sb = new StringBuilder();
        
        if(forceReload) {
            sb.append("load =").append(g.getPdbid().toUpperCase()).append("; ");
        }
        
        sb.append(JmolTools.getHideWatersCommand());
        sb.append("cartoon only; ");
             
        // color all residues blue        
        sb.append(JmolTools.getSelectAllResiduesCommand(residuesToColor));                
        sb.append("color blue;");
        
        if(hideOtherChains) {
            sb.append("display :" + g.getChainid() + "; ");
        }
        
        return sb.toString();
    }
    
    
    /**
     * Determines the jmol command to select all residues in the list
     * @param res the residue list
     * @return the jmol command string
     */
    public static String getSelectAllResiduesCommand(ArrayList<Residue> res) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for(int i = 0; i < res.size(); i++) {
            sb.append(JmolTools.getSingleResidueIdentificationString(res.get(i)));
            if(i < res.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(";");
        
        return sb.toString();
    }
    
    
    /**
     * Determines the jmol identification string for the given residue.
     * @param r the residue
     * @return the jmol identification string, e.g., "23:a" for residue # 23 in chain A
     */ 
    public static String getSingleResidueIdentificationString(Residue r) {
        return r.getPdbResNum() + ":" + r.getChainID().toLowerCase();
    }
    
    
    /**
     * Function to limit visibility to one graph and show its atoms transparent. Deletes waters.
     * @param chain the PDB chain id
     * @return the Jmol command string 
     */
    public static String limitOneChainTranslucentCommands(String chain) {
        StringBuilder sb = new StringBuilder();
        sb.append("delete water; cartoon only; display :").append(chain).append("; color cartoon translucent orange; ");
        return sb.toString();
    }
    
    
    
    
    
    
}
