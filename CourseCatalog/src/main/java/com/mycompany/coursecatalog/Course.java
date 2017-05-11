/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.coursecatalog;

/**
 *
 * @author ESchreiber
 */
//made this because I didn't realize I actually want to use course_offering.
//Oh well. might have use in the future, otherwise tag this for deletion.
public class Course {
    private int id;
    private String name;
    private String sisid;
    private int term_id;
    private int needalt;
    private int graded;
    private String short_name;
    private int catalog;
    private int offering;
    private String subdiscipline;
    private int reqd;
    private String gradelevels;
    private String requirements;
    private int departmend_id;
    private String shortname; //duplicate of short_name? this is how it appears in documentation
    private int archived;
    private float credits;
    private int sort_tier;
}
