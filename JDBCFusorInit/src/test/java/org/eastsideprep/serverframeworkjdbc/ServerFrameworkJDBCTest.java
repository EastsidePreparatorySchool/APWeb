/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eastsideprep.serverframeworkjdbc;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import spark.Request;
import spark.Session;

/**
 *
 * @author azhuang
 */
public class ServerFrameworkJDBCTest {
    
    public ServerFrameworkJDBCTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class ServerFrameworkJDBC.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        ServerFrameworkJDBC.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of useWebcam method, of class ServerFrameworkJDBC.
     */
    @Test
    public void testUseWebcam() throws Exception {
        System.out.println("useWebcam");
        Request req = null;
        String expResult = "";
        String result = ServerFrameworkJDBC.useWebcam(req);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of putHandler method, of class ServerFrameworkJDBC.
     */
    @Test
    public void testPutHandler() {
        System.out.println("putHandler");
        Request req = null;
        String expResult = "";
        String result = ServerFrameworkJDBC.putHandler(req);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of postHandler method, of class ServerFrameworkJDBC.
     */
    @Test
    public void testPostHandler() {
        System.out.println("postHandler");
        Request req = null;
        String expResult = "";
        String result = ServerFrameworkJDBC.postHandler(req);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHandler method, of class ServerFrameworkJDBC.
     */
    @Test
    public void testGetHandler() {
        System.out.println("getHandler");
        Request req = null;
        Object expResult = null;
        Object result = ServerFrameworkJDBC.getHandler(req);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTablesHandler method, of class ServerFrameworkJDBC.
     */
    @Test
    public void testGetTablesHandler() {
        System.out.println("getTablesHandler");
        Request req = null;
        Object expResult = null;
        Object result = ServerFrameworkJDBC.getTablesHandler(req);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDukakisHandler method, of class ServerFrameworkJDBC.
     */
    @Test
    public void testGetDukakisHandler() {
        System.out.println("getDukakisHandler");
        Request req = null;
        Object expResult = null;
        Object result = ServerFrameworkJDBC.getDukakisHandler(req);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getContextFromSession method, of class ServerFrameworkJDBC.
     */
    @Test
    public void testGetContextFromSession() {
        System.out.println("getContextFromSession");
        Session s = null;
        Context expResult = null;
        Context result = ServerFrameworkJDBC.getContextFromSession(s);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
