/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author daman
 */
public class ScheduleRequest {

    int id, individual_id, course_id, first_alternate_course_id, second_alternate_course_id;
    String notes;
    boolean advisor_reviewed, parent_reviewed;
    Database db;

    public ScheduleRequest(int id, int individual_id, int course_id, int FirstAlternateCourse_id, int SecondAlternateCourse_id, String notes, boolean AdvisorReviewed, boolean ParentReviewed) {
        this.id = id;
        this.individual_id = individual_id;
        this.course_id = course_id;
        this.first_alternate_course_id = FirstAlternateCourse_id;
        this.second_alternate_course_id = SecondAlternateCourse_id;
        this.notes = notes;
        this.advisor_reviewed = AdvisorReviewed;
        this.parent_reviewed = ParentReviewed;
    }

    public int CreateScheduleRequest(ScheduleRequest sr) {
        int generatedKey = 0;
        try {
            String key[] = {"ID"}; //put the name of the primary key column
            String sql = "INSERT INTO schedule_requests (id, individual_id, course_id, first_alternate_course_id, second_alternate_course_id, notes, advisor_reviewed, parent_reviewed;)"
                    + " VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = db.conn.prepareStatement(sql, key);
            statement.setInt(1, sr.id);
            statement.setInt(2, sr.individual_id);
            statement.setInt(3, sr.course_id);
            statement.setInt(4, sr.first_alternate_course_id);
            statement.setInt(5, sr.second_alternate_course_id);
            statement.setString(6, sr.notes);
            statement.setBoolean(7, sr.advisor_reviewed);
            statement.setBoolean(8, sr.parent_reviewed);

            ResultSet rs;

            rs = statement.getGeneratedKeys();
            if (rs.next()) {
                generatedKey = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return generatedKey;
    }

    public void DeleteScheduleRequest(int id) {
        //delete from schedule_requests where id=1
        try {
            String sql = "DELETE FROM schedule_requests WHERE id=?";
            PreparedStatement statement = db.conn.prepareStatement(sql);
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("A user was deleted successfully!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void UpdateScheduleRequest(int id, String fieldToUpdate, String value) {
        try {
            String query1 = "SELECT * FROM schedule_requests WHERE id=?";
            PreparedStatement statement = db.conn.prepareStatement(query1);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            ScheduleRequest sr = null;
            while (rs.next()) {
                sr = new ScheduleRequest(rs.getInt("id"), rs.getInt("individual_id"), rs.getInt("course_id"), rs.getInt("first_alternate_course_id"),
                        rs.getInt("second_alternate_course_id"), rs.getString("notes"),
                        rs.getBoolean("advisor_reviewed"), rs.getBoolean("parent_reviewed"));
            }
            if (sr != null) {
                switch (fieldToUpdate) {
                    case "id":
                        sr.id = Integer.valueOf(value);
                        break;
                    case "individual_id":
                        sr.individual_id = Integer.valueOf(value);
                        break;
                    case "course_id":
                        sr.course_id = Integer.valueOf(value);
                        break;
                    case "first_alternate_course":
                        sr.first_alternate_course_id = Integer.valueOf(value);
                        break;
                    case "second_alternate_course":
                        sr.second_alternate_course_id = Integer.valueOf(value);
                        break;
                    case "notes":
                        sr.notes = value;
                        break;
                    case "advisor_reviewed":
                        sr.advisor_reviewed = Boolean.valueOf(value);
                        break;
                    case "parent_reviewed":
                        sr.parent_reviewed = Boolean.valueOf(value);
                        break;
                    default:
                        break;
                }
                
                String query2 = "UPDATE schedule_requests (id, individual_id, course_id, first_alternate_course_id, second_alternate_course_id, notes, advisor_reviewed, parent_reviewed;)"
                    + " VALUES (?, ?, ?, ?, ?) WHERE id=?";
            }
        } catch (Exception e) {

        }

    }

    public ArrayList<ScheduleRequest> RetrieveScheduleRequest(int id) {
        ArrayList<ScheduleRequest> requests = new ArrayList<ScheduleRequest>();
        try {
            String sql = "SELECT * FROM schedule_requests WHERE id=?";
            PreparedStatement statement = db.conn.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                requests.add(new ScheduleRequest(rs.getInt("id"), rs.getInt("individual_id"), rs.getInt("course_id"), rs.getInt("first_alternate_course_id"),
                        rs.getInt("second_alternate_course_id"), rs.getString("notes"),
                        rs.getBoolean("advisor_reviewed"), rs.getBoolean("parent_reviewed")));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return requests;
    }
}
