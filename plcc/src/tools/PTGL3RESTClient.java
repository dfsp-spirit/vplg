/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
   
    public PTGL3RESTClient() {
        super(PTGL3RESTClient.defaultRestUrl, PTGL3RESTClient.defaultRestPath);
    }
    
    public PTGL3RESTClient(String restUrl, String restPath) {
        super(restUrl, restPath);
    }
            
    
    public String doProteinGraphQueryJSON(String pdbid, String chain, String graphType) throws IOException {
        return doRequestGET(PTGL3RESTClient.getQueryStringProteinGraph(pdbid, chain, graphType, "json"), MediaType.APPLICATION_JSON_TYPE);
    }
            
    
    private static String getQueryStringProteinGraph(String pdbid, String chain, String graphType, String graphFormat) {
        return "pg/" + pdbid + "/" + chain + "/" + graphType + "/" + graphFormat;
    }
    
    private static String getQueryStringFoldingGraph(String pdbid, String chain, String graphType, Integer foldNumber, String graphFormat) {
        return "fg/" + pdbid + "/" + chain + "/" + graphType + "/" + foldNumber + "/" + graphFormat;
    }
}
