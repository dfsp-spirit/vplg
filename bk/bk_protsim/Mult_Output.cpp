/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include <sstream>
#include <list>
#include "Mult_Output.h"

typedef std::list<EdgeDescriptor> c_edge;

typedef std::list<c_edge> c_clique;
typedef std::list<VertexDescriptor_p> s_clique_p;
typedef std::list<VertexDescriptor> s_clique;

/* default constructor*/
Mult_Output::Mult_Output() : DIR(""){
    streams = std::vector<std::ofstream*>(0);
    counts = std::vector<unsigned int>(0);
};
   
/* Constructor for the Mult_Output class. All outputs of the instance will be saved to files in the specified directory.*/
Mult_Output::Mult_Output(std::string directory) : DIR(directory){
    streams = std::vector<std::ofstream*>(10);
    counts = std::vector<unsigned int>(10);
    std::stringstream ss;
    for (int i = 0; i < 10; ++i) {
        ss << this->DIR << "\\" << i+1 << ".txt";
        streams[i] = new std::ofstream();
        streams[i]->open(ss.str(), std::ofstream::trunc);
        streams[i]->close();
        streams[i]->open(ss.str(), std::ofstream::app);
        ss.clear();
        counts[i] = 0;
    }
};  

Mult_Output::~Mult_Output() {
    for (std::ofstream* fsp : streams) {
        if (fsp) { fsp->close(); delete fsp; }
    }
}



/*
 * Manages the output operations
 */
void Mult_Output::out(c_clique& complex, const std::vector<Graph*>& graphs){
    short num = complex.size()-1;
    
    //expand the vectors if no file for the needed length exists yet
    while (this->streams.size() < num){  
        streams.push_back(new std::ofstream());
        counts.push_back(0);
        std::stringstream ss;
        ss << this->DIR << "\\" << num+1 << ".txt";
        streams.back()->open(ss.str(), std::ofstream::trunc);
        streams.back()->close();
        streams.back()->open(ss.str(), std::ofstream::app);
    }//end expand vectors
    
    //new output, one line = one graph, listing all vertices of that graph in the complex clique, in ascending order
    int pos = 0;
    *streams[num] << "{\n";
    std::stringstream ss;
    while (!complex.front().empty()) { //iterate over each layer of the complex clique
        *streams[num] << "\t\"g" << pos+1 <<"\" : [";
        std::set<VertexDescriptor> s;
        for (c_edge& ce : complex) { //iterator over the complex edges of the complex clique
            s.insert((*graphs[pos])[boost::source(ce.front(),(*graphs[pos]))].id);
            s.insert((*graphs[pos])[boost::target(ce.front(),(*graphs[pos]))].id);
            ce.pop_front();
        }
        auto e = --s.end();
        for (auto i = s.begin(); i != e; ++i) {
            *streams[num] << *i << ",";
            ss << *i;
        }
        *streams[num] << *e << "],\n";
        ss << *e;
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
void Mult_Output::filter_iso() {
    int num = 0;
    std::stringstream ss;
    for (int i = 0; i<= streams.size(); ++i) {
        streams[i]->close(); 

        ss << this->DIR << "\\" << num+1 << ".txt";
        std::string oldF = ss.str();
        ss << ".filtered";
        std::string newF = ss.str();

        counts[i] = filter_copy(oldF, newF);
        std::remove(oldF.c_str());
        std::rename(newF.c_str(), oldF.c_str());
        streams[i]->open(oldF);
    }
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
    
    int b = 0;
    int e = 0;
    std::string id;
    std::set<std::string> idents;
    
    while (ifs.good()) {
        std::string str;
        std::getline(ifs,str);
        
        
         if (str == "{") {
            b = ifs.tellg();
        } else if (str == "}") {
            if (idents.insert(id).second) {
                ifs.seekg(b-2);
                while(ifs.tellg() != e) {
                    std::cout << (char) ifs.get();
                }
                std::cout << "\n}\n\n";
            }
            
            
        } else if (str.substr(0,7) == "\t\"iden\""){
            id = str.substr(11,str.size()-12);
            
        } else {
            e = ifs.tellg();
            e -= 2;
        }
    }
    
    if(!ifs.eof()) { exit(1);}

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