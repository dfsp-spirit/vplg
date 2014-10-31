/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package plcc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import tools.DP;

/**
 *
 * @author ts
 */
public class DrawTools {
    
    public static final String FORMAT_PNG = "PNG";
    public static final String FORMAT_SVG = "SVG";
    public static final String FORMAT_TIFF = "TIFF";
    public static final String FORMAT_PDF = "PDF";
    public static final String FORMAT_JPEG = "JPEG";
    
    public static final String DEFAULT_FORMAT_BITMAP = FORMAT_PNG;
    public static final String DEFAULT_FORMAT_VECTOR = FORMAT_SVG;
    
    public static final String[] ALL_IMAGE_FORMATS = new String[] { FORMAT_PNG, FORMAT_SVG, FORMAT_TIFF, FORMAT_PDF, FORMAT_JPEG };
    
    public enum IMAGEFORMAT { PNG, SVG, TIFF, PDF, JPEG }
    
    /**
     * Returns the file extension including the dot, e.g., ".pdf" for format PDF.
     * @param f the image format
     * @return the file extension, including the dot (e.g., ".pdf" for format PDF).
     */
    public static String getFileExtensionForImageFormat(IMAGEFORMAT f) {
        if (f.equals(IMAGEFORMAT.JPEG)) {
            return ".jpg";
        } 
        else if(f.equals(IMAGEFORMAT.TIFF)) {
            return ".tiff";
        }
        else if(f.equals(IMAGEFORMAT.PDF)) {
            return ".pdf";
        }
        else if(f.equals(IMAGEFORMAT.SVG)) {
            return ".svg";
        }
        else if(f.equals(IMAGEFORMAT.PNG)) {
            return ".png";
        }
        else {
            DP.getInstance().e("DrawTools", "getFileExtensionForImageFormat: Unsupported image format, returning empty file extension.");
            return "";
        }
    }
    
    
    /**
     * Writes an SVGGraphics object (the contents of the canvas) to an image file in SVG format.
     * @param svgFilePath the path to the output file, including file extension
     * @param drawRes the graphics object, its contents will be saved to the image file
     * @throws IOException if something went wrong with writing the file
     */
    public static void writeG2dToSVGFile(String svgFilePath, DrawResult drawRes) throws IOException {
        // reset output stream to suppress the annoying output of the Apache batik library. Gets reset after lib call.
        OutputStream tmp=System.out;
        System.setOut(new PrintStream(new org.apache.commons.io.output.NullOutputStream()));
        drawRes.g2d.stream(new FileWriter(svgFilePath), false);     
        System.setOut(new PrintStream(tmp));
    }
    


    
    /**
     * Converts the input SVG file to various other formats.
     * @param svgInputFilePath the SVG input image
     * @param outputFileBasePathNoExt the base output file name (without . and without extension)
     * @param drawRes the drawRes, which is required to determine the ROI within the SVG
     * @param formats a list of formats, use the constants in DrawTools class
     * @return a list of files that were written successfully, by format
     */
    public static HashMap<IMAGEFORMAT, String> convertSVGFileToOtherFormats(String svgInputFilePath, String outputFileBasePathNoExt, DrawResult drawRes, IMAGEFORMAT[] formats) {
        
        HashMap<IMAGEFORMAT, String> outfilesByFormat = new HashMap<IMAGEFORMAT, String>();
        
        // write other formats
        SVGConverter svgConverter = new SVGConverter();
        svgConverter.setArea(drawRes.roi);
        svgConverter.setWidth((float) drawRes.roi.getWidth());
        svgConverter.setHeight((float) drawRes.roi.getHeight());

        String formatFileExt = "";
        for(IMAGEFORMAT format : formats) {           

            if(format.equals(IMAGEFORMAT.PNG)) {                
                svgConverter.setDestinationType(DestinationType.PNG);                
                formatFileExt = DestinationType.PNG_EXTENSION;
            } else if(format.equals(IMAGEFORMAT.JPEG)) {
                svgConverter.setDestinationType(DestinationType.JPEG);
                svgConverter.setQuality(0.8F);  // JPEG compression
                formatFileExt = DestinationType.JPEG_EXTENSION;
            } else if(format.equals(IMAGEFORMAT.TIFF)) {
                svgConverter.setDestinationType(DestinationType.TIFF);                
                formatFileExt = DestinationType.TIFF_EXTENSION;
            } else if(format.equals(IMAGEFORMAT.PDF)) {
                svgConverter.setDestinationType(DestinationType.PDF);
                formatFileExt = DestinationType.PDF_EXTENSION;
            } else {
                DP.getInstance().w("Unsupported image output format ignored.");
                continue;
            }
            
            String outputFileBasePathWithExt = outputFileBasePathNoExt + formatFileExt;

            svgConverter.setSources(new String[]{svgInputFilePath});            
            svgConverter.setDst(new File(outputFileBasePathWithExt));
            
            // reset output stream to suppress the annoying output of the Apache batik library. Gets reset after lib call.
            OutputStream tmp=System.out;
            System.setOut(new PrintStream(new org.apache.commons.io.output.NullOutputStream()));
        
            try {      
                svgConverter.execute();
                outfilesByFormat.put(format, outputFileBasePathNoExt + formatFileExt);
                System.setOut(new PrintStream(tmp));
            } catch (SVGConverterException ex) {
                System.setOut(new PrintStream(tmp));
                DP.getInstance().e("Could not convert SVG file to format '" + format + "': '" + ex.getMessage() + "'. Skipping.");
            } finally {
                System.setOut(new PrintStream(tmp));
            }
            
        }
        return outfilesByFormat;
    }
    
}
