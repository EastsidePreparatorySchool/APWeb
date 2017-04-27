/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatserveras;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.MultipartConfigElement;
import static spark.Spark.*;

/**
 *
 * @author asharma
 */
public class ChatServerAS {
    static ArrayList<ReplyMessage> messages = new ArrayList<>();
    
    public static void main(String[] args) {
        staticFiles.location("/static");
        get("/protected/hello", (req, res) -> "Hello World");
        put("/protected/putmsg", (req, res) -> putMsg(req));
        //post("/protected/postmsg", (req, res) -> postMsg(req));
        get("/protected/getnewmessages", "application/json", (req, res) -> getNewMessages(req, res), new JSONRT());
        
        before("/protected/*", (req, res) -> {
            if (req.session().attribute("initials") == null) {
                halt(401, "YOU SHALL NOT PASS.");
            }
        });
        
        put("/login", (req, res) -> login(req));
    }
    
    private static String login (spark.Request req) {
        
        req.session().attribute("initials", req.body());
        return req.body();
    }
    
    public static String putMsg(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        JSONRT responseTransformer = new JSONRT();
        
        ReplyMessage replyMessage = responseTransformer.parse(req.body());
        
        synchronized (ctx) {
            System.out.println("put msg: " + req.body());
            synchronized(messages) {
                messages.add(/*req.session().attribute("initials").toString() + ":" + req.body()*/replyMessage);
            }
        }
        return req.session().id();
    }
    
//    public static String postMsg(spark.Request req) {
//        Context ctx = getContextFromSession(req.session());
//        
//        synchronized (ctx){
//            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
//            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
//
//            System.out.println("put msg: " + req.body());
//            synchronized (messages) {
//                messages.add(/*req.session().id() + ":" + */req.body());
//            }
//        }
//        return req.session().id();
//    }
    
    public static Object getNewMessages(spark.Request req, spark.Response res) {
        Context ctx = getContextFromSession(req.session());
        List<ReplyMessage> myMessages;
        
        
        
        synchronized (ctx) {
            synchronized (messages) {
                myMessages = messages.subList(ctx.seen, messages.size());
                ctx.seen = messages.size();
            }
            System.out.println(myMessages.toArray());
            return myMessages.toArray();
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
