/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Scoring.h
 * Author: ben
 *
 * Created on November 24, 2015, 2:08 PM
 */

#ifndef SCORING_H
#define SCORING_H

#include <stdio.h>
#include <math.h>

class Scoring {
    
public:
    
    static float euklid_int(std::vector<int>,std::vector<int>);
    static float euklid_float(std::vector<float>,std::vector<float>);
    static float tanimoto_int(std::vector<int>,std::vector<int>,float);
    static float tanimoto_float(std::vector<float>,std::vector<float>,float);
    
};

#endif /* SCORING_H */

