/*
 * This file is part of the Visualization of Protein Ligand Graphs (VPLG) software package.
 *
 * Copyright Tim Schäfer 2012. VPLG is free software, see the LICENSE and README files for details.
 *
 * @author ts
 */
package plcc;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    static String tbl_graph = "plcc_graph";
    static String tbl_protein = "plcc_protein";
    static String tbl_chain = "plcc_chain";
    static String tbl_sse = "plcc_sse";
    
    static String tbl_ssetypes = "plcc_ssetypes";
    static String tbl_contacttypes = "plcc_contacttypes";
    static String tbl_graphtypes = "plcc_graphtypes";
    
    static String tbl_contact = "plcc_contact";
    static String view_ssecontacts = "plcc_ssetype_contacts";
    static String view_graphs = "plcc_graphs";

    /**
     * Sets the database address and the credentials to access the DB.
     * @return True if the connection could be established, false otherwise.
     */
    public static Boolean init(String db, String host, Integer port, String user, String password) {

        dbName = db;
        dbHost = host;
        dbPort = port;


        dbURL = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        dbUsername = user;
        dbPassword = password;

        dbDriver = "org.postgresql.Driver";

        try {
            Class.forName(dbDriver);
        } catch (Exception e) {
            System.err.println("ERROR: Could not load JDBC driver '" + dbDriver + "'. Is the correct db driver installed at lib/postgresql-jdbc.jar?");
            System.err.println("ERROR: See the README for more info on getting the proper driver for your PostgreSQL server and Java versions.'");
            System.err.println("ERROR: Message was: '" + e.getMessage() + "'.");
            System.exit(1);
            
        }

        Boolean conOK = connect();
        return (conOK);

    }

    /**
     * Connects to the database using the DB address and credentials defined during the call to init().
     * @return Whether a connection to the DB could be established.
     */
    private static Boolean connect() {

        Boolean conOK = false;

        try {
            dbc = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            dbmd = dbc.getMetaData();
            sql = dbc.createStatement();
            conOK = true;
        } catch (Exception e) {
            //System.err.println("ERROR: Could not connect to database at '" + dbURL + "'.");
            System.err.println("ERROR: Could not connect to database at '" + dbURL + "' with user '" + dbUsername + "'.");
            System.err.println("ERROR: The error message was: '" + e.getMessage() + "'.");
            System.exit(1);
        }

        try {
            System.out.println("Connection to " + dbmd.getDatabaseProductName() + " " + dbmd.getDatabaseProductVersion() + " successful.");
        } catch (Exception e) {
            // Something didn't work out if this failed.
            conOK = false;
        }

        return (conOK);
    }

    /**
     * Checks whether a DB connection exists. Tries to establish it if not.
     * @return: Whether a DB connection could be established in the end.
     */
    private static Boolean ensureConnection() {

        try {
            dbc.getMetaData();
        } catch (Exception e) {
            return (connect());
        }

        return (true);
    }
        

    /**
     * Determines whether the underlying DBMS supports transactions.
     * @return true if it does, false otherwise
     */
    boolean supportsTransactions() throws SQLException {

        ensureConnection();

        return (dbc.getMetaData().supportsTransactions());
    }

    /**
     * Executes the SQL insert query 'query' and returns the number of inserted rows if it succeeds, -1 otherwise.
     * WARNING: This does not do any checks on the input so do not expose this to user input.
     * @return The number of inserted rows if it succeeds, -1 otherwise.
     */
    public static int doInsertQuery(String query) {

        ensureConnection();

        PreparedStatement ps = null;
        try {
            ps = dbc.prepareStatement(query);
            return (ps.executeUpdate());          // num rows affected
        } catch (Exception e) {
            System.err.println("WARNING: doInsertQuery(): SQL statement '" + query + "' failed.");
            System.err.println("WARNING: The error message was: '" + e.getMessage() + "'.");
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doInsertQuery(): Could not close prepared statement.");
                    System.err.println("WARNING: The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }
    }

    /**
     * Executes the SQL update query 'query' and returns the number of updated rows if it succeeds, -1 otherwise.
     * WARNING: This does not do any checks on the input so do not expose this to user input.
     * @return The number of updated rows if it succeeds, -1 otherwise.
     */
    public static int doUpdateQuery(String query) {

        ensureConnection();

        PreparedStatement ps = null;
        try {
            ps = dbc.prepareStatement(query);
            return (ps.executeUpdate());          // num rows affected
        } catch (Exception e) {
            System.err.println("WARNING: doUpdateQuery(): SQL statement '" + query + "' failed.");
            System.err.println("WARNING: The error message was: '" + e.getMessage() + "'.");
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doUpdateQuery(): Could not close prepared statement.");
                    System.err.println("WARNING: The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }
    }

    /**
     * Executes the SQL delete query 'query' and returns the number of updated rows if it succeeds, -1 otherwise.
     * WARNING: This does not do any checks on the input so do not expose this to user input.
     * @return The number of deleted rows if it succeeds, -1 otherwise.
     */
    public static int doDeleteQuery(String query) {

        ensureConnection();

        PreparedStatement ps = null;
        try {
            ps = dbc.prepareStatement(query);
            return ps.executeUpdate();           // num rows affected
        } catch (Exception e) {
            System.err.println("WARNING: doDeleteQuery(): SQL statement '" + query + "' failed.");
            System.err.println("WARNING: The error message was: '" + e.getMessage() + "'.");
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doDeleteQuery(): Could not close prepared statement.");
                    System.err.println("WARNING: The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }
    }

    /**
     * Executes a select query. WARNING: This does not do any checks on the input so do not expose this to user input.
     * @param query the SQL query
     * @return the data as 2D matrix of Strings.
     */
    public static ArrayList<ArrayList<String>> doSelectQuery(String query) {

        ensureConnection();

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
        } catch (Exception e) {
            System.err.println("WARNING: doDeleteQuery(): SQL statement '" + query + "' failed.");
            System.err.println("WARNING: The error message was: '" + e.getMessage() + "'.");
            //e.printStackTrace();
            System.exit(1);
            return (null);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doSelectQuery(): Could not close result set.");
                    System.err.println("WARNING: The error message was: '" + ex.getMessage() + "'.");
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doSelectQuery(): Could not close prepared statement.");
                    System.err.println("WARNING: The error message was: '" + ex.getMessage() + "'.");
                }
            }
        }

    }

    /**
     * Closes the DB connection.
     * @return Whether the connection could be closed.
     */
    public static Boolean closeConnection() {

        if (dbc != null) {
            try {
                if (!dbc.isClosed()) {
                    if (!dbc.getAutoCommit()) {
                        dbc.commit();
                    }
                    dbc.close();
                    return (true);
                } else {
                    return (true);        // already closed
                }
            } catch (Exception e) {
                System.err.println("WARNING: closeConnection(): Could not close DB connection.");
                System.err.println("WARNING: The error message was: '" + e.getMessage() + "'.");
                return (false);
            }
        } else {
            // there is no connection object
            return (true);
        }
    }

    /**
     * Drops (=deletes) all statistics tables in the database.
     * @return whether it worked out
     */
    public static Boolean dropTables() {
        ensureConnection();
        Boolean res = false;

        try {
            doDeleteQuery("DROP TABLE " + tbl_protein + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_chain + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_sse + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_contact + " CASCADE;");
            doDeleteQuery("DROP TABLE " + tbl_graph + " CASCADE;");
            
            doDeleteQuery("DROP TABLE " + tbl_graphtypes + ";");
            doDeleteQuery("DROP TABLE " + tbl_contacttypes + ";");
            doDeleteQuery("DROP TABLE " + tbl_ssetypes + ";");

            // The indices get dropped with the tables.
            //doDeleteQuery("DROP INDEX plcc_idx_chain_insert;");
            //doDeleteQuery("DROP INDEX plcc_idx_sse_insert;");

            res = true;      // Not really, need to check all of them

        } catch (Exception e) {
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
     * @return Whether they could be created.
     */
    public static Boolean createTables() {


        ensureConnection();
        Boolean res = false;

        try {
            // create tables
            doInsertQuery("CREATE TABLE " + tbl_protein + " (pdb_id varchar(4) primary key, header varchar(200) not null, title varchar(400) not null, experiment varchar(200) not null, keywords varchar(400) not null, resolution real not null);");
            doInsertQuery("CREATE TABLE " + tbl_chain + " (chain_id serial primary key, chain_name varchar(2) not null, mol_name varchar(200) not null, organism_scientific varchar(200) not null, organism_common varchar(200) not null, pdb_id varchar(4) not null references plcc_protein ON DELETE CASCADE);");
            doInsertQuery("CREATE TABLE " + tbl_sse + " (sse_id serial primary key, chain_id int not null references plcc_chain ON DELETE CASCADE, dssp_start int not null, dssp_end int not null, pdb_start varchar(20) not null, pdb_end varchar(20) not null, sequence varchar(2000) not null, sse_type int not null, lig_name varchar(5));");
            doInsertQuery("CREATE TABLE " + tbl_contact + " (contact_id serial primary key, sse1 int not null references plcc_sse ON DELETE CASCADE, sse2 int not null references plcc_sse ON DELETE CASCADE, contact_type int not null, check (sse1 < sse2));");
            doInsertQuery("CREATE TABLE " + tbl_graph + " (graph_id serial primary key, chain_id int not null references plcc_chain ON DELETE CASCADE, graph_type int not null, graph_string text not null, graph_image_svg text, sse_string text);");
            
            // various types encoded by integers. these tables should be removed in the future and the values stored as string directly instead.
            doInsertQuery("CREATE TABLE " + tbl_ssetypes + " (ssetype_id int not null primary key,  ssetype_text text not null);");
            doInsertQuery("CREATE TABLE " + tbl_contacttypes + " (contacttype_id int not null primary key,  contacttype_text text not null);");
            doInsertQuery("CREATE TABLE " + tbl_graphtypes + " (graphtype_id int not null primary key,  graphtype_text text not null);");

            // set constraints
            doInsertQuery("ALTER TABLE " + tbl_protein + " ADD CONSTRAINT constr_protein_uniq UNIQUE (pdb_id);");
            doInsertQuery("ALTER TABLE " + tbl_chain + " ADD CONSTRAINT constr_chain_uniq UNIQUE (chain_name, pdb_id);");
            doInsertQuery("ALTER TABLE " + tbl_sse + " ADD CONSTRAINT constr_sse_uniq UNIQUE (chain_id, dssp_start, dssp_end);");
            doInsertQuery("ALTER TABLE " + tbl_contact + " ADD CONSTRAINT constr_contact_uniq UNIQUE (sse1, sse2);");
            doInsertQuery("ALTER TABLE " + tbl_graph + " ADD CONSTRAINT constr_graph_uniq UNIQUE (chain_id, graph_type);");
            
            // create views
            doInsertQuery("CREATE VIEW " + view_ssecontacts + " AS SELECT contact_id, least(sse1_type, sse2_type) sse1_type, greatest(sse1_type, sse2_type) sse2_type, sse1_lig_name, sse2_lig_name  FROM (SELECT k.contact_id, sse1.sse_type AS sse1_type, sse2.sse_type AS sse2_type, sse1.lig_name AS sse1_lig_name, sse2.lig_name AS sse2_lig_name FROM " + tbl_contact + " k LEFT JOIN " + tbl_sse + " sse1 ON k.sse1=sse1.sse_id LEFT JOIN " + tbl_sse + " sse2 ON k.sse2=sse2.sse_id) foo;");
            doInsertQuery("CREATE VIEW " + view_graphs + " AS SELECT graph_id, pdb_id, chain_name, graph_type, graph_string FROM (SELECT k.graph_id, k.graph_type, k.graph_string, chain.chain_name AS chain_name, chain.pdb_id AS pdb_id FROM " + tbl_graph + " k LEFT JOIN " + tbl_chain + " chain ON k.chain_id=chain.chain_id) bar;");

            // add comments for tables
            doInsertQuery("COMMENT ON TABLE " + tbl_protein + " IS 'Stores information on a whole PDB file.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_chain + " IS 'Stores information on a protein chain.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_sse + " IS 'Stores information on a secondary structure element (SSE).';");
            doInsertQuery("COMMENT ON TABLE " + tbl_contact + " IS 'Stores information on a contact between a pair of SSEs.';");
            doInsertQuery("COMMENT ON TABLE " + tbl_graph + " IS 'Stores a description of the protein graph of a protein chain in VPLG text format. This is enough to draw the graph.';");

            // add comments for specific fields
            doInsertQuery("COMMENT ON COLUMN " + tbl_sse + ".sse_type IS '1=helix, 2=beta strand, 3=ligand, 4=other';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_contact + ".contact_type IS '1=mixed, 2=parallel, 3=antiparallel, 4=ligand';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_sse + ".lig_name IS 'The 3-letter ligand name from the PDB file and the RCSB ligand expo website. If this SSE is not a ligand SSE, this is the empty string.';");
            doInsertQuery("COMMENT ON COLUMN " + tbl_graph + ".graph_type IS '1=alpha, 2=beta, 3=albe, 4=alphalig, 5=betalig, 6=albelig';");

            // add indices
            doInsertQuery("CREATE INDEX plcc_idx_chain_insert ON " + tbl_chain + " (pdb_id, chain_name);");         // for SELECTs during data insert
            doInsertQuery("CREATE INDEX plcc_idx_sse_insert ON " + tbl_sse + " (dssp_start, chain_id);");           // for SELECTs during data insert

            doInsertQuery("CREATE INDEX plcc_idx_chain_fk ON " + tbl_chain + " (pdb_id);");                          // for JOINs, ON CASCADE, etc. (foreign key, FK)
            doInsertQuery("CREATE INDEX plcc_idx_sse_fk ON " + tbl_sse + " (chain_id);");                            // FK
            doInsertQuery("CREATE INDEX plcc_idx_contact_fk1 ON " + tbl_contact + " (sse1);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_contact_fk2 ON " + tbl_contact + " (sse2);");                       // FK
            doInsertQuery("CREATE INDEX plcc_idx_graph_fk ON " + tbl_graph + " (chain_id);");                       // FK

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


            res = true;      // Not really, need to check all of them.

        } catch (Exception e) {            
            res = false;
        }

        return (res);
    }

    
    
    /**
     * Writes information on a SSE to the database. Note that the protein + chain have to exist in the database already.
     */
    public static Boolean writeSSEToDB(String pdb_id, String chain_name, Integer dssp_start, Integer dssp_end, String pdb_start, String pdb_end, String sequence, Integer sse_type, String lig_name) throws SQLException {

        Integer chain_id = getDBChainID(pdb_id, chain_name);

        if (chain_id < 0) {
            System.err.println("ERROR: writeSSEToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }
      
        Boolean result = false;

        PreparedStatement statement = null;
 
        /*
        if (lig_name.length() >= 1) {
            query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type, lig_name) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ", '" + lig_name + "');";
        } else {
            query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ");";
        }
         * 
         */

        String query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type, lig_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
                // chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ", '" + lig_name + "');";
                
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, chain_id);
            statement.setInt(2, dssp_start);
            statement.setInt(3, dssp_end);
            statement.setString(4, pdb_start);
            statement.setString(5, pdb_end);
            statement.setString(6, sequence);
            statement.setInt(7, sse_type);
            statement.setString(8, lig_name);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeSSEToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeSSEToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeSSEToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        return(result);       
    }
    
        
    
    /**
     * Writes data on a protein to the database
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
                System.err.println("WARNING: DB: writeProteinToDB: Protein '" + pdb_id + "' already in DB and deleting it failed.");
            }                        
        }
        
        Boolean result = false;

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_protein + " (pdb_id, title, header, keywords, experiment, resolution) VALUES (?, ?, ?, ?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
            statement.setString(2, title);
            statement.setString(3, header);
            statement.setString(4, keywords);
            statement.setString(5, experiment);
            statement.setDouble(6, resolution);
                                
            statement.executeUpdate();
            dbc.commit();
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
            dbc.setAutoCommit(true);
        }
        return(result);

    }
    

    /**
     * Deletes all entries related to the PDB ID 'pdb_id' from the plcc database tables.
     * @return The number of affected records (0 if the PDB ID was not in the database).
     */
    public static Integer deletePdbidFromDB(String pdb_id) {

        PreparedStatement statement = null;        
        ResultSetMetaData md;
        int count = 0;        
        ResultSet rs = null;
        
        
        String query = "DELETE FROM " + tbl_protein + " WHERE pdb_id = ?;";
        
        
        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
              
            
            count = statement.executeUpdate();
            dbc.commit();
            
            //md = rs.getMetaData();
            //count = md.getColumnCount();
            
            
        } catch (SQLException e) {
            System.err.println("ERROR: SQL: deletePdbidFromDB: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                dbc.setAutoCommit(true);
            } catch(Exception e) { System.err.println("WARNING: DB: deletePdbidFromDB: Could not close statement and reset autocommit."); }
        }
        
        // The other tables are handled automatically via the ON DELETE CASCADE constraint.

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
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, chain_name);
            statement.setString(2, pdb_id);
            statement.setString(3, molName);
            statement.setString(4, orgScientific);
            statement.setString(5, orgCommon);
                                
            statement.executeUpdate();
            dbc.commit();
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
            dbc.setAutoCommit(true);
        }
        return(result);

    }
    
    
    /**
     * Writes information on a protein graph to the database. This includes a string representing the protein
     * graph in VPLG format. Note that the chain has to exist in the database already.
     * 
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the chain represented by the graph_string
     * @param graph_type the Integer representation of the graph type. use ProtGraphs.getGraphTypeCode() to get it.
     * @param graph_string the graph in VPLG format
     * @return true if the graph was inserted, false if errors occurred
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static Boolean writeGraphToDB(String pdb_id, String chain_name, Integer graph_type, String graph_string, String sse_string) throws SQLException {
               
        Integer chain_db_id = getDBChainID(pdb_id, chain_name);
        Boolean result = false;

        if (chain_db_id < 0) {
            System.err.println("ERROR: writeGraphToDB: Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_graph + " (chain_id, graph_type, graph_string, sse_string) VALUES (?, ?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, chain_db_id);
            statement.setInt(2, graph_type);
            statement.setString(3, graph_string);
            statement.setString(4, sse_string);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: writeGraphToDB: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: writeGraphToDB: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: writeGraphToDB: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
                }
            }
            result = false;
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        return(result);
    }
    

    /**
     * Writes information on a SSE contact to the database.
     * @param pdb_id the PDB identifier of the protein
     * @param chain_name the PDB chain name of the chain represented by the graph_string
     * @param sse1_dssp_start the DSSP number of the first residue of the first SSE
     * @param sse2_dssp_start the DSSP number of the first residue of the second SSE
     * @param contact_type the contact type code
     */
    public static Boolean writeContactToDB(String pdb_id, String chain_name, Integer sse1_dssp_start, Integer sse2_dssp_start, Integer contact_type) throws SQLException {

        // Just abort if this is not a valid contact type. Note that 0 is CONTACT_NONE.
        if (contact_type <= 0) {
            return (false);
        }

        Integer chain_id = getDBChainID(pdb_id, chain_name);

        if (chain_id < 0) {
            System.err.println("ERROR: DB: writeContactToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        Integer sse1_id = getDBSseID(sse1_dssp_start, chain_id);
        Integer sse2_id = getDBSseID(sse2_dssp_start, chain_id);
        Integer tmp;

        // We may need to switch the IDs to make sure the 1st of them is always lower
        if (sse1_id > sse2_id) {
            tmp = sse2_id;
            sse2_id = sse1_id;
            sse1_id = tmp;
        }

        Boolean result = false;
        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_contact + " (sse1, sse2, contact_type) VALUES (?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, sse1_id);
            statement.setInt(2, sse2_id);
            statement.setInt(3, contact_type);
                                
            statement.executeUpdate();
            dbc.commit();
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
            dbc.setAutoCommit(true);
        }
                
        return(result);
    }

    
    /**
     * Retrieves the internal database SSE ID of a SSE from the DB.
     * @param dssp_start the DSSP start residue number of the SSE
     * @param chain_id the DB chain ID of the chain the SSE is part of
     * @return The ID if it was found, -1 otherwise.
     */
    private static Integer getDBSseID(Integer dssp_start, Integer chain_id) {
        Integer id = -1;
        ArrayList<ArrayList<String>> rowarray = doSelectQuery("SELECT s.sse_id FROM " + tbl_sse + " s JOIN " + tbl_chain + " c ON ( s.chain_id = c.chain_id ) WHERE ( s.dssp_start = " + dssp_start + " AND c.chain_id = '" + chain_id + "' );");

        if (rowarray == null) {
            return (-1);
        } else {
            try {
                id = Integer.valueOf(rowarray.get(0).get(0));
                return (id);
            } catch (Exception e) {
                return (-1);
            }
        }
    }

    
   
    
    /**
     * Retrieves the internal database chain ID of a chain (it's PK) from the DB. The chain is identified by (pdb_id, chain_name).
     * @param pdb_id the PDB ID of the chain
     * @param chain_name the PDB chain name
     * @return the internal database chain id (its primary key, e.g. '2352365175365'). This is NOT the pdb chain name.
     */
    public static Integer getDBChainID(String pdb_id, String chain_name) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT chain_id FROM " + tbl_chain + " WHERE (pdb_id = ? AND chain_name = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
            statement.setString(2, chain_name);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
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
            System.err.println("ERROR: SQL: getDBChainID: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                dbc.setAutoCommit(true);
            } catch(Exception e) { System.err.println("WARNING: DB: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(Integer.valueOf(tableData.get(0).get(0)));
            }
            else {
                System.err.println("WARNING: DB: Chain '" + chain_name + "' of PDB ID '" + pdb_id + "' not in DB.");
                return(-1);
            }
        }
        else {
            return(-1);
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
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, dbChainID);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
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
            System.err.println("ERROR: SQL: getDBChainID: '" + e.getMessage() + "'.");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                dbc.setAutoCommit(true);
            } catch(Exception e) { System.err.println("WARNING: DB: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() == 1) {
            if(tableData.get(0).size() == 2) {
                return(new String[] { tableData.get(0).get(0), tableData.get(0).get(1)});
            }
            else {
                System.err.println("ERROR: DB: getPDBIDandChain(): Result row has unexpected length.");
                return(new String[] { "" } );
            }
        }
        else {
            if(tableData.isEmpty()) {
                // no such PK, empty result list
                return(new String[] { "" } );
            } else {
                System.err.println("ERROR: DB: getPDBIDandChain(): Result table has unexpected length '" + tableData.size() + "'. Should be either 0 or 1.");
                return(new String[] { "" } );
            }
            
        }        
    }
    
    
    /**
     * Retrieves the internal database chain ID of a chain (it's PK) from the DB. The chain is identified by (pdb_id, chain_name).
     * @param pdb_id the PDB ID of the chain
     * @param chain_name the PDB chain name
     * @return the internal database chain id (its primary key, e.g. '2352365175365'). This is NOT the pdb chain name.
     */
    public static Boolean proteinExistsInDB(String pdb_id) {
        
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        
        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT pdb_id FROM " + tbl_protein + " WHERE (pdb_id = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setString(1, pdb_id);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
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
                dbc.setAutoCommit(true);
            } catch(Exception e) { System.err.println("WARNING: DB: Could not close statement and reset autocommit."); }
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(true);
            }
            else {
                System.err.println("WARNING: DB: Protein with PDB ID '" + pdb_id + "' not in DB.");
                return(false);
            }
        }
        else {
            return(false);
        }        
    }
    
    
    /**
     * Retrieves the VPLG format graph string for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return the string representation of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraph(String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Integer chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            System.err.println("WARNING: getGraph(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT graph_string FROM " + tbl_graph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
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
            System.err.println("ERROR: SQL: getGraph: Retrieval of graph string failed: '" + e.getMessage() + "'.");
        } finally {
            if (statement != null) {
                statement.close();
            }
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(tableData.get(0).get(0));
            }
            else {
                System.err.println("WARNING: DB: No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
    
    /**
     * Retrieves the SSEstring for the requested graph from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return the SSEstring of the graph or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getSSEString(String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Integer chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            System.err.println("WARNING: getSSEString(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT sse_string FROM " + tbl_graph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
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
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(tableData.get(0).get(0));
            }
            else {
                System.err.println("WARNING: DB: getSSEString(): No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
    
    
    /**
     * Retrieves the graph data of all protein graphs from the database.
     * @param graphType the graph type, use one of the constants SSEGraph.GRAPHTYPE_* (e.g., SSEGraph.GRAPHTYPE_ALBE) or the word 'ALL' for all types.
     * @return an ArrayList of String arrays. Each of the String arrays in the list has 3 fields. The 
     * first fields (array[0]) contains the PDB ID of the chain, the second one (array[1]) contains the chain ID, the 
     * third field contains the graph type and the fourth field contains the SSE string.
     *  position 0 := pdb id
     *  position 1 := chain id
     *  position 2 := graph type
     *  position 3 := SSE string
     *  position 4 := graph string
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

        String query = "SELECT sse_string, chain_id, graph_type, graph_string FROM " + tbl_graph + " WHERE (graph_type = ?);";
        
        if(allGraphs) {
            query = "SELECT sse_string, chain_id, graph_type, graph_string FROM " + tbl_graph + ";";
        }

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            if( ! allGraphs) {
                statement.setInt(1, gtc);
            }
            
                                
            rs = statement.executeQuery();
            dbc.commit();
            
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
            dbc.setAutoCommit(true);
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
                } catch(Exception e) {
                    System.err.println("WARNING: DB: getAllGraphData(): '" + e.getMessage() + "' Ignoring data row.");
                    continue;
                }

                // OK, now get the PDB ID and chain name
                data = getPDBIDandChain(chainPK);
                
                if(data.length != 2) {
                    System.err.println("WARNING: DB: getAllGraphData(): Could not find chain with PK '" + chainPK + "' in DB, ignoring data row.");
                    continue;
                }
                else {
                    pdbidDB = data[0];
                    chainNameDB = data[1];
                    graphData.add(new String[]{pdbidDB, chainNameDB, graphTypeDB, graphSSEStringDB, graphStringDB});
                }                
            }
            else {
                System.err.println("WARNING: DB: getAllGraphData(): Result row #" + i + " has unexpected length " + tableData.get(i).size() + ".");
                return(graphData);
            }
        }
        
        return(graphData);                        
    }
    
    
    /**
     * Retrieves the VPLG format graph image for the requested graph in SVG format from the database. The graph is identified by the
     * unique triplet (pdbid, chain_name, graph_type).
     * @param pdbid the requested pdb ID, e.g. "1a0s"
     * @param chain_name the requested pdb ID, e.g. "A"
     * @param graph_type the requested graph type, e.g. "albe"
     * @return an XML string representing the graph in SVG format, or null if no such graph exists.
     * @throws SQLException if the database connection could not be closed or reset to auto commit (in the finally block)
     */
    public static String getGraphImageSVG(String pdb_id, String chain_name, String graph_type) throws SQLException {
        Integer gtc = ProtGraphs.getGraphTypeCode(graph_type);
        
        Integer chain_db_id = getDBChainID(pdb_id, chain_name);
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            System.err.println("WARNING: getGraph(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB.");
            return(null);
        }

        PreparedStatement statement = null;
        ResultSet rs = null;

        String query = "SELECT graph_image_svg FROM " + tbl_graph + " WHERE (chain_id = ? AND graph_type = ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, chain_db_id);
            statement.setInt(2, gtc);
                                
            rs = statement.executeQuery();
            dbc.commit();
            
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
            dbc.setAutoCommit(true);
        }
        
        // OK, check size of results table and return 1st field of 1st column
        if(tableData.size() >= 1) {
            if(tableData.get(0).size() >= 1) {
                return(tableData.get(0).get(0));
            }
            else {
                System.err.println("WARNING: DB: No entry for graph '" + graph_type + "' of PDB ID '" + pdb_id + "' chain '" + chain_name + "'.");
                return(null);
            }
        }
        else {
            return(null);
        }        
    }
    
}
