/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

import java.util.*;

/** 
 * Simple comparator which allows comparison of the SSEs by their sequential position in the AA sequence of their chain.
 * 
 * @author ts
 */
public class SSEComparator implements Comparator<SSE> {

    @Override public int compare(SSE o1, SSE o2) {
        return o1.getSSESeqChainNum().compareTo(o2.getSSESeqChainNum());
    }
}

