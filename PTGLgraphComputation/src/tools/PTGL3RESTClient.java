/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2014. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package tools;

import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author spirit
 */
public class PTGL3RESTClient extends RESTClient {

    public static final String defaultScheme = "http";
    public static final String defaultRestUrl = "ptgl.uni-frankfurt.de";
    public static final String defaultRestPath = "/api/index.php/";
   
    /**
     * Constructor which uses the default URL and path for the PTGL3 web service.
     */
    public PTGL3RESTClient() {
        super(PTGL3RESTClient.defaultScheme, PTGL3RESTClient.defaultRestUrl, PTGL3RESTClient.defaultRestPath);
    }
    
    public PTGL3RESTClient(String scheme, String restUrl, String restPath) {
        super(scheme, restUrl, restPath);
    }
            
    /**
     * Queries a PG from the REST web service in JSON format.
     * @param pdbid
     * @param chain
     * @param graphType
     * @return the response
     * @throws IOException 
     */
    public String doProteinGraphQueryJSON(String pdbid, String chain, String graphType) throws IOException {
        return doRequestGET(getQueryStringProteinGraph(pdbid, chain, graphType, "json"), MediaType.APPLICATION_JSON_TYPE);
    }
            
    /**
     * Builds a PG query string.
     * @param pdbid
     * @param chain
     * @param graphType
     * @param graphFormat
     * @return the URI
     */
    private URI getQueryStringProteinGraph(String pdbid, String chain, String graphType, String graphFormat) {
        UriBuilder builder = UriBuilder.fromPath(restHost).path("pg/" + pdbid + "/" + chain + "/" + graphType + "/" + graphFormat);    
        URI uri = builder.build();
        return uri;
    }
    
    /**
     * Builds a FG query string.
     * @param pdbid
     * @param chain
     * @param graphType
     * @param foldNumber
     * @param graphFormat
     * @return the URI
     */
    private URI getQueryStringFoldingGraph(String pdbid, String chain, String graphType, Integer foldNumber, String graphFormat) {
        UriBuilder builder = UriBuilder.fromPath(restHost).path("fg/" + pdbid + "/" + chain + "/" + graphType + "/" + foldNumber + "/" + graphFormat);    
        URI uri = builder.build();
        return uri;
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
