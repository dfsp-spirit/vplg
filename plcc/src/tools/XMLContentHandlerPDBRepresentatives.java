/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2014. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */package tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for the XML list of representative chains from the PDB. You can
 * retrieve this list at http://www.rcsb.org/pdb/rest/representatives?cluster=40.
 * An example of the format can be seen in the main function.
 * @author spirit
 */
public class XMLContentHandlerPDBRepresentatives extends DefaultHandler {
    
    public List<String> pdbChains;
    private boolean doneParsing = false;
    protected String tag = "[SAX-CH] ";
    private final boolean verbose = false;

    @Override
    public void startDocument() throws SAXException {
        doneParsing = false;
        pdbChains = new ArrayList<>();
    }
    
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

        String key = localName;
        if(key.equals("representatives")) {
            if(verbose) {
                System.out.println(tag + "Found representatives list.");
            }
        }
        else if(key.equals("pdbChain")) {
            for(int i = 0; i < atts.getLength(); i++) {
                String attribute = atts.getLocalName(i);
                if(attribute.equals("name")) {
                    pdbChains.add(atts.getValue(attribute));
                }                
            }
        }    
    }
    
    public List<String> getPdbChainList() {
        if( ! doneParsing) {
            throw new java.lang.RuntimeException("XML Handler ERROR: XMLContentHandlerSequenceClusterList: Not finished parsing the document yet, but being asked for results.");
        }
        else {
            return pdbChains;
        }
    }


    @Override
    public void endDocument() throws SAXException {
        //for (String pdbChain : pdbChains) {
        //    System.out.println(tag + "Found PDB chain: " + pdbChain);
        //}
        //System.out.println(tag + "Found " + pdbChains.size() + " chains total.");
        doneParsing = true;
    }
    
    /**
     * Main class for testing PDB representatives parsing only.
     * @param args ignored
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        String xml = "<representatives>\n" +
"  <pdbChain name=\"12AS.A\" />\n" +
"  <pdbChain name=\"16VP.A\" />\n" +
"  <pdbChain name=\"1914.A\" />\n" +
"  <pdbChain name=\"1A02.F\" />\n" +
"  <pdbChain name=\"1A04.A\" />\n" +
"  <pdbChain name=\"1A0A.A\" />\n" +
"  <pdbChain name=\"1A0C.A\" />\n" +
                "</representatives>\n";
        
        XMLParserJAX p;
        String[] sep;
        try {
            p = new XMLParserJAX();
            p.setErrorHandler(new XMLErrorHandlerJAX(System.err));
            XMLContentHandlerPDBRepresentatives handler = new XMLContentHandlerPDBRepresentatives();            
            p.handleXML(xml, handler);
            List<String> pdbChains = handler.getPdbChainList();
            System.out.println("Received a list of " + pdbChains.size() + " chains from handler:");
            for(String ic : pdbChains) {
                sep = PlccUtilities.parsePdbidAndChain(ic);
                if(sep != null) {
                    System.out.println("PDB ID: " + sep[0] + ", chain " + sep[1] + "");
                } else {
                    System.out.println("Result could not be parsed.");
                }
            }
            
        } catch(ParserConfigurationException | SAXException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    
}
