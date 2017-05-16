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
    
    public int id;
    public String name;
    public String sisid;
    public int term_id; // this is used
    public int needalt;
    public int graded;
    public String short_name;
    public int catalog;
    public int offering;
    public String subdiscipline;
    public int reqd;
    public String gradelevels; // this is used
    public String requirements;
    public int department_id; // this is used
    public int archived;
    public float credits;
    public int sort_tier;
    
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
    
    public Course() {    
    }
}
