public class GenTest {
    public static void main(String[] args) {
        PasswordGenerator gen = new PasswordGenerator();

        // Test generation
        
        System.out.println(" Generated passwords ");
        for (int i = 0; i < 5; i++) {
            String pwd = gen.generate(16);
            System.out.println(pwd + "  (score: " + gen.scoreStrength(pwd) + ")");
        }

        // Test scoring on various passwords

        System.out.println("\n Strength scores ");
        String[] testPasswords = {
            "abc",
            "password",
            "Password1",
            "MyP@ssw0rd123",
            "X7$mK9pQ#vL2nR8s",
            gen.generate(20)
        };
        for (String pwd : testPasswords) {
            int score = gen.scoreStrength(pwd);
            String label = gen.strengthLabel(pwd);
            System.out.printf("%-25s → %d (%s)%n", pwd, score, label);
        }

        // Test enforcement

        System.out.println("\n Enforce strong password ");
        try {
            gen.enforceStrong("weak");
            System.out.println("Accepted (this shouldn't print)");
        } catch (WeakPasswordException ex) {
            System.out.println("Rejected: " + ex.getMessage());
        }

    /**
    * Test that a strong password is accepted by the enforceStrong method
    */

        try {
            gen.enforceStrong("X7$mK9pQ#vL2nR8s");
            System.out.println("Accepted: strong password passes");
        } catch (WeakPasswordException ex) {
            System.out.println("Rejected: " + ex.getMessage());
        }
    }
}