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
public class Chain implements java.io.Serializable {

    // declare class vars
    private String pdbChainID = null;           // chain ID from PDB file
    private String dsspChainID = null;          // chain ID from DSSP file
    private ArrayList<Residue> residues = null;          // a list of all Residues of the Chain
    private String modelID = null;
    private Model model = null;                 // the Model of this Chain

    // constructor
    public Chain(String ci) { pdbChainID = ci; residues = new ArrayList<Residue>(); }


    // getters
    public String getPdbChainID() { return(pdbChainID); }
    public String getDsspChainID() { return(dsspChainID); }
    public String getModelID() { return(modelID); }
    public Model getModel() { return(model); }
    public ArrayList<Residue> getResidues() { return(residues); }

    // setters
    public void addResidue(Residue r) { residues.add(r); }
    public void setPdbChainID(String s) { pdbChainID = s; }
    public void setDsspChainID(String s) { dsspChainID = s; }
    public void setModelID(String s) { modelID = s; }
    public void setModel(Model m) { model = m; }


}
