/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package vpg;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import java.util.Properties;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author ts
 */
public class VpgSettingsFrame extends javax.swing.JFrame implements DocumentListener {

    /**
     * Creates new form VpgSettingsFrame
     */
    public VpgSettingsFrame() {
        initComponents();
        
        // set general settings
        jTextFieldDefInputPath.setText(Settings.get("vpg_S_input_dir"));
        jTextFieldDefOutputPath.setText(Settings.get("vpg_S_output_dir"));
        
        // set application path settings
        jTextFieldPathDssp.setText(Settings.get("vpg_S_path_dssp"));
        jTextFieldPathPlccJar.setText(Settings.get("vpg_S_path_plcc"));
        jTextFieldPathSplitPDBJar.setText(Settings.get("vpg_S_path_splitpdb"));
        
        // set internet settings
        this.jTextFieldPdbDownloadUrl.setText(Settings.get("vpg_S_download_pdbfile_URL"));
        this.jTextFieldDsspDownloadUrl.setText(Settings.get("vpg_S_download_dsspfile_URL"));
        
        this.jCheckBoxPdbfileLowercase.setSelected(Settings.getBoolean("vpg_B_download_pdbid_is_lowercase"));
        this.jCheckBoxDsspfileLowercase.setSelected(Settings.getBoolean("vpg_B_download_dsspid_is_lowercase"));
        
        this.jTextFieldDefInputPath.getDocument().addDocumentListener(this);
        this.jTextFieldDefOutputPath.getDocument().addDocumentListener(this);
        this.jTextFieldPathDssp.getDocument().addDocumentListener(this);
        this.jTextFieldPathPlccJar.getDocument().addDocumentListener(this);
        this.jTextFieldPathSplitPDBJar.getDocument().addDocumentListener(this);
        
        // set database settings, they are parsed from the plcc config file
        parsePlccConfig();        
        checkPathSettings();
    }
    
    
    /*
    @Override public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        System.out.println("event!");
        if(source == this.jButtonBrowseDefInputPath || source == this.jButtonBrowseDefOutputPath) {
            checkPathSettings();
        }
    }
    * 
    */
    
    @Override public void changedUpdate(DocumentEvent e) {
        this.checkPathSettings();
    }
    
    @Override public void removeUpdate(DocumentEvent e) {
        this.checkPathSettings();
    }
    
    @Override public void insertUpdate(DocumentEvent e) {
        this.checkPathSettings();
    }

    
    
    /**
     * Checks the current path settings and marks fields with problems in red.
     */
    private void checkPathSettings() {
        File inputDir = new File(this.jTextFieldDefInputPath.getText());
        if(inputDir.isDirectory() && inputDir.canRead()) {
            this.jTextFieldDefInputPath.setBackground(Color.WHITE);
            
            if(! inputDir.canWrite()) {
                System.err.println(Settings.getApptag() + "WARNING: Input directory '" + inputDir.getAbsolutePath() + "' is not writeable, download of input files to this dir not possible.");
            }
            
        } else {
            System.err.println(Settings.getApptag() + "WARNING: Input directory '" + inputDir.getAbsolutePath() + "' does not exist or is not readable.");
            this.jTextFieldDefInputPath.setBackground(Color.RED);
        }
        
        File outputDir = new File(this.jTextFieldDefOutputPath.getText());
        if(outputDir.isDirectory() && outputDir.canWrite()) {
            this.jTextFieldDefOutputPath.setBackground(Color.WHITE);
        } else {
            this.jTextFieldDefOutputPath.setBackground(Color.RED);
            System.err.println(Settings.getApptag() + "WARNING: Output directory '" + outputDir.getAbsolutePath() + "' does not exist or is not writeable.");
        }
        
        File plccJar = new File(this.jTextFieldPathPlccJar.getText());
        if(plccJar.isFile() && plccJar.canRead()) {
            this.jTextFieldPathPlccJar.setBackground(Color.WHITE);
        } else {
            this.jTextFieldPathPlccJar.setBackground(Color.RED);
            System.err.println(Settings.getApptag() + "WARNING: PLCC jar file at '" + plccJar.getAbsolutePath() + "' does not exist or is not readable. ESSENTIAL!");
        }
        
        File splitpdbJar = new File(this.jTextFieldPathSplitPDBJar.getText());
        if(splitpdbJar.isFile() && splitpdbJar.canRead()) {
            this.jTextFieldPathSplitPDBJar.setBackground(Color.WHITE);
        } else {
            this.jTextFieldPathSplitPDBJar.setBackground(Color.ORANGE);
            System.err.println(Settings.getApptag() + "WARNING: SplitPDB jar file at '" + splitpdbJar.getAbsolutePath() + "' does not exist or is not readable.");
        }
        
        File dssp = new File(this.jTextFieldPathDssp.getText());
        if(dssp.isFile() && dssp.canExecute()) {
            this.jTextFieldPathDssp.setBackground(Color.WHITE);
        } else {
            this.jTextFieldPathDssp.setBackground(Color.ORANGE);
            System.err.println(Settings.getApptag() + "WARNING: dsspcmbi executable at '" + dssp.getAbsolutePath() + "' does not exist or is not executable.");
        }
        
        File plccConfig = new File(this.jTextFieldConfigfilePlcc.getText());
        if(plccConfig.isFile() && plccConfig.canRead()) {
            this.jTextFieldConfigfilePlcc.setBackground(Color.WHITE);
        } else {
            this.jTextFieldConfigfilePlcc.setBackground(Color.ORANGE);
            System.err.println(Settings.getApptag() + "WARNING: PLCC config file at '" + plccConfig.getAbsolutePath() + "' does not exist or is not readable.");
        }
        
        
    }


    
    /**
     * Parses DB info from plcc config and updates text fields.
     * @return 
     */
    public Boolean parsePlccConfig() {
        File cfgFile = new File(IO.getPlccConfigFilePath());
        
        if(! (cfgFile.canRead() && cfgFile.isFile()) ) {
            System.err.println("[VPG] WARNING: Cannot read PLCC config file at '" + cfgFile.getAbsolutePath() + "'. Run plcc once to create it.");
            this.jTextFieldConfigfilePlcc.setBackground(Color.RED);
            this.jTextFieldConfigfilePlcc.setToolTipText("Config file does not exist, run PLCC to create it. Setting database defaults.");
            this.jTextFieldDatabaseHost.setText("127.0.0.1");
            this.jTextFieldDatabasePort.setText("5432");
            this.jTextFieldDatabaseName.setText("vplg");
            this.jTextFieldDatabaseUsername.setText("vplg");
            this.jTextFieldDatabasePassword.setText("");                                
            
            return false;
        }
        else {
        
            Properties plccSettings = IO.getSettingsFromFile(cfgFile);
            this.jTextFieldConfigfilePlcc.setText(IO.getPlccConfigFilePath());

            this.jTextFieldDatabaseHost.setText(plccSettings.getProperty("plcc_S_db_host"));
            this.jTextFieldDatabasePort.setText(plccSettings.getProperty("plcc_I_db_port"));
            this.jTextFieldDatabaseName.setText(plccSettings.getProperty("plcc_S_db_name"));
            this.jTextFieldDatabaseUsername.setText(plccSettings.getProperty("plcc_S_db_username"));
            this.jTextFieldDatabasePassword.setText(plccSettings.getProperty("plcc_S_db_password"));                                
            this.jTextFieldConfigfilePlcc.setBackground(Color.WHITE);
            this.jTextFieldConfigfilePlcc.setToolTipText("The path to the PLCC configuration file.");
            
            return true;
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

        jTabbedPaneSettings = new javax.swing.JTabbedPane();
        jPanelSettingsGeneral = new javax.swing.JPanel();
        jLabelDefInputPath = new javax.swing.JLabel();
        jTextFieldDefInputPath = new javax.swing.JTextField();
        jButtonBrowseDefInputPath = new javax.swing.JButton();
        jLabelDefOutputPath = new javax.swing.JLabel();
        jTextFieldDefOutputPath = new javax.swing.JTextField();
        jButtonBrowseDefOutputPath = new javax.swing.JButton();
        jButtonSaveSettingsGeneral = new javax.swing.JButton();
        jPanelSettingsPaths = new javax.swing.JPanel();
        jLabelPathPlccJar = new javax.swing.JLabel();
        jTextFieldPathPlccJar = new javax.swing.JTextField();
        jLabelPathSplitPDBJar = new javax.swing.JLabel();
        jTextFieldPathSplitPDBJar = new javax.swing.JTextField();
        jLabelPathDssp = new javax.swing.JLabel();
        jTextFieldPathDssp = new javax.swing.JTextField();
        jButtonBrowsePlccJar = new javax.swing.JButton();
        jButtonBrowseSplitPDBJar = new javax.swing.JButton();
        jButtonBrowseDssp = new javax.swing.JButton();
        jLabelDownloadDsspInfo = new javax.swing.JLabel();
        jButtonSaveSettingsApplications = new javax.swing.JButton();
        jPanelSettingsInternet = new javax.swing.JPanel();
        jLabelPdbDownloadURL = new javax.swing.JLabel();
        jTextFieldPdbDownloadUrl = new javax.swing.JTextField();
        jLabelDsspDownloadUrl = new javax.swing.JLabel();
        jTextFieldDsspDownloadUrl = new javax.swing.JTextField();
        jCheckBoxPdbfileLowercase = new javax.swing.JCheckBox();
        jSeparatorPdbDssp = new javax.swing.JSeparator();
        jCheckBoxDsspfileLowercase = new javax.swing.JCheckBox();
        jButtonSaveSettingsInternet = new javax.swing.JButton();
        jPanelSettingsDatabase = new javax.swing.JPanel();
        jLabelDatabaseHost = new javax.swing.JLabel();
        jTextFieldDatabaseHost = new javax.swing.JTextField();
        jLabelDatabasePort = new javax.swing.JLabel();
        jTextFieldDatabasePort = new javax.swing.JTextField();
        jLabelDatabaseName = new javax.swing.JLabel();
        jTextFieldDatabaseName = new javax.swing.JTextField();
        jLabelDatabaseUsername = new javax.swing.JLabel();
        jTextFieldDatabaseUsername = new javax.swing.JTextField();
        jLabelDatabasePassword = new javax.swing.JLabel();
        jTextFieldDatabasePassword = new javax.swing.JTextField();
        jLabelNoteParsedFromConfigFile = new javax.swing.JLabel();
        jLabelNoteParsedFromConfigFile2 = new javax.swing.JLabel();
        jTextFieldConfigfilePlcc = new javax.swing.JTextField();
        jButtonParsePlccConfigDB = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VPG Settings");
        setName("frameSettings");
        setPreferredSize(new java.awt.Dimension(600, 500));
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jTabbedPaneSettings.setToolTipText("Shows Paths settings.");
        jTabbedPaneSettings.setName("Internet"); // NOI18N
        jTabbedPaneSettings.setNextFocusableComponent(jPanelSettingsGeneral);

        jPanelSettingsGeneral.setToolTipText("Shows general settings.");
        jPanelSettingsGeneral.setMinimumSize(new java.awt.Dimension(200, 100));
        jPanelSettingsGeneral.setName("General"); // NOI18N
        jPanelSettingsGeneral.setNextFocusableComponent(jPanelSettingsPaths);
        jPanelSettingsGeneral.setPreferredSize(new java.awt.Dimension(500, 400));

        jLabelDefInputPath.setText("Default input path");

        jTextFieldDefInputPath.setText("/home/ts/data/PDB/");
        jTextFieldDefInputPath.setToolTipText("The path that is used to check for PDB and DSSP files by default.");

        jButtonBrowseDefInputPath.setText("Browse...");
        jButtonBrowseDefInputPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseDefInputPathActionPerformed(evt);
            }
        });

        jLabelDefOutputPath.setText("Default output path");

        jTextFieldDefOutputPath.setText("/home/ts/vplg/");
        jTextFieldDefOutputPath.setToolTipText("The path where output files are written by default.");

        jButtonBrowseDefOutputPath.setText("Browse...");
        jButtonBrowseDefOutputPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseDefOutputPathActionPerformed(evt);
            }
        });

        jButtonSaveSettingsGeneral.setText("Save general settings");
        jButtonSaveSettingsGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveSettingsGeneralActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSettingsGeneralLayout = new javax.swing.GroupLayout(jPanelSettingsGeneral);
        jPanelSettingsGeneral.setLayout(jPanelSettingsGeneralLayout);
        jPanelSettingsGeneralLayout.setHorizontalGroup(
            jPanelSettingsGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSettingsGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSettingsGeneralLayout.createSequentialGroup()
                        .addGroup(jPanelSettingsGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldDefOutputPath, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                            .addComponent(jLabelDefOutputPath, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                            .addComponent(jLabelDefInputPath, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldDefInputPath, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelSettingsGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonBrowseDefInputPath)
                            .addComponent(jButtonBrowseDefOutputPath)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSettingsGeneralLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSaveSettingsGeneral)))
                .addContainerGap())
        );
        jPanelSettingsGeneralLayout.setVerticalGroup(
            jPanelSettingsGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsGeneralLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabelDefInputPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSettingsGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldDefInputPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowseDefInputPath))
                .addGap(18, 18, 18)
                .addComponent(jLabelDefOutputPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSettingsGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldDefOutputPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowseDefOutputPath))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
                .addComponent(jButtonSaveSettingsGeneral)
                .addContainerGap())
        );

        jTabbedPaneSettings.addTab("General", null, jPanelSettingsGeneral, "Shows general settings.");
        jPanelSettingsGeneral.getAccessibleContext().setAccessibleName("General");

        jPanelSettingsPaths.setToolTipText("Shows path settings.");
        jPanelSettingsPaths.setName("Paths"); // NOI18N
        jPanelSettingsPaths.setNextFocusableComponent(jPanelSettingsInternet);

        jLabelPathPlccJar.setText("Path to PLCC (plcc.jar, part of VPLG):");

        jTextFieldPathPlccJar.setText("/home/ts/software/vplg/plcc.jar");
        jTextFieldPathPlccJar.setToolTipText("The path to the PLCC jar file. PLCC is the core of VPLG, it computes and visualizes protein ligand graphs.");

        jLabelPathSplitPDBJar.setText("Path to SplitPDB (splitpdb.jar, part of VPLG):");

        jTextFieldPathSplitPDBJar.setText("/home/ts/software/vplg/splitpdb.jar");
        jTextFieldPathSplitPDBJar.setToolTipText("The path to the SplitPDB jar file. Allows you to generate DSSP files from PDB files which include several models on your computer with dsspcmbi.");

        jLabelPathDssp.setText("Path to DSSP (dsspcmbi or dsspcmbi.exe)");

        jTextFieldPathDssp.setText("/home/ts/software/dssp/dsspcmbi");
        jTextFieldPathDssp.setToolTipText("The path to the external tool dsspcmbi by Kabsch and Sander. Allows you to generate DSSP files from PDB files on your computer.");
        jTextFieldPathDssp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPathDsspActionPerformed(evt);
            }
        });

        jButtonBrowsePlccJar.setText("Browse...");
        jButtonBrowsePlccJar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowsePlccJarActionPerformed(evt);
            }
        });

        jButtonBrowseSplitPDBJar.setText("Browse...");
        jButtonBrowseSplitPDBJar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseSplitPDBJarActionPerformed(evt);
            }
        });

        jButtonBrowseDssp.setText("Browse...");
        jButtonBrowseDssp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseDsspActionPerformed(evt);
            }
        });

        jLabelDownloadDsspInfo.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabelDownloadDsspInfo.setText("DSSP can be downloaded from http://swift.cmbi.ru.nl/gv/dssp/");

        jButtonSaveSettingsApplications.setText("Save application settings");
        jButtonSaveSettingsApplications.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveSettingsApplicationsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSettingsPathsLayout = new javax.swing.GroupLayout(jPanelSettingsPaths);
        jPanelSettingsPaths.setLayout(jPanelSettingsPathsLayout);
        jPanelSettingsPathsLayout.setHorizontalGroup(
            jPanelSettingsPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsPathsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSettingsPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSettingsPathsLayout.createSequentialGroup()
                        .addGroup(jPanelSettingsPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldPathSplitPDBJar)
                            .addComponent(jTextFieldPathDssp)
                            .addComponent(jLabelPathDssp, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPathSplitPDBJar, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPathPlccJar, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldPathPlccJar, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelSettingsPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonBrowsePlccJar)
                            .addComponent(jButtonBrowseSplitPDBJar)
                            .addComponent(jButtonBrowseDssp))
                        .addGap(10, 10, 10))
                    .addGroup(jPanelSettingsPathsLayout.createSequentialGroup()
                        .addComponent(jLabelDownloadDsspInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSettingsPathsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSaveSettingsApplications)
                        .addContainerGap())))
        );
        jPanelSettingsPathsLayout.setVerticalGroup(
            jPanelSettingsPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsPathsLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabelPathPlccJar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSettingsPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPathPlccJar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowsePlccJar))
                .addGap(18, 18, 18)
                .addComponent(jLabelPathSplitPDBJar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSettingsPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPathSplitPDBJar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowseSplitPDBJar))
                .addGap(18, 18, 18)
                .addComponent(jLabelPathDssp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSettingsPathsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPathDssp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBrowseDssp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDownloadDsspInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                .addComponent(jButtonSaveSettingsApplications)
                .addContainerGap())
        );

        jTabbedPaneSettings.addTab("Applications", null, jPanelSettingsPaths, "Shows path settings.");
        jPanelSettingsPaths.getAccessibleContext().setAccessibleName("Internet");

        jPanelSettingsInternet.setName("Internet"); // NOI18N
        jPanelSettingsInternet.setNextFocusableComponent(jPanelSettingsGeneral);

        jLabelPdbDownloadURL.setText("PDB file download URL (<PDBID> becomes something like 8ICD):");

        jTextFieldPdbDownloadUrl.setText("http://www.rcsb.org/pdb/files/<PDBID>.pdb.gz");
        jTextFieldPdbDownloadUrl.setToolTipText("The template URL to download PDB files.");
        jTextFieldPdbDownloadUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPdbDownloadUrlActionPerformed(evt);
            }
        });

        jLabelDsspDownloadUrl.setText("DSSP file download URL (<DSSPID> becomes something like 8ICD):");

        jTextFieldDsspDownloadUrl.setText("ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp/<DSSPID>.dssp");
        jTextFieldDsspDownloadUrl.setToolTipText("The template URL to dowload DSSP files.");

        jCheckBoxPdbfileLowercase.setText("PDB ID is lowercase on server");
        jCheckBoxPdbfileLowercase.setToolTipText("If this is selected, the PDB ID will be converted to lowercase in the URL.");

        jCheckBoxDsspfileLowercase.setSelected(true);
        jCheckBoxDsspfileLowercase.setText("DSSP ID is lowercase on server");
        jCheckBoxDsspfileLowercase.setToolTipText("If this is selected, the PDB ID will be converted to lowercase in the URL.");

        jButtonSaveSettingsInternet.setText("Save web services settings");
        jButtonSaveSettingsInternet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveSettingsInternetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSettingsInternetLayout = new javax.swing.GroupLayout(jPanelSettingsInternet);
        jPanelSettingsInternet.setLayout(jPanelSettingsInternetLayout);
        jPanelSettingsInternetLayout.setHorizontalGroup(
            jPanelSettingsInternetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsInternetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSettingsInternetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxDsspfileLowercase)
                    .addComponent(jCheckBoxPdbfileLowercase)
                    .addComponent(jTextFieldPdbDownloadUrl)
                    .addComponent(jLabelPdbDownloadURL)
                    .addComponent(jSeparatorPdbDssp)
                    .addComponent(jLabelDsspDownloadUrl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                    .addComponent(jTextFieldDsspDownloadUrl)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSettingsInternetLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonSaveSettingsInternet)))
                .addContainerGap())
        );
        jPanelSettingsInternetLayout.setVerticalGroup(
            jPanelSettingsInternetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsInternetLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabelPdbDownloadURL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldPdbDownloadUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxPdbfileLowercase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparatorPdbDssp, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDsspDownloadUrl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDsspDownloadUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDsspfileLowercase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                .addComponent(jButtonSaveSettingsInternet)
                .addContainerGap())
        );

        jTabbedPaneSettings.addTab("Web services", null, jPanelSettingsInternet, "Shows internet settings.");
        jPanelSettingsInternet.getAccessibleContext().setAccessibleName("Internet");
        jPanelSettingsInternet.getAccessibleContext().setAccessibleDescription("Shows internet settings.");

        jLabelDatabaseHost.setText("PostgreSQL Database server (IP address or hostname):");

        jTextFieldDatabaseHost.setBackground(new java.awt.Color(200, 200, 200));
        jTextFieldDatabaseHost.setEditable(false);
        jTextFieldDatabaseHost.setText("127.0.0.1");
        jTextFieldDatabaseHost.setToolTipText("Database server IP or hostname, e.g., 127.0.0.1 or pgsql.mydomain.edu");

        jLabelDatabasePort.setText("Database port (PostgreSQL default is 5432):");

        jTextFieldDatabasePort.setBackground(new java.awt.Color(200, 200, 200));
        jTextFieldDatabasePort.setEditable(false);
        jTextFieldDatabasePort.setText("5432");
        jTextFieldDatabasePort.setToolTipText("Database port, PostgreSQL uses 5432 by default.");

        jLabelDatabaseName.setText("Database name:");

        jTextFieldDatabaseName.setBackground(new java.awt.Color(200, 200, 200));
        jTextFieldDatabaseName.setEditable(false);
        jTextFieldDatabaseName.setText("vplg");
        jTextFieldDatabaseName.setToolTipText("The name of the database you created for VPLG.");

        jLabelDatabaseUsername.setText("Database username:");

        jTextFieldDatabaseUsername.setBackground(new java.awt.Color(200, 200, 200));
        jTextFieldDatabaseUsername.setEditable(false);
        jTextFieldDatabaseUsername.setText("vplg");
        jTextFieldDatabaseUsername.setToolTipText("The database username for VPLG.");

        jLabelDatabasePassword.setText("Database password:");

        jTextFieldDatabasePassword.setBackground(new java.awt.Color(200, 200, 200));
        jTextFieldDatabasePassword.setEditable(false);
        jTextFieldDatabasePassword.setToolTipText("The database password for the user given above.");

        jLabelNoteParsedFromConfigFile.setText("These settings are always read from the plcc configuration file at:");

        jLabelNoteParsedFromConfigFile2.setText("Edit that file to change them. Also note that you will need to create DB structure");

        jTextFieldConfigfilePlcc.setBackground(new java.awt.Color(200, 200, 200));
        jTextFieldConfigfilePlcc.setEditable(false);
        jTextFieldConfigfilePlcc.setText("/home/ts/.plcc_settings");
        jTextFieldConfigfilePlcc.setToolTipText("The path to the PLCC configuration file.");

        jButtonParsePlccConfigDB.setText("Reparse PLCC config file");
        jButtonParsePlccConfigDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonParsePlccConfigDBActionPerformed(evt);
            }
        });

        jLabel1.setText("before using the database for the first time. Run 'java -jar plcc.jar -r' to do this.");

        javax.swing.GroupLayout jPanelSettingsDatabaseLayout = new javax.swing.GroupLayout(jPanelSettingsDatabase);
        jPanelSettingsDatabase.setLayout(jPanelSettingsDatabaseLayout);
        jPanelSettingsDatabaseLayout.setHorizontalGroup(
            jPanelSettingsDatabaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsDatabaseLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSettingsDatabaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSettingsDatabaseLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonParsePlccConfigDB))
                    .addGroup(jPanelSettingsDatabaseLayout.createSequentialGroup()
                        .addGroup(jPanelSettingsDatabaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelDatabaseHost)
                            .addComponent(jTextFieldDatabaseHost, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDatabasePort)
                            .addComponent(jTextFieldDatabasePort, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDatabaseName)
                            .addComponent(jTextFieldDatabaseName, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDatabaseUsername)
                            .addComponent(jTextFieldDatabaseUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDatabasePassword)
                            .addComponent(jTextFieldDatabasePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelNoteParsedFromConfigFile)
                            .addComponent(jTextFieldConfigfilePlcc, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelNoteParsedFromConfigFile2)
                            .addComponent(jLabel1))
                        .addGap(0, 1, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelSettingsDatabaseLayout.setVerticalGroup(
            jPanelSettingsDatabaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingsDatabaseLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabelDatabaseHost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDatabaseHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDatabasePort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDatabasePort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDatabaseName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDatabaseName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDatabaseUsername)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDatabaseUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDatabasePassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDatabasePassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabelNoteParsedFromConfigFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldConfigfilePlcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelNoteParsedFromConfigFile2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonParsePlccConfigDB)
                .addContainerGap())
        );

        jTabbedPaneSettings.addTab("Database", jPanelSettingsDatabase);

        getContentPane().add(jTabbedPaneSettings);
        jTabbedPaneSettings.getAccessibleContext().setAccessibleName("Internet");

        getAccessibleContext().setAccessibleDescription("The VPG Settings");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldPathDsspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPathDsspActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPathDsspActionPerformed

    private void jTextFieldPdbDownloadUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPdbDownloadUrlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPdbDownloadUrlActionPerformed

    private void jButtonBrowseDefInputPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseDefInputPathActionPerformed
        
        File defaultDir = new File(Settings.get("vpg_S_input_dir"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldDefInputPath.setText(fc.getSelectedFile().toString());
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
        }
    }//GEN-LAST:event_jButtonBrowseDefInputPathActionPerformed

    private void jButtonBrowseDefOutputPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseDefOutputPathActionPerformed
        
        File defaultDir = new File(Settings.get("vpg_S_output_dir"));
        JFileChooser fc = new JFileChooser(defaultDir);        
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldDefOutputPath.setText(fc.getSelectedFile().toString());
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
        }
    }//GEN-LAST:event_jButtonBrowseDefOutputPathActionPerformed

    private void jButtonBrowsePlccJarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowsePlccJarActionPerformed
        File defaultDir = new File(Settings.get("vpg_S_path_plcc"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldPathPlccJar.setText(fc.getSelectedFile().toString());
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
        }
    }//GEN-LAST:event_jButtonBrowsePlccJarActionPerformed

    private void jButtonBrowseSplitPDBJarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseSplitPDBJarActionPerformed

        File defaultDir = new File(Settings.get("vpg_S_path_splitpdb"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldPathSplitPDBJar.setText(fc.getSelectedFile().toString());
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
        }
    }//GEN-LAST:event_jButtonBrowseSplitPDBJarActionPerformed

    private void jButtonBrowseDsspActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseDsspActionPerformed

        File defaultDir = new File(Settings.get("vpg_S_path_dssp"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldPathDssp.setText(fc.getSelectedFile().toString());
        }
        if (rVal == JFileChooser.CANCEL_OPTION) {
        }
    }//GEN-LAST:event_jButtonBrowseDsspActionPerformed

    private void jButtonSaveSettingsGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveSettingsGeneralActionPerformed

        Settings.set("vpg_S_output_dir", this.jTextFieldDefOutputPath.getText());
        Settings.set("vpg_S_input_dir", this.jTextFieldDefInputPath.getText());
        
        if(Settings.writeToFile("")) {
            System.out.println("[VPG] General settings saved.");
        }
    }//GEN-LAST:event_jButtonSaveSettingsGeneralActionPerformed

    private void jButtonSaveSettingsApplicationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveSettingsApplicationsActionPerformed
        
        Settings.set("vpg_S_path_dssp", this.jTextFieldPathDssp.getText());
        Settings.set("vpg_S_path_splitpdb", this.jTextFieldPathSplitPDBJar.getText());
        Settings.set("vpg_S_path_plcc", this.jTextFieldPathPlccJar.getText());
        
        if(Settings.writeToFile("")) {
            System.out.println("[VPG] Application settings saved.");
        }
    }//GEN-LAST:event_jButtonSaveSettingsApplicationsActionPerformed

    private void jButtonSaveSettingsInternetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveSettingsInternetActionPerformed
        // TODO add your handling code here:
        
        Settings.set("vpg_S_download_pdbfile_URL", this.jTextFieldPdbDownloadUrl.getText());
        Settings.set("vpg_S_download_dsspfile_URL", this.jTextFieldDsspDownloadUrl.getText());
        
        if(this.jCheckBoxPdbfileLowercase.isSelected()) {
            Settings.set("vpg_B_download_pdbid_is_lowercase", "true");
        }
        
        if(this.jCheckBoxDsspfileLowercase.isSelected()) {
            Settings.set("vpg_B_download_dsspid_is_lowercase", "true");
        }
        
        if(Settings.writeToFile("")) {
            System.out.println("[VPG] Internet settings saved.");
        }
        
    }//GEN-LAST:event_jButtonSaveSettingsInternetActionPerformed

    private void jButtonParsePlccConfigDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonParsePlccConfigDBActionPerformed

        this.parsePlccConfig();
    }//GEN-LAST:event_jButtonParsePlccConfigDBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(VpgSettingsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VpgSettingsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VpgSettingsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VpgSettingsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override public void run() {
                VpgSettingsFrame s = new VpgSettingsFrame();                
                s.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseDefInputPath;
    private javax.swing.JButton jButtonBrowseDefOutputPath;
    private javax.swing.JButton jButtonBrowseDssp;
    private javax.swing.JButton jButtonBrowsePlccJar;
    private javax.swing.JButton jButtonBrowseSplitPDBJar;
    private javax.swing.JButton jButtonParsePlccConfigDB;
    private javax.swing.JButton jButtonSaveSettingsApplications;
    private javax.swing.JButton jButtonSaveSettingsGeneral;
    private javax.swing.JButton jButtonSaveSettingsInternet;
    private javax.swing.JCheckBox jCheckBoxDsspfileLowercase;
    private javax.swing.JCheckBox jCheckBoxPdbfileLowercase;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelDatabaseHost;
    private javax.swing.JLabel jLabelDatabaseName;
    private javax.swing.JLabel jLabelDatabasePassword;
    private javax.swing.JLabel jLabelDatabasePort;
    private javax.swing.JLabel jLabelDatabaseUsername;
    private javax.swing.JLabel jLabelDefInputPath;
    private javax.swing.JLabel jLabelDefOutputPath;
    private javax.swing.JLabel jLabelDownloadDsspInfo;
    private javax.swing.JLabel jLabelDsspDownloadUrl;
    private javax.swing.JLabel jLabelNoteParsedFromConfigFile;
    private javax.swing.JLabel jLabelNoteParsedFromConfigFile2;
    private javax.swing.JLabel jLabelPathDssp;
    private javax.swing.JLabel jLabelPathPlccJar;
    private javax.swing.JLabel jLabelPathSplitPDBJar;
    private javax.swing.JLabel jLabelPdbDownloadURL;
    private javax.swing.JPanel jPanelSettingsDatabase;
    private javax.swing.JPanel jPanelSettingsGeneral;
    private javax.swing.JPanel jPanelSettingsInternet;
    private javax.swing.JPanel jPanelSettingsPaths;
    private javax.swing.JSeparator jSeparatorPdbDssp;
    private javax.swing.JTabbedPane jTabbedPaneSettings;
    private javax.swing.JTextField jTextFieldConfigfilePlcc;
    private javax.swing.JTextField jTextFieldDatabaseHost;
    private javax.swing.JTextField jTextFieldDatabaseName;
    private javax.swing.JTextField jTextFieldDatabasePassword;
    private javax.swing.JTextField jTextFieldDatabasePort;
    private javax.swing.JTextField jTextFieldDatabaseUsername;
    private javax.swing.JTextField jTextFieldDefInputPath;
    private javax.swing.JTextField jTextFieldDefOutputPath;
    private javax.swing.JTextField jTextFieldDsspDownloadUrl;
    private javax.swing.JTextField jTextFieldPathDssp;
    private javax.swing.JTextField jTextFieldPathPlccJar;
    private javax.swing.JTextField jTextFieldPathSplitPDBJar;
    private javax.swing.JTextField jTextFieldPdbDownloadUrl;
    // End of variables declaration//GEN-END:variables
}
