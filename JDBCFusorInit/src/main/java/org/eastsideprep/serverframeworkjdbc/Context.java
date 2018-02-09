/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.serverframeworkjdbc;

/**
 *
 * @author gmein
 */
public class Context {    
    String login;
    public Database db;

    Context(String login) {
        this.db = new Database();
        this.login = login;

        db.connect();
    }
    
}
