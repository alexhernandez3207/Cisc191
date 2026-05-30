import javax.swing.*;
import java.awt.*;
import javax.swing.event.ListSelectionEvent;
import javax.crypto.SecretKey;
import java.util.List;

/*
 * The panel for displaying and managing the password vault.
 */

public class VaultPanel extends JPanel {

    private PasswordManagerGui parent;
    private Vault vault;
    private SecretKey key;

    private JList<CredentialItem> credentialList;
    private DefaultListModel<CredentialItem> listModel;
    private JButton addCredentialButton;
    private JButton viewDetailsButton;
    private JButton deleteCredentialButton;
    private JButton logoutButton;

    public VaultPanel(PasswordManagerGui parent, Vault vault, SecretKey key) {
        this.parent = parent;
        this.vault = vault;
        this.key = key;

        buildUI();
        loadCredentials();
    }

    /** 
     * Builds the user interface for the vault panel.
    */

    private void buildUI() {
        setLayout(new BorderLayout());

        // Top panel with title and logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Account Vault", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> parent.showLogin());
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center panel with credential list

        listModel = new DefaultListModel<>();
        credentialList = new JList<>(listModel);
        credentialList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        credentialList.addListSelectionListener(this::onCredentialSelected);
        add(new JScrollPane(credentialList), BorderLayout.CENTER);

        // Bottom panel with action buttons

        JPanel bottomPanel = new JPanel();
        addCredentialButton = new JButton("Create");
        addCredentialButton.addActionListener(e -> showAddCredentialDialog());
        bottomPanel.add(addCredentialButton);

        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setEnabled(false);
        viewDetailsButton.addActionListener(e -> showCredentialDetails());
        bottomPanel.add(viewDetailsButton);

        deleteCredentialButton = new JButton("Delete Login");
        deleteCredentialButton.setEnabled(false);
        deleteCredentialButton.addActionListener(e -> deleteSelectedCredential());
        bottomPanel.add(deleteCredentialButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /** 
     * Loads the credentials from the vault and displays them in the list.
     */

    private void loadCredentials() {
        listModel.clear();
        List<Credential> credentials = vault.getAllCredentials();
        for (Credential cred : credentials) {
            listModel.addElement(new CredentialItem(cred));
        }
    }

    /** 
     * Handles the selection of a credential in the list.
     * @param e the list selection event
    
    */
    private void onCredentialSelected(ListSelectionEvent e) {
        boolean selected = !credentialList.isSelectionEmpty();
        viewDetailsButton.setEnabled(selected);
        deleteCredentialButton.setEnabled(selected);
    }

    /**
     * Shows the dialog for adding a new credential.
     */

    private void showAddCredentialDialog() {
        String[] options = {"New Login", "New Note", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "What would you like to create?",
            "Add Credential",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);

        if (choice == 0) addLoginDialog();
        else if (choice == 1) addNoteDialog();
    }

    private void addLoginDialog() {
        JTextField labelField = new JTextField();
        JTextField userField = new JTextField();
        JTextField pwdField = new JTextField();
        JTextField urlField = new JTextField();
        JButton generateBtn = new JButton("Generate Strong Password");

        generateBtn.addActionListener(e -> {
            String generated = new PasswordGenerator().generate(16);
            pwdField.setText(generated);
        });

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Label:"));    panel.add(labelField);
        panel.add(new JLabel("Username:")); panel.add(userField);
        panel.add(new JLabel("Password:")); panel.add(pwdField);
        panel.add(generateBtn);
        panel.add(new JLabel("URL:"));      panel.add(urlField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String label = labelField.getText().trim();
            if (label.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Label is required.");
                return;
            }
            vault.add(new LoginCredential(label, userField.getText().trim(),
                                          pwdField.getText(), urlField.getText().trim()));
            saveAndRefresh();
        }
    }

    private void addNoteDialog() {
        JTextField labelField = new JTextField();
        JTextArea contentArea = new JTextArea(5, 30);
        JTextField tagsField = new JTextField();

        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JPanel topFields = new JPanel(new GridLayout(0, 1, 4, 4));
        topFields.add(new JLabel("Label:"));
        topFields.add(labelField);
        topFields.add(new JLabel("Content:"));
        panel.add(topFields, BorderLayout.NORTH);
        panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        JPanel bottomFields = new JPanel(new GridLayout(0, 1, 4, 4));
        bottomFields.add(new JLabel("Tags (comma-separated):"));
        bottomFields.add(tagsField);
        panel.add(bottomFields, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Note",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String label = labelField.getText().trim();
            if (label.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Label is required.");
                return;
            }
            SecureNote note = new SecureNote(label, contentArea.getText());
            String tagsRaw = tagsField.getText().trim();
            if (!tagsRaw.isEmpty()) {
                for (String tag : tagsRaw.split(",")) {
                    String t = tag.trim();
                    if (!t.isEmpty()) note.addTag(t);
                }
            }
            vault.add(note);
            saveAndRefresh();
        }
    }

    private void showCredentialDetails() {
        CredentialItem item = credentialList.getSelectedValue();
        if (item == null) return;
        Credential c = item.credential;

        StringBuilder sb = new StringBuilder();
        sb.append("Label:   ").append(c.getLabel()).append("\n");
        sb.append("Created: ").append(c.getDateCreated()).append("\n\n");

        if (c instanceof LoginCredential) {
            LoginCredential lc = (LoginCredential) c;
            sb.append("Username: ").append(lc.getUsername()).append("\n");
            sb.append("Password: ").append(lc.getPassword()).append("\n");
            sb.append("URL:      ").append(lc.getUrl()).append("\n");
        } else if (c instanceof SecureNote) {
            SecureNote sn = (SecureNote) c;
            sb.append("Content:\n").append(sn.getContent()).append("\n\n");
            sb.append("Tags: ").append(String.join(", ", sn.getTags()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(400, 250));

        JOptionPane.showMessageDialog(this, scroll, "Login Details",
                JOptionPane.PLAIN_MESSAGE);
    }

    private void deleteSelectedCredential() {
        CredentialItem item = credentialList.getSelectedValue();
        if (item == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete \"" + item.credential.getLabel() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            vault.remove(item.credential.getId());
            saveAndRefresh();
        }
    }

    private void saveAndRefresh() {
        try {
            VaultFileManager.saveVault(vault, key);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save vault: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
        loadCredentials();
    }

   
    private static class CredentialItem {
        final Credential credential;
        CredentialItem(Credential c) { this.credential = c; }
        @Override public String toString() { return credential.getSummary(); }
    }
}