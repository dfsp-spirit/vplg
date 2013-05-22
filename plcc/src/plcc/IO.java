/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package plcc;

import Tools.DP;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
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
import resources.Resources;

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
    
    public static boolean fileExistsIsFileAndCanRead(File f) {
        if(f != null) {
            if(f.isFile() && f.canRead()) {
                return true;
            }            
        }
        return false;
    }
    
    public static boolean dirExistsIsDirectoryAndCanWrite(File f) {
        if(f != null) {
            if(f.isDirectory() && f.canWrite()) {
                return true;
            }            
        }
        return false;
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
    
    
    /**
     * Writes a SVG Doc to a PNG format file.
     * @param doc
     * @param outputFilename
     * @param aoi
     * @throws Exception 
     */
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
    
    
    /**
     * Writes the string 'text' to the text file 'targetFile'. Tries to create the file and overwrite stuff in it.
     * @return true if it worked out, false otherwise. Will spit warning to STDERR if things go wrong.
     */ 
    public static Boolean stringToTextFile(String targetFile, String text) {
        String file = targetFile;
        FileWriter fw = null;
        PrintWriter pw = null;

        try {
            fw = new FileWriter(file);
            pw = new PrintWriter(fw);

        }
        catch (Exception e) {
            System.err.println("ERROR: Could not write to file '" + file + "': " + e.getMessage() + ".");            
            return(false);
        }

        pw.print(text);      
        pw.close();  // If it isn't closed it won't flush the buffer and large parts of the file will be missing!

        try {
            fw.close();
        } catch(Exception ex) {
            DP.getInstance().w("Could not close FileWriter for file '" + file + "': " + ex.getMessage() + ".");
            return(false);
        }
        
        return(true);
    }
    
    
    public static String intArrayToString(Integer[] ar) {
        String s = "";
        for(Integer i : ar) {
            s += i + " ";
        }
        return s;
    }
    
    
    public static String intArrayListToString(ArrayList<Integer> ar) {
        String s = "";
        for(Integer i : ar) {
            s += i + " ";
        }
        return s;
    }
    
    
    /**
     * Tries to create targetDir if it does not yet exist. Uses the existing dir (without deleting anything in there) if it does
     * already exist.
     * @param targetDir the directory, may be a dir structure
     * @return a list of error messages. If this list is empty, everything worked and the dir is ready. If not, something went wrong.
     */
    public static ArrayList<String> createDirIfItDoesntExist(File targetDir) {
        
        ArrayList<String> problems = new ArrayList<String>();
        
        if(targetDir.isDirectory()) {
            // dir already exsts
            if( ! targetDir.canWrite()) {
                problems.add("Cannot write to existing output directory '" + targetDir.getAbsolutePath() + "'.");
            } else {
                // all fine, we cqan write to it and will just use the existing one
                return new ArrayList<String>();
            }
        } else {
            if(targetDir.isFile()) {
                problems.add("Cannot create output directory '" + targetDir.getAbsolutePath() + "', file (not a directory!) with that name exists.");
            }
            else {
                try {
                    Boolean resMkdir = targetDir.mkdirs();
                    if(resMkdir) {
                        // all ok, we created it (and thus can write to it)
                        return new ArrayList<String>();                        
                    }
                }catch(Exception e) {
                    problems.add("Could not create required directory structure.");
                }
            }
        }
        
        return problems;
    }
    
    
    /**
     * Generates the PDB-style subdir path name for a given PDB ID and base directory
     * (like on the RCSB PDB FTP-server), but does NOT create the path in the file system.
     * 
     * @param pdbid the pdbid, a 4 character string
     * @param baseOutputDir the base dir under which to create the path
     * @return the path or null if no such path could be generated from the input data
     */
    public static File generatePDBstyleSubdirTreeName(String pdbid, File baseOutputDir) {
        
        if(baseOutputDir == null) {
            return null;
        }
        //System.out.println("generatePDBstyleSubdirTreeName: baseOutputDir=" + baseOutputDir.getAbsolutePath() + ".");
        
        File dirStructure;
        String fs = System.getProperty("file.separator");
        
        if(! (pdbid.length() == 4)) {
            //System.err.println("ERROR: PDB ID of length 4 required to output images in directory tree, using '" + baseOutputDir + "'.");
            dirStructure = null;
            //System.exit(1);
        } else {                    
            String mid2Chars = pdbid.substring(1, 3);                    
            dirStructure = new File(baseOutputDir.getAbsolutePath() + fs + mid2Chars + fs + pdbid);
        }
        
        return dirStructure;
    }
    
    /**
     * Generates the PDB-style subdir path name for a given PDB ID, chain and base directory
     * (similar to on the RCSB PDB FTP-server), but does NOT create the path in the file system.
     * 
     * @param pdbid the pdbid, a 4 character string
     * @param chain the pdb chain id, a 1 character string
     * @param baseOutputDir the base dir under which to create the path
     * @return the path or null if no such path could be generated from the input data
     */
    public static File generatePDBstyleSubdirTreeNameWithChain(File baseOutputDir, String pdbid, String chain) {
        
        if(baseOutputDir == null) {
            return null;
        }
        //System.out.println("generatePDBstyleSubdirTreeName: baseOutputDir=" + baseOutputDir.getAbsolutePath() + ".");
        
        File dirStructure;
        String fs = System.getProperty("file.separator");
        
        if(! (pdbid.length() == 4)) {
            //System.err.println("ERROR: PDB ID of length 4 required to output images in directory tree, using '" + baseOutputDir + "'.");
            dirStructure = null;
            //System.exit(1);
        } else {                    
            String mid2Chars = pdbid.substring(1, 3);                    
            dirStructure = new File(baseOutputDir.getAbsolutePath() + fs + mid2Chars + fs + pdbid + fs + chain);
        }
        
        return dirStructure;
    }
    
    /**
     * Generates the PDB-style subdir path name for a given PDB ID and base directory
     * (similar to on the RCSB PDB FTP-server), but does NOT create the path in the file system.
     * 
     * @param pdbid the pdbid, a 4 character string
     * @param baseOutputDir the base dir under which to create the path
     * @return the path or null if no such path could be generated from the input data
     */
    public static File generatePDBstyleSubdirTreeName(File baseOutputDir, String pdbid) {
        
        if(baseOutputDir == null) {
            return null;
        }
        //System.out.println("generatePDBstyleSubdirTreeName: baseOutputDir=" + baseOutputDir.getAbsolutePath() + ".");
        
        File dirStructure;
        String fs = System.getProperty("file.separator");
        
        if(! (pdbid.length() == 4)) {
            //System.err.println("ERROR: PDB ID of length 4 required to output images in directory tree, using '" + baseOutputDir + "'.");
            dirStructure = null;
            //System.exit(1);
        } else {                    
            String mid2Chars = pdbid.substring(1, 3);                    
            dirStructure = new File(baseOutputDir.getAbsolutePath() + fs + mid2Chars + fs + pdbid);
        }
        
        return dirStructure;
    }
    
    
    /**
     * Copies a file from this JAR's resources to a file system destination.
     * @param pathToResourceFile the path to the resource inside the JAR, e.g., "resources/vplg_logo.png".
     * @param targetFile the destination file
     * @return true it the file was copied, false otherwise
     * @throws Exception if the resource was not found
     */
    public static boolean copyResourceFileToFileSystemLocation(String pathToResourceFile, File targetFile) throws Exception {
        boolean found = false;
        InputStream is = Resources.class.getClassLoader().getResourceAsStream(pathToResourceFile);
        if(is != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            FileOutputStream fos = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.flush();
            br.close();
            is.close();
            found = true;
        } else {
            throw new Exception ("Resource '" + pathToResourceFile + "' not found.");
        }
        return found;
    }

    
}
