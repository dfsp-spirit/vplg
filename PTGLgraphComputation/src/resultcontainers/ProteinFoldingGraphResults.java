/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package resultcontainers;

import proteingraphs.FoldingGraph;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple container data structure for folding graph results.
 * @author ts
 */
public class ProteinFoldingGraphResults {
    
    private HashMap<Integer, FoldingGraph> foldingGraphs;
    
    public ProteinFoldingGraphResults() {
        foldingGraphs = new HashMap<Integer, FoldingGraph>();
    }
    
    public ProteinFoldingGraphResults(HashMap<Integer, FoldingGraph> foldingGraphs) {
        this.foldingGraphs = foldingGraphs;
    }

    public HashMap<Integer, FoldingGraph> getFoldingGraphs() {
        return foldingGraphs;
    }

    public void setFoldingGraphs(HashMap<Integer, FoldingGraph> foldingGraphs) {
        this.foldingGraphs = foldingGraphs;
    }
    
}
