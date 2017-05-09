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

    ScheduleRequest() {
        this.id = 0;
        this.individual_id = 0;
        this.course_id = 0;
        this.first_alternate_course_id = 0;
        this.second_alternate_course_id = 0;
        this.notes = "";
        this.advisor_reviewed = false;
        this.parent_reviewed = false;
    }

 
 }
