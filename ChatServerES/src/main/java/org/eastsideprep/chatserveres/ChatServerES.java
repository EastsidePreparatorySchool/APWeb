/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatserveres;

import static spark.Spark.get;
import static spark.Spark.staticFiles;
import static spark.Spark.put;
import java.util.*;

/**
 *
 * @author ESchreiber
 */
public class ChatServerES {
    static ArrayList<String> messages = null;
    public static void main(String[] args) {
        messages = new ArrayList<String>();
        staticFiles.location("/static");
        put("/putmessage", (req, res) -> putMessage(req));
        get("/getnewmessages", (req, res) -> getNewMessages(req, res));
        get("/hello", (req, res) -> syncTest());
        get("/Sandwich", (req, res) -> "Que Interesante");
        get("/shutdown", (req, res) -> {System.exit(0); return 0;});
    }
    public static boolean putMessage(spark.Request request) {
        messages.add(request.session().id()+": "+request.body());
        return true;
    }
    public static String getNewMessages(spark.Request request, spark.Response response) {
        Context ctx = getCtxFromSession(request.session());
        StringBuilder result = new StringBuilder();
        for(int i = ctx.seen; i <messages.size(); i++) {
            String s = messages.get(i);
            if (s.startsWith(request.session().id())) {
                result.append(s.substring(request.session().id().length() + 1));
            }
            else {
                result.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                result.append(s);
            }
            result.append("<br>");
        }
        ctx.seen = messages.size();
        return result.toString();
    }
    public static String syncTest() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
        }
        return "Hello World";
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
