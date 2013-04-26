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

    /**
     * Sets a list of CSS files which should be linked in the headers of all produced HTML files. Ensure that
     * the paths are relative to the basedir (or absolute).
     * @param relativeCssFilePaths 
     */
    public void setRelativeCssFilePathsFromBasedir(String[] relativeCssFilePaths) {
        this.relativeCssFilePathsFromBasedir = relativeCssFilePaths;
    }
    
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
	
	
    public String generateProteinWebpage(ProteinResults pr, String pathToBaseDir) {

        StringBuilder sb = new StringBuilder();
        String fs = File.separator;
        
        String pdbid = pr.getPdbid();
        List<String> chains = pr.getAvailableChains();
        ProteinChainResults pcr;

        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- PDB " + pdbid, pathToBaseDir));
        // ------------- body -- logo and title ---------------
        sb.append("<body>\n");
        sb.append(this.generateLogo(pathToBaseDir));

        // -------------------- protein info -------------------
        sb.append("<div class=\"protein\">\n");
        sb.append(HtmlTools.heading("Protein info", 2));
        sb.append("<p>");
        sb.append("PDB identifier: ").append(pdbid).append("<br/>\n");
        sb.append("Link to structure at RCSB PDB website: ");
        sb.append(HtmlTools.link("http://www.rcsb.org/pdb/explore/explore.do?structureId=" + pdbid, pdbid));
        sb.append("</p>\n");
        sb.append("</div>\n");


        // -------------------- chain info -------------------
        sb.append("<div class=\"chains\">\n");
        sb.append(HtmlTools.heading("Chain info", 2));
        sb.append("<p>");
        
        if(chains.size() > 0) {
            sb.append("All ").append(chains.size()).append(" chains of the protein:<br/>");
            sb.append(HtmlTools.uListStart());
            for(String chain : chains) {
                pcr = pr.getProteinChainResults(chain);
                if(pcr != null) {
                    sb.append(HtmlTools.listItem(HtmlTools.link("" + chain + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, chain), "Chain " + chain)));
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
        
        sb.append("</p>\n");
        sb.append("</div>\n");

        // ------------- body -- footer ---------------
        sb.append(this.generateFooter(pathToBaseDir));
        sb.append("</body>\n</html>\n");                                    

        return sb.toString();
    }
    
    
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
        sb.append("<body>\n");
        sb.append(this.generateLogo(pathToBaseDir));


        // -------------------- chain info -------------------
        sb.append("<div class=\"chain\">\n");
        sb.append(HtmlTools.heading("Protein chain info", 2));
        sb.append("<p>");
        sb.append("PDB chain: ").append(pdbid).append(" chain ").append(chain).append("<br/>\n");
        sb.append("Link to structure at RCSB PDB website: ");
        sb.append(HtmlTools.link("http://www.rcsb.org/pdb/explore/explore.do?structureId=" + pdbid, pdbid));
        sb.append("</p>\n");
        sb.append("</div>");
        
        // -------------------- navigation -------------------
        
        sb.append("<div class=\"navigation_chain\">\n");
        sb.append(HtmlTools.heading("Navigation", 2));
        sb.append("<p>");
        
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
                    sb.append(HtmlTools.listItem(HtmlTools.link("" + chain + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, chain), "Chain " + otherChain)));
                }
                else {
                    sb.append(HtmlTools.listItem("chain " + otherChain));
                }
            }
            sb.append(HtmlTools.uListEnd());
        }
        else {
            sb.append("The PDB file contains no other protein chains.<br/>\n");
        }
        
        // --- links to graphs ---
        
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
        
        sb.append("</p>\n");
        sb.append("</div>");

        // -------------------- protein graphs -------------------
        sb.append("<div class=\"protein_graphs\">\n");
        sb.append(HtmlTools.heading("Protein graphs", 2));
        sb.append("<p>");
        
        
        SSEGraph g;
        SSE sse;
        if(pcr == null) {
            sb.append("No result files are available for this chain.<br/>\n");
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
                        
                        sb.append("This ").append(graphType).append(" graph consists of ").append(g.numVertices()).append(" SSEs.<br/>");
                        
                        if(g.numVertices() > 0) {
                            sb.append(HtmlTools.uListStart());
                            for(int i = 0; i < g.numVertices(); i++) {
                                sse = g.getSSEBySeqPosition(i);
                                sb.append(HtmlTools.listItem(sse.getSseType()));
                            }
                            sb.append(HtmlTools.uListEnd());
                        }                                                
                    }
                    else {
                        sb.append("No SSE details are available for this graph.<br/>\n");
                    }
                    sb.append(HtmlTools.endParagraph());
                    
                    // ---------------------- graph image ----------------------
                    sb.append(HtmlTools.heading("Graph image", 4));
                    sb.append(HtmlTools.startParagraph());
                    File graphImage = pcr.getProteinGraphImage(graphType);
                    if(IO.fileExistsIsFileAndCanRead(graphImage)) {
                        sb.append(HtmlTools.img(graphImage.getName(), "" + graphType + " graph of " + pdbid + " chain " + chain));  //TODO: we assume the image is in the same dir here, which is true atm but kinda ugly
                    } else {
                        sb.append("No image of this graph is available.<br/>");
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
                        sb.append("No downloads are available for this graph.<br/>");
                    }
                    sb.append(HtmlTools.endParagraph());
                    
                }                
            }
            else {
                sb.append("No graphs are available for this chain.<br/>\n");
            }
        }
        
        sb.append("</p>\n");
        sb.append("</div>");

        // ------------- body -- footer ---------------
        sb.append(this.generateFooter(pathToBaseDir));
        sb.append("</body>\n</html>\n");                                    

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
        sb.append("<div class=\"footer\" align=\"center\">\n");
        sb.append(HtmlTools.hr());
        sb.append(HtmlTools.paragraph("VPLGweb by ts"));
        sb.append("</div>\n");
        return sb.toString();
    }
    
    public static String getFileNameProtein(String pdbid) {
        return "" + pdbid + ".html";
    }
    
    public static String getFileNameProteinAndChain(String pdbid, String chain) {
        return "" + pdbid + "_" + chain + ".html";
    }






    
	
}
