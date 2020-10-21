/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Jan Niclas Wolf 2020. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author jnw
 */

package plccSettings;

import java.util.ArrayList;
import tools.DP;

/**
 *
 * @author jnw
 */
class Setting {
    final String name;  // name of the setting, should have format: plcc_D_NAME, where D is the data type
    final char dataType;  // values can only be saved as string, so we need to know, which data type they should have, capital letter
    final private String defaultValue;  // hard coded default value
    final String documentation;  // documentation string
    private String overwrittenValue = "";  // the value of the setting that can be changed by the user via a settings file or command line options
    
    // contains the allowed data types for the check in the constructor as capital letters
    static final ArrayList<Character> ALLOWED_DATA_TYPES = new ArrayList<Character>() {
        {
            add('B');
            add('I');
            add('F');
            add('S');
        }
    };

    
    /**
     * Constructor for SingleSetting.
     * @param name name of the setting, should have format: plcc_D_NAME, where D is the data type
     * @param dataType data type of the setting
     * @param defaultValue hard coded default value of the setting
     * @param documentation
     */
    public Setting(String name, char dataType, String defaultValue, String documentation) {
        this.name = name;
        
        // check that data type is allowed to avoid sloppy programming
        if (ALLOWED_DATA_TYPES.contains(dataType)) {
            this.dataType = dataType;
        } else {
            DP.getInstance().e(Settings.PACKAGE_TAG, "Creating the setting '" + name + "' failed, because the data type is not allowed or correctly formatted. "
                    + "This is an error in the code, please inform the developer of this software. Exiting now.");
            this.dataType = 'S';  // for the IDE
            System.exit(1);
        }
        
        // if developer do no mistakes, this check is unneccessary - that is why we do the check
        if (checkDataType(defaultValue)) { 
            this.defaultValue = defaultValue;
        } else {
            DP.getInstance().e(Settings.PACKAGE_TAG, "Creating the setting '" + name + "' failed, because the default value '" + defaultValue + "' "
                    + "seems not to be of the required data type '" + getDataTypeString() + "'. "
                    + "This is an error in the code, please inform the developer of this software. Exiting now.");
            System.exit(1);
            this.defaultValue = "";  // for the IDE
        }
        
        this.documentation = documentation;
    }
    
    
    private Boolean checkDataType(String value) {
        switch (dataType) {
            case 'S':
                return true;  // String is always String
            case 'I':
                if (tools.TextTools.isInteger(value)) {
                    return true;
                }
                break;
            case 'F':
                if (tools.TextTools.isFloat(value)) {
                    return true;
                }
                break;
            case 'B':
                if (tools.TextTools.isTrueOrFalse(value)) {
                    return true;
                }
                break;
        }
        return false;
    }
    
    
    /**
     * 
     * @param value
     * @return 
     */
    void setOverwrittenValue(String value) {
        if (checkDataType(value)) {
            this.overwrittenValue = value;
        } else {
            DP.getInstance().w(Settings.PACKAGE_TAG, "Could not overwrite setting '" + name + "', because value '" + value + "' is of wrong format. "
                    + "Required for this setting: " + getDataTypeString());
        }
    }
    
    
    /**
     * Returns the value set by the user and otherwise the default value.
     * @return value as String
     */
    String getValue() {
        return (overwrittenValue.equals("") ? defaultValue : overwrittenValue);
        
        // TODO warn fallback to default
    }
    
    String getDefaultValue() { return defaultValue; }
    
    private String getDataTypeString() {
        switch (dataType) {
            case 'S': { return "String"; }
            case 'I': { return "Integer"; }
            case 'F': { return "Float"; }
            case 'B': { return "Boolean"; }
            default: { return "UNSPECIFIED DATATYPE"; }
        }
    }
}
