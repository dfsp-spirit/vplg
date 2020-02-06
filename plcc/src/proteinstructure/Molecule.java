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
 *This file was written by Katja Korolew. To create the Molecule superclass, the methods and parameters 
 * were taken from the Residue class.
 * @author kk
 */
abstract public class Molecule {
    
    public static final Integer RESIDUE_TYPE_AA = 0;
    public static final Integer RESIDUE_TYPE_LIGAND = 1;
    public static final Integer RESIDUE_TYPE_OTHER = 2;
    public static final Integer RESIDUE_TYPE_RNA = 3;
    // declare class vars
    public ArrayList<Atom> atoms = null;                         // a list of all (non-H) Atoms of the molecule
    public ArrayList<Atom> hydrogenatoms = null;                         // a list of all hydrogen Atoms of the molecule
    public Chain chain = null;                             // the Chain this molecule belongs to
    public String chainID = null;
    public String modelID = null;
    public String iCode = null; 
    public Integer pdbNum = null;                       // pdb molecule number
    public Integer dsspNum = null;   
    public String Name3 = null; // guess what
    public String AAName1 = null;
    public String sseString = null;                        // initially the SSE column from the DSSP file, may be the empty string "" because not all molecules are assigned an SSE by DSSP, gets replaced by PLCC SSE string later
    public SSE sse = null;
    public String sseStringDssp = "?";
    public String plccSSEType = "N";                       // not part of any PLCC SSE by default
    public Boolean isPartOfDsspSse = false;                // whether this molecule is part of a valid SSE according to DSSP (which does NOT assign a SSE to *all* molecules)
    protected Integer centerSphereRadius = null;
    private Integer[] centroidCoords = null;                // x,y,z coordinates of residue centroid
    private Integer centroidSphereRadius = null; // distance from centroid to farthest atom (= radius of sphere around centroid encompassing all atoms)

    
    //constructor
    //public Molecule(){this.atoms = new ArrayList<>(); this.hydrogenatoms = new ArrayList<>(); this.Name3=null;}

    
    /**
     * Determines whether this Molecule has at least one Atom.
     * @return true if this Molecule has at least one Atom, false otherwise.
     */ 
    public boolean hasAtoms() {
        return(this.atoms.size() > 0);
    }
    
    //public Boolean isLigand() { return(this.type.equals(Residue.RESIDUE_TYPE_LIGAND)); }
    //public Boolean isAA() { return(this.type.equals(Residue.RESIDUE_TYPE_AA)); }
    //public Boolean isOtherRes() { return(this.type.equals(Residue.RESIDUE_TYPE_OTHER)); }
    //public Boolean isRNA() { return(this.type.equals(Residue.RESIDUE_TYPE_RNA)); }


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
     * Determines the center atom of this molecule, and also sets the center sphere radius for the molecule.
     * @return the center atom
     */
    abstract public Atom getCenterAtom();
      
        /**
     * Returns the radius of the collision sphere of this molecule. This radius doe NOT yet include the outer hull, it is the distance from the center atom to the (non-H) atom farthest away from it.
     * @return the radius, in 1/10th Angstroem (so 20 means 2.0 A).
     */
    
    protected Integer getCenterSphereRadius() {

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
     * Calculates the sphere radius of the centroid of this residue and saves it to the class var centerSphereRadius.
     */
    private void calculateCentroidSphereRadius() {
        Integer[] centroidCoordinates = this.getCentroidCoords();
        Integer curDist, maxDist;
        curDist = maxDist = 0;
        for (Atom a : this.atoms) {
            curDist = a.distToPoint(centroidCoordinates[0], centroidCoordinates[1], centroidCoordinates[2]);
            if (curDist > maxDist) { maxDist = curDist; }
        }
        this.centroidSphereRadius = maxDist;
    }
    
    private Integer getCentroidSphereRadius() {
        // if called 1st time calculate sphere radius
        if (this.centroidSphereRadius == null) {
            this.calculateCentroidSphereRadius();
        }
        return this.centroidSphereRadius;
    }
    
    
    public Integer[] getCentroidCoords () {
        // calculate if called for 1st time
        if (this.centroidCoords == null) {
            this.calculateCentroid();
        }
        return this.centroidCoords;
    }
    
    
    /**
     * Returns the sphere radius of this residue depending on the settings.
     * @return sphere radius as 10th of Angström
     */
    public Integer getSphereRadius() {
        return (Settings.getBoolean("plcc_B_centroid_method")) ? getCentroidSphereRadius() : getCenterSphereRadius();
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
     * Calculates the centroid of this residue (center of mass of all atoms) and saves it to class var centroidCoords.
     */
    private void calculateCentroid() {
        Integer[] centroid = {0,0,0};
        
        for (Atom a : this.atoms) {
            centroid[0] += a.getCoordX();
            centroid[1] += a.getCoordY();
            centroid[2] += a.getCoordZ();
        }
        
        centroid[0] = (int) (Math.round((double) centroid[0] / this.atoms.size()));
        centroid[1] = (int) (Math.round((double) centroid[1] / this.atoms.size()));
        centroid[2] = (int) (Math.round((double) centroid[2] / this.atoms.size()));
        
        this.centroidCoords = centroid;
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
    //abstract public Boolean interchainContactPossibleWithResidue (Class<M extends Molecule> molecule);
    // NOTE the body of this function has been moved to Residue. It could possibly be implemented here with some changes
    
    
    /**
     * This function determines whether we need to look at the atoms to check for contacts betweens
     * this residue and a 2nd one. If the center spheres don't overlap, there cannot exist any atom contacts.
     * @param r the other residue
     */
    abstract public Boolean contactPossibleWithResidue(Molecule r);
    // NOTE the body of this function has been moved to Residue. It could possibly be implemented here with some changes
    
    
    
    /**
     * Determines the distance to another Molecule, from center atom center atom (C alpha atom to C alpha atom for AAs)
     * @param m the other molecule
     * @return The center-to-center distance.
     */
    private Integer centerDistTo(Molecule m) {

        Atom a = this.getCenterAtom();
        Atom b = m.getCenterAtom();

        if(a == null || b == null) {
            if( ! Settings.getBoolean("plcc_B_no_parse_warn")) {
                DP.getInstance().w("Could not determine distance of PDB Residues # " + pdbNum + " and " + m.getPdbNum() + " lacking center atoms, assuming 100.");
            }
            return(100);      
        }
        else {
            //DEBUG
            //System.out.println("DEBUG: Residue C-alpha distance of " + this + " and " + m + " (C-alpha coords: " + a.getCoordString() + " / " + b.getCoordString() + ") is " + a.distToAtom(b) + ".");
            return(a.distToAtom(b));
        }
    }
    
    
    /**
     * Determines the distance to another molecule, from centroid to centroid.
     * @param m the other residue
     * @return centroid-to-centroid distance
     */
    private Integer centroidDistTo(Molecule m) {
        Atom helperCentroidAtom = new Atom();
        Integer[] thisCentroidCoordinates = this.getCentroidCoords();
        
        helperCentroidAtom.setCoordX(thisCentroidCoordinates[0]);
        helperCentroidAtom.setCoordY(thisCentroidCoordinates[1]);
        helperCentroidAtom.setCoordZ(thisCentroidCoordinates[2]);
        
        Integer[] rCentroidCoords = m.getCentroidCoords();
        
        return helperCentroidAtom.distToPoint(rCentroidCoords[0], rCentroidCoords[1], rCentroidCoords[2]);
    }
    
    
    /**
     * Determines the distance from this molecule to another, depending on settings.
     * @param m other molecule
     * @return molecule-molecule distance
     */
    public Integer distTo(Molecule m) {
        return (Settings.getBoolean("plcc_B_centroid_method") ? this.centroidDistTo(m) : this.centerDistTo(m));
    }

    // ugly abstract methods
    abstract public Integer getType();
    abstract public Boolean isAA();
    abstract public Boolean isLigand();
   
}
   

