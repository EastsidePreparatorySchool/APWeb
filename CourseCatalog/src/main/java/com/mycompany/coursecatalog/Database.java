/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author gmein
 */
public class Database {

    private Connection conn;
    private Statement stmt;

    Database() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
        }
    }

    public void connect() {

        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://10.20.30.27/course_requests", "webapps", "LAyKqTDPsb7eMa8u");
        } catch (Exception e) {
            System.out.println(e);
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

    private class StudentData {
        int id;
        String firstName;
        String lastName;
        String login;
        int gradYear;
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
                sd.login =rs.getString(4);
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
