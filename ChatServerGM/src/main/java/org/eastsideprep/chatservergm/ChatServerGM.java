/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatservergm;

import static spark.Spark.*;

/**
 *
 * @author gmein
 */
public class ChatServerGM {

    public static void main(String[] args) {
        staticFiles.location("/static");
        get("/hello", (req, res) -> "Hello World");
        put("/sendmessage", (req, res) -> putMessage(req));
        get("/getnewmessages", (req, res) -> getNewMessages(req, res));
    }

    public static String putMessage(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        System.out.println("msg: " + req.body());
        ctx.messages.add(req.body());
        return "ok";
    }

    public static String getNewMessages(spark.Request req, spark.Response res) {
        Context ctx = getContextFromSession(req.session());
        StringBuilder result = new StringBuilder();
        for (int i = ctx.seen; i < ctx.messages.size(); i++) {
            result.append(ctx.messages.get(i));
            result.append("<br>");
        }
        ctx.seen = ctx.messages.size();
        return result.toString();
    }

    public static Context getContextFromSession(spark.Session s) {
        Context ctx = s.attribute("Context");
        if (ctx == null) {
            ctx = new Context();
            s.attribute("Context", ctx);
        }

        return ctx;
    }
}
