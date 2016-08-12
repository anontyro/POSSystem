/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package possystem.security;

/**
 *
 * @author Alex
 */
public class Test {
    public static void main(String[]args){
        
        String savedPass = PasswordHashing.getPBKDF2Hash("admin");
        int len = savedPass.length();
        System.out.println(savedPass + "\n" + len);
        System.out.println(PasswordHashing.validatePBKDF2Hash("admin", savedPass));
        
        
    }

}
