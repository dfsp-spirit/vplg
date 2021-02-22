/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteingraphs;

import proteinstructure.SSE;
import java.util.*;

/** 
 * Simple comparator which allows comparison of SSEs by their sequential position in the AA sequence
 * of their chain.
 * 
 * @author ts
 */
public class SSEComparator implements Comparator<SSE> {

    @Override public int compare(SSE o1, SSE o2) {
        return o1.getSSESeqChainNum().compareTo(o2.getSSESeqChainNum());
    }
}

