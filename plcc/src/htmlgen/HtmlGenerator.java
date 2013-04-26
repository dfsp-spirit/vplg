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

public class HtmlGenerator {
    
    static Logger logger = LogManager.getLogger(HtmlGenerator.class.getName());
    
    private String[] relativeCssFilePaths;
    private File baseDir;
    
    public HtmlGenerator(File basDir) {
        this.baseDir = baseDir;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public String[] getRelativeCssFilePaths() {
        return relativeCssFilePaths;
    }

    /**
     * Sets a list of CSS files which should be linked in the headers of all produced HTML files. Ensure that
     * the paths are relative to the basedir (or absolute).
     * @param relativeCssFilePaths 
     */
    public void setRelativeCssFilePaths(String[] relativeCssFilePaths) {
        this.relativeCssFilePaths = relativeCssFilePaths;
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
        String proteinWebsiteHtmlFile = this.baseDir + fs + pdbid + ".html";
        
        if(IO.stringToTextFile(proteinWebsiteHtmlFile, this.generateProteinWebpage(pr))) {
            System.out.println("    Wrote protein website for PDB '" + pdbid + "' to " + new File(proteinWebsiteHtmlFile).getAbsolutePath() + ".");
        } else {
            System.err.println("ERROR: Could not write protein website for PDB '" + pdbid + "' to " + new File(proteinWebsiteHtmlFile).getAbsolutePath() + ".");
        }
        
        // ------------------- chain webpages -----------------------
        
        File targetDirChain;
        for(String chain : pr.getAvailableChains()) {
            targetDirChain = new File(this.baseDir + fs + chain);
            ArrayList<String> res = IO.createDirIfItDoesntExist(targetDirChain);
            if(! res.isEmpty()) {
                System.err.println("ERROR: Could not create directory for chain '" + chain + "' at '" +  targetDirChain.getAbsolutePath() + "'.");
            }
        }
    }
	
	
    public String generateProteinWebpage(ProteinResults pr) {

        StringBuilder sb = new StringBuilder();
        
        String pdbid = pr.getPdbid();
        String[] chains = pr.getAvailableChains();
        ProteinChainResults pcr;

        //-------------- header ------------
        sb.append("<html>\n<head>\n");
        sb.append("<title>" + "VPLGweb -- PDB ").append(pdbid).append(" --" + "</title>\n");

        for(String s : relativeCssFilePaths) {            
            sb.append("<link href=\"").append(s).append("\" rel=\"stylesheet\" type=\"text/css\">");
        }
        sb.append("</head>\n");

        // ------------- body -- logo and title ---------------
        sb.append("<body>\n");

        sb.append("<div class=\"logo\" align=\"center\">\n");
        sb.append(HtmlTools.heading("VPLGweb -- Visualization of Protein Ligand Graphs web server", 1));
        sb.append(HtmlTools.hr());
        sb.append("</div>\n");


        // -------------------- protein info -------------------
        sb.append("<div class=\"protein\">\n");
        sb.append(HtmlTools.heading("Protein info", 2));
        sb.append("<p>");
        sb.append("PDB identifier: ").append(pdbid).append("<br/>");
        sb.append("Link to structure at RCSB PDB website: ");
        sb.append(HtmlTools.link("http://www.rcsb.org/pdb/explore/explore.do?structureId=" + pdbid, pdbid));
        sb.append("</p>\n");
        sb.append("</div>");


        // -------------------- chain info -------------------
        sb.append("<div class=\"chains\">\n");
        sb.append(HtmlTools.heading("Chain info", 2));
        sb.append("<p>");
        
        if(chains.length > 0) {
            sb.append("All ").append(chains.length).append(" chains of the protein:<br/>");
            sb.append(HtmlTools.uListStart());
            for(String chain : chains) {
                pcr = pr.getProteinChainResults(chain);
                if(pcr != null) {

                }
                sb.append(HtmlTools.listItem("chain " + chain));
            }
            sb.append(HtmlTools.uListEnd());
        }
        else {
            sb.append("This PDB file contains no protein chains.\n");
        }
        
        sb.append("</p>\n");
        sb.append("</div>");

        // ------------- body -- footer ---------------
        sb.append("<div class=\"footer\" align=\"center\">\n");
        sb.append(HtmlTools.hr());
        sb.append(HtmlTools.paragraph("VPLGweb by ts"));
        sb.append("</div>\n");


        sb.append("</body>\n</html>\n");                                    

        return sb.toString();
    }                






    /**
    * Usage example.
    * @param argv the command line arguments, ignored
    */
    public static void main(String[] argv) {

        String pdbid = "7TIM";
        String[] chains = new String[] { "A", "B" };
        String[] cssFiles = new String[] { "vplgweb.css" };


        String fs = System.getProperty("file.separator");
        File outputBaseDir = new File(System.getProperty("user.home") + fs + "htmlTest");
        IO.createDirIfItDoesntExist(outputBaseDir);

        HtmlGenerator hg = new HtmlGenerator(outputBaseDir);
        String cssFilePath = outputBaseDir.getAbsolutePath() + fs + "vplgweb.css";
        CssGenerator cssG = new CssGenerator();
        cssG.writeDefaultCssFileTo(new File(cssFilePath));
        hg.setRelativeCssFilePaths(cssFiles);

        String chainFilePath = outputBaseDir.getAbsolutePath() + fs + "index.html";
        //IO.stringToTextFile(chainFilePath, hg.generateProteinChainWebpage(pdbid, chains, null));
        System.err.println("OUT OF ORDER");
    }
	
}
