/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
     * 
     * @param resourcePath
     * @param resultMediaType
     * @return
     * @throws ClientProtocolException
     * @throws IOException 
     */
    public String doRequestGET(String resourcePath, MediaType resultMediaType) throws ClientProtocolException, IOException {
        String result = service. path(this.restPath).path(resourcePath).accept(resultMediaType).get(String.class);
        return result;

    }
    
    
    /**
     * 
     * @param postBody
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

    

