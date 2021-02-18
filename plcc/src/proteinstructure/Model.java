/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteinstructure;

// imports
import java.util.ArrayList;

/**
 * Represents a model from a PDB file. Models usually only occur in NMR-based PDB files and should not occur 
 * in the files parsed by plcc, since splitpdb removes them. A default model is still used in plcc, the idea
 * is to make support for other SSE assignment algorithms besides DSSP easier to implement in the future.
 * We also need to check the data for multiple models (the user may have supplied a wrong input file), so this 
 * is required.
 * 
 * @author ts
 */
public class Model implements java.io.Serializable {

    // declare class vars
    private String modelID = null;
    private ArrayList<Chain> chains = null;

    // constructor
    public Model() { 
        modelID = "1"; 
        this.chains = new ArrayList<>();
    }
    public Model(String mID) { 
        modelID = mID;
        this.chains = new ArrayList<>();
    }

    // getters
    public String getModelID() { return(modelID); }
    public ArrayList<Chain> getChains() { return(chains); }

    // setters
    public void setModelID(String mi) { modelID = mi; }
    public void addChain(Chain c) { chains.add(c); }

    @Override public String toString() { return(modelID); }

}
