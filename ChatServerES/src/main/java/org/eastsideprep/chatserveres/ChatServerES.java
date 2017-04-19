/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatserveres;

import static spark.Spark.*;
import java.util.*;
import javax.servlet.MultipartConfigElement;

/**
 *
 * @author ESchreiber
 */
public class ChatServerES {

    static ArrayList<String> messages = null;

    public static void main(String[] args) {
        messages = new ArrayList<String>();
        staticFiles.location("/static");
        put("/protected/putmessage", (req, res) -> putMessage(req));
        get("/protected/getnewmessages", (req, res) -> getNewMessages(req, res));
        post("/protected/postmessage", (req, res) -> postMessage(req));
        before("/protected/*", (req, res) -> {
            if (req.session().attribute("initials") == null) {
                halt(401, "I require login");
            }
        });
        put("/login", (req, res) -> login(req));
    }

    public static boolean putMessage(spark.Request request) {
        synchronized (messages) {
            messages.add(request.session().attribute("initials") + ": " + request.body());
            return true;
        }
    }

    public static String postMessage(spark.Request req) {
        synchronized (messages) {
            Context ctx = getCtxFromSession(req.session());
            MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
            messages.add(req.session().attribute("initials") + ":" + req.queryParams("message"));
            return req.session().id();
        }
    }

    private static String login(spark.Request req) {
        req.session().attribute("initials", req.body());
        return req.body();
    }

    public static String getNewMessages(spark.Request request, spark.Response response) {
        synchronized (messages) {
            Context ctx = getCtxFromSession(request.session());
            StringBuilder result = new StringBuilder();
            for (int i = ctx.seen; i < messages.size(); i++) {
                String s = messages.get(i);
                if (s.startsWith(request.session().attribute("initials"))) {
                    result.append(s.substring(request.session().attribute("initials").toString().length() + 1));
                } else {
                    result.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    result.append(s);
                }
                result.append("<br>");
            }
            ctx.seen = messages.size();
            return result.toString();
        }
    }

    public static Context getCtxFromSession(spark.Session s) {
        Context ctx = s.attribute("Context");
        if (ctx == null) {
            ctx = new Context();
            s.attribute("Context", ctx);
        }
        return ctx;
    }
}
