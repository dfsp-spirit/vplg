/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteinstructure;

// imports
import proteinstructure.SSE;
import tools.DP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import plcc.Main;
import plcc.Settings;


/**
 * Represents a Residue. Based on information in the PDB and DSSP file.
 * 
 * @author ts
 */
public class Residue implements java.io.Serializable {
    
    public static final Integer RESIDUE_TYPE_AA = 0;
    public static final Integer RESIDUE_TYPE_LIGAND = 1;
    public static final Integer RESIDUE_TYPE_OTHER = 2;
    
    
    // declare class vars
    private String resName3 = null;                          // 3 letter name
    private String AAName1 = null;                          // 1 letter name, AAs only
    private Integer type = null;                            // residue type: 0=AA, 1=Ligand, 2=Other
    private ArrayList<Atom> atoms = null;                         // a list of all Atoms of the Residue
    private Integer pdbResNum = null;                       // pdb residue number
    private Integer dsspResNum = null;                      // guess what
    private Chain chain = null;                             // the Chain this Residue belongs to
    private String chainID = null;
    private String modelID = null;
    private String iCode = null;                            // PDB insertion code
    
    private Float phi = null;
    private Float psi = null;

    // see http://www.wwpdb.org/documentation/format32/sect4.html for the format of the heterogen section of PDB files
    private String ligName = null;                          // HETNAM record of PDB file (name of this hetero group)
    private String ligFormula = null;                       // FORMUL record of PDB file (chemical formula of this hetero group)
    private String ligSynonyms = null;                      // HETSYN record of PDB file (synonyms for this hetero group)

    private Integer centerSphereRadius = null;
    private String sseStringDssp = "?";
    private String plccSSEType = "N";                       // not part of any PLCC SSE by default
    private String sseString = null;                        // initially the SSE column from the DSSP file, may be the empty string "" because not all residues are assigned an SSE by DSSP, gets replaced by PLCC SSE string later
    private SSE sse = null;
    private Boolean isPartOfDsspSse = false;                // whether this Residue is part of a valid SSE according to DSSP (which does NOT assign a SSE to *all* residues)

    
    /**
     * Determines the chemical properties of this AA, according to the 5 types system.
     * @return the chemical property, which is one of the constants at AminoAcid.CHEMPROP5_AA_INT_* (Example: ALA => AminoAcid.CHEMPROP5_AA_INT_SMALL_APOLAR).
     */
    public Integer getChemicalProperty5Type() {
        if(this.isAA()) {            
            return AminoAcid.getChemProp5OfAAByName3(this.resName3);
        }
        else {
            return AminoAcid.CHEMPROP5_AA_INT_UNKNOWN;            
        }
    }
    
    /**
     * Determines the chemical properties of this AA, according to the 3 types system.
     * @return the chemical property, which is one of the constants at AminoAcid.CHEMPROP3_AA_INT_* (Example: ALA => AminoAcid.CHEMPROP3_AA_INT_HYDROPHOBIC).
     */
    public Integer getChemicalProperty3Type() {
        if(this.isAA()) {            
            return AminoAcid.getChemProp3OfAAByName3(this.resName3);
        }
        else {
            return AminoAcid.CHEMPROP3_AA_INT_UNKNOWN;            
        }
    }
    
    /**
     * Determines the chemical properties of this AA in the 5 types system and returns the string.
     * @return the chemical property string, which is one of the constants at AminoAcid.CHEMPROP5_AA_STRING_* (Example: ALA => AminoAcid.CHEMPROP5_AA_SMALL_APOLAR).
     */
    public String getChemicalProperty5OneLetterString() {
        return AminoAcid.getChemProp5_OneLetterString(AminoAcid.getChemProp5OfAAByName3(this.resName3));
    }
    
    /**
     * Determines the chemical properties of this AA in the 3 types system and returns the string.
     * @return the chemical property string, which is one of the constants at AminoAcid.CHEMPROP3_AA_STRING_* (Example: ALA => CHEMPROP3_AA_STRING_HYDROPHOBIC).
     */
    public String getChemicalProperty3OneLetterString() {
        return AminoAcid.getChemProp3_OneLetterString(AminoAcid.getChemProp3OfAAByName3(this.resName3));
    }
    
    // constructor
    public Residue() { atoms = new ArrayList<Atom>(); }

    /**
     * Constructs a new residue with PDB residue number 'prn' and DSSP residue number 'drn'.
     * @param residueNumberPDB the PDB residue number
     * @param residueNumberDSSP the DSSP residue number
     */
    public Residue(Integer residueNumberPDB, Integer residueNumberDSSP) {
        atoms = new ArrayList<Atom>();
        pdbResNum = residueNumberPDB;
        dsspResNum = residueNumberDSSP;
    }
    
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
            DP.getInstance().w("Chosen altLoc '" + chosenAltLoc + "' leads to " + numAtomsWithChosenAltLoc + " atoms for AA residue " + this.getFancyName() + ".");
        }
        
        
        
        deletedAtoms = this.deleteAtomsWithAltLocDifferentFrom(chosenAltLoc);
                
        if(this.atoms.size() < 1) {
            DP.getInstance().w("Residue " + this.getFancyName() + " of chain " + this.getChainID() + " has no atoms after choosing alternative location PDB field (had " + numAtomsBefore + " before).");
        }
        
        return deletedAtoms;
    }
    
    

    /**
     * Determines the distance to another Residue, from Residue center to Residue center (C alpha atom to C alpha atom for AAs)
     * @param r the other residue
     * @return The center-to-center distance.
     */
    public Integer resCenterDistTo(Residue r) {

        Atom a = this.getCenterAtom();
        Atom b = r.getCenterAtom();

        if(a == null || b == null) {
            if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Could not determine distance of PDB Residues # " + pdbResNum + " and " + r.getPdbResNum() + " lacking center atoms, assuming 100.");
            }
            return(100);      
        }
        else {
            //DEBUG
            //System.out.println("DEBUG: Residue C-alpha distance of " + this + " and " + r + " (C-alpha coords: " + a.getCoordString() + " / " + b.getCoordString() + ") is " + a.distToAtom(b) + ".");
            return(a.distToAtom(b));
        }
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
                DP.getInstance().w("getCenterAtom(): PDB residue " + this.pdbResNum + " chain " + this.getChainID() + " of type " + this.getName3() + " has " + atoms.size() + " atoms in default location, returning null.");
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


    /**
     * This function determines whether we need to look at the atoms to check for contacts betweens
     * this residue and a 2nd one. If the center spheres don't overlap, there cannot exist any atom contacts.
     * @param r the other residue
     */
    public Boolean contactPossibleWithResidue(Residue r) {

        Integer dist = Integer.MAX_VALUE;
        try {
            dist = this.getCenterAtom().distToAtom(r.getCenterAtom());
        } catch(Exception e) {
            if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Could not determine distance between DSSP residues " + this.getDsspResNum() + " and " + r.getDsspResNum() + ", assuming out of contact distance.");
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

        Integer justToBeSure = 4;   // Setting this to 0 shouldn't change the number of contacts found (but all harm it could do is to increase the runtime a tiny bit)
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

    
    @Override public String toString() {
        return("[Residue] PDB# " + pdbResNum + ", DSSP# " + dsspResNum + ", Type " + type + ", AA1 " + AAName1 + ", AA3 " + resName3 + ", Chain " + chainID + ", Model " + modelID + ", # of Atoms " + atoms.size());
    }

    
    /**
     * Returns a string representation of the Atoms of this Residue.
     * @return the string representation
     */ 
    public String getAtomsString() {

        Atom a;

        String s = "" + this.resName3 + "-" + this.pdbResNum + " (DSSP#" + this.dsspResNum + "): ";

        for(Integer i = 0; i < this.atoms.size(); i++) {
            a = this.atoms.get(i);
            s += a.getPdbAtomNum() + "(" + a.getAtomShortName() + ") ";
        }

        return(s);
    }


    

    // getters
    public String getName3() { return(resName3); }
    
    /** Returns the trimmed name3, to enter into DB. Otherwise we get stuff like ' NA' in the database, and searching for 'NA' fails.
     * @return  the trimmed lig name (length 1 - 3 chars)*/
    public String getTrimmedName3() { return(resName3.trim()); }
    public String getAAName1() { return(AAName1); }
    
    /** Returns the residue type: 0=AA, 1=Ligand, 2=Other. */
    public Integer getType() { return(type); }
    public Integer getPdbResNum() { return(pdbResNum); }
    public Integer getDsspResNum() { return(dsspResNum); }
    public Chain getChain() { return(chain); }
    public String getChainID() { return(chainID); }
    
    /**
     * Returns the list of atoms of this residue.
     * @return the atom list
     */
    public ArrayList<Atom> getAtoms() { return(atoms); }
    
    /**
     * Returns the atom count of this Residue.
     * @return the number of atoms
     */
    public Integer getNumAtoms() { return(this.atoms.size()); }
    public String getModelID() { return(modelID); }
    
    /**
     * Returns the PDB insertion code field of this Residue.
     * @return the PDB insertion code 
     */
    public String getiCode() { return(iCode); }
    
    /**
     * Returns the PTGL internal ID for this residue type (AA-type based).
     * @return the PTGL internal ID for this residue type
     */
    public Integer getInternalAAID() { return(AminoAcid.name3ToID(resName3)); }
    
    /**
     * Returns a string in pattern 'resName3 + '-' + pdbResNum', e.g., 'ARG-47'. Note that this
     * does note include a reference to the chain (or insertion code) and thus is NOT unique for the
     * PDB file. Use getUniquePDBName() instead if you need a unique name based on the PDB residue number.
     * @return a residue string like 'ARG-47'
     */
    public String getFancyName() { return(this.resName3 + "-" + this.pdbResNum); }
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
     * Returns a string in format 'chainID + '-' + pdbResNum + '-' + iCode'. Note that the iCode may
     * be empty (it is for most residues). Example: 'A-45-'.
     * @return a string like 'A-45-'.
     */
    public String getUniquePDBName() { return(chainID + "-" + pdbResNum + "-" + iCode); }
    
    /**
     * Returns a string in format '(chainID + '-' + pdbResNum + '-' + iCode)'. Note that the iCode may
     * be empty (it is for most residues). Example: '(A-45-)'.
     * @return a string like '(A-45-)'.
     */
    public String getUniqueString() { return("(" + chainID + "-" + pdbResNum + "-" + iCode + ")"); }
    public Float getPhi() { if(this.isAA()) { return(phi); } else { return(0.0f); } }
    public Float getPsi() { if(this.isAA()) { return(psi); } else { return(0.0f); } }
    public String getResName3() { return resName3;}

    public String getLigName() {
        if(this.isLigand()) {
            return(this.ligName);
        }
        else {
            System.out.println("WARNING: getLigName() Called for non-ligand residue '" + this.getFancyName() + "'.");
            return("NOT_A_LIGAND");
        }
    }

    public String getLigFormula() {
        if(this.isLigand()) {
            return(this.ligFormula);
        }
        else {
            System.err.println("WARNING: getLigFormula() Called for non-ligand residue '" + this.getFancyName() + "'.");
            return("NOT_A_LIGAND_SO_NO_HET_FORMULA");
        }
    }

    public String getLigSynonyms() {
        if(this.isLigand()) {
            return(this.ligSynonyms);
        }
        else {
            System.err.println("WARNING: getLigSynonyms() Called for non-ligand residue '" + this.getFancyName() + "'.");
            return("NOT_A_LIGAND_SO_NO_HET_SYNONYMS");
        }
    }

    // setters
    public void addAtom(Atom a) { atoms.add(a); }
    public void setResName3(String s) { resName3 = s; }
    public void setAAName1(String s) { AAName1 = s; }
    public void setType(Integer i) { type = i; }
    public void setPdbResNum(Integer i) { pdbResNum = i; }
    public void setDsspResNum(Integer i) { dsspResNum = i; }
    public void setChain(Chain c) { chain = c; }
    public void setChainID(String s) { chainID = s; }
    public void setModelID(String s) { modelID = s; }
    public void setAtoms(ArrayList<Atom> a) { atoms = a; }
    public void setSSEString(String s) { sseString = s; }
    public void setSSEStringDssp(String s) { sseStringDssp = s; }
    public void setSSETypePlcc(String s) { plccSSEType = s; }
    public void setSSE(SSE s) { sse = s; }
    public void setDsspSseState(Boolean b) { isPartOfDsspSse = b; }
    public void setLigName(String s) { ligName = s; }
    public void setiCode(String s) { iCode = s; }
    public void setLigFormula(String s) { ligFormula = s; }
    public void setLigSynonyms(String s) { ligSynonyms = s; }
    public void setPhi(Float f) { phi = f; }
    public void setPsi(Float f) { psi = f; }
    
    
    /**
     * Returns information on all atoms of this residue (used for debugging only).
     * @return a multi-line String which contains info on all atoms of this residue
     */
    public String atomInfo() {
        String info = "    *Residue DSSP# " + this.getDsspResNum() + " has " + this.getAtoms().size() + " (non-ignored) atoms:\n";
        Integer num = 0;
        for(Atom a : this.getAtoms()) {
            num++;
            info += "     #" + num + ":" + a.toString() + "\n";
        }        
        return(info);        
    }
}