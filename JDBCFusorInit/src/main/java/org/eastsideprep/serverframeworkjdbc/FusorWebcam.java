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

/**
 *
 * @author azhuang
 */
public class FusorWebcam {

    /*
                Webcam streaming methods made by GitHub User Sarxos
                https://github.com/sarxos/webcam-capture
     */
    Webcam webcam;
    WebcamStreamer webcamStreamer;

    FusorWebcam() {
        this.webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
    }

    void activateStream() {
        this.webcamStreamer = new WebcamStreamer(8080, this.webcam, 0.5, true);
    }

    void terminateStream() {
        this.webcamStreamer.stop();
        this.webcam.close();
    }

}
