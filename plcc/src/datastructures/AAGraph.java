/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import proteinstructure.AminoAcid;
import graphformats.IGraphModellingLanguageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import plcc.Settings;
import proteingraphs.ResContactInfo;
import proteinstructure.Residue;
import tools.DP;

/**
 * An undirected, adjacency list based amino acid graph. Suitable for large, sparse graphs. 
 * Holds the AAs for a single chain or multiple chains.
 * 
 * @author ts
 */
public class AAGraph extends SparseGraph<Residue, AAEdgeInfo> implements IGraphModellingLanguageFormat {
    
    
    public static final String CHAINID_ALL_CHAINS = "ALL";
    
    /** PDB identifier. */
    private String pdbid;
    
    
    /** PDB chain identifier. */
    private String chainid;

    
    /** Constructor
     * @param vertices the vertex list to use for the graph */
    public AAGraph(ArrayList<Residue> vertices) {
        super(vertices);
        this.pdbid = "";
        this.chainid = "";
    }
    
    /**
     * Checks whether a residue contact satisifies the minimal sequential residue distance rule.
     * @param minSeqDist the minimal allowed seq dist
     * @param c the contact
     * @return whether the contact satisfies the minimal sequential residue distance rule
     */
    private Boolean checkMinSeqDistance(Integer minSeqDist, ResContactInfo c) {
        if(minSeqDist <= 0) {
             return true;
        } else {
            Residue resA = c.getResA();
            Residue resB = c.getResB();
            if( ! resA.getChainID().equals(resB.getChainID())) {
                //different chains, add contact
                return true;
            }
            else {
                // same chain, gotta check residue distance
                int seqDist = Math.abs(resA.getPdbResNum() - resB.getPdbResNum());
                if(seqDist < minSeqDist) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
    }
    
    /**
     * Checks whether a residue contact satisifies the maximal sequential residue distance rule.
     * @param maxSeqDist the max allowed seq dist
     * @param c the contact
     * @return whether the contact satisfies the maximal sequential residue distance rule
     */
    private Boolean checkMaxSeqDistance(Integer maxSeqDist, ResContactInfo c) {
        if(maxSeqDist <= 0) {
             return true;
        } else {
            Residue resA = c.getResA();
            Residue resB = c.getResB();
            if( ! resA.getChainID().equals(resB.getChainID())) {
                //different chains, do NOT add contact
                return false;
            }
            else {
                // same chain, gotta check residue distance
                int seqDist = Math.abs(resA.getPdbResNum() - resB.getPdbResNum());
                if(seqDist > maxSeqDist) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
    }
    
    /**
     * Returns the maximal degree for any residue of a specific type, for all 20 different residue types (amino acids).
     * @return a map of 1 letter amino acid names to their respective max degree in this graph. Only contains entries for the 20 natural AAs, not for stuff like ligands or other special residues.
     */
    public Map<String, Integer> getMaxDegreeByAminoacidType() {
        Map<String, Integer> maxDegs = new HashMap<>();                
        for(int i = 0; i < 20; i++) {
            maxDegs.put(AminoAcid.names1[i], 0);
        }
        
        Residue r; String name1; Integer deg;
        for(int i = 0; i < this.getNumVertices(); i++) {
            r = this.getVertex(i);
            if(r.isAA()) {
                name1 = r.getAAName1();
                deg = this.neighborsOf(i).size();
                if(maxDegs.containsKey(name1)) {
                    if(maxDegs.get(name1) < deg) {
                        maxDegs.put(name1, deg);
                    }
                }
            }
        }
        return maxDegs;
    }
    
    /**
     * Returns the maximal center sphere radius (which is the Ferret diameter of the residue, NOT including the outer 2A collision hull added to it (on each side)) for any residue of a specific type, for all 20 different residue types (amino acids). Note that the diameter for residues of a specific type may vary based on the residue conformation. Note that it includes 2A around each atom, and is given in 1/10 A.
     * @return a map of 1 letter amino acid names to their max ferret diameter in this graph. Only contains entries for the 20 natural AAs, not for stuff like ligands or other special residues. NOTE that the values are given in 1/10th Angstroem (so 50 means 5 Angstroem).
     */
    public Map<String, Integer> getMaxFerretDiameterByAminoacidType() {
        Map<String, Integer> maxDiameters = new HashMap<>();                
        for(int i = 0; i < 20; i++) {
            maxDiameters.put(AminoAcid.names1[i], 0);
        }
        
        Residue r; String name1; Integer diam;
        for(int i = 0; i < this.getNumVertices(); i++) {
            r = this.getVertex(i);
            if(r.isAA()) {
                name1 = r.getAAName1();
                diam = r.getSphereRadius() * 2;
                if(maxDiameters.containsKey(name1)) {
                    if(maxDiameters.get(name1) < diam) {
                        maxDiameters.put(name1, diam);
                    }
                }
            }
        }
        return maxDiameters;
    }
    
    
    /**
     * Returns the average degree for any residue of a specific type, for all 20 different residue types (amino acids). 
     * @return a map of 1 letter amino acid names to their average degree in this graph. Only contains entries for the 20 natural AAs, not for stuff like ligands or other special residues. 
     */
    public Map<String, Double> getAverageDegreeByAminoacidType() {
        Map<String, Integer> degreeSum = new HashMap<>();
        Map<String, Integer> numValues = new HashMap<>();
        Map<String, Double> avg = new HashMap<>();
        for(int i = 0; i < 20; i++) {
            degreeSum.put(AminoAcid.names1[i], 0);
            numValues.put(AminoAcid.names1[i], 0);
        }
        
        Residue r; String name1; Integer degree;
        for(int i = 0; i < this.getNumVertices(); i++) {
            r = this.getVertex(i);
            if(r.isAA()) {
                name1 = r.getAAName1();
                degree = this.neighborsOf(i).size();
                if(degreeSum.containsKey(name1)) {
                    degreeSum.put(name1, (degreeSum.get(name1) + degree));
                    numValues.put(name1, (numValues.get(name1) + 1));
                }
            }
        }
        
        for(String key : degreeSum.keySet()) {
            avg.put(key, degreeSum.get(key).doubleValue() / numValues.get(key).doubleValue());
        }
        return avg;
    }
    
    
    /**
     * Returns the average center sphere radius (which is the Ferret diameter of the residue, NOT including the outer 2A collision hull added to it (on each side)) for any residue of a specific type, for all 20 different residue types (amino acids). Note that the diameter for residues of a specific type may vary based on the residue conformation. Note that it includes 2A around each atom, and is given in 1/10 A.
     * @return a map of 1 letter amino acid names to their average ferret diameter in this graph. Only contains entries for the 20 natural AAs, not for stuff like ligands or other special residues. NOTE that the values are given in 1/10th Angstroem (so 50 means 5 Angstroem).
     */
    public Map<String, Double> getAverageFerretDiameterByAminoacidType() {
        Map<String, Integer> diameterSum = new HashMap<>();
        Map<String, Integer> numValues = new HashMap<>();
        Map<String, Double> avg = new HashMap<>();
        for(int i = 0; i < 20; i++) {
            diameterSum.put(AminoAcid.names1[i], 0);
            numValues.put(AminoAcid.names1[i], 0);
        }
        
        Residue r; String name1; Integer diam;
        for(int i = 0; i < this.getNumVertices(); i++) {
            r = this.getVertex(i);
            if(r.isAA()) {
                name1 = r.getAAName1();
                diam = r.getSphereRadius() * 2;
                if(diameterSum.containsKey(name1)) {
                    diameterSum.put(name1, (diameterSum.get(name1) + diam));
                    numValues.put(name1, (numValues.get(name1) + 1));
                }
            }
        }
        for(String key : diameterSum.keySet()) {
            avg.put(key, diameterSum.get(key).doubleValue() / numValues.get(key).doubleValue());
        }
        return avg;
    }
    
    private Boolean contactSatisfiesRules(ResContactInfo c) {
        Integer minSeqDist = Settings.getInteger("plcc_I_aag_min_residue_seq_distance_for_contact");
        Integer maxSeqDist = Settings.getInteger("plcc_I_aag_max_residue_seq_distance_for_contact");
        Boolean minSeqDistanceCheckPassed = checkMinSeqDistance(minSeqDist, c);
        Boolean maxSeqDistanceCheckPassed = checkMaxSeqDistance(maxSeqDist, c);
        
        Boolean allChecksPassed = minSeqDistanceCheckPassed && maxSeqDistanceCheckPassed;
        
        return allChecksPassed;
    }
    
    /** Advanced Constructor, constructs the edges automatically from ResContactInfo list
     * @param vertices the vertex list to use
     * @param contacts the contacts, which are used to create the edges of the graph */
    public AAGraph(List<Residue> vertices, ArrayList<ResContactInfo> contacts) {
        super(vertices);
        for(int i = 0; i < contacts.size(); i++) {
            if(contactSatisfiesRules(contacts.get(i))) {
                this.addEdgeFromRCI(contacts.get(i));
            }
        }
        this.pdbid = "";
        this.chainid = "";
    }
    
    /** Constructor
     * @param vertices the vertex list to use
     * @param pdbid the PDB ID of the protein chain represented by this graph (meta data)
     * @param chainid the PDB chain name of the protein chain represented by this graph (meta data) 
     */
    public AAGraph(ArrayList<Residue> vertices, String pdbid, String chainid) {
        super(vertices);
        this.setPdbid(pdbid);
        this.setChainid(chainid);
    }
    
    
    /**
     * Counts the number of amino acids of all types in the protein (how many ARG, how many PHE, ...).
     * Note that only the 20 natural AAs are considered (ligands and PDB special AAs like 'unknown' are ignored.
     * 
     * @return a list of the AA type counts. The index in the list is the PTGL-style AA identifier (see the AminoAcid class).
     * This means that you can get the count for "ALA" by: count_ala = counts[name3ToID("ALA")];.
     * Because the internal IDs start with 1, the 0 field is not required for the AA types. It contains the total number of valid AAs.
     */
    public int[] getAATypeCountsIncludingSum() {
        int[] counts = new int[21];
        Arrays.fill(counts, 0);
        
        Integer residueTypeID;        
        for(Residue r : this.vertices) {
            residueTypeID = r.getInternalAAID();
            if(residueTypeID > 0 && residueTypeID < counts.length) {
                counts[residueTypeID]++;
                counts[0]++;    // total number of valid AAs
            }
        }                
        
        return counts;
    }
    
    
    /**
     * Returns residue counts for each AA type.
     * @return residue counts for each AA type. Length is 20, AA order is that of AminoAcid.name3 list.
     */
    public int[] getAATypeCountsNoSum() {
        int[] countsSum = this.getAATypeCountsIncludingSum();
        int[] counts = new int[20];
        for(int i = 1; i < countsSum.length; i++) {
            counts[i-1] = countsSum[i];
        }
        return counts;
    }
    
    /**
     * Computes a matrix of the number of interactions for each AA type, e.g., how many interactions exist for this
     * protein between AAs of the types ARG and LYS, ARG and ARG, ... This returns a 21*21 matrix, ignore the 0 lines (matrix[0][whatever] and matrix[whatever][0]). The reason for this to start at 1
     * is that it uses the internal PTGL AA type identifier (which starts at 1 for historical/compatibility reasons).
     
     * @param computeCountsForAllAminoAcidTypes if this parameter is set to true, then the counts for all AA types are also computed. THESE ARE NOT CONTACT COUNTS! They are AA counts, i.e., how many times does valine occur in the protein! They
     * are written to the 0 fields of the first matrix dimension (matrix[0][AAtypeID]). The field matrix[0][0] contains the total number of valid (natural) amino acids in the residue list.
     * If this parameter is false, the count computation is skipped and all these fields contain zeros.
     * 
     * @return a matrix of dimension 21x21 which describes the number of contacts between the 20 natural amino acid types (LYS, ARG, ...) for this graph (or protein chain). The indices in the list correspond to the internal AA ID for that type, i.e., the amino acids in the lists start at index 1 and you can get
     * their names using the AminoAcid.intIDToName3() function. The 0 fields of the 2nd dimension (matrix[AAtypeID][0]) contain the sum of all 
     * contacts (no matter the type of the other AA) for the given AA type.
     * 
     */
    public int[][] getAminoAcidTypeInteractionMatrix(boolean computeCountsForAllAminoAcidTypes) {
                
        
        int[][] matrix = new int[21][21];
        for (int i = 0; i < matrix.length; i++) {
            Arrays.fill(matrix[i], 0);
        }
        
        List<Integer[]> allEdges = this.getEdgeListIndex();
        int numIgnored = 0;
        for(Integer[] edge : allEdges) {
            Integer resAPtglAAtype = this.vertices.get(edge[0]).getInternalAAID();
            Integer resBPtglAAtype = this.vertices.get(edge[1]).getInternalAAID();
            
            if(resAPtglAAtype > 0 && resAPtglAAtype < matrix.length) {
                if(resBPtglAAtype > 0 && resBPtglAAtype < matrix.length) {
                    matrix[resAPtglAAtype][resBPtglAAtype]++;
                    matrix[resBPtglAAtype][resAPtglAAtype]++;
                    
                    matrix[resAPtglAAtype][0]++;    // total contacts of AA type resA
                    matrix[resBPtglAAtype][0]++;    // total contacts of AA type resB                
                                   
                }                
                else {
                    numIgnored++;
                }
            }                        
            else {
                numIgnored++;
            }
        }
        
        if(computeCountsForAllAminoAcidTypes) {
            int[] counts = getAATypeCountsIncludingSum();
            matrix[0][0] = counts[0];   // total number of AAs
            
            for(int i = 1; i < counts.length; i++) {
                matrix[0][i] = counts[i];   // fill in number of AAs of specific type
            }
            
        }
        
        return matrix;
    }
    
    
    /**
     * Computes the sum of contacts in the AAG for each amino acid type. E.g., how many contacts does val, ala, etc... have with AAs of any type. 
     * @return an array with the counts for each aa type. The order in the array is the internal PTGL AA type identifier. The PTGL AA type identifier starts at 1 instead of 0, so this is shifted by 1.
     */
    public int[] getTotalContactCountByAAType() {
        int[][] iim = this.getAminoAcidTypeInteractionMatrix(true);
        int[] countsByType = new int[20];
        for(int i = 0; i < countsByType.length; i++) {
            countsByType[i] = iim[i+1][0];
        }
        return countsByType;
    }
    
    
    /**
     * Computes a GML representation of the amino acid contact statistics matrix. GML is the Graph Modeling Language format.
     * This uses the getAminoAcidTypeInteractionMatrix() function to compute the matrix.
     * @return a GML string representation of the amino acid contact stats matrix
     */
    public String getAminoAcidTypeInteractionMatrixGML() {
        int[][]matrix = this.getAminoAcidTypeInteractionMatrix(false);
        
        StringBuilder gml = new StringBuilder();
        
        
        String label_pdbid = (this.pdbid == null ? "" : " PDB " + this.pdbid);
        String label_chainid = ((this.chainid == null ||  this.chainid.equals(AAGraph.CHAINID_ALL_CHAINS)) ? "" : " chain " + this.chainid);
        // print the header

        String startNode = "  node [";
        String endNode   = "  ]";
        String startEdge = "  edge [";
        String endEdge   = "  ]";
        
        gml.append("graph [\n");
        gml.append("  id ").append(1).append("\n");
        gml.append("  label \"" + "VPLG Amino acid contact stats matrix ").append(label_pdbid).append(label_chainid).append("\"\n");
        gml.append("  comment \"" + "VPLG Amino acid contact stats matrix  ").append(label_pdbid).append("\"\n");
        gml.append("  directed 0\n");
        gml.append("  isplanar 0\n");
        gml.append("  creator \"PLCC\"\n");
        gml.append("  pdb_id \"").append(this.pdbid).append("\"\n");
        gml.append("  chain_id \"").append(this.chainid).append("\"\n");
        gml.append("  graph_type \"" + "aa_graph" + "\"\n");
        gml.append("  is_protein_graph 1\n");
        gml.append("  is_folding_graph 0\n");
        gml.append("  is_SSE_graph 0\n");
        gml.append("  is_AA_graph 1\n");
        gml.append("  is_AA_type_contact_matrix 1\n");
        gml.append("  is_all_chains_graph ").append(this.isAllChainsGraph() ? "1" : "0").append("\n");
        
        
        // print the 20 vertices -- one for each of the 20 amino acid types
        for(Integer i = 1; i < matrix.length; i++) {
            gml.append(startNode).append("\n");
            gml.append("    id ").append(i).append("\n");
            String aaType = AminoAcid.intIDToName3(i);
            gml.append("    label \"").append(aaType).append("\"\n");
            gml.append(endNode).append("\n");
        }
        
        // print the edges with labels -- an edge label is the number of contacts between AAs of the given types
        Integer src, tgt, numContacts;
        List<Integer[]> allEdges = this.getEdgeListIndex();
        for(Integer i = 1; i < matrix.length; i++) {
            for(Integer j = 1; j < matrix[0].length; j++) {
                src = i;
                tgt = j;
                numContacts = matrix[src][tgt];
                
                if(numContacts > 0) {
                    gml.append(startEdge).append("\n");
                    gml.append("    source ").append(src).append("\n");
                    gml.append("    target ").append(tgt).append("\n");                        
                    gml.append("    weight ").append(numContacts).append("\n");                        
                    gml.append(endEdge).append("\n");
                }
            }
        }
            
        // print footer (close graph)
        gml.append("]\n");
        
        return(gml.toString());        
    }
    
    /**
     * Determines whether this graph is for all chains of a PDB file or only for a single chain.
     * @return true if it is for all chains, false otherwise
     */
    public boolean isAllChainsGraph() {
        if(this.chainid.equals(AAGraph.CHAINID_ALL_CHAINS)) {
            return true;
        }
        return false;
    }
    
    /**
     * Automatically adds an edge from a ResContactInfo object if applicable. Note that the edge is only added if the RCI describes a contact.
     * @param rci the ResContactInfo object, must be for 2 residues which are part of this graph
     * @return true if the edge was added, false otherwise
     */
    public final boolean addEdgeFromRCI(ResContactInfo rci) {
        if(rci.describesAnyContact()) {            
            Residue resA = rci.getResA();
            Residue resB = rci.getResB();

            int indexResA = this.getVertexIndex(resA);
            int indexResB = this.getVertexIndex(resB);
            if(indexResA >= 0 && indexResB >= 0) {
                if(rci.describesAnyContact()) {
                    AAEdgeInfo ei = new AAEdgeInfo(rci);
                    this.addEdge(indexResA, indexResB, ei);
                    return true;
                }
                else {
                    return false;                           
                }
            }
            else {
                Boolean notFoundIsorAreLigands = Boolean.FALSE;
                StringBuilder sb = new StringBuilder();
                sb.append("addEdgeFromRCI: Could not add edge from ResContactInfo between vertices " + resA.getFancyName() + " and " + resB.getFancyName() + ".");
                if(indexResA < 0 && indexResB < 0) {
                    sb.append(" BOTH residues not found.\n");
                    if( (! resA.isAA()) && (! resB.isAA())) { notFoundIsorAreLigands = Boolean.TRUE; }
                }
                else {
                    if(indexResA < 0) {
                        sb.append(" FIRST residue not found.\n");
                        if( ! resA.isAA() ) { notFoundIsorAreLigands = Boolean.TRUE; }
                    }
                    else {  // indexResA < 0
                        sb.append(" SECOND residue not found.\n");
                        if( ! resB.isAA() ) { notFoundIsorAreLigands = Boolean.TRUE; }
                    }
                }
                if( ! notFoundIsorAreLigands) { // only warn for non-ligand residues which were not found.
                    DP.getInstance().w("AAGraph", sb.toString());
                }                
                return false;
            }
        }
        return false;
    }
    
    /** Constructor */
    public AAGraph() {
        super();
    }
    
    /**
     * Find residues based on chain, PDB residue number, and icode. You can set these properties to null to have them ignored. Example: If you set chain to "A" and the rest to null, you will get all residues from chain A. If you specify values for all 3 fields, the result set should have size 1.
     * @param chain the PDB chain name (all are accepted if set to null)
     * @param pdbresnum the PDB residue number (all are accepted if set to null)
     * @param icode the PDB iCode field (all are accepted if set to null)
     * @return a list of residues that match the criteria
     */
    public Set<Integer> findResidues(String chain, Integer pdbresnum, String icode){
        Set<Integer> res = new HashSet<>();
        int resIndex = 0;
        for(Residue r : this.vertices) {
            if((chain == null || r.getChainID().equals(chain)) && (pdbresnum == null || r.getPdbResNum().equals(pdbresnum)) && (icode == null || r.getiCode().equals(icode))) {
                res.add(resIndex);
                resIndex++;
            }
        }
        return res;
    }
   
    /** Returns a Graph Modelling Language format representation of this graph.
     *  See http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf for the publication 
     * and http://en.wikipedia.org/wiki/Graph_Modelling_Language for a brief description.
     * 
     * @return the GML string
     */
    @Override
    public String toGraphModellingLanguageFormat() {
        
        StringBuilder gmlf = new StringBuilder();
        
        
        String label_pdbid = (this.pdbid == null ? "" : " PDB " + this.pdbid);
        String label_chainid = ((this.chainid == null ||  this.chainid.equals(AAGraph.CHAINID_ALL_CHAINS)) ? "" : " chain " + this.chainid);
        // print the header

        String startNode = "  node [";
        String endNode   = "  ]";
        String startEdge = "  edge [";
        String endEdge   = "  ]";
        
        gmlf.append("graph [\n");
        gmlf.append("  id ").append(1).append("\n");
        gmlf.append("  label \"" + "VPLG Protein Graph ").append(label_pdbid).append(label_chainid).append("\"\n");
        gmlf.append("  comment \"" + "VPLG Protein Graph ").append(label_pdbid).append("\"\n");
        gmlf.append("  directed 0\n");
        gmlf.append("  isplanar 0\n");
        gmlf.append("  creator \"PLCC\"\n");
        gmlf.append("  pdb_id \"").append(this.pdbid).append("\"\n");
        gmlf.append("  chain_id \"").append(this.chainid).append("\"\n");
        gmlf.append("  graph_type \"" + "aa_graph" + "\"\n");
        gmlf.append("  is_protein_graph 1\n");
        gmlf.append("  is_folding_graph 0\n");
        gmlf.append("  is_SSE_graph 0\n");
        gmlf.append("  is_AA_graph 1\n");
        gmlf.append("  is_AA_type_contact_matrix 0\n");
        gmlf.append("  is_all_chains_graph ").append(this.isAllChainsGraph() ? "1" : "0").append("\n");
        
        
        // print all nodes
        Residue residue;
        for(Integer i = 0; i < this.getNumVertices(); i++) {
            residue = this.vertices.get(i);
            gmlf.append(startNode).append("\n");
            gmlf.append("    id ").append(i).append("\n");
            gmlf.append("    label \"").append(i).append("-").append(residue.getUniquePDBName()).append("\"\n");
            gmlf.append("    residue \"").append(residue.getName3()).append("\"\n");
            gmlf.append("    chem_prop5 \"").append(residue.getChemicalProperty5OneLetterString()).append("\"\n");
            gmlf.append("    chem_prop3 \"").append(residue.getChemicalProperty3OneLetterString()).append("\"\n");
            gmlf.append("    sse \"").append(residue.getNonEmptySSEString()).append("\"\n");
            gmlf.append("    sse_type \"").append(residue.getNonEmptySSEString()).append("\"\n");   // required for graphlet analyser
            gmlf.append("    chain \"").append(residue.getChainID()).append("\"\n");            
            gmlf.append(endNode).append("\n");
        }
        
        // print all edges
        Integer src, tgt;
        List<Integer[]> allEdges = this.getEdgeListIndex();
        for(Integer[] edge : allEdges) {
            src = edge[0];
            tgt = edge[1];
            
            gmlf.append(startEdge).append("\n");
            gmlf.append("    source ").append(src).append("\n");
            gmlf.append("    target ").append(tgt).append("\n");                        
            gmlf.append("    weight ").append(this.getEdgeDistance(src, tgt)).append("\n");                        
            gmlf.append("    spatial \"").append("m").append("\"\n");   // required for graphlet analyser
            gmlf.append(endEdge).append("\n");
        }
        
        // print footer (close graph)
        gmlf.append("]\n");
        
        return(gmlf.toString());
    }
    
    /**
     * Creates a simple text represenation of the graph used by the FANMOD software and also maps the indices from the standard AAGraph to this simple representation.
     * The standard AAGraph format does not suit the required input format for the FANMOD software. (See: http://theinf1.informatik.uni-jena.de/~wernicke/motifs/fanmod-manual.pdf
     * for more details)
     * This will also return a string that maps the index a vertex has in the standard AAGraph format to the index the vertex now has in the simple representation.
     * @return two strings, one representing the graph to be used by the FANMOD software and the other mapping the vertices index from the standard AAGraph to this simple representation.
     */
    public ArrayList<String> toFanMod() {
        StringBuilder sbAag = new StringBuilder();
        StringBuilder sbIdx = new StringBuilder();
        Residue residue;
        HashMap<Integer, Integer> indexTable = new HashMap<Integer, Integer>();
        
        // Header for index file
        sbIdx.append("# SimpleGraphID, AAGraphID, PDBResNum, PDBResName");
        sbIdx.append(System.lineSeparator());
        
        
        List<Integer[]> allEdges = this.getEdgeListIndex();
        
        // Create a list that contains all the vertices that contribute to edges
        ArrayList<Integer> allVerticesFromEdges = new ArrayList<Integer>();
        for(Integer[] edge : allEdges) {
            allVerticesFromEdges.add(edge[0]);
            allVerticesFromEdges.add(edge[1]);
        }
        
        // Get rid of redundant entries from the allVerticesFromEdges list (make a set out of it) and then sort and convert it to a new list.
        // This is done because the FANMOD software wants a graph whose vertices are labelled as consecutive integers with the lowest integer
        // being zero.
        HashSet<Integer> tmpHashSet = new HashSet<Integer>(allVerticesFromEdges);   // gets rid off non-redundant entries
        TreeSet<Integer> tmpTreeSet = new TreeSet<Integer>(tmpHashSet);             // sorts the set
        ArrayList<Integer> sortedList = new ArrayList<Integer>(tmpTreeSet);         // converts the sorted, non-redundant set back to an ArrayList
        
        // Save the vertex ID from the AAGraph and corresponding vertex ID from the simple AAGraph in the HashMap.
        // Also build a string that contains those IDs, the PDBResNum and the PDBResName to be able to identify the
        // vertices from the simple AAGraph. This string will be saved in the *_aag_simple.id file
        for(int i = 0; i < sortedList.size(); i++) {
            indexTable.put(sortedList.get(i), i);
            
            residue = this.vertices.get(sortedList.get(i));
            
            sbIdx.append(i);
            sbIdx.append(",");
            sbIdx.append(sortedList.get(i));
            sbIdx.append(",");
            sbIdx.append(residue.getPdbResNum());
            sbIdx.append(",");
            sbIdx.append(residue.getResName3());
            sbIdx.append(System.lineSeparator());
        }
       
        // Build string for the *_aag_simple.fanmod file.
        Integer src, tgt;
        for(Integer[] edge : allEdges) {
            src = edge[0];
            tgt = edge[1];
            
            sbAag.append(indexTable.get(src));
            sbAag.append(" ");
            sbAag.append(indexTable.get(tgt));
            sbAag.append(System.lineSeparator());
        }
        
        
        ArrayList<String> output = new ArrayList<String>();
        output.add(sbAag.toString());
        output.add(sbIdx.toString());
        
        return(output);
    }
    
    
    
    
    /**
     * Returns the 3D euclidian distance between the Residues at indices i and j in this graph.
     * @param i the residue i by index
     * @param j the residue j by index
     * @return the euclidian distance
     */
    public int getEdgeDistance(int i, int j) {
        return this.getVertex(i).resDistTo(this.getVertex(j));
    }
    
    /**
     * Determines whether this graph contains AAs from multiple chains or a single chain only. This 
     * simply checks the chainid string, it does not look at each residue or something.
     * @return true if this is a multi-chain graph, false otherwise
     */
    public boolean isMultiChainGraph() {
        return this.chainid.equals(AAGraph.CHAINID_ALL_CHAINS);
    }
    
    // simple getters and setters follow, auto-generated
    public String getPdbid() {
        return pdbid;
    }

    public final void setPdbid(String pdbid) {
        this.pdbid = pdbid;
    }

    public String getChainid() {
        return chainid;
    }

    public final void setChainid(String chainid) {
        this.chainid = chainid;
    }
    
    public List<Residue> getResiduesFromSetByIndex(Set<Integer> vertIndices) {
        List<Residue> l = new ArrayList<>();
        for(int index : vertIndices) {
            l.add(this.vertices.get(index));
        }
        return l;
    }
}
