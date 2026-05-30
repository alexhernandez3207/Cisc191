import javax.swing.*;
import java.awt.*;
import javax.crypto.SecretKey;
 
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

    // Set application icon 
    
     try {
        ImageIcon icon = new ImageIcon("icon.png");
        setIconImage(icon.getImage());
    } catch (Exception e) {}   
    }

/** 
     * Displays the vault panel.
     * @param vault The vault to display.
     * @param key The secret key for encryption/decryption.
    
*/
    public void showVault(Vault vault, SecretKey key, String username) {
        mainPanel.add(new VaultPanel(this, vault, key, username), "Vault");
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
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ignored) {}
    SwingUtilities.invokeLater(() -> new PasswordManagerGui().setVisible(true));
    }
 }

