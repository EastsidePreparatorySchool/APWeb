package com.mycompany.coursecatalog;

public class Context {

    int messagesSeen;
    String login;
    public Database db;
    long timeLastSeen;

    Context(String login) {
        this.db = new Database();
        this.login = login;
        this.timeLastSeen = System.currentTimeMillis();

        db.connect();
    }

    void updateTimer() {
        timeLastSeen = System.currentTimeMillis();
    }

    boolean checkExpired() {
        return (System.currentTimeMillis() - timeLastSeen >= (5 * 60 * 1000)); // if it has been more than 5 minutes
    }

    
    
    
    
    
    
    // route functionality
    Object getStudents(spark.Request req) { // hah!
        System.out.println("entered getStudents");

//        db.dumpTable("students");

        Object[] ao = db.queryStudents("select * from students");
//        System.out.println(ao.length);

        return ao;
    }

    Object getCourseStudentsFirst(String id) {
        System.out.println("getCourseStudentsFirst");
        return db.firstChoice(id);
    }

    Object getCourseStudentsAll(String id) {
        System.out.println("getCourseStudentsAll");
        return db.allChoice(id);
    }

    Object getCourseOfferings(spark.Request req) {
        System.out.println("entered getCourseOfferings");

//        db.getAllFrom("course_offerings");
//        Object[] ao = db.queryCourseOfferings"select * from course_offerings where year_id='18'");
        Object[] ao = db.queryCourseOfferings("select course_offerings.*, courses.name as name from course_offerings, courses where course_offerings.course_id=courses.id and course_offerings.year_id='18'");
//        System.out.println(ao.length);

        return ao;
    }

    Object getAllRequests(String login) {
        System.out.println("entered getAllRequests");
        Object[] ao;
//        db.getAllFrom("schedule_requests");
        if (login == null) {
            ao = db.queryAllRequests("select * from schedule_requests");
        } else {
            ao = db.queryAllRequests("select schedule_requests.*,students.id from schedule_requests, students where schedule_requests.individual_id=students.id and students.login='"+login+"'");
        }
//        System.out.println(ao.length);

        return ao;
    }
    //not yet supported
    Object getSpecificCourses(String disc, String grad, String len) {
       System.out.println("entered getSpecificCourses");
       return db.getSpecificCoursesDB(disc, grad, len);
    }

}
