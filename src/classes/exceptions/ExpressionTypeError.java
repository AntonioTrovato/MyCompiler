package classes.exceptions;

public class ExpressionTypeError extends Error{
    public ExpressionTypeError() {}

    public ExpressionTypeError(String message) {
        super(message);
    }
}
