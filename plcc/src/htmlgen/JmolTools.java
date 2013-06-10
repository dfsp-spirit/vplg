/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package htmlgen;

import plcc.Position3D;
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
        sb.append("draw ").append(refName).append(" color ").append(color).append(" ").append(JmolTools.posString(start)).append(" ").append(JmolTools.posString(end));
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
        sb.append("draw ").append(refName).append(" diameter ").append(diameter).append(" color ").append(color).append(" circle {atomno=").append(atomNum).append("};");
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
        sb.append("draw ").append(refName).append(" color ").append(color).append(" diameter ").append(diameter).append(" circle ").append(JmolTools.posString(position)).append(";");
        return sb.toString();        
    }
    
    /**
     * Returns the Jmol color string for a certain SSE type.
     * @param sse the SSE
     * @return the Jmol color string for this SSE type, e.g., "red"
     */
    public static String getColorForSSE(SSE sse) {
        if(sse.isHelix()) {
            return "red";
        }
        else if(sse.isBetaStrand()) {
            return "black";
        }
        else if(sse.isLigandSSE()) {
            return "purple";
        }
        else {
            return "gray";
        }
    }
    
    
    /**
     * Returns the Jmol color string for a certain spatial orientation type.
     * @param spatRel the spatial orientation (between two SSEs), e.g., SpatRel.PARALLEL
     * @return the Jmol color string, e.g., "red"
     */
    public static String getColorForSpatRel(Integer spatRel) {
        if(spatRel.equals(SpatRel.ANTIPARALLEL)) {
            return "blue";
        }
        else if(spatRel.equals(SpatRel.PARALLEL)) {
            return "red";
        }
        else if(spatRel.equals(SpatRel.LIGAND) ){
            return "purple";
        }
        else if(spatRel.equals(SpatRel.MIXED) ){
            return "green";
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
        
        Position3D sseCenter = s.getCentralAtomPosition();
        if(sseCenter != null) {
            sb.append(getCommandDrawSSECircleAtPosition(s.shortLabel(), sseCenter, JmolTools.drawSSEcircleDiameter, JmolTools.getColorForSSE(s)));
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
            String label = "" + s1.shortLabel() + "=" + s2.shortLabel() + "";
            sb.append(getCommandDrawLine(label, sseCenter1, sseCenter2, JmolTools.getColorForSpatRel(spatRel)));
        }
        
        return sb.toString();
    }
    
    
    /**
     * Highest-level function to visualize a whole SSE graph.
     * @param g the input graph
     * @return the Jmol command string to visualize the graph.
     */
    public static String visualizeGraphCommands(SSEGraph g) {
        StringBuilder sb = new StringBuilder();
        // edges first (lines)
        for(Integer[] edge : g.getEdgeList()) {
            sb.append(JmolTools.visualizeContactCommands(g.getVertex(edge[0]), g.getVertex(edge[1]), g.getContactType(edge[0], edge[1])));
        }
        // now the vertices (SSE circles)
        for(int i = 0; i < g.numVertices(); i++) {
            sb.append(JmolTools.visualizeSSECommands(g.getVertex(i)));
        }
        return sb.toString();
    }
    
    
    
    
    
    
}
