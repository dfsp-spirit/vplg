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
import org.apache.http.client.ClientProtocolException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;


/**
 * A REST client, a very simple wrapper around Jersey only.
 * @author spirit
 */
public class RESTClient {
    
    String restHost;
    String restPath;
    String scheme;
    ClientConfig config;
    Client client;
    WebResource service;
    
    
    /**
     * Creates a new REST client
     * @param scheme the scheme, like "http"
     * @param restHost the URL, like www.server.com
     * @param restPath the path, like "/api/"
     */
    public RESTClient(String scheme, String restHost, String restPath) {
        this.scheme = scheme;
        this.restHost = restHost;
        this.restPath = restPath;        
        
        config = new DefaultClientConfig();
        client = Client.create(config);
        service = client.resource(UriBuilder.fromPath(restHost).scheme(scheme).path(restPath).build());
        System.out.println("Created service with URI '" + service.getURI() + "'.");
    }
    

    /**
     * Function to perform GET queries.
     * @param resourcePath the path to the resource on the server (URL), relative to base URL
     * @param resultMediaType the media type
     * @return the result string
     * @throws ClientProtocolException
     * @throws IOException 
     */
    public String doRequestGET(URI uri, MediaType resultMediaType) throws ClientProtocolException, IOException {        
        ClientResponse response = null;
        Integer status = -1;
        try {
            response = service.uri(uri).accept(resultMediaType).get(ClientResponse.class);
            status = response.getStatus();
            System.out.println("GET HTTP request result was " + status + ".");
            if(status < 200 || status > 299) {
                System.err.println("ERROR: web service returned status code " + status + ".");
            }
        } catch(UniformInterfaceException | ClientHandlerException e) {
            System.err.println("REST ERROR: doRequestGET: '" + e.getMessage() + "'.");
        }
        
        //System.out.println("Queried URI: " + service.path(this.restPath).path(resourcePath).getURI() + ".");
        
        //return service.path(this.restPath).path(resourcePath).accept(resultMediaType).get(String.class);
        return service.uri(uri).accept(resultMediaType).get(String.class);

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
    
    public URI getURIForRelativePath(String appendPath) {
        UriBuilder builder = UriBuilder.fromPath(restHost).scheme(scheme).path(restPath).path(appendPath);
        System.out.println("restHost is " + restHost);
        //builder.host(restHost);
        URI uri = builder.build();
        
        /*
        URI uri = null;
        try {
            uri = new URI("http://ptgl.uni-frankfurt.de/api/index.php/");
        }
        catch(Exception e) {
            System.err.println("OHNO: " + e.getMessage());
        }
        */
        return uri;
    }
    
    /**
     * Test main class
     * @param args ignored
     */
    public static void main(String[] args) {
        
        RESTClient c = new RESTClient("http", "ptgl.uni-frankfurt.de", "/api/index.php/");
        
        // test GET
        String res1 = null;
        try {
            URI uri = c.getURIForRelativePath("pg/7tim/A/albe/json");
            System.out.println("uri is " + uri);
            res1 = c.doRequestGET(uri, MediaType.APPLICATION_JSON_TYPE);
        } catch(Exception e) {
            System.err.println("GET ERROR: " + e.getMessage());
        }
        System.out.println("GET response was: '" + res1 + "'.");
        
        
    }
    
    

}

    

