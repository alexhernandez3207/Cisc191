import java.security.MessageDigest;
import java.util.Base64;

/**
 * @author Brennan  & Jesus
 * Represents a user in the system with a username and a hashed password.
 */
public class User{ 


    private String username;
    private String masterHash;


    /**
     * Creates a new user, then the master password is hashed
     * @param username the username of the user
     * @param masterPassword the master password of the user
     * @throws Exception if there is an error hashing the password
     */

    public User(String username, String masterPassword) throws Exception {
        this.username = username;
        this.masterHash = hashPassword(masterPassword);
    }

    public String getUsername() {
        return username;
    }
    public String getMasterHash() {
        return masterHash;
    }
    
/**
 * Checks the password and matches it to the stored hash
 * Hashes attempt and compares to the stored hash
 * @param attemptedPassword the password to check
 * @return true if the password is correct, false otherwise
 * @throws AuthFailedException if the password is incorrect
 * @throws Exception if there is an error hashing the password
 */

    public boolean authenticate(String attemptedPassword) throws Exception {
        String attemptedHash = hashPassword(attemptedPassword);
        if (!attemptedHash.equals(masterHash)) {
            throw new AuthFailedException("Authentication failed: Incorrect password.");
        }
        return true;
    }

    /**
     * Hashes the given password using SHA-256 and Base64 encoding
     * @param password the password to hash
     * @return the hashed password
     * @throws Exception if there is an error hashing the password
     */

    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(password.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
