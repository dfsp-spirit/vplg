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
import java.util.ArrayList;
import java.util.Arrays;
import io.IO;
import plcc.Settings;
import tools.DP;

/**
 * Represents a protein chain in a PDB file.
 * @author ts
 */
public class Chain implements java.io.Serializable {

    // declare class vars
    private String pdbChainID = null;                // chain ID from PDB file
    private String dsspChainID = null;               // chain ID from DSSP file
    private ArrayList<Residue> residues = null;      // a list of all Residues of the Chain
    private String macromolID = null;                // the macromolecule ID of the chain in the PDB file, defines chains forming a single macromolecule
    private String macromolName = null;              // the macromol name from the PDB file
    private String modelID = null;
    private Model model = null;                      // the Model of this Chain
    private ArrayList<String> homologues = null;     // a list of homologue chains (defined by PDB COMPND)
    private final Integer[] chainCenter = new Integer[3];  // X-/Y-/Z-coordinates as 10th of Angström of the center of all non-H atoms
    private Integer radiusFromCenter = null;         // distance from center to farthest non-H atom. -1 if no protein-atoms

    // constructor
    public Chain(String ci) { pdbChainID = ci; residues = new ArrayList<Residue>(); }


    // getters
    /** Returns the chain ID (chain name) from PDB file. Something like "A". */
    public String getPdbChainID() { return(pdbChainID); }
    public String getDsspChainID() { return(dsspChainID); }
    public String getModelID() { return(modelID); }
    public String getMacromolID() { return(macromolID); }
    public String getMacromolName() { return(macromolName); }
    public Model getModel() { return(model); }
    public ArrayList<Residue> getResidues() { return(residues); }
    public ArrayList<String> getHomologues() { return(homologues); }
    public Integer[] getChainCenter() { return(chainCenter); }
    public Integer getRadiusFromCenter() { return(radiusFromCenter); }
    
    /**
     * Returns a list of all ligand residues in this chain.
     * @return a list of all ligand residues in this chain
     */
    public ArrayList<Residue> getAllLigandResidues() {
        ArrayList<Residue> ligands = new ArrayList<>();
        for(Residue r : this.residues) {
            if(r.isLigand()) {
                ligands.add(r);
            }
        }
        return ligands;
    }

    // setters
    public void addResidue(Residue r) { residues.add(r); }
    public void setPdbChainID(String s) { pdbChainID = s; }
    public void setDsspChainID(String s) { dsspChainID = s; }
    public void setMacromolID(String s) { macromolID = s; }
    public void setMacromolName(String s) { macromolName = s; }
    public void setModelID(String s) { modelID = s; }
    public void setModel(Model m) { model = m; }
    public void setHomologues(ArrayList<String> h) { homologues = h; }
    public void addHomologue(String s) {
        if(!homologues.contains(s)){
            homologues.add(s);
        }
    }

    /**
     * Returns the chemical property string for this chain according to the 5 types system, i.e., a concatenation of all chemProps of all the chain residues.
     * @return the chemical property string, 5 types system
     */
    public String getChainChemProps5StringAllResidues() {
        StringBuilder sb = new StringBuilder();        
        for(Residue r : this.residues) {
            sb.append(r.getChemicalProperty5OneLetterString());            
        }        
        return sb.toString();
    }
    
    /**
     * Returns the chemical property string for this chain according to the 3 types system, i.e., a concatenation of all chemProps of all the chain residues.
     * @return the chemical property string, 3 types system
     */
    public String getChainChemProps3StringAllResidues() {
        StringBuilder sb = new StringBuilder();        
        for(Residue r : this.residues) {
            sb.append(r.getChemicalProperty3OneLetterString());            
        }        
        return sb.toString();
    }
    
    /**
     * Returns the chemical property string for this chain in the 5 types system, but considers only AA residues which are part of an SSE (ignores ligands and AAs in coiled regions).
     * @param sseSeparator the String (most likely a single character) to add between two SSEs. You can specify the empty String if you do not want anything inserted, of course.
     * @return the chemical property string, 5 types system
     */
    public String[] getChainChemPropsStringSSEResiduesOnly(String sseSeparator) {
        StringBuilder sbChemProp = new StringBuilder();        
        StringBuilder sbSSE = new StringBuilder();
        SSE s;
        String sseString = null;
        String sseStringlast = null;
        int numSeps = 0;
        
        // some basic stat stuff, just a quick test
        int numSSETypes = 2; int H = 0; int E = 1; // helix and strand
        int numResH = 0; int numResE = 0;
        Integer[][] chemPropsBySSEType = new Integer[numSSETypes][AminoAcid.ALL_CHEM_PROPS5.length];
        for(int i = 0; i < numSSETypes; i++) {
            Arrays.fill(chemPropsBySSEType[i], 0);
        }
        
        for(Residue r : this.residues) {
            if(r.isAA()) {
                s = r.getSSE();
                
                if(s != null) {
                    if(s.isOtherSSE()) {
                        continue;
                    }
                    sseString = s.getSSEClass();
                    if(sseStringlast != null && (sseString == null ? sseStringlast != null : !sseString.equals(sseStringlast))) {
                        sbChemProp.append(sseSeparator);
                        sbSSE.append(sseSeparator);
                        numSeps++;
                    }
                    sbChemProp.append(r.getChemicalProperty5OneLetterString());
                    if(sseString.equals("H")) {
                        numResH++;
                        chemPropsBySSEType[H][r.getChemicalProperty5Type()]++;
                    }
                    if(sseString.equals("E")) {
                        numResE++;
                        chemPropsBySSEType[E][r.getChemicalProperty5Type()]++;
                    }
                    sbSSE.append(s.getSSEClass());
                }
                sseStringlast = sseString;
            }            
        }
        
        // stat stuff
        Boolean doStats = false;
        if(doStats) {
            float[] sharesH = new float[AminoAcid.ALL_CHEM_PROPS5.length];
            float[] sharesE = new float[AminoAcid.ALL_CHEM_PROPS5.length];
            for(int i = 0; i < AminoAcid.ALL_CHEM_PROPS5.length; i++) {
                sharesH[i] = (float)numResH / (float)chemPropsBySSEType[H][i];
                sharesE[i] = (float)numResE / (float)chemPropsBySSEType[E][i];
            }
            System.out.println("Chain: chemType by SSEtype[H]: totalH=" + numResH + ", shares: " + IO.floatArrayToString(sharesH));
            System.out.println("Chain: chemType by SSEtype[E]: totalE=" + numResE + ", shares: " + IO.floatArrayToString(sharesE));
        }
        
        //System.out.println("Added " + numSeps + " separators.");
        return new String[] { sbChemProp.toString(), sbSSE.toString() };
    }
    
    /**
     * Computes the geometrical center of all atoms and the largest distance from center to an atom (=radius).
     */
    public void computeChainCenterAndRadius() {
        // compute center
        Integer[] tmpCenter = new Integer[3];
        tmpCenter[0] = tmpCenter[1] = tmpCenter[2] = 0;
        int tmpAtomNumber = 0;
        for (Residue r : residues) {
            for (Atom a : r.getAtoms()) {
                tmpCenter[0] += a.getCoordX();
                tmpCenter[1] += a.getCoordY();
                tmpCenter[2] += a.getCoordZ();
                tmpAtomNumber += 1;
            }
        }
        
        // there could be the case that only RNA/DNA atoms are in a chain
        // in this case radius is set to -1
        
        if (tmpAtomNumber > 0) {
        
            chainCenter[0] = tmpCenter[0] / tmpAtomNumber;
            chainCenter[1] = tmpCenter[1] / tmpAtomNumber;
            chainCenter[2] = tmpCenter[2] / tmpAtomNumber;

            if (Settings.getInteger("plcc_I_debug_level") > 0) {
                System.out.println("[DEBUG] Center of chain " + pdbChainID + " is at " + Arrays.toString(chainCenter));
            }

            // compute radius
            int tmpBiggestDist = 0;
            int tmpCurrentDist;
            for (Residue r : residues) {
                for (Atom a : r.getAtoms()) {
                    tmpCurrentDist = a.distToPoint(chainCenter[0], chainCenter[1], chainCenter[2]);
                    // System.out.println("[DEBUG] Distance to center from atom " + a.toString() + " is " + String.valueOf(tmpCurrentDist));
                    if (tmpCurrentDist > tmpBiggestDist) {
                        tmpBiggestDist = tmpCurrentDist;
                    }
                }
            }

            if (Settings.getInteger("plcc_I_debug_level") > 0) {
                System.out.println("[DEBUG] Radius of chain " + pdbChainID + " is " + String.valueOf(tmpBiggestDist));
            }

            radiusFromCenter = tmpBiggestDist;
        
        } else {
            System.out.println("  [WARNING] Chain " + this.pdbChainID + " seems not to hold protein atoms. No center can be detected.");
            radiusFromCenter = -1;
        }        
    }
    
    /**
     * This function determines whether we need to look at the residues to check for contacts betweens
     * this chain and another one. If the center spheres don't overlap, there cannot exist any atom contacts.
     * @param c Chain: the other chain
     * @return Bool: if spheres overlap
     */
    public Boolean contactPossibleWithChain(Chain c) {
        
        // no contact possible if either chain contains no atoms
        if ((! this.containsAtoms()) || (! c.containsAtoms())) {
            return false;
        }

        Integer dist, tmpSum;
        
        tmpSum = 0;
        tmpSum += (this.chainCenter[0] - c.getChainCenter()[0]) * (this.chainCenter[0] - c.getChainCenter()[0]);
        tmpSum += (this.chainCenter[1] - c.getChainCenter()[1]) * (this.chainCenter[1] - c.getChainCenter()[1]);
        tmpSum += (this.chainCenter[2] - c.getChainCenter()[2]) * (this.chainCenter[2] - c.getChainCenter()[2]);
        
        dist = (int)Math.round(Math.sqrt(tmpSum));
        
        // presume there are ligands in the chain
        Integer atomRadius = Settings.getInteger("plcc_I_lig_atom_radius");
        
        Integer justToBeSure = 4;   // account for small errors due to rounding
        Integer summedSpheres = this.getRadiusFromCenter() + c.getRadiusFromCenter() + (atomRadius * 2) + justToBeSure;

        //System.out.println("    Center sphere radius for PDB residue " + this.getPdbResNum() + " = " + this.getCenterSphereRadius() + ", for " + r.getPdbResNum() + " = " + r.getCenterSphereRadius() + ", atom radius is " + atomRadius + ".");
        //System.out.println("    DSSP Res distance " + this.getDsspResNum() + "/" + r.getDsspResNum() + " is " + dist + " (no contacts possible above distance " + maxDistForContact + ").");

        if (Settings.getInteger("plcc_I_debug_level") > 0) {
            System.out.println("[DEBUG][CHAIN] Chain " + this.pdbChainID + " and " + c.pdbChainID);
            System.out.println(" ... mid points: " + this.chainCenter[0] + "|" + this.chainCenter[1] + "|" + this.chainCenter[2]);
            System.out.println(" ... mid points: " + c.chainCenter[0] + "|" + c.chainCenter[1] + "|" + c.chainCenter[2]);
            System.out.println(" ... radii: " + this.radiusFromCenter + " and " + c.radiusFromCenter);
            System.out.println(" ... distance: " + dist);
            System.out.println(" ... summedSpheres: " + summedSpheres);
        }
        
        if(dist <= summedSpheres) {
            return(true);
        }
        else {
            return(false);
        }
    }
    
    public Boolean containsAtoms() {
        for (Residue r : this.getResidues()) {
            for (Atom a : r.getAtoms()) {
                return true;
            }
        }
        return false;
    }
}
