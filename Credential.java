import java.time.LocalDateTime;
import java.util.UUID;

//*
// An Abstracct base class for anything that is stored in the vault
// Both LoginCredential (user/password/url) and Secure note extend this class
// */

public abstract class Credential{

    private  final String id;
    private final LocalDateTime dateCreated;
    private  String label;
   

    public Credential(String label) {

        this.id = UUID.randomUUID().toString();
        this.label = label;
        this.dateCreated = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public LocalDateTime getDateCreated(){
        return dateCreated;
    }

    public abstract String getSummary();
}
