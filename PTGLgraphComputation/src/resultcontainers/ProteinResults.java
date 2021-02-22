/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package resultcontainers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores all results produced for a PDB file. This is used to generate the HTML pages.
 * This is a singleton.
 * @author ts
 */
public class ProteinResults {
    
    private HashMap<String, ProteinChainResults> chainResults;
    private String pdbid;
    private String pdbHeaderTitle;
    private HashMap<String, String> proteinMetadata;
    private ComplexGraphResult compGraphRes;

    public ComplexGraphResult getCompGraphRes() {
        return compGraphRes;
    }

    public void setCompGraphRes(ComplexGraphResult compGraphRes) {
        this.compGraphRes = compGraphRes;
    }

    public String getPdbid() {
        return pdbid;
    }

    public void setPdbid(String pdbid) {
        this.pdbid = pdbid;
    }

    public String getPdbHeaderTitle() {
        return pdbHeaderTitle;
    }

    public void setPdbHeaderTitle(String pdbHeaderTitle) {
        this.pdbHeaderTitle = pdbHeaderTitle;
    }
    
    
    private static ProteinResults instance = null;
    
    protected ProteinResults() {
        // prevent instantiation
    }
    
    public static ProteinResults getInstance() {
      if(instance == null) {
         instance = new ProteinResults();
         instance.init();
      }
      return instance;
   }
    
    private void init() {
        chainResults = new HashMap<String, ProteinChainResults>();
        proteinMetadata = new HashMap<String, String>();
        pdbid = "";
        pdbHeaderTitle = "";
        compGraphRes = null;
    }
    
    public boolean addProteinChainResults(ProteinChainResults res, String chainName) {
        if(chainResults.containsKey(chainName)) {
            return false;
        } else {
            chainResults.put(chainName, res);
            return true;
        }
    }
    
    public String getProteinMetaDataEntry(String key) {
        return this.proteinMetadata.get(key);
    }
    
    public HashMap<String, String> getProteinMetaData() {
        return this.proteinMetadata;
    }
    
    public void addProteinMetaData(String key, String value) {
        this.proteinMetadata.put(key, value);
    }
    
    @Deprecated
    public String getPdbMetaData(String key) {
        if(this.getAvailableChains().size() > 0) {
            ProteinChainResults pcr = this.getProteinChainResults(this.getAvailableChains().get(0));
            if(pcr != null) {
                if(pcr.getAvailableGraphs().size() > 0) {
                    pcr.getPdbMetaDataFromGraph(pcr.getAvailableGraphs().get(0), key);
                } else {
                    //System.err.println("PCR has no graphs.");
                }
            } else {
                //System.err.println("PCR for chain is NULL.");
            }
        } else {
            //System.err.println("PR has no chains.");
        }
        return null;
    }
    
    public ProteinChainResults getProteinChainResults(String chainName) {
        return this.chainResults.get(chainName);        
    }
    
    public List<String> getAvailableChains() {        
        List<String> chains = new ArrayList<String>();
        chains.addAll(this.chainResults.keySet());
        return chains;
    }
    
}
