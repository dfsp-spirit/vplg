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
    private String resName3 = null;                          // 3 letter name
    private String AAName1 = null;                          // 1 letter name, AAs only
    private Integer type = null;                            // residue type: 0=AA, 1=Ligand, 2=Other
    private ArrayList<Atom> atoms = null;                         // a list of all (non-H) Atoms of the Residue
    private ArrayList<Atom> hydrogenatoms = null;                         // a list of all hydrogen Atoms of the Residue
    private Integer pdbResNum = null;                       // pdb residue number
    private Integer dsspResNum = null;                      // guess what
    private Chain chain = null;                             // the Chain this Residue belongs to
    private String chainID = null;
    private String modelID = null;
    private String iCode = null;    // PDB insertion code
    
    /** The binding sites, if any, that this residue is part of (for protein residues which are part of the pocket, NOT for ligands which dock into a pocket).  */
    private List<BindingSite> partOfBindingSites;

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
    public Residue() { this.atoms = new ArrayList<>(); this.hydrogenatoms = new ArrayList<>(); this.partOfBindingSites = new ArrayList<>(); }

    /**
     * Constructs a new residue with PDB residue number 'prn' and DSSP residue number 'drn'.
     * @param residueNumberPDB the PDB residue number
     * @param residueNumberDSSP the DSSP residue number
     */
    public Residue(Integer residueNumberPDB, Integer residueNumberDSSP) {
        atoms = new ArrayList<>();
        hydrogenatoms = new ArrayList<>();
        pdbResNum = residueNumberPDB;
        dsspResNum = residueNumberDSSP;
        this.partOfBindingSites = new ArrayList<>();
    }
    
    
    /**
     * Determines whether this residue is part of any binding site.
     * @return whether this residue is part of any binding site
     */
    public Boolean isBindingSiteResidue() {
        return(this.partOfBindingSites.size() > 0);
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
                DP.getInstance().w("Could not determine distance between DSSP residues " + this.getDsspResNum() + " and " + r.getDsspResNum() + ", assuming out of contact distance.");
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

    
    
    public void setResName3(String s) { resName3 = s; }
    public void setAAName1(String s) { AAName1 = s; }
    public void setPdbResNum(Integer i) { pdbResNum = i; }
    public void setDsspResNum(Integer i) { dsspResNum = i; }
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
    public void setAcc(Integer f) { acc = f; }
    
    
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