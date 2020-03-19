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
import java.util.List;
import java.util.Objects;
import plcc.Main;
import plcc.Settings;


/**
 * Represents a Residue. Based on information in the PDB and DSSP file.
 * 
 * @author ts
 */
public class Residue extends Molecule implements java.io.Serializable {
    
    public static final Integer RESIDUE_TYPE_AA = 0;
    public static final Integer RESIDUE_TYPE_LIGAND = 1;
    public static final Integer RESIDUE_TYPE_OTHER = 2;
    
    
    // declare class vars
                             // 3 letter name
                              // 1 letter name, AAs only
    private Integer type = null;                            // residue type: 0=AA, 1=Ligand, 2=Other
       // PDB insertion code
    
    /** The binding sites, if any, that this residue is part of (for protein residues which are part of the pocket, NOT for ligands which dock into a pocket).  */
    private List<BindingSite> partOfBindingSites;
    
    
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

    public List<BindingSite> getPartOfBindingSites() {
        return partOfBindingSites;
    }

    public void setPartOfBindingSites(List<BindingSite> partOfBindingSites) {
        this.partOfBindingSites = partOfBindingSites;
    }
    
    /** Assigns a new binding site the residue is part of.
     * @param s the binding site the residue s part of
     */
    public void addPartOfBindingSite(BindingSite s) {
        this.partOfBindingSites.add(s);
    }
    
    private Float phi = null;       // phi backbone angle
    private Float psi = null;       // psi backbone angle
    private Integer acc = null;     // solvent accessible surface, from DSSP file

    // see http://www.wwpdb.org/documentation/format32/sect4.html for the format of the heterogen section of PDB files
    private String ligName = null;                          // HETNAM record of PDB file (name of this hetero group)
    private String ligFormula = null;                       // FORMUL record of PDB file (chemical formula of this hetero group)
    private String ligSynonyms = null;                      // HETSYN record of PDB file (synonyms for this hetero group)

    
    /**
     * Determines the chemical properties of this AA, according to the 5 types system.
     * @return the chemical property, which is one of the constants at AminoAcid.CHEMPROP5_AA_INT_* (Example: ALA => AminoAcid.CHEMPROP5_AA_INT_SMALL_APOLAR).
     */
    public Integer getChemicalProperty5Type() {
        if(this.isAA()) {            
            return AminoAcid.getChemProp5OfAAByName3(this.Name3);
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
            return AminoAcid.getChemProp3OfAAByName3(this.Name3);
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
        return AminoAcid.getChemProp5_OneLetterString(AminoAcid.getChemProp5OfAAByName3(this.Name3));
    }
    
    /**
     * Determines the chemical properties of this AA in the 3 types system and returns the string.
     * @return the chemical property string, which is one of the constants at AminoAcid.CHEMPROP3_AA_STRING_* (Example: ALA => CHEMPROP3_AA_STRING_HYDROPHOBIC).
     */
    public String getChemicalProperty3OneLetterString() {
        return AminoAcid.getChemProp3_OneLetterString(AminoAcid.getChemProp3OfAAByName3(this.Name3));
    }
    
    // constructor
    public Residue() { this.atoms = new ArrayList<>(); this.hydrogenatoms = new ArrayList<>(); this.partOfBindingSites = new ArrayList<>(); }

    public Residue(Molecule Mol) {
        this.atoms = Mol.getAtoms();
        this.chain = Mol.getChain();
        this.chainID = Mol.getChainID();
        this.modelID = Mol.getModelID();
        this.iCode = Mol.getiCode();
        this.pdbNum = Mol.getPdbNum();
        this.dsspNum = Mol.getDsspNum();
        this.Name3 = Mol.getName3();
        this.AAName1 = Mol.getAAName1();
        this.sseString = Mol.getSSEString();
        this.sse = Mol.getSSE();
        this.sseStringDssp = Mol.getSSEStringDssp();
        this.isPartOfDsspSse = Mol.getDsspSseState();
    }
    
    /**
     * Constructs a new residue with PDB residue number 'prn' and DSSP residue number 'drn'.
     * @param residueNumberPDB the PDB residue number
     * @param residueNumberDSSP the DSSP residue number
     */
    public Residue(Integer residueNumberPDB, Integer residueNumberDSSP) {
        atoms = new ArrayList<>();
        hydrogenatoms = new ArrayList<>();
        pdbNum = residueNumberPDB;
        dsspNum = residueNumberDSSP;
        this.partOfBindingSites = new ArrayList<>();
    }
    
    
    /**
     * This function determines whether we need to look at the atoms to check for interchain contacts between this residue and a second one.
     * If the center spheres don't overlap, there cannot exist any atom contacts. Also if both residues are from the same chain, there cannot
     * exist any interchain atom contacts.
     * @param r the other residue
     * @return True if contact is possible, false otherwise.
     */
    public Boolean interchainContactPossibleWithResidue (Residue r) {
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
//    public Boolean contactPossibleWithResidue(Molecule m) {
//        // ugly code: instead of instanceof some generic methods should be used
//        if (m instanceof Residue) {
//            Residue r;
//            r = (Residue) m;
//            
//            Integer dist = Integer.MAX_VALUE;
//            try {
//                dist = this.getCenterAtom().distToAtom(r.getCenterAtom());
//            } catch(Exception e) {
//                if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
//                    DP.getInstance().w("Could not determine distance between DSSP residues " + this.getDsspNum() + " and " + r.getDsspNum() + ", assuming out of contact distance.");
//                }
//                return(false);
//            }
//            Integer atomRadius;
//            if(this.isLigand() || r.isLigand()) {
//                atomRadius = Settings.getInteger("plcc_I_lig_atom_radius");
//            }
//            else {
//                atomRadius = Settings.getInteger("plcc_I_atom_radius");
//            }
//
//            Integer justToBeSure = 4;   // Setting this to 0 shouldn't change the number of contacts found (but all harm it could do is to increase the runtime a tiny bit). Verified: has no influence. Should be removed in future release.
//            Integer maxDistForContact = this.getCenterSphereRadius() + r.getCenterSphereRadius() +  (atomRadius * 2) + justToBeSure;
//
//            //System.out.println("    Center sphere radius for PDB residue " + this.getPdbResNum() + " = " + this.getCenterSphereRadius() + ", for " + r.getPdbResNum() + " = " + r.getCenterSphereRadius() + ", atom radius is " + atomRadius + ".");
//            //System.out.println("    DSSP Res distance " + this.getDsspResNum() + "/" + r.getDsspResNum() + " is " + dist + " (no contacts possible above distance " + maxDistForContact + ").");
//
//            if(dist > (maxDistForContact)) {
//                return(false);
//            }
//            else {
//                return(true);
//            }
//        } else {
//            DP.getInstance().w("Tried to get distance between Molecules. Due to ugly code this is not possible atm.");
//        }
//
//        // if one of them is Molecule or RNA
//        return false;
//    }
//    
    
    /**
     * Determines whether this residue is part of any binding site.
     * @return whether this residue is part of any binding site
     */
    public Boolean isBindingSiteResidue() {
        return(this.partOfBindingSites.size() > 0);
    }
    
    public Boolean isLigand() { return(this.type.equals(Residue.RESIDUE_TYPE_LIGAND)); }
    public Boolean isAA() { return(this.type.equals(Residue.RESIDUE_TYPE_AA)); }
    public Boolean isOtherRes() { return(this.type.equals(Residue.RESIDUE_TYPE_OTHER)); }
    

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
    
    
    @Override public String toString() {
        return("[Residue] PDB# " + pdbNum + ", DSSP# " + dsspNum + ", Type " + type + ", AA1 " + AAName1 + ", AA3 " + Name3 + ", Chain " + chainID + ", Model " + modelID + ", # of Atoms " + atoms.size());
    }

    public Float getPhi() { if(this.isAA()) { return(phi); } else { return(0.0f); } }
    public Float getPsi() { if(this.isAA()) { return(psi); } else { return(0.0f); } }
    public Integer getAcc() { if(this.isAA()) { return(acc); } else { return(0); } }
    

 
    /**
     * Determines whether this residue lies at the protein surface.
     * @return whether this residue lies at the protein surface.
     */
    public Boolean isSurfaceResidue() {
        if(this.acc == null) {
            return true;    // if we do not have SAS data, assume yes
        }
        return(this.acc >= 15);
    }
    
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
    
    public Integer getType() {return this.type;}

    
    //setters
    public void setLigName(String s) { ligName = s; }
    public void setiCode(String s) { iCode = s; }
    public void setLigFormula(String s) { ligFormula = s; }
    public void setLigSynonyms(String s) { ligSynonyms = s; }
    public void setPhi(Float f) { phi = f; }
    public void setPsi(Float f) { psi = f; }
    public void setAcc(Integer f) { acc = f; }   
    public void setType(Integer i) { type = i;}
    
}
    
    
