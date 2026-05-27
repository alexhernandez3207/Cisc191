public class TestVault {
    public static void main(String[] args) {
        Vault vault = new Vault();

        // Add some credentials
        vault.add(new LoginCredential(
            "Gmail", "john@gmail.com", "hunter2", "https://gmail.com"
        ));
        vault.add(new LoginCredential(
            "Gmail", "work@gmail.com", "differentPass", "https://gmail.com"
        ));
        vault.add(new LoginCredential(
            "GitHub", "john_dev", "octocat99", "https://github.com"
        ));

        SecureNote wifi = new SecureNote(
            "Home WiFi",
            "Network: MyHome / Password: house2024"
        );
        wifi.addTag("home");
        wifi.addTag("network");
        vault.add(wifi);

        // Test getAllCredentials
        System.out.println(" All credentials in vault ");
        for (Credential c : vault.getAllCredentials()) {
            System.out.println(c.getSummary());
        }
        System.out.println("Total: " + vault.size());
        System.out.println();

        // Test search
        System.out.println(" Search for 'mail' ");
        for (Credential c : vault.search("mail")) {
            System.out.println(c.getSummary());
        }
        System.out.println();

        // Test remove
        String firstGmailId = vault.search("Gmail").get(0).getId();
        System.out.println("Removing credential with ID: " + firstGmailId);
        boolean removed = vault.remove(firstGmailId);
        System.out.println("Removed: " + removed);
        System.out.println();

        // Verify removal
        System.out.println(" After removal ");
        for (Credential c : vault.getAllCredentials()) {
            System.out.println(c.getSummary());
        }
        System.out.println("Total: " + vault.size());
    }
}