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

    public Connection conn;

    Database() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.print("Error creating datbase object: ");
            System.out.println(ex);
        }
    }
    // sets the conn field to a connection with the course_requests database.
    // if connection is unsuccessful, prints the detail message of the
    // exception (a string associated with it on construction), a 5
    // character code associated with the SQL state, and the vendor-specific
    // error code associated with the error.
    public void connect() {
        System.out.println("Attempting to connect...");
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost/course_requests", "webapps", "LAyKqTDPsb7eMa8u");
            System.out.println("Connection successful");
        } catch (SQLException ex) {
            System.out.print("Error connecting to database: ");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    // closes the conn field's connection, prints exception is unsuccessful
    public void disconnect() {

        try {
            this.conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // returns the id of the last row inserted into the database
    // if unsuccessful, returns -1
    public int getLastID() {
        try {
            int lastID = 0;
            Statement stmt = conn.createStatement();
            stmt.executeQuery("SELECT LAST_INSERTED_ID() AS id;");
            ResultSet rs;

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                lastID = rs.getInt(1);
            }
            return lastID;
        } catch (Exception e) {
            return -1;
        }
    }

    // prints the contents of the table.
    // first prints column names and types, then prints the row id and first
    // two columns of all rows. prints exception if unsuccessful
    public void dumpTable(String tableName) {

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName); // select everything in the table

            System.out.println();
            System.out.println(tableName + ":");

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                System.out.println(rsmd.getColumnName(i) + ",  " + rsmd.getColumnTypeName(i)); // prints column name and type
            }

            System.out.println();
            System.out.println("Rows:");

            while (rs.next()) { // prints the id and first two columns of all rows
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
            }

            System.out.println();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // executes a prepared sql statement specified by the sql argument
    // the first parameter of that prepared statement will be the parameter argument
    // prints exception if unsuccessful
    void executePS(String sql, int parameter) {

        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, parameter);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
//                System.out.println("Statement executed: " + sql + ", " + parameter + ", rows affected:" + rowsAffected);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    // CRUD: Students
    // makes a new row in the students table using the fields of the Student argument
    // prints success message if successful, prints exception otherwise
    public void createStudent(Student sd) {
        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO students (id, firstname, lastname, login, gradyear) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, sd.id);
            statement.setString(2, sd.firstName);
            statement.setString(3, sd.lastName);
            statement.setString(4, sd.login);
            statement.setInt(5, sd.gradYear);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new student was inserted successfully!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // todo: write this, implement route
    public Student retrieveStudent(int id) {
        Student request = null;
        try {
            String sql = "SELECT * FROM students WHERE id=?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                request = new Student(rs.getInt("id"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("login"), rs.getInt("gradyear"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return request;
    }

    // todo: write this, implement route
    public Student retrieveStudent(String login) {
        Student request = null;
        try {
            String sql = "SELECT * FROM students WHERE login=?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, login);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                request = new Student(rs.getInt("id"), rs.getString("firstname"), rs.getString("lastname"), rs.getString("login"), rs.getInt("gradyear"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return request;
    }

    // to do: write this, implement route
    public void updateStudent(Student sd) {
        try {
            String sql = "UPDATE FROM students SET firstname=?, lastname=?, login=?, gradyear=? WHERE id=?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, sd.firstName);
            statement.setString(2, sd.lastName);
            statement.setString(3, sd.login);
            statement.setInt(4, sd.getGradYear());
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    //todo: Make this only look at id, not the other fields
    // todo: implement route
    public void deleteStudent(Student sd) {
        try {
            String sql = "DELETE FROM students WHERE id=?, firstname=?, lastname=?, login=?, gradyear=?";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, sd.id);
            statement.setString(2, sd.firstName);
            statement.setString(3, sd.lastName);
            statement.setString(4, sd.login);
            statement.setInt(5, sd.gradYear);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("A student was deleted successfully!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // CRUD: ScheduleReqeusts
    // makes a new row in the schedule_requests table using the fields of the
    // scheduleRequest argument
    // prints success message if successful, prints exception otherwise
    public int createScheduleRequest(ScheduleRequest sr) {
        int generatedKey = 0;

        try {
            String key[] = {"ID"}; //put the name of the primary key column
            int id = getLastID();
            id++; // make sure the row will be inserted at the end of the table
            String sql = "INSERT INTO schedule_requests (id, individual_id, course_id, first_alternate_course_id, second_alternate_course_id, notes, advisor_reviewed, parent_reviewed)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = conn.prepareStatement(sql, key);
            statement.setInt(1, id);
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

    // returns a ScheduleRequest whose fields are retrieved from the 
    // schedule_requests table at the row specified by the id argument
    // prints exception if unsuccessful
    public ScheduleRequest retrieveScheduleRequest(int id) {
        ScheduleRequest request = null;
        try {
            String sql = "SELECT * FROM schedule_requests WHERE id=?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id); // prepares sql statement that will select the row associated with id argument

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                request = new ScheduleRequest(rs.getInt("id"), rs.getInt("individual_id"), rs.getInt("course_id"), rs.getInt("first_alternate_course_id"),
                        rs.getInt("second_alternate_course_id"), rs.getString("notes"),
                        rs.getBoolean("advisor_reviewed"), rs.getBoolean("parent_reviewed"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return request;
    }

    // changes the values of the row specified by the id field of the 
    // ScheduleRequest argument to the values specified by that argument's fields
    // prints exception if unsuccessful
    public void updateScheduleRequest(ScheduleRequest sr) {
        try {
            String query2 = "UPDATE schedule_requests SET (id=?, individual_id=?, course_id=?, first_alternate_course_id=?, second_alternate_course_id=?, notes=?, advisor_reviewed=?, parent_reviewed=?) WHERE id=?;";
            int id = sr.id;
            PreparedStatement statement = conn.prepareStatement(query2);
            statement.setInt(1, sr.id);
            statement.setInt(2, sr.individual_id);
            statement.setInt(3, sr.course_id);
            statement.setInt(4, sr.first_alternate_course_id);
            statement.setInt(5, sr.second_alternate_course_id);
            statement.setString(6, sr.notes);
            statement.setBoolean(7, sr.advisor_reviewed);
            statement.setBoolean(8, sr.parent_reviewed);
            statement.setInt(9, id);
            statement.execute(query2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // deletes the row in the schedule_requests table specified by the id argument
    // prints exception if unsuccessful
    public void deleteScheduleRequest(int id) {
        //delete from schedule_requests where id=1
        try {
            String sql = "DELETE FROM schedule_requests WHERE id=?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("A ScheduleRequest was deleted successfully!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    // queries of all kinds
    // returns an array of Student objects made from the data selected by the query argument
    // this method is made to be used with the students table. 
    // use with other tables at your own risk
    // prints exception and returns null if unsuccessful
    public Object[] queryStudents(String query) {

        Object[] result = null;
        ArrayList<Student> asd = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) { // create Student objects from the data in the specified table and adds them to asd array
                Student sd = new Student(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5));
                asd.add(sd);
            }
            result = asd.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    // returns an array of CourseOffering objects made from the data selected by the query argument
    // this method is made to be used with the course_offerings table. 
    // use with other tables at your own risk
    // prints exception and returns null if unsuccessful
    public Object[] queryCourseOfferings(String query) {

        Object[] result = null;
        ArrayList<CourseOffering> asd = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                CourseOffering cd = new CourseOffering();
                cd.courseId = rs.getInt(2);
                cd.yearId = rs.getInt(3);
                cd.description = rs.getString(5);
                cd.info = rs.getString(9);
                cd.gradeLevels = rs.getString(10);
                cd.id = rs.getInt(1);
                cd.name = rs.getString(11);

                asd.add(cd);
            }
            result = asd.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    // returns an array of ScheduleRequest objects made from the data selected by the query argument
    // this method is made to be used with the course_requests table. 
    // use with other tables at your own risk
    // prints exception and returns null if unsuccessful
    public Object[] queryAllRequests(String query) {

        Object[] result = null;
        ArrayList<ScheduleRequest> asd = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                ScheduleRequest sr = new ScheduleRequest();
                sr.individual_id = rs.getInt(2);
                sr.course_id = rs.getInt(3);
                sr.first_alternate_course_id = rs.getInt(4);
                sr.second_alternate_course_id = rs.getInt(5);
                sr.notes = rs.getString(6);
                sr.advisor_reviewed = rs.getBoolean(7);
                sr.parent_reviewed = rs.getBoolean(8);
                sr.id = rs.getInt(1);

                asd.add(sr);
            }
            result = asd.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    // sets name and id fields of Context argument to data selected from  the
    // row of the students table specified by the login field of the Context argument.
    // sets the name field of Context argument the student's first name and last name
    // sets the id field of the Context argument to the student's id
    // returns the string "unknown" if unsuccessful
    public String queryName(Context ctx) {

        String result = "unknown";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select firstname, lastname, id from students where login='" + ctx.login + "'");
//            System.out.println("Querying student name for login " + login);

            if (rs.next()) {
                String firstName = rs.getString(1);
                String lastName = rs.getString(2);
                result = firstName + " " + lastName + " (" + ctx.login + ", " + rs.getInt(3) + ")";
                ctx.name = firstName + " " + lastName;
                ctx.id = rs.getInt(3);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

    //not sure how else to get the course ID we want. for now just be careful not to pass in droptable
    public Object firstChoice(String courseID) {
        try {
//            System.out.println("firstChoicecourse id: " + courseID);
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("select schedule_requests.individual_id, students.* from schedule_requests, students "
                    + "where schedule_requests.individual_id=students.id and schedule_requests.course_id = " + courseID);
            ArrayList<Student> s = new ArrayList<>();
            while (results.next()) {
                s.add(new Student(results.getInt(2), results.getString(3), results.getString(4), results.getString(5), results.getInt(6)));
            }
            return s.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    //Given a specific course id, which student ids want that as a course?
    //not sure how else to get the course ID we want. for now just be careful not to pass in droptable
    public Object allChoice(String courseID) {
        try {
//            System.out.println("allChoicecourse id: " + courseID);

            Statement stmt = conn.createStatement();
            ArrayList<Student> s = new ArrayList<>();
            ResultSet results = stmt.executeQuery("select schedule_requests.individual_id, students.* from schedule_requests, students "
                    + "where schedule_requests.individual_id=students.id and (schedule_requests.course_id = " + courseID
                    + " or schedule_requests.first_alternate_course_id = " + courseID
                    + " or schedule_requests.second_alternate_course_id = " + courseID + ")");
            while (results.next()) {
                s.add(new Student(results.getInt(2), results.getString(3), results.getString(4), results.getString(5), results.getInt(6)));
            }
            return s.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    //Gets the corresponding course_offerings and returns them as an array.
    public Object getSpecificCoursesDB(String disc, String grad, String len) {
        try {
            //string that we modify to add the thingy
            String query = "select * from courses where ";
            //dept ID addition
            if (disc.equals("Eng")) {
                query += "courses.department_id = 1";
            } else if (disc.equals("His")) {
                query += "courses.department_id = 2";
            } else if (disc.equals("Mth")) {
                query += "courses.department_id = 3";
            } else if (disc.equals("Sci")) {
                query += "courses.department_id = 4";
            } else if (disc.equals("Spa")) {
                query += "courses.department_id = 5";
            } else if (disc.equals("FPA")) {
                query += "courses.department_id = 6";
            } else if (disc.equals("PE")) {
                query += "courses.department_id = 7";
            } else if (disc.equals("Elc")) {
                query += "courses.department_id = 8";
            } else if (disc.equals("N/A")) {
                query += "courses.department_id = 9";
            } else if (disc.equals("Tech")) {
                query += "courses.department_id = 10";
            } else if (disc.equals("Sem")) {
                query += "courses.department_id = 11";
            }
            //grade addition
            if (grad.equals("MS")) {
                System.out.println("It's middle school");
                if (query.length() == 28) {
                    query += "courses.gradelevels in (5, 6, 7, 8, 5-6, 6-7, 7-8, 5-7, 5-8, 6-8, 5-8)";
                }
                else {
                    query += " and courses.gradelevels in (5, 6, 7, 8, 5-6, 6-7, 7-8, 5-7, 5-8, 6-8, 5-8)";
                }
            } else if (grad.equals("US")) {
                System.out.println("It's upper school");
                if (query.length() == 28) {
                    query += "courses.gradelevels in (9, 10, 11, 12, 9-10, 10-11, 11-12, 9-11, 9-12, 10-12, 9-12)";
                }
                else {
                    query += " and courses.gradelevels in (9, 10, 11, 12, 9-10, 10-11, 11-12, 9-11, 9-12, 10-12, 9-12)";
                }
                
            }
            //length addition
            if (len.equals("year")) {
                System.out.println("For the year");
                if (query.length() == 28) {
                    query += "courses.term_id = 4";
                }
                else {
                    query += " and courses.term_id = 4";
                }
            } else if (len.equals("tri")) {
                System.out.println("For the trimester");
                if (query.length() == 28) {
                    query += "courses.term_id in (1, 2, 3)";
                }
                else {
                    query += " and courses.term_id in (1, 2, 3)";
                }
            }
            //does not deal with seminars. This should be client side?
            //edge case handling where no conditions are added. removes "where."
            if (query.length() == 28) {
                query = query.substring(0, 21);
            }
            Statement stmt = conn.createStatement();
            ArrayList<Course> s = new ArrayList<>();
            ResultSet results = stmt.executeQuery(query + ";"); //execute dat query
            while (results.next()) {
                //creates a new Course object with all fields filled. There has GOT to be a better way to do this.
                s.add(new Course(results.getInt(1), results.getString(2), results.getString(3), results.getInt(5), results.getInt(6), results.getInt(7), results.getString(8), results.getInt(9), results.getInt(10), results.getString(11), results.getInt(12), results.getString(13), results.getString(14), results.getInt(15), results.getInt(17), results.getFloat(18), results.getInt(19)));
            }
            return s.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    //given a schedule request, does the student meet the prereqs?
    //also, this should be moved to 'validation' once the branches are merged.
    //needs an enrollment table to function. Currently does not.
    //returns true if student is eligible, false if not.
    public boolean preReqEligibility(ScheduleRequest r) {
        String sql = "select courses.requirements from courses where courses.id = ";
        String prereq = "";
        try {
            Statement stmt = conn.createStatement();
            //check first course option
            ResultSet results = stmt.executeQuery(sql + r.course_id + ";");
            results.next();
            prereq = results.getString(1);
            if (prereq != null) {
                //Prerequisite is spelled like 40 different ways in the database. why.
                ResultSet resultX = stmt.executeQuery("select * from enrollemnts where enrollments.name = " + prereq.substring(14) + ";");
                if (resultX == null) {
                    //ineligible.
                    return false;
                }
            }
            //checking alternate course id.
            ResultSet resultsY = stmt.executeQuery(sql + r.first_alternate_course_id + ";");
            resultsY.next();
            prereq = resultsY.getString(1);
            if (prereq != null) {
                //Prerequisite is spelled like 40 different ways in the database. why.
                ResultSet resultX = stmt.executeQuery("select * from enrollemnts where enrollments.name = " + prereq.substring(14) + ";");
                if (resultX == null) {
                    //ineligible.
                    return false;
                }
            }
            //check second alt course id.
            ResultSet resultsZ = stmt.executeQuery(sql + r.second_alternate_course_id + ";");
            resultsZ.next();
            prereq = resultsZ.getString(1);
            if (prereq != null) {
                //Prerequisite is spelled like 40 different ways in the database. why.
                ResultSet resultX = stmt.executeQuery("select * from enrollemnts where enrollments.name = " + prereq.substring(14) + ";");
                if (resultX == null) {
                    //ineligible.
                    return false;
                }
            }
        } catch (Exception e) {
            
        }
        //if passes all tests, is eligible.
        return true;
    }

}
