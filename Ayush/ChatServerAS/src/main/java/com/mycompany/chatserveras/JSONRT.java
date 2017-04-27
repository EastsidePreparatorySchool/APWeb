/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.chatserveras;
import com.google.gson.*;
import spark.*;
/**
 *
 * @author asharma
 */
public class JSONRT implements ResponseTransformer{
    final static private Gson gson = new Gson();

    @Override
    public String render(Object objectToRender) {
        return gson.toJson(objectToRender);
    }
    
    static ReplyMessage parse(String stringToParse) {
        return gson.fromJson(stringToParse, ReplyMessage.class);
    }
}
