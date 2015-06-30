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
    
    std::vector<float> cl; 
    
        // all possible labeling of g1 graphlet (triangle) concerning the symmetry
    std::string g1_vertex_patterns[] = { "HHH","HHE","HEE","EEE" };
    
    // all possible labeling of g2 graphlet (2-path) concerning the symmetry
    std::string g2_vertex_patterns[] = { "HHH","HHE","EHE","HEH","HEE","EEE" };
    
    // bio-motivated labeling of graphlets
    // currently implemented are the beta-alpha-beta and beta-beta-beta motifs
    // NOTE: also check if its composing vertices are adjacent
    std::string g2_bio_patterns[]    = { "EaHaE",    // beta-alpha-beta motif
                                    "EaEaE" };  // beta-beta-beta motif
    
        // all possible labeling of g6 graphlet (3-path) concerning the symmetry
    std::string g6_vertex_patterns[] = { "HHHH","HHHE",       "EHHE",
                                    "HHEH","HHEE","EHEH","EHEE",
                                    "HEEH","HEEE",       "EEEE"};
    std::string g6_bio_patterns[]    = { "EaEaEaE",  // greek key
                                    "EpEpEpE"}; // 4-beta-barrel, non-adjacent
    // all possible labeling of edge (2-vertex path of length 1) graphlet (triangle) concerning the symmetry
    std::string g0_vertex_patterns[] = { "HH", "EH", "HE", "EE" };

    
    /*
     * NOTE the correspondence 
     *   lcount[0..3]   := g1_vertex_patterns[0..3] 
     *   lcount[4..9]   := g2_vertex_patterns[0..6]
     *   lcount[10..11] := g2_bio_patterns[0..1]
     *   lcount[12..21] := g6_vertex_patterns[0..9]
     *   lcount[22,23] := g6_bio_patterns[0,1]
     */

    
    
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
            if(withLabeled) {
                allGraphletcounts.reserve(graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size() + cl.size());
                allGraphletcounts.insert(allGraphletcounts.end(), graphlet3CountsNormalized.begin(), graphlet3CountsNormalized.end());
                allGraphletcounts.insert(allGraphletcounts.end(), graphlet4CountsNormalized.begin(), graphlet4CountsNormalized.end());
                allGraphletcounts.insert(allGraphletcounts.end(), graphlet5CountsNormalized.begin(), graphlet5CountsNormalized.end());
                allGraphletcounts.insert(allGraphletcounts.end(), cl.begin(), cl.end());
            }
            else {
                allGraphletcounts.reserve(graphlet3CountsNormalized.size() + graphlet4CountsNormalized.size() + graphlet5CountsNormalized.size());
                allGraphletcounts.insert(allGraphletcounts.end(), graphlet3CountsNormalized.begin(), graphlet3CountsNormalized.end());
                allGraphletcounts.insert(allGraphletcounts.end(), graphlet4CountsNormalized.begin(), graphlet4CountsNormalized.end());
                allGraphletcounts.insert(allGraphletcounts.end(), graphlet5CountsNormalized.begin(), graphlet5CountsNormalized.end());
            }
            
            // check stuff
            int numExpected = 55;
            if(! withLabeled) {
                numExpected = 55 - 26;
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
            
            
            // fill the remaining fields if there is no data on labeled graphlets
            if( ! withLabeled) {
                float valueForMissing = -1.0f;
                cout << apptag << "WARNING: Writing graphlets to database but missing data on labeled graphlets, setting count " << valueForMissing << " for them.\n";
                int numMissingValues = (55  - allGraphletcounts.size());                
                for (int i = 0; i < numMissingValues; i++) {
                    std::stringstream ssVal;
                    ssVal << std::fixed << std::setprecision(4) << valueForMissing;
                    strVal = ssVal.str();
                    query += W.esc(strVal);
                    if(i < numMissingValues - 1) {
                        query += ", ";
                    }
                }                
            }
            
            
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