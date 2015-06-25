/* 
 * File:   JSON_printer_float.h
 * Author: ben
 *
 * Created on June 25, 2015, 1:24 PM
 */

#ifndef JSON_PRINTER_FLOAT_H
#define	JSON_PRINTER_FLOAT_H

class JSON_printer_float : public JSON_printer {
    
    public:
        std::string print_vector(std::vector<float>);
        std::string print_vec_vector(std::vector<std::vector<float>>);
    
    
};

#endif	/* JSON_PRINTER_FLOAT_H */

