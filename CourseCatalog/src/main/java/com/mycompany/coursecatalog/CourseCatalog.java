/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import static spark.Spark.*;

/**
 *
 * @author daman
 */
public class CourseCatalog {

    public static void main(String[] args) {
        staticFiles.location("/static");
        Database db = new Database();
        db.connect();

        db.example("courses");
        db.example("students");
        
        Object[] ao = db.queryStudents("select * from students");
        System.out.println(ao.length);
        
        db.disconnect();
    }

}
