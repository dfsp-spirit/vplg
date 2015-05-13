/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteinstructure;

// imports

import plcc.Position3D;
import plcc.Settings;


/**
 * Represents an Atom, based on information in a PDB file.
 * 
 * @author ts
 */
public class Atom implements java.io.Serializable {

    // declare class vars
    private String name = null;                 // atom name from PDB file, spaces included (they are important and differentiate atoms, e.g. 'CA  '= C alpha from ' CA '=calcium)
    private String chemSym = null;              // chemical symbol of the atom (periodic table notation, extracted from pdb file)
    private String chainID = null;
    private String modelID = null;
    private Model model = null;
    private Integer pdbAtomNumber = null;       // atom number from pdb file
    private Residue residue = null;             // Residue this Atom belongs to
    private Integer type = null;               // atom type:  0=AA, 1=Ligand, 2=Ignored HETATM (e.g. 'DOD'-residue atoms) 3=Ignored ATOM (e.g. H, Q)
    private Integer pdbResNum = null;
    private Integer dsspResNum = null;
    private Integer coordX = null;              // 3D coordinate X from pdb file, converted to 10th part Angstroem
    private Integer coordY = null;
    private Integer coordZ = null;
    private Integer pdbLineNum = null;
    private Chain chain = null;
    private String altLoc = null;
    
    public static final Integer ATOMTYPE_AA = 0;
    public static final Integer ATOMTYPE_LIGAND = 1;
    public static final Integer ATOMTYPE_IGNORED_LIGAND = 2;
    public static final Integer ATOMTYPE_IGNORED_ATOM = 3;

    /**
     * Getter for PDB alternate location identifier.
     * @return the altLoc string
     */
    public String getAltLoc() {
        return altLoc;
    }

    /**
     * Sets the alternate location identifier (from PDB file).
     * @param altLoc the altLoc string (one character, usually " ")
     */
    public void setAltLoc(String altLoc) {
        this.altLoc = altLoc;
    }

    public Boolean isLigandAtom() { return(residue.getType() == 1); }
    public Boolean isProteinAtom() { return(residue.getType() == 0); }
    public Boolean isOtherAtom() { return(residue.getType() == 2); }

    
    
    /**
     * Returns the distance from this atom to atom 'a'. It uses the atom centers to calculate
     * the distance, so you have to take care of the collision sphere size yourself.
     * @param a the other Atom
     * @return the euclidian distance, rounded to an Integer
     */
    public Integer distToAtom(Atom a) {
        Double dd = 0.0;
        Integer di, dx, dy, dz = 0;

        dx = this.getCoordX() - a.getCoordX();
        dd = dd + dx * dx;
        dy = this.getCoordY() - a.getCoordY();
        dd = dd + dy * dy;
        dz = this.getCoordZ() - a.getCoordZ();
        dd = dd + dz * dz;

        di = (int)Math.sqrt(dd);
        
        if(Settings.getBoolean("plcc_B_contact_debug_dysfunct")) {
            if(this.isCalphaAtom() && a.isCalphaAtom()) {
                System.out.println("Distance between C-alpha atoms " + this.pdbAtomNumber + " of " + this.getPdbResNum() + " and " + a.pdbAtomNumber + " of " + a.getPdbResNum() + " is " + di + " (" + dd + " before sqrt).");
                System.out.println(this.getCoordString() + "/" + a.getCoordString());
            }            
        }
        
        return(di);
    }
    
    
    /**
     * Returns the distance from this atom to atom 'a'. It uses the atom centers to calculate
     * the distance, so you have to take care of the collision sphere size yourself.
     * @param a the other Atom
     * @return the euclidian distance, rounded to an Integer
     */
    @Deprecated public Integer distToAtomOld(Atom a) {
        Double distDouble = .0;
        Integer distInt, dx, dy, dz, dd = 0;

        dx = this.getCoordX() - a.getCoordX();
        dy = this.getCoordY() - a.getCoordY();
        dz = this.getCoordZ() - a.getCoordZ();

        dd = dx * dx + dy * dy + dz * dz;
        distDouble = Math.sqrt(dd);
        distInt = Integer.valueOf((int)Math.round(distDouble));
        
        if(Settings.getBoolean("plcc_B_contact_debug_dysfunct")) {
            if(this.isCalphaAtom() && a.isCalphaAtom()) {
                System.out.println("Distance between C-alpha atoms " + this.pdbAtomNumber + " of " + this.getPdbResNum() + " and " + a.pdbAtomNumber + " of " + a.getPdbResNum() + " is " + distInt + " (" + dd + " before sqrt, " + distDouble + " as Double).");
            }            
        }
        
        return(distInt);
    }
    
    
    /**
     * Determines whether this is a C alpha atom. This is determined from the ATOM NAME entry of the PDB file.
     * @return true if this is a C alpha atom, false otherwise.
     */
    public boolean isCalphaAtom() {
        return(this.isProteinAtom() && this.name.equals(" CA "));
    }



    /**
     * Checks whether a contact (vdW radius overlap) exists to another atom. Returns the distance
     * to the atom (a positive Integer) if a contact exists, -1 otherwise. (This behavior saves
     * us from having to call the function twice during atom contact calculation, which is important
     * since we don't want to do useless Math.sqrt() operations (for performance reasons).
     */
    public Boolean atomContactTo(Atom a) {


        Integer atomRadiusThis;
        Integer atomRadiusOther;
        
        Integer radProt = Settings.getInteger("plcc_I_atom_radius");
        Integer radLig = Settings.getInteger("plcc_I_lig_atom_radius");

        if(this.isLigandAtom()) {
            atomRadiusThis = radLig;
        }
        else {
            atomRadiusThis = radProt;
        }
        
        if(a.isLigandAtom()) {
            atomRadiusOther = radLig;
        }
        else {
            atomRadiusOther = radProt;
        }


        Integer dist = this.distToAtom(a);
        Integer maxDist = atomRadiusThis + atomRadiusOther;

        //if(dist < 0) {
        //    System.err.println("ERROR: Distance of atoms " + this.getPdbAtomNum() + " and " + a.getPdbAtomNum() + " is " + dist + ", but should be > 0.");
        //    System.exit(1);
        //}

        if( dist < maxDist) {
            // Contact!
            //System.out.println("        ++++ CONTACT between atoms " + this.pdbAtomNumber + " and " + a.getPdbAtomNum() + " in dist " + dist + " (maxDist is " + maxDist + ").");
            return(true);
        }
        else {
            // No contact
            //System.out.println("        ---- No contact between atoms " + this.pdbAtomNumber + " and " + a.getPdbAtomNum() + " in dist " + dist + " (maxDist is " + maxDist + ").");
            return(false);
        }
    }

    @Override public String toString() {
        return("[Atom] #" + this.pdbAtomNumber + " NAME=" + this.name + " CS=" + this.chemSym + " Type=" + this.type + " Chain=" + this.chainID + " ResDssp=" + this.getResidue().getDsspResNum() + " ResPDB=" + this.getResidue().getUniquePDBName() + " Coords=" + getCoordString() + " AltLoc='" + this.altLoc + "'.");
    }

    /**
     * Compares two Atoms via their PDB atom number.
     * @param other the other atom
     * @return  true if they are the same, false otherwise
     */
    public Boolean equalsAtom(Atom other) {
        return(this.pdbAtomNumber == other.pdbAtomNumber);
    }

    // getters
    public String getAtomName() { return(name); }
    public String getAtomShortName() { return(name.trim()); }
    public String getChemSym() { return(chemSym); }
    public String getChainID() { return(chainID); }
    public String getModelID() { return(modelID); }
    public Chain getChain() { return(chain); }
    public Model getModel() { return(model); }
    public Integer getPdbAtomNum() { return(pdbAtomNumber); }
    public Integer getCoordX() { return(coordX); }
    public Integer getCoordY() { return(coordY); }
    public Integer getCoordZ() { return(coordZ); }
    public Integer getPdbLineNum() { return(pdbLineNum); }
    public Residue getResidue() { return(residue); }
    public Integer getPdbResNum() { return(pdbResNum); }
    public Integer getDsspResNum() { return(dsspResNum); }
    public Integer getAtomType() { return(type); }
    public String getCoordString() { return("(" + coordX + "," + coordY + "," + coordZ + ")"); }

    // setters
    public void setAtomName(String s) { name = s; }
    public void setChemSym(String s) { chemSym = s; }
    public void setChainID(String s) { chainID = s; }
    public void setModelID(String s) { modelID = s; }
    public void setPdbAtomNum(Integer i) { pdbAtomNumber = i; }
    public void setCoordX(Integer i) { coordX = i; }
    public void setCoordY(Integer i) { coordY = i; }
    public void setCoordZ(Integer i) { coordZ = i; }
    public void setPdbLineNum(Integer i) { pdbLineNum = i; }
    public void setResidue(Residue r) { residue = r; }
    public void setPdbResNum(Integer i) { pdbResNum = i; }
    public void setDsspResNum(Integer i) { dsspResNum = i; }
    public void setAtomtype(Integer i) { type = i; }
    public void setChain(Chain c) { chain = c; }
    public void setModel(Model m) { model = m; }
    
    public Position3D getPosition3D() {
        return new Position3D(coordX / 10.0f, coordY  / 10.0f, coordZ / 10.0f);
    }

}
