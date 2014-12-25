/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2014. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package tools;

import java.io.IOException;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author spirit
 */
public class PTGL3RESTClient extends RESTClient {

    public static final String defaultRestUrl = "http://ptgl.uni-frankfurt.de";
    public static final String defaultRestPath = "/api/index.php/";
   
    /**
     * Constructor which uses the default URL and path for the PTGL3 web service.
     */
    public PTGL3RESTClient() {
        super(PTGL3RESTClient.defaultRestUrl, PTGL3RESTClient.defaultRestPath);
    }
    
    public PTGL3RESTClient(String restUrl, String restPath) {
        super(restUrl, restPath);
    }
            
    /**
     * Queries a PG from the REST web service in JSON format.
     * @param pdbid
     * @param chain
     * @param graphType
     * @return
     * @throws IOException 
     */
    public String doProteinGraphQueryJSON(String pdbid, String chain, String graphType) throws IOException {
        return doRequestGET(PTGL3RESTClient.getQueryStringProteinGraph(pdbid, chain, graphType, "json"), MediaType.APPLICATION_JSON_TYPE);
    }
            
    /**
     * Builds a PG query string.
     * @param pdbid
     * @param chain
     * @param graphType
     * @param graphFormat
     * @return 
     */
    private static String getQueryStringProteinGraph(String pdbid, String chain, String graphType, String graphFormat) {
        return "pg/" + pdbid + "/" + chain + "/" + graphType + "/" + graphFormat;
    }
    
    /**
     * Builds a FG query string.
     * @param pdbid
     * @param chain
     * @param graphType
     * @param foldNumber
     * @param graphFormat
     * @return 
     */
    private static String getQueryStringFoldingGraph(String pdbid, String chain, String graphType, Integer foldNumber, String graphFormat) {
        return "fg/" + pdbid + "/" + chain + "/" + graphType + "/" + foldNumber + "/" + graphFormat;
    }
    
    /**
     * Test main only
     * @param args ignored
     */
    public static void main(String[] args) {
        PTGL3RESTClient c = new PTGL3RESTClient();
        String json = null;
        try {
            json = c.doProteinGraphQueryJSON("7tim", "A", "albe");
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
        
        System.out.println("json: " + json);
    }
}
