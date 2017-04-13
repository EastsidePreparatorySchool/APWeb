/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsidepreparatoryschool.myfirstsparkapp;

import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import static spark.Spark.*;

/**
 *
 * @author gmein
 */
public class MyFirstSparkApp {

    public static void main(String[] args) {
        staticFiles.location("/static");
        get("/hello", (req, res) -> syncTest());
        get("/params", (req, res) -> returnParams(req));
        get("/session", (req, res) -> returnSession(req));
        get("/shutdown", (req, res) -> {System.exit(0); return "";});
    }
    
    
    public static String returnParams(spark.Request req) {
        String result = "";
        for (Entry<String, String[]> e: req.queryMap().toMap().entrySet()) {
            result += "Parameter: "+e.getKey();
            result += " Values :";
            for (String s: e.getValue()) {
                result += " and "+s;
            }
            result += ";  ";
        }
        return result;
    }
    public static String returnSession(spark.Request req) {
        String result = "Session (id="+req.session().id()+", new:"+req.session().isNew()+") info: ";
        for (String s: req.session().attributes()) {
            result += "Attribute: "+s;
            result += ";  ";
        }
        return result;
    }
    
    public static String syncTest() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
        }
        return "Hello world";
    }

}
