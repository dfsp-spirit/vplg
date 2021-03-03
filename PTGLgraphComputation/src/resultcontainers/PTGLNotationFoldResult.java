/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package resultcontainers;

import proteingraphs.FoldingGraph;
import java.util.List;

/**
 * Stores the results of the PTGL linear notation computation for a single fold (connected component) of a protein graph.
 * @author ts
 */
public class PTGLNotationFoldResult {
            
    public FoldingGraph fg;
    
    public String adjNotation;
    public String redNotation;
    public String keyNotation;
    public String seqNotation;
    
    public Integer adjStart;
    public Integer redStart;
    public Integer keyStartFG;
    public Integer seqStart;
    
    public Integer adjSize;
    public Integer redSize;
    public Integer keySize;
    public Integer seqSize;
    
    public List<Integer> adjpos;
    public List<Integer> redpos;
    public List<Integer> keypos;
    public List<Integer> seqpos;
    
    /**
     * Constructor. Does nothing but setting a name.
     * @param fg the folding graph
     */
    public PTGLNotationFoldResult(FoldingGraph fg) {
                
        this.fg = fg;
        
        this.adjNotation = null;
        this.redNotation = null;
        this.keyNotation = null;
        this.seqNotation = null;
        
        this.adjStart = -1;
        this.redStart = -1;
        this.keyStartFG = -1;
        this.seqStart = -1;
        
        this.adjSize = 0;
        this.redSize = 0;
        this.keySize = 0;
        this.seqSize = 0; 
        
        this.adjpos = null;
        this.redpos = null;
        this.seqpos = null;
        this.keypos = null;
    }
    
    
    /**
     * Returns the fold name. This is only unique if there are not more than foldnames.size() folds in the protein graph.
     * @return the fold name
     */
    public String getFoldName() {
        return fg.getFoldingGraphFoldName();
    }
    
    
    /**
     * Returns the fold number. This should be unique within a single protein graph.
     * @return the fold number
     */
    public Integer getFoldNumber() {
        return fg.getFoldingGraphNumber();
    }
    
    public FoldingGraph getFoldingGraph() {
        return this.fg;
    }
}
