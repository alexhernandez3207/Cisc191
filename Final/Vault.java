import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author Brennan  & Jesus
 * Represents a vault that can store multiple credentials for a user.
 */

public class Vault implements Serializable {

    private Map<String, List<Credential>> entries;
    public Vault() {
        this.entries = new HashMap<>();
    }

    /**
     * Adds a new credential to the vault
     * @param entry the credential to add
     */
    public void add(Credential credential) {
        String label = credential.getLabel();

        if (!entries.containsKey(label)){
            entries.put(label, new ArrayList<>());
        }
        entries.get(label).add(credential);
    }

    public boolean remove(String credentialId){
        for (List<Credential> creds : entries.values()) {
            for (int i = 0; i < creds.size(); i++){
                if (creds.get(i).getId().equals(credentialId)){
                    creds.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    public List<Credential> search(String keyword){
        List<Credential> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (List<Credential> list : entries.values()) {
            for (Credential c : list) {
                if (c.getLabel().toLowerCase().contains(lowerKeyword)){
                    results.add(c);
                }
            }
        }
        return results;
    }

    public List<Credential> getAllCredentials(){
        List<Credential> all = new ArrayList<>();
        for (List<Credential> list : entries.values()) {
            all.addAll(list);
        }
        return all;
    }

    public int size() {
        int total = 0;
        for (List<Credential> list : entries.values()) {
            total += list.size();
        }
        return total;
    }

    public Map<String, List<Credential>> getEntries() {
        return entries;
    }
}