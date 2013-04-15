/*
 * This file is part of SPARGEL, the sparse graph extension library.
 * 
 * This is free software, published under the WTFPL.
 * 
 * Written by Tim Schaefer, see http://rcmd.org/contact/.
 * 
 */
package jgraph;

import java.util.BitSet;
import plcc.SpatRel;

/**
 * Ab edge for a protein ligand graph. This edge in undirected, and it holds information on the 
 * relative spatial orientation of the two SSEs it connects (e.g., parallel) and the type of the 
 * connection between them (e.g., disulfide bridge).
 * 
 * @author ts
 */
public class PLGEdge extends org.jgrapht.graph.DefaultEdge {
    
    protected int spatialOrientation;
    protected BitSet bondProperties;
    
    public static final Integer BONDTYPE_VDW = 0;
    public static final Integer BONDTYPE_DISULFIDE = 1;
    public static final Integer BONDTYPE_INTERCHAIN = 2;
    
    public PLGEdge() {
        super();
        bondProperties = new BitSet(3);
        spatialOrientation = SpatRel.MIXED;
    }

    public int getSpatialOrientation() {
        return spatialOrientation;
    }

    public void setSpatialOrientation(int spatialOrientation) {
        this.spatialOrientation = spatialOrientation;
    }

    public BitSet getBondProperties() {
        return bondProperties;
    }

    public void setBondProperties(BitSet bondProperties) {
        this.bondProperties = bondProperties;
    }
    
    
    @Override
    public String toString() {
      return "s=" + spatialOrientation;
    }
    
}
