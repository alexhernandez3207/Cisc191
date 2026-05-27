public class TestUser {
    public static void main(String[] args) throws Exception {

        // Create a user
        User user = new User("john", "MyMasterPass!");

        System.out.println("Username: " + user.getUsername());
        System.out.println("Stored hash: " + user.getMasterHash());
        System.out.println();

        // Try logging in with the correct password
        try {
            user.authenticate("MyMasterPass!");
            System.out.println("Login successful with correct password.");
        } catch (AuthFailedException ex) {
            System.out.println("Login failed: " + ex.getMessage());
        }

        // Try logging in with the WRONG password
        try {
            user.authenticate("WrongPassword");
            System.out.println("Login successful (this shouldn't print).");
        } catch (AuthFailedException ex) {
            System.out.println("Login failed (as expected): " + ex.getMessage());
        }
    }
}