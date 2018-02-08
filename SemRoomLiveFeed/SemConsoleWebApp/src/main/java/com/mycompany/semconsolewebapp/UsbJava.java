package com.mycompany.semconsolewebapp;

import com.lucasF.JCommUSB_2_0.USBDevice;
import com.lucasF.JCommUSB_2_0.USBException;
import java.util.ArrayList;

public class UsbJava {

    public static void test() {
        int i;
        try {
            i = USBDevice.getNumAttachedDevices();
            System.out.println("Attached Devices: " + i);
            if (i == 0) {
                return;
            }
            String szPath = USBDevice.getAttachedDevicePath(0);
            System.out.println("Device Path of Device at index 0 = " + szPath);
            USBDevice myUSB = new USBDevice(szPath);
            byte[] bufferOut = new byte[8];
            byte[] bufferIn = new byte[8];
            bufferOut[0] = (byte) 15;
            bufferOut[1] = (byte) 14;
            bufferOut[2] = (byte) 16;
            bufferOut[3] = (byte) 17;
            bufferOut[4] = (byte) 18;
            bufferOut[5] = (byte) 20;
            bufferOut[6] = (byte) 21;
            bufferOut[7] = (byte) 22;

            myUSB.writePipeBulkInterrupt(0, 0, bufferOut, 0, 8);
            myUSB.readPipeBulkInterrupt(0, 1, bufferIn, 0, 8);
            ArrayList<Byte> vector = new ArrayList<Byte>();
            for (int j = 0; j < bufferIn.length; j++) {
                vector.add(bufferIn[j]);
            }
            System.out.println(vector);

        } catch (USBException e) {

            e.printStackTrace();
        }

    }

}
