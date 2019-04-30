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
import java.util.List;
import java.util.Objects;
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
    private String RNAName1=null;
    public Integer type = null;                            // residue type: 0=AA, 1=Ligand, 2=Other
    public ArrayList<Atom> atoms = null;                         // a list of all (non-H) Atoms of the Residue
    public ArrayList<Atom> hydrogenatoms = null;                         // a list of all hydrogen Atoms of the Residue
    public Chain chain = null;                             // the Chain this Residue belongs to
    public String chainID = null;
    public String modelID = null;
    public String iCode = null; 
    public Integer centerSphereRadius = null;
    
    //constructor
    public Molecule(){this.atoms = new ArrayList<>(); this.hydrogenatoms = new ArrayList<>(); }

   
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
    
    /**
     * Returns the radius of the collision sphere of this residue. This radius doe NOT yet include the outer hull, it is the distance from the center atom to the (non-H) atom farthest away from it.
     * @return the radius, in 1/10th Angstroem (so 20 means 2.0 A).
     */
    public Integer getCenterSphereRadius() {

        Atom throwAway = null;

        if(this.centerSphereRadius == null) {
            // calling this function will set the centerSphereRadius variable!
            throwAway = this.getCenterAtom();
        }

        // Should have been set by now!
        if(this.centerSphereRadius == null) {
            
            Integer rad = 50;      // 5 A
            if(! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Could not determine center sphere radius of PDB residue " + this.getPdbResNum() + ", may have no atoms. Using guessed value " + rad + ".");
            }
            this.centerSphereRadius = rad;
        }


        return(this.centerSphereRadius);
    }
        /**
     * Determines the center atom of this residue, and also sets the center sphere radius for the residue.
     * @return the center atom
     */
    public Atom getCenterAtom() {

        Atom a, b, center;
        a = b = center = null;
        Integer maxDistForAtom, dist = 0; // just assign a small start value
        Integer MAXDIST = Integer.MAX_VALUE;   // just assign a *very* large start value
        Integer totalMinMaxDist = MAXDIST;
        //Integer atomRadius = Settings.getInteger("plcc_I_atom_radius");

        if(atoms.size() < 1) {
            if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("getCenterAtom(): PDB residue " + this.pdbResNum + " chain " + this.getChainID() + " of type " + residue.getName3() + " has " + atoms.size() + " atoms in default location, returning null.");
            }
            return(null);
        }

        // If this is an AA, use the CA.
        if(this.isAA()) {

            for(Integer i = 0; i < atoms.size(); i++) {
                a = atoms.get(i);
                if(a.isCalphaAtom()) {
                    center = a;
                    break;
                }
            }

            
            if(center == null) {

                // Dying may be too harsh -- maybe use the ligand version if this residue has no C alpha atom?
                //System.err.println("ERROR: Could not determine C alpha atom of PDB residue " + this.pdbResNum + ", PDB file broken.");
                //System.exit(1);

                System.out.println("WARNING: PDB residue " + this.pdbResNum + " has no C alpha atom, PDB file broken. Using 1st atom as center.");
                center = atoms.get(0);
                
            }

            // For the return value of this function alone we would be done, but we want to set the C alpha sphere
            //  radius as well. It is the maximal distance of the center atom to any other atom of this residue.
            maxDistForAtom = 0;
            for(Integer j = 0; j < this.atoms.size(); j++) {

                b = this.atoms.get(j);
                
                if(a.equalsAtom(b)) {
                   continue; 
                }
                
                dist = center.distToAtom(b);

                if(dist > maxDistForAtom) {
                    maxDistForAtom = dist;
                }

            }

            // The distance to any other atom cannot be smaller than 2 * atomRadius, otherwise the vdW radii of
            //  the atoms would overlap. If this occurs in a PDB file, something most likely is wrong with the file.
            //  Note though that this only applies to AA residues because a ligand could consist of a single atom
            //  and in that case the center sphere radius can be smaller than 2 * atomRadius.
            //if(maxDistForAtom < (2 * atomRadius)) {
            //    maxDistForAtom = 2 * atomRadius;
            //}

            // The maximal distance of the Atom we chose to any other Atom of this residue is the C alpha/center sphere radius
            this.centerSphereRadius = maxDistForAtom;

        }
        else {

            // If this is a ligand, calculate the center by using the atom with the
            //  minimal maximal distance to all other atoms of this residue.      
          
            for(Integer i = 0; i < atoms.size(); i++) {
                
                a = atoms.get(i);
                maxDistForAtom = 0;

                for(Integer j = 0; j < atoms.size(); j++) {     // we need to compare the atom to itself (distance = 0 then) if there only is a single atom in this residue (which holds for ligands like 'MG'). So j=i, not j=i+1.

                    b = atoms.get(j);
                    dist = a.distToAtom(b);

                    if(dist > maxDistForAtom) {
                        maxDistForAtom = dist;
                    }
                }

                // We determined the maximal distance of this atom to any other atom of this residue.
                // Now check whether this maxDist is smaller than the smallest current maxDist.

                if(maxDistForAtom < totalMinMaxDist) {
                    totalMinMaxDist = maxDistForAtom;

                    // Also update the current center atom. We can't break here though because
                    //  this may still get improved/overwritten during the rest of the loop.
                    center = a;
                }

            }

            // The maximal distance of the Atom we chose to any other Atom of this residue is the C alpha/center sphere radius
            centerSphereRadius = totalMinMaxDist;            

        }

        // If totalMinMaxDist still has the original value of MAXDIST something most likely is very wrong since
        //  no atoms within a single residue should have such a large distance.
        // Note though that this value is not touched for AAs since the C alpha is assumed to be the center, thus
        //  we don't compare all atoms with each other for AAs. We only calculate the distance from the CA to all others.
        if(Objects.equals(totalMinMaxDist, MAXDIST) && (! this.isAA())) {
            System.err.println("ERROR: MinMax distance of the atoms of PDB residue " + pdbResNum + " is >= " + MAXDIST + ", seems *very* unlikely.");
            System.exit(-1);
        }


        // just die if we could not determine a center atom
        if(center == null) {
                System.err.println("ERROR: Could not determine center atom of residue type " + this.getType() + " with PDB number " + pdbResNum + ", DSSP number " + dsspResNum + ".");
                System.exit(-1);
        }
        
        
        return(center);

    }
    
    /**
     * Returns the PDB atom number of the center atom of this residue.
     */
    public Integer getCenterAtomNum() {
        if(atoms.size() > 0) {
            return(getCenterAtom().getPdbAtomNum());
        }
        else {
            System.err.println("ERROR: Could not determine center atom of PDB residue " + pdbResNum + " because it has no atoms.");
            System.exit(-1);
            return(null);       // for the IDE
        }
    }
    
    
    public String getChainID() { return(chainID); }
    public Integer getType() { return(type); }
    
    /**
     * Returns the list of (non-H) atoms of this residue.
     * @return the atom list
     */
    public ArrayList<Atom> getAtoms() { return(atoms); }
    
    /**
     * Returns the list of hydrogen atoms of this residue. Only available with special command line options and PDB files!
     * @return the hydrogen atom list
     */
    public List<Atom> getHydrogenAtoms() { return hydrogenatoms; }
    
    /**
     * Returns the atom count of this Residue.
     * @return the number of atoms
     */
    public Integer getNumAtoms() { return(this.atoms.size()); }
    public String getModelID() { return(modelID); }
    public Chain getChain() { return(chain); }
    
    // setters
    public void addAtom(Atom a) { atoms.add(a); }
    public void addHydrogenAtom(Atom a) { hydrogenatoms.add(a); }
    public void setType(Integer i) { type = i; }
    public void setChain(Chain c) { chain = c; }
    public void setChainID(String s) { chainID = s; }
    public void setModelID(String s) { modelID = s; }
    public void setAtoms(ArrayList<Atom> a) { atoms = a; }
   
}
