public class LoginCredential extends Credential{

    private String username;
    private String password;
    private String url;

    public LoginCredential(String label, String username, String password, String url){
        super(label);
        this.username = username;
        this.password = password;
        this.url = url;
    }
        public String getUsername(){
            return username;
        }

        public String getPassword(){
            return password;
        }

        public String getUrl(){
            return url;
        }

        @Override
        public String getSummary() {
            return "Login Credential: " + getLabel() + " for " + url;
        }
    }
