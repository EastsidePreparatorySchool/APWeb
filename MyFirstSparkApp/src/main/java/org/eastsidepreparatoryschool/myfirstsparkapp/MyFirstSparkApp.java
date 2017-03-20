/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsidepreparatoryschool.myfirstsparkapp;

import static spark.Spark.*;

/**
 *
 * @author gmein
 */
public class MyFirstSparkApp {

    public static void main(String[] args) {
        staticFiles.location("/static");
        get("/hello", (req, res) -> "Hello World");
    }

}
