/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.semconsolewebapp;

import java.util.ArrayList;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public class Bluetooth {

    public static ArrayList<RemoteDevice> getDevices() {
        /* Create Vector variable */
        final ArrayList<RemoteDevice> devicesDiscovered = new ArrayList<>();
        try {
            final Object inquiryCompletedEvent = new Object();
            /* Clear Vector variable */
            devicesDiscovered.clear();

            /* Create an object of DiscoveryListener */
            DiscoveryListener listener = new DiscoveryListener() {

                @Override
                public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                    /* Get devices paired with system or in range(Without Pair) */
                    devicesDiscovered.add(btDevice);
                }

                @Override
                public void inquiryCompleted(int discType) {
                    /* Notify thread when inquiry completed */
                    synchronized (inquiryCompletedEvent) {
                        inquiryCompletedEvent.notifyAll();
                    }
                }

                /* To find service on bluetooth */
                @Override
                public void serviceSearchCompleted(int transID, int respCode) {
                }

                /* To find service on bluetooth */
                @Override
                public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                }
            };

            synchronized (inquiryCompletedEvent) {
                /* Start device discovery */
                boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
                if (started) {
                    System.out.println("wait for device inquiry to complete...");
                    inquiryCompletedEvent.wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* Return list of devices */
        return devicesDiscovered;
    }
}
