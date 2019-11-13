/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package io;

// imports

import proteinstructure.Residue;


/**
 * File parser for legacy PDB file format.
 * 
 * @author jnw
 */
public class LegacyParser extends FileParser {
    static Integer maxResidues = 11000;  // could be useless, but was included in pre-dissolved file parser
    static Integer maxUsedDsspResNumInDsspFile = null;  // used to determine fake DSSP numbers for ligands
    static String firstModelName = "1";  // the model ID that identifies the first model in a PDB file
    static String defaultModelName = firstModelName;
    
    static Integer curLineNumPDB = null;
    static Integer curLineNumDSSP = null;
    static String curLinePDB = null;
    static String curLineDSSP = null;
    static String curModelID = null;
    static String oldModelID = null;
    static String curChainID = null;
    static String oldChainID = null;
    static Integer curResNumPDB = null;
    static Integer curResNumDSSP = null;
    static Residue curRes = null;
    
}
