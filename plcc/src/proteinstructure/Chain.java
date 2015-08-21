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

/**
 * Represents a protein chain in a PDB file.
 * @author ts
 */
public class Chain implements java.io.Serializable {

    // declare class vars
    private String pdbChainID = null;           // chain ID from PDB file
    private String dsspChainID = null;          // chain ID from DSSP file
    private ArrayList<Residue> residues = null;          // a list of all Residues of the Chain
    private String modelID = null;
    private Model model = null;                 // the Model of this Chain
    private ArrayList<String> homologues = null; // a list of homologue chains (defined by PDB COMPND)

    // constructor
    public Chain(String ci) { pdbChainID = ci; residues = new ArrayList<Residue>(); }


    // getters
    /** Returns the chain ID (chain name) from PDB file. Something like "A". */
    public String getPdbChainID() { return(pdbChainID); }
    public String getDsspChainID() { return(dsspChainID); }
    public String getModelID() { return(modelID); }
    public Model getModel() { return(model); }
    public ArrayList<Residue> getResidues() { return(residues); }
    public ArrayList<String> getHomologues() { return(homologues); }
    
    /**
     * Returns a list of all ligand residues in this chain.
     * @return a list of all ligand residues in this chain
     */
    public ArrayList<Residue> getAllLigandResidues() {
        ArrayList<Residue> ligands = new ArrayList<Residue>();
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
}
