/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package htmlgen;

import java.io.File;
import plcc.IO;

/**
 * A rather stupid CSS generator. Does nothing but create strings.
 * @author ts
 */
public class CssGenerator {
    
    public boolean writeDefaultCssFileTo(File outFile) {
        return IO.stringToTextFile(outFile.getAbsolutePath(), this.getDefaultCSS());
    }
    
    public String getDefaultCSS() {
        
        int colorScheme = 0;
                
        String colorBackgroundBody = "white";
        String colorBackgroundMain = "#DCDCDC";
        String colorSections = "#C0C0C0";
        String colorFont = "black";
        String colorLinks = "#800000";
        String colorLinksActive = "#FF0000";
        
        if(colorScheme == 1) {            
            // "2e3438"; // dark grayish
            // "628767"; // greenish
            // "aba972"; // dark yellowish
            // "dbba75"; // brighter yellow
            // "b25e54"; // red
            colorBackgroundBody = "2e3438";  // dark grayish
            colorBackgroundMain = "aba972"; // dark yellowish
            colorSections = "dbba75"; // brighter yellow
            colorFont = "black";
            colorLinks = "#800000";
            colorLinksActive = "#FF0000";            
        }
        
        if(colorScheme == 2) {
            // "4c2b2f"; // brownish
            // "e57152"; // orange
            // "e8de67"; // yellowish
            // "ffefc3"; // egg shell
            // "c0ccab"; // mint green
            colorBackgroundBody = "4c2b2f"; // brownish
            colorBackgroundMain = "c0ccab"; // mint green
            colorSections = "e8de67"; // yellowish
            colorFont = "black";
            colorLinks = "#800000";
            colorLinksActive = "#FF0000";
        }
        
        
        
        StringBuilder sb = new StringBuilder();
        sb.append("body {background-color:").append(colorBackgroundBody).append("; margin-top: 50px;margin-bottom: 50px;margin-right: 100px;margin-left: 100px;}\n");
        sb.append("p { font-size:10pt; font-family:Arial,sans-serif;color:").append(colorFont).append("}\n");
        sb.append("p.text { font-size:10pt; font-family:Arial,sans-serif; color:").append(colorFont).append("}\n");
        //sb.append(".protein_graphs { font-size:10pt; font-family:Arial,sans-serif; color:" + colorFont + ";width:80%; align:center; }\n");
        sb.append(".protein_graphs {margin-left: 0px;margin-top: 20px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #B0B0B0;padding: 20px 20px;}");
        sb.append(".protein_graph {margin-left: 0px;margin-top: 80px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:").append(colorSections).append(";padding: 20px 20px;width:90%;}");
        sb.append(".top_row { font-size:10pt; font-family:Arial,sans-serif; color:").append(colorFont).append(";width:100%; }\n");
        sb.append(".navigation {margin-left: 0px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:").append(colorSections).append(";padding: 20px 20px;}");
        //sb.append(".navigation_graphs p { font-size:10pt; font-family:Arial,sans-serif; color:" + colorFont + " }\n");
        sb.append(".protein {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:").append(colorSections).append(";padding: 20px 20px;}");
        sb.append(".main {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:").append(colorBackgroundMain).append(";padding: 20px 20px;width:100%;}");
        //sb.append(".navigation_graphs {margin-left: 0px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:" + colorSections + ";padding: 20px 20px;}");
        //sb.append(".navigation_chains {margin-left: 0px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:" + colorSections + ";padding: 20px 20px;}");
        //sb.append(".navigation {margin-left: 0px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:" + colorSections + ";padding: 20px 20px;width:45%;float:right;}");
        sb.append(".chain {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:").append(colorSections).append(";padding: 20px 20px; }");
        sb.append(".searchform {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:").append(colorSections).append(";padding: 20px 20px; }");
        sb.append(".chains {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:").append(colorSections).append(";padding: 20px 20px;}");
        sb.append(".protein_links {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color:").append(colorSections).append(";padding: 20px 20px;}");
        sb.append("ul { font-size:10pt; font-family:Arial,sans-serif; color:").append(colorFont).append(" }\n");
        sb.append("ol { font-size:10pt; font-family:Arial,sans-serif; color:").append(colorFont).append(" }\n");
        sb.append("li { font-size:10pt; font-family:Arial,sans-serif; color:").append(colorFont).append(" }\n");
        sb.append("p.code { font-size:10pt; font-family:monospace; color:blue }\n");
        sb.append("p.warn { font-size:10pt; font-family:monospace; color:red }\n");
        sb.append("p.top { font-size:10pt; font-family:Arial,sans-serif; color:").append(colorFont).append("; text-decoration:underline }\n");
        sb.append("p.big { font-size:14pt; font-family:Arial,sans-serif; color:").append(colorFont).append(" }\n");
        sb.append("p.tiny { font-size:7pt; font-family:Arial,sans-serif; color:").append(colorFont).append(" }\n");
        sb.append("div.footer { font-size:7pt; font-family:Arial,sans-serif; color:").append(colorFont).append(" }\n");
        sb.append(".footer p { font-size:7pt; font-family:Arial,sans-serif; color:").append(colorFont).append(" }\n");
        sb.append("p tinylink {font-size:7pt; color:").append(colorLinks).append("; text-decoration:none }\n");
        sb.append("a:link {font-size:10pt;font-family:Arial,sans-serif;color:").append(colorLinks).append(";text-decoration:none}\n");        
        sb.append("a:visited { font-size:10pt; color:").append(colorLinks).append("; text-decoration:none }\n");
        sb.append("a:hover { font-size:10pt; color:").append(colorLinksActive).append("; text-decoration:none }\n");
        sb.append("a:active { font-size:10pt; color:").append(colorLinksActive).append("; text-decoration:none }\n");
        sb.append("a:focus { font-size:10pt;color:").append(colorLinks).append("; text-decoration:none }\n");
        sb.append(".footer a:link {font-size:7pt;font-family:Arial,sans-serif;color:").append(colorLinks).append(";text-decoration:none}\n");        
        sb.append(".footer a:visited { font-size:7pt; color:").append(colorLinks).append("; text-decoration:none }\n");
        sb.append(".footer a:hover { font-size:7pt; color:").append(colorLinksActive).append("; text-decoration:none }\n");
        sb.append(".footer a:active { font-size:7pt; color:").append(colorLinksActive).append("; text-decoration:none }\n");
        sb.append(".footer a:focus { font-size:7pt;color:").append(colorLinks).append("; text-decoration:none }\n");
        sb.append("td.caption { text-align: left; font-size:8pt; font-family:Arial,sans-serif; color:").append(colorFont).append("; }\n");
        sb.append("table.image { margin-left: 4em; }\n");
        sb.append("Span.CENTERTEXT{text-align: center;}\n");
        sb.append("div.CENTERTEXT{text-align: center;}\n");
        sb.append("table, th, td {border: 1px black; font-size:10pt; font-family:monospace;}\n");
        sb.append("tr:nth-child(odd){ background-color:#eee; }\n");
        sb.append("tr:nth-child(even){ background-color:#fff; }");
        sb.append("#main {max-width: 800px;margin: 0 auto;}\n");
        //sb.append("footer {position: fixed;bottom: 0;left: 0;height: 50px;background-color: white;width: 100%;}");        
        sb.append("footer {bottom: 0;left: 0;height: 50px;width: 100%;}");        
        sb.append("#clear{clear: both;}");
        sb.append("#footer a {font-size:7pt; color:").append(colorLinks).append("; text-decoration:none }\n");
        //sb.append(".protein_graphs img{max-width:1000px;}");
        // rounded table stuff
        //sb.append("table {border-collapse:separate;border:solid black 1px;border-radius:6px;-moz-border-radius:6px;}\n");
        //sb.append("td, th {border-left:solid black 1px;border-top:solid black 1px;padding: 0px;}\n");
        //sb.append("th {border-top: none;}\n");
        //sb.append("td:first-child, th:first-child {border-left: none;}\n");
        //sb.append("th:first-child { -moz-border-radius: 6px 0 0 0; }\n");
        //sb.append("th:last-child { -moz-border-radius: 0 6px 0 0; }\n");
        //sb.append("tr:last-child td:first-child { -moz-border-radius: 0 0 0 6px; }\n");
        //sb.append("tr:last-child td:last-child { -moz-border-radius: 0 0 6px 0; }\n");
        // input forms
        sb.append("form    {font-size: 10px;font-weight: bold;text-decoration: none;-webkit-border-radius: 10px;-moz-border-radius: 10px;border-radius: 10px;padding:10px;border: 1px solid #999}");
        sb.append(".button, button {padding: 5px;margin: 5px;margin-left:10px;}");
        sb.append("input.button {font-size: 10px;padding: 5px;margin: 5px;margin-left:10px;}");
        sb.append("input {font-size: 10px;}");
        return sb.toString();
    }
    
}
