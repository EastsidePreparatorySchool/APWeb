/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import com.google.gson.Gson;
import javax.servlet.MultipartConfigElement;
import spark.Service;
import static spark.Spark.*;
import spark.staticfiles.StaticFilesConfiguration;

/**
 *
 * @author daman
 */
public class CourseCatalog {

    final static private Gson gson = new Gson();

    public static void main(String[] args) {

        // see below
        //staticFiles.location("/static");
        // login route and enforcing filter
        post("/login", (req, res) -> login(req, res));
        post("/logout", (req, res) -> logout(req, res));
        get("/protected/name", (req, res) -> getName(req, res));

        before("/protected/*", (req, res) -> {
            if (req.session().attribute("context") == null) {
                System.out.println("unauthorized " + req.url());
                res.redirect("login.html");
            }
        });
        before("status.html", (req, res) -> {
            if (req.session().attribute("context") == null) {
                System.out.println("unauthorized " + req.url());
                res.redirect("login.html");
            }
        });
        before("browse.html", (req, res) -> {
            if (req.session().attribute("context") == null) {
                System.out.println("unauthorized " + req.url());
                res.redirect("login.html");
            }
        });

        // Static files filter is LAST
        StaticFilesConfiguration staticHandler = new StaticFilesConfiguration();
        staticHandler.configure("/static");
        before((request, response) -> staticHandler.consume(request.raw(), response.raw()));

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

        Object[] ao = ctx.db.queryAllRequests("select * from schedule_requests");
        System.out.println(ao.length);

        return ao;
    }

    private static String login(spark.Request req, spark.Response res) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        String login = req.queryParams("login");
        Context ctx = new Context(login);

        if (ctx.db.queryName(login).equals("unknown")) {
            internalLogout(req);
            res.redirect("login.html");
            return "";
        }

        System.out.println("login: " + login);
        req.session().attribute("context", ctx);
        res.redirect("status.html");

        return "ok";
    }

    private static void internalLogout(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        ctx.db.disconnect();
        req.session().attribute("context", null);

        System.out.println("logged off.");
    }

    private static String logout(spark.Request req, spark.Response res) {
        internalLogout(req);
        res.redirect("login.html");

        return "ok";
    }

    public static String getName(spark.Request req, spark.Response res) {
        Context ctx = getContextFromSession(req.session());
        String result = "";
        System.out.println("Name for: " + ctx.login);

        result = ctx.db.queryName(ctx.login);
        System.out.println("Name: " + result);
        return result;
    }

    public static Context getContextFromSession(spark.Session s) {
        Context ctx = s.attribute("context");
        if (ctx == null) {
            // this should never happen since we require login with a before-filter
            ctx = new Context("unknown");
            s.attribute("Context", ctx);
        }

        return ctx;
    }

}
