/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.util.ArrayList;

/**
 * Stores all results produced for a PDB file. This is used to generate the HTML pages.
 * @author spirit
 */
public class ProteinResults {
    
    private ArrayList<ProteinChainResults> chainResults;
    
    private static ProteinResults instance = null;
    
    protected ProteinResults() {
      // Exists only to defeat instantiation.
    }
    
    public static ProteinResults getInstance() {
      if(instance == null) {
         instance = new ProteinResults();
      }
      return instance;
   }
    
}
