package com.mycompany.coursecatalog;

import com.google.gson.Gson;
import javax.servlet.MultipartConfigElement;
import static spark.Spark.*;
import spark.staticfiles.StaticFilesConfiguration;

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
            System.out.println("filter: timer alive?" + req.url());
            Context ctx = getContextFromSession(req.session());
            if (ctx != null && ctx.checkExpired()) {
                internalLogout(req);
                res.redirect("/expired.html");
            }
        });

        before("/protected/*", (req, res) -> {
            System.out.println("filter: /protected/*");
            if (req.session().attribute("context") == null) {
                System.out.println("unauthorized " + req.url());
                res.redirect("/unauthorized.html");
            }
        });
        before("/protected/admin/*", (req, res) -> {
            System.out.println("filter: /protected/admin/*");
            Context ctx = getContextFromSession(req.session());
            if (ctx == null || !ctx.login.equalsIgnoreCase("bgummere")) {
                System.out.println("unauthorized " + req.url());
                res.redirect("/unauthorized.html");
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
        get("/protected/getStudents", (req, res) -> getReqCtx(req).getStudents(req), new JSONRT());
        get("/protected/getCourseOfferings", (req, res) -> getReqCtx(req).getCourseOfferings(req), new JSONRT());
        get("/protected/getAllRequests", (req, res) -> getReqCtx(req).getAllRequests(req.queryParams("login")), new JSONRT());
        get("/protected/getCourseStudentsFirst", (req, res) ->  getReqCtx(req).getCourseStudentsFirst(req.queryParams("id")), new JSONRT());
        get("/protected/getCourseStudentsAll", (req, res) -> getReqCtx(req).getCourseStudentsAll(req.queryParams("id")), new JSONRT());

        // schedule request CRUD
        put("/protected/createScheduleRequest", (req, res) -> {
            ScheduleRequest sr = gson.fromJson(req.body(), ScheduleRequest.class);
            return getReqDb(req).createScheduleRequest(sr);
        });
        get("/protected/retrieveScheduleRequest", (req, res) -> {
            return getReqDb(req).retrieveScheduleRequest(Integer.parseInt(req.queryParams("id")));
        }, new JSONRT());
        patch("/protected/updateScheduleRequest", (req, res) -> {
            getReqDb(req).updateScheduleRequest(gson.fromJson(req.body(), ScheduleRequest.class));
            return 0;
        });
        delete("/protected/deleteScheduleRequest", (req, res) -> {
            getReqDb(req).deleteScheduleRequest(Integer.parseInt(req.body()));
            return 0;
        });
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
        Context ctx = getReqCtx(req);
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

    // this can be called even when there is no context
    private static Context getContextFromSession(spark.Session s) {
        Context ctx = s.attribute("context");
        return ctx;
    }

    // this should only be called when we know there is a context in the session
    private static Context getReqCtx(spark.Request req) {
        return getContextFromSession(req.session());
    }

    // this should only be called when we know there is a context in the session
    private static Database getReqDb(spark.Request req) {
        return getContextFromSession(req.session()).db;
    }

}
