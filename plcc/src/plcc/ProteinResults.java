/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

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
        pdbid = "";
        pdbHeaderTitle = "";
    }
    
    public boolean addProteinChainResults(ProteinChainResults res, String chainName) {
        if(chainResults.containsKey(chainName)) {
            return false;
        } else {
            chainResults.put(chainName, res);
            return true;
        }
    }
    
    public String getPdbMetaData(String key) {
        if(this.getAvailableChains().size() > 0) {
            ProteinChainResults pcr = this.getProteinChainResults(this.getAvailableChains().get(0));
            if(pcr != null) {
                if(pcr.getAvailableGraphs().size() > 0) {
                    pcr.getPdbMetaDataFromGraph(pcr.getAvailableGraphs().get(0), key);
                }
            }
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
