package utilities;
import java.security.*;

/**
 * HashPass class provides password hashing and verification using SHA-256.
 * It ensures secure password storage by converting passwords into fixed-length hashes.
 */
public class HashPass {

    /**
     * Hashes a given password using SHA-256.
     *
     * @param plainPassword The password in plain text.
     * @return The hashed password in hexadecimal format.
     */
    public static String hashPassword(String plainPassword) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Compute the hash as a byte array
            byte[] hashedBytes = md.digest(plainPassword.getBytes());

            // Convert byte array to a hexadecimal string for easy storage
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b)); // Format each byte as a 2-character hex string
            }
            return hexString.toString(); // Return hashed password as a string
        } catch (NoSuchAlgorithmException e) {
            // If SHA-256 is not supported (unlikely), throw an error
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies if the entered plain text password matches the stored hashed password.
     *
     * @param plainPassword The password entered by the user.
     * @param hashedPassword The stored hashed password.
     * @return true if the passwords match, false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        // Hash the input password and compare it to the stored hashed password
        return hashPassword(plainPassword).equals(hashedPassword);
    }
}
