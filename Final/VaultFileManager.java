import java.io.*;
import java.util.Base64;
import javax.crypto.SecretKey;

/**
 * @author Brennan  & Jesus
 * Utility class for managing vault file operations such as saving and loading the vault data.
 */

public class VaultFileManager
{
    private static final String VAULT_FILE = "vault.dat";
    
    public static void saveVault(Vault vault, SecretKey key) throws Exception
    {
        // Serialize vault to bytes
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(vault);
        objOut.close();

         // Base64 encode
        String encoded = Base64.getEncoder().encodeToString(byteOut.toByteArray());

        // Encrypt
        String encrypted = CryptoEngine.encrypt(encoded, key);

        // Write to file
        try (FileWriter fw = new FileWriter(VAULT_FILE)) {
            fw.write(encrypted);
        }
    }

    public static Vault loadVault(SecretKey key) throws Exception
    {
        // Read from file
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(VAULT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }

        // Decrypt
        String decoded = CryptoEngine.decrypt(sb.toString(), key);

        // Base64 decode and deserialize
        byte[] bytes = Base64.getDecoder().decode(decoded);
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
        return (Vault) objIn.readObject();
    }

    public static void saveKey(SecretKey key) throws Exception {
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        try (FileWriter fw = new FileWriter("vault.key")) {
            fw.write(encoded);
        }
    }

    public static SecretKey loadKey() throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("vault.key"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        byte[] keyBytes = Base64.getDecoder().decode(sb.toString());
        return new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
    }

    public static boolean vaultExists() {
        return new java.io.File(VAULT_FILE).exists();
    }

}