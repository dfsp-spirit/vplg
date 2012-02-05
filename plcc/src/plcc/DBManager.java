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
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doInsertQuery(): Could not close prepared statement.");
                }
            }
        }
    }

    /**
     * Executes the SQL update query 'query' and returns the number of updated rows if it succeeds, -1 otherwise.
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
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doUpdateQuery(): Could not close prepared statement.");
                }
            }
        }
    }

    /**
     * Executes the SQL delete query 'query' and returns the number of updated rows if it succeeds, -1 otherwise.
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
            return (-1);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doDeleteQuery(): Could not close prepared statement.");
                }
            }
        }
    }

    /**
     * Executes a select query.
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
            e.printStackTrace();
            System.exit(1);
            return (null);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doSelectQuery(): Could not close result set.");
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception ex) {
                    System.err.println("WARNING: doSelectQuery(): Could not close prepared statement.");
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
            doInsertQuery("CREATE TABLE " + tbl_graph + " (graph_id serial primary key, chain_id int not null references plcc_chain ON DELETE CASCADE, graph_type int not null, graph_string text not null);");

            // set constraints
            doInsertQuery("ALTER TABLE " + tbl_protein + " ADD CONSTRAINT constr_protein_uniq UNIQUE (pdb_id);");
            doInsertQuery("ALTER TABLE " + tbl_chain + " ADD CONSTRAINT constr_chain_uniq UNIQUE (chain_name, pdb_id);");
            doInsertQuery("ALTER TABLE " + tbl_sse + " ADD CONSTRAINT constr_sse_uniq UNIQUE (chain_id, dssp_start, dssp_end);");
            doInsertQuery("ALTER TABLE " + tbl_contact + " ADD CONSTRAINT constr_contact_uniq UNIQUE (sse1, sse2);");
            doInsertQuery("ALTER TABLE " + tbl_graph + " ADD CONSTRAINT constr_graph_uniq UNIQUE (chain_id, graph_type);");
            
            // create views
            //doInsertQuery("CREATE VIEW " + view_ssecontacts + " as select contact_id, least(sse1_type, sse2_type) sse1_type, greatest(sse1_type, sse2_type) sse2_type from (select k.contact_id, sse1.sse_type as sse1_type, sse2.sse_type as sse2_type from plcc_contact k left join plcc_sse sse1 on k.sse1=sse1.sse_id left join plcc_sse sse2 on k.sse2=sse2.sse_id) foo;");
            doInsertQuery("CREATE VIEW " + view_ssecontacts + " as select contact_id, least(sse1_type, sse2_type) sse1_type, greatest(sse1_type, sse2_type) sse2_type, sse1_lig_name, sse2_lig_name  from (select k.contact_id, sse1.sse_type as sse1_type, sse2.sse_type as sse2_type, sse1.lig_name as sse1_lig_name, sse2.lig_name as sse2_lig_name from plcc_contact k left join plcc_sse sse1 on k.sse1=sse1.sse_id left join plcc_sse sse2 on k.sse2=sse2.sse_id) foo;");
            doInsertQuery("CREATE VIEW " + view_graphs + " as select graph_id, pdb_id, chain_name, graph_type, graph_string from (select k.graph_id, k.graph_type, k.graph_string, chain.chain_name as chain_name, chain.pdb_id as pdb_id from plcc_graph k left join plcc_chain chain on k.chain_id=chain.chain_id) foo;");

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


            res = true;      // Not really, need to check all of them.

        } catch (Exception e) {
            res = false;
        }

        return (res);
    }

    /**
     * Writes information on a SSE to the database. Note that the chain has to exist in the database already.
     */
    public static Boolean writeSSEToDB(String pdb_id, String chain_name, Integer dssp_start, Integer dssp_end, String pdb_start, String pdb_end, String sequence, Integer sse_type, String lig_name) {

        // TODO: rewrite this using prepared statements
        Integer numRows = null;
        Integer chain_id = getDBChainID(pdb_id, chain_name);

        if (chain_id < 0) {
            System.err.println("ERROR: writeSSEToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        // The lig_name value is NULL for non-ligand SSEs (the string in the arguments list of this function is empty "" for them.)
        String query = null;

        if (lig_name.length() >= 1) {
            query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type, lig_name) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ", '" + lig_name + "');";
        } else {
            query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ");";
        }

        numRows = doInsertQuery(query);

        if (numRows < 0) {
            System.err.println("ERROR: writeSSEToDB(): Could not write data on an SSE of protein '" + pdb_id + "' and chain '" + chain_name + "' to DB.");
            return (false);
        } else {
            return (true);
        }
    }
    
    
    /**
     * Writes information on a protein graph to the database. This function is deprecated because it does
     * not use prepared statements. Use writeGraphToDB() instead.
     */
    @Deprecated public static Boolean writeGraphToDBOld(String pdb_id, String chain_name, Integer graph_type, String graph_string) {

        Integer numRows = null;
        Integer chain_id = getDBChainID(pdb_id, chain_name);

        if (chain_id < 0) {
            System.err.println("ERROR: writeGraphToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        // The lig_name value is NULL for non-ligand SSEs (the string in the arguments list of this function is empty "" for them.)
        String query = null;
        
        // (graph_id serial primary key, chain_id int not null references plcc_chain ON DELETE CASCADE, graph_type int not null, graph_string text not null)
        query = "INSERT INTO " + tbl_graph + " (chain_id, graph_type, graph_string) VALUES (" + chain_id + ", " + graph_type + ", " + graph_string + ");";
        
        numRows = doInsertQuery(query);

        if (numRows < 0) {
            System.err.println("ERROR: writeGraphToDB(): Could not write data on the protein graph of type '" + ProtGraphs.getGraphTypeString(graph_type) + "' of protein '" + pdb_id + "' and chain '" + chain_name + "' to DB.");
            return (false);
        } else {
            return (true);
        }
    }
    

    /**
     * Writes information on a protein to the database.
     */
    public static Boolean writeProteinToDB(String pdb_id, String title, String header, String keywords, String experiment, Double resolution) {

        // TODO: rewrite this using prepared statements
        Integer numRows = null;

        String query = "INSERT INTO " + tbl_protein + " (pdb_id, title, header, keywords, experiment, resolution) VALUES ('" + pdb_id + "', '" + title + "', '" + header + "', '" + keywords + "', '" + experiment + "', " + resolution + ");";

        numRows = doInsertQuery(query);

        if (numRows < 0) {
            System.err.println("ERROR: writeProteinToDB(): Could not write data on protein '" + pdb_id + "' to DB.");
            return (false);
        } else {
            return (true);
        }
    }

    /**
     * Deletes all entries related to the PDB ID 'pdb_id' from the plcc database tables.
     * @return The number of affected records (0 if the PDB ID was not in the database).
     */
    public static Integer deletePdbidFromDB(String pdb_id) {

        // TODO: rewrite this using prepared statements
        Integer numRows = null;

        String query = "DELETE FROM " + tbl_protein + " WHERE pdb_id = '" + pdb_id + "';";

        numRows = doDeleteQuery(query);

        // The other tables are handled automatically via the ON DELETE CASCADE constraint.

        return (numRows);
    }

    /**
     * Writes information on a chain to the database.
     */
    public static Boolean writeChainToDB(String chain_name, String pdb_id, String molName, String orgScientific, String orgCommon) {

        // TODO: rewrite this using prepared statements
        
        Integer numRows = null;

        String query = "INSERT INTO " + tbl_chain + " (chain_name, pdb_id, mol_name, organism_scientific, organism_common) VALUES ('" + chain_name + "', '" + pdb_id + "', '" + molName + "', '" + orgScientific + "', '" + orgCommon + "');";

        numRows = doInsertQuery(query);

        if (numRows < 0) {
            System.err.println("ERROR: writeChainToDB(): Could not write data on chain '" + chain_name + "' of protein '" + pdb_id + "' to DB.");
            return (false);
        } else {
            return (true);
        }
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
    public static Boolean writeGraphToDB(String pdb_id, String chain_name, Integer graph_type, String graph_string) throws SQLException {
               
        Integer chain_db_id = getDBChainID(pdb_id, chain_name);
        Boolean result = false;

        if (chain_db_id < 0) {
            System.err.println("ERROR: writeGraphToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        PreparedStatement statement = null;

        String query = "INSERT INTO " + tbl_graph + " (chain_id, graph_type, graph_string) VALUES (?, ?, ?);";

        try {
            dbc.setAutoCommit(false);
            statement = dbc.prepareStatement(query);

            statement.setInt(1, chain_db_id);
            statement.setInt(2, graph_type);
            statement.setString(3, graph_string);
                                
            statement.executeUpdate();
            dbc.commit();
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: '" + e.getMessage() + "'.");
            if (dbc != null) {
                try {
                    System.err.print("ERROR: SQL: Transaction is being rolled back.");
                    dbc.rollback();
                } catch(SQLException excep) {
                    System.err.println("ERROR: SQL: Could not roll back transaction: '" + excep.getMessage() + "'.");                    
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
     */
    public static Boolean writeContactToDB(String pdb_id, String chain_name, Integer sse1_dssp_start, Integer sse2_dssp_start, Integer contact_type) {
        // TODO: rewrite this using prepared statements

        // Just abort if this is not a valid contact type. Note that 0 is CONTACT_NONE.
        if (contact_type <= 0) {
            return (false);
        }

        Integer chain_id = getDBChainID(pdb_id, chain_name);

        if (chain_id < 0) {
            System.err.println("ERROR: writeContactToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
            return (false);
        }

        Integer numRows = null;

        Integer sse1_id = getDBSseID(sse1_dssp_start, chain_id);
        Integer sse2_id = getDBSseID(sse2_dssp_start, chain_id);
        Integer tmp;

        // We may need to switch the IDs to make sure the 1st of them is always lower
        if (sse1_id > sse2_id) {
            tmp = sse2_id;
            sse2_id = sse1_id;
            sse1_id = tmp;
        }

        String query = "INSERT INTO " + tbl_contact + " (sse1, sse2, contact_type) VALUES (" + sse1_id + ", " + sse2_id + "," + contact_type + ");";

        numRows = doInsertQuery(query);

        if (numRows < 0) {
            System.err.println("ERROR: writeChainToDB(): Could not write data on chain '" + chain_name + "' of protein '" + pdb_id + "' to DB.");
            return (false);
        } else {
            return (true);
        }
    }

    
    /**
     * Retrieves the internal database SSE ID of a SSE from the DB.
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
        
        /*
         * These checks are not needed anymore, now using prepared statements.
         * 
        if(pdb_id.length() != 4 || chain_name.length() != 1) {
            System.err.println("ERROR: SQL: Format of requested PDB ID '" + pdb_id + "' and chain '" + chain_name + "' invalid (wrong length).");
            return(-1);
        }
        
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m_pdb_id = p.matcher(pdb_id);
        Matcher m_chain_name = p.matcher(chain_name);
        Boolean found_pdb_id = m_pdb_id.find();
        Boolean found_chain_name = m_chain_name.find();
        if (found_pdb_id ||found_chain_name) {
            System.err.println("ERROR: SQL: Requested PDB ID '" + pdb_id + "' and chain '" + chain_name + "' contain invalid characters.");
            return(-1);
        } 
         * 
         */
        
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
            System.err.println("ERROR: SQL: '" + e.getMessage() + "'.");
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
        Boolean result = false;
        ResultSetMetaData md;
        ArrayList<String> columnHeaders;
        ArrayList<ArrayList<String>> tableData = new ArrayList<ArrayList<String>>();
        ArrayList<String> rowData = null;
        int count;
        

        if (chain_db_id < 0) {
            System.err.println("ERROR: writeGraphToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
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
            
            result = true;
        } catch (SQLException e ) {
            System.err.println("ERROR: SQL: '" + e.getMessage() + "'.");
            result = false;
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
