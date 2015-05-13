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
import plcc.ResContactInfo;
import proteinstructure.Residue;

/**
 * An undirected, adjacency list based amino acid graph. Suitable for large, sparse graphs. 
 * Holds the AAs for a single chain or multiple chains.
 * 
 * @author ts
 */
public class AAGraph extends SparseGraph<Residue, AAEdgeInfo> implements IGraphModellingLanguageFormat {
    
    
    public static final String CHAINID_ALL_CHAINS = "ALL_CHAINS";
    
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
    
    /** Advanced Constructor, constructs the edges automatically from ResContactInfo list
     * @param vertices the vertex list to use
     * @param contacts the contacts, which are used to create the edges of the graph */
    public AAGraph(ArrayList<Residue> vertices, ArrayList<ResContactInfo> contacts) {
        super(vertices);
        for(int i = 0; i < contacts.size(); i++) {
            this.addEdgeFromRCI(contacts.get(i));
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
    public int[] getAATypeCounts() {
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
     * Computes a matrix of the number of interactions for each AA type, e.g., how many interactions exist for this
     * protein between AAs of the types ARG and LYS, ARG and ARG, ... This returns a 21*21 matrix, ignore the 0 lines (matrix[0][whatever] and matrix[whatever][0]). The reason for this to start at 1
     * is that it uses the internal PTGL AA type identifier (which starts at 1 for historical/compatibility reasons).
     
     * @param computeCountsForAllAminoAcidTypes if this parameter is set to true, then the counts for all AA types are also computed. They
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
        
        ArrayList<Integer[]> allEdges = this.getEdgeListIndex();
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
            int[] counts = getAATypeCounts();
            matrix[0][0] = counts[0];   // total number of AAs
            
            for(int i = 1; i < counts.length; i++) {
                matrix[0][i] = counts[i];   // fill in number of AAs of specific type
            }
            
        }
        
        return matrix;
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
        ArrayList<Integer[]> allEdges = this.getEdgeListIndex();
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
                System.err.println("WARNING: Could not add edge from ResContactInfo, vertices not found in graph.");
                return false;
            }
        }
        return false;
    }
    
    /** Constructor */
    public AAGraph() {
        super();
    }
    
   
    /** Returns a Graph Modelling Language format representation of this graph.
     *  See http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf for the publication 
     * and http://en.wikipedia.org/wiki/Graph_Modelling_Language for a brief description.
     * 
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
            gmlf.append("    chem_prop \"").append(residue.getChemicalProperty1LetterString()).append("\"\n");
            gmlf.append("    sse \"").append(residue.getNonEmptySSEString()).append("\"\n");
            gmlf.append("    sse_type \"").append(residue.getNonEmptySSEString()).append("\"\n");   // required for graphlet analyser
            gmlf.append("    chain \"").append(residue.getChainID()).append("\"\n");            
            gmlf.append(endNode).append("\n");
        }
        
        // print all edges
        Integer src, tgt;
        ArrayList<Integer[]> allEdges = this.getEdgeListIndex();
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
     * Returns the 3D euclidian distance between the Residues at indices i and j in this graph.
     * @param i the residue i by index
     * @param j the residue j by index
     * @return the euclidian distance
     */
    public int getEdgeDistance(int i, int j) {
        return this.getVertex(i).resCenterDistTo(this.getVertex(j));
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
}
