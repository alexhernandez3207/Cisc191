import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * @author Brennan  & Jesus
 * Utility class for generating random passwords based on specified criteria.
 */

public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    private static final int STRONG_THRESHOLD = 5;

    private static final List<String> commonPasswords = Arrays.asList(
        "password", "123456", "123456789", "qwerty", "abc123",
        "football", "monkey", "letmein", "111111", "welcome","admin",
        "dragon", "sunshine", "princess", "master", "hello","pizza", "iloveyou",
        "123123", "654321", "superman", "1q2w3e4r", "asdfgh", "password1"
    );

    private SecureRandom random;

/**
 * Initializes the PasswordGenerator with a new instance of SecureRandom for generating random numbers securely.
 */

    public PasswordGenerator() 
    {
        this.random = new SecureRandom();
    }

/**
 * Generates a random password of the specified length using a combination of uppercase letters, lowercase letters, digits, and special characters.
 * @param length the desired length of the generated password
 */

    public String generate(int length) {
        String allChars = UPPERCASE + LOWERCASE + DIGITS + SPECIALS;
        StringBuilder password = new StringBuilder(length);
            
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allChars.length());
                password.append(allChars.charAt(index));
            }
        return password.toString();
    }

/**
 * Scores the strength of a given password based on its length, character variety, and whether it is a common password. The score is calculated by awarding points for length and character types, and penalizing if the password is found in a list of common passwords.
 * @param password the password to score
 * @return the strength score of the password
 */

    public int scoreStrength(String password) {
        if(password == null || password.isEmpty()){
            return 0;
        }
        
        if (commonPasswords.contains (password.toLowerCase())) {
            return 0; 
        }

    /**
    * Calculates the strength score of the password by awarding points for length and character variety. The scoring system awards points for length (1 point for 8+ characters, 2 points for 12+ characters, 3 points for 16+ characters) and for the presence of different character types (1 point each for lowercase, uppercase, digits, and special characters). The total score is returned as an integer.
    * @param password the password to score
    * @return the strength score of the password
    */

        int score = 0;

        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;

        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        if (hasLower) score++;
        if (hasUpper) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;

        return score;
    }

    /** 
    *  Enforces that a given password meets a minimum strength requirement by scoring the password and throwing a WeakPasswordException if the score is below the defined STRONG_THRESHOLD. The method uses the scoreStrength method to evaluate the password and provides feedback through the exception message if the password is deemed too weak.
    * @param password the password to enforce strength on
    */

    public void enforceStrong(String password) throws WeakPasswordException {
        int strength = scoreStrength(password);
        if (strength < STRONG_THRESHOLD) {
            throw new WeakPasswordException("Password is too weak. Use a stronger password with a mix of uppercase, lowercase, digits, and special characters, and avoid common passwords.");
        }
    }

    /**
    * Returns a strength label for the given password based on its strength score. The method categorizes passwords into "Weak", "Moderate", "Strong", and "Very Strong" based on the score calculated by the scoreStrength method. This provides a user-friendly way to understand the strength of a password at a glance.
    * @param password the password to evaluate
    * @return the strength label of the password
    */

    public String strengthLabel(String password) {
        int score = scoreStrength(password);
        if (score <= 2) return "Weak";
        if (score <= 4) return "Moderate";
        if (score <= 6) return "Strong";
        return "Very Strong";
    }
}