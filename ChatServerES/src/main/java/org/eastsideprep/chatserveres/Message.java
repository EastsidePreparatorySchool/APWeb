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

    public Message(int i, int p, String in, String m) {
        id = i;
        parent = p;
        initials = in;
        message = m;
    }
}
