package com.mycompany.semconsolewebapp;

import dk.thibaut.serial.SerialChannel;
import dk.thibaut.serial.SerialPort;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.LinkedTransferQueue;
import javafx.application.Platform;

public class SEMPort {

    public enum SEMError {
        ERROR_WRONG_PHASE,
        ERROR_BYTE_COUNT,
        ERROR_VALUE,
        ERROR_CHECK_SUM,
        ERROR_UNKNOWN_COMMAND,
        ERROR_OTHER

    }

    public class SEMException extends Exception {

        SEMError error;

        SEMException(SEMError error
        ) {
            this.error = error;
        }
    }

    String name = "";
    SerialPort port;
    SerialChannel channel;
    ByteBuffer buffer;
    int dotCounter = 0;
    int numErrors = 0;
    int numOKs = 0;
    int lastBytes = 0;
    long lastTime = 0;

    //int[] rawMultiChannelBuffer;
    private int rawLength = 0;
    static private final byte[] msgBuffer = new byte[16];
    static private final int[] capturedChannels = new int[4];

    ByteBuffer commandConnect = ByteBuffer.wrap("EPS_SEM_CONNECT.".getBytes(StandardCharsets.UTF_8));

    static private final ByteBuffer commandAbort = ByteBuffer.wrap("AB".getBytes(StandardCharsets.UTF_8));
    static private final ByteBuffer commandChannelSelect = ByteBuffer.wrap("CH0".getBytes(StandardCharsets.UTF_8));
    static private final ByteBuffer commandOk = ByteBuffer.wrap("OK".getBytes(StandardCharsets.UTF_8));

    int proposedBytes;
    long frameStartTime;
    SEMImage si;

    SEMPort() {
    }

    public void initialize() throws Exception {

        ArrayList<String> portNames = null;

        // Get a list of available ports names (COM2, COM4, ...)
        try {
            // copy this so it does not change underneath us while iterating
            portNames = new ArrayList<>(SerialPort.getAvailablePortsNames());
        } catch (Exception e) {
            Console.println("SEMPort: No ports on this system");
            throw e;
        }
        System.out.println("Ports: " + portNames.toString());

        for (String name : portNames) {
            // Get a new instance of SerialPort by opening a port.
            try {
                System.out.println();
                System.out.println("Opening port: " + name);
                this.port = SerialPort.open(name);
            } catch (IOException e) {
                System.out.println(e.toString());
                continue;
            } catch (NullPointerException n) {
                System.out.println(n.toString());
                continue;
            }
            try {
                // Configure the connection
                port.setTimeout(300);

                channel = port.getChannel();
                //ostream = port.getOutputStream();
                //istream = port.getInputStream();
                buffer = ByteBuffer.allocateDirect(32000);
                buffer.order(ByteOrder.LITTLE_ENDIAN);

                commandConnect.rewind();
                channel.write(commandConnect);

                int n = channel.read(buffer);
                if (n != 0) {
                    buffer.position(0);
                    buffer.get(msgBuffer);
                    String result = new String(msgBuffer);
                    if (result.equals("EPS_SEM_READY...")) {
                        return;
                    } else {
                        System.out.println("Wrong answer: 0x" + Integer.toHexString(msgBuffer[0]) + " 0x" + Integer.toHexString(msgBuffer[0]));
                    }
                } else {
                    System.out.println("No answer.");
                }
            } catch (Exception e) {
                Console.println(e.toString());

            }
            if (port != null) {
                port.close();
                port = null;
            }
            System.out.println("Next port");
        }
        System.out.println("SEMPort: SEM port not found or no answer.");
        throw new Exception("SEMPort: SEM port not found or no answer.");
    }

    void shutdown() {
        try {
            if (this.port != null) {
                // send abort
                commandAbort.rewind();
                channel.write(commandAbort);
                // drain all messages

                // wait for ack
                for (int i = 0; i < 10; i++) {
                    try {
                        channel.flush(true, true);

                        buffer.position(0);
                        buffer.limit(16); // look for commands

                        int n = channel.read(buffer);
                        //System.out.println("[read " + n + "bytes]");
                        if (n != 0) {
                            buffer.position(0);
                            buffer.get(msgBuffer);
                            String message = new String(msgBuffer);
                            //System.out.println(result);

                            if (message.equals("EPS_SEM_RESET...")) {
                                break;
                            }
                        }
                        Thread.sleep(200);
                    } catch (Exception e) {
                    }
                }
                port.close();
                this.port = null;
            }
            name = "";
        } catch (Exception e) {
        }
    }

    SEMThread.Phase processMessage(LinkedTransferQueue<SEMImage> ltq,
            Runnable updateDisplayLambda, Runnable updateScanning, Runnable updateMeta,
            SEMThread.Phase phase) {
        SEMThread.Phase result = phase;
        String message;
        byte[] ab;
        int checkSum = 0;
        int checkSumRead = 0;
        int lines = 10;

        try {
            buffer.position(0);
            buffer.limit(16); // look for commands

            int n = channel.read(buffer);
            //System.out.println("[read " + n + "bytes]");
            if (n != 0) {
                buffer.position(0);
                buffer.get(msgBuffer);
                message = new String(msgBuffer);
                //System.out.println(result);

                switch (message) {

                    case "EPS_SEM_RESET...":
                        Console.printOn();
                        Console.println();
                        Console.println("Reset");
                        return SEMThread.Phase.ABORTED;

                    case "EPS_SEM_IDLE....":
                        // read scanline of unrecognized freq
                        buffer.rewind();
                        buffer.limit(8);
                        n = channel.read(buffer);
                        //System.out.print("[read " + n + "bytes]");
                        if (n != 8) {
                            throw new SEMException(SEMError.ERROR_BYTE_COUNT);
                        }
                        buffer.flip();

                        // read line number (unsigned short)
                        int reason = buffer.getInt();
                        int argument = buffer.getInt();

                        Console.printOn();

                        switch (reason) {
                            case 0: // truly idle
                                if (System.currentTimeMillis() > lastTime + 1000) {
                                    lastTime = System.currentTimeMillis();
                                    Console.println("[SEM idle]");
                                    SEMThread.progress = -1;
                                    Platform.runLater(updateScanning);
                                }
                                break;
                            case 1: // unrecognized scan time
                                if (System.currentTimeMillis() > lastTime + 500) {
                                    lastTime = System.currentTimeMillis();
                                    SEMThread.progress = -1;
                                    Platform.runLater(updateScanning);
                                    Console.print("[SEM resolution not recognized]");
                                    Console.println("[" + argument + "]");
                                }
                                break;
                            case 2: // changed scan time
                                Console.println("[SEM resolution changed]");
                                break;
                            case 3: // other vsync
                                Console.println("[SEM vsync]");
                                break;
                            default:
                                Console.print("[SEM idle reason unknown]");
                                Console.println("[argument:" + argument + "]");
                                break;
                        }
                        dotCounter++;
                        return SEMThread.Phase.WAITING_FOR_FRAME;

                    case "EPS_SEM_META....":
                        // read scanline of unrecognized freq
                        buffer.rewind();
                        buffer.limit(12);
                        n = channel.read(buffer);
                        //System.out.print("[read " + n + "bytes]");
                        if (n != 12) {
                            throw new SEMException(SEMError.ERROR_BYTE_COUNT);
                        }
                        buffer.flip();

                        // read line number (unsigned short)
                        SEMThread.mag = buffer.getInt();
                        SEMThread.kv = buffer.getInt();
                        SEMThread.wd = buffer.getInt();

                        if (SEMThread.mag != SEMThread.oldmag
                                || SEMThread.kv != SEMThread.oldkv
                                || SEMThread.wd != SEMThread.oldwd) {
                            SEMThread.oldmag = SEMThread.mag;
                            SEMThread.oldkv = SEMThread.kv;
                            SEMThread.oldwd = SEMThread.wd;
                            Platform.runLater(updateMeta);
                        }

                        return SEMThread.Phase.WAITING_FOR_FRAME;
                    case "EPS_SEM_FRAME...":
                        if (phase != SEMThread.Phase.WAITING_FOR_FRAME) {
                            throw new SEMException(SEMError.ERROR_WRONG_PHASE);
                        }
                        dotCounter = 0;
                        numErrors = 0;
                        numOKs = 0;
                        lastBytes = 0;

                        // read channel count, width, height
                        buffer.rewind();
                        buffer.limit(16);
                        n = channel.read(buffer);
                        //System.out.print("[read " + n + "bytes]");
                        if (n != 16) {
                            throw new SEMException(SEMError.ERROR_BYTE_COUNT);
                        }
                        buffer.flip();

                        int channelCount = Short.toUnsignedInt(buffer.getShort());
                        int width = Short.toUnsignedInt(buffer.getShort());
                        int height = Short.toUnsignedInt(buffer.getShort());
                        int time = Short.toUnsignedInt(buffer.getShort());
                        frameStartTime = System.currentTimeMillis();

                        if (time > 1000 || System.currentTimeMillis() > lastTime + 1000) {
                            lastTime = System.currentTimeMillis();

                            Console.printOn();
                            Console.print("Start of frame: ");
                            Console.print("width: " + width + ", height: " + height);
                            Console.print(", line scan time: ");
                            Console.print("" + ((long) time));
                            Console.print("us");
                            Console.print(", channels: ");
                        } else {
                            Console.printOff();
                        }

                        if (height > 1500) {
                            SEMThread.progress = -1;
                            Platform.runLater(updateScanning);
                        }

                        for (int i = 0; i < 4; i++) {
                            capturedChannels[i] = Short.toUnsignedInt(buffer.getShort());
                            if (i < channelCount) {
                                Console.print(capturedChannels[i] + " ");
                            }
                        }
                        this.proposedBytes = channelCount * width * 2;

                        /*// allocate buffer and image
                        if (rawLength < channelCount * width) {
                            rawLength = channelCount * width;
                            rawMultiChannelBuffer = new int[rawLength];
                        }*/
                        this.si = new SEMImage(channelCount, capturedChannels, width, height, SEMThread.kv, SEMThread.mag, SEMThread.wd, SEMThread.operators);
                        result = SEMThread.Phase.WAITING_FOR_BYTES_OR_EFRAME;
                        lastBytes = this.proposedBytes + 24;
                        break;

                    case "EPS_SEM_BYTES...":
                        if (phase != SEMThread.Phase.WAITING_FOR_BYTES_OR_EFRAME) {
                            throw new SEMException(SEMError.ERROR_WRONG_PHASE);
                        }
                        // read whole line if length known
                        if (lastBytes != 0) {
                            buffer.rewind();
                            buffer.limit(lastBytes);
                            n = channel.read(buffer);
                            //System.out.print("[prefetch read " + n + "bytes]");
                            if (n != lastBytes) {
                                dotCounter++;
                                throw new SEMException(SEMError.ERROR_BYTE_COUNT);
                            }
                            buffer.flip();
                        }

                        // read check sum
                        checkSumRead = buffer.getInt();
                        // read line number (unsigned short)
                        int line = Short.toUnsignedInt(buffer.getShort());
                        //System.out.println(line);
                        // read byte count (unsigned short)
                        int bytes = Short.toUnsignedInt(buffer.getShort());

                        // read line bytes
                        checkSum = bytes + line;
                        if (bytes != this.proposedBytes) {
                            dotCounter++;

                            throw new SEMException(SEMError.ERROR_BYTE_COUNT);
                        }

                        int word;
                        int[] nextLineBuffer = this.si.getNextDataLine();
                        if (nextLineBuffer != null) {
                            for (int i = 0; i < bytes / 2; i++) {
                                word = Short.toUnsignedInt(buffer.getShort());
                                nextLineBuffer[i] = word;
                                checkSum += word;
                            }

                            if (checkSum != checkSumRead) {
                                throw new SEMException(SEMError.ERROR_CHECK_SUM);
                            }

                            this.si.fileDataLine(line, nextLineBuffer, bytes / 2);
                        }
                        // print dot for successful lines, send "ok", process line
                        if (++dotCounter % lines == 0) {
                            if (this.si.height > 600) {
                                Console.print(".");
                                if (this.si.height > 1500) {
                                    SEMThread.progress = ((double) line) / (double) si.height;
                                    Platform.runLater(updateScanning);
                                }
                            }
                        }
                        numOKs++;
                        result = SEMThread.Phase.WAITING_FOR_BYTES_OR_EFRAME;
                        break;

                    case "EPS_SEM_ENDFRAME":
                        if (phase != SEMThread.Phase.WAITING_FOR_BYTES_OR_EFRAME) {
                            throw new SEMException(SEMError.ERROR_WRONG_PHASE);
                        }

                        if (this.si.height > 1500) {
                            SEMThread.progress = 1.0;
                            Platform.runLater(updateScanning);
                        }
                        Console.println();
                        Console.print("End of frame. Max line adc time: ");
                        if (buffer.remaining() < 2) {
                            buffer.position(0);
                            buffer.limit(2);
                            n = channel.read(buffer);
                            buffer.position(0);
                            //System.out.print("[read " + n + "bytes]");
                        }
                        short s = buffer.getShort();
                        Console.print(Short.toUnsignedInt(s) + "us, frame send time: ");
                        if (buffer.remaining() < 2) {
                            buffer.position(0);
                            buffer.limit(2);
                            n = channel.read(buffer);
                            buffer.position(0);
                            //System.out.print("[read " + n + "bytes]");
                        }
                        int reasonEnd = buffer.getShort(); //unused frame time from Arduino

                        // acknowledge receipt by sending channel selection
                        commandChannelSelect.rewind();
                        commandChannelSelect.put(2, SEMThread.channels);
                        channel.write(commandChannelSelect);

                        String reasonS = "unknown";
                        if (reasonEnd < 4) {
                            reasonS = (new String[]{"idle", "no res", "track", "vsync"})[reasonEnd];
                        }
                        Console.print((System.currentTimeMillis() - frameStartTime) + "ms, reason: " + reasonS + ", OKs: ");
                        Console.println(numOKs + ", errors: " + numErrors + ", maxline: " + si.maxLine);
                        // process the raw data
                        if (reasonEnd == 3) { // &&  si.maxLine > (si.height-10)) { // vsync normal
                            //this.si.parseAllLines();
                            //this.si.cleanUp();

                            // transfer image to ui thread
                            synchronized (ltq) {
                                ltq.add(this.si);
                                this.si = null;
                            }
                            Platform.runLater(updateDisplayLambda);
                        }
                        result = SEMThread.Phase.WAITING_FOR_FRAME;
                        break;

                    default:
                        throw new SEMException(SEMError.ERROR_UNKNOWN_COMMAND);
                }
                // done processing succesful message. reset buffer
                buffer.position(0);
            } else {
                //  we get here through timeout
                //Thread.sleep(1);
                if (phase == SEMThread.Phase.WAITING_FOR_FRAME) {
                    result = phase;
                } else if (phase == SEMThread.Phase.WAITING_TO_CONNECT) {
                    result = phase;
                } else if (phase == SEMThread.Phase.WAITING_FOR_BYTES_OR_EFRAME) {
                    result = SEMThread.Phase.WAITING_FOR_FRAME;
                } else {
                    result = SEMThread.Phase.ABORTED;
                }
            }
        } catch (SEMException e) {
            result = phase;
            numErrors++;
        } catch (Exception e) {
            System.out.println(e.toString());
            for (StackTraceElement s : e.getStackTrace()) {
                System.out.println(s.toString());
            }

        }

        return result;
    }

    boolean findSentinel() {
        int n;
        byte[] ab = new byte[16];
        int skipped = 16;

        //System.out.print("Attempting recovery ...");
        try {
            do {
                buffer.position(0);
                buffer.limit(16);
                n = channel.read(buffer);
                buffer.position(0);
                buffer.get(ab);

                // look for a sequence of 0, 1, 2, ...
                int i, j, k;
                for (i = 0; i < 16; i++) {
                    if (ab[i] == 0) {
                        for (j = 0; j < 16 - i; j++) {
                            if (ab[i + j] != j) {
                                break;
                            }
                        }
                        if (i == 0 && j == 16) {
                            //System.out.println("... found sentinel. Lost bytes*: " + skipped);
                            return (true);
                        }

                        if (j == 16 - i) {
                            // if we get here, we have the beginnings of a sentinel
                            if (j < 16) {
                                buffer.position(0);
                                buffer.limit(16 - j); //
                                n = channel.read(buffer);
                                buffer.position(0);

                                // now we have the rest of the sentinel bytes, let's make sure they are correct
                                for (k = 0; k < 16 - j; k++) {
                                    if (buffer.get() != j + k) {
                                        break;
                                    }
                                }
                                if (k == 16 - j) {
                                    //System.out.println(" found sentinel. Lost bytes: " + (skipped + i));
                                    return true;
                                }

                            }
                        }
                    }
                }
                skipped += 16;
            } while (n > 0);
        } catch (Exception e) {
        }
        return false;
    }

}
