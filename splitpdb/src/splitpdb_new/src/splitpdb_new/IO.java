/*
 * This file is part of the PTGLtools software package.
 *
 * Copyright Tim Schäfer 2012. PTGLtools is free software, see the LICENSE and README files for details.
 *
 * Originally written by Tim Schäfer. Modified for Cif files by Mariella Zunker.
 */

package splitpdb_new;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author mariella
 */
public class IO {
    
        /**
     * Writes a string to a text file in a zip archive.
     * @param contents the string to write to the text file
     * @param outputZipFile the path and file name of the output zip file
     * @param fileNameInZipArchive the file name of the text file in the zip archive
     * @return true if everything was fine, false if errors occurred
     */
    public static Boolean writeStringToZippedTextFile(String contents, File outputZipFile, String fileNameInZipArchive) {
        try {            
            final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputZipFile));
            
            out.putNextEntry(new ZipEntry(fileNameInZipArchive));
            byte[] data = contents.getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            out.close();
            return(true);
        }
        catch(Exception e) {
            //System.err.println("WARNING: Could not write zip file '" + outputZipFile + "': '" + e.getMessage() + "'.");
            return(false);
        }
    }
}
