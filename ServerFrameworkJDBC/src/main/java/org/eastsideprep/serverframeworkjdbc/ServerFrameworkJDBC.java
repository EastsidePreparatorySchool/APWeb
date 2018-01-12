/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.serverframeworkjdbc;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.MultipartConfigElement;
import static spark.Spark.*;

/**
 *
 * @author gmein
 */
public class ServerFrameworkJDBC {

    static ArrayList<String> messages;

    public static void main(String[] args) {
        messages = new ArrayList<>();

        staticFiles.location("/static");
        get("/hello", (req, res) -> hello(req), new JSONRT());
        
        put("/protected/put", (req, res) -> putHandler(req));
        post("/protected/post", (req, res) -> postHandler(req));
        get("/protected/get", "application/json", (req, res) -> getHandler(req),new JSONRT());
        
        get("/protected/gettables", "application/json", (req, res) -> getTablesHandler(req),new JSONRT());
        
        before("/protected/*", (req, res) -> {
            if (req.session().attribute("initials") == null) {
                halt(401, "You must login.");
            }
        });

        put("/login", (req, res) -> login(req));
    }

    private static String login(spark.Request req) {
        req.session().attribute("initials", req.body());
        return req.body();
    }
    
     private static Object hello(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        
        String initials = req.session().attribute("initials");
        if (initials == null) {
            initials = "<not logged in>";
        }

        return "Hello World, "+initials;
    }


    public static String putHandler(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        System.out.println("put: " + req.body());
        return "put handled";
    }

    public static String postHandler(spark.Request req) {
        Context ctx = getContextFromSession(req.session());

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        System.out.println("post 'message': " + req.queryParams("message"));
        return "post handled";
    }


    public static Object getHandler(spark.Request req) {
        System.out.println("entered getNewMessages");
        Context ctx = getContextFromSession(req.session());
 
        return "Here is what you got";
    }

     public static Object getTablesHandler(spark.Request req) {
        System.out.println("entered getNewMessages");
        Context ctx = getContextFromSession(req.session());
  
        return ctx.db.showTables();
    }

    
    
    public static Context getContextFromSession(spark.Session s) {
        Context ctx = s.attribute("Context");
        if (ctx == null) {
            ctx = new Context(s.attribute("initials"));
            s.attribute("Context", ctx);
        }

        return ctx;
    }
}
