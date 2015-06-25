/* 
 * File:   JSON_printer.h
 * Author: ben
 *
 * Created on June 25, 2015, 1:16 PM
 */

#ifndef JSON_PRINTER_H
#define	JSON_PRINTER_H


/* Provides Interface for printing stuff in JSON format */
class JSON_printer {
    
    
    
    public:
        std::string print_int_vector(std::vector<int>);
        std::string print_int_vec_vector(std::vector<vector<int>>);
        std::string print_float_vector(std::vector<float>);
        std::string print_float_vec_vector(std::vector<std::vector<float>>);
    
    
};

#endif	/* JSON_PRINTER_H */

