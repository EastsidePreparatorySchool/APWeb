/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatserveres;

/**
 *
 * @author ESchreiber
 */
public class Message {

    int id;
    int parent;
    String initials;
    String message;

    public Message(int p, String in, String m) {
        parent = p;
        initials = in;
        message = m;
    }
    public boolean setIdd(int s) {
        this.id = s;
        return true;
    }
}
