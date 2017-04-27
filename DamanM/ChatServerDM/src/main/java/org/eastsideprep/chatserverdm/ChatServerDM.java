/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatserverdm;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static spark.Spark.*;

/**
 *
 * @author daman
 */
public class ChatServerDM {
        static ArrayList<Message> messages;
    final static private Gson gson = new Gson();

    public static void main(String[] args) {
        messages = new ArrayList<Message>();

        staticFiles.location("/static");
        get("/hello", (req, res) -> "Hello World");
        put("/protected/putmessage", "application/json", (req, res) -> putMessage(req), new JSONRT());
//        post("/protected/postmessage", (req, res) -> postMessage(req));
        get("/protected/getnewmessages", "application/json", (req, res) -> getNewMessages(req, res), new JSONRT());
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

    public static Object putMessage(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        Message m;
        synchronized (ctx) {
            synchronized (messages) {
                String myObjString = "" + req.body();
                System.out.println("put msg: " + req.body());
                m = gson.fromJson(myObjString, Message.class);
                m.setID(messages.size());
                messages.add(m);
            }
        }
        return m;
    }

//    public static String postMessage(spark.Request req) {
//        Context ctx = getContextFromSession(req.session());
//
//        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
//        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
//
//        System.out.println("post msg: " + req.queryParams("message"));
//        messages.add(req.session().attribute("initials") + ":" + req.queryParams("message"));
//        return req.session().id();
//    }

    /*
    public static String getNewMessages(spark.Request req, spark.Response res) {
        System.out.println("entered getNewMessages");
        Context ctx = getContextFromSession(req.session());
        //
        StringBuilder result = new StringBuilder();
        for (int i = ctx.seen; i < messages.size(); i++) {
            String s = messages.get(i);
            
            if (s.startsWith(req.session().attribute("initials"))) {
                result.append(s.substring(req.session().attribute("initials").toString().length() + 1));
            } else {
                result.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                result.append(s);
            }
            result.append("<br>");
        }
        
        ctx.seen = messages.size();
        return result.toString();
    }
     */
    public static Object getNewMessages(spark.Request req, spark.Response res) {
        //System.out.println("entered getNewMessages");
        Context ctx = getContextFromSession(req.session());
        List<Message> myMessages;

        synchronized (ctx) {
            synchronized (messages) {
                myMessages = messages.subList(ctx.seen, messages.size());
                ctx.seen = messages.size();
            }
        }
        return myMessages;
    }

    public static Context getContextFromSession(spark.Session s) {
        Context ctx = s.attribute("Context");
        if (ctx == null) {
            ctx = new Context();
            s.attribute("Context", ctx);
        }

        return ctx;
    }

//    static ArrayList<String> messages = null;
//
//    public static void main(String[] args) {
//        messages = new ArrayList<String>();
//        staticFiles.location("/static");
//        get("/hello", (req, res) -> "Hello World");
//        get("/login", (req, res) -> returnSession(req));
//        put("/send", (req, res) -> sendMessage(req));
//    }
//
//    public static String sendMessage(spark.Request req) {
//        Context ctx = getContextFromSession(req.session());
//        System.out.println(req.body().toString());
//        if (req.session().attribute("username") == null) {
//            return new Message("Server", "Please log in").toString();
//        } else {
//            Message m = new Message(req.session().attribute("username"), req.body());
//            messages.add(m.toString());
//            return m.toString();
//        }
//    }
//
//    public static String getNewMessages(spark.Request req, spark.Response res) {
//        Context ctx = getContextFromSession(req.session());
//        StringBuilder result = new StringBuilder();
//        for (int i = ctx.seen; i < messages.size(); i++) {
//            String s = messages.get(i);
//            if (s.startsWith(req.session().id())) {
//                s = s.substring(req.session().id().length() + 1);
//            } else {
//                s = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + s;
//            }
//            result.append(s);
//            result.append("<br>");
//        }
//        ctx.seen = messages.size();
//        return result.toString();
//    }
//
//    public static String returnParams(spark.Request req) {
//        String result = "";
//        for (Entry<String, String[]> e : req.queryMap().toMap().entrySet()) {
//            result += "Parameter: " + e.getKey();
//            result += " Values :";
//            for (String s : e.getValue()) {
//                result += " and " + s;
//            }
//            result += ";  ";
//        }
//        return result;
//    }
//
//    public static String returnSession(spark.Request req) {
//        if (req.session().isNew()) {
//            String username = req.queryParams("username");
//            req.session().attribute("username", username);
//            String result = "Session (id=" + req.session().id() + ", new:" + req.session().isNew() + ") info: username = " + req.session().attribute("username");
//            return result;
//        } else {
//            return "ERROR: You are not a new user.";
//        }
//    }
//
//    public static Context getContextFromSession(spark.Session s) {
//        Context context = s.attribute("Context");
//        if (context == null) {
//            context = new Context();
//            s.attribute("Context", context);
//        }
//
//        return context;
//    }

}
