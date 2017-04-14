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
    String username;
    String message;
    
    public Message(String u, String m) {
        this.username = u;
        this.message = m;
    }
    
    public String toString() {
        return this.username + ": " + this.message;
    }
    
}
