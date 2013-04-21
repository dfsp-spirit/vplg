/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package jgrapht;

import java.util.Comparator;

/**
 * Compares two SSEs via their sequential position in the chain. Only makes sense if they are part of the same chain, so assure
 * that this is the case.
 * @author ts
 */
public class VertexSSEComparator implements Comparator<VertexSSE> {
    
    @Override public int compare(VertexSSE o1, VertexSSE o2) {
        return o1.getSequentialSSENumberInChain().compareTo(o2.getSequentialSSENumberInChain());
    }
}
    
