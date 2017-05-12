/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author daman
 */
public class Validator {

    Context ctx;

    public String validateEligibleGradeLevel(ScheduleRequest sr) {

        String courses = "SELECT * FROM courses WHERE id=?;";
        String students = "SELECT * FROM students WHERE id=?;";

        try {

            PreparedStatement courseStatement = ctx.db.conn.prepareStatement(courses);
            courseStatement.setInt(1, sr.course_id);
            
            ResultSet results = courseStatement.executeQuery();
            if (results.next()) {
                Course c = new Course(results.getInt(1), results.getString(2), results.getString(3), results.getInt(4), results.getInt(5), results.getInt(6), results.getString(7), results.getInt(8), results.getInt(9), results.getString(10), results.getInt(11), results.getString(12), results.getString(13), results.getInt(14), results.getInt(15), results.getFloat(16), results.getInt(17));
            }

            PreparedStatement studentStatement = ctx.db.conn.prepareStatement(courses);
            studentStatement.setInt(1, sr.individual_id);

            ResultSet rs = studentStatement.executeQuery();
            if (rs.next()) {
                Student s = new Student(rs.getInt("id"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("login"), rs.getInt("gradyear"));
            }

        } catch (Exception e) {

            System.out.println("Error Validating");

        }

        return "lmao";
    }
}
