/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */

package vpg;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A frame to create a protein graph from local input files.
 * @author ts
 */
public class VpgCreateGraphFrame extends javax.swing.JFrame implements ItemListener, DocumentListener {
           
    
    @Override public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        if (source == this.jCheckBoxAdditionalOutput) {            
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ Additional output");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- Additional output");                
            }
            */
            this.setCommandTextField();
        } 
        else if (source == this.jCheckBoxAllowCoils) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ Coils");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- Coils");                
            }
            */
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxCustomOutputDir) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                //System.out.println("+++ Custom output dir");
                this.jTextFieldCustomOutputDir.setEnabled(true);
                this.jTextFieldCustomOutputDir.setToolTipText("Currently using the custom directory from this field.");
                this.jButtonSelectCustomOutputDir.setEnabled(true);
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                //System.out.println("---  Custom output dir");
                this.jTextFieldCustomOutputDir.setEnabled(false);
                this.jTextFieldCustomOutputDir.setToolTipText("Currently using default directory '" + Settings.get("vpg_S_output_dir") + "'.");
                this.jButtonSelectCustomOutputDir.setEnabled(false);
                //this.jTextFieldCustomOutputDir.setText(Settings.get("vpg_S_output_dir"));
            }
            //this.inputFilesOK();
            this.checkInput();
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxDsspFileIsGzipped) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ DSSP file is gzipped");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- DSSP file is gzipped");                
            }
            */
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxPdbFileIsGzipped) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ PDB file is gzipped");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- PDB file is gzipped");                
            }
            */
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxFoldingGraphs) {
            
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.jCheckBoxFoldingGraphsKEY.setEnabled(true);
                this.jCheckBoxFoldingGraphsRED.setEnabled(true);                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                this.jCheckBoxFoldingGraphsKEY.setEnabled(false);
                this.jCheckBoxFoldingGraphsRED.setEnabled(false);                
            }
            
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxRamachandranPlot) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ Ramaplot");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- Ramaplot");                
            }
            */
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxForceChain) {
            
            if (e.getStateChange() == ItemEvent.SELECTED) {
                //System.out.println("+++ Force chain");                
                this.jTextFieldForceChain.setEnabled(true);
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                //System.out.println("--- Force chain");                
                this.jTextFieldForceChain.setEnabled(false);
            }
            
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxOutputDirTree) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ Output dir tree");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- Output dir tree");                
            }
            */
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxUseDatabase) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ Use database");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- Use database");                
            }
            */
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxPGtypes) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                //System.out.println("+++ Limit PG types");
                this.jCheckBoxPGalpha.setEnabled(true);
                this.jCheckBoxPGbeta.setEnabled(true);
                this.jCheckBoxPGalbe.setEnabled(true);
                this.jCheckBoxPGalphalig.setEnabled(true);
                this.jCheckBoxPGbetalig.setEnabled(true);
                this.jCheckBoxPGalbelig.setEnabled(true);
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                //System.out.println("--- Limit PG types");
                this.jCheckBoxPGalpha.setEnabled(false);
                this.jCheckBoxPGbeta.setEnabled(false);
                this.jCheckBoxPGalbe.setEnabled(false);
                this.jCheckBoxPGalphalig.setEnabled(false);
                this.jCheckBoxPGbetalig.setEnabled(false);
                this.jCheckBoxPGalbelig.setEnabled(false);
            }
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxPGalpha) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ PG type alpha");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- PG type alpha");                
            }
            */
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxPGbeta) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ PG type beta");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- PG type beta");                
            }
            */
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxPGalbe) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ PG type albe");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- PG type albe");                
            }
            */ 
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxPGalphalig) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ PG type alphalig");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- PG type alphalig");                
            }
            */
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxPGbetalig) {
            /**
            if (e.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("+++ PG type betalig");                
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                System.out.println("--- PG type betalig");                
            }
            */ 
            this.setCommandTextField();
        }
        else if(source == this.jCheckBoxPGalbelig) {
            //if (e.getStateChange() == ItemEvent.SELECTED) {
            //    System.out.println("+++ PG type albelig");                
            //} else if(e.getStateChange() == ItemEvent.DESELECTED) {
            //    System.out.println("--- PG type albelig");                
            //}
            this.setCommandTextField();
        }
        else if(source == this.jComboBoxImageFormat) {
            //System.out.println("=== Image format");                            
            this.setCommandTextField();
        } else if(source == this.jCheckBoxBackboneContacts) {
            this.setCommandTextField();
        } 
        else if(source == this.jCheckBoxLimitLigandAtoms) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.jTextFieldSetLigAtomsMin.setEnabled(true);
                this.jTextFieldSetLigAtomsMax.setEnabled(true);
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                this.jTextFieldSetLigAtomsMin.setEnabled(false);
                this.jTextFieldSetLigAtomsMax.setEnabled(false);
            }
            
            this.setCommandTextField();
        }
        else if (source == this.jCheckBoxGraphFormatDOT) {
            this.setCommandTextField();
        }
        else if (source == this.jCheckBoxGraphFormatGML) {
            this.setCommandTextField();
        }
        else if (source == this.jCheckBoxGraphFormatTGF) {
            this.setCommandTextField();
        }
        else if (source == this.jCheckBoxGraphFormatEdgeList) {
            this.setCommandTextField();
        }
        else if (source == this.jCheckBoxGraphFormatPlcc) {
            this.setCommandTextField();
        }
        else {
            //System.err.println(Settings.getApptag() + "WARNING: VpgCreateGraphFrame: Event from source " + source.toString() + " ignored.");
        }
    }
    
    /**
     * Item listener implementation.
     * @param e the DocumentEvent to handle
     */
    @Override public void changedUpdate(DocumentEvent e) {
        //this.inputFilesOK();
        this.setZippedCheckbox(e);
        this.checkInput();                
    }
    
    /**
     * Item listener implementation.
     * @param e the DocumentEvent to handle
     */
    @Override public void removeUpdate(DocumentEvent e) {
        //this.inputFilesOK();
        this.setZippedCheckbox(e);
        this.checkInput();
        //System.out.println("remove");
    }    
    
    /**
     * Item listener implementation.
     * @param e the DocumentEvent to handle
     */
    @Override public void insertUpdate(DocumentEvent e) {
        //this.inputFilesOK();
        this.setZippedCheckbox(e);
        this.checkInput();
        //System.out.println("insert");
    }
    
    private void setZippedCheckbox(DocumentEvent e) {
        if(e.getDocument() == this.jTextFieldInputFileDSSP.getDocument()) {
            //System.out.println("change DSSP");
            if(this.jTextFieldInputFileDSSP.getText().endsWith(".gz")) {
                this.jCheckBoxDsspFileIsGzipped.setSelected(true);
            } else {
                this.jCheckBoxDsspFileIsGzipped.setSelected(false);
            }
        } else if(e.getDocument() == this.jTextFieldInputFilePDB.getDocument()) {
            //System.out.println("change PDB");
            if(this.jTextFieldInputFilePDB.getText().endsWith(".gz")) {
                this.jCheckBoxPdbFileIsGzipped.setSelected(true);
            } else {
                this.jCheckBoxPdbFileIsGzipped.setSelected(false);
            }
        } else {
            //System.out.println("change other");
        }        
    }
    

    /**
     * Checks whether the input files exist and are readable.
     * @return whether they are ok
     */
    public Boolean inputFilesOK() {
        Boolean ok = true;
        
        File pdbFile = new File(this.jTextFieldInputFilePDB.getText());
        if( ! (pdbFile.isFile() && pdbFile.canRead())) {
            System.err.println(Settings.getApptag() + "WARNING: Cannot read input PDB file at path '" + pdbFile.getAbsolutePath() + "'.");
            this.jTextFieldInputFilePDB.setBackground(Color.RED);
            ok = false;
        } else {
            this.jTextFieldInputFilePDB.setBackground(Color.WHITE);
        }
        
        File dsspFile = new File(this.jTextFieldInputFileDSSP.getText());
        if( ! (dsspFile.isFile() && dsspFile.canRead())) {
            System.err.println(Settings.getApptag() + "WARNING: Cannot read input DSSP file at path '" + dsspFile.getAbsolutePath() + "'.");
            this.jTextFieldInputFileDSSP.setBackground(Color.RED);
            ok = false;
        } else {
            this.jTextFieldInputFileDSSP.setBackground(Color.WHITE);
        } 
        
        File outDir = new File(this.getOutputDirFromForm());
        if( ! (outDir.isDirectory() && outDir.canWrite())) {
            System.err.println(Settings.getApptag() + "WARNING: Cannot write to output directory '" + outDir.getAbsolutePath() + "' or is does not even exist.");
            
            if(this.jCheckBoxCustomOutputDir.isSelected()) {
                this.jTextFieldCustomOutputDir.setBackground(Color.RED);
            } else {
                this.jCheckBoxAdditionalOutput.setBackground(Color.RED);
            }
            
            ok = false;
        } else {
            
            this.jTextFieldCustomOutputDir.setBackground(Color.WHITE);
            
            this.jCheckBoxAdditionalOutput.setBackground(Color.WHITE);
            
        }
        
        
        if(this.jCheckBoxLimitLigandAtoms.isSelected()) {
            Integer atmMin = 0;
            Integer atmMax = 0;
            Boolean minIsInteger = true;
            Boolean maxIsInteger = true;
            
            String tooltipMin = "The minimum number of atoms a ligand molecule must have to be considered. Set to zero to disable.";
            String tooltipMax = "The maximum number of atoms a ligand molecule must have to be considered. Set to zero to disable.";
            
            try {
                atmMin = Integer.parseInt(this.jTextFieldSetLigAtomsMin.getText());                                
                this.jTextFieldSetLigAtomsMin.setBackground(Color.WHITE);
                this.jTextFieldSetLigAtomsMin.setToolTipText(tooltipMin);
            } catch (Exception e) {
                this.jTextFieldSetLigAtomsMin.setBackground(Color.RED);
                this.jTextFieldSetLigAtomsMin.setToolTipText("Minimum atom number for ligands must be numeric / an integer.");
                System.err.println(Settings.getApptag() + "WARNING: Minimum atom number for ligands must be numeric / an integer.");
                minIsInteger = false;
                ok = false;
            }
            
            try {
                atmMax = Integer.parseInt(this.jTextFieldSetLigAtomsMax.getText());
                this.jTextFieldSetLigAtomsMax.setBackground(Color.WHITE);
                this.jTextFieldSetLigAtomsMax.setToolTipText(tooltipMax);
            } catch (Exception e) {
                this.jTextFieldSetLigAtomsMax.setBackground(Color.RED);
                this.jTextFieldSetLigAtomsMax.setToolTipText("Maximum atom number for ligands must be numeric / an integer.");
                System.err.println(Settings.getApptag() + "WARNING: Maximum atom number for ligands must be numeric / an integer.");
                ok = false;
                maxIsInteger = false;
            }
            
            if(minIsInteger && maxIsInteger) {
                if( (atmMin > atmMax) && (atmMax != 0) ) {
                    ok = false;
                    this.jTextFieldSetLigAtomsMax.setBackground(Color.RED);
                    this.jTextFieldSetLigAtomsMin.setToolTipText("Minimum ligand atom number is greater than maximum ligand atom number (set to zero to disable one).");
                    this.jTextFieldSetLigAtomsMin.setBackground(Color.RED);
                    this.jTextFieldSetLigAtomsMax.setToolTipText("Minimum ligand atom number is greater than maximum ligand atom number (set to zero to disable one).");
                    System.err.println(Settings.getApptag() + "WARNING: Minimum ligand atom number is greater than maximum ligand atom number.");
                } else {
                    
                    if(atmMin < 0 || atmMax < 0) {
                        ok = false;
                        this.jTextFieldSetLigAtomsMax.setBackground(Color.RED);
                        this.jTextFieldSetLigAtomsMin.setToolTipText("Both minimum and maximum ligand atom numbers must be zero or greater (set to zero to disable).");
                        this.jTextFieldSetLigAtomsMin.setBackground(Color.RED);
                        this.jTextFieldSetLigAtomsMax.setToolTipText("Both minimum and maximum ligand atom numbers must be zero or greater (set to zero to disable).");
                        System.err.println(Settings.getApptag() + "WARNING: Both minimum and maximum ligand atom numbers must be zero or greater (set to zero to disable).");
                        
                    } else {                    
                        this.jTextFieldSetLigAtomsMax.setBackground(Color.WHITE);
                        this.jTextFieldSetLigAtomsMax.setToolTipText(tooltipMax);
                        this.jTextFieldSetLigAtomsMin.setBackground(Color.WHITE);
                        this.jTextFieldSetLigAtomsMin.setToolTipText(tooltipMin);                    
                    }
                }
            }
        }
        
        return ok;        
    }
    
    /**
     * Tries to determine the PDB ID from the input file name.
     * @return the PDBID if it succeeds, a null String otherwise
     * @see VpgMassGraphProcessingFrame.determinePdbidFromPdbFilename()
     */
    public String determinePDBID() {
        File f = new File(this.jTextFieldInputFilePDB.getText());        
        return VpgMassGraphProcessingFrame.determinePdbidFromPdbFilename(f.getName());        
    }
    

    
    private ArrayList<String> getNonEssentialOptions(ArrayList<String> cmdList) {                
        
        if(cmdList == null) {
            cmdList = new ArrayList<String>();
        }
        
        // --------------- all non-essential options ---------------
        
        if(this.jCheckBoxAdditionalOutput.isSelected()) {
            cmdList.add("--textfiles");
        }
        
        if(this.jCheckBoxAllowCoils.isSelected()) {
            cmdList.add("--include-coils");
        }
        
        if(this.jCheckBoxFoldingGraphs.isSelected()) {
            cmdList.add("--folding-graphs");
            cmdList.add("--fg-notations");
            String gtypes = "";
            
            if(this.jCheckBoxFoldingGraphsKEY.isSelected()) { gtypes += "k"; }
            if(this.jCheckBoxFoldingGraphsRED.isSelected()) { gtypes += "r"; }
            
            if(gtypes.isEmpty()) {
                System.err.println(Settings.getApptag() + "WARNING: FGs active but no types selected, assuming RED.");
                gtypes = "r";
                this.jCheckBoxFoldingGraphsRED.setSelected(true);
            }
            cmdList.add(gtypes);
        }
        
        if(this.jCheckBoxForceChain.isSelected()) {
            cmdList.add("--force-chain");
            cmdList.add(this.jTextFieldForceChain.getText());
        }
        
        if(this.jCheckBoxRamachandranPlot.isSelected()) {
            cmdList.add("--ramaplot");
        }
        
        if(this.jCheckBoxUseDatabase.isSelected()) {
            cmdList.add("--use-database");
        }
        
        if(this.jCheckBoxBackboneContacts.isSelected()) {
            cmdList.add("--force-backbone");
        }
        
        
        if(this.jCheckBoxOutputDirTree.isSelected()) {
            //cmdList.add("--img-dir-tree");
            cmdList.add("--output-subdir-tree");
            
        }
        
        cmdList.add("--image-format");
        cmdList.add(this.jComboBoxImageFormat.getSelectedItem().toString());
        
        if(this.jCheckBoxPGtypes.isSelected()) {
            cmdList.add("--sse-graphtypes");
            String gtypes = "";
            
            if(this.jCheckBoxPGalpha.isSelected()) { gtypes += "a"; }
            if(this.jCheckBoxPGbeta.isSelected()) { gtypes += "b"; }
            if(this.jCheckBoxPGalbe.isSelected()) { gtypes += "c"; }
            if(this.jCheckBoxPGalphalig.isSelected()) { gtypes += "d"; }
            if(this.jCheckBoxPGbetalig.isSelected()) { gtypes += "e"; }
            if(this.jCheckBoxPGalbelig.isSelected()) { gtypes += "f"; }
            
            if(gtypes.isEmpty()) {
                System.err.println(Settings.getApptag() + "WARNING: PG types limited but no protein graph type selected, assuming albelig.");
                gtypes = "f";
                this.jCheckBoxPGalbelig.setSelected(true);
            }
            cmdList.add(gtypes);
        }
        
        if(this.jCheckBoxLimitLigandAtoms.isSelected()) {
            cmdList.add("--lig-filter");
            cmdList.add(this.jTextFieldSetLigAtomsMin.getText());
            cmdList.add(this.jTextFieldSetLigAtomsMax.getText());
        }
        
        cmdList.add("--outputformats");
        String formatList = "";
        Boolean noneSelected = true;
        if(this.jCheckBoxGraphFormatDOT.isSelected()) {
            noneSelected = false;
            formatList += "d";
        }
        if(this.jCheckBoxGraphFormatEdgeList.isSelected()) {
            noneSelected = false;
            formatList += "e";
        }
        if(this.jCheckBoxGraphFormatGML.isSelected()) {
            noneSelected = false;
            formatList += "g";
        }
        if(this.jCheckBoxGraphFormatTGF.isSelected()) {
            noneSelected = false;
            formatList += "t";
        }
        if(this.jCheckBoxGraphFormatPlcc.isSelected()) {
            noneSelected = false;
            formatList += "p";
        }
        if(noneSelected) {
            formatList += "x";
        }
        cmdList.add(formatList);
        
        return cmdList;
    }
    
    /**
     * Constructs the command array from the form data.
     * @return the command array
     */
    public String[] getCommandArray() {
        ArrayList<String> cmdList = new ArrayList<String>();
        
        cmdList.add(Settings.get("vpg_S_java_command"));
        cmdList.add("-jar");
        cmdList.add(Settings.get("vpg_S_path_plcc"));
        cmdList.add(this.determinePDBID());
        
        // PDB input file
        if(this.jCheckBoxPdbFileIsGzipped.isSelected()) {
            cmdList.add("--gz-pdbfile");
        } else {
            cmdList.add("--pdbfile");
        }
        cmdList.add(this.jTextFieldInputFilePDB.getText());
          
        // DSSP input file
        if(this.jCheckBoxDsspFileIsGzipped.isSelected()) {
            cmdList.add("--gz-dsspfile");
        } else {
            cmdList.add("--dsspfile");
        }
        cmdList.add(this.jTextFieldInputFileDSSP.getText());
        
        // output directory
        cmdList.add("--outputdir");
        cmdList.add(this.getOutputDirFromForm());       
        
        // add all non-essential options (not related to input and output)
        cmdList = getNonEssentialOptions(cmdList);                                


        return cmdList.toArray(new String[cmdList.size()]);
    }
    
    private String getOutputDirFromForm() {
        if(this.jCheckBoxCustomOutputDir.isSelected()) {
            return(this.jTextFieldCustomOutputDir.getText());
        } else {
            return(Settings.get("vpg_S_output_dir"));
        }
    }
    

    /**
     * Creates new form VpgCreateGraphFrame
     */
    public VpgCreateGraphFrame() {
        initComponents();
        
        String fs = System.getProperty("file.separator");
        
        this.jTextFieldInputFilePDB.setText(Settings.get("vpg_S_input_dir") + fs + "8icd.pdb");
        this.jTextFieldInputFileDSSP.setText(Settings.get("vpg_S_input_dir") + fs + "8icd.dssp");
        this.jTextFieldCustomOutputDir.setToolTipText("Currently using default directory '" + Settings.get("vpg_S_output_dir") + "'.");
        this.jTextFieldCustomOutputDir.setText(Settings.get("vpg_S_last_custom_output_dir"));
        
        this.jCheckBoxAdditionalOutput.addItemListener(this);
        this.jCheckBoxAllowCoils.addItemListener(this);
        this.jCheckBoxDsspFileIsGzipped.addItemListener(this);
        this.jCheckBoxFoldingGraphs.addItemListener(this);
        this.jCheckBoxOutputDirTree.addItemListener(this);
        this.jCheckBoxPdbFileIsGzipped.addItemListener(this);
        this.jCheckBoxRamachandranPlot.addItemListener(this);
        this.jCheckBoxUseDatabase.addItemListener(this);
        this.jCheckBoxForceChain.addItemListener(this);
        this.jComboBoxImageFormat.addItemListener(this);
        this.jCheckBoxCustomOutputDir.addItemListener(this);
        
        this.jCheckBoxPGtypes.addItemListener(this);
        this.jCheckBoxPGalpha.addItemListener(this);
        this.jCheckBoxPGbeta.addItemListener(this);
        this.jCheckBoxPGalbe.addItemListener(this);
        this.jCheckBoxPGalphalig.addItemListener(this);
        this.jCheckBoxPGbetalig.addItemListener(this);
        this.jCheckBoxPGalbelig.addItemListener(this);
        this.jCheckBoxBackboneContacts.addItemListener(this);
        this.jComboBoxImageFormat.addItemListener(this);
        this.jCheckBoxFoldingGraphsKEY.addItemListener(this);
        this.jCheckBoxFoldingGraphsRED.addItemListener(this);
        this.jCheckBoxLimitLigandAtoms.addItemListener(this);
        
        this.jCheckBoxGraphFormatDOT.addItemListener(this);
        this.jCheckBoxGraphFormatEdgeList.addItemListener(this);
        this.jCheckBoxGraphFormatGML.addItemListener(this);
        this.jCheckBoxGraphFormatTGF.addItemListener(this);
        this.jCheckBoxGraphFormatPlcc.addItemListener(this);
        
        this.jTextFieldInputFilePDB.getDocument().addDocumentListener(this);
        this.jTextFieldInputFileDSSP.getDocument().addDocumentListener(this);
        this.jTextFieldCustomOutputDir.getDocument().addDocumentListener(this);
        
        this.jTextFieldSetLigAtomsMax.getDocument().addDocumentListener(this);
        this.jTextFieldSetLigAtomsMin.getDocument().addDocumentListener(this);

        
        
        this.setCommandTextField();
             
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMainContent = new javax.swing.JPanel();
        jLabelInputPDB = new javax.swing.JLabel();
        jTextFieldInputFilePDB = new javax.swing.JTextField();
        jLabelInputDSSP = new javax.swing.JLabel();
        jTextFieldInputFileDSSP = new javax.swing.JTextField();
        jButtonSelectFilePDB = new javax.swing.JButton();
        jButtonSelectFileDSSP = new javax.swing.JButton();
        jSeparatorUpper = new javax.swing.JSeparator();
        jLabelOptions = new javax.swing.JLabel();
        jSeparatorLower = new javax.swing.JSeparator();
        jButtonRun = new javax.swing.JButton();
        jCheckBoxPdbFileIsGzipped = new javax.swing.JCheckBox();
        jCheckBoxDsspFileIsGzipped = new javax.swing.JCheckBox();
        jCheckBoxFoldingGraphs = new javax.swing.JCheckBox();
        jCheckBoxOutputDirTree = new javax.swing.JCheckBox();
        jCheckBoxUseDatabase = new javax.swing.JCheckBox();
        jLabelDebugOptions = new javax.swing.JLabel();
        jCheckBoxAdditionalOutput = new javax.swing.JCheckBox();
        jCheckBoxAllowCoils = new javax.swing.JCheckBox();
        jCheckBoxRamachandranPlot = new javax.swing.JCheckBox();
        jCheckBoxForceChain = new javax.swing.JCheckBox();
        jTextFieldForceChain = new javax.swing.JTextField();
        jLabelImageFormat = new javax.swing.JLabel();
        jComboBoxImageFormat = new javax.swing.JComboBox();
        jSeparatorCenter = new javax.swing.JSeparator();
        jLabelPlccCommand = new javax.swing.JLabel();
        jScrollPaneStatus = new javax.swing.JScrollPane();
        jTextPaneStatus = new javax.swing.JTextPane();
        jLabelResults = new javax.swing.JLabel();
        jButtonCheckInput = new javax.swing.JButton();
        jScrollPanePlccCommand = new javax.swing.JScrollPane();
        jTextFieldPlccCommand = new javax.swing.JTextField();
        jCheckBoxCustomOutputDir = new javax.swing.JCheckBox();
        jTextFieldCustomOutputDir = new javax.swing.JTextField();
        jButtonSelectCustomOutputDir = new javax.swing.JButton();
        jLabelInputOutput = new javax.swing.JLabel();
        jCheckBoxPGtypes = new javax.swing.JCheckBox();
        jCheckBoxPGalpha = new javax.swing.JCheckBox();
        jCheckBoxPGbeta = new javax.swing.JCheckBox();
        jCheckBoxPGalbe = new javax.swing.JCheckBox();
        jCheckBoxPGalphalig = new javax.swing.JCheckBox();
        jCheckBoxPGbetalig = new javax.swing.JCheckBox();
        jCheckBoxPGalbelig = new javax.swing.JCheckBox();
        jCheckBoxBackboneContacts = new javax.swing.JCheckBox();
        jCheckBoxLimitLigandAtoms = new javax.swing.JCheckBox();
        jLabelContactOptions = new javax.swing.JLabel();
        jCheckBoxFoldingGraphsRED = new javax.swing.JCheckBox();
        jCheckBoxFoldingGraphsKEY = new javax.swing.JCheckBox();
        jLabelLigAtomsMin = new javax.swing.JLabel();
        jTextFieldSetLigAtomsMin = new javax.swing.JTextField();
        jLabelLigAtomsMax = new javax.swing.JLabel();
        jTextFieldSetLigAtomsMax = new javax.swing.JTextField();
        jButtonExportPlccOptions = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jCheckBoxGraphFormatDOT = new javax.swing.JCheckBox();
        jCheckBoxGraphFormatGML = new javax.swing.JCheckBox();
        jCheckBoxGraphFormatTGF = new javax.swing.JCheckBox();
        jCheckBoxGraphFormatEdgeList = new javax.swing.JCheckBox();
        jCheckBoxGraphFormatPlcc = new javax.swing.JCheckBox();
        jPanelStats = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();

        setTitle("VPG Graph Creator");

        jPanelMainContent.setForeground(new java.awt.Color(212, 0, 0));

        jLabelInputPDB.setText("Input PDB file:");

        jTextFieldInputFilePDB.setText("/data/PDB/8icd.pdb");

        jLabelInputDSSP.setText("Input DSSP file:");

        jTextFieldInputFileDSSP.setText("/data/PDB/8icd.dssp");

        jButtonSelectFilePDB.setText("Select PDB file...");
        jButtonSelectFilePDB.setToolTipText("Select the input PDB file.");
        jButtonSelectFilePDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectFilePDBActionPerformed(evt);
            }
        });

        jButtonSelectFileDSSP.setText("Select DSSP file...");
        jButtonSelectFileDSSP.setToolTipText("Select the input DSSP file.");
        jButtonSelectFileDSSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectFileDSSPActionPerformed(evt);
            }
        });

        jLabelOptions.setText("Input and output options");

        jButtonRun.setText("Run PLCC");
        jButtonRun.setToolTipText("Runs PLCC with the settings defined above.");
        jButtonRun.setEnabled(false);
        jButtonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunActionPerformed(evt);
            }
        });

        jCheckBoxPdbFileIsGzipped.setText("is gzipped");
        jCheckBoxPdbFileIsGzipped.setToolTipText("Whether the selected input file is compressed with gzip.");

        jCheckBoxDsspFileIsGzipped.setText("is gzipped");
        jCheckBoxDsspFileIsGzipped.setToolTipText("Whether the selected input file is compressed with gzip.");

        jCheckBoxFoldingGraphs.setText("Compute Folding Graph notations:");
        jCheckBoxFoldingGraphs.setToolTipText("Whether folding graphs (connected components) of the Protein Graph should also be drawn");

        jCheckBoxOutputDirTree.setText("Write output files to subdirectory tree");
        jCheckBoxOutputDirTree.setToolTipText("Write output files to a directory structure like the one used by the PDB servers instead of putting all files in the same directory.");

        jCheckBoxUseDatabase.setText("Write output graphs into database");
        jCheckBoxUseDatabase.setToolTipText("Whether the graphs should be written to the database. Requires a properly configured database.");

        jLabelDebugOptions.setText("Graph Options");

        jCheckBoxAdditionalOutput.setText("Write additional output text files");
        jCheckBoxAdditionalOutput.setToolTipText("Writes additional meta data and detailed contact info to text files for each chain.");

        jCheckBoxAllowCoils.setText("Allow SSE type coil");
        jCheckBoxAllowCoils.setToolTipText("Experimental. Counts coiled regions as SSEs. This may lead to fragmented helices and strands.");

        jCheckBoxRamachandranPlot.setText("Draw Ramachandran plots");
        jCheckBoxRamachandranPlot.setToolTipText("Draws a Ramachandran plot of the backbone angles of a chain to an image file.");

        jCheckBoxForceChain.setText("Force chain:");
        jCheckBoxForceChain.setToolTipText("Handle only the chain with the specified chain ID. PLCC will abort if no such chain exists.");

        jTextFieldForceChain.setText("A");
        jTextFieldForceChain.setToolTipText("The PDB chain identifier of the chain.");
        jTextFieldForceChain.setEnabled(false);

        jLabelImageFormat.setText("Image format:");

        jComboBoxImageFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "PNG", "JPG" }));
        jComboBoxImageFormat.setToolTipText("The output image format.");

        jLabelPlccCommand.setText("Resulting PLCC command line:");

        jScrollPaneStatus.setBackground(new java.awt.Color(220, 220, 220));

        jTextPaneStatus.setEditable(false);
        jTextPaneStatus.setBackground(new java.awt.Color(220, 220, 220));
        jTextPaneStatus.setToolTipText("Results of settings check or PLCC run.");
        jScrollPaneStatus.setViewportView(jTextPaneStatus);

        jLabelResults.setText("Results:");

        jButtonCheckInput.setText("Check settings");
        jButtonCheckInput.setToolTipText("Verifies the input and output settings.");
        jButtonCheckInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCheckInputActionPerformed(evt);
            }
        });

        jScrollPanePlccCommand.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPanePlccCommand.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextFieldPlccCommand.setEditable(false);
        jTextFieldPlccCommand.setBackground(new java.awt.Color(220, 220, 220));
        jTextFieldPlccCommand.setText("java -jar plcc.jar 8icd");
        jScrollPanePlccCommand.setViewportView(jTextFieldPlccCommand);

        jCheckBoxCustomOutputDir.setText("Use custom output directory:");
        jCheckBoxCustomOutputDir.setToolTipText("Whether the directory on the right should be used instead of the default directory specified in the settings.");

        jTextFieldCustomOutputDir.setText("/tmp");
        jTextFieldCustomOutputDir.setEnabled(false);

        jButtonSelectCustomOutputDir.setText("Set output dir...");
        jButtonSelectCustomOutputDir.setToolTipText("Selects the output directory where images and other files are written.");
        jButtonSelectCustomOutputDir.setEnabled(false);
        jButtonSelectCustomOutputDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectCustomOutputDirActionPerformed(evt);
            }
        });

        jLabelInputOutput.setText("Input and output");

        jCheckBoxPGtypes.setText("Compute only protein graphs of type:");
        jCheckBoxPGtypes.setToolTipText("Compute only the selected protein graph types instead of all types.");

        jCheckBoxPGalpha.setText("alpha");
        jCheckBoxPGalpha.setToolTipText("Alpha graphs consist of all alpha helices in a chain and the contacts between them.");
        jCheckBoxPGalpha.setEnabled(false);

        jCheckBoxPGbeta.setText("beta");
        jCheckBoxPGbeta.setToolTipText("Beta graphs consist of all beta strands in a chain and the contacts between them.");
        jCheckBoxPGbeta.setEnabled(false);

        jCheckBoxPGalbe.setText("albe");
        jCheckBoxPGalbe.setToolTipText("Albe graphs (or alphabeta graphs) consist of all alpha helices, beta strands and the contacts between them in a chain.");
        jCheckBoxPGalbe.setEnabled(false);

        jCheckBoxPGalphalig.setSelected(true);
        jCheckBoxPGalphalig.setText("alphalig");
        jCheckBoxPGalphalig.setToolTipText("Alphalig graphs consist of all alpha helices and ligands in a chain, and the contacts between them.");
        jCheckBoxPGalphalig.setEnabled(false);

        jCheckBoxPGbetalig.setSelected(true);
        jCheckBoxPGbetalig.setText("betalig");
        jCheckBoxPGbetalig.setToolTipText("Betalig graphs consist of all beta strands and ligands in a chain, and the contacts between them.");
        jCheckBoxPGbetalig.setEnabled(false);

        jCheckBoxPGalbelig.setSelected(true);
        jCheckBoxPGalbelig.setText("albelig");
        jCheckBoxPGalbelig.setToolTipText("Albelig graphs consist of all alpha helices, beta strands and ligands in a chain, and the contacts between them.");
        jCheckBoxPGalbelig.setEnabled(false);

        jCheckBoxBackboneContacts.setText("Add SSE backbone contacts");
        jCheckBoxBackboneContacts.setToolTipText("Adds contacts of type backbone between consecutive SSEs in the primary sequence.");

        jCheckBoxLimitLigandAtoms.setText("Filter ligands by atom count");
        jCheckBoxLimitLigandAtoms.setToolTipText("If this is selected, only ligands which have min..max atoms are considered in the output graphs.");

        jLabelContactOptions.setText("Contact options");

        jCheckBoxFoldingGraphsRED.setSelected(true);
        jCheckBoxFoldingGraphsRED.setText("PGS");
        jCheckBoxFoldingGraphsRED.setToolTipText("Draws folding graphs in VPLG protein graph style.");
        jCheckBoxFoldingGraphsRED.setEnabled(false);

        jCheckBoxFoldingGraphsKEY.setText("BIOS");
        jCheckBoxFoldingGraphsKEY.setToolTipText("Draws folding graphs in biology style.");
        jCheckBoxFoldingGraphsKEY.setEnabled(false);

        jLabelLigAtomsMin.setText("min:");

        jTextFieldSetLigAtomsMin.setText("2");
        jTextFieldSetLigAtomsMin.setToolTipText("The lower border (including). Set to zero to disable.");
        jTextFieldSetLigAtomsMin.setEnabled(false);

        jLabelLigAtomsMax.setText("max:");

        jTextFieldSetLigAtomsMax.setText("0");
        jTextFieldSetLigAtomsMax.setToolTipText("The upper border (including). Set to zero to disable.");
        jTextFieldSetLigAtomsMax.setEnabled(false);

        jButtonExportPlccOptions.setText("Export options...");
        jButtonExportPlccOptions.setToolTipText("Export the current PLCC settings to a file for usage in batch processing mode.");
        jButtonExportPlccOptions.setEnabled(false);
        jButtonExportPlccOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportPlccOptionsActionPerformed(evt);
            }
        });

        jLabel1.setText("Graphs:");
        jLabel1.setToolTipText("Export graphs in these formats:");

        jCheckBoxGraphFormatDOT.setSelected(true);
        jCheckBoxGraphFormatDOT.setText("DOT");
        jCheckBoxGraphFormatDOT.setToolTipText("The DOT language format (.gv)");

        jCheckBoxGraphFormatGML.setSelected(true);
        jCheckBoxGraphFormatGML.setText("GML");
        jCheckBoxGraphFormatGML.setToolTipText("GML format (.gml)");

        jCheckBoxGraphFormatTGF.setText("TGF");
        jCheckBoxGraphFormatTGF.setToolTipText("Trivial Graph Format (.tgf)");

        jCheckBoxGraphFormatEdgeList.setText("EL");
        jCheckBoxGraphFormatEdgeList.setToolTipText("Kavosh edge list (.kavosh)");

        jCheckBoxGraphFormatPlcc.setSelected(true);
        jCheckBoxGraphFormatPlcc.setText("PLCC");
        jCheckBoxGraphFormatPlcc.setToolTipText("PLCC graph format");

        javax.swing.GroupLayout jPanelMainContentLayout = new javax.swing.GroupLayout(jPanelMainContent);
        jPanelMainContent.setLayout(jPanelMainContentLayout);
        jPanelMainContentLayout.setHorizontalGroup(
            jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMainContentLayout.createSequentialGroup()
                        .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPanePlccCommand)
                            .addComponent(jScrollPaneStatus)
                            .addComponent(jSeparatorLower, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparatorUpper)
                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                        .addComponent(jLabelInputDSSP)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                        .addComponent(jLabelInputPDB)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextFieldInputFilePDB)
                                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                                .addComponent(jTextFieldInputFileDSSP, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBoxPdbFileIsGzipped, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jCheckBoxDsspFileIsGzipped, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButtonSelectFileDSSP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonSelectFilePDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBoxOutputDirTree)
                                    .addComponent(jCheckBoxUseDatabase)
                                    .addComponent(jCheckBoxAdditionalOutput)
                                    .addComponent(jCheckBoxRamachandranPlot))
                                .addGap(29, 29, 29)
                                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBoxAllowCoils)
                                    .addComponent(jLabelDebugOptions)
                                    .addComponent(jCheckBoxPGtypes)
                                    .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                        .addGap(21, 21, 21)
                                        .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jCheckBoxPGalpha)
                                            .addComponent(jCheckBoxPGalphalig))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jCheckBoxPGbeta)
                                            .addComponent(jCheckBoxPGbetalig))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jCheckBoxPGalbelig)
                                            .addComponent(jCheckBoxPGalbe))))
                                .addGap(18, 18, 18)
                                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBoxLimitLigandAtoms, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                        .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jCheckBoxBackboneContacts)
                                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                                .addComponent(jCheckBoxForceChain)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextFieldForceChain, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                                .addGap(21, 21, 21)
                                                .addComponent(jLabelLigAtomsMin)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextFieldSetLigAtomsMin, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabelLigAtomsMax)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextFieldSetLigAtomsMax, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addComponent(jSeparatorCenter)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainContentLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButtonCheckInput)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonExportPlccOptions)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonRun))
                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                .addComponent(jCheckBoxCustomOutputDir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldCustomOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonSelectCustomOutputDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                        .addComponent(jLabelOptions)
                                        .addGap(315, 315, 315)
                                        .addComponent(jLabelContactOptions))
                                    .addComponent(jLabelPlccCommand)
                                    .addComponent(jLabelResults)
                                    .addComponent(jLabelInputOutput))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanelMainContentLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                .addComponent(jLabelImageFormat)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxImageFormat, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxGraphFormatDOT)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxGraphFormatGML)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxGraphFormatTGF)))
                        .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainContentLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxGraphFormatEdgeList)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBoxGraphFormatPlcc)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jCheckBoxFoldingGraphsRED)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBoxFoldingGraphsKEY))
                            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(jCheckBoxFoldingGraphs)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanelMainContentLayout.setVerticalGroup(
            jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainContentLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabelInputOutput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldInputFilePDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSelectFilePDB)
                    .addComponent(jCheckBoxPdbFileIsGzipped)
                    .addComponent(jLabelInputPDB))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldInputFileDSSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSelectFileDSSP)
                    .addComponent(jCheckBoxDsspFileIsGzipped)
                    .addComponent(jLabelInputDSSP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSelectCustomOutputDir)
                    .addComponent(jTextFieldCustomOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxCustomOutputDir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparatorUpper, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelOptions)
                    .addComponent(jLabelDebugOptions)
                    .addComponent(jLabelContactOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxOutputDirTree)
                    .addComponent(jCheckBoxAllowCoils)
                    .addComponent(jCheckBoxForceChain)
                    .addComponent(jTextFieldForceChain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxUseDatabase)
                    .addComponent(jCheckBoxPGtypes)
                    .addComponent(jCheckBoxBackboneContacts))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxAdditionalOutput)
                    .addComponent(jCheckBoxPGalpha)
                    .addComponent(jCheckBoxPGbeta)
                    .addComponent(jCheckBoxPGalbe)
                    .addComponent(jCheckBoxLimitLigandAtoms))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxRamachandranPlot)
                    .addComponent(jCheckBoxPGalphalig)
                    .addComponent(jCheckBoxPGbetalig)
                    .addComponent(jCheckBoxPGalbelig)
                    .addComponent(jLabelLigAtomsMin)
                    .addComponent(jTextFieldSetLigAtomsMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelLigAtomsMax)
                    .addComponent(jTextFieldSetLigAtomsMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxFoldingGraphs)
                    .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelImageFormat)
                        .addComponent(jComboBoxImageFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxFoldingGraphsRED)
                    .addComponent(jCheckBoxFoldingGraphsKEY)
                    .addComponent(jLabel1)
                    .addComponent(jCheckBoxGraphFormatDOT)
                    .addComponent(jCheckBoxGraphFormatGML)
                    .addComponent(jCheckBoxGraphFormatTGF)
                    .addComponent(jCheckBoxGraphFormatEdgeList)
                    .addComponent(jCheckBoxGraphFormatPlcc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addComponent(jSeparatorCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelPlccCommand)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPanePlccCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCheckInput)
                    .addComponent(jButtonRun)
                    .addComponent(jButtonExportPlccOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparatorLower, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelResults)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabelStatus.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabelStatus.setText("VPG Graph Creator ready.");

        javax.swing.GroupLayout jPanelStatsLayout = new javax.swing.GroupLayout(jPanelStats);
        jPanelStats.setLayout(jPanelStatsLayout);
        jPanelStatsLayout.setHorizontalGroup(
            jPanelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelStatsLayout.setVerticalGroup(
            jPanelStatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMainContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelStats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelMainContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelStats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSelectFilePDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectFilePDBActionPerformed
        
        File defaultDir = new File(Settings.get("vpg_S_input_dir"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldInputFilePDB.setText(fc.getSelectedFile().toString());
        }
    }//GEN-LAST:event_jButtonSelectFilePDBActionPerformed

    private void jButtonSelectFileDSSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectFileDSSPActionPerformed
        
        File defaultDir = new File(Settings.get("vpg_S_input_dir"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldInputFileDSSP.setText(fc.getSelectedFile().toString());
        }
    }//GEN-LAST:event_jButtonSelectFileDSSPActionPerformed

    /**
     * Exports the current non-essential PLCC command line options to a text file. The options in there
     * contain no file paths and are save to split at spaces.
     * @param propFile the output text file
     * @param description a free form description of the settings in the file. Optional, may be null. Must not contain newlines.
     * @return true if the file was written, false otherwise
     */
    private Boolean exportCurrentSettingsToFile(File propFile, String description) {                

        if(description == null) {
            description = "# No description available for these settings.";
        }
        ArrayList<String> options;
        options = this.getNonEssentialOptions(new ArrayList<String>());
        String optionsString = "# These settings for PLCC can be loaded in the VPG batch processing module.\n";
        optionsString += description + "\n";
        for (String s : options) {
            optionsString += s + " ";
        }
        optionsString += "\n";
        
        if(propFile.exists()) {
            if(propFile.isDirectory()) {
                System.err.println(Settings.getApptag() + "ERROR: Cannot export settings to file '" + propFile.getAbsolutePath() + "', directory with that name exists.");
                return false;
            }
        }        
        
        
               
        return (IO.stringToTextFile(propFile.getAbsolutePath(), optionsString));
    }        
    
    
    private File getWorkingDirFromForm() {
        return new File(Settings.get("vpg_S_path_plcc")).getAbsoluteFile().getParentFile();
    }

    
    private void jButtonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunActionPerformed

        String [] cmd = this.getCommandArray();
        File workingDir = this.getWorkingDirFromForm();
        
        System.out.println(Settings.getApptag() + "Running plcc...");
        this.jTextPaneStatus.setText("Running plcc...");
        String resText = "";
        
        try {
            String[] inputAndError = IO.execCmd(cmd, workingDir);
            Integer retVal = Integer.valueOf(inputAndError[2]);
            System.out.println(Settings.getApptag() + "Finished running plcc with return value " + retVal.toString() + ".");
            System.out.println(Settings.getApptag() + "PLCC output follows:\n" + inputAndError[0]);
            System.out.println(Settings.getApptag() + "End of PLCC output.");
            if( ! inputAndError[1].isEmpty()) {
                System.out.println(Settings.getApptag() + "PLCC errors and warnings follow:\n" + inputAndError[1]);
                System.out.println(Settings.getApptag() + "End of PLCC errors and warnings.");
            } else {
                System.out.println(Settings.getApptag() + "PLCC wrote no errors or warnings.");
            }
           
            resText = "Finished running plcc with return value " + retVal.toString() + ".\n";
            if(retVal == 0) {
                resText += "OK: Process terminated as expected.\nOutput files should be in '" + this.getOutputDirFromForm() + "'. You can view them with the Graph Viewer.\n";
            } else {
                resText += "ERROR: Running PLCC failed, see the PLCC output below for details:\n-------------------------------------------------\n";
                resText += inputAndError[1];
            }
            this.jTextPaneStatus.setText(resText);
            
        } catch(Exception e) {
            System.err.println(Settings.getApptag() + "ERROR running plcc: '" + e.getMessage() + "'.");
            this.jTextPaneStatus.setText("ERROR running plcc: '" + e.getMessage() + "'.");
        }
        
        this.jButtonRun.setEnabled(false); 
        this.jButtonExportPlccOptions.setEnabled(false);
    }//GEN-LAST:event_jButtonRunActionPerformed

    
    /**
     * Manually calls the check input function.
     * @param evt 
     */
    private void jButtonCheckInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCheckInputActionPerformed
        this.checkInput();
                        
    }//GEN-LAST:event_jButtonCheckInputActionPerformed


    /**
     * Checks form input and validates data.
     */
    private void checkInput() {
        String statusText = "";
        File workingDir = this.getWorkingDirFromForm();
        
        if(! workingDir.isDirectory() && workingDir.canRead()) {
            this.jTextPaneStatus.setText("ERROR: Working directory '" + workingDir.toString() + "' with PLCC jar file does not exist.");
            this.jLabelStatus.setText("WARNING: Path to plcc.jar does not exist, please fix in settings.");
            this.jLabelPlccCommand.setToolTipText("Could not find a proper working directory with current settings.");
            this.jButtonRun.setEnabled(false);
            return;
        } else {
            this.jLabelStatus.setText("VPG Graph Creator ready.");
            this.jLabelPlccCommand.setToolTipText("Working directory for the command will be '" + workingDir.getAbsolutePath() + "'.");
        }
        
        
        if(this.inputFilesOK()) {
            statusText = "Input files seem OK.\n";
            this.jTextPaneStatus.setText(statusText);
            
            String pdbid = this.determinePDBID();
            if(pdbid == null) {
                statusText += "ERROR: Cannot determine PDB ID from filename, please fix.\n";
                this.jButtonRun.setEnabled(false);
            } else {
                statusText += "PDB ID '" + pdbid + "' determined from filename. Ready to run.\n";
                
                
                this.setCommandTextField();
                this.jButtonRun.setEnabled(true);
                this.jButtonExportPlccOptions.setEnabled(true);
            }                                            
        } else {
            statusText = "ERROR: Input files invalid, please fix.\n";
            this.jButtonRun.setEnabled(false);
            this.jButtonExportPlccOptions.setEnabled(false);
        }
        
        this.jTextPaneStatus.setText(statusText);
    }
    
    private void jButtonSelectCustomOutputDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectCustomOutputDirActionPerformed
        
        File defaultDir = new File(Settings.get("vpg_S_output_dir"));
        JFileChooser fc = new JFileChooser(defaultDir);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int rVal = fc.showOpenDialog(this);

        if (rVal == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldCustomOutputDir.setText(fc.getSelectedFile().toString());
        }
    }//GEN-LAST:event_jButtonSelectCustomOutputDirActionPerformed

    private void jButtonExportPlccOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportPlccOptionsActionPerformed

        String fs = System.getProperty("file.separator");
        JFileChooser savePlccOptionsDialog = new JFileChooser();
        savePlccOptionsDialog.setSelectedFile(new File(Settings.get("vpg_S_output_dir") + fs + "current_settings.plccopt"));
        int status = savePlccOptionsDialog.showSaveDialog(this);
        Boolean res = false;
        try {
            if (status == JFileChooser.APPROVE_OPTION) {

                File outputFile = savePlccOptionsDialog.getSelectedFile();
                String description;
                description = JOptionPane.showInputDialog("You can enter an optional description for these settings here:");

                if(description.isEmpty()) {
                    description = "No description available for these settings.";
                }
                
                description = "# " + description;
                
                res = this.exportCurrentSettingsToFile(outputFile, description);
                if(res) {
                    JOptionPane.showMessageDialog(this, "The current PLCC options were saved to '" + outputFile.getAbsolutePath() + "'.", "VPG -- File saved", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "The current PLCC options could not be saved.", "VPG -- ERROR saving file", JOptionPane.ERROR_MESSAGE);
                }

                
            } else if (status == JFileChooser.CANCEL_OPTION) {
   
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "The current PLCC options could not be saved.", "VPG -- ERROR saving file", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonExportPlccOptionsActionPerformed

    
    /**
     * Sets the command line text field.
     * @return true atm
     */
    public Boolean setCommandTextField() {
        this.jLabelPlccCommand.setText("Resulting PLCC command line:");
        String[] cmd = this.getCommandArray();
        String cmdString = "";
        for(String s : cmd) { cmdString += s + " "; }
        this.jTextFieldPlccCommand.setText(cmdString);        
        
        return true;
    }
    
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
            java.util.logging.Logger.getLogger(VpgCreateGraphFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VpgCreateGraphFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VpgCreateGraphFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VpgCreateGraphFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new VpgCreateGraphFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCheckInput;
    private javax.swing.JButton jButtonExportPlccOptions;
    private javax.swing.JButton jButtonRun;
    private javax.swing.JButton jButtonSelectCustomOutputDir;
    private javax.swing.JButton jButtonSelectFileDSSP;
    private javax.swing.JButton jButtonSelectFilePDB;
    private javax.swing.JCheckBox jCheckBoxAdditionalOutput;
    private javax.swing.JCheckBox jCheckBoxAllowCoils;
    private javax.swing.JCheckBox jCheckBoxBackboneContacts;
    private javax.swing.JCheckBox jCheckBoxCustomOutputDir;
    private javax.swing.JCheckBox jCheckBoxDsspFileIsGzipped;
    private javax.swing.JCheckBox jCheckBoxFoldingGraphs;
    private javax.swing.JCheckBox jCheckBoxFoldingGraphsKEY;
    private javax.swing.JCheckBox jCheckBoxFoldingGraphsRED;
    private javax.swing.JCheckBox jCheckBoxForceChain;
    private javax.swing.JCheckBox jCheckBoxGraphFormatDOT;
    private javax.swing.JCheckBox jCheckBoxGraphFormatEdgeList;
    private javax.swing.JCheckBox jCheckBoxGraphFormatGML;
    private javax.swing.JCheckBox jCheckBoxGraphFormatPlcc;
    private javax.swing.JCheckBox jCheckBoxGraphFormatTGF;
    private javax.swing.JCheckBox jCheckBoxLimitLigandAtoms;
    private javax.swing.JCheckBox jCheckBoxOutputDirTree;
    private javax.swing.JCheckBox jCheckBoxPGalbe;
    private javax.swing.JCheckBox jCheckBoxPGalbelig;
    private javax.swing.JCheckBox jCheckBoxPGalpha;
    private javax.swing.JCheckBox jCheckBoxPGalphalig;
    private javax.swing.JCheckBox jCheckBoxPGbeta;
    private javax.swing.JCheckBox jCheckBoxPGbetalig;
    private javax.swing.JCheckBox jCheckBoxPGtypes;
    private javax.swing.JCheckBox jCheckBoxPdbFileIsGzipped;
    private javax.swing.JCheckBox jCheckBoxRamachandranPlot;
    private javax.swing.JCheckBox jCheckBoxUseDatabase;
    private javax.swing.JComboBox jComboBoxImageFormat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelContactOptions;
    private javax.swing.JLabel jLabelDebugOptions;
    private javax.swing.JLabel jLabelImageFormat;
    private javax.swing.JLabel jLabelInputDSSP;
    private javax.swing.JLabel jLabelInputOutput;
    private javax.swing.JLabel jLabelInputPDB;
    private javax.swing.JLabel jLabelLigAtomsMax;
    private javax.swing.JLabel jLabelLigAtomsMin;
    private javax.swing.JLabel jLabelOptions;
    private javax.swing.JLabel jLabelPlccCommand;
    private javax.swing.JLabel jLabelResults;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JPanel jPanelMainContent;
    private javax.swing.JPanel jPanelStats;
    private javax.swing.JScrollPane jScrollPanePlccCommand;
    private javax.swing.JScrollPane jScrollPaneStatus;
    private javax.swing.JSeparator jSeparatorCenter;
    private javax.swing.JSeparator jSeparatorLower;
    private javax.swing.JSeparator jSeparatorUpper;
    private javax.swing.JTextField jTextFieldCustomOutputDir;
    private javax.swing.JTextField jTextFieldForceChain;
    private javax.swing.JTextField jTextFieldInputFileDSSP;
    private javax.swing.JTextField jTextFieldInputFilePDB;
    private javax.swing.JTextField jTextFieldPlccCommand;
    private javax.swing.JTextField jTextFieldSetLigAtomsMax;
    private javax.swing.JTextField jTextFieldSetLigAtomsMin;
    private javax.swing.JTextPane jTextPaneStatus;
    // End of variables declaration//GEN-END:variables
}
