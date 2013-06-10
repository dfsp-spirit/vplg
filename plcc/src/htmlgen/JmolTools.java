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
    public static String getCommandDrawLine(String refName, Position3D start, Position3D end, Integer spatRel, String color) {
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
    public static String getCommandDrawSSECircleAroundAtom(String refName, Integer atomNum, String color) {
        StringBuilder sb = new StringBuilder();
        sb.append("draw ").append(refName).append(" diameter ").append(drawSSEcircleDiameter).append(" color ").append(color).append(" circle {atomno=").append(atomNum).append("};");
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
    public static String getCommandDrawSSECircleAtPosition(String refName, Position3D position, String color) {
        StringBuilder sb = new StringBuilder();
        sb.append("draw ").append(refName).append(" color ").append(color).append(" diameter ").append(drawSSEcircleDiameter).append(" circle ").append(JmolTools.posString(position)).append(";");
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
    
    
    
    
    
    
}
