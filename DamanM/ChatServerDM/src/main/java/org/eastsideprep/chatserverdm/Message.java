/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatserverdm;

/**
 *
 * @author daman
 */
public class Message {
    String message;
    int id;
    int parent;
    String initials;
    
    Message(String message, int parent, String initials) {
        this.message = message;
        this.parent = parent;
        this.initials = initials;
    }
    
    public void setID(int id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        if(this.parent != -1) {
            return "#" + this.id + " in reply to " + this.parent + " " + this.initials + ": " + this.message;
        } else {
            return "#" + this.id + " " + this.initials + ": " + this.message;
        }
    }
}
