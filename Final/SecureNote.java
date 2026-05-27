import java.util.ArrayList;
import java.util.List;

/**
 * @author Brennan  & Jesus
 * Represents a secure note credential that can be stored in the vault, with content and tags for
 */ 
public class SecureNote extends Credential{

    private String content;
    private List<String> tags;

    /**
    * Creates a new secure note with the given label and content, and initializes an empty list of tags
    * @param label the label for the secure note
    * @param content the content of the secure note
    */

    public SecureNote(String label, String content){
        super(label);
        this.content = content;
        this.tags = new ArrayList<>();
    }
        public String getContent(){
            return content;
        }

        public void setContent(String content){
           this.content = content;
        }

        public List<String> getTags(){
            return tags;
        }

        public void addTag(String newTag){
            this.tags.add(newTag);
        }

        public void removeTag(String tagToRemove){
            this.tags.remove(tagToRemove);
        }
        @Override
        public String getSummary() {
            return String.format("%s (%d tags)", getLabel(), getTags().size());
        }
        
}