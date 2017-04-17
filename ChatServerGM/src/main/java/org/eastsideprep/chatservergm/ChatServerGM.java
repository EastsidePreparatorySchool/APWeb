/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatservergm;

import java.util.ArrayList;
import javax.servlet.MultipartConfigElement;
import static spark.Spark.*;

/**
 *
 * @author gmein
 */
public class ChatServerGM {

    static ArrayList<String> messages;

    public static void main(String[] args) {
        messages = new ArrayList<>();

        staticFiles.location("/static");
        get("/hello", (req, res) -> "Hello World");
        put("/putmessage", (req, res) -> putMessage(req));
        post("/postmessage", (req, res) -> postMessage(req));
        get("/getnewmessages", (req, res) -> getNewMessages(req, res));
    }

    public static String putMessage(spark.Request req) {

        Context ctx = getContextFromSession(req.session());
        System.out.println("put msg: " + req.body());
        messages.add(req.session().id() + ":" + req.body());
        return req.session().id();
    }

    public static String postMessage(spark.Request req) {
        Context ctx = getContextFromSession(req.session());

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        System.out.println("post msg: " + req.queryParams("message"));
        messages.add(req.session().id() + ":" + req.queryParams("message"));
        return req.session().id();
    }

    public static String getNewMessages(spark.Request req, spark.Response res) {
        System.out.println("entered getNewMessages");
        Context ctx = getContextFromSession(req.session());

        //
        StringBuilder result = new StringBuilder();
        for (int i = ctx.seen; i < messages.size(); i++) {
            String s = messages.get(i);
            
            if (s.startsWith(req.session().id())) {
                result.append(s.substring(req.session().id().length() + 1));
            } else {
                result.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                result.append(s);
            }
            result.append("<br>");
        }
        
        ctx.seen = messages.size();
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
