/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */


package vpg;

import java.awt.Color;
import java.awt.ItemSelectable;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
 
 
public class VpgGraphViewerFrame extends JFrame {
 
  private JTree fileTree;
  private FileSystemModel fileSystemModel;
  private JTextArea fileDetailsTextArea = new JTextArea();
  private JLabel imageLabel = new JLabel();
  private JPanel comboPanel;
  private JButton comboButton;
  private String[] extensions;
  private JComboBox combobox = new JComboBox();
 
  
  public VpgGraphViewerFrame(String directory) {                  
      
    super("VPG Graph Image Viewer");
    
    File[] roots = File.listRoots();
    String[] fsRoots = new String[roots.length + 1];
    for(Integer i = 0; i < roots.length; i++) { 
        fsRoots[i] = roots[i].toString();
    }
    fsRoots[roots.length] = System.getProperty("user.home");
    
    comboPanel = new JPanel();
    
    comboButton = new JButton("Set root");
    comboButton.addActionListener(new ActionListener() {
 
            @Override public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
                System.out.println("[VPG] Setting root to " + combobox.getSelectedItem().toString() + ".");
                //fileTree = new JTree(new FileSystemModel(new File(combobox.getSelectedItem().toString())));
                fileSystemModel = new FileSystemModel(new File(combobox.getSelectedItem().toString()));
                fileTree.setModel(fileSystemModel);
            }
        });      
    
    
    this.combobox = new JComboBox(fsRoots);
    this.combobox.setSize(80, 20);
    this.combobox.setSelectedIndex(roots.length);
    comboPanel.add(combobox, JFrame.LEFT_ALIGNMENT);
    comboPanel.add(comboButton, JFrame.RIGHT_ALIGNMENT);
    
            
    this.extensions = new String[] { ".png", ".gif", ".jpg", ".jpeg" };
    
    fileDetailsTextArea.setEditable(false);
    fileDetailsTextArea.setText("Select an image file on the left to load it.\n\nThe image will be displayed above and this area will show information on the selected file.\n\nSupported file formats are JPG, GIF and PNG.");
    fileSystemModel = new FileSystemModel(new File(directory));
    //fileSystemModel.setRoot(new File(System.getProperty("user.home")));
    fileSystemModel.setValidExtensions(this.extensions);
    
    fileTree = new JTree(fileSystemModel);
    fileTree.setEditable(false);
    fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
    
    this.imageLabel.setOpaque(true);
    this.imageLabel.setBackground(Color.LIGHT_GRAY);
    this.imageLabel.setHorizontalAlignment(JLabel.CENTER);
 
    
    fileTree.addTreeSelectionListener(new TreeSelectionListener() {
        
      @Override public void valueChanged(TreeSelectionEvent event) {
          
        File file = (File) fileTree.getLastSelectedPathComponent();                
        
        if(file == null) {
            //System.out.println("No valid file selected, not showing new image.");
            fileDetailsTextArea.setText("(Nothing selected)");
        }
        else {
            
            if(file.exists() && file.canRead()) {        
                fileDetailsTextArea.setText(getFileDetails(file));
            }
            
            if(file.isFile()) {            
                if(IO.fileIsImage(file.toString())) {
                    ImageIcon image = new ImageIcon(file.getAbsolutePath());
                    //System.out.println("Created image icon from file " + file + ".");
                    imageLabel.setIcon(image);
                }
            }
            else if(file.isDirectory()) {
                //Object root = fileTree.getModel().getRoot();                
                //TreePath p = event.getNewLeadSelectionPath();                                
                //fileTree.expandPath(p);
                //System.out.println("Directory '" + file.getAbsolutePath() + "' selected");
                
            }
            else {
                // what else could it be                
            }
        }
        
      }
      
    });
    
    
    
    
    JSplitPane splitPaneImgDetails = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JScrollPane(
        imageLabel), new JScrollPane(fileDetailsTextArea));
    
    JSplitPane splitPaneFiletreeRoot = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JScrollPane(
        comboPanel), new JScrollPane(fileTree));
    
    //JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, new JScrollPane(fileTree), new JScrollPane(imageLabel));
    JSplitPane splitPaneFilesRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, splitPaneFiletreeRoot, splitPaneImgDetails);
        
        
    this.getContentPane().add(splitPaneFilesRight);
    this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    this.setSize(800, 600);
    this.setVisible(true);
    splitPaneFilesRight.setDividerLocation(0.3);
    splitPaneImgDetails.setDividerLocation(0.8);
  }
  
  
 
  /**
   * Creates an info string for a file.
   * @param file the file
   * @return the string with information on the file
   */
  private String getFileDetails(File file) {
    if (file == null) {
      return "(Nothing selected)";
    }
    String info = "Selected object: " + file.getName() + "\n";
    //info += "File name: " + file.getName() + "\n";
    info += "Path: " + file.getPath() + "\n";
    info += "Type: " + (file.isDirectory() ? "directory" : "file") + "\n";
    
    if(file.isDirectory()) {
        info += "Subobjects: " + file.list().length + " (only subdirs and supported images shown on the left)\n";
        String[] children = FileSystemModel.filterChildren(file.list(), extensions, file);
        Integer numImages = 0;
        for(String child : children) {
            if(IO.fileIsImage(child)) {
                numImages++;
            }
        }
        
        info += "Valid Subobjects: " + children.length + "\n";
        info += "Images directly in there: " + numImages + "\n";
        
    } else {
        info += "Size: " + file.length() + "\n";
    }

    
    //File[] roots = File.listRoots();
    //String fsRoots = "roots: ";
    //for (File root : roots) {
    //    fsRoots += " " + root.getAbsolutePath();
    //}
    //System.out.println(fsRoots);
    
    return info;
  }
  
  
  
 
  
}


/**
 * A file system model.
 * @author ts
 */
class FileSystemModel implements TreeModel {
  private File root;
  private String[] extensions;
 
  private Vector listeners = new Vector();
 
  
  /**
   * Constructor. Creates a FSM from the given root directory.
   * @param rootDirectory the root dir
   */
  public FileSystemModel(File rootDirectory) {
    root = rootDirectory;
    this.extensions = new String[] { ".png", ".jpg", ".jpeg", ".gif" };
  }
  
  
  /**
   * Sets a list of file extensions which will be listed.
   * @param extensions 
   */
  public void setValidExtensions(String[] extensions) {
      this.extensions = extensions;
  }
  

  /**
   * Sets the root to r.
   * @param r the root to set
   */
  public void setRoot(File r) {
      this.root = r;
  }
 

  /**
   * Returns the root of this FSM.
   * @return the root
   */
  @Override public Object getRoot() {
    return root;
  }
 
  
  /**
   * Returns the child with index 'index' of the object 'parent'.
   * @param parent the parent
   * @param index the index of the child
   * @return the child object
   */
  @Override public TreeFile getChild(Object parent, int index) {
    File directory = (File) parent;
    String[] children = directory.list();
    String[] filteredChildren = filterChildren(children, this.extensions, directory);
    return new TreeFile(directory, filteredChildren[index]);
  }
  
  /**
   * Scans strings in children and returns all that end with an extension from validExtensions.
   * @param children a list of strings
   * @param validExtensions the extensions that are valid
   * @param parent the parent directory
   * @return a list of all children that satisfy the condition 
   */
  public static String[] filterChildren(String[] children, String[] validExtensions, File parent) {
      ArrayList<String> imgFiles = new ArrayList<String>();
      String fs = System.getProperty("file.separator");
      
      //System.out.println("Filtering children of dir " + parent.toString() + ":");
      
      for(String child : children) {
          
          File fullPath = new File(parent.getAbsolutePath() + fs + child);
          
          if( ! fullPath.exists()) {
              //System.err.println("ERROR: Object at path '" + fullPath + "' does not exist in FS.");
          }
          
          if(fullPath.isDirectory()) {
              //System.out.println("File " + fullPath + " added: is directory.");
              imgFiles.add(child);
              continue;
          }
          
          for(String ext : validExtensions) {
              if(child.endsWith(ext)) {
                  imgFiles.add(child);
                  //System.out.println("File " + fullPath + " added: is file with valid extension.");
                  break;
              }
          }
      }

      String[] filtered = new String[imgFiles.size()];
      filtered = imgFiles.toArray(filtered);
      return(filtered);
  }
 
  @Override public int getChildCount(Object parent) {
    File file = (File) parent;
    if (file.isDirectory()) {
      String[] fileList = file.list();
      String[] filteredFileList = filterChildren(fileList, this.extensions, file);
      if (filteredFileList != null) {
          //String s = "Children of " + file.getAbsolutePath() + ":";
          //for(String child : filteredFileList) {
          //    s += " " + child.toString();
          //}
          //s += " (" + filteredFileList.length + ")";
          //System.out.println(s);
        return filteredFileList.length;
      }
    }
    return 0;
  }
 
  @Override public boolean isLeaf(Object node) {
    File file = (File) node;
    return file.isFile();
  }
 
  @Override public int getIndexOfChild(Object parent, Object child) {
    File directory = (File) parent;
    File file = (File) child;
    String[] children = directory.list();
    String[] filteredChildren = filterChildren(children, this.extensions, directory);
    for (int i = 0; i < filteredChildren.length; i++) {
      if (file.getName().equals(filteredChildren[i])) {
        return i;
      }
    }
    return -1;
 
  }
 
  
  @Override public void valueForPathChanged(TreePath path, Object value) {}
  
 
  @Override public void addTreeModelListener(TreeModelListener listener) {
    listeners.add(listener);
  }
 
  @Override public void removeTreeModelListener(TreeModelListener listener) {
    listeners.remove(listener);
  }
 
  /** 
   * TreeFile class.
   * 
   */
  private class TreeFile extends File {
    
      public TreeFile(File parent, String child) {
        super(parent, child);
      }
 
    @Override public String toString() {
      return getName();
    }
  }
}