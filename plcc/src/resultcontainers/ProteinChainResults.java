/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package resultcontainers;

import io.IO;
import proteinstructure.ProtMetaInfo;
import proteingraphs.SSEGraph;
import graphdrawing.DrawTools;
import graphformats.GraphFormats;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import graphdrawing.DrawTools.IMAGEFORMAT;

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
    
    public static final String GRAPHFORMAT_GML = GraphFormats.GRAPHFORMAT_GML;
    public static final String GRAPHFORMAT_KAVOSH = GraphFormats.GRAPHFORMAT_KAVOSH;
    public static final String GRAPHFORMAT_EDGELIST = GraphFormats.GRAPHFORMAT_EDGELIST;
    public static final String GRAPHFORMAT_VPLG = GraphFormats.GRAPHFORMAT_VPLG;
    public static final String GRAPHFORMAT_DOTLANGUAGE = GraphFormats.GRAPHFORMAT_DOTLANGUAGE;
    public static final String GRAPHFORMAT_TGF = GraphFormats.GRAPHFORMAT_TGF;
    
    private String[] getGraphFormatsList() {
        return new String[] { GRAPHFORMAT_GML, GRAPHFORMAT_KAVOSH, GRAPHFORMAT_EDGELIST, GRAPHFORMAT_VPLG, GRAPHFORMAT_DOTLANGUAGE, GRAPHFORMAT_TGF };
    }
    
    protected String chainName;
    protected HashMap<String, SSEGraph> proteinGraphs;
    protected HashMap<String, File> proteinGraphImagesBitmap;
    protected HashMap<String, File> proteinGraphImagesVector;
    protected HashMap<String, File> proteinGraphFilesByFormat;
    protected HashMap<String, File> proteinGraphImagesByFormat;
    protected HashMap<String, File> proteinGraphVisJmolCommandFiles;
    protected HashMap<String, File> proteinGraphVisJmolResBlueCommandFiles;
    protected HashMap<String, ProteinFoldingGraphResults> foldingGraphResults;
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
        proteinGraphImagesByFormat = new HashMap<String, File>();
        proteinGraphImagesBitmap = new HashMap<String, File>();
        proteinGraphImagesVector = new HashMap<String, File>();
        proteinGraphVisJmolCommandFiles = new HashMap<String, File>();
        proteinGraphVisJmolResBlueCommandFiles = new HashMap<String, File>();
        chainMetaData = null;
        foldingGraphResults = new HashMap<String, ProteinFoldingGraphResults>();
    }
    
    public void addProteinFoldingGraphResults(String graphType, ProteinFoldingGraphResults res) {
        this.foldingGraphResults.put(graphType, res);
    }
    
    public ProteinFoldingGraphResults getProteinFoldingGraphResults(String graphType) {
        return this.foldingGraphResults.get(graphType);
    }
    
    public void addProteinGraph(SSEGraph g, String graphType) {
        this.proteinGraphs.put(graphType, g);        
    }
    
    private String getHashMapKeyForFile(String graphType, String format) {
        return "" + graphType + "_" + format;
    }
    
    private String getImageFormatHashMapKeyForFile(String graphType, IMAGEFORMAT format) {
        return "" + graphType + "_" + format.toString();
    }
    
    public String getPdbMetaDataFromGraph(String graphType, String metaDataKey) {
        SSEGraph g = this.getProteinGraph(graphType);
        if(g != null) {
            //System.err.println("YO");
            return g.getMetadata().get(metaDataKey);            
        } else {
            //System.err.println("Graph " + graphType + " is null.");
            return null;
        }
    }
    
    public void addProteinGraphOutputFile(String graphType, String format, File outFile) {
        proteinGraphFilesByFormat.put(getHashMapKeyForFile(graphType, format), outFile);
    }
    
    public File getProteinGraphOutputFile(String graphType, String format) {
        return this.proteinGraphFilesByFormat.get(getHashMapKeyForFile(graphType, format));
    }
    
    public void addProteinGraphOutputImage(String graphType, String format, File outFile) {
        proteinGraphImagesByFormat.put(getHashMapKeyForFile(graphType, format), outFile);
    }
    
    public File getProteinGraphOutputImage(String graphType, String format) {
        return this.proteinGraphImagesByFormat.get(getHashMapKeyForFile(graphType, format));
    }
    
    @Deprecated
    public void addProteinGraphImageBitmap(String graphType, File outFile) {
        proteinGraphImagesBitmap.put(graphType, outFile);
    }
    
    public void addProteinGraphVisJmolCommandFile(String graphType, File outFile) {
        proteinGraphVisJmolCommandFiles.put(graphType, outFile);
    }
    
    public void addProteinGraphVisResBlueJmolCommandFile(String graphType, File outFile) {
        proteinGraphVisJmolResBlueCommandFiles.put(graphType, outFile);
    }
    
    @Deprecated
    public File getProteinGraphImageBitmap(String graphType) {
        //return this.proteinGraphImagesBitmap.get(graphType);
        return this.getProteinGraphOutputImage(graphType, DrawTools.DEFAULT_FORMAT_BITMAP);
    }
    
    @Deprecated
    public void addProteinGraphImageVector(String graphType, File outFile) {
        proteinGraphImagesVector.put(graphType, outFile);
    }
    
    public File getProteinGraphVisJmolCommandFile(String graphType) {
        return this.proteinGraphVisJmolCommandFiles.get(graphType);
    }
    
    public File getProteinGraphVisJmolResBlueCommandFile(String graphType) {
        return this.proteinGraphVisJmolResBlueCommandFiles.get(graphType);
    }
    
    @Deprecated
    public File getProteinGraphImageVector(String graphType) {
        //return this.proteinGraphImagesVector.get(graphType);
        return this.getProteinGraphOutputImage(graphType, DrawTools.DEFAULT_FORMAT_VECTOR);
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
    
    
    public boolean validJmolCommandFileExistsFor(String graphType) {        
        File jmolCmdFile = this.proteinGraphVisJmolCommandFiles.get(graphType);
        if(jmolCmdFile != null) {
            if(IO.fileExistsIsFileAndCanRead(jmolCmdFile)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean validJmolCommandFileResBlueExistsFor(String graphType) {        
        File jmolCmdFile = this.proteinGraphVisJmolResBlueCommandFiles.get(graphType);
        if(jmolCmdFile != null) {
            if(IO.fileExistsIsFileAndCanRead(jmolCmdFile)) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<String> checkForGraphTypesWithValidJmolCmdFiles() {
        ArrayList<String> graphTypesValid = new ArrayList<String>();
                
        for(String gt : this.getGraphTypesList()) {
            if(this.validJmolCommandFileExistsFor(gt)) {
               graphTypesValid.add(gt); 
            }
        }
        return graphTypesValid;
    }
    
    public ArrayList<String> checkForGraphTypesWithValidJmolResBlueCmdFiles() {
        ArrayList<String> graphTypesValid = new ArrayList<String>();
                
        for(String gt : this.getGraphTypesList()) {
            if(this.validJmolCommandFileResBlueExistsFor(gt)) {
               graphTypesValid.add(gt); 
            }
        }
        return graphTypesValid;
    }
    
    public List<String> getAvailableGraphs() {        
        List<String> graphs = new ArrayList<String>();
        graphs.addAll(this.proteinGraphs.keySet());
        return graphs;
    }
    
    @Deprecated
    public List<File> getAvailableGraphImagesVectorOrBitmap(String graphType) {        
        List<File> graphImages = new ArrayList<File>();
        if(IO.fileExistsIsFileAndCanRead(this.getProteinGraphImageBitmap(graphType))) {
            graphImages.add(this.getProteinGraphImageBitmap(graphType));
        }
        if(IO.fileExistsIsFileAndCanRead(this.getProteinGraphImageVector(graphType))) {
            graphImages.add(this.getProteinGraphImageVector(graphType));
        }
        return graphImages;
    }
    
    
    public List<File> getAvailableGraphImages(String graphType) {        
        List<File> graphImages = new ArrayList<File>();
                
        SSEGraph g;
        for(String format : DrawTools.ALL_IMAGE_FORMATS) {
            File img = this.getProteinGraphOutputImage(graphType, format);
            if(IO.fileExistsIsFileAndCanRead(img)) {
                graphImages.add(img);                        
            }            
        }
        
        return graphImages;
    }
}
