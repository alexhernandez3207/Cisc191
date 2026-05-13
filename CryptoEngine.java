/**
 * Demonstrates AES-256 encryption for the password manager project.
 * Generates a random key, encrypts a  password, then decrypts it back
 * to verify the decrypter works correctly.
 *
 * @author  Brennan R & Jesus Hernandez 
 */

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class CryptoEngine {

    public static String encrypt(String value, SecretKey key) throws Exception
    {
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedValue = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encryptedValue);
        }
        catch(Exception ex){
            System.getLogger(CryptoEngine.class.getName())
                  .log(System.Logger.Level.ERROR, (String) null, ex);
            throw ex;
        }
    }

    public static String decrypt(String encryptedValue, SecretKey key) throws Exception
    {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedValue = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            String decrypted = new String(decryptedValue);
           return decrypted;

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