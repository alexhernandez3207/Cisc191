import javax.swing.*;
import java.awt.*;
import javax.crypto.SecretKey;

public class LoginPanel extends JPanel {

    private PasswordManagerGui parent;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel strengthLabel;
    private JButton actionButton;
    private JLabel statusLabel;
    private JCheckBox showPasswordCheckBox;

    private PasswordGenerator generator;
    private boolean isRegisterMode;

    public LoginPanel(PasswordManagerGui parent) {
        this.parent = parent;
        this.generator = new PasswordGenerator();
        this.isRegisterMode = !VaultFileManager.vaultExists();

        buildUI();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 9, 9, 9);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title 

        JLabel title = new JLabel(isRegisterMode ? "Create Account" : "Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        // Username label

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);

        // Username field 

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        add(usernameField, gbc);

        // Password label 

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Master Password:"), gbc);

        // Password field 

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // Show Password Checkbox 

        gbc.gridx = 1;
        gbc.gridy = 3;
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '\u2022');
        });
        add(showPasswordCheckBox, gbc);

        // Password Strength Label

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        strengthLabel = new JLabel(" ");
        strengthLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        add(strengthLabel, gbc);

        // Live strength updates

        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateStrength(); }
        });

        // Action button 

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        actionButton = new JButton(isRegisterMode ? "Register" : "Login");
        actionButton.addActionListener(e -> handleAction());
        add(actionButton, gbc);

        // Status label 

        gbc.gridy = 6;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        add(statusLabel, gbc);
    }

    // Updates the strength label color and text based on the current password
    private void updateStrength() {
        String pwd = new String(passwordField.getPassword());
        if (pwd.isEmpty()) {
            strengthLabel.setText(" ");
            return;
        }
        String label = generator.strengthLabel(pwd);
        int score = generator.scoreStrength(pwd);
        strengthLabel.setText("Password Strength: " + label + " (" + score + "/7)");

        if (score < 3) {
            strengthLabel.setForeground(Color.RED);
        } else if (score < 5) {
            strengthLabel.setForeground(Color.ORANGE);
        } else {
            strengthLabel.setForeground(new Color(0, 128, 0));  // Dark Green
        }
    }

    private void handleAction() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
        statusLabel.setText("Username and password are required.");
        return;
    }

    try {
        if (isRegisterMode) {
            // Reject weak passwords on registration
            generator.enforceStrong(password);

            User user = new User(username, password);
            Vault vault = new Vault();
            SecretKey key = CryptoEngine.generateKey();

            VaultFileManager.saveKey(key);
            VaultFileManager.saveVault(vault, key);

            statusLabel.setForeground(new Color(0, 130, 0));
            statusLabel.setText("Account created. Loading vault...");
            parent.showVault(vault, key);

        } else {
            // Login: load the vault from disk
            SecretKey key = VaultFileManager.loadKey();
            Vault vault = VaultFileManager.loadVault(key);
            parent.showVault(vault, key);
        }

    } catch (WeakPasswordException ex) {
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(ex.getMessage());
    } catch (Exception ex) {
        statusLabel.setForeground(Color.RED);
        statusLabel.setText("Error: " + ex.getMessage());
    }

    }
}