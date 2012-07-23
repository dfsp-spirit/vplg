/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package splitpdb;

//import java.util.*;
import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 *
 * The 'splitpdb' program parses a file in PDB (RCSB Protein Data Bank) format and
 * extracts various meta data on the protein from it. If the file describes a single
 * model (i.e., is X-Ray crystallography data), the output file is the input file.
 * If the input file contains multiple models, i.e., is NMR data, splitpdb
 * splits the file, extracting general data (not related to models) and all the
 * data on a certain model (usually the 1st) from it
 * and discarding the rest (all data related to other models). The input PDB file is
 * never altered.
 *
 */
public class Main {

    private static String apptag = "[SPLITPDB] ";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println(apptag + "splitpdb -- extract data on a specific model from NMR-data RCSB Protein Data Bank files.");

        String handleModelID = "1";
        String pdbSource = null;
        String pdbTargetFileName = null;
        Boolean allowOverwrite = false;
        Boolean requestedModelFound = true;             // only used if a model is explicitely specified (-m)
        Boolean zippedInputFile = false;
        Boolean zipOutput = false;
        Integer debug = 0;  // debug level, higher value means more output

        // TODO: parse command line arguments here
        // ****************************************** parse command line args ******************************************
        if(args.length > 0) {

            if(args[0].equals("-h") || args[0].equals("--help")) {
                usage();
                System.exit(0);
            }

            // get PDB input file from first arg
            pdbSource = args[0];
            pdbTargetFileName = pdbSource + ".split";

            // parse the rest of the arguments, if any
            if(args.length > 1) {

                for (Integer i = 1; i < args.length; i++) {

                    String s = args[i];

                    if(s.equals("-h") || s.equals("--help")) {
                        usage();
                        System.exit(0);
                    }

                    if(s.equals("-a") || s.equals("--allow-overwrite")) {
                        allowOverwrite = true;
                    }
                    
                    if(s.equals("-x") || s.equals("--zipped-input")) {
                        zippedInputFile = true;
                    }
                    
                    if(s.equals("-z") || s.equals("--zip-output")) {
                        zipOutput = true;
                    }                    

                    if(s.equals("-o") || s.equals("--outfile")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            pdbTargetFileName = args[i+1];
                        }
                    }
                    
                    if(s.equals("-d") || s.equals("--debug")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            try {
                                debug = Integer.valueOf(args[i+1]);
                            } catch(Exception e) {
                                syntaxError();
                            }
                        }
                    }

                    if(s.equals("-m") || s.equals("--model")) {
                        if(args.length <= i+1 ) {
                            syntaxError();
                        }
                        else {
                            handleModelID = args[i+1];
                            requestedModelFound = false;
                        }
                    }



                } //end for loop
            }


        } else {
            usage();      // the first argument (PDB input file) is required!
            System.exit(1);
        }




        // ****************************************** check and open files ******************************************

        String pdbTargetGzArchiveName = pdbTargetFileName + ".gz";
        // Check whether input file exists and is readable
        File pdbSourceFile = new File(pdbSource);
        if( ! (pdbSourceFile.isFile() && pdbSourceFile.canRead()) ) {
            System.err.println(apptag + "ERROR: Could not read required source PDB file '" + pdbSourceFile + "', exiting.");
            System.exit(1);
        }

        // check whether output file exists
        File pdbTargetFile;
        
        if(zipOutput) {
            pdbTargetFile = new File(pdbTargetGzArchiveName);
        }
        else {
            pdbTargetFile = new File(pdbTargetFileName);
        }
        
        
        if( pdbTargetFile.exists() ) {
            if(allowOverwrite) {
                System.out.println(apptag + "  Target file '" + pdbTargetFile.getAbsolutePath() + "' exists, overwriting as requested.");
            }
            else {
                System.err.println(apptag + "ERROR: Target file '" + pdbTargetFile.getAbsolutePath() + "' exists, exiting (use -a to allow overwriting).");
                System.exit(1);
            }
        }

        if(pdbTargetFile.getAbsolutePath().equals(pdbSourceFile.getAbsolutePath())) {
            System.err.println(apptag + "ERROR: Input and output files must differ. Exiting.");
            System.exit(1);
        }
        
        // Open file readers for the input and output files
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String outputString = "";
        
        
        if(zippedInputFile) {
            try {
                reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(pdbSourceFile) ) ) );
            } catch(Exception e) {
                System.err.println(apptag + "ERROR: Could not open gz reader for gzipped text file '" + pdbSource + "', exiting.");
                System.exit(1);
            }
        }
        else {
            try {
                reader = new BufferedReader(new FileReader(pdbSourceFile));
            } catch(Exception e) {
                System.err.println(apptag + "ERROR: Could not open reader for text file '" + pdbSource + "', exiting.");
                System.exit(1);
            }            
        }
        

        System.out.println(apptag + "  Input PDB file '" + pdbSource + "' opened successfully.");

        
        if(! zipOutput) {
            try {
                writer = new BufferedWriter(new FileWriter(pdbTargetFile));
            } catch(Exception e) {
                System.err.println(apptag + "ERROR: Could not open writer for file '" + pdbTargetFileName + "', exiting.");
                System.exit(1);
            }

            System.out.println(apptag + "  Output PDB file '" + pdbTargetFileName + "' opened successfully.");
        }
        
        
        System.out.println(apptag + "Parsing...");

        

        // ****************************************** start the parsing ******************************************

        Boolean containsModels = false;             // whether this file contains any models at all
        Boolean inIncorrectModel = false;           // whether we are in a part of the PDB file that contains data on a model we are not interested in
        Integer numModels = 0;                      // number of models found while parsing
        String curModelID = null;                   // current model id while parsing
        Integer numModelsReported = 0;              // the number of models reported by the NUMMDL line of the PDB file (only NMR files must have this line)
        String line = null;                         // current line
        Integer lineNum = 0;                        // line number of the current line
        Integer numLinesWritten = 0;                // The number of lines written to the target PDB file
        String resolutionLine = "<NOT FOUND>";      // The REMARK 2 record holding the resolution info
        Double resolution = -1.0;                   // The resolution of the PDB file, -1.0 if it is 'NOT APPLICABLE'. This has to be
                                                    //  inited to -1.0 here because if the file contains no REMARK 2 line, this value has to be used.


        // Let's finally do the work we came here for
        try {

            while ((line = reader.readLine()) != null) {
                lineNum++;

                // The NUMMDL record indicates total number of models in a PDB entry.
                if(line.startsWith("NUMMDL")) {
                    try {
                        numModelsReported = Integer.valueOf(line.substring(10, 12).trim());
                        System.out.println(apptag + "  The NUMMDL record in line #" + lineNum + " reports " + numModelsReported + " models in this PDB file.");
                    } catch(Exception ex) {
                        System.err.println(apptag + "WARNING: Misformed NUMMDL record found in line " + lineNum + ", ignoring.");
                        numModelsReported = 0;
                    }
                    continue;       // Don't write the NUMMDL line
                }
                // REMARK RESOLUTION holds the resolution of the file. Not all files have this.
                else if(line.startsWith("REMARK   2 RESOLUTION.")) {
                    resolutionLine = line.trim();
                    resolution = getResFromREMARK2Line(resolutionLine);
                }
                // marks the end of the current model
                else if(line.startsWith("ENDMDL")) {
                    // We hit the end of the current model so we are not in any model atm. Obviously, we can't be in the wrong model then. ;)
                    if(debug > 0) {
                        System.out.println(apptag + "    Found end of model '" + curModelID + "' in line " + lineNum + ".");
                    }
                    inIncorrectModel = false;
                    continue;       // Don't write the ENDMDL line
                }
                // a new model starts here
                else if (line.startsWith("MODEL")) {
                    containsModels = true;
                    numModels++;
                    curModelID = line.substring(12, 14).trim();

                    if(curModelID.length() < 1) {
                        System.err.println(apptag + "WARNING: Found MODEL record with empty model ID in line " + lineNum + ".");
                    }
                    else {
                        if(debug > 0) {
                            System.out.println(apptag + "  #" + numModels + ": Found MODEL with model ID '" + curModelID + "' in line " + lineNum + ".");
                        }
                    }

                    // Check whether this is the model we are interested in
                    if( ! curModelID.equals(handleModelID)) {
                        inIncorrectModel = true;
                    }
                    else {
                        requestedModelFound = true;
                    }

                    continue;       // Don't write the MODEL line
                }
                else {
                    // We are not interested in this record type
                }

                // We checked everything. Copy all lines of the PDB file which are not within the boundaries of a model we are not interested in
                //  to the new file. Note that we copy all the model-independent lines (e.g., all the header lines etc) to the output file.

                if( ! inIncorrectModel) {
                    if(zipOutput) {
                        outputString += line + "\n";
                    }
                    else {
                        writer.write(line);
                        writer.newLine();   // Write system dependent end of line.
                    }                    
                    numLinesWritten++;
                }
                
            }
        } catch(Exception e) {
            System.err.println(apptag + "ERROR: Could not read/write while parsing PDB files: '" + e.getMessage() + "'.");
            //e.printStackTrace();
            System.exit(1);
        }




        // ****************************************** close files and exit ******************************************

        // close reader
        try {
            reader.close();
        } catch (Exception e) {
            // This ain't good (a potential waste of resources), but it should not be considered a catastrophe.
            System.err.println(apptag + "WARNING: Could not close file reader for input file: '" + e.getMessage() + "'.");
        }

        if(zipOutput) {
            // close writer to flush the buffer            
            if(IO.writeStringToZippedTextFile(outputString, new File(pdbTargetGzArchiveName), pdbTargetFileName)) {
                System.out.println(apptag + "Wrote gzipped output PDB file to archive'" + pdbTargetGzArchiveName + "', file name in archive is '" + pdbTargetFileName + "'.");
            }
            else {
                System.err.println(apptag + "ERROR: Could not write gzipped PDB output file to '" + pdbTargetGzArchiveName + "'.");
            }
            pdbTargetFileName = pdbTargetGzArchiveName;    // makes sure the output at the end has the correct output file name
        }
        else {
            // close writer to flush the buffer
            try {
                writer.flush();
                writer.close();
            } catch (Exception e) {
                System.err.println(apptag + "ERROR: Could not close file writer for output file. Buffers may not have been flushed, output file may be incomplete.");
                System.exit(1);
            }            
        }
        

        // We're done.
        System.out.println(apptag + "Parsed " + lineNum + " lines in input file, wrote " + numLinesWritten + " of them to output file.");
        System.out.println(apptag + "Input PDB file reported " + numModelsReported + " models in NUMMDL record, found " + numModels + " models while parsing data.");
        System.out.println(apptag + "Resolution line was '" + resolutionLine + "', resolution value is '" + resolution + "'.");

        if(! numModelsReported.equals(numModels)) {
            System.err.println(apptag + "WARNING: Found number of models does not match info in NUMMDL record pf PDB file.");
        }
      

        if( ! requestedModelFound) {
            System.err.println(apptag + "ERROR: Whole PDB input file parsed but the specified model '" + handleModelID + "' was not found (omit -m if you are not sure whether a certain model exists).");
            System.exit(1);
        }

        if(! containsModels) {
            System.out.println(apptag + "Input PDB file does not contain any models, output file is the input file.");
            System.out.println(apptag + "All done, output file written to '" + pdbTargetFileName + "'. Exiting.");
            System.exit(2);
        }

        System.out.println(apptag + "All done, output file with information on model '" + handleModelID + "' written to '" + pdbTargetFileName + "'. Exiting.");
        System.exit(0);
    }

    
    /**
     * Prints usage info to STDOUT.
     */
    public static void usage() {
        System.out.println(apptag + "This program is part of VPLG, http://vplg.sourceforge.net. Copyright Tim Schaefer 2012.");
        System.out.println(apptag + "VPLG is free software and comes without any warranty. See LICENSE for details.");
        System.out.println(apptag + "");
        System.out.println(apptag + "USAGE: java -jar splitpdb.jar <infile> [OPTIONS]");
        System.out.println(apptag + "       java -jar splitpdb.jar --help");
        System.out.println(apptag + "valid OPTIONS are: ");
        System.out.println(apptag + "-h | --help              : show this help message and exit.");
        System.out.println(apptag + "-a | --allow-overwrite   : overwrite the output file if it exists.");
        System.out.println(apptag + "-d | --debug <level>     : set debug level to <level>, which has to be an integer.");
        System.out.println(apptag + "-o | --outfile <ofile>   : write output pdb file to <ofile> (instead of '<infile>.split').");
        System.out.println(apptag + "-m | --model   <mid>     : extract data on model with id <mid> (instead of model 1).");
        System.out.println(apptag + "-x | --zipped-input      : assume that the input PDB file is in zipped format and has to be extracted.");        
        System.out.println(apptag + "-z | --zip-output        : write the output file in zipped format (archive will be named <ofile>.gz).");
        System.out.println(apptag + "");
        System.out.println(apptag + "EXAMPLES: java -jar splitpdb.jar 1blr.pdb -a");
        System.out.println(apptag + "          java -jar splitpdb.jar 1blr.pdb -o /tmp/1st_mdl/1blr.pdb");
        System.out.println(apptag + "          java -jar splitpdb.jar 1blr.pdb --model 8 -o 1blr_M8.pdb");
        System.out.println(apptag + "          java -jar splitpdb.jar pdb1blr.ent.gz -x -z -o 1blr_M1.pdb");        
        System.out.println(apptag + "");
        System.out.println(apptag + "REQUIRED INPUT FILES: This program obviously requires the PDB file <infile>.");
        System.out.println(apptag + "");
        System.out.println(apptag + "NOTE: -If the file does not contain any models, the output file is a copy of the input file.");
        System.out.println(apptag + "      -It is considered an error if a model that does not exist is specified explicitely (-m).");
        System.out.println(apptag + "");
        System.out.println(apptag + "RETURN VALUES: 0 on success, 1 on error, 2 on success if the input file did not contain any models.");

    }

    /**
     * Prints short info on how to get help on command line options and exits the program.
     */
    public static void syntaxError() {
        System.err.println(apptag + "ERROR: Invalid command line. Use '-h' or --help' for info on how to run this program.");
        System.exit(1);
    }
    
    

    /**
     * Parses the 'REMARK 2 RESOLUTION' record of a PDB file and returns the resolution in Angstroem, or -1.0 if it is 'NOT APPLICABLE' or could not be determined.
     * Lines may look like this 'REMARK   2 RESOLUTION.    2.24 ANGSTROMS.' or this 'REMARK   2 RESOLUTION. NOT APPLICABLE.'
     * @param l the line to parse
     * @return the resolution
     */
    public static Double getResFromREMARK2Line(String l) {
        Double res = -1.0;

        String prefix = "REMARK   2 RESOLUTION.";

        // Should have been checked already, but you never know.
        if(l.indexOf(prefix) < 0) {
            return(res);
        }

        try {
            Integer startIndex = l.indexOf(prefix) + prefix.length();       // the column where the data starts (cut off the prefix)
            String data = l.substring(startIndex, l.length());              // the data part            

            Integer suffixStart = data.indexOf("ANGSTROMS");
            if(suffixStart >= 0) {
                // There should be a resolution in this data part, it looks similar to this: '    2.24 ANGSTROMS.'
                res = Double.valueOf(data.substring(0, suffixStart).trim());
            }
            else {
                // The data part may look similar to this: ' NOT APPLICABLE.' - or be total rubbish, we don't care.
                //System.out.println(apptag + "Data part contains no 'ANGSTROMS', assuming 'NOT APPLICABLE'.");
                res = -1.0;
            }

        } catch(Exception e) {
            System.err.println(apptag + "WARNING: Could not parse resolution from 'REMARK 2 RESOLUTION' record, assuming 'NOT APPLICABLE'.");
            res = -1.0;
        }


        return(res);
    }


}
