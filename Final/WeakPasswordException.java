/**
 * Custom exception class for weak passwords
 */

public class WeakPasswordException extends Exception 
{
    public WeakPasswordException(String message) 
    {
        super(message);
    }
}