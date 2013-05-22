/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package Tools;

import plcc.Settings;

/**
 * A debug printer. This is a singleton.
 * @author ts
 */
public class DP {
    private static DP instance = null;
    
    protected DP() {
        // prevent instantiation
    }
    
    public static DP getInstance() {
      if(instance == null) {
         instance = new DP();
         instance.init();
      }
      return instance;
   }
    
    private void init() {
        // init stuff here
    }
    
    public void w(String msg) {
        Boolean doWarn = true;
        try {
            doWarn = Settings.getBoolean("plcc_B_no_warn");
        } catch(Exception e) {
            // the settings have not been inited yet, so assume that we should warn
            //System.err.println("WARNING: No settings yet.");
        }
        if(doWarn) {
            System.err.println("[WARNING] " + msg);
        }
    }
    
    public void e(String msg) {
        System.err.println("[ERROR] " + msg);
    }
    
    public void p(String msg) {
        System.out.println("[MSG] " + msg);
    }
    
    public void i(String msg) {
        System.out.println("[INFO] " + msg);
    }
    
    public void d(String msg) {
        System.out.println("[DEBUG] " + msg);
    }
    
    
}
