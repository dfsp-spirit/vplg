/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package vpg;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author ts
 */
public class VpgBatchDsspFrame extends javax.swing.JFrame implements ItemListener, DocumentListener, PropertyChangeListener {

    private Boolean downloadWarningAlreadyShown;
    
    HashMap<String, File> pdbFilesReadyForDssp;
    HashMap<String, File> pdbFilesRequiringSplit;
    HashMap<String, File> allPdbFiles;
    
    private ProgressMonitor pm;
    private BatchCreateDsspFilesTask task;
    private int maxProgress = 100;
    private String taskOutput = "";
    private String neOptionsSplitPdb = "";
    private String neOptionsDsspcmbi = "";
    private ArrayList<String> successList;
    private ArrayList<String> failList;
    private ArrayList<String> failReasons;
    private File batchLog;
    private String logText;
    
    /**
     * Creates new form VpgBatchDsspFrame
     */
    public VpgBatchDsspFrame() {
        initComponents();
        String fs = System.getProperty("file.separator");
        
        this.logText = "";
        
        this.downloadWarningAlreadyShown = false;
        if( ! Settings.getBoolean("vpg_B_dssp_download_warning")) {
            this.downloadWarningAlreadyShown = true;
        }
        
        this.jTextFieldDsspOutputDir.setText(System.getProperty("user.home") + fs + "data" + fs  + "DSSP");
        this.jTextFieldPdbFileDirectory.setText(System.getProperty("user.home") + fs + "data" + fs  + "PDB");
        this.jTextFieldPdbSplitOutputDir.setText(System.getProperty("user.home") + fs + "data" + fs  + "PDB_split");
        
        this.jTextFieldDsspOutputDir.getDocument().addDocumentListener(this);
        this.jTextFieldPdbFileDirectory.getDocument().addDocumentListener(this);
        this.jTextFieldPdbSplitOutputDir.getDocument().addDocumentListener(this);
        
        this.jCheckBoxDownloadFailedDsspFiles.addItemListener(this);
        this.jCheckBoxRunSplitPdb.addItemListener(this);
        this.jCheckBoxSkipSplitPdbIfSplitAvailable.addItemListener(this);
        this.jCheckBoxSplitPdbGzippedOutput.addItemListener(this);
        
        this.jComboBoxPutDsspFiles.addItemListener(this);
        this.jComboBoxPutPdbFiles.addItemListener(this);
        
        this.checkInput();
        
    }
    
    
    
    /**
     * A worker thread that calls the external SplitPDB and dsspcmbi programs for many proteins. It reads the input PDB and DSSP files
     * from class variables. 
     * 
     * @author ts
     */
    class BatchCreateDsspFilesTask extends SwingWorker<Void, Void> {
        
        Component parent = null;
        Boolean createSubdirsSplitPdb = false;
        Boolean createSubdirsDssp = false;        
        Boolean downloadDsspIfFailed = false;  
        Integer numDownloaded;
        
        public void setParent(Component c) {
            this.parent = c;
        }
        
        /**
         * If this is set to true, the SplitPDB output files will be stored in PDB-style subdirectories of
         * the output directory (instead of directly in the output dir).
         * @param b whether to store in sub directory tree
         */
        public void setCreateSubdirsForSplitPdbOutputFiles(Boolean b) {
            this.createSubdirsSplitPdb = b;
        }
        
        /**
         * If this is set to true, the process will try to download the DSSP file from the internet in case
         * creating it locally failed.
         * @param b whether to try to download the DSSP file if creating it failed
         */
        public void setDownloadDsspIfFailed(Boolean b) {
            this.downloadDsspIfFailed = b;
        }
        
        
        /**
         * If this is set to true, the DSSP output files will be stored in PDB-style subdirectories of
         * the output directory (instead of directly in the output dir).
         * @param b whether to store in sub directory tree
         */
        public void setCreateSubdirsForDsspOutputFiles(Boolean b) {
            this.createSubdirsDssp = b;
        }
               
        
        @Override
        public Void doInBackground() {
            String fs = System.getProperty("file.separator");
            String pdbid, pdbTag;
            ProcessResult prSplitPdb, prDssp;
            Boolean allOk, splitPdbFailed, dsspFailed, downloadFailed;
            Integer numOk = 0;
            this.numDownloaded = 0;
            String text;
            int progress = 0;
            File outputFileSplitPDB, outputFileDssp, outputDirSplitPDB, outputDirDssp;
            setProgress(progress);
            maxProgress = allPdbFiles.size();
            for (Iterator<String> it = allPdbFiles.keySet().iterator(); it.hasNext() && ! this.isCancelled() ; ) {
                
                // ----------------------------- init vars, get data and check for cancel ---------------------------------
                pdbid  = it.next();
                pdbTag = "[" + pdbid + "] ";
                allOk = true; 
                splitPdbFailed = false; 
                dsspFailed = false;
                downloadFailed = false;
                                            
                if(pm.isCanceled()) {
                    text = "NOTE: Batch creating DSSP files canceled in progress monitor.";
                    System.out.println(Settings.getApptag() + text);
                    logText += (pdbTag + text + "\n");
                    this.cancel(true);
                    return null;
                }

                pm.setProgress(Math.min(progress, maxProgress));
                text = "SplitPDB: " + pdbid.toUpperCase() + " (" + progress + "/" + maxProgress + ").";
                pm.setNote(text);
                logText += (pdbTag + text + "\n");

                System.out.println(Settings.getApptag() + "#" + progress + ", " + pdbid.toUpperCase() + ": '" + allPdbFiles.get(pdbid) + "'.");
                
                // ---------------------- determine output file names --------------------------------
                String fName; File outDir;
                outputDirSplitPDB = new File(getSplitPDBOutputDir());                
                fName = pdbid + ".split.pdb";
                if(createSubdirsSplitPdb) {               
                    outputFileSplitPDB = determinePdbStylePathForFile(pdbid, fName, outputDirSplitPDB);
                    outDir = outputFileSplitPDB.getParentFile();
                    if( ! outDir.isDirectory()) {
                        if(! outDir.mkdirs()) {
                            text = "ERROR: Could not create directory '" + outputFileSplitPDB.getParentFile().getAbsolutePath() + "' and thus not write split PDB file for PDB ID " + pdbid + ", skipping.";
                            System.err.println(Settings.getApptag() + text);
                            logText += (pdbTag + text + "\n");
                            allOk = false;
                        } else {
                            logText += (pdbTag + "Created SplitPDB output subdirectory '"  + outDir.getAbsolutePath() + "'." + "\n");
                        }
                    } 
                } else {
                    outputFileSplitPDB = new File(fName);                    
                }
                
                
                
                outputDirDssp = new File(getDsspcmbiOutputDir());                
                fName = pdbid + ".dssp";
                if(createSubdirsDssp) {               
                    outputFileDssp = determinePdbStylePathForFile(pdbid, fName, outputDirDssp);
                    outDir = outputFileDssp.getParentFile();
                    if( ! outDir.isDirectory()) {
                        if( ! outDir.mkdirs()) {
                            text = "ERROR: Could not create directory '" + outputFileDssp.getParentFile().getAbsolutePath() + "' and thus not write DSSP file for PDB ID " + pdbid + ", skipping.";
                            System.err.println(Settings.getApptag() + text);
                            logText += (pdbTag + text + "\n");
                            allOk = false;
                        } else {
                            logText += (pdbTag + "Created DSSP output subdirectory '"  + outDir.getAbsolutePath() + "'." + "\n");
                        }
                    } 
                } else {
                    outputFileDssp = new File(fName);                    
                }
                
                System.out.println(Settings.getApptag() + "The SplitPDB output file for " + pdbid + " will be written to '" + outputFileSplitPDB.getAbsolutePath() + "'.");
                System.out.println(Settings.getApptag() + "The DSSP output file for " + pdbid + " will be written to '" + outputFileDssp.getAbsolutePath() + "'.");
                
                // ----------------------------- run SplitPDB ---------------------------------
                
                
                prSplitPdb = null;
                if(allOk) {
                    prSplitPdb = VpgJobs.runSplitPdb(allPdbFiles.get(pdbid), outputFileSplitPDB, new File(System.getProperty("user.home")), neOptionsSplitPdb);
                    if(prSplitPdb == null) {
                        allOk = false;
                        text = "ERROR: SplitPDB failed for protein " + pdbid + " and result was NULL (No output file at '" + outputFileSplitPDB.getAbsolutePath() + "').";
                        System.err.println(Settings.getApptag() + text);
                        logText += pdbTag + text + "\n";
                    } else {
                        allOk = (prSplitPdb.getReturnValue() == 0 || prSplitPdb.getReturnValue() == 2);
                        text = "SplitPDB finished, return value was " + prSplitPdb.getReturnValue() + ".";
                        System.out.println(Settings.getApptag() + text);
                        logText += pdbTag + text + "\n";
                        if(allOk) {
                            text = "SplitPDB succeeded for protein " + pdbid + ", output file is at '" + outputFileSplitPDB.getAbsolutePath() + "'.";
                            System.out.println(Settings.getApptag() + text);
                            logText += pdbTag + text + "\n";
                        }
                    }
                }
                      
                if( ! allOk) {
                    splitPdbFailed = true;
                    if(prSplitPdb != null) {
                        text = "ERROR: SplitPDB failed for protein " + pdbid + ": retVal=" + prSplitPdb.getReturnValue() + " (No output file at '" + outputFileSplitPDB.getAbsolutePath() + "').";
                        logText += pdbTag + text + "\n";
                        System.err.println(Settings.getApptag() + text);
                    }
                }
                // ----------------------------- run DSSP ---------------------------------
                
                                                
                if(allOk) { 
                            
                    text = "DSSP: " + pdbid.toUpperCase() + " (" + progress + "/" + maxProgress + ").";
                    System.out.println(Settings.getApptag() + text);
                    pm.setNote(text);
                    logText += pdbTag + (text + "\n");
                    
                    prDssp = VpgJobs.runDssp(allPdbFiles.get(pdbid), outputFileDssp, new File(System.getProperty("user.home")), neOptionsDsspcmbi);
                    if(prDssp == null) {
                        allOk = false;
                        text = "ERROR: DSSP failed for protein " + pdbid + " and result was NULL (No output file at '" + outputFileDssp.getAbsolutePath() + "').";
                        logText += pdbTag + text + "\n";
                        System.err.println(Settings.getApptag() + text);
                    } else {
                        allOk = (prDssp.getReturnValue() == 0);
                    }
                    
                    // DSSP has been run now
                    if(allOk) {
                        numOk++; 
                        successList.add(pdbid); 
                        text = "DSSP succeeded for protein " + pdbid + ", output file is at '" + outputFileDssp.getAbsolutePath() + "').";
                        logText += pdbTag + text + "\n";
                        System.err.println(Settings.getApptag() + text);
                        dsspFailed = false;
                    } else {
                        dsspFailed = true;
                        if(prDssp != null) {
                            text = "ERROR: DSSP failed for protein " + pdbid + ": retVal=" + prDssp.getReturnValue() + " (No output file at '" + outputFileDssp.getAbsolutePath() + "').";
                            logText += pdbTag + text + "\n";
                            System.err.println(Settings.getApptag() + text);
                        }
                    }
                    
                    
                } else {          
                    // If SplitPdb failed, running DSSP makes no sense and we assume it also failed.
                    dsspFailed = true;
                    text = "NOTE: Not running DSSP for protein " + pdbid + " because its SplitPDB run failed.";
                    System.err.println(Settings.getApptag() + text);
                    logText += pdbTag + text + "\n";
                }
                
                // ------------------ download DSSP file if something went wrong and requested --------------
                if(splitPdbFailed || dsspFailed) {
                    if(this.downloadDsspIfFailed) {                        
                        
                        URL downloadUrlPdb = IO.getDownloadUrlPDB(pdbid);
                        ArrayList<String> dlErrors = IO.wget(downloadUrlPdb, outputFileDssp.getAbsolutePath());
                        
                        if(dlErrors.isEmpty()) {
                            text = "Downloaded DSSP file for protein " + pdbid + ".";
                            System.out.println(Settings.getApptag() + text);
                            logText += pdbTag + text + "\n";
                            this.numDownloaded++;
                            numOk++; 
                            successList.add(pdbid);
                        } else {
                            // SplitPDB and/or DSSP failed and downloading failed, so this one is lost
                            text = "ERROR: Failed to downloaded DSSP file for protein " + pdbid + " to '" + outputFileDssp.getAbsolutePath() + "'.";
                            System.err.println(Settings.getApptag() + text);
                            logText += pdbTag + text + "\n";
                            downloadFailed = true;
                            failList.add(pdbid);
                            
                            text = "FAILED: All methods (including download) failed for protein " + pdbid + ", could not create DSSP file.";
                            logText += pdbTag + text + "\n";
                            System.err.println(Settings.getApptag() + text);
                        }                                      
                    } else {
                        // SplitPDB and/or DSSP failed and downloading is disabled, so this one is lost
                        failList.add(pdbid);    
                        text = "FAILED: Generating the DSSP file locally failed for protein " + pdbid + " (and download disabled), could not create DSSP file.";
                        logText += pdbTag + text + "\n";
                        System.err.println(Settings.getApptag() + text);
                    }
                    
                }

                progress++;                                                    
            }
                        
            return null;
        }
 
        @Override
        public void done() {
            
            pm.close();
            parent.setCursor(null);

            Integer numNotProcessed = allPdbFiles.size() - successList.size() - failList.size();

            System.out.println(Settings.getApptag() + "Results: " + successList.size() + " succeeded, " + failList.size() + " failed, " + numNotProcessed + " not processed.");

            String shortLogText = "Results: " + successList.size() + " succeeded (" + this.numDownloaded + " downloaded), " + failList.size() + " failed, " + numNotProcessed + " not processed.\n";
            logText += shortLogText;
            String failOutput = "Failed PDB IDs: ";
            if(failList.size() > 0) {
                for(String f : failList) {
                    failOutput += (f + " ");
                }
                
                System.err.println(Settings.getApptag() + failOutput);                
            }

            failOutput += "\n";
            logText += failOutput;

            String successOutput = "Succeeded PDB IDs: ";
            if(successList.size() > 0) {                
                for(String f : successList) {
                    successOutput += (f + " ");
                }                
                System.out.println(Settings.getApptag() + successOutput);                
            }

            successOutput += "\n";
            logText += successOutput;

            String logInfoText = "Log file written to '" + batchLog.getAbsolutePath() + "'.";
            if(IO.stringToTextFile(batchLog.getAbsolutePath(), logText)) {
                System.out.println(Settings.getApptag() + logInfoText);
            }
            
            shortLogText += logInfoText;

            JOptionPane.showMessageDialog(parent, shortLogText, "VPG -- Batch processing results", JOptionPane.INFORMATION_MESSAGE);
            
            jButtonRunBatchDssp.setEnabled(false);   // prevents user from starting over accidently or thinking it didnt run, he/she can click 'Check Settings', then start again.
            jButtonCheckSettings.setEnabled(true);
            jLabelStatus.setText("VPG Batch DSSP creator ready.");
            pm.setProgress(0);
            pm.close();        
        }
    }
    
    
    /**
     * Determines the full pdb-style output path (including the filename) for a given input file name and the basedir. This
     * is done by adding a subdir that is named after the 2nd and 3rd letter of the PDB ID of the input file, e.g., for a file
     * named "8icd.dssp" and a basedir of "/data/DSSP/", it will return "/data/DSSP/ic/8icd.dssp". Note that this function does
     * NOT create the subdirectories.
     * @param pdbid the PDB identifier, e.g., "8icd". The subdir name is determined from the 2 middle letters.
     * @param outputFilename The filename (without directory) of the output file 
     * @param baseOutputDir the base output dir
     * @return the full pdb-style output path (including the filename) for the given input file name, PDB ID and basedir
     */
    public File determinePdbStylePathForFile(String pdbid, String outputFilename, File baseOutputDir) {
        String fs = System.getProperty("file.separator");
        if(pdbid.length() != 4) {
            System.err.println(Settings.getApptag() + "ERROR: PDB ID '" + pdbid + "' in DSSP batch genertor has wrong length, skipping.");
            return null;
        }
        
        String mid2PdbLetters = pdbid.substring(1, 3);
        
        return new File(baseOutputDir.getAbsolutePath() + fs + mid2PdbLetters + fs + outputFilename);
    }
    
    
    
    /**
     * This is invoked when task's progress property changes to update the ProgressMonitor accordingly.
     */
    @Override public void propertyChange(PropertyChangeEvent evt) {
        
        if ("state".equals(evt.getPropertyName()) ) {
            if (pm.isCanceled() || task.isDone()) {
                if (pm.isCanceled()) {
                    this.jLabelStatus.setText("Batch processing is being cancelled, please wait...");
                    task.cancel(true);

                    taskOutput += "Batch processing task canceled in progress monitor.\n";
                } else {
                    taskOutput += "Batch processing task completed.\n";
                }
                this.jButtonCheckSettings.setEnabled(true);
                this.jLabelStatus.setText("VPG Batch processor ready.");
                System.out.print(Settings.getApptag() + taskOutput);
            }
        }
        
        if ("progress".equals(evt.getPropertyName()) ) {
            int progress = (Integer) evt.getNewValue();
            pm.setProgress(progress);
            String message = "Batch processing task done with " + progress + " / " + maxProgress + " proteins. ";
            System.out.println(Settings.getApptag() + message);
            pm.setNote(message);
            taskOutput += message;
            
            
        }                              
 
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelStatus = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();
        jPanelContent = new javax.swing.JPanel();
        jLabelInputFiles = new javax.swing.JLabel();
        jLabelPdbDir = new javax.swing.JLabel();
        jTextFieldPdbFileDirectory = new javax.swing.JTextField();
        jButtonSelectPdbDir = new javax.swing.JButton();
        jSeparatorTop = new javax.swing.JSeparator();
        jLabeloptions = new javax.swing.JLabel();
        jCheckBoxRunSplitPdb = new javax.swing.JCheckBox();
        jCheckBoxSkipSplitPdbIfSplitAvailable = new javax.swing.JCheckBox();
        jCheckBoxSplitPdbGzippedOutput = new javax.swing.JCheckBox();
        jLabelPutPdbFiles = new javax.swing.JLabel();
        jComboBoxPutPdbFiles = new javax.swing.JComboBox();
        jTextFieldPdbSplitOutputDir = new javax.swing.JTextField();
        jButtonSelectPdbSplitOutputDir = new javax.swing.JButton();
        jCheckBoxDownloadFailedDsspFiles = new javax.swing.JCheckBox();
        jLabelPutDsspFiles = new javax.swing.JLabel();
        jComboBoxPutDsspFiles = new javax.swing.JComboBox();
        jTextFieldDsspOutputDir = new javax.swing.JTextField();
        jButtonSelectDsspOutputDir = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButtonRunBatchDssp = new javax.swing.JButton();
        jButtonCheckSettings = new javax.swing.JButton();

        setTitle("VPG -- Batch DSSP file processing");

        jLabelStatus.setText("VPG batch DSSP file creator ready.");

        javax.swing.GroupLayout jPanelStatusLayout = new javax.swing.GroupLayout(jPanelStatus);
        jPanelStatus.setLayout(jPanelStatusLayout);
        jPanelStatusLayout.setHorizontalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelStatusLayout.setVerticalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
        );

        jLabelInputFiles.setText("Input files");

        jLabelPdbDir.setText("PDB file directory:");

        jTextFieldPdbFileDirectory.setText("/home/ts/data/PDB");
        jTextFieldPdbFileDirectory.setToolTipText("A directory containing PDB files. They may be in subdirectories.");

        jButtonSelectPdbDir.setText("Select...");
        jButtonSelectPdbDir.setToolTipText("Select PDB directory.");
        jButtonSelectPdbDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectPdbDirActionPerformed(evt);
            }
        });

        jLabeloptions.setText("Options");

        jCheckBoxRunSplitPdb.setSelected(true);
        jCheckBoxRunSplitPdb.setText("Run SplitPDB to extract one model from the PDB file for DSSP (required unless already done!)");
        jCheckBoxRunSplitPdb.setToolTipText("Runs SplitPDB. Required unless it has already been done! Run it if in doubt. Not running it saves time but leads to errors if it has not been run before!");

        jCheckBoxSkipSplitPdbIfSplitAvailable.setSelected(true);
        jCheckBoxSkipSplitPdbIfSplitAvailable.setText("...but skip running if a PDB file ending with \".split\" or \".split.gz\" is available for that protein ");
        jCheckBoxSkipSplitPdbIfSplitAvailable.setToolTipText("Uses existing SplitPDB output files if they exist instead of running SplitPDB again.");

        jCheckBoxSplitPdbGzippedOutput.setText("Write gzipped PDB output files (NOT recommended because DSSP currently cannot process them)");
        jCheckBoxSplitPdbGzippedOutput.setToolTipText("Whether to zip the split output PDB file.");

        jLabelPutPdbFiles.setText("Put the resulting split PDB files:");

        jComboBoxPutPdbFiles.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Into the directory where the original PDB file also is", "All directly into the directory given below", "In PDB-like subdirectories under the directory given below" }));
        jComboBoxPutPdbFiles.setToolTipText("Where to put the generated and split PDB files.");

        jTextFieldPdbSplitOutputDir.setText("/home/ts/data/PDB_split");
        jTextFieldPdbSplitOutputDir.setToolTipText("The split PDB file output directory.");
        jTextFieldPdbSplitOutputDir.setEnabled(false);

        jButtonSelectPdbSplitOutputDir.setText("Select...");
        jButtonSelectPdbSplitOutputDir.setToolTipText("Select split PDB output directory.");
        jButtonSelectPdbSplitOutputDir.setEnabled(false);
        jButtonSelectPdbSplitOutputDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectPdbSplitOutputDirActionPerformed(evt);
            }
        });

        jCheckBoxDownloadFailedDsspFiles.setText("Try to download a DSSP file from the internet if something went wrong while generating it locally");
        jCheckBoxDownloadFailedDsspFiles.setToolTipText("Dowloads failed DSSP files from the internet. Use with care, may lead to mass download!");

        jLabelPutDsspFiles.setText("Put the resulting DSSP files:");

        jComboBoxPutDsspFiles.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Into the directory where the original PDB file also is", "All directly into the directory given below", "In PDB-like subdirectories under the directory given below" }));
        jComboBoxPutDsspFiles.setToolTipText("Where to put the generated DSSP files.");

        jTextFieldDsspOutputDir.setText("/home/ts/data/DSSP");
        jTextFieldDsspOutputDir.setToolTipText("DSSP output directory.");
        jTextFieldDsspOutputDir.setEnabled(false);

        jButtonSelectDsspOutputDir.setText("Select...");
        jButtonSelectDsspOutputDir.setToolTipText("Select DSSP output directory.");
        jButtonSelectDsspOutputDir.setEnabled(false);

        jButtonRunBatchDssp.setText("Start batch processing");
        jButtonRunBatchDssp.setToolTipText("Starts batch processing. May take a while for many input files.");
        jButtonRunBatchDssp.setEnabled(false);
        jButtonRunBatchDssp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunBatchDsspActionPerformed(evt);
            }
        });

        jButtonCheckSettings.setText("Check settings");
        jButtonCheckSettings.setToolTipText("Validate input.");
        jButtonCheckSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCheckSettingsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelContentLayout = new javax.swing.GroupLayout(jPanelContent);
        jPanelContent.setLayout(jPanelContentLayout);
        jPanelContentLayout.setHorizontalGroup(
            jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanelContentLayout.createSequentialGroup()
                        .addComponent(jLabelPdbDir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldPdbFileDirectory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonSelectPdbDir))
                    .addComponent(jSeparatorTop)
                    .addGroup(jPanelContentLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxSplitPdbGzippedOutput)
                            .addComponent(jCheckBoxSkipSplitPdbIfSplitAvailable)
                            .addGroup(jPanelContentLayout.createSequentialGroup()
                                .addComponent(jLabelPutPdbFiles)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxPutPdbFiles, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanelContentLayout.createSequentialGroup()
                                        .addComponent(jTextFieldPdbSplitOutputDir)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButtonSelectPdbSplitOutputDir))))))
                    .addGroup(jPanelContentLayout.createSequentialGroup()
                        .addComponent(jLabelPutDsspFiles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxPutDsspFiles, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelContentLayout.createSequentialGroup()
                                .addComponent(jTextFieldDsspOutputDir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonSelectDsspOutputDir))))
                    .addGroup(jPanelContentLayout.createSequentialGroup()
                        .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxDownloadFailedDsspFiles)
                            .addComponent(jCheckBoxRunSplitPdb)
                            .addComponent(jLabelInputFiles)
                            .addComponent(jLabeloptions))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelContentLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonCheckSettings)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRunBatchDssp)))
                .addContainerGap())
        );
        jPanelContentLayout.setVerticalGroup(
            jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelInputFiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPdbDir)
                    .addComponent(jTextFieldPdbFileDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSelectPdbDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparatorTop, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelContentLayout.createSequentialGroup()
                        .addComponent(jLabeloptions)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxRunSplitPdb)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBoxSkipSplitPdbIfSplitAvailable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxSplitPdbGzippedOutput)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelPutPdbFiles))
                    .addComponent(jComboBoxPutPdbFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPdbSplitOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSelectPdbSplitOutputDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxDownloadFailedDsspFiles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPutDsspFiles)
                    .addComponent(jComboBoxPutDsspFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldDsspOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSelectDsspOutputDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRunBatchDssp)
                    .addComponent(jButtonCheckSettings))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSelectPdbDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectPdbDirActionPerformed
        
        File defaultDir = new File(Settings.get("vpg_S_output_dir"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldPdbFileDirectory.setText(fc.getSelectedFile().toString());
        }
    }//GEN-LAST:event_jButtonSelectPdbDirActionPerformed

    private void jButtonSelectPdbSplitOutputDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectPdbSplitOutputDirActionPerformed
        
        File defaultDir = new File(Settings.get("vpg_S_output_dir"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldPdbSplitOutputDir.setText(fc.getSelectedFile().toString());
        }
    }//GEN-LAST:event_jButtonSelectPdbSplitOutputDirActionPerformed

    private void jButtonCheckSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCheckSettingsActionPerformed
        
        this.checkInput();
    }//GEN-LAST:event_jButtonCheckSettingsActionPerformed

    
    /**
     * Returns the current SplitPDB output dir setting from the form.
     * If the respective input text field is currently disabled, it returns the input dir path.
     * @return the output directory as a string
     */ 
    public String getSplitPDBOutputDir() {
        if(this.jTextFieldPdbSplitOutputDir.isEnabled()) {
            return this.jTextFieldPdbSplitOutputDir.getText();
        } else {
            return this.jTextFieldPdbFileDirectory.getText();
        }
    }
    
    /**
     * Returns the current dsspcmbi output dir setting from the form.
     * If the respective input text field is currently disabled, it returns the input dir path.
     * @return the output directory as a string
     */ 
    public String getDsspcmbiOutputDir() {
        if(this.jTextFieldDsspOutputDir.isEnabled()) {
            return this.jTextFieldDsspOutputDir.getText();
        } else {
            return this.jTextFieldPdbFileDirectory.getText();
        }
    }
    
    private void jButtonRunBatchDsspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunBatchDsspActionPerformed
        
        logText = "";
        //System.out.println("Running batch DSSP processing... NOT SUPPORTED YET.");
        //JOptionPane.showMessageDialog(this, "DSSP batch processing not supported yet.", "VPG -- DSSP batch creator", JOptionPane.WARNING_MESSAGE);
        
        
        Integer numProteins = this.allPdbFiles.size();
        String fs = System.getProperty("file.separator");
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        
        batchLog = new File(Settings.get("vpg_S_log_dir") + fs + "vpg_batch_dssp_log_" + dateString + ".txt");
                        
        
        
        if(numProteins < 1) {
            System.err.println(Settings.getApptag() + "ERROR: Batch processor has nothing to process.");
            return;
        }
        
        if(numProteins > 100) {
            String warningText = "Processing " + numProteins + " proteins is gonna take quite a while. Are you sure about this?";
            if(numProteins > 1000) {
                warningText = "Processing " + numProteins + " proteins is gonna take a lot of time and should maybe be done with a shell script on the command line. Are you sure about this?";
            }
            if(numProteins > 10000) {
                warningText = "Processing " + numProteins + " proteins is gonna take ages and should maybe be done with a shell script on a cluster. Are you sure about this?";
            }
            
            Integer dialogResult = JOptionPane.showConfirmDialog(this, warningText, "VPG -- Really process " + numProteins + " input proteins?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if(dialogResult == JOptionPane.YES_OPTION) {
                System.out.println(Settings.getApptag() + "Throwing cautions to the wind and processing " + numProteins + " proteins. Go read a book.");
            } else {
                System.out.println(Settings.getApptag() + "The user decided against processing " + numProteins + " proteins at once. Most likely better.");
                return;
            }

        }
        
        System.out.println(Settings.getApptag() + "Processing " + numProteins + " proteins (PDB files).");
        this.maxProgress = numProteins;
        
        pm = new ProgressMonitor(this, "Batch processing proteins...", "Running...", 0, this.maxProgress);
        pm.setMillisToPopup(0);
        pm.setMillisToDecideToPopup(0);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        this.jButtonRunBatchDssp.setEnabled(false);
        this.jLabelStatus.setText("VPG Batch processor running, processing " + numProteins + " proteins...");
        
        
        this.successList = new ArrayList<String>();
        this.failList = new ArrayList<String>();
        
        pm.setProgress(0);
        task = new BatchCreateDsspFilesTask();
        task.addPropertyChangeListener(this);
        task.setParent(this);
        

        task.setCreateSubdirsForSplitPdbOutputFiles(this.jComboBoxPutPdbFiles.getSelectedIndex() == 2);                
        task.setCreateSubdirsForDsspOutputFiles(this.jComboBoxPutDsspFiles.getSelectedIndex() == 2);             
        task.setDownloadDsspIfFailed(this.jCheckBoxDownloadFailedDsspFiles.isEnabled());
        
        //System.out.println("Indices: subSplitPDB=" + this.jComboBoxPutPdbFiles.getSelectedIndex() +", subDssp=" + this.jComboBoxPutDsspFiles.getSelectedIndex() + ", dl=" + this.jCheckBoxDownloadFailedDsspFiles.isEnabled() + ".");
        
        if(task.createSubdirsDssp) { logText += "[task] Creating DSSP sub dirs.\n"; } else { logText += "[task] Not creating DSSP sub dirs.\n"; }
        if(task.createSubdirsSplitPdb) { logText += "[task] Creating SplitPDB sub dirs.\n"; } else { logText += "[task] Not creating SplitPDB sub dirs.\n"; }
        if(task.downloadDsspIfFailed) { logText += "[task] Download enabled.\n"; } else { logText += "[task] Download disabled.\n"; }
        
        // ========== run the task =========
        task.execute();                                        
    }//GEN-LAST:event_jButtonRunBatchDsspActionPerformed

    
    /**
     * Item listener implementation.
     * @param e the DocumentEvent to handle
     */
    @Override public void changedUpdate(DocumentEvent e) {
        this.checkInput();
    }
    
    /**
     * Item listener implementation.
     * @param e the DocumentEvent to handle
     */
    @Override public void removeUpdate(DocumentEvent e) {
        this.checkInput();        
    }    
    
    /**
     * Item listener implementation.
     * @param e the DocumentEvent to handle
     */
    @Override public void insertUpdate(DocumentEvent e) {
        this.checkInput();
    }
    
    
    @Override public void itemStateChanged(ItemEvent e) {
        Object source = e.getSource();

        
        if (source == this.jCheckBoxDownloadFailedDsspFiles) {   
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if( ! this.downloadWarningAlreadyShown) {                                        
                    //JOptionPane.showMessageDialog(this, "Activating the option may result in mass download DSSP files from the internet. Use with care.", "VPG -- Mass download warning", JOptionPane.WARNING_MESSAGE);
                    System.out.println(Settings.getApptag() + "NOTE: Activating the download failed DSSP files option may result in mass download DSSP files from the internet. Use with care.");
                }
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {                
            }                        
        }
        else if(source == this.jCheckBoxRunSplitPdb) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.jCheckBoxSkipSplitPdbIfSplitAvailable.setEnabled(true);
                this.jCheckBoxSkipSplitPdbIfSplitAvailable.setEnabled(true);
                this.jCheckBoxSplitPdbGzippedOutput.setEnabled(true);
                this.jComboBoxPutPdbFiles.setEnabled(true);
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {  
                System.out.println(Settings.getApptag() + "NOTE: Not running SplitPDB leads to errors if it has not been run before for all PDB files. Use with care.");
                this.jCheckBoxSkipSplitPdbIfSplitAvailable.setEnabled(false);
                this.jCheckBoxSkipSplitPdbIfSplitAvailable.setEnabled(false);
                this.jCheckBoxSplitPdbGzippedOutput.setEnabled(false);
                this.jComboBoxPutPdbFiles.setEnabled(false);
            }
        }
        
        
        this.setComboboxDependantFields(e);
        this.checkInput();
    }
    
    
    private void setComboboxDependantFields(ItemEvent e) {
        
        // SplitPDB output
        if(this.jCheckBoxRunSplitPdb.isSelected()) {
            if(this.jComboBoxPutPdbFiles.getSelectedIndex() > 0) {
                this.jTextFieldPdbSplitOutputDir.setEnabled(true);
                this.jButtonSelectPdbSplitOutputDir.setEnabled(true);
            } else {
                this.jTextFieldPdbSplitOutputDir.setEnabled(false);
                this.jButtonSelectPdbSplitOutputDir.setEnabled(false);
            }
        } else {
            this.jTextFieldPdbSplitOutputDir.setEnabled(false);
            this.jButtonSelectPdbSplitOutputDir.setEnabled(false);
        }
        
        // DSSP output
        if(this.jComboBoxPutDsspFiles.getSelectedIndex() > 0) {
            this.jTextFieldDsspOutputDir.setEnabled(true);
            this.jButtonSelectDsspOutputDir.setEnabled(true);
        } else {
            this.jTextFieldDsspOutputDir.setEnabled(false);
            this.jButtonSelectDsspOutputDir.setEnabled(false);
        }
    }
    
    
    
    public void checkInput() {
        
        Boolean allOk = true;
        String statusText = "";        
        
        File inputDirPdb = new File(this.jTextFieldPdbFileDirectory.getText());
        if(inputDirPdb.canRead() && inputDirPdb.isDirectory()) {
            this.jTextFieldPdbFileDirectory.setBackground(Color.WHITE);
            statusText += "PDB input directory seems ok. ";                        
        } else {
            this.jTextFieldPdbFileDirectory.setBackground(Color.RED);
            statusText += "PDB input directory  '" + inputDirPdb.getAbsolutePath() + "' invalid. ";
            allOk = false;
        }
        
        
        File dsspcmbi = new File(Settings.get("vpg_S_path_dssp"));
        if( ! (dsspcmbi.isFile() && dsspcmbi.canExecute())) {
            statusText += "ERROR: Path to dsspcmbi executable invalid.";
            System.err.println(Settings.getApptag() + "ERROR: Path to dsspcmbi executable is not configured properly in settings or file not executable: '" + Settings.get("vpg_S_path_dssp") + "'.\n");
            allOk = false;
        } else {
            System.out.println(Settings.getApptag() + "Dsspcmbi path ok.");
        }
        
        
        if(this.jTextFieldPdbSplitOutputDir.isEnabled()) {
            File outputDirSplitPdb = new File(this.jTextFieldPdbSplitOutputDir.getText());
            if(outputDirSplitPdb.canWrite() && outputDirSplitPdb.isDirectory()) {
                this.jTextFieldPdbSplitOutputDir.setBackground(Color.WHITE);
                
                long freeSpaceMB = outputDirSplitPdb.getUsableSpace() / 1024 / 1024;
                if(freeSpaceMB < 100) {
                    statusText += "SplitPDB output directory seems ok but has only " + freeSpaceMB + " MB free disk space.";
                    System.out.println(Settings.getApptag() + "WARNING: Output directory '" + outputDirSplitPdb.getAbsolutePath() + "' has only " + freeSpaceMB + " MB free disk space. ");
                } else {
                    statusText += "SplitPDB output directory seems ok and has " + freeSpaceMB + " MB free disk space. ";
                }
                
                
            } else {
                this.jTextFieldPdbSplitOutputDir.setBackground(Color.RED);
                statusText += "SplitPDB output directory + '" + outputDirSplitPdb.getAbsolutePath() + "' invalid. ";
                allOk = false;
            }
            
        }
        
        if(this.jTextFieldDsspOutputDir.isEnabled()) {
            File outputDirDssp = new File(this.jTextFieldDsspOutputDir.getText());
            if(outputDirDssp.canWrite() && outputDirDssp.isDirectory()) {
                this.jTextFieldDsspOutputDir.setBackground(Color.WHITE);
                
                long freeSpaceMB = outputDirDssp.getUsableSpace() / 1024 / 1024;
                if(freeSpaceMB < 100) {
                    statusText += "DSSP output directory seems ok but has only " + freeSpaceMB + " MB free disk space.";
                    System.out.println(Settings.getApptag() + "WARNING: Output directory '" + outputDirDssp.getAbsolutePath() + "' has only " + freeSpaceMB + " MB free disk space. ");
                } else {
                    statusText += "DSSP output directory seems ok and has " + freeSpaceMB + " MB free disk space. ";
                }                                
            } else {
                this.jTextFieldDsspOutputDir.setBackground(Color.RED);
                statusText += "DSSP output directory + '" + outputDirDssp.getAbsolutePath() + "' invalid. ";
                allOk = false;
            }
        }
        
        
        
        if(allOk) {
            System.out.println(Settings.getApptag() +  statusText);
            this.pdbFilesReadyForDssp = this.getSplitNonGzippedPdbFilesInDirectory(inputDirPdb);
            this.pdbFilesRequiringSplit = this.getNonSplitOrGzippedPdbFilesInDirectory(inputDirPdb);
            
            
            
            this.allPdbFiles = new HashMap<String, File>();
            this.allPdbFiles.putAll(pdbFilesReadyForDssp);
            this.allPdbFiles.putAll(pdbFilesRequiringSplit);
            
            Integer numProtDsspReady = this.pdbFilesReadyForDssp.size();
            Integer numProtReqSplit = this.pdbFilesRequiringSplit.size();
            Integer numProtTotal = allPdbFiles.size();
            
            System.out.println(Settings.getApptag() + "DSSP Batch processor ready: " + numProtDsspReady + " PDB files already split, " + numProtReqSplit + " require splitting (" + numProtTotal + " total).");
            
            if( ! this.jCheckBoxSkipSplitPdbIfSplitAvailable.isSelected()) {
                for(String pdbid : pdbFilesReadyForDssp.keySet()) {
                    pdbFilesRequiringSplit.put(pdbid, pdbFilesReadyForDssp.get(pdbid));
                    pdbFilesReadyForDssp.remove(pdbid);
                }
                numProtDsspReady = this.pdbFilesReadyForDssp.size();
                numProtReqSplit = this.pdbFilesRequiringSplit.size();
                numProtTotal = allPdbFiles.size();
                System.out.println(Settings.getApptag() + "Reprocessing split files, DSSP Batch ready and assuming: " + numProtDsspReady + " PDB files already split, " + numProtReqSplit + " require splitting (" + numProtTotal + " total).");
            }
            
            if(numProtTotal > 0) {
                this.jButtonRunBatchDssp.setText("Process " + numProtTotal + " proteins");
                this.jButtonRunBatchDssp.setEnabled(true);
            } else {
                this.jButtonRunBatchDssp.setText("Start batch processing");
                this.jButtonRunBatchDssp.setEnabled(false);
            }
        } else {
            System.err.println(Settings.getApptag() +  statusText);
            this.jButtonRunBatchDssp.setText("Start batch processing");
            this.jButtonRunBatchDssp.setEnabled(false);
        }
        
    }
    
    
    /**
     * Finds all split PDB files under the directory tree with root 'dir' and adds them to the HashMap pdbFiles. This version differs from the
     * one in the batch graph processing frame: it does only accept PDB files in format '<PDBID>.pdb.split'. These files are 
     * already split, not gzipped and thus ready for DSSP.
     * @param dir the directory, will be searched recursively
     * @param pdbFiles the Hashmap to which the files are added. Keys are PDB identifiers, values are the PDB File objects.
     */
    public void recurseSearchPdbSplitNotGzipped(File dir, HashMap<String, File> pdbFiles) {
        File[] children;
        String lcName;
        if(dir.isDirectory()) {
            children = dir.listFiles();
            
            for(File child : children) {
                if(child.isFile()) {
                    lcName = child.getName().toLowerCase();
                    if(lcName.endsWith(".pdb.split")) {
                        String pdbid = VpgMassGraphProcessingFrame.determinePdbidFromPdbFilename(lcName);
                        if(pdbid != null) {
                            pdbFiles.put(pdbid, child);
                        }
                    }
                    
                } else if (child.isDirectory()) {
                    recurseSearchPdbSplitNotGzipped(child, pdbFiles);
                }
            }            
        }
    }
    
    /**
     * Finds all non-split PDB files under the directory tree with root 'dir' and adds them to the HashMap pdbFiles. This version differs from the
     * one in the batch graph processing frame: it does only accept PDB files in format '<PDBID>.pdb', '<PDBID>.pdb.gz' and '<PDBID>.ent.gz'.
     * @param dir the directory, will be searched recursively
     * @param pdbFiles the Hashmap to which the files are added. Keys are PDB identifiers, values are the PDB File objects.
     */
    public void recurseSearchPdbNonSplitOrGzipped(File dir, HashMap<String, File> pdbFiles) {
        File[] children;
        String lcName;
        if(dir.isDirectory()) {
            children = dir.listFiles();
            
            for(File child : children) {
                if(child.isFile()) {
                    lcName = child.getName().toLowerCase();
                    if(lcName.endsWith(".pdb") || lcName.endsWith(".pdb.gz") || lcName.endsWith(".ent.gz") || lcName.endsWith(".split.gz")) {
                        String pdbid = VpgMassGraphProcessingFrame.determinePdbidFromPdbFilename(lcName);
                        if(pdbid != null) {
                            pdbFiles.put(pdbid, child);
                        }
                    }
                    
                } else if (child.isDirectory()) {
                    recurseSearchPdbNonSplitOrGzipped(child, pdbFiles);
                }
            }            
        }
    }
    
    
    
    /**
     * Finds all split PDB files under the directory tree with root 'dir'.
     * @param dir the directory, will be searched recursively
     * @return the file as a HashMap. Keys are PDB identifiers, values are the PDB File objects.
     */
    HashMap<String, File> getSplitNonGzippedPdbFilesInDirectory(File dir) {
        HashMap<String, File> locPdbFiles = new HashMap<String, File>();
        
        if(dir.isDirectory()) {
            recurseSearchPdbSplitNotGzipped(dir, locPdbFiles);
        }
        
        return locPdbFiles;
    }
    
    
    /**
     * Finds all non-split and/or gzipped PDB files under the directory tree with root 'dir'.
     * @param dir the directory, will be searched recursively
     * @return the file as a HashMap. Keys are PDB identifiers, values are the PDB File objects.
     */
    HashMap<String, File> getNonSplitOrGzippedPdbFilesInDirectory(File dir) {
        HashMap<String, File> locPdbFiles = new HashMap<String, File>();
        
        if(dir.isDirectory()) {
            recurseSearchPdbNonSplitOrGzipped(dir, locPdbFiles);
        }
        
        return locPdbFiles;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VpgBatchDsspFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VpgBatchDsspFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VpgBatchDsspFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VpgBatchDsspFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VpgBatchDsspFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCheckSettings;
    private javax.swing.JButton jButtonRunBatchDssp;
    private javax.swing.JButton jButtonSelectDsspOutputDir;
    private javax.swing.JButton jButtonSelectPdbDir;
    private javax.swing.JButton jButtonSelectPdbSplitOutputDir;
    private javax.swing.JCheckBox jCheckBoxDownloadFailedDsspFiles;
    private javax.swing.JCheckBox jCheckBoxRunSplitPdb;
    private javax.swing.JCheckBox jCheckBoxSkipSplitPdbIfSplitAvailable;
    private javax.swing.JCheckBox jCheckBoxSplitPdbGzippedOutput;
    private javax.swing.JComboBox jComboBoxPutDsspFiles;
    private javax.swing.JComboBox jComboBoxPutPdbFiles;
    private javax.swing.JLabel jLabelInputFiles;
    private javax.swing.JLabel jLabelPdbDir;
    private javax.swing.JLabel jLabelPutDsspFiles;
    private javax.swing.JLabel jLabelPutPdbFiles;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabeloptions;
    private javax.swing.JPanel jPanelContent;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparatorTop;
    private javax.swing.JTextField jTextFieldDsspOutputDir;
    private javax.swing.JTextField jTextFieldPdbFileDirectory;
    private javax.swing.JTextField jTextFieldPdbSplitOutputDir;
    // End of variables declaration//GEN-END:variables
}
