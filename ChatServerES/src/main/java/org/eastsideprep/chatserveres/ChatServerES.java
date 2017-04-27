/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatserveres;

import static spark.Spark.*;
import java.util.*;
import javax.servlet.MultipartConfigElement;
import com.google.gson.*;
/**
 *
 * @author ESchreiber
 */
public class ChatServerES {

    static ArrayList<Message> messages = null;

    public static void main(String[] args) {
        messages = new ArrayList<Message>();
        staticFiles.location("/static");
        put("/protected/putmessage", (req, res) -> putMessage(req));
        put("/protected/putmessage1", (req, res) -> putMessage1(req));
        get("/protected/getnewmessages", "application/json", (req, res) -> getNewMessages(req, res), new JSONRT());
        post("/protected/postmessage", (req, res) -> postMessage(req));
        before("/protected/*", (req, res) -> {
            if (req.session().attribute("initials") == null) {
                halt(401, "I require login");
            }
        });
        put("/login", (req, res) -> login(req));
    }

    public static boolean putMessage(spark.Request request) {
        Context ctx = getCtxFromSession(request.session());
        synchronized (ctx) {
            synchronized (messages) {
                messages.add(new Message(messages.size(), 0, request.session().attribute("initials"), request.body()));
                return true;
            }
        }
    }
    public static boolean putMessage1(spark.Request request) {
        Context ctx = getCtxFromSession(request.session());
        Gson gson = new Gson();
        synchronized (ctx) {
            synchronized (messages) {
                System.out.println(request.body());
                messages.add(gson.fromJson(request.body(), Message.class));
                return true;
            }
        }
    }

    public static String postMessage(spark.Request req) {
        Context ctx = getCtxFromSession(req.session());
        synchronized (ctx) {
            synchronized (messages) {
                MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
                req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
                messages.add(new Message(messages.size(), 0, req.session().attribute("initials"), req.queryParams("message")));
                return req.session().id();
            }
        }
    }

    private static String login(spark.Request req) {
        req.session().attribute("initials", req.body());
        return req.body();
    }

    public static Object getNewMessages(spark.Request request, spark.Response response) {
        Context ctx = getCtxFromSession(request.session());
        List <Message> myMessages;
        synchronized (ctx) {
            synchronized (messages) {
                myMessages = messages.subList(ctx.seen, messages.size());
                ctx.seen = messages.size();
            }
        }
        return myMessages.toArray();
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