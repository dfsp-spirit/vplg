/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package htmlgen;

import plcc.Position3D;

/**
 * Some helper functions which generate the Jmol commands to visualize stuff.
 * @author ts
 */
public class JmolTools {
    
    /**
     * Produces the Jmol command to draw a line from start to end, with a reference name of 'refName'.
     * @param refName the name to assign to the drawn object
     * @param start the 3D start position of the line
     * @param end the 3D end position of the line
     * @return the Jmol command to draw the line
     */
    public static String getCommandDrawLine(String refName, Position3D start, Position3D end) {
        StringBuilder sb = new StringBuilder();
        sb.append("draw ").append(refName).append(" {").append(start.x).append(" ").append(start.y).append(" ").append(start.z).append("} {").append(end.x).append(" ").append(end.y).append(" ").append(end.z).append("};");
        return sb.toString();        
    }
    
}
