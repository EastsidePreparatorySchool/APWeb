/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.serverframeworkjdbc;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.MultipartConfigElement;
import static spark.Spark.*;
import com.github.sarxos.webcam.Webcam;
import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamStreamer;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import java.net.MalformedURLException;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

/**
 *
 * @author azhuang
 */
public class FusorWebcam {

    /*
                Webcam streaming methods made by GitHub User Sarxos
                https://github.com/sarxos/webcam-capture
     */
    Webcam webcam; //webcam object
    WebcamStreamer webcamStreamer; //webcam stream object
    int index;

    FusorWebcam(Webcam w, int index) {
        this.webcam = w; //uses server host's default webcam
        w.setViewSize(WebcamResolution.VGA.getSize());
        this.index = index;

        //uses default webcam resolution
    }

    void activateStream() {
        this.webcamStreamer = new WebcamStreamer(8080 + this.index, this.webcam, 0.5, true); //starts stream, steam is an image stream with 0.5fps, you can change the fps setting.
        System.out.println("Stream " + index + " activated on port " + (8080 + index));

    }

    void terminateStream() {
        this.webcam.close();
        this.webcamStreamer.stop();
    }

}
