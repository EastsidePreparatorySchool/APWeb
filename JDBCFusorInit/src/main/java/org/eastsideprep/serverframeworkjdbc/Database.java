/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.serverframeworkjdbc;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.servlet.http.Part;

/**
 *
 * @author gmein
 */
public class Database {

    public static Connection conn;

    Database() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
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
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/FusorDB", "User", "Password");
            System.out.println("Connection successful");
        } catch (SQLException ex) {
            System.out.print("Error connecting to database: ");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    // closes the conn field's connection, prints exception is unsuccessful
    public void disconnect() {

        try {
            this.conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // returns the id of the last row inserted into the database
    // if unsuccessful, returns -1
    public int getLastID() {
        try {
            int lastID = 0;
            Statement stmt = conn.createStatement();
            stmt.executeQuery("SELECT LAST_INSERTED_ID() AS id;");
            ResultSet rs;

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                lastID = rs.getInt(1);
            }
            return lastID;
        } catch (Exception e) {
            return -1;
        }
    }

    // prints the contents of the table.
    // first prints column names and types, then prints the row id and first
    // two columns of all rows. prints exception if unsuccessful
    public void dumpTable(String tableName) {

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName); // select everything in the table

            System.out.println();
            System.out.println(tableName + ":");

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                System.out.println(rsmd.getColumnName(i) + ",  " + rsmd.getColumnTypeName(i)); // prints column name and type
            }

            System.out.println();
            System.out.println("Rows:");

            while (rs.next()) { // prints the id and first two columns of all rows
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
            }

            System.out.println();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // return all tables to user
    String showTables() {
        StringBuilder result = new StringBuilder();
        try {
            String sql = "show tables;";
            PreparedStatement statement = conn.prepareStatement(sql);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result.append(rs.getString("Tables_in_sakila"));
                result.append(" ");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return result.toString();
    }

    // executes a prepared sql statement specified by the sql argument
    // the first parameter of that prepared statement will be the parameter argument
    // prints exception if unsuccessful
    void executePS(String sql, int parameter) {

        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, parameter);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
//                System.out.println("Statement executed: " + sql + ", " + parameter + ", rows affected:" + rowsAffected);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    static Object addImages(spark.Request req, Part file) {

        try {
            // the mysql insert statement
            //to insert the picture into the databse
            String query = " insert into pictures (Picture)" //sql query, insert into database
                    + " values (?)";

            // create the mysql insert preparedstatement
            InputStream is = file.getInputStream(); //inputstream to convert to blob
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setBinaryStream(1, is); //sets the one parameter

            // execute the preparedstatement
            preparedStmt.execute();

            //conn.close(); //works without this
            return "temp";
        } catch (Exception e) {
            System.err.println("Got an exception in uploadImages: " + e);

        }
        return "Should not get here";
    }

    Object registerOperator(spark.Request req, String firstName, String lastName, String initials) {

        try {

            String sql = "select * from operators where first_name=\"" + firstName + "\" and last_name=\""
                    + lastName + "\";"; //select query to get message from DB
            //System.out.println(sql + "sfdjksdfkosfdsfd"); //for debugging
            PreparedStatement statement = conn.prepareStatement(sql);

            ResultSet rs = statement.executeQuery();
            //System.out.println("Result set: " + rs.toString());

            if (rs.next() == false) { //check for empty resultset to add to database if operator is not yet registered

                String query = " insert into operators (first_name, last_name, initials)" //sql query, insert into database
                        + " values (?, ?, ?)";
                System.out.println("got here");

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString(1, firstName); //sets the parameters
                preparedStmt.setString(2, lastName);
                preparedStmt.setString(3, initials);

                // execute the preparedstatement
                preparedStmt.execute();

                //conn.close();
                return firstName + lastName + initials;
            } else {
                return "already registered";
            }

            //System.out.println(message + "---------------------------"); //debugging
            //message = req.queryParams("message").toString();
            // the mysql insert statement
        } catch (Exception e) {
            System.err.println("Got an exception in registerOperator: " + e);

        }
        return "Should not get here";
    }

    static int getk() {
        int k = 0;
        try {

            String query2 = "select Picture from pictures;"; //select all pictures uploaded
            PreparedStatement ps2 = conn.prepareStatement(query2); //create prepared statement
            ResultSet rs2 = ps2.executeQuery(); //create result set

            //add to k while there are still pictures - find the total number
            while (rs2.next()) {
                k++;
            }
        } catch (Exception e) {
            System.err.println("Got an exception in getk" + e);
        }

        return k;
    }

    static ArrayList<byte[]> getImage(spark.Request req, spark.Response res) {
        int k = getk(); //use total number 
        byte[] data = null; //make empty byte array for photos
        ArrayList photos = new ArrayList<byte[]>(); //make arraylist of byte arrays to return
        try {
            //get all images from db
            for (int i = 1; i <= k; i++) {

                String query = "select Picture as file from pictures WHERE Picture_id = (?);"; //select individual images based on id

                PreparedStatement preparedStmt = conn.prepareStatement(query); //prepare statement
                preparedStmt.setInt(1, i); //set variable in statement to i so as to loop through and return all of them
                ResultSet rs = preparedStmt.executeQuery(); //execute statement

                //add bytes from BLOB file into array
                while (rs.next()) {
                    data = rs.getBytes("file");
                }
                photos.add(data); //add each photo to arraylist
            }

            return photos;

        } catch (Exception e) {
            System.err.println("Got an exception in getImage" + e);
        }

        return null;
    }

    Object logSession(spark.Request req) {
        try {
            String query = " insert into session_ops (time_in, date_in)" //sql query, insert into database
                    + " values (?, ?)";
            String dt = LocalDateTime.now().toString(); //get date and time

            String time = dt.substring(dt.indexOf("T") + 1, dt.length() - 4); //repeatedly printed out dt to figure
            //out the indexes to substring for date and time
            String date = dt.substring(0, dt.indexOf("T"));
//            System.out.println("--------------");
//            System.out.println(time); //for debugging
//            System.out.println(date);
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, time);
            preparedStmt.setString(2, date);
            preparedStmt.execute();
            return "session made";
        } catch (Exception e) {
            System.err.println("Error in logSession: " + e);
        }
        return "Should not get here";
    }

    Object getAttributes(spark.Request req, Attributes a) {
        

        try {
            String sql = "select voltage, "
                    + "deuterium, vacuum, radlevel from session;"; //select query to get message from DB
            PreparedStatement statement = conn.prepareStatement(sql);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                a.voltage = rs.getInt("voltage");
                a.deuterium = rs.getInt("deuterium");
                a.vpressure = rs.getInt("vacuum");
                a.radiationLevel = rs.getInt("radlevel");
                
            }

        } catch (Exception e) {
            System.out.println("Exception in getAttributes: "+ e);

        }
        return a;
    }

}
