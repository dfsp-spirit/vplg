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
    public Integer type = null;                            // residue type: 0=AA, 1=Ligand, 2=Other
    public ArrayList<Atom> atoms = null;                         // a list of all (non-H) Atoms of the molecule
    public ArrayList<Atom> hydrogenatoms = null;                         // a list of all hydrogen Atoms of the molecule
    public Chain chain = null;                             // the Chain this molecule belongs to
    public String chainID = null;
    public String modelID = null;
    public String iCode = null; 
    public Integer centerSphereRadius = null;
    public Integer pdbNum = null;                       // pdb molecule number
    public Integer dsspNum = null;   
    public String Name3 = null; // guess what
    public String AAName1 = null;
    public String sseString = null;                        // initially the SSE column from the DSSP file, may be the empty string "" because not all molecules are assigned an SSE by DSSP, gets replaced by PLCC SSE string later
    public SSE sse = null;
    public String sseStringDssp = "?";
    public String plccSSEType = "N";                       // not part of any PLCC SSE by default
    public Boolean isPartOfDsspSse = false;                // whether this molecule is part of a valid SSE according to DSSP (which does NOT assign a SSE to *all* molecules)

    
    //constructor
    //public Molecule(){this.atoms = new ArrayList<>(); this.hydrogenatoms = new ArrayList<>(); this.Name3=null;}

   
     /**
     * Returns the C alpha atoms of this molecule or null if it has none.
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
     * Determines whether this Molecule has at least one Atom.
     * @return true if this Molecule has at least one Atom, false otherwise.
     */ 
    public boolean hasAtoms() {
        return(this.atoms.size() > 0);
    }
    
    public Boolean isLigand() { return(this.type.equals(Residue.RESIDUE_TYPE_LIGAND)); }
    public Boolean isAA() { return(this.type.equals(Residue.RESIDUE_TYPE_AA)); }
    public Boolean isOtherRes() { return(this.type.equals(Residue.RESIDUE_TYPE_OTHER)); }
    public Boolean isRNA() { return(this.type.equals(Residue.RESIDUE_TYPE_RNA)); }


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
    protected ArrayList<Atom> deleteAtomsWithAltLocDifferentFrom(String keepAltLoc) {
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
     * Determines all AltLoc identifiers which have an alpha carbon atom assigned to them for this residue.
     * @return the list of all altLocs which include alpha carbons
     */ 
    protected ArrayList<String> getAltLocsWithAlphaCarbonAtoms() {
        
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
     * Tells this molecule to choose its alternate location PDB identifier and delete all its
     * atoms which have other AltLocs. For amino acids, this will choose the AltLoc which has a C-alpha atom
     * and maximizes the number of atoms in the molecule. For non-AAs, this function will always choose the AltLoc identifier which
     * maximizes the number of atoms in this molecule. The list of deleted atoms is returned so they can be deleted
     * from the global atom list as well.
     * @return the list of the atoms that were deleted
     */
    public ArrayList<Atom> chooseYourAltLoc() {
    
        int numAtomsBefore = this.atoms.size();
        if(numAtomsBefore < 1) {
            if(! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Molecule " + this.getFancyName() + " of chain " + this.getChainID() + " has NO atoms at all *before* choosing alternative location PDB field and deleting others.");
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
                DP.getInstance().w("Chosen altLoc '" + chosenAltLoc + "' leads to " + numAtomsWithChosenAltLoc + " atoms for AA molecule " + this.getFancyName() + ".");
            }
        }
   
        
        
        deletedAtoms = this.deleteAtomsWithAltLocDifferentFrom(chosenAltLoc);
                
        if(this.atoms.size() < 1) {
            DP.getInstance().w("Molecule  " + this.getFancyName() + " of chain " + this.getChainID() + " has no atoms after choosing alternative location PDB field (had " + numAtomsBefore + " before).");
        }
        
        return deletedAtoms;
    }
    
        /**
     * Returns the alternate location identifier that most atoms of this molecule share.
     * @return the most common alternate location identifier of this molecule. If this molecule
     * has no atoms, a space (" ", the PDB default altLoc) will be returned.
     */
       public String getAltLocWithAlphaCarbonAndMostAtoms() {
        
        ArrayList<String> altLocsCA = this.getAltLocsWithAlphaCarbonAtoms();
        if(altLocsCA.size() < 1) {
            DP.getInstance().w("Molecule  " + this.getFancyName() + " has no AltLoc which includes an alpha carbon atom.");
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
     * Determines the center atom of this molecule, and also sets the center sphere radius for the molecule.
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
                 DP.getInstance().w("getCenterAtom(): PDB molecule  " + this.pdbNum + " chain " + this.getChainID() + " of type " + getName3() + " has " + atoms.size() + " atoms in default location, returning null.");
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

                 // Dying may be too harsh -- maybe use the ligand version if this molecule has no C alpha atom?
                 //System.err.println("ERROR: Could not determine C alpha atom of PDB molecule " + this.pdbResNum + ", PDB file broken.");
                 //System.exit(1);

                 System.out.println("WARNING: PDB molecule  " + this.pdbNum + " has no C alpha atom, PDB file broken. Using 1st atom as center.");
                 center = atoms.get(0);
                 
             }

             // For the return value of this function alone we would be done, but we want to set the C alpha sphere
             //  radius as well. It is the maximal distance of the center atom to any other atom of this molecule.
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
             //  Note though that this only applies to AA molecules because a ligand could consist of a single atom
             //  and in that case the center sphere radius can be smaller than 2 * atomRadius.
             //if(maxDistForAtom < (2 * atomRadius)) {
             //    maxDistForAtom = 2 * atomRadius;
             //}

             // The maximal distance of the Atom we chose to any other Atom of this molecule is the C alpha/center sphere radius
             this.centerSphereRadius = maxDistForAtom;

         }
         else {

             // If this is a ligand, calculate the center by using the atom with the
             //  minimal maximal distance to all other atoms of this molecule.      
           
             for(Integer i = 0; i < atoms.size(); i++) {
                 
                 a = atoms.get(i);
                 maxDistForAtom = 0;

                 for(Integer j = 0; j < atoms.size(); j++) {     // we need to compare the atom to itself (distance = 0 then) if there only is a single atom in this molecule (which holds for ligands like 'MG'). So j=i, not j=i+1.

                     b = atoms.get(j);
                     dist = a.distToAtom(b);

                     if(dist > maxDistForAtom) {
                         maxDistForAtom = dist;
                     }
                 }

                 // We determined the maximal distance of this atom to any other atom of this molecule.
                 // Now check whether this maxDist is smaller than the smallest current maxDist.

                 if(maxDistForAtom < totalMinMaxDist) {
                     totalMinMaxDist = maxDistForAtom;

                     // Also update the current center atom. We can't break here though because
                     //  this may still get improved/overwritten during the rest of the loop.
                     center = a;
                 }

             }

             // The maximal distance of the Atom we chose to any other Atom of this molecule is the C alpha/center sphere radius
             centerSphereRadius = totalMinMaxDist;            

         }

         // If totalMinMaxDist still has the original value of MAXDIST something most likely is very wrong since
         //  no atoms within a single molecule should have such a large distance.
         // Note though that this value is not touched for AAs since the C alpha is assumed to be the center, thus
         //  we don't compare all atoms with each other for AAs. We only calculate the distance from the CA to all others.
         if(Objects.equals(totalMinMaxDist, MAXDIST) && (! this.isAA())) {
             System.err.println("ERROR: MinMax distance of the atoms of PDB molecule  " + pdbNum + " is >= " + MAXDIST + ", seems *very* unlikely.");
             System.exit(-1);
         }


         // just die if we could not determine a center atom
         if(center == null) {
                 System.err.println("ERROR: Could not determine center atom of molecule  type " + this.getType() + " with PDB number " + pdbNum + ", DSSP number " + dsspNum + ".");
                 System.exit(-1);
         }
         
         
         return(center);

     
    }
      
        /**
     * Returns the radius of the collision sphere of this molecule. This radius doe NOT yet include the outer hull, it is the distance from the center atom to the (non-H) atom farthest away from it.
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
                DP.getInstance().w("Could not determine center sphere radius of PDB molecule " + this.getPdbNum() + ", may have no atoms. Using guessed value " + rad + ".");
            }
            this.centerSphereRadius = rad;
        }


        return(this.centerSphereRadius);
    }
    
    /**
     * Returns a string representation of the Atoms of this molecule.
     * @return the string representation
     */ 
    public String getAtomsString() {

        Atom a;

        String s = "" + this.Name3 + "-" + this.pdbNum + " (DSSP#" + this.dsspNum + "): ";

        for(Integer i = 0; i < this.atoms.size(); i++) {
            a = this.atoms.get(i);
            s += a.getPdbAtomNum() + "(" + a.getAtomShortName() + ") ";
        }

        return(s);
    }


    

    // getters
    public String getName3() { return Name3; }
//    public String getResName3() {return resName;}
    
    /** Returns the trimmed name3, to enter into DB. Otherwise we get stuff like ' NA' in the database, and searching for 'NA' fails.
     * @return  the trimmed lig name (length 1 - 3 chars)*/
    public String getTrimmedName3() { return(Name3.trim()); }
    public String getAAName1() { return(AAName1); }
    
    /** Returns the residue type: 0=AA, 1=Ligand, 2=Other. */
    public Integer getType() { return(type); }
    public Integer getPdbNum() { return(pdbNum); }
    public Integer getDsspNum() { return(dsspNum); }
    
    
    
  
    
    /**
     * Returns the PDB insertion code field of this Molecule.
     * @return the PDB insertion code 
     */
    public String getiCode() { return(iCode); }
    
    /**
     * Returns the PTGL internal ID for this residue type (AA-type based).
     * @return the PTGL internal ID for this residue type
     */
    public Integer getInternalAAID() { return(AminoAcid.name3ToID(Name3)); }
    
    /**
     * Returns a string in pattern 'resName3 + '-' + pdbResNum', e.g., 'ARG-47'. Note that this
     * does note include a reference to the chain (or insertion code) and thus is NOT unique for the
     * PDB file. Use getUniquePDBName() instead if you need a unique name based on the PDB molecule number.
     * @return a molecule string like 'ARG-47'
     */
    public String getFancyName() { return(this.Name3 + "-" + this.pdbNum); }

    /**
     * Returns a string in format 'chainID + '-' + pdbResNum + '-' + iCode'. Note that the iCode may
     * be empty (it is for most molecules). Example: 'A-45-'.
     * @return a string like 'A-45-'.
     */
    public String getUniquePDBName() { return(chainID + "-" + pdbNum + "-" + iCode); }
    
    /**
     * Returns a string in format '(chainID + '-' + pdbResNum + '-' + iCode)'. Note that the iCode may
     * be empty (it is for most molecules). Example: '(A-45-)'.
     * @return a string like '(A-45-)'.
     */
    public String getUniqueString() { return("(" + chainID + "-" + pdbNum + "-" + iCode + ")"); }
    
    
    public String getChainID() { return(chainID); }
    
    /**
     * Returns the list of (non-H) atoms of this molecule.
     * @return the atom list
     */
    public ArrayList<Atom> getAtoms() { return(atoms); }
    
    /**
     * Returns the list of hydrogen atoms of this molecule. Only available with special command line options and PDB files!
     * @return the hydrogen atom list
     */
    public List<Atom> getHydrogenAtoms() { return hydrogenatoms; }
    
    /**
     * Returns the atom count of this Molecule.
     * @return the number of atoms
     */
    public Integer getNumAtoms() { return(this.atoms.size()); }
    public String getModelID() { return(modelID); }
    public Chain getChain() { return(chain); }
    
     /**
     * Returns the PLCC SSE string of this SSE. May be blank/ a single space ' '.
     * @return the PLCC SSE string of this SSE, e.g., "H", "E", " " or "L"
     */
 
    public String getSSEString() { return(sseString); }
    /**
     * Returns the PLCC SSE string of this SSE, but uses "C" (instead of space " ") for empty SSE strings.
     * @return the PLCC SSE string of this SSE, e.g., "H", "E", "C", or "L". C is for coil/none.
     */
    public String getNonEmptySSEString() { 
        return((this.sseString.isEmpty() || this.sseString.equals(" ")) ? "C" : this.sseString); 
    }
    
    public String getSSEStringDssp() { return(sseStringDssp); }
    public String getSSETypePlcc() { return(this.plccSSEType); }
    public SSE getSSE() { return(sse); }
    public Boolean getDsspSseState() { return(isPartOfDsspSse); }
    
    /**
     * Return true, if this is type is a protein and false if this is a type of rna.
     * @return boolean 
     */
    public boolean getMolType(Integer type){
        if(type == 2){
            return false;
        }
        
        return true;
    }
    
    // setters
    public void addAtom(Atom a) { atoms.add(a); }
    public void addHydrogenAtom(Atom a) { hydrogenatoms.add(a); }
    public void setType(Integer i) { type = i; }
    public void setChain(Chain c) { chain = c; }
    public void setChainID(String s) { chainID = s; }
    public void setModelID(String s) { modelID = s; }
    public void setAtoms(ArrayList<Atom> a) { atoms = a; }
    public void setSSEString(String s) { sseString = s; }
    public void setSSEStringDssp(String s) { sseStringDssp = s; }
    public void setSSETypePlcc(String s) { plccSSEType = s; }
    public void setSSE(SSE s) { sse = s; }
    public void setDsspSseState(Boolean b) { isPartOfDsspSse = b; }
    public void setName3(String s) { Name3 = s; }
    public void setAAName1(String s) { AAName1 = s; }
    public void setPdbNum(Integer i) { pdbNum = i; }
    public void setDsspNum(Integer i) { dsspNum = i; }
    
    /**
     * Returns information on all atoms of this molecule (used for debugging only).
     * @return a multi-line String which contains info on all atoms of this molecule
     */
    public String atomInfo() {
        String info = "    *Molecule DSSP# " + this.getDsspNum() + " has " + this.getAtoms().size() + " (non-ignored) atoms:\n";
        Integer num = 0;
        for(Atom a : this.getAtoms()) {
            num++;
            info += "     #" + num + ":" + a.toString() + "\n";
        }        
        return(info);        
    }
    
        /**
     * Returns the PDB atom number of the center atom of this molecule.
     */
    public Integer getCenterAtomNum() {
        if(atoms.size() > 0) {
            return(getCenterAtom().getPdbAtomNum());
        }
        else {
            System.err.println("ERROR: Could not determine center atom of PDB molecule " + pdbNum + " because it has no atoms.");
            System.exit(-1);
            return(null);       // for the IDE
        }
    }
    
    /**
     * This function determines whether we need to look at the atoms to check for interchain contacts between this residue and a second one.
     * If the center spheres don't overlap, there cannot exist any atom contacts. Also if both residues are from the same chain, there cannot
     * exist any interchain atom contacts.
     * @param r the other residue
     * @return True if contact is possible, false otherwise.
     */
    public Boolean interchainContactPossibleWithResidue (Molecule r) {
        Integer dist = Integer.MAX_VALUE;
        try {
            dist = this.getCenterAtom().distToAtom(r.getCenterAtom());
        } catch(Exception e) {
            if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Could not determine distance between DSSP residues " + this.getDsspNum() + " and " + r.getDsspNum() + ", assuming out of contact distance.");
            }
            return(false);
        }
        Integer atomRadius;
        if(this.isLigand() || r.isLigand()) {
            atomRadius = Settings.getInteger("plcc_I_lig_atom_radius");
        }
        else {
            atomRadius = 40; //Settings.getInteger("plcc_I_atom_radius");
        }

        Integer maxDistForContact = this.getCenterSphereRadius() + r.getCenterSphereRadius() +  (atomRadius * 2);

        //System.out.println("    Center sphere radius for PDB residue " + this.getPdbResNum() + " = " + this.getCenterSphereRadius() + ", for " + r.getPdbResNum() + " = " + r.getCenterSphereRadius() + ", atom radius is " + atomRadius + ".");
        //System.out.println("    DSSP Res distance " + this.getDsspResNum() + "/" + r.getDsspResNum() + " is " + dist + " (no contacts possible above distance " + maxDistForContact + ").");

        if(dist > (maxDistForContact) || ((this.isAA() || r.isAA()) && (this.getChainID().equals(r.getChainID())))) {
            return(false);
        }
        else {
            return(true);
        }
    }
    
    /**
     * This function determines whether we need to look at the atoms to check for contacts betweens
     * this residue and a 2nd one. If the center spheres don't overlap, there cannot exist any atom contacts.
     * @param r the other residue
     */
    public Boolean contactPossibleWithResidue(Molecule r) {

        Integer dist = Integer.MAX_VALUE;
        try {
            dist = this.getCenterAtom().distToAtom(r.getCenterAtom());
        } catch(Exception e) {
            if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Could not determine distance between DSSP residues " + this.getDsspNum() + " and " + r.getDsspNum() + ", assuming out of contact distance.");
            }
            return(false);
        }
        Integer atomRadius;
        if(this.isLigand() || r.isLigand()) {
            atomRadius = Settings.getInteger("plcc_I_lig_atom_radius");
        }
        else {
            atomRadius = Settings.getInteger("plcc_I_atom_radius");
        }

        Integer justToBeSure = 4;   // Setting this to 0 shouldn't change the number of contacts found (but all harm it could do is to increase the runtime a tiny bit). Verified: has no influence. Should be removed in future release.
        Integer maxDistForContact = this.getCenterSphereRadius() + r.getCenterSphereRadius() +  (atomRadius * 2) + justToBeSure;

        //System.out.println("    Center sphere radius for PDB residue " + this.getPdbResNum() + " = " + this.getCenterSphereRadius() + ", for " + r.getPdbResNum() + " = " + r.getCenterSphereRadius() + ", atom radius is " + atomRadius + ".");
        //System.out.println("    DSSP Res distance " + this.getDsspResNum() + "/" + r.getDsspResNum() + " is " + dist + " (no contacts possible above distance " + maxDistForContact + ").");

        if(dist > (maxDistForContact)) {
            return(false);
        }
        else {
            return(true);
        }
    }
    
    /**
     * Determines the distance to another Residue, from Residue center to Residue center (C alpha atom to C alpha atom for AAs)
     * @param r the other residue
     * @return The center-to-center distance.
     */
    public Integer resCenterDistTo(Molecule r) {

        Atom a = this.getCenterAtom();
        Atom b = r.getCenterAtom();

        if(a == null || b == null) {
            if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Could not determine distance of PDB Residues # " + pdbNum + " and " + r.getPdbNum() + " lacking center atoms, assuming 100.");
            }
            return(100);      
        }
        else {
            //DEBUG
            //System.out.println("DEBUG: Residue C-alpha distance of " + this + " and " + r + " (C-alpha coords: " + a.getCoordString() + " / " + b.getCoordString() + ") is " + a.distToAtom(b) + ".");
            return(a.distToAtom(b));
        }
    }
   
}
   

