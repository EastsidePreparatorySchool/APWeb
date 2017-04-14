/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatserverdm;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import static spark.Spark.*;

/**
 *
 * @author daman
 */
public class ChatServerDM {

    static ArrayList<String> messages = null;

    public static void main(String[] args) {
        messages = new ArrayList<String>();
        staticFiles.location("/static");
        get("/hello", (req, res) -> "Hello World");
        get("/login", (req, res) -> returnSession(req));
        put("/send", (req, res) -> sendMessage(req));
    }

    public static String sendMessage(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        System.out.println(req.body().toString());
        if (req.session().attribute("username") == null) {
            return new Message("Server", "Please log in").toString();
        } else {
            Message m = new Message(req.session().attribute("username"), req.body());
            messages.add(m.toString());
            return m.toString();
        }
    }

    public static String getNewMessages(spark.Request req, spark.Response res) {
        Context ctx = getContextFromSession(req.session());
        StringBuilder result = new StringBuilder();
        for (int i = ctx.seen; i < messages.size(); i++) {
            String s = messages.get(i);
            if (s.startsWith(req.session().id())) {
                s = s.substring(req.session().id().length() + 1);
            } else {
                s = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + s;
            }
            result.append(s);
            result.append("<br>");
        }
        ctx.seen = messages.size();
        return result.toString();
    }

    public static String returnParams(spark.Request req) {
        String result = "";
        for (Entry<String, String[]> e : req.queryMap().toMap().entrySet()) {
            result += "Parameter: " + e.getKey();
            result += " Values :";
            for (String s : e.getValue()) {
                result += " and " + s;
            }
            result += ";  ";
        }
        return result;
    }

    public static String returnSession(spark.Request req) {
        if (req.session().isNew()) {
            String username = req.queryParams("username");
            req.session().attribute("username", username);
            String result = "Session (id=" + req.session().id() + ", new:" + req.session().isNew() + ") info: username = " + req.session().attribute("username");
            return result;
        } else {
            return "ERROR: You are not a new user.";
        }
    }

    public static Context getContextFromSession(spark.Session s) {
        Context context = s.attribute("Context");
        if (context == null) {
            context = new Context();
            s.attribute("Context", context);
        }

        return context;
    }

}
