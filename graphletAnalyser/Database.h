/* 
 * File:   Database.h
 * Author: ts
 *
 * Created on July 15, 2014, 12:37 PM
 */

#ifndef DATABASE_H
#define	DATABASE_H

#include "global.h"
#include "db.h"

/**
 * This is a database manager, implemented as a singleton.
 */
class Database {
public:
    // singleton getter for instance, lazy evaluated
    static Database& getInstance() {
        static Database instance;
        return instance;
    }
    
    // methods
    std::string get_connect_string();
    std::string get_dbname();
    std::string get_dbusername();
    std::string get_dbpassword();
    std::string get_dbhost();
    std::string get_dbport();
    std::string get_dbtimeout();
    
private:    
    Database() {};                      // singleton stuff
    Database(Database const&());        // singleton, so do not implement this
    void operator=(Database const&);    // singleton, so do not implement this
  
    //const static std::string dbuser = "vplg;"
};

#endif	/* DATABASE_H */

