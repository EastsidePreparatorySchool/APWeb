/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.semconsolewebapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.*;

public class FileUpload {
    final static String LOCAL_PATH = "http://localhost:13080/";
    final static String DEPOLYED_PATH = "http://semphotogallery.appspot.com/";
    
    final static boolean IS_LOCAL = false;
    
    public static void uploadFileToServer(Part[] parts) throws FileNotFoundException, IOException, MalformedURLException {
        String stem = DEPOLYED_PATH;
        if (IS_LOCAL) {
            stem = LOCAL_PATH;
        }

        String uploadUrl = getUploadURL(stem + "geturl");
        uploadImage(uploadUrl, parts);
    }
    
    public static void uploadImage(String urlString, Part[] parts) throws FileNotFoundException, IOException {
        PostMethod postMessage = new PostMethod(urlString);
        postMessage.setRequestEntity(new MultipartRequestEntity(parts, postMessage.getParams()));
        HttpClient client = new HttpClient();
        
        int status = client.executeMethod(postMessage);
        System.out.println("Got status message " + status);
    }
    public static String getUploadURL(String reqURL) throws MalformedURLException, IOException {
        
        URL url = new URL(reqURL);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(url.openStream()));

        return in.readLine(); // Will only ever return one line, so we don't need a loop
    }
    
    public static void uploadFileAndMetaDataToServer(String imagePath, String operators, String channel, int kv, int mag, int wd) throws FileNotFoundException {
        File f = new File(imagePath);
        Part[] parts = {
                new StringPart("working_depth", Integer.toString(wd)),
                new StringPart("magnification", Integer.toString(mag)),
                new StringPart("voltage", Integer.toString(kv*1000)),
                new StringPart("operators", operators),
                new StringPart("sensor", channel),
                new StringPart("update_metadata", "1"),
                new FilePart("img", f)
        };
        try {
            uploadFileToServer(parts);
        } catch (IOException e) {}
    }

    public static void uploadFileToServer(String imagePath) {
        String stem = DEPOLYED_PATH;
        if (IS_LOCAL) {
            stem = LOCAL_PATH;
        }

        try {
            String uploadURL = getUploadURL(stem + "geturl");
            Part[] p = {new FilePart("img", new File(imagePath))};
            uploadImage(uploadURL, p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
