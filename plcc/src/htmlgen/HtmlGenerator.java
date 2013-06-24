/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package htmlgen;

import tools.DP;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plcc.ComplexGraph;
import plcc.IO;
import plcc.ProtMetaInfo;
import plcc.ProteinChainResults;
import plcc.ProteinResults;
import plcc.SSE;
import plcc.SSEGraph;
import plcc.Settings;

public class HtmlGenerator {
    
    //static Logger logger = LogManager.getLogger(HtmlGenerator.class.getName());
    
    private String[] relativeCssFilePathsFromBasedir;
    private String[] cssTitles; //for switching via JS
    private File baseDir;

    public void setCssTitles(String[] cssTitles) {
        this.cssTitles = cssTitles;
    }
    
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
    
    public String[] getCssTitles() {
        return cssTitles;
    }
    
    public static final String visualizeWebsiteFileNameRelativeToBasedir = "visualize.php";
    
    /**
     * Generates a visualize link for a whole PDB file. Loads the model without anything special.
     * @param pathToBaseDir path to base dir
     * @param pdbid the PDB identifier
     * @return a HTML link
     */
    public static String getVisualizeLinkPDB(String pathToBaseDir, String pdbid) {
        String webFs = "/";
        return HtmlTools.makeWebPath(pathToBaseDir + webFs + HtmlGenerator.visualizeWebsiteFileNameRelativeToBasedir + "?mode=structure&pdbid=" + pdbid);
    }
    
    /**
     * Generates a visualize link for a whole PDB file. Loads the model without anything special.
     * @param pathToBaseDir path to base dir
     * @param pdbid the PDB identifier
     * @param chainID the chain identifier
     * @return a HTML link
     */
    public static String getVisualizeLinkChainAllGraph(String pathToBaseDir, String pdbid, String chainID) {
        String webFs = "/";
        return HtmlTools.makeWebPath(pathToBaseDir + webFs + HtmlGenerator.visualizeWebsiteFileNameRelativeToBasedir + "?mode=allgraphs&pdbid=" + pdbid + "&chain=" + chainID);
    }
    
    /**
     * Generates a visualize link for a whole PDB file. Loads the model without anything special.
     * @param pathToBaseDir path to base dir
     * @param pdbid the PDB identifier
     * @param chainID the chain identifier
     * @param graphType the graph type
     * @return a HTML link
     */
    public static String getVisualizeLinkGraph(String pathToBaseDir, String pdbid, String chainID, String graphType) {
        String webFs = "/";
        return HtmlTools.makeWebPath(pathToBaseDir + webFs + HtmlGenerator.visualizeWebsiteFileNameRelativeToBasedir + "?mode=graph&pdbid=" + pdbid + "&chain=" + chainID + "&graphtype=" + graphType);
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
    public static final String DIV_APPLETAREA = "appletarea";
    

    /**
     * Sets a list of CSS files which should be linked in the headers of all produced HTML files. Ensure that
     * the paths are relative to the basedir (or absolute).
     * @param relativeCssFilePaths 
     */
    public void setRelativeCssFilePathsFromBasedir(String[] relativeCssFilePaths) {
        this.relativeCssFilePathsFromBasedir = relativeCssFilePaths;
    }
    
    /**
     * Generates all wepages which are only required once for VPLGweb, i.e., those which are independent of the protein (start page, search form, etc.).
     * @param outputBaseDir the base directory to use
     */
    public void generateCoreWebpages(File outputBaseDir) {
        String fs = System.getProperty("file.separator");
        String startWebsiteHtmlFile =  outputBaseDir.getAbsolutePath() + fs + "index.html";
        String searchWebsiteHtmlFile =  outputBaseDir.getAbsolutePath() + fs + "search.html";
        String findWebsitePhpFile =  outputBaseDir.getAbsolutePath() + fs + "find.php";
        String visualizeWebsitePhpFile =  outputBaseDir.getAbsolutePath() + fs + "visualize.php";
        
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
        
        if(IO.stringToTextFile(visualizeWebsitePhpFile, this.generateVisualize3DWebpage("."))) {
            System.out.println("   Wrote VPLGweb visualization website to " + new File(visualizeWebsitePhpFile).getAbsolutePath() + ".");
        } else {
            System.err.println("ERROR: Could not write VPLGweb visualization website to " + new File(visualizeWebsitePhpFile).getAbsolutePath() + ".");
        }
        
        
        
        
        if(this.writeResourceFilesToFilesystem(outputBaseDir.getAbsolutePath())) {
            System.out.println("   Wrote resources files like images to base directory '" + outputBaseDir.getAbsolutePath() + "'.");
        } else {
            System.err.println("ERROR: Could not write resource files to base directory '" + outputBaseDir.getAbsolutePath() + "'.");
        }
    }
    
    /**
     * Writes all resource files for the web pages (like images) from the JAR archive to the proper file system location for the website.
     * @param pathToBaseDir
     * @return true if all resources could be written, false otherwise
     */
    public boolean writeResourceFilesToFilesystem(String pathToBaseDir) {
        boolean allOk = true;
        String fs = File.separator;
        
        // copy logo
        String source = "resources/vplg_logo.png";
        File destination = new File(pathToBaseDir + fs + "vplg_logo.png");        
        try {            
            IO.copyResourceFileToFileSystemLocation(source, destination);                
        } catch(Exception e) {
            System.err.println("ERROR: Failed to copy logo from JAR resources at '" + source + "' to file system path '" + destination + "': '" + e.getMessage() + "'.");
            allOk = false;
        }
        
        // ----- copy more stuff -----
        HashMap<String, File> stuffToCopy = new HashMap<String, File>();
        
        // graph type images for the chain webpage
        stuffToCopy.put("resources/graphtype_alpha.png", new File(pathToBaseDir + fs + "graphtype_alpha.png"));
        stuffToCopy.put("resources/graphtype_alphalig.png", new File(pathToBaseDir + fs + "graphtype_alphalig.png"));
        stuffToCopy.put("resources/graphtype_beta.png", new File(pathToBaseDir + fs + "graphtype_beta.png"));
        stuffToCopy.put("resources/graphtype_betalig.png", new File(pathToBaseDir + fs + "graphtype_betalig.png"));
        stuffToCopy.put("resources/graphtype_albe.png", new File(pathToBaseDir + fs + "graphtype_albe.png"));
        stuffToCopy.put("resources/graphtype_albelig.png", new File(pathToBaseDir + fs + "graphtype_albelig.png"));
        
        // protein ligand graph image for start webpage, 7tim chain a
        stuffToCopy.put("resources/protein_ligand_graph.png", new File(pathToBaseDir + fs + "protein_ligand_graph.png"));
        
        // 7tim chain a structure rendered
        stuffToCopy.put("resources/7tim_a_pymol.png", new File(pathToBaseDir + fs + "7tim_a_pymol.png"));
        
        for(String res : stuffToCopy.keySet()) {
            source = res;
            destination = stuffToCopy.get(res);
            try {            
                IO.copyResourceFileToFileSystemLocation(source, destination);                
            } catch(Exception e) {
                System.err.println("ERROR: Failed to copy resource from JAR at '" + source + "' to file system path '" + destination + "': '" + e.getMessage() + "'.");
                allOk = false;
            }
        }                        
        return allOk;
    }
    
    public String generateStartWebpage(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        
        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- The Visualization of Protein Ligand Graphs web server -- Welcome", pathToBaseDir));
        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBodyAndCommonJS());
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_MAIN));
        sb.append(this.generateLogo(pathToBaseDir));
        sb.append(HtmlGenerator.generateTopPageTitle("Welcome to VPLGweb"));
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_INTRO));
        
            // intro
        
            sb.append(HtmlTools.heading("About VPLGWeb", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("VPLGweb is a web server which allows for quick access to Protein Ligand Graphs for all ");
            sb.append("protein chains in the RCSB Protein Data Bank (PDB). These graphs were computed from the ");
            sb.append("3D atom coordinates in PDB files and the secondary structure assignments of the DSSP algorithm using the ");
            sb.append(HtmlTools.linkBlank(HtmlGenerator.getVPLGSoftwareWebsite(), "VPLG software."));
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlTools.brAndNewline());
            
            // PLGs
            sb.append(HtmlTools.heading("Protein ligand graphs", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("A protein graph models the structure of a protein chain. Each vertex ");
            sb.append("in the graph represents a secondary structure element (SSE, e.g., an alpha helix ");
            sb.append("or a beta strand) or a ligand. An edge between two vertices in the graph means that ");
            sb.append("the respective SSEs are in contact in the 3D structure. Thus, protein ligand graphs model protein ");
            sb.append("topology on the super-secondary structure level.");
            sb.append(HtmlTools.brAndNewline());
            sb.append(HtmlTools.brAndNewline());
            sb.append("A typical protein ligand graph for a PDB chain has 15 to 50 vertices and a similar number of edges. More information on protein ligand graphs is available in the ");
            sb.append(HtmlTools.linkBlank(HtmlGenerator.getVPLGHelpWebsite(), "VPLG Documentation"));
            sb.append(".\n");            
            sb.append(HtmlTools.endParagraph());
            sb.append("<img src=\"").append(HtmlTools.makeWebPath(pathToBaseDir)).append("/protein_ligand_graph.png\" alt=\"A sample protein ligand graph (PDB 7TIM, chain A).\" style=\"display: block;margin-left: auto;margin-right: auto;\" />\n");
            sb.append(HtmlTools.brAndNewline());
            sb.append(HtmlTools.startParagraph("tinycenter"));
            sb.append(HtmlTools.bold("Protein ligand graph of 7TIM, chain A. "));
            sb.append("This graph consists of 13 alpha helices, 8 beta strands and a ligand. ");
            //sb.append(HtmlTools.brAndNewline());
            sb.append("The structure contains a parallel beta-barrel consisting of all 8 beta strands, and the ligand PGH is close to ");
            //sb.append(HtmlTools.brAndNewline());
            sb.append("several strands of the barrel and to some of the alpha helices surrounding it. ");
            //sb.append(HtmlTools.brAndNewline());
            //sb.append("The 3D structure of 7TIM is shown for comparison below.");
            sb.append("[");
            sb.append(HtmlTools.startSpan("tool"));
            sb.append(HtmlGenerator.popupWindowLink("Show 3D structure", HtmlTools.makeWebPath(pathToBaseDir) + "/7tim_a_pymol.png"));
            sb.append(HtmlTools.endSpan());
            sb.append("]");
            //sb.append(HtmlTools.brAndNewline());
            //sb.append("<img src=\"").append(HtmlTools.makeWebPath(pathToBaseDir)).append("/7tim_a_pymol.png\" alt=\"Rendered structure of PDB 7TIM chain A.\" style=\"display: block;margin-left: auto;margin-right: auto;\" />\n");            
            //sb.append(HtmlTools.brAndNewline());
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
            sb.append("If you already know the PDB ID your are looking for, use this quickfind box:");
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlGenerator.generateQuickPDBBox("find.php"));
            sb.append(HtmlTools.startParagraph());
            sb.append("Last but not least, you can also use the address bar of your browser directly. The directory structure ");
            sb.append("on this web server is identical to the RCSB PDB ftp server. So to see the graphs for PDB file ");
            sb.append(HtmlTools.italic("8icd"));
            sb.append(", go to ");
            sb.append(HtmlTools.link("./ic/8icd/", "ic/8icd/"));
            sb.append(" and for chain A of ");
            sb.append(HtmlTools.italic("7tim"));
            sb.append(", go to ");
            sb.append(HtmlTools.link("./ti/7tim/A/", "ti/7tim/A/"));
            sb.append(".");
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlTools.brAndNewline());
            
            // contact and authors
            
            sb.append(HtmlTools.heading("Contact and citing VPLGweb", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("VPLGweb was written by ");
            sb.append(HtmlTools.linkBlank("http://rcmd.org/ts/", "Tim Sch&auml;fer"));
            sb.append(" at the ");
            sb.append(HtmlTools.linkBlank("http://www.bioinformatik.uni-frankfurt.de", "Molecular Bioinformatics (MolBI) group"));
            sb.append(" of Ina Koch at Goethe-University Frankfurt am Main, Germany.");
            sb.append(HtmlTools.brAndNewline());
            sb.append(HtmlTools.brAndNewline());
            sb.append("Please cite the following publication if you use VPLG or VPLGweb:");
            sb.append(HtmlTools.brAndNewline());
            sb.append(HtmlTools.uListStart());
            sb.append(HtmlTools.listItemStart());
            sb.append("Tim Sch&auml;fer, Patrick May and Ina Koch.");
            sb.append(HtmlTools.italic("Computation and Visualization of Protein Topology Graphs Including Ligand Information."));
            sb.append("German Conference on Bioinformatics 2012. ");
            sb.append("(");
            sb.append(HtmlTools.linkBlank("http://sourceforge.net/p/vplg/wiki/Citing/", "Details and BibTex file"));
            sb.append(", ");
            sb.append(HtmlTools.linkBlank("http://drops.dagstuhl.de/opus/volltexte/2012/3722", "Open Access Paper"));
            sb.append(")");
            sb.append(HtmlTools.listItemEnd());
            sb.append(HtmlTools.uListEnd());
            sb.append(HtmlTools.brAndNewline());
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlTools.brAndNewline());
            
        sb.append(HtmlTools.endDiv());  // intro       
        sb.append(HtmlTools.brAndNewline());  
        
        // ------------- switch style form -----------------        
        sb.append(HtmlGenerator.jsSwitchStyleSheetForm());
        
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
        sb.append(HtmlTools.startBodyAndCommonJS());
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_MAIN));
        sb.append(this.generateLogo(pathToBaseDir));
        sb.append(HtmlGenerator.generateTopPageTitle("VPLGweb Search Results"));
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_SEARCH));
        
            // intro
        
            sb.append(HtmlTools.heading("Search results for your VPLGweb query", 2));
            sb.append(HtmlTools.startParagraph());
            
            // PHP code to handle query
            sb.append("<?php\n");
            
            //sb.append("error_reporting(E_ALL);\n");
            //sb.append("ini_set('display_errors', 'on');\n");
            sb.append("$pdbid = $_GET['pdbid'];\n");              
            sb.append("$chain = $_GET['chain'];\n");
            //sb.append("echo \"This line was written using PHP.<br/>\";\n");
            //sb.append("echo \"PDB ID = $pdbid<br/>\";\n");              
            //sb.append("echo \"Chain  = $chain<br/>\";\n");
            
            sb.append("$valid_pdbid = FALSE;\n");
            sb.append("$valid_chain = FALSE;\n");
            
            sb.append("if(ctype_alnum($pdbid) && strlen($pdbid) == 4) { $valid_pdbid = TRUE; }\n");
            sb.append("if(ctype_alnum($chain) && strlen($chain) == 1) { $valid_chain = TRUE; }\n");
            
            sb.append("if($valid_pdbid) {\n");
            //sb.append("    echo \"PDB ID is valid.<br/>\";\n");
            sb.append("    $mid_chars = substr($pdbid, 1, 2);\n");
            
            sb.append("    if($valid_chain) {\n");
            //sb.append("        echo \"Chain is valid.<br/>\";\n");
            sb.append("        $link = \"./\" . $mid_chars . \"/\" . $pdbid . \"/\" . $chain . \"/\";\n");
            sb.append("    }\n");
            sb.append("    else {\n");
            //sb.append("        echo \"PDB ID is valid but chain is not.<br/>\";\n");
            sb.append("        $link = \"./\" . $mid_chars . \"/\" . $pdbid . \"/\";\n");
            sb.append("    }\n");
            //sb.append("    echo \"Link is $link.\";\n");
            
            sb.append("    if (file_exists($link)) {\n");
            sb.append("        echo \"Protein data found.<br/><br/>\";\n");
            sb.append("        if ($valid_chain) {\n");
            sb.append("            echo \"<a href='\" . $link . \"'>\" . \"Protein $pdbid chain $chain\" . \"</a><br/>\";\n");
            sb.append("        }\n");
            sb.append("        else {\n");            
            sb.append("            echo \"<a href='\" . $link . \"'>\" . \"Protein $pdbid\" . \"</a><br/>\";\n");
            sb.append("        }\n");            
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
    
    /**
     * Writes the PHP function to generate the file name of the Jmol commands file without the starting and closing PHP brackets.
     * @return the function PHP code
     */
    public static String phpFunctionGetJmolFileName() {
        StringBuilder sb = new StringBuilder();
        sb.append("function getJmolFileName($pdbid, $chain, $graphtype) {\n");
        sb.append("  return $pdbid . \"_\" . $chain . \"_\" . $graphtype . \"_PG.jmol\";\n");
        sb.append("}\n");
        return sb.toString();
    }
    
    /**
     * Writes the JS code to generate the Jmol button, without the JS script tags.
     * @return the js code
     */
    public static String phpFunctionWriteJmolButton() {
        StringBuilder sb = new StringBuilder();
        sb.append("function writeJmolButtonJs($jmolcommands, $label) {\n");
        sb.append("  echo \"Jmol.jmolButton(myJmol, '\" . $jmolcommands . \"', '\" . $label . \"');\";\n");
        sb.append("}\n");
        return sb.toString();
    }
    
    
    
    public String generateSearchWebpage(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        
        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- The Visualization of Protein Ligand Graphs web server -- Search Form", pathToBaseDir));
        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBodyAndCommonJS());
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_MAIN));
        sb.append(this.generateLogo(pathToBaseDir));
        sb.append(HtmlGenerator.generateTopPageTitle("VPLGweb Search Form"));
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_SEARCH));                
            sb.append(HtmlTools.heading("Find by PDB identifier", 2));            
            sb.append(HtmlTools.startParagraph());
            sb.append("Here you can search by PDB identifier:");
            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlGenerator.generateQuickPDBBox("find.php"));
        sb.append(HtmlTools.endDiv());  // search     
        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_SEARCH));
        sb.append(HtmlTools.heading("Find by PDB identifier and chain", 2));            
            sb.append(HtmlTools.startParagraph());
            sb.append("Here you can search by PDB identifier and chain:");
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
    
    public String generateVisualize3DWebpage(String pathToBaseDir) {
        String fs = File.separator;
        String webFs = "/";
        StringBuilder sb = new StringBuilder();
        
        //-------------- header ------------
        sb.append(this.generateHeaderSpecialJS("VPLGweb -- The Visualization of Protein Ligand Graphs web server -- Visualization", pathToBaseDir, new String[] { HtmlTools.makeWebPath("jsmol/JSmol.min.js") }));
        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBodyAndCommonJS());
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_MAIN));
        sb.append(this.generateLogo(pathToBaseDir));
        sb.append(HtmlGenerator.generateTopPageTitle("VPLGweb 3D Visualization"));
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_SEARCH));   
        sb.append(HtmlTools.heading("About the visualization", 2));
        sb.append(HtmlTools.startParagraph());                
        sb.append("This page visualizes proteins using ");
        sb.append(HtmlTools.linkBlank("http://www.jmol.org", "Jmol/JSmol")); 
        sb.append(".");        
        
        // PHP code for mode specific stuff
        sb.append("<?php\n");
        sb.append("$mode = $_GET['mode'];\n");
        sb.append("if($mode == \"graph\" || $mode == \"allgraphs\") {\n");
        sb.append("    echo \" Use the buttons below the 3D window to visualize graphs in 3D. This will draw the different SSEs (vertices) as circles and show their 3D contacts (edges) as lines. The color code is identical to the 2D visualization.\";\n");
        sb.append("}\n");
        sb.append("\n?>\n");
        // end of PHP part
        
        sb.append(HtmlTools.endParagraph());                
        sb.append(HtmlTools.startParagraph("tiny"));    
        sb.append(HtmlTools.bold("Technical information: "));
        sb.append("The visualization requires a modern browser with JavaScript enabled. The default is to load the recommended ");
        sb.append(HtmlTools.link("./"  + HtmlGenerator.visualizeWebsiteFileNameRelativeToBasedir + "?version=html5", "HTML 5 version"));
        sb.append(", but you can also use the ");
        sb.append(HtmlTools.link("./" + HtmlGenerator.visualizeWebsiteFileNameRelativeToBasedir + "?version=java", "Java plugin version"));
        sb.append(" if you have the Java Web Plugin installed and activated in your browser. Also note that the model files are loaded from external servers, so this page will not work properly while these servers are down.");
        sb.append(HtmlTools.brAndNewline());                
        sb.append(HtmlTools.brAndNewline());
        
        sb.append(HtmlTools.endParagraph());                
        sb.append(HtmlTools.endDiv());  // search     
        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline()); 
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_APPLETAREA));                
        sb.append(HtmlTools.startParagraph());                        
        
        sb.append(HtmlGenerator.jsFunctionJSmolInit(pathToBaseDir + webFs + "jsmol"));
        sb.append(HtmlTools.endParagraph());                
        sb.append(HtmlTools.endDiv());  // applet area
            
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline()); 
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_SEARCH));   
        sb.append(HtmlTools.heading("Usage help", 2));             
        sb.append(HtmlTools.startParagraph());             
        sb.append("Use the context menu (right mouse button) to customize the viewer. Interact with the structure in 3D using the mouse (left mouse button): ");
        sb.append(HtmlTools.endParagraph());                
        sb.append(HtmlTools.uListStart());
        sb.append(HtmlTools.listItem("Rotate around x/y axis: drag"));
        sb.append(HtmlTools.listItem("Rotate around z axis: SHIFT + drag horizontally"));        
        sb.append(HtmlTools.listItem("Zoom in and out: SHIFT + drag vertically"));
        sb.append(HtmlTools.listItem("Translate: SHIFT + double-click, then drag"));
        sb.append(HtmlTools.uListEnd());
        sb.append(HtmlTools.startParagraph());                    
        
        // PHP code for mode specific help text
        sb.append("<?php\n");
        sb.append("$mode = $_GET['mode'];\n");
        sb.append("if( ! ($mode == \"graph\" || $mode == \"allgraphs\")) {\n");
        sb.append("    echo \" You can use the menu to load a structure or small molecule from a database of your choice. Try searching for 'caffeine' in PubChem or NCI. Or search for proteins like '7tim' in the RCSB PDB.\";\n");
        sb.append("}\n");
        sb.append("\n?>\n");
        // end of PHP part
        sb.append(HtmlTools.endParagraph());                
        sb.append(HtmlTools.endDiv());  // search     
        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                                        
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
        sb.append("<form name=\"input\" action=\"").append(pathToSearchForm).append("\" method=\"get\">");
        sb.append("PDB ID: ");
        sb.append("<input type=\"text\" name=\"pdbid\"  maxlength=\"4\" value=\"8icd\">");
        sb.append("<input type=\"submit\" value=\"Find protein\">");
        sb.append("</form>");
        return sb.toString();
    }
    
    public static String generateQuickPDBChainBox(String pathToSearchForm) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form name=\"input\" action=\"").append(pathToSearchForm).append("\" method=\"get\">");
        sb.append("PDB ID: ");
        sb.append("<input type=\"text\" name=\"pdbid\" maxlength=\"4\" value=\"8icd\">");
        sb.append(" Chain: ");
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

        /*
        logger.entry();
        if(! IO.dirExistsIsDirectoryAndCanWrite(baseDir)) {
            logger.error("ERROR: Cannot create webpages under directory '" + baseDir + "', does not exist or cannot write to it.");
            return;
        }
        */
        
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
        String fsWeb = "/";
        
        String pdbid = pr.getPdbid();
        List<String> chains = pr.getAvailableChains();
        ProteinChainResults pcr;

        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- PDB " + pdbid, pathToBaseDir));
        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBodyAndCommonJS());
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
            sb.append("3D Visualization: ").append(HtmlTools.linkBlank(HtmlGenerator.getVisualizeLinkPDB(pathToBaseDir, pdbid), "Open 3D viewer")).append(" (opens in new browser tab)").append(HtmlTools.brAndNewline());
            sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // protein info

        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
        
        // ------------------------ complex graph info ----------------------
        if(Settings.getBoolean("plcc_B_complex_graphs") && Settings.getBoolean("plcc_B_html_add_complex_graph_data")) {
            
            String compGraphSubDir = "ALL";
            
            sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_CHAINS));
            sb.append(HtmlTools.heading("Complex graph info", 2));
            sb.append(HtmlTools.startParagraph());
            sb.append("The complex graph describes the inter-chain contacts between chains of the PDB file. Each vertex represents a chain, and the edges represent 3D atom contacts between chains.\n");            
            sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());

            if(chains.size() > 0) {
                ComplexGraph compGraph = pr.getCompGraphRes().getCompGraph();
                if(compGraph != null) {
                    int numVerts = compGraph.getVertices().size();
                    int numEdges = compGraph.getEdges().size();
                    sb.append("The complex graph for PDB file ").append(pdbid).append(" consists of ").append(numVerts).append(" vertices and ").append(numEdges).append(" edges.\n");
                    sb.append(HtmlTools.br());
                    sb.append(HtmlTools.brAndNewline());
                    
                    File gmlCGFile = pr.getCompGraphRes().getComGraphFileGML();
                    if(IO.fileExistsIsFileAndCanRead(gmlCGFile)) {
                        System.out.println("CompGraph file exists at '" + gmlCGFile.getAbsolutePath() + "'.");
                        sb.append("Download complex graph: ");
                        sb.append(HtmlTools.link(compGraphSubDir + fsWeb + gmlCGFile.getName(), (gmlCGFile.getName() + " (GML format)\n") ));
                        sb.append(HtmlTools.brAndNewline());
                    } else {
                        sb.append("No complex graph download available.\n");
                        sb.append(HtmlTools.brAndNewline());
                    }
                }
                else {
                    sb.append("No complex graph data available.\n");
                    sb.append(HtmlTools.brAndNewline());
                }
            }
            else {
                sb.append("This PDB file contains no protein chains, no complex graph data available.\n");
            }

            sb.append(HtmlTools.endParagraph());
            sb.append(HtmlTools.endDiv());  // chains
            sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());
            
        }

        // -------------------- chain info -------------------
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_CHAINS));
        sb.append(HtmlTools.heading("Chain info and VPLGweb navigation", 2));
        sb.append(HtmlTools.startParagraph());
        
        if(chains.size() > 0) {
            sb.append("All ").append(chains.size()).append(" chains of the protein:<br/>");
            sb.append(HtmlTools.uListStart());
            for(String chain : chains) {
                pcr = pr.getProteinChainResults(chain);
                if(pcr != null) {
                    ProtMetaInfo pmi = pcr.getChainMetaData();
                    if(pmi != null) {
                        sb.append(HtmlTools.listItem(HtmlTools.link("" + chain + fsWeb + HtmlGenerator.getFileNameProteinAndChain(pdbid, chain), "Chain " + chain) + " (Molecule " + pmi.getMolName() + " from organism " + pmi.getOrgScientific() + ")"));
                    } 
                    else {
                        sb.append(HtmlTools.listItem(HtmlTools.link("" + chain + fsWeb + HtmlGenerator.getFileNameProteinAndChain(pdbid, chain), "Chain " + chain)));
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
        sb.append(HtmlTools.linkBlank("http://www.rcsb.org/pdb/explore/explore.do?structureId=" + pdbid, pdbid + " @ RCSB PDB"));                        
        sb.append(HtmlTools.br());
        
        // CATH
        sb.append("Domain info from CATH: ");        
        sb.append(HtmlTools.linkBlank("http://www.cathdb.info/pdb/" + pdbid, pdbid + " @ CATH"));                        
        sb.append(HtmlTools.br());
        
        //SCOP
        sb.append("Structural classification from SCOP: ");        
        sb.append(HtmlTools.linkBlank("http://scop.mrc-lmb.cam.ac.uk/scop/search.cgi?pdb=" + pdbid, pdbid + " @ SCOP"));                        
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
        String fsWeb = "/";
        
        String pdbid = pr.getPdbid();
        List<String> chains = pr.getAvailableChains();
        List<String> graphs;
        ProteinChainResults pcr, otherChainPcr;
        pcr = pr.getProteinChainResults(chain);

        //-------------- header ------------
        sb.append(this.generateHeader("VPLGweb -- PDB " + pdbid + " -- chain " + chain, pathToBaseDir));

        // ------------- body -- logo and title ---------------
        sb.append(HtmlTools.startBodyAndCommonJS());
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
        
        sb.append("3D visualization: ");
        sb.append(HtmlTools.linkBlank(HtmlGenerator.getVisualizeLinkChainAllGraph(pathToBaseDir, pdbid, chain.toLowerCase()), "Open 3D viewer"));
        sb.append(" (opens in new browser tab)").append(HtmlTools.brAndNewline());
        
        sb.append(HtmlTools.endParagraph());
        sb.append(HtmlTools.endDiv());  // chain
        
        sb.append(HtmlTools.br()).append(HtmlTools.brAndNewline());                        
        
        // -------------------- navigation -------------------
        
        sb.append(HtmlTools.startDiv(HtmlGenerator.DIV_NAVIGATION));
        sb.append(HtmlTools.heading("Navigation", 2));
        sb.append(HtmlTools.startParagraph());
        
        // --- links to mother protein ---
        sb.append("Part of protein: ").append(HtmlTools.link(".." + fsWeb + HtmlGenerator.getFileNameProtein(pdbid), pdbid)).append(HtmlTools.brAndNewline());
        
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
                        sb.append(HtmlTools.listItem(HtmlTools.link("" + otherChain + fsWeb + HtmlGenerator.getFileNameProteinAndChain(pdbid, otherChain), "Chain " + otherChain) + " (Molecule " + pmi.getMolName() + " from organism " + pmi.getOrgScientific() + ")"));
                    } 
                    else {
                        sb.append(HtmlTools.listItem(HtmlTools.link("" + otherChain + fsWeb + HtmlGenerator.getFileNameProteinAndChain(pdbid, otherChain), "Chain " + otherChain)));
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
                    
                    // js hide/show functions
                    sb.append(HtmlTools.resizeJavascriptFunctionFixedDiv("hide_" + graphType + "_structure"));
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
                    
                    sb.append("<img src=\"").append(HtmlTools.makeWebPath(pathToBaseDir)).append("/graphtype_").append(graphType).append(".png\" alt=\"The ").append(graphType).append(" graph.\" style=\"float:right;\" />\n");
                    
                    if(HtmlGenerator.getGraphInfoString(graphType) != null) {
                        sb.append(HtmlTools.startParagraph());
                        sb.append(HtmlGenerator.getGraphInfoString(graphType));
                        sb.append(HtmlTools.endParagraph());
                    }
                    
                    // ---------------------- SSE info table ----------------------
                    sb.append(HtmlTools.heading("Graph structure", 4));
                    sb.append(HtmlTools.startParagraph());
                    sb.append("The graph structure tables hold detailed information on the SSEs, spatial contacts and ligands of the ").append(graphType).append(" graph of this protein chain.");
                    sb.append(HtmlTools.endParagraph());
                    sb.append(HtmlTools.startDivHideableWithID("hide_" + graphType + "_structure"));
                    sb.append(HtmlTools.startParagraph());
                    g = pcr.getProteinGraph(graphType);
                    if(g != null) {           
                        
                        sb.append("This ").append(graphType).append(" graph consists of ").append(g.numVertices()).append(" SSEs and ").append(g.numSSEContacts()).append(" edges.");
                        sb.append(HtmlTools.br());
                        sb.append(HtmlTools.brAndNewline());
                        
                        sb.append(HtmlTools.startParagraph());
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
                    sb.append(HtmlTools.endDiv());  // end hide graph structure div
                    sb.append(HtmlTools.hideAndExpandLinksFixedDivFor("hide_" + graphType + "_structure", "&gt;&gt;Expand " + graphType + " graph structure tables", "&lt;&lt;Hide " + graphType + " graph structure tables"));  // draw hide/show graph structure buttons
                    sb.append(HtmlTools.endParagraph());
                    
                    sb.append(HtmlTools.brAndNewline());
                    
                    // ---------------------- graph image ----------------------
                    
                    boolean useVectorImageOnWebsite = false;
                    boolean showImageOnWebsite = false;
                    
                    sb.append(HtmlTools.heading("Graph 2D visualization", 4));
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
                            DP.getInstance().w("No valid " + (useVectorImageOnWebsite ? "vector" : "bitmap") + " graph image registered for PDB " + pdbid + " chain " + chain + " graphtype " + graphType + ". " + (graphImage == null ? "Image is null." : "Path is '" + graphImage.getAbsolutePath() + "'."));
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
                    
                    // ------------------------- 3D visualization -----------------------
                    sb.append(HtmlTools.heading("Graph 3D visualization", 4));
                    sb.append(HtmlTools.startParagraph());
                    
                    if(pcr.checkForGraphTypesWithValidJmolCmdFiles().contains(graphType)) {
                        sb.append("3D visualization: ");
                        sb.append(HtmlTools.linkBlank(HtmlGenerator.getVisualizeLinkChainAllGraph(pathToBaseDir, pdbid, chain.toLowerCase()), "Open 3D viewer"));
                        sb.append(" (opens in new browser tab)").append(HtmlTools.brAndNewline());
                    } else {
                        sb.append("No 3D visualization is available for this graph.").append(HtmlTools.brAndNewline());
                    }
                    
                    sb.append(HtmlTools.endParagraph());                    
                    sb.append(HtmlTools.br());
                    sb.append(HtmlTools.brAndNewline());
                    
                    // that's it
                    
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
    
    
    public String generateHeaderSpecialJS(String title, String pathToBaseDir, String[] relativeJsFilePathsFromBasedir) {
        StringBuilder sb = new StringBuilder();
                
        sb.append("<!DOCTYPE HTML>\n");
        sb.append("<html>\n<head>\n");
        sb.append("<title>").append(title).append("</title>\n");

        for(int i = 0; i < relativeCssFilePathsFromBasedir.length; i++) {            
            String cssFileName = relativeCssFilePathsFromBasedir[i];
            sb.append("<link href=\"").append(HtmlTools.makeWebPath(pathToBaseDir)).append(cssFileName).append("\" rel=\"stylesheet\" type=\"text/css\" title=\"").append(this.cssTitles[i]).append("\">\n");
        }
        
        for(int i = 0; i < relativeJsFilePathsFromBasedir.length; i++) {
            String jsFileName = relativeJsFilePathsFromBasedir[i];
            sb.append("<script type=\"text/javascript\" src=\"").append(jsFileName).append("\"></script>\n");
        }
        
        sb.append("<meta http-equiv=\"Default-Style\" content=\"red\">\n");
        sb.append("</head>\n");
        return sb.toString();        
    }
    
    public String generateHeader(String title, String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
                
        sb.append("<!DOCTYPE HTML>\n");
        sb.append("<html>\n<head>\n");
        sb.append("<title>").append(title).append("</title>\n");

        for(int i = 0; i < relativeCssFilePathsFromBasedir.length; i++) {            
            String cssFileName = relativeCssFilePathsFromBasedir[i];
            sb.append("<link href=\"").append(HtmlTools.makeWebPath(pathToBaseDir)).append(cssFileName).append("\" rel=\"stylesheet\" type=\"text/css\" title=\"").append(this.cssTitles[i]).append("\">\n");
        }                
        
        sb.append("<meta http-equiv=\"Default-Style\" content=\"red\">\n");
        sb.append("</head>\n");
        return sb.toString();        
    }
    
    public String generateLogo(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"logo\" align=\"center\">\n");
        sb.append("<img src=\"").append(HtmlTools.makeWebPath(pathToBaseDir)).append("/vplg_logo.png\" alt=\"VPLG logo\" style=\"float:right;\" />\n");
        sb.append(HtmlTools.heading("VPLGweb -- Visualization of Protein Ligand Graphs web server", 1));
        sb.append(HtmlTools.hr());
        sb.append("</div>\n");
        return sb.toString();
    }
    
    public static String getVPLGwebServerUrl() {
        return "http://rcmd.org/vplgweb/";
    }
    
    public static String getVPLGwebServerUrlRelative(String pathToBaseDir) {
        return HtmlTools.makeWebPath(pathToBaseDir);
    }
    
    public static String jsSwitchStyleSheetForm() {
        StringBuilder sb = new StringBuilder();
        sb.append("<form class=\"centerform\">\n");
        sb.append("VPLGweb Style: ");
        sb.append("<input type=\"submit\" onclick=\"switch_style('red');return false;\" name=\"theme\" value=\"Red\" id=\"red\">\n");
        sb.append("<input type=\"submit\" onclick=\"switch_style('blue');return false;\" name=\"theme\" value=\"Blue\" id=\"blue\">\n");
        sb.append("<input type=\"submit\" onclick=\"switch_style('green');return false;\" name=\"theme\" value=\"Green\" id=\"green\">\n");        
        sb.append("</form>\n");
        return sb.toString();
    }
    
    public static String getVPLGSoftwareWebsite() {
        return "http://www.bioinformatik.uni-frankfurt.de/tools/vplg/";
    }
    
    public static String getVPLGSourceforgeWebsite() {
        return "http://sourceforge.net/projects/vplg/";
    }
    
    public static String getVPLGHelpWebsite() {
        return "http://sourceforge.net/p/vplg/wiki/Home/";
    }
    
    public String generateFooter(String pathToBaseDir) {
        StringBuilder sb = new StringBuilder();
        sb.append("<footer>\n");
        sb.append("<div class=\"footer\" align=\"center\">\n");
        sb.append(HtmlTools.hr());
        sb.append(HtmlTools.startParagraph());
        
        sb.append("VPLGweb by ");
        sb.append("Tim Sch&auml;fer");
        sb.append(HtmlTools.brAndNewline());
        
        sb.append(HtmlTools.link(HtmlGenerator.getVPLGwebServerUrlRelative(pathToBaseDir), "VPLGweb"));
        //sb.append(HtmlTools.link(HtmlGenerator.getVPLGwebServerUrl(), "VPLGweb"));
        sb.append(" | ");
        sb.append(HtmlTools.linkBlank(HtmlGenerator.getVPLGSoftwareWebsite(), "VPLG"));
        sb.append(" | ");
        sb.append(HtmlTools.linkBlank(HtmlGenerator.getVPLGSourceforgeWebsite(), "VPLG project @ SF"));
        sb.append(" | ");
        sb.append(HtmlTools.linkBlank(HtmlGenerator.getVPLGHelpWebsite(), "VPLG Help and Documentation"));
        sb.append(" | ");
        
        sb.append(HtmlTools.linkBlank("http://www.bioinformatik.uni-frankfurt.de", "MolBI Group @ Goethe-University Frankfurt"));
        
        
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
    
    
    /**
     * Generates the JS code for a popup window. Use the popupWindowLink() function to access this.
     * @param title
     * @return 
     */
    public static String jsFunctionPopupWindow(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script lanuage=\"JavaScript\" type=\"text/Javascript\">\n");
        sb.append("<!--\n");                
        sb.append("function popup(someStuff){\n");
        sb.append("  popupWindow = window.open(someStuff, \"").append(title).append("\", \"width=800,height=600,toolbar=0,titlebar=1,menubar=0,location=0,status=0,directories=0\")  \n");
        sb.append("  popupWindow.document.title='").append(title).append("';\n");
        sb.append("  popupWindow.focus()\n");
        sb.append("}\n");
        sb.append("// -->\n");
        sb.append("</script>\n");
        return sb.toString();
    }
    
    /**
     * A popup link which uses the JS created by jsFunctionPopupWindow() function.
     * @param title the window title
     * @param stuffToPopup the URL to popup, can be an image file
     * @return the link string
     */
    public static String popupWindowLink(String linkTitle, String stuffToPopup) {
        return "<a href=\"javascript:popup('" + stuffToPopup + "')\">" + linkTitle + "</a>";
    }
    
    /*
    public static String jsFunctionJmolInit(String webPathToJmolFolder) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script language=\"JavaScript\" type=\"text/javascript\">\n");
        sb.append("<!--\n");                
        sb.append("jmolInitialize(").append(webPathToJmolFolder).append(")\n");
        sb.append("// -->\n");
        sb.append("</script>\n");
        return sb.toString();
    }
        
    
    public static String jsFunctionJmolPdb(String pdbid) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script language=\"JavaScript\" type=\"text/javascript\">\n");
        sb.append("<!--\n");                
        //TODO: add code here
        sb.append("TODO: add js code here\n"); 
        sb.append("// -->\n");
        sb.append("</script>\n");
        return sb.toString();
    }
    */
    
    
    /**
     * Function to add a JSMOL object.
     * See http://wiki.jmol.org/index.php/Jmol_JavaScript_Object for info.
     * You need to copy jsmol to $WEBROOT/jsmol/ for this to work.
     * @param webPathToJmolFolder
     * @return 
     */
    public static String jsFunctionJSmolInit(String webPathToJmolFolder) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script language=\"JavaScript\" type=\"text/javascript\">\n");
        sb.append("<!--\n");   
        
        // test
        sb.append("var pdb = getParameterByName('pdbid');\n");        
        sb.append("var mode = getParameterByName('mode');\n");        
        sb.append("var pdbInvalidorUnset = true;\n");
        sb.append("var pdbUnset = true;\n");
        //sb.append("document.write('<p>pdb= ' + pdb + '</p>');\n");
        sb.append("var loadModel=\":caffeine\"\n");
        sb.append("if(pdb != \"\") {\n");
        sb.append("  pdbUnset = false;\n");
        sb.append("  if(pdb.length == 4) {\n");
        sb.append("    if(pdb.match(/^[0-9a-z]+$/)) {\n");
        sb.append("      loadModel=\"=\" + pdb;\n");
        sb.append("      pdbInvalidorUnset = false;\n");
        sb.append("    }\n");        
        sb.append("  }\n");                        
        sb.append("}\n");        
        //sb.append("else {\n");
        //sb.append("  document.write('<div class=\"searchform\">');\n");
        //sb.append("  document.write('<p>Use the controls below to load a structure of your choice.</p>');\n");        
        //sb.append("  document.write('</div>');\n");
        //sb.append("  document.write('<br/><br/>');\n");
        //sb.append("}\n");
        sb.append("\n");
        
        // set mode to structure if unset
        sb.append("if(mode == \"\") {\n");
        sb.append("  mode=\"structure\";\n");
        sb.append("}\n");
        
        sb.append("var selectavailable = false;\n");        
        sb.append("if(mode == \"structure\") {\n");
        sb.append("  selectavailable=true;\n");
        sb.append("}\n");
        
        sb.append("var version = getParameterByName('version');\n");
        sb.append("if(version == \"\") {\n");
        sb.append("  version = \"html5\";\n");    // possible values: 'java' for java plugin, everything else is considered html5
        sb.append("}\n");        
        
        sb.append("\n");
        
        // for Java plugin version
        sb.append("var InfoJavaPlugin = {\n");
        sb.append("  addSelectionOptions: selectavailable,\n");
        sb.append("  color: \"#FFFFFF\",\n");
        sb.append("  debug: false,\n");
        sb.append("  defaultModel: loadModel,\n");
        sb.append("  width: 600,\n");
        sb.append("  height: 600,\n");
        sb.append("  isSigned: false,             // Java only\n");
        sb.append("  jarFile: \"jsmol/JmolApplet0.jar\",  // Java only\n");
        sb.append("  jarPath: \"./jsmol\",                // Java only\n");
        sb.append("  memoryLimit: 128,            // Java only\n");
        sb.append("  readyFunction: null,\n");
        sb.append("  script: null,\n");
        sb.append("  serverURL: \"http://rcmd.org/vplgweb/jsmol/jsmol.php\",\n");
        sb.append("  src: null,\n");
        sb.append("  use: \"Java noWebGL noHTML5 noImage\"\n");        
        sb.append("};	 \n");
        sb.append("\n");
        
        // for HTML5 version
        sb.append("var InfoHTML5 = {\n");
        sb.append("  addSelectionOptions: selectavailable,\n");
        sb.append("  color: \"#FFFFFF\",\n");
        sb.append("  debug: false,\n");
        sb.append("  defaultModel: loadModel,\n");
        sb.append("  width: 600,\n");
        sb.append("  height: 600,\n");
        sb.append("  j2sPath: \"jsmol/j2s\",              // HTML5 only\n");
        sb.append("  readyFunction: null,\n");
        sb.append("  script: null,\n");
        sb.append("  serverURL: \"http://rcmd.org/vplgweb/jsmol/jsmol.php\",\n");
        sb.append("  src: null,\n");
        sb.append("  use: \"HTML5 Image\"\n");        
        sb.append("};	 \n");
        sb.append("\n");
        
        // set CSS stuff for appearance
        sb.append("Jmol.setButtonCss(null, \"style='font-family:Arial,sans-serif;'\");\n");
        sb.append("Jmol.setAppletCss(null, \"style='font-family:Arial,sans-serif;margin-left=100px;'\");\n");
        sb.append("Jmol.setCheckboxCss(null, \"style='font-family:Arial,sans-serif;'\");\n");
        sb.append("Jmol.setLinkCss(null, \"style='font-family:Arial,sans-serif;'\");\n");
        sb.append("Jmol.setMenuCss(null, \"style='font-family:Arial,sans-serif;'\");\n");
        sb.append("Jmol.setRadioCss(null, \"style='font-family:Arial,sans-serif;'\");\n");
        sb.append("Jmol.setGrabberOptions([ [\"$\", \"DB: Small molecules at NCI\"], [\":\", \"DB: Small molecules at PubChem\"], [\"==\", \"DB: Ligands at PDB\"], [\"=\", \"DB: Macromolecules at PDB\"] ]);\n");
        sb.append("\n");
        
        // go
        sb.append("if(version == \"java\") {\n");
        sb.append("Jmol.getApplet(\"myJmol\", InfoJavaPlugin);\n");        
        sb.append("}\n");        
        sb.append("else {\n");        
        sb.append("  Jmol.getApplet(\"myJmol\", InfoHTML5);\n");
        sb.append("}\n");                   
        
        
        // add customized controls
        //sb.append("Jmol.jmolBr();");
        //sb.append("Jmol.jmolCheckbox(myJmol,\"spacefill on\",\"spacefill off\",\"Toggle display as spheres\");\n");
        
        sb.append("Jmol.jmolBr();\n");
        sb.append("document.write('<p class=\"tiny\">Jmol interactive scripting window:</p>');\n");        
        sb.append("Jmol.jmolCommandInput(\"myJmol\", \"Execute Jmol command\", \"90%\", \"jmol_cmd\", \"Jmol command prompt\")\n");
        sb.append("document.write('<p class=\"tiny\">');\n");
        sb.append("document.write('Selection examples: Try \"select 1-20:A; color red;\" to color residues 1 to 20 of chain A red.<br/>Display/hide examples: Try \"display :A\" to show only chain A, \"display [ALA]\" to show only alanine residues and \"display *\" to reset.<br/>Rendering examples: Try \"spacefill 100%\" for space-filling, \"wireframe 0.15; spacefill 20%;\" for Ball-and-stick, \"wireframe 0.0; spacefill 0%;\" to reset. See the <a href=\"http://www.jmol.org\" target=\"_blank\">Jmol documentation</a> for more info.');\n");        
        sb.append("document.write('</p>');\n");
        
        sb.append("if(pdbInvalidorUnset) {\n");
        sb.append("    document.write('<p>');\n");
        sb.append("    if(pdbUnset) {\n");
        sb.append("        document.write('No PDB ID specified.');\n");
        sb.append("    }\n");        
        sb.append("    else {\n");
        sb.append("        document.write('Invalid PDB ID specified.');\n");
        sb.append("    }\n");        
        sb.append("    document.write(' Loading default model caffeine.');\n");        
        sb.append("    document.write('</p>');\n");
        sb.append("}\n");        
        
        
        sb.append("// -->\n");
        sb.append("</script>\n");
        
        sb.append(HtmlGenerator.writePhpCodeToRenderVisualizeButtonsInJS());
        return sb.toString();
    }
    
    public static String writePhpCodeToRenderVisualizeButtonsInJS() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?php\n");
            
        sb.append(HtmlGenerator.phpFunctionGetJmolFileName());
        sb.append(HtmlGenerator.phpFunctionWriteJmolButton());
        sb.append("$mode = $_GET['mode'];\n");
        sb.append("$pdbid = $_GET['pdbid'];\n");              
        sb.append("$chain = $_GET['chain'];\n");        
        sb.append("$chain = strtoupper($chain);\n");        
        sb.append("$graphtype = $_GET['graphtype'];\n");
        //sb.append("echo \"This line was written using PHP.<br/>\";\n");
        //sb.append("echo \"Mode  = $mode<br/>\";\n");
        //sb.append("echo \"PDB ID = $pdbid<br/>\";\n");              
        //sb.append("echo \"Chain  = $chain<br/>\";\n");
        //sb.append("echo \"Graph type  = $graphtype<br/>\";\n");
        
        sb.append("if($mode == \"graph\" || $mode == \"allgraphs\") {\n");
        
        sb.append("    $graphtypes = array();\n");
        sb.append("    if($mode == \"graph\") {\n");
        sb.append("        $graphtypes = array($graphtype);\n");
        sb.append("    }\n");
        sb.append("    if($mode == \"allgraphs\") {\n");
        sb.append("        $graphtypes = array('alpha', 'beta', 'albe', 'alphalig', 'betalig', 'albelig');\n");
        sb.append("    }\n");

        sb.append("    $valid_pdbid = FALSE;\n");
        sb.append("    $valid_chain = FALSE;\n");
        sb.append("    if(ctype_alnum($pdbid) && strlen($pdbid) == 4) { $valid_pdbid = TRUE; }\n");
        sb.append("    if(ctype_alnum($chain) && strlen($chain) == 1) { $valid_chain = TRUE; }\n");
        
        sb.append("    if($valid_pdbid && $valid_chain) { echo \"<p>Visualization options for chain $chain of PDB $pdbid:</p>\";  }\n");
        
        sb.append("    foreach ($graphtypes as $graphtype) {\n");
        
        
        sb.append("        $valid_graphtype = FALSE;\n");
        sb.append("        $valid_all = FALSE;\n");

        sb.append("        if($graphtype == \"alpha\" || $graphtype == \"beta\" || $graphtype == \"albe\" || $graphtype == \"alphalig\" || $graphtype == \"betalig\" || $graphtype == \"albelig\") { $valid_graphtype = TRUE; }\n");
        sb.append("        $link = \"\";\n");

        sb.append("        if($valid_pdbid) {\n");
        //sb.append("            echo \"PDB ID is valid.<br/>\";\n");
        sb.append("            $mid_chars = substr($pdbid, 1, 2);\n");

        sb.append("            if($valid_chain) {\n");
        //sb.append("                echo \"Chain is valid.<br/>\";\n");
        sb.append("                if($valid_graphtype) {\n");
        //sb.append("                    echo \"Graph type is valid.<br/>\";\n"); 
       sb.append("                    $valid_all = TRUE;\n");
        sb.append("                    $jmolfile = getJmolFileName($pdbid, $chain, $graphtype);\n");
        sb.append("                    $link = \"./\" . $mid_chars . \"/\" . $pdbid . \"/\" . $chain . \"/\" . $jmolfile ;\n");       
        sb.append("                }\n");
        sb.append("                else {\n");
        sb.append("                    echo \"<p>ERROR: Graph visualization: PDB ID and Chain are valid but graph type is not.</p>\";\n");
        sb.append("                }\n");
        sb.append("            }\n");
        sb.append("            else {\n");
        sb.append("                echo \"<p>ERROR: Graph visualization: PDB ID is valid but chain is not. Cannot show graphs.</p>\";\n");
        sb.append("            }\n");
        //sb.append("            echo \"Link is '$link'.<br/>\";\n");

        sb.append("            if ($valid_all) {\n");
        sb.append("                if (file_exists($link)) {\n");
        //sb.append("                    echo \"Jmol command file found.<br/><br/>\";\n");
        // draw button
        //sb.append("                    echo \"The Jmol file is at $link.<br/>\";\n");
        //sb.append("                    $command = \"display :A\";\n");
        sb.append("                    $command = file_get_contents($link);\n");
        
        sb.append("                    $label = \"Visualize $graphtype graph\";\n");
        sb.append("                    \n?>\n");
        
        sb.append("                    <script language=\"JavaScript\" type=\"text/javascript\">\n");
        sb.append("                    <!--\n");   
        
        sb.append("                    <?php\n");
        sb.append("                    writeJmolButtonJs($command, $label);\n"); 
        sb.append("                    \n?>\n");
        
        sb.append("                    // -->\n");
        sb.append("                    </script>\n");
        
        sb.append("                    <?php\n");
        // button end
        sb.append("                }\n");
        sb.append("                else {\n");
        sb.append("                    echo \"<p>INFO: Graph visualization: No visualization available for $graphtype graph of protein $pdbid chain $chain.</p>\";\n");
        sb.append("                }\n");
        sb.append("            }\n");
        sb.append("            else {\n");
        sb.append("                echo \"<p>ERROR: Graph visualization: Some query parameters were invalid.</p>\";\n");
        sb.append("            }\n");
        

        sb.append("        }\n");            
        sb.append("        else {\n");
        sb.append("            echo \"<p>ERROR: Graph visualization: The given PDB ID is invalid. Invalid query.</p>\";\n");                        
        sb.append("        }\n");        
        sb.append("    }\n"); // foreach
        sb.append("}\n");
        sb.append("\n?>\n");
        
        return sb.toString();              
    }
    
    public static String jsJmolButton(String jmolCommands, String label) {
        return "Jmol.jmolButton(myJmol,\"" + jmolCommands + "\", \"" + label + "\");";
    }
    
    public static String jsFunctionSwitchStyleSheet() {
        StringBuilder sb = new StringBuilder();
        sb.append("<script language=\"JavaScript\" type=\"text/javascript\">\n");
        sb.append("<!--\n");                
        sb.append("var style_cookie_name = \"style\";\n");
        sb.append("var style_cookie_duration = 30;\n");
        sb.append("\n");
        sb.append("function switch_style ( css_title )\n");
        sb.append("{\n");
        sb.append("  // You may use this script on your site free of charge provided\n");
        sb.append("  // you do not remove this notice or the URL below. Script from\n");
        sb.append("  // http://www.thesitewizard.com/javascripts/change-style-sheets.shtml\n");
        sb.append("  var i, link_tag ;\n");
        sb.append("  for (i = 0, link_tag = document.getElementsByTagName(\"link\");i < link_tag.length ; i++ ) {\n");
        sb.append("    if ((link_tag[i].rel.indexOf( \"stylesheet\" ) != -1) && link_tag[i].title) {\n");
        sb.append("      link_tag[i].disabled = true ;\n");
        sb.append("      if (link_tag[i].title == css_title) {\n");
        sb.append("        link_tag[i].disabled = false ;\n");
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  set_cookie( style_cookie_name, css_title, style_cookie_duration );\n");
        sb.append("  }\n");
        sb.append("}\n");
        sb.append("\n");
        sb.append("function set_style_from_cookie()\n");
        sb.append("{\n");
        sb.append("  var css_title = get_cookie( style_cookie_name );\n");
        sb.append("  if (css_title.length) {\n");
        sb.append("    switch_style( css_title );\n");
        sb.append("  }\n");
        sb.append("}\n");
        sb.append("\n");
        sb.append("function set_cookie ( cookie_name, cookie_value, lifespan_in_days, valid_domain )\n");
        sb.append("{\n");
        sb.append("  // http://www.thesitewizard.com/javascripts/cookies.shtml\n");
        //sb.append("  console.log('Setting cookie.');\n");
        sb.append("  var domain_string = valid_domain ? (\"; domain=\" + valid_domain) : '' ;\n");
        sb.append("  document.cookie = cookie_name + \"=\" + encodeURIComponent( cookie_value ) + \"; max-age=\" + 60 * 60 * 24 * lifespan_in_days + \"; path=/\" + domain_string ;\n");
        sb.append("}\n");
        sb.append("\n");
        sb.append("function get_cookie ( cookie_name )\n");
        sb.append("{\n");
        sb.append("  // http://www.thesitewizard.com/javascripts/cookies.shtml\n");
        sb.append("  var cookie_string = document.cookie ;\n");
        sb.append("  if (cookie_string.length != 0) {\n");
        sb.append("    var cookie_value = cookie_string.match ('(^|;)[\\s]*' +cookie_name + '=([^;]*)' );\n");
        sb.append("    return decodeURIComponent ( cookie_value[2] ) ;\n");
        sb.append("  }\n");
        sb.append("  return '' ;\n");
        sb.append("}\n");
        sb.append("\n");
        sb.append("// -->\n");
        sb.append("</script>\n");
        return sb.toString();
    }
    
    public static String jsFunctionGetQueryVariable() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("<script language=\"JavaScript\" type=\"text/javascript\">\n");
        sb.append("<!--\n");                
        sb.append("function getParameterByName(name) {\n");
        sb.append("name = name.replace(/[\\[]/, \"\\\\\\[\").replace(/[\\]]/, \"\\\\\\]\");\n");
        sb.append("var regex = new RegExp(\"[\\\\?&]\" + name + \"=([^&#]*)\"),\n");
        sb.append("results = regex.exec(location.search);\n");
        sb.append("return results == null ? \"\" : decodeURIComponent(results[1].replace(/\\+/g, \" \"));\n");
        sb.append("}\n");
        sb.append("// -->\n");
        sb.append("</script>\n");
        return sb.toString();
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
    
    
    public static String commonJSFunctions() {
        StringBuilder sb = new StringBuilder();
        sb.append(HtmlGenerator.jsFunctionSwitchStyleSheet());
        sb.append(HtmlGenerator.jsFunctionPopupWindow("VPLGweb popup"));
        sb.append(HtmlGenerator.jsFunctionGetQueryVariable());
        return sb.toString();
    }






    
	
}
