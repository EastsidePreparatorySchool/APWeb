/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author gmein
 */
public class Database {

    public static Connection conn;
    public Statement stmt;

    Database() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.print("Error creating datbase object: ");
            System.out.println(ex);
        }
    }

    public void connect() {
           System.out.println("Attempting to connect...");
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/course_requests", "webapps", "LAyKqTDPsb7eMa8u");
            System.out.println("Connection successful");
        } catch (SQLException ex) {
            System.out.print("Error connecting to database: ");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void disconnect() {

        try {
            this.conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void example(String tableName) {

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName);

            System.out.println();
            System.out.println(tableName + ":");

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                System.out.println(rsmd.getColumnName(i) + ",  " + rsmd.getColumnTypeName(i));
            }

            System.out.println();
            System.out.println("Rows:");

            while (rs.next()) {
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
            }

            System.out.println();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public class MessageData {

        public int id;
        public String login;
        public String message;

        public MessageData() {
        }

        public MessageData(String login, String message) {
            this.login = login;
            this.message = message;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public Object[] getNewMessages(int after) {

        Object[] result = null;
        ArrayList<MessageData> asd = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM messages WHERE id > " + after);

            while (rs.next()) {
                MessageData md = new MessageData();
                md.id = rs.getInt(1);
                md.login = rs.getString(2);
                md.message = rs.getString(3);
                asd.add(md);
            }
            result = asd.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    public void insertMessage(MessageData md) {
        //So MessageData object that gets passed in may need an ID supplied either from auto_increment or a lookup
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO Messages VALUES (" + md.id + "," + md.login + "," + md.message + ")");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void getIDFromDatabase() {
        try {
            Statement stmt = conn.createStatement();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public class StudentData {

        int id;
        String firstName;
        String lastName;
        String login;
        int gradYear;
    }

    public void retrieveStudent(StudentData sd) {

    }

    public void createStudent(StudentData sd) {
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
                System.out.println("A new user was inserted successfully!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateStudent() {

    }

    public void deleteStudent(StudentData sd) {
        try {
            String sql = "DELETE FROM Users WHERE id=?, firstname=?, lastname=?, login=?, gradyear=?";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, sd.id);
            statement.setString(2, sd.firstName);
            statement.setString(3, sd.lastName);
            statement.setString(4, sd.login);
            statement.setInt(5, sd.gradYear);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("A user was deleted successfully!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Object[] queryStudents(String query) {

        Object[] result = null;
        ArrayList<StudentData> asd = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                StudentData sd = new StudentData();
                sd.firstName = rs.getString(2);
                sd.lastName = rs.getString(3);
                sd.login = rs.getString(4);
                sd.gradYear = rs.getInt(5);
                sd.id = rs.getInt(1);

                asd.add(sd);
            }
            result = asd.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

}
