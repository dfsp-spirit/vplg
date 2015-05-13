/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package graphformats;

/**
 * The Graph Modelling Language Format (not to be confused with GraphML) this one
 * was published in http://www.fim.uni-passau.de/fileadmin/files/lehrstuhl/brandenburg/projekte/gml/gml-technical-report.pdf 
 * and is described at http://en.wikipedia.org/wiki/Graph_Modelling_Language.
 * @author ts
 */
public interface IGraphModellingLanguageFormat {
    
    /** Exports this graph in GML format. */
    public String toGraphModellingLanguageFormat();
    
}
