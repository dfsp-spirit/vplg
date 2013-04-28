/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package htmlgen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plcc.IO;
import plcc.ProteinChainResults;
import plcc.ProteinResults;
import plcc.SSE;
import plcc.SSEGraph;

public class HtmlGenerator {
    
    static Logger logger = LogManager.getLogger(HtmlGenerator.class.getName());
    
    private String[] relativeCssFilePathsFromBasedir;
    private File baseDir;
    
    public HtmlGenerator(File baseDir) {
        this.baseDir = baseDir;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public String[] getRelativeCssFilePathsFromBasedir() {
        return relativeCssFilePathsFromBasedir;
    }
    
    public static final String DIV_MAIN = "main";
    public static final String DIV_PROTEIN = "protein";
    public static final String DIV_CHAIN = "chain";
    public static final String DIV_CHAINS = "chains";
    public static final String DIV_NAVIGATION_CHAINS = "navigation_chains";
    public static final String DIV_NAVIGATION_GRAPHS = "navigation_graphs";
    public static final String DIV_PROTEIN_GRAPHS = "protein_graphs";
    public static final String DIV_FOLDING_GRAPHS = "folding_graphs";
    

    /**
     * Sets a list of CSS files which should be linked in the headers of all produced HTML files. Ensure that
     * the paths are relative to the basedir (or absolute).
     * @param relativeCssFilePaths 
     */
    public void setRelativeCssFilePathsFromBasedir(String[] relativeCssFilePaths) {
        this.relativeCssFilePathsFromBasedir = relativeCssFilePaths;
    }
 
    /**
     * Generates all webpages for the given protein results.
     * @param pr The protein result data structure.
     */
    public void generateAllWebpagesForResult(ProteinResults pr) {

        logger.entry();
        if(! IO.dirExistsIsDirectoryAndCanWrite(baseDir)) {
            logger.error("ERROR: Cannot create webpages under directory '" + baseDir + "', does not exist or cannot write to it.");
            return;
        }
        
        // ------------------- protein webpages -----------------------
        
        String pdbid = pr.getPdbid();
        String fs = System.getProperty("file.separator");
        String proteinWebsiteHtmlFile = this.baseDir + fs + HtmlGenerator.getFileNameProtein(pdbid);
        
        if(IO.stringToTextFile(proteinWebsiteHtmlFile, this.generateProteinWebpage(pr, ".." + fs + ".."))) {
            System.out.println("    Wrote protein website for PDB '" + pdbid + "' to " + new File(proteinWebsiteHtmlFile).getAbsolutePath() + ".");
        } else {
            System.err.println("ERROR: Could not write protein website for PDB '" + pdbid + "' to " + new File(proteinWebsiteHtmlFile).getAbsolutePath() + ".");
        }
        
        // ------------------- chain webpages -----------------------
        
        File targetDirChain;
        for(String chain : pr.getAvailableChains()) {
            targetDirChain = new File(this.baseDir + fs + chain);
            String chainWebsiteHtmlFile = targetDirChain.getAbsolutePath() + fs + pdbid + "_" + chain + ".html";
            ArrayList<String> errors = IO.createDirIfItDoesntExist(targetDirChain);
            if(errors.isEmpty()) {
                        
                if(IO.stringToTextFile(chainWebsiteHtmlFile, this.generateChainWebpage(pr, chain, ".." + fs + ".." + fs + ".."))) {
                    System.out.println("    Wrote protein website for PDB '" + pdbid + "' to " + new File(chainWebsiteHtmlFile).getAbsolutePath() + ".");
                } else {
                    System.err.println("ERROR: Could not write protein website for PDB '" + pdbid + "' to " + new File(chainWebsiteHtmlFile).getAbsolutePath() + ".");
                }                
            }
            else {
                System.err.println("ERROR: Could not create directory for chain '" + chain + "' at '" +  targetDirChain.getAbsolutePath() + "'.");
            }
        }
    }
	
	
    /**
     * Generates the overview website for the protein (PDB file), which links all chains and shows PDB meta info.
     * @param pr the protein result
     * @param pathToBaseDir the base dir which holds the CSS style sheet and other global files
     * @return the website string as HMTL
     */
    public String generateProteinWebpage(ProteinResults pr, String pathToBaseDir) {

        StringBuilder sb = new StringBuilder();
        String fs = File.separator;
        
        String pdbid = pr.getPdbid();
        List<String> chains = pr.getAvailableChains();
        ProteinChainResults pcr;

        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- PDB " + pdbid, pathToBaseDir));
        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBody());
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_MAIN));
        sb.append(this.generateLogo(pathToBaseDir));

        // -------------------- protein info -------------------
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_PROTEIN));
            sb.append(HtmlTools.heading("Protein info", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("PDB identifier: ").append(pdbid).append("<br/>\n");
            sb.append("Link to structure at RCSB PDB website: ");
            sb.append(HtmlTools.link("http://www.rcsb.org/pdb/explore/explore.do?structureId=" + pdbid, pdbid));
            sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // protein info


        // -------------------- chain info -------------------
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_CHAINS));
        sb.append(HtmlTools.heading("Chain info", 2));
        sb.append(HtmlTools.startParagraph());
        
        if(chains.size() > 0) {
            sb.append("All ").append(chains.size()).append(" chains of the protein:<br/>");
            sb.append(HtmlTools.uListStart());
            for(String chain : chains) {
                pcr = pr.getProteinChainResults(chain);
                if(pcr != null) {
                    sb.append(HtmlTools.listItem(HtmlTools.link("" + chain + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, chain), "Chain " + chain)));
                    //pcr.getChainMetaData().
                }
                else {
                    sb.append(HtmlTools.listItem("chain " + chain));
                }
            }
            sb.append(HtmlTools.uListEnd());
        }
        else {
            sb.append("This PDB file contains no protein chains.\n");
        }
        
        sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // chains

        
        // ------------- body -- footer ---------------
        sb.append(this.generateFooter(pathToBaseDir));
        
        sb.append(HtmlTools.endDiv());  // main
        sb.append(HtmlTools.endBody());
        sb.append(HtmlTools.endHtml());

        return sb.toString();
    }
    
    /**
     * Generates the chain website for a  protein chain, which links all graphs and shows chain meta info.
     * @param pr the protein result
     * @param chain the PDB chain name
     * @param pathToBaseDir the base dir which holds the CSS style sheet and other global files
     * @return the website string as HMTL
     */
    public String generateChainWebpage(ProteinResults pr, String chain, String pathToBaseDir) {

        StringBuilder sb = new StringBuilder();
        String fs = File.separator;
        
        String pdbid = pr.getPdbid();
        List<String> chains = pr.getAvailableChains();
        List<String> graphs;
        ProteinChainResults pcr, otherChainPcr;
        pcr = pr.getProteinChainResults(chain);

        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- PDB " + pdbid + " -- chain " + chain, pathToBaseDir));

        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBody());
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_MAIN));
        sb.append(this.generateLogo(pathToBaseDir));


        // -------------------- chain info -------------------
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_CHAIN));
        sb.append(HtmlTools.heading("Protein chain info", 2));
        sb.append(HtmlTools.startParagraph());
        sb.append("PDB chain: ").append(pdbid).append(" chain ").append(chain).append("<br/>\n");
        sb.append("Link to structure at RCSB PDB website: ");
        sb.append(HtmlTools.link("http://www.rcsb.org/pdb/explore/explore.do?structureId=" + pdbid, pdbid)).append("<br/>\n");
        
        if(pcr != null) {
            
        }
        
        sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // chain
        
        // -------------------- navigation -------------------
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_NAVIGATION_CHAINS));
        sb.append(HtmlTools.heading("Navigation", 2));
        sb.append(HtmlTools.startParagraph());
        
        // --- links to mother protein ---
        sb.append("Part of protein: ").append(HtmlTools.link(".." + fs + HtmlGenerator.getFileNameProtein(pdbid), pdbid)).append("<br/>\n");
        
        // --- links to other chains of the same protein ---
        sb.append("Other chains of this protein: ");
        if(chains.size() > 1) {
            sb.append("All ").append(chains.size() - 1).append(" chains of the protein:<br/>");
            sb.append(HtmlTools.uListStart());
            for(String otherChain : chains) {
                
                if(otherChain.equals(chain)) {
                    continue;
                }
                
                otherChainPcr = pr.getProteinChainResults(otherChain);
                if(otherChainPcr != null) {
                    sb.append(HtmlTools.listItem(HtmlTools.link(".." + fs + otherChain + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, otherChain), "Chain " + otherChain)));
                }
                else {
                    sb.append(HtmlTools.listItem("chain " + otherChain));
                }
            }
            sb.append(HtmlTools.uListEnd());
        }
        else {
            sb.append(HtmlTools.italic("The PDB file contains no other protein chains."));
            sb.append("<br/>\n");
        }
        sb.append(HtmlTools.endDiv());  // navigation chains
        
        // --- links to graphs ---
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_NAVIGATION_GRAPHS));
        if(pcr != null) {
            graphs = pcr.getAvailableGraphs();
            if(graphs.size() > 0) {
                sb.append("All ").append(graphs.size()).append(" graphs of the protein:<br/>");
                sb.append(HtmlTools.uListStart());
                
                // ---------------------- handle graph types ----------------------
                for(String graphType : graphs) {
                    sb.append(HtmlTools.listItem("" + HtmlTools.link("#" + graphType, "The " + graphType + " graph")));
                }
                sb.append(HtmlTools.uListEnd());
            }
        }
        
        sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // navigation graphs

        // -------------------- protein graphs -------------------
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_PROTEIN_GRAPHS));
        sb.append(HtmlTools.heading("Protein graphs", 2));
        sb.append(HtmlTools.endParagraph());
        
        
        SSEGraph g;
        if(pcr == null) {
            sb.append(HtmlTools.italic("No result files are available for this chain."));
            sb.append("<br/>\n");            
        }
        else {
            graphs = pcr.getAvailableGraphs();
            if(graphs.size() > 0) {                
                
                // ---------------------- handle graph types ----------------------
                for(String graphType : graphs) {
                    
                    sb.append(HtmlTools.aname(graphType));
                    sb.append(HtmlTools.heading("The " + graphType + " graph", 3));
                    
                    // ---------------------- SSE info table ----------------------
                    sb.append(HtmlTools.heading("SSE information", 4));
                    sb.append(HtmlTools.startParagraph());
                    g = pcr.getProteinGraph(graphType);
                    if(g != null) {           
                        
                        sb.append("This ").append(graphType).append(" graph consists of ").append(g.numVertices()).append(" SSEs.<br/><br/>\n");
                        
                        if(g.numVertices() > 0) {
                            //sb.append(HtmlTools.uListStart());
                            sb.append(HtmlTools.tableStart(g.getInfoFieldNames()));
                            for(int i = 0; i < g.numVertices(); i++) {
                                sb.append(HtmlTools.tableRow(g.getInfoFieldsForSSE(i)));
                            //    sb.append(HtmlTools.listItem(sse.getSseType()));
                            }
                            //sb.append(HtmlTools.uListEnd());
                            sb.append(HtmlTools.tableEnd());                                                        
                        }                                                
                    }
                    else {
                        sb.append(HtmlTools.italic("No SSE details are available for this graph."));
                        sb.append("<br/>\n");
                    }
                    sb.append(HtmlTools.endParagraph());
                    
                    // ---------------------- graph image ----------------------
                    sb.append(HtmlTools.heading("Graph image", 4));
                    sb.append(HtmlTools.startParagraph());
                    File graphImage = pcr.getProteinGraphImage(graphType);
                    if(IO.fileExistsIsFileAndCanRead(graphImage)) {
                        sb.append(HtmlTools.img(graphImage.getName(), "" + graphType + " graph of " + pdbid + " chain " + chain));  //TODO: we assume the image is in the same dir here, which is true atm but kinda ugly
                    } else {
                        sb.append(HtmlTools.italic("No image of this graph is available."));
                        sb.append("<br/>\n");
                    }
                    sb.append(HtmlTools.endParagraph());
                        
                    // ---------------------- graph file download options ----------------------
                    sb.append(HtmlTools.heading("Download graph", 4));
                    sb.append(HtmlTools.startParagraph());
                    List<String> formats = pcr.checkForOutputFormatsWithValidFiles(graphType);
                    if(formats.size() > 0) {
                        sb.append("This graph is available for download in the following formats:<br/>");
                        sb.append(HtmlTools.uListStart());
                        for(String f : formats) {
                            sb.append(HtmlTools.listItem(HtmlTools.link(pcr.getProteinGraphOutputFile(graphType, f).getName(), f))); //TODO: we assume the graph file is in the same dir here, which is true atm but kinda ugly
                        }                        
                        sb.append(HtmlTools.uListEnd());
                    }
                    else {
                        sb.append(HtmlTools.italic("No downloads are available for this graph."));
                        sb.append("<br/>\n");
                    }
                    sb.append(HtmlTools.endParagraph());
                    
                }                
            }
            else {
                sb.append(HtmlTools.italic("No graphs are available for this chain."));
                sb.append("<br/>\n");
            }
        }
        
        sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // protein graphs

        // ------------- body -- footer ---------------
        sb.append(this.generateFooter(pathToBaseDir));
        sb.append(HtmlTools.endDiv());  // main
        sb.append(HtmlTools.endBody());
        sb.append(HtmlTools.endHtml());                                    

        return sb.toString();
    }
    
    
    public String generateHeader(String title, String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
                
        sb.append("<html>\n<head>\n");
        sb.append("<title>" + title +  "</title>\n");

        for(String cssFileName : relativeCssFilePathsFromBasedir) {            
            sb.append("<link href=\"").append(pathToBaseDir).append(cssFileName).append("\" rel=\"stylesheet\" type=\"text/css\">");
        }
        sb.append("</head>\n");
        return sb.toString();        
    }
    
    public String generateLogo(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"logo\" align=\"center\">\n");
        sb.append(HtmlTools.heading("VPLGweb -- Visualization of Protein Ligand Graphs web server", 1));
        sb.append(HtmlTools.hr());
        sb.append("</div>\n");
        return sb.toString();
    }
    
    public String generateFooter(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        sb.append("<footer>\n");
        sb.append("<div class=\"footer\" align=\"center\">\n");
        sb.append(HtmlTools.hr());
        sb.append(HtmlTools.paragraph("VPLGweb by Tim Sch&auml;fer."));
        sb.append("</div>\n");
        sb.append("</footer>\n");
        return sb.toString();
    }
    
    public static String getFileNameProtein(String pdbid) {
        return "" + pdbid + ".html";
    }
    
    public static String getFileNameProteinAndChain(String pdbid, String chain) {
        return "" + pdbid + "_" + chain + ".html";
    }






    
	
}
