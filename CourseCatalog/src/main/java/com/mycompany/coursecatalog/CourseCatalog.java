/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import java.util.List;
import javax.servlet.MultipartConfigElement;
import static spark.Spark.*;

/**
 *
 * @author daman
 */
public class CourseCatalog {

    public static void main(String[] args) {

        staticFiles.location("/static");

        // login route and enforcing filter
        put("/login", (req, res) -> login(req));
        before("/protected/*", (req, res) -> {
            if (req.session().attribute("context") == null) {
                halt(401, "You must login.");
            }
        });

        // simple chat server as part of service
        put("/protected/putmessage", (req, res) -> putMessage(req));
        post("/protected/postmessage", (req, res) -> postMessage(req));
        get("/protected/getnewmessages", "application/json", (req, res) -> getNewMessages(req, res), new JSONRT());

        get("/protected/example", (req, res) -> example(req));
    }

    private static Object example(spark.Request req) {
        System.out.println("entered example");

        Context ctx = getContextFromSession(req.session());
        System.out.println("put msg: " + req.body());

        ctx.db.example("courses");
        ctx.db.example("students");

        Object[] ao = ctx.db.queryStudents("select * from students");
        System.out.println(ao.length);

        return ao;
    }

    private static String login(spark.Request req) {
        req.session().attribute("context", new Context(req.body()));
        return req.body();
    }

    public static String putMessage(spark.Request req) {

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
