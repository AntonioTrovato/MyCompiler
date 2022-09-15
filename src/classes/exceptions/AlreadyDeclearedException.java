package classes.exceptions;

public class AlreadyDeclearedException extends Error{
    public AlreadyDeclearedException() {}

    public AlreadyDeclearedException(String message) {
        super(message);
    }
}
