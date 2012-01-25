/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

/**
 * This static class holds various methods related to input and output.
 * 
 * @author ts
 */
public class IO {
   
    
    /**
     * Converts an area of interest within an input SVG image to PNG format and writes it to the specified file system location.
     * @param inputFileSVG the input file, has to be in SVG format (e.g., "infile.svg")
     * @param outputFilePNG the output path for the PNG image, including file extension (e.g., "outfile.png")
     * @param aoiX x coordinate of the upper left corner of the area of interest in the SVG image
     * @param aoiY y coordinate of the upper left corner of the area of interest in the SVG image
     * @param aoiWidth width of the area of interest
     * @param aoiHeight height of the area of interest
     * @return true if it worked out, false if some error occurred
     */
    public static Boolean convertSVGtoPNG(String inputFileSVG, String outputFilePNG, Integer aoiX, Integer aoiY, Integer aoiWidth, Integer aoiHeight) {
        
        
        PNGTranscoder png = new PNGTranscoder();
        
        // define area of interest
        Rectangle aoi = new Rectangle();
        aoi.x = aoiX;
        aoi.y = aoiY;
        aoi.width = aoiWidth;
        aoi.height = aoiHeight;
        
        png.addTranscodingHint(PNGTranscoder.KEY_AOI, aoi);
        BufferedReader istream;
        
        try {
            istream = new BufferedReader(new FileReader(inputFileSVG));
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: convertSVGtoPNG(): SVG input file  '" + inputFileSVG + "' not found.");
            return(false);
        }
        
        TranscoderInput input = new TranscoderInput(istream);
        FileOutputStream fout;
        TranscoderOutput fileOutput;
        
        try {
            fout = new FileOutputStream(outputFilePNG);
            fileOutput= new TranscoderOutput(fout);
            png.transcode(input, fileOutput);
        } catch (Exception ex) {
            System.err.println("ERROR: convertSVGtoPNG(): Can't write to output file '" + outputFilePNG + "'.");
            return(false);
        }
                                   
        try {
            fout.flush();
            fout.close();
        } catch (IOException ex) {
            System.err.println("ERROR: convertSVGtoPNG(): Writing of output file '" + outputFilePNG + "' failed, could not flush buffer.");
            return(false);
        }
        return(true);
    }
    
}
