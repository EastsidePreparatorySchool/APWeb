/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatserveras;

import java.util.ArrayList;
import static spark.Spark.*;

/**
 *
 * @author asharma
 */
public class ChatServerAS {
    static ArrayList<String> messages = new ArrayList<>();
    
    public static void main(String[] args) {
        staticFiles.location("/static");
        get("/hello", (req, res) -> "Hello World");
        put("/putmsg", (req, res) -> putMsg(req));
        //post("/postmsg", (req, res) -> postMsg(req));
    }
    
    public static String putMsg(spark.Request req) {
        //Context ctx = getContextFromSession(req.session());
        System.out.println("put msg: " + req.body());
        messages.add(req.session().id() + ":" + req.body());
        return req.session().id();
    }
    
    /*public static Context getContextFromSession(spark.Session s) {
        Context ctx = s.attribute("Context");
        if (ctx == null) {
            ctx.add()
        }
    }*/
}
