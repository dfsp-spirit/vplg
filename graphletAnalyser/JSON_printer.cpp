/* 
 * 
#include "JSON_printer.h"
#include "JSON_printer.h"
 * File:   JSON_printer.cpp
 * Author: ben
 *
 * Created on June 25, 2015, 3.34 PM
 * 
 *
 */

#include "JSON_printer.h"

std::string JSON_printer::print_float_vector(std::vector<float> vec) {
    
    std::string out_string = "[";
    std::string str_number = "";
    
    
    
    for (int i = 0; i < vec.size(); i++) {
        
        //remove zeros from the end
        str_number = std::to_string(vec[i]);
        str_number.erase ( str_number.find_last_not_of('0') + 1, std::string::npos );
        if (str_number.back() == '.') {
            str_number = str_number + "0";
        }
 
        
        out_string = out_string + str_number;
        
        //add commas, when appropriate
        if (i != vec.size() -1) {
            out_string = out_string + ",";
            
        }
    }
    
    out_string.append("]");
    
    return out_string;
}

std::string JSON_printer::print_float_vec_vector(std::vector<std::vector<float>> vec) {
    std::string out_string = "[";
    
    for (int i = 0; i < vec.size(); i++) {
        out_string = out_string + print_float_vector(vec[i]);
        if (i  != vec.size() - 1) {
            out_string = out_string + ",";
        }
        
        
    }
    out_string.append("]");
    
    return out_string;
}

std::string JSON_printer::print_int_vector(std::vector<int> vec) {
    std::string out_string = "[";
    
    for (int i = 0; i < vec.size(); i++) {
        
        out_string = out_string + std::to_string(vec[i]);
        if (i != vec.size() -1) {
            out_string = out_string + ",";
            
        }
    }
    
    out_string.append("]");
    
    return out_string;
    
}

std::string JSON_printer::print_int_vec_vector(std::vector<std::vector<int>> vec) {
    std::string out_string = "[";
    
    for (int i = 0; i < vec.size(); i++) {
        out_string = out_string + print_int_vector(vec[i]);
        if (i  != vec.size() - 1) {
            out_string = out_string + ",";
        }
        
        
    }
    out_string.append("]");
    
    return out_string;
    
}

std::string JSON_printer::print_vectors_with_info(std::string graph_name, int num_nodes, int num_edges, std::vector<std::vector<float>> rel_counts, std::vector<std::vector<int> > abs_counts) {
    std::vector<int> two_graphletsABS = abs_counts[0];
    std::vector<int> three_graphletsABS = abs_counts[1];
    std::vector<int> four_graphletsABS = abs_counts[2];
    std::vector<int> five_graphletsABS = abs_counts[3];
    
    std::vector<float> two_graphletsNORM = rel_counts[0];
    std::vector<float> three_graphletsNORM = rel_counts[1];
    std::vector<float> four_graphletsNORM = rel_counts[2];
    std::vector<float> five_graphletsNORM = rel_counts[3];
    
    std::string quot_marks = "\"";
    
    
    std::string out_str = "{ \"Graphname\" : " + quot_marks + graph_name + quot_marks + ", \"Number of vertices\" : " +  std::to_string(num_nodes) + ", ";
    out_str = out_str + "\"Number of edges\" : " + std::to_string(num_edges) + ", ";
    std::string abs_str = "\"Absolute Counts\" : { ";
    std::string norm_str = "\"Normalized Counts\" : { ";
    
    if (two_graphletsABS.empty()) {
        abs_str += "\"2-graphlets\" : null, ";
    } else {
        abs_str += "\"2-graphlets\" : " + print_int_vector(two_graphletsABS) + ", ";
    }
    if (three_graphletsABS.empty()) {
        abs_str += "\"3-graphlets\" : null, ";
    } else {
        abs_str += "\"3-graphlets\" : " + print_int_vector(three_graphletsABS) + ", ";
    }
    if (four_graphletsABS.empty()) {
        abs_str += "\"4-graphlets\" : null, ";
    } else {
        abs_str += "\"4-graphlets\" : " + print_int_vector(four_graphletsABS) + ", ";
    }
    if (five_graphletsABS.empty()) {
        abs_str += "\"5-graphlets\" : null}";
    } else {
        abs_str += "\"5-graphlets\" : " + print_int_vector(five_graphletsABS) + "}, ";
    }
    
    
    out_str += abs_str;
    
    
    if (two_graphletsNORM.empty()) {
        norm_str += "\"2-graphlets\" : null, ";
    } else {
        norm_str += "\"2-graphlets\" : " + print_float_vector(two_graphletsNORM) + ", ";
    }
    if (three_graphletsNORM.empty()) {
        norm_str += "\"3-graphlets\" : null, ";
    } else {
        norm_str += "\"3-graphlets\" : " + print_float_vector(three_graphletsNORM) + ", ";
    }
    if (four_graphletsNORM.empty()) {
        norm_str += "\"4-graphlets\" : null, ";
    } else {
        norm_str += "\"4-graphlets\" : " + print_float_vector(four_graphletsNORM) + ", ";
    }
    if (two_graphletsNORM.empty()) {
        norm_str += "\"5-graphlets\" : null}";
    } else {
        norm_str += "\"5-graphlets\" : " + print_float_vector(five_graphletsNORM) + "}";
    }
    
    out_str += norm_str + "}";
    
    return out_str;
}

std::string JSON_printer::print_labeled_counts(std::string graph_name, int num_nodes, int num_edges, std::unordered_map<std::string, std::vector<int>> map) {
    
    
    std::string out_str = "{ \"Graphname\" : " + graph_name + ", \"Number of vertices\" : " +  std::to_string(num_nodes) + ", ";
    out_str = out_str + "\"Number of edges\" : " + std::to_string(num_edges) + ", ";
    out_str += "\"Labeled Counts \" : { ";
    
    
    
    for (auto k : map) {
        
        
        out_str += "\"" + k.first + "\" : ";
        out_str += print_int_vector(k.second);
        out_str += ", ";
                
    }
    out_str.erase(out_str.size() - 3, 2);
    out_str += "}}";
    
    
    return out_str;
}