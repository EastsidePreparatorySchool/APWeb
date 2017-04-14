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
        get("/getnewmessages", (req, res) -> getNewMessages(req, res));
        //post("/postmsg", (req, res) -> postMsg(req));
    }
    
    public static String putMsg(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        System.out.println("put msg: " + req.body());
        messages.add(req.session().id() + ":" + req.body());
        return req.session().id();
    }
    
    public static String getNewMessages(spark.Request req, spark.Response res) {
        Context ctx = getContextFromSession(req.session());
        
        StringBuilder result = new StringBuilder();
        for (int counter = ctx.seen; counter < messages.size(); counter++) {
            String newMessage = messages.get(counter);
            if (newMessage.startsWith(req.session().id())) {
                result.append(newMessage.substring(req.session().id().length() + 1));
            }
            else {
                result.append("&nbsp;&nbsp;&nbsp&nbsp;&nbsp;&nbsp;");
                result.append(newMessage);
            }
            result.append("<br>");
        }
        
        ctx.seen = messages.size();
        return result.toString();
    }
    
    public static Context getContextFromSession(spark.Session session) {
        Context ctx = session.attribute("Context");
        if (ctx == null) {
            ctx = new Context();
            session.attribute("Context", ctx);
        }
        
        return ctx;
    }

}
