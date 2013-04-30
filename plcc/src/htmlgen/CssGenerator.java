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
 *
 * @author ts
 */
public class CssGenerator {
    
    public boolean writeDefaultCssFileTo(File outFile) {
        return IO.stringToTextFile(outFile.getAbsolutePath(), this.getDefaultCSS());
    }
    
    public String getDefaultCSS() {
        StringBuilder sb = new StringBuilder();
        sb.append("body {background-color: white; margin-top: 50px;margin-bottom: 50px;margin-right: 100px;margin-left: 100px;}\n");
        sb.append("p { font-size:10pt; font-family:Arial,sans-serif;color:black }\n");
        sb.append("p.text { font-size:10pt; font-family:Arial,sans-serif; color:black }\n");
        //sb.append(".protein_graphs { font-size:10pt; font-family:Arial,sans-serif; color:black;width:80%; align:center; }\n");
        sb.append(".protein_graphs {margin-left: 0px;margin-top: 100px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #B0B0B0;padding: 20px 20px;width:80%;float:center;}");
        sb.append(".protein_graph {margin-left: 0px;margin-top: 100px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #C0C0C0;padding: 20px 20px;width:90%;float:center;}");
        sb.append(".top_row { font-size:10pt; font-family:Arial,sans-serif; color:black;width:100%; }\n");
        sb.append(".navigation {margin-left: 0px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #C0C0C0;padding: 20px 20px;width:45%;float:right;}");
        //sb.append(".navigation_graphs p { font-size:10pt; font-family:Arial,sans-serif; color:black }\n");
        sb.append(".protein {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #C0C0C0;padding: 20px 20px;}");
        sb.append(".main {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #DCDCDC;padding: 20px 20px;width:100%;}");
        //sb.append(".navigation_graphs {margin-left: 0px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #C0C0C0;padding: 20px 20px;}");
        //sb.append(".navigation_chains {margin-left: 0px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #C0C0C0;padding: 20px 20px;}");
        sb.append(".navigation {margin-left: 0px; -moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #C0C0C0;padding: 20px 20px;width:45%;float:right;}");
        sb.append(".chain {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #C0C0C0;padding: 20px 20px; width:45%;float:left;}");
        sb.append(".chains {-moz-border-radius: 15px;-webkit-border-radius: 15px;border-radius: 15px;background-color: #C0C0C0;padding: 20px 20px;}");
        sb.append("ul { font-size:10pt; font-family:Arial,sans-serif; color:black }\n");
        sb.append("ol { font-size:10pt; font-family:Arial,sans-serif; color:black }\n");
        sb.append("li { font-size:10pt; font-family:Arial,sans-serif; color:black }\n");
        sb.append("p.code { font-size:10pt; font-family:monospace; color:blue }\n");
        sb.append("p.warn { font-size:10pt; font-family:monospace; color:red }\n");
        sb.append("p.top { font-size:10pt; font-family:Arial,sans-serif; color:black; text-decoration:underline }\n");
        sb.append("p.big { font-size:14pt; font-family:Arial,sans-serif; color:black }\n");
        sb.append("p.tiny { font-size:7pt; font-family:Arial,sans-serif; color:black }\n");
        sb.append("div.footer { font-size:7pt; font-family:Arial,sans-serif; color:black }\n");
        sb.append(".footer p { font-size:7pt; font-family:Arial,sans-serif; color:black }\n");
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
        sb.append("table, th, td {border: 1px black; font-size:10pt; font-family:monospace;}\n");
        sb.append("tr:nth-child(odd){ background-color:#eee; }\n");
        sb.append("tr:nth-child(even){ background-color:#fff; }");
        sb.append("#main {max-width: 800px;margin: 0 auto;}\n");
        //sb.append("footer {position: fixed;bottom: 0;left: 0;height: 50px;background-color: white;width: 100%;}");        
        sb.append("footer {bottom: 0;left: 0;height: 50px;width: 100%;}");        
        sb.append("#clear{clear: both;}");
        sb.append("#footer a {font-size:7pt; color:#800000; text-decoration:none }\n");
        //sb.append(".protein_graphs img{max-width:1000px;}");
        return sb.toString();
    }
    
}
