/* 
 * File:   JSON_printer.h
 * Author: ben
 *
 * Created on June 25, 2015, 1:16 PM
 */

#ifndef JSON_PRINTER_H
#define	JSON_PRINTER_H


/* printing vectors in JSON format */
class JSON_printer {
    
    
    
    public:
        std::string print_int_vector(std::vector<int>);
        std::string print_int_vec_vector(std::vector<vector<int>>);
        std::string print_float_vector(std::vector<float>);
        std::string print_float_vec_vector(std::vector<std::vector<float>>);
        std::string print_vectors_with_info(std::string graph_name, std::string graphlet_info, 
                                           std::vector<std::vector<float>> rel_counts, 
                                           std::vector<std::vector<int>> abs_counts);
    
    
};

#endif	/* JSON_PRINTER_H */

