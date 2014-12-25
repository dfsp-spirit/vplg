/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2014. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package tools;

/**
 * A JAX-based XML parser. This one is suitable if you do not need to edit/re-use the
 * document and thus do not need the whole DOM tree in memory at any time. It is an event-based read-once and
 * use instantly parser. We need it to parse XML received by calling web services like the RCSB PDB REST API.
 * @author spirit
 */
public class XMLParserJAX {
    
}
