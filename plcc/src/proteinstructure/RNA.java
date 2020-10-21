/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteinstructure;
import tools.DP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import plcc.Main;
import plcc.SettingsOld;

/**
 *
 * @author katja
 */
public class RNA extends Molecule implements java.io.Serializable {
    public RNA() {} //Standardkonstruktor
    public RNA (Molecule Mol) {
        this.atoms = Mol.getAtoms();
        this.chain = Mol.getChain();
        this.chainID = Mol.getChainID();
        this.modelID = Mol.getModelID();
        this.iCode = Mol.getiCode();
        this.centerSphereRadius = Mol.getCenterSphereRadius();
        this.pdbNum = Mol.getPdbNum();
        this.dsspNum = Mol.getDsspNum();
        this.Name3 = Mol.getName3();
        this.AAName1 = Mol.getAAName1();
        this.sseString = Mol.getSSEString();
        this.sse = Mol.getSSE();
        this.sseStringDssp = Mol.getSSEStringDssp();
        this.isPartOfDsspSse = Mol.getDsspSseState();
    }

//    public List<BindingSite> partOfBindingSites;          // Methods involving BindingSites have not been implemented in this class yet due to problems with BindingSites-class.
    @Override public String toString() {
        return("[RNA] PDB# " + pdbNum + ", DSSP# " + dsspNum + ", Type " + type + ", AA1 " + AAName1 + ", AA3 " + Name3 + ", Chain " + chainID + ", Model " + modelID + ", # of Atoms " + atoms.size());
    }
}
