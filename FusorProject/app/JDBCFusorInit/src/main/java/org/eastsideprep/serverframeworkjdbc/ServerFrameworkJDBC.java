/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.serverframeworkjdbc;

import com.github.sarxos.webcam.Webcam;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.MultipartConfigElement;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.Request;

/**
 *
 * @author gmein
 */
public class ServerFrameworkJDBC {

    static ArrayList<String> messages;

    public static void main(String[] args) {
        ArrayList<FusorWebcam> fws = getWebcams();

        staticFiles.location("/static");
        get("/hello", (req, res) -> hello(req), new JSONRT());
        get("/showface", (req, res) -> useWebcam(req, fws), new VelocityTemplateEngine());

        put("/protected/put", (req, res) -> putHandler(req));
        post("/protected/post", (req, res) -> postHandler(req));
        get("/protected/get", "application/json", (req, res) -> getHandler(req), new JSONRT());

        get("/protected/gettables", "application/json", (req, res) -> getTablesHandler(req), new JSONRT());
        get("/protected/getdukakisfilms", "application/json", (req, res) -> getDukakisHandler(req), new JSONRT());

        before("/protected/*", (req, res) -> {
            if (req.session().attribute("initials") == null) {
                halt(401, "You must login.");
            }
        });

        put("/login", (req, res) -> login(req));
    }
    
    public static ArrayList<FusorWebcam> getWebcams() {
        ArrayList<FusorWebcam> fws = new ArrayList<FusorWebcam>(); //list of FusorWebcam objects to return
        List<Webcam> ws = Webcam.getWebcams(); //get list of all available webcams connected to computer
        int i = 0;
        for (Webcam w: ws) {
            FusorWebcam fw = new FusorWebcam(w, i); //creates new FusorWebcam object for each webcam
            fws.add(fw);
            i++;
        }
        return fws;
    }

    public static ModelAndView useWebcam(spark.Request req, ArrayList<FusorWebcam> fws) {
        String onoff = req.queryParams("onoff");
        System.out.println(onoff);
        /*
                Webcam streaming methods made by GitHub User Sarxos
                https://github.com/sarxos/webcam-capture
         */
        //WebcamStreamer webcamStreamer = new WebcamStreamer(8080, webcam, 0.5, true);
        /*do {
                Thread.sleep(5000);
            } while (onoff.equalsIgnoreCase("on"));
         */
        if (onoff.equalsIgnoreCase("on")) {
            int i = 0;
            ArrayList<String> url = new ArrayList();
            for(FusorWebcam fw: fws) {
                fw.activateStream();
                url.add("http://10.20.81.244:" + (8080+i) + "/");
                i++;
            }
            Map<String, Object> model = new HashMap<>();
            model.put("urls", url);
            return new ModelAndView(model, "template.vm");
//            return "Stream active!";
        } else {
            for(FusorWebcam fw: fws) {
            fw.terminateStream();
            }
            return new ModelAndView("nothing", "template.vm");
        }
    }

    private static String login(spark.Request req) {
        req.session().attribute("initials", req.body());
        return req.body();
    }

    private static Object hello(spark.Request req) {
        Context ctx = getContextFromSession(req.session());

        String initials = req.session().attribute("initials");
        if (initials == null) {
            initials = "<not logged in>";
        }

        return "Hello World, " + initials;
    }

    public static String putHandler(spark.Request req) {
        Context ctx = getContextFromSession(req.session());
        System.out.println("put: " + req.body());
        return "put handled";
    }

    public static String postHandler(spark.Request req) {
        Context ctx = getContextFromSession(req.session());

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

        System.out.println("post 'message': " + req.queryParams("message"));
        return "post handled";
    }

    public static Object getHandler(spark.Request req) {
        System.out.println("entered getNewMessages");
        Context ctx = getContextFromSession(req.session());

        return "Here is what you got";
    }

    public static Object getTablesHandler(spark.Request req) {
        System.out.println("entered getNewMessages");
        Context ctx = getContextFromSession(req.session());

        return ctx.db.showTables();
    }

    public static Object getDukakisHandler(spark.Request req) {
        System.out.println("entered getNewMessages");
        Context ctx = getContextFromSession(req.session());

        return ctx.db.showFilmsWithRockDukakis();
    }

    public static Context getContextFromSession(spark.Session s) {
        Context ctx = s.attribute("Context");
        if (ctx == null) {
            ctx = new Context(s.attribute("initials"));
            s.attribute("Context", ctx);
        }

        return ctx;
    }
}
