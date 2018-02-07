/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication2;

import java.nio.IntBuffer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author gmein
 */
public class JavaFXApplication2 extends Application {

    ImageView iv;
    Image img;
    Image old;
    int width;
    int height;
    VBox root;
    CheckBox zoom;

    @Override
    public void start(Stage primaryStage) {
        img = new Image("file:c:\\users\\gmein\\desktop\\test.png");
        this.width = (int) img.getWidth();
        this.height = (int) img.getHeight();

        Slider slider = new Slider();
        CheckBox btn = new CheckBox("Apply filter");
        btn.setOnAction((e) -> {
            if (btn.isSelected()) {
                filter(120000, slider.getValue() * 1000, 1000);
            } else {
                undo();
            }
        });

        zoom = new CheckBox("Zoom");
        zoom.setOnAction((e) -> {
            if (zoom.isSelected()) {
                this.iv.setFitHeight(10000);
            } else {
                this.iv.setFitHeight(900);
            }
        }
        );

        slider.valueProperty().addListener((f) -> {
            filter(120000, slider.getValue() * 1000, 1000);
            btn.setSelected(true);
        });

        slider.setPrefWidth(800);
        slider.setMin(0);
        slider.setMax(60);
        slider.setValue(60);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        HBox hb = new HBox();
        hb.getChildren().addAll(btn, slider, zoom);

        iv = new ImageView(img);
        iv.setPreserveRatio(true);
        iv.setFitHeight(900);

        root = new VBox();
        root.getChildren().addAll(hb, iv);

        Scene scene = new Scene(root, 0, 0);

        primaryStage.setTitle("Filter test");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void filter(double sampleRate, double cutoff, double notchwidth) {
        int[] input = new int[this.width];
        int[] output = new int[this.width];

        double RC = 1.0 / (cutoff * 2 * 3.14);
        double dt = 1.0 / sampleRate;
        double alpha = dt / (RC + dt);

        WritableImage wimg = new WritableImage(width, height);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = wimg.getPixelWriter();
        WritablePixelFormat<IntBuffer> pf = WritablePixelFormat.getIntArgbInstance();

        for (int line = 0; line < this.height; line++) {
            // read pixels
            pr.getPixels(0, line, this.width, 1, pf, input, 0, 0);

            // decode pixels
            for (int i = 0; i < this.width; i++) {
                input[i] = decode(input[i]);
            }

            // apply filter
            lineFilterLowPass(input, output, sampleRate, cutoff);
            //lineFilterNotch(input, output, sampleRate, cutoff, 4000);

            
            // encode pixels
            for (int i = 0; i < this.width; i++) {
                output[i] = encode(output[i]);
            }

            // write pixels
            pw.setPixels(0, line, this.width, 1, pf, output, 0, 0);
        }

        try {
            this.iv.setImage(wimg);

        } catch (Exception e) {
            this.iv = null;
        }
    }

    void lineFilterLowPass(int[] input, int[] output, double sampleRate, double f) {
        double RC = 1.0 / (f * 2 * 3.14);
        double dt = 1.0 / sampleRate;
        double alpha = dt / (RC + dt);
        output[0] = (input[0]+input[1]+input[2]+input[3])/4;
        for (int i = 1; i < this.width; i++) {
            output[i] = output[i - 1] + (int) (alpha * (input[i] - output[i - 1]));
        }
    }

    void lineFilterNotch(int[] x, int[] y, double sampleRate, double f, double bw) {
        f /= sampleRate;
        bw /= sampleRate;

        double[] a = {0, 0, 0};
        double[] b = {0, 0, 0};

        double R = 1 - 3 * bw;
        double K = (1 - 2 * R * Math.cos(2 * Math.PI * f) + R * R) / (2 - 2 * Math.cos(2 * Math.PI * f));

        a[0] = 1 - K;
        a[1] = 0 - 2 * K * Math.cos(2 * Math.PI * f);
        a[2] = K;
        b[1] = 2 * R * Math.cos(2 * Math.PI * f);
        b[2] = 0 - R * R;

        double x_2 = 0.0f;                    // delayed x, y samples
        double x_1 = 0.0f;
        double y_2 = 0.0f;
        double y_1 = 0.0f;

        for (int i = 0; i < x.length; ++i) {
            y[i] = (int) (a[0] * x[i] + a[1] * x_1 + a[2] * x_2 + b[1] * y_1 + b[2] * y_2);

            x_2 = x_1;    // shift delayed x, y samples
            x_1 = x[i];
            y_2 = y_1;
            y_1 = y[i];
        }
    }

    int decode(int pixel) {
        int highsix = (pixel & 0xFC) >> 2;
        int lowsix = (pixel & 0x03) << 4;
        lowsix += ((pixel & 0x300) >> 8) << 2;
        lowsix += (pixel & 0x30000) >> 16;
        return (highsix << 6) + lowsix;
    }

    int encode(int intensity) {
        if (intensity > 4095) {
            intensity = 4095;
        } else if (intensity < 0) {
            intensity = 0;
        }

        int highSix = ((intensity >> 6) & 0x3F) << 2;
        int lowSix = intensity & 0x3F;
        int r = lowSix & 0x3;
        lowSix >>= 2;
        int g = lowSix & 0x3;
        lowSix >>= 2;
        int b = lowSix & 0x3;

        return 0xFF000000 // full alpha
                + ((highSix + r) << 16) // red
                + ((highSix + g) << 8) // green
                + (highSix + b);         // blue
    }

    public void undo() {
        this.iv.setImage(this.img);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
