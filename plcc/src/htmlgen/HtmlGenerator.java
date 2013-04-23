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
import plcc.IO;

public class HtmlGenerator {
	
	
	public String generateProteinChainWebpage(String pdbid, String[] chains, HashMap<String, File> graphTypeFiles, String[] relativeCssFilePaths) {
            
            StringBuilder sb = new StringBuilder();
            
            //-------------- header ------------
            sb.append("<html>\n<head>\n");
            sb.append("<title>" + "VPLG -- PDB ").append(pdbid).append(" --" + "</title>\n");
            
            for(String s : relativeCssFilePaths) {            
                sb.append("<link href=\"").append(s).append("\" rel=\"stylesheet\" type=\"text/css\">");
            }
            sb.append("</head>\n");
            
            // ------------- body -- logo and title ---------------
            sb.append("<body>\n");
            
            sb.append("<div class=\"logo\" align=\"center\">\n");
            sb.append("<h1>VPLGweb</h1>\n");
            sb.append("<hr/>\n");
            sb.append("</div>\n");
            
            
            // -------------------- protein info -------------------
            sb.append("<div class=\"protein\">\n");
            sb.append("<h2>Protein info</h2>\n");
            sb.append("<p>");
            sb.append("PDB identifier: ").append(pdbid).append("<br/>");
            sb.append("Link to structure at RCSB PDB website: <a href=\"http://www.rcsb.org/pdb/explore/explore.do?structureId=").append(pdbid).append("\">PDB ").append(pdbid).append("</a><br/>");
            sb.append("</p>\n");
            sb.append("</div>");
            
            
            // -------------------- chain info -------------------
            sb.append("<div class=\"chains\">\n");
            sb.append("<h2>Chain info</h2>\n");
            sb.append("<p>");
            sb.append("All ").append(chains.length).append(" chains of the protein:<br/>");
            for(String c : chains) {
                sb.append("chain <a href=\"./").append(c).append("/\">chain ").append(c).append("</a><br/>");
            }
            sb.append("</p>\n");
            sb.append("</div>");
            
            // ------------- body -- footer ---------------
            sb.append("<div class=\"footer\" align=\"center\">\n");
            sb.append("<hr/>\n");
            sb.append("<p>VPLGweb by ts</p>\n");
            sb.append("</div>\n");
            
            
            sb.append("</body>\n</html>\n");                                    
            
            return sb.toString();
	}                
        
        
        public String getCSS() {
            StringBuilder sb = new StringBuilder();
            sb.append("body {background-color: white; margin-top: 50px;margin-bottom: 50px;margin-right: 100px;margin-left: 100px;}\n");
            sb.append("p { font-size:10pt; font-family:Arial,sans-serif;color:black }\n");
            sb.append("p.text { font-size:10pt; font-family:Arial,sans-serif; color:black }\n");
            sb.append("p.code { font-size:10pt; font-family:monospace; color:blue }\n");
            sb.append("p.warn { font-size:10pt; font-family:monospace; color:red }\n");
            sb.append("p.top { font-size:10pt; font-family:Arial,sans-serif; color:black; text-decoration:underline }\n");
            sb.append("p.big { font-size:14pt; font-family:Arial,sans-serif; color:black }\n");
            sb.append("p.tiny { font-size:7pt; font-family:Arial,sans-serif; color:black }\n");
            sb.append("div.footer { font-size:7pt; font-family:Arial,sans-serif; color:black }\n");
            sb.append("a:link {font-size:10pt;font-family:Arial,sans-serif;color:#800000;text-decoration:none}\n");
            sb.append("p tinylink {font-size:7pt; color:#800000; text-decoration:none }\n");
            sb.append("a:visited { font-size:10pt; color:#800000; text-decoration:none }\n");
            sb.append("a:hover { font-size:10pt; color:#FF0000; text-decoration:none }\n");
            sb.append("a:active { font-size:10pt; color:#FF0000; text-decoration:none }\n");
            sb.append("a:focus { font-size:10pt;color:#800000; text-decoration:none }\n");
            sb.append("td.caption { text-align: left; font-size:8pt; font-family:Arial,sans-serif; color:black; }\n");
            sb.append("table.image { margin-left: 4em; }\n");
            sb.append("Span.CENTERTEXT{text-align: center;}\n");
            sb.append("div.CENTERTEXT{text-align: center;}\n");
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
            
            HtmlGenerator hg = new HtmlGenerator();
            String cssFilePath = outputBaseDir.getAbsolutePath() + fs + "vplgweb.css";
            IO.stringToTextFile(cssFilePath, hg.getCSS());
            
            String chainFilePath = outputBaseDir.getAbsolutePath() + fs + "index.html";
            IO.stringToTextFile(chainFilePath, hg.generateProteinChainWebpage(pdbid, chains, null, cssFiles));
        }

	
}
