/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package vpg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A static class that can be used to start external programs.
 * @author spirit
 */
public class VpgJobs {
    
       
    /**
     * Runs PLCC with the given settings.
     * @param pdbid the PDB identifier for the input file
     * @param pdbFile the input PDB file
     * @param dsspFile the input DSSP file, has to be split already (i.e., must not contain more than one model)
     * @param outputDir the output directory
     * @param nonEssentialPlccOptions the input option string, e.g. "--file input.txt --not-today --min-max 1 2 --faster"
     * @return a ProcessResult which holds various info. It is null if an exception occurred while trying to run the command.
     */
    public static synchronized ProcessResult runPlcc(String pdbid, File pdbFile, File dsspFile, File workingDir, File outputDir, String nonEssentialPlccOptions) {
        String stdout, stderr;
        String fs = System.getProperty("file.separator");
        Integer retVal;
        ArrayList<String> cmdList = new ArrayList<String>();
        
        // check for libs
        File libDir = new File(workingDir.getAbsolutePath() + fs + "lib");
        if( ! (libDir.isDirectory() && libDir.canRead())) {
            System.err.println(Settings.getApptag() + "WARNING: No sub directory 'lib' found in the PLCC working directory.");
        }
        
        // build essential command
        cmdList.add(Settings.get("vpg_S_java_command"));
        cmdList.add("-jar");
        cmdList.add(Settings.get("vpg_S_path_plcc"));
        cmdList.add(pdbid);
        cmdList.add(VpgJobs.fileSeemsGzipped(pdbFile.getName()) ? "--gz-pdbfile" : "--pdbfile");
        cmdList.add(pdbFile.getAbsolutePath());
        cmdList.add(VpgJobs.fileSeemsGzipped(dsspFile.getName()) ? "--gz-dsspfile" : "--dsspfile");
        cmdList.add(dsspFile.getAbsolutePath());
        cmdList.add("--outputdir");
        cmdList.add(outputDir.getAbsolutePath());
        
        // add non-essential options
        String[] neOptions = VpgJobs.getCommandArrayFromString(nonEssentialPlccOptions);
        for(String opt : neOptions) {
            cmdList.add(opt);
        }
        
        String [] cmd = cmdList.toArray(new String[cmdList.size()]); 
        
        try {
            String[] inputAndError = IO.execCmd(cmd, workingDir);
            stdout = inputAndError[0];
            stderr = inputAndError[1];
            retVal = Integer.valueOf(inputAndError[2]);    
            
        } catch(Exception e) {
            System.err.println(Settings.getApptag() + "ERROR running plcc: '" + e.getMessage() + "'.");
            return null;
        }
        

        return new ProcessResult(cmd, workingDir, retVal, stdout, stderr);
    }
    
    /**
     * Tries to guess whether a file is gzipped from its name.
     * @param fname the file name
     * @return true if it ends with ".gz", false otherwise
     */
    public static Boolean fileSeemsGzipped(String fname) {
        return fname.toLowerCase().endsWith(".gz");
    }
    
    
    /**
     * Runs splitPdb with the given settings.
     * @param pdbFile the input PDB file
     * @param requestedOutputFile the path where to write the output file, including file name
     * @param workingDir the working directory
     * @param nonEssentialSplitPdbOptions the input option string, e.g. "--file input.txt --not-today --min-max 1 2 --faster"
     * @return a ProcessResult which holds various info. It is null if an exception occurred while trying to run the command.
     */
    public static synchronized ProcessResult runSplitPdb(File pdbFile, File requestedOutputFile, File workingDir, String nonEssentialSplitPdbOptions) {
        
        String stdout, stderr;
        Integer retVal;
        ArrayList<String> cmdList = new ArrayList<String>();
        
        cmdList.add(Settings.get("vpg_S_java_command"));
        cmdList.add("-jar");
        cmdList.add(Settings.get("vpg_S_path_splitpdb"));
        
        cmdList.add(pdbFile.getAbsolutePath());
        
        if(VpgJobs.fileSeemsGzipped(pdbFile.getName().toLowerCase())) {
            cmdList.add("--zipped-input");
        }
                
        cmdList.add("--allow-overwrite");                        
        
        cmdList.add("--outfile");
        cmdList.add(requestedOutputFile.getAbsolutePath());     
        
        // add non-essential options
        String[] neOptions = VpgJobs.getCommandArrayFromString(nonEssentialSplitPdbOptions);
        for(String opt : neOptions) {
            cmdList.add(opt);
        }
        
        String [] cmd = cmdList.toArray(new String[cmdList.size()]);                       
        
        try {
            String[] inputAndError = IO.execCmd(cmd, workingDir);
            stdout = inputAndError[0];
            stderr = inputAndError[1];
            retVal = Integer.valueOf(inputAndError[2]);    
            
        } catch(Exception e) {
            System.err.println(Settings.getApptag() + "ERROR running SplitPDB: '" + e.getMessage() + "'.");
            return null;
        }
        

        return new ProcessResult(cmd, workingDir, retVal, stdout, stderr);
        
        
    }
    
    
    /**
     * Runs DSSP (dsspcmbi) with the given settings.
     * @param pdbFile the input PDB file
     * @param requestedOutputFile the path where to write the output file, including file name
     * @param workingDir the working directory 
     * @param nonEssentialSplitPdbOptions the input option string, e.g. "--file input.txt --not-today --min-max 1 2 --faster"
     * @return a ProcessResult which holds various info. It is null if an exception occurred while trying to run the command.
     */
    public static synchronized ProcessResult runDssp(File pdbFile, File requestedOutputFile, File workingDir, String nonEssentialDsspOptions) {
        
        String stdout, stderr;
        Integer retVal;
        ArrayList<String> cmdList = new ArrayList<String>();
        
        
        cmdList.add(Settings.get("vpg_S_path_dssp")); 
        
        cmdList.add("-i");
        cmdList.add(pdbFile.getAbsolutePath());
        
        cmdList.add("-o");
        cmdList.add(requestedOutputFile.getAbsolutePath());
        
        
        // add non-essential options
        String[] neOptions = VpgJobs.getCommandArrayFromString(nonEssentialDsspOptions);
        for(String opt : neOptions) {
            cmdList.add(opt);
        }
        
        String [] cmd = cmdList.toArray(new String[cmdList.size()]);                       
        
        try {
            String[] inputAndError = IO.execCmd(cmd, workingDir);
            stdout = inputAndError[0];
            stderr = inputAndError[1];
            retVal = Integer.valueOf(inputAndError[2]);    
            
        } catch(Exception e) {
            System.err.println(Settings.getApptag() + "ERROR running DSSP: '" + e.getMessage() + "'.");
            return null;
        }
        

        return new ProcessResult(cmd, workingDir, retVal, stdout, stderr);        
    }
    
    
    /**
     * Returns a command array from a string. Assumes that the options contain no spaces. Actually, all it does atm
     * is to split the string at single spaces.
     * @param options the input option string, e.g. "--file input.txt --not-today --min-max 1 2 --faster"
     * @return an array of the options (the input split at " ")
     */
    public static String[] getCommandArrayFromString(String options) {
        return options.split(" ");
    }
    
    
    /**
     * Returns the first line from the file which does not start with the comment character '#'.
     * @param file the plccopt file to parse
     * @return the first line from the file which does not start with the comment character '#'. It is the empty string if no such line was found, the line was empty or the file is null.
     */
    public static String getOptionsFromOptFile(File file) {
        
        if(file == null) {
            return "";
        }
        
        if(file.isFile() && file.canRead()) {
            try {
                ArrayList<String> lines = IO.multiLineStringToStringList(IO.slurpFileSingString(file.getAbsolutePath()));
                
                for(String line : lines) {
                    if( ! line.startsWith("#")) {
                        return line;
                    }
                }
                
            } catch (Exception e) {
                System.err.println(Settings.getApptag() + "WARNING: Plcc options file '" + file.getAbsolutePath() + "' has invalid format, ignored.");
                return "";
            }
        }
        
        System.err.println(Settings.getApptag() + "WARNING: Could not read Plcc options file '" + file.getAbsolutePath() + "', ignored.");
        return "";
    }
    
}

/**
 * A ProcessResult, it holds information on the run of an external command, e.g., return value, output, etc.
 * @author spirit
 */
class ProcessResult {
    
    private String[] commandArray;
    private File workingDirectory;
    private Integer returnValue;
    private String stdOutString;
    private String stdErrString;
    private HashMap<String, Object> extraInfo;
    
    
    /**
     * Creates a new ProcessResult object, which holds information on the run of an external command.
     * @param cmdArray the command array
     * @param workDir the working directory
     * @param retVal the return value of the process
     * @param stdout the output the process wrote to STDOUT
     * @param stderr the output the process wrote to STDERR
     */
    public ProcessResult(String[] cmdArray, File workDir, Integer retVal, String stdout, String stderr) {
        this.commandArray = cmdArray;
        this.workingDirectory = workDir;
        this.returnValue = retVal;
        this.stdOutString = stdout;
        this.stdErrString = stderr;
        extraInfo = new HashMap<String, Object>();
    }

    /**
     * @return the commandArray
     */
    public String[] getCommandArray() {
        return commandArray;
    }

    /**
     * @return the workingDirectory
     */
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * @return the returnValue
     */
    public Integer getReturnValue() {
        return returnValue;
    }

    /**
     * @return the stdOutString
     */
    public String getStdOutString() {
        return stdOutString;
    }

    /**
     * @return the stdErrString
     */
    public String getStdErrString() {
        return stdErrString;
    }

    /**
     * @return the extraInfo
     */
    public HashMap<String, Object> getExtraInfo() {
        return extraInfo;
    }

    /**
     * @param extraInfo the extraInfo to set
     */
    public void setExtraInfo(HashMap<String, Object> extraInfo) {
        this.extraInfo = extraInfo;
    }
    
    
    
    
}
