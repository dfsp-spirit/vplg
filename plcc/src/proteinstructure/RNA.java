/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteinstructure;

import java.util.ArrayList;

/**
 *
 * @author katja
 */
public class RNA extends Molecule implements java.io.Serializable {
    private ArrayList<Atom> atoms = null;                         // a list of all (non-H) Atoms of the Residue
    private ArrayList<Atom> hydrogenatoms = null;                         // a list of all hydrogen Atoms of the Residue
    private Integer pdbResNum = null;                       // pdb residue number
    private Integer dsspResNum = null; 
    private String rnaName= null;
    
    public RNA() { this.atoms = new ArrayList<>(); this.hydrogenatoms = new ArrayList<>(); }
    
    public RNA(Integer residueNumberPDB, Integer residueNumberDSSP) {
        atoms = new ArrayList<>();
        hydrogenatoms = new ArrayList<>();
        pdbResNum = residueNumberPDB;
        dsspResNum = residueNumberDSSP;
    }
}
