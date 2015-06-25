/* 
 * File:   JSON_printer_int.h
 * Author: ben
 *
 * Created on June 25, 2015, 1:24 PM
 */

#ifndef JSON_PRINTER_INT_H
#define	JSON_PRINTER_INT_H


class JSON_printer_int : public JSON_printer {
    
    public:
        std::string print_vector(std::vector<int>);
        std::string print_vec_vector(std::vector<std::vector<int>>);
    
    
};


#endif	/* JSON_PRINTER_INT_H */

