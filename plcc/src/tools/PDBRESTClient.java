/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2014. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package tools;

/**
 * A REST client to access the RCSB PDB API. See http://www.rcsb.org/pdb/software/rest.do for API documentation.
 * @author spirit
 */
public class PDBRESTClient extends RESTClient {
    
    public static final String defaultRestUrl = "http://www.rcsb.org";
    public static final String defaultRestPath = "/pdb/rest/";

    public PDBRESTClient(String restUrl, String restPath) {
        super(restUrl, restPath);
    }
    
    public PDBRESTClient() {
        super(PDBRESTClient.defaultRestUrl, PDBRESTClient.defaultRestPath);
    }
        
    private static String getQueryStringPfamOfChain(String pdbid) {
        return "hmmer?structureId=" + pdbid;
    }
    
    private static String getQueryStringRepresDomainsOfChain(String pdbid, String chain) {
        return "representativeDomains?structureId=" + pdbid + "." + chain;
    }
    
    private static String getQueryStringSeqCluster40OfChain(String pdbid, String chain) {
        return "sequenceCluster?cluster=40&structureId=" + pdbid + "." + chain;
    }
    
    public static String getQueryStringGOTermsOfChain(String pdbid, String chain) {
        return "goTerms?structureId=" + pdbid + "." + chain;
    }
}
