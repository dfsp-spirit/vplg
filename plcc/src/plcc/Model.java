/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

// imports
import java.util.ArrayList;

/**
 *
 * @author ts
 */
public class Model implements java.io.Serializable {

    // declare class vars
    private String modelID = null;
    private ArrayList<Chain> chains = null;

    // constructor
    public Model() { modelID = "1"; }
    public Model(String mID) { modelID = mID; }

    // getters
    public String getModelID() { return(modelID); }
    public ArrayList<Chain> getChains() { return(chains); }

    // setters
    public void setModelID(String mi) { modelID = mi; }
    public void addChain(Chain c) { chains.add(c); }

    @Override public String toString() { return(modelID); }

}
