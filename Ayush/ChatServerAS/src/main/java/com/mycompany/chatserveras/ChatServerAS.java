/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatserveras;

import java.util.ArrayList;
import javax.servlet.MultipartConfigElement;
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
        post("/postmsg", (req, res) -> postMsg(req));
        get("/getnewmessages", (req, res) -> getNewMessages(req, res));
        
//        before("/protected/*", (req, res) -> {
//            if (req.session().attribute("initials") == null) {
//                halt(401, "YOU SHALL NOT PASS.");
//            }
//        });
        
        //put("/login", (req, res) -> login(req));
        //post("/postmsg", (req, res) -> postMsg(req));
    }
    
//    private static String login (spark.Request req) {
//        
//        req.session().attribute("initials", req.body());
//        return req.body();
//    }
    
    public static String putMsg(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        synchronized (ctx) {
            System.out.println("put msg: " + req.body());
            synchronized(messages) {
                messages.add(req.session().id() + ":" + req.body());
            }
        }
        return req.session().id();
    }
    
    public static String postMsg(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        
        synchronized (ctx){
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

            System.out.println("put msg: " + req.body());
            synchronized (messages) {
                messages.add(req.session().id() + ":" + req.body());
            }
        }
        return req.session().id();
    }
    
    public static String getNewMessages(spark.Request req, spark.Response res) {
        Context ctx = getContextFromSession(req.session());
        synchronized (ctx) {
            StringBuilder result = new StringBuilder();
            synchronized (messages) {
                for (int counter = ctx.seen; counter < messages.size(); counter++) {
                    String newMessage = messages.get(counter);
                    if (newMessage.startsWith(req.session().id())) {
                        result.append(newMessage.substring(req.session().id().length() + 1));
                    }
                    else {
                        result.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        result.append(newMessage);
                    }
                    result.append("<br>");
                }

                ctx.seen = messages.size();
            }
            return result.toString();
        }
    }
    
    public static Context getContextFromSession(spark.Session session) {
        Context ctx = session.attribute("Context");
        synchronized (session) {
            if (ctx == null) {
                ctx = new Context();
                session.attribute("Context", ctx);
            }

            return ctx;
        }
    }

}
