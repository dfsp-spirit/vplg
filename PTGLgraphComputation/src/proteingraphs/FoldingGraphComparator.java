/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteingraphs;

import java.util.Comparator;

/**
 * A comparator to order folding graphs in their ordering, to determine the fg_number. This order is defined by the position of the vertices in the parent folding graph. 
 * The fg_number is the folding graph identifier number, a number starting with 0 and up to number of FGs of this graph -1. The order of the connected components is determined by ordering according to their left-most vertex in the parent. So if the parent graph consists of 4 vertices, which have indices 0, 1, 2, and 3. And it has a CC1={0,3} and a second CC2={1,2}. Then CC1 gets fg_number=0 because its left-most vertex is 0. The left-most vertex of CC2 is 1.
 * @author ts
 */
public class FoldingGraphComparator implements Comparator<FoldingGraph> {

    @Override public int compare(FoldingGraph fg1, FoldingGraph fg2) {
        return fg1.getMinimalVertexIndexInParentGraph().compareTo(fg2.getMinimalVertexIndexInParentGraph());
    }
}
