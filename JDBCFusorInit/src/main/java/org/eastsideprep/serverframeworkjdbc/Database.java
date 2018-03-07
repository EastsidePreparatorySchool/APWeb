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
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/sakila", "user", "password");
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
    
    
    

    Object showFilmsWithRockDukakis() {
        ArrayList<Film> films = new ArrayList<>();
        
        try {
            String sql = "select title as name, release_year as year from actor, film_actor, film "
                    + "where first_name='Rock' and last_name='Dukakis' "
                    + "and film_actor.actor_id = actor.actor_id "
                    + "and film.film_id = film_actor.film_id;";
            PreparedStatement statement = conn.prepareStatement(sql);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Film film = new Film();
                film.name = rs.getString("name");
                film.year = rs.getString("year");
                films.add(film);
                System.out.println(film.name+" "+film.year);
            }
        } catch (Exception e) {
            System.out.println(e);

        }
        return films.toArray();
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
            
            String sql =  "select * from operators where first_name=\"" + firstName + "\" and last_name=\"" 
                    + lastName+ "\";"; //select query to get message from DB
            //System.out.println(sql + "sfdjksdfkosfdsfd"); //for debugging
            PreparedStatement statement = conn.prepareStatement(sql);

            ResultSet rs = statement.executeQuery();
            //System.out.println("Result set: " + rs.toString());
            
                
                if (rs.next()==false) { //check for empty resultset to add to database if operator is not yet registered
                    
                
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

     Object logSession(spark.Request req) {
        try {
            String query = " insert into session (time, date)" //sql query, insert into database
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
    /*
        // CRUD: Students
    // makes a new row in the students table using the fields of the Student argument
    // prints success message if successful, prints exception otherwise
    public void createStudent(Student sd) {
        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO students (id, firstname, lastname, login, gradyear) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, sd.id);
            statement.setString(2, sd.firstName);
            statement.setString(3, sd.lastName);
            statement.setString(4, sd.login);
            statement.setInt(5, sd.gradYear);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new student was inserted successfully!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Student retrieveStudent(int id) {
        Student request = null;
        try {
            String sql = "SELECT * FROM students WHERE id=?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                request = new Student(rs.getInt("id"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("login"), rs.getInt("gradyear"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return request;
    }

    public Student retrieveStudent(String login) {
        Student request = null;
        try {
            String sql = "SELECT * FROM students WHERE login=?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, login);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                request = new Student(rs.getInt("id"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("login"), rs.getInt("gradyear"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return request;
    }

    public void updateStudent(Student sd) {
        try {
            String sql = "UPDATE FROM students SET firstname=?, lastname=?, login=?, gradyear=? WHERE id=?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, sd.firstName);
            statement.setString(2, sd.lastName);
            statement.setString(3, sd.login);
            statement.setInt(4, sd.getGradYear());
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    //todo: Make this only look at id, not the other fields
    public void deleteStudent(Student sd) {
        try {
            String sql = "DELETE FROM students WHERE id=?, firstname=?, lastname=?, login=?, gradyear=?";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, sd.id);
            statement.setString(2, sd.firstName);
            statement.setString(3, sd.lastName);
            statement.setString(4, sd.login);
            statement.setInt(5, sd.gradYear);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("A student was deleted successfully!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

     */
}
