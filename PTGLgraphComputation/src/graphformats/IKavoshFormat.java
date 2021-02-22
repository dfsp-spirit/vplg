/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package graphformats;

/**
 *
 * @author ts
 */
public interface IKavoshFormat {
    
    /**
     * Returns a string representation of this object in Kavosh format. This line-based format is very simple: 
     * the first line in the file contains the total number of SSEs(=vertices), the other lines contain one edge
     * per line. An edge is encoded by the two vertices it connects, e.g., "1 3" means the that vertex 1 and 3 are connected.
     * See http://lbb.ut.ac.ir/ for more information on Kavosh, or Kashani et al. 2009, Kavosh: a new algorithm for finding network motifs.
     *
     * @return the Kavosh format string
     */ 
    public String toKavoshFormat();
    
}
