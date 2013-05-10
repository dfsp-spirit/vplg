/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package htmlgen;

/**
 * Some utility functions for generating HTML strings.
 * @author ts
 */
public class HtmlTools {
    
    public static String link(String targetUrl, String label) {
        return "<a href=\"" + targetUrl + "\">" + label + "</a>";
    }
    
    public static String aname(String id) {
        return "<a name=\"" + id + "\"></a>\n";
    }
    
    public static String tableStart(String[] headerStrings) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>\n<tr>\n");
        for(String header : headerStrings) {
            sb.append("<th>").append(header).append("</th>\n");
        }        
        sb.append("</tr>\n");
        return sb.toString();
    }
    
    public static String tableRow(String[] cellStrings) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>\n");
        for(String header : cellStrings) {
            sb.append("<td>").append(header).append("</td>\n");
        }        
        sb.append("</tr>\n");
        return sb.toString();
    }
    
    public static String tableEnd() {
        return "</table>\n";
    }
    
    public static String uList(String[] strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>\n");
        for(String header : strings) {
            sb.append("<li>").append(header).append("</li>\n");
        }        
        sb.append("</ul>\n");
        return sb.toString();
    }
    
    public static String uListStart() {
        return "<ul>\n";
    }
    
    public static String uListEnd() {
        return "</ul>\n";
    }
    
    public static String oListStart() {
        return "<ol>\n";
    }
    
    public static String oListEnd() {
        return "</ol>\n";
    }
    
    public static String listItem(String s) {
        return "<li>" + s + "</li>\n";
    }
    
    public static String listItemStart() {
        return "<li>";
    }
    
    public static String listItemEnd() {
        return "</li>\n";
    }
    
    public static String oList(String[] strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ol>\n");
        for(String header : strings) {
            sb.append("<li>").append(header).append("</li>\n");
        }        
        sb.append("</ol>\n");
        return sb.toString();
    }
    
    public static String img(String relPathToImage, String altText) {
        return "<img src=\"" + relPathToImage + "\" alt=\"" + altText + "\">\n";
    }
    
    public static String img(String relPathToImage, String altText, int width, int height) {
        return "<img src=\"" + relPathToImage + "\" alt=\"" + altText + "\" width=\"" + width + "\" height=\"" + height + "\">\n";
    }
    
    public static String imgClickToEnlarge(String relPathToImage, String altText, Integer width, Integer height) {
        
        String widthString = " width=\"" + width + "\" ";
        if(width == null) {
            widthString = "";
        }
        
        String heightString = " height=\"" + height + "\" ";
        if(height == null) {
            heightString = "";
        }
        
        return "<a href=\"" + relPathToImage + "\"><img src=\"" + relPathToImage + "\" alt=\"" + altText + "\"" + widthString + heightString + "></a>\n";
    }    
    
    public static String svgImageObject(String relPathToImage) {
        return "<object data=\"" + relPathToImage + "\" type=\"image/svg+xml\"></object>";
    }
    
    public static String svgImageEmbed(String relPathToImage) {
        return "<embed src=\"" + relPathToImage + "\" type=\"image/svg+xml\"/>";
    }    
    
    public static String paragraph(String contents) {
        return "<p>\n" + contents + "</p>\n";
    }
    
    public static String paragraphClass(String contents, String classx) {
        return "<p class=\"" + classx + "\">\n" + contents + "</p>\n";
    }
    
    public static String heading(String contents, int level) {
        return "<h" + level + ">" + contents + "</h" + level + ">\n";
    }
    
    public static String startDiv(String classString) {
        return "<div class=\"" + classString + "\">\n";
    }
    
    public static String startDivHideableWithID(String id) {
        return "<div id=\"" + id + "\" name=\"" + id + "\" style=\"overflow:hidden;display:none\">\n";
    }
    
    /*
    public static String resizeJavascriptFunction() {
        StringBuilder sb = new StringBuilder();
        sb.append("<script language=\"JavaScript\" type=\"text/javascript\">\n");
        sb.append("<!--\n");
        sb.append("function setDivDisplayStyle(div_id, display_style) {\n");
        sb.append("    var the_div = document.getElementById('div_id');\n");
        sb.append("    the_div.style.display = display_style;\n");
        sb.append("}\n");
        sb.append("// -->\n");
        sb.append("</script>\n");
        return sb.toString();
    }
    */
    
    public static String resizeJavascriptFunctionFixedDiv(String divId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script language=\"JavaScript\" type=\"text/javascript\">\n");
        sb.append("<!--\n");
        sb.append("function setDivDisplayStyle").append(divId).append("(displaystyle) {\n");
        sb.append("    var thediv = document.getElementById('").append(divId).append("');\n");
        sb.append("    thediv.style.display = displaystyle;\n");
        sb.append("}\n");
        sb.append("// -->\n");
        sb.append("</script>\n");
        return sb.toString();
    }
    
    /*
    public static String divHideLinkFor(String divId, String label) {
        return "<a href=\"javascript:setDivDisplayStyle('" + divId + "', 'none')\">" + label + "</a>\n";
    }
    
    public static String divExpandLinkFor(String divId, String label) {
        return "<a href=\"javascript:setDivDisplayStyle('" + divId + "', 'block')\">" + label + "</a>\n";
    }
    */
    
    public static String divHideLinkFixedDivFor(String divId, String label) {
        return "<a href=\"javascript:setDivDisplayStyle" + divId + "('none')\">" + label + "</a>\n";
    }
    
    public static String divExpandLinkFixedDivFor(String divId, String label) {
        return "<a href=\"javascript:setDivDisplayStyle" + divId + "('block')\">" + label + "</a>\n";
    }
        
    /*
    public static String hideAndExpandLinksFor(String divId, String expandText, String hideText) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(HtmlTools.divExpandLinkFor(divId, expandText));
        sb.append(" | ");
        sb.append(HtmlTools.divHideLinkFor(divId, hideText));
        sb.append("]");
        sb.append(HtmlTools.brAndNewline());
        return sb.toString();
    }
    */
    
    public static String hideAndExpandLinksFixedDivFor(String divId, String expandText, String hideText) {
        StringBuilder sb = new StringBuilder();
        sb.append(HtmlTools.startSpan("tool"));
        sb.append("[");
        sb.append(HtmlTools.divExpandLinkFixedDivFor(divId, expandText));
        sb.append(" | ");
        sb.append(HtmlTools.divHideLinkFixedDivFor(divId, hideText));
        sb.append("]");
        sb.append(HtmlTools.brAndNewline());
        sb.append(HtmlTools.endSpan());
        return sb.toString();
    }
    
    public static String endDiv() {
        return "</div>\n";
    }
    
    public static String startSpan(String classString) {
        return "<span class=\"" + classString + "\">\n";
    }
    
    public static String endSpan() {
        return "</span>\n";
    }
    
    public static String startParagraph() {
        return "<p>\n";
    }
    
    public static String endParagraph() {
        return "</p>\n";
    }
    
    public static String hr() {
        return "<hr/>\n";
    }
    
    public static String startBody() {
        //return "<body>\n";
        return "<body onload=\"set_style_from_cookie()\">\n";
    }
     
    public static String endBody() {
        return "</body>\n";
    }
    
    public static String endHtml() {
        return "</html>\n";
    }
    
    public static String comment(String c) {
        return "<!-- " + c + " -->";
    }
    
    public static String italic(String c) {
        return "<span style=\"font-style:italic\">" + c + "</span>";
    }
    
    public static String br() {
        return "<br/>";
    }
    
    public static String brAndNewline() {
        return "<br/>\n";
    }
    
    
}
