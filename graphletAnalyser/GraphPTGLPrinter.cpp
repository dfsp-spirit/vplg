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
    ProteinGraphService service_tmp;

    ProteinGraphService service();
};


GraphPTGLPrinter::GraphPTGLPrinter(Graph g) {
    ProteinGraphService service(g);
};

/*
 * prints vertices and their sse types to a string
 * CAUTION: only applicable to ptgl protein graphs */
std::string GraphPTGLPrinter::printVertices() {
    VertexIterator vi, vi_end, next; // create vertex iterator objects
    
    Graph g = service.getGraph();
    
    tie(vi, vi_end) = vertices(g); // that belong to the graph
    stringstream sstream; // to be saved in a string
    
    sstream << "Iterate over the vertices and print their properties:\n";
    for (next = vi; vi != vi_end; vi = next) { // iterate over the vertices
        ++next;
        // and print them with their sse type
        sstream << "  vertex " << setw(2) << *vi << " has  sse_type = " << g[*vi].properties["sse_type"] << endl;
    }
    sstream << endl;
    
    return sstream.str();
};

/*
 * print edges with their spatial (protein related) relationships to a string
 * CAUTION: only applicable to ptgl protein graphs */
std::string GraphPTGLPrinter::printEdges() {
    bool formatted = true;
    EdgeIterator ei, ei_end;
    stringstream sstream;
    
    Graph g = service.getGraph();
    
    sstream << "Iterate over the edges and print their properties:\n";
    if (formatted) {
        for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) { // iterate over edges
            sstream << "  edge ("
                    << setw(2) << g[*ei].source
                    << ","
                    << setw(2) << g[*ei].target
                    << ") is "
                    << g[*ei].properties["spatial"] << endl; // add their spatial relationships
        }       
    } else {
        for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) {
            sstream << "  edge number " << *ei
                    << " has: source = " << g[*ei].source
                    << ", target = " << g[*ei].target
                    << ", spatial = " << g[*ei].properties["spatial"] << endl;
        }
    }
    sstream << endl;
    
    return sstream.str();    
};

/* Print all vertices, then all edges, and then then all adjacent vertices for
 * each vertex
 * CAUTION: only applicable to ptgl protein graphs */
void GraphPTGLPrinter::printGraphInfo() {
        stringstream sstream;

    sstream << "[GRAPH INFO]\n"
            << printVertices()
            << printEdges() 
            << printAdjacentAll();
    
    cout << sstream.str();

};

/* Get the vertices with their sse types, aa residues, and the number of residues,
 * and the edges with their protein-related spatial relationships
 */
string GraphPTGLPrinter::printGraphString() {
    // define iterators
    VertexIterator vi, vi_end, next;
    EdgeIterator ei, ei_end;
    
    Graph g = service.getGraph();
    
    tie(vi, vi_end) = vertices(g);
    
    // set up the string stream
    stringstream sstream;
    
    // add vertices with their info
    sstream << "Vertices:\n";
    for (next = vi; vi != vi_end; vi = next) {
        ++next;
        sstream << "  Vertex " << setw(2) << *vi << ": id=" << g[*vi].id << ", label=" << g[*vi].label << ", sse_type=" << g[*vi].properties["sse_type"] << ", num_residues=" << g[*vi].properties["num_residues"] << ", aa_sequence=" << g[*vi].properties["aa_sequence"] << "." << endl;
    }
    sstream << endl;
    
    // add edges with their info
    sstream << "Edges:\n";
    for (tie(ei, ei_end) = edges(g); ei != ei_end; ++ei) {
        sstream << "  edge ("
                << setw(2) << g[*ei].source
                << ","
                << setw(2) << g[*ei].target
                << ") is "
                << g[*ei].properties["spatial"] << endl;
    }
    
    return sstream.str();
};


/** 
 * Uses the pgxx library to connect to a postgresql database server.
 * For this, you will need to link against lpq and lpqxx and include the proper headers.
 * 
 * 
 * @param <vector<string>> graph_properties:    contains the following elements:
 *                                              0: pdbid
 *                                              1: chain
 *                                              2: label - for which labeled graphlets were searched
 *                                              
 *
 */
int GraphPTGLPrinter::saveCountsToDatabasePGXX() {
    
    Database db = Database::getInstance();
    string connection_string = db.get_connect_string();
 
    
    std::string pdbid = service.getPdbid();
    std::string chain = service.getChainID();
    std::string label = service.get_label();
    std::string graphtypestr = service.getGraphTypeString();
    int graphtype = service.getGraphTypeInt(graphtypestr);
    std::vector<std::vector<float>> norm_counts = service.get_norm_counts();
    std::vector<float> graphlet3CountsNormalized = norm_counts[1];
    std::vector<float> graphlet4CountsNormalized = norm_counts[2];
    std::vector<float> graphlet5CountsNormalized = norm_counts[3];
    std::string id = service.get_graphlet_identifier();
    std::vector<std::string> patterns = service.get_patterns();
    std::unordered_map<std::string, std::vector<float>> labeled_counts_map = service.get_labeled_norm_counts(id, patterns);
    
    //TODO: implement function get--counts in graphservice
    //      so that the labeled counts can be written into the database
    
    std::vector<float> cl = service.get_norm_ptgl_counts();
    
    
    
    
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
        long dbpk = getGraphDatabaseID(pdbid, chain, graphtype);

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
            cerr << apptag << "ERROR: ID of graph not found in the database, cannot assign graphlets to it. Skipping.\n";
            return 1;
        }
        else {
            if( ! silent) {
                cout << apptag << "      Found graph in database, ID is " << dbpk << ".\n";  
            }
            
            int numGraphletCountsAlreadyInDatabase = databaseContainsGraphletsForGraph(dbpk);
            if(numGraphletCountsAlreadyInDatabase == 0) {
                if( ! silent) {
                    cout << apptag << "      Database does not contain graphlet count entries for this graph, ok.\n";
                }
            }
            else {
                if( ! silent) {
                    cout << apptag << "      Database already contains graphlet count entry for this graph, trying to delete old entry.\n";
                }
                deleteGraphletCountEntryForGraph(dbpk);
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
            int numExpected = 67;
            if(cl.empty()) {
                numExpected = 67 - 38;
            }
            
            
            if(allGraphletcounts.size() != numExpected) {
                cerr << apptag << "ERROR: Expected counts for " << numExpected << " different graphlets, but found " << allGraphletcounts.size() << ". Cannot write graphlet counts to database.\n";
                return 1;
            }
            
            float val;
            string query = "INSERT INTO plcc_graphlets (graph_id, graphlet_counts) VALUES (" + W.esc(dbpkStr) + ", '{";
            for (int i = 0; i < allGraphletcounts.size(); i++) {
                std::stringstream ssVal;
                val = allGraphletcounts[i];
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
        cerr << apptag << "SQL trouble when trying to save graphlets to DB: '" << e.what() << "'." << endl;
        return 1;
    }
}

long GraphPTGLPrinter::getGraphDatabaseID(string pdbid, string chain, int graphType) {
    
    long id = -1;
    
    stringstream ssgt;
    ssgt << graphType;
    string graphTypeStr = ssgt.str();
    
    try {
        connection C("dbname=vplg user=vplg host=localhost port=5432 connect_timeout=10 password=vplg");
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
        cerr << apptag << "ERROR: SQL trouble when trying to retrieve graph PK from DB: '" << e.what() << "'." << endl;
        return -1;
    }
    
    return id;
}

int GraphPTGLPrinter::databaseContainsGraphletsForGraph(unsigned long int databaseIDofGraph) {

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
        cerr << apptag << "SQL trouble when checking for graphlet entry for graph in DB: '" << e.what() << "'." << endl;
        return -1;
    }
}


/**
 * Saves the graphlet counts in NOVA format to the NOVA output file. If the file does already exist,
 * the data for this graph gets appended to it. This format is basically CSV.
 * See http://www.bioinformatik.uni-frankfurt.de/tools/nova/ for more info on NOVA.
 * @param withLabeled whether to write labeled graphlet info as well
 */
void GraphPTGLPrinter::saveCountsInNovaFormat(std::vector<std::vector<float>> norm_counts, bool withLabeled) {
    ofstream countsNovaFormatFile;
    const string countsNovaFormatFileName = output_path + "countsNovaFormat.csv";
    int pos;
    int numberOfGraphlets;
    
    std::vector<float> graphlet3CountsNormalized = norm_counts[1];
    std::vector<float> graphlet4CountsNormalized = norm_counts[2];
    std::vector<float> graphlet5CountsNormalized = norm_counts[3];
    std::vector<float> cl;
    std::string graphName = service.get_label();

    countsNovaFormatFile.open(countsNovaFormatFileName.c_str(), std::ios_base::app);    
    if (!countsNovaFormatFile.is_open()) {
        cout << apptag << "ERROR: could not open counts file in NOVA format.\n";
    } else {
        pos = countsNovaFormatFile.tellp();
        if (pos == 0) {
            countsNovaFormatFile << "ID,Group";
            
            if (withLabeled){ cl = norm_counts[4]; numberOfGraphlets = graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size() + cl.size(); }                      
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
        if (withLabeled) {
            for (int i = 0; i < cl.size(); i++) countsNovaFormatFile << "," << cl[i];
        }
        countsNovaFormatFile << "\n";
        countsNovaFormatFile.close();
    
        if( ! silent) {
            cout << apptag << "    The counts were added to the \"" << countsNovaFormatFileName << "\".\n"; 
        }
    }
}

void GraphPTGLPrinter::deleteGraphletCountEntryForGraph(unsigned long int databaseIDofGraph) {
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
        cerr << apptag << "SQL trouble when checking for graphlet entry for graph in DB: '" << e.what() << "'." << endl;
    }
}
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
