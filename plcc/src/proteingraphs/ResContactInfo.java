/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package proteingraphs;

import proteinstructure.Residue;
import java.util.Arrays;
import plcc.Main;


/**
 * This class implements a residue level contact, i.e., all information required about the contact between a pair (a, b) of Residue objects.
 * @author ts
 */
public class ResContactInfo {

    // declare class vars

    // The contents of all the fields of these arrays is explained in Main.calculateAtomContactsBetweenResidues(),
    //  the function that creates these arrays.
    //  Just to be sure a copy of these lines can be found below. The length of all of these is 12.
    
    private Integer[] numPairContacts;
    // The positions in the numPairContacts array hold the number of contacts of each type for a pair of residues:
    // Some cheap vars to make things easier to understand (a poor replacement for #define):
    public static final Integer TT = 0;         //  0 = total number of contacts            (all residue type combinations)
    public static final Integer BB = 1;         //  1 = # of backbone-backbone contacts     (protein - protein only)
    public static final Integer CB = 2;         //  2 = # of sidechain-backbone contacts    (protein - protein only)
    public static final Integer BC = 3;         //  3 = # of backbone-sidechain contacts    (protein - protein only)
    public static final Integer CC = 4;         //  4 = # of sidechain-sidechain contacts   (protein - protein only)
    public static final Integer HB = 5;         //  5 = # of H-bridge contacts 1, N=>0      (protein - protein only)
    public static final Integer BH = 6;         //  6 = # of H-bridge contacts 2, 0=>N      (protein - protein only)
    public static final Integer BL = 7;         //  7 = # of backbone-ligand contacts       (protein - ligand only)
    public static final Integer LB = 8;         //  8 = # of ligand-backbone contacts       (protein - ligand only)
    public static final Integer CL = 9;         //  9 = # of sidechain-ligand contacts      (protein - ligand only)
    public static final Integer LC = 10;        // 10 = # of ligand-sidechain contacts      (protein - ligand only)
    public static final Integer LL = 11;        // 11 = # of ligand-ligand contacts         (ligand - ligand only)
    public static final Integer DISULFIDE = 12;        // 12 = # of disulfide bridges
    public static final Integer BBHB = 13;       // 13 = # of interchain H-bridge contacts, backbone-backbone, donor-acceptor (N=>O)
    public static final Integer BBBH = 14;       // 14 = # of interchain H-bridge contacts, backbone-backbone, acceptor-donor (O=>N)
    public static final Integer IVDW = 15;      // 15 = # of interchain van der Waals interactions
    public static final Integer ISS = 16;       // 16 = # of interchain disulfide bridges
    public static final Integer IPI = 17;       // 17 = # of interchain pi-effects
    public static final Integer ISB = 18;       // 18 = # of interchain salt bridges
    public static final Integer BCHB = 19;      // 19 = # of interchain H-bridge contacts, backbone-sidechain, donor-acceptor
    public static final Integer BCBH = 20;      // 20 = # of interhcain H-bridge contacts, backbone-sidechain, acceptor-donor
    public static final Integer CBHB = 21;      // 21 = # of interchain H-bridge contacts, sidechain-backbone, donor-acceptor
    public static final Integer CBBH = 22;      // 22 = # of interchain H-bridge contacts, sidechain-backbone, acceptor-donor
    public static final Integer CCHB = 23;      // 23 = # of interchain H-bridge contacts, sidechain-sidechain, donor-acceptor
    public static final Integer CCBH = 24;      // 24 = # of interchain H-bridge contacts, sidechain-sidechain, acceptor-donor
    public static final Integer NHPI = 25;      // 25 = # of interchain N-H...Pi contacts, backbone-sidechain, donor-acceptor (non-canonical interaction)
    public static final Integer PINH = 26;      // 26 = # of interchain N-H...Pi contacts, sidechain-backbone, acceptor-donor (non-canonical interaction)
    public static final Integer CAHPI = 27;     // 27 = # of interchain CA-H...Pi contacts, backbone-sidechain, donor-acceptor (non-canonical interaction)
    public static final Integer PICAH = 28;     // 28 = # of interchain CA-H...Pi contacts, sidechain-backbone, acceptor-donor (non-canonical interaction) 
    public static final Integer CNHPI = 29;     // 29 = # of interchain N-H...Pi contacts, sidechain-sidechain, donor-acceptor (non-canonical interaction)
    public static final Integer PICNH = 30;     // 30 = # of interchain N-H...Pi contacts, sidechain-sidechain, acceptor-donor (non-canonical interaction)
    public static final Integer SHPI = 31;      // 31 = # of interchain S-H...Pi contacts, sidechain-sidechain, donor-acceptor (non-canonical interaction)
    public static final Integer PISH = 32;      // 32 = # of interchain S-H...Pi contacts, sidechain-sidechain, acceptor-donor (non-canonical interaction)
    public static final Integer XOHPI = 33;     // 33 = # of interchain X-O-H...Pi contacts witch X = Ser || Thr || Tyr, sidechain-sidechain, donor-accepor (non-canonical interaction
    public static final Integer PIXOH = 34;     // 34 = # of interchain X-O-H...Pi contacts witch X = Ser || Thr || Tyr, sidechain-sidechain, acceptor-donor(non-canonical interaction
    public static final Integer PROCDHPI = 35;  // 35 = # of interchain Pro-CD-H...Pi contacts, sidechain-sidechain, donor-acceptor (non-canonical)
    public static final Integer PIPROCDH = 36;  // 36 = # of interchain Pro-CD-H...Pi contacts, sidechain-sidechain, acceptor-donor (non-canonical)
    
    
    
    // The different values in 'XY' vs 'YX' are only produced by the the sequential order of the
    //  residues (e.g., the ligand came fist: this was treated as an 'LB' contact, otherwise it
    //  had been treated as an 'BL' contact (note that residue pairs are compared only once by the
    //  loop in Main.calculateAllContacts()). Saving this separately seems strange but we have to
    //  do it because geom_neo does it and we want our output to be compatible with it.

    private Integer numTotalLigContactsPair;

    private Integer[] minContactDistances;
    // Holds the minimal distances of contacts of the appropriate type (see numPairContacts, index 0 is unused)

    private Integer[] contactAtomIndexInResidueA;
    // Holds the number Atom x has in its residue a for the contact with minimal distance of that type.
    // See minContactDistances and numPairContacts; index 0 is unused; index 5 + 6 are also unused (atom is obvious)

    private Integer[] contactAtomIndexInResidueB;
    // Holds the number Atom y has in its residue b for the contact with minimal distance of that type.
    // See minContactDistances and numPairContacts; index 0 is unused; index 5 + 6 are also unused because the atom is
    //  fixed for these (HB1: backbone N, HB2: backbone O)

    private Residue resA, resB;
    private Integer dist;
    


    /**
     * Constructor for a residue pair contact between the residues (a, b).
     * 
     * @param npcs array holding the number of atom contacts of all different types (e.g., BB, BC, CB, ...) of this residue pair.
     * @param mcds array holding the minimum contact distances of all different types (BB, ...)
     * @param can_a array holding the atom indices of the contact atoms of the different types (BB, ...) in Residue a
     * @param can_b array holding the atom indices of the contact atoms of the different types (BB, ...) in Residue b
     * @param a the first Residue of (a, b)
     * @param b the second Residue of (a, b)
     * @param d the distance of this residue pair (residue center to residue center)
     * @param nlc the number of total ligand contacts of this residue pair 
     */    
    public ResContactInfo(Integer[] npcs, Integer[] mcds, Integer[] can_a, Integer[] can_b, Residue a, Residue b, Integer d, Integer nlc) {

        numPairContacts = npcs;
        minContactDistances = mcds;
        contactAtomIndexInResidueA = can_a;
        contactAtomIndexInResidueB = can_b;
        resA = a;
        resB = b;
        dist = d;
        numTotalLigContactsPair = nlc;
    }
    
    
    /**
     * Creates an empty ResContactInfo between the two residues and fills in their distance. All other values
     * are set to zero or not initialized.
     * @param a the first Residue
     * @param b the second Residue
     */
    public ResContactInfo(Residue a, Residue b) {
        resA = a;
        resB = b;
        dist = a.resCenterDistTo(b);  
        numTotalLigContactsPair = 0;
        
        numPairContacts = new Integer[Main.NUM_RESIDUE_PAIR_CONTACT_TYPES];
        minContactDistances = new Integer[Main.NUM_RESIDUE_PAIR_CONTACT_TYPES];
        contactAtomIndexInResidueA = new Integer[Main.NUM_RESIDUE_PAIR_CONTACT_TYPES];
        contactAtomIndexInResidueB = new Integer[Main.NUM_RESIDUE_PAIR_CONTACT_TYPES];
        
        Arrays.fill(numPairContacts, 0);
        Arrays.fill(minContactDistances, Integer.MAX_VALUE);
        Arrays.fill(contactAtomIndexInResidueA, -1);
        Arrays.fill(contactAtomIndexInResidueB, -1);
    }

    /*
     * @return A short String representation of the contact (resA<->resB).
     */
    public String shortStringRep() {
        return(this.getResA().getUniqueString() + "<-->" + this.getResB().getUniqueString());
    }

    @Override public String toString() {
        return("[RCI] TypeA " + getResTypeStringA() + ", TypeB " + getResTypeStringB() + ", ResA "+ getDsspResNumResA() + ", ResB " + getDsspResNumResB());
    }


    /** Returns a string indicating whether residue A is a protein residue ("PRT"), a ligand ("LIG"), or something else ("OTH").
     * @return  a string indicating whether residue B is a protein residue ("PRT"), a ligand ("LIG"), or something else ("OTH").  
     */
    public String getResTypeStringA() {  if(this.resA.isAA()) { return("PRT"); } else if(this.resA.isLigand()) { return("LIG"); } else { return("OTH"); } }
    /** Returns a string indicating whether residue B is a protein residue ("PRT"), a ligand ("LIG"), or something else ("OTH").
     * @return  a string indicating whether residue B is a protein residue ("PRT"), a ligand ("LIG"), or something else ("OTH").
     */
    public String getResTypeStringB() {  if(this.resB.isAA()) { return("PRT"); } else if(this.resB.isLigand()) { return("LIG"); } else { return("OTH"); } }


    // getters required for writing the <pdbid>.geo file
    public Integer getDsspResNumResA() { return(this.resA.getDsspResNum()); }
    public Integer getDsspResNumResB() { return(this.resB.getDsspResNum()); }
    public Integer getPdbResNumResA() { return(this.resA.getPdbResNum()); }
    public Integer getPdbResNumResB() { return(this.resB.getPdbResNum()); }
    public String getResName3A() { return(this.resA.getName3()); }
    public String getResName3B() { return(this.resB.getName3()); }
    public String getResName1A() { return(this.resA.getAAName1()); }
    public String getResName1B() { return(this.resB.getAAName1()); }
    public Integer getAAIDResA() { return(this.resA.getInternalAAID()); }
    public Integer getAAIDResB() { return(this.resB.getInternalAAID()); }

    public Integer getCenterSphereRadiusResA() { return(this.resA.getCenterSphereRadius()); }
    public Integer getCenterSphereRadiusResB() { return(this.resB.getCenterSphereRadius()); }
    public Integer getResPairDist() { return(dist); }

    public Integer getHB1Dist() { return(minContactDistances[HB]); }
    public Integer getHB2Dist() { return(minContactDistances[BH]); }

    public Integer getBBContactDist() { return(minContactDistances[BB]); }
    public Integer getBBContactAtomNumA() { return(contactAtomIndexInResidueA[BB] + 1); }   // The '+1' is required because geom_neo starts the atom index at '1' but we start it at '0'.
    public Integer getBBContactAtomNumB() { return(contactAtomIndexInResidueB[BB] + 1); }   //  This also means that we HAVE to init the atom index arrays in Main.calculateAtomContactsBetweenResidues()
                                                                                            //  with '-1'.  This return statement will add 1, and '0' means 'no contact' for geom_neo. :)
    public Integer getCBContactDist() { return(minContactDistances[CB]); }
    public Integer getCBContactAtomNumA() { return(contactAtomIndexInResidueA[CB] + 1); }
    public Integer getCBContactAtomNumB() { return(contactAtomIndexInResidueB[CB] + 1); }

    public Integer getBCContactDist() { return(minContactDistances[BC]); }
    public Integer getBCContactAtomNumA() { return(contactAtomIndexInResidueA[BC] + 1); }
    public Integer getBCContactAtomNumB() { return(contactAtomIndexInResidueB[BC] + 1); }

    public Integer getCCContactDist() { return(minContactDistances[CC]); }
    public Integer getCCContactAtomNumA() { return(contactAtomIndexInResidueA[CC] + 1); }
    public Integer getCCContactAtomNumB() { return(contactAtomIndexInResidueB[CC] + 1); }

    public Integer getBLContactDist() { return(minContactDistances[BL]); }
    public Integer getBLContactAtomNumA() { return(contactAtomIndexInResidueA[BL] + 1); }
    public Integer getBLContactAtomNumB() { return(contactAtomIndexInResidueB[BL] + 1); }

    public Integer getLBContactDist() { return(minContactDistances[LB]); }
    public Integer getLBContactAtomNumA() { return(contactAtomIndexInResidueA[LB] + 1); }
    public Integer getLBContactAtomNumB() { return(contactAtomIndexInResidueB[LB] + 1); }

    public Integer getCLContactDist() { return(minContactDistances[CL]); }
    public Integer getCLContactAtomNumA() { return(contactAtomIndexInResidueA[CL] + 1); }
    public Integer getCLContactAtomNumB() { return(contactAtomIndexInResidueB[CL] + 1); }

    public Integer getLCContactDist() { return(minContactDistances[LC]); }
    public Integer getLCContactAtomNumA() { return(contactAtomIndexInResidueA[LC] + 1); }
    public Integer getLCContactAtomNumB() { return(contactAtomIndexInResidueB[LC] + 1); }

    public Integer getLLContactDist() { return(minContactDistances[LL]); }
    public Integer getLLContactAtomNumA() { return(contactAtomIndexInResidueA[LL] + 1); }
    public Integer getLLContactAtomNumB() { return(contactAtomIndexInResidueB[LL] + 1); }
    
    public Integer getIVDWContactDist() { return(minContactDistances[IVDW]); }
    public Integer getIVDWContactAtomNumA() { return(contactAtomIndexInResidueA[IVDW]); }
    public Integer getIVDWContactAtomNumB() { return(contactAtomIndexInResidueB[IVDW]); }
    
    public Integer getBBHBContactDist() { return(minContactDistances[BBHB]); }
    public Integer getBBHBContactAtomNumA() { return(contactAtomIndexInResidueA[BBHB]); }
    public Integer getBBHBContactAtomNumB() { return(contactAtomIndexInResidueB[BBHB]); }
    
    public Integer getBBBHContactDist() { return(minContactDistances[BBBH]); }
    public Integer getBBBHContactAtomNumA() { return(contactAtomIndexInResidueA[BBBH]); }
    public Integer getBBBHContactAtomNumB() { return(contactAtomIndexInResidueB[BBBH]); }
    
    public Integer getBCHBContactDist() { return(minContactDistances[BCHB]); }
    public Integer getBCHBContactAtomNumA() { return(contactAtomIndexInResidueA[BCHB]); }
    public Integer getBCHBContactAtomNumB() { return(contactAtomIndexInResidueB[BCHB]); }
    
    public Integer getBCBHContactDist() { return(minContactDistances[BCBH]); }
    public Integer getBCBHContactAtomNumA() { return(contactAtomIndexInResidueA[BCBH]); }
    public Integer getBCBHContactAtomNumB() { return(contactAtomIndexInResidueB[BCBH]); }
    
    public Integer getCBHBContactDist() { return(minContactDistances[CBHB]); }
    public Integer getCBHBContactAtomNumA() { return(contactAtomIndexInResidueA[CBHB]); }
    public Integer getCBHBContactAtomNumB() { return(contactAtomIndexInResidueB[CBHB]); }
    
    public Integer getCBBHContactDist() { return(minContactDistances[CBBH]); }
    public Integer getCBBHContactAtomNumA() { return(contactAtomIndexInResidueA[CBBH]); }
    public Integer getCBBHContactAtomNumB() { return(contactAtomIndexInResidueB[CBBH]); }
    
    public Integer getCCHBContactDist() { return(minContactDistances[CCHB]); }
    public Integer getCCHBContactAtomNumA() { return(contactAtomIndexInResidueA[CCHB]); }
    public Integer getCCHBContactAtomNumB() { return(contactAtomIndexInResidueB[CCHB]); }
    
    public Integer getCCBHContactDist() { return(minContactDistances[CCBH]); }
    public Integer getCCBHContactAtomNumA() { return(contactAtomIndexInResidueA[CCBH]); }
    public Integer getCCBHContactAtomNumB() { return(contactAtomIndexInResidueB[CCBH]); }
    
    public Integer getNHPIContactDist() { return(minContactDistances[NHPI]); }
    public Integer getNHPIContactAtomNumA() { return(contactAtomIndexInResidueA[NHPI]); }
    public Integer getNHPIContactAtomNumB() { return(contactAtomIndexInResidueB[NHPI]); }
    
    public Integer getPINHContactDist() { return(minContactDistances[PINH]); }
    public Integer getPINHContactAtomNumA() { return(contactAtomIndexInResidueA[PINH]); }
    public Integer getPINHContactAtomNumB() { return(contactAtomIndexInResidueB[PINH]); }
    
   
    
    
// the getters for statistics follow
    public Integer getNumContactsTotal() { return(numPairContacts[TT]); }
    public Integer getNumLigContactsTotal() { return(numTotalLigContactsPair); }
    public Integer getNumContactsBB() { return(numPairContacts[BB]); }
    public Integer getNumContactsBC() { return(numPairContacts[BC]); }
    public Integer getNumContactsCB() { return(numPairContacts[CB]); }
    public Integer getNumContactsCC() { return(numPairContacts[CC]); }
    public Integer getNumContactsHB1() { return(numPairContacts[HB]); }
    public Integer getNumContactsHB2() { return(numPairContacts[BH]); }
    public Integer getNumContactsBL() { return(numPairContacts[BL]); }
    public Integer getNumContactsLB() { return(numPairContacts[LB]); }
    public Integer getNumContactsCL() { return(numPairContacts[CL]); }
    public Integer getNumContactsLC() { return(numPairContacts[LC]); }
    public Integer getNumContactsLL() { return(numPairContacts[LL]); }
    public Integer getNumContactsDisulfide() { return(numPairContacts[DISULFIDE]); }
    public Integer getNumContactsBBHB() { return(numPairContacts[BBHB]); }
    public Integer getNumContactsBBBH() { return(numPairContacts[BBBH]); }
    public Integer getNumContactsIVDW() { return(numPairContacts[IVDW]); }
    public Integer getNumContactsISS() { return(numPairContacts[ISS]); }
    public Integer getNumContactsIPI() { return(numPairContacts[IPI]); }
    public Integer getNumContactsISB() { return(numPairContacts[ISB]); }
    public Integer getNumContactsBCHB() { return(numPairContacts[BCHB]); }
    public Integer getNumContactsBCBH() { return(numPairContacts[BCBH]); }
    public Integer getNumContactsCBHB() { return(numPairContacts[CBHB]); }
    public Integer getNumContactsCBBH() { return(numPairContacts[CBBH]); }
    public Integer getNumContactsCCHB() { return(numPairContacts[CCHB]); }
    public Integer getNumContactsCCBH() { return(numPairContacts[CCBH]); }
    
    // DEBUG only
    public Residue getResA() { return(resA); }
    public Residue getResB() { return(resB); }
    
    /**
     * Determines whether the residue pair described by this RCI is in any contact, i.e., has contacts of any type.
     * @return true if this RCI contains any kind of contact
     */
    public boolean describesAnyContact() {
        return(this.getNumContactsTotal() > 0 || this.getNumLigContactsTotal() > 0);
    }


    // no setters needed, this class is only a nice wrapper for the ugly arrays

}
