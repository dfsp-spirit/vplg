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
import plcc.ProtMetaInfo;
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
    public static final String DIV_TOP_ROW = "top_row";
    public static final String DIV_PROTEIN = "protein";
    public static final String DIV_CHAIN = "chain";
    public static final String DIV_CHAINS = "chains";
    public static final String DIV_CLEAR = "clear";
    public static final String DIV_NAVIGATION = "navigation";
    public static final String DIV_NAVIGATION_CHAINS = "navigation_chains";
    public static final String DIV_NAVIGATION_GRAPHS = "navigation_graphs";
    public static final String DIV_PROTEIN_GRAPHS = "protein_graphs";
    public static final String DIV_PROTEIN_GRAPH = "protein_graph";
    public static final String DIV_PROTEIN_LINKS = "protein_links";
    public static final String DIV_FOLDING_GRAPHS = "folding_graphs";
    public static final String DIV_FOLDING_GRAPH = "folding_graph";
    public static final String DIV_INTRO = "intro";
    public static final String DIV_SEARCH = "searchform";
    

    /**
     * Sets a list of CSS files which should be linked in the headers of all produced HTML files. Ensure that
     * the paths are relative to the basedir (or absolute).
     * @param relativeCssFilePaths 
     */
    public void setRelativeCssFilePathsFromBasedir(String[] relativeCssFilePaths) {
        this.relativeCssFilePathsFromBasedir = relativeCssFilePaths;
    }
    
    public void generateCoreWebpages(File outputBaseDir) {
        String fs = System.getProperty("file.separator");
        String startWebsiteHtmlFile =  outputBaseDir.getAbsolutePath() + fs + "index.html";
        String searchWebsiteHtmlFile =  outputBaseDir.getAbsolutePath() + fs + "search.html";
        String findWebsitePhpFile =  outputBaseDir.getAbsolutePath() + fs + "find.php";
        
        if(IO.stringToTextFile(startWebsiteHtmlFile, this.generateStartWebpage("."))) {
            System.out.println("   Wrote VPLGweb start website to " + new File(startWebsiteHtmlFile).getAbsolutePath() + ".");
        } else {
            System.err.println("ERROR: Could not write VPLGweb start website to " + new File(startWebsiteHtmlFile).getAbsolutePath() + ".");
        }
        
        if(IO.stringToTextFile(searchWebsiteHtmlFile, this.generateSearchWebpage("."))) {
            System.out.println("   Wrote VPLGweb search website to " + new File(searchWebsiteHtmlFile).getAbsolutePath() + ".");
        } else {
            System.err.println("ERROR: Could not write VPLGweb search website to " + new File(searchWebsiteHtmlFile).getAbsolutePath() + ".");
        }
        
        if(IO.stringToTextFile(findWebsitePhpFile, this.generateFindWebpage("."))) {
            System.out.println("   Wrote VPLGweb find website to " + new File(findWebsitePhpFile).getAbsolutePath() + ".");
        } else {
            System.err.println("ERROR: Could not write VPLGweb find website to " + new File(findWebsitePhpFile).getAbsolutePath() + ".");
        }
    }
    
    public String generateStartWebpage(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        
        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- The Visualization of Protein Ligand Graphs web server -- Welcome", pathToBaseDir));
        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBody());
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_MAIN));
        sb.append(this.generateLogo(pathToBaseDir));
        sb.append(HtmlGenerator.generateTopPageTitle("Welcome to VPLGweb"));
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_INTRO));
        
            // intro
        
            sb.append(HtmlTools.heading("About VPLGWeb", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("VPLGweb is a web server which allows for quick access to Protein Ligand Graphs for all ");
            sb.append("protein chains in the RCSB Protein Data Bank (PDB). These graphs were computed from the ");
            sb.append("3D atom coordinates in PDB files and the secondary structure assignments of the DSSP algortihm using the ");
            sb.append(HtmlTools.link(HtmlGenerator.getVPLGSoftwareWebsite(), "VPLG software."));
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlTools.brAndNewline());
            
            // PLGs
            sb.append(HtmlTools.heading("Protein ligand graphs", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("A protein graph models the structure of a protein chain. Each vertex ");
            sb.append("in the graph represents a secondary structure element (SSE, e.g., an alpha helix ");
            sb.append("or a beta strand) or a ligand. An edge between two vertices in the graph means that, ");
            sb.append(" the respective SSEs are in contact in the 3D structure.");
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlTools.brAndNewline());
            
            // using
            
            sb.append(HtmlTools.heading("Using VPLGWeb", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("You can use the ");
            sb.append(HtmlTools.link("search.html", "search form"));
            sb.append(" to access the Protein Ligand Graphs of a certain PDB file.");
            sb.append(HtmlTools.br());
            sb.append(HtmlTools.brAndNewline());
            sb.append("If you already know the PDB ID your are looing for, use this quickfind box:");
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlGenerator.generateQuickPDBBox("find.php"));
            sb.append(HtmlTools.startParagraph());
            sb.append("Last but not least, you can also use the address bar of your browser directly. The directory structure ");
            sb.append("on this web server is identical to the RCSB PDB ftp server. So to see the graphs for PDB file ");
            sb.append(HtmlTools.italic("8icd"));
            sb.append(", go to ");
            sb.append(HtmlTools.link("./ic/8icd/", "ic/8icd/"));
            sb.append(".");
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlTools.brAndNewline());
            
            // contact and authors
            
            sb.append(HtmlTools.heading("Contact and citing VPLGweb", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("VPLGweb was writteb by Tim Sch&auml;fer at the ");
            sb.append(HtmlTools.link("http://www.bioinformatik.uni-frankfurt.de", "Molecular Bioinformatics (MolBI) group"));
            sb.append(" of Ina Koch at Goethe-University Frankfurt am Main, Germany.");
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlTools.brAndNewline());
            
        sb.append(HtmlTools.endDiv());  // intro       
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
        
        // ------------- body -- footer ---------------
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
        sb.append(this.generateFooter(pathToBaseDir));
        
        sb.append(HtmlTools.endDiv());  // main
        sb.append(HtmlTools.endBody());
        sb.append(HtmlTools.endHtml());
        
        
        return sb.toString();
    }
    
    public String generateFindWebpage(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        
        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- The Visualization of Protein Ligand Graphs web server -- Search Results", pathToBaseDir));
        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBody());
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_MAIN));
        sb.append(this.generateLogo(pathToBaseDir));
        sb.append(HtmlGenerator.generateTopPageTitle("VPLGweb Search Results"));
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_SEARCH));
        
            // intro
        
            sb.append(HtmlTools.heading("Search results for your VPLGweb query", 2));
            sb.append(HtmlTools.startParagraph());
            
            // TODO: add PHP code to handle query here
            sb.append("<?php\n");
            
            //sb.append("error_reporting(E_ALL);\n");
            //sb.append("ini_set('display_errors', 'on');\n");
            sb.append("$pdbid = $_GET['pdbid'];\n");              
            sb.append("$chain = $_GET['chain'];\n");
            //sb.append("echo \"This line was written using PHP.<br/>\";\n");
            sb.append("echo \"PDB ID = $pdbid<br/>\";\n");              
            sb.append("echo \"Chain  = $chain<br/>\";\n");
            
            sb.append("$valid_pdbid = FALSE;\n");
            sb.append("$valid_chain = FALSE;\n");
            
            sb.append("if(ctype_alnum($pdbid) && strlen($pdbid) == 4) { $valid_pdbid = TRUE; }\n");
            sb.append("if(ctype_alnum($chain) && strlen($chain) == 1) { $valid_chain = TRUE; }\n");
            
            sb.append("if($valid_pdbid) {\n");
            sb.append("    echo \"PDB ID is valid.<br/>\";\n");
            
            sb.append("    if($valid_chain) {\n");
            sb.append("        echo \"Chain is valid.<br/>\";\n");
            sb.append("        $link = \"./\" . \"ic/\" . $pdbid . \"/\" . $chain . \"/\";\n");
            sb.append("    }\n");
            sb.append("    else {\n");
            sb.append("        echo \"PDB ID is valid but chain is not.<br/>\";\n");
            sb.append("        $link = \"./\" . \"ic/\" . $pdbid . \"/\";\n");
            sb.append("    }\n");
            //sb.append("    echo \"Link is $link.\";\n");
            
            sb.append("    if (file_exists($link)) {\n");
            sb.append("        echo \"<a href='\" . $link . \">\" . \"target\" . \"</a><br/>\";\n");
            sb.append("    }\n");
            sb.append("    else {\n");
            sb.append("        echo \"Sorry, no data available for that protein.<br/>\";\n");
            sb.append("    }\n");
            
            
            sb.append("}\n");            
            sb.append("else { echo \"ERROR: The given PDB ID is invalid. Invalid query.<br/>\"; }\n");                        
            sb.append("\n?>\n");
            
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlTools.brAndNewline());
            
            
        sb.append(HtmlTools.endDiv());  // search       
        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
                             
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_SEARCH));
        sb.append(HtmlTools.heading("Start a new search", 2));
        sb.append(HtmlTools.startParagraph());
        sb.append("You can ");
        sb.append(HtmlTools.link("search.html", "start a new search here"));
        sb.append(".").append(HtmlTools.brAndNewline());
        sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // search
        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
        
        // ------------- body -- footer ---------------
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
        sb.append(this.generateFooter(pathToBaseDir));
        
        
        
        sb.append(HtmlTools.endBody());
        sb.append(HtmlTools.endHtml());
        
        
        return sb.toString();
    }
    
    public String generateSearchWebpage(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        
        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- The Visualization of Protein Ligand Graphs web server -- Search Form", pathToBaseDir));
        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBody());
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_MAIN));
        sb.append(this.generateLogo(pathToBaseDir));
        sb.append(HtmlGenerator.generateTopPageTitle("VPLGweb Search Form"));
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_SEARCH));                
            sb.append(HtmlTools.heading("Find by PDB identifier", 2));            
            sb.append(HtmlTools.startParagraph());
            sb.append("Here you search by PDB identifier:");
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlGenerator.generateQuickPDBBox("find.php"));
        sb.append(HtmlTools.endDiv());  // search     
        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_SEARCH));
        sb.append(HtmlTools.heading("Find by PDB identifier and chain", 2));            
            sb.append(HtmlTools.startParagraph());
            sb.append("Here you search by PDB identifier and chain:");
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlGenerator.generateQuickPDBChainBox("find.php"));            
            sb.append(HtmlTools.brAndNewline());
        sb.append(HtmlTools.endDiv()); //search
            
            
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
        
        // ------------- body -- footer ---------------
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
        sb.append(this.generateFooter(pathToBaseDir));
        
        sb.append(HtmlTools.endDiv());  // main
        sb.append(HtmlTools.endBody());
        sb.append(HtmlTools.endHtml());
        
        
        return sb.toString();
    }
    
    public static String generateQuickPDBBox(String pathToSearchForm) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form name=\"input\" action=\"" + pathToSearchForm + "\" method=\"get\">");
        sb.append("PDB ID: ");
        sb.append("<input type=\"text\" name=\"pdbid\"  maxlength=\"4\" value=\"8icd\">");
        sb.append("<input type=\"submit\" value=\"Find protein\">");
        sb.append("</form>");
        return sb.toString();
    }
    
    public static String generateQuickPDBChainBox(String pathToSearchForm) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form name=\"input\" action=\"" + pathToSearchForm + "\" method=\"get\">");
        sb.append("PDB ID: ");
        sb.append("<input type=\"text\" name=\"pdbid\" maxlength=\"4\" value=\"8icd\">");
        sb.append("<input type=\"text\" name=\"chain\" maxlength=\"1\" value=\"A\">");
        sb.append("<input type=\"submit\" value=\"Find protein chain\">");
        sb.append("</form>");
        return sb.toString();
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
            System.out.println("   Wrote protein website for PDB '" + pdbid + "' to " + new File(proteinWebsiteHtmlFile).getAbsolutePath() + ".");
        } else {
            System.err.println("ERROR: Could not write protein website for PDB '" + pdbid + "' to " + new File(proteinWebsiteHtmlFile).getAbsolutePath() + ".");
        }
        
        // ------------------- chain webpages -----------------------
        
        File targetDirChain;
        for(String chain : pr.getAvailableChains()) {
            targetDirChain = new File(this.baseDir + fs + chain);
            String chainWebsiteHtmlFile = targetDirChain.getAbsolutePath() + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, chain);
            ArrayList<String> errors = IO.createDirIfItDoesntExist(targetDirChain);
            if(errors.isEmpty()) {
                        
                if(IO.stringToTextFile(chainWebsiteHtmlFile, this.generateChainWebpage(pr, chain, ".." + fs + ".." + fs + ".."))) {
                    System.out.println("    Wrote chain website for PDB '" + pdbid + "' chain '" + chain + "' to " + new File(chainWebsiteHtmlFile).getAbsolutePath() + ".");
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
        sb.append(HtmlGenerator.generateTopPageTitle(pdbid.toUpperCase()));

        
        // -------------------- protein info -------------------
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_PROTEIN));
            sb.append(HtmlTools.heading("Protein info", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("PDB identifier: ").append(pdbid).append(HtmlTools.brAndNewline());            
            
            for(String key : pr.getProteinMetaData().keySet()) {
                if(pr.getProteinMetaData().get(key) != null) {
                    sb.append(key).append(": ").append(pr.getProteinMetaData().get(key)).append(HtmlTools.br());
                }
            }
            
            sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // protein info

        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());

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
                    ProtMetaInfo pmi = pcr.getChainMetaData();
                    if(pmi != null) {
                        sb.append(HtmlTools.listItem(HtmlTools.link("" + chain + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, chain), "Chain " + chain) + " (Molecule " + pmi.getMolName() + " from organism " + pmi.getOrgScientific() + ")"));
                    } 
                    else {
                        sb.append(HtmlTools.listItem(HtmlTools.link("" + chain + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, chain), "Chain " + chain)));
                    }                                        
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
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
        
        // ----------------------------------- links --------------------------------
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_PROTEIN_LINKS));
        sb.append(HtmlTools.heading("Links to external tools and databases", 2));
        sb.append(HtmlTools.startParagraph());
        
        // RCSB PDB
        sb.append("Structure at RCSB PDB: ");
        sb.append(HtmlTools.link("http://www.rcsb.org/pdb/explore/explore.do?structureId=" + pdbid, pdbid + " @ RCSB PDB"));                        
        sb.append(HtmlTools.br());
        
        // CATH
        sb.append("Domain info from CATH: ");        
        sb.append(HtmlTools.link("http://www.cathdb.info/pdb/" + pdbid, pdbid + " @ CATH"));                        
        sb.append(HtmlTools.br());
        
        //SCOP
        sb.append("Structural classification from SCOP: ");        
        sb.append(HtmlTools.link("http://scop.mrc-lmb.cam.ac.uk/scop/search.cgi?pdb=" + pdbid, pdbid + " @ SCOP"));                        
        sb.append(HtmlTools.br());
        
        
        sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // chains
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
                        
        
        // ------------- body -- footer ---------------
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
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
        sb.append(HtmlGenerator.generateTopPageTitle(pdbid.toUpperCase() + " chain " + chain.toUpperCase()));


        // -------------------- chain info -------------------        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_TOP_ROW));
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_CHAIN));
        sb.append(HtmlTools.heading("Protein chain info", 2));
        sb.append(HtmlTools.startParagraph());
        sb.append("PDB chain: ").append(pdbid).append(" chain ").append(chain).append(HtmlTools.brAndNewline());
        //sb.append("Link to structure at RCSB PDB website: ");
        //sb.append(HtmlTools.link("http://www.rcsb.org/pdb/explore/explore.do?structureId=" + pdbid, pdbid)).append(HtmlTools.brAndNewline());
        
        if(pcr != null) {
            ProtMetaInfo pmi = pcr.getChainMetaData();
            if(pmi != null) {
                sb.append("Molecule ID: ").append(pmi.getMolID()).append(HtmlTools.brAndNewline());
                sb.append("Molecule name: ").append(pmi.getMolName()).append(HtmlTools.brAndNewline());
                sb.append("Organims (common): ").append(pmi.getOrgCommon()).append(HtmlTools.brAndNewline());
                sb.append("Organims (scientific): ").append(pmi.getOrgScientific()).append(HtmlTools.brAndNewline());
                sb.append("Taxon ID: ").append(pmi.getOrgTaxid()).append(HtmlTools.brAndNewline());
            }            
        }
        
        sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // chain
        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
        
        // -------------------- navigation -------------------
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_NAVIGATION));
        sb.append(HtmlTools.heading("Navigation", 2));
        sb.append(HtmlTools.startParagraph());
        
        // --- links to mother protein ---
        sb.append("Part of protein: ").append(HtmlTools.link(".." + fs + HtmlGenerator.getFileNameProtein(pdbid), pdbid)).append(HtmlTools.brAndNewline());
        
        // --- links to other chains of the same protein ---
        sb.append("Other chains of this protein: ");
        if(chains.size() > 1) {
            sb.append("All ").append(chains.size() - 1).append(" other chains of this protein:").append(HtmlTools.brAndNewline());
            sb.append(HtmlTools.uListStart());
            for(String otherChain : chains) {
                
                if(otherChain.equals(chain)) {
                    continue;
                }
                
                otherChainPcr = pr.getProteinChainResults(otherChain);
                if(otherChainPcr != null) {
                    ProtMetaInfo pmi = otherChainPcr.getChainMetaData();
                    if(pmi != null) {
                        sb.append(HtmlTools.listItem(HtmlTools.link("" + otherChain + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, otherChain), "Chain " + otherChain) + " (Molecule " + pmi.getMolName() + " from organism " + pmi.getOrgScientific() + ")"));
                    } 
                    else {
                        sb.append(HtmlTools.listItem(HtmlTools.link("" + otherChain + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, otherChain), "Chain " + otherChain)));
                    }                                        
                }
                else {
                    sb.append(HtmlTools.listItem("chain " + otherChain));
                }
                
                /*
                if(otherChainPcr != null) {
                    sb.append(HtmlTools.listItem(HtmlTools.link(".." + fs + otherChain + fs + HtmlGenerator.getFileNameProteinAndChain(pdbid, otherChain), "Chain " + otherChain)));
                }
                else {
                    sb.append(HtmlTools.listItem("chain " + otherChain));
                }
                */
            }
            sb.append(HtmlTools.uListEnd());
        }
        else {
            sb.append(HtmlTools.italic("The PDB file contains no other protein chains."));
            sb.append(HtmlTools.brAndNewline());
        }
        //sb.append(HtmlTools.endParagraph());
        //sb.append(HtmlTools.endDiv());  // navigation chains
        
        //sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
        
        // --- links to graphs ---
        //sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_NAVIGATION_GRAPHS));
        //sb.append(HtmlTools.startParagraph());
        if(pcr != null) {
            SSEGraph g;
            graphs = pcr.getAvailableGraphs();
            if(graphs.size() > 0) {
                sb.append("All ").append(graphs.size()).append(" graphs of this protein chain:");
                sb.append(HtmlTools.uListStart());
                
                // ---------------------- handle graph types ----------------------
                for(String graphType : graphs) {
                    
                    
                    g = pcr.getProteinGraph(graphType);
                    if(g != null) {
                        sb.append(HtmlTools.listItem("" + HtmlTools.link("#" + graphType, "The " + graphType + " graph") + " (|V|=" + g.numVertices() + ", |E|=" + g.numSSEContacts() + ")"));
                    } else {
                        sb.append(HtmlTools.listItem("" + HtmlTools.link("#" + graphType, "The " + graphType + " graph")));
                    }
                }
                sb.append(HtmlTools.uListEnd());
            }
        }
        
        sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // navigation graphs        
        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
        
        sb.append(HtmlTools.endDiv());  // top row
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_CLEAR));
        sb.append(HtmlTools.endDiv());  // clear
        
        sb.append(HtmlTools.br());
        sb.append(HtmlTools.br());
        
        // -------------------- protein graphs -------------------
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_PROTEIN_GRAPHS));
        sb.append(HtmlTools.heading("Protein graphs", 2));
        sb.append(HtmlTools.startParagraph());
        sb.append("A protein graph models the structure of a protein chain. Each vertex ");
        sb.append("in the graph represents a secondary structure element (SSE, e.g., an alpha helix ");
        sb.append("or a beta strand) or a ligand. An edge between two vertices in the graph means that, ");
        sb.append(" the respective SSEs are in contact in the 3D structure.");
        sb.append(HtmlTools.endParagraph());
        
        
        SSEGraph g;
        if(pcr == null) {
            sb.append(HtmlTools.italic("No result files are available for this chain."));
            sb.append(HtmlTools.brAndNewline());            
        }
        else {
            graphs = pcr.getAvailableGraphs();
            if(graphs.size() > 0) {                
                
                // ---------------------- handle graph types ----------------------
                for(String graphType : graphs) {
                    
                    sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_PROTEIN_GRAPH));
                    sb.append(HtmlTools.aname(graphType));
                    sb.append(HtmlTools.heading("The " + graphType + " graph", 3));
                    
                    if(HtmlGenerator.getGraphInfoString(graphType) != null) {
                        sb.append(HtmlTools.startParagraph());
                        sb.append(HtmlGenerator.getGraphInfoString(graphType));
                        sb.append(HtmlTools.endParagraph());
                    }
                    
                    // ---------------------- SSE info table ----------------------
                    sb.append(HtmlTools.heading("Graph structure", 4));
                    sb.append(HtmlTools.startParagraph());
                    g = pcr.getProteinGraph(graphType);
                    if(g != null) {           
                        
                        sb.append("This ").append(graphType).append(" graph consists of ").append(g.numVertices()).append(" SSEs and ").append(g.numSSEContacts()).append(" edges.");
                        sb.append(HtmlTools.br());
                        sb.append(HtmlTools.brAndNewline());
                        
                        if(g.numVertices() > 0) {
                            sb.append("Information on the " + g.numVertices() + " SSEs:");
                            sb.append(HtmlTools.brAndNewline());
                            sb.append(HtmlTools.tableStart(g.getVertexInfoFieldNames()));
                            for(int i = 0; i < g.numVertices(); i++) {
                                sb.append(HtmlTools.tableRow(g.getVertexInfoFieldsForSSE(i)));
                            }
                            sb.append(HtmlTools.tableEnd());                                                        
                            sb.append(HtmlTools.brAndNewline());
                        }             
                        
                        if(g.numSSEContacts() > 0) {
                            sb.append("Information on the " + g.numSSEContacts()+ " spatial SSE contacts:");
                            sb.append(HtmlTools.brAndNewline());
                            sb.append(HtmlTools.tableStart(g.getEdgeInfoFieldNames()));
                            
                            for(Integer[] edge : g.getEdgeList()) {
                                sb.append(HtmlTools.tableRow(g.getEdgeInfoFieldsForEdge(edge[0], edge[1])));
                            }
                            sb.append(HtmlTools.tableEnd());    
                            sb.append(HtmlTools.brAndNewline());
                        }
                        
                        if(g.numLigands() > 0) {
                            sb.append("Information on the " + g.numLigands()+ " ligands:");
                            sb.append(HtmlTools.brAndNewline());
                            sb.append(HtmlTools.tableStart(g.getLigandInfoFieldNames()));
                            
                            for(int i = 0; i < g.numVertices(); i++) {
                                if(g.getSSEBySeqPosition(i).isLigandSSE()) {
                                    sb.append(HtmlTools.tableRow(g.getLigandInfoFieldsForSSE(i)));
                                }                                
                            }
                            sb.append(HtmlTools.tableEnd());    
                            sb.append(HtmlTools.brAndNewline());
                            
                        }
                        
                    }
                    else {
                        sb.append(HtmlTools.italic("No SSE details are available for this graph."));
                        sb.append(HtmlTools.brAndNewline());
                    }
                    sb.append(HtmlTools.endParagraph());
                    
                    sb.append(HtmlTools.brAndNewline());
                    
                    // ---------------------- graph image ----------------------
                    
                    boolean useVectorImageOnWebsite = false;
                    boolean showImageOnWebsite = false;
                    
                    sb.append(HtmlTools.heading("Graph visualization", 4));
                    sb.append(HtmlTools.startParagraph());                    
                    File graphImage;
                    
                    if(useVectorImageOnWebsite) {
                        graphImage = pcr.getProteinGraphImageVector(graphType);
                    } else {
                        graphImage = pcr.getProteinGraphImageBitmap(graphType);
                    }
                    
                    if(showImageOnWebsite) {
                        if(IO.fileExistsIsFileAndCanRead(graphImage)) {
                            sb.append("Visualization of the ").append(graphType).append(" graph. The SSEs are ordered from the N terminus (left) to the"
                                + "C terminus (right). Edges represent spatial contacts and their color encodes the relative orientation (see legend below for details). Click image to enlarge.");
                            String relImagePath = graphImage.getName();
                            if(useVectorImageOnWebsite) {
                                sb.append(HtmlTools.svgImageObject(relImagePath));
                            } else {
                                sb.append(HtmlTools.imgClickToEnlarge(relImagePath, "" + graphType + " graph of " + pdbid + " chain " + chain, 800, null));  //TODO: we assume the image is in the same dir here, which is true atm but kinda ugly
                            }
                        } else {
                            sb.append(HtmlTools.italic("No image of this graph is available."));
                            sb.append(HtmlTools.brAndNewline());
                            System.err.println("WARNING: No valid " + (useVectorImageOnWebsite ? "vector" : "bitmap") + " graph image registered for PDB " + pdbid + " chain " + chain + " graphtype " + graphType + ". " + (graphImage == null ? "Image is null." : "Path is '" + graphImage.getAbsolutePath() + "'."));
                        }
                        sb.append(HtmlTools.brAndNewline());
                    }
                    
                    if(pcr.getAvailableGraphImages(graphType).size() > 0) {
                        sb.append("Download graph visualization images: ");
                        sb.append(HtmlTools.uListStart());
                        for(File imgFile : pcr.getAvailableGraphImages(graphType)) {                            
                            sb.append(HtmlTools.listItem("" + HtmlTools.link(imgFile.getName(), imgFile.getName())));                                                                        
                        }
                        sb.append(HtmlTools.uListEnd());
                    } else {
                        sb.append(HtmlTools.italic("No visualizations of this graph are available for download."));
                    }
                    sb.append(HtmlTools.brAndNewline());
                    sb.append(HtmlTools.endParagraph());
                    
                    sb.append(HtmlTools.brAndNewline());
                        
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
                        sb.append(HtmlTools.brAndNewline());
                    }
                    sb.append(HtmlTools.endParagraph());                    
                    sb.append(HtmlTools.br());
                    sb.append(HtmlTools.brAndNewline());
                    
                    sb.append(HtmlTools.endDiv());  // protein graph
                    sb.append(HtmlTools.brAndNewline());
                }                
                
            }
            else {
                sb.append(HtmlTools.italic("No graphs are available for this chain."));
                sb.append(HtmlTools.brAndNewline());
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
    
    public static String getVPLGwebServerUrl() {
        return "http://rcmd.org/vplgweb/";
    }
    
    public static String getVPLGSoftwareWebsite() {
        return "http://vplg.sourceforge.net/";
    }
    
    public String generateFooter(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        sb.append("<footer>\n");
        sb.append("<div class=\"footer\" align=\"center\">\n");
        sb.append(HtmlTools.startParagraph());
        
        sb.append("VPLGweb by ");
        sb.append("Tim Sch&auml;fer");
        sb.append(HtmlTools.brAndNewline());
        
        sb.append(HtmlTools.link(HtmlGenerator.getVPLGwebServerUrl(), "VPLGweb"));
        sb.append(" | ");
        sb.append(HtmlTools.link(HtmlGenerator.getVPLGSoftwareWebsite(), "VPLG"));
        sb.append(" | ");
        
        sb.append(HtmlTools.link("http://www.bioinformatik.uni-frankfurt.de", "MolBI Group"));
        
        
        sb.append(HtmlTools.endParagraph());
        sb.append("</div>\n");
        sb.append("</footer>\n");
        return sb.toString();
    }
    
    public static String getFileNameProtein(String pdbid) {
        //return "" + pdbid + ".html";
        return "index.html";
    }
    
    public static String getFileNameProteinAndChain(String pdbid, String chain) {
        //return "" + pdbid + "_" + chain + ".html";
        return "index.html";
    }
    
    public static String generateTopPageTitle(String t) {
        return "<h1 align=\"center\">" + t + "</h1>\n";
    }
    
    public static String getGraphInfoString(String graphType) {
        if(graphType.equals(SSEGraph.GRAPHTYPE_ALPHA)) {
            return "The alpha graph only considers the alpha helices of the protein chain.";
        }
        else if(graphType.equals(SSEGraph.GRAPHTYPE_BETA)) {
            return "The beta graph only considers the beta strands of the protein chain.";            
        }
        else if(graphType.equals(SSEGraph.GRAPHTYPE_ALBE)) {
            return "The albe graph considers both the alpha helices and beta strands of the protein chain.";            
        }
        else if(graphType.equals(SSEGraph.GRAPHTYPE_ALPHALIG)) {
            return "The alphalig graph considers the alpha helices and ligands of the protein chain.";
        }
        else if(graphType.equals(SSEGraph.GRAPHTYPE_BETALIG)) {
            return "The betalig graph considers the beta strands and ligands of the protein chain.";            
        }
        else if(graphType.equals(SSEGraph.GRAPHTYPE_ALBELIG)) {
            return "The albelig graph considers the alpha helices, beta strands and ligands of the protein chain.";            
        }
        else {
            return null;
        }
    }






    
	
}
