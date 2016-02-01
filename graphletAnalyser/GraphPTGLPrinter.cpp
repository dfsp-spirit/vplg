/* 
 * File:   GraphPTGLPrinter.cpp
 * Author: ben
 *
 * Created on May 13, 2015, 11:46 AM
 */

#include "GraphPTGLPrinter.h"
#include "global.h"
#include "Database.h"
#include "db.h"

using namespace pqxx;

/* default constructor */
GraphPTGLPrinter::GraphPTGLPrinter() {

}



/*
 * prints vertices and their sse types to a string
 * CAUTION: only applicable to ptgl protein graphs */
std::string GraphPTGLPrinter::printVertices(const Graph& g) const {
    VertexIterator vi, vi_end, next; // create vertex iterator objects
    
    
    tie(vi, vi_end) = vertices(g); // that belong to the graph
    stringstream sstream; // to be saved in a string
    
    
    sstream << "Iterate over the vertices and print their properties:\n";
    for (next = vi; vi != vi_end; vi = next) { // iterate over the vertices
        ++next;
        // and print them with their sse type
        
        std::unordered_map<std::string, std::string> map = g[*vi].properties;
        
        sstream << "  vertex " << setw(2) << *vi << " has  sse_type = " << map["sse_type"] << endl;
        //sstream << "  vertex " << setw(2) << *vi << " has  sse_type = " << g[*vi].properties["sse_type"] << endl;
    }
    sstream << endl;
    
    return sstream.str();
};

/*
 * print edges with their spatial (protein related) relationships to a string
 * CAUTION: only applicable to ptgl protein graphs */
std::string GraphPTGLPrinter::printEdges(const Graph& g) const {
    bool formatted = true;
    EdgeIterator ei, ei_end;
    stringstream sstream;
    
    
    
    sstream << "Iterate over the edges and print their properties:\n";
    if (formatted) {
        for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) { // iterate over edges
            
            std::unordered_map<std::string, std::string> map = g[*ei].properties;
            
            
            sstream << "  edge ("
                    << setw(2) << g[*ei].source
                    << ","
                    << setw(2) << g[*ei].target
                    << ") is "
                    << map["spatial"] << endl; // add their spatial relationships
        }       
    } else {
        for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) {
            
            std::unordered_map<std::string, std::string> map = g[*ei].properties;
            sstream << "  edge number " << *ei
                    << " has: source = " << g[*ei].source
                    << ", target = " << g[*ei].target
                    << ", spatial = " << map["spatial"] << endl;
        }
    }
    sstream << endl;
    
    return sstream.str();    
};

/* Print all vertices, then all edges, and then then all adjacent vertices for
 * each vertex
 * CAUTION: only applicable to ptgl protein graphs */
void GraphPTGLPrinter::printGraphInfo(const Graph& g) const {
    stringstream sstream;
        
    
    
    std::vector<std::vector<int>> adj_all_vector = std::vector<std::vector<int>>();
    
    int n = num_vertices(g);
    
    for (int i = 0; i < n; i++) {
        std::vector<int> adj_vector = std::vector<int>();
        adj_vector.push_back(i);
        AdjacencyIterator first, last;
        for (tie(first,last) = adjacent_vertices(i,g); first != last; ++first) {
            adj_vector.push_back(g[*first].id);
        }
        
        
        adj_all_vector.push_back(adj_vector);
    }
        

    sstream << "[GRAPH INFO]\n"
            << printVertices(g)
            << printEdges(g) 
            << printAdjacentAll(adj_all_vector);
    
    cout << sstream.str();

}

/* Get the vertices with their sse types, aa residues, and the number of residues,
 * and the edges with their protein-related spatial relationships
 */
string GraphPTGLPrinter::printGraphString(const Graph& g) const {
    // define iterators
    VertexIterator vi, vi_end, next;
    EdgeIterator ei, ei_end;
    
    
    tie(vi, vi_end) = vertices(g);
    
    // set up the string stream
    stringstream sstream;
    
    
    
    // add vertices with their info
    sstream << "Vertices:\n";
    for (next = vi; vi != vi_end; vi = next) {
        ++next;
        std::unordered_map<std::string, std::string> map = g[*vi].properties;
        sstream << "  Vertex " << setw(2) << *vi << ": id=" << g[*vi].id << ", label=" << g[*vi].label << ", sse_type=" << map["sse_type"] << ", num_residues=" << map["num_residues"] << ", aa_sequence=" << map["aa_sequence"] << "." << endl;
    }
    sstream << endl;
    
    // add edges with their info
    sstream << "Edges:\n";
    for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) {
        
        std::unordered_map<std::string, std::string> map = g[*ei].properties;
        sstream << "  edge ("
                << setw(2) << g[*ei].source
                << ","
                << setw(2) << g[*ei].target
                << ") is "
                << map["spatial"] << endl;
    }
    
    return sstream.str();
}


/** 
 * Uses the pgxx library to connect to a postgresql database server.
 * For this, you will need to link against lpq and lpqxx and include the proper headers.
 * 
 * @param <int> graphtype_int -> the graphtype as an integer taken for ProteinGraphService
 * @param <vector<sting>> ids -  an arry of length 4 containing the identifiers of the                                              
 *                          at the following positions:
 *                          0 -> PDB ID
 *                          1 -> Chain ID
 *                          2 -> Label
 *                          3 -> Graph type as string
 * @param <vector<vector<float>> normalized graphlet counts
 * @param <vector<vector<float>> normalized ptgl counts
 */
int GraphPTGLPrinter::savePGCountsToDatabasePGXX(int graphtype_int, std::vector<std::string> ids, std::vector<std::vector<float>> norm_counts, std::vector<float> lab_counts, int time) {
    
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
 
    if (ids.size() != 4) {std::cerr << "Invalid number of Identifiers";}
    
    std::string pdbid = ids[0];
    std::string chain = ids[1];
    std::string label = ids[2];
    std::string graphtypestr = ids[3];
    int graphtype = graphtype_int;
    std::vector<float> graphlet3CountsNormalized = norm_counts[1];
    std::vector<float> graphlet4CountsNormalized = norm_counts[2];
    std::vector<float> graphlet5CountsNormalized = norm_counts[3];
    
    //TODO: implement function get--counts in graphservice
    //      so that the labeled counts can be written into the database
    
    std::vector<float> cl = lab_counts;
    
    
    
    if(graphtype <= 0 || graphtype >= 7) {
        cerr << apptag << "Cannot save graphlet counts for graph type '" << graphtype << "' to database, not implemented for this graphtype. Skipping.\n";
    }
    
    if( ! silent) {
        cout << apptag <<  "    Saving graphlets for graphtype " << graphtypestr << " (gt code=" << graphtype << ") of pdbid " << pdbid << " chain " << chain << " to PostgreSQL database.\n";
    }
    
    // TODO: remove this
    long unsigned int numGraphletsTypesCounted;
    if(label.compare("") != 0) {
        numGraphletsTypesCounted = graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size() + cl.size();
        if( ! silent) {
            cout << apptag << "    Saving " << graphlet3CountsNormalized.size() << " different 3-graphlets, " << graphlet4CountsNormalized.size() << " different 4-graphlets, " << graphlet5CountsNormalized.size() << " different 5-graphlets and " << cl.size() << " different labeled graphlets (" << numGraphletsTypesCounted << " total).\n";           
        }
    }
    else {
        numGraphletsTypesCounted = graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size();
        if( ! silent) {
            cout << apptag << "    Saving " << graphlet3CountsNormalized.size() << " different 3-graphlets, " << graphlet4CountsNormalized.size() << " different 4-graphlets and " << graphlet5CountsNormalized.size() << " different 5-graphlets (" << numGraphletsTypesCounted << " total).\n";   
        }
    }
    
    try {				                                
        long dbpk = getPGGraphDatabaseID(pdbid, chain, graphtype);

        connection C(connection_string);
        //cout << "      Connected to database '" << C.dbname() << "'.\n";
        work W(C);

        // The relevant table in the database is called 'plcc_graphlets'. It get created by the PLCC application using the following line of code:
        //   doInsertQuery("CREATE TABLE plcc_graphlets (graphlet_id serial primary key, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, graphlet_counts int[55] not null);");
        // referenced tables:
        //   doInsertQuery("CREATE TABLE plcc_graph (graph_id serial primary key, chain_id int not null references plcc_chain ON DELETE CASCADE, graph_type int not null references plcc_graphtypes, graph_string_gml text, graph_string_kavosh text, graph_string_dotlanguage text, graph_string_plcc text, graph_image_png text, graph_image_svg text, graph_image_pdf text, sse_string text, graph_containsbetabarrel int DEFAULT 0);");
        //   doInsertQuery("CREATE TABLE plcc_graphtypes (graphtype_id int not null primary key,  graphtype_text text not null);");
        //   doInsertQuery("CREATE TABLE plcc_chain (chain_id serial primary key, chain_name varchar(2) not null, mol_name varchar(200) not null, organism_scientific varchar(200) not null, organism_common varchar(200) not null, pdb_id varchar(4) not null references plcc_protein ON DELETE CASCADE, chain_isinnonredundantset smallint DEFAULT 1);");
        //   doInsertQuery("CREATE TABLE plcc_protein (pdb_id varchar(4) primary key, header varchar(200) not null, title varchar(400) not null, experiment varchar(200) not null, keywords varchar(400) not null, resolution real not null);");


        // Let's insert the graphlet counts into the DB.
        if(dbpk <= 0) {
            cerr << apptag << "ERROR: ID of protein graph not found in the database, cannot assign graphlets to it. Skipping.\n";
            return 1;
        }
        else {
            if( ! silent) {
                cout << apptag << "      Found graph in database, ID is " << dbpk << ".\n";  
            }
            
            int numGraphletCountsAlreadyInDatabase = databaseContainsGraphletsForPGGraph(dbpk);
            if(numGraphletCountsAlreadyInDatabase == 0) {
                if( ! silent) {
                    cout << apptag << "      Database does not contain graphlet count entries for this graph, ok.\n";
                }
            }
            else {
                if( ! silent) {
                    cout << apptag << "      Database already contains graphlet count entry for this graph, trying to delete old entry.\n";
                }
                deletePGGraphletCountEntryForGraph(dbpk);
            }
            
            stringstream ssdbpk;
            ssdbpk << dbpk;
            string dbpkStr = ssdbpk.str();
            
            // collect and insert graphlets
            // DB table: doInsertQuery("CREATE TABLE " + tbl_graphletcount + " (graphlet_id serial primary key, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, graphlet_counts int[55] not null);");
             string strVal;
            
            std::vector<float> allGraphletcounts;

            allGraphletcounts.reserve(graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size() + cl.size());
            allGraphletcounts.insert(allGraphletcounts.end(), graphlet3CountsNormalized.begin(), graphlet3CountsNormalized.end());
            allGraphletcounts.insert(allGraphletcounts.end(), graphlet4CountsNormalized.begin(), graphlet4CountsNormalized.end());
            allGraphletcounts.insert(allGraphletcounts.end(), graphlet5CountsNormalized.begin(), graphlet5CountsNormalized.end());
            allGraphletcounts.insert(allGraphletcounts.end(), cl.begin(), cl.end());

            
            // check stuff
            
            int numExpected = 64;
            if(cl.empty()) {
                numExpected = 64 - 38;
            }
            
            
            if(allGraphletcounts.size() != numExpected) {
                cerr << apptag << "ERROR: Expected counts for " << numExpected << " different graphlets, but found " << allGraphletcounts.size() << ". Cannot write graphlet counts to database.\n";
                return 1;
            }
            
            float val;
            string query = "INSERT INTO plcc_graphlets (graph_id, runtime_secs, graphlet_counts) VALUES (" + W.esc(dbpkStr) + ", " + std::to_string(time) + ", '{";
            for (int i = 0; i < allGraphletcounts.size(); i++) {
                std::stringstream ssVal;
                val = allGraphletcounts[i];
                
                // check for NaN values (resulting from division by zero during normalization, when the total number of graphlets found is 0)
                if(val != val) {    // proper way to check for NaN in C++ it seems
                    // no values will be added, resulting in an empty array {}
                    //continue; 
                    break;      // if one value is NaN, all others also are NaN anyway, so break instead of continue.
                }
                
                if(val < 0.0f || val > 1.0f) {
                    cout << apptag << "WARNING: Graphlet count for graphlet #" << i << " is '" << val << "', but should be normalized between 0.0 and 1.0.\n";
                }
                ssVal << std::fixed << std::setprecision(4) << val;
                strVal = ssVal.str();
                //cout << "      Adding value '" << strVal << "'.\n";
                query += W.esc(strVal);
                if(i < allGraphletcounts.size() - 1) {
                    query += ", ";
                }
            }
            
            
//            // fill the remaining fields if there is no data on labeled graphlets
//            if( ! withLabeled) {
//                float valueForMissing = -1.0f;
//                cout << apptag << "WARNING: Writing graphlets to database but missing data on labeled graphlets, setting count " << valueForMissing << " for them.\n";
//                int numMissingValues = (55  - allGraphletcounts.size());                
//                for (int i = 0; i < numMissingValues; i++) {
//                    std::stringstream ssVal;
//                    ssVal << std::fixed << std::setprecision(4) << valueForMissing;
//                    strVal = ssVal.str();
//                    query += W.esc(strVal);
//                    if(i < numMissingValues - 1) {
//                        query += ", ";
//                    }
//                }                
//            }
            
            
            query += "}');";
            //cout << "      SQL query is '" << query << "'.\n";
            pqxx::result res = W.exec(query);
            int numInserted = res.affected_rows();
            if(numInserted == 1) {
                if( ! silent) {
                    cout << apptag << "      Inserted " << numInserted << " graphlet count row.\n";
                }
            }
            else {
                cerr << apptag << "ERROR: Inserted " << numInserted << " graphlet count rows.\n";
            }
            W.commit();
            return 0;
        }

    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when trying to save graphlets for protein graph to DB: '" << e.what() << "'." << endl;
        return 1;
    }
}


/* Retrieves the ID of the graph from the ptgl
 * @param <string> PDB-ID
 * @param <string> Chain
 * @param <int> graphtype
 * @return <long> graph ID */
long GraphPTGLPrinter::getPGGraphDatabaseID(string pdbid, string chain, int graphType) const {
    
    long id = -1;
    
    stringstream ssgt;
    ssgt << graphType;
    string graphTypeStr = ssgt.str();
    
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    
    try {
        //connection C("dbname=vplg user=vplg host=localhost port=5432 connect_timeout=10 password=vplg");
        connection C(connection_string);
        //cout << "      Connected to database '" << C.dbname() << "'.\n";
        work W(C);
        result R = W.exec("SELECT g.graph_id FROM plcc_graph g INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON  p.pdb_id = c.pdb_id WHERE (c.chain_name = '" + W.esc(chain) + "' AND g.graph_type = " + W.esc(graphTypeStr) + " AND p.pdb_id = '" + W.esc(pdbid) + "');");

        //cout << "      Found " << R.size() << " graphs of type " << graphType << " for PDB " << pdbid << " chain " << chain << "." << endl;

        if(R.size() == 1) {
                result::const_iterator r = R.begin();
                id = atol(r[0].c_str());	// cast string to long and return
                return id;
        }

        //for (result::const_iterator r = R.begin(); r != R.end(); ++r) {
        //	cout << r[0].c_str() << endl;
        //}

    } catch (const std::exception &e) {
        cerr << apptag << "ERROR: SQL trouble when trying to retrieve protein graph PK from DB: '" << e.what() << "'." << endl;
        return -1;
    }
    
    return id;
}

/* Checks whether graphlets ahve already been computed for a given graph.
 * Returns -1 if no graphlets were computed.
 * 
 * @param <unsigned loong int> Graph ID
 * @return <int>  */
int GraphPTGLPrinter::databaseContainsGraphletsForPGGraph(unsigned long int databaseIDofGraph) const {

    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    
    stringstream ssdbpk;
    ssdbpk << databaseIDofGraph;
    string dbpkStr = ssdbpk.str();

    try {
        connection C(connection_string);
        work W(C);
        result R = W.exec("SELECT g.graphlet_id FROM plcc_graphlets g WHERE g.graph_id = " + W.esc(dbpkStr) + ";");

        int count = R.size();
        return count;		
        
    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when checking for graphlet entry for protein graph in DB: '" << e.what() << "'." << endl;
        return -1;
    }
}


/**
 * Saves the graphlet counts in NOVA format to the NOVA output file. If the file does already exist,
 * the data for this graph gets appended to it. This format is basically CSV.
 * See http://www.bioinformatik.uni-frankfurt.de/tools/nova/ for more info on NOVA.
 * @param <string> graphname
 * @param <vector<vector<float>> normalized counts
 * @param <vector<float>> normalized labeled counts
 */
void GraphPTGLPrinter::saveCountsInNovaFormat(std::string graphName, std::vector<std::vector<float>> norm_counts, std::vector<float> lab_counts) const {
    ofstream countsNovaFormatFile;
    const string countsNovaFormatFileName = output_path + "countsNovaFormat.csv";
    int pos;
    int numberOfGraphlets;
    
    std::vector<float> graphlet3CountsNormalized = norm_counts[1];
    std::vector<float> graphlet4CountsNormalized = norm_counts[2];
    std::vector<float> graphlet5CountsNormalized = norm_counts[3];
    std::vector<float> cl;

    countsNovaFormatFile.open(countsNovaFormatFileName.c_str(), std::ios_base::app);    
    if (!countsNovaFormatFile.is_open()) {
        cout << apptag << "ERROR: could not open counts file in NOVA format.\n";
    } else {
        pos = countsNovaFormatFile.tellp();
        if (pos == 0) {
            countsNovaFormatFile << "ID,Group";
            
            if (!lab_counts.empty()){ cl = lab_counts;
                              numberOfGraphlets = graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size() + cl.size(); }                      
            else             {numberOfGraphlets = graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size();}
                       
            for (int i = 1; i <= numberOfGraphlets; i++) {
                countsNovaFormatFile << ",Graphlet" << i;
            }        
            countsNovaFormatFile << "\n";
        }
        countsNovaFormatFile << graphName << ",A";             
        //countsNovaFormatFile << graphName << ",B";             
        for (int i = 0; i < graphlet3CountsNormalized.size(); i++) countsNovaFormatFile << "," << graphlet3CountsNormalized[i];
        for (int i = 0; i < graphlet4CountsNormalized.size(); i++) countsNovaFormatFile << "," << graphlet4CountsNormalized[i];
        for (int i = 0; i < graphlet5CountsNormalized.size(); i++) countsNovaFormatFile << "," << graphlet5CountsNormalized[i];
        if (!lab_counts.empty()) {
            for (int i = 0; i < cl.size(); i++) countsNovaFormatFile << "," << cl[i];
        }
        countsNovaFormatFile << "\n";
        countsNovaFormatFile.close();
    
        if( ! silent) {
            cout << apptag << "    The counts were added to the \"" << countsNovaFormatFileName << "\".\n"; 
        }
    }
}

/* Delete the graphlet count entry for a given graph
 * @param <unsigned long int> Graph ID
 * @return <void> */
void GraphPTGLPrinter::deletePGGraphletCountEntryForGraph(unsigned long int databaseIDofGraph) {
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    
    stringstream ssdbpk;
    ssdbpk << databaseIDofGraph;
    string dbpkStr = ssdbpk.str();

    try {
        connection C(connection_string);
        work W(C);
        result R = W.exec("DELETE FROM plcc_graphlets g WHERE g.graph_id = " + W.esc(dbpkStr) + ";");        
        int count = R.affected_rows();
        if( ! silent) {
            cout << apptag << "      Deleted " << count << " graphlet count entries.\n";
        }
        W.commit();                        
    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when checking for graphlet entry for protein graph in DB: '" << e.what() << "'." << endl;
    }
}

/* test connection to database */
int GraphPTGLPrinter::testDatabasePGXX() {

    if( ! silent) {
        cout << apptag << "    Testing PostgreSQL database connection.\n";
    }
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    if( ! silent) {
        cout << apptag << "    Database connection string is '" << connection_string << "'.\n";
    }
    try {
        connection C(connection_string);
        if( ! silent) {
            cout << apptag << "      Connected to database '" << C.dbname() << "'.\n";
        }
        work W(C);
        result R = W.exec("SELECT p.pdb_id FROM plcc_protein p");

        if( ! silent) {
            cout << apptag << "      Found " << R.size() << " proteins in the DB:" << endl;
        }

        for (result::const_iterator r = R.begin(); r != R.end(); ++r) {
            if( ! silent) {
                cout << apptag << "      PDB ID: " << r[0].c_str() << endl;
            }
        }

        return 0;		
    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when testing DB: '" << e.what() << "'." << endl;
        return 1;
    }
}

int GraphPTGLPrinter::saveAACountsToDatabasePGXX(std::string pdbid, std::string label, std::vector<std::vector<float>> norm_counts, std::vector<float> lab_counts, int time) {
    
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    std::string chain_description = "ALL";
 
    
    
    std::vector<float> graphlet3CountsNormalized = norm_counts[1];
    std::vector<float> graphlet4CountsNormalized = norm_counts[2];
    std::vector<float> graphlet5CountsNormalized = norm_counts[3];
    
    //TODO: implement function get--counts in graphservice
    //      so that the labeled counts can be written into the database
    
    std::vector<float> cl = lab_counts;

    
    if( ! silent) {
        cout << apptag <<  "    Saving graphlets of amino acid graph for pdbid " << pdbid << " to PostgreSQL database.\n";
    }
    
    // TODO: remove this
    long unsigned int numGraphletsTypesCounted;
    if(label.compare("") != 0) {
        numGraphletsTypesCounted = graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size() + cl.size();
        if( ! silent) {
            cout << apptag << "    Saving " << graphlet3CountsNormalized.size() << " different 3-graphlets, " << graphlet4CountsNormalized.size() << " different 4-graphlets, " << graphlet5CountsNormalized.size() << " different 5-graphlets and " << cl.size() << " different labeled graphlets (" << numGraphletsTypesCounted << " total).\n";           
        }
    }
    else {
        numGraphletsTypesCounted = graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size();
        if( ! silent) {
            cout << apptag << "    Saving " << graphlet3CountsNormalized.size() << " different 3-graphlets, " << graphlet4CountsNormalized.size() << " different 4-graphlets and " << graphlet5CountsNormalized.size() << " different 5-graphlets (" << numGraphletsTypesCounted << " total).\n";   
        }
    }
    
    try {				                                
        long dbpk = getAAGraphDatabaseID(pdbid, chain_description);

        connection C(connection_string);
        //cout << "      Connected to database '" << C.dbname() << "'.\n";
        work W(C);

        // The relevant table in the database is called 'plcc_graphlets'. It get created by the PLCC application using the following line of code:
        //   doInsertQuery("CREATE TABLE plcc_graphlets (graphlet_id serial primary key, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, graphlet_counts int[55] not null);");
        // referenced tables:
        //   doInsertQuery("CREATE TABLE plcc_graph (graph_id serial primary key, chain_id int not null references plcc_chain ON DELETE CASCADE, graph_type int not null references plcc_graphtypes, graph_string_gml text, graph_string_kavosh text, graph_string_dotlanguage text, graph_string_plcc text, graph_image_png text, graph_image_svg text, graph_image_pdf text, sse_string text, graph_containsbetabarrel int DEFAULT 0);");
        //   doInsertQuery("CREATE TABLE plcc_graphtypes (graphtype_id int not null primary key,  graphtype_text text not null);");
        //   doInsertQuery("CREATE TABLE plcc_chain (chain_id serial primary key, chain_name varchar(2) not null, mol_name varchar(200) not null, organism_scientific varchar(200) not null, organism_common varchar(200) not null, pdb_id varchar(4) not null references plcc_protein ON DELETE CASCADE, chain_isinnonredundantset smallint DEFAULT 1);");
        //   doInsertQuery("CREATE TABLE plcc_protein (pdb_id varchar(4) primary key, header varchar(200) not null, title varchar(400) not null, experiment varchar(200) not null, keywords varchar(400) not null, resolution real not null);");


        // Let's insert the graphlet counts into the DB.
        if(dbpk <= 0) {
            cerr << apptag << "ERROR: ID of amino acid graph for " << pdbid << " chain description " << chain_description << " not found in the database, cannot assign graphlets to it. Skipping.\n";
            return 1;
        }
        else {
            if( ! silent) {
                cout << apptag << "      Found amino acid graph in database, ID is " << dbpk << ".\n";  
            }
            
            int numGraphletCountsAlreadyInDatabase = databaseContainsGraphletsForAAGraph(dbpk);
            if(numGraphletCountsAlreadyInDatabase == 0) {
                if( ! silent) {
                    cout << apptag << "      Database does not contain graphlet count entries for this amino acid graph, ok.\n";
                }
            }
            else {
                if( ! silent) {
                    cout << apptag << "      Database already contains graphlet count entry for this amino acid graph, trying to delete old entry.\n";
                }
                deleteAAGraphletCountEntryForGraph(dbpk);
            }
            
            stringstream ssdbpk;
            ssdbpk << dbpk;
            string dbpkStr = ssdbpk.str();
            
            // collect and insert graphlets
            // DB table: doInsertQuery("CREATE TABLE " + tbl_graphletcount + " (graphlet_id serial primary key, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, graphlet_counts int[55] not null);");
             string strVal;
            
            std::vector<float> allGraphletcounts;

            allGraphletcounts.reserve(graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size() + cl.size());
            allGraphletcounts.insert(allGraphletcounts.end(), graphlet3CountsNormalized.begin(), graphlet3CountsNormalized.end());
            allGraphletcounts.insert(allGraphletcounts.end(), graphlet4CountsNormalized.begin(), graphlet4CountsNormalized.end());
            allGraphletcounts.insert(allGraphletcounts.end(), graphlet5CountsNormalized.begin(), graphlet5CountsNormalized.end());
            allGraphletcounts.insert(allGraphletcounts.end(), cl.begin(), cl.end());

            
            // check stuff
            /*
            int numExpected = 67;
            if(cl.empty()) {
                numExpected = 67 - 38;
            }
            
            
            if(allGraphletcounts.size() != numExpected) {
                cerr << apptag << "ERROR: Expected counts for " << numExpected << " different graphlets, but found " << allGraphletcounts.size() << ". Cannot write graphlet counts to database.\n";
                return 1;
            }
            
             */
            float val;
            string query = "INSERT INTO plcc_aa_graphlets (aagraph_id, runtime_secs, aa_graphlet_counts, ) VALUES (" + W.esc(dbpkStr) + ", " + std::to_string(time) + ", '{";
            for (int i = 0; i < allGraphletcounts.size(); i++) {
                std::stringstream ssVal;
                val = allGraphletcounts[i];
                
                // check for NaN values (resulting from division by zero during normalization, when the total number of graphlets found is 0)
                if(val != val) {    // proper way to check for NaN in C++ it seems
                    // no values will be added, resulting in an empty array {}
                    //continue; 
                    break;      // if one value is NaN, all others also are NaN anyway, so break instead of continue.
                }
                
                if(val < 0.0f || val > 1.0f) {
                    cout << apptag << "WARNING: Graphlet count for graphlet #" << i << " is '" << val << "', but should be normalized between 0.0 and 1.0.\n";
                }
                ssVal << std::fixed << std::setprecision(4) << val;
                strVal = ssVal.str();
                //cout << "      Adding value '" << strVal << "'.\n";
                query += W.esc(strVal);
                if(i < allGraphletcounts.size() - 1) {
                    query += ", ";
                }
            }
            
            
//            // fill the remaining fields if there is no data on labeled graphlets
//            if( ! withLabeled) {
//                float valueForMissing = -1.0f;
//                cout << apptag << "WARNING: Writing graphlets to database but missing data on labeled graphlets, setting count " << valueForMissing << " for them.\n";
//                int numMissingValues = (55  - allGraphletcounts.size());                
//                for (int i = 0; i < numMissingValues; i++) {
//                    std::stringstream ssVal;
//                    ssVal << std::fixed << std::setprecision(4) << valueForMissing;
//                    strVal = ssVal.str();
//                    query += W.esc(strVal);
//                    if(i < numMissingValues - 1) {
//                        query += ", ";
//                    }
//                }                
//            }
            
            
            query += "}');";
            //cout << "      SQL query is '" << query << "'.\n";
            pqxx::result res = W.exec(query);
            int numInserted = res.affected_rows();
            if(numInserted == 1) {
                if( ! silent) {
                    cout << apptag << "      Inserted " << numInserted << " graphlet count row.\n";
                }
            }
            else {
                cerr << apptag << "ERROR: Inserted " << numInserted << " graphlet count rows.\n";
            }
            W.commit();
            return 0;
        }

    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when trying to save graphlets for amino acid graph to DB: '" << e.what() << "'." << endl;
        return 1;
    }
}

long GraphPTGLPrinter::getAAGraphDatabaseID(string pdbid, string chain_description) const {
    
    long id = -1;
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    
    try {
        //connection C("dbname=vplg user=vplg host=localhost port=5432 connect_timeout=10 password=vplg");
        connection C(connection_string);
        //cout << "      Connected to database '" << C.dbname() << "'.\n";
        work W(C);
        result R = W.exec("SELECT g.aagraph_id FROM plcc_aagraph g WHERE (g.pdb_id = '" + W.esc(pdbid) + "' AND g.chain_description = '" + W.esc(chain_description) + "');");

        //cout << "      Found " << R.size() << " graphs of type " << graphType << " for PDB " << pdbid << " chain " << chain << "." << endl;

        if(R.size() == 1) {
                result::const_iterator r = R.begin();
                id = atol(r[0].c_str());	// cast string to long and return
                return id;
        }

        //for (result::const_iterator r = R.begin(); r != R.end(); ++r) {
        //	cout << r[0].c_str() << endl;
        //}

    } catch (const std::exception &e) {
        cerr << apptag << "ERROR: SQL trouble when trying to retrieve amino acid graph PK from DB: '" << e.what() << "'." << endl;
        return -1;
    }
    
    std::cout << pdbid << std::endl << id << std::endl;
    
    return id;
}

int GraphPTGLPrinter::databaseContainsGraphletsForAAGraph(unsigned long int databaseIDofGraph) const {

    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    
    stringstream ssdbpk;
    ssdbpk << databaseIDofGraph;
    string dbpkStr = ssdbpk.str();

    try {
        connection C(connection_string);
        work W(C);
        result R = W.exec("SELECT g.aa_graphlet_id FROM plcc_aa_graphlets g WHERE g.aagraph_id = " + W.esc(dbpkStr) + ";");

        int count = R.size();
        return count;		
        
    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when checking for graphlet entry for graph in DB: '" << e.what() << "'." << endl;
        return -1;
    }
}

/* Delete the graphlet count entry for a given graph
 * @param <unsigned long int> Graph ID
 * @return <void> */
void GraphPTGLPrinter::deleteAAGraphletCountEntryForGraph(unsigned long int databaseIDofGraph) {
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    
    stringstream ssdbpk;
    ssdbpk << databaseIDofGraph;
    string dbpkStr = ssdbpk.str();

    try {
        connection C(connection_string);
        work W(C);
        result R = W.exec("DELETE FROM plcc_aa_graphlets g WHERE g.aagraph_id = " + W.esc(dbpkStr) + ";");        
        int count = R.affected_rows();
        if( ! silent) {
            cout << apptag << "      Deleted " << count << " graphlet count entries.\n";
        }
        W.commit();                        
    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when checking for graphlet entry for graph in DB: '" << e.what() << "'." << endl;
    }
}

/* Delete the graphlet count entry for a given graph
 * @param <unsigned long int> Graph ID
 * @return <void> */
void GraphPTGLPrinter::deleteCGGraphletCountEntryForGraph(unsigned long int databaseIDofGraph) {
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    
    stringstream ssdbpk;
    ssdbpk << databaseIDofGraph;
    string dbpkStr = ssdbpk.str();

    try {
        connection C(connection_string);
        work W(C);
        result R = W.exec("DELETE FROM plcc_complex_graphlets g WHERE g.complexgraph_id = " + W.esc(dbpkStr) + ";");        
        int count = R.affected_rows();
        if( ! silent) {
            cout << apptag << "      Deleted " << count << " graphlet count entries.\n";
        }
        W.commit();                        
    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when checking for graphlet entry for graph in DB: '" << e.what() << "'." << endl;
    }
}

/* Retrieves the ID of the graph from the ptgl
 * @param <string> PDB-ID
 * @param <string> Chain
 * @param <int> graphtype
 * @return <long> graph ID */
long GraphPTGLPrinter::getCGGraphDatabaseID(string pdbid) const {
    
    long id = -1;
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    
    
    try {
        //connection C("dbname=vplg user=vplg host=localhost port=5432 connect_timeout=10 password=vplg");
        connection C(connection_string);
        //cout << "      Connected to database '" << C.dbname() << "'.\n";
        work W(C);
        result R = W.exec("SELECT g.complexgraph_id FROM plcc_complexgraph g INNER JOIN plcc_protein p ON  p.pdb_id = g.pdb_id WHERE (p.pdb_id = '" + W.esc(pdbid) + "');");

        //cout << "      Found " << R.size() << " graphs of type " << graphType << " for PDB " << pdbid << " chain " << chain << "." << endl;

        if(R.size() == 1) {
                result::const_iterator r = R.begin();
                id = atol(r[0].c_str());	// cast string to long and return
                return id;
        }

        //for (result::const_iterator r = R.begin(); r != R.end(); ++r) {
        //	cout << r[0].c_str() << endl;
        //}

    } catch (const std::exception &e) {
        cerr << apptag << "ERROR: SQL trouble when trying to retrieve complex graph PK from DB: '" << e.what() << "'." << endl;
        return -1;
    }
    
    return id;
}

int GraphPTGLPrinter::saveCGCountsToDatabasePGXX(std::string pdbid, std::string label, std::vector<std::vector<float>> norm_counts, std::vector<float> lab_counts, int time) {
    
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
 
    
    
    std::vector<float> graphlet3CountsNormalized = norm_counts[1];
    std::vector<float> graphlet4CountsNormalized = norm_counts[2];
    std::vector<float> graphlet5CountsNormalized = norm_counts[3];
    
    //TODO: implement function get--counts in graphservice
    //      so that the labeled counts can be written into the database
    
    std::vector<float> cl = lab_counts;

    
    if( ! silent) {
        cout << apptag <<  "    Saving graphlets of pdbid " << pdbid << " complex graph to PostgreSQL database.\n";
    }
    
    // TODO: remove this
    long unsigned int numGraphletsTypesCounted;
    if(label.compare("") != 0) {
        numGraphletsTypesCounted = graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size() + cl.size();
        if( ! silent) {
            cout << apptag << "    Saving " << graphlet3CountsNormalized.size() << " different 3-graphlets, " << graphlet4CountsNormalized.size() << " different 4-graphlets, " << graphlet5CountsNormalized.size() << " different 5-graphlets and " << cl.size() << " different labeled graphlets (" << numGraphletsTypesCounted << " total).\n";           
        }
    }
    else {
        numGraphletsTypesCounted = graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size();
        if( ! silent) {
            cout << apptag << "    Saving " << graphlet3CountsNormalized.size() << " different 3-graphlets, " << graphlet4CountsNormalized.size() << " different 4-graphlets and " << graphlet5CountsNormalized.size() << " different 5-graphlets (" << numGraphletsTypesCounted << " total).\n";   
        }
    }
    
    try {				                                
        long dbpk = getCGGraphDatabaseID(pdbid);

        connection C(connection_string);
        //cout << "      Connected to database '" << C.dbname() << "'.\n";
        work W(C);

        // The relevant table in the database is called 'plcc_complex_graphlets'. It get created by the PLCC application using the following line of code:
        //   doInsertQuery("CREATE TABLE plcc_complex_graphlets (graphlet_id serial primary key, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, graphlet_counts int[55] not null);");
        // referenced tables:
        //   doInsertQuery("CREATE TABLE plcc_graph (graph_id serial primary key, chain_id int not null references plcc_chain ON DELETE CASCADE, graph_type int not null references plcc_graphtypes, graph_string_gml text, graph_string_kavosh text, graph_string_dotlanguage text, graph_string_plcc text, graph_image_png text, graph_image_svg text, graph_image_pdf text, sse_string text, graph_containsbetabarrel int DEFAULT 0);");
        //   doInsertQuery("CREATE TABLE plcc_graphtypes (graphtype_id int not null primary key,  graphtype_text text not null);");
        //   doInsertQuery("CREATE TABLE plcc_chain (chain_id serial primary key, chain_name varchar(2) not null, mol_name varchar(200) not null, organism_scientific varchar(200) not null, organism_common varchar(200) not null, pdb_id varchar(4) not null references plcc_protein ON DELETE CASCADE, chain_isinnonredundantset smallint DEFAULT 1);");
        //   doInsertQuery("CREATE TABLE plcc_protein (pdb_id varchar(4) primary key, header varchar(200) not null, title varchar(400) not null, experiment varchar(200) not null, keywords varchar(400) not null, resolution real not null);");


        // Let's insert the graphlet counts into the DB.
        if(dbpk <= 0) {
            cerr << apptag << "ERROR: ID of complex graph for " << pdbid << " not found in the database, cannot assign graphlets to it. Skipping.\n";
            return 1;
        }
        else {
            if( ! silent) {
                cout << apptag << "      Found complex graph in database, ID is " << dbpk << ".\n";  
            }
            
            int numGraphletCountsAlreadyInDatabase = databaseContainsGraphletsForCGGraph(dbpk);
            if(numGraphletCountsAlreadyInDatabase == 0) {
                if( ! silent) {
                    cout << apptag << "      Database does not contain graphlet count entries for this complex graph, ok.\n";
                }
            }
            else {
                if( ! silent) {
                    cout << apptag << "      Database already contains graphlet count entry for this complex graph, trying to delete old entry.\n";
                }
                deleteCGGraphletCountEntryForGraph(dbpk);
            }
            
            stringstream ssdbpk;
            ssdbpk << dbpk;
            string dbpkStr = ssdbpk.str();
            
            // collect and insert graphlets
            // DB table: doInsertQuery("CREATE TABLE " + tbl_graphletcount + " (graphlet_id serial primary key, graph_id int not null references " + tbl_proteingraph + " ON DELETE CASCADE, graphlet_counts int[55] not null);");
             string strVal;
            
            std::vector<float> allGraphletcounts;

            allGraphletcounts.reserve(graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size() + cl.size());
            allGraphletcounts.insert(allGraphletcounts.end(), graphlet3CountsNormalized.begin(), graphlet3CountsNormalized.end());
            allGraphletcounts.insert(allGraphletcounts.end(), graphlet4CountsNormalized.begin(), graphlet4CountsNormalized.end());
            allGraphletcounts.insert(allGraphletcounts.end(), graphlet5CountsNormalized.begin(), graphlet5CountsNormalized.end());
            allGraphletcounts.insert(allGraphletcounts.end(), cl.begin(), cl.end());

            
            // check stuff
            
            int numExpected = 64;
            if(cl.empty()) {
                numExpected = 64 - 38;
            }
            
            
            if(allGraphletcounts.size() != numExpected) {
                cerr << apptag << "ERROR: Expected counts for " << numExpected << " different graphlets, but found " << allGraphletcounts.size() << ". Cannot write graphlet counts to database.\n";
                return 1;
            }
            
            
            float val;
            string query = "INSERT INTO plcc_complex_graphlets (complexgraph_id, runtime_secs, complex_graphlet_counts) VALUES (" + W.esc(dbpkStr) + ", " + std::to_string(time) + ", '{";
            for (int i = 0; i < allGraphletcounts.size(); i++) {
                std::stringstream ssVal;
                val = allGraphletcounts[i];
                
                // check for NaN values (resulting from division by zero during normalization, when the total number of graphlets found is 0)
                if(val != val) {    // proper way to check for NaN in C++ it seems
                    // no values will be added, resulting in an empty array {}
                    //continue; 
                    break;      // if one value is NaN, all others also are NaN anyway, so break instead of continue.
                }
                
                if(val < 0.0f || val > 1.0f) {
                    cout << apptag << "WARNING: Graphlet count for graphlet #" << i << " is '" << val << "', but should be normalized between 0.0 and 1.0.\n";
                }
                ssVal << std::fixed << std::setprecision(4) << val;
                strVal = ssVal.str();
                
                
                //cout << "      Adding value '" << strVal << "'.\n";
                query += W.esc(strVal);
                if(i < allGraphletcounts.size() - 1) {
                    query += ", ";
                }
            }
            
            
//            // fill the remaining fields if there is no data on labeled graphlets
//            if( ! withLabeled) {
//                float valueForMissing = -1.0f;
//                cout << apptag << "WARNING: Writing graphlets to database but missing data on labeled graphlets, setting count " << valueForMissing << " for them.\n";
//                int numMissingValues = (55  - allGraphletcounts.size());                
//                for (int i = 0; i < numMissingValues; i++) {
//                    std::stringstream ssVal;
//                    ssVal << std::fixed << std::setprecision(4) << valueForMissing;
//                    strVal = ssVal.str();
//                    query += W.esc(strVal);
//                    if(i < numMissingValues - 1) {
//                        query += ", ";
//                    }
//                }                
//            }
            
            
            query += "}');";
            //cout << "      SQL query is '" << query << "'.\n";
            pqxx::result res = W.exec(query);
            int numInserted = res.affected_rows();
            if(numInserted == 1) {
                if( ! silent) {
                    cout << apptag << "      Inserted " << numInserted << " graphlet count row.\n";
                }
            }
            else {
                cerr << apptag << "ERROR: Inserted " << numInserted << " graphlet count rows.\n";
            }
            W.commit();
            return 0;
        }

    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when trying to save graphlets for complex graph to DB: '" << e.what() << "'." << endl;
        return 1;
    }
}

int GraphPTGLPrinter::databaseContainsGraphletsForCGGraph(unsigned long int databaseIDofGraph) const {

    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
    
    stringstream ssdbpk;
    ssdbpk << databaseIDofGraph;
    string dbpkStr = ssdbpk.str();

    try {
        connection C(connection_string);
        work W(C);
        result R = W.exec("SELECT g.complex_graphlet_id FROM plcc_complex_graphlets g WHERE g.complexgraph_id = " + W.esc(dbpkStr) + ";");

        int count = R.size();
        return count;		
        
    } catch (const std::exception &e) {
        cerr << apptag << "SQL trouble when checking for graphlet entry for graph in DB: '" << e.what() << "'." << endl;
        return -1;
    }
}