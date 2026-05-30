/**
 * Custom exception class for authentication failures 
 */
public class AuthFailedException extends Exception 
{
    public AuthFailedException(String message) 
    {
        super(message);
    }
}