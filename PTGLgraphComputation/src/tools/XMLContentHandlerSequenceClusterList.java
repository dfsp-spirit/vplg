/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2014. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package tools;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses the XML output from the PDB API which returns all PDB IDs similar to a given one.
 * This is the sequence cluster 40 API call.
 * @author spirit
 */
public class XMLContentHandlerSequenceClusterList extends DefaultHandler {
    
    public List<String> pdbChains;
    private boolean doneParsing = false;

    @Override
    public void startDocument() throws SAXException {
        doneParsing = false;
        pdbChains = new ArrayList<>();
    }
    
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

        String key = localName;
        if(key.equals("sequenceCluster")) {
            System.out.println("Found a seq cluster.");
        }
        else if(key.equals("pdbChain")) {
            System.out.print("Found a PDB chain: namespaceURI=" + namespaceURI + ", localName=" + localName + ", qName=" + qName + ". Attributes: ");
            for(int i = 0; i < atts.getLength(); i++) {
                String attribute = atts.getLocalName(i);
                if(attribute.equals("name")) {
                    System.out.print("PDB chain=" + atts.getValue(attribute));
                    pdbChains.add(atts.getValue(attribute));
                }
                if(attribute.equals("rank")) {
                    System.out.print("rank=" + atts.getValue(attribute));
                }
                
                if(i < atts.getLength() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print("\n");
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
        for (String pdbChain : pdbChains) {
            System.out.println("Found PDB chain: " + pdbChain);
        }
        System.out.println("Found " + pdbChains.size() + " chains total.");
        doneParsing = true;
    }
    
}

