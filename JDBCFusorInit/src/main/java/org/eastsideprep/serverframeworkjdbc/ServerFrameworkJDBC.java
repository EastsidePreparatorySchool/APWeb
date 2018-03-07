/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.serverframeworkjdbc;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import static spark.Spark.*;
import spark.Request;

/**
 *
 * @author gmein
 */
public class ServerFrameworkJDBC {

    static ArrayList<String> messages;

    public static void main(String[] args) {
        FusorWebcam fw = new FusorWebcam();

        staticFiles.location("/static");
        get("/hello", (req, res) -> hello(req), new JSONRT());
        get("/showface", (req, res) -> useWebcam(req, fw));
        post("/login", (req, res) -> logSessionHandler(req));
        
        post("upload", (req, res) -> uploadFile(req, res));     //uploading pictures    

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

    public static String useWebcam(spark.Request req, FusorWebcam fw) {
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
            fw.activateStream();
            return "Stream active!";
        } else {
            fw.terminateStream();
            return "Stream terminated.";
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
    
    static Object uploadFile(spark.Request request, spark.Response response) {
        //System.out.println("upload");
        Context ctx = getContextFromSession(request.session()); //normal posthandler code
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
        //System.out.println("fidosdjkff");
        try {
            Part file = request.raw().getPart("scannedfile"); //gets file as a part
            //System.out.println("File string: " + file.toString());
            ctx.db.addImages(request, file); //database method to complete upload
//            final Path path = Paths.get(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") 
//                     + file.getSubmittedFileName()); //in case you want to upload the image to a file, leaving
                                                        //this here just in case
//            Files.copy(in, path);
            System.out.println("Uploaded file " + file.getSubmittedFileName());
            return "Uploaded file " + file.getSubmittedFileName();
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return "Should not get here";

    }
    
    public static Object logSessionHandler(spark.Request req) { //handler to log a new session in the db, first logs users in the session
        //then data about the session
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement); //necessary post code
        System.out.println("entered logSessionHandler"); //for debugging

        Context ctx = getContextFromSession(req.session());
        //System.out.println("MSFDOIJFJKF");
        //String input = req.queryParams("init");
        String input = "Steve Harvey";
        if (input.contains(" ")) {
            //System.out.println("sdkffjdsdfmk");
            String firstName = input.substring(0, input.indexOf(" ")); //input must be formatted in First + Last
            //gets first and last name based on location of space
            String lastName = input.substring(input.indexOf(" ") + 1);
            String initials = firstName.substring(0, 1) + lastName.substring(0, 1); //concatonates first letter of 
            //both names to get initials
            ctx.db.registerOperator(req, firstName, lastName, initials); //register operator if not already registered
            ctx.db.logSession(req); //log the session
            //System.out.println(initials + " initials of operator"); //for debugging
            return firstName + " " + lastName;
        }
       
        //System.out.println(m + "from addHandler"); for debugging
        
        
        return "Error in input, please enter your name in format: First Last";
}
}