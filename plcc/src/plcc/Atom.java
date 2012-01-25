/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

// imports

/**
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

    // constructor
    //public Atom() {}

    public Boolean isLigandAtom() { return(residue.getType() == 1); }
    public Boolean isProteinAtom() { return(residue.getType() == 0); }
    public Boolean isOtherAtom() { return(residue.getType() == 2); }

    /**
     * Returns the distance from this atom to atom 'a'. It uses the atom centers to calculate
     * the distance, so you have to take care of the collision sphere size yourself.
     */
    public Integer distToAtom(Atom a) {
        Double distDouble = .0;
        Integer distInt, dx, dy, dz = 0;

        dx = this.getCoordX() - a.getCoordX();
        dy = this.getCoordY() - a.getCoordY();
        dz = this.getCoordZ() - a.getCoordZ();

        distDouble = Math.sqrt(dx * dx + dy * dy + dz * dz);
        distInt = Integer.valueOf((int)Math.round(distDouble));

        // TODO: remove this
        //System.out.println("  Distance between atoms " + this.pdbAtomNumber + this.getCoordString() + " and " + a.getPdbAtomNum() + a.getCoordString() + " is " + distInt + ".");
        //if(this.name.equals(" CA ") && a.name.equals(" CA "))
        //System.out.println("  Distance between atoms " + this + " and " + a + " is " + distInt + ".");
        
        return(distInt);
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
        //    System.exit(-1);
        //}

        if( dist <= maxDist) {
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
        return("[Atom] #" + this.pdbAtomNumber + " NAME=" + this.name + " CS=" + this.chemSym + " Type=" + this.type + " Chain=" + this.chainID + " ResDssp=" + this.getResidue().getDsspResNum() + " ResPDB=" + this.getResidue().getUniquePDBName() + " Coords=" + getCoordString() + "");
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

}
