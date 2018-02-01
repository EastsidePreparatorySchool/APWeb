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
<<<<<<< HEAD
        get("/myButton", (req, res) -> "Nice!");
=======
        get("/shutdown", (req, res) -> {System.exit(0); return "";});
>>>>>>> 9e8da881dbce8c07685c5469b3459a32095127da
    }

}
