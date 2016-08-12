
package possystem.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Security class to generate password hashes using PBKDF2 and salt to hash
 * the user passwords, saves as 166 char strings
 * @author Alex
 */
public class PasswordHashing {

    private static String passwordToHash = "password";
    private static String hashedPassword = "";

    /**
    * no args constructor to create a PasswordHashing object
    */
    public PasswordHashing() {
    }
    /**
     * Constructor which takes the basic password to be hashed, returns a String 
     * output for the hash.
     * @param passwordToHash basic user password string.
     */
    public PasswordHashing(String passwordToHash) {
        this.passwordToHash = passwordToHash;
        getPBKDF2Hash(passwordToHash);
        System.out.println(hashedPassword);
    }

    /**
     * To hash a password using PBKDF hashing with salt.
     * @param password simple password string to be converted into hashed element.
     * @return returns the String hashed with PBKDF and salt.
     */
    public static String getPBKDF2Hash(String password) {
        String output = "";
        try {
            output = get_PBKDF2_SecurePassword(password);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.getStackTrace();
        }
        return output;
    }
    /**
     * boolean validation method that checks a simple password against the saved
     * hashed version to ensure it matches.
     * @param Newpass simple password string to be checked against the saved.
     * @param storedPass saved hashed version to be checked.
     * @return boolean true: match, false: mismatch.
     */
    public static boolean validatePBKDF2Hash(String Newpass, String storedPass){
        boolean match = false;
        try {
            match =  validatePassword(Newpass, storedPass);
        } catch (NoSuchAlgorithmException| InvalidKeySpecException ex) {
           System.out.println(ex.getMessage());
           ex.getStackTrace();
        }
        return match;
    }

    //back up low level security hash
    private static String getSHA512Hash(String password) {
        String output = "";
        try {
            output = get_SHA_512_SecurePassword(password, getSalt());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.getStackTrace();
        }
        return output;
    }

    //creates the hashing
    private static String get_SHA_512_SecurePassword(String passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex.getMessage());
            ex.getStackTrace();
        }
        return generatedPassword;
    }

    // creates the PBKDF2 hashing in the algorithm here
    private static String get_PBKDF2_SecurePassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);

    }

    private static boolean validatePassword(String originalPass, String storedPass) throws NoSuchAlgorithmException, InvalidKeySpecException {

        String[] parts = storedPass.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPass.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;

    }

    //additional security
    private static byte[] getSalt() throws NoSuchAlgorithmException {

        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    //adds to security
    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    //for validation
    private static byte[] fromHex(String hex) {

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }

}
