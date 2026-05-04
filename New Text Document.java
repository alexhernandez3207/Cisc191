import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

public SecretKey generateKey() throws Exception {
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    // Explicitly initialize with 256 bits
    keyGen.init(256, SecureRandom.getInstanceStrong());
    return keyGen.generateKey();
}
