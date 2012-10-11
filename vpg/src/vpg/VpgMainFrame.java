/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package vpg;

import java.awt.Container;
import java.awt.Desktop;
import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.net.URLDecoder;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;

/**
 *
 * @author ts
 */
public class VpgMainFrame extends javax.swing.JFrame {
           
    
    String appTag = "[VPG] ";
    
    /**
     * Creates new form VpgMainFrame
     */
    public VpgMainFrame() {
        
        System.out.println(appTag + "VPG -- A frontend for VPLG");
        System.out.println(appTag + "Loading settings...");
        
        
        // The settings are defined in the Settings class. They are loaded from the config file below and can then be overwritten
        //  by command line arguments.
        Settings.init();
        

        if(Settings.load("")) {             // Empty string means that the default file of the Settings class is used
            //System.out.println("  Settings loaded from properties file.");
        }
        else {
            System.err.println(appTag + "WARNING: Could not load settings from properties file, trying to create it.");
            if(Settings.createDefaultConfigFile()) {
                System.out.println(appTag + "  Default config file created, will use it from now on.");
            } else {
                System.err.println(appTag + "WARNING: Could not create default config file, check permissions. Using internal default settings.");
            }
            Settings.resetAll();        // init settings with internal defaults for this run
        }

        String vpgVersion = Settings.getVersion();
        System.out.println(appTag + "Starting VPG version " + vpgVersion + " ...");
        initComponents();
        
        
        
    }
    
    
    /**
     * Simple helper function that determines whether basic settings are ok. Used for status bar
     * notification of broken settings. Checks input path, output path and the plcc.jar file. Ignores
     * splitpdb.jar and dsspcmbi.
     * @return true if the essential stuff is configured correctly, false otherwise.
     */
    public Boolean essentialSettingsOK() {
        Boolean allgood = true;
        
        File file;
        
        file = new File(Settings.get("vpg_S_input_dir"));
        if(! (file.canRead() && file.isDirectory())) {
            allgood = false;
            System.out.println("[VPG] WARNING: Input directory '" + file.getAbsolutePath() + "' not set correctly, does not exist or not readable.");
        }
        
        file = new File(Settings.get("vpg_S_output_dir"));
        if(! (file.canWrite() && file.isDirectory())) {
            allgood = false;
            System.out.println("[VPG] WARNING: Output directory '" + file.getAbsolutePath() + "' not set correctly, does not exist or not writeable.");
        }
        
        file = new File(Settings.get("vpg_S_log_dir"));
        if(! (file.canWrite() && file.isDirectory())) {
            allgood = false;
            System.out.println("[VPG] WARNING: Log directory '" + file.getAbsolutePath() + "' not set correctly, does not exist or not writeable.");
        }
        
        file = new File(Settings.get("vpg_S_path_plcc"));
        if(! (file.canRead() && file.isFile())) {
            allgood = false;
            System.out.println("[VPG] WARNING: Path to plcc.jar '" + file.getAbsolutePath() + "' not set correctly. This program is essential, please fix.");
        }
        
        return allgood;
    }
    
    
    /**
     * Simple helper function that determines whether non-essential settings are ok.
     * @return true if some non-essential stuff is configured correctly, false otherwise.
     */
    public Boolean minorSettingsCheck() {
        Boolean allgood = true;
        
        File file;                
        
        file = new File(Settings.get("vpg_S_path_splitpdb"));
        if(! (file.canRead() && file.isFile())) {
            allgood = false;
            System.out.println("[VPG] INFO: Path to splitpdb.jar '" + file.getAbsolutePath() + "' not set correctly. This program is optional.");
        }
        
        file = new File(Settings.get("vpg_S_path_dssp"));
        if(! (file.canRead() && file.isFile())) {
            allgood = false;
            System.out.println("[VPG] INFO: Path to dsspcmbi '" + file.getAbsolutePath() + "' not set correctly. This program is optional.");
            System.out.println("[VPG] INFO+: The dsspcmbi binary may be called 'dssp-2.09-win32.exe' or similar, which is fine. You can also rename it to 'dsspcmbi.exe' if you want.)");                        
        }
        
        return allgood;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jStatusBarPanel = new javax.swing.JPanel();
        jStatusLabel = new javax.swing.JLabel();
        jPanelMainContent = new javax.swing.JPanel();
        jLabelWelcomeText1 = new javax.swing.JLabel();
        jLabelWelcomeLogo = new javax.swing.JLabel();
        jLabelWelcomeTextWhatsup = new javax.swing.JLabel();
        jComboBoxTasks = new javax.swing.JComboBox();
        jButtonStartTask = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemCreateGraphs = new javax.swing.JMenuItem();
        jMenuItemOpenImage = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuInput = new javax.swing.JMenu();
        jMenuItemDownloadFiles = new javax.swing.JMenuItem();
        jMenuItemGenerateDsspFile = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemSettings = new javax.swing.JMenuItem();
        jMenuBatchProcessing = new javax.swing.JMenu();
        jMenuItemBatchDssp = new javax.swing.JMenuItem();
        jMenuItemBatchGraph = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemHelp = new javax.swing.JMenuItem();
        jMenuManual = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VPG -- Frontend for Visualization of Protein Ligand Graphs");
        setMinimumSize(new java.awt.Dimension(400, 300));
        setName("VPG Main Window"); // NOI18N

        jStatusBarPanel.setBackground(new java.awt.Color(210, 210, 210));

        jStatusLabel.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jStatusLabel.setText("VPG ready.");

        javax.swing.GroupLayout jStatusBarPanelLayout = new javax.swing.GroupLayout(jStatusBarPanel);
        jStatusBarPanel.setLayout(jStatusBarPanelLayout);
        jStatusBarPanelLayout.setHorizontalGroup(
            jStatusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jStatusBarPanelLayout.setVerticalGroup(
            jStatusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusLabel)
        );

        jLabelWelcomeText1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabelWelcomeText1.setText("Welcome to the Visualization of Protein Ligand Graphs software.");

        jLabelWelcomeLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWelcomeLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/vplg_logo.png"))); // NOI18N
        jLabelWelcomeLogo.setToolTipText("VPLG logo");

        jLabelWelcomeTextWhatsup.setText("What would you like to do?");

        jComboBoxTasks.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Create a protein graph", "Download a PDB or DSSP file", "Create a DSSP file from a PDB file", "View existing graph images" }));

        jButtonStartTask.setText("Let's start!");
        jButtonStartTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartTaskActionPerformed(evt);
            }
        });

        jLabel1.setText("VPLG allows you to compute, visualize and save protein ligand graphs for protein structure analysis.");

        jLabel2.setText("It computes these graphs based on 3D atom coordinates from PDB files and SSE assignments from DSSP files.");

        javax.swing.GroupLayout jPanelMainContentLayout = new javax.swing.GroupLayout(jPanelMainContent);
        jPanelMainContent.setLayout(jPanelMainContentLayout);
        jPanelMainContentLayout.setHorizontalGroup(
            jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabelWelcomeTextWhatsup)
                    .addComponent(jLabelWelcomeLogo)
                    .addComponent(jLabelWelcomeText1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMainContentLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                .addComponent(jComboBoxTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addComponent(jButtonStartTask)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jLabel2))
                .addGap(148, 148, 148))
        );
        jPanelMainContentLayout.setVerticalGroup(
            jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelWelcomeText1)
                .addGap(18, 18, 18)
                .addComponent(jLabelWelcomeLogo)
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(12, 12, 12)
                .addComponent(jLabelWelcomeTextWhatsup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonStartTask))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jMenuFile.setMnemonic('f');
        jMenuFile.setText("File");

        jMenuItemCreateGraphs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCreateGraphs.setMnemonic('g');
        jMenuItemCreateGraphs.setText("Create protein graph");
        jMenuItemCreateGraphs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCreateGraphsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemCreateGraphs);

        jMenuItemOpenImage.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpenImage.setMnemonic('v');
        jMenuItemOpenImage.setText("Graph Image Viewer");
        jMenuItemOpenImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenImageActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpenImage);

        jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemExit.setMnemonic('x');
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuItemExitMouseClicked(evt);
            }
        });
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        jMenuInput.setMnemonic('i');
        jMenuInput.setText("Input");

        jMenuItemDownloadFiles.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemDownloadFiles.setMnemonic('d');
        jMenuItemDownloadFiles.setText("Download input files");
        jMenuItemDownloadFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDownloadFilesActionPerformed(evt);
            }
        });
        jMenuInput.add(jMenuItemDownloadFiles);

        jMenuItemGenerateDsspFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemGenerateDsspFile.setMnemonic('g');
        jMenuItemGenerateDsspFile.setText("Generate DSSP file");
        jMenuItemGenerateDsspFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateDsspFileActionPerformed(evt);
            }
        });
        jMenuInput.add(jMenuItemGenerateDsspFile);

        jMenuBar.add(jMenuInput);

        jMenuEdit.setMnemonic('e');
        jMenuEdit.setText("Edit");

        jMenuItemSettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSettings.setMnemonic('s');
        jMenuItemSettings.setText("Settings");
        jMenuItemSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSettingsActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemSettings);

        jMenuBar.add(jMenuEdit);

        jMenuBatchProcessing.setMnemonic('b');
        jMenuBatchProcessing.setText("Batch Processing");

        jMenuItemBatchDssp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemBatchDssp.setMnemonic('d');
        jMenuItemBatchDssp.setText("Batch DSSP file creator");
        jMenuItemBatchDssp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBatchDsspActionPerformed(evt);
            }
        });
        jMenuBatchProcessing.add(jMenuItemBatchDssp);

        jMenuItemBatchGraph.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemBatchGraph.setMnemonic('g');
        jMenuItemBatchGraph.setText("Batch Graph Creator");
        jMenuItemBatchGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBatchGraphActionPerformed(evt);
            }
        });
        jMenuBatchProcessing.add(jMenuItemBatchGraph);

        jMenuBar.add(jMenuBatchProcessing);

        jMenuHelp.setMnemonic('h');
        jMenuHelp.setText("Help");

        jMenuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItemHelp.setMnemonic('h');
        jMenuItemHelp.setText("Help");
        jMenuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemHelp);

        jMenuManual.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuManual.setMnemonic('o');
        jMenuManual.setText("Online Documentation");
        jMenuManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuManualActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuManual);

        jMenuItemAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemAbout.setMnemonic('a');
        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar.add(jMenuHelp);

        setJMenuBar(jMenuBar);
        jMenuBar.getAccessibleContext().setAccessibleName("Menu bar");
        jMenuBar.getAccessibleContext().setAccessibleDescription("The menu bar of VPG.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jStatusBarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanelMainContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelMainContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStatusBarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemExitMouseClicked
        System.out.println(appTag + "Exiting.");
        this.dispose();
        System.exit(0);
    }//GEN-LAST:event_jMenuItemExitMouseClicked

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed

        System.out.println(appTag + "Exiting.");
        this.dispose();
        System.exit(0);
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSettingsActionPerformed

        VpgSettingsFrame setFrame = new VpgSettingsFrame();
        setFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        setFrame.setVisible(true);
    }//GEN-LAST:event_jMenuItemSettingsActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed

        VpgAboutFrame abFrame = new VpgAboutFrame();
        abFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        abFrame.setVisible(true);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuManualActionPerformed
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(Settings.get("vpg_S_online_manual_url")));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "ERROR: Cannot open manual URI in browser: '" + e.getMessage() + "'.", "VPLG Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("ERROR: Cannot open manual URI in browser: '" + e.getMessage() + "'.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "ERROR: Cannot open manual in browser, Desktop API not supported on this Java VM.", "VPLG Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("ERROR: Cannot open manual in browser, Desktop API not supported on this Java VM.");
        }
    }//GEN-LAST:event_jMenuManualActionPerformed

    private void jMenuItemOpenImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenImageActionPerformed
          
        /*
        class PngFilter extends javax.swing.filechooser.FileFilter {
            
            @Override public boolean accept(File file) {
                String filename = file.getName();
                return (filename.endsWith(".png"));
            }
            
            @Override public String getDescription() {
                return "*.png";
            }
        }
        
        
        File defaultDir = new File(Settings.get("vpg_S_output_dir"));
        File selectedFile;
        JFileChooser fc = new JFileChooser(defaultDir);        
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);       
        fc.addChoosableFileFilter(new PngFilter());
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            if(selectedFile.canRead()) {
                VpgImageFrame imgFrame = new VpgImageFrame();
                imgFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
                imgFrame.setVisible(true);
            }
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
            fc.setVisible(false);
        }
        * 
        */
        /*
        VpgImageFrame imgFrame = new VpgImageFrame();
        JTree tree = new JTree(FileTree.addTree("."));
        imgFrame.getFiletystemPanel().add(tree);
        System.out.println(appTag + "Opened VpgImageFrame from VpgMainFrame.");
        imgFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        imgFrame.setVisible(true);
         * 
         */
        VpgGraphViewerFrame fs = new VpgGraphViewerFrame(System.getProperty("user.home"));
        fs.setDefaultCloseOperation(HIDE_ON_CLOSE);
        fs.setVisible(true);
        
        
    }//GEN-LAST:event_jMenuItemOpenImageActionPerformed

    private void jButtonStartTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartTaskActionPerformed

        String selection = (String) this.jComboBoxTasks.getSelectedItem();
        //System.out.println("Selected item was '" + selection + "'.");
        
        if(selection.equals("Create a protein graph")) {
            System.out.println("[VPG] Starting module to create protein graphs...");            
            new VpgCreateGraphFrame().setVisible(true);
        }
        else if(selection.equals("Download a PDB or DSSP file")) {
            System.out.println("[VPG] Starting module to download input files...");
            VpgDownloadFrame dlf = new VpgDownloadFrame();
            dlf.setDefaultCloseOperation(HIDE_ON_CLOSE);
            dlf.setVisible(true);
        }
        else if(selection.equals("Create a DSSP file from a PDB file")) {
            System.out.println("[VPG] Starting module to generate DSSP file using dsspcmbi...");
            new VpgGenerateDsspFileFrame().setVisible(true);
        }
        else if(selection.equals("View existing graph images")) {
            System.out.println("[VPG] Starting module to view graph images...");
            VpgGraphViewerFrame fs = new VpgGraphViewerFrame(System.getProperty("user.home"));
            fs.setDefaultCloseOperation(HIDE_ON_CLOSE);
            fs.setVisible(true);
        }        
        else {
            System.out.println("[VPG] I don't care whether you want to do that.");
        }
    }//GEN-LAST:event_jButtonStartTaskActionPerformed

    private void jMenuItemCreateGraphsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCreateGraphsActionPerformed
       
        new VpgCreateGraphFrame().setVisible(true);
    }//GEN-LAST:event_jMenuItemCreateGraphsActionPerformed

    private void jMenuItemDownloadFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDownloadFilesActionPerformed
        
        new VpgDownloadFrame().setVisible(true);
    }//GEN-LAST:event_jMenuItemDownloadFilesActionPerformed

    private void jMenuItemGenerateDsspFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateDsspFileActionPerformed

        new VpgGenerateDsspFileFrame().setVisible(true);
    }//GEN-LAST:event_jMenuItemGenerateDsspFileActionPerformed

    private void jMenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpActionPerformed
        
        new VpgHelpFrame().setVisible(true);
    }//GEN-LAST:event_jMenuItemHelpActionPerformed

    private void jMenuItemBatchGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBatchGraphActionPerformed
        new VpgMassGraphProcessingFrame().setVisible(true);
    }//GEN-LAST:event_jMenuItemBatchGraphActionPerformed

    private void jMenuItemBatchDsspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBatchDsspActionPerformed
        
        new VpgBatchDsspFrame().setVisible(true);
    }//GEN-LAST:event_jMenuItemBatchDsspActionPerformed

    
    private static void usage() {
        System.out.println(Settings.getApptag() + "USAGE: java -jar vpg.jar [<options>]");
        System.out.println(Settings.getApptag() + "Starting this program without any options runs the GUI.");
        System.out.println(Settings.getApptag() + "Valid options:");
        System.out.println(Settings.getApptag() + "  -h | --help     : show this help screen and exit.");        
        System.out.println(Settings.getApptag() + "  -v | --version  : show VPG version info and exit.");
        System.out.println(Settings.getApptag() + "Note that VPG is a GUI application, it requires X11 or something like that.");
        System.out.println(Settings.getApptag() + "You can run the SPLITPDB and PLCC programs from the command line though.");
    }
    
    /**
     * Determines the path of the JAR file and tries to guess where the other
     * programs are installed based on this path.
     * @return whether it seems to have worked, i.e., whether files exist at the guessed positions. Makes no guarantee that this are really the correct files, but the names are fine if this returns true.
     */
    private static Boolean tryToGuessSettingsFromJarPath() {
        String path = null;
        Boolean jarOK = false;
        Boolean installPathOK = false;
        
        try {
            path = VpgMainFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (Exception e) {
            System.err.println(Settings.getApptag() + "WARNING: Could not create URI: '" + e.getMessage() + "'.");
        }   
        
        if(path == null) {
            System.err.println(Settings.getApptag() + "WARNING: Could not get path for VpgMainFrame class, cannot guess install path.");
            return false;
        } else {
            System.out.println(Settings.getApptag() + "Undecoded path is '" + path + "'.");
            File jarPath = new File(path);
            if(jarPath.exists() && jarPath.isFile()) {
                System.out.println(Settings.getApptag() + "Assumed JAR path '" + jarPath.getAbsolutePath() + "' exists.");
                jarOK = true;
            } else {
                System.err.println(Settings.getApptag() + "WARNING: No file exists at assumed JAR path '" + jarPath.getAbsolutePath() + "'.");
                jarOK = false;
            }
            
            File installDir = jarPath.getParentFile();
            if(installDir.isDirectory()) {
                System.out.println(Settings.getApptag() + "Assumed install directory '" + installDir.getAbsolutePath() + "' exists.");
                installPathOK = true;
            } else {
                System.err.println(Settings.getApptag() + "WARNING: Assumed install directory '" + installDir.getAbsolutePath() + "' exists.");
                installPathOK = false;
            }
            
            if(jarOK && installPathOK) {
                System.out.println(Settings.getApptag() + "OK, guessed install path '" + installDir.getAbsolutePath() + "' contains JAR file (at '" + jarPath.getAbsolutePath() + "').");
                setSettingsBasedOnInstallDir(installDir, false);
                return true;
            }
        }

        /*
        String decodedPath = null;
        try { 
            URLDecoder.decode(path, "UTF-8");        
        } catch(Exception e) {
            System.err.println(Settings.getApptag() + "WARNING: Could not decode path of JAR file, cannot guess other settings: '" + e.getMessage() + "'.");
            return false;
        }
        
        if(decodedPath != null) {
            System.out.println(Settings.getApptag() + "The JAR path seems to be '" + decodedPath + "'.");
            //TODO: do some guessing on locations of splitpdb, output and log paths here and set them
            return true;
        } else {
            System.err.println(Settings.getApptag() + "WARNING: Could not guess install path, decoded path is null (Maybe loaded from non-path?).");
        }
        */
        
        return false;
    }
    
    /**
     * Sets settings to application paths based on the install dir. This is done
     * by using knowledge on how the VPLG programs are organized in the install directory, it does
     * not search for them in the file system or do any other magic.
     * This function will try to verify a guess unless forced not to, i.e., it only sets a setting if a file exists at
     * the guessed path. Of course, the file could be anything.
     * 
     * @param installDir the base VPLG install dir
     * @param forceSet if set to true, even settings that failed verification of file existence of file system level will be applied
     */
    public static void setSettingsBasedOnInstallDir(File installDir, Boolean forceSet) {
        //TODO: implement me
        System.out.println(Settings.getApptag() + "WARNING: setSettingsBasedOnInstallDir(): Implement me");
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {                        
        
        System.out.println(Settings.getApptag() + "=============== VPG ===============");
        System.out.println(Settings.getApptag() + "This is free software, it comes WITHOUT ANY WARRANTIES, not even implied warranties.");
        System.out.println(Settings.getApptag() + "Copyright Tim Schaefer 2012, see http://vplg.sourceforge.net for more info.");
        
        if(args.length > 0) {
            for(Integer i = 0; i < args.length; i++) {
                
                if(args[i].equals("-h") || args[i].equals("--help")) {
                    usage();
                    System.exit(0);                    
                }
                
                if(args[i].equals("-v") || args[i].equals("--version")) {
                    System.out.println(Settings.getApptag() + "VPG version: " + Settings.getVersion() + "");
                    System.exit(0);                    
                }
                
            }
        }
        
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VpgMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VpgMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VpgMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VpgMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                VpgMainFrame vpg = new VpgMainFrame();
                vpg.setVisible(true);
                
                if(vpg.essentialSettingsOK()) {
                    vpg.jStatusLabel.setText("VPG version " + Settings.getVersion() + " ready.");
                } else {
                    System.out.println(Settings.getApptag() + "Trying to guess paths to VPLG programs from JAR path.");
                    if(tryToGuessSettingsFromJarPath()) {
                        System.out.println(Settings.getApptag() + "Settings determined and applied, hope they fit.");
                    } else {
                        System.err.println(Settings.getApptag() + "WARNING: Could not guess settings, user will have to adapt them manually.");
                    }
                    vpg.jStatusLabel.setText("VPG version " + Settings.getVersion() + " ready.");
                    JOptionPane.showMessageDialog(vpg, "Welcome to VPG!\n\n You should start by configuring the paths to essential programs under Edit => Settings.\nOnce the settings are ready, you will be able to compute and visualize your first protein ligand graph!\n\nEnjoy!", "VPG -- Welcome", JOptionPane.INFORMATION_MESSAGE);
                }
        
                vpg.minorSettingsCheck();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonStartTask;
    private javax.swing.JComboBox jComboBoxTasks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelWelcomeLogo;
    private javax.swing.JLabel jLabelWelcomeText1;
    private javax.swing.JLabel jLabelWelcomeTextWhatsup;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuBatchProcessing;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenu jMenuInput;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemBatchDssp;
    private javax.swing.JMenuItem jMenuItemBatchGraph;
    private javax.swing.JMenuItem jMenuItemCreateGraphs;
    private javax.swing.JMenuItem jMenuItemDownloadFiles;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemGenerateDsspFile;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemOpenImage;
    private javax.swing.JMenuItem jMenuItemSettings;
    private javax.swing.JMenuItem jMenuManual;
    private javax.swing.JPanel jPanelMainContent;
    private javax.swing.JPanel jStatusBarPanel;
    private javax.swing.JLabel jStatusLabel;
    // End of variables declaration//GEN-END:variables
}
