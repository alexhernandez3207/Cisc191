import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

//*
// An Abstracct base class for anything that is stored in the vault
// Both LoginCredential (user/password/url) and Secure note extend this class
// */

public abstract class Credential implements Serializable {

    private  final String id;
    private final LocalDateTime dateCreated;
    private  String label;
   
/**
 * Creates a new credential with a unique ID, the current date/time, and the given label
 * @param label the label for the credential 
 */
    public Credential(String label) {

        this.id = UUID.randomUUID().toString();
        this.label = label;
        this.dateCreated = LocalDateTime.now();
    }
/**
 * Gets the unique ID of the credential
 * @return the unique ID of the credential
 */
    public String getId() {
        return id;
    }
/**
 * Gets the label of the credential
 * @return the label of the credential
 */
    public String getLabel() {
        return label;
    }
/** 
 * Gets the date the credential was created
 * @return the date the credential was created
 */
    public LocalDateTime getDateCreated(){
        return dateCreated;
    }
/** * Gets a summary string for the credential, which can be used for display purposes
 * @return a summary string for the credential
 */
    public abstract String getSummary();
}
