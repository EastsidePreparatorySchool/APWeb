/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.serverframeworkjdbc;

import com.github.sarxos.webcam.Webcam;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
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
        get("/showface", (req, res) -> useWebcam(req, fws));
        post("/login", (req, res) -> logSessionHandler(req));
        
        post("upload", (req, res) -> uploadFile(req, res));     //uploading pictures    

        put("/protected/put", (req, res) -> putHandler(req));
        post("/protected/post", (req, res) -> postHandler(req));
        get("/protected/get", "application/json", (req, res) -> getHandler(req), new JSONRT());

        get("/protected/gettables", "application/json", (req, res) -> getTablesHandler(req), new JSONRT());

        before("/protected/*", (req, res) -> {
            if (req.session().attribute("initials") == null) {
                halt(401, "You must login.");
            }
        });

        put("/login", (req, res) -> login(req));
    
    get("/download", "application/json", (req, res) -> getint(req), new JSONRT());

        get("/upload/download", (req, res) -> {
            String y = req.queryParams("arg1"); //get index from params
            int x = Integer.parseInt(y);
            System.out.println("x is: " + x);
            Context ctx = getContextFromSession(req.session());
            ArrayList<byte[]> images = ctx.db.getImage(req, res); //arraylist of byte array containing all DB images

            byte[] image = images.get(x);  //get image based on param

            HttpServletResponse raw = res.raw(); //use Java Servlets to get raw response
            res.header("Content-Disposition", "attachment; filename=image.jpg"); // set response header (communicates between server and requests)
            res.type("application/force-download"); //use Servlets force download functionality
            try {
                //this is for writing to file

                //change the imagetofolder route to wherever you want the images to go to
                FileOutputStream imagetofolder = new FileOutputStream("src/main/resources/design/image" + x + ".jpg"); //set OutputStream location to folder
                imagetofolder.write(image); //write into the output streeam
                imagetofolder.flush(); //takes care of buffered output
                imagetofolder.close(); //close the stream

                //this is for displaying on webpage
                raw.getOutputStream().write(image); //getOutputStream returns a stream for writing binary data (like our blob)
                raw.getOutputStream().flush(); //takes care of buffered output
                raw.getOutputStream().close(); //closes the stream
                return raw; //return written stream
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        });
        before("/protected/*", (req, res) -> {
            if (req.session().attribute("initials") == null) {
                halt(401, "You must login.");
            }
        });
    
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
        req.session().attribute("initials", req.body());
        String input = req.queryParams("initials");
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