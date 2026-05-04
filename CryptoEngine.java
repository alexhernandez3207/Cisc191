/**
 * Demonstrates AES-256 symmetric encryption for the password manager project.
 * Generates a random key, encrypts a sample password, then decrypts it back
 * to verify the round-trip works correctly.
 *
 * @author  Brennan R & Jesus Hernandez 
 */

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;


public class CryptoEngine {

    /**
     * Entry point for the encryption demo. Generates an AES-256 key,
     * encrypts a hard-coded test password, prints the encrypted ciphertext
     * in Base64, then decrypts it back to confirm the original is recovered.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            SecretKey key = generateKey();

            String password = "TestPassword!d0ntSt34l";
            System.out.println("Original Password: " + password);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes());
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);
            System.out.println("Encrypted Password: " + encrypted);

            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            String decrypted = new String(decryptedBytes);
            System.out.println("Decrypted Password: " + decrypted);

        } catch (Exception ex) {
            System.getLogger(CryptoEngine.class.getName())
                  .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    /**
     * Generates a fresh 256-bit AES key using a cryptographically strong
     * random number generator.
     *
     * @return a newly generated SecretKey suitable for AES-256 encryption
     * @throws Exception if the AES algorithm or strong RNG is unavailable
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }
}