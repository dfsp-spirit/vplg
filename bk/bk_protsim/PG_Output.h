/* 
 * File:   PG_Output.h
 * Author: julian
 *
 * Created on June 10, 2015, 2:19 PM
 */

#ifndef PG_OUTPUT_H
#define	PG_OUTPUT_H

#include "ProductGraph.h"


class PG_Output {
public:
    static std::list<unsigned long> get_common_first(const ProductGraph& pg, std::list<unsigned long> clique);
    static std::list<unsigned long> get_common_second(const ProductGraph& pg, std::list<unsigned long> clique);
};

#endif	/* PG_OUTPUT_H */

