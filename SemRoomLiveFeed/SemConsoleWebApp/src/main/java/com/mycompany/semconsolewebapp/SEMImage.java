/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.semconsolewebapp;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

/**
 *
 * @author gmein
 */
public class SEMImage {

    public int channels;
    public int width;
    public int height;
    public WritableImage[] images;
    public int[] capturedChannels;
    public int kv;
    public int magnification;
    public int wd;
    public String operators;

    private PixelWriter[] writers;
    private PixelFormat pf;
    private WritablePixelFormat<IntBuffer> format;

    private int[] lineBuffer;
    ArrayList<int[]> aRawLineBuffers;
    public int rangeMin[];
    public int rangeMax[];
    public int rangeMaxLine[];
    public int maxLine = 0;
    public String imageNames[];
    public LineBuffer lb;
    public int lineCounter;

    // for construction from stereo pairs only
    SEMImage left = null;
    SEMImage right = null;
    String fileName = null;

    Image thumbnail = null;

    SEMImage(int channels, int[] capturedChannels, int width, int height, int kv, int mag, int wd, String operators) {
        this.format = WritablePixelFormat.getIntArgbInstance();
        this.channels = channels;
        this.width = width;
        this.height = height;
        this.kv = kv;
        this.magnification = mag;
        this.wd = wd;
        this.operators = operators;

        this.lineBuffer = new int[width];
        this.aRawLineBuffers = new ArrayList<>(3000);
        this.capturedChannels = new int[channels];
        this.rangeMin = new int[channels];
        this.rangeMax = new int[channels];
        this.rangeMaxLine = new int[channels];

        images = new WritableImage[channels];
        writers = new PixelWriter[channels];
        imageNames = new String[channels];

        System.arraycopy(capturedChannels, 0, this.capturedChannels, 0, channels);

        //
        // make line buffer pool
        //
        this.lb = LineBuffer.grabLineBuffer(width, height);
        this.lineCounter = 0;
    }

    SEMImage(String fileName) {
        // img_<count>_channel-<capturedchannel>.png>
        this.format = null;
        this.lb = null;
        this.lineCounter = 0;
        this.lineBuffer = null;
        this.aRawLineBuffers = null;
        this.rangeMin = null;
        this.rangeMax = null;
        this.rangeMaxLine = null;
        this.writers = null;

        this.width = -1;
        this.height = -1;
        this.kv = -1;
        this.magnification = -1;
        this.wd = -1;
        this.operators = null;
        this.channels = 1;
        this.capturedChannels = new int[]{-1};
        
        Session.parseFileName(fileName, this);

        this.images = new WritableImage[1];
        this.imageNames = new String[]{fileName};
    }

    SEMImage(SEMImage left, SEMImage right) {
        this.left = left;
        this.right = right;
        this.format = WritablePixelFormat.getIntArgbInstance();
        this.channels = Math.min(left.channels, right.channels);
        this.width = Math.min(left.width, right.width);
        this.height = Math.min(left.height, right.height);
        this.kv = left.kv;
        this.magnification = left.magnification;
        this.wd = left.wd;
        this.operators = left.operators;

        this.capturedChannels = new int[channels];
        System.arraycopy(left.capturedChannels, 0, this.capturedChannels, 0, channels);

        images = new WritableImage[channels];
        writers = new PixelWriter[channels];
        imageNames = new String[channels];

        // allocate images
        for (int i = 0; i < channels; i++) {
            images[i] = new WritableImage(width, height);
            writers[i] = images[i].getPixelWriter();
        }
    }

    void dehydrate() {
        for (int i = 0; i < channels; i++) {
            if (imageNames[i] != null) {
                images[i] = null;
            }
        }
    }

    void rehydrate() {
        for (int i = 0; i < channels; i++) {
            if (imageNames[i] != null) {
                // todo: reload image from file;
            }
        }
    }

    void knitStereoImage() {
        int[] leftBuffer = new int[this.width];
        int[] rightBuffer = new int[this.width];
        for (int i = 0; i < this.channels; i++) {
            PixelReader prLeft = left.images[i].getPixelReader();
            PixelReader prRight = right.images[i].getPixelReader();
            PixelWriter pw = this.images[i].getPixelWriter();

            for (int line = 0; line < this.height; line++) {
                prLeft.getPixels(0, line, this.width, 1, this.format, leftBuffer, 0, 0);
                prRight.getPixels(0, line, this.width, 1, this.format, rightBuffer, 0, 0);

                for (int pixel = 0; pixel < this.width; pixel++) {
                    leftBuffer[pixel] = combinePixels(leftBuffer[pixel], rightBuffer[pixel], this.capturedChannels[i]);
                }

                pw.setPixels(0, line, this.width, 1, this.format, leftBuffer, 0, 0);
            }
        }
    }

    int[] getNextDataLine() {
        if (lineCounter >= height) {
            return null;
        }
        return this.lb.data[lineCounter++];

    }

    void fileDataLine(int line, int[] data, int count) {
        if (line < 0) {
            return;
        }

        data[count] = line;

        this.aRawLineBuffers.add(data);
        if (line > maxLine) {
            maxLine = line;
        }

    }

    // keep track of min and max for a line, for all channels
    void rangeLine(int line, int[] data, int count
    ) {
        int intensity;

        for (int channel = 0; channel < this.channels; channel++) {
            for (int i = channel; i < count; i += this.channels) {
                intensity = getValue(data[i]);
                recordRange(channel, line, intensity);
            }
        }
    }

    //todo: is this what we want?
    int autoContrast(int value, int min, int max
    ) {
        //return value;
        int newValue = (int) (((double) value - (double) min) * (double) 4095 / ((double) max - (double) min));
        if (newValue > 4095) {
            newValue = 4095;
        } else if (newValue < 0) {
            newValue = 0;
        }
        return newValue;
    }

    //
    // extract separate image lines from one line of data
    //
    void parseRawLineToWriters(int line, int[] data, int count
    ) {
        int pixel;
        int capturedChannel;
        int writeChannel;
        int intensity;

        if (line >= height) { // since we adjusted downward for jitter, some lines might be too high in number now.
            return;
        }

        for (int channel = 0; channel < this.channels; channel++) {
            // copy one line of pixels for a specific channel out of data into rawBuffer
            pixel = 0;
            capturedChannel = translateChannel(getEncodedChannel(data[channel]));
            for (int i = channel; i < count; i += this.channels) {
                intensity = getValue(data[i]);
                intensity = autoContrast(intensity, rangeMin[channel], rangeMax[channel]);

                lineBuffer[pixel++] = grayScale(capturedChannel, intensity);
            }

            // find the right image to write into
            writeChannel = 0;
            for (int i = 0; i < this.channels; i++) {
                if (this.capturedChannels[i] == capturedChannel) {
                    writeChannel = i;
                    break;
                }
            }
            // write rawBuffer into images[writeChannel]
            try {
                writers[writeChannel].setPixels(0, line, this.width, 1, this.format, lineBuffer, 0, this.width);
            } catch (Exception e) {
                System.out.println("Write failed: " + line + ", " + height);
                System.out.println(e.getStackTrace());

            }
        }
    }

    public void rangeImages() {
        if (aRawLineBuffers.isEmpty()) {
            return;
        }

        int size = aRawLineBuffers.size();

//        if (maxLine + 1 > height) {
//            this.height = maxLine + 1;
//        }
        for (int i = 0; i < channels; i++) {
            rangeMin[i] = 4095;
            rangeMax[i] = 0;
        }

        // compute ranges from first 75% of image. ignore duplicates as best we can
        int prevLine = -1;
        for (int i = 0; i < size; i++) {
            int[] data = aRawLineBuffers.get(i);

            int line = data[data.length - 1];
            if (line != prevLine) {
                rangeLine(line, data, data.length - 1); // don't range that last int, which is the line number
            }
            prevLine = line;
        }
        //Console.printOn();
        //Console.println(""+rangeMin[0]+" "+rangeMax[0]);
    }

    public void makeImagesForDisplay() {
        if (aRawLineBuffers == null || aRawLineBuffers.isEmpty()) {
            return;
        }

        // already done?
        if (images[0] != null) {
            return;
        }

        // compute min and max for contrast
        rangeImages();

        int size = aRawLineBuffers.size();

        // allocate images
        for (int i = 0; i < channels; i++) {
            images[i] = new WritableImage(width, height);
            writers[i] = images[i].getPixelWriter();
        }

        // parse all lines, correcting data values for ranges
        int prevLine = -1;
        for (int i = 0; i < size; i++) {
            int[] lineData = aRawLineBuffers.get(i);
            int line = (lineData[lineData.length - 1]) + (height - (maxLine + 1)); // correct for vsync jitter by aligning at bottom
            if (line < 0) {
                line = 0;
            }
            while (++prevLine <= line) {
                this.parseRawLineToWriters(prevLine, lineData, lineData.length - 1);
            }
            prevLine = line;
        }

        cleanUp();
    }

    // get the encoded channel number from a word in the data stream
    int getEncodedChannel(int word) {
        return (word >> 12);

    }

    // get the raw value of the ADC reading, and adjust it to fit into a byte
    int getValue(int word) {
        word = ((word & 0xFFF));
        if (word > 4095) {
            word = 4095;
        }
        if (word < 0) {
            word = 0;
        }
        return word;
    }

    void recordRange(int channel, int line, int intensity) {
        if (intensity < rangeMin[channel]) {
            rangeMin[channel] = intensity;
        }

        if (intensity > rangeMax[channel]) {
            rangeMax[channel] = intensity;
            rangeMaxLine[channel] = line;
        }
    }

    // converts intensity into a kind of gray scale, 6 bits are distributed evenly, 2+2+2 divided up between the colors
    // that way, we can save and later parse all the data with no losses
    int grayScale(int realChannel, int intensity) {
        // todo: real gain calibration
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

    // makes a red/blu stereo pixel out of two source pixels
    // decodes intensities, put 8bit + 8bit back together again
    // presumes that source pixels were computed by grayScale() (see above)
    int combinePixels(int left, int right, int realChannel) {
        int intensityLeft = left & 0xFF; // blue contained the high 8 bits of intensity
        int intensityRight = right & 0xFF;

        return (0xFF000000 // full alpha
                + (intensityLeft << 16) // left becomes red
                + (intensityRight)); // right becomes blue
    }

// maps encoded Arduino ADC channel tags into Ax input pin numbers (7 -> A0, 6-> A1 etc.)
    int translateChannel(int word) {
        return 7 - word;
    }

    void cleanUp() {
        for (int i = 0; i < channels; i++) {
            writers[i] = null;
        }

        format = null;
        pf = null;

        lineBuffer = null;
        aRawLineBuffers = null;
        LineBuffer.returnLineBuffer(this.lb);

    }
}
