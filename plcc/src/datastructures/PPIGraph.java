/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package datastructures;

import graphformats.IGraphModellingLanguageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import plccSettings.Settings;
import proteingraphs.MolContactInfo;
import proteinstructure.Residue;
import proteinstructure.Molecule;
import proteinstructure.Ligand;
import proteinstructure.RNA;
import tools.DP;

/**
 * An undirected, adjacency list based amino acid graph that models
 * protein-protein interactions. Holds the amino acids of all chains that are
 * part of an PPI.
 *
 * @author as
 */
public class PPIGraph extends SparseGraph<Molecule, AAEdgeInfo> implements IGraphModellingLanguageFormat {

    /**
     * PDB identifier.
     */
    private String pdbid;

    /**
     * PDB chain identifier.
     */
    private String chainid;

    private Boolean contactSatisfiesRules(MolContactInfo c) {
        Integer minSeqDist = Settings.getInteger("plcc_I_aag_min_residue_seq_distance_for_contact");
        Integer maxSeqDist = Settings.getInteger("plcc_I_aag_max_residue_seq_distance_for_contact");
        Boolean minSeqDistanceCheckPassed = checkMinSeqDistance(minSeqDist, c);
        Boolean maxSeqDistanceCheckPassed = checkMaxSeqDistance(maxSeqDist, c);

        Boolean allChecksPassed = minSeqDistanceCheckPassed && maxSeqDistanceCheckPassed;

        return allChecksPassed;
    }

    /**
     * Checks whether a residue contact satisfies the minimal sequential
     * residue distance rule.
     *
     * @param minSeqDist the minimal allowed seq dist
     * @param c the contact
     * @return whether the contact satisfies the minimal sequential residue
     * distance rule
     */
    private Boolean checkMinSeqDistance(Integer minSeqDist, MolContactInfo c) {
        if (minSeqDist <= 0) {
            return true;
        } else {
            Molecule molA = c.getMolA();
            Molecule molB = c.getMolB();
            if (!molA.getChainID().equals(molB.getChainID())) {
                //different chains, add contact
                return true;
            } else {
                // same chain, gotta check residue distance
                int seqDist = Math.abs(molA.getPdbNum() - molB.getPdbNum());
                if (seqDist < minSeqDist) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    /**
     * Checks whether a residue contact satisfies the maximal sequential
     * residue distance rule.
     *
     * @param maxSeqDist the max allowed seq dist
     * @param c the contact
     * @return whether the contact satisfies the maximal sequential residue
     * distance rule
     */
    private Boolean checkMaxSeqDistance(Integer maxSeqDist, MolContactInfo c) {
        if (maxSeqDist <= 0) {
            return true;
        } else {
            Molecule molA = c.getMolA();
            Molecule molB = c.getMolB();
            if (!molA.getChainID().equals(molB.getChainID())) {
                //different chains, do NOT add contact
                return false;
            } else {
                // same chain, gotta check residue distance
                int seqDist = Math.abs(molA.getPdbNum() - molB.getPdbNum());
                if (seqDist > maxSeqDist) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    /**
     * Advanced Constructor, constructs the edges automatically from MolContactInfo list
     *
     * @param vertices the vertex list to use
     * @param contacts the contacts, which are used to create the edges of the graph
     */
    public PPIGraph(List<Molecule> vertices, ArrayList<MolContactInfo> contacts) {
        super(vertices);
        for (int i = 0; i < contacts.size(); i++) {
            if (contactSatisfiesRules(contacts.get(i))) {
                this.addPPIEdgeFromRCI(contacts.get(i));
            }
        }
        this.pdbid = "";
        this.chainid = "";
    }

    /**
     * Automatically adds an edge from a MolContactInfo object if applicable.
     * Note that the edge is only added if the RCI describes a contact.
     *
     * @param rci the MolContactInfo object, must be for 2 residues which are
 part of this graph
     * @return true if the edge was added, false otherwise
     */
    public final boolean addPPIEdgeFromRCI(MolContactInfo rci) {
        if (rci.describesPPIContact()) {
            Molecule resA = rci.getResA();
            Molecule resB = rci.getResB();
            
            // check that both are residues (null if e.g. RNA)
            if (resA == null || resB == null) {
                return false;
            }

            int indexResA = this.getVertexIndex(resA);
            int indexResB = this.getVertexIndex(resB);
            if (indexResA >= 0 && indexResB >= 0) {
                if (rci.describesPPIContact()) {
                    AAEdgeInfo ei = new AAEdgeInfo(rci);
                    this.addEdge(indexResA, indexResB, ei);
                    return true;
                } else {
                    return false;
                }
            } else {
                Boolean notFoundIsorAreLigands = Boolean.FALSE;
                StringBuilder sb = new StringBuilder();
                sb.append("addEdgeFromRCI: Could not add edge from ResContactInfo between vertices " + resA.getFancyName() + " and " + resB.getFancyName() + ".");
                if (indexResA < 0 && indexResB < 0) {
                    sb.append(" BOTH residues not found.\n");
                    if ((!resA.isAA()) && (!resB.isAA())) {
                        notFoundIsorAreLigands = Boolean.TRUE;
                    }
                } else {
                    if (indexResA < 0) {
                        sb.append(" FIRST residue not found.\n");
                        if (!resA.isAA()) {
                            notFoundIsorAreLigands = Boolean.TRUE;
                        }
                    } else {  // indexResA < 0
                        sb.append(" SECOND residue not found.\n");
                        if (!resB.isAA()) {
                            notFoundIsorAreLigands = Boolean.TRUE;
                        }
                    }
                }
                if (!notFoundIsorAreLigands) { // only warn for non-ligand residues which were not found.
                    DP.getInstance().w("AAGraph", sb.toString());
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Returns a Graph Modelling Language format representation of this graph.
     * See
     * http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf
     * for the publication and
     * http://en.wikipedia.org/wiki/Graph_Modelling_Language for a brief
     * description.
     *
     * @return the GML string
     */
    @Override
    public String toGraphModellingLanguageFormat() {

        StringBuilder gmlf = new StringBuilder();

        String label_pdbid = (this.pdbid == null ? "" : " PDB " + this.pdbid);
        String label_chainid = ((this.chainid == null || this.chainid.equals(AAGraph.CHAINID_ALL_CHAINS)) ? "" : " chain " + this.chainid);
        // print the header

        String startNode = "  node [";
        String endNode = "  ]";
        String startEdge = "  edge [";
        String endEdge = "  ]";

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
        Residue residue ;
        Molecule molecule;
        for (Integer i = 0; i < this.getNumVertices(); i++) {
            molecule = this.vertices.get(i);
            gmlf.append(startNode).append("\n");
            gmlf.append("    id ").append(i).append("\n");
            gmlf.append("    label \"").append(i).append("-").append(molecule.getUniquePDBName()).append("\"\n");
            gmlf.append("    chain \"").append(molecule.getChainID()).append("\"\n"); 
                
            if(molecule instanceof Residue){
                residue = (Residue) molecule;
                gmlf.append("    chem_prop5 \"").append(residue.getChemicalProperty5OneLetterString()).append("\"\n");
                gmlf.append("    chem_prop3 \"").append(residue.getChemicalProperty3OneLetterString()).append("\"\n");
                gmlf.append("    sse \"").append(residue.getNonEmptySSEString()).append("\"\n");
                gmlf.append("    sse_type \"").append(residue.getNonEmptySSEString()).append("\"\n");   // required for graphlet analyser
            
            }
            else if (molecule instanceof RNA) {
                gmlf.append("    rna \"");
            }
            else {
                gmlf.append("    lig \"");
            }
            gmlf.append(endNode).append("\n");
        }

        // print all edges
        Integer src, tgt;
        List<Integer[]> allEdges = this.getEdgeListIndex();
        for (Integer[] edge : allEdges) {
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

        return (gmlf.toString());
    }

    /**
     * Returns the 3D euclidian distance between the Residues at indices i and j
     * in this graph.
     *
     * @param i the residue i by index
     * @param j the residue j by index
     * @return the euclidian distance
     */
    public int getEdgeDistance(int i, int j) {
        return this.getVertex(i).distTo(this.getVertex(j));
    }

    /**
     * Determines whether this graph is for all chains of a PDB file or only for
     * a single chain.
     *
     * @return true if it is for all chains, false otherwise
     */
    public boolean isAllChainsGraph() {
        if (this.chainid.equals(AAGraph.CHAINID_ALL_CHAINS)) {
            return true;
        }
        return false;
    }

    public String getChainid() {
        return chainid;
    }

    public final void setChainid(String chainid) {
        this.chainid = chainid;
    }

    public final void setPdbid(String pdbid) {
        this.pdbid = pdbid;
    }

    /**
     * Creates a simple text represenation of the graph used by the FANMOD
     * software and also maps the indices from the standard AAGraph to this
     * simple representation. The standard AAGraph format does not suit the
     * required input format for the FANMOD software. (See:
     * http://theinf1.informatik.uni-jena.de/~wernicke/motifs/fanmod-manual.pdf
     * for more details) This will also return a string that maps the index a
     * vertex has in the standard AAGraph format to the index the vertex now has
     * in the simple representation.
     *
     * @return two strings, one representing the graph to be used by the FANMOD
     * software and the other mapping the vertices index from the standard
     * AAGraph to this simple representation.
     */
    public ArrayList<String> toFanMod() {
        StringBuilder sbAag = new StringBuilder();
        StringBuilder sbIdx = new StringBuilder();
        Molecule molecule;
        HashMap<Integer, Integer> indexTable = new HashMap<Integer, Integer>();

        // Header for index file
        sbIdx.append("# SimpleGraphID, AAGraphID, PDBResNum, PDBResName");
        sbIdx.append(System.lineSeparator());

        List<Integer[]> allEdges = this.getEdgeListIndex();

        // Create a list that contains all the vertices that contribute to edges
        ArrayList<Integer> allVerticesFromEdges = new ArrayList<Integer>();
        for (Integer[] edge : allEdges) {
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
        for (int i = 0; i < sortedList.size(); i++) {
            indexTable.put(sortedList.get(i), i);

            molecule = this.vertices.get(sortedList.get(i));

            sbIdx.append(i);
            sbIdx.append(",");
            sbIdx.append(sortedList.get(i));
            sbIdx.append(",");
            sbIdx.append(molecule.getPdbNum());
            sbIdx.append(",");
            sbIdx.append(molecule.getName3());
            sbIdx.append(System.lineSeparator());
        }

        // Build string for the *_aag_simple.fanmod file.
        Integer src, tgt;
        for (Integer[] edge : allEdges) {
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

        return (output);
    }
}
