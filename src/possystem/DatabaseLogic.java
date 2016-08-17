/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package possystem;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class DatabaseLogic {

    private final static String SQLITE_CONNECTION = "jdbc:sqlite:./resources/posdb.db";
    private static Connection con;
    private static PreparedStatement pst;

    public DatabaseLogic() {
    }

    /**
     *
     * @param table name of database table
     * @param value string values seperated using ","
     */
    public static void addToDB(String table, String value) throws SQLException {
        String preparedValues = "";
        /*
         MySQL query INSERT INTO table (column1, colum2, etc) VALUES (?,?,etc);
         (columnNo1,value);
         (2,value);
        
         SQLite3 INSERT INTO table VALUES (value1, value2, etc);
         */
        try {
            //connects and tests table connection
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        con = DriverManager.getConnection(SQLITE_CONNECTION);

        //regex to check if the input is in format value1, value2, value3, etc
        //removes all whitespace and adds values to the arrays for values to be added to the db
        value = value.replaceAll("\\s", "");
        String[] valueData = value.split(",");

        if (valueData == null) {
            System.err.println("Error, no data added to values");
            System.exit(1);
        }

        //craetes the two prepared Strings to be added to the database
        for (String x : valueData) {
            preparedValues += "'" + x + "'" + ",";
        }

        //removes the last , in the strings and returns a new substring
        preparedValues = preparedValues.substring(0, (preparedValues.length() - 1));

        //creates the preparedStatement for use
        String constructedStatement = "INSERT INTO " + table
                + " VALUES " + "(" + preparedValues + ")";

        System.out.println(constructedStatement);

        //create a preparedStatement object
        try {
            //connect to the database
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(SQLITE_CONNECTION);

            //add the statement to the prepared statement
            pst = con.prepareStatement(constructedStatement);

            //submits all the values
            pst.execute();
            System.out.println("Database successfully updated");
            con.close();

        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
            ex.getStackTrace();
        } finally {
            con.close();
        }
    }

    /**
     *
     * @param table
     * @param column
     * @param value
     * @return
     */
    public boolean queryFind(String table, String column, String value) throws SQLException {
        //String queryUsername ="SELECT username FROM login WHERE username = ?"; 
        String query = "SELECT " + column + " FROM " + table + " WHERE " + column + " = " + "'" + value + "'";
        ResultSet rs;
        boolean exists = false;
        String output = null;

        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(SQLITE_CONNECTION);
            //connect(table);

            pst = con.prepareStatement(query);
            System.out.println(query);
            rs = pst.executeQuery();

            if (rs.next()) {
                exists = true;
                output = rs.toString();

            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            ex.getStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseLogic.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.close();
        }
        System.out.println(output);
        return exists;
    }

    /**
     * Method that opens the connection to the database base, will also close
     * any previous connections if they are still open and checks it can access
     * the requested table.
     *
     * @param table table in database you wish to access
     */
    public void connect(String table) {

        connectionLogic();
        checkConnection(table);

    }

    private static void connectionLogic() {
        try {
            if (con != null) {
                con.close();
                con = null;
            }
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(SQLITE_CONNECTION);
        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
            ex.getStackTrace();
        }
    }

    /**
     * method to check that the database still exists currently just checks if
     * it exists or not will add the ability to rebuild from the saved sql
     * files.
     *
     * @throws SQLException
     */
    private boolean checkConnection(String table) {
        boolean connected = false;
        try {
            if (con != null) {
                Statement state = con.createStatement();
                ResultSet rs = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'");
                if (rs.next()) {
                    System.out.println("Connected to SQLite database");
                    connected = true;
                    rs = null;
                } else {
                    System.err.println("Error connecting to database"
                            + "\n table: " + table + " doesn't exist");
                    con.close();
                    System.exit(1);
                }
            }
        } catch (SQLException ex) {
            ex.getStackTrace();
        }
        return connected;
    }

    //testing class
    public static void main(String[] args) throws SQLException {

        DatabaseLogic db = new DatabaseLogic();
        //db.connect("user");
        //db.addToDB("user", "steve, ste@home.com, steveo, false");
        System.out.println(db.queryFind("user", "password", "steveo"));
    }
}
