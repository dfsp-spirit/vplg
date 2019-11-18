/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package io;

// imports
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import plcc.Settings;
import proteinstructure.AminoAcid;
import proteinstructure.Atom;
import proteinstructure.Chain;
import proteinstructure.Model;
import proteinstructure.Molecule;
import proteinstructure.Residue;
import tools.DP;


/**
 *
 * @author niclas
 */
class CifParser {
    
    // declare class vars
    static HashMap<String, String> metaData;
    static boolean dataInitDone;
    static boolean silent;
    
    
    protected static HashMap<String, String> getMetaData() {
        return metaData;
    }
    
    /**
     * Calls hidden FileParser method initVariables and inits additional CIF Parser variables.
     * @param pf PDB file path
     */
    private static void initVariables(String pf) {
        metaData = new HashMap<>();
    }
    
    /**
     * Like initData but for mmCIF data.
     * @param pf Path to a PDB file. Does NOT test whether it exist, do that earlier.
     * @param df Path to a DSSP file. Does NOT test whether it exist, do that earlier.
     * @return 
     */
    protected static Boolean initData(String pf) {
        
        initVariables(pf);
        
        silent = FileParser.settingSilent();
        
        if(parseData()) {
            dataInitDone = true;            
            return(true);
        }
        else {
            System.err.println("ERROR: Could not parse dssp and pdb data.");
            dataInitDone = false;
            System.exit(1);
            return(false);          // for the IDE ;)
        }
        
    }
    
    
    /**
     * Like parseData but for mmCIF files: goes through all lines of PDB and DSSP file and applies appropriate function to handle each line. 
     * @return ignore (?)
     */
    private static Boolean parseData() {
        // - - - Vars - - -
        //
        // for now local variables, may be needed as class variable though
        Boolean dataBlockFound = false; // for now only parse the first data block (stop if seeing 2nd block)
        Boolean inLoop = false;
        int ligandsTreatedNum = 0;
        int numberAtoms = 0;
        
        // variables for one loop (reset when hitting new loop)
        String tableCategory = null;
        // Key: name of column; Val: position in list
        //  -> if auth columns from atom_site not present they will be mapped to the PDB columns
        //     therefore always use auth columns unless you explicitly want the PDB ones
        HashMap<String,Integer> colHeaderPosMap = new HashMap<>();
        Boolean columnsChecked = false;
              
        // variables per (atom) line
        Integer atomSerialNumber, coordX, coordY, coordZ, molNumPDB;
        String atomRecordName, atomName, chainID, chemSym, altLoc, iCode, molNamePDB;
        Double oCoordX, oCoordY, oCoordZ;            // the original coordinates in Angstroem (coordX are 10th part Angstroem)
        Float oCoordXf, oCoordYf, oCoordZf;
        int lastLigandNumPDB = 0; // used to determine if atom belongs to new ligand residue
        String lastChainID = ""; // s.a.
        String[] tmpLineData;
        String tmp_modelID;
        
        // variables for successive matching atom -> residue/RNA : Molecule -> chain  
        // remember them so we dont need to lookup
        Model m = null;
        Molecule lastMol = null;    // starts as first residue and is always the actual one
        Residue tmpMol = null;      // used to save lastMol if getResidue returns null
        Chain tmpChain = null;
        Residue lig = null;
        
        Integer numLine = 0;
        
        // variables for already printed warnings
        Boolean furtherModelWarningPrinted = false;


        // - - - DSSP - - -
        //
        // - - residues - -
        if(! FileParser.silent) {
            System.out.println("  Creating all Molecules...");
        }

        // fills s_residues using AUTHCHAIN for chain ids
        // we need to do this here to get DsspResNum
        DsspParser.createAllResiduesFromDsspData(true);

        // If there is no data part at all in the DSSP file, the function readDsspToData() will catch
        //  this error and exit, this code will never be reached in that case.
        if(FileParser.s_molecules.size() < 1) {
            DP.getInstance().e("FP_CIF", "DSSP file contains no residues (maybe the PDB file only holds DNA/RNA data). Exiting.");
            System.exit(2);
        }
        
        lastMol = FileParser.s_molecules.get(0);  // just start with first one ( we check below if it really matches)
        
        // - - ligands - -
        // -> in difference to old parser ligands are created "on the fly" together with the other residues
        
        
        // - - - PDB - - - 
        //
        // idea: read each line once and dont save them. If it works that would save time and space.
        // lets do this for all the basic stuff first and neglect models, sites etc
        //     -> atoms, residues (s.a.), chains, SSEs (?)
        //     -> do the matching atom <-> residue, residue <-> chain later
        //         -> actually try to do it on the fly

        
        try {
            BufferedReader in = new BufferedReader(new FileReader(FileParser.pdbFile));
            String line;
            while ((line = in.readLine()) != null) {
                numLine ++;
                // first, check if line is a comment
                if (! line.startsWith("#")) {
                    
                    // check for data block
                    if (line.startsWith("data_")) {
                        if (dataBlockFound) {
                            DP.getInstance().w("FP_CIF", " Parsing of first data block ended at line " + numLine.toString()
                                + " as right now only the first data block is parsed.");
                            break; // for now we only parse first data block
                        }
                        else {
                            dataBlockFound = true;
                            if (line.length() > 5) {
                                if (! silent) {
                                    System.out.println("  PDB: Found the first data block named: " + line.subSequence(5, line.length()));
                                }
                            }
                            else {
                                if (! silent) {
                                    System.out.println("  PDB: Found the first data block (without a name).");
                                }
                            }
                        }
                    }
                    
                    // check for experiment method (meta data)
                    if (line.startsWith("_exptl.method")) {
                        tmpLineData = lineToArrayCIF(line);
                        if (tmpLineData.length > 1) {
                            if (! (tmpLineData[1] == "?" || tmpLineData[1] == ".")) {
                                metaData.put("experiment", tmpLineData[1]);
                            } else {
                                metaData.put("experiment", "");
                            }
                        }
                    }
                    
                    // check for resolution (meta data)
                    // TODO: really the best attribute(s) to do this?
                    if (line.startsWith("_reflns.d_resolution_high") || line.startsWith("_reflns.d_res_high") || line.startsWith("_refine.ls_d_res_high")) {
                        tmpLineData = lineToArrayCIF(line);
                        if (tmpLineData.length > 1) {
                            if (! (tmpLineData[1] == "?" || tmpLineData[1] == ".")) {
                                metaData.put("resolution", tmpLineData[1]);
                            } else {
                                metaData.put("resolution", "");
                            }
                        }
                    }
                    
                    // handle line when in loop
                    if (inLoop) {
                        
                        // check if we need data from this loop or if we can skip it
                        if (! (tableCategory == null)) {
                            if (! tableCategory.equals("_atom_site")) {
                                continue;
                            }
                        }
                        
                        // check for column heads
                        if (line.startsWith("_")) {
                            if (tableCategory == null) {
                                if (line.split("\\.").length < 2) {
                                    DP.getInstance().w("FP_CIF", " Expected table definition in line " + 
                                            numLine.toString() + " but couldnt parse it. Skip it (may miss important data!).");
                                } else {
                                    tableCategory = line.split("\\.")[0];
                                    if (! tableCategory.equals("_atom_site")) {
                                        continue;
                                    }
                                }
                            }
                            
                            colHeaderPosMap.put(line.split("\\.")[1].trim(), colHeaderPosMap.size());
                            
                            
                        } else {
                            // we are in the row section (data!)
                            
                            numberAtoms++;
                            
                            // check once if required column headers are present
                            if (! columnsChecked) {
                                ArrayList<String> missingCols = checkColumns(tableCategory, new ArrayList<>(colHeaderPosMap.keySet()));
                                if (missingCols.size() > 0) {
                                    DP.getInstance().e("FP_CIF", "Missing following columns in " + tableCategory + 
                                            ": " + missingCols);
                                    DP.getInstance().e("FP_CIF", " Exiting now.");
                                    System.exit(1);
                                }
                                
                                // if auth columns not present map them to PDB ones
                                // pdbx_PDB_model_num not checked as no equivalent existing (just use default model 1 if not existing)
                                String[] authCols = {"auth_atom_id", "auth_asym_id", "auth_comp_id", "auth_seq_id"};
                                // Matching equivalents to author columns
                                String[] pdbCols = {"label_atom_id", "label_asym_id", "label_comp_id", "label_seq_id"};
                                for (int i = 0; i < authCols.length; i++) {
                                    if (colHeaderPosMap.get(authCols[i]) == null) {
                                        colHeaderPosMap.put(authCols[i], colHeaderPosMap.get(pdbCols[i]));
                                        if (! silent) {
                                            System.out.println("   Using " + pdbCols[i] + " instead of "+ 
                                                    "missing column " + authCols[i]);
                                        }
                                    }
                                }
                                
                                columnsChecked = true;
                            }
                            
                            // get data of line
                            tmpLineData = lineToArrayCIF(line);
                            
                            // - - model - -
                            // Look if model numbers are included
                            if (colHeaderPosMap.get("pdbx_PDB_model_num") != null) {
                                tmp_modelID = tmpLineData[colHeaderPosMap.get("pdbx_PDB_model_num")];
                               
                                // save modelID for print later
                                if (! FileParser.s_allModelIDsFromWholePDBFile.contains(tmp_modelID)) {
                                    FileParser.s_allModelIDsFromWholePDBFile.add(tmp_modelID);
                                }
                                        
                                if (m == null) {
                                    // use first model
                                    m = new Model(tmp_modelID);
                                    FileParser.s_models.add(m);
                                    if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
                                        System.out.println("   PDB: New model '" + m.getModelID() + "' found");
                                    }
                                } else {
                                    // same model as before?
                                    if (! m.getModelID().equals(tmp_modelID)) {
                                        if (! furtherModelWarningPrinted) {
                                            System.out.println("   PDB: Found further models. Ignoring them.");
                                            furtherModelWarningPrinted = true;
                                        }

                                        // skip this line
                                        continue;
                                    }
                                }
                            } else {
                                // create default model instead
                                m = new Model("1");
                                FileParser.s_models.add(m);
                                System.out.println("   PDB: No model column. Creating default model '1'");
                            }
                            
                            // - - chain - -
                            // check for a new chain (always hold the current 
                            if (colHeaderPosMap.get("auth_asym_id") != null) {
                                if (tmpLineData.length >= colHeaderPosMap.get("auth_asym_id") + 1) {
                                    String tmp_cID = tmpLineData[colHeaderPosMap.get("auth_asym_id")];
                                    if (tmpChain == null) {
                                        tmpChain = getOrCreateChain(tmp_cID, m);
                                    } else 
                                        if (! (tmpChain.getPdbChainID().equals(tmp_cID))) {
                                            tmpChain = getOrCreateChain(tmp_cID, m);
                                        }
                                } else {
                                    DP.getInstance().w("FP_CIF", " Line " + numLine + " should contain a value in column " + 
                                            colHeaderPosMap.get("auth_asym_id") + " (expected chain name) but didnt. Skipping line.");
                                }  
                            }
                            
                            // - - atom - -
                            // reset variables
                            atomSerialNumber = molNumPDB = coordX =  coordY = coordZ = null;
                            atomRecordName = atomName = molNamePDB = chainID = chemSym = altLoc = null;
                            iCode = " "; // if column does not exist or ? || . is assigned use 1 blank (compare old parser)
                            oCoordX = oCoordY = oCoordZ = null;            // the original coordinates in Angstroem (coordX are 10th part Angstroem)
                            oCoordXf = oCoordYf = oCoordZf = null;
                            
                            // chain name
                            chainID = tmpLineData[colHeaderPosMap.get("auth_asym_id")];

                            // PDBx field alias atom record name
                            if (colHeaderPosMap.get("group_PDB") != null) {
                                if (colHeaderPosMap.get("group_PDB") < 0) {
                                    atomRecordName = tmpLineData[colHeaderPosMap.get("group_PDB")];
                                }
                            } else {
                                if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                    DP.getInstance().w("FP_CIF", "Seems like _atom_site.group_PDB is missing. Trying to ignore it.");  
                                    colHeaderPosMap.put("group_PDB", -1);  // save that warning has been printed
                                } 
                            }
                            
                            // atom id alias serial number
                            atomSerialNumber = Integer.valueOf(tmpLineData[colHeaderPosMap.get("id")]); // there should be no need to trim as whitespaces should be ignored earlier
                             
                            // detailed atom name
                            // old PDB files used spacing to differentiate between atoms
                            // e.g. " CA " = C alpha, how to deal with this? mmCIF has no spacings
                            // for now workaround for probable C alpha
                            if (tmpLineData[colHeaderPosMap.get("label_atom_id")].equals("CA")) {
                                atomName = " " + tmpLineData[colHeaderPosMap.get("label_atom_id")] + " ";
                            } else {
                                atomName = tmpLineData[colHeaderPosMap.get("label_atom_id")];
                            }
                                
                            // alternative location
                            if (colHeaderPosMap.get("label_alt_id") != null) {
                                altLoc = tmpLineData[colHeaderPosMap.get("label_alt_id")];
                            }
                            
                            // residue name or rna name 
                             //resNamePDB = tmpLineData[colHeaderPosMap.get("label_comp_id")];
                            molNamePDB = tmpLineData[colHeaderPosMap.get("label_comp_id")];
                            
                            // residue number or rna number 
                            
                            // use auth_seq_id > label_seq_id (hope DSSP does so too)
                            // resNumPDB = Integer.valueOf(tmpLineData[colHeaderPosMap.get("auth_seq_id")]);
                            molNumPDB = Integer.valueOf(tmpLineData[colHeaderPosMap.get("auth_seq_id")]);
                             
                            
                            // insertion code
                            // only update if column and value exist, otherwise stick to blank ""
                            if (colHeaderPosMap.get("pdbx_PDB_ins_code") != null) {
                                if (! (tmpLineData[colHeaderPosMap.get("pdbx_PDB_ins_code")].equals("?") || tmpLineData[colHeaderPosMap.get("pdbx_PDB_ins_code")].equals("."))) {
                                    iCode = tmpLineData[colHeaderPosMap.get("pdbx_PDB_ins_code")];
                                }
                            }
                            
                            // coordX
                            // for information on difference between ptgl and plcc style look in old parser
                            if (Settings.getBoolean("plcc_B_strict_ptgl_behaviour")) {
                                oCoordX = Double.valueOf(tmpLineData[colHeaderPosMap.get("Cartn_x")]) * 10.0;
                                coordX = oCoordX.intValue();
                            } else {
                                oCoordXf = Float.valueOf(tmpLineData[colHeaderPosMap.get("Cartn_x")]) * 10;
                                coordX = Math.round(oCoordXf);
                            }

                            
                            // coordY
                            if (Settings.getBoolean("plcc_B_strict_ptgl_behaviour")) {
                                oCoordY = Double.valueOf(tmpLineData[colHeaderPosMap.get("Cartn_y")]) * 10.0;
                                coordY = oCoordY.intValue();
                            } else {
                                oCoordYf = Float.valueOf(tmpLineData[colHeaderPosMap.get("Cartn_y")]) * 10;
                                coordY = Math.round(oCoordYf);
                            }
                            
                            // coordZ
                            if (Settings.getBoolean("plcc_B_strict_ptgl_behaviour")) {
                                oCoordZ = Double.valueOf(tmpLineData[colHeaderPosMap.get("Cartn_z")]) * 10.0;
                                coordZ = oCoordZ.intValue();
                            } else {
                                oCoordZf = Float.valueOf(tmpLineData[colHeaderPosMap.get("Cartn_z")]) * 10;
                                coordZ = Math.round(oCoordZf);
                            }
                            
                            // chemical symbol
                            chemSym = tmpLineData[colHeaderPosMap.get("type_symbol")];
                            
                            // standard AAs and (some) non-standard, atm: UNK, MSE
                            //   -> may be changed below if it is free (treat as ligand then)
                            Boolean isAA = FileParser.isAminoacid(molNamePDB, true);

                            // TODO: possible to ignore alt loc atoms right now?
                            
                            // >> DNA/RNA <<
                            // ignore atm 
                            /*if(FileParser.isDNAorRNAresidueName(leftInsertSpaces(resNamePDB, 3))) {
                                if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                    DP.getInstance().w("Atom #" + atomSerialNumber + " in PDB file belongs to DNA/RNA residue (residue 3-letter code is '" + resNamePDB + "'), skipping.");
                                }
                                continue; // do not use that atom
                            }*/
                            
                            if(FileParser.isDNAresidueName(FileParser.leftInsertSpaces(molNamePDB, 3))) {
                                if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                    DP.getInstance().w("Atom #" + atomSerialNumber + " in PDB file belongs to DNA residue (residue 3-letter code is '" + molNamePDB + "'), skipping.");
                                }
                                continue; // do not use that atom
                            }
        
                            if( ! Settings.getBoolean("plcc_B_include_rna")) {
                                if(FileParser.isRNAresidueName(FileParser.leftInsertSpaces(molNamePDB, 3))) {
                                    if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                                        DP.getInstance().w("Atom #" + atomSerialNumber + " in PDB file belongs to RNA residue (residue 3-letter code is '" + molNamePDB + "'), skipping.");
                                    }
                                    continue; // do not use that atom
                                }
                            }
                                                                               
                            // >> AA <<
                            // update lastMol (only if needed) 
                            //     -> enables getting DsspResNum for atom from res
                            // match res <-> chain here 
                            // also decide here if modified amino acids is free (no DSSP entry -> not in s_residue) and needs to be treated as ligand
                            if (isAA) {
                                // we no start with lastMol is first residue from s_residues, so no check for null required!
                                // load new Residue into lastMol if we approached next Residue
                                if (! (Objects.equals(molNumPDB, lastMol.getPdbNum()) && chainID.equals(lastMol.getChainID()) && iCode.equals(lastMol.getiCode()))) {
                                    tmpMol = FileParser.getResidueFromList(molNumPDB, chainID, iCode);
                                    // check that a peptid residue could be found
                                    if (tmpMol == null || tmpMol.isLigand()) {
                                        // residue is not in DSSP file -> must be free (modified) amino acid, treat as ligand
                                        if (! silent) {
                                            // print note only once
                                            if (! molNumPDB.equals(lastLigandNumPDB))
                                            System.out.println("   PDB: Found a free (modified) amino acid at PDB# " + molNumPDB + ", treating it as ligand.");
                                        }
                                        isAA = false;
                                    } else {
                                        lastMol = tmpMol;
                                        lastMol.setModelID(m.getModelID());
                                        lastMol.setChain(tmpChain);
                                        tmpChain.addMolecule(lastMol);
                                        
                                        // assign PDB res name (which differs in case of modifed residues)
                                        lastMol.setName3(molNamePDB);
                                    }
                                }
                            }
                            
                            Atom a = new Atom();
                            
                            // handle stuff that's different between ATOMs (AA) and HETATMs (ligand)
                            if(isAA) {
                                // >> AA <<
                                if (FileParser.isIgnoredAtom(chemSym)) {
                                    if( ! (Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce") && chemSym.trim().equals("H"))) {
                                        if (Settings.getInteger("plcc_I_debug_level") > 0) {
                                            System.out.println("DEBUG Ignored atom line " + numLine.toString() + 
                                                    " as it is either in ignored list or handle_hydrogens turned off.");
                                        }
                                        continue;
                                    }
                                }
                                
                                // set atom type
                                a.setAtomtype(Atom.ATOMTYPE_AA);
                                
                                // only ATOMs, not HETATMs, have a DSSP entry
                                if((Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce") && chemSym.trim().equals("H"))) {
                                    a.setDsspResNum(null);
                                }
                                else {
                                    a.setDsspResNum(lastMol.getDsspNum());
                                }
                                
                                /*if(  Settings.getBoolean("plcc_B_include_rna")) {
                                    a.setAtomtype(Atom.ATOMTYPE_RNA);
                                }*/
                                
                                
                            } else {
                                // >> LIG <<
                                
                                // idea: add always residue (for consistency) but atom only if needed
                                
                                // currently not used
                                // String lf, ln, ls;      // temp for lig formula, lig name, lig synonyms
                                                               
                                // check if we have created ligand residue for s_residue
                                if( ! ( molNumPDB.equals(lastLigandNumPDB) && chainID.equals(lastChainID) ) ) {

                                    // create new Residue from info, we'll have to see whether we really add it below though
                                    lig = new Residue();
                                    
                                    lig.setPdbNum(molNumPDB);
                                    lig.setType(Residue.RESIDUE_TYPE_LIGAND);
                                    
                                    // assign fake DSSP Num increasing with each seen ligand
                                    ligandsTreatedNum ++;
                                    int resNumDSSP = DsspParser.lastUsedDsspNum + ligandsTreatedNum; // assign an unused fake DSSP residue number
                                    
                                    lig.setDsspNum(resNumDSSP);
                                    lig.setChainID(chainID);
                                    lig.setiCode(iCode);
                                    lig.setName3(molNamePDB);
                                    lig.setAAName1(AminoAcid.getLigandName1());
                                    lig.setChain(FileParser.getChainByPdbChainID(chainID));
                                    // still just assigning default model 1
                                    lig.setModelID(m.getModelID());
                                    lig.setSSEString(Settings.get("plcc_S_ligSSECode"));
                                    
                                    
                                    // add ligand to list of residues if it not on the ignore list
                                    if(FileParser.isIgnoredLigRes(molNamePDB)) {
                                        ligandsTreatedNum--;    // We had to increment before to determine the fake DSSP res number, but
                                                        //  this ligand won't be stored so decrement to previous value.
                                        //System.out.println("    PDB: Ignored ligand '" + resNamePDB + "-" + molNumPDB + "' at PDB line " + pLineNum + ".");
                                    } else {
                                        
                                        // ignore this for now: needs parsing of two more loops (_pdbx_nonpoly_scheme, _chem_comp)   	 

                                        /*                                        
                                        // add info from PDB HET fields (HET, HETNAM, HETSYN, FORMUL)
                                        // Note: we now use prepared statements so any strange chars do no longer lead to irritations or security trouble
                                        Boolean removeStuff = Settings.getBoolean("plcc_B_uglySQLhacks");
                                        lf = getLigFormula(resNamePDB);
                                        if(removeStuff) {
                                            lf = lf.replaceAll("\\s", "");               // remove all whitespace
                                            lf = lf.replaceAll("~", "");                 // remove tilde char (it causes SQL trouble during DB insert)
                                            lf = lf.replaceAll("\\\\", "");              // remove all backslashes (it causes SQL WARNING 'nonstandard use of escape in a string literal' during DB insert)
                                            lf = lf.replaceAll("'", "");              // remove all ticks (obviously SQL trouble)
                                        }

                                        ln = getLigName(resNamePDB);
                                        if(removeStuff) {
                                            ln = ln.replaceAll(" ", "_");                // replace spaces with underscores
                                            ln = ln.replaceAll("\\s", "");               // remove all other whitespace
                                            ln = ln.replaceAll("~", "");
                                            ln = ln.replaceAll("\\\\", "");
                                            ln = ln.replaceAll("'", "");
                                        }

                                        ls = getLigSynonyms(resNamePDB);
                                        if(removeStuff) {
                                            ls = ls.replaceAll(" ", "_");
                                            ls = ls.replaceAll("\\s", "");
                                            ls = ls.replaceAll("~", "");
                                            ls = ls.replaceAll(";", ".");
                                            ls = ls.replaceAll("\\\\", "");
                                            ls = ls.replaceAll("'", "");
                                        }

                                        lig.setLigFormula(lf);
                                        lig.setLigName(ln);
                                        lig.setLigSynonyms(ls);
                                        
                                        */

                                        lastLigandNumPDB = molNumPDB;
                                        lastChainID = chainID;

                                        FileParser.s_molecules.add(lig);
                                        
                                        FileParser.getChainByPdbChainID(chainID).addMolecule(lig);

                                        // do we need this?
                                        //resIndex = s_residues.size() - 1;
                                        //resIndexDSSP[resNumDSSP] = resIndex;
                                        //resIndexPDB[molNumPDB] = resIndex;      // This will crash because some PDB files contain negative residue numbers so fuck it.
                                        if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
                                            System.out.println("   PDB: Added ligand '" +  molNamePDB + "-" + molNumPDB + "', chain " + chainID + " (line " + numLine + ", ligand #" + ligandsTreatedNum + ", Fake DSSP #" + resNumDSSP + ").");
                                            System.out.println("   PDB:   => Ligand name = '" + lig.getLigName() + "', formula = '" + lig.getLigFormula() + "', synonyms = '" + lig.getLigSynonyms() + "'.");
                                        }

                                    }
                                }
                                   
                                
                                if(FileParser.isIgnoredLigRes(molNamePDB)) {
                                    a.setAtomtype(Atom.ATOMTYPE_IGNORED_LIGAND);       // invalid ligand (ignored)

                                    // We do not need these atoms and they may lead to trouble later on, so
                                    //  just return without adding the new Atom to any Residue here so this line
                                    //  is skipped and the next line can be handled.
                                    //  If people want all ligands they have to change the isIgnoredLigRes() function.
                                    
                                    // DEBUG
                                    // DP.getInstance().w("FP_CIF", " Ignored ligand atom of '" +  resNamePDB + "-" + molNumPDB + "', chain " + chainID + " (line " + numLine + ", ligand #" + ligandsTreatedNum + ", Fake DSSP #" + lig.getDsspResNum().toString() + ").");
                                    continue; // can we do this here? Does it cut off other important stuff? -> added to res but not atom (s.a.)
                                }
                                else {
                                    a.setAtomtype(Atom.ATOMTYPE_LIGAND);       // valid ligand
                                    //a.setDsspResNum(getDsspResNumForPdbFields(molNumPDB, chainID, iCode));  // We can't do this because the fake DSSP residue number has not yet been assigned
                                }
                                
                                
                            }
                            
                            // >> AA + LIG <<
                            // now create the new Atom

                            // lastMol may be NULL
                            // Note that the command above may have returned NULL, we care for that below

                            a.setPdbAtomNum(atomSerialNumber);
                            a.setAtomName(atomName);
                            a.setAltLoc(altLoc);
                            a.setMolecule(lastMol);
                            a.setChainID(chainID);        
                            a.setChain(FileParser.getChainByPdbChainID(chainID));
                            a.setPdbResNum(molNumPDB);
                            // we cant get the DSSP res num easily here and have to do it later (I guess)
                            // old parser seems to assign 0 here whatsoever so we just leave the default value there
                            // a.setDsspResNum(resNumDSSP);
                            a.setCoordX(coordX);
                            a.setCoordY(coordY);
                            a.setCoordZ(coordZ);
                            a.setChemSym(chemSym);
                            
                            // from old parser, not working with models right now
                            /*
                            if(curModelID != null) {
                                a.setModelID(curModelID);
                                a.setModel(getModelByModelID(curModelID));
                            }
                            */
                            
                            if (isAA) {
                                // >> AA <<
                                if (lastMol == null) {
                                    DP.getInstance().w("Residue with PDB # " + molNumPDB + " of chain '" + chainID + "' with iCode '" + iCode + "' not listed in DSSP data, skipping atom " + atomSerialNumber + " belonging to that residue (PDB line " + numLine.toString() + ").");
                                    continue;
                                } else {

                                    if(Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce") && chemSym.trim().equals("H")) {
                                        lastMol.addHydrogenAtom(a);
                                    }
                                    else {
                                        lastMol.addAtom(a);
                                        FileParser.s_atoms.add(a);
                                    }
                                }
                            } else {
                                // >> LIG <<
                                if (! (lig == null)) {
                                    lig.addAtom(a);
                                    a.setMolecule(lig);
                                    FileParser.s_atoms.add(a);
                                }
                            }  
                        }
                    } else {
                        // loops must not be nested according to file format definition
                        //     that is why we can check for a new loop here
                        // check if new loop starts (check for loop end is done with # in beginning)
                        if (line.startsWith("loop_")) {
                            inLoop = true;
                            
                            // reset vars per loop
                            tableCategory = null;
                            lastLigandNumPDB = 0;
                            chainID = "";
                        }
                    }
                } else {
                    // '#' seems to stand between each category, we use it to decide if loop ended
                    //     and hope it does not occur inside a loop
                    inLoop = false;
                    colHeaderPosMap.clear();
                    columnsChecked = false;
                    
                }
            } // end of reading in lines
	} catch (IOException e) {
            System.err.println("ERROR: Could not parse PDB file.");
            System.err.println("ERROR: Message: " + e.getMessage());
            System.exit(1);
	}
        
        if (! (silent || FileParser.essentialOutputOnly)) {
            System.out.println("  PDB: Found in total " + FileParser.s_chains.size() + " chains.");
        }
        
        // alt loc treatment copy&pasted from old parser
        if(! (FileParser.silent || FileParser.essentialOutputOnly)) {
            System.out.println("    PDB: Hit end of PDB file at line " + numLine + ".");

            // remove duplicate atoms from altLoc here
            System.out.println("    PDB: Selecting alternative locations for atoms of all residues.");
        }
        ArrayList<Atom> deletedAtoms;
        int numAtomsDeletedAltLoc = 0;
        int numResiduesAffected = 0;
        Residue r;
        for(int i = 0; i < FileParser.s_molecules.size(); i++) {
            if (FileParser.s_molecules.get(i) instanceof Residue) {
                r = (Residue) FileParser.s_molecules.get(i);
                deletedAtoms = r.chooseYourAltLoc();


                if(deletedAtoms.size() > 0) {
                    numResiduesAffected++;
                }

                //delete atoms from global atom list as well
                for(Atom a : deletedAtoms) {
                    if(FileParser.s_atoms.remove(a)) {
                        numAtomsDeletedAltLoc++;
                    } else {
                        DP.getInstance().w("Atom requested to be removed from global list does not exist in there.");
                    }
                }
            }
        }
        
        // add empty Strings to metadata to avoid SQL null errors
        fillMetadataEmptyStrings();
        if (FileParser.s_chains.size() > 62 || numberAtoms > 99999) {
            metaData.put("isLarge", "true");
        } else {
            metaData.put("isLarge", "false");
        }
        
        // all lines have been read
        return(true);
    }
    
    
    /**
     * Returns an array of 'words' seperated by an arbitrary amount of spaces.
     * @param line
     * @return
     */
    private static String[] lineToArrayCIF(String line) {
        String tmpReturnList[] = new String[line.split(" ").length];
        String tmpLineList[];
        int counterValues = 0;
        tmpLineList = line.split(" ");
        
        for (int i=0; i < tmpLineList.length; i++) {
            if (! tmpLineList[i].isEmpty()) {
                tmpReturnList[counterValues] = tmpLineList[i];
                counterValues++;
            }
        }
        
        // return Array without null entries
        return Arrays.copyOfRange(tmpReturnList, 0, counterValues);
    }
    
    
    /**
     * Fills important metaData fields that are not existing with empty Strings.
     */
    private static void fillMetadataEmptyStrings() {
        List<String> mdFields = Arrays.asList("title", "keywords", "experiment", "resolution", "date", "header");
        for (String field : mdFields) {
            if (! metaData.containsKey(field)) {
                metaData.put(field, "");
            }
        }
    }
    
    /**
     * Checks for the presence of predefined (hard coded) column headers.
     * @param categoryName The name of the category
     * @param columnHeaders The column headers found in the file
     * @return A list of all required columns that were missing
     */
    private static ArrayList<String> checkColumns(String categoryName, ArrayList<String> columnHeaders) {
        // different columns are expected depending on category
        // define them here:
        String[] reqColumns;
        switch (categoryName) {
            case "_atom_site":
                reqColumns = new String[] {"id", "type_symbol", "label_atom_id", "label_comp_id", 
                    "label_asym_id", "Cartn_x", "Cartn_y", "Cartn_z"};
                break;
            default:
                if (! Settings.getBoolean("plcc_B_no_warn")) {
                    DP.getInstance().w("FP_CIF", "Tried to check table of category " + categoryName +
                            " for presence of important columns, but function is not defined for that " +
                            "category. Ignoring the check and moving on.");
                }
                reqColumns = new String[0];
        }
        
        ArrayList<String> missingColumns = new ArrayList<>();

        Boolean found;
        for (String reqColumn : reqColumns) {
            found = false;
            for (String colHeader: columnHeaders) {
                if (colHeader.equals(reqColumn)) {
                    found = true;
                    break;
                }
            }
            if (! found) {
                    missingColumns.add(reqColumn);
            }
        }
        return missingColumns;
    }
    
    
    /**
     * Gets chain by ID if existing otherwise creates it.
     * @param cID chain ID as String
     * @param m Model to which the chain belongs
     * @return 
     */
    private static Chain getOrCreateChain(String cID, Model m) {
        for (Chain existing_c : FileParser.s_chains) {
            if (existing_c.getPdbChainID().equals(cID)) {
                return existing_c;
            }
        }
        
        // reaching this code only if chain didnt exist
        Chain c = new Chain(cID);
        c.setModel(m);
        c.setModelID(m.getModelID());
        m.addChain(c);
        FileParser.s_chains.add(c);
        if (! (FileParser.silent || FileParser.essentialOutputOnly)) {
            System.out.println("   PDB: New chain named " + cID + " found.");
        }
        return c;
    }
    
      
}
