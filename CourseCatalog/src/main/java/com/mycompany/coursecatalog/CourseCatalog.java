/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import com.google.gson.Gson;
import javax.servlet.MultipartConfigElement;
import static spark.Spark.*;
import spark.staticfiles.StaticFilesConfiguration;
import static com.mycompany.coursecatalog.ScheduleRequest.CreateScheduleRequest;
import static com.mycompany.coursecatalog.ScheduleRequest.DeleteScheduleRequest;
import static com.mycompany.coursecatalog.ScheduleRequest.RetrieveScheduleRequest;
import static com.mycompany.coursecatalog.ScheduleRequest.UpdateScheduleRequest;

/**
 *
 * @author daman
 */
public class CourseCatalog {

    final static private Gson gson = new Gson();

    public static void main(String[] args) {

        // housekeeping routes and filters
        post("/login", (req, res) -> login(req, res));
        post("/logout", (req, res) -> logout(req, res));
        get("/protected/name", (req, res) -> getName(req, res));
        
        // HTML pages use this to switch to the "expired" page
        get("/protected/checktimeout", (req, res) -> {
            Context ctx = getContextFromSession(req.session());
            if (ctx == null || ctx.checkExpired()) {
                System.out.println("filter: expired");
                internalLogout(req);
                return "expired";
            }
            return "alive";
        });

        // liveness check - this actually governs expiration
        before((req, res) -> {
            System.out.println("filter: check time " + req.url());
            Context ctx = getContextFromSession(req.session());
            if (ctx != null && ctx.checkExpired()) {
                internalLogout(req);
                res.redirect("expired.html");
            }
        });

        before("/protected/*", (req, res) -> {
            System.out.println("filter: protect *");
            if (req.session().attribute("context") == null) {
                System.out.println("unauthorized " + req.url());
                res.redirect("login.html");
            }
        });
        before("/protected/admin/*", (req, res) -> {
            System.out.println("filter: protect status");
            Context ctx = getContextFromSession(req.session());
            if (ctx == null || !ctx.login.equalsIgnoreCase("bgummere")) {
                System.out.println("unauthorized " + req.url());
                res.redirect("login.html");
            }
        });
        // liveness timer - this keeps the context alive for valid pages and requests
        before((req, res) -> {
            Context ctx = getContextFromSession(req.session());
            if (ctx != null) {
                if (!req.url().endsWith("/protected/checktimeout")) {
                    System.out.println("timer reset from URL: " + req.url());
                    ctx.updateTimer();
                }
            }
        });
        
        // Static files filter is LAST
        StaticFilesConfiguration staticHandler = new StaticFilesConfiguration();
        staticHandler.configure("/static");
        before((request, response) -> staticHandler.consume(request.raw(), response.raw()));

        // functionality:
        get("/protected/getStudents", (req, res) -> getStudents(req), new JSONRT());
        get("/protected/getCourseOfferings", (req, res) -> getCourseOfferings(req), new JSONRT());
        get("/protected/getAllRequests", (req, res) -> getAllRequests(req), new JSONRT());
        get("/protected/getCourseStudentsFirst", (req, res) -> getCourseStudentsFirst(req), new JSONRT());
        get("/protected/getCourseStudentsAll", (req, res) -> getCourseStudentsAll(req), new JSONRT());

        // schedule request CRUD
        put("/protected/createScheduleRequest", (req, res) -> {
            Context ctx = getContextFromSession(req.session());
            ScheduleRequest sr = gson.fromJson(req.body(), ScheduleRequest.class);
            return CreateScheduleRequest(sr, ctx.db);
        });
        get("/protected/retrieveScheduleRequest", (req, res) -> {
            Context ctx = getContextFromSession(req.session());
            int id = Integer.getInteger(req.body());
            return RetrieveScheduleRequest(id, ctx.db);
        }, new JSONRT());
        delete("/protected/deleteScheduleRequest", (req, res) -> {
            Context ctx = getContextFromSession(req.session());
            int id = Integer.getInteger(req.body());
            DeleteScheduleRequest(id, ctx.db);
            return 0;
        });
        patch("/protected/updateScheduleRequest", (req, res) -> {
            Context ctx = getContextFromSession(req.session());
            ScheduleRequest sr = gson.fromJson(req.body(), ScheduleRequest.class);
            UpdateScheduleRequest(sr, ctx.db);
            return 0;
        });
    }

    private static Object getStudents(spark.Request req) {
        System.out.println("entered getStudents");
        Context ctx = getContextFromSession(req.session());

        ctx.db.getAllFrom("students");

        Object[] ao = ctx.db.queryStudents("select * from students");
        System.out.println(ao.length);

        return ao;
    }

    private static Object getCourseStudentsFirst(spark.Request req) {
        System.out.println("getCourseStudentsFirst");
        Context ctx = getContextFromSession(req.session());
        return Student.firstChoice(req.body());
    }

    private static Object getCourseStudentsAll(spark.Request req) {
        System.out.println("getCourseStudentsAll");
        Context ctx = getContextFromSession(req.session());
        return Student.allChoice(req.body());
    }

    private static Object getCourseOfferings(spark.Request req) {
        System.out.println("entered getCourseOfferings");
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

    // housekeeping:
    private static String login(spark.Request req, spark.Response res) {
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        String login = req.queryParams("login");
        Context ctx = new Context(login);

        System.out.println("\"" + login + "\"");
        if (login.equalsIgnoreCase("bgummere")) {
            System.out.println("login: admin " + login);
            req.session().attribute("context", ctx);
            res.redirect("protected/admin/admin_student.html");

            return "ok";
        }

        if (ctx.db.queryName(login).equals("unknown")) {
            internalLogout(req);
            res.redirect("login.html");
            return "";
        }

        System.out.println("login: " + login);
        req.session().attribute("context", ctx);
        res.redirect("protected/status.html");

        return "ok";
    }

    private static void internalLogout(spark.Request req) {
        if (req.session().attribute("context") != null) {
            Context ctx = getContextFromSession(req.session());
            ctx.db.disconnect();
            req.session().attribute("context", null);

            System.out.println("logged off.");
        }
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

        if (ctx.login.equalsIgnoreCase("bgummere")) {
            System.out.println("Name: Mr. Gummere");
            return "Mr. Gummere";
        }

        result = ctx.db.queryName(ctx.login);
        System.out.println("Name: " + result);
        return result;
    }

    public static Context getContextFromSession(spark.Session s) {
        Context ctx = s.attribute("context");
        return ctx;
    }

}
