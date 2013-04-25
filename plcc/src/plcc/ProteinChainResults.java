/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A data structure to store the PLCC results for a chain, i.e., all the graphs and their output files.
 * @author ts
 */
public class ProteinChainResults {
    
    public static final String GRAPHTYPE_ALPHA = "alpha";
    public static final String GRAPHTYPE_BETA = "beta";
    public static final String GRAPHTYPE_ALBE = "albe";
    public static final String GRAPHTYPE_ALPHALIG = "alphalig";
    public static final String GRAPHTYPE_BETALIG = "betalig";
    public static final String GRAPHTYPE_ALBELIG = "albelig";
    
    private String[] getGraphTypesList() {
        return new String[] { GRAPHTYPE_ALPHA, GRAPHTYPE_BETA, GRAPHTYPE_ALBE, GRAPHTYPE_ALPHALIG, GRAPHTYPE_BETALIG, GRAPHTYPE_ALBELIG };
    }
    
    public static final String GRAPHFORMAT_GML = "gml";
    public static final String GRAPHFORMAT_KAVOSH = "kavosh";
    public static final String GRAPHFORMAT_EDGELIST = "edgelist";
    public static final String GRAPHFORMAT_VPLG = "vplg";
    public static final String GRAPHFORMAT_DOTLANGUAGE = "dotlanguage";
    public static final String GRAPHFORMAT_TGF = "tgf";
    
    private String[] getGraphFormatsList() {
        return new String[] { GRAPHFORMAT_GML, GRAPHFORMAT_KAVOSH, GRAPHFORMAT_EDGELIST, GRAPHFORMAT_VPLG, GRAPHFORMAT_DOTLANGUAGE, GRAPHFORMAT_TGF };
    }
    
    protected String chainName;
    protected HashMap<String, SSEGraph> proteinGraphs;
    protected HashMap<String, File> proteinGraphFilesByFormat;
    protected ProtMetaInfo chainMetaData;

    public ProtMetaInfo getChainMetaData() {
        return chainMetaData;
    }

    public void setChainMetaData(ProtMetaInfo chainMetaData) {
        this.chainMetaData = chainMetaData;
    }
    
    public ProteinChainResults(String chainName) {
        this.chainName = chainName;
        proteinGraphs = new HashMap<String, SSEGraph>();
        proteinGraphFilesByFormat = new HashMap<String, File>();
        chainMetaData = null;
    }
    
    public void addProteinGraph(SSEGraph g, String graphType) {
        this.proteinGraphs.put(graphType, g);
    }
    
    private String getHashMapKeyForFile(String graphType, String format) {
        return "" + graphType + "_" + format;
    }
    
    public void addProteinGraphOutputFile(String graphType, String format, File outFile) {
        proteinGraphFilesByFormat.put(getHashMapKeyForFile(graphType, format), outFile);
    }
    
    public File getProteinGraphOutputFile(String graphType, String format) {
        return this.proteinGraphFilesByFormat.get(getHashMapKeyForFile(graphType, format));
    }
    
    public SSEGraph getProteinGraph(String graphType) {
        return this.proteinGraphs.get(graphType);
    }
    
    public ArrayList<String> checkForOutputFormatsWithValidFiles(String graphType) {
        ArrayList<String> formatsWithValidFiles = new ArrayList<String>();
        
        File file;
        for(String format : this.getGraphFormatsList()) {
            file = getProteinGraphOutputFile(graphType, format);
            if(IO.fileExistsIsFileAndCanRead(file)) {
                formatsWithValidFiles.add(format);
            }
        }        
        return formatsWithValidFiles;
    }
    
    public ArrayList<String> checkForGraphTypesWithValidGraphs() {
        ArrayList<String> graphTypesValid = new ArrayList<String>();
        
        SSEGraph g;
        for(String gt : this.getGraphTypesList()) {
            g = this.getProteinGraph(gt);
            if(g != null) {
               graphTypesValid.add(gt); 
            }
        }
        return graphTypesValid;
    }
    
}
