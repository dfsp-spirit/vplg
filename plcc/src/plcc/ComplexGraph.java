/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Marcus Kessler 2013. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author Marcus Kessler
 */
package plcc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.spargel.datastructures.UAdjListGraph;
import net.sourceforge.spargel.writers.GMLWriter;

/**
 *
 * @author marcus
 */
public class ComplexGraph extends UAdjListGraph {
    
    public Map<Edge, Integer> numHHInteractionsMap;
    public Map<Edge, Integer> numHSInteractionsMap;
    public Map<Edge, Integer> numHLInteractionsMap;
    public Map<Edge, Integer> numSSInteractionsMap;
    public Map<Edge, Integer> numSLInteractionsMap;
    public Map<Edge, Integer> numLLInteractionsMap;
    public Map<Edge, Integer> numAllInteractionsMap;
    public Map<Edge, Integer> numDisulfidesMap;
    public Map<Vertice, String> proteinNodeMap;
    private String pdbid;
    
    
    /**
     * Constructor.
     */
    ComplexGraph(String pdbid) {
        this.pdbid = pdbid;
        numHHInteractionsMap = createEdgeMap();
        numHSInteractionsMap = createEdgeMap();
        numHLInteractionsMap = createEdgeMap();
        numSSInteractionsMap = createEdgeMap();
        numSLInteractionsMap = createEdgeMap();
        numLLInteractionsMap = createEdgeMap();
        numAllInteractionsMap = createEdgeMap();
        numDisulfidesMap = createEdgeMap();
        proteinNodeMap = createVerticeMap();
    }
    
    public Vertice getVerticeFromChain(String chainID){
        for (int i = 0; i < ComplexGraph.this.getNumVertices(); i++){
            if (ComplexGraph.this.proteinNodeMap.get(ComplexGraph.this.getVertice(i)).equals(chainID)){
                return ComplexGraph.this.getVertice(i);
            }
        }
        return null;
    }
    
    public String getPDBID(){
        return this.pdbid;
    }
    
    /**
     * Writes this complex graph to the file 'file' in GML format. Note that this function
     * will overwrite the file if it exists.
     * @param file the target file. Has to be writable.
     * @return true if the file was written, false otherwise
     */
    public boolean writeToFileGML(File file) {
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.err.println("ERROR: Could not create file '" + file.getAbsolutePath() + "': " + ex.getMessage() + ".");
                return false;
            }
        }
        
        GMLWriter gw = new GMLWriter(this);
        gw.joinVerticeMap("chain", proteinNodeMap);
        gw.joinEdgeMap("num_total_contacts", numAllInteractionsMap);
        FileOutputStream fop = null;
        boolean allOK = true;        
        try {
            fop = new FileOutputStream(file);
            gw.write(fop);    
            fop.flush();
            fop.close();
        } catch(Exception e) {
            System.err.println("ERROR: Could not write complex graph to file '" + file.getAbsolutePath() + "': " + e.getMessage() + ".");
            allOK = false;
        } finally {
            if(fop != null)  {
                try {
                    fop.close();
                } catch(Exception e) {
                    // nvm
                }
            }
            
        }
        return allOK;
    }
}
