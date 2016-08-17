/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package possystem;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import possystem.security.PasswordHashing;

/**
 *
 * @author Alex
 */
public class Login extends DatabaseLogic{
    private static String username = "";
    private static String password ="";
    private static String email ="";
    private static boolean databaseUpdate = false;
    
    public Login(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public static String newUser(String username, String email, String PreHashPassword){
        String output = "Failed to make new user";
        
        Login.username = username;
        Login.email = email;
        Login.password = PasswordHashing.getPBKDF2Hash(PreHashPassword);
        
        addUserDB(Login.username, email, Login.password);
        if(databaseUpdate){
            databaseUpdate = false;
            output = "Success! user: " +Login.username + " added";
        }
        
        return output;
    }
    
    private static Boolean addUserDB(String username, String email, String password){
        try {
            addToDB("user", username + ", " +email + ", " +"false");
            databaseUpdate = true;
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return databaseUpdate;
    }
    
    
    
}
