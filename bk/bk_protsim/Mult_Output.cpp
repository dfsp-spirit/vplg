/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include <sstream>
#include <list>
#include "Mult_Output.h"
#include <sstream>
#include <iostream>

typedef std::list<EdgeDescriptor> c_edge;

typedef std::list<c_edge> c_clique;
typedef std::list<VertexDescriptor_p> s_clique_p;
typedef std::list<VertexDescriptor> s_clique;

/* default constructor*/
Mult_Output::Mult_Output() : DIR(""){
    this->streams = std::vector<std::ofstream*>(0);
    this->counts = std::vector<unsigned int>(0);
}
   
/* Constructor for the Mult_Output class. All outputs of the instance will be saved to files in the specified directory.*/
Mult_Output::Mult_Output(std::string directory) : DIR(directory){
    this->streams = std::vector<std::ofstream*>(10);
    this->counts = std::vector<unsigned int>(10);
    std::stringstream ss;
    for (int i = 0; i < 10; ++i) {
        ss << this->DIR << "/" << i+1 << ".txt";
        streams[i] = new std::ofstream();
        streams[i]->open(ss.str(), std::ofstream::trunc);
        ss.clear();
        ss.str("");
        counts[i] = 0;
    }
}  

Mult_Output::~Mult_Output() {
    for (std::ofstream* fsp : streams) {
        if (fsp) { fsp->close(); delete fsp; }
    }
}



/*
 * Manages the output operations
 */
void Mult_Output::out(c_clique& complex, const std::vector<Graph*>& graphs){
    unsigned short num = complex.size()-1;
   
    //expand the vectors if no file for the needed length exists yet
    while (this->streams.size() <= num){  
        streams.push_back(new std::ofstream());
        counts.push_back(0);
        std::stringstream ss;
        ss << this->DIR << "/" << num+1 << ".txt";
        streams.back()->open(ss.str(), std::ofstream::trunc);
    }//end expand vectors
      
    //new output, one line = one graph, listing all vertices of that graph in the complex clique, in ascending order
    int pos = 0;
    *streams[num] << "{\n";
    std::stringstream ss;
    while (!complex.front().empty()) { //iterate over each layer of the complex clique
        *streams[num] << "\t\"graph "<< (*graphs[pos])[boost::graph_bundle].properties["pdb_id"] <<"\" : [";
        std::set<EdgeDescriptor> s;
        for(c_edge& ce : complex){
            s.insert(ce.front());
            ce.pop_front();
        }
        
        
        auto e = --s.end();
        auto iter = s.begin();
        for (auto iter = s.begin(); iter != e; ++iter) {
            *streams[num] << "(" << (*graphs[pos])[boost::source(*iter,(*graphs[pos]))].id +1;
            *streams[num] << "," << (*graphs[pos])[boost::target(*iter,(*graphs[pos]))].id +1<< "),";
        }
        *streams[num] << "(" << (*graphs[pos])[boost::source(*e,(*graphs[pos]))].id+1;
        *streams[num] << "," << (*graphs[pos])[boost::target(*e,(*graphs[pos]))].id+1 << ")],\n"; //for the last element dont include the "," but end the collection
        ss << *iter;
        pos += 1;
    }
    *streams[num] << "\t\"iden\" : \"" << ss.rdbuf() << "\"\n";
    *streams[num] << "}\n\n";
    counts[num] += 1; 
}

/*
 * Function to remove duplicate results in the output files.
 * Should be called after all results have been passed to the out funktion.
 * This will require additional disk space as each file will be temporarilly duplicated.
 */
int Mult_Output::filter_iso() {
    std::stringstream ss;
    int total = 0; 
    for (int i = 0; i < streams.size(); ++i) {
        streams[i]->close(); 
        ss << this->DIR << "/" << i+1 << ".txt";
        std::string oldF = ss.str();
        ss << ".filtered";
        std::string newF = ss.str();
        
        counts[i] = filter_copy(oldF, newF);
        total += counts[i];
        std::remove(oldF.c_str());
        std::rename(newF.c_str(), oldF.c_str());
        streams[i]->open(oldF, std::ofstream::app);
        ss.clear();
        ss.str("");
    }
    return total;
}

/*
 * Does the filters one file for duplicates.
 * reads the file at the first path and writes to the second.
 * returns the new number of cliques after the filter.
 */
int Mult_Output::filter_copy(std::string& in, std::string& out) {
    std::ifstream ifs;
    std::ofstream ofs;
    ifs.open(in, std::ifstream::in);
    ofs.open(out, std::ofstream::trunc);
    
    std::stringstream ss;
    std::string delim;
    
    std::string id;
    std::set<std::string> idents;
    while (ifs.good()) {
        std::string token;
        ifs >> token;
        if (token == "{") {
            ss << "{";
            delim = "";
        } else if (token == "}") {
            ss << "\n}\n\n";
            if (idents.insert(id).second) {
                ofs << ss.rdbuf();
            }
            ss.clear();
            ss.str("");
        } else if (token == "\"iden\"") {
            ifs >> token >> token;
            id = token;
        } else {
            ss << delim << "\n\t" << token;
            delim = ";";
            std::getline(ifs,token);
            ss << token.substr(0, token.size()-1);
        }
    }
    if(!ifs.eof()) { std::cout << "shit went down"<< std::endl;exit(1);}

    ifs.close();
    ofs.close();
    return idents.size();
}

/*
 * Returns a vector with the number of cliques of each size. 
 * The value at possiotion i is the number of cliques with size i+1.
 */
std::vector<unsigned int> Mult_Output::distribution() {
    return this->counts;
}