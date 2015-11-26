/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "Scoring.h"


float Scoring::euklid_float(std::vector<float> vec1, std::vector<float> vec2) {
    
    float ret_val = 0;
    float val = 0;
    
    if (vec1.size() != vec2.size()) {
        std::cerr << "WARNING: Cannot compute euklid distance for vectors with different lengths" << std::endl;
        return -1;
    }
    
    for (int i = 0; i < vec1.size(); i++) {
        
        val = vec1[i] - vec2[i];
        val = val * val;
        ret_val += val;
    }
    
    ret_val = sqrt(ret_val);
    
    return ret_val;
}

float Scoring::euklid_int(std::vector<int> vec1, std::vector<int> vec2) {
    
    float ret_val = 0;
    int val = 0;
    
    if (vec1.size() != vec2.size()) {
        std::cerr << "WARNING: Cannot compute euklid distance for vectors with different lengths" << std::endl;
        return -1;
    }
    
    for (int i = 0; i < vec1.size(); i++) {
        
        val = vec1[i] - vec2[i];
        val = val * val;
        ret_val += float (val);
    }
    
    ret_val = sqrt(ret_val);
    
    return ret_val;
}

float Scoring::tanimoto_float(std::vector<float> vec1, std::vector<float> vec2, float precision) {
    
    float ret_val = 0;
    float numerator = 0;
    float denominator = 0;
    
    if (vec1.size() != vec2.size()) {
        std::cerr << "WARNING: Cannot compute tanimoto coefficient for vectors with different lengths" << std::endl;
        return -1;
    } else if (precision > 1. || precision < 0.) {
        
        std::cerr << "WARNING: Precision should only be set to values between 0 and 1" << std::endl;
        return -1;
    }
    
    
    for (int i = 0; i < vec1.size(); i++) {
        
        if (vec1[i] >= (vec2[i] * precision) && vec2[i] >= (vec1[i] * precision)) {
            
            numerator += vec1[i] * vec2[i];
        } else {
            
            denominator += vec1[i] * vec1[i];
            denominator += vec2[i] * vec2[i];
        }
        
        
    }
    
    ret_val = numerator / denominator;
    
    return ret_val;
    
}

float Scoring::tanimoto_int(std::vector<int> vec1, std::vector<int> vec2, float precision) {
    
    float ret_val = 0;
    float numerator = 0;
    float denominator = 0;
    
    if (vec1.size() != vec2.size()) {
        std::cerr << "WARNING: Cannot compute tanimoto coefficient for vectors with different lengths" << std::endl;
        return -1;
    } else if (precision > 1. || precision < 0.) {
        
        std::cerr << "WARNING: Precision should only be set to values between 0 and 1" << std::endl;
        return -1;
    }
    
    
    for (int i = 0; i < vec1.size(); i++) {
        
        if (float (vec1[i]) >= (float (vec2[i]) * precision) && float (vec2[i]) >= (float (vec1[i]) * precision) ) {
            
            numerator += float (vec1[i]) * float (vec2[i]);
        } else {
            
            denominator += float (vec1[i]) * float (vec1[i]);
            denominator += float (vec2[i]) * float (vec2[i]);
        }
        
        
    }
    
    ret_val = numerator / denominator;
    
    return ret_val;
    
}