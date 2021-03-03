/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2014. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package tools;

import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * A REST client to access the RCSB PDB API. See http://www.rcsb.org/pdb/software/rest.do for API documentation.
 * @author spirit
 */
public class PDBRESTClient extends RESTClient {
    
    public static final String defaultScheme = "http";
    public static final String defaultRestUrl = "www.rcsb.org";
    public static final String defaultRestPath = "/pdb/rest/";

    public PDBRESTClient(String scheme, String restUrl, String restPath) {
        super(scheme, restUrl, restPath);
    }
    
    public PDBRESTClient() {
        super(PDBRESTClient.defaultScheme, PDBRESTClient.defaultRestUrl, PDBRESTClient.defaultRestPath);
    }
        
    private static String getQueryStringPfamOfChain(String pdbid) {
        return "hmmer?structureId=" + pdbid;
    }
    
    private static String getQueryStringRepresDomainsOfChain(String pdbid, String chain) {
        return "representativeDomains?structureId=" + pdbid + "." + chain;
    }
    
    private URI getQueryStringSeqCluster40OfChain(String pdbid, String chain) {        
        UriBuilder builder = UriBuilder.fromPath(restHost).queryParam("cluster", 40).queryParam("structureId", pdbid + "." + chain);    
        URI uri = builder.build();
        return uri;
    }
    
    public String doSeqCluster40QueryXML(String pdbid, String chain) throws IOException {        
        URI uri = getQueryStringSeqCluster40OfChain(pdbid, chain);        
        return doRequestGET(uri, MediaType.APPLICATION_XML_TYPE);
    }
    
    public static String getQueryStringGOTermsOfChain(String pdbid, String chain) {
        return "goTerms?structureId=" + pdbid + "." + chain;
    }
    
    
    public static void main(String[] args) {
        
        String pdbid = "7tim";
        String chain = "A";
        
        System.out.println("Querying PDB REST web service for " + pdbid + " chain " + chain + "...");
        
        PDBRESTClient c = new PDBRESTClient();
        String xml = null;
        try {
            xml = c.doSeqCluster40QueryXML(pdbid, chain);
        } catch (IOException e) {
            System.err.println("REST ERROR: " + e.getMessage());
            System.exit(1);
        }
        
        //System.out.println("xml: " + xml);
        
        /*
        String xml = "<sequenceCluster clusterNr=\"4\" clusterPercID=\"40\">\n" +
"<pdbChain name=\"2W72.A\" rank=\"1\"/>\n" +
"<pdbChain name=\"2W72.B\" rank=\"2\"/>\n" +
"<pdbChain name=\"2W72.D\" rank=\"2\"/>\n" +
"<pdbChain name=\"2W72.C\" rank=\"3\"/>\n" +
"<pdbChain name=\"1IRD.A\" rank=\"4\"/>\n" +
"<pdbChain name=\"1IRD.B\" rank=\"5\"/>\n" +
                "</sequenceCluster>\n";
        */
        
        System.out.println("Query result received, parsing XML...");
        
        XMLParserJAX p;
        try {
            p = new XMLParserJAX();
            p.setErrorHandler(new XMLErrorHandlerJAX(System.err));
            p.handleXML(xml, new XMLContentHandlerCountLocalNames());
            
        } catch(ParserConfigurationException | IOException | SAXException e) {
            System.err.println("XML ERROR: " + e.getMessage());
        }
        
        System.out.println("All done.");
    }
}
