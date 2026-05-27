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
    
    /**
     * Serializes the Vault to bytes, Base64-encodes them, encrypts the result
     * with the given AES key, and writes the encrypted string to vault.dat.
     * @param vault the Vault object to persist
     * @param key the AES SecretKey used to encrypt the vault data
     * @throws Exception if serialization, encryption, or file writing fails
     */
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

    /**
     * Reads the encrypted string from vault.dat, decrypts it with the given AES key,
     * Base64-decodes the result, and deserializes it back into a Vault object.
     * @param key the AES SecretKey used to decrypt the vault data
     * @return the Vault object that was previously saved
     * @throws Exception if file reading, decryption, or deserialization fails
     */
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

    /**
     * Saves the AES SecretKey to vault.key so it can be reloaded on the next session.
     * The key bytes are Base64-encoded before writing so the file stays plain text.
     * @param key the SecretKey to persist
     * @throws Exception if the file cannot be written
     */
    public static void saveKey(SecretKey key) throws Exception {
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        try (FileWriter fw = new FileWriter("vault.key")) {
            fw.write(encoded);
        }
    }

    /**
     * Reads the AES SecretKey from vault.key and reconstructs it as a SecretKeySpec.
     * @return the SecretKey that was saved by saveKey
     * @throws Exception if vault.key is missing or cannot be read
     */
    public static SecretKey loadKey() throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("vault.key"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        byte[] keyBytes = Base64.getDecoder().decode(sb.toString());
        return new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Checks whether a saved vault file exists on disk.
     * Used at startup to decide whether to show the register screen or the login screen.
     * @return true if vault.dat is present, false otherwise
     */
    public static boolean vaultExists() {
        return new java.io.File(VAULT_FILE).exists();
    }

}