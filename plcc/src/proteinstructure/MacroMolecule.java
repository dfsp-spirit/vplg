/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012 - 2015. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteinstructure;

/**
 *
 * @author spirit
 */
public class MacroMolecule {
    /** The Macromol name, from the PDB file field MOLECULE in the COMPND section. */
    private String macromolName;
    
    /** The Macromol name, from the PDB file field MOL_ID in the COMPND section. */
    private String macromolID;
    
    /** The chains of the PDB file which belong to this compound, from the PDB file field CHAIN in the COMPND section */
    private String macromolChains;
    
    
    
    /** The Macromol EC number, from the PDB file field MOL_ID in the COMPND section. Optional data in the PDB, does not exist for many PDB files. */
    private String macromolInfoEC;    
    /** Whether the compound is engineered. Optional data in the PDB, does not exist for many PDB files. */
    private String macromolInfoIsEngineered;
}
