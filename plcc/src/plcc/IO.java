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
import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.w3c.dom.Document;

/**
 * This static class holds various methods related to input and output. It makes use of the Apache commons compress
 * library to extract .tar.gz. files.
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
        png.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(aoi.width) );
        png.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(aoi.height) );

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
    
    
    /** Untar an input file into an output file.

     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.tar' extension. 
     * 
     * @param inputFile     the input .tar file
     * @param outputDir     the output directory file. 
     * @throws IOException 
     * @throws FileNotFoundException
     *  
     * @return  The {@link List} of {@link File}s with the untared content.
     * @throws ArchiveException 
     */
    public static List<File> unTar(final File inputFile, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {
        
        final List<File> untaredFiles = new LinkedList<File>();
        final InputStream is = new FileInputStream(inputFile); 
        final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
        TarArchiveEntry entry = null; 
        while ((entry = (TarArchiveEntry)debInputStream.getNextEntry()) != null) {
            final File outputFile = new File(outputDir, entry.getName());
            if (entry.isDirectory()) {
                if (!outputFile.exists()) {
                    if (!outputFile.mkdirs()) {
                        throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                    }
                }
            } else {
                final OutputStream outputFileStream = new FileOutputStream(outputFile); 
                IOUtils.copy(debInputStream, outputFileStream);
                outputFileStream.close();
            }
            untaredFiles.add(outputFile);
        }
        debInputStream.close(); 

        return untaredFiles;
    }

    /**
     * Ungzip an input file into an output file.
     * <p>
     * The output file is created in the output folder, having the same name
     * as the input file, minus the '.gz' extension. 
     * 
     * @param inputFile     the input .gz file
     * @param outputDir     the output directory file. 
     * @throws IOException 
     * @throws FileNotFoundException
     *  
     * @return  The {@File} with the ungzipped content.
     */
    public static File unGzip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {

        final File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

        final GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
        final FileOutputStream out = new FileOutputStream(outputFile);

        for (int c = in.read(); c != -1; c = in.read()) {
            out.write(c);
        }

        in.close();
        out.close();

        return outputFile;
    }
    
    /**
     * Tries to delete all Files in the list.
     * @param files a list of files
     * @return the number of successfully deleted files
     */ 
    public static Integer deleteFiles(ArrayList<File> files) {
        
        Integer numSuccess = 0;
        
        for (File f : files) {
            if(f.delete()) {
                numSuccess++;
            }
        }
        
        return(numSuccess);
    }
    
    
    public static void writeSVGDOC2PNG (Document doc, String outputFilename, Rectangle aoi) throws Exception {
        
        PNGTranscoder trans = new PNGTranscoder();
        
        trans.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(aoi.width));
        trans.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(aoi.height));
        trans.addTranscodingHint(PNGTranscoder.KEY_AOI , aoi);

        TranscoderInput input = new TranscoderInput(doc);
        OutputStream ostream = new FileOutputStream(outputFilename);
        TranscoderOutput output = new TranscoderOutput(ostream);
        trans.transcode(input, output);

        ostream.flush();
        ostream.close();
    }

    
}
