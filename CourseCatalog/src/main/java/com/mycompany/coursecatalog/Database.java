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
    
    public void getAllFrom(String tableName) {

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
    
    public class CourseData {
        int id;
        int courseId;
        int yearId;
        String description;
        String info;
        String gradeLevels;
    }
    
    public Object[] queryCourses(String query) {

        Object[] result = null;
        ArrayList<CourseData> asd = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                CourseData cd = new CourseData();
                cd.courseId = rs.getInt(2);
                cd.yearId = rs.getInt(3);
                cd.description = rs.getString(5);
                cd.info = rs.getString(9);
                cd.gradeLevels = rs.getString(10);
                cd.id = rs.getInt(1);

                asd.add(cd);
            }
            result = asd.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }
    
    public Object[] queryAllRequests(String query) {

        Object[] result = null;
        ArrayList<ScheduleRequest> asd = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                ScheduleRequest sr = new ScheduleRequest();
                sr.individual_id = rs.getInt(2);
                sr.course_id = rs.getInt(3);
                sr.first_alternate_course_id = rs.getInt(4);
                sr.second_alternate_course_id = rs.getInt(5);
                sr.notes = rs.getString(6);
                sr.advisor_reviewed = rs.getBoolean(7);
                sr.parent_reviewed = rs.getBoolean(8);
                sr.id = rs.getInt(1);

                asd.add(sr);
            }
            result = asd.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    public String queryName(String login) {

        String result = "unknown";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select firstname, lastname from students where login='"+login+"'");
            System.out.println("Querying student name for login "+login);

            if (rs.next()) {
                StudentData sd = new StudentData();
                sd.firstName = rs.getString(1);
                sd.lastName = rs.getString(2);
                result = sd.firstName + " " + sd.lastName;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

}
