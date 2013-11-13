/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

import java.util.ArrayList;
import plcc.AminoAcid;

/**
 * An undirected, adjacency list based amino acid graph. Suitable for large, sparse graphs.
 * @author ts
 */
public class AAGraph extends SparseGraph<AminoAcid, AAEdgeInfo> {
    
    public AAGraph(ArrayList<AminoAcid> vertices) {
        super(vertices);
    }
    
    public AAGraph() {
        super();
    }
}
