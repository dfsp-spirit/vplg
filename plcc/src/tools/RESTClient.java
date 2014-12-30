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
import javax.ws.rs.core.UriBuilder;
import org.apache.http.client.ClientProtocolException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;


/**
 * A REST client, a very simple wrapper around Jersey only.
 * @author spirit
 */
public class RESTClient {
    
    String restUrl;
    String restPath;
    ClientConfig config;
    Client client;
    WebResource service;
    
    
    /**
     * Creates a new REST client
     * @param restUrl the URL, like http://www.server.com
     * @param restPath the path, like "/api/"
     */
    public RESTClient(String restUrl, String restPath) {
        this.restPath = restPath;
        this.restUrl = restUrl;
        
        config = new DefaultClientConfig();
        client = Client.create(config);
        service = client.resource(UriBuilder.fromUri(restUrl).build());
    }


    /**
     * Function to perform GET queries.
     * @param resourcePath the path to the resource on the server (URL), relative to base URL
     * @param resultMediaType the media type
     * @return the result string
     * @throws ClientProtocolException
     * @throws IOException 
     */
    public String doRequestGET(String resourcePath, MediaType resultMediaType) throws ClientProtocolException, IOException {
        String result = service.path(this.restPath).path(resourcePath).accept(resultMediaType).get(String.class);
        return result;

    }
    
    
    /**
     * Untested, need adaptation to expected media types
     * @param postBody a map of parameters (key/value) for the query
     * @param resultMediaType
     * @return 
     * @throws java.io.IOException 
     */
    public String doRequestPOST(Map<String,Object> postBody, MediaType resultMediaType) throws IOException {                
        
        ClientResponse response = service.accept(resultMediaType).type(resultMediaType).post(ClientResponse.class, postBody);

        // check response status code
        if (response.getStatus() != 200) {
            throw new IOException("POST failed with HTTP error code: " + response.getStatus() + ".");
        }
        
        String resp = response.getEntity(String.class);
        return resp;
    }
    
    
    /**
     * Test main class
     * @param args ignored
     */
    public static void main(String[] args) {
        
        RESTClient c = new RESTClient("http://ptgl.uni-frankfurt.de", "/api/index.php/");
        
        // test GET
        String res1 = null;
        try {
            res1 = c.doRequestGET("pg/7tim/A/albe/json", MediaType.APPLICATION_JSON_TYPE);
        } catch(IOException e) {
            System.err.println("GET ERROR: " + e.getMessage());
        }
        System.out.println("GET response was: '" + res1 + "'.");
        
        
    }
    
    

}

    

