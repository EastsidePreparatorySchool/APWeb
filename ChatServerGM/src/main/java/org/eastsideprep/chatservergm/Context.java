/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.chatservergm;

import java.util.ArrayList;

/**
 *
 * @author gmein
 */
public class Context {
    ArrayList<String> messages;
    int seen = 0;
    
    
    Context() {
        messages = new ArrayList<>();
    }
    
}
