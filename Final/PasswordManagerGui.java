import javax.swing.*;
import java.awt.*;
 
/**
 * The main GUI class for the password manager application. 
 *
 */
public class PasswordManagerGui extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public PasswordManagerGui() {
        setTitle("EncryptedKeyz");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new LoginPanel(this), "Login");

        add(mainPanel);
        cardLayout.show(mainPanel, "Login");
    }

/** 
     * Displays the vault panel.
     * @param vault The vault to display.
     * @param key The secret key for encryption/decryption.
    
*/
    public void showVault(Vault vault, javax.crypto.SecretKey key) {
        mainPanel.add(new VaultPanel(this, vault, key), "Vault");
        cardLayout.show(mainPanel, "Vault");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /** 
     * Displays the login panel.
     */

    public void showLogin() { 
        mainPanel.removeAll();
        mainPanel.add(new LoginPanel(this), "Login");
        cardLayout.show(mainPanel, "Login");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordManagerGui().setVisible(true));
    }
}