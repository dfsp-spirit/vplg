/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2016. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package proteinstructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a binding site in a certain protein structure. Binding site information is parsed from the SITE entries of a PDB file.
 * @author spirit
 */
public class BindingSite {
    
    public BindingSite() {
        this.siteResidues = new ArrayList<>();
        this.ligandResidues = new ArrayList<>();
        this.siteName = "";
        this.modelID = null;
        this.numResDeclared = null;
    }
    
    public BindingSite(String siteName) {
        this.siteResidues = new ArrayList<>();
        this.ligandResidues = new ArrayList<>();
        this.siteName = siteName;
        this.modelID = null;
        this.numResDeclared = null;
    }
    
    protected List<Residue> siteResidues;
    protected List<Residue> ligandResidues;
    protected String modelID;
    /** The number of residues declared in the PDB SITE line for this site. */
    protected Integer numResDeclared;
    
    /** The name assigned in the SITE line of the PDB file. */
    protected String siteName;

    public List<Residue> getSiteResidues() {
        return siteResidues;
    }
    
    public void addSiteResidue(Residue r) {
        this.siteResidues.add(r);
    }
    
    public void setNumResDeclared(Integer i) {
        this.numResDeclared = i;
    }
    
    public void addLigandResidue(Residue r) {
        this.ligandResidues.add(r);
    }

    public void setSiteResidues(List<Residue> siteResidues) {
        this.siteResidues = siteResidues;
    }

    public List<Residue> getLigandResidues() {
        return ligandResidues;
    }

    public void setLigandResidues(List<Residue> ligandResidues) {
        this.ligandResidues = ligandResidues;
    }
    
    public void setModelID(String modelID) {
        this.modelID = modelID;
    }
    
    public Integer getNumResiduesDeclared() {
        return this.numResDeclared;
    }
}
