/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.semconsolewebapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author shinson
 */
public class Database {

    //This class will combine code from the JDBCFramework project with code from the Session class
    //this will add support for a sql database that will hold the images along with their meta data
    public Connection conn;

    Database() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connect();
        } catch (ClassNotFoundException ex) {
            System.out.print("Error creating datbase object: ");
            System.out.println(ex);
        }
    }

    // sets the conn field to a connection with the course_requests database.
    // if connection is unsuccessful, prints the detail message of the
    // exception (a string associated with it on construction), a 5
    // character code associated with the SQL state, and the vendor-specific
    // error code associated with the error.
    public void connect() {
        System.out.println("Attempting to connect...");
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/SemImagesDatabase", "user", "password");
            System.out.println("Connection successful");
        } catch (SQLException ex) {
            System.out.print("Error connecting to database: ");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        //SET PACKET SIZE TO A LARGER VALUE
        String sql = "SET GLOBAL max_allowed_packet=1024*1024*256;";
        try {
            PreparedStatement statement = conn.prepareCall(sql);
            statement.execute();
            System.out.println("got here");
        } catch (Exception e) {
            System.out.println(e);
        }

        //TESTING
        File file = new File("C:/temp/original.png");
        storeImageInDatabase(file);
    }

    // closes the conn field's connection, prints exception is unsuccessful
    public void disconnect() {

        try {
            this.conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void storeImageInDatabase(File file) {
        try {
            String sql = "INSERT INTO images (image) VALUES (?);";
            PreparedStatement statement = conn.prepareCall(sql);

            //create file input stream and add blob as parameter
            FileInputStream input = new FileInputStream(file);
            statement.setBinaryStream(1, input, file.length());

            statement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Object retrieveImage(int index) {
        InputStream image = null;
        byte[] data = null;
        try {
            String sql = "SELECT image FROM images WHERE images.image_id=1;";
            PreparedStatement statement = conn.prepareCall(sql);

            ResultSet rs = statement.executeQuery();
            rs.next();
            image = rs.getBinaryStream(1);
            System.out.println(image);
        } catch (Exception e) {
        }

        return image;
    }
}
