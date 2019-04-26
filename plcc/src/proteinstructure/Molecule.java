/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package proteinstructure;

import java.util.ArrayList;
import java.util.HashMap;
import plcc.Main;
import plcc.Settings;
import proteinstructure.SSE;
import tools.DP;
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
    
   
     /**
     * Returns the C alpha atoms of this residue or null if it has none.
     * @return the alpha carbon or null 
     */
    public Atom getAlphaCarbonAtom() {
        if(this.isAA()) {
            for(Atom a : this.atoms) {
                if(a.isCalphaAtom()) {
                    return a;
                }
            }
        }
        return null;
    }
    
    /**
     * Determines whether this Residue has at least one Atom.
     * @return true if this Residue has at least one Atom, false otherwise.
     */ 
    public boolean hasAtoms() {
        return(this.atoms.size() > 0);
    }
    
    public Boolean isLigand() { return(this.type.equals(Residue.RESIDUE_TYPE_LIGAND)); }
    public Boolean isAA() { return(this.type.equals(Residue.RESIDUE_TYPE_AA)); }
    public Boolean isOtherRes() { return(this.type.equals(Residue.RESIDUE_TYPE_OTHER)); }
    public Boolean isRNA() { return(this.type.equals(Residue.RESIDUE_TYPE_RNA)); }

        /**
     * Returns the alternate location identifier that most atoms of this residue share.
     * @return the most common alternate location identifier of this residue. If this residue
     * has no atoms, a space (" ", the PDB default altLoc) will be returned.
     */
    public String getAltLocWithMostAtoms() {
        String maxAltLoc = " ";
        
        // add all altLocs
        String altLoc;
        Integer newCount;
        Integer maxCount = 0;
        HashMap<String, Integer> atomCountsByAltLoc = new HashMap<String, Integer>();
        for(Atom a : this.atoms) {
            altLoc = a.getAltLoc();
            if(atomCountsByAltLoc.containsKey(altLoc)) {
                // altLoc already listed, so just increase count
                newCount = atomCountsByAltLoc.get(altLoc) + 1;
            } else {
                // altLoc not listed yet so this is the first atom with this type, put it in there
                newCount = 1;                
            }
            atomCountsByAltLoc.put(altLoc, newCount);
            
            // keep track of maximum
            if(newCount > maxCount) {
                maxAltLoc = altLoc;
                maxCount = newCount;
            }
        }
        return maxAltLoc;
    }
        
        /**
     * Returns the alternate location identifier that most atoms of this residue share.
     * @return the most common alternate location identifier of this residue. If this residue
     * has no atoms, a space (" ", the PDB default altLoc) will be returned.
     */
    public String getAltLocWithAlphaCarbonAndMostAtoms() {
        
        ArrayList<String> altLocsCA = this.getAltLocsWithAlphaCarbonAtoms();
        if(altLocsCA.size() < 1) {
            DP.getInstance().w("Residue " + this.getFancyName() + " has no AltLoc which includes an alpha carbon atom.");
            return this.getAltLocWithMostAtoms();
        }
        
        if(altLocsCA.size() == 1) {
            return altLocsCA.get(0);
        }
        
        String maxAltLoc = " ";
        
        // add all altLocs
        String altLoc;
        Integer newCount;
        Integer maxCount = 0;
        HashMap<String, Integer> atomCountsByAltLoc = new HashMap<String, Integer>();
        for(Atom a : this.atoms) {
            altLoc = a.getAltLoc();
            
            // skip AltLocs which do not contain C alpha atoms
            if( ! altLocsCA.contains(altLoc)) {
                continue;
            }
            
            if(atomCountsByAltLoc.containsKey(altLoc)) {
                // altLoc already listed, so just increase count
                newCount = atomCountsByAltLoc.get(altLoc) + 1;
            } else {
                // altLoc not listed yet so this is the first atom with this type, put it in there
                newCount = 1;                
            }
            atomCountsByAltLoc.put(altLoc, newCount);
            
            // keep track of maximum
            if(newCount > maxCount) {
                maxAltLoc = altLoc;
                maxCount = newCount;
            }
        }
        
        //DEBUG
        /*
        if(this.pdbResNum == 209) {
            System.out.println("DEBUG: " + atomCountsByAltLoc.keySet().size() + " Alternate locations and their atoms counts for " + this.toString() + ": ");
            for(String al : atomCountsByAltLoc.keySet()) {
                System.out.println(" '" + al + "' has " +  atomCountsByAltLoc.get(al) + " atoms (of " + this.atoms.size() + " total).");
            }
            System.out.println("DEBUG: MaxAltLoc=" + maxAltLoc + ", maxCount=" + maxCount + ".");
        }
        */
                
        return maxAltLoc;
    }
    
    /**
     * Determines all atoms with the requested alternate location identifier.
     * @param reqAltLoc the alternate location identifier (PDB file field)
     * @return the atom list
     */
    public ArrayList<Atom> getAtomsWithAltLoc(String reqAltLoc) {
        ArrayList<Atom> reqAtoms = new ArrayList<Atom>();
        
        for(Atom a : this.atoms) {
            if(reqAltLoc.equals(a.getAltLoc())) {
                reqAtoms.add(a);
            }
        }        
        return reqAtoms;
    }
    
    /**
     * Determines the number of atoms with the requested alternate location identifier.
     * @param reqAltLoc the alternate location identifier (PDB file field)
     * @return the atom count with that altLoc
     */
    public int getNumAtomsWithAltLoc(String reqAltLoc) {
        return this.getAtomsWithAltLoc(reqAltLoc).size();
    }
    
    /**
     * Deletes all atoms from this residue which do NOT have the specified alternate location identifier.
     * @param keepAltLoc
     * @return a list of the deleted atoms 
     */
    private ArrayList<Atom> deleteAtomsWithAltLocDifferentFrom(String keepAltLoc) {
        ArrayList<Atom> deletedAtoms = new ArrayList<Atom>();               
        
        Atom a;        
        for(int i = 0; i < this.atoms.size(); i++) {
            a = this.atoms.get(i);
            if( ! (a.getAltLoc()).equals(keepAltLoc) ) {
                deletedAtoms.add(a);
                this.atoms.remove(a);
                i--;                
            }
        }        
        return deletedAtoms;
    }
    
    
    /**
     * Determines all AltLoc identifiers which have an alpha carbon atom assigned to them for this residue.
     * @return the list of all altLocs which include alpha carbons
     */ 
    private ArrayList<String> getAltLocsWithAlphaCarbonAtoms() {
        
        if(! this.isAA()) {
            DP.getInstance().w("Non-AA residue cannot contain alpha carbon atom but asked to look for one.");
        }
        
        ArrayList<String> altLocsWithAlphaCarbon = new ArrayList<String>();
        
        String candidateAltLoc;
        for(Atom a : this.atoms) {
            if(a.isCalphaAtom()) {
                // Be careful not to add an AltLoc which has several C-alpha 
                // atoms listed twice (should never happen and makes no sense, but
                // with PDB files you never know).
                candidateAltLoc = a.getAltLoc();
                if( ! altLocsWithAlphaCarbon.contains(candidateAltLoc)) {
                    altLocsWithAlphaCarbon.add(candidateAltLoc);
                }
            }
        }
        
        return altLocsWithAlphaCarbon;
    }
    
    
    /**
     * Tells this residue to choose its alternate location PDB identifier and delete all its
     * atoms which have other AltLocs. For amino acids, this will choose the AltLoc which has a C-alpha atom
     * and maximizes the number of atoms in the residue. For non-AAs, this function will always choose the AltLoc identifier which
     * maximizes the number of atoms in this residue. The list of deleted atoms is returned so they can be deleted
     * from the global atom list as well.
     * @return the list of the atoms that were deleted
     */
    public ArrayList<Atom> chooseYourAltLoc() {
        
        int numAtomsBefore = this.atoms.size();
        if(numAtomsBefore < 1) {
            if(! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Residue " + this.getFancyName() + " of chain " + this.getChainID() + " has NO atoms at all *before* choosing alternative location PDB field and deleting others.");
            }
            return new ArrayList<Atom>();
        }                
        ArrayList<Atom> deletedAtoms;
        
        String chosenAltLoc;
        if(this.isAA()) {
            chosenAltLoc = getAltLocWithAlphaCarbonAndMostAtoms();
        } else {
            chosenAltLoc = this.getAltLocWithMostAtoms();
        }
        
        int numAtomsWithChosenAltLoc = this.getNumAtomsWithAltLoc(chosenAltLoc);
        
        if((numAtomsWithChosenAltLoc < 1 || numAtomsWithChosenAltLoc > Main.MAX_ATOMS_PER_AA) && this.isAA()) {
            if( ! Settings.getBoolean("plcc_B_handle_hydrogen_atoms_from_reduce")) {
                DP.getInstance().w("Chosen altLoc '" + chosenAltLoc + "' leads to " + numAtomsWithChosenAltLoc + " atoms for AA residue " + this.getFancyName() + ".");
            }
        }
        
        
        
        deletedAtoms = this.deleteAtomsWithAltLocDifferentFrom(chosenAltLoc);
                
        if(this.atoms.size() < 1) {
            DP.getInstance().w("Residue " + this.getFancyName() + " of chain " + this.getChainID() + " has no atoms after choosing alternative location PDB field (had " + numAtomsBefore + " before).");
        }
        
        return deletedAtoms;
    }
    
    public String getFancyName() { return(this.resName3 + "-" + this.pdbResNum); }
    /**
     * Returns the PLCC SSE string of this SSE. May be blank/ a single space ' '.
     * @return the PLCC SSE string of this SSE, e.g., "H", "E", " " or "L"
     */
    
    public String getChainID() { return(chainID); }
   
}
