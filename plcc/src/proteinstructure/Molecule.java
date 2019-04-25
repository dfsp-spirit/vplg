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
 * @author kk
 */
public class Molecule {
    
    public static final Integer RESIDUE_TYPE_AA = 0;
    public static final Integer RESIDUE_TYPE_LIGAND = 1;
    public static final Integer RESIDUE_TYPE_OTHER = 2;
    public static final Integer RESIDUE_TYPE_RNA = 3;
    
    // declare class vars
    private String resName3 = null;                          // 3 letter name
    private String AAName1 = null;                          // 1 letter name, AAs only
    private String RNAName1=null;
    private Integer type = null;                            // residue type: 0=AA, 1=Ligand, 2=Other
    private ArrayList<Atom> atoms = null;                         // a list of all (non-H) Atoms of the Residue
    private ArrayList<Atom> hydrogenatoms = null;                         // a list of all hydrogen Atoms of the Residue
    private Integer pdbResNum = null;                       // pdb residue number
    private Integer dsspResNum = null;                      // guess what
    private Chain chain = null;                             // the Chain this Residue belongs to
    private String chainID = null;
    private String modelID = null;
    private String iCode = null; 
}
