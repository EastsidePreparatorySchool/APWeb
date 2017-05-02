/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 *
 * @author daman
 */
public class ScheduleRequest {
    int id, individual_id, course_id, FirstAlternateCourse_id, SecondAlternateCourse_id;
    String notes;
    boolean AdvisorReviewed, ParentReviewed;
    Database db;

    public void CreateScheduleRequest(ScheduleRequest sr) {
        try {
            String sql = "INSERT INTO schedule_requests (id, individual_id, course_id, first_alternate_course_id, second_alternate_course_id, notes, advisor_reviewed, parent_reviewed;)"
                    + " VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = db.conn.prepareStatement(sql);
            statement.setInt(1, sr.id);
            statement.setInt(2, sr.individual_id);
            statement.setInt(3, sr.course_id);
            statement.setInt(4, sr.FirstAlternateCourse_id);
            statement.setInt(5, sr.SecondAlternateCourse_id);
            statement.setString(6, sr.notes);
            statement.setBoolean(7, sr.AdvisorReviewed);
            statement.setBoolean(8, sr.ParentReviewed);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new user was inserted successfully!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

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

    public void UpdateScheduleRequest(int id) {

    }

    public void RetrieveScheduleRequest(int id) {
        try {
            String sql = "SELECT * FROM schedule_requests WHERE id=?";
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
}
