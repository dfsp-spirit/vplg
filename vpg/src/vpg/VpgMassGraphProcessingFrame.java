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
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A form for the batch processing of many PDB files.
 * @author ts
 */
public class VpgMassGraphProcessingFrame extends javax.swing.JFrame implements ItemListener, DocumentListener, PropertyChangeListener {
    
    private ArrayList<String> pdbFilesWithValidDsspFiles;
    HashMap<String, File> pdbFiles;
    HashMap<String, File> dsspFiles;
    private ProgressMonitor pm;
    private BatchProcessPdbFilesTask task;
    private int maxProgress = 100;
    private String taskOutput = "";
    private String neOptions = "";
    private ArrayList<String> successList;
    private ArrayList<String> failList;
    private File batchLog;

    /**
     * Creates new form VpgMassGraphProcessingFrame
     */
    public VpgMassGraphProcessingFrame() {
        initComponents();
        
        String fs = System.getProperty("file.separator");
        this.jTextFieldPdbFileDirectory.setText(System.getProperty("user.home") + fs + "data" + fs + "PDB");
        this.jTextFieldPlccSettingsFile.setText(System.getProperty("user.home") + fs + "software" + fs + "vplg" + fs + "example_data" + fs + "plccopt" + fs + "default_settings.plccopt");
        this.jTextFieldFinalOutputDir.setText(System.getProperty("user.home") + fs + "data" + fs + "VPLG");
        
        this.jCheckBoxCustomPlccSettings.addItemListener(this);
        
        this.jTextFieldPdbFileDirectory.getDocument().addDocumentListener(this);
        this.jTextFieldPlccSettingsFile.getDocument().addDocumentListener(this);
        this.jTextFieldFinalOutputDir.getDocument().addDocumentListener(this);
        
        this.checkInput();
    }
    
    /**
     * A worker thread that calls the external SplitPDB program for many proteins.
     * @author ts
     */
    class BatchProcessPdbFilesTask extends SwingWorker<Void, Void> {
        
        Component parent = null;
        
        public void setParent(Component c) {
            this.parent = c;
        }
        
        @Override
        public Void doInBackground() {
            String pdbid;
            ProcessResult pr;
            Boolean allOk;
            Integer numOk = 0;
            int progress = 0;
            setProgress(0);
            maxProgress = pdbFilesWithValidDsspFiles.size();
                            
            while (progress < maxProgress && !isCancelled()) {    
                if(pm.isCanceled()) {
                    System.out.println(Settings.getApptag() + "NOTE: Batch processing canceled in progress monitor.");
                    this.cancel(true);
                    return null;
                }

                pdbid = pdbFilesWithValidDsspFiles.get(progress);

                pm.setProgress(Math.min(progress, maxProgress));
                pm.setNote("Processing " + pdbid.toUpperCase() + " (" + progress + "/" + maxProgress + ").");

                System.out.println(Settings.getApptag() + "#" + progress + ", " + pdbid.toUpperCase() + ": '" + pdbFiles.get(pdbid) + "', '" + dsspFiles.get(pdbid) + "'.");

                pr = VpgJobs.runPlcc(pdbid, pdbFiles.get(pdbid), dsspFiles.get(pdbid), new File(System.getProperty("user.home")), new File(getOutputDir()), neOptions);
                allOk = (pr.getReturnValue() == 0);

                if(allOk) { 
                    numOk++; 
                    successList.add(pdbid);
                } else {
                    failList.add(pdbid);
                }

                progress++;                                                    
            }
                        
            return null;
        }
 
        @Override
        public void done() {
            
            pm.close();
            parent.setCursor(null);

            Integer numNotProcessed = pdbFilesWithValidDsspFiles.size() - successList.size() - failList.size();

            System.out.println(Settings.getApptag() + "Results: " + successList.size() + " succeeded, " + failList.size() + " failed, " + numNotProcessed + " not processed.");

            String logText = "Results: " + successList.size() + " succeeded, " + failList.size() + " failed, " + numNotProcessed + " not processed.\n";
            String failOutput = "";
            if(failList.size() > 0) {
                failOutput += "WARNING: Failed PDB IDs: ";
                for(String f : failList) {
                    failOutput += (f + " ");
                }
                failOutput += "\n";
                System.err.print(Settings.getApptag() + failOutput);                
            }

            logText += failOutput;

            String successOutput = "";
            if(successList.size() > 0) {
                successOutput += "Succeeded PDB IDs: ";
                for(String f : successList) {
                    successOutput += (f + " ");
                }
                successOutput += "\n";
                System.out.print(Settings.getApptag() + successOutput);                
            }

            logText += successOutput;

            IO.stringToTextFile(batchLog.getAbsolutePath(), logText);

            JOptionPane.showMessageDialog(parent, logText, "VPG -- Batch processing results", JOptionPane.INFORMATION_MESSAGE);
            
            jButtonRunBatchJob.setEnabled(false);   // prevents user from starting over accidently or thinking it didnt run, he/she can click 'Check Settings', then start again.
            jButtonCheckSettings.setEnabled(true);
            jLabelStatus.setText("VPG Batch processor ready.");
            pm.setProgress(0);
            pm.close();        }
    }
    
    
    /**
     * 
     * @return the current plccopt file or null if no such file is in use atm
     */
    public File getPlccoptFileFromForm() {
        if(this.jCheckBoxCustomPlccSettings.isSelected()) {
            return new File(this.jTextFieldPlccSettingsFile.getText());
        } else {
            return null;
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
        jLabelPdbFileDir = new javax.swing.JLabel();
        jTextFieldPdbFileDirectory = new javax.swing.JTextField();
        jLabelGetDsspFiles = new javax.swing.JLabel();
        jComboBoxGetDsspFilesBy = new javax.swing.JComboBox();
        jSeparatorTop = new javax.swing.JSeparator();
        jButtonSelectPdbDir = new javax.swing.JButton();
        jLabelSettings = new javax.swing.JLabel();
        jCheckBoxCustomPlccSettings = new javax.swing.JCheckBox();
        jButtonSelectPlccSettingsFile = new javax.swing.JButton();
        jScrollPanePlccSettingsFile = new javax.swing.JScrollPane();
        jTextFieldPlccSettingsFile = new javax.swing.JTextField();
        jButtonRunBatchJob = new javax.swing.JButton();
        jButtonCheckSettings = new javax.swing.JButton();
        jSeparatorBottom = new javax.swing.JSeparator();
        jLabelFinalOutputDir = new javax.swing.JLabel();
        jTextFieldFinalOutputDir = new javax.swing.JTextField();
        jButtonSelectFinalOutputDir = new javax.swing.JButton();
        jCheckBoxDoNotRunSplitPDB = new javax.swing.JCheckBox();

        setTitle("VPG -- Batch Processor");

        javax.swing.GroupLayout jPanelStatusLayout = new javax.swing.GroupLayout(jPanelStatus);
        jPanelStatus.setLayout(jPanelStatusLayout);
        jPanelStatusLayout.setHorizontalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelStatusLayout.setVerticalGroup(
            jPanelStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabelStatus.setBackground(new java.awt.Color(200, 200, 200));
        jLabelStatus.setText("VPG Batch processor ready.");

        jLabelPdbFileDir.setText("Directory to search for PDB files:");

        jTextFieldPdbFileDirectory.setText("/home/ts/data/PDB");
        jTextFieldPdbFileDirectory.setToolTipText("The directory to search recursively for PDB files.");

        jLabelGetDsspFiles.setText("Get DSSP files by...");

        jComboBoxGetDsspFilesBy.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Searching the directory above (PDB and DSSP file for a protein can be in different subdirs)" }));
        jComboBoxGetDsspFilesBy.setToolTipText("How to get the DSSP file for a PDB file,");

        jButtonSelectPdbDir.setText("Select...");
        jButtonSelectPdbDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectPdbDirActionPerformed(evt);
            }
        });

        jLabelSettings.setText("Settings");

        jCheckBoxCustomPlccSettings.setText("Use custom PLCC settings from file:");

        jButtonSelectPlccSettingsFile.setText("Select...");
        jButtonSelectPlccSettingsFile.setToolTipText("Select PLCC settings file. You can save one in the graph creator.");
        jButtonSelectPlccSettingsFile.setEnabled(false);
        jButtonSelectPlccSettingsFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectPlccSettingsFileActionPerformed(evt);
            }
        });

        jScrollPanePlccSettingsFile.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPanePlccSettingsFile.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextFieldPlccSettingsFile.setText("/home/ts/software/vplg/example_data/plccopt/default_settings.plccopt");
        jTextFieldPlccSettingsFile.setEnabled(false);
        jScrollPanePlccSettingsFile.setViewportView(jTextFieldPlccSettingsFile);

        jButtonRunBatchJob.setText("Start batch processing");
        jButtonRunBatchJob.setToolTipText("Start the batch processing. Takes a while if many PDB files are in the input directory.");
        jButtonRunBatchJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunBatchJobActionPerformed(evt);
            }
        });

        jButtonCheckSettings.setText("Check settings");
        jButtonCheckSettings.setToolTipText("Checks input of this form (not from the PLCC settings file).");
        jButtonCheckSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCheckSettingsActionPerformed(evt);
            }
        });

        jLabelFinalOutputDir.setText("Output directory:");
        jLabelFinalOutputDir.setToolTipText("The base output directory.");

        jTextFieldFinalOutputDir.setText("/home/ts/data/VPLG");
        jTextFieldFinalOutputDir.setToolTipText("");

        jButtonSelectFinalOutputDir.setText("Select...");
        jButtonSelectFinalOutputDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectFinalOutputDirActionPerformed(evt);
            }
        });

        jCheckBoxDoNotRunSplitPDB.setSelected(true);
        jCheckBoxDoNotRunSplitPDB.setText("The DSSP files do NOT need to be preprocessed by running SplitPDB, i.e., none of them contain multiple models");
        jCheckBoxDoNotRunSplitPDB.setToolTipText("Activating this option will save time if all DSSP files are already proprocessed but will result in errors if this is not the case. Handle with care!");
        jCheckBoxDoNotRunSplitPDB.setEnabled(false);

        javax.swing.GroupLayout jPanelContentLayout = new javax.swing.GroupLayout(jPanelContent);
        jPanelContent.setLayout(jPanelContentLayout);
        jPanelContentLayout.setHorizontalGroup(
            jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparatorBottom, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparatorTop)
                    .addGroup(jPanelContentLayout.createSequentialGroup()
                        .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelContentLayout.createSequentialGroup()
                                .addComponent(jLabelPdbFileDir)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanelContentLayout.createSequentialGroup()
                                .addComponent(jLabelGetDsspFiles)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxGetDsspFilesBy, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jTextFieldPdbFileDirectory))
                        .addGap(10, 10, 10)
                        .addComponent(jButtonSelectPdbDir))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelContentLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonCheckSettings)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonRunBatchJob))
                    .addGroup(jPanelContentLayout.createSequentialGroup()
                        .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelContentLayout.createSequentialGroup()
                                .addComponent(jCheckBoxCustomPlccSettings)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPanePlccSettingsFile))
                            .addGroup(jPanelContentLayout.createSequentialGroup()
                                .addComponent(jLabelFinalOutputDir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldFinalOutputDir)))
                        .addGap(10, 10, 10)
                        .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonSelectPlccSettingsFile)
                            .addComponent(jButtonSelectFinalOutputDir)))
                    .addGroup(jPanelContentLayout.createSequentialGroup()
                        .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelSettings)
                            .addComponent(jCheckBoxDoNotRunSplitPDB))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelContentLayout.setVerticalGroup(
            jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPdbFileDir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPdbFileDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSelectPdbDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGetDsspFiles)
                    .addComponent(jComboBoxGetDsspFilesBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparatorTop, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelSettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCheckBoxCustomPlccSettings)
                        .addComponent(jButtonSelectPlccSettingsFile))
                    .addComponent(jScrollPanePlccSettingsFile, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFinalOutputDir)
                    .addComponent(jTextFieldFinalOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSelectFinalOutputDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDoNotRunSplitPDB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(jSeparatorBottom, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRunBatchJob)
                    .addComponent(jButtonCheckSettings)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanelContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanelContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)))
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

    private void jButtonSelectPlccSettingsFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectPlccSettingsFileActionPerformed
        
        File defaultDir = new File(Settings.get("vpg_S_output_dir"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override 
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".plccopt") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "PLCC option files";
            }
        });
        
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldPlccSettingsFile.setText(fc.getSelectedFile().toString());
        }
    }//GEN-LAST:event_jButtonSelectPlccSettingsFileActionPerformed

    
    /**
     * This is invoked when task's progress property changes to update the ProgressMonitor accordingly.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        
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
            String message = "Batch processing task done with " + progress + " / " + maxProgress + " proteins.";
            System.out.println(Settings.getApptag() + message);
            pm.setNote(message);
            taskOutput += message;
            
            
        } 
       
       
       
        
 
    }
    
    
    private void jButtonRunBatchJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunBatchJobActionPerformed
        
        Integer numProteins = this.pdbFilesWithValidDsspFiles.size();
        String fs = System.getProperty("file.separator");
        batchLog = new File(this.getOutputDir() + fs + "vpg_batch.log");
                        
        
        this.neOptions = VpgJobs.getOptionsFromOptFile(this.getPlccoptFileFromForm());
        
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
        this.maxProgress = (numProteins - 1);
        
        pm = new ProgressMonitor(this, "Batch processing proteins...", "Running...", 0, this.maxProgress);
        pm.setMillisToPopup(0);
        pm.setMillisToDecideToPopup(0);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        this.jButtonRunBatchJob.setEnabled(false);
        this.jLabelStatus.setText("VPG Batch processor running, processing " + numProteins + " proteins...");
        
        
        this.successList = new ArrayList<String>();
        this.failList = new ArrayList<String>();
        
        pm.setProgress(0);
        task = new BatchProcessPdbFilesTask();
        task.addPropertyChangeListener(this);
        task.setParent(this);
        
        // ========== run the task =========
        task.execute();                                        
        
    }//GEN-LAST:event_jButtonRunBatchJobActionPerformed

    /*
    private Boolean checkPMstate(ProgressMonitor pm, Boolean taskDone) {
        if(pm.isCanceled()) {            
            pm.close();
            task.cancel(true);
            return false;
        }          
        
        return true;
    }
    */
    
    private void jButtonSelectFinalOutputDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectFinalOutputDirActionPerformed
        
        File defaultDir = new File(Settings.get("vpg_S_output_dir"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldFinalOutputDir.setText(fc.getSelectedFile().toString());
        }
    }//GEN-LAST:event_jButtonSelectFinalOutputDirActionPerformed

    private void jButtonCheckSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCheckSettingsActionPerformed
        
        this.checkInput();
    }//GEN-LAST:event_jButtonCheckSettingsActionPerformed

    
    public String getOutputDir() {
        return this.jTextFieldFinalOutputDir.getText();
    }
    
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
        Object source = e.getItemSelectable();

        if (source == this.jCheckBoxCustomPlccSettings) {                        
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.jTextFieldPlccSettingsFile.setEnabled(true);
                this.jButtonSelectPlccSettingsFile.setEnabled(true);
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                this.jTextFieldPlccSettingsFile.setEnabled(false);
                this.jButtonSelectPlccSettingsFile.setEnabled(false);
            }                        
        }
        
        this.checkInput();
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
        
        // TODO: Implement that the DSSP dir may differ from the PDB dir
        File inputDirDssp = new File(this.jTextFieldPdbFileDirectory.getText());
        if(inputDirDssp.canRead() && inputDirDssp.isDirectory()) {
            this.jTextFieldPdbFileDirectory.setBackground(Color.WHITE);
            statusText += "DSSP input directory seems ok. ";            
            
        } else {
            this.jTextFieldPdbFileDirectory.setBackground(Color.RED);
            statusText += "DSSP input directory  '" + inputDirDssp.getAbsolutePath() + "' invalid. ";
            allOk = false;
        }
        
        File outputDir = new File(this.jTextFieldFinalOutputDir.getText());
        if(outputDir.canWrite() && outputDir.isDirectory()) {
            this.jTextFieldFinalOutputDir.setBackground(Color.WHITE);
            statusText += "Output directory seems ok. ";                        
        } else {
            this.jTextFieldFinalOutputDir.setBackground(Color.RED);
            statusText += "Output directory  '" + inputDirPdb.getAbsolutePath() + "' invalid or not writeable. ";
            allOk = false;
        }
        
        if(this.jCheckBoxCustomPlccSettings.isSelected()) {
            File inputFilePlccopt = new File(this.jTextFieldPlccSettingsFile.getText());
           if(inputFilePlccopt.canRead() && inputFilePlccopt.isFile() && inputFilePlccopt.getName().endsWith(".plccopt")) {
                this.jTextFieldPlccSettingsFile.setBackground(Color.WHITE);
                statusText += "PLCC options file seems ok. ";
            } else {
                this.jTextFieldPlccSettingsFile.setBackground(Color.RED);
               statusText += "PLCC options file '" + inputFilePlccopt.getAbsolutePath() + "' invalid (must exist and have .plccopt file extension). ";
                allOk = false;
            }            
        }                
        
        
        if(allOk) {
            this.pdbFiles = getPdbFilesInDirectory(inputDirPdb);
            this.dsspFiles = getDsspFilesInDirectory(inputDirDssp);
            Integer numPdbFiles = this.pdbFiles.size();
            Integer numDsspFiles = this.dsspFiles.size();
            this.pdbFilesWithValidDsspFiles = getProteinsWithBothInputFiles(this.pdbFiles, this.dsspFiles);
            Integer numPdbFilesWithValidDsspFiles = this.pdbFilesWithValidDsspFiles.size();
            
            System.out.println(Settings.getApptag() + "Found " + numPdbFiles + " PDB files in batch directory.");
            System.out.println(Settings.getApptag() + "Found " + numDsspFiles + " DSSP files in batch directory.");
            System.out.println(Settings.getApptag() + "Found " + numPdbFilesWithValidDsspFiles + " valid input protein files (PDB+matching DSSP)");
            
            statusText += "Found " + numPdbFilesWithValidDsspFiles + " valid input protein files (PDB+matching DSSP)";
            
            if(numPdbFilesWithValidDsspFiles > 0) {
                
                if(numPdbFilesWithValidDsspFiles > 100) {
                    String lastWarning = "WARNING: Batch processing of " + numPdbFilesWithValidDsspFiles + " proteins is going to take a lot of time.";
                    if(numPdbFilesWithValidDsspFiles > 1000) {
                        lastWarning = "WARNING: Batch processing of " + numPdbFilesWithValidDsspFiles + " proteins is going to take a damn long time.";
                    }
                    if(numPdbFilesWithValidDsspFiles > 10000) {
                        lastWarning = "WARNING: Batch processing of " + numPdbFilesWithValidDsspFiles + " proteins is going to take ages.";
                    }
                    System.err.println(Settings.getApptag() + lastWarning);
                }
                
                this.jButtonRunBatchJob.setEnabled(true);                
                this.jButtonRunBatchJob.setText("Process " + numPdbFilesWithValidDsspFiles + " proteins.");
                System.out.println(Settings.getApptag() +  statusText);
            } else {
                this.jButtonRunBatchJob.setEnabled(false);
                this.jButtonRunBatchJob.setText("Start batch processing");
                System.err.println(Settings.getApptag() +  "WARNING: " + statusText);
                
            }            
            
        } else {
            this.jButtonRunBatchJob.setEnabled(false);
            this.jButtonRunBatchJob.setText("Start batch processing");
            System.err.println(Settings.getApptag() +  "WARNING: " + statusText);
        }
        
        
    }
    
    /**
     * Determines all PDB file entries in 'pdbFiles' which have a mathcing DSSP file entry in 'dsspFiles'.
     * @param pdbFiles the PDB file HashMap. Keys are PDB identifiers, values are file objects which represent the path to the PDB file for the protein with the PDB ID in key.
     * @param dsspFiles the DSSP file HashMap. Keys are PDB identifiers, values are file objects which represent the path to the DSSP file for the protein with the PDB ID in key.
     * @return a list of all PDB IDs (keys in pdbFiles) for which a key with the same name in 'dsspFiles' exists.
     */
    public ArrayList<String> getProteinsWithBothInputFiles(HashMap<String, File> pdbFiles, HashMap<String, File> dsspFiles) {
        ArrayList<String> validInputFiles = new ArrayList<String>();
        
        for(String pdbid : pdbFiles.keySet()) {
            if(dsspFiles.containsKey(pdbid)) {
                validInputFiles.add(pdbid);
            }
        }
            
        return validInputFiles;
    }
    
    
    /**
     * Finds all PDB files under the directory tree with root 'dir'.
     * @param dir the directory, will be searched recursively
     * @return the file as a HashMap. Keys are PDB identifiers, values are the PDB File objects.
     */
    HashMap<String, File> getPdbFilesInDirectory(File dir) {
        HashMap<String, File> pdbFiles = new HashMap<String, File>();
        
        if(dir.isDirectory()) {
            recurseSearchPdb(dir, pdbFiles);
        }
        
        return pdbFiles;
    }
    
    /**
     * Finds all DSSP files under the directory tree with root 'dir'.
     * @param dir the directory, will be searched recursively
     * @return the file as a HashMap. Keys are PDB identifiers, values are the DSSP File objects.
     */
    HashMap<String, File> getDsspFilesInDirectory(File dir) {
        HashMap<String, File> dsspFiles = new HashMap<String, File>();
        
        if(dir.isDirectory()) {
            recurseSearchDssp(dir, dsspFiles);
        }
        
        return dsspFiles;
    }
    
    /**
     * Finds all PDB files under the directory tree with root 'dir' and adds them to the HashMap pdbFiles.
     * @param dir the directory, will be searched recursively
     * @param pdbFiles the Hashmap to which the files are added. Keys are PDB identifiers, values are the PDB File objects.
     */
    public void recurseSearchPdb(File dir, HashMap<String, File> pdbFiles) {
        File[] children;
        if(dir.isDirectory()) {
            children = dir.listFiles();
            
            for(File child : children) {
                if(child.isFile()) {
                    if(child.getName().toLowerCase().endsWith(".pdb") || child.getName().toLowerCase().endsWith(".pdb.gz") || child.getName().toLowerCase().endsWith(".ent.gz")) {
                        String pdbid = determinePdbidFromPdbFilename(child.getName());
                        if(pdbid != null) {
                            pdbFiles.put(pdbid, child);
                        }
                    }
                    
                } else if (child.isDirectory()) {
                    recurseSearchPdb(child, pdbFiles);
                }
            }            
        }
    }
    
    
    /**
     * Finds all DSSP files under the directory tree with root 'dir' and adds them to the HashMap pdbFiles.
     * @param dir the directory, will be searched recursively
     * @param dsspFiles the Hashmap to which the files are added. Keys are PDB identifiers, values are the DSSP File objects.
     */
    public void recurseSearchDssp(File dir, HashMap<String, File> dsspFiles) {
        File[] children;
        if(dir.isDirectory()) {
            children = dir.listFiles();
            
            for(File child : children) {
                if(child.isFile()) {
                    if(child.getName().toLowerCase().endsWith(".dssp") || child.getName().toLowerCase().endsWith(".dssp.gz")) {
                        String pdbid = determinePdbidFromDsspFilename(child.getName());
                        if(pdbid != null) {
                            dsspFiles.put(pdbid, child);
                        }
                    }
                    
                } else if (child.isDirectory()) {
                    recurseSearchDssp(child, dsspFiles);
                }
            }            
        }
    }
    
    
    /**
     * Tries to determine the PDB identifier from a PDB file name. The file may be gzipped and
     * the following name schemes are supported: "<PDBID>.pdb", "<PDBID>.pdb.gz", "pdb<PDBID>.ent.gz". 
     * @param fname the input file name, must NOT contain the directory part 
     * @return the determined PDB identifier (a string of length 4, e.g., "8icd") or null if the name format was not supported
     */
    public String determinePdbidFromPdbFilename(String fname) {
        String filename = fname.toLowerCase();
        String pdbid = "";
        
        try {
        
            if(filename.endsWith(".pdb")) {
                pdbid = filename.split("\\.")[0];
            } else if(filename.endsWith(".pdb.gz")) {
                pdbid = filename.split("\\.")[0];
            } else if(filename.endsWith(".ent.gz")) {
                pdbid = (filename.split("\\.")[0]).replaceAll("pdb", "");
            } else {
                System.err.println(Settings.getApptag() + "WARNING: PDB file '" + fname + "' did not end with handled pattern, ignored.");
            }
        
        } catch (Exception e) {
            System.err.println(Settings.getApptag() + "WARNING: PDB file '" + fname + "' caused error while splitting pattern, ignored.");
            return null;
        }
        
        if(pdbid.length() == 4) {
            return pdbid;
        } else {
            System.err.println(Settings.getApptag() + "WARNING: PDB file '" + fname + "' caused error, determined pdbid '" + pdbid + "' has wrong number of characters, ignored.");
            return null;            
        }
                
    }
    
    
    
    /**
     * Tries to determine the PDB identifier from a DSSP file name. The file may be gzipped and
     * the following name schemes are supported: "<PDBID>.dssp", "<PDBID>.dssp.gz". 
     * @param fname the input file name, must NOT contain the directory part 
     * @return the determined PDB identifier (a string of length 4, e.g., "8icd") or null if the name format was not supported
     */
    public String determinePdbidFromDsspFilename(String fname) {
        String filename = fname.toLowerCase();
        String pdbid = "";
        
        try {
        
            if(filename.endsWith(".dssp")) {
                pdbid = filename.split("\\.")[0];
            } else if(filename.endsWith(".dssp.gz")) {
                pdbid = filename.split("\\.")[0];
            } else {
                System.err.println(Settings.getApptag() + "WARNING: DSSP file '" + fname + "' did not end with handled pattern, ignored.");
            }
        
        } catch (Exception e) {
            System.err.println(Settings.getApptag() + "WARNING: DSSP file '" + fname + "' caused error while splitting pattern, ignored.");
            return null;
        }
        
        if(pdbid.length() == 4) {
            return pdbid;
        } else {
            System.err.println(Settings.getApptag() + "WARNING: DSSP file '" + fname + "' caused error, determined pdbid '" + pdbid + "' has wrong number of characters, ignored.");
            return null;            
        }
                
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
            java.util.logging.Logger.getLogger(VpgMassGraphProcessingFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VpgMassGraphProcessingFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VpgMassGraphProcessingFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VpgMassGraphProcessingFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VpgMassGraphProcessingFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCheckSettings;
    private javax.swing.JButton jButtonRunBatchJob;
    private javax.swing.JButton jButtonSelectFinalOutputDir;
    private javax.swing.JButton jButtonSelectPdbDir;
    private javax.swing.JButton jButtonSelectPlccSettingsFile;
    private javax.swing.JCheckBox jCheckBoxCustomPlccSettings;
    private javax.swing.JCheckBox jCheckBoxDoNotRunSplitPDB;
    private javax.swing.JComboBox jComboBoxGetDsspFilesBy;
    private javax.swing.JLabel jLabelFinalOutputDir;
    private javax.swing.JLabel jLabelGetDsspFiles;
    private javax.swing.JLabel jLabelPdbFileDir;
    private javax.swing.JLabel jLabelSettings;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JPanel jPanelContent;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JScrollPane jScrollPanePlccSettingsFile;
    private javax.swing.JSeparator jSeparatorBottom;
    private javax.swing.JSeparator jSeparatorTop;
    private javax.swing.JTextField jTextFieldFinalOutputDir;
    private javax.swing.JTextField jTextFieldPdbFileDirectory;
    private javax.swing.JTextField jTextFieldPlccSettingsFile;
    // End of variables declaration//GEN-END:variables
}



