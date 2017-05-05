/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import static com.mycompany.coursecatalog.CourseCatalog.getContextFromSession;

/**
 *
 * @author gmein
 */
public class Context {
    int messagesSeen;
    String login;
    public Database db;
    long timeLastSeen;
    
    
    Context(String login) {
        this.db = new Database();
        this.login = login;
        this.timeLastSeen = System.currentTimeMillis();
        
        db.connect();
    }
    
    void updateTimer() {
        timeLastSeen = System.currentTimeMillis();
    }

    boolean checkExpired() {
        return (System.currentTimeMillis() - timeLastSeen >= (5 * 60 * 1000)); // if it has been more than 5 minutes
    }
    
}
