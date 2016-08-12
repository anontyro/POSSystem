/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package possystem;

import java.sql.*;

/**
 *
 * @author Alex
 */
public class DatabaseLogic {

    private final static String SQLITE_CONNECTION = "jdbc:sqlite:./resources/posdb.db";
    private static Connection con;

    public DatabaseLogic() {

    }

    /**
     * method to check that the database still exists currently just checks if
     * it exists or not will add the ability to rebuild from the saved sql
     * files.
     *
     * @throws SQLException
     */
    public boolean checkConnection(String table) throws SQLException {
        boolean connected = false;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(SQLITE_CONNECTION);
            if (con != null) {
                Statement state = con.createStatement();
                ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"+table+"'");
                if (res.next()) {
                    System.out.println("Connected to SQLite database");
                    connected = true;
                    con.close();
                } else {
                    System.err.println("Error connecting to database"
                            + "\n table: " +table+ " doesn't exist");
                    con.close();
                    System.exit(1);
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.getStackTrace();
        } finally {
            con.close();
        }
        return connected;
    }

    /**
     *
     * @param table
     */
    public void addToDB(String table, String column, String value) throws SQLException {
        String preparedColumns = ""; //in SQLite does not need column names only MySQL
        String preparedValues = "";
        /*
         SQL query INSERT INTO table (column1, colum2, etc) VALUES (?,?,etc);
         (columnNo1,value);
         (2,value);
         */

        //tests the connection and ensures the table exists
        checkConnection(table);
        
        //removes all whitespace and adds values to the array for column names
        column = column.replaceAll("\\s", "");
        String[] columnData = column.split(",");

        //check to see if the column has been populated
        if (columnData == null) {
            System.err.println("Error, no data added to columns");
            System.exit(1);
        }

        //removes all whitespace and adds values to the arrays for values to be added to the db
        value = value.replaceAll("\\s", "");
        String[] valueData = value.split(",");

        if (valueData == null) {
            System.err.println("Error, no data added to values");
            System.exit(1);
        }

        if (columnData.length != valueData.length) {
            System.out.println("Error values are not equal!"
                    + "\n number of Columns: " + columnData.length
                    + "\n number of Values: " + valueData.length);
            System.exit(1);
        }

        //craetes the two prepared Strings to be added to the database
        for (String x : columnData) {
            preparedColumns += x + ",";
            preparedValues += "?,";
        }

        //removes the last , in the strings and returns a new substring
        preparedColumns = preparedColumns.substring(0, (preparedColumns.length() - 1));
        preparedValues = preparedValues.substring(0, (preparedValues.length() - 1));

        //preparedstatement to add
        String constructedStatement = "INSERT INTO " + table
                + "(" + preparedColumns + ")" + " VALUES " + "(" + preparedValues + ")";

        System.out.println(constructedStatement);
        
        //create a preparedStatement object
        PreparedStatement pst;

        try {
            //connect to the database
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(SQLITE_CONNECTION);

            //add the statement to the prepared statement
            pst = con.prepareStatement(constructedStatement);
            
            //creates the value statements to replace the ?, ?
            for(int i = 0; i< columnData.length; i++){
                pst.setString(i + 1, valueData[i]);
                System.out.println(i + 1 + valueData[i]);
            }
            
            //submits all the values
            pst.execute();
            System.out.println("Database successfully updated");
            con.close();
            
        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
            ex.getStackTrace();
        }
        finally{
            con.close();
        }

        
        
        
        
        
    }

    //testing class
    public static void main(String[] args) throws SQLException {

        DatabaseLogic db = new DatabaseLogic();
        db.checkConnection("user");
        db.addToDB("user", "username, password, email, validated", "alex, newpass, me@home.com, false");

    }
}
