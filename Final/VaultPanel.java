import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;

/*
 * The panel for displaying and managing the password vault.
 */

public class VaultPanel extends JPanel {

    private PasswordManagerGui parent;
    private Vault vault;
    private SecretKey key;

    private JTable credentialTable;
    private CredentialTableModel tableModel;
    private JButton addCredentialButton;
    private JButton viewDetailsButton;
    private JButton deleteCredentialButton;
    private JButton togglePasswordsButton;
    private JButton logoutButton;
    private String username;

    private boolean passwordsVisible = false;

    public VaultPanel(PasswordManagerGui parent, Vault vault, SecretKey key, String username) {
        this.parent = parent;
        this.vault = vault;
        this.key = key;
        this.username = username;

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
        JLabel titleLabel = new JLabel("Welcome back, " + username, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> parent.showLogin());
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center: the table of credentials
        tableModel = new CredentialTableModel();
        credentialTable = new JTable(tableModel);
        credentialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        credentialTable.setRowHeight(20);
        credentialTable.setFont(new Font("Arial", Font.PLAIN, 14));
        credentialTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        credentialTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = credentialTable.getSelectedRow() != -1;
            viewDetailsButton.setEnabled(selected);
            deleteCredentialButton.setEnabled(selected);
        });

        // Set column widths
        credentialTable.getColumnModel().getColumn(0).setPreferredWidth(120);  // Label
        credentialTable.getColumnModel().getColumn(1).setPreferredWidth(160);  // Username
        credentialTable.getColumnModel().getColumn(2).setPreferredWidth(140);  // Password
        credentialTable.getColumnModel().getColumn(3).setPreferredWidth(200);  // Domain

        add(new JScrollPane(credentialTable), BorderLayout.CENTER);

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

        togglePasswordsButton = new JButton("Show Passwords");
        togglePasswordsButton.addActionListener(e -> togglePasswordVisibility());
        bottomPanel.add(togglePasswordsButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads the credentials from the vault and displays them in the table.
     */
    private void loadCredentials() {
        tableModel.setCredentials(vault.getAllCredentials());
    }

    /**
     * Toggles whether passwords are shown in plaintext or as dots in the table.
     */
    private void togglePasswordVisibility() {
        passwordsVisible = !passwordsVisible;
        togglePasswordsButton.setText(passwordsVisible ? "Hide Passwords" : "Show Passwords");
        tableModel.fireTableDataChanged();
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
        panel.setPreferredSize(new Dimension(400, 280));
        panel.add(new JLabel("Name:"));    panel.add(labelField);
        panel.add(new JLabel("Username:")); panel.add(userField);
        panel.add(new JLabel("Password:")); panel.add(pwdField);
        panel.add(generateBtn);
        panel.add(new JLabel("Domain:"));   panel.add(urlField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String label = labelField.getText().trim();
            if (label.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required.");
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
        topFields.add(new JLabel("Name:"));
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
                JOptionPane.showMessageDialog(this, "Name is required.");
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
        int row = credentialTable.getSelectedRow();
        if (row == -1) return;
        Credential c = tableModel.getCredentialAt(row);

        StringBuilder sb = new StringBuilder();
        sb.append("Label:   ").append(c.getLabel()).append("\n");
        sb.append("Created: ").append(c.getDateCreated()).append("\n\n");

        if (c instanceof LoginCredential) {
            LoginCredential lc = (LoginCredential) c;
            sb.append("Username: ").append(lc.getUsername()).append("\n");
            sb.append("Password: ").append(lc.getPassword()).append("\n");
            sb.append("Domain:   ").append(lc.getUrl()).append("\n");
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
        int row = credentialTable.getSelectedRow();
        if (row == -1) return;
        Credential c = tableModel.getCredentialAt(row);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete \"" + c.getLabel() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            vault.remove(c.getId());
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

    /**
     * Custom table model that controls how credentials appear in each row and column.
     */
    private class CredentialTableModel extends AbstractTableModel {
        private final String[] columns = {"Name", "Username", "Password", "Domain"};
        private List<Credential> credentials = new ArrayList<>();

        public void setCredentials(List<Credential> list) {
            this.credentials = list;
            fireTableDataChanged();
        }

        public Credential getCredentialAt(int row) {
            return credentials.get(row);
        }

        @Override public int getRowCount()    { return credentials.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Credential c = credentials.get(row);

            if (c instanceof LoginCredential) {
                LoginCredential lc = (LoginCredential) c;
                switch (col) {
                    case 0: return lc.getLabel();
                    case 1: return lc.getUsername();
                    case 2: return passwordsVisible ? lc.getPassword() : "••••••••";
                    case 3: return lc.getUrl();
                }
            } else if (c instanceof SecureNote) {
                SecureNote sn = (SecureNote) c;
                switch (col) {
                    case 0: return sn.getLabel();
                    case 1: return "—";
                    case 2: return passwordsVisible
                            ? (sn.getContent().length() > 20
                                ? sn.getContent().substring(0, 20) + "..."
                                : sn.getContent())
                            : "••••••••";
                    case 3: return "(" + sn.getTags().size() + " tags)";
                }
            }
            return "";
        }
    }
}