/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 *
 * @author ESchreiber
 */
public class Student {
    private int id;
    private String firstname;
    private String lastname;
    private String login;
    private int gradyear;
    public Student(int i, String f, String l, String log, int g) {
        id = i;
        firstname = f;
        lastname = l;
        login = log;
        gradyear = g;
    }
    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getGradyear() {
        return gradyear;
    }

    public void setGradyear(int gradyear) {
        this.gradyear = gradyear;
    }
    //Given a specific course id, which student ids want that as their first choice course?
    //TODO: make a student class and make it get arrays of students back from the thingy
    public static Student[] firstChoice(String courseID) { //not sure how else to get the course ID we want. for now just be careful not to pass in droptable
        try {
            System.out.println("firstChoice");
            Statement stmt = Database.conn.createStatement();
            ResultSet results = stmt.executeQuery("select schedule_requests.individual_id from schedule_requests where schedule_requests.course_id = " + courseID + ";");
            ArrayList<Student> s = new ArrayList<>();
            while (results.next()) {
                s.add(new Student(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getInt(5)));
            }
            return (Student[]) s.toArray();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    //Given a specific course id, which student ids want that as a course?
    public static Student[] allChoice(String courseID) { //not sure how else to get the course ID we want. for now just be careful not to pass in droptable
        try {
            
            Statement stmt = Database.conn.createStatement();
            ArrayList<Student> s = new ArrayList<>();
            ResultSet results = stmt.executeQuery("select schedule_requests.individual_id from schedule_requests where schedule_requests.course_id = " + courseID + " or schedule_requests.first_alternate_course_id = " + courseID + " or schedule_requests.second_alternate_course_id = " + courseID + ";");
            while (results.next()) {
                s.add(new Student(results.getInt(1), results.getString(2), results.getString(3), results.getString(4), results.getInt(5)));
            }
            return (Student[]) s.toArray();
        }
        catch (Exception e ) {
            System.out.println(e);
        }
        return null;
    }
}