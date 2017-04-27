/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

/**
 *
 * @author gmein
 */
public class Context {
    int messagesSeen;
    String login;
    public Database db;
    
    
    Context(String login) {
        this.messagesSeen = 0;
        this.db = new Database();
        this.login = login;
        
        db.connect();
    }
    
}
