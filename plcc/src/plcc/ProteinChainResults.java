/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author spirit
 */
public class ProteinChainResults {
    
    public static final String GRAPHTYPE_ALPHA = "alpha";
    public static final String GRAPHTYPE_BETA = "beta";
    public static final String GRAPHTYPE_ALBE = "albe";
    public static final String GRAPHTYPE_ALPHALIG = "alphalig";
    public static final String GRAPHTYPE_BETALIG = "betalig";
    public static final String GRAPHTYPE_ALBELIG = "albelig";
    
    protected String chainName;
    protected HashMap<String, SSEGraph> proteinGraphs;
    protected HashMap<String, HashMap<String, File>> proteinGraphFilesInFormat;
    
    public ProteinChainResults(String chainName) {
        this.chainName = chainName;
        proteinGraphs = new HashMap<String, SSEGraph>();
    }
    
    public void addProteinGraph(SSEGraph g, String graphType) {
        this.proteinGraphs.put(graphType, g);
    }
    
    public void addProteinGraphOutputFile(String graphType, String format, File outFile) {
        
    }
    
    public SSEGraph getProteinGraph(String graphType) {
        return this.proteinGraphs.get(graphType);
    }
    
}
