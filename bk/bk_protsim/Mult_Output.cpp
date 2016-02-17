/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include <sstream>
#include <list>


#include "Mult_Output.h"

Mult_Output::Mult_Output() : DIR(""){
    streams = std::vector<std::ofstream*>(0);
    counts = std::vector<unsigned int>(0);
};
        
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
void Mult_Output::out(const std::list<std::list<EdgeDescriptor>>& complex, const std::vector<Graph*>& graphs){
    short num = complex.size()-1;
    while (this->streams.size() < num){          //if necessary, expand the vectors to include new the appropriate files
        streams.push_back(new std::ofstream());
        std::stringstream ss;
        ss << this->DIR << "\\" << num+1 << ".txt";
        streams.back()->open(ss.str(), std::ofstream::trunc);
        streams.back()->close();
        streams.back()->open(ss.str(), std::ofstream::app);
    }//end expand vectors
    for (const std::list<EdgeDescriptor>& ce : complex){
        int pos = 0;
        for (auto i = ce.begin(); i != ce.end(); ++i, ++pos) {                                           //for each simple edge
                    *streams[num] << (*graphs[pos])[boost::source(*i,(*graphs[pos]))].id+1 <<"-"<< (*graphs[pos])[boost::target(*i,(*graphs[pos]))].id+1 << ";";   
        }
        *streams[num] << std::endl;
    }
    *streams[num] << std::endl;
    streams[num]->flush(); 
}


std::vector<unsigned int> Mult_Output::distribution() {
    return this->counts;
}