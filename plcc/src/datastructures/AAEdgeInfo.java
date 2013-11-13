/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

import plcc.ResContactInfo;

/**
 * Edge information for an amino acid (AA) graph.
 * @author ts
 */
public class AAEdgeInfo {
    
    /** Information about the contacts between this residue pair. */
    protected ResContactInfo rci;
    
    
    public AAEdgeInfo() {
        this.rci = null;
    }
    
    public AAEdgeInfo(ResContactInfo rci) {
        this.rci = rci;
    }
    
    
    
    // simple getters and setters follow
    public ResContactInfo getRci() {
        return rci;
    }

    public void setRci(ResContactInfo rci) {
        this.rci = rci;
    }
    
}
