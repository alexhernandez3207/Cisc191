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
        "dragon", "sunshine", "princess", "master", "hello","pizza"
    );

    private SecureRandom random;

    public PasswordGenerator() {
        this.random = new SecureRandom();
    }

        public String generate(int length) {
            String allChars = UPPERCASE + LOWERCASE + DIGITS + SPECIALS;
            StringBuilder password = new StringBuilder(length);
            
            for (int i = 0; i < length; i++) {
                int index = random.nextInt(allChars.length());
                password.append(allChars.charAt(index));
            }
            return password.toString();
        }

        public int scoreStrength(String password) {
            if(password == null || password.isEmpty()){
                return 0;
            }
        
            if (commonPasswords.contains (password.toLowerCase())) {
                return 0; 
            }

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

        public void enforceStrong(String password) throws WeakPasswordException {
            int strength = scoreStrength(password);
            if (strength < STRONG_THRESHOLD) {
                throw new WeakPasswordException("Password is too weak. Use a stronger password with a mix of uppercase, lowercase, digits, and special characters, and avoid common passwords.");
            }
        }

        public String strengthLabel(String password) {
            int score = scoreStrength(password);
            if (score <= 2) return "Weak";
            if (score <= 4) return "Moderate";
            if (score <= 6) return "Strong";
            return "Very Strong";
        }
}