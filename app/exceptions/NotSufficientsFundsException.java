package exceptions;

public class NotSufficientsFundsException extends RuntimeException {

    public NotSufficientsFundsException(String message) {
        super(message);
    }
}
