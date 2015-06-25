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
    std::string sub_str = "";
    
    
    
    for (int i = 0; i < vec.size(); i++) {
        
        //remove zeros from the end
        str_number = std::to_string(vec[i]);
        str_number.erase ( str_number.find_last_not_of('0') + 1, std::string::npos );
 
        
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

std::string JSON_printer::print_int_vec_vector(std::vector<vector<int>> vec) {
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