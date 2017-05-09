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

    public void disconnect() {

        try {
            this.conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

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

    public void dumpTable(String tableName) {

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName);

            System.out.println();
            System.out.println(tableName + ":");

            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            for (int i = 1; i <= numberOfColumns; i++) {
                System.out.println(rsmd.getColumnName(i) + ",  " + rsmd.getColumnTypeName(i));
            }

            System.out.println();
            System.out.println("Rows:");

            while (rs.next()) {
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
            }

            System.out.println();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

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
    public int createScheduleRequest(ScheduleRequest sr) {
        int generatedKey = 0;

        try {
            String key[] = {"ID"}; //put the name of the primary key column
            int id = getLastID();
            id++;
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

    public ScheduleRequest retrieveScheduleRequest(int id) {
        ScheduleRequest request = null;
        try {
            String sql = "SELECT * FROM schedule_requests WHERE id=?;";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);

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
    public Object[] queryStudents(String query) {

        Object[] result = null;
        ArrayList<Student> asd = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Student sd = new Student(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5));
                asd.add(sd);
            }
            result = asd.toArray();
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }

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

    public String queryName(String login) {

        String result = "unknown";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select firstname, lastname from students where login='" + login + "'");
//            System.out.println("Querying student name for login " + login);

            if (rs.next()) {
                String firstName = rs.getString(1);
                String lastName = rs.getString(2);
                result = firstName + " " + lastName;
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
    public Object getSpecificCoursesDB(String disc, String grad, String len) {
        try {
            Statement stmt = conn.createStatement();
            ArrayList<Student> s = new ArrayList<>();
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }

}
