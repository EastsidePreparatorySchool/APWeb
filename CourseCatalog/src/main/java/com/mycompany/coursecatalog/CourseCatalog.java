/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import com.google.gson.Gson;
import com.mycompany.coursecatalog.Database.MessageData;
import java.util.List;
import javax.servlet.MultipartConfigElement;
import spark.Spark;
import static spark.Spark.*;

/**
 *
 * @author daman
 */
public class CourseCatalog {
    
    
    final static private Gson gson = new Gson();

    public static void main(String[] args) {

        staticFiles.location("/static");

        // login route and enforcing filter
        post("/login", (req, res) -> login(req, res));
        before("/protected/*", (req, res) -> {
            if (req.session().attribute("context") == null) {
                halt(401, "You must login.");
            }
        });
        before("/static/status.html", (req, res) -> {
            if (req.session().attribute("context") == null) {
                halt(401, "You must login.");
            }
        });

        // simple chat server as part of service
        put("/protected/putmessage", (req, res) -> putMessage(req), new JSONRT());
        post("/protected/postmessage", (req, res) -> postMessage(req));
        get("/protected/getnewmessages", "application/json", (req, res) -> getNewMessages(req, res), new JSONRT());

        get("/protected/getStudents", (req, res) -> getStudents(req), new JSONRT());
        get("/protected/getCourseOfferings", (req, res) -> getCourseOfferings(req), new JSONRT());
        get("/protected/getAllRequests", (req, res) -> getAllRequests(req), new JSONRT());
    }

    private static Object getStudents(spark.Request req) {
        System.out.println("entered getStudents");
        Context ctx = getContextFromSession(req.session());

        ctx.db.getAllFrom("students");

        Object[] ao = ctx.db.queryStudents("select * from students");
        System.out.println(ao.length);

        return ao;
    }
    
    private static Object getCourseOfferings(spark.Request req) {
        System.out.println("entered getCourseRequests");
        Context ctx = getContextFromSession(req.session());

        ctx.db.getAllFrom("course_offerings");

        Object[] ao = ctx.db.queryCourses("select * from course_offerings");
        System.out.println(ao.length);
        
        return ao;
    }
    
    private static Object getAllRequests(spark.Request req) {
        System.out.println("entered getAllRequests");
        Context ctx = getContextFromSession(req.session());

        ctx.db.getAllFrom("schedule_requests");

        Object[] ao = ctx.db.queryAllRequests ("select * from schedule_requests");
        System.out.println(ao.length);
        
        return ao;
    }

    private static String login(spark.Request req, spark.Response res) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
        req.session().attribute("context", new Context(req.body()));

        System.out.println("login: " + req.session().attribute("context"));
        
        res.redirect("status.html");
        
        return req.body();
    }

    public static String putMessage(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        MessageData m;
        synchronized (ctx) {
            //MessageData needs an ID
                String myObjString = "" + req.body();
                System.out.println("put msg: " + req.body());
                m = gson.fromJson(myObjString, MessageData.class);
                ctx.db.insertMessage(m);
        }
        return "ok";
    }

    public static String postMessage(spark.Request req) {
        Context ctx = getContextFromSession(req.session());

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        System.out.println("post msg: " + req.queryParams("message"));
        
        return "ok";
    }

    public static Object getNewMessages(spark.Request req, spark.Response res) {
//        System.out.println("entered getNewMessages");
        Context ctx = getContextFromSession(req.session());
        Object[] result = null;
        synchronized (ctx) {
            result = ctx.db.getNewMessages(ctx.messagesSeen);
            if (result != null) {
                ctx.messagesSeen += result.length;
            }
        }
        return result;
    }

    public static Context getContextFromSession(spark.Session s) {
        Context ctx = s.attribute("Context");
        if (ctx == null) {
            // this should never happen since we require login with a before-filter
            ctx = new Context("unknown");
            s.attribute("Context", ctx);
        }

        return ctx;
    }

}
