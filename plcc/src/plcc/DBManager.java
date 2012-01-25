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
   static String tbl_protein = "plcc_protein";
   static String tbl_chain = "plcc_chain";
   static String tbl_sse = "plcc_sse";
   static String tbl_contact = "plcc_contact";
   static String view_ssecontacts = "plcc_ssetype_contacts";

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
       } catch(Exception e) {
           System.err.println("ERROR: Could not load JDBC driver '" + dbDriver + "'. Is the correct db driver installed at lib/postgresql-jdbc.jar?");
           System.err.println("ERROR: See the README for more info on getting the proper driver for your PostgreSQL server and Java versions.'");
           System.exit(-1);
       }

       Boolean conOK = connect();
       return(conOK);

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
       } catch(Exception e) {
           System.err.println("ERROR: Could not connect to database at '" + dbURL + "'.");
           System.exit(-1);
       }

       try {
           System.out.println("Connection to " + dbmd.getDatabaseProductName() + " " + dbmd.getDatabaseProductVersion() + " successful.");
       } catch(Exception e) {
           // Something didn't work out if this failed.
           conOK = false;
       }

       return(conOK);
   }

   /**
    * Checks whether a DB connection exists. Tries to establish it if not.
    * @return: Whether a DB connection could be established in the end.
    */
   private static Boolean ensureConnection() {

       try {
           dbc.getMetaData();
       }
       catch(Exception e) {
           return(connect());
       }

       return(true);
   }


   /**
    * Determines whether the underlying DBMS supports transactions.
    */
   boolean supportsTransactions() throws SQLException {

       ensureConnection();

       return(dbc.getMetaData().supportsTransactions());
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
           return(ps.executeUpdate());          // num rows affected
       }
       catch(Exception e) {
           System.err.println("WARNING: doInsertQuery(): SQL statement '" + query + "' failed.");
           return(-1);
       }
       finally {
           if (ps  != null) {
               try {
                   ps.close();
               }
               catch (Exception ex) {
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
           return(ps.executeUpdate());          // num rows affected
       }
       catch(Exception e) {
           System.err.println("WARNING: doUpdateQuery(): SQL statement '" + query + "' failed.");
           return(-1);
       }
       finally {
           if (ps  != null) {
               try {
                   ps.close();
               }
               catch (Exception ex) {
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
       }
       catch(Exception e) {
           System.err.println("WARNING: doDeleteQuery(): SQL statement '" + query + "' failed.");
           return(-1);
       }
       finally {
           if (ps  != null)
           try { ps.close();  }
           catch (Exception ex) {
               System.err.println("WARNING: doDeleteQuery(): Could not close prepared statement.");
           }
       }
   }


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
           return(tableData);
       }
       catch(Exception e) {
           System.err.println("WARNING: doDeleteQuery(): SQL statement '" + query + "' failed.");
           e.printStackTrace();
           System.exit(-1);
           return(null);
       }
       finally
       { if (rs  != null)
          try { rs.close();  }
          catch (Exception ex) { System.err.println("WARNING: doSelectQuery(): Could not close result set."); }
        if (ps  != null)
          try { ps.close();  }
          catch (Exception ex) { System.err.println("WARNING: doSelectQuery(): Could not close prepared statement."); }
       }

    }





   /**
    * Closes the DB connection.
    * @return Whether the connection could be closed.
    */
   public static Boolean closeConnection() {

       if(dbc != null) {
           try {
               if( ! dbc.isClosed()) {
                   if( ! dbc.getAutoCommit()) {
                    dbc.commit();
                   }
                   dbc.close();
                   return(true);
               }
               else {
                   return(true);        // already closed
               }
           } catch(Exception e) {
               System.err.println("WARNING: closeConnection(): Could not close DB connection.");
               return(false);
           }
       }
       else {
           // there is no connection object
           return(true);
       }
   }

   /**
    * Drops (=deletes) all statistics tables in the database.
    */
   public static Boolean dropTables() {
       ensureConnection();
       Boolean res = false;

       try {
           doDeleteQuery("DROP TABLE " + tbl_protein + " CASCADE;");
           doDeleteQuery("DROP TABLE " + tbl_chain + " CASCADE;");
           doDeleteQuery("DROP TABLE " + tbl_sse + " CASCADE;");
           doDeleteQuery("DROP TABLE " + tbl_contact + " CASCADE;");
           
           // The indices get dropped with the tables.
           //doDeleteQuery("DROP INDEX plcc_idx_chain_insert;");
           //doDeleteQuery("DROP INDEX plcc_idx_sse_insert;");
           
           res = true;      // Not really, need to check all of them

       } catch(Exception e) {
           res = false;
       }

       return(res);

   }
   

   /**
    * Creates the statistics tables in the database.
    * @return Whether they could be created.
    */
   public static Boolean createTables() {
       

       ensureConnection();
       Boolean res = false;

       try {
           // create tables
           doInsertQuery("CREATE TABLE " + tbl_protein + " (pdb_id varchar(4) primary key, header varchar(200) not null, title varchar(400) not null, experiment varchar(200) not null, keywords varchar(400) not null, resolution real not null);");
           doInsertQuery("CREATE TABLE " + tbl_chain + " (chain_id serial primary key, chain_name varchar(2) not null, mol_name varchar(200) not null, organism_scientific varchar(200) not null, organism_common varchar(200) not null, pdb_id varchar(4) not null references plcc_protein ON DELETE CASCADE, prot_graph text);");
           doInsertQuery("CREATE TABLE " + tbl_sse + " (sse_id serial primary key, chain_id int not null references plcc_chain ON DELETE CASCADE, dssp_start int not null, dssp_end int not null, pdb_start varchar(20) not null, pdb_end varchar(20) not null, sequence varchar(2000) not null, sse_type int not null, lig_name varchar(5));");
           doInsertQuery("CREATE TABLE " + tbl_contact + " (contact_id serial primary key, sse1 int not null references plcc_sse ON DELETE CASCADE, sse2 int not null references plcc_sse ON DELETE CASCADE, contact_type int not null, check (sse1 < sse2));");

           // create views
           //doInsertQuery("CREATE VIEW " + view_ssecontacts + " as select contact_id, least(sse1_type, sse2_type) sse1_type, greatest(sse1_type, sse2_type) sse2_type from (select k.contact_id, sse1.sse_type as sse1_type, sse2.sse_type as sse2_type from plcc_contact k left join plcc_sse sse1 on k.sse1=sse1.sse_id left join plcc_sse sse2 on k.sse2=sse2.sse_id) foo;");
           doInsertQuery("CREATE VIEW " + view_ssecontacts + " as select contact_id, least(sse1_type, sse2_type) sse1_type, greatest(sse1_type, sse2_type) sse2_type, sse1_lig_name, sse2_lig_name  from (select k.contact_id, sse1.sse_type as sse1_type, sse2.sse_type as sse2_type, sse1.lig_name as sse1_lig_name, sse2.lig_name as sse2_lig_name from plcc_contact k left join plcc_sse sse1 on k.sse1=sse1.sse_id left join plcc_sse sse2 on k.sse2=sse2.sse_id) foo;");

           // add comments
           doInsertQuery("COMMENT ON COLUMN " + tbl_sse  + ".sse_type is '1=helix, 2=beta strand, 3=ligand, 4=other';");
           doInsertQuery("COMMENT ON COLUMN " + tbl_contact  + ".contact_type is '1=mixed, 2=parallel, 3=antiparallel, 4=ligand';");
           doInsertQuery("COMMENT ON COLUMN " + tbl_sse  + ".lig_name is 'The 3-letter ligand name from the PDB file and the RCSB ligand expo website. If this SSE is not a ligand SSE, this is the empty string.';");
           
           // add indices
           doInsertQuery("CREATE INDEX plcc_idx_chain_insert ON " + tbl_chain  + " (pdb_id, chain_name);");         // for SELECTs during data insert
           doInsertQuery("CREATE INDEX plcc_idx_sse_insert ON " + tbl_sse  + " (dssp_start, chain_id);");           // for SELECTs during data insert
           
           doInsertQuery("CREATE INDEX plcc_idx_chain_fk ON " + tbl_chain  + " (pdb_id);");                          // for JOINs, ON CASCADE, etc. (foreign key, FK)
           doInsertQuery("CREATE INDEX plcc_idx_sse_fk ON " + tbl_sse  + " (chain_id);");                            // FK
           doInsertQuery("CREATE INDEX plcc_idx_contact_fk1 ON " + tbl_contact  + " (sse1);");                       // FK
           doInsertQuery("CREATE INDEX plcc_idx_contact_fk2 ON " + tbl_contact  + " (sse2);");                       // FK
           
           // indices on PKs get created automatically


           res = true;      // Not really, need to check all of them.

       } catch(Exception e) {
           res = false;
       }

       return(res);
   }
   

   /**
    * Writes information on a SSE to the database. Note that the chain has to exist in the database already.
    */
   public static Boolean writeSSEToDB(String pdb_id, String chain_name, Integer dssp_start, Integer dssp_end, String pdb_start, String pdb_end, String sequence, Integer sse_type, String lig_name) {

       Integer numRows = null;
       Integer chain_id = getDBChainID(pdb_id, chain_name);

       if(chain_id < 0) {
           System.err.println("ERROR: writeSSEToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
           return(false);
       }

       // The lig_name value is NULL for non-ligand SSEs (the string in the arguments list of this function is empty "" for them.)
       String query = null;
       
       if(lig_name.length() >= 1) {
           query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type, lig_name) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type +  ", '" + lig_name + "');";
       }
       else {
           query = "INSERT INTO " + tbl_sse + " (chain_id, dssp_start, dssp_end, pdb_start, pdb_end, sequence, sse_type) VALUES (" + chain_id + ", " + dssp_start + ", " + dssp_end + ", '" + pdb_start + "', '" + pdb_end + "', '" + sequence + "', " + sse_type + ");";
       }

       numRows = doInsertQuery(query);

       if(numRows < 0) {
           System.err.println("ERROR: writeSSEToDB(): Could not write data on an SSE of protein " + pdb_id + "' and chain '" + chain_name + "' to DB.");
           return(false);
       }
       else {
           return(true);
       }
   }


   /**
    * Writes information on a protein to the database.
    */
   public static Boolean writeProteinToDB(String pdb_id, String title, String header, String keywords, String experiment, Double resolution) {

       Integer numRows = null;

       String query = "INSERT INTO " + tbl_protein + " (pdb_id, title, header, keywords, experiment, resolution) VALUES ('" + pdb_id + "', '" + title + "', '" + header  + "', '" + keywords  + "', '" + experiment  + "', " + resolution + ");";

       numRows = doInsertQuery(query);

       if(numRows < 0) {
           System.err.println("ERROR: writeProteinToDB(): Could not write data on protein '" + pdb_id + "' to DB.");
           return(false);
       }
       else {
           return(true);
       }
   }


   /**
    * Deletes all entries related to the PDB ID 'pdb_id' from the plcc database tables.
    * @return The number of affected records (0 if the PDB ID was not in the database).
    */
   public static Integer deletePdbidFromDB(String pdb_id) {

       Integer numRows = null;

       String query = "DELETE FROM " + tbl_protein + " WHERE pdb_id = '" + pdb_id + "';";

       numRows = doDeleteQuery(query);

       // The other tables are handled automatically via the ON DELETE CASCADE constraint.

       return(numRows);
   }
   

   /**
    * Writes information on a chain to the database.
    */
   public static Boolean writeChainToDB(String chain_name, String pdb_id, String molName, String orgScientific, String orgCommon) {

       Integer numRows = null;

       String query = "INSERT INTO " + tbl_chain + " (chain_name, pdb_id, mol_name, organism_scientific, organism_common) VALUES ('" + chain_name + "', '" + pdb_id + "', '" + molName + "', '" + orgScientific + "', '" + orgCommon + "');";

       numRows = doInsertQuery(query);

       if(numRows < 0) {
           System.err.println("ERROR: writeChainToDB(): Could not write data on chain '" + chain_name + "' of protein '" + pdb_id + "' to DB.");
           return(false);
       }
       else {
           return(true);
       }
   }

   
   /**
    * Writes information on a SSE contact to the database.
    */
   public static Boolean writeContactToDB(String pdb_id, String chain_name, Integer sse1_dssp_start, Integer sse2_dssp_start, Integer contact_type) {

       // Just abort if this is not a valid contact type. Note that 0 is CONTACT_NONE.
       if(contact_type <= 0) {
           return(false);
       }

       Integer chain_id = getDBChainID(pdb_id, chain_name);

       if(chain_id < 0) {
           System.err.println("ERROR: writeContactToDB(): Could not find chain with pdb_id '" + pdb_id + "' and chain_name '" + chain_name + "' in DB, could not insert SSE.");
           return(false);
       }

       Integer numRows = null;

       Integer sse1_id = getDBSseID(sse1_dssp_start, chain_id);
       Integer sse2_id = getDBSseID(sse2_dssp_start, chain_id);
       Integer tmp;

       // We may need to switch the IDs to make sure the 1st of them is always lower
       if(sse1_id > sse2_id) {
           tmp = sse2_id;
           sse2_id = sse1_id;
           sse1_id = tmp;
       }

       String query = "INSERT INTO " + tbl_contact + " (sse1, sse2, contact_type) VALUES (" + sse1_id + ", " + sse2_id + "," + contact_type + ");";

       numRows = doInsertQuery(query);

       if(numRows < 0) {
           System.err.println("ERROR: writeChainToDB(): Could not write data on chain '" + chain_name + "' of protein '" + pdb_id + "' to DB.");
           return(false);
       }
       else {
           return(true);
       }
   }


   /**
    * Retrieves the internal database SSE ID of a SSE from the DB.
    * @return The ID if it was found, -1 otherwise.
    */
   private static Integer getDBSseID(Integer dssp_start, Integer chain_id) {
       Integer id = -1;
       ArrayList<ArrayList<String>> rowarray = doSelectQuery("SELECT s.sse_id FROM " + tbl_sse + " s JOIN " + tbl_chain + " c ON ( s.chain_id = c.chain_id ) WHERE ( s.dssp_start = " + dssp_start + " AND c.chain_id = '" + chain_id + "' );");

       if(rowarray == null) {
           return(-1);
       }
       else {
           try {
               id = Integer.valueOf(rowarray.get(0).get(0));
               return(id);
           } catch(Exception e) {
               return(-1);
           }
       }
   }
   

   /**
    * Retrieves the internal database chain ID of a chain from the DB.
    * @return The ID if it was found, -1 otherwise.
    */
   private static Integer getDBChainID(String pdb_id, String chain_name) {

       Integer id = -1;
       ArrayList<ArrayList<String>> rowarray = doSelectQuery("SELECT chain_id FROM " + tbl_chain + " WHERE pdb_id = '" + pdb_id + "' AND chain_name = '" + chain_name + "';");

       if(rowarray == null) {
           return(-1);
       }
       else {
           try {
               id = Integer.valueOf(rowarray.get(0).get(0));
               return(id);
           } catch(Exception e) {
               return(-1);
           }
       }
   }

}
