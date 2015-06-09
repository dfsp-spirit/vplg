/* 
 * File:   Database.cpp
 * Author: Tim Schaefer
 * 
 * Created on July 15, 2014, 12:37 PM
 */

#include "Database.h"
#include "db.h"

using namespace std;
using namespace boost;
using namespace pqxx;


string Database::get_connect_string() {
    // dbname=vplg user=vplg host=localhost port=5432 connect_timeout=10 password=vplg
    string s = "dbname=" + get_dbname() + " user=" + get_dbusername() + " host=" + get_dbhost() + " port=" + get_dbport() + " connect_timeout=" + get_dbtimeout() + " password=" + get_dbpassword() + "";
    return s;
}

string Database::get_dbusername() {
    //string s = "vplg";
	//return s;
	return options["dbusername"];
    
}

string Database::get_dbname() {
    //string s = "vplg";
    //return s;
	return options["dbname"];
}

string Database::get_dbpassword() {
    //string s = "vplg";
    //return s;
	return options["dbpassword"];
}

string Database::get_dbhost() {
    //string s = "127.0.0.1";
    //return s;
	return options["dbhost"];
}

string Database::get_dbport() {
    //string s = "5432";
    //return s;
	return options["dbport"];
}

string Database::get_dbtimeout() {
    //string s = "10";
    //return s;
	return options["dbtimeout"];
}

