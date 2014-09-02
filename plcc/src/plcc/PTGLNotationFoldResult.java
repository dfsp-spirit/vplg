/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

import java.util.List;

/**
 * Stores the results of the PTGL linear notation computation for a single fold (connected component) of a protein graph.
 * @author ts
 */
public class PTGLNotationFoldResult {
    
    private String foldName;
    private Integer foldNumber;
    
    public String graphType;
    public List<Integer> verticesInParent;
    
    public String adjNotation;
    public String redNotation;
    public String keyNotation;
    public String seqNotation;
    
    public Integer adjStart;
    public Integer redStart;
    public Integer keyStart;
    public Integer seqStart;
    
    public Integer adjSize;
    public Integer redSize;
    public Integer keySize;
    public Integer seqSize;
    
    /**
     * Constructor. Does nothing but setting a name.
     * @param foldNumber the sequential fold number (CC number)
     * @param foldName the PTGL fold name. This is "A" for the first connected component of a PG, then "B", and so on.
     */
    public PTGLNotationFoldResult(Integer foldNumber, String foldName) {
        this.foldNumber = foldNumber;
        this.foldName = foldName;
        
        this.adjNotation = null;
        this.redNotation = null;
        this.keyNotation = null;
        this.seqNotation = null;
        
        this.adjStart = -1;
        this.redStart = -1;
        this.keyStart = -1;
        this.seqStart = -1;
        
        this.adjSize = 0;
        this.redSize = 0;
        this.keySize = 0;
        this.seqSize = 0;    
        
        this.graphType = null;
        this.verticesInParent = null;
    }
    
    
    /**
     * Returns the fold name. This is only unique if there are not more than foldnames.size() folds in the protein graph.
     * @return the fold name
     */
    public String getFoldName() {
        return foldName;
    }
    
    
    /**
     * Returns the fold number. This should be unique within a single protein graph.
     * @return the fold number
     */
    public Integer getFoldNumber() {
        return foldNumber;
    }
}
