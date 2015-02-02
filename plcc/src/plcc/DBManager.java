/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import plcc.DrawTools.IMAGEFORMAT;
import similarity.SimilarityByGraphlets;
import tools.DP;
import tools.PlccUtilities;

/**
 * A database manager class that is used to create and maintain a connection to a PostgreSQL database server.
 * 
 * @author ts
 */
public class DBManager {

    static String dbName;
    static String dbHost;
    static Integer dbPort;       // default for PostgreSQL is 5432
    static String dbURL;
    static String dbUsername;
    static String dbPassword;
    static Statement sql;
    static DatabaseMetaData dbmd;
    static String dbDriver;
    static Connection dbc;
    // table names
        
    
    /** Name of the table which stores info on a PDB protein, identified by the PDB ID. */
    static String tbl_protein = "plcc_protein";
    static String tbl_chain = "plcc_chain";
    static String tbl_sse = "plcc_sse";
    static String tbl_proteingraph = "plcc_graph";
    static String tbl_foldinggraph = "plcc_foldinggraph";
    static String tbl_complexgraph = "plcc_complexgraph";
    static String tbl_graphletcount = "plcc_graphlets";
    static String tbl_motif = "plcc_motif";
    static String tbl_motiftype = "plcc_motiftype";
    static String tbl_ligand = "plcc_ligand";
    static String tbl_nm_ligandtochain = "plcc_nm_ligandtochain";
    static String tbl_nm_ssetoproteingraph = "plcc_nm_ssetoproteingraph";
    static String tbl_nm_ssetofoldinggraph = "plcc_nm_ssetofoldinggraph";
    static String tbl_nm_chaintomotif = "plcc_nm_chaintomotif";
    
    /** Name of the table which stores info on SSE types, e.g., alpha-helix, beta-strand and ligand. */
    static String tbl_ssetypes = "plcc_ssetypes";
    static String tbl_secondat = "plcc_secondat";
    
    /** Name of the table which stores info on intra-chain SSE contact types, e.g., parallel, anti-parallel, mixed or ligand. */
    static String tbl_contacttypes = "plcc_contacttypes";
    
    /** Name of the table which stores info on inter-chain SSE contact types, e.g., van-der-Waals or disulfide bridge. */
    static String tbl_complexcontacttypes = "plcc_complexcontacttypes";
    
    /** Name of the table which stores info on graph types, e.g., alpha-graph, beta-graph, alphabeta-graph, alphalig-graph, and so on. */
    static String tbl_graphtypes = "plcc_graphtypes";
    
    static String tbl_ssecontact = "plcc_contact";
    static String tbl_ssecontact_complexgraph = "plcc_ssecontact_complexgraph";
    static String tbl_complex_contact_stats = "plcc_complex_contact";
    
    /** New table that stores linear notations for all graph types. They are distinguished by a graph_type field, like in all other tables. This tables replaces the albe, alpha and beta tables of the PTGL, which were useless from a DB design point of view. */
    static String tbl_fglinnot = "plcc_fglinnot";
    static String tbl_graphletsimilarity = "plcc_graphletsimilarity";
    
    /** Name of the table which stores the PTGL alpha linear notation of a folding graph. */
    //static String tbl_fglinnot_alpha = "plcc_fglinnot_alpha";
    
    /** Name of the table which stores the PTGL beta linear notation of a folding graph. */
    //static String tbl_fglinnot_beta = "plcc_fglinnot_beta";
    
    /** Name of the table which stores the PTGL linear notation of a folding graph. */
    //static String tbl_fglinnot_albe = "plcc_fglinnot_albe";        
    
    /** Name of the table which stores the PTGL linear notation of a folding graph. */
    //static String tbl_fglinnot_alphalig = "plcc_fglinnot_alphalig";
    
    /** Name of the table which stores the PTGL linear notation of a folding graph. */
    //static String tbl_fglinnot_betalig = "plcc_fglinnot_betalig";
    
    /** Name of the table which stores the PTGL linear notation of a folding graph. */
    //static String tbl_fglinnot_albelig = "plcc_fglinnot_albelig";        
    
    static String view_sses = "plcc_view_sses";
    static String view_chainmotifs = "plcc_view_chainmotifs";
    
    static String view_ssecontacts = "plcc_view_ssetype_contacts";
    static String view_graphs = "plcc_view_graphs";
    static String view_foldinggraphs = "plcc_view_foldinggraphs";
    //static String view_fglinnotsalpha = "plcc_view_fglinnotsalpha";
    //static String view_fglinnotsbeta = "plcc_view_fglinnotsbeta";
    //static String view_fglinnotsalbe = "plcc_view_fglinnotsalbe";
    //static String view_fglinnotsalphalig = "plcc_view_fglinnotsalphalig";
    //static String view_fglinnotsbetalig = "plcc_view_fglinnotsbetalig";
    //static String view_fglinnotsalbelig = "plcc_view_fglinnotsalbelig";
    static String view_fglinnots = "plcc_view_fglinnots";
    static String view_secondat = "plcc_view_secondat";
    static String view_graphlets = "plcc_view_graphlets";
    static String view_graphletsimilarity = "plcc_view_graphletsimilarity";

    /**
     * Sets the database address and the credentials to access the DB, then connects by calling the connect() function.
     * @return True if the connection could be established, false otherwise.
     */
    public static Boolean init(String db, String host, Integer port, String user, String password, Boolean setAutoCommit) {

        dbName = db;
        dbHost = host;
        dbPort = port;

        String appTag = "?ApplicationName=plcc";

        dbURL = "jdbc:postgresql://" + host + ":" + port + "/" + db + appTag;
        dbUsername = user;
        dbPassword = password;

        dbDriver = "org.postgresql.Driver";               

        try {
            Class.forName(dbDriver);
        } catch (Exception e) {
            System.err.println("ERROR: Could not load JDBC driver '" + dbDriver + "'. Is the correct db driver installed at lib/postgresql-jdbc.jar?");
            System.err.println("ERROR: See the README for more info on getting the proper driver for your PostgreSQL server and Java versions.'");
            System.err.println("ERROR: Message was: '" + e.getMessage() + "'.");
            System.exit(1);     // if this code is called, the user explcitely requested to use the DB, so we should die if it does not work.
            return false;
        }

        Boolean conOK = connect(setAutoCommit);
        return (conOK);
    }
    
    /**
     * This function sets the attribute that marks a chain as part of the representative chains set for all chains in the given list.
     * @param pdbChains a list of PDB chains, each String array in the list has length 2 and looks like ["7tim", "A"].
     * @return an array of numbers, each position logs a count. 0 = number of chains in input list. 1 = number of chains updated in DB.
     * @throws SQLException if DB stuff goes wrong
     */
    public static Integer[] markAllRepresentativeChainsFromList(List<String[]> pdbChains) throws SQLException {
        Integer numUpdatedInDB = 0;
        
        String pdb_id, chain;
        for(String[] pdbChain : pdbChains) {
            pdb_id = pdbChain[0];
            chain = pdbChain[1];
            numUpdatedInDB += DBManager.markChainRepresentative(pdb_id, chain, true);
        }
        
        return new Integer[] { pdbChains.size(), numUpdatedInDB };
    }
    
    
    /**
     * Marks all chains which are currently in the DB as NOT part of the representative 40 set. This
     * function can be called before labeling those in a list of representative, to ensure that an
     * already existing labeling is removed before applying the new one.
     * @return the number of updated rows in the DB
     * @throws SQLException if DB stuff goes wrong
     */
    public static Integer markAllChainsAsNonRepresentative() throws SQLException {
        
        String query = "UPDATE " + tbl_chain + " SET chain_isinnonredundantset = 0;";        
        
        Integer numRowsAffected = 0;
        PreparedStatement statement = null;
        
        try {
            statement = dbc.prepareStatement(query);
                                
            numRowsAffected = statement.executeUpdate();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: markAllChainsAsNonRepresentative: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: markAllChainsAsNonRepresentative: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: markAllChainsAsNonRepresentative: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        } 
       
        return numRowsAffected;
    }
    
    
    /**
     * Updates the part of representative chain set status of the given chain.
     * @param pdb_id the PDB ID, e.g., "7tim"
     * @param chain the chain name, e.g., "A"
     * @param targetValue true for chains which are part of the set, false otherwise
     * @return the number of updated rows in the DB
     * @throws SQLException if DB stuff goes wrong
     */
    public static Integer markChainRepresentative(String pdb_id, String chain, boolean targetValue) throws SQLException {
        
        String query = "UPDATE " + tbl_chain + " SET chain_isinnonredundantset = ? WHERE pdb_id = ? AND chain_name = ?;";        
        
        Integer numRowsAffected = 0;
        PreparedStatement statement = null;
        
        try {
            statement = dbc.prepareStatement(query);
            
            statement.setInt(1, (targetValue == true ? 1 : 0));
            statement.setString(2, pdb_id);
            statement.setString(3, chain);
                                
            numRowsAffected = statement.executeUpdate();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: markChainRepresentative: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: markChainRepresentative: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: markChainRepresentative: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        } 
       
        return numRowsAffected;
    }
    
    /**
     * Calls init, which connects to the DB, with the settings from the currently loaded settings (from cfg file or internal if no cfg file/unset in it).
     * @return whether the connection succeeded
     */
    public static Boolean initUsingDefaults() {
        return init(Settings.get("plcc_S_db_name"), Settings.get("plcc_S_db_host"), Settings.getInteger("plcc_I_db_port"), Settings.get("plcc_S_db_username"), Settings.get("plcc_S_db_password"), false);
    }

    /**
     * Connects to the database using the DB address and credentials defined during the call to init().
     * @return Whether a connection to the DB could be established.
     */
    private static Boolean connect(Boolean setAutoCommit) {

        Boolean conOK = false;

        try {
            dbc = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            dbc.setAutoCommit(setAutoCommit);
            dbmd = dbc.getMetaData();
            sql = dbc.createStatement();
            conOK = true;
        } catch (SQLException e) {
            //System.err.println("ERROR: Could not connect to database at '" + dbURL + "'.");
            System.err.println("ERROR: Could not connect to database at '" + dbURL + "' with user '" + dbUsername + "'.");
            System.err.println("ERROR: The error message was: '" + e.getMessage() + "'.");
            System.exit(1);
        }

        String dbProductName = null;
        String dbProductVersion = null;
        try {
            dbProductName = dbmd.getDatabaseProductName();
            dbProductVersion = dbmd.getDatabaseProductVersion();
            conOK = true;
        } catch (SQLException e) {
            // Something didn't work out if this failed.
            System.err.println("ERROR: DB Connection failed: '" + e.getMessage() + "'.");
            conOK = false;
        }
        
        if(conOK) {
            try {                
                if(! Settings.getBoolean("plcc_B_silent")) {
                    System.out.println("Connection to " + dbProductName + " " + dbProductVersion + " successful. Autocommit is " + (dbc.getAutoCommit() ? "on" : "off") + ".");                    
                }
            } catch(Exception e) {
                // settings may be missing and the getBoolean call may thus crash, just ignore it.
            }
            
        }

        return (conOK);
    }

    
    /**
     * Tries to set autocommit on the current DB connection.
     * @param v the value to set autocommit to
     * @return whether the value was set (it is not set if the dbc is null or closed)
     */
    public boolean setAutoCommit(boolean v) {
        if(dbc != null) {
            try {
                if(! dbc.isClosed()) {
                    dbc.setAutoCommit(v);
                    return true;
                }
            } catch(SQLException e) {
                DP.getInstance().e("DBManager", "Could not set autocommit: '" + e.getMessage() + "'.");
                return false;
            }
        }
        return false;
    }
    
    /**
     * Computes the pairwise graphlet similarity scores between all protein chains in the DB, using the given graph type. Note that graphlet counts for all the graphs have already to exist in the database!
     * Also note that this function is gonna take a lot of time AND use a lot of memory if you have a large DB.
     * 
     * @param graphType the graph type to use for comparison of the graphlet scores, currently only "albe" is supported.
     * @param numberOfTopScoresToSavePerPair all pairwise scores are computes, but you may want to keep only the 10 most similar scores. That is what this is for. If you set it to null, all scores will be kept, which may leave you with a vast database filled with mainly useless stuff.
     * @return a long array of size 3: first position holds the number of chains found in the DB, second holds number of graphlet counts found in DB for these chains, third holds number of computed scores (or PG pairs), fourth holds the number of scores stored in the database
     */
    public static Long[] computeGraphletSimilarityScoresForWholeDatabaseAndStoreBest(String graphType, Integer numberOfTopScoresToSavePerPair) {
        
        /** If you set fakeIt to true, this function will write fake graphlet degree 
         distributions to the DB before computing scores. This is useful if you are
         coding on a Windows machine where you cannot run graphletanalyzer to compute
         the real graphlet distributions. */
        boolean fakeIt = false;
        if(fakeIt) {
            DP.getInstance().w("DBManager", "WARNING: DEBUG mode set, writing fake graphlet distributions to database for testing purposes!");
        }
        
        if(! graphType.equals(ProtGraph.GRAPHTYPE_ALBE)) {
            DP.getInstance().w("DBManager", "computeGraphletSimilarityScoresForWholeDatabaseAndStoreBest(): Graphlets for your chose graph type '" + graphType + "' may not be in the database. By default, we compute \"albe\" graphlets only.");
        }
        
        Long numScoresComputed = 0L;
        Long numScoresSaved = 0L;
        ArrayList<ArrayList<String>> allChains = DBManager.getAllPDBIDsandChains(); // these are more than 200,000 chains if the while PDB is in the database
        
        Long numChainsFound = ((Integer)allChains.size()).longValue();
        Long numGraphletsFound = 0L;
        
        Double[] src_graphlets, cmp_graphlets;
        
        String src_pdb_id, src_chain_name, cmp_pdb_id, cmp_chain_name;
        Double[] src_scores;
        ArrayList<String> src_row, cmp_row;
        for(int i = 0; i < allChains.size(); i++) {
            src_row = allChains.get(i);
            src_pdb_id = src_row.get(0);
            src_chain_name = src_row.get(1);
            src_scores = new Double[allChains.size()];
            src_graphlets = null;
            
            
            // some status output never hurts
            if(i % 5 == 0) {
                System.out.println("  At chain #" + i + " of " + allChains.size() + ".");
            }
            
            if(fakeIt) {
                try {
                    System.out.println("  Writing fake graphlet counts for " + src_pdb_id + " chain " + src_chain_name + " to DB.");
                    DBManager.writeNormalizedGraphletsToDB(src_pdb_id, src_chain_name, ProtGraph.GRAPHTYPE_INT_ALBE, SimilarityByGraphlets.getRandDoubleArray(30));
                } catch(SQLException e) {
                    System.err.println("    FAILED, could not write fake graphlet counts.");
                }
            }
            
            try {
                src_graphlets = DBManager.getNormalizedGraphletCounts(src_pdb_id, src_chain_name, graphType);            
            }
            catch(SQLException e) {
                DP.getInstance().e("DBManager", "Could not get src_graphlets for " + src_pdb_id + " " + src_chain_name + " " +  graphType + ": '" + e.getMessage() + "', skipping.");
                continue;
            }
            
            if(src_graphlets == null) { 
                continue;
            }
            else {
                numGraphletsFound++;
            }
            
            
            for(int j = 0; j < allChains.size(); j++) {
                cmp_row = allChains.get(j);
                cmp_pdb_id = cmp_row.get(0);
                cmp_chain_name = cmp_row.get(1);
                cmp_graphlets = null;
                
                if(i == j) {
                    continue;
                }
                
                if(fakeIt) {      
                    
                    try {
                        System.out.println("  Writing fake graphlet counts for " + cmp_pdb_id + " chain " + cmp_chain_name + " to DB.");
                        DBManager.writeNormalizedGraphletsToDB(cmp_pdb_id, cmp_chain_name, ProtGraph.GRAPHTYPE_INT_ALBE, SimilarityByGraphlets.getRandDoubleArray(30));
                        
                        // if we write new graphlets, we should delete the old scores based on them:
                        Long graphID = DBManager.getProteinGraphDBidsOf(src_pdb_id, src_chain_name).get(ProtGraph.GRAPHTYPE_ALBE);
                        if(graphID > 0) {
                            int numDelTarget = DBManager.deleteAllGraphletSimilaritiesFromDBForTargetGraph(graphID);
                            int numDelSource = DBManager.deleteAllGraphletSimilaritiesFromDBForSourceGraph(graphID);
                            System.out.println("  Inserted new fake graphlets and deleted old scores of the graph (" + numDelSource + " source, " + numDelTarget + " target scores).");
                        }
                    } catch(SQLException e) {
                        System.err.println("    FAILED, could not write fake graphlet counts.");
                    }
                    
                }
                
                try {
                    cmp_graphlets = DBManager.getNormalizedGraphletCounts(cmp_pdb_id, cmp_chain_name, graphType);            
                }
                catch(SQLException e) {
                    DP.getInstance().e("DBManager", "Could not get cmp_graphlets for " + cmp_pdb_id + " " + cmp_chain_name + " " +  graphType + ": '" + e.getMessage() + "', skipping.");                    
                }
                
                if(cmp_graphlets == null) {  
                    src_scores[j] = null; 
                }
                else {        
                    numGraphletsFound++;
                    src_scores[j] = SimilarityByGraphlets.getRelativeGraphletFrequencyDistanceNormalized(src_graphlets, cmp_graphlets);
                    numScoresComputed++;
                }
            }
            
            // Now we have a long list of score for src_pdb_id and src_chain_name. Sort it and write best to the database.
            String[] allPDBChains = new String[allChains.size()];
            for(int k = 0; k < allChains.size(); k++) {
                allPDBChains[k] = allChains.get(k).get(0) + allChains.get(k).get(1);    // add PDB id + chain, e.g., "7timA"
            }
            
            //PlccUtilities.multiSortUniqueArrays(src_scores, allPDBChains);    // cannot use this, scores are not unique!
            Double scoreTooLarge = 1000.0;
            PlccUtilities.replaceNullValuesInArrayWith(src_scores, scoreTooLarge);
            PlccUtilities.multiQuickSortTS(src_scores, allPDBChains);
            
            if(numberOfTopScoresToSavePerPair == null) {
                numberOfTopScoresToSavePerPair = allPDBChains.length;
            }
            
            if(numberOfTopScoresToSavePerPair > allPDBChains.length) {
                numberOfTopScoresToSavePerPair = allPDBChains.length;
            }
            
            System.out.println("Similarity of all DB chains to " + src_pdb_id + " chain " + src_chain_name + ":");
            for(int k = 0; k < numberOfTopScoresToSavePerPair; k++) {
                if(src_scores[k] != null) {
                    if(src_scores[k] < scoreTooLarge) {
                        System.out.println("  #" + k + ": " + allPDBChains[k] + " with score " + src_scores[k] + ".");
                        try {
                            DBManager.writeGraphletSimilarityScoreToDB(src_pdb_id, src_chain_name, allPDBChains[k].substring(0, 4), allPDBChains[k].substring(4), graphType, src_scores[k]);
                        } catch (SQLException e) {
                            System.err.println("Could not write graphlet similarity score to DB: '" + e.getMessage() + "'.");
                        }
                        numScoresSaved++;
                    }
                }
            }
            
        }
        
        return new Long[]{ numChainsFound, numGraphletsFound, numScoresComputed, numScoresSaved };
    }
    
    /**
     * Checks whether a DB connection exists. Tries to establish it if not.
     * @return: Whether a DB connection could be established in the end.
     */
    private static Boolean ensureConnection(Boolean setAutoCommit) {

        try {
            dbc.getMetaData();
            dbc.setAutoCommit(setAutoCommit);
        } catch (SQLException e) {
            return (connect(setAutoCommit));
        }

        return (true);
    }
        

    /**
     * Determines whether the underlying DBMS supports transactions.
     * @return true if it does, false otherwise
     */
    boolean supportsTransactions() throws SQLException {

        //ensureConnection(Settings.getBoolean("plcc_B_db_use_autocommit"));

        return (dbc.getMetaData().supportsTransactions());
    }

    /**
     * Executes the SQL insert query 'query' and returns the number of inserted rows if it succeeds, -1 otherwise.
     * WARNING: This does not do any checks on the input so do not expose this to user input.
     * @param query the SQL query
     * @return The number of inserted rows if it succeeds, -1 otherwise.
     */
    public static int doInsertQuery(String query) {

        //ensureConnection(Settings.getBoolean("plcc_B_db_use_autocommit"));

        PreparedStatement ps = null;
        try {
            ps = dbc.prepareStatement(query);
            return (ps.executeUpdate());          // num rows affected
        } catch (SQLException e) {
            DP.getInstance().e("doInsertQuery(): SQL statement '" + query + "' failed: '" + e.getMessage() + "'.");
            //DP.getInstance().e("The error message was: '" + e.getMessage() + "'.");
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    DP.getInstance().w("doInsertQuery(): Could not close prepared statement: '" + ex.getMessage() + "'.");
                    //DP.getInstance().w("The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }
    }

    /**
     * Executes the SQL update query 'query' and returns the number of updated rows if it succeeds, -1 otherwise.
     * WARNING: This does not do any checks on the input so do not expose this to user input.
     * @param query the SQL query
     * @return The number of updated rows if it succeeds, -1 otherwise.
     */
    public static int doUpdateQuery(String query) {

        //ensureConnection(Settings.getBoolean("plcc_B_db_use_autocommit"));

        PreparedStatement ps = null;
        try {
            ps = dbc.prepareStatement(query);
            return (ps.executeUpdate());          // num rows affected
        } catch (SQLException e) {
            DP.getInstance().w("doUpdateQuery(): SQL statement '" + query + "' failed: '" + e.getMessage() + "'.");
            //DP.getInstance().w("The error message was: '" + e.getMessage() + "'.");
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    DP.getInstance().w("doUpdateQuery(): Could not close prepared statement: '" + ex.getMessage() + "'.");
                    //DP.getInstance().w("The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }
    }

    /**
     * Executes the SQL delete query 'query' and returns the number of updated rows if it succeeds, -1 otherwise.
     * WARNING: This does not do any checks on the input so do not expose this to user input.
     * @param query the SQL query
     * @return The number of deleted rows if it succeeds, -1 otherwise.
     */
    public static int doDeleteQuery(String query) {

        //ensureConnection(Settings.getBoolean("plcc_B_db_use_autocommit"));

        PreparedStatement ps = null;
        try {
            ps = dbc.prepareStatement(query);
            return ps.executeUpdate();           // num rows affected
        } catch (SQLException e) {
            DP.getInstance().w("doDeleteQuery(): SQL statement '" + query + "' failed: '" + e.getMessage() + "'.");
            //DP.getInstance().w("The error message was: '" + e.getMessage() + "'.");
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    DP.getInstance().w("doDeleteQuery(): Could not close prepared statement: '" + ex.getMessage() + "'.");
                    //DP.getInstance().w("The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }
    }

    
    /**
     * Determines the number of chains in the database.
     * @return the chain count
     */
    public static Integer countChainsInDB() {
        String query = "SELECT count(*) FROM plcc_chain";
        ArrayList<ArrayList<String>> tableData = DBManager.doSelectQuery(query);
        
        if(tableData == null) {
            System.err.println("ERROR: Could not count chains in database.");
            return -1;
        }
        else {
            Integer num = -1;
            try {
                num = Integer.parseInt(tableData.get(0).get(0));
            } catch(Exception e) {
                System.err.println("ERROR: Database count result parsing failed '" + e.getMessage() + "'.");
            }
            return num;
        }
    }
    
    /**
     * Executes a select query. WARNING: This does not do any checks on the input so do not expose this to user input.
     * @param query the SQL query
     * @return the data as 2D matrix of Strings.
     */
    public static ArrayList<ArrayList<String>> doSelectQuery(String query) {

        //ensureConnection(Settings.getBoolean("plcc_B_db_use_autocommit"));

        ResultSet rs = null;
        PreparedStatement ps = null;
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData;
        ArrayList<String> rowData = null;

        int count;

        try {
            ps = dbc.prepareStatement(query);
            rs = ps.executeQuery();
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();
            tableData = new ArrayList<ArrayList<String>>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            return (tableData);
        } catch (SQLException e) {
            DP.getInstance().w("doDeleteQuery(): SQL statement '" + query + "' failed.: '" + e.getMessage() + "'.");
            System.exit(1);
            return (null);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    DP.getInstance().w("doSelectQuery(): Could not close result set: '" + ex.getMessage() + "'.");
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    DP.getInstance().w("doSelectQuery(): Could not close prepared statement: '" + ex.getMessage() + "'.");
                }
            }
        }

    }

    /**
     * Commits pending queries. Returns true if it called commit on the connection object, false otherwise (e.g., connection was null or closed).
     * @return true if it called commit on the connection object, false otherwise (e.g., connection was null or closed).
     */
    public static Boolean commit() {
        if (dbc != null) {
            try {
                if ( ! dbc.isClosed()) {                    
                    dbc.commit();                    
                    return (true);
                } else {
                    return (false);        // closed
                }
            } catch (SQLException e) {
                DP.getInstance().w("commit(): Could not commit: '" + e.getMessage() + "'.");                
                return (false);
            }
        } else {
            // there is no connection object
            return (false);
        }
        
    }
    
    /**
     * Closes the DB connection and commit pending queries unless autocommit is set.
     * @return Whether the connection could be closed.
     */
    public static Boolean closeConnection() {

        if (dbc != null) {
            try {
                if (!dbc.isClosed()) {
                    try {
                        if ( ! dbc.getAutoCommit()) {
                            dbc.commit();
                        } 
                    } catch (SQLException e2) {
                        DP.getInstance().w("closeConnection(): Commit: '" + e2.getMessage() + "'.");                
                        return (false);
                    }
                    dbc.close();
                    return (true);
                } else {
                    return (true);        // already closed
                }
            } catch (SQLException e) {
                DP.getInstance().w("closeConnection(): Could not close DB connection: '" + e.getMessage() + "'.");                
                return (false);
            }
        } else {
            // there is no connection object
            return (true);
        }
    }

    /**
     * Drops (=deletes) all PLCC tables in the database and all of their content.
     * @return whether it worked out
     */
    public static Boolean dropTables() {
        //ensureConnection(Settings.getBoolean("plcc_B_db_use_autocommit"));
        Boolean res = false;

        try {
            doDeleteQuery("DROP TABLE " + tbl_protein + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_chain + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_sse + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_ssecontact + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_ssecontact_complexgraph + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_complex_contact_stats + " CASCADE;");            
            doDeleteQuery("DROP TABLE " + tbl_proteingraph + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_foldinggraph + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_complexgraph + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_graphletcount + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_nm_ssetoproteingraph + ";");
            doDeleteQuery("DROP TABLE " + tbl_nm_ssetofoldinggraph + ";");
            doDeleteQuery("DROP TABLE " + tbl_graphtypes + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_contacttypes + ";");            
            doDeleteQuery("DROP TABLE " + tbl_motiftype + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_motif + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_nm_chaintomotif + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_complexcontacttypes + ";");
            doDeleteQuery("DROP TABLE " + tbl_ssetypes + ";");
            doDeleteQuery("DROP TABLE " + tbl_ligand + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_nm_ligandtochain + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_fglinnot + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_secondat + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_graphletsimilarity + " CASCADE;");
            
            //doDeleteQuery("DROP TABLE " + tbl_fglinnot_alpha + " CASCADE;");
            //doDeleteQuery("DROP TABLE " + tbl_fglinnot_beta + " CASCADE;");
            //doDeleteQuery("DROP TABLE " + tbl_fglinnot_albe + " CASCADE;");
            //doDeleteQuery("DROP TABLE " + tbl_fglinnot_alphalig + " CASCADE;");
            //doDeleteQuery("DROP TABLE " + tbl_fglinnot_betalig + " CASCADE;");
            //doDeleteQuery("DROP TABLE " + tbl_fglinnot_albelig + " CASCADE;");
            

            // The indices get dropped with the tables.
            //doDeleteQuery("DROP INDEX plcc_idx_chain_insert;");
            //doDeleteQuery("DROP INDEX plcc_idx_sse_insert;");

            res = true;      // Not really, need to check all of them. But errors are written to STDERR by the methods, so the user will know if something went wrong

        } catch (Exception e) {
            System.err.println("ERROR: Drop tables SQL failed: '" + e.getMessage() + "'.");
            res = false;
        }

        return (res);

    }

    /**
     * Creates the statistics tables in the database.
     * Note that you have to create the DB and the DB user and set the credentials in the plcc config file.
     * 
     * To create the DB stuff, use the 'psql' shell command as the DB admin account (usually 'postgres' on UNIX).
     * 
     * postgres@srv> psql
     * psql> CREATE ROLE vplg WITH LOGIN;
     * psql> CREATE DATABASE vplg OWNER vplg;
     * psql> \q
     * postgre@srv>
     * 
     * Also ensure that the user vplg is allowed to connect using password in pg_hba.conf.
     * 
     * @return Whether they could be created.
     */
    public static Boolean createTables() {


        //ensureConnection(Settings.getBoolean("plcc_B_db_use_autocommit"));
        Boolean res = false;

        try {
            // create tables
            
            // various types encoded by integers. these tables should be removed in the future and the values stored as string directly instead.
            doInsertQuery("CREATE TABLE " + tbl_ssetypes + " (ssetype_id int not null primary key,  ssetype_text text not null);");
            doInsertQuery("CREATE TABLE " + tbl_contacttypes + " (contacttype_id int not null primary key,  contacttype_text text not null);");
            doInsertQuery("CREATE TABLE " + tbl_complexcontacttypes + " (complexcontacttype_id int not null primary key,  complexcontacttype_text text not null);");
            doInsertQuery("CREATE TABLE " + tbl_graphtypes + " (graphtype_id int not null primary key,  graphtype_text text not null);");
            
            doInsertQuery("CREATE TABLE " + tbl_protein + " (pdb_id varchar(4) primary key, header text not null, title text not null, experiment text not null, keywords text not null, resolution real not null);");
            doInsertQuery("CREATE TABLE " + tbl_chain + " (chain_id serial primary key, chain_name varchar(2) not null, mol_name text not null, organism_scientific text not null, organism_common text not null, pdb_id varchar(4) not null references " + tbl_protein + " ON DELETE CASCADE, chain_isinnonredundantset smallint DEFAULT 0);");
            doInsertQuery("CREATE TABLE " + tbl_sse + " (sse_id serial primary key, chain_id int not null references " + tbl_chain + " ON DELETE CASCADE, dssp_start int not null, dssp_end int not null, pdb_start varchar(20) not null, pdb_end varchar(20) not null, sequence text not null, sse_type int not null references " + tbl_ssetypes + " ON DELETE CASCADE, lig_name varchar(5), position_in_chain int);");
            doInsertQuery("CREATE TABLE " + tbl_secondat + " (secondat_id serial primary key, sse_id int not null references " + tbl_sse + " ON DELETE CASCADE, alpha_fg_number int, alpha_fg_foldname varchar(2), alpha_fg_position int, beta_fg_number int, beta_fg_foldname varchar(2), beta_fg_position int, albe_fg_number int, albe_fg_foldname varchar(2), albe_fg_position int, alphalig_fg_number int, alphalig_fg_foldname varchar(2), alphalig_fg_position int, betalig_fg_number int, betalig_fg_foldname varchar(2), betalig_fg_position int, albelig_fg_number int, albelig_fg_foldname varchar(2), albelig_fg_position int);");
            doInsertQuery("CREATE TABLE " + tbl_ssecontact + " (contact_id serial primary key, sse1 int not null references " + tbl_sse + " ON DELETE CASCADE, sse2 int not null references " + tbl_sse + " ON DELETE CASCADE, contact_type int not null references " + tbl_contacttypes + " ON DELETE CASCADE, check (sse1 < sse2));");
            doInsertQuery("CREATE TABLE " + tbl_ssecontact_complexgraph + " (ssecontact_complexgraph_id serial primary key, sse1 int not null references " + tbl_sse + " ON DELETE CASCADE, sse2 int not null references " + tbl_sse + " ON DELETE CASCADE, complex_contact_type int not null references " + tbl_complexcontacttypes + " ON DELETE CASCADE check (sse1 < sse2));");            
            doInsertQuery("CREATE TABLE " + tbl_complex_contact_stats + " (complex_contact_id serial primary key, chain1 int not null references " + tbl_chain + " ON DELETE CASCADE, chain2 int not null references " + tbl_chain + " ON DELETE CASCADE, contact_num_HH int not null, contact_num_HS int not null, contact_num_HL int not null, contact_num_SS int not null, contact_num_SL int not null, contact_num_LL int not null, contact_num_DS int not null);");
            doInsertQuery("CREATE TABLE " + tbl_proteingraph + " (graph_id serial primary key, chain_id int not null references " + tbl_chain + " ON DELETE CASCADE, graph_type int not null references " + tbl_graphtypes + ", graph_string_gml text, graph_string_kavosh text, graph_string_dotlanguage text, graph_string_plcc text, graph_string_json text, graph_string_xml text, graph_image_png text, graph_image_svg text, graph_image_pdf text, filepath_graphfile_gml text, filepath_graphfile_kavosh text, filepath_graphfile_plcc text, filepath_graphfile_dotlanguage text, filepath_graphfile_json text, filepath_graphfile_xml text, sse_string text, graph_containsbetabarrel int DEFAULT 0);");
            doInsertQuery("CREATE TABLE " + tbl_foldinggraph + " (foldinggraph_id serial primary key, parent_graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, fg_number int not null, fold_name varchar(2) not null, first_vertex_position_in_parent int not null, graph_string_gml text, graph_string_kavosh text, graph_string_dotlanguage text, graph_string_plcc text, graph_string_json text, graph_string_xml text, sse_string text, graph_containsbetabarrel int DEFAULT 0);");
            doInsertQuery("CREATE TABLE " + tbl_complexgraph + " (complexgraph_id serial primary key, pdb_id varchar(4) not null references " + tbl_protein + " ON DELETE CASCADE, graph_string_gml text, graph_string_kavosh text, graph_image_svg text, graph_image_png text);");
            doInsertQuery("CREATE TABLE " + tbl_motiftype + " (motiftype_id serial primary key, motiftype_name varchar(40));");
            doInsertQuery("CREATE TABLE " + tbl_motif + " (motif_id serial primary key, motiftype_id int not null references " + tbl_motiftype + " ON DELETE CASCADE, motif_name varchar(40), motif_abbreviation varchar(9));");
                        
            
            /**
             * The contents of the graphlet_counts[55] SQL array is as follows (from Tatianas thesis, pp. 36-37):
             *   The structure of the feature vector with the length 55:
             *   - The ﬁrst 29 entries are unlabeled graphlets in the order of their appearance on the
             *   ﬁgure 3.1 of Tatianas bachelor thesis:
             *       – 2 entries: unlabeled graphlets g1 , g2 with 3 vertices;
             *       – 6 entries: unlabeled graphlets g1 , g6 with 4 vertices;
             *       – 21 entries: unlabeled graphlets g1 , g21 with 5 vertices.
             *   - The rest 26 entries are the proposed labeled graphlets:
             *       – 4 entries: all labelings of the g1 (triangle) with 3 vertices: [HHH, HHE, HEE, EEE];
             *       – 6 entries: all labelings of the g2 (2-path) with 3 vertices: [HHH, HHE, EHE, HEH, HEE, EEE];
             *
             *       – 10 entries: all labelings of the g6 (3-path) with 4 vertices: [HHHH, HHHE, EHHE, HHEH, HHEE, EHEH, EHEE, HEEH, HEEE, EEEE];
             *       – 4 entries: biologically-motivated graphlets, which encode the structural motifs, see the illustration 3.2, in the following order:
             *          ∗ β − α − β motif: [EaHaE],
             *          ∗ β − β − β motif: [EaEaE],
             *          ∗ Greek key motif: [EaEaEaE],
             *          ∗ 4 parallel not necessarily adjacent β sheets as a part of a β-barrel structure: [EpEpEpE] with “H” standing for α-helices, “E” for β-sheets, “p” for parallel and “a” for anti-parallel orientation;
             *       – 2 entries: all labelings of the graphlet g1 with one vertex, or simply the vertices with the label “H” and the vertices with the label “E” added to get the distribution vertex labels.
             *
             */
            doInsertQuery("CREATE TABLE " + tbl_graphletcount + " (graphlet_id serial primary key, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, graphlet_counts decimal[55] not null);");
            doInsertQuery("CREATE TABLE " + tbl_nm_ssetoproteingraph + " (ssetoproteingraph_id serial primary key, sse_id int not null references " + tbl_sse + " ON DELETE CASCADE, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, position_in_graph int not null);");
            doInsertQuery("CREATE TABLE " + tbl_nm_ssetofoldinggraph + " (ssetofoldinggraph_id serial primary key, sse_id int not null references " + tbl_sse + " ON DELETE CASCADE, foldinggraph_id int not null references " + tbl_foldinggraph + " ON DELETE CASCADE, position_in_graph int not null);");
            doInsertQuery("CREATE TABLE " + tbl_nm_chaintomotif + " (chaintomotif_id serial primary key, chain_id int not null references " + tbl_chain + " ON DELETE CASCADE, motif_id int not null references " + tbl_motif + " ON DELETE CASCADE);");
            
            doInsertQuery("CREATE TABLE " + tbl_ligand + " (ligand_name3 varchar(3) primary key, ligand_longname varchar(300), ligand_formula varchar(300), ligand_synonyms varchar(300));");
            doInsertQuery("CREATE TABLE " + tbl_nm_ligandtochain + " (ligandtochain_id serial primary key, ligandtochain_chainid int not null references " + tbl_chain + " ON DELETE CASCADE, ligandtochain_ligandname3 varchar(3) not null references " + tbl_ligand + " ON DELETE CASCADE);");
            
            //doInsertQuery("CREATE TABLE " + tbl_fglinnot_alpha + " (linnotalpha_id serial primary key, linnot_foldinggraph_id int not null references " + tbl_foldinggraph + " ON DELETE CASCADE, ptgl_linnot_adj text, ptgl_linnot_red text, ptgl_linnot_key text, ptgl_linnot_seq text, firstvertexpos_adj int, firstvertexpos_red int, firstvertexpos_key int, firstvertexpos_seq int, filepath_linnot_image_adj_svg text, filepath_linnot_image_adj_png text, filepath_linnot_image_adj_pdf text, filepath_linnot_image_red_svg text, filepath_linnot_image_red_png text, filepath_linnot_image_red_pdf text, filepath_linnot_image_key_svg text, filepath_linnot_image_key_png text, filepath_linnot_image_key_pdf text, filepath_linnot_image_seq_svg text, filepath_linnot_image_seq_png text, filepath_linnot_image_seq_pdf text);");
            //doInsertQuery("CREATE TABLE " + tbl_fglinnot_beta + " (linnotbeta_id serial primary key, linnot_foldinggraph_id int not null references " + tbl_foldinggraph + " ON DELETE CASCADE, ptgl_linnot_adj text, ptgl_linnot_red text, ptgl_linnot_key text, ptgl_linnot_seq text, firstvertexpos_adj int, firstvertexpos_red int, firstvertexpos_key int, firstvertexpos_seq int, filepath_linnot_image_adj_svg text, filepath_linnot_image_adj_png text, filepath_linnot_image_adj_pdf text, filepath_linnot_image_red_svg text, filepath_linnot_image_red_png text, filepath_linnot_image_red_pdf text, filepath_linnot_image_key_svg text, filepath_linnot_image_key_png text, filepath_linnot_image_key_pdf text, filepath_linnot_image_seq_svg text, filepath_linnot_image_seq_png text, filepath_linnot_image_seq_pdf text);");
            //doInsertQuery("CREATE TABLE " + tbl_fglinnot_albe + " (linnotalbe_id serial primary key, linnot_foldinggraph_id int not null references " + tbl_foldinggraph + " ON DELETE CASCADE, ptgl_linnot_adj text, ptgl_linnot_red text, ptgl_linnot_key text, ptgl_linnot_seq text, firstvertexpos_adj int, firstvertexpos_red int, firstvertexpos_key int, firstvertexpos_seq int, filepath_linnot_image_adj_svg text, filepath_linnot_image_adj_png text, filepath_linnot_image_adj_pdf text, filepath_linnot_image_red_svg text, filepath_linnot_image_red_png text, filepath_linnot_image_red_pdf text, filepath_linnot_image_key_svg text, filepath_linnot_image_key_png text, filepath_linnot_image_key_pdf text, filepath_linnot_image_seq_svg text, filepath_linnot_image_seq_png text, filepath_linnot_image_seq_pdf text);");
            //doInsertQuery("CREATE TABLE " + tbl_fglinnot_alphalig + " (linnotalphalig_id serial primary key, linnot_foldinggraph_id int not null references " + tbl_foldinggraph + " ON DELETE CASCADE, ptgl_linnot_adj text, ptgl_linnot_red text, ptgl_linnot_key text, ptgl_linnot_seq text, firstvertexpos_adj int, firstvertexpos_red int, firstvertexpos_key int, firstvertexpos_seq int, filepath_linnot_image_adj_svg text, filepath_linnot_image_adj_png text, filepath_linnot_image_adj_pdf text, filepath_linnot_image_red_svg text, filepath_linnot_image_red_png text, filepath_linnot_image_red_pdf text, filepath_linnot_image_key_svg text, filepath_linnot_image_key_png text, filepath_linnot_image_key_pdf text, filepath_linnot_image_seq_svg text, filepath_linnot_image_seq_png text, filepath_linnot_image_seq_pdf text);");
            //doInsertQuery("CREATE TABLE " + tbl_fglinnot_betalig + " (linnotbetalig_id serial primary key, linnot_foldinggraph_id int not null references " + tbl_foldinggraph + " ON DELETE CASCADE, ptgl_linnot_adj text, ptgl_linnot_red text, ptgl_linnot_key text, ptgl_linnot_seq text, firstvertexpos_adj int, firstvertexpos_red int, firstvertexpos_key int, firstvertexpos_seq int, filepath_linnot_image_adj_svg text, filepath_linnot_image_adj_png text, filepath_linnot_image_adj_pdf text, filepath_linnot_image_red_svg text, filepath_linnot_image_red_png text, filepath_linnot_image_red_pdf text, filepath_linnot_image_key_svg text, filepath_linnot_image_key_png text, filepath_linnot_image_key_pdf text, filepath_linnot_image_seq_svg text, filepath_linnot_image_seq_png text, filepath_linnot_image_seq_pdf text);");
            //doInsertQuery("CREATE TABLE " + tbl_fglinnot_albelig + " (linnotalbelig_id serial primary key, linnot_foldinggraph_id int not null references " + tbl_foldinggraph + " ON DELETE CASCADE, ptgl_linnot_adj text, ptgl_linnot_red text, ptgl_linnot_key text, ptgl_linnot_seq text, firstvertexpos_adj int, firstvertexpos_red int, firstvertexpos_key int, firstvertexpos_seq int, filepath_linnot_image_adj_svg text, filepath_linnot_image_adj_png text, filepath_linnot_image_adj_pdf text, filepath_linnot_image_red_svg text, filepath_linnot_image_red_png text, filepath_linnot_image_red_pdf text, filepath_linnot_image_key_svg text, filepath_linnot_image_key_png text, filepath_linnot_image_key_pdf text, filepath_linnot_image_seq_svg text, filepath_linnot_image_seq_png text, filepath_linnot_image_seq_pdf text);");           
            doInsertQuery("CREATE TABLE " + tbl_fglinnot + " (linnot_id serial primary key, denorm_pdb_id varchar(4) not null, denorm_chain_name varchar(2) not null, denorm_graph_type int not null, denorm_graph_type_string text not null, linnot_foldinggraph_id int not null references " + tbl_foldinggraph + " ON DELETE CASCADE, ptgl_linnot_adj text, ptgl_linnot_red text, ptgl_linnot_key text, ptgl_linnot_seq text, firstvertexpos_adj int, firstvertexpos_red int, firstvertexpos_key int, firstvertexpos_seq int, filepath_linnot_image_adj_svg text, filepath_linnot_image_adj_png text, filepath_linnot_image_adj_pdf text, filepath_linnot_image_red_svg text, filepath_linnot_image_red_png text, filepath_linnot_image_red_pdf text, filepath_linnot_image_key_svg text, filepath_linnot_image_key_png text, filepath_linnot_image_key_pdf text, filepath_linnot_image_seq_svg text, filepath_linnot_image_seq_png text, filepath_linnot_image_seq_pdf text, filepath_linnot_image_def_svg text, filepath_linnot_image_def_png text, filepath_linnot_image_def_pdf text, num_sses int);");
            doInsertQuery("CREATE TABLE " + tbl_graphletsimilarity + " (graphletsimilarity_id serial primary key, graphletsimilarity_sourcegraph int not null references " + tbl_proteingraph + " ON DELETE CASCADE, graphletsimilarity_targetgraph int not null references " + tbl_proteingraph + " ON DELETE CASCADE, score numeric);");

            // set constraints
            doInsertQuery("ALTER TABLE " + tbl_protein + " ADD CONSTRAINT constr_protein_uniq UNIQUE (pdb_id);");
            doInsertQuery("ALTER TABLE " + tbl_chain + " ADD CONSTRAINT constr_chain_uniq UNIQUE (chain_name, pdb_id);");
            doInsertQuery("ALTER TABLE " + tbl_sse + " ADD CONSTRAINT constr_sse_uniq UNIQUE (chain_id, dssp_start, dssp_end);");
            doInsertQuery("ALTER TABLE " + tbl_ssecontact + " ADD CONSTRAINT constr_contact_uniq UNIQUE (sse1, sse2);");
            doInsertQuery("ALTER TABLE " + tbl_complex_contact_stats + " ADD CONSTRAINT constr_complex_contact_uniq UNIQUE (chain1, chain2);");
            doInsertQuery("ALTER TABLE " + tbl_proteingraph + " ADD CONSTRAINT constr_graph_uniq UNIQUE (chain_id, graph_type);");
            doInsertQuery("ALTER TABLE " + tbl_foldinggraph + " ADD CONSTRAINT constr_foldgraph_uniq UNIQUE (parent_graph_id, fg_number);");
            doInsertQuery("ALTER TABLE " + tbl_graphletcount + " ADD CONSTRAINT constr_graphlet_uniq UNIQUE (graph_id);");
            doInsertQuery("ALTER TABLE " + tbl_nm_ligandtochain + " ADD CONSTRAINT constr_ligtochain_uniq UNIQUE (ligandtochain_chainid, ligandtochain_ligandname3);");
            doInsertQuery("ALTER TABLE " + tbl_nm_chaintomotif + " ADD CONSTRAINT constr_chaintomotif_uniq UNIQUE (chain_id, motif_id);");
            doInsertQuery("ALTER TABLE " + tbl_secondat + " ADD CONSTRAINT constr_secondat_uniq UNIQUE (sse_id);");
            doInsertQuery("ALTER TABLE " + tbl_graphletsimilarity + " ADD CONSTRAINT constr_graphletsimilarity_uniq UNIQUE (graphletsimilarity_sourcegraph, graphletsimilarity_targetgraph);");
            
            //doInsertQuery("ALTER TABLE " + tbl_fglinnot_alpha + " ADD CONSTRAINT constr_fglinnotalpha_uniq UNIQUE (linnot_foldinggraph_id);");
            //doInsertQuery("ALTER TABLE " + tbl_fglinnot_beta + " ADD CONSTRAINT constr_fglinnotbeta_uniq UNIQUE (linnot_foldinggraph_id);");
            //doInsertQuery("ALTER TABLE " + tbl_fglinnot_albe + " ADD CONSTRAINT constr_fglinnotalbe_uniq UNIQUE (linnot_foldinggraph_id);");
            //doInsertQuery("ALTER TABLE " + tbl_fglinnot_alphalig + " ADD CONSTRAINT constr_fglinnotalphalig_uniq UNIQUE (linnot_foldinggraph_id);");
            //doInsertQuery("ALTER TABLE " + tbl_fglinnot_betalig + " ADD CONSTRAINT constr_fglinnotbetalig_uniq UNIQUE (linnot_foldinggraph_id);");
            //doInsertQuery("ALTER TABLE " + tbl_fglinnot_albelig + " ADD CONSTRAINT constr_fglinnotalbelig_uniq UNIQUE (linnot_foldinggraph_id);");
            
            // The constraint in the next line cannot be set, because the graph type is not stored (redundantly) in that table anymore. Get it from parent graphs.
            //doInsertQuery("ALTER TABLE " + tbl_fglinnot + " ADD CONSTRAINT constr_fglinnot_uniq UNIQUE (linnot_foldinggraph_id, linnot_graph_type);");
            
            // create views
            doInsertQuery("CREATE VIEW " + view_ssecontacts + " AS SELECT contact_id, least(sse1_type, sse2_type) sse1_type, greatest(sse1_type, sse2_type) sse2_type, sse1_lig_name, sse2_lig_name  FROM (SELECT k.contact_id, sse1.sse_type AS sse1_type, sse2.sse_type AS sse2_type, sse1.lig_name AS sse1_lig_name, sse2.lig_name AS sse2_lig_name FROM " + tbl_ssecontact + " k LEFT JOIN " + tbl_sse + " sse1 ON k.sse1=sse1.sse_id LEFT JOIN " + tbl_sse + " sse2 ON k.sse2=sse2.sse_id) foo;");
            doInsertQuery("CREATE VIEW " + view_graphs + " AS SELECT graph_id, pdb_id, chain_name, graphtype_text, graph_string_gml, sse_string, graph_containsbetabarrel, length(regexp_replace(sse_string, '[^E]', '', 'g')) as num_strands, length(regexp_replace(sse_string, '[^H]', '', 'g')) as num_helices, length(regexp_replace(sse_string, '[^L]', '', 'g')) as num_ligands FROM (SELECT k.graph_id, gt.graphtype_text, k.graph_string_gml, k.sse_string, k.graph_containsbetabarrel, chain.chain_name AS chain_name, chain.pdb_id AS pdb_id FROM " + tbl_proteingraph + " k LEFT JOIN " + tbl_chain + " chain ON k.chain_id=chain.chain_id LEFT JOIN " + tbl_graphtypes + " gt ON k.graph_type=gt.graphtype_id) bar;");
            doInsertQuery("CREATE VIEW " + view_foldinggraphs + " AS SELECT foldinggraph_id, parent_graph_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, sse_string, graph_containsbetabarrel FROM (SELECT fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM " + tbl_foldinggraph + " fg LEFT JOIN " + tbl_proteingraph + " pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN " + tbl_chain + " c ON pg.chain_id=c.chain_id LEFT JOIN " + tbl_graphtypes + " gt ON pg.graph_type=gt.graphtype_id) bar;");
            doInsertQuery("CREATE VIEW " + view_graphletsimilarity + " AS SELECT src_pdb_id, src_chain_name, cmp_pdb_id, cmp_chain_name, score, src_graphtype_text, cmp_graphtype_text, src_graph_id, cmp_graph_id FROM (SELECT gs.graphletsimilarity_sourcegraph as src_graph_id, gs.graphletsimilarity_targetgraph AS cmp_graph_id, gs.score, src_gt.graphtype_text AS src_graphtype_text, cmp_gt.graphtype_text AS cmp_graphtype_text, src_chain.chain_name AS src_chain_name, cmp_chain.chain_name AS cmp_chain_name, src_chain.pdb_id AS src_pdb_id, cmp_chain.pdb_id AS cmp_pdb_id FROM " + tbl_graphletsimilarity + " gs INNER JOIN " + tbl_proteingraph + " src_pg ON gs.graphletsimilarity_sourcegraph = src_pg.graph_id LEFT JOIN " + tbl_chain + " src_chain ON src_pg.chain_id = src_chain.chain_id LEFT JOIN " + tbl_graphtypes + " src_gt ON src_pg.graph_type = src_gt.graphtype_id INNER JOIN " + tbl_proteingraph + " cmp_pg ON gs.graphletsimilarity_targetgraph = cmp_pg.graph_id LEFT JOIN " + tbl_chain + " cmp_chain ON cmp_pg.chain_id = cmp_chain.chain_id LEFT JOIN " + tbl_graphtypes + " cmp_gt ON cmp_pg.graph_type = cmp_gt.graphtype_id) bar;");
            
            //doInsertQuery("CREATE VIEW " + view_fglinnotsalpha + " AS SELECT linnotalpha_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key FROM (SELECT la.linnotalpha_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM " + tbl_fglinnot_alpha + " la LEFT JOIN " + tbl_foldinggraph + " fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN " + tbl_proteingraph + " pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN " + tbl_chain + " c ON pg.chain_id=c.chain_id LEFT JOIN " + tbl_graphtypes + " gt ON pg.graph_type=gt.graphtype_id) bar ;");
            //doInsertQuery("CREATE VIEW " + view_fglinnotsbeta + " AS SELECT linnotbeta_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key FROM (SELECT la.linnotbeta_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM " + tbl_fglinnot_beta + " la LEFT JOIN " + tbl_foldinggraph + " fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN " + tbl_proteingraph + " pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN " + tbl_chain + " c ON pg.chain_id=c.chain_id LEFT JOIN " + tbl_graphtypes + " gt ON pg.graph_type=gt.graphtype_id) bar ;");
            //doInsertQuery("CREATE VIEW " + view_fglinnotsalbe + " AS SELECT linnotalbe_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key FROM (SELECT la.linnotalbe_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM " + tbl_fglinnot_albe + " la LEFT JOIN " + tbl_foldinggraph + " fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN " + tbl_proteingraph + " pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN " + tbl_chain + " c ON pg.chain_id=c.chain_id LEFT JOIN " + tbl_graphtypes + " gt ON pg.graph_type=gt.graphtype_id) bar ;");
            //doInsertQuery("CREATE VIEW " + view_fglinnotsalphalig + " AS SELECT linnotalphalig_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key FROM (SELECT la.linnotalphalig_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM " + tbl_fglinnot_alphalig + " la LEFT JOIN " + tbl_foldinggraph + " fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN " + tbl_proteingraph + " pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN " + tbl_chain + " c ON pg.chain_id=c.chain_id LEFT JOIN " + tbl_graphtypes + " gt ON pg.graph_type=gt.graphtype_id) bar ;");
            //doInsertQuery("CREATE VIEW " + view_fglinnotsbetalig + " AS SELECT linnotbetalig_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key FROM (SELECT la.linnotbetalig_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM " + tbl_fglinnot_betalig + " la LEFT JOIN " + tbl_foldinggraph + " fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN " + tbl_proteingraph + " pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN " + tbl_chain + " c ON pg.chain_id=c.chain_id LEFT JOIN " + tbl_graphtypes + " gt ON pg.graph_type=gt.graphtype_id) bar ;");
            //doInsertQuery("CREATE VIEW " + view_fglinnotsalbelig + " AS SELECT linnotalbelig_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key FROM (SELECT la.linnotalbelig_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM " + tbl_fglinnot_albelig + " la LEFT JOIN " + tbl_foldinggraph + " fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN " + tbl_proteingraph + " pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN " + tbl_chain + " c ON pg.chain_id=c.chain_id LEFT JOIN " + tbl_graphtypes + " gt ON pg.graph_type=gt.graphtype_id) bar ;");
            doInsertQuery("CREATE VIEW " + view_fglinnots + " AS SELECT linnot_id, pdb_id, chain_name, graphtype_text, fg_number, fold_name, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_seq, firstvertexpos_key, filepath_linnot_image_adj_png, filepath_linnot_image_red_png, filepath_linnot_image_seq_png, filepath_linnot_image_key_png FROM (SELECT la.linnot_id, la.ptgl_linnot_adj, la.ptgl_linnot_red, la.ptgl_linnot_key, la.ptgl_linnot_seq, la.firstvertexpos_adj, la.firstvertexpos_red, la.firstvertexpos_seq, la.firstvertexpos_key, la.filepath_linnot_image_adj_png, la.filepath_linnot_image_red_png, la.filepath_linnot_image_seq_png, la.filepath_linnot_image_key_png, fg.foldinggraph_id, fg.fg_number, fg.parent_graph_id, fg.fold_name, fg.sse_string, fg.graph_containsbetabarrel, gt.graphtype_text, fg.graph_string_gml, c.chain_name AS chain_name, c.pdb_id AS pdb_id FROM " + tbl_fglinnot + " la LEFT JOIN " + tbl_foldinggraph + " fg ON la.linnot_foldinggraph_id = fg.foldinggraph_id LEFT JOIN " + tbl_proteingraph + " pg ON fg.parent_graph_id = pg.graph_id LEFT JOIN " + tbl_chain + " c ON pg.chain_id=c.chain_id LEFT JOIN " + tbl_graphtypes + " gt ON pg.graph_type=gt.graphtype_id) bar ;");
            
            doInsertQuery("CREATE VIEW " + view_sses + " AS SELECT s.sse_id, p.pdb_id, c.chain_name, s.position_in_chain, s.dssp_start, s.dssp_end, sset.ssetype_text FROM " + tbl_sse + " s INNER JOIN " + tbl_chain + " c ON s.chain_id = c.chain_id INNER JOIN " + tbl_protein + " p ON c.pdb_id = p.pdb_id INNER JOIN " + tbl_ssetypes + " sset ON s.sse_type = sset.ssetype_id;");
            doInsertQuery("CREATE VIEW " + view_secondat + " AS SELECT s.sse_id, p.pdb_id, c.chain_name, s.position_in_chain, s.dssp_start, s.dssp_end, sset.ssetype_text, sd.alpha_fg_number, sd.alpha_fg_foldname, sd.alpha_fg_position, sd.beta_fg_number, sd.beta_fg_foldname, sd.beta_fg_position, sd.albe_fg_number, sd.albe_fg_foldname, sd.albe_fg_position, sd.alphalig_fg_number, sd.alphalig_fg_foldname, sd.alphalig_fg_position, sd.betalig_fg_number, sd.betalig_fg_foldname, sd.betalig_fg_position, sd.albelig_fg_number, sd.albelig_fg_foldname, sd.albelig_fg_position FROM " + tbl_sse + " s INNER JOIN " + tbl_chain + " c ON s.chain_id = c.chain_id INNER JOIN " + tbl_protein + " p ON c.pdb_id = p.pdb_id INNER JOIN " + tbl_ssetypes + " sset ON s.sse_type = sset.ssetype_id INNER JOIN " + tbl_secondat + " sd ON s.sse_id = sd.sse_id;");
            doInsertQuery("CREATE VIEW " + view_graphlets + " AS SELECT p.pdb_id, c.chain_name, gt.graphtype_text, gc.graphlet_counts FROM " + tbl_graphletcount + " gc INNER JOIN " + tbl_proteingraph + " g ON gc.graph_id = g.graph_id INNER JOIN " + tbl_chain + " c ON g.chain_id = c.chain_id INNER JOIN " + tbl_protein + " p ON c.pdb_id = p.pdb_id INNER JOIN " + tbl_graphtypes + " gt ON  g.graph_type = gt.graphtype_id;");
            doInsertQuery("CREATE VIEW " + view_chainmotifs + " AS SELECT p.pdb_id, c.chain_name, m.motif_name FROM " + tbl_nm_chaintomotif + " c2m INNER JOIN " + tbl_motif + " m ON c2m.motif_id = m.motif_id INNER JOIN " + tbl_chain + " c ON c2m.chain_id = c.chain_id INNER JOIN " + tbl_protein + " p ON c.pdb_id = p.pdb_id;");
            
            // add comments on views
            doInsertQuery("COMMENT ON VIEW " + view_ssecontacts + " IS 'Easy overview of SSE contacts.';");
            doInsertQuery("COMMENT ON VIEW " + view_graphs + " IS 'Easy overview of graph information.';");
            doInsertQuery("COMMENT ON VIEW " + view_foldinggraphs + " IS 'Easy overview of folding graph information.';");
            
            doInsertQuery("COMMENT ON VIEW " + view_fglinnots + " IS 'Overview of linear notation information for all graph types, includes the PDB ID, chain name, graph type and fold number.';");
            doInsertQuery("COMMENT ON VIEW " + view_secondat + " IS 'Overview of secondat info for all SSEs.';");
            doInsertQuery("COMMENT ON VIEW " + view_graphlets + " IS 'Overview of graphlet info for all protein graphs.';");
            
            //doInsertQuery("COMMENT ON VIEW " + view_fglinnotsalpha + " IS 'Overview of alpha linear notation information, includes the PDB ID, chain name, graph type and fold number.';");
            //doInsertQuery("COMMENT ON VIEW " + view_fglinnotsbeta + " IS 'Overview of beta linear notation information, includes the PDB ID, chain name, graph type and fold number.';");
            //doInsertQuery("COMMENT ON VIEW " + view_fglinnotsalbe + " IS 'Overview of albe linear notation information, includes the PDB ID, chain name, graph type and fold number.';");
            //doInsertQuery("COMMENT ON VIEW " + view_fglinnotsalphalig + " IS 'Overview of alphalig linear notation information, includes the PDB ID, chain name, graph type and fold number.';");
            //doInsertQuery("COMMENT ON VIEW " + view_fglinnotsbetalig + " IS 'Overview of betalig linear notation information, includes the PDB ID, chain name, graph type and fold number.';");
            //doInsertQuery("COMMENT ON VIEW " + view_fglinnotsalbelig + " IS 'Overview of albelig linear notation information, includes the PDB ID, chain name, graph type and fold number.';");
            
            // add comments for tables
            doInsertQuery("COMMENT ON TABLE " + tbl_protein + " IS 'Stores information on a whole PDB file.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_complexcontacttypes + " IS 'Stores information on complex contact types like van-der-Waals and disulfide.';");            
            doInsertQuery("COMMENT ON TABLE " + tbl_chain + " IS 'Stores information on a protein chain.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_sse + " IS 'Stores information on a secondary structure element (SSE).';");
            doInsertQuery("COMMENT ON TABLE " + tbl_ssecontact + " IS 'Stores information on a contact between a pair of SSEs which are part of the same chain.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_ssecontact_complexgraph + " IS 'Stores information on a contact between a pair of SSEs which are part of different chains of the same protein.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_complex_contact_stats + " IS 'Stores statistical information on the atom contacts of a complex contact. Does NOT include info for SSEs or AAs involved.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_proteingraph + " IS 'Stores descriptions of the protein graph of a protein chain. Multiple of these exist for a chain due to alpha, beta, alphabeta, alphalig, betalig and alphabetalig versions.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_foldinggraph + " IS 'Stores descriptions of a folding graph, which is a connected component of a protein graph.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_complexgraph + " IS 'Stores descriptions of a complex graph.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_graphletcount + " IS 'Stores the graphlet counts for the different graphlets for a certain graph defined by pdbid, chain and graph type.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_nm_ssetoproteingraph + " IS 'Assigns SSEs to protein graphs. An SSE may be part of multiple graphs, e.g., alpha, alphalig, and albe.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_nm_ssetofoldinggraph + " IS 'Assigns SSEs to folding graphs. An SSE may be part of multiple folding graphs, e.g., alpha, alphalig, and albe. It cannot be part of multiple alpha folding graphs though.';");
            
            doInsertQuery("COMMENT ON TABLE " + tbl_ligand + " IS 'Stores information on a ligand. This is something like ICT in general.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_nm_ligandtochain + " IS 'Assigns a certain ligand to a protein chain, meaning that the chain contains a ligand of that type.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_nm_chaintomotif + " IS 'Assigns a certain motif to a protein chain, meaning that the chain contains that motif.';");
            
            doInsertQuery("COMMENT ON TABLE " + tbl_motif + " IS 'Stores information on a specific protein motif, e.g., globin fold.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_motiftype + " IS 'Stores information on a motif type, e.g., alpha motif or beta motif.';");
            
            doInsertQuery("COMMENT ON TABLE " + tbl_ssetypes + " IS 'Stores the names of the SSE types, e.g., 1=helix.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_contacttypes + " IS 'Stores the names of the contact types, e.g., 1=mixed.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_graphtypes + " IS 'Stores the names of the graph types, e.g., 1=alpha.';");

            //doInsertQuery("COMMENT ON TABLE " + tbl_fglinnot_alpha + " IS 'Stores the PTGL linear notation strings ADJ, RED, KEY and SEQ for a single fold of some alpha protein graph. Also stores file system paths to graph images.';");
            //doInsertQuery("COMMENT ON TABLE " + tbl_fglinnot_beta + " IS 'Stores the PTGL linear notation strings ADJ, RED, KEY and SEQ for a single fold of some beta protein graph. Also stores file system paths to graph images.';");
            //doInsertQuery("COMMENT ON TABLE " + tbl_fglinnot_albe + " IS 'Stores the PTGL linear notation strings ADJ, RED, KEY and SEQ for a single fold of some albe protein graph. Also stores file system paths to graph images.';");
            //doInsertQuery("COMMENT ON TABLE " + tbl_fglinnot_alphalig + " IS 'Stores the PTGL linear notation strings ADJ, RED, KEY and SEQ for a single fold of some alphalig protein graph. Also stores file system paths to graph images.';");
            //doInsertQuery("COMMENT ON TABLE " + tbl_fglinnot_betalig + " IS 'Stores the PTGL linear notation strings ADJ, RED, KEY and SEQ for a single fold of some betalig protein graph. Also stores file system paths to graph images.';");
            //doInsertQuery("COMMENT ON TABLE " + tbl_fglinnot_albelig + " IS 'Stores the PTGL linear notation strings ADJ, RED, KEY and SEQ for a single fold of some albelig protein graph. Also stores file system paths to graph images.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_fglinnot + " IS 'Stores the PTGL linear notation strings ADJ, RED, KEY and SEQ for a single fold (all graph types). Also stores file system paths to graph images.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_secondat + " IS 'Stores the position of a certain SSE in the folding graphs. For each graph type, the FG number (and name) in which the SSE occurs and its position in that fg are given. This table is ugly and does not follow good DB design rules, it is required for historical reasons though.';");
            
            
            // add comments for specific fields
            doInsertQuery("COMMENT ON COLUMN " + tbl_sse + ".sse_type IS '1=helix, 2=beta strand, 3=ligand, 4=other';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_ssecontact + ".contact_type IS '1=mixed, 2=parallel, 3=antiparallel, 4=ligand, 5=backbone';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_sse + ".lig_name IS 'The 3-letter ligand name from the PDB file and the RCSB ligand expo website. If this SSE is not a ligand SSE, this is null.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_type IS '1=alpha, 2=beta, 3=albe, 4=alphalig, 5=betalig, 6=albelig';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_string_gml IS 'The graph string in GML format';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_string_kavosh IS 'The graph string in Kavosh format format';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_image_svg IS 'The path to the SVG format file of the protein graph image, relative to plcc_S_graph_image_base_path';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_image_png IS 'The path to the PNG format file of the protein graph image, relative to plcc_S_graph_image_base_path';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_proteingraph + ".graph_image_pdf IS 'The path to the PDF format file of the protein graph image, relative to plcc_S_graph_image_base_path';");
            
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_HH IS 'Number of helix-helix contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_HS IS 'Number of helix-strand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_HL IS 'Number of helix-ligand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_SS IS 'Number of strand-strand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_SL IS 'Number of strand-ligand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_LL IS 'Number of ligand-ligand contacts between the chains.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_complex_contact_stats + ".contact_num_DS IS 'Number of disulfide bridge contacts between the chains.';");
            
            doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot + ".denorm_pdb_id IS 'Denormalization field, added for performance reasons.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot + ".linnot_foldinggraph_id IS 'The folding graph represented by this notation.';");
            //doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot_alpha + ".linnot_foldinggraph_id IS 'The folding graph represented by this notation, has to be an alpha graph.';");
            //doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot_beta + ".linnot_foldinggraph_id IS 'The folding graph represented by this notation, has to be a beta graph.';");
            //doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot_albe + ".linnot_foldinggraph_id IS 'The folding graph represented by this notation, has to be an albe graph.';");

            doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot + ".ptgl_linnot_adj IS 'The ADJ linear notation string of the folding graph.';");
            //doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot_alpha + ".ptgl_linnot_adj IS 'The ADJ linear notation string of the folding graph.';");
            //doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot_beta + ".ptgl_linnot_adj IS 'The ADJ linear notation string of the folding graph.';");
            //doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot_albe + ".ptgl_linnot_adj IS 'The ADJ linear notation string of the folding graph.';");
            
            doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot + ".firstvertexpos_adj IS 'The start vertex of the ADJ notation. This is the index in the protein graph, not in this connected component.';");
            //doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot_alpha + ".firstvertexpos_adj IS 'The start vertex of the ADJ notation. This is the index in the protein graph, not in this connected component.';");
            //doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot_beta + ".firstvertexpos_adj IS 'The start vertex of the ADJ notation. This is the index in the protein graph, not in this connected component.';");
            //doInsertQuery("COMMENT ON COLUMN " + tbl_fglinnot_albe + ".firstvertexpos_adj IS 'The start vertex of the ADJ notation. This is the index in the protein graph, not in this connected component.';");            

            
            doInsertQuery("COMMENT ON COLUMN " + tbl_secondat + ".alpha_fg_number IS 'The number of the alpha folding graph in which this SSE occurs. Note that it can occur in 0 or 1 alpha FGs (in 0 if it is not an alpha helix, in 1 otherwise). If it does not occur in any alpha FG, this is null.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_secondat + ".alpha_fg_foldname IS 'The fold name of the alpha folding graph in which this SSE occurs. Note that it can occur in 0 or 1 alpha FGs (in 0 if it is not an alpha helix, in 1 otherwise). If it does not occur in any alpha FG, this is null.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_secondat + ".alpha_fg_position IS 'The position of this SSE in the alpha FG defined by the field alpha_fg_number. Starts counting at 1.';");
            
            doInsertQuery("COMMENT ON COLUMN " + tbl_nm_ssetoproteingraph + ".position_in_graph IS 'The position of this SSE in the protein graph. Starts counting at 1.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_nm_ssetofoldinggraph + ".position_in_graph IS 'The position of this SSE in the folding graph. Starts counting at 1.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_foldinggraph + ".fg_number IS 'The connected component number within the parent protein graph. The order of the CCs is defined by the lowest vertex index (in the parent graph) of the CCs. Starts counting at 0.';");
            
            
            // add indices
            doInsertQuery("CREATE INDEX plcc_idx_chain_insert ON " + tbl_chain + " (pdb_id, chain_name);");         // for SELECTs during data insert
            doInsertQuery("CREATE INDEX plcc_idx_chain_pdb ON " + tbl_chain + " (pdb_id);");         // for SELECTs during data insert
            doInsertQuery("CREATE INDEX plcc_idx_sse_insert ON " + tbl_sse + " (dssp_start, chain_id);");           // for SELECTs during data insert

            doInsertQuery("CREATE INDEX plcc_idx_chain_fk ON " + tbl_chain + " (pdb_id);");                          // for JOINs, ON CASCADE, etc. (foreign key, FK)
            doInsertQuery("CREATE INDEX plcc_idx_sse_fk ON " + tbl_sse + " (chain_id);");                            // FK
            doInsertQuery("CREATE INDEX plcc_idx_sse_fk2 ON " + tbl_sse + " (sse_type);");
            doInsertQuery("CREATE INDEX plcc_idx_contact_fk1 ON " + tbl_ssecontact + " (sse1);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_contact_fk2 ON " + tbl_ssecontact + " (sse2);");
            doInsertQuery("CREATE INDEX plcc_idx_contact_type ON " + tbl_ssecontact + " (contact_type);");// FK
            doInsertQuery("CREATE INDEX plcc_idx_contact_complexgraph_fk1 ON " + tbl_ssecontact_complexgraph + " (sse1);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_contact_complexgraph_fk2 ON " + tbl_ssecontact_complexgraph + " (sse2);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_complex_contact_fk1 ON " + tbl_complex_contact_stats + " (chain1);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_complex_contact_fk2 ON " + tbl_complex_contact_stats + " (chain2);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_graph_fk ON " + tbl_proteingraph + " (chain_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_graph_graphtype ON " + tbl_proteingraph + " (graph_type);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_foldinggraph_fk ON " + tbl_foldinggraph + " (foldinggraph_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_foldinggraph_parent ON " + tbl_foldinggraph + " (parent_graph_id);");
            doInsertQuery("CREATE INDEX plcc_idx_foldinggraph_fgnum ON " + tbl_foldinggraph + " (fg_number);");// FK
            doInsertQuery("CREATE INDEX plcc_idx_graphlets_fk ON " + tbl_graphletcount + " (graph_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_ssetoproteingraph_fk1 ON " + tbl_nm_ssetoproteingraph + " (sse_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_ssetoproteingraph_fk2 ON " + tbl_nm_ssetoproteingraph + " (graph_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_ssetofoldinggraph_fk1 ON " + tbl_nm_ssetofoldinggraph + " (sse_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_ssetofoldinggraph_fk2 ON " + tbl_nm_ssetofoldinggraph + " (foldinggraph_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_chaintomotif_fk1 ON " + tbl_nm_chaintomotif + " (chain_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_chaintomotif_fk2 ON " + tbl_nm_chaintomotif + " (motif_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_ligand_name3 ON " + tbl_ligand + " (ligand_name3);");                       // FK
            //doInsertQuery("CREATE INDEX plcc_idx_fglinnotalpha_fk ON " + tbl_fglinnot_alpha + " (linnot_foldinggraph_id);");          // FK
            //doInsertQuery("CREATE INDEX plcc_idx_fglinnotbeta_fk ON " + tbl_fglinnot_beta + " (linnot_foldinggraph_id);");          // FK
            //doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbe_fk ON " + tbl_fglinnot_albe + " (linnot_foldinggraph_id);");          // FK
            //doInsertQuery("CREATE INDEX plcc_idx_fglinnotalphalig_fk ON " + tbl_fglinnot_alphalig + " (linnot_foldinggraph_id);");          // FK
            //doInsertQuery("CREATE INDEX plcc_idx_fglinnotbetalig_fk ON " + tbl_fglinnot_betalig + " (linnot_foldinggraph_id);");          // FK
            //doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbelig_fk ON " + tbl_fglinnot_albelig + " (linnot_foldinggraph_id);");          // FK
            //doInsertQuery("CREATE INDEX plcc_idx_fglinnot_fk ON " + tbl_fglinnot + " (linnot_foldinggraph_id);");          // FK
            
            // speed-up pattern op queries, e.g., WHERE ptgl_linnot_red like 'blah%'
            /*
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalpha_search_adj ON " + tbl_fglinnot_alpha + " (ptgl_linnot_adj text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalpha_search_red ON " + tbl_fglinnot_alpha + " (ptgl_linnot_red text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalpha_search_seq ON " + tbl_fglinnot_alpha + " (ptgl_linnot_seq text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalpha_search_key ON " + tbl_fglinnot_alpha + " (ptgl_linnot_key text_pattern_ops);");
            
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotbeta_search_adj ON " + tbl_fglinnot_beta + " (ptgl_linnot_adj text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotbeta_search_red ON " + tbl_fglinnot_beta + " (ptgl_linnot_red text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotbeta_search_seq ON " + tbl_fglinnot_beta + " (ptgl_linnot_seq text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotbeta_search_key ON " + tbl_fglinnot_beta + " (ptgl_linnot_key text_pattern_ops);");
            
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbe_search_adj ON " + tbl_fglinnot_albe + " (ptgl_linnot_adj text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbe_search_red ON " + tbl_fglinnot_albe + " (ptgl_linnot_red text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbe_search_seq ON " + tbl_fglinnot_albe + " (ptgl_linnot_seq text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbe_search_key ON " + tbl_fglinnot_albe + " (ptgl_linnot_key text_pattern_ops);");
            
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalphalig_search_adj ON " + tbl_fglinnot_alphalig + " (ptgl_linnot_adj text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalphalig_search_red ON " + tbl_fglinnot_alphalig + " (ptgl_linnot_red text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalphalig_search_seq ON " + tbl_fglinnot_alphalig + " (ptgl_linnot_seq text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalphalig_search_key ON " + tbl_fglinnot_alphalig + " (ptgl_linnot_key text_pattern_ops);");
            
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotbetalig_search_adj ON " + tbl_fglinnot_betalig + " (ptgl_linnot_adj text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotbetalig_search_red ON " + tbl_fglinnot_betalig + " (ptgl_linnot_red text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotbetalig_search_seq ON " + tbl_fglinnot_betalig + " (ptgl_linnot_seq text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotbetalig_search_key ON " + tbl_fglinnot_betalig + " (ptgl_linnot_key text_pattern_ops);");
            
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbelig_search_adj ON " + tbl_fglinnot_albelig + " (ptgl_linnot_adj text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbelig_search_red ON " + tbl_fglinnot_albelig + " (ptgl_linnot_red text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbelig_search_seq ON " + tbl_fglinnot_albelig + " (ptgl_linnot_seq text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnotalbelig_search_key ON " + tbl_fglinnot_albelig + " (ptgl_linnot_key text_pattern_ops);");
            */
            
            
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_fk ON " + tbl_fglinnot + " (linnot_foldinggraph_id);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_numsses ON " + tbl_fglinnot + " (num_sses);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_search_adj ON " + tbl_fglinnot + " (ptgl_linnot_adj text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_search_red ON " + tbl_fglinnot + " (ptgl_linnot_red text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_search_seq ON " + tbl_fglinnot + " (ptgl_linnot_seq text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_search_key ON " + tbl_fglinnot + " (ptgl_linnot_key text_pattern_ops);");
            
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_denormpdbid ON " + tbl_fglinnot + " (denorm_pdb_id);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_denormchain ON " + tbl_fglinnot + " (denorm_chain_name);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_denormgraphtypeint ON " + tbl_fglinnot + " (denorm_graph_type);");
            doInsertQuery("CREATE INDEX plcc_idx_fglinnot_denormgraphtypestring ON " + tbl_fglinnot + " (denorm_graph_type_string);");
            
            
            doInsertQuery("CREATE INDEX plcc_idx_motif_search_name ON " + tbl_motif + " (motif_name text_pattern_ops);");
            doInsertQuery("CREATE INDEX plcc_idx_motif_search_abbrev ON " + tbl_motif + " (motif_abbreviation text_pattern_ops);");
            
            doInsertQuery("CREATE INDEX plcc_idx_secondat_fk ON " + tbl_secondat + " (sse_id);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_graphletsimilarity_graph1 ON " + tbl_graphletsimilarity + " (graphletsimilarity_sourcegraph);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_graphletsimilarity_graph2 ON " + tbl_graphletsimilarity + " (graphletsimilarity_targetgraph);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_graphletsimilarity_score ON " + tbl_graphletsimilarity + " (score);");
            doInsertQuery("CREATE INDEX plcc_idx_ligandtochain_chain ON " + tbl_nm_ligandtochain + " (ligandtochain_chainid);");
            doInsertQuery("CREATE INDEX plcc_idx_ligandtochain_name3 ON " + tbl_nm_ligandtochain + " (ligandtochain_ligandname3);");

            // indices on PKs get created automatically
            
            // fill the type tables
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (1, 'alpha');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (2, 'beta');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (3, 'albe');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (4, 'alphalig');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (5, 'betalig');");
            doInsertQuery("INSERT INTO " + tbl_graphtypes + " (graphtype_id, graphtype_text) VALUES (6, 'albelig');");
            
            doInsertQuery("INSERT INTO " + tbl_ssetypes + " (ssetype_id, ssetype_text) VALUES (1, 'helix');");
            doInsertQuery("INSERT INTO " + tbl_ssetypes + " (ssetype_id, ssetype_text) VALUES (2, 'beta strand');");
            doInsertQuery("INSERT INTO " + tbl_ssetypes + " (ssetype_id, ssetype_text) VALUES (3, 'ligand');");
            doInsertQuery("INSERT INTO " + tbl_ssetypes + " (ssetype_id, ssetype_text) VALUES (4, 'other');");
            
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (1, 'mixed');");
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (2, 'parallel');");
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (3, 'antiparallel');");
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (4, 'ligand');");
            doInsertQuery("INSERT INTO " + tbl_contacttypes + " (contacttype_id, contacttype_text) VALUES (5, 'backbone');");
            
            doInsertQuery("INSERT INTO " + tbl_complexcontacttypes + " (complexcontacttype_id, complexcontacttype_text) VALUES (1, 'van-der-Waals');");
            doInsertQuery("INSERT INTO " + tbl_complexcontacttypes + " (complexcontacttype_id, complexcontacttype_text) VALUES (2, 'disulfide');");
            
            doInsertQuery("INSERT INTO " + tbl_motiftype + " (motiftype_id, motiftype_name) VALUES (1, 'alpha');");
            doInsertQuery("INSERT INTO " + tbl_motiftype + " (motiftype_id, motiftype_name) VALUES (2, 'beta');");
            doInsertQuery("INSERT INTO " + tbl_motiftype + " (motiftype_id, motiftype_name) VALUES (3, 'alpha beta');");
            
            // alpha motifs
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (1, 1, 'Four Helix Bundle', '" + Motifs.MOTIF__FOUR_HELIX_BUNDLE + "');");
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (2, 1, 'Globin Fold', 'globin');");
            // beta motifs
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (3, 2, 'Up and Down Barrel', 'barrel');");
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (4, 2, 'Immunoglobin Fold', 'immuno');");
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (5, 2, 'Beta Propeller', 'propeller');");
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (6, 2, 'Jelly Roll', 'jelly');");
            // alpha beta motifs
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (7, 3, 'Ubiquitin Roll', 'ubi');");
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (8, 3, 'Alpha Beta Plait', 'plait');");
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (9, 3, 'Rossman Fold', 'rossman');");
            doInsertQuery("INSERT INTO " + tbl_motif + " (motif_id, motiftype_id, motif_name, motif_abbreviation) VALUES (10, 3, 'TIM Barrel', 'tim');");
            

            if( ! DBManager.getAutoCommit()) {
                DBManager.commit();
            }
            
            res = true;      // Not really, need to check all of them. We currently leave this to the user (failed queries will at least spit error messages to STDERR).

        } catch (Exception e) { 
            System.err.println("ERROR: Creating DB tables failed: '" + e.getMessage() + "'.");
            res = false;
        }

        return (res);
    }
    
    
    /**
     * Tries to determine the autocommit setting of the current connection.
     * @return the autocommit setting. If an SQL exception occurs trying to determine it an error is printed and true is returned. Other exceptions are not caught (e.g., connection is null), ensure this yourself.
     */
    public static Boolean getAutoCommit() {
        Boolean ac = true;
        try {
            ac = dbc.getAutoCommit();
        }
        catch(SQLException e) {
            DP.getInstance().e("DBManager", "getAutoCommit: '" + e.getMessage() + "'.");
        }
        return ac;
    }
    
    /**
     * Retrieves all the internal DB ids of all SSEs of the chain (also given by internal DB id) from the database.
     * @param chain_db_id the chain DB id
     * @return a list of SSE DB ids
     * @throws SQLException if DB stuff goes wrong
     */
    public static List<Long> getAllSSEIDsOfChain(Long chain_db_id) throws SQLException {
        List<Long> ids = new ArrayList<>();
        
        if(chain_db_id <= 0L) {
            DP.getInstance().c("DBManager", "getAllSSEIDsOfChain: Invalid chain DB id, must be > 0. Returning empty SSE list.");
            return ids;
        }
        
        String query = "SELECT s.sse_id FROM plcc_sse s WHERE s.chain_id = ?";
                     
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        int count;
        PreparedStatement statement = null;
        ResultSet rs = null;        

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            statement.setLong(1, chain_db_id);
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();
            
            while (rs.next()) {
                ids.add(rs.getLong(0));
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "HH getAllSSEIDsOfChain: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "getAllSSEIDsOfChain: Could not close statement and reset autocommit."); }
        }
        return ids;
    }

    /**
     * Writes the whole SSE list to the database in a single batch statement, also writes the secondat table entries for all the SSEs.
     * @param pdbid the PDB identifier
     * @param chain the PDB chain name
     * @param allChainSSEs a list of SSE objects. These will be added to the DB. The ssePositionInChain property is determined by the order of the array (starting with 1).
     * @return the insert count of the queries
     * @throws SQLException if something goes wrong with the DB
     */
    public static int writeAllSSEsOfChainToDB(String pdb_id, String chain_name, List<SSE> allChainSSEs) throws SQLException {
        if(allChainSSEs.isEmpty()) {
            return 0;
        }
        
        Long chain_id = getDBChainID(pdb_id, chain_name);

        if (chain_id <= 0) {
            DP.getInstance().e("DBManager", "writeAllSSEsOfChainToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (0);
        }
              
        PreparedStatement statement = null;                        
        String query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type, lig_name, position_in_chain) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
                
        int updateCount = 0;
        
        
        //TODO: also write the secondat entries
        
        Boolean oldAutoCommitSetting = dbc.getAutoCommit();
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            for(int i = 0; i < allChainSSEs.size(); i++) {
            
                SSE sse = allChainSSEs.get(i);
                statement.setLong(1, chain_id);
                statement.setInt(2, sse.getStartDsspNum());
                statement.setInt(3, sse.getEndDsspNum());
                statement.setString(4, sse.getStartPdbResID());
                statement.setString(5, sse.getEndPdbResID());
                statement.setString(6, sse.getAASequence());
                statement.setInt(7, sse.getSSETypeInt());
                statement.setString(8, sse.getTrimmedLigandName3());
                statement.setInt(9, (i+1));
                
                statement.addBatch();                
            }
            
            int[] count = statement.executeBatch();
            
            
            
            dbc.commit();
            
            
            updateCount = 0;
            for(int i = 0; i < count.length; i++) { updateCount += count[i]; }

        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "writeAllSSEsOfChainToDB: Batch SQL failed: '" + e.getMessage() + "'.");
            SQLException se = e.getNextException();
            if(se != null) {
                DP.getInstance().e("DBManager", "writeAllSSEsOfChainToDB: Batch Exception: '" + se.getMessage() + "'.");                
            }
            
            if (dbc != null) {
                try {
                    DP.getInstance().e("DBManager", "writeAllSSEsOfChainToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    DP.getInstance().e("DBManager", "writeAllSSEsOfChainToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }

        } finally {
            if (statement != null) {
                statement.close();
                dbc.setAutoCommit(oldAutoCommitSetting);
            }
        }
        
        System.err.println("TODO: NEED TO WRITE SECONDAT STUFF!");
        List<Long> sse_ids = DBManager.getAllSSEIDsOfChain(chain_id);
        int updateCountSeconDat = DBManager.batchInsertSecondatEntriesForSSEs(sse_ids);
        
        if(updateCountSeconDat != sse_ids.size()) {
            DP.getInstance().e("DBManager", "writeAllSSEsOfChainToDB: Only " + updateCountSeconDat + " of the " + sse_ids.size() + " secondat entries were written to the DB."); 
            Main.doExit(1);
        }
        
        return(updateCount);                       
    }
    
    /**
     * Batch inserts the empty secondat entries for all SSEs (defined by their internal DB ids) into the database.
     * @param sse_ids al list of database SSE IDs
     * @return the total update count
     */
    public static int batchInsertSecondatEntriesForSSEs(List<Long> sse_ids) throws SQLException {

        if(sse_ids.isEmpty()) {
            return 0;
        }
        
        PreparedStatement statement = null;                

        String query = "INSERT INTO " + tbl_secondat + " (sse_id) VALUES (?);";        

        int updateCount = 0;
        Boolean oldAutoCommitSetting = dbc.getAutoCommit();
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            for(int i = 0; i < sse_ids.size(); i++) {            
                statement.setLong(1, sse_ids.get(0));
            
                statement.addBatch();
            }
            
            int[] count = statement.executeBatch();            
            dbc.commit();            
            
            updateCount = 0;
            for(int i = 0; i < count.length; i++) { updateCount += count[i]; }

        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "batchInsertSecondatEntriesForSSEs: Batch SQL failed: '" + e.getMessage() + "'.");
            SQLException se = e.getNextException();
            if(se != null) {
                DP.getInstance().e("DBManager", "batchInsertSecondatEntriesForSSEs: Batch Exception: '" + se.getMessage() + "'.");                
            }
            
            if (dbc != null) {
                try {
                    DP.getInstance().e("DBManager", "batchInsertSecondatEntriesForSSEs: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    DP.getInstance().e("DBManager", "batchInsertSecondatEntriesForSSEs: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }

        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(oldAutoCommitSetting);
        }
        
        return updateCount;
    }
  
    /* batched batch code to prevent out-of-memory trouble in vast batch statement
    String sql = "insert into employee (name, city, phone) values (?, ?, ?)";
Connection connection = new getConnection();
PreparedStatement ps = connection.prepareStatement(sql);
 
final int batchSize = 1000;
int count = 0;
 
for (Employee employee: employees) {
 
    ps.setString(1, employee.getName());
    ps.setString(2, employee.getCity());
    ps.setString(3, employee.getPhone());
    ps.addBatch();
     
    if(++count % batchSize == 0) {
        ps.executeBatch();
    }
}
ps.executeBatch(); // insert remaining records
ps.close();
connection.close();
*/
    
    /**
     * Adds an empty secondat entry to the DB for this SSE.
     * @param sse_db_id the SSE internal database ID
     * @return the insert ID or -1 on failure
     * @throws SQLException if something went wrong
     */
    public static Long writeEmptySecondatEntryForSSE(Long sse_db_id) throws SQLException {
        PreparedStatement statement = null;

      
        ResultSet generatedKeys = null;
        Long insertID = -1L;
        
        String query = "INSERT INTO " + tbl_secondat + " (sse_id) VALUES (?);";
                
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setLong(1, sse_db_id);
                                
            statement.executeUpdate();
            //dbc.commit();
            
            // get DB insert id
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                insertID = generatedKeys.getLong(1);
            } else {
                DP.getInstance().e("DBManager", "Inserting empty secondat entry for SSE into DB failed, no generated key obtained.");
            }

        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "writeEmptySecondatEntryForSSE: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    DP.getInstance().e("DBManager", "writeEmptySecondatEntryForSSE: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    DP.getInstance().e("DBManager", "writeEmptySecondatEntryForSSE: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }

        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(insertID);       
        
    }
    
    /**
     * Writes information on a SSE to the database. Note that the protein + chain have to exist in the database already.
     * @param pdb_id the PDB identifier, e.g., "7tim"
     * @param chain_name the PDB chain name, e.g., "A"
     * @param dssp_start the dssp residue number of the first residue of the SSE
     * @param pdb_end the PDB residue string of the last residue of the SSE
     * @param pdb_start the PDB residue number of the first residue of the SSE
     * @param dssp_end the dssp residue number of the last residue of the SSE
     * @param lig_name the ligand name in 3 letter notation if this SSE is a ligand, the empty string otherwise. Should be trimmed!
     * @param sse_type the SSE type as an integer, see the SSE class
     * @param sequence the AA sequence of this SSE
     * @param ssePositionInChain the position of this SSE in the SSE list of the whole chain (N to C)
     * @return whether it worked out
     * @throws java.sql.SQLException if something with the DB went wrong 
     */
    public static Long writeSSEToDB(String pdb_id, String chain_name, Integer dssp_start, Integer dssp_end, String pdb_start, String pdb_end, String sequence, Integer sse_type, String lig_name, Integer ssePositionInChain) throws SQLException {
        
        Long chain_id = getDBChainID(pdb_id, chain_name);

        if (chain_id <= 0L) {
            DP.getInstance().e("DBManager", "writeSSEToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (-1L);
        }
      
        if(lig_name.contains(" ")) {
            DP.getInstance().w("DBManager", "writeSSEToDB: The ligand_name3 '" + lig_name + "' contains spaces! Trim it before giving it to me.");
        }

        PreparedStatement statement = null;

        if(lig_name != null) {
            if(lig_name.isEmpty()) {
                lig_name = null;
            }
        }
        
        /*
        if (lig_name.length() >= 1) {
            query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type, lig_name) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ", '" + lig_name + "');";
        } else {
            query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ");";
        }
         * 
         */

        ResultSet generatedKeys = null;
        Long insertID = -1L;
        
        String query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type, lig_name, position_in_chain) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
                // chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ", '" + lig_name + "');";
                
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setLong(1, chain_id);
            statement.setInt(2, dssp_start);
            statement.setInt(3, dssp_end);
            statement.setString(4, pdb_start);
            statement.setString(5, pdb_end);
            statement.setString(6, sequence);
            statement.setInt(7, sse_type);
            statement.setString(8, lig_name);
            statement.setInt(9, ssePositionInChain);
                                
            statement.executeUpdate();
            
            
            // get DB insert id
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                insertID = generatedKeys.getLong(1);
            } else {
                DP.getInstance().e("DBManager", "Inserting SSE into DB failed, no generated key obtained.");
            }
            
            //dbc.commit();

        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "writeSSEToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    DP.getInstance().e("DBManager", "writeSSEToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    DP.getInstance().e("DBManager", "writeSSEToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }

        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(insertID);       
    }
    
    
    /**
     * Checks a protein chain (all folding graphs of all graph types) and assigns it to all known protein motifs that occur in it (e.g., 4-helix bundle or TIM barrel) in the database.
     * This function should be called only when ALL linear notations of ALL graph types of the chain have been entered into the database. If it is called earlier, it will miss some motifs.
     * @param pdbid the PDB id
     * @param chain the chain id
     * @return the total number of rows affected
     * @throws SQLException if SQL stuff goes bad
     */
    public static Integer checkAndAssignChainToAllMotifsInDatabase(String pdbid, String chain) throws SQLException {
        Long chain_db_id = DBManager.getDBChainID(pdbid, chain);
        if(chain_db_id < 1L) {
            DP.getInstance().w("DBManager", "checkAndAssignChainToAllMotifsInDatabase(): Entry for chain " + chain + " of PDB " + pdbid + " not found in DB, skipping motif.");
            return 0;
        }
        
        // check whether all the graphs are there
        Map<String, Long> proteinGraphDBids = DBManager.getProteinGraphDBidsOf(pdbid, chain);
        if(proteinGraphDBids.keySet().size() != 6) {
            DP.getInstance().w("DBManager", "checkAndAssignChainToAllMotifsInDatabase(): Found only " + proteinGraphDBids.keySet().size() + " instead of 6 protein graphs for PDB " + pdbid + " chain " + chain + " in the DB. May miss motifes.");
        }
                
        Integer rowsAffectedTotal = 0;
        Integer rowsAffectedThisMotif;
        Long motif_db_id;
        
        List<String> foundMotifsForChain = new ArrayList<String>();
        
        // check all 10 motives: 
        
        // four helix bundle
        if(DBManager.chainContainsMotif_FourHelixBundle(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__FOUR_HELIX_BUNDLE.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__FOUR_HELIX_BUNDLE);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
        
        if(DBManager.chainContainsMotif_UpAndDownBarrel(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__UP_AND_DOWN_BARREL.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__UP_AND_DOWN_BARREL);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
        
        if(DBManager.chainContainsMotif_GlobinFold(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__GLOBIN_FOLD.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__GLOBIN_FOLD);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
        
        if(DBManager.chainContainsMotif_JellyRoll(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__JELLY_ROLL.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__JELLY_ROLL);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
        
        if(DBManager.chainContainsMotif_ImmunoglobinFold(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__IMMUNOGLOBIN_FOLD.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__IMMUNOGLOBIN_FOLD);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
        
        if(DBManager.chainContainsMotif_BetaPropeller(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__BETA_PROPELLER.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__BETA_PROPELLER);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
        
        if(DBManager.chainContainsMotif_RossmanFold(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__ROSSMAN_FOLD.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__ROSSMAN_FOLD);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
        
        if(DBManager.chainContainsMotif_TIMBarrel(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__TIM_BARREL.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__TIM_BARREL);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
        
        if(DBManager.chainContainsMotif_AlphaBetaPlait(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__ALPHA_BETA_PLAIT.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__ALPHA_BETA_PLAIT);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
        
        if(DBManager.chainContainsMotif_UbiquitinRoll(chain_db_id)) {
            motif_db_id = Motifs.MOTIFCODE__UBIQUITIN_ROLL.longValue();
            rowsAffectedThisMotif = DBManager.assignChainToMotiv(chain_db_id, motif_db_id);
            if(rowsAffectedThisMotif > 0) {
                foundMotifsForChain.add(Motifs.MOTIF__UBIQUITIN_ROLL);
            }
            rowsAffectedTotal += rowsAffectedThisMotif;
        }
                                        
        
        // OK -- all motifs tested
        if(! Settings.getBoolean("plcc_B_silent")) {
            if(foundMotifsForChain.size() > 0) {
                System.out.println("      Found " + foundMotifsForChain.size() + " motives in all folding graph linear notations of " + pdbid + " chain " + chain + ": " + IO.stringListToString(foundMotifsForChain));
            } else {
                System.out.println("      Found no motives in all folding graph linear notations of " + pdbid + " chain " + chain + ".");
            }
        }
        
        DP.getInstance().flush();
        
        return rowsAffectedTotal;
    }
            
        
    /**
     * Checks whether the chain contains a 4 helix bundle motif. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id the chain database id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_FourHelixBundle(Long chain_db_id) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;
              
        
        StringBuilder querySB = new StringBuilder();
        
        
        querySB.append("SELECT ln.linnot_id ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("WHERE ( pg.chain_id = ? AND (pg.graph_type = 1 AND (ln.ptgl_linnot_red LIKE '%1a,1a,1a,-3_%' and ln.ptgl_linnot_red not LIKE '%-1a,1a,1a,-3_%') or (ln.ptgl_linnot_red LIKE '%-3_,1a,1a,1a%') or (ln.ptgl_linnot_red LIKE '%3_,1a,1a,1a%' and ln.ptgl_linnot_red not LIKE '%-3_,1a,1a,1a%') or (ln.ptgl_linnot_red LIKE '%-4_,1a,1a,2a%') or (ln.ptgl_linnot_red LIKE '%2a,1a,1a,-4_%' and ln.ptgl_linnot_red not LIKE '%-2a,1a,1a,-4_%') or (ln.ptgl_linnot_red LIKE '%1p,1a,1p%' and ln.ptgl_linnot_red not LIKE '%-1p,1a,1p%') or (ln.ptgl_linnot_red LIKE '%1a,1a,1a%' and ln.ptgl_linnot_seq LIKE '%1,1,1%' and ln.num_sses < 6 ) or (pg.graph_type = 4 AND ln.ptgl_linnot_red LIKE '[h,1ah,1ah,1ah]') ) )");
        
        String query = querySB.toString();
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "chainContainsMotif_FourHelixBundle: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "chainContainsMotif_FourHelixBundle: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table
        if(tableData.size() >= 1) {
            return true;
        }
        else {
            return(false);
        }        

    }
    
    public static void main(String[] argv) {
        Boolean silent = false;
        Settings.init();
        int numSettingsLoaded = Settings.load("");
        if(DBManager.initUsingDefaults()) {
                if(! silent) {
                    System.out.println("  -> Database connection OK.");
                    
                    Long chainDBID = DBManager.getDBChainID("7tim", "A");
                    if(DBManager.chainContainsMotif_RossmanFold(chainDBID)) {
                        System.out.println("its in there!");
                    }
                    else {
                        System.out.println("Nope");
                    }
                    
                }
            }
            else {
                System.out.println("  -> Database connection FAILED.");
                DP.getInstance().w("Could not establish database connection, not writing anything to the DB.");
                Settings.set("plcc_B_useDB", "false");
            }
       
    }
    
    /**
     * Checks whether the chain contains an up and down barrel motif. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id the chain database id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_UpAndDownBarrel(Long chain_db_id) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
                
        PreparedStatement statement = null;
        ResultSet rs = null;             
        
        StringBuilder querySB = new StringBuilder();
        
        // barrel1.pl
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%1a,1a,1a,1a,1a,1a,1a,-7a%' AND ln.ptgl_linnot_red NOT LIKE '%-1a,1a,1a,1a,1a,1a,1a,-7a%') ) ) ");
        
        // barrel2.pl
        querySB.append(" UNION ");
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%-1a,-1a,-1a,-1a,-1a,-1a,-1a,7a%') ) ) ");
        
        // barrel3.pl
        querySB.append(" UNION ");
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%7a,-1a,-1a,-1a,-1a,-1a,-1a,-1a%' AND ln.ptgl_linnot_red NOT LIKE '%-7a,-1a,-1a,-1a,-1a,-1a,-1a,-1a%') ) ) ");
        
        // barrel4.pl
        querySB.append(" UNION ");
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%-7a,1a,1a,1a,1a,1a,1a,1a%') ) ) ");
        
        // order
        querySB.append("GROUP BY p.pdb_id, c.chain_name ");
        
        String query = querySB.toString();
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setLong(2, chain_db_id);
            statement.setLong(3, chain_db_id);
            statement.setLong(4, chain_db_id);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "chainContainsMotif_UpAndDownBarrel: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "chainContainsMotif_UpAndDownBarrel: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table
        if(tableData.size() >= 1) {
            return true;
        }
        else {
            return(false);
        }        

    }
    
    /**
     * Checks whether the chain contains a jelly roll motif. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id the chain database id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_JellyRoll(Long chain_db_id) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
                
        PreparedStatement statement = null;
        ResultSet rs = null;             
        
        StringBuilder querySB = new StringBuilder();
        
        // jelly1.pl
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%-1a,2a,-3a%' ) or (ln.ptgl_linnot_red LIKE '%-1a,3a,-5a%') or (ln.ptgl_linnot_red like '%-3a,2a,-1a%' ) or (ln.ptgl_linnot_red like '%-5a,3a,-1a%') ) ) ");
        
        // jelly2.pl
        querySB.append(" UNION ");        
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%3a,1a,1a,-3a,%a,%a,%a%'  or ln.ptgl_linnot_red like '%3a,-1a,-1a,3a,%a,%a,%a%') ) ) ");
        
        // jelly3.pl
        querySB.append(" UNION ");        
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND ((ln.ptgl_linnot_red like '%1a,-3a,5a%' and ln.ptgl_linnot_red not like '%-1a,-3a,5a%') or (ln.ptgl_linnot_red like '%5a,-3a,1a%' and ln.ptgl_linnot_red not like '%-5a,-3a,1a%')) ) ) ");
                
        // order
        querySB.append("GROUP BY p.pdb_id, c.chain_name ");
        
        String query = querySB.toString();
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setLong(2, chain_db_id);
            statement.setLong(3, chain_db_id);
            
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "chainContainsMotif_JellyRoll: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "chainContainsMotif_JellyRoll: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table
        if(tableData.size() >= 1) {
            return true;
        }
        else {
            return(false);
        }        

    }
    
    
    /**
     * Checks whether the chain contains a immunoglobin fold motif. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id the chain database id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_ImmunoglobinFold(Long chain_db_id) {
        
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
                
        PreparedStatement statement = null;
        ResultSet rs = null;             
        
        StringBuilder querySB = new StringBuilder();
        
        // immuno1.pl
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%-1a,-2a,1a%'  and ln.ptgl_linnot_red like '%1a,1a%' and ln.num_sses >= 7) ) ) ");
        
        // immuno2.pl
        querySB.append(" UNION ");
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%1a,2a,-1a%' and ln.ptgl_linnot_red not like '%-1a,2a,-1a%' and ln.ptgl_linnot_red like '%1a,1a%' and ln.num_sses >= 8) ) ) ");
        
        // immuno3.pl
        querySB.append(" UNION ");
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%-1a,2a,1a%'  and ln.ptgl_linnot_red like '%1a,1a%' and ln.num_sses >= 8 ) ) ) ");
        
        // immuno4.pl
        querySB.append(" UNION ");
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red like '%3a,-1a,-1a,3a%' and ln.num_sses >=7 ) ) ) ");
        
        // immuno5.pl
        querySB.append(" UNION ");
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red like '%-3a,1a,1a,-3a%' and ln.num_sses >= 7) ) ) ");             
                
        // order
        querySB.append("GROUP BY p.pdb_id, c.chain_name ");
        
        String query = querySB.toString();
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setLong(2, chain_db_id);
            statement.setLong(3, chain_db_id);
            statement.setLong(4, chain_db_id);
            statement.setLong(5, chain_db_id);
            
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "chainContainsMotif_ImmunoglobinFold: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "chainContainsMotif_ImmunoglobinFold: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table
        if(tableData.size() >= 1) {
            return true;
        }
        else {
            return(false);
        }        

    }
    
    
    /**
     * Checks whether the chain contains a rossman fold motif. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id the chain database id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_RossmanFold(Long chain_db_id) {
        if( ! Settings.getBoolean("plcc_B_no_not_impl_warn")) {
            DP.getInstance().w("DBManager", "chainContainsMotif_RossmanFold: Not implemented yet, returning false for chain with ID '" + chain_db_id + "'.");
        }
        return false;
        
        /**
    
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
                
        PreparedStatement statement = null;
        ResultSet rs = null;             
        
        StringBuilder querySB = new StringBuilder();
       
        // rossman1.pl
        querySB.append("SELECT p.pdb_id, c.chain_name, ln.firstvertexpos_adj, ln.ptgl_linnot_adj, ln.ptgl_linnot_red ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%3p,-1p,-1p%'  and ln.ptgl_linnot_red not like '%-3p,-1p,-1p%') ) ) ");
        
        
        // order
        querySB.append("GROUP BY p.pdb_id, c.chain_name, ln.ptgl_linnot_adj, ln.firstvertexpos_adj, ln.ptgl_linnot_red ");
        
        String query = querySB.toString();
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setLong(2, chain_db_id);
            statement.setLong(3, chain_db_id);
            statement.setLong(4, chain_db_id);
            statement.setLong(5, chain_db_id);
            
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "chainContainsMotif_RossmanFold: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "chainContainsMotif_RossmanFold: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table
        if(tableData.size() >= 1) {
            return true;
        }
        else {
            return(false);
        }        
               
                    
        }**/
    }
    
    
    
    
    
    /**
     * Checks whether the chain contains a TIM-barrel motif. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id the chain database id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_TIMBarrel(Long chain_db_id) {
        if( ! Settings.getBoolean("plcc_B_no_not_impl_warn")) {
            DP.getInstance().w("DBManager", "chainContainsMotif_TIMBarrel: Not implemented yet, returning false for chain with ID '" + chain_db_id + "'.");
        }
        return false;
    }
    
    /**
     * Checks whether the chain contains an alpha-beta plait motif. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id the chain database id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_AlphaBetaPlait(Long chain_db_id) {
        if( ! Settings.getBoolean("plcc_B_no_not_impl_warn")) {
            DP.getInstance().w("DBManager", "chainContainsMotif_AlphaBetaPlait: Not implemented yet, returning false for chain with ID '" + chain_db_id + "'.");
        }
        return false;
    }
    
    /**
     * Checks whether the chain contains an ubiquitin roll motif. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id the chain database id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_UbiquitinRoll(Long chain_db_id) {
        if( ! Settings.getBoolean("plcc_B_no_not_impl_warn")) {
            DP.getInstance().w("DBManager", "chainContainsMotif_UbiquitinRoll: Not implemented yet, returning false for chain with ID '" + chain_db_id + "'.");
        }
        return false;
    }
    
    /**
     * Checks whether the chain contains a beta propeller motif. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id the chain database id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_BetaPropeller(Long chain_db_id) {
        
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
                
        PreparedStatement statement = null;
        ResultSet rs = null;             
        
        StringBuilder querySB = new StringBuilder();
        
        // propeller1.pl
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '%1a,1a,1a,%,1a,1a,1a%1a,1a,1a%1a,1a,1a%' OR ln.ptgl_linnot_red LIKE '%-1a,-1a,-1a,%,-1a,-1a,-1a,%,-1a,-1a,-1a,%,-1a,-1a,-1a%') ) ) ");                
                        
        // order
        querySB.append("GROUP BY p.pdb_id, c.chain_name ");
        
        String query = querySB.toString();
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);            
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "chainContainsMotif_BetaPropeller Query1: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "chainContainsMotif_BetaPropeller Query1: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table
        if(tableData.size() >= 1) {
            return true;
        }
        else {
            // check the other option, we need a second query for this though
            // propeller2.pl
            querySB = null; // avoid accidental use of wrong SB
            query = null;
            
            StringBuilder query2SB = new StringBuilder();
            query2SB.append("SELECT p.pdb_id, c.chain_name, ln.firstvertexpos_adj ");
            query2SB.append("FROM plcc_fglinnot ln ");
            query2SB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
            query2SB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
            query2SB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
            query2SB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
            query2SB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 2 AND (ln.ptgl_linnot_red LIKE '[1a,1a,1a]' ) ) ) ");

            // order
            query2SB.append("GROUP BY p.pdb_id, c.chain_name, ln.firstvertexpos_adj ");
            
            String query2 = query2SB.toString();
            tableData = new ArrayList<ArrayList<String>>();
            
            try {
                //dbc.setAutoCommit(false);
                statement = dbc.prepareStatement(query2);

                statement.setLong(1, chain_db_id);            

                rs = statement.executeQuery();
                //dbc.commit();

                md = rs.getMetaData();
                count = md.getColumnCount();

                columnHeaders = new ArrayList<String>();

                for (int i = 1; i <= count; i++) {
                    columnHeaders.add(md.getColumnName(i));
                }


                while (rs.next()) {
                    rowData = new ArrayList<String>();
                    for (int i = 1; i <= count; i++) {
                        rowData.add(rs.getString(i));
                    }
                    tableData.add(rowData);                                                            
                }
                
                // further check on query2 results
                if(tableData.size() >= 3) {
                    return true;
                }
            
            } catch (SQLException e ) {
                DP.getInstance().e("DBManager", "chainContainsMotif_BetaPropeller Query2: '" + e.getMessage() + "'.");
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    //dbc.setAutoCommit(true);
                } catch(SQLException e) { DP.getInstance().w("DBManager", "chainContainsMotif_BetaPropeller Query2: Could not close statement and reset autocommit."); }
            }
        
        }        
        
        return false;

    }
    
    
    /**
     * Checks whether the chain contains a globin fold motif, like PDB 1mba does. These checks consider the different linear notations of several graph types.
     * This function does not find the motif if the required linear notations and/or graphs are not yet available in the database, of course.
     * @param chain_db_id
     * @return true if the motif was found in the linear notations of the folding graphs of the chain, false otherwise
     */
    public static Boolean chainContainsMotif_GlobinFold(Long chain_db_id) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
                
        PreparedStatement statement = null;
        ResultSet rs = null;             
        
        StringBuilder querySB = new StringBuilder();
        
        // globin1.pl step 1 -- find chains
        querySB.append("SELECT p.pdb_id, c.chain_name ");
	querySB.append("FROM plcc_fglinnot ln ");
	querySB.append("INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id ");
	querySB.append("INNER JOIN plcc_graph pg ON fg.parent_graph_id = pg.graph_id ");
	querySB.append("INNER JOIN plcc_chain c ON pg.chain_id = c.chain_id ");
	querySB.append("INNER JOIN plcc_protein p ON p.pdb_id = c.pdb_id ");
	querySB.append("WHERE ( c.chain_id = ? AND (pg.graph_type = 1 AND (ln.ptgl_linnot_red LIKE '%,%,%,%,%,%,%a%' AND ln.num_sses >= 8) ) ) ");
        querySB.append("GROUP BY p.pdb_id, c.chain_name, ln.firstvertexpos_adj ");
        
        
        String query = querySB.toString();
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "chainContainsMotif_GlobinFold: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "chainContainsMotif_GlobinFold: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table
        if(tableData.size() >= 1) {
            //System.out.println("DEBUG: chainContainsMotif_GlobinFold: matched linnot");
            String row_pdbid, row_chain_name;
            for(ArrayList<String> row : tableData) {
                if(row.size() >= 2) {
                    row_pdbid = row.get(0);
                    row_chain_name = row.get(1);
                    
                    try {
                        // check whether the chain contains any non-alpha helix SSEs
                        Set<String> sseTypesOfChain = getSSETypesOfChain(row_pdbid, row_chain_name);
                        
                        if(sseTypesOfChain != null) {
                            // the chain contains only alpha helices, motif found
                            if(sseTypesOfChain.contains("H")) {
                                if( ! sseTypesOfChain.contains("E")) {  // note that ligands are allowed!
                                    return true;
                                }
                            }
                            
                        } else {
                            DP.getInstance().e("DBManager", "chainContainsMotif_GlobinFold: Could not determine SSE string for chain, is the albelig graph missing in the DB? Cannot detect motif.");
                        }
                        
                    } catch(SQLException ex) {
                        DP.getInstance().e("DBManager", "chainContainsMotif_GlobinFold: Could not determine SSE string for chain, is the albelig graph missing in the DB? Cannot detect motif.");
                        return false;
                    }
                }                
            }
        }
        
        return(false);                
    }
    
    
    /**
     * Returns all protein graph database IDs for the given PDB ID and chain.
     * @param pdbid the PDB id
     * @param chain the chain ID
     * @return a Map, each key is a graph type string (e.g., "albe"), and each value contains the DB ID of that graph. If a certain graph is not found, it is NOT listed in the Map.
     * @throws SQLException if SQL stuff goes wrong
     */
    public static Map<String, Long> getProteinGraphDBidsOf(String pdbid, String chain) throws SQLException {
        Map<String, Long> proteinGraphDBids = new HashMap<>();
        
        String[] graphTypes = new String[]{ ProtGraphs.GRAPHTYPE_STRING_ALPHA, ProtGraphs.GRAPHTYPE_STRING_BETA, ProtGraphs.GRAPHTYPE_STRING_ALBE, ProtGraphs.GRAPHTYPE_STRING_ALPHALIG, ProtGraphs.GRAPHTYPE_STRING_BETALIG, ProtGraphs.GRAPHTYPE_STRING_ALBELIG };

        for(String gt : graphTypes) {
            Long idAlpha = DBManager.getDBProteinGraphID(pdbid, chain, gt);
            if(idAlpha > 0L) {
                proteinGraphDBids.put(gt, idAlpha);
            }
        }
        
        return proteinGraphDBids;
    }
    
    /**
     * Assigns a chain to a motif in the database.
     * @param chain_db_id the chain database ID
     * @param motif_db_id the motif database ID
     * @return the number of affected rows
     * @throws SQLException if SQL stuff goes bad
     */
    public static Integer assignChainToMotiv(Long chain_db_id, Long motif_db_id) throws SQLException {
        Integer numRowsAffected = 0;        
        PreparedStatement statement = null;
        
        String query = "INSERT INTO " + tbl_nm_chaintomotif + " (chain_id, motif_id) VALUES (?, ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            statement.setLong(1, chain_db_id);
            statement.setLong(2, motif_db_id);
                                
            numRowsAffected = statement.executeUpdate();
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: assignChainToMotiv(): '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: assignChainToMotiv(): Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: assignChainToMotiv(): Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        } 
        
        return numRowsAffected;
        
    }
    
    /**
     * Writes data on a protein to the database. This will delete old versions in the database.
     * @param pdb_id the PDB id of the protein
     * @param title the PDB title field
     * @param header the PDB header field
     * @param keywords the PDB keywords field
     * @param experiment the PDB experiment field
     * @param resolution the resolution of the structure, from the PDB headers
     * @return true if the protein was written to the DB, false otherwise
     * @throws SQLException if the DB could not be reset or closed properly
     */
    public static Boolean writeProteinToDB(String pdb_id, String title, String header, String keywords, String experiment, Double resolution) throws SQLException {
        
        if(proteinExistsInDB(pdb_id)) {
            try {
                deletePdbidFromDB(pdb_id);
            } catch (Exception e) {
                DP.getInstance().w("DB: writeProteinToDB: Protein '" + pdb_id + "' already in DB and deleting it failed.");
            }                        
        }
        
        Boolean result = false;

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_protein + " (pdb_id, title, header, keywords, experiment, resolution) VALUES (?, ?, ?, ?, ?, ?);";

        try {
            ////dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
            statement.setString(2, title);
            statement.setString(3, header);
            statement.setString(4, keywords);
            statement.setString(5, experiment);
            statement.setDouble(6, resolution);
                                
            statement.executeUpdate();
            ////dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeProteinToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeProteinToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeProteinToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(result);

    }
    
    /**
     * Writes data on a ligand to the database. This will NOT delete old versions in the database.
     * @param ligand_name3 the PDB ligand abbreviation, 3 chars, e.g., "ICT" for isocitric acid. See http://ligand-expo.rcsb.org/  for details. Should NOT contain spaces, trim it!
     * @param ligand_longname the long name, usually chemical formular or other name, for the ligand
     * @param ligand_formula the chemical formula for the ligand, from PDB file header
     * @param ligand_synonyms the synonyms for the ligand, from PDB file header
     * @return true if the ligand was written to the DB, false otherwise
     * @throws SQLException if the DB could not be reset or closed properly
     */
    public static Boolean writeLigandToDBUnlessAlreadyThere(String ligand_name3, String ligand_longname, String ligand_formula, String ligand_synonyms) throws SQLException {
        
        if(ligand_name3.contains(" ")) {
            DP.getInstance().w("DBManager", "writeLigandToDBUnlessAlreadyThere: The ligand_name3 '" + ligand_name3 + "' contains spaces! Trim it before giving it to me.");
        }
        
        Boolean autoCommitOldSetting = dbc.getAutoCommit();
        dbc.setAutoCommit(false);
        
        if(ligandExistsInDB(ligand_name3)) {
            dbc.setAutoCommit(autoCommitOldSetting);
            return false;                   
        }                
        
        if(ligand_synonyms.equals("''") || ligand_synonyms.isEmpty()) {
            //DP.getInstance().w("DBManager", "writeLigandToDBUnlessAlreadyThere: Replacing ligand synonym string \"''\" with null.");
            ligand_synonyms = null;
        }
        
        
        
        Boolean result = false;

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_ligand + " (ligand_name3, ligand_longname, ligand_formula, ligand_synonyms) VALUES (?, ?, ?, ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, ligand_name3);
            statement.setString(2, ligand_longname);
            statement.setString(3, ligand_formula);
            statement.setString(4, ligand_synonyms);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeLigandToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeLigandToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeLigandToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            dbc.setAutoCommit(autoCommitOldSetting);
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(result);

    }
    

    /**
     * Deletes all entries related to the PDB ID 'pdb_id' from the plcc database tables.
     * @param pdb_id the PDB identifier, e.g., "7tim"
     * @return The number of affected records (0 if the PDB ID was not in the database).
     */
    public static Integer deletePdbidFromDB(String pdb_id) {

        PreparedStatement statement = null;        
        ResultSetMetaData md;
        int count = 0;               
        
        String query = "DELETE FROM " + tbl_protein + " WHERE pdb_id = ?;";
        
        
        try {
            ////dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
              
            
            count = statement.executeUpdate();
            ////dbc.commit();
            
            //md = rs.getMetaData();
            //count = md.getColumnCount();
            
            
        } catch (SQLException e) {
            System.err.println("ERROR: SQL: deletePdbidFromDB: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                ////dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DB: deletePdbidFromDB: Could not close statement and reset autocommit."); }
        }
        
        // The other tables are handled automatically via the ON DELETE CASCADE constraint.

        return (count);
    }
    
    /**
     * Deletes all graphlet similarity entries for a protein graph pair from the plcc database tables.
     * @param source_graph_id the source graph id
     * @param target_graph_id the target graph id
     * @return The number of affected records (0 if the PDB ID was not in the database).
     */
    public static Integer deleteGraphletSimilaritiesFromDBForGraphs(Long source_graph_id, Long target_graph_id) {

        PreparedStatement statement = null;        
        ResultSetMetaData md;
        int count = 0;        
        ResultSet rs = null;
        
        
        String query = "DELETE FROM " + tbl_graphletsimilarity + " gs WHERE ( gs.graphletsimilarity_sourcegraph = ? AND gs.graphletsimilarity_targetgraph = ? );";
        
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, source_graph_id);
            statement.setLong(2, target_graph_id);
              
            
            count = statement.executeUpdate();
            //dbc.commit();
            
            //md = rs.getMetaData();
            //count = md.getColumnCount();
            
            
        } catch (SQLException e) {
            DP.getInstance().e("DBManager", "deleteGraphletSimilaritiesFromDBForGraphs: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "deleteGraphletSimilaritiesFromDBForGraphs: Could not close statement and reset autocommit."); }
        }
        

        return (count);
    }
    
    /**
     * Deletes all graphlet similarity entries for a source protein graph from the plcc database tables. Note that this function does NOT delete the entries where this graph is the target graph.
     * @param source_graph_id the source graph id
     * @return The number of affected records (0 if the PDB ID was not in the database).
     */
    public static Integer deleteAllGraphletSimilaritiesFromDBForSourceGraph(Long source_graph_id) {

        PreparedStatement statement = null;        
        ResultSetMetaData md;
        int count = 0;        
        ResultSet rs = null;
        
        
        String query = "DELETE FROM " + tbl_graphletsimilarity + " gs WHERE ( gs.graphletsimilarity_sourcegraph = ?);";
        
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, source_graph_id);
            
            count = statement.executeUpdate();
            //dbc.commit();
                       
            
        } catch (SQLException e) {
            DP.getInstance().e("DBManager", "deleteAllGraphletSimilaritiesFromDBForSourceGraph: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "deleteAllGraphletSimilaritiesFromDBForSourceGraph: Could not close statement and reset autocommit."); }
        }
        

        return (count);
    }
    
    /**
     * Deletes all graphlet similarity entries for a target protein graph from the plcc database tables. Note that this function does NOT delete the entries where this graph is the source graph.
     * @param target_graph_id the target graph id
     * @return The number of affected records (0 if the PDB ID was not in the database).
     */
    public static Integer deleteAllGraphletSimilaritiesFromDBForTargetGraph(Long target_graph_id) {

        PreparedStatement statement = null;        
        ResultSetMetaData md;
        int count = 0;        
        ResultSet rs = null;
                
        String query = "DELETE FROM " + tbl_graphletsimilarity + " gs WHERE ( gs.graphletsimilarity_targetgraph = ? );";
                
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, target_graph_id);
              
            
            count = statement.executeUpdate();
            //dbc.commit();
            
        } catch (SQLException e) {
            DP.getInstance().e("DBManager", "deleteAllGraphletSimilaritiesFromDBForTargetGraph: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "deleteAllGraphletSimilaritiesFromDBForTargetGraph: Could not close statement and reset autocommit."); }
        }
        

        return (count);
    }
    
    
    /**
     * Deletes all graphlet counts for the given graph from the database tables.
     * @param graph_db_id the graph ID in the database
     * @return The number of affected records (0 if the PDB ID was not in the database).
     */
    public static Integer deleteGraphletsFromDBForGraph(Long graph_db_id) {

        PreparedStatement statement = null;        
        ResultSetMetaData md;
        int count = 0;        
        ResultSet rs = null;
                
        String query = "DELETE FROM " + tbl_graphletcount + " WHERE graph_id = ?;";
                
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, graph_db_id);              
            
            count = statement.executeUpdate();
            //dbc.commit();
            
            //md = rs.getMetaData();
            //count = md.getColumnCount();
            
            
        } catch (SQLException e) {
            DP.getInstance().e("DBManager", "deleteGraphletsFromDBForGraph: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "deleteGraphletsFromDBForGraph: Could not close statement and reset autocommit."); }
        }        

        return (count);
    }

    
    
    /**
     * Writes data on a protein chain to the database
     * @param chain_name the chain name
     * @param pdb_id the PDB id of the protein this chain belongs to. The protein has to exist in the DB already.
     * @param molName the molName record of the respective PDB header field
     * @param orgScientific the orgScientific record of the respective PDB header field
     * @param orgCommon the orgCommon record of the respective PDB header field
     * @return
     * @throws SQLException if the DB could not be reset or closed properly
     */
    public static Boolean writeChainToDB(String chain_name, String pdb_id, String molName, String orgScientific, String orgCommon) throws SQLException {
        
        if(! proteinExistsInDB(pdb_id)) {
            return(false);
        }
        
        Boolean result = false;

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_chain + " (chain_name, pdb_id, mol_name, organism_scientific, organism_common) VALUES (?, ?, ?, ?, ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, chain_name);
            statement.setString(2, pdb_id);
            statement.setString(3, molName);
            statement.setString(4, orgScientific);
            statement.setString(5, orgCommon);
                                
            statement.executeUpdate();
            //dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeChainToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeChainToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeChainToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(result);

    }
    
    
    /**
     * Writes information on a protein graph to the database. This includes a string representing the protein
     * graph in VPLG format. Note that the chain has to exist in the database already. You can set the path to
     * the graph images later using the updateGraphImage... functions.
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the chain represented by the graph_string
     * @param graph_type the Integer representation of the graph type. use ProtGraphs.getGraphTypeCode() to get it.
     * @param graph_string_gml the graph in GML format
     * @param graph_string_plcc the graph in plcc format
     * @param graph_string_kavosh the graph in kavosh format
     * @param graph_string_dotlanguage the graph in DOT language format
     * @param graph_string_json the graph in JSON format
     * @param graph_string_xml the graph in XML format
     * @param sse_string the graph in SSE string notation
     * @param containsBetaBarrel whether the graph contains a beta barrel (used for stats only)
     * @return true if the graph was inserted, false if errors occurred
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Boolean writeProteinGraphToDB(String pdb_id, String chain_name, Integer graph_type, String graph_string_gml, String graph_string_plcc, String graph_string_kavosh, String graph_string_dotlanguage, String graph_string_json, String graph_string_xml, String sse_string, Boolean containsBetaBarrel) throws SQLException {
               
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        Boolean result = false;

        if (chain_db_id < 0) {
            DP.getInstance().e("DBManager", "riteProteinGraphToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert protein graph.");
            //System.err.println("ERROR: writeProteinGraphToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert protein graph.");
            return (false);
        }
        
        Integer graph_containsbetabarrel = (containsBetaBarrel ? 1 : 0);
        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_proteingraph + " (chain_id, graph_type, graph_string_gml, graph_string_plcc, graph_string_kavosh, graph_string_dotlanguage, graph_string_json, graph_string_xml, sse_string, graph_containsbetabarrel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        //System.out.println("########################## length of gml string is " + graph_string_gml.length() + " ##################" );
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, graph_type);
            statement.setString(3, graph_string_gml);
            statement.setString(4, graph_string_plcc);
            statement.setString(5, graph_string_kavosh);
            statement.setString(6, graph_string_dotlanguage);
            statement.setString(7, graph_string_json);
            statement.setString(8, graph_string_xml);
            statement.setString(9, sse_string);
            statement.setInt(10, graph_containsbetabarrel);
                                
            statement.executeUpdate();
            //dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeProteinGraphToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeProteinGraphToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeProteinGraphToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(result);
    }
    
    
    /**
     * Writes information on a folding graph to the database. This includes a string representing the folding
     * graph in VPLG format. Note that the chain and the parent protein graph has to exist in the database already. You can set the path to
     * the graph images later using the updateFoldingGraphImage... functions. The PTGL linear notations get added to separate tables.
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the chain represented by the graph_string
     * @param graph_type the Integer representation of the graph type. use ProtGraphs.getGraphTypeCode() to get it.
     * @param fg_number the folding graph identifier number, a number starting with 0 and up to number of FGs of this graph -1. The order of the connected components is determined by ordering according to their left-most vertex in the parent. So if the parent graph consists of 4 vertices, which have indices 0, 1, 2, and 3. And it has a CC1={0,3} and a second CC2={1,2}. Then CC1 gets fg_number=0 because its left-most vertex is 0. The left-most vertex of CC2 is 1.
     * 
     * This was a letter in the PTGL ('A', 'B', ...) and one
     * could map this to letters, of course (1=>A, 2=>B, ...). This mapping is stored in the fold_name field.
     * 
     * @param first_vertex_position_in_parent the position (vertex index) of the first vertex of this FG in the parent PG
     * @param fold_name the PTGL fold name, e.g., "A" for the graph with fg_number 0, "B" for the second, and so on. It is better to use the fg_number because this field may not be unique for PGs with many connected components.
     * @param graph_string_gml the graph in GML format
     * @param graph_string_plcc the graph in plcc format
     * @param graph_string_kavosh the graph in kavosh format
     * @param graph_string_dotlanguage the graph in DOT language format
     * @param graph_string_json the graph in JSON format
     * @param graph_string_xml the graph in XML format
     * @param sse_string the graph in SSE string notation
     * @param containsBetaBarrel whether the graph contains a beta barrel (used for stats only)
     * @return the database insert ID or a value smaller than 1 if something went wrong
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Long writeFoldingGraphToDB(String pdb_id, String chain_name, Integer graph_type, Integer fg_number, String fold_name, Integer first_vertex_position_in_parent, String graph_string_gml, String graph_string_plcc, String graph_string_kavosh, String graph_string_dotlanguage, String graph_string_json, String graph_string_xml, String sse_string, Boolean containsBetaBarrel) throws SQLException {
               
        
        if(fg_number < 0) {
            DP.getInstance().e("writeFoldingGraphToDB", "Folding graph number must be 0 or greater, skipping.");
            return(-1L);
        }
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSet generatedKeys = null;
        Long insertID = -1L;

        if (chain_db_id < 0) {
            DP.getInstance().e("writeFoldingGraphToDB()" , "Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert folding graph.");
            return (-1L);
        }
        
        String graphTypeString = ProtGraphs.getGraphTypeString(graph_type);
        Long parent_graph_id = DBManager.getDBProteinGraphID(pdb_id, chain_name, graphTypeString);
        if(parent_graph_id <= 0) {
            DP.getInstance().e("writeFoldingGraphToDB()" , "Could not find parent " + graphTypeString + " graph with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert folding graph.");
            return (-1L);
        } 
        
        Integer graph_containsbetabarrel = (containsBetaBarrel ? 1 : 0);

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_foldinggraph + " (parent_graph_id, fg_number, fold_name, first_vertex_position_in_parent, graph_string_gml, graph_string_plcc, graph_string_kavosh, graph_string_dotlanguage, graph_string_json, graph_string_xml, sse_string, graph_containsbetabarrel) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        int affectedRows = 0;
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setLong(1, parent_graph_id);
            statement.setInt(2, fg_number);
            statement.setString(3, fold_name);
            statement.setInt(4, first_vertex_position_in_parent);            
            statement.setString(5, graph_string_gml);
            statement.setString(6, graph_string_plcc);
            statement.setString(7, graph_string_kavosh);
            statement.setString(8, graph_string_dotlanguage);
            statement.setString(9, graph_string_json);
            statement.setString(10, graph_string_xml);
            statement.setString(11, sse_string);
            statement.setInt(12, graph_containsbetabarrel);
                                
            affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                DP.getInstance().w("Inserting folding graph into DB failed, no rows affected.");
            }
            
            // get DB insert id
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                insertID = generatedKeys.getLong(1);
            } else {
                DP.getInstance().w("Inserting folding graph into DB failed, no generated key obtained.");
            }
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeFoldingGraphToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeFoldingGraphToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeFoldingGraphToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(insertID);
    }

    /**
     * Writes all 4 linear notations of a single fold (connected component) to the database.
     * @param pdb_id the PBD id
     * @param chain_name the chain name
     * @param graph_type the graph type string, use constants
     * @param fg_number the fg_number of the fold, the FG knows it
     * @param pnfr the results of computing the linear notations
     * @return the DB id of the insert or -1 if nothing has been inserted
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    /*
    public static Long writeFoldLinearNotationsToDatabaseMultiTable(String pdb_id, String chain_name, String graph_type, Integer fg_number, PTGLNotationFoldResult pnfr) throws SQLException {
        
        Long insertID = -1L;
        ResultSet generatedKeys = null;        
        
        String linnot_table = "";
        
        if(graph_type.equals(ProtGraph.GRAPHTYPE_ALPHA)) {
            linnot_table = DBManager.tbl_fglinnot_alpha;
        }
        else if(graph_type.equals(ProtGraph.GRAPHTYPE_BETA)) {
            linnot_table = DBManager.tbl_fglinnot_beta;
        }
        else if(graph_type.equals(ProtGraph.GRAPHTYPE_ALBE)) {
            linnot_table = DBManager.tbl_fglinnot_albe;
        }
        else if(graph_type.equals(ProtGraph.GRAPHTYPE_ALPHALIG)) {
            linnot_table = DBManager.tbl_fglinnot_alphalig;
        }
        else if(graph_type.equals(ProtGraph.GRAPHTYPE_BETALIG)) {
            linnot_table = DBManager.tbl_fglinnot_betalig;
        }
        else if(graph_type.equals(ProtGraph.GRAPHTYPE_ALBELIG)) {
            linnot_table = DBManager.tbl_fglinnot_albelig;
        }
        else {
            DP.getInstance().e("DBManager", "Wrong folding graph type '" + graph_type + "', writing linear notation to DB not supported.");
            return -1L;
        }
        
        
        Long fgdbid = -1L;
        try {
            fgdbid = DBManager.getDBFoldingGraphID(pdb_id, chain_name, graph_type, fg_number);
        } catch(SQLException e) {
            DP.getInstance().e("DBManager", "Error finding " + pdb_id + " chain " + chain_name + " " + graph_type + " folding graph: '" + e.getMessage() + "'. Cannot write linear notations.");
            return -1L;
        }
        
        if(fgdbid < 0) {
            DP.getInstance().e("DBManager", "Could not find " + pdb_id + " chain " + chain_name + " " + graph_type + " folding graph graph in DB. Cannot write linear notations.");
            return -1L;
        }
        
        PreparedStatement statement = null;
        

        String query = "INSERT INTO " + linnot_table + " (linnot_foldinggraph_id, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_key, firstvertexpos_seq) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        int affectedRows = 0;
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setLong(1, fgdbid);
            statement.setString(2, pnfr.adjNotation);
            statement.setString(3, pnfr.redNotation);
            statement.setString(4, pnfr.keyNotation);            
            statement.setString(5, pnfr.seqNotation);
            statement.setInt(6, pnfr.adjStart);
            statement.setInt(7, pnfr.redStart);
            statement.setInt(8, pnfr.keyStart);
            statement.setInt(9, pnfr.seqStart);
                                
            affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                DP.getInstance().w("Inserting folding graph linear notation into DB failed, no rows affected.");
            }
            
            // get DB insert id
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                insertID = generatedKeys.getLong(1);
            } else {
                DP.getInstance().w("Inserting folding graph linear notation into DB failed, no generated key obtained.");
            }
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeFoldLinearNotationsToDatabase: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeFoldLinearNotationsToDatabase: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeFoldLinearNotationsToDatabase: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(insertID);                
    }
    */
    
    
    /**
     * Writes all 4 linear notations of a single fold (connected component) to the database. This function uses a single table for all graph types instead of separate tables.
     * The table has a graph_type field, which is rather ugly because the graph type could be deduced from the protein graph linked to the folding graph that is linked to this notation.
     * The field makes finding stuff faster though and enables me to set a unique (linnot_foldinggraph_id, linnot_graph_type) constraint on the table.
     * @param pdb_id the PBD id
     * @param chain_name the chain name
     * @param graph_type the graph type string, use constants
     * @param fg_number the fg_number of the fold, the FG knows it
     * @param pnfr the results of computing the linear notations
     * @return the DB id of the insert or -1 if nothing has been inserted
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Long writeFoldLinearNotationsToDatabaseSingleTable(String pdb_id, String chain_name, String graph_type, Integer fg_number, PTGLNotationFoldResult pnfr) throws SQLException {
        
        Long insertID = -1L;
        ResultSet generatedKeys = null;
        
        Integer graphTypeInt = ProtGraphs.getGraphTypeCode(graph_type);
        if(graphTypeInt < 0) {
            DP.getInstance().e("DBManager", "writeFoldLinearNotationsToDatabaseSingleTable(): Invalid graph type given.");
            return -1L;
        }
                                
        Long fgdbid = -1L;
        try {
            fgdbid = DBManager.getDBFoldingGraphID(pdb_id, chain_name, graph_type, fg_number);
        } catch(SQLException e) {
            DP.getInstance().e("DBManager", "Error finding " + pdb_id + " chain " + chain_name + " " + graph_type + " folding graph: '" + e.getMessage() + "'. Cannot write linear notations.");
            return -1L;
        }
        
        if(fgdbid < 0) {
            DP.getInstance().e("DBManager", "Could not find " + pdb_id + " chain " + chain_name + " " + graph_type + " folding graph graph in DB. Cannot write linear notations.");
            return -1L;
        }
        
        PreparedStatement statement = null;
        

        String query = "INSERT INTO " + DBManager.tbl_fglinnot + " (denorm_pdb_id, denorm_chain_name, denorm_graph_type, denorm_graph_type_string, linnot_foldinggraph_id, ptgl_linnot_adj, ptgl_linnot_red, ptgl_linnot_key, ptgl_linnot_seq, firstvertexpos_adj, firstvertexpos_red, firstvertexpos_key, firstvertexpos_seq, num_sses) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        int affectedRows = 0;
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            // the first 3 values are de-normalization for perfermance reasons. You could also join via the fgdbid to get them.
            statement.setString(1, pdb_id);
            statement.setString(2, chain_name);
            statement.setInt(3, graphTypeInt);
            statement.setString(4, graph_type);
            
            statement.setLong(5, fgdbid);
            //statement.setInt(2, graphTypeInt);
            statement.setString(6, pnfr.adjNotation);
            statement.setString(7, pnfr.redNotation);
            statement.setString(8, pnfr.keyNotation);            
            statement.setString(9, pnfr.seqNotation);
            statement.setInt(10, pnfr.adjStart);
            statement.setInt(11, pnfr.redStart);
            statement.setInt(12, pnfr.keyStartFG);
            statement.setInt(13, pnfr.seqStart);
            statement.setInt(14, pnfr.adjSize);
                                
            affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                DP.getInstance().w("Inserting folding graph linear notation into DB failed, no rows affected.");
            }
            
            // get DB insert id
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                insertID = generatedKeys.getLong(1);
            } else {
                DP.getInstance().w("Inserting folding graph linear notation into DB failed, no generated key obtained.");
            }
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeFoldLinearNotationsToDatabase: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeFoldLinearNotationsToDatabase: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeFoldLinearNotationsToDatabase: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(insertID);                
    }
    
    
    /**
     * Determines DB field name. Name of the field that stores the path to the protein graph image in given format.
     * @param format the format, use DrawTools.FORMAT_* constants
     * @return the name of the database field that stores the path to the graph image in given format and representation, or null if no field for the combination exists in the DB modell
     */
    public static String getFieldnameForProteinGraphImageFormat(String format) {
        String fieldName = null;
        
        if(format.equals(DrawTools.FORMAT_PNG)) {            
            fieldName = "graph_image_png";
        }
        else if(format.equals(DrawTools.FORMAT_SVG)) {
            fieldName = "graph_image_svg";
        }
        else if(format.equals(DrawTools.FORMAT_JPEG)) {
            fieldName = "graph_image_jpg";
        }
        else if(format.equals(DrawTools.FORMAT_TIFF)) {
            fieldName = "graph_image_tif";
        }
        else if(format.equals(DrawTools.FORMAT_PDF)) {
            fieldName = "graph_image_pdf";
        }
        
        return fieldName;
    }
        
    
    /**
     * Determines DB field name. Name of the field that stores the path to the graph image in given format and representation.
     * @param format the format, use DrawTools.FORMAT_* constants
     * @param notation the notation, use ProtGraphs.GRAPHNOTATION_* constants
     * @return the name of the database field that stores the path to the graph image in given format and representation, or null if no field for the combination exists in the DB modell
     */
    public static String getFieldnameForFoldingGraphImageType(String format, String notation) {
        String fieldName = null;
        
        if(format.equals(DrawTools.FORMAT_PNG)) {            
            if(notation.equals(ProtGraphs.GRAPHNOTATION_ADJ)) {
                fieldName = "filepath_linnot_image_adj_png";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_RED)) {
                fieldName = "filepath_linnot_image_red_png";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_SEQ)) {
                fieldName = "filepath_linnot_image_seq_png";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_KEY)) {
                fieldName = "filepath_linnot_image_key_png";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_DEF)) {
                fieldName = "filepath_linnot_image_def_png";
            }
        }
        else if(format.equals(DrawTools.FORMAT_SVG)) {
            if(notation.equals(ProtGraphs.GRAPHNOTATION_ADJ)) {
                fieldName = "filepath_linnot_image_adj_svg";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_RED)) {
                fieldName = "filepath_linnot_image_red_svg";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_SEQ)) {
                fieldName = "filepath_linnot_image_seq_svg";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_KEY)) {
                fieldName = "filepath_linnot_image_key_svg";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_DEF)) {
                fieldName = "filepath_linnot_image_def_svg";
            }
            
        }
        else if(format.equals(DrawTools.FORMAT_PDF)) {            
            if(notation.equals(ProtGraphs.GRAPHNOTATION_ADJ)) {
                fieldName = "filepath_linnot_image_adj_pdf";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_RED)) {
                fieldName = "filepath_linnot_image_red_pdf";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_SEQ)) {
                fieldName = "filepath_linnot_image_seq_pdf";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_KEY)) {
                fieldName = "filepath_linnot_image_key_pdf";
            }
            else if(notation.equals(ProtGraphs.GRAPHNOTATION_DEF)) {
                fieldName = "filepath_linnot_image_def_pdf";
            }
        }
        
        return fieldName;
    }
    
    /**
     * Determines the database field name (in the plcc_graph table) for the given image representation.
     * @param graphImageRepresentationType the graph image representation, use constants in ProtGraphs class, e.g., ProtGraphs.GRAPHIMAGE_BITMAP_REPRESENTATION_VPLG_DEFAULT.
     * @return the field name or null if the representation is invalid
     */
    public static String getFieldnameForGraphImageRepresentationType(String graphImageRepresentationType) {
        String fieldName = null;
        
        switch (graphImageRepresentationType) {
            case ProtGraphs.GRAPHIMAGE_PNG_DEFAULT:
                fieldName = "graph_image_png";
                break;
            case ProtGraphs.GRAPHIMAGE_SVG_DEFAULT:
                fieldName = "graph_image_svg";
                break;
            case ProtGraphs.GRAPHIMAGE_PNG_ADJ:
                fieldName = "graph_image_adj_png";
                break;
            case ProtGraphs.GRAPHIMAGE_SVG_ADJ:
                fieldName = "graph_image_adj_svg";
                break;
            case ProtGraphs.GRAPHIMAGE_PNG_RED:
                fieldName = "graph_image_red_png";
                break;
            case ProtGraphs.GRAPHIMAGE_SVG_RED:
                fieldName = "graph_image_red_svg";
                break;
            case ProtGraphs.GRAPHIMAGE_PNG_KEY:
                fieldName = "graph_image_key_png";
                break;
            case ProtGraphs.GRAPHIMAGE_SVG_KEY:
                fieldName = "graph_image_key_svg";
                break;
            case ProtGraphs.GRAPHIMAGE_PNG_SEQ:
                fieldName = "graph_image_seq_png";
                break;
            case ProtGraphs.GRAPHIMAGE_SVG_SEQ:
                fieldName = "graph_image_seq_svg";
                break;
            // PDF
            case ProtGraphs.GRAPHIMAGE_PDF_DEFAULT:
                fieldName = "graph_image_pdf";
                break;
            case ProtGraphs.GRAPHIMAGE_PDF_ADJ:
                fieldName = "graph_image_adj_pdf";
                break;
            case ProtGraphs.GRAPHIMAGE_PDF_RED:
                fieldName = "graph_image_red_pdf";
                break;
            case ProtGraphs.GRAPHIMAGE_PDF_KEY:
                fieldName = "graph_image_key_pdf";
                break;
            case ProtGraphs.GRAPHIMAGE_PDF_SEQ:
                fieldName = "graph_image_seq_pdf";
                break;
        }
        
        return fieldName;
    }
    
    
    
    /**
     * Sets the image path for a specific protein graph representation in the database. The graph has to exist in the database already.
     * 
     * @param graphDatabaseID the graph ID in the database. Use the getGrapDatabaseID function if you do not know it.
     * @param format The image format, see DrawTools.IMAGEFORMAT
     * @param relativeImagePath the relative image path to set in the database for the specified representation
     * @return the number of rows affected by the SQL query
     * @throws SQLException if something goes wrong with the database
     */
    public static Integer updateProteinGraphImagePathInDB(Long graphDatabaseID, IMAGEFORMAT format, String relativeImagePath) throws SQLException {
        
        PreparedStatement statement = null;
        String graphImageFieldName = DBManager.getFieldnameForProteinGraphImageFormat(format.toString());
        if(graphImageFieldName == null) {
            DP.getInstance().w("DBManager", "updateProteinGraphImagePathInDB: Invalid graph image represenation type (or no field for type in DB). Cannot set protein graph image path in database.");
            return 0;
        }

        String query = "UPDATE " + tbl_proteingraph + " SET " + graphImageFieldName + " = ? WHERE graph_id = ?;";
        Integer numRowsAffected = 0;
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setString(1, relativeImagePath);
            statement.setLong(2, graphDatabaseID);
                                
            numRowsAffected = statement.executeUpdate();
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: updateProteinGraphImagePathInDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: updateProteinGraphImagePathInDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: updateProteinGraphImagePathInDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        } 
       
        return numRowsAffected;
        
    }
    
    
    /**
     * Sets the path for a specific protein graph output text file format (e.g., GML) in the database. The graph has to exist in the database already.
     * 
     * @param graphDatabaseID the graph ID in the database. Use the getGrapDatabaseID function if you do not know it.
     * @param format The format, e.g., "GML"
     * @param relativeFilePath the relative image path to set in the database for the specified representation
     * @return the number of rows affected by the SQL query
     * @throws SQLException if something goes wrong with the database
     */
    public static Integer updateProteinGraphTextformatPathInDB(Long graphDatabaseID, String format, String relativeFilePath) throws SQLException {
        
        PreparedStatement statement = null;
        String dbFieldNameGraphFile = "";

        if(format.equals(GraphFormats.GRAPHFORMAT_GML)) {
            dbFieldNameGraphFile = "filepath_graphfile_gml";            
        } else if(format.equals(GraphFormats.GRAPHFORMAT_JSON)) {
            dbFieldNameGraphFile = "filepath_graphfile_json";
        } else if(format.equals(GraphFormats.GRAPHFORMAT_XML)) {
            dbFieldNameGraphFile = "filepath_graphfile_xml";
        } else if(format.equals(GraphFormats.GRAPHFORMAT_VPLG)) {
            dbFieldNameGraphFile = "filepath_graphfile_plcc";
        } else if(format.equals(GraphFormats.GRAPHFORMAT_DOTLANGUAGE)) {
            dbFieldNameGraphFile = "filepath_graphfile_dotlanguage";
        } else if(format.equals(GraphFormats.GRAPHFORMAT_KAVOSH)) {
            dbFieldNameGraphFile = "filepath_graphfile_kavosh";
        } else {
            // TODO: create fields in DB and handle other fields here
            DP.getInstance().w("updateProteinGraphTextformatPathInDB", "Format '" + format + "' not supported by DB scheme yet, ignoring.");
            return 0;
        }
        
        String query = "UPDATE " + tbl_proteingraph + " SET " + dbFieldNameGraphFile + " = ? WHERE graph_id = ?;";
        Integer numRowsAffected = 0;
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setString(1, relativeFilePath);
            statement.setLong(2, graphDatabaseID);
                                
            numRowsAffected = statement.executeUpdate();
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: updateProteinGraphImagePathInDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: updateProteinGraphImagePathInDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: updateProteinGraphImagePathInDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        } 
       
        return numRowsAffected;
        
    }
    
    
    /**
     * Updates secondat data on an SSE in the database.
     * @param sseDatabaseID the internal database ID of the SSE
     * @param fg_graph_type the graph type of the folding graph (which is the graph type of its parent protein graph). Use ProtGraphs.GRAPHTYPE_STRING_*.
     * @param fg_number the folding graph number
     * @param fg_foldname the fold name
     * @param sse_position_in_fg the position of the SSE within the folding graph
     * @return the number of affected rows
     * @throws SQLException if SQL stuff went wrong
     */
    public static Integer updateSecondatSSEInfoForGraphInDB(Long sseDatabaseID, String fg_graph_type, Integer fg_number, String fg_foldname, Integer sse_position_in_fg) throws SQLException {
        
        PreparedStatement statement = null;
        
        if( ! ProtGraphs.isValidGraphTypeString(fg_graph_type)) {
            return 0;
        }
        
        String verified_graphtype = fg_graph_type;
        
        String fg_number_field =  verified_graphtype + "_fg_number";
        String fg_foldname_field =  verified_graphtype + "_fg_foldname";
        String fg_position_field =  verified_graphtype + "_fg_position";

        String query = "UPDATE " + tbl_secondat + " SET " + fg_number_field + " = ?, " + fg_foldname_field + " = ?, " + fg_position_field + " = ? WHERE sse_id = ?;";
        Integer numRowsAffected = 0;
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            statement.setInt(1, fg_number);
            statement.setString(2, fg_foldname);
            statement.setInt(3, sse_position_in_fg);
            statement.setLong(4, sseDatabaseID);
                                
            numRowsAffected = statement.executeUpdate();
            //dbc.commit();
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "updateSecondatSSEInfoForGraphInDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    DP.getInstance().e("DBManager", "updateSecondatSSEInfoForGraphInDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    DP.getInstance().e("DBManager", "updateSecondatSSEInfoForGraphInDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        } 
       
        return numRowsAffected;
        
    }
    
    /**
     * Sets the image path for a specific folding graph representation in the database. The graph has to exist in the database already.
     * 
     * @param foldingGraphDatabaseID the folding graph ID in the database.
     * @param format The image format, see DrawTools.IMAGEFORMAT
     * @param notation The graph notation type, e.g., "KEY". Use the constants in ProtGraphs class, e.g., ProtGraphs.GRAPHNOTATION_KEY.
     * @param relativeImagePath the relative image path to set in the database for the specified representation
     * @return the number of rows affected by the SQL query
     * @throws SQLException if something goes wrong with the database
     */        
    public static Integer updateFoldingGraphImagePathInDB(Long foldingGraphDatabaseID, IMAGEFORMAT format, String notation, String relativeImagePath) throws SQLException {
        
        PreparedStatement statement = null;
        String graphImageFieldName = DBManager.getFieldnameForFoldingGraphImageType(format.toString(), notation);
        if(graphImageFieldName == null) {
            DP.getInstance().e("DBManager", "updateFoldingGraphImagePathInDB: Invalid folding graph image represenation type. Cannot set folding graph image path in database.");
            return 0;
        }

        String query = "UPDATE " + tbl_fglinnot + " SET " + graphImageFieldName + " = ? WHERE linnot_foldinggraph_id = ?;";        
        
        Integer numRowsAffected = 0;
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setString(1, relativeImagePath);
            statement.setLong(2, foldingGraphDatabaseID);
                                
            numRowsAffected = statement.executeUpdate();
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: updateFoldingGraphImagePathInDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: updateFoldingGraphImagePathInDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: updateFoldingGraphImagePathInDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        } 
       
        return numRowsAffected;
        
    }
    
    
    
    /**
     * Assigns the SSEs in the list to the given protein graph (identified by PDB ID, chain ID and graph type) in the database, using the order
     * of the SSEs in the input list.
     * @param sses a list of SSEs, in the order of the graph (N to C terminus on the chain, but some SSEs of the chain may be missing of course, depending on the graph type)
     * @param pdb_id the PDB ID of the graph
     * @param chain_name the chain name of the graph
     * @param graph_type the graph type. Use the integer codes in ProtGraphs class
     * @return the number of SSEs successfully assigned to the graph
     * @throws SQLException if something goes wrong with the database server
     */
    public static Integer assignSSEsToProteinGraphInOrder(ArrayList<SSE> sses, String pdb_id, String chain_name, Integer graph_type) throws SQLException {
        Integer numAssigned = 0;
        
        Long chain_id = getDBChainID(pdb_id, chain_name);
        if(chain_id <= 0) {
            DP.getInstance().e("DBManager", "assignSSEsToProteinGraphInOrder(): Chain not found in DB, cannot assign SSEs.");
            return 0;
        }
        
        ArrayList<Long> sseDBids = new ArrayList<Long>();
        for(SSE sse : sses) {
            Long sseID = DBManager.getDBSseIDByDsspStartResidue(sse.getStartDsspNum(), chain_id);
            if(sseID > 0) {
                sseDBids.add(sseID);
            }
            else {
                DP.getInstance().e("DBManager", "assignSSEsToProteinGraphInOrder(): SSE not found in DB, cannot assign it to graph.");
            }
        }
        
        Long graphDbID = DBManager.getDBProteinGraphID(pdb_id, chain_name, ProtGraphs.getGraphTypeString(graph_type));
        if(graphDbID > 0) {
            for(int i = 0; i < sseDBids.size(); i++) {
                numAssigned += DBManager.assignSSEtoProteinGraph(sseDBids.get(i), graphDbID, (i+1));
            }                            
        } else {
            DP.getInstance().e("DBManager", "assignSSEsToProteinGraphInOrder(): Graph not found in DB, cannot assign SSEs to it.");            
        }                
        
        return numAssigned;
    }
    
    /**
     * Assigns the SSEs in the list to the given folding graph (identified by PDB ID, chain ID and graph type) in the database, using the order
     * of the SSEs in the input list.
     * @param sses a list of SSEs, in the order of the graph (N to C terminus on the chain, but some SSEs of the chain may be missing of course, depending on the graph type)
     * @param foldingGraphDbId the database ID of the folding graph
     * @return the number of SSEs successfully assigned to the graph
     * @throws SQLException if something goes wrong with the database server
     */
    public static Integer assignSSEsToFoldingGraphInOrder(ArrayList<SSE> sses, Long foldingGraphDbId) throws SQLException {
        Integer numAssigned = 0;
        
        if(foldingGraphDbId < 1) {
            DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): Folding graph database ID must be >= 1 but is  '" + foldingGraphDbId + " ', aborting.");
            return 0;
        }
        
        Long chain_database_id = DBManager.getDBChainIDofFoldingGraph(foldingGraphDbId);
        
        if(chain_database_id < 1) {
            DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): Could not find chain of folding graph with ID '" + foldingGraphDbId + " ' in database.");
            return 0;
        }
        
        ArrayList<Long> sseDBids = new ArrayList<Long>();
        for(SSE sse : sses) {
            Long sseID = DBManager.getDBSseIDByDsspStartResidue(sse.getStartDsspNum(), chain_database_id);
            if(sseID > 0) {
                sseDBids.add(sseID);
            }
            else {
                DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): SSE not found in DB, cannot assign it to graph.");
            }
        }
        
       

        if(foldingGraphDbId > 0) {
            for(int i = 0; i < sseDBids.size(); i++) {
                numAssigned += DBManager.assignSSEtoFoldingGraph(sseDBids.get(i), foldingGraphDbId, (i+1));
            }                            
        } else {
            DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): Graph not found in DB, cannot assign SSEs to it.");            
        }
        
        return numAssigned;
    }
    
    /**
     * Assigns the SSEs in the list to the given folding graph (identified by PDB ID, chain ID and graph type) in the database, using the order
     * of the SSEs in the input list.
     * @param sses a list of SSEs, in the order of the graph (N to C terminus on the chain, but some SSEs of the chain may be missing of course, depending on the graph type)
     * @param foldingGraphDbId the database ID of the folding graph
     * @param fg_graph_type the graph type (used for secondat only)
     * @param fg_number the folding graph number (used for secondat only)
     * @param fg_foldname the folding graph fold name (depends on fg_number, used for secondat only)
     * @return an Integer array: position 0 = the number of SSEs successfully assigned to the graph in sse2fg table. position 1 = the number assigned in secondat table.
     * @throws SQLException if something goes wrong with the database server
     */
    public static Integer[] assignSSEsToFoldingGraphInOrderWithSecondat(ArrayList<SSE> sses, Long foldingGraphDbId, String fg_graph_type, Integer fg_number, String fg_foldname) throws SQLException {
        Integer numAssignedSSE2FG = 0;
        Integer numAssignedSecondat = 0;
        
        if(foldingGraphDbId < 1) {
            DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): Folding graph database ID must be >= 1 but is  '" + foldingGraphDbId + " ', aborting.");
            return new Integer[]{ 0, 0 };
        }
        
        Long chain_database_id = DBManager.getDBChainIDofFoldingGraph(foldingGraphDbId);
        
        if(chain_database_id < 1) {
            DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): Could not find chain of folding graph with ID '" + foldingGraphDbId + " ' in database.");
            return new Integer[]{ 0, 0 };
        }
        
        ArrayList<Long> sseDBids = new ArrayList<Long>();
        for(SSE sse : sses) {
            Long sseID = DBManager.getDBSseIDByDsspStartResidue(sse.getStartDsspNum(), chain_database_id);
            if(sseID > 0) {
                sseDBids.add(sseID);
            }
            else {
                DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): SSE not found in DB, cannot assign it to graph.");
            }
        }
                

        if(foldingGraphDbId > 0) {
            for(int i = 0; i < sseDBids.size(); i++) {
                numAssignedSSE2FG += DBManager.assignSSEtoFoldingGraph(sseDBids.get(i), foldingGraphDbId, (i+1));
                numAssignedSecondat += updateSecondatSSEInfoForGraphInDB(sseDBids.get(i), fg_graph_type, fg_number, fg_foldname, (i+1));
            }                            
        } else {
            DP.getInstance().e("DBManager", "assignSSEsToFoldingGraphInOrder(): Graph not found in DB, cannot assign SSEs to it.");            
        }
        
        return new Integer[]{ numAssignedSSE2FG, numAssignedSecondat };
    }
    
    
    
    /**
     * Determines the internal database ID (primary key) of the protein chain of the given folding graph.
     * @param foldingGraphDbId the internal folding graph ID from the database
     * @return the database ID or a value smaller than zero if no such chain (or graph) exists
     * @throws SQLException 
     */
    public static Long getDBChainIDofFoldingGraph(Long foldingGraphDbId) throws SQLException {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT c.chain_id FROM " + tbl_foldinggraph + " f INNER JOIN " + tbl_proteingraph + " p ON f.parent_graph_id = p.graph_id INNER JOIN " + tbl_chain + " c ON p.chain_id = c.chain_id WHERE (f.foldinggraph_id = ?) ;";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, foldingGraphDbId);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getDBChainIDofFoldingGraph(): '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
                //dbc.setAutoCommit(true);
            } catch(Exception e) { DP.getInstance().w("DB: getDBChainIDofFoldingGraph(): Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(Long.valueOf(tableData.get(0).get(0)));
            }
            else {
                DP.getInstance().w("DB: getDBChainIDofFoldingGraph(): Folding graph not in DB.");
                return(-1L);
            }
        }
        else {
            return(-1L);
        }        
                      
    }
    
    
    
    /**
     * Assigns an SSE to a protein graph, defining its position in it.
     * @param sseDbId the database id (primary key) of the SSE
     * @param graphDbId the database id (primary key) of the graph
     * @param ssePositionInGraph the position of the SSE in the graph. The first SSE should be 1 (NOT 0).
     * @return the number of affected rows (1 on success, 0 on error)
     * @throws java.sql.SQLException if something goes wrong with the DB
     */
    public static Integer assignSSEtoProteinGraph(Long sseDbId, Long graphDbId, Integer ssePositionInGraph) throws SQLException {
        if(ssePositionInGraph <= 0) {
            DP.getInstance().e("DBManager", "assignSSEToProteinGraph(): ssePositionInGraph must be > 0, skipping SSE assignment.");            
            return 0;
        }
        
        Integer numRowsAffected = 0;
        
        // assign SSE
        PreparedStatement statement = null;
        
        String query = "INSERT INTO " + tbl_nm_ssetoproteingraph + " (sse_id, graph_id, position_in_graph) VALUES (?, ?, ?);";
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setLong(1, sseDbId);
            statement.setLong(2, graphDbId);
            statement.setInt(3, ssePositionInGraph);
                                
            numRowsAffected = statement.executeUpdate();
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: assignSSEToProteinGraph(): '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: assignSSEToProteinGraph(): Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: assignSSEToProteinGraph(): Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        } 
        
        return numRowsAffected;
    }
    
    /**
     * Assigns an SSE to a protein graph, defining its position in it.
     * @param chainDbId the database id (primary key) of the chain
     * @param ligand_name3 the 3-char ligand name (PDB ligand expo)
     * @return the number of affected rows (1 on success, 0 on error)
     * @throws java.sql.SQLException if something goes wrong with the DB
     */
    public static synchronized Integer assignLigandToProteinChain(Long chainDbId, String ligand_name3) throws SQLException {
        
        Integer numRowsAffected = 0;
        
        // assign SSE
        PreparedStatement statement = null;
        
        String query = "INSERT INTO " + tbl_nm_ligandtochain + " (ligandtochain_chainid, ligandtochain_ligandname3) VALUES (?, ?);";
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setLong(1, chainDbId);
            statement.setString(2, ligand_name3);
                                
            numRowsAffected = statement.executeUpdate();
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: assignLigandToProteinChain(): '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: assignLigandToProteinChain(): Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: assignLigandToProteinChain(): Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        } 
        
        return numRowsAffected;
    }
    
    
    /**
     * Assigns an SSE to a folding graph, defining its position in it.
     * @param sseDbId the database id (primary key) of the SSE
     * @param graphDbId the database id (primary key) of the graph
     * @param ssePositionInGraph the position of the SSE in the graph. The first SSE should be 1 (NOT 0).
     * @return the number of affected rows (1 on success, 0 on error)
     * @throws java.sql.SQLException if something goes wrong with the DB
     */
    public static Integer assignSSEtoFoldingGraph(Long sseDbId, Long graphDbId, Integer ssePositionInGraph) throws SQLException {
        if(ssePositionInGraph <= 0) {
            DP.getInstance().e("DBManager", "assignSSEToFoldingGraph(): ssePositionInGraph must be > 0, skipping SSE assignment.");            
            return 0;
        }
        
        Integer numRowsAffected = 0;
        
        // assign SSE
        PreparedStatement statement = null;
        
        String query = "INSERT INTO " + tbl_nm_ssetofoldinggraph + " (sse_id, foldinggraph_id, position_in_graph) VALUES (?, ?, ?);";
        
        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            
            statement.setLong(1, sseDbId);
            statement.setLong(2, graphDbId);
            statement.setInt(3, ssePositionInGraph);
                                
            numRowsAffected = statement.executeUpdate();
            //dbc.commit();
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: assignSSEToFoldingGraph(): '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: assignSSEToFoldingGraph(): Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: assignSSEToFoldingGraph(): Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        } 
        
        return numRowsAffected;    
    }
    
    
    /**
     * Writes information on the graphlet counts for a protein graph to the database. Used by graphlet computation
     * algorithms to store the graphlets. Currently not used because this is done in a separate C++ program.
     * 
     * OLD VERSION, USES INTEGER FIELDS!
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the chain represented by the graph_string
     * @param graph_type the Integer representation of the graph type. use ProtGraphs.getGraphTypeCode() to get it.
     * @param graphlet_counts an array holding the counts for the different graphlet types
     * @return true if the graph was inserted, false if errors occurred
     * @throws SQLException if the data could not be written or the database connection could not be closed or reset to auto commit (in the finally block)
     */
    /*
    @Deprecated
    public static Boolean writeGraphletsToDB(String pdb_id, String chain_name, Integer graph_type, Integer[] graphlet_counts) throws SQLException {

        int numReqGraphletTypes = 3;
        if(graphlet_counts.length != numReqGraphletTypes) {
            System.err.println("ERROR: writeGraphletsToDB: Invalid number of graphlet types specified (got " + graphlet_counts.length + ", required " + numReqGraphletTypes + "). Skipping.");
            return false;
        }
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        Boolean result = false;

        if (chain_db_id < 0) {
            System.err.println("ERROR: writeGraphletsToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_graphletcount + " (chain_id, graph_type, graphlet_count_000, graphlet_count_001, graphlet_count_002) VALUES (?, ?, ?, ?, ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, graph_type);
            statement.setInt(3, graphlet_counts[0]);
            statement.setInt(4, graphlet_counts[1]);
            statement.setInt(5, graphlet_counts[2]);
                                
            statement.executeUpdate();
            //dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeGraphletsToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeGraphletsToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeGraphletsToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        return(result);
    }
    */
    
    /**
     * Formats the array for SQL insert into an array field.
     * @param arr the input array
     * @return the SQL String for the array, e.g., "'{0.81, 0.45}'".
     */
    public static String getSQLArrayString(Double[] arr) {
        StringBuilder pgArrayString = new StringBuilder();
        String valStr;
        pgArrayString.append("'{");
        for(int i = 0; i < arr.length; i++) {
            valStr = String.format(Locale.ENGLISH, "%.2f", arr[i]);
            pgArrayString.append(valStr);            
            if(i < arr.length - 1) {
                pgArrayString.append(", ");
            }
        }
        pgArrayString.append("}'");
        return pgArrayString.toString();
    }
    
    /**
     * Writes information on the normalized graphlet counts for a protein graph to the database. Used by graphlet computation
     * algorithms to store the graphlets. Currently not used because this is done in a separate C++ program.
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the chain represented by the graph_string
     * @param graph_type the Integer representation of the graph type. use ProtGraphs.getGraphTypeCode() to get it.
     * @param graphlet_counts an array holding the counts for the different graphlet types
     * @return true if the graph was inserted, false if errors occurred
     * @throws SQLException if the data could not be written or the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Boolean writeNormalizedGraphletsToDB(String pdb_id, String chain_name, Integer graph_type, Double[] graphlet_counts) throws SQLException {

        int numReqGraphletTypes = Settings.getInteger("plcc_I_number_of_graphlets");
        if(graphlet_counts.length != numReqGraphletTypes) {
            System.err.println("ERROR: writeNormalizedGraphletsToDB: Invalid number of graphlet types specified (got " + graphlet_counts.length + ", required " + numReqGraphletTypes + "). Skipping graphlets.");
            return false;
        }                
        
        Long graph_db_id = DBManager.getDBProteinGraphID(pdb_id, chain_name, ProtGraphs.getGraphTypeString(graph_type));
        if (graph_db_id < 0) {
            System.err.println("ERROR: writeNormalizedGraphletsToDB: Could not find graph with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert graphlets.");
            return (false);
        }

        if(DBManager.graphletsExistsInDBForGraph(graph_db_id)) {
            int numDel = DBManager.deleteGraphletsFromDBForGraph(graph_db_id);      
            System.out.println("Deleted " + numDel + " graphlet counts for graph with ID " + graph_db_id + ".");
        }

        StringBuilder querySB = new StringBuilder();
        querySB.append("INSERT INTO " + tbl_graphletcount + " (graph_id, graphlet_counts) VALUES ( ");
        
        String pgArray = DBManager.getSQLArrayString(graphlet_counts);
        
        querySB.append(graph_db_id);
        querySB.append(", ");
        querySB.append(pgArray);
        querySB.append(" )");
        
        String query = querySB.toString();
        
        int numAffected = DBManager.doInsertQuery(query);
        Boolean result = (numAffected > 0);
        
        return(result);
    }
    

    /**
     * Writes information on a SSE contact to the database.
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name, e.g., A
     * @param sse1_dssp_start the DSSP number of the first residue of the first SSE
     * @param sse2_dssp_start the DSSP number of the first residue of the second SSE
     * @param contact_type the contact type code
     * @return whether it worked out
     * @throws java.sql.SQLException if something goes wrong with the DB
     */
    public static Boolean writeContactToDB(String pdb_id, String chain_name, Integer sse1_dssp_start, Integer sse2_dssp_start, Integer contact_type) throws SQLException {

        // Just abort if this is not a valid contact type. Note that 0 is CONTACT_NONE.
        if (contact_type <= 0) {
            return (false);
        }

        Long db_chain_id = getDBChainID(pdb_id, chain_name);

        if (db_chain_id <= 0L) {
            System.err.println("ERROR: DB: writeContactToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        Long sse1_id = getDBSseIDByDsspStartResidue(sse1_dssp_start, db_chain_id);
        Long sse2_id = getDBSseIDByDsspStartResidue(sse2_dssp_start, db_chain_id);
        Long tmp;

        // We may need to switch the IDs to make sure the 1st of them is always lower
        if (sse1_id > sse2_id) {
            tmp = sse2_id;
            sse2_id = sse1_id;
            sse1_id = tmp;
        }

        Boolean result = false;
        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_ssecontact + " (sse1, sse2, contact_type) VALUES (?, ?, ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, sse1_id);
            statement.setLong(2, sse2_id);
            statement.setInt(3, contact_type);
                                
            statement.executeUpdate();
            //dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeContactToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeContactToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeContactToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
                
        return(result);
    }

    /**
     * Batch writes all SSE contacts to the DB in a single commit.
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the protein chain
     * @param contactInfoList a list of contacts. Each entry is of length 3, and the three positions in the array are: 0=DSSP residue number of the first residue of the first SSE which is part of the SSE contact. 1=the same for the second SSE. 2=the spatial contact code.
     * @return the total DB insert count
     * @throws SQLException if DB stuff went wrong
     */
    public static int batchWriteContactsToDB(String pdb_id, String chain_name, List<Integer[]> contactInfoList) throws SQLException {
        if(contactInfoList.isEmpty()) {
            return 0;
        }
        
        Long db_chain_id = getDBChainID(pdb_id, chain_name);

        if (db_chain_id <= 0L) {
            DP.getInstance().e("DBManager", "batchWriteContactsToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (0);
        }
              
        PreparedStatement statement = null;                
        ResultSet generatedKeys = null;

        // it is kind of ugly that we need to do this many queries to determine the SSE DB ids, may the insert could do it?
        List<Long[]> sse_db_ids = new ArrayList<>();
        Long tmp, sse1_id, sse2_id;
        for(Integer[] contactInfo : contactInfoList) {
            sse1_id = getDBSseIDByDsspStartResidue(contactInfo[0], db_chain_id);
            sse2_id = getDBSseIDByDsspStartResidue(contactInfo[1], db_chain_id);
            // We may need to switch the IDs to make sure the 1st of them is always lower
            if (sse1_id > sse2_id) {
                tmp = sse2_id;
                sse2_id = sse1_id;
                sse1_id = tmp;
            }
            sse_db_ids.add(new Long[] { sse1_id, sse2_id});
        }               
        
        String query = "INSERT INTO " + tbl_ssecontact + " (sse1, sse2, contact_type) VALUES (?, ?, ?);";

        int updateCount = 0;        
        Boolean oldAutoCommitSetting = dbc.getAutoCommit();
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            for(int i = 0; i < contactInfoList.size(); i++) {
            
                statement.setLong(1, sse_db_ids.get(i)[0]);
                statement.setLong(2, sse_db_ids.get(i)[1]);
                statement.setInt(3, contactInfoList.get(i)[2]);
            
                statement.addBatch();
                
            }
            
            int[] count = statement.executeBatch();                        
            
            dbc.commit();            
            
            updateCount = 0;
            for(int i = 0; i < count.length; i++) { updateCount += count[i]; }

        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "batchWriteContactsToDB: Batch SQL failed: '" + e.getMessage() + "'.");
            SQLException se = e.getNextException();
            if(se != null) {
                DP.getInstance().e("DBManager", "batchWriteContactsToDB: Batch Exception: '" + se.getMessage() + "'.");                
            }
            
            if (dbc != null) {
                try {
                    DP.getInstance().e("DBManager", "batchWriteContactsToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    DP.getInstance().e("DBManager", "batchWriteContactsToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }

        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(oldAutoCommitSetting);
        }
        return(updateCount);                       
        
    }
    
    
    /**
     * Writes the graphlet similarity score to the DB for the given graph pair
     * @param src_pdb_id the source graph PDB ID
     * @param src_chain_name the source graph chain name
     * @param tgt_pdb_id the target graph PDB ID
     * @param tgt_chain_name the target graph chain name
     * @param graph_type the graph type (for both of them)
     * @param score the similarity score
     * @return whether it worked out
     * @throws SQLException if something went wrong
     */
    public static Boolean writeGraphletSimilarityScoreToDB(String src_pdb_id, String src_chain_name, String tgt_pdb_id, String tgt_chain_name, String graph_type, Double score) throws SQLException {
        Long src_graph_id = DBManager.getDBProteinGraphID(src_pdb_id, src_chain_name, graph_type);
        Long tgt_graph_id = DBManager.getDBProteinGraphID(tgt_pdb_id, tgt_chain_name, graph_type);
        
        if(src_graph_id < 0 || tgt_graph_id < 0) {
            return false;
        }
        
        return DBManager.writeGraphletSimilarityScoreToDB(src_graph_id, tgt_graph_id, score);
    }
    
    /**
     * Writes information on a graphlet similarity between two protein graphs to the DB.
     * @param source_graph_id the first graph db id
     * @param target_graph_id the second graph db id
     * @param score the similarity score
     * @return whether it worked out
     * @throws java.sql.SQLException if something goes wrong with the DB
     */
    public static Boolean writeGraphletSimilarityScoreToDB(Long source_graph_id, Long target_graph_id, Double score) throws SQLException {

        

        if (source_graph_id < 0 || target_graph_id < 0) {            
            return (false);
        }
        
        if(DBManager.graphletSimilarityScoreExistsInDBForGraphs(source_graph_id, target_graph_id)) {
            deleteGraphletSimilaritiesFromDBForGraphs(source_graph_id, target_graph_id);
        }

        Boolean result = false;
        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_graphletsimilarity + " (graphletsimilarity_sourcegraph, graphletsimilarity_targetgraph, score) VALUES (?, ?, ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, source_graph_id);
            statement.setLong(2, target_graph_id);
            statement.setDouble(3, score);
                                
            statement.executeUpdate();
            //dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeGraphletSimilarityScoreToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeGraphletSimilarityScoreToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeGraphletSimilarityScoreToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
                
        return(result);
    }
    
    /**
     * Writes information on an inter-chain contact between a pair of SSEs from two different chains of a PDB file contact to the database.
     * This stores the complex graph contacts. It is used for statistical purposes only at the moment.
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain1_name the PDB chain name of the first chain involved in the inter-chain contact
     * @param chain2_name the PDB chain name of the second chain involved in the inter-chain contact
     * @param numContactsHH the number of helix-helix contacts
     * @param numContactsHS the number of helix-strand contacts
     * @param numContactsHL the number of helix-ligand contacts
     * @param numContactsSS the number of strand-strand contacts
     * @param numContactsSL the number of strand-ligand contacts
     * @param numContactsLL the number of ligand-ligand contacts
     * @param numContactsDS the number of disulfide contacts
     * @throws SQLException if the data could not be written or the database connection could not be closed or reset to auto commit (in the finally block)
     * @return true if the data was written to the database, false otherwise
     */
    public static Boolean writeInterchainContactsToDB(String pdb_id, String chain1_name, String chain2_name, Integer numContactsHH, Integer numContactsHS, Integer numContactsHL, Integer numContactsSS, Integer numContactsSL, Integer numContactsLL, Integer numContactsDS) throws SQLException {
        
        if(numContactsHH + numContactsHS + numContactsHL + numContactsSS + numContactsSL + numContactsLL + numContactsDS <= 0) {
            System.err.println("WARNING: Not writing interchain contacts to DB for PDB " + pdb_id + " chains " + chain1_name + " and " + chain2_name + ", sum is zero.");
            return false;
        }

        Long chain1_id = getDBChainID(pdb_id, chain1_name);
        Long chain2_id = getDBChainID(pdb_id, chain2_name);

        if (chain1_id < 0) {
            System.err.println("ERROR: DB: writeContactToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain1_name + "' in DB, could not insert complex contact.");
            return (false);
        }
        if (chain2_id < 0) {
            System.err.println("ERROR: DB: writeContactToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain2_name + "' in DB, could not insert complex contact.");
            return (false);
        }
        

        Boolean result = false;
        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_complex_contact_stats + " (chain1, chain2, contact_num_HH, contact_num_HS, contact_num_HL, contact_num_SS, contact_num_SL, contact_num_LL, contact_num_DS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            ////dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            statement.setLong(1, chain1_id);
            statement.setLong(2, chain2_id);
            statement.setInt(3, numContactsHH);
            statement.setInt(4, numContactsHS);
            statement.setInt(5, numContactsHL);
            statement.setInt(6, numContactsSS);
            statement.setInt(7, numContactsSL);
            statement.setInt(8, numContactsLL);
            statement.setInt(9, numContactsDS);
                                
            statement.executeUpdate();
            ////dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeInterchainContactsToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeInterchainContactsToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeInterchainContactsToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
                
        return(result);
    }

    
    /**
     * Retrieves the internal database SSE ID of a SSE from the DB, based on chain and DSSP start residue.
     * @param dssp_start the DSSP start residue number of the SSE
     * @param db_chain_id the DB chain ID of the chain the SSE is part of
     * @return The ID if it was found, -1 otherwise.
     */
    private static Long getDBSseIDByDsspStartResidue(Integer dssp_start, Long db_chain_id) {
        
        if(db_chain_id <= 0L) {
            DP.getInstance().e("DBManager", "getDBSseIDByDsspStartResidue: the provided internal database ID is <= 0 and thus invalid.");
            return -1L;
        }
        
        if(dssp_start < 0) {
            DP.getInstance().w("DBManager", "getDBSseIDByDsspStartResidue: I am being asked for a DSSP start id of " + dssp_start + ".");
        }
        
        Long id = -1L;
        ArrayList<ArrayList<String>> rowarray = doSelectQuery("SELECT s.sse_id FROM " + tbl_sse + " s JOIN " + tbl_chain + " c ON ( s.chain_id = c.chain_id ) WHERE ( s.dssp_start = " + dssp_start + " AND c.chain_id = '" + db_chain_id + "' );");

        if (rowarray == null) {
            return (-1L);
        } else {
            if(rowarray.isEmpty()) {
                // no such SSE entry in database
                return -1L;
            }
            try {
                id = Long.valueOf(rowarray.get(0).get(0));
                return (id);
            } catch (NumberFormatException e) {
                return (-1L);
            } catch (java.lang.ArrayIndexOutOfBoundsException aob) {
                // no such entry in DB
                return (-1L);
            }
        }
    }
    
    /**
     * Retrieves the internal database SSE ID of a SSE from the DB, based on the sequential position in the chain (N to C terminus).
     * @param seq_pos the DSSP start residue number of the SSE
     * @param db_chain_id the DB chain ID of the chain the SSE is part of
     * @return The ID if it was found, -1 otherwise.
     */
    private static Long getDBSseIDBySequentialNumberInChain(Integer seq_pos, Long db_chain_id) {
        Long id = -1L;
        ArrayList<ArrayList<String>> rowarray = doSelectQuery("SELECT s.sse_id FROM " + tbl_sse + " s JOIN " + tbl_chain + " c ON ( s.chain_id = c.chain_id ) WHERE ( s.position_in_chain = " + seq_pos + " AND c.chain_id = '" + db_chain_id + "' );");

        if (rowarray == null) {
            return (-1L);
        } else {
            try {
                id = Long.valueOf(rowarray.get(0).get(0));
                return (id);
            } catch (NumberFormatException e) {
                return (-1L);
            }
        }
    }
    
    /**
     * Retrieves the list of SSEs of a chain. The SSEs are returned in an ordered list (N to C terminus), represented by their internal DB ids.
     * @param db_chain_id the internal DB chain ID
     * @return the list of internal SSE database IDs of the chain. The SSEs are returned in an ordered list (N to C terminus), represented by their internal DB ids.
     */
    private static List<Long> getOrderedSSEDBIDsOfChain(Long db_chain_id) {
        List<Long> ids = new ArrayList<Long>();
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT s.sse_id, s.position_in_chain FROM " + tbl_sse + " s JOIN " + tbl_chain + " c ON ( s.chain_id = c.chain_id ) WHERE ( c.chain_id = ? ) ORDER BY s.position_in_chain ASC;";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, db_chain_id);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "ERROR: SQL: getOrderedSSEDBIDsOfChain: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "getOrderedSSEDBIDsOfChain: Could not close statement and reset autocommit."); }
        }
        
        // convert results to Long and add to list
        for(ArrayList<String> row : tableData) {
            if(row.size() > 0) {
                try {
                    Long id = Long.valueOf(row.get(0));
                    ids.add(id);
                } catch (Exception e) {
                    DP.getInstance().e("DBManager", "getOrderedSSEDBIDsOfChain: Could not cast DB id to Long, skipping.");
                }
            }
        }
        
        return ids;
    }

    
    /**
     * Determines all SSE types which occur in a chain, basedon the sse_string property of the albelig graph in the database.
     * @param pdb_id the PDB id
     * @param chain_name the chain name
     * @return a set of SSE types or null if the albelig graph was not found in the DB
     * @throws SQLException if something went wrong with the DB
     */
    public static Set<String> getSSETypesOfChain(String pdb_id, String chain_name) throws SQLException {
        String sse_string = getSSEStringOfChain(pdb_id, chain_name);
        if(sse_string != null) {
            Set<String> types = new HashSet<String>();
            for(Character c : sse_string.toCharArray()) {
                types.add("" + c);
            }
            return types;
        }
        return null;
    }
    
    /**
     * Returns the SSE string of a chain from the database.
     * @param pdb_id the PDB id
     * @param chain_name the chain name
     * @return the SSE string of the chain, or null if it cannot be found in the DB
     * @throws SQLException if something goes wrong with the DB
     */
    public static String getSSEStringOfChain(String pdb_id, String chain_name) throws SQLException {
       Long db_chain_id = DBManager.getDBChainID(pdb_id, chain_name);
       
       if(db_chain_id < 1L) {
           return null;
       }
       
       ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT g.sse_string FROM " + tbl_proteingraph + " g INNER JOIN " + tbl_chain + " c ON g.chain_id = c.chain_id INNER JOIN " + tbl_protein + " p ON c.pdb_id = p.pdb_id WHERE (p.pdb_id = ? AND c.chain_name = ? AND g.graph_type = 6);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
            statement.setString(2, chain_name);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "getSSEStringOfChain: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "getSSEStringOfChain: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() == 1) {
            if(tableData.get(0).size() == 1) {
                return((tableData.get(0).get(0)));
            }
            return null;
        }
        else {
            return(null);
        }
       
   }
    
    /**
     * Retrieves the internal database chain ID of a chain (it's PK) from the DB. The chain is identified by (pdb_id, chain_name).
     * @param pdb_id the PDB ID of the chain
     * @param chain_name the PDB chain name
     * @return the internal database chain id (its primary key, e.g. '2352365175365'). This is NOT the pdb chain name.
     */
    public static Long getDBChainID(String pdb_id, String chain_name) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT chain_id FROM " + tbl_chain + " WHERE (pdb_id = ? AND chain_name = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
            statement.setString(2, chain_name);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "getDBChainID: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (rs != null) {
                    rs.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "getDBChainID: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(Long.valueOf(tableData.get(0).get(0)));
            }
            else {
                DP.getInstance().w("DB: Chain '" + chain_name + "' of PDB ID '" + pdb_id + "' not in DB.");
                return(-1L);
            }
        }
        else {
            return(-1L);
        }        
    }
    
    
    
    /**
     * Retrieves the PDB ID and the PDB chain name from the DB. The chain is identified by its PK.
     * @param pk the primary key of the chain in the db (e.g., from the graphs table)
     * @return an array of length 2 that contains the PDB ID at position 0 and the chain name at position 1. If no chain with the requested PK exists, the array has a length != 2.
     */
    public static String[] getPDBIDandChain(Integer dbChainID) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
               
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT pdb_id, chain_name FROM " + tbl_chain + " WHERE (chain_id = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, dbChainID);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "getPDBIDandChain: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", " getPDBIDandChain: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() == 1) {
            if(tableData.get(0).size() == 2) {
                return(new String[] { tableData.get(0).get(0), tableData.get(0).get(1)});
            }
            else {
                DP.getInstance().e("DBManager", "getPDBIDandChain: Result row has unexpected length.");
                return(new String[] { "" } );
            }
        }
        else {
            if(tableData.isEmpty()) {
                // no such PK, empty result list
                return(new String[] { "" } );
            } else {
                DP.getInstance().e("DBManager", "getPDBIDandChain(): Result table has unexpected length '" + tableData.size() + "'. Should be either 0 or 1.");
                return(new String[] { "" } );
            }
            
        }        
    }
    
    
    /**
     * Retrieves the PDB ID and the PDB chain name of all chains from the DB.
     * @return a 2-dim ArrayList. The outer one has the size = number of chains in DB, the inner ones of length 2 contain the PDB ID at position 0 and the chain name at position 1.
     */
    public static ArrayList<ArrayList<String>> getAllPDBIDsandChains() {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
               
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT pdb_id, chain_name FROM " + tbl_chain + " ;";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "getAllPDBIDsandChains: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "getAllPDBIDsandChains: Could not close statement and reset autocommit."); }
        }
        
        return tableData;
    }
    
    
    /**
     * Retrieves the PDB ID  of all proteins that are currently in the DB.
     * @return a list of PDB IDs
     */
    public static List<String> getAllPDBIDsInTheDB() {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        List<String> tableData = new ArrayList<>();
        ArrayList<String> rowData = null;
        int count;
               
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT pdb_id FROM " + tbl_protein + " ;";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);
            
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                for (int i = 1; i <= count; i++) {
                    tableData.add(rs.getString(i));
                }
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "getAllPDBIDsInTheDB: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "getAllPDBIDsInTheDB: Could not close statement and reset autocommit."); }
        }
        
        return tableData;
    }
    
    
    /**
     * Determines whether a protein exists in the database. Note race condition problems with this
     * approach if run on several machines which write to the same DB.
     * @param pdb_id the PDB ID of the protein
     * @return true if it exists, false otherwise
     */
    public static synchronized Boolean proteinExistsInDB(String pdb_id) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT pdb_id FROM " + tbl_protein + " WHERE (pdb_id = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: proteinExistsInDB:'" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DB: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(true);
            }
            else {
                DP.getInstance().w("DB: Protein with PDB ID '" + pdb_id + "' not in DB.");
                return(false);
            }
        }
        else {
            return(false);
        }        
    }
    
    
    /**
     * Determines whether graphlet counts for a certain graph exists in the database. Note race condition problems with this
     * approach if run on several machines which write to the same DB.
     * @param graph_db_id the graph database id
     * @return true if it exists, false otherwise
     */
    public static synchronized Boolean graphletsExistsInDBForGraph(Long graph_db_id) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT graphlet_id FROM " + DBManager.tbl_graphletcount + " WHERE (graph_id = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, graph_db_id);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "graphletsExistsInDBForGraph:'" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "graphletsExistsInDBForGraph: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            return true;
        }
        
        
        return(false);
                
    }
    
    
    /**
     * Determines whether a certain ligand2chain assignment already exists in the DB.
     * @param chainDbId the internal chain database ID
     * @param ligName3 the ligand name, 3 letter code. Should be trimmed.
     * @return true if the assignment exists, false otherwise
     */
    public static synchronized Boolean assignmentLigandToProteinChainExistsInDB(Long chainDbId, String ligName3) {
        if(ligName3 == null) {
            DP.getInstance().w("DBManger", "assignmentLigandToProteinChainExistsInDB: Invalid ligand code, must not be null.");
            return false;
        }
        else {
            if(ligName3.length() < 1 || ligName3.length() > 3) {
                DP.getInstance().w("DBManger", "assignmentLigandToProteinChainExistsInDB: Invalid ligand code, must consist of 1 - 3 characters.");
                return false;
            }
        }
        
        if(ligName3.contains(" ")) {
            DP.getInstance().w("DBManager", "assignmentLigandToProteinChainExistsInD: The ligand_name3 '" + ligName3 + "' contains spaces! Trim it before giving it to me.");
        }
        
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT ligandtochain_id FROM " + tbl_nm_ligandtochain + " WHERE (ligandtochain_chainid = ? AND ligandtochain_ligandname3 = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chainDbId);
            statement.setString(2, ligName3);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "assignmentLigandToProteinChainExistsInDB: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "assignmentLigandToProteinChainExistsInDB: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(true);
            }
            else {
                return(false);
            }
        }
        else {
            return(false);
        }
        
    }
    
    /**
     * Determines whether a ligand, identified by its 3-char abbreviation like "ICT", exists in the database. Note race condition problems with this
     * approach if run in parallel on several machines which write to the same DB.
     * @param ligand_name3 the PDB ligand abbreviation, 3 chars, e.g., "ICT" for isocitric acid. See http://ligand-expo.rcsb.org/  for details.
     * @return true if it exists, false otherwise
     */
    public static synchronized Boolean ligandExistsInDB(String ligand_name3) {
        
        
        if(ligand_name3 == null) {
            DP.getInstance().w("DBManger", "ligandExistsInDB: Invalid ligand code, must not be null.");
            return false;
        }
        else {
            if(ligand_name3.length() < 1  || ligand_name3.length() > 3) {
                DP.getInstance().w("DBManger", "ligandExistsInDB: Invalid ligand code, must consist of 1 to 3 characters.");
                return false;
            }
        }
        
        if(ligand_name3.contains(" ")) {
            DP.getInstance().w("DBManager", "ligandExistsInDB: The ligand_name3 '" + ligand_name3 + "' contains spaces. Trim it before giving it to me.");
        }
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT ligand_name3 FROM " + tbl_ligand + " WHERE (ligand_name3 = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, ligand_name3);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "SQL: ligandExistsInDB:'" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "DB: ligandExistsInDB: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(true);
            }
            else {
                return(false);
            }
        }
        else {
            return(false);
        }        
    }
    
    
    /**
     * Determines whether a graphlet similarity score entry exists in the DB for the given pair of protein graphs.
     * @param source_graph_id the source graph id
     * @param target_graph_id the target graph id
     * @return true if it exists, false otherwise
     */
    public static synchronized Boolean graphletSimilarityScoreExistsInDBForGraphs(Long source_graph_id, Long target_graph_id) {
        
        if(source_graph_id < 0 || target_graph_id < 0) {
            return false;
        }
        
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT graphletsimilarity_id FROM " + tbl_graphletsimilarity + " WHERE ( graphletsimilarity_sourcegraph = ? AND graphletsimilarity_targetgraph = ? );";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, source_graph_id);
            statement.setLong(2, target_graph_id);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "graphletSimilarityScoreExistsInDBForGraphs:'" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                //dbc.setAutoCommit(true);
            } catch(SQLException e) { DP.getInstance().w("DBManager", "graphletSimilarityScoreExistsInDBForGraphs: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {            
            return(true);     
        }
        else {
            return(false);
        }        
    }
    
    
    
    
    
    /**
     * Retrieves the GML format graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdb_id the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphStringGML(String pdb_id, String chain_name, String graph_type) throws SQLException {
        return DBManager.getGraphString(ProtGraphs.GRAPHFORMAT_GML, pdb_id, chain_name, graph_type);
    }
    
    /**
     * Retrieves the Kavosh format graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdb_id the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphStringKavosh(String pdb_id, String chain_name, String graph_type) throws SQLException {
        return DBManager.getGraphString(ProtGraphs.GRAPHFORMAT_KAVOSH, pdb_id, chain_name, graph_type);
    }
    
    /**
     * Retrieves the PLCC format graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdb_id the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphStringPLCC(String pdb_id, String chain_name, String graph_type) throws SQLException {
        return DBManager.getGraphString(ProtGraphs.GRAPHFORMAT_PLCC, pdb_id, chain_name, graph_type);
    }
    
    /**
     * Retrieves the DOT language format graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdb_id the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphStringDOTLanguage(String pdb_id, String chain_name, String graph_type) throws SQLException {
        return DBManager.getGraphString(ProtGraphs.GRAPHFORMAT_DOTLANGUAGE, pdb_id, chain_name, graph_type);
    }
    
    /**
     * Retrieves a graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type). You need to supply the format you want. Some formats may be null or the empty string.
     * @param graph_format the requested graph format, e.g. "GML". Use the constants in ProtGraphs class, like ProtGraphs.GRAPHFORMAT_GML.
     * @param pdb_id the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe". Use the constants in the ProtGraphs class.
     * @return the GML format string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphString(String graph_format, String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("DBManager", "getGraphString(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;
        
        String query_graph_format_field = "graph_string_gml";
        if(graph_format.equals(ProtGraphs.GRAPHFORMAT_GML)) {
            query_graph_format_field = "graph_string_gml";
        }
        else if(graph_format.equals(ProtGraphs.GRAPHFORMAT_KAVOSH)) {
            query_graph_format_field = "graph_string_kavosh";
        }
        else if(graph_format.equals(ProtGraphs.GRAPHFORMAT_DOTLANGUAGE)) {
            query_graph_format_field = "graph_string_dotlanguage";
        }
        else if(graph_format.equals(ProtGraphs.GRAPHFORMAT_PLCC)) {
            query_graph_format_field = "graph_string_plcc";
        }
        

        String query = "SELECT " + query_graph_format_field + " FROM " + tbl_proteingraph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "ERROR: SQL: getGraph: Retrieval of graph string failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(tableData.get(0).get(0));
            }
            else {
                DP.getInstance().w("DBManager", "DB: No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
    
    /**
     * Determines and returns the internal database ID (primary key) of the protein graph identified by the given properties (PDB ID, chain, gt).
     * 
     * @param pdb_id the PDB identifier of the graph
     * @param chain_name the PDB chain name of the graph
     * @param graph_type the graph type, use the string constants in ProtGraphs class
     * @return the database ID of the graph or a negative number if an error occurred or no such graph exists in the db
     * @throws SQLException 
     */
    public static Long getDBProteinGraphID(String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("getGraph(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(-1L);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;
        
       

        String query = "SELECT graph_id FROM " + tbl_proteingraph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getGraphDatabaseID(): Retrieval of graph failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                String graph_id_str = tableData.get(0).get(0);
                try {
                    Long graph_id_int = Long.parseLong(graph_id_str);
                    return graph_id_int;
                } catch(java.lang.NumberFormatException e) {
                    DP.getInstance().e("DB: getGraphDatabaseID(): Could not parse graph database ID as integer, seems invalid.");
                    return -1L;
                }
            }
            else {
                DP.getInstance().w("DB: No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(-1L);
            }
        }
        else {
            return(-1L);
        }        
    }
    
    
    /**
     * Determines and returns the internal database ID (primary key) of the folding graph identified by the given properties (PDB ID, chain, gt, fg_number).
     * 
     * @param pdb_id the PDB identifier of the graph
     * @param chain_name the PDB chain name of the graph
     * @param graph_type the graph type, use the string constants in ProtGraphs class
     * @param fg_number the folding graph number
     * @return the database ID of the graph or a negative number if an error occurred or no such graph exists in the db
     * @throws SQLException 
     */
    public static Long getDBFoldingGraphID(String pdb_id, String chain_name, String graph_type, Integer fg_number) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;
        
       Long parentGraphID = DBManager.getDBProteinGraphID(pdb_id, chain_name, graph_type);
       if (parentGraphID < 1) {
            DP.getInstance().w("getDBFoldingGraphID(): Could not find parent " + graph_type + " graph with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(-1L);
        }

        String query = "SELECT foldinggraph_id FROM " + tbl_foldinggraph + " WHERE (parent_graph_id = ? AND fg_number = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, parentGraphID);
            statement.setInt(2, fg_number);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getDBFoldingGraphID(): Retrieval of graph failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                String graph_id_str = tableData.get(0).get(0);
                try {
                    Long graph_id_int = Long.parseLong(graph_id_str);
                    return graph_id_int;
                } catch(java.lang.NumberFormatException e) {
                    DP.getInstance().e("DB: getDBFoldingGraphID(): Could not parse graph database ID as integer, seems invalid.");
                    return -1L;
                }
            }
            else {
                DP.getInstance().w("DB: No entry for folding graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(-1L);
            }
        }
        else {
            return(-1L);
        }        
    }
    
    
    /**
     * Retrieves the graphlet counts for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdb_id the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return an Integer array of the graphlet counts
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Integer[] getGraphletCounts(String pdb_id, String chain_name, String graph_type) throws SQLException {
        
        int numReqGraphletTypes = Settings.getInteger("plcc_I_number_of_graphlets");
        
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("DBManager", "getGraphletCounts(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;
        Long graphid = DBManager.getDBProteinGraphID(pdb_id, chain_name, graph_type);
        
        if(graphid <= 0) {
            return null;
        }

        String query = "SELECT graphlet_counts[0], graphlet_counts[1], graphlet_counts[2], graphlet_counts[3], graphlet_counts[4],"
                + " graphlet_counts[5], graphlet_counts[6], graphlet_counts[7], graphlet_counts[8], graphlet_counts[9],"
                + " graphlet_counts[10], graphlet_counts[11], graphlet_counts[12], graphlet_counts[13], graphlet_counts[14],"
                + " graphlet_counts[15], graphlet_counts[16], graphlet_counts[17], graphlet_counts[18], graphlet_counts[19],"
                + " graphlet_counts[20], graphlet_counts[21], graphlet_counts[22], graphlet_counts[23], graphlet_counts[24],"
                + " graphlet_counts[25], graphlet_counts[26], graphlet_counts[27], graphlet_counts[28], graphlet_counts[29]"
                + " FROM " + tbl_graphletcount + " WHERE (graph_id = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, graphid);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "getGraphletCounts: Retrieval of graphlets failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        ArrayList<String> rowGraphlets;
        Integer[] result = new Integer[numReqGraphletTypes];        
        if(tableData.size() >= 1) {
            rowGraphlets = tableData.get(0);
            if(rowGraphlets.size() > 0) {
                if(rowGraphlets.size() == numReqGraphletTypes) {
                    for(int i = 0; i < rowGraphlets.size(); i++) {
                        try {
                            result[i] = Integer.valueOf(rowGraphlets.get(i));
                        } catch(NumberFormatException ce) {
                            DP.getInstance().w("DBManager", "getGraphletCounts: Cast error. Could not cast entry for graphlets of graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "' to Integer.");
                            return null;
                        }
                    }
                    return(result);
                } else {
                    DP.getInstance().w("DBManager", "getGraphletCounts: Entry for graphlets of graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "' has wrong size.");
                    return(null);
                }                                
            }
            else {
                DP.getInstance().w("DBManager", "getGraphletCounts: No entry for graphlets of graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
    
    /**
     * Retrieves the graphlet counts for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdb_id the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return a Double array of the graphlet counts
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Double[] getNormalizedGraphletCounts(String pdb_id, String chain_name, String graph_type) throws SQLException {
        
        int numReqGraphletTypes = 30;
        
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<Double>> tableData = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().e("DBManager", "getNormalizedGraphletCounts(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;
        Long graphid = DBManager.getDBProteinGraphID(pdb_id, chain_name, graph_type);
        
        if(graphid <= 0) {
            DP.getInstance().e("DBManager", "getNormalizedGraphletCounts(): Could not find " + graph_type + " protein graph with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return null;
        }

        String query = "SELECT graphlet_counts[0], graphlet_counts[1], graphlet_counts[2], graphlet_counts[3], graphlet_counts[4],"
                + " graphlet_counts[5], graphlet_counts[6], graphlet_counts[7], graphlet_counts[8], graphlet_counts[9],"
                + " graphlet_counts[10], graphlet_counts[11], graphlet_counts[12], graphlet_counts[13], graphlet_counts[14],"
                + " graphlet_counts[15], graphlet_counts[16], graphlet_counts[17], graphlet_counts[18], graphlet_counts[19],"
                + " graphlet_counts[20], graphlet_counts[21], graphlet_counts[22], graphlet_counts[23], graphlet_counts[24],"
                + " graphlet_counts[25], graphlet_counts[26], graphlet_counts[27], graphlet_counts[28], graphlet_counts[29]"
                + " FROM " + tbl_graphletcount + " WHERE (graph_id = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, graphid);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<Double>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getDouble(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            DP.getInstance().e("DBManager", "getNormalizedGraphletCounts: Retrieval of graphlets failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        ArrayList<Double> rowGraphlets;
        Double[] result = new Double[numReqGraphletTypes];        
        if(tableData.size() >= 1) {
            rowGraphlets = tableData.get(0);
            if(rowGraphlets.size() > 0) {
                if(rowGraphlets.size() == numReqGraphletTypes) {
                    NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
                    for(int i = 0; i < rowGraphlets.size(); i++) {
                        try {
                            // old, without locale: result[i] = Double.valueOf(rowGraphlets.get(i));
                            result[i] = rowGraphlets.get(i);
                        } catch(NumberFormatException ce) {
                            DP.getInstance().e("DBManager", "getNormalizedGraphletCounts: Cast error. Could not cast entry for graphlets of graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "' to Double.");
                            return null;
                        } 
                    }
                    return(result);
                } else {
                    DP.getInstance().e("DBManager", "getNormalizedGraphletCounts: Entry for graphlets of graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "' has wrong size (found " + rowGraphlets.size() + ", expected " + numReqGraphletTypes + ").");
                    return(null);
                }                                
            }
            else {
                DP.getInstance().e("DBManager", "getNormalizedGraphletCounts: No entry for graphlets of graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
    
    /**
     * Returns the requested ProtGraph object or NULL if no such graph exists in the DB (or DB errors occurred).
     * Note that the ProtGraph is created from the PLCC format graph string.
     * @param pdb_id the PDB identifier, e.g., "7TIM"
     * @param chain_name the PDB chain name, e.g., "A"
     * @param graph_type the graph type, e.g., "albe". Use the constants in ProtGraphs class.
     * @return the ProtGraph instance created from the string representation in the database
     */
    public static ProtGraph getGraph(String pdb_id, String chain_name, String graph_type) {

        String graphString = null;
        
        try { 
            graphString = DBManager.getGraphStringPLCC(pdb_id, chain_name, graph_type); 
        } catch (SQLException e) { 
            System.err.println("ERROR: SQL: Could not get graph from DB: '" + e.getMessage() + "'."); 
            return(null);            
        }
        
        if(graphString == null) {
            DP.getInstance().w("DB: getGraph: Graph '" + pdb_id + "-" + chain_name + "-" + graph_type + "' not found in database.");
            return(null);
        }
        
        return(ProtGraphs.fromPlccGraphFormatString(graphString));        
    }
    
    
    /**
     * Retrieves the SSEstring for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdb_id the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return the SSEstring of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getSSEString(String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("getSSEString(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT sse_string FROM " + tbl_proteingraph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getSSEString(): Retrieval of graph string failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(tableData.get(0).get(0));
            }
            else {
                DP.getInstance().w("DB: getSSEString(): No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
    
    
    /**
     * Retrieves the graph data of all protein graphs from the database.
     * @param graph_type the graph type, use one of the constants SSEGraph.GRAPHTYPE_* (e.g., SSEGraph.GRAPHTYPE_ALBE) or the word 'ALL' for all types.
     * @return an ArrayList of String arrays. Each of the String arrays in the list has 3 fields. The 
     * first fields (array[0]) contains the PDB ID of the chain, the second one (array[1]) contains the chain ID, the 
     * third field contains the graph type and the fourth field contains the SSE string.
     *  position 0 := pdb id
     *  position 1 := chain id
     *  position 2 := graph type
     *  position 3 := SSE string
     *  position 4 := graph string
     * @throws java.sql.SQLException if something goes wrong with the DB
     */
    public static ArrayList<String[]> getAllGraphData(String graph_type) throws SQLException {
        
        ArrayList<String[]> graphData = new ArrayList<String[]>();
        
        // get a list of values pairs from the db here    
                
        Integer gtc = 1;        // graphTypeCode, e.g., 1 for alpha
        Boolean allGraphs = false;
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        if(graph_type.equals("ALL")) {
            allGraphs = true;
        }
        else {
            gtc = ProtGraphs.getGraphTypeCode(graph_type);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT sse_string, chain_id, graph_type, graph_string FROM " + tbl_proteingraph + " WHERE (graph_type = ?);";
        
        if(allGraphs) {
            query = "SELECT sse_string, chain_id, graph_type, graph_string FROM " + tbl_proteingraph + ";";
        }

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            if( ! allGraphs) {
                statement.setInt(1, gtc);
            }
            
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getAllGraphData: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        String graphSSEStringDB, graphTypeDB, pdbidDB, chainNameDB, graphStringDB;
        Integer chainPK;
        String[] data;
        
        
        for(Integer i = 0; i < tableData.size(); i++) {            
            
            if(tableData.get(i).size() == 4) {
                graphSSEStringDB = tableData.get(i).get(0);
                graphTypeDB = tableData.get(i).get(2);
                graphStringDB = tableData.get(i).get(3);
                
                try {
                    chainPK = Integer.valueOf(tableData.get(i).get(1));
                } catch(NumberFormatException e) {
                    DP.getInstance().w("DB: getAllGraphData(): '" + e.getMessage() + "' Ignoring data row.");
                    continue;
                }

                // OK, now get the PDB ID and chain name
                data = getPDBIDandChain(chainPK);
                
                if(data.length != 2) {
                    DP.getInstance().w("DB: getAllGraphData(): Could not find chain with PK '" + chainPK + "' in DB, ignoring data row.");
                    continue;
                }
                else {
                    pdbidDB = data[0];
                    chainNameDB = data[1];
                    graphData.add(new String[]{pdbidDB, chainNameDB, graphTypeDB, graphSSEStringDB, graphStringDB});
                }                
            }
            else {
                DP.getInstance().w("DB: getAllGraphData(): Result row #" + i + " has unexpected length " + tableData.get(i).size() + ".");
                return(graphData);
            }
        }
        
        return(graphData);                        
    }
    
    
    /**
     * Retrieves the relative path of the graph image for the requested graph in SVG format from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdb_id the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return the relative path to the graph image in SVG format, or null if no such graph image exists (path relative to the base directory, see Settings class).
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphImagePathSVG(String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Long chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            DP.getInstance().w("getGraph(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT graph_image_svg FROM " + tbl_proteingraph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            //dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setLong(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            //dbc.commit();
            
            md = rs.getMetaData();
            count = md.getColumnCount();

            columnHeaders = new ArrayList<String>();

            for (int i = 1; i <= count; i++) {
                columnHeaders.add(md.getColumnName(i));
            }


            while (rs.next()) {
                rowData = new ArrayList<String>();
                for (int i = 1; i <= count; i++) {
                    rowData.add(rs.getString(i));
                }
                tableData.add(rowData);
            }
            
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: getGraph: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            //dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(tableData.get(0).get(0));
            }
            else {
                DP.getInstance().w("DB: No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }

    /**
     * Queries the PG information schema and retrieves the names of all (non-system) tables
     * in the vplg database.
     * @return a list of table names currently in the DB (without system tables)
     */
    public static ArrayList<String> getPlccTablesCurrentlyInDatabase() {
        ArrayList<String> t = new ArrayList<String>();
        
        ArrayList<ArrayList<String>> tableData = DBManager.doSelectQuery("SELECT table_name, table_schema FROM information_schema.tables WHERE table_schema NOT IN ('pg_catalog', 'information_schema');");
        
        for(ArrayList<String> row : tableData) {
            if(row.size() >= 1) {
                t.add(row.get(0));
            }
        }
        
        return t;
    }
    
    
    /**
     * Returns the name of the database currently in use.
     * @return the name of the database currently in use
     */
    public static String getConnectionInfoDatabaseName() {
        return dbName;
    }
    
    
}
