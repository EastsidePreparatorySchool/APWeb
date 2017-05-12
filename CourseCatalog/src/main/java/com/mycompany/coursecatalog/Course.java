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
public class Course {
    private int id;
    private String name;
    private String sisid;
    private int term_id; // this is used
    private int needalt;
    private int graded;
    private String short_name;
    private int catalog;
    private int offering;
    private String subdiscipline;
    private int reqd;
    private String gradelevels; // this is used
    private String requirements;
    private int department_id; // this is used
    private int archived;
    private float credits;
    private int sort_tier;
    public Course(int i, String n, String s, int t, int ne, int g, String sh, int c, int o, String sub, int re, String gr, String req, int dep, int arc, float cre, int sor) {
        id = i;
        name = n;
        sisid = s;
        term_id = t;
        needalt = ne;
        graded = g;
        short_name = sh;
        catalog = c;
        offering = o;
        subdiscipline = sub;
        reqd = re;
        gradelevels = gr;
        requirements = req;
        department_id = dep;
        archived = arc;
        credits = cre;
        sort_tier = sor;
    }
}
