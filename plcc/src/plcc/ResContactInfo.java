/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;


public class ResContactInfo {

    // declare class vars

    // The contents of all the fields of these arrays is explained in Main.calculateAtomContactsBetweenResidues(),
    //  the function that creates these arrays.
    //  Just to be sure a copy of these lines can be found below. The length of all of these is 12.
    
    private Integer[] numPairContacts;
    // The positions in the numPairContacts array hold the number of contacts of each type for a pair of residues:
    // Some cheap vars to make things easier to understand (a poor replacement for #define):
    private Integer TT = 0;         //  0 = total number of contacts            (all residue type combinations)
    private Integer BB = 1;         //  1 = # of backbone-backbone contacts     (protein - protein only)
    private Integer CB = 2;         //  2 = # of sidechain-backbone contacts    (protein - protein only)
    private Integer BC = 3;         //  3 = # of backbone-sidechain contacts    (protein - protein only)
    private Integer CC = 4;         //  4 = # of sidechain-sidechain contacts   (protein - protein only)
    private Integer HB1 = 5;         //  5 = # of H-bridge contacts 1, N=>0      (protein - protein only)
    private Integer HB2 = 6;         //  6 = # of H-bridge contacts 2, 0=>N      (protein - protein only)
    private Integer BL = 7;         //  7 = # of backbone-ligand contacts       (protein - ligand only)
    private Integer LB = 8;         //  8 = # of ligand-backbone contacts       (protein - ligand only)
    private Integer CL = 9;         //  9 = # of sidechain-ligand contacts      (protein - ligand only)
    private Integer LC = 10;        // 10 = # of ligand-sidechain contacts      (protein - ligand only)
    private Integer LL = 11;        // 11 = # of ligand-ligand contacts         (ligand - ligand only)

    // The different values in 'XY' vs 'YX' are only produced by the the sequential order of the
    //  residues (e.g., the ligand came fist: this was treated as an 'LB' contact, otherwise it
    //  had been treated as an 'BL' contact (note that residue pairs are compared only once by the
    //  loop in Main.calculateAllContacts()). Saving this separately seems strange but we have to
    //  do it because geom_neo does it and we want out output to be compatible with it.

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
    


    // constructor
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

    /*
     * @return A short String representation of the contact (resA<->resB).
     */
    public String shortStringRep() {
        return(this.getResA().getUniqueString() + "<-->" + this.getResB().getUniqueString());
    }

    @Override public String toString() {
        return("[RCI] TypeA " + getResTypeStringA() + ", TypeB " + getResTypeStringB() + ", ResA "+ getDsspResNumResA() + ", ResB " + getDsspResNumResB());
    }


    // getters for overview
    public String getResTypeStringA() {  if(this.resA.isAA()) { return("PRT"); } else if(this.resA.isLigand()) { return("LIG"); } else { return("OTH"); } }
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
    public Integer getAAIDResA() { return(this.resA.getInternalID()); }
    public Integer getAAIDResB() { return(this.resB.getInternalID()); }

    public Integer getCenterSphereRadiusResA() { return(this.resA.getCenterSphereRadius()); }
    public Integer getCenterSphereRadiusResB() { return(this.resB.getCenterSphereRadius()); }
    public Integer getResPairDist() { return(dist); }

    public Integer getHB1Dist() { return(minContactDistances[HB1]); }
    public Integer getHB2Dist() { return(minContactDistances[HB2]); }

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

    // the getters for statistics follow
    public Integer getNumContactsTotal() { return(numPairContacts[TT]); }
    public Integer getNumLigContactsTotal() { return(numTotalLigContactsPair); }
    public Integer getNumContactsBB() { return(numPairContacts[BB]); }
    public Integer getNumContactsBC() { return(numPairContacts[BC]); }
    public Integer getNumContactsCB() { return(numPairContacts[CB]); }
    public Integer getNumContactsCC() { return(numPairContacts[CC]); }
    public Integer getNumContactsHB1() { return(numPairContacts[HB1]); }
    public Integer getNumContactsHB2() { return(numPairContacts[HB2]); }
    public Integer getNumContactsBL() { return(numPairContacts[BL]); }
    public Integer getNumContactsLB() { return(numPairContacts[LB]); }
    public Integer getNumContactsCL() { return(numPairContacts[CL]); }
    public Integer getNumContactsLC() { return(numPairContacts[LC]); }
    public Integer getNumContactsLL() { return(numPairContacts[LL]); }

    // DEBUG only
    public Residue getResA() { return(resA); }
    public Residue getResB() { return(resB); }


    // no setters needed, this class is only a nice wrapper for the ugly arrays

}
